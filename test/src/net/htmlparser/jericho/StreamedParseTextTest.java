package net.htmlparser.jericho;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.nio.*;

public class StreamedParseTextTest {
	private static final String text="ABCDEFGHIJKLMNOPQRSTUVWXYZ<0123456789A";

	@Test public void testExpandableBuffer() {
		Reader reader=new StringReader(text);
		int originalInitialExpandableBufferSize=StreamedText.INITIAL_EXPANDABLE_BUFFER_SIZE;
		StreamedText.INITIAL_EXPANDABLE_BUFFER_SIZE=2;
		StreamedText streamedText=new StreamedText(reader);
		StreamedText.INITIAL_EXPANDABLE_BUFFER_SIZE=originalInitialExpandableBufferSize;
		StreamedParseText streamedParseText=new StreamedParseText(streamedText);
		assertEquals('a',streamedParseText.charAt(0));
		assertEquals(2,streamedText.getBuffer().length);
		assertEquals("ab",streamedParseText.substring(0,2));
		assertEquals('c',streamedParseText.charAt(2));
		assertEquals(4,streamedText.getBuffer().length);
		int tagStartPos=streamedParseText.indexOf('<',0);
		streamedText.setMinRequiredBufferBegin(tagStartPos);
		assertEquals(26,tagStartPos);
		assertEquals(32,streamedText.getBuffer().length);
		assertEquals(7,streamedParseText.indexOf('h',5));
		assertEquals('z',streamedParseText.charAt(25));
		assertEquals(36,streamedParseText.indexOf('9',0)); // position 0 is still available in the buffer at start of search, then as search passes position 32 it is shifted to discard up to position 26 (setMinRequiredBufferBegin(26) called implicitly by writePlainTextSegment call above), allowing the rest of the text to fit in the buffer without expanding it.
		assertEquals(32,streamedText.getBuffer().length);
		assertEquals("<0123456789A",streamedText.getCurrentBufferContent());
		try {
			streamedParseText.charAt(0); // now position 0 is no longer in the buffer
			fail("Should throw IllegalStateException");
		} catch (IllegalStateException ex) {
			assertEquals("StreamedText position 0 has been discarded",ex.getMessage());
		}
		assertEquals(-1,streamedParseText.indexOf('A',30)); // search strings must be in lower case.
	}

	@Test public void testFixedBuffer() {
		Reader reader=new StringReader(text);
		char[] buffer=new char[10];
		StreamedText streamedText=new StreamedText(reader,buffer);
		StreamedParseText streamedParseText=new StreamedParseText(streamedText);
		assertEquals('a',streamedParseText.charAt(0));
		assertEquals("abc",streamedParseText.substring(0,3));
		assertEquals(7,streamedParseText.indexOf('h',5));
		assertEquals(10,streamedText.getBuffer().length);
		try {
			streamedParseText.indexOf('<',0);
			fail("Should throw BufferOverflowException");
		} catch (BufferOverflowException ex) {}
		assertEquals(10,streamedText.getBuffer().length);
		try {
			streamedParseText.indexOf('z',5);
			fail("Should throw BufferOverflowException");
		} catch (BufferOverflowException ex) {}
		try {
			streamedParseText.indexOf('H',5);
			fail("Should throw BufferOverflowException");
		} catch (BufferOverflowException ex) {}
		streamedText.setMinRequiredBufferBegin(20);
		assertEquals('z',streamedParseText.charAt(25));
		assertEquals('<',streamedParseText.charAt(26));
		assertEquals("xyz<01",streamedParseText.substring(23,29));
		streamedText.setMinRequiredBufferBegin(30);
		assertEquals(35,streamedParseText.indexOf('8',28));
		assertEquals(36,streamedParseText.indexOf('9',30));
		assertEquals(-1,streamedParseText.indexOf('A',30)); // search strings must be in lower case.
		assertEquals(37,streamedParseText.indexOf('a',30));
	}

	@Test public void testCharBuffer() {
		char[] charArray=text.toCharArray();
		CharBuffer charBuffer=CharBuffer.wrap(charArray,0,20); // no tag in document
		StreamedText streamedText=new StreamedText(charBuffer);
		StreamedParseText streamedParseText=new StreamedParseText(streamedText);
		int tagStartPos=streamedParseText.indexOf('<',15);
		assertEquals(-1,tagStartPos);
		assertEquals('a',streamedParseText.charAt(0));
		assertEquals(-1,streamedParseText.indexOf('a',18));
		streamedText.setMinRequiredBufferBegin(20);
		try {
			streamedParseText.charAt(25);
			fail("Should throw IndexOutOfBoundsException");
		} catch (IndexOutOfBoundsException ex) {}
		streamedText.setMinRequiredBufferBegin(30);
		assertEquals(1,streamedParseText.indexOf('b',0));
		charBuffer=CharBuffer.wrap(charArray,0,30); // tag in document
		streamedText=new StreamedText(charBuffer);
		streamedParseText=new StreamedParseText(streamedText);
		tagStartPos=streamedParseText.indexOf('<',15);
		assertEquals(26,tagStartPos);
		streamedText.setMinRequiredBufferBegin(tagStartPos);
		assertEquals('a',streamedParseText.charAt(0));
	}

}
