import net.htmlparser.jericho.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class Encoding {
	public static void main(String[] args) throws Exception {
		String sourceUrlString="data/test.html";
		if (args.length==0)
		  System.err.println("Using default argument of \""+sourceUrlString+'"');
		else
			sourceUrlString=args[0];
		if (sourceUrlString.indexOf(':')==-1) sourceUrlString="file:"+sourceUrlString;
		System.out.println("\nSource URL:");
		System.out.println(sourceUrlString);
		URL url=new URL(sourceUrlString);
		Source source=new Source(url);
		System.out.println("\nDocument Title:");
		Element titleElement=source.getFirstElement(HTMLElementName.TITLE);
		System.out.println(titleElement!=null ? titleElement.getContent().toString() : "(none)");
		System.out.println("\nSource.getEncoding():");
		System.out.println(source.getEncoding());
		System.out.println("\nSource.getEncodingSpecificationInfo():");
		System.out.println(source.getEncodingSpecificationInfo());
		System.out.println("\nSource.getPreliminaryEncodingInfo():");
		System.out.println(source.getPreliminaryEncodingInfo());
	}
}
