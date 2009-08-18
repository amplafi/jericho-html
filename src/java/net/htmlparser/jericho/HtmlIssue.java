/**
 * Copyright 2006-2008 by Amplafi. All rights reserved.
 * Confidential. 
 */
package net.htmlparser.jericho;


public class HtmlIssue {
    private final RowColumnVector begin;
    private final RowColumnVector end;
    private final String priorToPosition;
    private final String message;

    public HtmlIssue(RowColumnVector begin, String priorToPosition, String message) {
        this.begin = begin;
        this.end = null;
        this.priorToPosition = priorToPosition;
        this.message = message;
    }
    public HtmlIssue(RowColumnVector begin, RowColumnVector end, String priorToPosition, String message) {
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
        if ( begin != null ) {
            return begin.appendTo(new StringBuilder(200).append(priorToPosition).append(" at ")).append(":").append(message).toString();
        } else {
            return priorToPosition+" "+message;
        }
    }
}