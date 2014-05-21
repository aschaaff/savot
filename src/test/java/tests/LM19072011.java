package tests;

import cds.savot.model.*;
import cds.savot.pull.*;
import cds.savot.writer.*;

/** Pour tester la methode getType de SavotResource */
public class LM19072011 {

    public LM19072011() {
    }

    public void test(String file) throws Exception {

        SavotPullParser parser = new SavotPullParser(file, SavotPullEngine.ROWREAD);
        SavotVOTable voTable = parser.getVOTable();
        //SavotTR currentTR = parser.getNextTR();

        /*
         * Current Saada Release only read the first resource
         */
        System.out.println(voTable.getResources().getItemCount());

        SavotResource currentResource = (SavotResource) voTable.getResources().getItemAt(0);
        if (currentResource == null) {
            throw new Exception("File <" + file + "> is not a VOTable");
        }

        SavotWriter writer = new SavotWriter();

        writer.generateDocument(parser.getAllResources(), "/Users/andre/MesDossiers/EnCours/SAVOT/VOTable/LM/bin_result_out.xml");
    }

    public static void main(String[] argv) throws Exception {
        new LM19072011().test("/Users/andre/MesDossiers/EnCours/SAVOT/VOTable/LM/bin_result.xml");
    }
}
