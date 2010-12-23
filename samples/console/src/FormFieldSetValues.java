import net.htmlparser.jericho.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class FormFieldSetValues {
	public static void main(String[] args) throws Exception {
		String sourceUrlString="data/form.html";
		if (args.length==0)
		  System.err.println("Using default argument of \""+sourceUrlString+'"');
		else
			sourceUrlString=args[0];
		if (sourceUrlString.indexOf(':')==-1) sourceUrlString="file:"+sourceUrlString;
		Source source=new Source(new URL(sourceUrlString));
		FormFields formFields=source.getFormFields();
		formFields.clearValues(); // clear any values that might be set in the source document
		formFields.addValue("Name","Humphrey Bear");
		formFields.addValue("Title","Prime Minister");
		formFields.addValue("Member","on");
		formFields.addValue("Address","The Lodge\nDeakin  ACT  2600\nAustralia");
		formFields.addValue("MailingList","A");
		formFields.addValue("MailingList","B");
		formFields.addValue("FavouriteFare","honey");
		formFields.addValue("FavouriteSports","BB");
		formFields.addValue("FavouriteSports","AFL");
		OutputDocument outputDocument=new OutputDocument(source);
		outputDocument.replace(formFields); // adds all segments necessary to effect changes

		// modify stylesheet link since the output file is in a different directory to the input file
		int cssPathPos=source.toString().indexOf("main.css");
		outputDocument.insert(cssPathPos,"data/");

		Writer out=new FileWriter("NewForm.html");
		outputDocument.writeTo(out);
		out.close();
		System.err.println("\nThe form containing new default values has been output to NewForm.html");
		System.err.println("This will open automatically in a web browser after you press a key.");
  }
}
