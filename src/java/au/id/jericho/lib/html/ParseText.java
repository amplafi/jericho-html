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

/**
 * Represents the text from the {@linkplain Source source} document that is to be parsed.
 * <p>
 * This class is normally only of interest to users who wish to create <a href="TagType.html#Custom">custom tag types</a>.
 * <p>
 * The parse text is defined as the entire text of the source document in lower case, with all
 * {@linkplain Segment#ignoreWhenParsing() ignored} segments replaced by space characters.
 * <p>
 * The text is stored in lower case to make case insensitive parsing as efficient as possible.
 * <p>
 * This class provides many methods which are also provided by the <code>java.lang.String</code> class,
 * but adds an extra parameter called <code>breakAtIndex</code> to the various <code>indexOf</code> methods.
 * This parameter allows a search on only a specified segment of the text, which is not possible using the normal <code>String</code> class.
 * <p>
 * <code>ParseText</code> instances are obtained using the {@link Source#getParseText()} method.
 */
public final class ParseText implements CharSequence {
	private final char[] text;

	/** A value to use as the <code>breakAtIndex</code> argument in certain methods to indicate that the search should continue to the start or end of the parse text. */
	public static final int NO_BREAK=-1;

	/**
	 * Constructs a new <code>ParseText</code> object based on the specified string.
	 * @param string  the string upon which the parse text is based.
	 */
	ParseText(final String string) {
		text=new char[string.length()];
		string.getChars(0,string.length(),text,0);
		for (int i=0; i<text.length; i++) text[i]=Character.toLowerCase(text[i]);
	}

	/**
	 * Indicates whether this parse text contains the specified string at the specified position.
	 * <p>
	 * This method is analogous to the <code>java.lang.String.startsWith(String prefix, int toffset)</code> method.
	 *
	 * @param str  a string.
	 * @param pos  the position (index) in this parse text at which to check for the specified string.
	 * @return <code>true</code> if this parse text contains the specified string at the specified position, otherwise <code>false</code>.
	 */
	public boolean containsAt(final String str, final int pos) {
		for (int i=0; i<str.length(); i++)
			if (str.charAt(i)!=text[pos+i]) return false;
		return true;
	}

	/**
	 * Returns the character at the specified index.
	 * @param index  the index of the character.
	 * @return the character at the specified index, which is always in lower case.
	 */
	public char charAt(final int index) {
		return text[index];
	}

	/**
	 * Returns the index within this parse text of the first occurrence of the specified character,
	 * starting the search at the position specified by <code>fromIndex</code>.
	 * <p>
	 * If the specified character is not found then -1 is returned.
	 *
	 * @param searchChar  a character.
	 * @param fromIndex  the index to start the search from. 
	 * @return the index within this parse text of the first occurrence of the specified character within the specified range, or -1 if the character is not found.
	 */
	public int indexOf(final char searchChar, final int fromIndex) {
		return indexOf(searchChar,fromIndex,NO_BREAK);
	}
	
	/**
	 * Returns the index within this parse text of the first occurrence of the specified character,
	 * starting the search at the position specified by <code>fromIndex</code>,
	 * and breaking the search at the index specified by <code>breakAtIndex</code>.
	 * <p>
	 * The position specified by <code>breakAtIndex</code> is not included in the search.
	 * <p>
	 * If the search is to continue to the end of the text,
	 * the value {@link #NO_BREAK ParseText.NO_BREAK} should be specified as the <code>breakAtIndex</code>.
	 * <p>
	 * If the specified character is not found then -1 is returned.
	 *
	 * @param searchChar  a character.
	 * @param fromIndex  the index to start the search from.
	 * @param breakAtIndex  the index at which to break off the search, or {@link #NO_BREAK} if the search is to continue to the end of the text.
	 * @return the index within this parse text of the first occurrence of the specified character within the specified range, or -1 if the character is not found.
	 */
	public int indexOf(final char searchChar, final int fromIndex, final int breakAtIndex) {
		final int actualBreakAtIndex=(breakAtIndex==NO_BREAK || breakAtIndex>text.length ? text.length : breakAtIndex);
		for (int i=(fromIndex<0 ? 0 : fromIndex); i<actualBreakAtIndex; i++)
			if (text[i]==searchChar) return i;
		return -1;
	}

	/**
	 * Returns the index within this parse text of the last occurrence of the specified character,
	 * searching backwards starting at the position specified by <code>fromIndex</code>.
	 * <p>
	 * If the specified character is not found then -1 is returned.
	 *
	 * @param searchChar  a character.
	 * @param fromIndex  the index to start the search from. 
	 * @return the index within this parse text of the last occurrence of the specified character within the specified range, or -1 if the character is not found.
	 */
	public int lastIndexOf(final char searchChar, final int fromIndex) {
		return lastIndexOf(searchChar,fromIndex,NO_BREAK);
	}
	
	/**
	 * Returns the index within this parse text of the last occurrence of the specified character,
	 * searching backwards starting at the position specified by <code>fromIndex</code>,
	 * and breaking the search at the index specified by <code>breakAtIndex</code>.
	 * <p>
	 * The position specified by <code>breakAtIndex</code> is not included in the search.
	 * <p>
	 * If the search is to continue to the start of the text,
	 * the value {@link #NO_BREAK ParseText.NO_BREAK} should be specified as the <code>breakAtIndex</code>.
	 * <p>
	 * If the specified character is not found then -1 is returned.
	 *
	 * @param searchChar  a character.
	 * @param fromIndex  the index to start the search from.
	 * @param breakAtIndex  the index at which to break off the search, or {@link #NO_BREAK} if the search is to continue to the start of the text.
	 * @return the index within this parse text of the last occurrence of the specified character within the specified range, or -1 if the character is not found.
	 */
	public int lastIndexOf(final char searchChar, final int fromIndex, final int breakAtIndex) {
		for (int i=(fromIndex>text.length ? text.length : fromIndex); i>breakAtIndex; i--)
			if (text[i]==searchChar) return i;
		return -1;
	}

	/**
	 * Returns the index within this parse text of the first occurrence of the specified string,
	 * starting the search at the position specified by <code>fromIndex</code>.
	 * <p>
	 * If the specified string is not found then -1 is returned.
	 *
	 * @param searchString  a string.
	 * @param fromIndex  the index to start the search from. 
	 * @return the index within this parse text of the first occurrence of the specified string within the specified range, or -1 if the string is not found.
	 */
	public int indexOf(final String searchString, final int fromIndex) {
		return (searchString.length()==1)
			? indexOf(searchString.charAt(0),fromIndex,NO_BREAK)
			: indexOf(searchString.toCharArray(),fromIndex,NO_BREAK);
	}

	/**
	 * Returns the index within this parse text of the first occurrence of the specified character array,
	 * starting the search at the position specified by <code>fromIndex</code>.
	 * <p>
	 * If the specified character array is not found then -1 is returned.
	 *
	 * @param searchCharArray  a character array.
	 * @param fromIndex  the index to start the search from. 
	 * @return the index within this parse text of the first occurrence of the specified character array within the specified range, or -1 if the character array is not found.
	 */
	public int indexOf(final char[] searchCharArray, final int fromIndex) {
		return indexOf(searchCharArray,fromIndex,NO_BREAK);
	}
	
	/**
	 * Returns the index within this parse text of the first occurrence of the specified string,
	 * starting the search at the position specified by <code>fromIndex</code>,
	 * and breaking the search at the index specified by <code>breakAtIndex</code>.
	 * <p>
	 * The position specified by <code>breakAtIndex</code> is not included in the search.
	 * <p>
	 * If the search is to continue to the end of the text,
	 * the value {@link #NO_BREAK ParseText.NO_BREAK} should be specified as the <code>breakAtIndex</code>.
	 * <p>
	 * If the specified string is not found then -1 is returned.
	 *
	 * @param searchString  a string.
	 * @param fromIndex  the index to start the search from.
	 * @param breakAtIndex  the index at which to break off the search, or {@link #NO_BREAK} if the search is to continue to the end of the text.
	 * @return the index within this parse text of the first occurrence of the specified string within the specified range, or -1 if the string is not found.
	 */
	public int indexOf(final String searchString, final int fromIndex, final int breakAtIndex) {
		return (searchString.length()==1)
			? indexOf(searchString.charAt(0),fromIndex,breakAtIndex)
			: indexOf(searchString.toCharArray(),fromIndex,breakAtIndex);
	}

	/**
	 * Returns the index within this parse text of the first occurrence of the specified character array,
	 * starting the search at the position specified by <code>fromIndex</code>,
	 * and breaking the search at the index specified by <code>breakAtIndex</code>.
	 * <p>
	 * The position specified by <code>breakAtIndex</code> is not included in the search.
	 * <p>
	 * If the search is to continue to the end of the text,
	 * the value {@link #NO_BREAK ParseText.NO_BREAK} should be specified as the <code>breakAtIndex</code>.
	 * <p>
	 * If the specified character array is not found then -1 is returned.
	 *
	 * @param searchCharArray  a character array.
	 * @param fromIndex  the index to start the search from.
	 * @param breakAtIndex  the index at which to break off the search, or {@link #NO_BREAK} if the search is to continue to the end of the text.
	 * @return the index within this parse text of the first occurrence of the specified character array within the specified range, or -1 if the character array is not found.
	 */
	public int indexOf(final char[] searchCharArray, final int fromIndex, final int breakAtIndex) {
		if (searchCharArray.length==0) return fromIndex;
		final char firstChar=searchCharArray[0];
		final int lastPossibleBreakAtIndex=text.length-searchCharArray.length+1;
		final int actualBreakAtIndex=(breakAtIndex==NO_BREAK || breakAtIndex>lastPossibleBreakAtIndex) ? lastPossibleBreakAtIndex : breakAtIndex;
		outerLoop: for (int i=(fromIndex<0 ? 0 : fromIndex); i<actualBreakAtIndex; i++) {
			if (text[i]==firstChar) {
				for (int j=1; j<searchCharArray.length; j++)
					if (searchCharArray[j]!=text[j+i]) continue outerLoop;
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index within this parse text of the last occurrence of the specified string,
	 * searching backwards starting at the position specified by <code>fromIndex</code>.
	 * <p>
	 * If the specified string is not found then -1 is returned.
	 *
	 * @param searchString  a string.
	 * @param fromIndex  the index to start the search from. 
	 * @return the index within this parse text of the last occurrence of the specified string within the specified range, or -1 if the string is not found.
	 */
	public int lastIndexOf(final String searchString, final int fromIndex) {
		return (searchString.length()==1)
			? lastIndexOf(searchString.charAt(0),fromIndex,NO_BREAK)
			: lastIndexOf(searchString.toCharArray(),fromIndex,NO_BREAK);
	}

	/**
	 * Returns the index within this parse text of the last occurrence of the specified character array,
	 * searching backwards starting at the position specified by <code>fromIndex</code>.
	 * <p>
	 * If the specified character array is not found then -1 is returned.
	 *
	 * @param searchCharArray  a character array.
	 * @param fromIndex  the index to start the search from. 
	 * @return the index within this parse text of the last occurrence of the specified character array within the specified range, or -1 if the character array is not found.
	 */
	public int lastIndexOf(final char[] searchCharArray, final int fromIndex) {
		return lastIndexOf(searchCharArray,fromIndex,NO_BREAK);
	}

	/**
	 * Returns the index within this parse text of the last occurrence of the specified string,
	 * searching backwards starting at the position specified by <code>fromIndex</code>,
	 * and breaking the search at the index specified by <code>breakAtIndex</code>.
	 * <p>
	 * The position specified by <code>breakAtIndex</code> is not included in the search.
	 * <p>
	 * If the search is to continue to the start of the text,
	 * the value {@link #NO_BREAK ParseText.NO_BREAK} should be specified as the <code>breakAtIndex</code>.
	 * <p>
	 * If the specified string is not found then -1 is returned.
	 *
	 * @param searchString  a string.
	 * @param fromIndex  the index to start the search from.
	 * @param breakAtIndex  the index at which to break off the search, or {@link #NO_BREAK} if the search is to continue to the start of the text.
	 * @return the index within this parse text of the last occurrence of the specified string within the specified range, or -1 if the string is not found.
	 */
	public int lastIndexOf(final String searchString, final int fromIndex, final int breakAtIndex) {
		return (searchString.length()==1)
			? lastIndexOf(searchString.charAt(0),fromIndex,breakAtIndex)
			: lastIndexOf(searchString.toCharArray(),fromIndex,breakAtIndex);
	}
	
	/**
	 * Returns the index within this parse text of the last occurrence of the specified character array,
	 * searching backwards starting at the position specified by <code>fromIndex</code>,
	 * and breaking the search at the index specified by <code>breakAtIndex</code>.
	 * <p>
	 * The position specified by <code>breakAtIndex</code> is not included in the search.
	 * <p>
	 * If the search is to continue to the start of the text,
	 * the value {@link #NO_BREAK ParseText.NO_BREAK} should be specified as the <code>breakAtIndex</code>.
	 * <p>
	 * If the specified character array is not found then -1 is returned.
	 *
	 * @param searchCharArray  a character array.
	 * @param fromIndex  the index to start the search from.
	 * @param breakAtIndex  the index at which to break off the search, or {@link #NO_BREAK} if the search is to continue to the start of the text.
	 * @return the index within this parse text of the last occurrence of the specified character array within the specified range, or -1 if the character array is not found.
	 */
	public int lastIndexOf(final char[] searchCharArray, int fromIndex, final int breakAtIndex) {
		if (searchCharArray.length==0) return fromIndex;
		final int rightIndex=text.length-searchCharArray.length;
		if (breakAtIndex>rightIndex) return -1;
		if (fromIndex>rightIndex) fromIndex=rightIndex;
		final int lastCharIndex=searchCharArray.length-1;
		final char lastChar=searchCharArray[lastCharIndex];
		final int actualBreakAtPos=breakAtIndex+lastCharIndex;
		outerLoop: for (int i=fromIndex+lastCharIndex; i>actualBreakAtPos; i--) {
			if (text[i]==lastChar) {
				final int startIndex=i-lastCharIndex;
				for (int j=lastCharIndex-1; j>=0; j--)
					if (searchCharArray[j]!=text[j+startIndex]) continue outerLoop;
				return startIndex;
			}
		}
		return -1;
	}

	/**
	 * Returns the length of the parse text.
	 * @return the length of the parse text.
	 */
	public int length() {
		return text.length;
	}

	/**
	 * Returns a new string that is a substring of this parse text.
	 * <p>
	 * The substring begins at the specified <code>beginIndex</code> and extends to the character at index <code>endIndex</code> - 1.
	 * Thus the length of the substring is <code>endIndex-beginIndex</code>. 
	 *
	 * @param beginIndex  the begin index, inclusive.
	 * @param endIndex  the end index, exclusive.
	 * @return a new string that is a substring of this parse text.
	 */
	public String substring(final int beginIndex, final int endIndex) {
		return new String(text,beginIndex,endIndex-beginIndex);
	}	

	/**
	 * Returns a new character sequence that is a subsequence of this sequence.
	 * <p>
	 * This is equivalent to {@link #substring(int,int) substring(beginIndex,endIndex)}.
	 *
	 * @param beginIndex  the begin index, inclusive.
	 * @param endIndex  the end index, exclusive.
	 * @return a new character sequence that is a subsequence of this sequence.
	 */
	public CharSequence subSequence(final int beginIndex, final int endIndex) {
		return substring(beginIndex,endIndex);
	}

	/**
	 * Returns the content of the parse text as a <code>String</code>.
	 * @return the content of the parse text as a <code>String</code>.
	 */
	public String toString() {
		return new String(text);
	}
}
