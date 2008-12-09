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

/**
 * Represents either a {@link StartTag} or {@link EndTag} in a specific {@linkplain Source source} document.
 * <p>
 * Take the following HTML segment as an example:
 * <p>
 * <code>&lt;p&gt;This is a sample paragraph.&lt;/p&gt;</code>
 * <p>
 * The "<code>&lt;p&gt;</code>" is represented by a {@link StartTag} object, and the "<code>&lt;/p&gt;</code>" is represented by an {@link EndTag} object,
 * both of which are subclasses of the <code>Tag</code> class.
 * The whole segment, including the start tag, its corresponding end tag and all of the content in between, is represented by an {@link Element} object.
 *
 * <h3><a name="ParsingProcess">Tag Parsing Process</a></h3>
 * The following process describes how each tag is identified by the parser:
 * <ol class="Separated">
 *  <li>
 *   Every '<code>&lt;</code>' character found in the source document is considered to be the start of a tag.
 *   The characters following it are compared with the {@linkplain TagType#getStartDelimiter() start delimiters}
 *   of all the {@linkplain TagType#register() registered} {@linkplain TagType tag types}, and a list of matching tag types
 *   is determined.
 *  <li>
 *   A more detailed analysis of the source is performed according to the features of each matching tag type from the first step,
 *   in order of <a href="TagType.html#Precedence">precedence</a>, until a valid tag is able to be constructed.
 *   <p>
 *   The analysis performed in relation to each candidate tag type is a two-stage process:
 *   <ol>
 *    <li>
 *     The position of the tag is checked to determine whether it is {@linkplain TagType#isValidPosition(Source,int,int[]) valid}.
 *     In theory, a {@linkplain TagType#isServerTag() server tag} is valid in any position, but a non-server tag is not valid inside any other tag,
 *     nor inside elements with CDATA content such as {@link HTMLElementName#SCRIPT SCRIPT} and {@link HTMLElementName#STYLE STYLE} elements.
 *     Theory dictates therefore that {@linkplain StartTagType#COMMENT comments} and explicit {@linkplain StartTagType#CDATA_SECTION CDATA sections}
 *     inside script elements should not be recognised as tags.
 *     <p>
 *     The {@link TagType#isValidPosition(Source, int pos, int[] fullSequentialParseData)} method is responsible for this check
 *     and has a common default implementation for all tag types
 *     (although <a href="TagType.html#custom">custom</a> tag types can override it if necessary).
 *     Its behaviour differs depending on whether or not a {@linkplain Source#fullSequentialParse() full sequential parse} is peformed.
 *     See the documentation of the {@link TagType#isValidPosition(Source,int,int[]) isValidPosition} method for full details.
 *    <li>
 *     A final analysis is performed by the {@link TagType#constructTagAt(Source, int pos)} method of the candidate tag type.
 *     This method returns a valid {@link Tag} object if all conditions of the candidate tag type are met, otherwise it returns
 *     <code>null</code> and the process continues with the next candidate tag type.
 *   </ol>
 *  <li>
 *   If the source does not match the start delimiter or syntax of any registered tag type, the segment spanning it and the next
 *   '<code>&gt;</code>' character is taken to be an {@linkplain #isUnregistered() unregistered} tag.
 *   Some tag search methods ignore unregistered tags.  See the {@link #isUnregistered()} method for more information.
 * </ol>
 * <p>
 * See the documentation of the {@link TagType} class for more details on how tags are recognised.
 *
 * <h3><a name="TagSearchMethods">Tag Search Methods</a></h3>
 * <p>
 * Methods that find tags in a source document are collectively referred to as <i>Tag Search Methods</i>.
 * They are found mostly in the {@link Source} and {@link Segment} classes, and can be generally categorised as follows:
 * <dl class="Separated">
 *  <dt><a name="OpenSearch">Open Search:</a>
 *   <dd>These methods search for tags of any {@linkplain #getName() name} and {@linkplain #getTagType() type}.
 *    <ul class="Unseparated">
 *     <li>{@link Tag#findNextTag()}
 *     <li>{@link Tag#findPreviousTag()}
 *     <li>{@link Segment#findAllElements()}
 *     <li>{@link Segment#findAllTags()}
 *     <li>{@link Source#getTagAt(int pos)}
 *     <li>{@link Source#findPreviousTag(int pos)}
 *     <li>{@link Source#findNextTag(int pos)}
 *     <li>{@link Source#findEnclosingTag(int pos)}
 *     <li>{@link Segment#findAllStartTags()}
 *     <li>{@link Source#findPreviousStartTag(int pos)}
 *     <li>{@link Source#findNextStartTag(int pos)}
 *     <li>{@link Source#findPreviousEndTag(int pos)}
 *     <li>{@link Source#findNextEndTag(int pos)}
 *    </ul>
 *  <dt><a name="NamedSearch">Named Search:</a>
 *   <dd>These methods usually include a parameter called <code>name</code> which is used to specify the {@linkplain #getName() name} of the
 *    tag to search for.  In some cases named search methods do not require this parameter because the context or name of the method implies
 *    the name to search for.
 *    In tag search methods specifically looking for start tags, specifying a name that ends in a colon (<code>:</code>)
 *    searches for all start tags in the specified XML namespace.
 *    <ul class="Unseparated">
 *     <li>{@link Segment#findAllElements(String name)}
 *     <li>{@link Segment#findAllStartTags(String name)}
 *     <li>{@link Source#findPreviousStartTag(int pos, String name)}
 *     <li>{@link Source#findNextStartTag(int pos, String name)}
 *     <li>{@link Source#findPreviousEndTag(int pos, String name)}
 *     <li>{@link Source#findNextEndTag(int pos, String name)}
 *     <li>{@link Source#findNextEndTag(int pos, String name, EndTagType)}
 *    </ul>
 *  <dt><a name="TagTypeSearch">Tag Type Search:</a>
 *   <dd>These methods usually include a parameter called <code>tagType</code> which is used to specify the {@linkplain #getTagType() type} of the
 *    tag to search for.  In some methods the search parameter is restricted to the {@link StartTagType} subclass of <code>TagType</code>.
 *    <ul class="Unseparated">
 *     <li>{@link Segment#findAllElements(StartTagType)}
 *     <li>{@link Segment#findAllTags(TagType)}
 *     <li>{@link Source#findPreviousTag(int pos, TagType)}
 *     <li>{@link Source#findNextTag(int pos, TagType)}
 *     <li>{@link Source#findEnclosingTag(int pos, TagType)}
 *     <li>{@link Source#findNextEndTag(int pos, String name, EndTagType)}
 *    </ul>
 *  <dt><a name="OtherSearch">Other Search:</a>
 *   <dd>A small number of methods do not fall into any of the above categories, such as the methods that search on
 *    {@linkplain Source#findNextStartTag(int pos, String attributeName, String value, boolean valueCaseSensitive) attribute values}.
 *    <ul class="Unseparated">
 *     <li>{@link Segment#findAllStartTags(String attributeName, String value, boolean valueCaseSensitive)}
 *     <li>{@link Source#findNextStartTag(int pos, String attributeName, String value, boolean valueCaseSensitive)}
 *    </ul>
 * </dl>
 */
public abstract class Tag extends Segment implements HTMLElementName {
	String name=null; // always lower case, can always use == operator to compare with constants in HTMLElementName interface
	private Object userData=null;
	// cached values:
	Element element=Element.NOT_CACHED;
	private Tag previousTag=NOT_CACHED;
	private Tag nextTag=NOT_CACHED;

	static final Tag NOT_CACHED=new StartTag();

	private static final boolean INCLUDE_UNREGISTERED_IN_SEARCH=false; // determines whether unregistered tags are included in searches

	Tag(final Source source, final int begin, final int end, final String name) {
		super(source,begin,end);
		this.name=HTMLElements.getConstantElementName(name.toLowerCase());
	}

	// only used to create Tag.NOT_CACHED
	Tag() {}

	/**
	 * Returns the {@linkplain Element element} that is started or ended by this tag.
	 * <p>
	 * {@link StartTag#getElement()} is guaranteed not <code>null</code>.
	 * <p>
	 * {@link EndTag#getElement()} can return <code>null</code> if the end tag is not properly matched to a start tag.
	 *
	 * @return the {@linkplain Element element} that is started or ended by this tag.
	 */
	public abstract Element getElement();

	/**
	 * Returns the name of this tag, always in lower case.
	 * <p>
	 * The name always starts with the {@linkplain TagType#getNamePrefix() name prefix} defined in this tag's {@linkplain TagType type}.
	 * For some tag types, the name consists only of this prefix, while in others it must be followed by a valid
	 * <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-Name">XML name</a>
	 * (see {@link StartTagType#isNameAfterPrefixRequired()}).
	 * <p>
	 * If the name is equal to one of the constants defined in the {@link HTMLElementName} interface, this method is guaranteed to return
	 * the constant itself.
	 * This allows comparisons to be performed using the <code>==</code> operator instead of the less efficient
	 * <code>String.equals(Object)</code> method.
	 * <p>
	 * For example, the following expression can be used to test whether a {@link StartTag} is from a
	 * <code><a target="_blank" href="http://www.w3.org/TR/html401/interact/forms.html#edef-SELECT">SELECT</a></code> element:
	 * <br /><code>startTag.getName()==HTMLElementName.SELECT</code>
	 * <p>
	 * To get the name of this tag in its original case, use {@link #getNameSegment()}<code>.toString()</code>.
	 *
	 * @return the name of this tag, always in lower case.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns the segment spanning the {@linkplain #getName() name} of this tag.
	 * <p>
	 * The code <code>getNameSegment().toString()</code> can be used to retrieve the name of this tag in its original case.
	 * <p>
	 * Every call to this method constructs a new <code>Segment</code> object.
	 *
	 * @return the segment spanning the {@linkplain #getName() name} of this tag.
	 * @see #getName()
	 */
	public Segment getNameSegment() {
		final int nameSegmentBegin=begin+getTagType().startDelimiterPrefix.length();
		return new Segment(source,nameSegmentBegin,nameSegmentBegin+name.length());
	}

	/**
	 * Returns the {@linkplain TagType type} of this tag.	
	 * @return the {@linkplain TagType type} of this tag.	
	 */
	public abstract TagType getTagType();

	/**
	 * Returns the general purpose user data object that has previously been associated with this tag via the {@link #setUserData(Object)} method.
	 * <p>
	 * If {@link #setUserData(Object)} has not been called, this method returns <code>null</code>.
	 *
	 * @return the generic data object that has previously been associated with this tag via the {@link #setUserData(Object)} method.
	 */
	public Object getUserData() {
		return userData;
	}

	/**
	 * Associates the specified general purpose user data object with this tag.
	 * <p>
	 * This property can be useful for applications that need to associate extra information with tags.
	 * The object can be retrieved later via the {@link #getUserData()} method.
	 *
	 * @param userData  general purpose user data of any type.
	 */
	public void setUserData(final Object userData) {
		this.userData=userData;
	}

	/**
	 * Returns the next tag in the source document.
	 * <p>
	 * This method also returns {@linkplain TagType#isServerTag() server tags}.
	 * <p>
	 * The result of a call to this method is cached.
	 * Performing a {@linkplain Source#fullSequentialParse() full sequential parse} prepopulates this cache.
	 * <p>
	 * If the result is not cached, a call to this method is equivalent to <code>source.</code>{@link Source#findNextTag(int) findNextTag}<code>(</code>{@link #getBegin() getBegin()}<code>+1)</code>.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @return the next tag in the source document, or <code>null</code> if this is the last tag.
	 */
	public Tag findNextTag() {
		if (nextTag==NOT_CACHED) nextTag=findNextTag(source,begin+1);
		return nextTag;
	}

	/**
	 * Returns the previous tag in the source document.
	 * <p>
	 * This method also returns {@linkplain TagType#isServerTag() server tags}.
	 * <p>
	 * The result of a call to this method is cached.
	 * Performing a {@linkplain Source#fullSequentialParse() full sequential parse} prepopulates this cache.
	 * <p>
	 * If the result is not cached, a call to this method is equivalent to <code>source.</code>{@link Source#findPreviousTag(int) findPreviousTag}<code>(</code>{@link #getBegin() getBegin()}<code>-1)</code>.
	 * <p>
	 * See the {@link Tag} class documentation for more details about the behaviour of this method.
	 *
	 * @return the previous tag in the source document, or <code>null</code> if this is the first tag.
	 */
	public Tag findPreviousTag() {
		if (previousTag==NOT_CACHED) previousTag=findPreviousTag(source,begin-1);
		return previousTag;
	}

	/**
	 * Indicates whether this tag has a syntax that does not match any of the {@linkplain TagType#register() registered} {@linkplain TagType tag types}.
	 * <p>
 	 * The only requirement of an unregistered tag type is that it {@linkplain TagType#getStartDelimiter() starts} with
 	 * '<code>&lt;</code>' and there is a {@linkplain TagType#getClosingDelimiter() closing} '<code>&gt;</code>' character
 	 * at some position after it in the source document.
	 * <p>
	 * The absence or presence of a '<code>/</code>' character after the initial '<code>&lt;</code>' determines whether an
	 * unregistered tag is respectively a
	 * {@link StartTag} with a {@linkplain #getTagType() type} of {@link StartTagType#UNREGISTERED} or an
	 * {@link EndTag} with a {@linkplain #getTagType() type} of {@link EndTagType#UNREGISTERED}.
	 * <p>
	 * There are no restrictions on the characters that might appear between these delimiters, including other '<code>&lt;</code>'
	 * characters.  This may result in a '<code>&gt;</code>' character that is identified as the closing delimiter of two
	 * separate tags, one an unregistered tag, and the other a tag of any type that {@linkplain #getBegin() begins} in the middle 
	 * of the unregistered tag.  As explained below, unregistered tags are usually only found when specifically looking for them,
	 * so it is up to the user to detect and deal with any such nonsensical results.
	 * <p>
	 * Unregistered tags are only returned by the {@link Source#getTagAt(int pos)} method,
	 * <a href="Tag.html#NamedSearch">named search</a> methods, where the specified <code>name</code>
	 * matches the first characters inside the tag, and by <a href="Tag.html#TagTypeSearch">tag type search</a> methods, where the
	 * specified <code>tagType</code> is either {@link StartTagType#UNREGISTERED} or {@link EndTagType#UNREGISTERED}.
	 * <p>
	 * <a href="Tag.html#OpenSearch">Open</a> tag searches and <a href="Tag.html#OtherSearch">other</a> searches always ignore
	 * unregistered tags, although every discovery of an unregistered tag is {@linkplain Source#getLogger() logged} by the parser.
	 * <p>
	 * The logic behind this design is that unregistered tag types are usually the result of a '<code>&lt;</code>' character 
	 * in the text that was mistakenly left {@linkplain CharacterReference#encode(CharSequence) unencoded}, or a less-than 
	 * operator inside a script, or some other occurrence which is of no interest to the user.
	 * By returning unregistered tags in <a href="Tag.html#NamedSearch">named</a> and <a href="Tag.html#TagTypeSearch">tag type</a>
	 * search methods, the library allows the user to specifically search for tags with a certain syntax that does not match any
	 * existing {@link TagType}.  This expediency feature avoids the need for the user to create a
	 * <a href="TagType.html#Custom">custom tag type</a> to define the syntax before searching for these tags.
	 * By not returning unregistered tags in the less specific search methods, it is providing only the information that 
	 * most users are interested in.
	 *
	 * @return <code>true</code> if this tag has a syntax that does not match any of the {@linkplain TagType#register() registered} {@linkplain TagType tag types}, otherwise <code>false</code>.
	 */
	public abstract boolean isUnregistered();

	/**
	 * Returns an XML representation of this tag.
	 * <p>
	 * This is an abstract method which is implemented in the {@link StartTag} and {@link EndTag} subclasses.
	 * See the documentation of the {@link StartTag#tidy()} and {@link EndTag#tidy()} methods for details.
	 *
	 * @return an XML representation of this tag.
	 */
	public abstract String tidy();

	/**
	 * Indicates whether the specified text is a valid <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-Name">XML Name</a>.
	 * <p>
	 * This implementation first checks that the first character of the specified text is a valid XML Name start character
	 * as defined by the {@link #isXMLNameStartChar(char)} method, and then checks that the rest of the characters are valid
	 * XML Name characters as defined by the {@link #isXMLNameChar(char)} method.
	 * <p>
	 * Note that this implementation does not exactly adhere to the
	 * <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-Name">formal definition of an XML Name</a>,
	 * but the differences are unlikely to be significant in real-world XML or HTML documents.
	 *
	 * @param text  the text to test.
	 * @return <code>true</code> if the specified text is a valid <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-Name">XML Name</a>, otherwise <code>false</code>.
	 * @see Source#findNameEnd(int pos)
	 */
	public static final boolean isXMLName(final CharSequence text) {
		if (text==null || text.length()==0 || !isXMLNameStartChar(text.charAt(0))) return false;
		for (int i=1; i<text.length(); i++)
			if (!isXMLNameChar(text.charAt(i))) return false;
		return true;
	}

	/**
	 * Indicates whether the specified character is valid at the start of an
	 * <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-Name">XML Name</a>.
	 * <p>
	 * The <a target="_blank" href="http://www.w3.org/TR/REC-xml/#sec-common-syn">XML 1.0 specification section 2.3</a> defines a
	 * <code><a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-Name">Name</a></code> as starting with one of the characters
	 * <br /><code>(<a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-Letter">Letter</a> | '_' | ':')</code>.
	 * <p>
	 * This method uses the expression
	 * <br /><code>Character.isLetter(ch) || ch=='_' || ch==':'</code>.
	 * <p>
	 * Note that there are many differences between the <code>Character.isLetter()</code> definition of a Letter and the
	 * <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-Letter">XML definition of a Letter</a>,
	 * but these differences are unlikely to be significant in real-world XML or HTML documents.
	 *
	 * @param ch  the character to test.
	 * @return <code>true</code> if the specified character is valid at the start of an <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-Name">XML Name</a>, otherwise <code>false</code>.
	 * @see Source#findNameEnd(int pos)
	 */
	public static final boolean isXMLNameStartChar(final char ch) {
		return Character.isLetter(ch) || ch=='_' || ch==':';
	}

	/**
	 * Indicates whether the specified character is valid anywhere in an
	 * <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-Name">XML Name</a>.
	 * <p>
	 * The <a target="_blank" href="http://www.w3.org/TR/REC-xml/#sec-common-syn">XML 1.0 specification section 2.3</a> uses the
	 * entity <code><a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-NameChar">NameChar</a></code> to represent this set of
	 * characters, which is defined as
	 * <br /><code>(<a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-Letter">Letter</a>
	 * | <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-Digit">Digit</a> | '.' | '-' | '_' | ':'
	 * | <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-CombiningChar">CombiningChar</a>
	 * | <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-Extender">Extender</a>)</code>.
	 * <p>
	 * This method uses the expression
	 * <br /><code>Character.isLetterOrDigit(ch) || ch=='.' || ch=='-' || ch=='_' || ch==':'</code>.
	 * <p>
	 * Note that there are many differences between these definitions,
	 * but these differences are unlikely to be significant in real-world XML or HTML documents.
	 *
	 * @param ch  the character to test.
	 * @return <code>true</code> if the specified character is valid anywhere in an <a target="_blank" href="http://www.w3.org/TR/REC-xml/#NT-Name">XML Name</a>, otherwise <code>false</code>.
	 * @see Source#findNameEnd(int pos)
	 */
	public static final boolean isXMLNameChar(final char ch) {
		return Character.isLetterOrDigit(ch) || ch=='.' || ch=='-' || ch=='_' || ch==':';
	}

	final boolean includeInSearch() {
		return INCLUDE_UNREGISTERED_IN_SEARCH || !isUnregistered();
	}

	static final Tag findPreviousTag(final Source source, final int pos) {
		// returns null if pos is out of range.
		return source.useAllTypesCache
			? source.cache.findPreviousTag(pos)
			: findPreviousTagUncached(source,pos,ParseText.NO_BREAK);
	}

	static final Tag findNextTag(final Source source, final int pos) {
		// returns null if pos is out of range.
		return source.useAllTypesCache
			? source.cache.findNextTag(pos)
			: findNextTagUncached(source,pos,ParseText.NO_BREAK);
	}
		
	static final Tag findPreviousTagUncached(final Source source, final int pos, final int breakAtPos) {
		// returns null if pos is out of range.
		try {
			final ParseText parseText=source.getParseText();
			int begin=pos;
			do {
				begin=parseText.lastIndexOf('<',begin,breakAtPos); // this assumes that all tags start with '<'
				// parseText.lastIndexOf and indexOf return -1 if pos is out of range.
				if (begin==-1) return null;
				final Tag tag=getTagAt(source,begin,false);
				if (tag!=null && tag.includeInSearch()) return tag;
			} while ((begin-=1)>=0);
		} catch (IndexOutOfBoundsException ex) {
			// this should never happen during a find previous operation so rethrow it:
			throw ex;
		}
		return null;
	}

	static final Tag findNextTagUncached(final Source source, final int pos, final int breakAtPos) {
		// returns null if pos is out of range.
		try {
			final ParseText parseText=source.getParseText();
			int begin=pos;
			do {
				begin=parseText.indexOf('<',begin,breakAtPos); // this assumes that all tags start with '<'
				// parseText.lastIndexOf and indexOf return -1 if pos is out of range.
				if (begin==-1) return null;
				final Tag tag=getTagAt(source,begin,false);
				if (tag!=null && tag.includeInSearch()) return tag;
			} while ((begin+=1)<source.end);
		} catch (IndexOutOfBoundsException ex) {
			// this should only happen when the end of file is reached in the middle of a tag.
			// we don't have to do anything to handle it as there are no more tags anyway.
		}
		return null;
	}

	static final Tag findPreviousTag(final Source source, final int pos, final TagType tagType) {
		// returns null if pos is out of range.
		if (source.useSpecialTypesCache) return source.cache.findPreviousTag(pos,tagType);
		return findPreviousTagUncached(source,pos,tagType,ParseText.NO_BREAK);
	}

	static final Tag findNextTag(final Source source, final int pos, final TagType tagType) {
		// returns null if pos is out of range.
		if (source.useSpecialTypesCache) return source.cache.findNextTag(pos,tagType);
		return findNextTagUncached(source,pos,tagType,ParseText.NO_BREAK);
	}

	static final Tag findPreviousTagUncached(final Source source, final int pos, final TagType tagType, final int breakAtPos) {
		// returns null if pos is out of range.
		if (tagType==null) return findPreviousTagUncached(source,pos,breakAtPos);
		final char[] startDelimiterCharArray=tagType.getStartDelimiterCharArray();
		try {
			final ParseText parseText=source.getParseText();
			int begin=pos;
			do {
				begin=parseText.lastIndexOf(startDelimiterCharArray,begin,breakAtPos);
				// parseText.lastIndexOf and indexOf return -1 if pos is out of range.
				if (begin==-1) return null;
				final Tag tag=getTagAt(source,begin,false);
				if (tag!=null && tag.getTagType()==tagType) return tag;
			} while ((begin-=1)>=0);
		} catch (IndexOutOfBoundsException ex) {
			// this should never happen during a find previous operation so rethrow it:
			throw ex;
		}
		return null;
	}

	static final Tag findNextTagUncached(final Source source, final int pos, final TagType tagType, final int breakAtPos) {
		// returns null if pos is out of range.
		if (tagType==null) return findNextTagUncached(source,pos,breakAtPos);
		final char[] startDelimiterCharArray=tagType.getStartDelimiterCharArray();
		try {
			final ParseText parseText=source.getParseText();
			int begin=pos;
			do {
				begin=parseText.indexOf(startDelimiterCharArray,begin,breakAtPos);
				// parseText.lastIndexOf and indexOf return -1 if pos is out of range.
				if (begin==-1) return null;
				final Tag tag=getTagAt(source,begin,false);
				if (tag!=null && tag.getTagType()==tagType) return tag;
			} while ((begin+=1)<source.end);
		} catch (IndexOutOfBoundsException ex) {
			// this should only happen when the end of file is reached in the middle of a tag.
			// we don't have to do anything to handle it as there are no more tags anyway.
		}
		return null;
	}

	static final Tag getTagAt(final Source source, final int pos, final boolean serverTagOnly) {
		// returns null if pos is out of range.
		return source.useAllTypesCache
			? source.cache.getTagAt(pos,serverTagOnly)
			: getTagAtUncached(source,pos,serverTagOnly);
	}

	static final Tag getTagAtUncached(final Source source, final int pos, final boolean serverTagOnly) {
		// returns null if pos is out of range.
		return TagType.getTagAt(source,pos,serverTagOnly,false);
	}

	static final Tag[] parseAll(final Source source, final boolean assumeNoNestedTags) {
		int registeredTagCount=0;
		int registeredStartTagCount=0;
		final ArrayList list=new ArrayList();
		source.fullSequentialParseData=new int[1]; // fullSequentialParseData is simply a holder for a single mutable integer. It holds the end position of the last normal tag (ie one that ignores enclosed markup), or MAX_VALUE if we are in a SCRIPT element.
		if (source.end!=0) {
			final ParseText parseText=source.getParseText();
			Tag tag=parseAllFindNextTag(source,parseText,0,assumeNoNestedTags);
			while (tag!=null) {
				list.add(tag);
				if (!tag.isUnregistered()) {
					registeredTagCount++;
					if (tag instanceof StartTag) registeredStartTagCount++;
				}
				// Look for next tag after end of next tag if we're assuming tags don't appear inside other tags, as long as the last tag found was not an unregistered tag:
				final int pos=(assumeNoNestedTags && !tag.isUnregistered()) ? tag.end : tag.begin+1;
				if (pos==source.end) break;
				tag=parseAllFindNextTag(source,parseText,pos,assumeNoNestedTags);
			}
		}
		final Tag[] allRegisteredTags=new Tag[registeredTagCount];
		final StartTag[] allRegisteredStartTags=new StartTag[registeredStartTagCount];
		source.cache.loadAllTags(list,allRegisteredTags,allRegisteredStartTags);
		source.allTagsArray=allRegisteredTags;
		source.allTags=Arrays.asList(allRegisteredTags);
		source.allStartTags=Arrays.asList(allRegisteredStartTags);
		final int lastIndex=allRegisteredTags.length-1;
		for (int i=0; i<allRegisteredTags.length; i++) {
			final Tag tag=allRegisteredTags[i];
			tag.previousTag=i>0 ? allRegisteredTags[i-1] : null;
			tag.nextTag=i<lastIndex ? allRegisteredTags[i+1] : null;
		}
		return allRegisteredTags;
	}

	private static final Tag parseAllFindNextTag(final Source source, final ParseText parseText, final int pos, final boolean assumeNoNestedTags) {
		try {
			int begin=pos;
			do {
				begin=parseText.indexOf('<',begin); // this assumes that all tags start with '<'
				if (begin==-1) return null;
				final Tag tag=TagType.getTagAt(source,begin,false,assumeNoNestedTags);
				if (tag!=null) {
					if (!assumeNoNestedTags) {
						final TagType tagType=tag.getTagType();
						if (tag.end>source.fullSequentialParseData[0]
								&& tagType!=StartTagType.DOCTYPE_DECLARATION
								&& tagType!=StartTagType.UNREGISTERED && tagType!=EndTagType.UNREGISTERED) {
							source.fullSequentialParseData[0]=(tagType==StartTagType.NORMAL && tag.name==SCRIPT) ? Integer.MAX_VALUE : tag.end;
						}
					}
					return tag;
				}
			} while ((begin+=1)<source.end);
		} catch (IndexOutOfBoundsException ex) {
			// this should only happen when the end of file is reached in the middle of a tag.
			// we don't have to do anything to handle it as there are no more tags anyway.
		}
		return null;
	}
}
