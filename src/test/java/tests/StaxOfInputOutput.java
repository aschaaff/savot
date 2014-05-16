package tests;
import java.util.Date;

import cds.savot.stax.*;
import cds.savot.writer.*;

public class StaxOfInputOutput {

	public StaxOfInputOutput() {}

	@SuppressWarnings("deprecation")
	public void test(String file) throws Exception {

		System.out.println("Parsing begin ");
		Date dates = new Date();
		SavotStaxParser parser = new SavotStaxParser(file, SavotStaxParser.FULL, false);
		Date datef = new Date();
		System.out.println("Parsing ends with a duration of " + ((datef.getHours()*3600 + datef.getMinutes()*60 + datef.getSeconds()) - (dates.getHours()*3600 + dates.getMinutes()*60 + dates.getSeconds())) + " s");
				
		System.out.println("============================== Table ==============================");
		System.out.println("Il y a " + parser.getAllResources().getResources().getItemAt(0).getTables().getItemCount() + " tables");
		for (int i = 0 ; i < parser.getAllResources().getResources().getItemAt(0).getTables().getItemCount(); i++) {
			System.out.print(parser.getAllResources().getResources().getItemAt(0).getTables().getItemAt(i).getName() + " avec le nb de Fields : ");
			System.out.print(parser.getAllResources().getResources().getItemAt(0).getTables().getItemAt(i).getFields().getItemCount());
			System.out.println(" et de nom : " + parser.getAllResources().getResources().getItemAt(0).getTables().getItemAt(i).getFields().getItemAt(0).getName());
			System.out.println(" et de nb de lignes : " + parser.getAllResources().getResources().getItemAt(0).getTables().getItemAt(i).getData().getTableData().getTRs().getItemCount());
			System.out.println(" ayant chacune nb data : " + parser.getAllResources().getResources().getItemAt(0).getTables().getItemAt(i).getData().getTableData().getTRs().getItemAt(0).getTDs().getItemCount());			
			System.out.println(parser.getAllResources().getInfos().getItemAt(0).getContent());			

		}
		System.out.println("============================== Table end ==============================");

		System.out.println("Writing begin");
		dates = new Date();
		SavotWriter writer = new SavotWriter();
		writer.generateDocument(parser.getAllResources(), "/Users/andre/MesDossiers/EnCours/SAVOT/VOTable-Tests-V4.0/vizier_votable3Bis.vot");
		datef = new Date();		
		System.out.println("Writing ends with a duration of " + ((datef.getHours()*3600 + datef.getMinutes()*60 + datef.getSeconds()) - (dates.getHours()*3600 + dates.getMinutes()*60 + dates.getSeconds())) + " s");
	}

	public static void main(String [] argv) throws Exception {
		new StaxOfInputOutput().test("/Users/andre/MesDossiers/EnCours/SAVOT/VOTable-Tests-V4.0/vizier_votable3.vot");
		System.exit(0);
	}
}