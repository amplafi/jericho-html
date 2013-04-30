package net.htmlparser.jericho;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.CharBuffer;

public class ScriptTest {
	private static final String sourceUrlString="file:test/data/ScriptTest.html";

	@Test public void testFullSequentialParse() throws Exception {
		Source source=new Source(new URL(sourceUrlString));
		source.fullSequentialParse();
		List<Element> scriptElements=source.getAllElements(HTMLElementName.SCRIPT);
		assertEquals(4,scriptElements.size());
		List<StartTag> scriptContentStartTags;
		StartTag scriptContentStartTag;

		scriptContentStartTags=scriptElements.get(0).getContent().getAllStartTags();
		assertEquals(0,scriptContentStartTags.size());

		scriptContentStartTags=scriptElements.get(1).getContent().getAllStartTags();
		assertEquals(0,scriptContentStartTags.size());

		scriptContentStartTags=scriptElements.get(2).getContent().getAllStartTags();
		assertEquals(0,scriptContentStartTags.size());

		scriptContentStartTags=scriptElements.get(3).getContent().getAllStartTags();
		assertEquals(2,scriptContentStartTags.size());
		scriptContentStartTag=scriptContentStartTags.get(0);
		assertEquals(HTMLElementName.P,scriptContentStartTag.getName());
		scriptContentStartTag=scriptContentStartTags.get(1);
		assertEquals(HTMLElementName.P,scriptContentStartTag.getName());
	}

	@Test public void testParseOnDemand() throws Exception {
		Source source=new Source(new URL(sourceUrlString));
		List<Element> scriptElements=source.getAllElements(HTMLElementName.SCRIPT);
		assertEquals(4,scriptElements.size());
		List<StartTag> scriptContentStartTags;
		StartTag scriptContentStartTag;

		scriptContentStartTags=scriptElements.get(0).getContent().getAllStartTags();
		assertEquals(1,scriptContentStartTags.size());
		scriptContentStartTag=scriptContentStartTags.get(0);
		assertEquals(HTMLElementName.P,scriptContentStartTag.getName());

		scriptContentStartTags=scriptElements.get(1).getContent().getAllStartTags();
		assertEquals(1,scriptContentStartTags.size());
		scriptContentStartTag=scriptContentStartTags.get(0);
		assertEquals(StartTagType.CDATA_SECTION,scriptContentStartTag.getTagType());

		scriptContentStartTags=scriptElements.get(2).getContent().getAllStartTags();
		assertEquals(2,scriptContentStartTags.size());
		scriptContentStartTag=scriptContentStartTags.get(0);
		assertEquals(StartTagType.COMMENT,scriptContentStartTag.getTagType());
		scriptContentStartTag=scriptContentStartTags.get(1);
		assertEquals(HTMLElementName.P,scriptContentStartTag.getName());

		scriptContentStartTags=scriptElements.get(3).getContent().getAllStartTags();
		assertEquals(2,scriptContentStartTags.size());
		scriptContentStartTag=scriptContentStartTags.get(0);
		assertEquals(StartTagType.COMMENT,scriptContentStartTag.getTagType());
		scriptContentStartTag=scriptContentStartTags.get(1);
		assertEquals(HTMLElementName.P,scriptContentStartTag.getName());
		assertEquals("<p>This paragraph is recognised in both modes.</p>",scriptContentStartTag.getElement().toString());
	}
}
