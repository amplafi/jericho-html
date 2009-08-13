package net.htmlparser.jericho;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.CharBuffer;
import java.nio.BufferOverflowException;

public class StreamedSourceTest {
	private static final String sourceUrlString="file:test/data/StreamedSourceTest.html";

	@Test public void testDefault() throws Exception {
		StreamedSource streamedSource=null;
		Segment segment;
		StartTag startTag;
		try {
			int originalInitialExpandableBufferSize=StreamedText.INITIAL_EXPANDABLE_BUFFER_SIZE;
			StreamedText.INITIAL_EXPANDABLE_BUFFER_SIZE=120;
			streamedSource=new StreamedSource(new URL(sourceUrlString));
			StreamedText.INITIAL_EXPANDABLE_BUFFER_SIZE=originalInitialExpandableBufferSize;
			assertNull(streamedSource.getCurrentSegment()); // doesn't have a value until iterator().next() is called
			assertEquals("UTF-8",streamedSource.getEncoding());
			try {
				streamedSource.isXML();
				fail("Should throw IllegalStateException");
			} catch (IllegalStateException ex) {
				assertEquals("isXML() method only available after iterator() has been called",ex.getMessage());
			}
			Iterator<Segment> i=streamedSource.iterator();
			assertTrue(streamedSource.isXML());
			assertTrue(i.hasNext());
			assertNull(streamedSource.getCurrentSegment()); // doesn't have a value until i.next() is called
			segment=i.next();
			assertSame(segment,streamedSource.getCurrentSegment());
			assertEquals(StartTagType.XML_DECLARATION,((Tag)segment).getTagType());
			assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>",segment.toString());
			assertEquals("<?xml ?> (XML declaration) ((p0)-(p39))",segment.getDebugInfo()); // note row and column information is not included
			try {
				segment.getSource();
				fail("Should throw UnsupportedOperationException");
			} catch (UnsupportedOperationException ex) {
				assertEquals("Source object is not available when using StreamedSource",ex.getMessage());
			}
			assertTrue(i.hasNext());
			segment=i.next();
			assertEquals("\r\n",segment.toString());
			assertTrue(i.hasNext());
			segment=i.next();
			assertEquals(StartTagType.SERVER_COMMON,((Tag)segment).getTagType());
			assertEquals("<%@ page language=\"java\" %>",segment.toString());
			assertEquals(120,streamedSource.getBufferSize());
			CharBuffer charBuffer=streamedSource.getCurrentSegmentCharBuffer();
			char[] charBufferArray=charBuffer.array();
			assertEquals(120,charBufferArray.length);
			assertEquals(segment.toString(),new String(charBufferArray,charBuffer.position(),charBuffer.length()));
			segment=i.next();
			assertEquals(StartTagType.SERVER_COMMON,((Tag)segment).getTagType());
			assertEquals("<%@ taglib uri=\"/WEB-INF/struts-i18n.tld\" prefix=\"i18n\" %>",segment.toString());
			segment=i.next();
			assertEquals("\r\n",segment.toString());
			segment=i.next();
			assertEquals(StartTagType.XML_PROCESSING_INSTRUCTION,((Tag)segment).getTagType());
			assertEquals("<?xml-stylesheet href=\"standardstyle.css\" title=\"Standard Stylesheet\" type=\"text/css\"?>",segment.toString());
			assertEquals(120,streamedSource.getBufferSize());
			segment=i.next();
			assertEquals(240,streamedSource.getBufferSize()); // last next() call fetches the next text segment as well as the following tag, which totals > 120 characters
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
			try {
				startTag.getElement();
				fail("Should throw UnsupportedOperationException");
			} catch (UnsupportedOperationException ex) {
				assertEquals("Elements are not supported when using StreamedSource",ex.getMessage());
			}
			for (int x=0; x<8; x++) segment=i.next();
			StringWriter plainTextWriter=new StringWriter();
			segment=i.next();
			assertEquals("This paragraph contains character references: ",segment.toString());
			plainTextWriter.append(segment);
			segment=i.next();
			assertEquals("&euro;",segment.toString());
			CharacterEntityReference characterEntityReference=(CharacterEntityReference)segment;
			characterEntityReference.appendCharTo(plainTextWriter);
			segment=i.next();
			assertEquals(" and ",segment.toString());
			plainTextWriter.append(segment);
			segment=i.next();
			assertEquals("&#169;",segment.toString());
			NumericCharacterReference numericCharacterReference=(NumericCharacterReference)segment;
			numericCharacterReference.appendCharTo(plainTextWriter);
			segment=i.next();
			assertEquals(".",segment.toString());
			plainTextWriter.append(segment);
			assertEquals("This paragraph contains character references: \u20AC and \u00A9.",plainTextWriter.toString());
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
			assertEquals(240,streamedSource.getBufferSize());
			segment=i.next();
			assertEquals(480,streamedSource.getBufferSize()); // last next() call fetches the next text segment as well as the following comment, which totals > 240 characters
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
			assertEquals(480,streamedSource.getBufferSize());

			// coming up is the very long text segment of 20071 characters
			for (int x=0; x<41; x++) {
				// because coalescing is false, it is handled as 41 separate segments of 480 characters (filling the available buffer), plus one extra segment of 391 characters to make up the entire 20071 characters.
				segment=i.next();
				assertEquals(480,segment.length());
			}
			segment=i.next(); 
			assertEquals(391,segment.length()); // last chunk of the large text
			assertEquals(480,streamedSource.getBufferSize()); // buffer hasn't been expanded because we are not coalescing

			segment=i.next();
			assertEquals("</p>",segment.toString());
			assertEquals(480,streamedSource.getBufferSize());

			segment=i.next(); // fetches whitespace and pre-fetches very long comment 
			assertEquals(30720,streamedSource.getBufferSize());
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
			assertEquals(30720,streamedSource.getBufferSize());
			assertFalse(i.hasNext());
			try {
				segment=i.next();
				fail("Should throw NoSuchElementException");
			} catch (NoSuchElementException ex) {}
		} finally {
			if (streamedSource!=null) streamedSource.close();
		}
	}

	@Test public void testCoalescing() throws Exception {
		StreamedSource streamedSource=null;
		Segment segment;
		StartTag startTag;
		try {
			int originalInitialExpandableBufferSize=StreamedText.INITIAL_EXPANDABLE_BUFFER_SIZE;
			StreamedText.INITIAL_EXPANDABLE_BUFFER_SIZE=120;
			streamedSource=new StreamedSource(new URL(sourceUrlString)).setCoalescing(true);
			StreamedText.INITIAL_EXPANDABLE_BUFFER_SIZE=originalInitialExpandableBufferSize;
			// behaviour is identical to non-coalescing until we hit a large non-tag text segment
			Iterator<Segment> i=streamedSource.iterator();
			segment=i.next();
			for (int x=0; x<66; x++) segment=i.next();
			assertEquals("<p>",segment.toString());
			assertEquals(480,streamedSource.getBufferSize());

			segment=i.next(); // fetches very long text segment of 20071 characters
			assertEquals(20071,segment.length());
			assertEquals(30720,streamedSource.getBufferSize()); // buffer has been expanded because we are coalescing
			assertEquals(20071,segment.toString().length()); // all plain text up to the next tag is returned in one segment

			segment=i.next();
			assertEquals("</p>",segment.toString());
			segment=i.next(); // fetches whitespace and pre-fetches very long comment 
			segment=i.next();
			startTag=(StartTag)segment;
			assertEquals(StartTagType.COMMENT,startTag.getTagType());
			assertEquals(20077,segment.length());
			assertEquals(30720,streamedSource.getBufferSize()); // buffer was already big enough to fit it so didn't change
			segment=i.next();
			segment=i.next();
			assertEquals("</html>",segment.toString());
			segment=i.next();
			assertEquals("\r\n",segment.toString());
			assertEquals(30720,streamedSource.getBufferSize());
			assertFalse(i.hasNext());
		} finally {
			if (streamedSource!=null) streamedSource.close();
		}
	}

	@Test public void testFixedBuffer() throws Exception {
		char[] buffer=new char[250];
		StreamedSource streamedSource=null;
		Segment segment;
		StartTag startTag;
		try {
			streamedSource=new StreamedSource(new URL(sourceUrlString)).setBuffer(buffer);
			Iterator<Segment> i=streamedSource.iterator();
			assertTrue(i.hasNext());
			segment=i.next();
			assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>",segment.toString());
			segment=i.next();
			assertEquals("\r\n",segment.toString());
			for (int x=0; x<14; x++) segment=i.next();
			segment=i.next();
			assertEquals("Jericho HTML Parser Test Document",segment.toString());
			for (int x=0; x<50; x++) segment=i.next();
			assertEquals("<p>",segment.toString());
			// coming up is the very long text segment of 20071 characters
			for (int x=0; x<80; x++) {
				// because coalescing is false, it is handled as 80 separate segments of 250 characters (filling the available buffer), plus one extra segment of 71 characters to make up the entire 20071 characters.
				segment=i.next();
				assertEquals(250,segment.length());
			}
			segment=i.next(); 
			assertEquals(71,segment.length()); // last chunk of the large text
			segment=i.next();
			assertEquals("</p>",segment.toString());
			assertEquals(250,streamedSource.getBufferSize());
			try {
				i.hasNext();
				// This call to hasNext() attempts to fetch the large comment.
				// This causes a BufferOverflowException because the comment is a tag, which can't be chunked like plain text and must fit entirely in the buffer.
				// Because we are using a fixed buffer, it is not able to expand to accommodate the large tag and throws the exception.
				fail("Should throw BufferOverflowException");
			} catch (BufferOverflowException ex) {}
		} finally {
			if (streamedSource!=null) streamedSource.close();
		}
	}

	@Test public void testCharSequence() throws Exception {
		String sourceText=new Source(new URL(sourceUrlString)).toString(); // convenient way of getting text from URL
		StreamedSource streamedSource=null;
		Segment segment;
		StartTag startTag;
		try {
			streamedSource=new StreamedSource(sourceText);
			assertEquals(42565,streamedSource.getBufferSize()); // covers entire document
			Iterator<Segment> i=streamedSource.iterator();
			segment=i.next();
			for (int x=0; x<11; x++) segment=i.next();
			assertEquals("<HTML>",segment.toString());
			StartTag htmlStartTag=(StartTag)segment;
			for (int x=0; x<55; x++) segment=i.next();
			assertEquals("<p>",segment.toString());
			segment=i.next(); // fetches very long text segment
			assertEquals(20071,segment.toString().length()); // can get text using normal segment.toString()
			for (int x=0; x<3; x++) segment=i.next();
			startTag=(StartTag)segment;
			assertEquals(StartTagType.COMMENT,startTag.getTagType());
			assertEquals(20077,segment.toString().length());
			segment=i.next();
			segment=i.next();
			assertEquals("</html>",segment.toString());
			segment=i.next();
			assertEquals("\r\n",segment.toString());
			assertFalse(i.hasNext());
			try {
				htmlStartTag.getElement();
				fail("Should throw UnsupportedOperationException");
			} catch (UnsupportedOperationException ex) {
				assertEquals("Elements are not supported when using StreamedSource",ex.getMessage());
			}
		} finally {
			if (streamedSource!=null) streamedSource.close();
		}
	}

	@Test public void exampleFetchElementContent() throws Exception {
		List<String> paragraphTextList=new ArrayList<String>();
		StreamedSource streamedSource=null;
		try {
			streamedSource=new StreamedSource(new URL(sourceUrlString));
			StringBuilder sb=new StringBuilder();
			boolean insideParagraphElement=false;
			for (Segment segment : streamedSource) {
				if (segment instanceof Tag) {
					Tag tag=(Tag)segment;
					if (tag.getName().equals("p")) {
						if (tag instanceof StartTag) {
							insideParagraphElement=true;
							sb.setLength(0);
						} else {
							insideParagraphElement=false;
							paragraphTextList.add(sb.toString());
						}
					}
				} else if (insideParagraphElement) {
					if (segment instanceof CharacterReference) {
						((CharacterReference)segment).appendCharTo(sb);
					} else {
						sb.append(segment);
					}
				}
			}
		} finally {
			if (streamedSource!=null) streamedSource.close();
		}
		assertEquals(3,paragraphTextList.size());
		assertEquals("This paragraph contains character references: \u20AC and \u00A9.",paragraphTextList.get(0));
		assertEquals("The following text demonstrates the use of a CDATA section which has limited browser compatability",paragraphTextList.get(1));
		assertEquals(20071,paragraphTextList.get(2).length());
	}

/*
	@Test public void benchmark() throws Exception {
		for (int i=0; i<5000; i++) {
			for (Segment segment : new StreamedSource(new URL(sourceUrlString))) {}
		}
	}
*/
}
