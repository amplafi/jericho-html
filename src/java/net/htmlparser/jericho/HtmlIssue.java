// Jericho HTML Parser - Java based library for analysing and manipulating HTML
// Version 3.3-dev
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

package net.htmlparser.jericho;


public class HtmlIssue {
    private final RowColumnVector begin;
    private final RowColumnVector end;
    private final String priorToPosition;
    private final String message;
    private transient Source source;

    public HtmlIssue(String message) {
        this.message = message;
        this.begin = null;
        this.end = null;
        this.priorToPosition = null;
    }
    public HtmlIssue(Source source, int begin, String priorToPosition, String message) {
        this(source,begin,null,priorToPosition,message);
    }
    public HtmlIssue(Source source, int begin, Integer end, String priorToPosition, String message) {
        this.source = source;
        this.begin = source.getRowColumnVector(begin);
        this.end = end ==null?null:source.getRowColumnVector(end);
        this.priorToPosition = priorToPosition;
        this.message = message;
    }
    public HtmlIssue(Source source, RowColumnVector begin, String priorToPosition, String message) {
        this(source, begin, null, priorToPosition, message);
    }
    public HtmlIssue(Source source, RowColumnVector begin, RowColumnVector end, String priorToPosition, String message) {
        this.source = source;
        this.begin = begin;
        this.end = end;
        this.priorToPosition = priorToPosition;
        this.message = message;
    }

    /**
     * @return the begin
     */
    public RowColumnVector getBegin() {
        return begin;
    }

    /**
     * @return the end
     */
    public RowColumnVector getEnd() {
        return end;
    }
    /**
     * @return the priorToPosition
     */
    public String getPriorToPosition() {
        return priorToPosition;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    @Override
    public String toString() {
        if ( begin == null ) {
            return priorToPosition+" "+message;
        } else if (end == null) {
            return begin.appendTo(new StringBuilder(200).append(priorToPosition).append(" at ")).append(":").append(message).toString();
        } else {
            return end.appendTo(
                begin.appendTo(new StringBuilder(200).append(priorToPosition).append(" at ")).append(message)
                    .append(" at position ")).toString();
        }
    }
    public Source getSource() {
        return this.source;
    }
}