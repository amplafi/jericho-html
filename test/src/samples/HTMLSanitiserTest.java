package samples;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

@Ignore
public class HTMLSanitiserTest {
//	@Test public void testEncodeInvalidMarkup() {
//		assertEquals("abc",HTMLSanitiser.encodeInvalidMarkup("abc")); // return text verbatim without markup
//		assertEquals("ab &amp; c",HTMLSanitiser.encodeInvalidMarkup("ab & c")); // encode text
//		assertEquals("abc <p>def</p> geh",HTMLSanitiser.encodeInvalidMarkup("abc <p>def</p> geh")); // keep <P> element
//		assertEquals("abc <b>def</b> geh",HTMLSanitiser.encodeInvalidMarkup("abc <b>def</b> geh")); // keep <B> element
//		assertEquals("abc <i>def</i> geh",HTMLSanitiser.encodeInvalidMarkup("abc <i>def</i> geh")); // keep <I> element
//		assertEquals("abc <a href=\"http://url\">def</a> geh",HTMLSanitiser.encodeInvalidMarkup("abc <a href=\"http://url\">def</a> geh")); // keep <A> element
//		assertEquals("abc <a href=\"http://url\" target=\"_blank\" title=\"Click here for link\">def</a> geh",HTMLSanitiser.encodeInvalidMarkup("abc <a href=\"http://url\" target=\"_blank\" title=\"Click here for link\">def</a> geh")); // keep href, target and title attributes
//		assertEquals("abc <a href=\"http://url?p1=x&amp;p2=y\">def</a> geh",HTMLSanitiser.encodeInvalidMarkup("abc <a href=\"http://url?p1=x&p2=y\">def</a> geh")); // encode parameter values
//		assertEquals("abc &lt;u&gt;def&lt;/u&gt; geh",HTMLSanitiser.encodeInvalidMarkup("abc <u>def</u> geh")); // <U> element not allowed
//		assertEquals("<p>abc</p>",HTMLSanitiser.encodeInvalidMarkup("<p>abc")); // add optional end tag
//		assertEquals("abc<br />def",HTMLSanitiser.encodeInvalidMarkup("abc<br>def")); // convert to XHTML empty element tag
//		assertEquals("&lt;script&gt;abc&lt;/script&gt;",HTMLSanitiser.encodeInvalidMarkup("<script>abc</script>")); // remove potentially dangerous script
//		assertEquals("<p class=\"xyz\">abc</p>",HTMLSanitiser.encodeInvalidMarkup("<p class=\"xyz\" onmouseover=\"nastyscript\">abc</p>")); // keep approved attributes but strip non-approved attributes
//		assertEquals("<p id=\"abc\" class=\"xyz\">abc</p>",HTMLSanitiser.encodeInvalidMarkup("<p id=\"abc\" class=\"xyz\">abc</p>")); // keep id and class attributes
//		assertEquals("<p id=\"abc\" class=\"xyz\">abc</p>",HTMLSanitiser.encodeInvalidMarkup("<p id=abc class='xyz'>abc</p>")); // tidy up attributes to make them XHTML compliant
//		assertEquals("List:<ul><li>A</li><li>B</li><li>C</li></ul>",HTMLSanitiser.encodeInvalidMarkup("List:<ul><li>A</li><li>B<li>C</ul>")); // inserts optional end tags
//		assertEquals("List:&lt;li&gt;A&lt;/li&gt;&lt;li&gt;B&lt;li&gt;C",HTMLSanitiser.encodeInvalidMarkup("List:<li>A</li><li>B<li>C")); // missing required <UL> or <OL> element
//		assertEquals("List:&lt;ul&gt;&lt;li&gt;A&lt;/li&gt;&lt;li&gt;B&lt;li&gt;C",HTMLSanitiser.encodeInvalidMarkup("List:<ul><li>A</li><li>B<li>C")); // missing required </UL> end tag
//		assertEquals("List:<ul><li>A</li><li><b>B</b></li><li>C</li></ul>",HTMLSanitiser.encodeInvalidMarkup("List:<ul><li>A</li><li><b>B</b><li>C</ul>")); // inserts optional end tags
//		assertEquals("List:<ul><li>A</li><b>&lt;li&gt;B</b><li>C</li></ul>",HTMLSanitiser.encodeInvalidMarkup("List:<ul><li>A</li><b><li>B</b><li>C</ul>")); // <LI> is invalid as it is not directly under <UL> or <OL>
//	}
//
//	@Test public void testStripInvalidMarkup() {
//		assertEquals("abc",HTMLSanitiser.stripInvalidMarkup("abc")); // return text verbatim without markup
//		assertEquals("ab &amp; c",HTMLSanitiser.stripInvalidMarkup("ab & c")); // encode text
//		assertEquals("abc <p>def</p> geh",HTMLSanitiser.stripInvalidMarkup("abc <p>def</p> geh")); // keep <P> element
//		assertEquals("abc <b>def</b> geh",HTMLSanitiser.stripInvalidMarkup("abc <b>def</b> geh")); // keep <B> element
//		assertEquals("abc <i>def</i> geh",HTMLSanitiser.stripInvalidMarkup("abc <i>def</i> geh")); // keep <I> element
//		assertEquals("abc <a href=\"http://url\">def</a> geh",HTMLSanitiser.stripInvalidMarkup("abc <a href=\"http://url\">def</a> geh")); // keep <A> element
//		assertEquals("abc <a href=\"http://url\" target=\"_blank\" title=\"Click here for link\">def</a> geh",HTMLSanitiser.stripInvalidMarkup("abc <a href=\"http://url\" target=\"_blank\" title=\"Click here for link\">def</a> geh")); // keep href, target and title attributes
//		assertEquals("abc <a href=\"http://url?p1=x&amp;p2=y\">def</a> geh",HTMLSanitiser.stripInvalidMarkup("abc <a href=\"http://url?p1=x&p2=y\">def</a> geh")); // encode parameter values
//		assertEquals("abc def geh",HTMLSanitiser.stripInvalidMarkup("abc <u>def</u> geh")); // <U> element not allowed
//		assertEquals("<p>abc</p>",HTMLSanitiser.stripInvalidMarkup("<p>abc")); // add optional end tag
//		assertEquals("abc<br />def",HTMLSanitiser.stripInvalidMarkup("abc<br>def")); // convert to XHTML empty element tag
//		assertEquals("abc",HTMLSanitiser.stripInvalidMarkup("<script>abc</script>")); // remove potentially dangerous script
//		assertEquals("<p class=\"xyz\">abc</p>",HTMLSanitiser.stripInvalidMarkup("<p class=\"xyz\" onmouseover=\"nastyscript\">abc</p>")); // keep approved attributes but strip non-approved attributes
//		assertEquals("<p id=\"abc\" class=\"xyz\">abc</p>",HTMLSanitiser.stripInvalidMarkup("<p id=\"abc\" class=\"xyz\">abc</p>")); // keep id and class attributes
//		assertEquals("<p id=\"abc\" class=\"xyz\">abc</p>",HTMLSanitiser.stripInvalidMarkup("<p id=abc class='xyz'>abc</p>")); // tidy up attributes to make them XHTML compliant
//		assertEquals("List:<ul><li>A</li><li>B</li><li>C</li></ul>",HTMLSanitiser.stripInvalidMarkup("List:<ul><li>A</li><li>B<li>C</ul>")); // inserts optional end tags
//		assertEquals("List:ABC",HTMLSanitiser.stripInvalidMarkup("List:<li>A</li><li>B<li>C")); // missing required <UL> or <OL> element
//		assertEquals("List:ABC",HTMLSanitiser.stripInvalidMarkup("List:<ul><li>A</li><li>B<li>C")); // missing required </UL> end tag
//		assertEquals("List:<ul><li>A</li><li><b>B</b></li><li>C</li></ul>",HTMLSanitiser.stripInvalidMarkup("List:<ul><li>A</li><li><b>B</b><li>C</ul>")); // inserts optional end tags
//		assertEquals("List:<ul><li>A</li><b>B</b><li>C</li></ul>",HTMLSanitiser.stripInvalidMarkup("List:<ul><li>A</li><b><li>B</b><li>C</ul>")); // <LI> is invalid as it is not directly under <UL> or <OL>
//	}
//
//	@Test public void testStripInvalidMarkupWithFormatting() {
//		assertEquals("abc\n   def",HTMLSanitiser.stripInvalidMarkup("abc\n   def",false)); // no conversion of formatting characters
//		assertEquals("abc<br />def",HTMLSanitiser.stripInvalidMarkup("abc\ndef",true)); // convert LF to <BR>
//		assertEquals("abc<br />def",HTMLSanitiser.stripInvalidMarkup("abc\rdef",true)); // convert CR to <BR>
//		assertEquals("abc<br />def",HTMLSanitiser.stripInvalidMarkup("abc\r\ndef",true)); // convert CRLF to <BR>
//		assertEquals("&nbsp; &nbsp; abc",HTMLSanitiser.stripInvalidMarkup("    abc",true)); // ensure consecutive spaces are rendered
//		assertEquals("&nbsp; &nbsp; abc",HTMLSanitiser.stripInvalidMarkup("\tabc",true)); // convert TAB to equivalent of four spaces
//	}
}
