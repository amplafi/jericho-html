import net.htmlparser.jericho.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class SplitLongLines {
	private static final int MAX_LENGTH=70;

	private static int col;

	public static void main(String[] args) throws Exception {
		String sourceUrlString="../../doc/index.html";
		if (args.length==0)
		  System.err.println("Using default argument of \""+sourceUrlString+'"');
		else
			sourceUrlString=args[0];
		if (sourceUrlString.indexOf(':')==-1) sourceUrlString="file:"+sourceUrlString;
		URL sourceUrl=new URL(sourceUrlString);
		BufferedReader reader=null;
		try {
			reader=new BufferedReader(new InputStreamReader(sourceUrl.openStream()));
			String line;
			while ((line=reader.readLine())!=null) {
				if (line.length()<=MAX_LENGTH) {
					println(line);
					continue;
				}
				line=line.trim();
				if (line.length()<=MAX_LENGTH) {
					println(line);
					continue;
				}
				Source source=new Source(line);
				int pos=0;
				for (Tag tag : source.getAllTags()) {
					if (pos!=tag.getBegin()) print(line.subSequence(pos,tag.getBegin())); // print the text between this tag and the last
					printTag(tag,line);
					pos=tag.getEnd();
				}
				if (pos!=line.length()) print(line.subSequence(pos,line.length())); // print the text between the last tag and the end of line
				println();
			}
		} finally {
			if (reader!=null) reader.close();
		}
  }

	private static void println() {
		System.out.println();
		col=0;
	}

	private static void println(CharSequence text) {
		System.out.println(text);
		col=0;
	}

	private static void print(CharSequence text) {
		print(text,true);
	}

	private static void print(CharSequence text, boolean splitLongText) {
		if (splitLongText && text.length()>MAX_LENGTH) {
			String[] words=text.toString().split("\\s");
			for (int i=0; i<words.length; i++) {
				print(words[i],false);
				if (i<words.length-1) print(" ");
			}
			return;
		}
		if (col>0 && col+text.length()>MAX_LENGTH) println();
		System.out.print(text);
		col+=text.length();
	}

	private static void printTag(Tag tag, String line) {
		if (tag.length()<=MAX_LENGTH || tag instanceof EndTag) {
			print(tag);
			return;
		}
		StartTag startTag=(StartTag)tag;
		Attributes attributes=startTag.getAttributes();
		if (attributes!=null) {
			print(line.substring(startTag.getBegin(),attributes.getBegin()));
			for (Attribute attribute : attributes) {
				print(" ");
				print(attribute);
			}
			print(line.substring(attributes.getEnd(),startTag.getEnd()));
		} else {
			print(startTag);
		}
	}
}
