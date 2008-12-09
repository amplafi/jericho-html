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

final class OutputSegmentComparator implements Comparator {
	public int compare(final Object o1, final Object o2) {
		final OutputSegment outputSegment1=(OutputSegment)o1;
		final OutputSegment outputSegment2=(OutputSegment)o2;
		if (outputSegment1.getBegin()<outputSegment2.getBegin()) return -1;
		if (outputSegment1.getBegin()>outputSegment2.getBegin()) return 1;
		if (outputSegment1.getEnd()<outputSegment2.getEnd()) return -1;
		if (outputSegment1.getEnd()>outputSegment2.getEnd()) return 1;
		return 0;
	}
}

