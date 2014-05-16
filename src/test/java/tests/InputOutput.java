package tests;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import cds.savot.pull.*;
import cds.savot.writer.*;

public class InputOutput {

	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public InputOutput(String file) throws IOException {
		
		System.out.println("Parsing begin ");
		Date dates = new Date();
		SavotPullParser parser = new SavotPullParser(file, SavotPullEngine.FULL, false);
		Date datef = new Date();
		System.out.println("Parsing ends with a duration of " + ((datef.getHours()*3600 + datef.getMinutes()*60 + datef.getSeconds()) - (dates.getHours()*3600 + dates.getMinutes()*60 + dates.getSeconds())) + " s");
				
		System.out.println("============================== Table ==============================");
		System.out.println("Il y a " + parser.getAllResources().getResources().getItemAt(0).getTables().getItemCount() + " tables");
		for (int i = 0 ; i < parser.getAllResources().getResources().getItemAt(0).getTables().getItemCount(); i++) {
			System.out.print(parser.getAllResources().getResources().getItemAt(0).getTables().getItemAt(i).getName() + " avec le nb de Fields : ");
			System.out.print(parser.getAllResources().getResources().getItemAt(0).getTables().getItemAt(i).getFields().getItemCount());
			System.out.println(" et de nom : " + parser.getAllResources().getResources().getItemAt(0).getTables().getItemAt(i).getFields().getItemAt(0).getName());
			System.out.println(parser.getAllResources().getResources().getItemAt(0).getTables().getItemAt(i).getData().getTableData().getTRs().getItemCount());
			System.out.println(parser.getAllResources().getResources().getItemAt(0).getTables().getItemAt(i).getData().getTableData().getTRs().getItemAt(0).getTDs().getItemCount());			
			System.out.println(parser.getAllResources().getInfos().getItemAt(0).getContent());			

		}
		System.out.println("============================== Table end ==============================");
		System.out.println("Writing begin");
		dates = new Date();
		SavotWriter writer = new SavotWriter();
		writer.generateDocument(parser.getAllResources(), "/Users/andre/MesDossiers/EnCours/SAVOT/VOTable-Tests-V4.0/vizier_votable7Bis.vot");
		datef = new Date();		
		System.out.println("Writing ends with a duration of " + ((datef.getHours()*3600 + datef.getMinutes()*60 + datef.getSeconds()) - (dates.getHours()*3600 + dates.getMinutes()*60 + dates.getSeconds())) + " s");
	}

	public static void main(String [] argv) throws IOException {
		new InputOutput("/Users/andre/MesDossiers/EnCours/SAVOT/VOTable-Tests-V4.0/vizier_votable7.vot");
		System.exit(0);
	}
}