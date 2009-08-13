package net.htmlparser.jericho;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class SegmentTest {
	private static final String sourceUrlString="file:test/data/SegmentTest.html";

	@Test public void test() throws Exception {
		Source source=new Source(new URL(sourceUrlString));
		assertEquals("<HTML>",source.getFirstStartTag().toString());
		assertEquals("SegmentTest",source.getFirstElement("title").getContent().toString());
		assertEquals("document keywords",source.getFirstStartTag("name","keywords",false).getAttributeValue("content"));
		assertEquals("<!--<p>commented p</p>-->",source.getFirstStartTag(StartTagType.COMMENT).toString());
		
		Segment outerDiv=source.getElementById("OuterDiv");
		List<Element> elements=outerDiv.getAllElements();
		assertEquals(4,elements.size()); // outerDiv itself plus 3 contained elements
		assertEquals(StartTagType.COMMENT,elements.get(2).getStartTag().getTagType());
		assertEquals(1,outerDiv.getAllElements(StartTagType.COMMENT).size());
		assertEquals(2,outerDiv.getAllElements(HTMLElementName.P).size());
		
		Segment testSegment=new Segment(source,outerDiv.getBegin(),source.getElementById("p2").getStartTag().getEnd()+1); // this segment ends in the middle of the content of p2.
		assertEquals(4,testSegment.getAllStartTags().size()); // outerDiv start tag plus 3 contained start tags
		assertEquals(2,testSegment.getAllElements().size()); // only p1 and comment, as outerDiv and p2 are not enclosed by testSegment
		assertNull(source.getFirstStartTag(StartTagType.COMMENT).getFirstStartTag(HTMLElementName.P));
		assertNull(testSegment.getFirstElement("id","p2",true));
		
		assertEquals(0,source.getAllElementsByClass("de").size());
		List<Element> defElements=source.getAllElementsByClass("def");
		assertEquals(4,defElements.size());
		assertEquals("p4",defElements.get(0).getContent().toString());
		assertEquals("p5",defElements.get(1).getContent().toString());
		assertEquals("p6",defElements.get(2).getContent().toString());
		assertEquals("p7",defElements.get(3).getContent().toString());
	}
}
