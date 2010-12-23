import net.htmlparser.jericho.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class StreamedSourceCopy {
	public static void main(String[] args) throws Exception {
		String sourceUrlString="data/test.html";
		if (args.length==0)
		  System.err.println("Using default argument of \""+sourceUrlString+'"');
		else
			sourceUrlString=args[0];
		if (sourceUrlString.indexOf(':')==-1) sourceUrlString="file:"+sourceUrlString;
		StreamedSource streamedSource=new StreamedSource(new URL(sourceUrlString));
		Writer writer=null;
		try {
			writer=new FileWriter("StreamedSourceCopyOutput.html");
			System.out.println("Processing segments:");
			int lastSegmentEnd=0;
			for (Segment segment : streamedSource) {
				System.out.println(segment.getDebugInfo());
				if (segment.getEnd()<=lastSegmentEnd) continue; // if this tag is inside the previous tag (e.g. a server tag) then ignore it as it was already output along with the previous tag.
				lastSegmentEnd=segment.getEnd();
				if (segment instanceof Tag) {
					Tag tag=(Tag)segment;
					// HANDLE TAG
					// Uncomment the following line to ensure each tag is valid XML:
					// writer.write(tag.tidy()); continue;
				} else if (segment instanceof CharacterReference) {
					CharacterReference characterReference=(CharacterReference)segment;
					// HANDLE CHARACTER REFERENCE
					// Uncomment the following line to decode all character references instead of copying them verbatim:
					// characterReference.appendCharTo(writer); continue;
				} else {
					// HANDLE PLAIN TEXT
				}
				// unless specific handling has prevented getting to here, simply output the segment as is:
				writer.write(segment.toString());
			}
			writer.close();
			System.err.println("\nA copy of the source document has been output to StreamedSourceCopyOuput.html");
		} catch (Throwable t) {
			if (writer!=null) try {writer.close();} catch (IOException ex) {}
		}
  }
}
