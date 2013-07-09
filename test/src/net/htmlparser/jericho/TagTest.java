package net.htmlparser.jericho;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class TagTest {
	@Test public void test() throws Exception {
		assertEquals("</div>",new Source("<div></div>").getFirstElement().getEndTag().tidy());
		assertEquals("</ABC>",new Source("<ABC></ABC>").getFirstElement().getEndTag().tidy());
		assertEquals("</div>",new Source("<div ></div\r\n   >").getFirstElement().getEndTag().tidy());
	}
}
