package cds.astrores.sax;

//Copyright 2002-2014 - UDS/CNRS
//The SAVOT library is distributed under the terms
//of the GNU General Public License version 3.
//
//This file is part of SAVOT.
//
// SAVOT is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, version 3 of the License.
//
// SAVOT is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// The GNU General Public License is available in COPYING file
// along with SAVOT.
//
//SAVOT - Simple Access to VOTable - Parser
//
//Author, Co-Author:  Andre Schaaff (CDS), Laurent Bourges (JMMC)

import java.io.IOException;

/**
 * <p>
 * Astrores SAX Parser Tester, a sample designed to show the SAX usage of the
 * parser
 * </p>
 * @author Andre Schaaff 
 */
public class TestSAX {

    static long freeMemory = 0;
    static long freeMemory2 = 0;

    public static boolean statistics = true;

    /**
     * Constructor
     * 
     * @param file
     */
    public TestSAX(String file) {
	try {
	    System.err.println("Total memory           : "
		    + Runtime.getRuntime().totalMemory());
	    freeMemory = Runtime.getRuntime().freeMemory();
	    System.err.println("Free memory (Begin)    : " + freeMemory);

	    AstroresSAXSample consumer = new AstroresSAXSample();
	    @SuppressWarnings("unused")
	    AstroresSAXParser sb = new AstroresSAXParser(consumer, file);

	} catch (Exception e) {
	    System.err.println("TestSAX : " + e);
	}
	;
    }

    /**
     * Main method
     * 
     * @param argv
     * @throws IOException
     */
    public static void main(String[] argv) throws IOException {

	if (argv.length == 0)
	    System.err.println("Usage: java TestSAX <source>");
	else {
	    System.err.println("Total memory           : "
		    + Runtime.getRuntime().totalMemory());
	    double freeMemory = Runtime.getRuntime().freeMemory();
	    System.err.println("Free memory (Begin)    : " + freeMemory);
	    new TestSAX(argv[0]);
	}
    }
}