package net.htmlparser.jericho;

/**
 * Implementers called when an issue with the html being processed is discovered.
 *
 */
public interface HtmlIssueProcessingHandler {
    public void htmlIssue(HtmlIssue htmlIssue);
}
