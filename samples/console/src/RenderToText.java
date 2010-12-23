import net.htmlparser.jericho.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class RenderToText {
	public static void main(String[] args) throws Exception {
		String sourceUrlString="data/test.html";
		if (args.length==0)
		  System.err.println("Using default argument of \""+sourceUrlString+'"');
		else
			sourceUrlString=args[0];
		if (sourceUrlString.indexOf(':')==-1) sourceUrlString="file:"+sourceUrlString;
		Source source=new Source(new URL(sourceUrlString));
		String renderedText=source.getRenderer().toString();
		System.out.println("\nSimple rendering of the HTML document:\n");
		System.out.println(renderedText);
  }
}
