import net.htmlparser.jericho.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class FormControlDisplayCharacteristics {
	public static void main(String[] args) throws Exception {
		String sourceUrlString="data/form.html";
		if (args.length==0)
		  System.err.println("Using default argument of \""+sourceUrlString+'"');
		else
			sourceUrlString=args[0];
		if (sourceUrlString.indexOf(':')==-1) sourceUrlString="file:"+sourceUrlString;
		Source source=new Source(new URL(sourceUrlString));
		FormFields formFields=source.getFormFields();
		// disable some controls:
		formFields.get("Password").getFormControl().setDisabled(true);
		formFields.setValue("MailingList","C");
		formFields.get("MailingList").getFormControl("C").setDisabled(true);
		formFields.get("MailingList").getFormControl("D").setDisabled(true);
		// remove some controls:
		formFields.get("button1").getFormControl().setOutputStyle(FormControlOutputStyle.REMOVE);
		FormControl rhubarbFormControl=formFields.get("FavouriteFare").getFormControl("rhubarb");
		rhubarbFormControl.setOutputStyle(FormControlOutputStyle.REMOVE);
		// set some controls to display value:
		formFields.setValue("Address","The Lodge\nDeakin  ACT  2600\nAustralia");
		formFields.get("Address").getFormControl().setOutputStyle(FormControlOutputStyle.DISPLAY_VALUE);
		formFields.setValue("FavouriteSports","BB");
		formFields.addValue("FavouriteSports","AFL");
		formFields.get("FavouriteSports").getFormControl().setOutputStyle(FormControlOutputStyle.DISPLAY_VALUE);
		OutputDocument outputDocument=new OutputDocument(source);
		outputDocument.replace(formFields); // adds all segments necessary to effect changes
		// also need to remove label for the removed "rhubarb" radio button:
		// label segment begins at the end of the rhubarb control, and ends at the start of the next control:
		Segment rhubarbLabelSegment=new Segment(source,rhubarbFormControl.getEnd(),source.getNextTag(rhubarbFormControl.getEnd()).getBegin());
		outputDocument.remove(rhubarbLabelSegment);
		// also need to remove instructions for favourite sports control which has been set to output display value:
		Segment instructionsSegment=source.getFirstElement("class","instructions",false).getContent();
		outputDocument.replace(instructionsSegment,"A comma separated list of favourite sports is shown above");

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
