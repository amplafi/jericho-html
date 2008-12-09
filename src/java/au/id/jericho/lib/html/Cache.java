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
 * Represents a cached map of character positions to tags.
 * The allTagTypesSubCache object is used to cache all tags.
 * Additional subcaches are used to cache single tag types. See the TagType.getTagTypesIgnoringEnclosedMarkup() method for details.
 */
final class Cache {
	public final Source source;
	private final SubCache allTagTypesSubCache;
	private final SubCache[] subCaches; // contains allTagTypesSubCache plus a SubCache object for each separately cached tag type

	public Cache(final Source source) {
		this.source=source;
		allTagTypesSubCache=new SubCache(this,null);
		TagType[] separatelyCachedTagTypes=getSeparatelyCachedTagTypes();
		subCaches=new SubCache[separatelyCachedTagTypes.length+1];
		subCaches[0]=allTagTypesSubCache;
		for (int i=0; i<separatelyCachedTagTypes.length; i++)
			subCaches[i+1]=new SubCache(this,separatelyCachedTagTypes[i]);
	}

	public void clear() {
		for (int i=0; i<subCaches.length; i++) subCaches[i].clear();
	}

	public Tag getTagAt(final int pos, final boolean serverTagOnly) {
		return source.useAllTypesCache
			?	allTagTypesSubCache.getTagAt(pos,serverTagOnly)
			: Tag.getTagAtUncached(source,pos,serverTagOnly);
	}

	public Tag findPreviousTag(final int pos) {
		// returns null if pos is out of range.
		return allTagTypesSubCache.findPreviousTag(pos);
	}

	public Tag findNextTag(final int pos) {
		// returns null if pos is out of range.
		return allTagTypesSubCache.findNextTag(pos);
	}

	public Tag findPreviousTag(final int pos, final TagType tagType) {
		// returns null if pos is out of range.
		for (int i=source.useAllTypesCache ? 0 : 1; i<subCaches.length; i++)
			if (tagType==subCaches[i].tagType) return subCaches[i].findPreviousTag(pos);
		return Tag.findPreviousTagUncached(source,pos,tagType,ParseText.NO_BREAK);
	}

	public Tag findNextTag(final int pos, final TagType tagType) {
		// returns null if pos is out of range.
		for (int i=source.useAllTypesCache ? 0 : 1; i<subCaches.length; i++)
			if (tagType==subCaches[i].tagType) return subCaches[i].findNextTag(pos);
		return Tag.findNextTagUncached(source,pos,tagType,ParseText.NO_BREAK);
	}

	public Tag addTagAt(final int pos, final boolean serverTagOnly) {
		final Tag tag=Tag.getTagAtUncached(source,pos,serverTagOnly);
		if (serverTagOnly && tag==null) return null; // don't add null to cache if we were only looking for server tags
		allTagTypesSubCache.addTagAt(pos,tag);
		if (tag==null) return null;
		final TagType tagType=tag.getTagType();
		for (int i=1; i<subCaches.length; i++) {
			if (tagType==subCaches[i].tagType) {
				subCaches[i].addTagAt(pos,tag);
				return tag;
			}
		}
		return tag;
	}

	public int getTagCount() {
		return allTagTypesSubCache.size()-2;
	}

	public Iterator getTagIterator() {
		return allTagTypesSubCache.getTagIterator();
	}

	public void loadAllTags(final List tags, final Tag[] allRegisteredTags, final StartTag[] allRegisteredStartTags) {
		// assumes the tags list implements RandomAccess
		final int tagCount=tags.size();
		allTagTypesSubCache.bulkLoad_Init(tagCount);
		int registeredTagIndex=0;
		int registeredStartTagIndex=0;
		for (int i=0; i<tagCount; i++) {
			Tag tag=(Tag)tags.get(i);
			if (!tag.isUnregistered()) {
				allRegisteredTags[registeredTagIndex++]=tag;
				if (tag instanceof StartTag) allRegisteredStartTags[registeredStartTagIndex++]=(StartTag)tag;
			}
			allTagTypesSubCache.bulkLoad_Set(i,tag);
			for (int x=1; x<subCaches.length; x++) {
				if (tag.getTagType()==subCaches[x].tagType) {
					subCaches[x].bulkLoad_AddToTypeSpecificCache(tag);
					break;
				}
			}
		}
		for (int x=1; x<subCaches.length; x++)
			subCaches[x].bulkLoad_FinaliseTypeSpecificCache();
	}

	public String toString() {
		StringBuffer sb=new StringBuffer();
		for (int i=0; i<subCaches.length; i++) subCaches[i].appendTo(sb);
		return sb.toString();
	}

	protected int getSourceLength() {
		return source.end;
	}
	
	private static TagType[] getSeparatelyCachedTagTypes() {
		return TagType.getTagTypesIgnoringEnclosedMarkup();
	}
}
