package samples;

//Copyright 2002-2014 - UDS/CNRS
//The SAVOT library is distributed under the terms
//of the GNU General Public License version 3.
//
//This file is part of SAVOT.
//
//SAVOT is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, version 3 of the License.
//
//SAVOT is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//The GNU General Public License is available in COPYING file
//along with SAVOT.
//
//SAVOT - Simple Access to VOTable - Parser
//
//Author, Co-Author:  Andre Schaaff (CDS), Laurent Bourges (JMMC)
import java.io.File;
import java.io.PrintStream;
import java.util.Date;

import cds.savot.binary.DataBinaryReader;
import cds.savot.model.FieldSet;
import cds.savot.model.SavotBinary;

import cds.savot.pull.SavotPullEngine;
import cds.savot.stax.SavotStaxParser;

/**
 * Tests the parsing of a "binary" VOTable by Savot + DataBinaryReader.
 * 
 * The Savot parser used is SavotPullParser in mode SEQUENTIAL.
 * 
 * @author Gregory Mantelet
 * @since 21/09/2011
 * 
 * @see DataBinaryReader
 */
public class VotableReader_SavotBinary {

    SavotStaxParser sb = null;

    public static final void main(String[] args) throws Exception {
        VotableReader_SavotBinary parser = new VotableReader_SavotBinary();
        parser.parse(VOTABLE_PATH);
    }

    // REQUIRED PARAMETERS:
    private static final String PARENT_DIRECTORY = "/Users/andre/MesDossiers/EnCours/SAVOT/VOTable-Tests-V4.0";
    private static final String VOTABLE_PATH = PARENT_DIRECTORY + File.separatorChar + "vizier_votable9.b64";

    // OPTIONAL PARAMETERS:
    /** Path of the file in which decoded data must be written. "null" means System.out. */
    private static final String DATA_OUTPUT = null;

    public VotableReader_SavotBinary() {;
    }

    @SuppressWarnings("deprecation")
    public void parse(final String fileOrUrl) throws Exception {
        System.out.println("Parsing begin ");
        Date dates = new Date();

        // begin the parsing
        sb = new SavotStaxParser(fileOrUrl, SavotPullEngine.FULL);

//		sb = new SavotPullParser(fileOrUrl, SavotPullEngine.FULL);
        System.out.println("item count : " + sb.getAllResources().getResources().getItemCount());

//		SavotStaxParser sb = new SavotStaxParser(fileOrUrl, SavotPullEngine.SEQUENTIAL);
/*
         // get the next resource of the VOTable file
         SavotResource currentResource = sb.getNextResource();

         // while a resource is available
         while (currentResource != null) {
         System.out.println("*");

         // for each table of this resource
         for (int i = 0; i < currentResource.getTableCount(); i++) {
         System.out.println("*** COLUMNS ***");

         FieldSet fields = currentResource.getFieldSet(i);
         for(int j=0 ; j<fields.getItemCount(); j++){
         SavotField field = (SavotField)fields.getItemAt(j);
         System.out.println("\t- "+field.getName()+" ("+field.getDescription()+") - "+field.getDataType()+"["+field.getArraySize()+"] - "+field.getUnit()+" - "+field.getUcd()+" - "+field.getUtype());
         }


         SavotBinary binary = currentResource.getData(i).getBinary();
         if (binary != null){
         // Read the raw binary data:
         DataBinaryReader parser = new DataBinaryReader(binary.getStream(), fields, false, PARENT_DIRECTORY);
         PrintStream output = (DATA_OUTPUT != null) ? new PrintStream(DATA_OUTPUT) : System.out;
         output.println("\n*** DATA ***");
         while(parser.next()){
         output.print("-> ");
         for(int j=0; j<fields.getItemCount(); j++)
         output.print(parser.getCellAsString(j)+" ; ");
         output.println();
         }
         parser.close();
         output.close();
         System.out.println("\nDONE");
         }
         }
			
         // get the next resource
         currentResource = sb.getNextResource();
         }
         */
        Date datef = new Date();
        System.out.println("Parsing ends with a duration of " + ((datef.getHours() * 3600 + datef.getMinutes() * 60 + datef.getSeconds()) - (dates.getHours() * 3600 + dates.getMinutes() * 60 + dates.getSeconds())) + " s");

        for (int item = 0; item < sb.getAllResources().getResources().getItemCount(); item++) {
            SavotBinary binary = sb.getAllResources().getResources().getItemAt(item).getData(0).getBinary();
            if (binary != null) {
                FieldSet fields = sb.getAllResources().getResources().getItemAt(item).getFieldSet(0);
                @SuppressWarnings("resource")
                DataBinaryReader parser = new DataBinaryReader(binary.getStream(), fields, false, PARENT_DIRECTORY);
                @SuppressWarnings("resource")
                PrintStream output = (DATA_OUTPUT != null) ? new PrintStream(DATA_OUTPUT) : System.out;
                output.println("\n*** DATA ***");
                while (parser.next()) {
                    output.print("-> ");
                    for (int j = 0; j < fields.getItemCount(); j++) {
                        output.print(parser.getCellAsString(j) + " ; ");
                    }
                    output.println();
                }
            } else {
                System.out.println("no binary");
            }
        }
        /*		
         for (int i = 0 ; i < sb.getAllResources().getResources().getItemAt(0).getTables().getItemCount(); i++) {
         System.out.print(sb.getAllResources().getResources().getItemAt(0).getTables().getItemAt(i).getName() + " avec le nb de Fields : ");
         System.out.print(sb.getAllResources().getResources().getItemAt(0).getTables().getItemAt(i).getFields().getItemCount());
         System.out.println(" et de nom : " + sb.getAllResources().getResources().getItemAt(0).getTables().getItemAt(i).getFields().getItemAt(0).getName());
         System.out.println(" et de nb de lignes : " + sb.getAllResources().getResources().getItemAt(0).getTables().getItemAt(i).getData().getTableData().getTRs().getItemCount());
         System.out.println(" ayant chacune nb data : " + sb.getAllResources().getResources().getItemAt(0).getTables().getItemAt(i).getData().getTableData().getTRs().getItemAt(0).getTDs().getItemCount());			
         //		System.out.println(parser.getAllResources().getInfos().getItemAt(0).getContent());			

         }*/
    }
}
