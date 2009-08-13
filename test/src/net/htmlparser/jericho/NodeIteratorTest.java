package net.htmlparser.jericho;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.CharBuffer;

public class NodeIteratorTest {
	private static final String sourceUrlString="file:test/data/StreamedSourceTest.html";

	@Test public void test() throws Exception {
		Source source=new Source(new URL(sourceUrlString));
		Segment segment;
		StartTag startTag;
		assertEquals("UTF-8",source.getEncoding());
		Iterator<Segment> i=source.iterator();
		assertTrue(i.hasNext());
		segment=i.next();
		assertEquals(StartTagType.XML_DECLARATION,((Tag)segment).getTagType());
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>",segment.toString());
		assertEquals("<?xml ?> (XML declaration) ((r1,c1,p0)-(r1,c40,p39))",segment.getDebugInfo()); // note row and column information is included
		assertTrue(i.hasNext());
		segment=i.next();
		assertEquals("\r\n",segment.toString());
		assertTrue(i.hasNext());
		segment=i.next();
		assertEquals(StartTagType.SERVER_COMMON,((Tag)segment).getTagType());
		assertEquals("<%@ page language=\"java\" %>",segment.toString());
		segment=i.next();
		assertEquals(StartTagType.SERVER_COMMON,((Tag)segment).getTagType());
		assertEquals("<%@ taglib uri=\"/WEB-INF/struts-i18n.tld\" prefix=\"i18n\" %>",segment.toString());
		segment=i.next();
		assertEquals("\r\n",segment.toString());
		segment=i.next();
		assertEquals(StartTagType.XML_PROCESSING_INSTRUCTION,((Tag)segment).getTagType());
		assertEquals("<?xml-stylesheet href=\"standardstyle.css\" title=\"Standard Stylesheet\" type=\"text/css\"?>",segment.toString());
		segment=i.next();
		assertEquals("\r\n",segment.toString());
		segment=i.next();
		assertEquals(StartTagType.DOCTYPE_DECLARATION,((Tag)segment).getTagType());
		assertEquals("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\" [\r\n <!ELEMENT greeting (#PCDATA)>\r\n <!ENTITY p CDATA \"<p>\">\r\n]>",segment.toString());
		segment=i.next();
		assertEquals(StartTagType.MARKUP_DECLARATION,((Tag)segment).getTagType());
		assertEquals("<!ELEMENT greeting (#PCDATA)>",segment.toString());
		segment=i.next();
		assertEquals(StartTagType.MARKUP_DECLARATION,((Tag)segment).getTagType());
		assertEquals("<!ENTITY p CDATA \"<p>\">",segment.toString());
		for (int x=0; x<7; x++) segment=i.next();
		assertEquals("Jericho HTML Parser Test Document",segment.toString());
		for (int x=0; x<5; x++) segment=i.next();
		startTag=(StartTag)segment;
		assertEquals(StartTagType.NORMAL,startTag.getTagType());
		assertEquals("<meta name=\"keywords\" content=\"HTML parser,test document,R&amp;D\" />",segment.toString());
		assertEquals("HTML parser,test document,R&D",startTag.getAttributeValue("content")); // note that character reference inside attribute value is not handled as a separate segment
		for (int x=0; x<8; x++) segment=i.next();

		StringBuilder sb=new StringBuilder();
		segment=i.next();
		assertEquals("This paragraph contains character references: ",segment.toString());
		sb.append(segment);
		segment=i.next();
		assertEquals("&euro;",segment.toString());
		CharacterEntityReference characterEntityReference=(CharacterEntityReference)segment;
		characterEntityReference.appendCharTo(sb);
		segment=i.next();
		assertEquals(" and ",segment.toString());
		sb.append(segment);
		segment=i.next();
		assertEquals("&#169;",segment.toString());
		NumericCharacterReference numericCharacterReference=(NumericCharacterReference)segment;
		numericCharacterReference.appendCharTo(sb);
		segment=i.next();
		assertEquals(".",segment.toString());
		sb.append(segment);
		assertEquals("This paragraph contains character references: \u20AC and \u00A9.",sb.toString());
		for (int x=0; x<3; x++) segment=i.next();
		startTag=(StartTag)segment;
		assertEquals(StartTagType.COMMENT,startTag.getTagType());
		assertEquals("<!-- <p>This paragraph is commented out</p> -->",segment.toString());
		assertEquals(" <p>This paragraph is commented out</p> ",startTag.getTagContent().toString());
		segment=i.next();
		assertEquals("\r\n",segment.toString()); // note that <p> tag isn't found inside comment
		segment=i.next();
		assertEquals("<input type=\"button\" value=\"Click here to execute script\" title=\"simply writes some text using document.write\"\r\n  onclick=\"document.write('<h2>This element is defined inside an onclick attribute</h2>')\"/>",segment.toString());
		segment=i.next();
		assertEquals("\r\n",segment.toString()); // note that <h2> tag isn't found inside the attribute value
		for (int x=0; x<5; x++) segment=i.next();
		startTag=(StartTag)segment;
		assertEquals(StartTagType.CDATA_SECTION,startTag.getTagType());
		assertEquals("<![CDATA[\r\n <an> example of <sgml> markup that is not <painful> to write with < and such.\r\n]]>",segment.toString());
		assertEquals("\r\n <an> example of <sgml> markup that is not <painful> to write with < and such.\r\n",startTag.getTagContent().toString());
		segment=i.next();
		segment=i.next();
		assertEquals("<script language=\"javascript\" type=\"text/javascript\">",segment.toString());
		segment=i.next();
		// note that <p> tag isn't recognised inside <script> element
		segment=i.next();
		assertEquals("</script>",segment.toString());
		segment=i.next();
		segment=i.next();
		assertEquals("<script language=\"javascript\" type=\"text/javascript\">",segment.toString());
		segment=i.next();
		// note that CDATA section isn't recognised inside <script> element
		segment=i.next();
		assertEquals("</script>",segment.toString());
		segment=i.next();
		segment=i.next();
		assertEquals("<script language=\"javascript\" type=\"text/javascript\">",segment.toString());
		segment=i.next();
		segment=i.next();
		startTag=(StartTag)segment;
		assertEquals(StartTagType.COMMENT,startTag.getTagType()); // note that a comment is recognised inside <script> element, whereas anything else isn't.
		segment=i.next();
		segment=i.next();
		assertEquals("</script>",segment.toString());
		segment=i.next();
		segment=i.next();
		startTag=(StartTag)segment;
		assertEquals(StartTagType.COMMENT,startTag.getTagType());
		segment=i.next();
		assertEquals("<% server tag %>",segment.toString()); // server tag is recognised inside comment
		segment=i.next();
		assertEquals("\r\n",segment.toString()); // processing instruction isn't recognised inside comment
		segment=i.next();
		assertEquals("<hr>",segment.toString());
		segment=i.next();
		assertEquals("\r\n<*abc def=\"ghi\">\r\n This is an example of an element from a hypothetical server language \r\n whose tag formats have not been registered with the TagTypeRegister class \r\n</*abc>\r\n",segment.toString());
		segment=i.next();
		assertEquals("<p>",segment.toString());
		segment=i.next();
		assertEquals(20071,segment.length());
		segment=i.next();
		assertEquals("</p>",segment.toString());
		segment=i.next();
		segment=i.next();
		startTag=(StartTag)segment;
		assertEquals(StartTagType.COMMENT,startTag.getTagType());
		assertEquals(20077,segment.length());
		segment=i.next();
		assertTrue(i.hasNext());
		segment=i.next();
		assertEquals("</html>",segment.toString());
		assertTrue(i.hasNext());
		segment=i.next();
		assertEquals("\r\n",segment.toString());
		assertFalse(i.hasNext());
		try {
			segment=i.next();
			fail("Should throw NoSuchElementException");
		} catch (NoSuchElementException ex) {}
	}

	@Test public void testLegacyIteratorCompatabilityMode() throws Exception {
		Source source=new Source(new URL(sourceUrlString));
		Segment segment;
		Source.LegacyIteratorCompatabilityMode=true;
		Iterator<Segment> i=source.iterator();
		Source.LegacyIteratorCompatabilityMode=false;
		for (int x=0; x<30; x++) segment=i.next();
		segment=i.next();
		assertEquals("This paragraph contains character references: &euro; and &#169;.",segment.toString());
		assertEquals("This paragraph contains character references: \u20AC and \u00A9.",CharacterReference.decode(segment.toString()));
	}

	@Test public void testCharacterReferences() throws Exception {
		String sourceText="&amp;<a>&amp;<b title=\"&amp;\">ww&amp;<c>&amp;xx<d>yy&amp;zz&amp;<e>&amp;";
		Source source=new Source(sourceText);
		Segment segment;
		Iterator<Segment> i=source.iterator();
		assertTrue(i.next() instanceof CharacterReference);
		assertTrue(i.next() instanceof Tag);
		assertTrue(i.next() instanceof CharacterReference);
		assertTrue(i.next() instanceof Tag);
		assertEquals("ww",i.next().toString());
		assertTrue(i.next() instanceof CharacterReference);
		assertTrue(i.next() instanceof Tag);
		assertTrue(i.next() instanceof CharacterReference);
		assertEquals("xx",i.next().toString());
		assertTrue(i.next() instanceof Tag);
		assertEquals("yy",i.next().toString());
		assertTrue(i.next() instanceof CharacterReference);
		assertEquals("zz",i.next().toString());
		assertTrue(i.next() instanceof CharacterReference);
		assertTrue(i.next() instanceof Tag);
		assertTrue(i.next() instanceof CharacterReference);
	}

/*
	@Test public void benchmark() throws Exception {
		for (int i=0; i<5000; i++) {
			for (Segment segment : new Source(new URL(sourceUrlString))) {}
		}
	}
*/
}
