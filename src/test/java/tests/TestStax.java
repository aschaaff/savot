package tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public class TestStax {

    public static void main(String args[]) throws Exception {
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        XMLStreamReader xmlsr = xmlif.createXMLStreamReader(new FileReader("/Users/andre/MesDossiers/EnCours/SAVOT/VOTable-Tests-V4.0/vizier_votable1.vot"));

        int eventType;

        Date date = new Date();

        Long start = date.getTime();

        File file = new File("/Users/andre/MesDossiers/EnCours/SAVOT/VOTable-Tests-V4.0/vizier_votable1bis.vot");

        FileOutputStream outStream = new FileOutputStream(file);

        BufferedWriter dataBuffWriter = new BufferedWriter(new OutputStreamWriter(outStream));

        while (xmlsr.hasNext()) {
            eventType = xmlsr.next();
            switch (eventType) {
                case XMLEvent.START_DOCUMENT:
                    //System.out.println("</" + xmlsr. + ">");
                    break;

                case XMLEvent.CDATA:
                    //System.out.println(xmlsr.);
                    break;

                case XMLEvent.COMMENT:
                    //System.out.println("comment --> " + xmlsr.getText());
                    dataBuffWriter.write("<!--" + xmlsr.getText() + "-->");
                    break;

                case XMLEvent.START_ELEMENT:
                    //System.out.print("<" + xmlsr.getLocalName() + ">");
                    dataBuffWriter.write("<" + xmlsr.getLocalName() + ">");
                    break;

                case XMLEvent.ATTRIBUTE:
                    //System.out.println("</" + xmlsr.getLocalName() + ">");
                    break;

                case XMLEvent.END_ELEMENT:
                    //System.out.println("</" + xmlsr.getLocalName() + ">");
                    if (xmlsr.getLocalName().equalsIgnoreCase("TD")) {
                        dataBuffWriter.write("</" + xmlsr.getLocalName() + ">");
                    } else {
                        dataBuffWriter.write("</" + xmlsr.getLocalName() + ">");
                    }
                    break;

                case XMLEvent.CHARACTERS:
                    //String chaine = xmlsr.getText();
                    //if (!xmlsr.isWhiteSpace()) {
                    //	System.out.print(chaine);
                    //}
                    break;

                case XMLEvent.END_DOCUMENT:
                    //System.out.println("</" + xmlsr.getLocalName() + ">");
                    break;

                case XMLEvent.NAMESPACE:
                    //System.out.println("</" + xmlsr.getLocalName() + ">");
                    break;

                case XMLEvent.SPACE:
                    //System.out.println("</" + xmlsr.getLocalName() + ">");
                    break;

                default:
                    break;
            }
        }
        Date date2 = new Date();
        System.out.println((double) (date2.getTime() - start) / 1000);
        outStream.close();
    }
}
