import net.htmlparser.jericho.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class FormFieldCSVOutput {
	// newValuesMap is designed to emulate the data structure returned by the
	// javax.servlet.ServletRequest.getParameterMap() method.
	private static Map<String,String[]> newValuesMap=new LinkedHashMap<String,String[]>();
	static {
		newValuesMap.put("Name",new String[] {"Humphrey Bear"});
		newValuesMap.put("Title",new String[] {"Prime Minister"});
		newValuesMap.put("Member",new String[] {"on"});
		newValuesMap.put("Address",new String[] {"The Lodge\nDeakin  ACT  2600\nAustralia"});
		newValuesMap.put("MailingList",new String[] {"A","B"});
		newValuesMap.put("FavouriteFare",new String[] {"honey"});
		newValuesMap.put("FavouriteSports",new String[] {"BB","AFL"});
	}

	public static void main(String[] args) throws Exception {
		String sourceUrlString="data/form.html";
		if (args.length==0)
		  System.err.println("Using default argument of \""+sourceUrlString+'"');
		else
			sourceUrlString=args[0];
		if (sourceUrlString.indexOf(':')==-1) sourceUrlString="file:"+sourceUrlString;
		Source source=new Source(new URL(sourceUrlString));
		FormFields formFields=source.getFormFields();
		Writer out=new FileWriter("FormData.csv");
		Util.outputCSVLine(out,formFields.getColumnLabels());
		Util.outputCSVLine(out,formFields.getColumnValues(newValuesMap));
		out.close();
		System.err.println("\nThe following form submission data has been output to the CSV file \nFormData.csv, based on the data structure defined in the HTML document \n"+sourceUrlString+'\n');
		System.err.println(format(newValuesMap));
		System.err.println("The FormData.csv file will open automatically after you press a key.");
  }

	private static String format(Map<String,String[]> valuesMap) {
		StringBuilder sb=new StringBuilder();
		for (Map.Entry<String,String[]> entry : valuesMap.entrySet()) {
			sb.append(entry.getKey()).append(":\n");
			for (String value : entry.getValue()) sb.append("- ").append(value).append('\n');
			sb.append('\n');
		}
		return sb.toString();
	}
}
