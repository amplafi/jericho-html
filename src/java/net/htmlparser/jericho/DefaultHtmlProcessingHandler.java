package net.htmlparser.jericho;

/**
 * @author patmoore
 *
 */
public class DefaultHtmlProcessingHandler implements HtmlIssueProcessingHandler {
    private Source source;
    public DefaultHtmlProcessingHandler(Source source) {
        this.source = source;
    }
    /**
     * notifies of html issue. Logged at the info level.
     * Allows detection of html issues.
     */
    @Override
    public void htmlIssue(HtmlIssue htmlIssue) {
        if (source.getLogger().isInfoEnabled()) {
            source.getLogger().info(htmlIssue.toString());
        }
    }
}
