package net.htmlparser.jericho;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.nio.*;

public class StreamedTextTest {
	private static final String text="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	@Test public void testFixedBuffer() {
		Reader reader=new StringReader(text);
		char[] buffer=new char[10];
		StreamedText streamedText=new StreamedText(reader,buffer);
		try {
			streamedText.length();
			fail("Should throw IllegalStateException");
		} catch (IllegalStateException ex) {
			assertEquals("Length of streamed text cannot be determined until end of file has been reached",ex.getMessage());
		}
		assertEquals("",streamedText.getCurrentBufferContent());
		assertEquals('C',streamedText.charAt(2));
		assertEquals("ABCDEFGHIJ",streamedText.getCurrentBufferContent());
		assertEquals('B',streamedText.charAt(1));
		assertEquals('A',streamedText.charAt(0));
		assertEquals('J',streamedText.charAt(9));
		try {
			streamedText.charAt(10);
			fail("Should throw BufferOverflowException");
		} catch (BufferOverflowException ex) {}
		streamedText.setMinRequiredBufferBegin(8);
		streamedText.setMinRequiredBufferBegin(5); // allowed to do this because position 5 is still in the buffer
		assertEquals('A',streamedText.charAt(0)); // only really gets discarded when we need to read more text into the buffer
		assertEquals('J',streamedText.charAt(9));
		assertEquals("ABCDEFGHIJ",streamedText.getCurrentBufferContent());
		assertEquals('K',streamedText.charAt(10));
		assertEquals("FGHIJKLMNO",streamedText.getCurrentBufferContent());
		assertEquals('F',streamedText.charAt(5));
		try {
			streamedText.charAt(4);
			fail("Should throw IllegalStateException");
		} catch (IllegalStateException ex) {
			assertEquals("StreamedText position 4 has been discarded",ex.getMessage());
		}
		assertEquals('O',streamedText.charAt(14));
		try {
			streamedText.charAt(15);
			fail("Should throw BufferOverflowException");
		} catch (BufferOverflowException ex) {}
		assertEquals("IJKL",streamedText.subSequence(8,12).toString());
		CharBuffer charBuffer=streamedText.getCharBuffer(8,12);
		char[] charBufferArray=charBuffer.array();
		assertEquals("IJKL",new String(charBufferArray,charBuffer.position(),charBuffer.length()));
		assertEquals("FGHIJKLMNO",streamedText.substring(5,15));
		try {
			streamedText.setMinRequiredBufferBegin(3);
			fail("Should throw IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
			assertEquals("Cannot set minimum required buffer begin to already discarded position 3",ex.getMessage());
		}
		streamedText.setMinRequiredBufferBegin(20);
		assertEquals("FGHIJKLMNO",streamedText.getCurrentBufferContent());
		assertEquals('O',streamedText.charAt(14));
		try {
			streamedText.charAt(16); // causes reader to skip to MinRequiredBufferBegin and empty buffer
			fail("Should throw IllegalStateException");
		} catch (IllegalStateException ex) {
			assertEquals("StreamedText position 16 has been discarded",ex.getMessage());
		}
		assertEquals("",streamedText.getCurrentBufferContent());
		assertEquals('U',streamedText.charAt(20));
		assertEquals("UVWXYZ0123",streamedText.getCurrentBufferContent());
		streamedText.setMinRequiredBufferBegin(30);
		try {
			streamedText.length();
			fail("Should throw IllegalStateException");
		} catch (IllegalStateException ex) {}
		assertEquals('4',streamedText.charAt(30));
		assertEquals("456789",streamedText.getCurrentBufferContent());
		assertEquals('9',streamedText.charAt(35));
		try {
			streamedText.length();
			fail("Should throw IllegalStateException"); // although we are at the end of the stream, the StreamedText object doesn't know that yet because the Reader class doesn't have a method to check for EOF.
		} catch (IllegalStateException ex) {}
		try {
			streamedText.charAt(36);
			fail("Should throw IndexOutOfBoundsException");
		} catch (IndexOutOfBoundsException ex) {}
		assertEquals(36,streamedText.length());
	}

	@Test public void testExpandableBuffer() {
		Reader reader=new StringReader(text);
		int originalInitialExpandableBufferSize=StreamedText.INITIAL_EXPANDABLE_BUFFER_SIZE;
		StreamedText.INITIAL_EXPANDABLE_BUFFER_SIZE=2;
		StreamedText streamedText=new StreamedText(reader);
		StreamedText.INITIAL_EXPANDABLE_BUFFER_SIZE=originalInitialExpandableBufferSize;
		assertEquals(2,streamedText.getBuffer().length);
		assertEquals('B',streamedText.charAt(1));
		assertEquals(2,streamedText.getBuffer().length);
		assertEquals("AB",streamedText.getCurrentBufferContent());
		assertEquals('E',streamedText.charAt(4));
		assertEquals(5,streamedText.getBuffer().length);
		assertEquals("ABCDE",streamedText.getCurrentBufferContent());
		assertEquals('B',streamedText.charAt(1));
		assertEquals('A',streamedText.charAt(0));
		assertEquals('F',streamedText.charAt(5));
		assertEquals(10,streamedText.getBuffer().length);
		assertEquals("ABCDEFGHIJ",streamedText.getCurrentBufferContent());
		streamedText.setMinRequiredBufferBegin(8);
		assertEquals('A',streamedText.charAt(0)); // only really gets discarded when we need to read more text into the buffer
		assertEquals('M',streamedText.charAt(12));
		assertEquals("IJKLMNOPQR",streamedText.getCurrentBufferContent());
		streamedText.setMinRequiredBufferBegin(20);
		assertEquals(10,streamedText.getBuffer().length);
		assertEquals('5',streamedText.charAt(31));
		assertEquals(20,streamedText.getBuffer().length);
		assertEquals("UVWXYZ0123456789",streamedText.getCurrentBufferContent());
		assertEquals('9',streamedText.charAt(35));
		try {
			streamedText.length();
			fail("Should throw IllegalStateException"); // although we are at the end of the stream, the StreamedText object doesn't know that yet because the Reader class doesn't have a method to check for EOF.
		} catch (IllegalStateException ex) {}
		try {
			streamedText.charAt(36);
			fail("Should throw IndexOutOfBoundsException");
		} catch (IndexOutOfBoundsException ex) {}
		assertEquals(36,streamedText.length());
	}

	@Test public void testEndOfFileFountWhileSkipping() {
		Reader reader=new StringReader(text);
		char[] buffer=new char[10];
		StreamedText streamedText=new StreamedText(reader,buffer);
		streamedText.setMinRequiredBufferBegin(40); // past end of stream
		try {
			streamedText.length();
			fail("Should throw IllegalStateException");
		} catch (IllegalStateException ex) {}
		try {
			streamedText.charAt(40);
			fail("Should throw IndexOutOfBoundsException");
		} catch (IndexOutOfBoundsException ex) {}
		assertEquals(36,streamedText.length());
	}
	
	@Test public void testCharBuffer() {
		char[] charArray=text.toCharArray();
		CharBuffer charBuffer=CharBuffer.wrap(charArray,0,26);
		StreamedText streamedText=new StreamedText(charBuffer);
		assertEquals(26,streamedText.length());
		assertEquals(36,streamedText.getBuffer().length);
		assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ",streamedText.getCurrentBufferContent());
		assertEquals('A',streamedText.charAt(0));
		assertEquals('Z',streamedText.charAt(25));
		streamedText.setMinRequiredBufferBegin(20);
		assertEquals('A',streamedText.charAt(0));
		try {
			streamedText.charAt(26);
			fail("Should throw IndexOutOfBoundsException");
		} catch (IndexOutOfBoundsException ex) {}
	}
}
