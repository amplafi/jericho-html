package net.htmlparser.jericho;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.CharBuffer;
import java.nio.BufferOverflowException;

public class StreamedSourceHugeFileTest {
	private static final String sourceUrlString="file:D:/Data/StreamedSourceHugeFileTest.txt";

	@Test public void testDefault() throws Exception {
		if (true) return; // disable test
		StreamedSource streamedSource=null;
		int segmentCount=0;
		try {
			int originalInitialExpandableBufferSize=StreamedText.INITIAL_EXPANDABLE_BUFFER_SIZE;
			StreamedText.INITIAL_EXPANDABLE_BUFFER_SIZE=120;
			streamedSource=new StreamedSource(new URL(sourceUrlString));
			StreamedText.INITIAL_EXPANDABLE_BUFFER_SIZE=originalInitialExpandableBufferSize;
			assertEquals(120,streamedSource.getBufferSize());
			for (Segment segment : streamedSource) {
				segmentCount++;
			}
			assertEquals(30720,streamedSource.getBufferSize());
			assertEquals(680158,segmentCount);
		} finally {
			if (streamedSource!=null) streamedSource.close();
		}
	}
	
	@Test public void exampleFetchElementContent() throws Exception {
		if (true) return; // disable test
		int paragraphCount=0;
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
							// don't actually do anything with paragraph text, just count them
							paragraphCount++;
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
		assertEquals(20000,paragraphCount);
	}
}
