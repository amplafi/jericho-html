// Jericho HTML Parser - Java based library for analysing and manipulating HTML
// Version 2.6
// Copyright (C) 2007 Martin Jericho
// http://jerichohtml.sourceforge.net/
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of either one of the following licences:
//
// 1. The Eclipse Public License (EPL) version 1.0,
// included in this distribution in the file licence-epl-1.0.html
// or available at http://www.eclipse.org/legal/epl-v10.html
//
// 2. The GNU Lesser General Public License (LGPL) version 2.1 or later,
// included in this distribution in the file licence-lgpl-2.1.txt
// or available at http://www.gnu.org/licenses/lgpl.txt
//
// This library is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied.
// See the individual licence texts for more details.

package au.id.jericho.lib.html;

import java.util.*;
import java.io.*;

/**
 * Represents an HTML <a target="_blank" href="http://www.w3.org/TR/REC-html40/charset.html#entities">Character Reference</a>,
 * implemented by the subclasses {@link CharacterEntityReference} and {@link NumericCharacterReference}.
 * <p>
 * This class, together with its subclasses, contains static methods to perform most required operations
 * without having to instantiate an object.
 * <p>
 * Instances of this class are useful when the positions of character references in a source document are required,
 * or to replace the found character references with customised text.
 * <p>
 * <code>CharacterReference</code> instances are obtained using one of the following methods:
 * <ul>
 *  <li>{@link CharacterReference#parse(CharSequence characterReferenceText)}
 *  <li>{@link Source#findNextCharacterReference(int pos)}
 *  <li>{@link Source#findPreviousCharacterReference(int pos)}
 *  <li>{@link Segment#findAllCharacterReferences()}
 * </ul>
 */
public abstract class CharacterReference extends Segment {
	int codePoint;

	/**
	 * Represents an invalid unicode code point.
	 * <p>
	 * This can be the result of parsing a numeric character reference outside of the valid unicode range of 0x000000-0x10FFFF, or any other invalid character reference.
	 */
	public static final int INVALID_CODE_POINT=-1;

	/**
	 * The maximum codepoint allowed by unicode, 0x10FFFF (decimal 1114111).
	 * This can be replaced by Character.MAX_CODE_POINT in java 1.5
	 */
	static final int MAX_CODE_POINT=0x10FFFF;

	static int MAX_ENTITY_REFERENCE_LENGTH; // set in CharacterEntityReference static class initialisation

	/** The number of spaces used to simulate a tab when {@linkplain #encodeWithWhiteSpaceFormatting encoding with white space formatting}. */
	private static final int TAB_LENGTH=4;

	CharacterReference(final Source source, final int begin, final int end, final int codePoint) {
		super(source,begin,end);
		this.codePoint=codePoint;
	}

	/**
	 * Returns the <a target="_blank" href="http://www.unicode.org">unicode</a> code point represented by this character reference.
	 * @return the unicode code point represented by this character reference.
	 */
	public int getCodePoint() {
		return codePoint;
	}

	/**
	 * Returns the character represented by this character reference.
	 * <p>
	 * If this character reference represents a unicode
	 * <a target="_blank" href="http://www.unicode.org/glossary/#supplementary_code_point">supplimentary code point</a>,
	 * any bits outside of the least significant 16 bits of the code point are truncated, yielding an incorrect result.
	 *
	 * @return the character represented by this character reference.
	 */
	public char getChar() {
		return (char)codePoint;
	}

	/**
	 * Indicates whether this character reference is terminated by a semicolon (<code>;</code>).
	 * <p>
	 * Conversely, this library defines an <i><a name="Unterminated">unterminated</a></i> character reference as one which does
	 * not end with a semicolon.
	 * <p>
	 * The SGML specification allows unterminated character references in some circumstances, and because the
	 * HTML 4.01 specification states simply that
	 * "<a target="_blank" href="http://www.w3.org/TR/REC-html40/charset.html#entities">authors may use SGML character references</a>",
	 * it follows that they are also valid in HTML documents, although their use is strongly discouraged.
	 * <p>
	 * Unterminated character references are not allowed in <a target="_blank" href="http://www.w3.org/TR/xhtml1/">XHTML</a> documents.
	 *
	 * @return <code>true</code> if this character reference is terminated by a semicolon, otherwise <code>false</code>.
	 * @see #decode(CharSequence encodedText, boolean insideAttributeValue)
	 */
	public boolean isTerminated() {
		return source.charAt(end-1)==';';
	}

	/**
	 * Encodes the specified text, escaping special characters into character references.
	 * <p>
	 * Each character is encoded only if the {@link #requiresEncoding(char)} method would return <code>true</code> for that character,
	 * using its {@link CharacterEntityReference} if available, or a decimal {@link NumericCharacterReference} if its unicode
	 * code point is greater than U+007F.
	 * <p>
	 * The only exception to this is an {@linkplain CharacterEntityReference#_apos apostrophe} (U+0027),
	 * which depending on the current setting of the static {@link Config#IsApostropheEncoded} property,
	 * is either left unencoded (default setting), or encoded as the numeric character reference "<code>&amp;#39;</code>".
	 * <p>
	 * This method never encodes an apostrophe into its character entity reference {@link CharacterEntityReference#_apos &amp;apos;}
	 * as this entity is not defined for use in HTML.  See the comments in the {@link CharacterEntityReference} class for more information.
	 * <p>
	 * To encode text using only numeric character references, use the<br />
	 * {@link NumericCharacterReference#encode(CharSequence)} method instead.
	 *
	 * @param unencodedText  the text to encode.
	 * @return the encoded string.
	 * @see #decode(CharSequence)
	 */
	public static String encode(final CharSequence unencodedText) {
		if (unencodedText==null) return null;
		return appendEncode(new StringBuffer(unencodedText.length()*2),unencodedText,false).toString();
	}

	/**
	 * Encodes the specified character into a character reference if {@linkplain #requiresEncoding(char) required}.
	 * <p>
	 * The encoding of the character follows the same rules as for each character in the {@link #encode(CharSequence unencodedText)} method.
	 *
	 * @param ch  the character to encode.
	 * @return a character reference if appropriate, otherwise a string containing the original character.
	 */
	public static String encode(final char ch) {
		return appendEncode(new StringBuffer(MAX_ENTITY_REFERENCE_LENGTH),ch).toString();
	}

	/**
	 * {@linkplain #encode(CharSequence) Encodes} the specified text, preserving line breaks, tabs and spaces for rendering by converting them to markup.
	 * <p>
	 * This performs the same encoding as the {@link #encode(CharSequence)} method, but also performs the following conversions:
	 * <ul>
	 *  <li>Line breaks, being Carriage Return (U+000D) or Line Feed (U+000A) characters, and Form Feed characters (U+000C)
	 *   are converted to "<code>&lt;br /&gt;</code>".  CR/LF pairs are treated as a single line break.
	 *  <li>Multiple consecutive spaces are converted so that every second space is converted to "<code>&amp;nbsp;</code>"
	 *   while ensuring the last is always a normal space.
	 *  <li>Tab characters (U+0009) are converted as if they were four consecutive spaces.
	 * </ul>
	 * <p>
	 * The conversion of multiple consecutive spaces to alternating space/non-breaking-space allows the correct number of
	 * spaces to be rendered, but also allows the line to wrap in the middle of it.
	 * <p>
	 * Note that zero-width spaces (U+200B) are converted to the numeric character reference
	 * "<code>&amp;#x200B;</code>" through the normal encoding process, but IE6 does not render them properly
	 * either encoded or unencoded.
	 * <p>
	 * There is no method provided to reverse this encoding.
	 *
	 * @param unencodedText  the text to encode.
	 * @return the encoded string with white space formatting converted to markup.
	 * @see #encode(CharSequence)
	 */
	public static String encodeWithWhiteSpaceFormatting(final CharSequence unencodedText) {
		if (unencodedText==null) return null;
		return appendEncode(new StringBuffer(unencodedText.length()*2),unencodedText,true).toString();
	}

	/**
	 * Decodes the specified HTML encoded text into normal text.
	 * <p>
	 * All {@linkplain CharacterEntityReference character entity references} and {@linkplain NumericCharacterReference numeric character references}
	 * are converted to their respective characters.
	 * <p>
	 * This is equivalent to {@link #decode(CharSequence,boolean) decode(encodedText,false)}.
	 * <p>
	 * <a href="#Unterminated">Unterminated</a> character references are dealt with according to the rules for
	 * text outside of attribute values in the {@linkplain Config#CurrentCompatibilityMode current compatibility mode}.
	 * <p>
	 * If the static {@link Config#ConvertNonBreakingSpaces} property is set to <code>true</code> (the default),
	 * then all non-breaking space ({@link CharacterEntityReference#_nbsp &amp;nbsp;}) character entity references are converted to normal spaces.
	 * <p>
	 * Although character entity reference names are case sensitive, and in some cases differ from other entity references only by their case,
	 * some browsers also recognise them in a case-insensitive way.
	 * For this reason, all decoding methods in this library recognise character entity reference names even if they are in the wrong case.
	 *
	 * @param encodedText  the text to decode.
	 * @return the decoded string.
	 * @see #encode(CharSequence)
	 */
	public static String decode(final CharSequence encodedText) {
		return decode(encodedText,false,Config.ConvertNonBreakingSpaces);
	}

	/**
	 * Decodes the specified HTML encoded text into normal text.
	 * <p>
	 * All {@linkplain CharacterEntityReference character entity references} and {@linkplain NumericCharacterReference numeric character references}
	 * are converted to their respective characters.
	 * <p>
	 * <a href="#Unterminated">Unterminated</a> character references are dealt with according to the
	 * value of the <code>insideAttributeValue</code> parameter and the
	 * {@linkplain Config#CurrentCompatibilityMode current compatibility mode}.
	 * <p>
	 * If the static {@link Config#ConvertNonBreakingSpaces} property is set to <code>true</code> (the default),
	 * then all non-breaking space ({@link CharacterEntityReference#_nbsp &amp;nbsp;}) character entity references are converted to normal spaces.
	 * <p>
	 * Although character entity reference names are case sensitive, and in some cases differ from other entity references only by their case,
	 * some browsers also recognise them in a case-insensitive way.
	 * For this reason, all decoding methods in this library recognise character entity reference names even if they are in the wrong case.
	 *
	 * @param encodedText  the text to decode.
	 * @param insideAttributeValue  specifies whether the encoded text is inside an attribute value.
	 * @return the decoded string.
	 * @see #decode(CharSequence)
	 * @see #encode(CharSequence)
	 */
	public static String decode(final CharSequence encodedText, final boolean insideAttributeValue) {
		return decode(encodedText,insideAttributeValue,Config.ConvertNonBreakingSpaces);
	}

	private static String decode(final CharSequence encodedText, final boolean insideAttributeValue, final boolean convertNonBreakingSpaces) {
		if (encodedText==null) return null;
		for (int i=0; i<encodedText.length(); i++) {
			if (encodedText.charAt(i)=='&')
				return appendDecode(new StringBuffer(encodedText.length()),encodedText,i,insideAttributeValue,convertNonBreakingSpaces).toString();
		}
		return encodedText.toString();
	}

	/**
	 * {@linkplain #decode(CharSequence) Decodes} the specified text after collapsing its {@linkplain #isWhiteSpace(char) white space}.
	 * <p>
	 * All leading and trailing white space is omitted, and any sections of internal white space are replaced by a single space.
	 * <p>
	 * The result is how the text would normally be rendered by a
	 * <a target="_blank" href="http://www.w3.org/TR/html401/conform.html#didx-user_agent">user agent</a>,
	 * assuming it does not contain any tags.
	 * <p>
	 * If the static {@link Config#ConvertNonBreakingSpaces} property is set to <code>true</code> (the default),
	 * then all non-breaking space ({@link CharacterEntityReference#_nbsp &amp;nbsp;}) character entity references are converted to normal spaces.
	 * <p>
	 * <a href="#Unterminated">Unterminated</a> character references are dealt with according to the rules for
	 * text outside of attribute values in the {@linkplain Config#CurrentCompatibilityMode current compatibility mode}.
	 * See the discussion of the <code>insideAttributeValue</code> parameter of the {@link #decode(CharSequence, boolean insideAttributeValue)}
	 * method for a more detailed explanation of this topic.
	 *
	 * @param text  the source text
	 * @return the decoded text with collapsed white space.
	 * @see FormControl#getPredefinedValues()
	 */
	public static String decodeCollapseWhiteSpace(final CharSequence text) {
		return decodeCollapseWhiteSpace(text,Config.ConvertNonBreakingSpaces);
	}

	static String decodeCollapseWhiteSpace(final CharSequence text, final boolean convertNonBreakingSpaces) {
		return decode(appendCollapseWhiteSpace(new StringBuffer(text.length()),text),false,convertNonBreakingSpaces);
	}

	/**
	 * Re-encodes the specified text, equivalent to {@linkplain #decode(CharSequence) decoding} and then {@linkplain #encode(CharSequence) encoding} again.
	 * <p>
	 * This process ensures that the specified encoded text does not contain any remaining unencoded characters.
	 * <p>
	 * IMPLEMENTATION NOTE: At present this method simply calls the {@link #decode(CharSequence) decode} method
	 * followed by the {@link #encode(CharSequence) encode} method, but a more efficient implementation
	 * may be used in future.
	 *
	 * @param encodedText  the text to re-encode.
	 * @return the re-encoded string.
	 */
	public static String reencode(final CharSequence encodedText) {
		return encode(decode(encodedText,true));
	}

	/**
	 * Returns the encoded form of this character reference.
	 * <p>
	 * The exact behaviour of this method depends on the class of this object.
	 * See the {@link CharacterEntityReference#getCharacterReferenceString()} and
	 * {@link NumericCharacterReference#getCharacterReferenceString()} methods for more details.
	 * <p>
	 * <dl>
	 *  <dt>Examples:</dt>
	 *   <dd><code>CharacterReference.parse("&amp;GT;").getCharacterReferenceString()</code> returns "<code>&amp;gt;</code>"</dd>
	 *   <dd><code>CharacterReference.parse("&amp;#x3E;").getCharacterReferenceString()</code> returns "<code>&amp;#3e;</code>"</dd>
	 * </dl>
	 *
	 * @return the encoded form of this character reference.
	 * @see #getCharacterReferenceString(int codePoint)
	 * @see #getDecimalCharacterReferenceString()
	 */
	public abstract String getCharacterReferenceString();

	/**
	 * Returns the encoded form of the specified unicode code point.
	 * <p>
	 * This method returns the {@linkplain CharacterEntityReference#getCharacterReferenceString(int) character entity reference} encoded form of the unicode code point
	 * if one exists, otherwise it returns the {@linkplain #getDecimalCharacterReferenceString(int) decimal character reference} encoded form.
	 * <p>
	 * The only exception to this is an {@linkplain CharacterEntityReference#_apos apostrophe} (U+0027),
	 * which is encoded as the numeric character reference "<code>&amp;#39;</code>" instead of its character entity reference
	 * "<code>&amp;apos;</code>".
	 * <p>
	 * <dl>
	 *  <dt>Examples:</dt>
	 *   <dd><code>CharacterReference.getCharacterReferenceString(62)</code> returns "<code>&amp;gt;</code>"</dd>
	 *   <dd><code>CharacterReference.getCharacterReferenceString('&gt;')</code> returns "<code>&amp;gt;</code>"</dd>
	 *   <dd><code>CharacterReference.getCharacterReferenceString('&#9786;')</code> returns "<code>&amp;#9786;</code>"</dd>
	 * </dl>
	 *
	 * @param codePoint  the unicode code point to encode.
	 * @return the encoded form of the specified unicode code point.
	 * @see #getHexadecimalCharacterReferenceString(int codePoint)
	 */
	public static String getCharacterReferenceString(final int codePoint) {
		String characterReferenceString=null;
		if (codePoint!=CharacterEntityReference._apos) characterReferenceString=CharacterEntityReference.getCharacterReferenceString(codePoint);
		if (characterReferenceString==null) characterReferenceString=NumericCharacterReference.getCharacterReferenceString(codePoint);
		return characterReferenceString;
	}

	/**
	 * Returns the <a href="NumericCharacterReference.html#DecimalCharacterReference">decimal encoded form</a> of this character reference.
	 * <p>
	 * This is equivalent to {@link #getDecimalCharacterReferenceString(int) getDecimalCharacterReferenceString}<code>(</code>{@link #getCodePoint()}<code>)</code>.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd><code>CharacterReference.parse("&amp;gt;").getDecimalCharacterReferenceString()</code> returns "<code>&amp;#62;</code>"</dd>
	 * </dl>
	 *
	 * @return the decimal encoded form of this character reference.
	 * @see #getCharacterReferenceString()
	 * @see #getHexadecimalCharacterReferenceString()
	 */
	public String getDecimalCharacterReferenceString() {
		return getDecimalCharacterReferenceString(codePoint);
	}

	/**
	 * Returns the <a href="NumericCharacterReference.html#DecimalCharacterReference">decimal encoded form</a> of the specified unicode code point.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd><code>CharacterReference.getDecimalCharacterReferenceString('&gt;')</code> returns "<code>&amp;#62;</code>"</dd>
	 * </dl>
	 *
	 * @param codePoint  the unicode code point to encode.
	 * @return the decimal encoded form of the specified unicode code point.
	 * @see #getCharacterReferenceString(int codePoint)
	 * @see #getHexadecimalCharacterReferenceString(int codePoint)
	 */
	public static String getDecimalCharacterReferenceString(final int codePoint) {
		return appendDecimalCharacterReferenceString(new StringBuffer(),codePoint).toString();
	}

	/**
	 * Returns the <a href="NumericCharacterReference.html#HexadecimalCharacterReference">hexadecimal encoded form</a> of this character reference.
	 * <p>
	 * This is equivalent to {@link #getHexadecimalCharacterReferenceString(int) getHexadecimalCharacterReferenceString}<code>(</code>{@link #getCodePoint()}<code>)</code>.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd><code>CharacterReference.parse("&amp;gt;").getHexadecimalCharacterReferenceString()</code> returns "<code>&amp;#x3e;</code>"</dd>
	 * </dl>
	 *
	 * @return the hexadecimal encoded form of this character reference.
	 * @see #getCharacterReferenceString()
	 * @see #getDecimalCharacterReferenceString()
	 */
	public String getHexadecimalCharacterReferenceString() {
		return getHexadecimalCharacterReferenceString(codePoint);
	}

	/**
	 * Returns the <a href="NumericCharacterReference.html#HexadecimalCharacterReference">hexadecimal encoded form</a> of the specified unicode code point.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd><code>CharacterReference.getHexadecimalCharacterReferenceString('&gt;')</code> returns "<code>&amp;#x3e;</code>"</dd>
	 * </dl>
	 *
	 * @param codePoint  the unicode code point to encode.
	 * @return the hexadecimal encoded form of the specified unicode code point.
	 * @see #getCharacterReferenceString(int codePoint)
	 * @see #getDecimalCharacterReferenceString(int codePoint)
	 */
	public static String getHexadecimalCharacterReferenceString(final int codePoint) {
		return appendHexadecimalCharacterReferenceString(new StringBuffer(),codePoint).toString();
	}

	/**
	 * Returns the unicode code point of this character reference in <a target="_blank" href="http://www.unicode.org/reports/tr27/#notation">U+ notation</a>.
	 * <p>
	 * This is equivalent to {@link #getUnicodeText(int) getUnicodeText(getCodePoint())}.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd><code>CharacterReference.parse("&amp;gt;").getUnicodeText()</code> returns "<code>U+003E</code>"</dd>
	 * </dl>
	 *
	 * @return the unicode code point of this character reference in U+ notation.
	 * @see #getUnicodeText(int codePoint)
	 */
	public String getUnicodeText() {
		return getUnicodeText(codePoint);
	}

	/**
	 * Returns the specified unicode code point in <a target="_blank" href="http://www.unicode.org/reports/tr27/#notation">U+ notation</a>.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd><code>CharacterReference.getUnicodeText('&gt;')</code> returns "<code>U+003E</code>"</dd>
	 * </dl>
	 *
	 * @param codePoint  the unicode code point.
	 * @return the specified unicode code point in U+ notation.
	 */
	public static String getUnicodeText(final int codePoint) {
		return appendUnicodeText(new StringBuffer(),codePoint).toString();
	}

	static final StringBuffer appendUnicodeText(final StringBuffer sb, final int codePoint) {
		sb.append("U+");
		final String hex=Integer.toString(codePoint,16).toUpperCase();
		for (int i=4-hex.length(); i>0; i--) sb.append('0');
		sb.append(hex);
		return sb;
	}

	/**
	 * Parses a single encoded character reference text into a <code>CharacterReference</code> object.
	 * <p>
	 * The character reference must be at the start of the given text, but may contain other characters at the end.
	 * The {@link #getEnd() getEnd()} method can be used on the resulting object to determine at which character position the character reference ended.
	 * <p>
	 * If the text does not represent a valid character reference, this method returns <code>null</code>.
	 * <p>
 	 * <a href="#Unterminated">Unterminated</a> character references are always accepted, regardless of the settings in the
	 * {@linkplain Config#CurrentCompatibilityMode current compatibility mode}.
	 * <p>
	 * To decode <i>all</i> character references in a given text, use the {@link #decode(CharSequence)} method instead.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd><code>CharacterReference.parse("&amp;gt;").getChar()</code> returns '<code>&gt;</code>'</dd>
	 * </dl>
	 *
	 * @param characterReferenceText  the text containing a single encoded character reference.
	 * @return a <code>CharacterReference</code> object representing the specified text, or <code>null</code> if the text does not represent a valid character reference.
	 * @see #decode(CharSequence)
	 */
	public static CharacterReference parse(final CharSequence characterReferenceText) {
		return construct(new Source(characterReferenceText),0,Config.UnterminatedCharacterReferenceSettings.ACCEPT_ALL);
	}

	/**
	 * Parses a single encoded character reference text into a unicode code point.
	 * <p>
	 * The character reference must be at the start of the given text, but may contain other characters at the end.
	 * <p>
	 * If the text does not represent a valid character reference, this method returns {@link #INVALID_CODE_POINT}.
	 * <p>
	 * This is equivalent to {@link #parse(CharSequence) parse(characterReferenceText)}<code>.</code>{@link #getCodePoint()},
	 * except that it returns {@link #INVALID_CODE_POINT} if an invalid character reference is specified instead of throwing a
	 * <code>NullPointerException</code>.
	 * <p>
	 * <dl>
	 *  <dt>Example:</dt>
	 *  <dd><code>CharacterReference.getCodePointFromCharacterReferenceString("&amp;gt;")</code> returns <code>38</code></dd>
	 * </dl>
	 *
	 * @param characterReferenceText  the text containing a single encoded character reference.
	 * @return the unicode code point representing representing the specified text, or {@link #INVALID_CODE_POINT} if the text does not represent a valid character reference.
	 */
	public static int getCodePointFromCharacterReferenceString(final CharSequence characterReferenceText) {
		final CharacterReference characterReference=parse(characterReferenceText);
		return (characterReference!=null) ? characterReference.getCodePoint() : INVALID_CODE_POINT;
	}

	/**
	 * Indicates whether the specified character would need to be encoded in HTML text.
	 * <p>
	 * This is the case if a {@linkplain CharacterEntityReference character entity reference} exists for the character, or the unicode code point is greater than U+007F.
	 * <p>
	 * The only exception to this is an {@linkplain CharacterEntityReference#_apos apostrophe} (U+0027),
	 * which only returns <code>true</code> if the static {@link Config#IsApostropheEncoded} property
	 * is currently set to <code>true</code>.
	 *
	 * @param ch  the character to test.
	 * @return <code>true</code> if the specified character would need to be encoded in HTML text, otherwise <code>false</code>.
	 */
	public static final boolean requiresEncoding(final char ch) {
		return ch>127 || (CharacterEntityReference.getName(ch)!=null && (ch!='\'' || Config.IsApostropheEncoded));
	}

	/**
	 * Returns a filter <code>Writer</code> that {@linkplain #encode(CharSequence) encodes} all text before passing it through to the specified <code>Writer</code>.
	 *
	 * @param writer  the destination for the encoded text
	 * @return a filter <code>Writer</code> that {@linkplain #encode(CharSequence) encodes} all text before passing it through to the specified <code>Writer</code>.
	 * @see #encode(CharSequence unencodedText)
	 */
	public static Writer getEncodingFilterWriter(final Writer writer) {
		return new EncodingFilterWriter(writer);
	}

	private static final class EncodingFilterWriter extends FilterWriter {
		StringBuffer sb=new StringBuffer(MAX_ENTITY_REFERENCE_LENGTH);
		public EncodingFilterWriter(final Writer writer) {
			super(writer);
		}
		public void write(final char ch) throws IOException {
			sb.setLength(0);
			appendEncode(sb,ch);
			if (sb.length()==1)
				out.write(sb.charAt(0));
			else
				Util.appendTo(out,sb);
		}
		public void write(final int chInt) throws IOException {
			write((char)chInt);
		}
		public void write(final char[] cbuf, final int off, final int len) throws IOException {
			final int end=off+len;
			for (int i=off; i<end; i++) write(cbuf[i]);
		}
		public void write(final String str, final int off, final int len) throws IOException {
			final int end=off+len;
			for (int i=off; i<end; i++) write(str.charAt(i));
		}
	}

	private static StringBuffer appendEncode(final StringBuffer sb, char ch) {
		if (appendEncodeCheckForWhiteSpaceFormatting(sb,ch,false)) return sb;
		return sb.append(ch);
	}

	static StringBuffer appendEncode(final StringBuffer sb, CharSequence unencodedText, final boolean whiteSpaceFormatting) {
		if (unencodedText==null) return sb;
		int beginPos=0;
		int endPos=unencodedText.length();
		if (unencodedText instanceof Segment) {
			// this might improve performance slightly
			final Segment segment=(Segment)unencodedText;
			final int segmentOffset=segment.getBegin();
			beginPos=segmentOffset;
			endPos+=segmentOffset;
			unencodedText=segment.source.string;
		}
		final boolean isApostropheEncoded=Config.IsApostropheEncoded;
		for (int i=beginPos; i<endPos; i++) {
			char ch=unencodedText.charAt(i);
			if (appendEncodeCheckForWhiteSpaceFormatting(sb,ch,whiteSpaceFormatting)) continue;
			// need to process white space
			// whiteSpaceFormatting tries to simulate the formatting characters by converting them to markup
			int spaceCount;
			int nexti=i+1;
			if (ch!=' ') {
				if (ch!='\t') {
					// must be line feed, carriage return or form feed, since zero-width space should have been processed as a character reference string
					if (ch=='\r' && nexti<endPos && unencodedText.charAt(nexti)=='\n') i++; // process cr/lf pair as one line break
					sb.append("<br />"); // add line break
					continue;
				} else {
					spaceCount=TAB_LENGTH;
				}
			} else {
				spaceCount=1;
			}
			while (nexti<endPos) {
				ch=unencodedText.charAt(nexti);
				if (ch==' ')
					spaceCount+=1;
				else if (ch=='\t')
					spaceCount+=TAB_LENGTH;
				else
					break;
				nexti++;
			}
			if (spaceCount==1) {
				// handle the very common case of a single character to improve efficiency slightly
				sb.append(' ');
				continue;
			}
			if (spaceCount%2==1) sb.append(' '); // fist character is a space if we have an odd number of spaces
			while (spaceCount>=2) {
				sb.append("&nbsp; "); // use alternating &nbsp; and spaces to keep original number of spaces
				spaceCount-=2;
			}
			// note that the last character is never a nbsp, so that word wrapping won't result in a nbsp before the first character in a line
			i=nexti-1; // minus 1 because top level for loop will add it again
		}
		return sb;
	}

	private static final boolean appendEncodeCheckForWhiteSpaceFormatting(final StringBuffer sb, char ch, final boolean whiteSpaceFormatting) {
		final String characterEntityReferenceName=CharacterEntityReference.getName(ch);
		if (characterEntityReferenceName!=null) {
			if (ch=='\'') {
				if (Config.IsApostropheEncoded)
					sb.append("&#39;");
				else
					sb.append(ch);
			} else {
				CharacterEntityReference.appendCharacterReferenceString(sb,characterEntityReferenceName);
			}
		} else if (ch>127) {
			appendDecimalCharacterReferenceString(sb,ch);
		} else if (!(whiteSpaceFormatting && isWhiteSpace(ch))) {
			sb.append(ch);
		} else {
			return false;
		}
		return true;
	}

	static CharacterReference findPrevious(final Source source, final int pos) {
		return findPrevious(source,pos,Config.UnterminatedCharacterReferenceSettings.ACCEPT_ALL);
	}

	static CharacterReference findNext(final Source source, final int pos) {
		return findNext(source,pos,Config.UnterminatedCharacterReferenceSettings.ACCEPT_ALL);
	}

	private static CharacterReference findPrevious(final Source source, int pos, final Config.UnterminatedCharacterReferenceSettings unterminatedCharacterReferenceSettings) {
		final ParseText parseText=source.getParseText();
		pos=parseText.lastIndexOf('&',pos);
		while (pos!=-1) {
			final CharacterReference characterReference=construct(source,pos,unterminatedCharacterReferenceSettings);
			if (characterReference!=null) return characterReference;
			pos=parseText.lastIndexOf('&',pos-1);
		}
		return null;
	}

	private static CharacterReference findNext(final Source source, int pos, final Config.UnterminatedCharacterReferenceSettings unterminatedCharacterReferenceSettings) {
		final ParseText parseText=source.getParseText();
		pos=parseText.indexOf('&',pos);
		while (pos!=-1) {
			final CharacterReference characterReference=construct(source,pos,unterminatedCharacterReferenceSettings);
			if (characterReference!=null) return characterReference;
			pos=parseText.indexOf('&',pos+1);
		}
		return null;
	}

	static final StringBuffer appendHexadecimalCharacterReferenceString(final StringBuffer sb, final int codePoint) {
		return sb.append("&#x").append(Integer.toString(codePoint,16)).append(';');
	}

	static final StringBuffer appendDecimalCharacterReferenceString(final StringBuffer sb, final int codePoint) {
		return sb.append("&#").append(codePoint).append(';');
	}

	private static CharacterReference construct(final Source source, final int begin, final Config.UnterminatedCharacterReferenceSettings unterminatedCharacterReferenceSettings) {
		try {
			if (source.getParseText().charAt(begin)!='&') return null;
			return (source.getParseText().charAt(begin+1)=='#')
				? NumericCharacterReference.construct(source,begin,unterminatedCharacterReferenceSettings)
				: CharacterEntityReference.construct(source,begin,unterminatedCharacterReferenceSettings.characterEntityReferenceMaxCodePoint);
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}

	private static StringBuffer appendDecode(final StringBuffer sb, final CharSequence encodedText, int pos, final boolean insideAttributeValue, final boolean convertNonBreakingSpaces) {
		final Config.UnterminatedCharacterReferenceSettings unterminatedCharacterReferenceSettings=Config.CurrentCompatibilityMode.getUnterminatedCharacterReferenceSettings(insideAttributeValue);
		int lastEnd=0;
		final Source source=new Source(encodedText);
		while (true) {
			final CharacterReference characterReference=findNext(source,pos,unterminatedCharacterReferenceSettings);
			if (characterReference==null) break;
			if (lastEnd!=characterReference.getBegin()) Util.appendTo(sb,encodedText,lastEnd,characterReference.getBegin());
			final char ch=characterReference.getChar();
			sb.append((ch==CharacterEntityReference._nbsp && convertNonBreakingSpaces) ? ' ' : ch);
			pos=lastEnd=characterReference.getEnd();
		}
		if (lastEnd!=encodedText.length()) Util.appendTo(sb,encodedText,lastEnd,encodedText.length());
		return sb;
	}
}
