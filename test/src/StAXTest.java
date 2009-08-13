/*// comment out entire class as it requires JDK 1.6
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.stream.*;

public class StAXTest {
	private static final String sourceUrlString="file:test/data/StAXTest.html";

	@Test public void test1() throws Exception {
		InputStream in=null;
		try {
			in=new URL(sourceUrlString).openStream();
			XMLInputFactory factory = XMLInputFactory.newInstance();
			factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,Boolean.FALSE);
			factory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event",Boolean.TRUE);
			assertTrue(factory.isPropertySupported(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES));
			//factory.setProperty(XMLInputFactory.IS_COALESCING,Boolean.TRUE);
			XMLStreamReader parser = factory.createXMLStreamReader(in);
			while (true) {
				int event = parser.next();
				if (event == XMLStreamConstants.START_ELEMENT) {
					System.out.println(parser.getLocalName());
				} else if (event == XMLStreamConstants.ATTRIBUTE) {
					System.out.println("attribute");
				} else if (event == XMLStreamConstants.END_ELEMENT) {
					System.out.println("/"+parser.getLocalName());
				} else if (event == XMLStreamConstants.CHARACTERS) {
					System.out.println("length="+parser.getTextLength());
					System.out.println("buffer size="+parser.getTextCharacters().length);
					System.out.println("CHARACTERS*"+parser.getText()+"*");
				} else if (event == XMLStreamConstants.CDATA) {
					System.out.println("CDATA*"+parser.getText()+"*");
				} else if (event == XMLStreamConstants.COMMENT) {
					System.out.println("length="+parser.getTextLength());
					System.out.println("buffer size="+parser.getTextCharacters().length);
					System.out.println("COMMENT*"+parser.getText()+"*");
				} else if (event == XMLStreamConstants.SPACE) {
					System.out.println("space*"+parser.getText()+"*");
				} else if (event == XMLStreamConstants.END_DOCUMENT) {
					parser.close();
					break;
				} else if (event == XMLStreamConstants.PROCESSING_INSTRUCTION) {
					System.out.println(parser.getPITarget());
				} else if (event == XMLStreamConstants.ENTITY_REFERENCE) {
					System.out.println("ENTITY_REFERENCE*"+parser.getText()+"*"); // can't get this to happen
				} else if (event == XMLStreamConstants.DTD) {
					System.out.println("DTD");
				}
			}
		} finally {
			if (in!=null) in.close();
		}
	}
}
*/