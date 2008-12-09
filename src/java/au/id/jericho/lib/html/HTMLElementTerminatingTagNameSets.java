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

final class HTMLElementTerminatingTagNameSets {
	// all fields are guaranteed not null and contain unique sets.
	public final Set TerminatingStartTagNameSet; // Set of start tags that terminate the element
	public final Set TerminatingEndTagNameSet; // Set of end tags that terminate the element (the end tag of this element is assumed and not included in this set)
	public final Set NonterminatingElementNameSet; // Set of elements that can be inside this element, which may contain tags from TerminatingStartTagNameSet and TerminatingEndTagNameSet that must be ignored

	public HTMLElementTerminatingTagNameSets(final Set terminatingStartTagNameSet, final Set terminatingEndTagNameSet, final Set nonterminatingElementNameSet) {
		this.TerminatingStartTagNameSet=terminatingStartTagNameSet;
		this.TerminatingEndTagNameSet=terminatingEndTagNameSet;
		this.NonterminatingElementNameSet=nonterminatingElementNameSet;
	}
}
