import net.htmlparser.jericho.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class CompactSource {
	public static void main(String[] args) throws Exception {
		String sourceUrlString="data/test.html";
		if (args.length==0)
		  System.err.println("Using default argument of \""+sourceUrlString+'"');
		else
			sourceUrlString=args[0];
		if (sourceUrlString.indexOf(':')==-1) sourceUrlString="file:"+sourceUrlString;
		MicrosoftTagTypes.register();
		PHPTagTypes.register();
		MasonTagTypes.register();
		Source source=new Source(new URL(sourceUrlString));

		new SourceCompactor(source).writeTo(new OutputStreamWriter(System.out));
	}
}
