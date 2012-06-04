/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package net.htmlparser.jericho;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author patmoore
 *
 */
public class TestErrorReporting {
    @Test
    public void test() {
        Source source = new Source("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
        		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
        		"<head>\n" +
        		"<meta http-equiv=\"content-type\" content=\"application/xhtml+xml; charset=utf-8\" />\n" +
        		"<link href=\"aswa.css\" type=\"text/css\" rel=\"stylesheet\" />\n" +
        		"<title>Welcome to the ASWA Silicon Valley Chapter</title>\n" +
        		"<style type=\"text/css\">/*<![CDATA[*/\n" +
        		"#ampmep_14 { }\n" +
        		"/*]]>*/</style>\n" +
        		"<link type=\"application/rss+xml\" rel=\"alternate\" title=\"President's Message: Monthly President's Message\" href=\"http://amplafi.net/rss/ampbp_2/ampmep_23.xml\" />\n" +
        		"<link rel=\"stylesheet\" type=\"text/css\" href=\"http://amplafi.net/clientcss/messages.css\" />\n" +
        		"</head>\n" +
        		"<body>\n" +
        		"<div align=\"center\">\n" +
        		"<table width=\"780\" border=\"1\" bgcolor=\"white\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" bordercolor=\"#ffcc00\">\n" +
        		"          <tbody><tr>\n" +
        		"            <td>\n" +
        		"                 <table>\n" +
        		"              <tbody>\n" +
        		"                <tr>\n" +
        		"                  <td class=\"chaptermoney\"><img src=\"images/aswalogo3.gif\" /></td>\n" +
        		"                </tr>\n" +
        		"              </tbody>\n" +
        		"            </table>\n" +
        		"            <!-- Begin Main Nav Table -->\n" +
        		"            <table cellpadding=\"5\" cellspacing=\"0\" width=\"780\">\n" +
        		"              <tbody>\n" +
        		"                <tr>\n" +
        		"                  <td width=\"780\" bgcolor=\"#000055\" class=\"mainnav\"><a href=\"index.html\" class=\"mainnav\">Home</a> - <a href=\"aboutus.html\" class=\"mainnav\">About Us</a> - <a href=\"local events.html\" class=\"mainnav\">Chapter Events</a> - <a href=\"events.html\" class=\"mainnav\">Other Events</a>\n" +
        		"                  - <a href=\"officers.html\" class=\"mainnavsel\">Officers</a> - <a href=\"contact.html\" class=\"mainnav\">News and Information</a></td>\n" +
        		"                </tr>\n" +
        		"              </tbody>\n" +
        		"            </table>\n" +
        		"            <!-- End Main Navigation --> <!-- Begin Body -->\n" +
        		"            <table padding=\"10\" cellspacing=\"0\">\n" +
        		"              <tbody>\n" +
        		"                <tr>\n" +
        		"                  <td valign=\"top\">\n" +
        		"                  <h2><strong>Welcome to the American Society of Women Accountants<br />\n" +
        		"                  Silicon Valley Chapter 103</strong></h2>\n" +
        		"                  <div>\n" +
        		"                  <p><i><b>Empowering Women in the Accounting and Financial Professions since 1938!</b></i></p>\n" +
        		"                  <p>ASWA was formed in 1938 to increase the opportunities for women in all fields of accounting and\n" +
        		"                  finance. The first chapter was chartered in Indianapolis, Indiana. Members include partners in\n" +
        		"                  national, regional and local CPA firms, financial officers, controllers, academicians, financial\n" +
        		"                  analysts and data processing consultants, recent college graduates and women returning to the work\n" +
        		"                  force. The majority of our members have attained professional certifications such as CPA, CMA, CIA,\n" +
        		"                  and CFP. The mission of ASWA is to enable women in all accounting and related fields to achieve their\n" +
        		"                  full personal, professional and economic potential and to contribute to the future development of\n" +
        		"                  their profession.</p>\n" +
        		"                  <p>ASWA  Silicon Valley<br />\n" +
        		"                  Chapter 103<br />\n" +
        		"                  P.O. Box 1301<br />\n" +
        		"                  Santa Clara, CA 950521301<br />\n" +
        		"                  4082350828 Voicemail</p>\n" +
        		"                  <hr />\n" +
        		"                  <p><em><font size=\"3\"><strong>The mission of the American Society of Women\n" +
        		"                  Accountants is to enable women in all accounting and related fields to achieve their full personal,\n" +
        		"                  professional, and economic potential, and to contribute to the future development of their\n" +
        		"                  professions.</strong></font></em></p>\n" +
        		"                  </div>\n" +
        		"                  <hr />\n" +
        		"                  <div id=\"ampmep_14\" class=\"ampmep amptpl_Block\">\n" +
        		"<div id=\"ampmep_14_ampbe_399\" class=\"ampmsg_399 ampbe_399 ampmep_14\">\n" +
        		"<div class=\"amp_headline\">\n" +
        		"Our Social Network</div>\n" +
        		"<div class=\"amp_messageBody\">\n" +
        		"<p>My year as your chapter president is just about finished and it was an amazing experience.&nbsp; When I first joined ASWA a number of years ago I thought it would be beneficial to belong to an organization that supported women in the various accounting professions. &nbsp;For years I would attend occasional meetings and, of course, the special events.&nbsp; I enjoyed meeting new people and learning from the presentations.&nbsp; However, I never realized what I was missing until I became more involved with the chapter.&nbsp; I realize now that our chapter is not just a monthly meeting to discuss some technical accounting topic.&nbsp; We are a group of people that are not only excited about what we do, but have a strong desire to help others succeed.&nbsp; I have seen month after month where we would share our experiences in order to help someone else find an easier and faster solution to their issue.&nbsp; I attended my first Joint National Conference and came home with so many great ideas to put into practice. It was fun to share the week with fellow &ldquo;bean counters&rdquo; from across the nation.&nbsp; Our chapter worked hard to raise money for scholarships and we were rewarded by witnessing 5 aspiring accounting students not only receive a financial award but also learn from our &ldquo;pearls of wisdom&rdquo;.&nbsp; Our chapter is also about to embark on a financial literacy project where we can take what we know and share it with a group of people that would greatly benefit from our knowledge.&nbsp; I see now that by becoming more involved and attending the monthly meetings I have formed strong relationships with people that I look forward to seeing on a regular basis.&nbsp; It is great that the old image of the accounting profession of someone working with huge ledger books in a dimly light room all by themselves is truly a myth.&nbsp; Our profession is vibrant, energetic, technical, and most important of all, very social.&nbsp; Welcome to our social network!</p>\n" +
        		"</div>\n" +
        		"</div>\n" +
        		"<hr/></div>\n" +
        		"                  <hr />\n" +
        		"                  <div>\n" +
        		"                  <p><em><strong>ASWA has been recognized as a team environment, so let&apos;s\n" +
        		"                  continue to pull together and &quot;position ASWA as the home for all women accounting professionals\n" +
        		"                  whether new to the field, experienced or considering a career transition!</strong></em></p>\n" +
        		"                  </div>\n" +
        		"                  <hr />\n" +
        		"                  <p align=\"center\"><font size=\"3\" face=\"Arial, Helvetica, sans-serif\">You \n" +
        		"                      are visitor number</font><img src=\"http://counter.digits.com/wc/-d/4/aswasiliconvalley\" align=\"absmiddle\" width=\"60\" height=\"20\" border=\"0\" hspace=\"4\" vspace=\"2\" /></p>\n" +
        		"                  <p align=\"center\"><em>Powered by</em> <a href=\"http://www.digits.com/\"><img src=\"images/wc-03.gif\" width=\"197\" height=\"21\" vspace=\"0\" border=\"0\" align=\"absmiddle\" /></a></p>\n" +
        		"                  </td>\n" +
        		"                </tr>\n" +
        		"              </tbody>\n" +
        		"            </table>\n" +
        		"            <!-- End Body --> <!-- Begin Footer Table -->\n" +
        		"            <table bgcolor=\"#000066\">\n" +
        		"              <tbody>\n" +
        		"                <tr>\n" +
        		"                  <td class=\"footer\" width=\"382\">Copyright © 2003 ASWA</td>\n" +
        		"                  <td class=\"footer\" align=\"right\" width=\"384\">Site Produced by <a href=\"http://www.thirdeyevis.com\" class=\"footer\">Third Eye Visions</a> and hosted by <a href=\"http://www.fortheweb.com\" class=\"footer\">For the Web</a></td>\n" +
        		"                </tr>\n" +
        		"              </tbody>\n" +
        		"            </table>\n" +
        		"            <!-- End Footer Navigation --></td>\n" +
        		"          </tr>\n" +
        		"        </tbody>\n" +
        		"      </table>\n" +
        		"</div>\n" +
        		"<script type=\"text/javascript\" src=\"http://amplafi.net/bscripts/ampbp_2/amplafi.js\"></script>\n" +
        		"</body></html>");
        TestHtmlIssueProcessingHandler htmlIssueProcessingHandler = new TestHtmlIssueProcessingHandler();
        source.setHtmlIssueProcessingHandler(htmlIssueProcessingHandler);
        source.fullSequentialParse();
        source.getAllElements();
        assertTrue(htmlIssueProcessingHandler.getHtmlIssues().isEmpty());
    }
    public static class TestHtmlIssueProcessingHandler implements HtmlIssueProcessingHandler {
        private List<HtmlIssue> htmlIssues = new ArrayList<HtmlIssue>();
        /**
         * @see net.htmlparser.jericho.HtmlIssueProcessingHandler#htmlIssue(net.htmlparser.jericho.HtmlIssue)
         */
        @Override
        public void htmlIssue(HtmlIssue htmlIssue) {
            this.htmlIssues.add(htmlIssue);
        }
        public List<HtmlIssue> getHtmlIssues() {
            return this.htmlIssues;
        }
    }
}
