package cds.savot.writer;

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

import java.io.*;

import cds.savot.model.*;

/**
 * <p>
 * VOTable document generation from memory
 * </p>
 * 
 * @author Andre Schaaff
 * 
 *          6 June 2005 : the user can now write a VOTable document flow step by
 *          step, the previous method is available too (writing of a whole
 *          document) (kickoff 31 May 02)
 */
public class SavotWriterMy {

    // default xml top
    private String top1 = "<?xml version=" + '"' + "1.0" + '"' + " encoding="
	    + '"' + "UTF-8" + '"' + "?>";

    @SuppressWarnings("unused")
    private String top2 = "\n<VOTABLE xmlns:xsi=" + '"'
	    + "http://www.w3.org/2001/XMLSchema-instance" + '"' + "\n"
	    + "xsi:noNamespaceSchemaLocation=" + '"'
	    + " xmlns:http://www.ivoa.net/xml/VOTable-1.2.xsd" + '"'
	    + " version="; // + "1.2" + '"' +">";

    private String styleSheet = "";
    private boolean attributeEntities = true;
    private boolean elementEntities = true;

    private OutputStream outStream = null;
    private BufferedWriter dataBuffWriter = null;

    private static final String tdbegin1 = "<TD";
    private static final String tdbegin2 = ">";
    private static final String tdend = "</TD>";
    private static final String trbegin = "<TR>";
    private static final String trend = "</TR>\n";
    private static final String tabledatabegin = "\n<TABLEDATA>\n";
    private static final String tabledataend = "</TABLEDATA>";
    private static final String databegin = "\n<DATA>";
    private static final String dataend = "\n</DATA>";
    private static final String tableend = "\n</TABLE>";
    private static final String resourceend = "\n</RESOURCE>";
    private static final String descriptionbegin = "\n<DESCRIPTION>";
    private static final String descriptionend = "</DESCRIPTION>";
    private static final String groupend = "\n</GROUP>";
    private static final String definitionsbegin = "\n<DEFINITIONS>";
    private static final String definitionsend = "\n</DEFINITIONS>";
    private static final String paramend = "\n</PARAM>";
    private static final String fieldend = "\n</FIELD>";
    private static final String linkend = "</LINK>";
    private static final String valuesend = "\n</VALUES>";
    private static final String fitsend = "\n</FITS>";
    private static final String binarybegin = "\n<BINARY>";
    private static final String binaryend = "\n</BINARY>";
    private static final String coosysend = "</COOSYS>";
    private static final String streamend = "\n</STREAM>";
    private static final String minend = "</MIN>";
    private static final String maxend = "</MAX>";
    private static final String optionend = "\n</OPTION>";

    /**
     * Change the default XML document head Default value <?xml
     * version="1.0 encoding="UTF-8"?>
     * 
     * @param top1
     * @since VOTable 1.2
     * 
     */
    public void setTop1(String top1) {
	this.top1 = top1;
    }

    /**
     * Change the default VOTable document head \n<VOTABLE
     * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     * xsi:noNamespaceSchemaLocation
     * ="xmlns:http://www.ivoa.net/xml/VOTable-1.2.xsd version="1.2">
     * 
     * @param top2
     * @since VOTable 1.2
     * 
     */
    public void setTop2(String top2) {
	this.top2 = top2;
    }

    /**
     * Set a stylesheet Also possible with setTop1
     * 
     * @param href
     * @since VOTable 1.2
     * 
     */
    public void setStyleSheet(String href) {
	this.styleSheet = href;
    }

    /**
     * Enable or disable Attribute entities mapping
     * 
     * @param entities
     *            true if Attribute entities are taken into account
     */
    public void enableAttributeEntities(boolean entities) {
	this.attributeEntities = entities;
    }

    /**
     * Enable or disable Element entities mapping
     * 
     * @param entities
     *            true if Element entities are taken into account
     */
    public void enableElementEntities(boolean entities) {
	this.elementEntities = entities;
    }

    /**
     * Enable or disable Attribute and Element entities mapping
     * 
     * @param entities
     *            true if all entities are taken into account
     */
    public void enableEntities(boolean entities) {
	this.attributeEntities = entities;
	this.elementEntities = entities;
    }

    /**
     * internal method
     * 
     * @param src
     * @param oldPattern
     * @param newPattern
     * @return String
     */
    private String replace(String src, String oldPattern, String newPattern) {

	StringBuilder dst = new StringBuilder(); // the new built up string based on src
	int i; // index of found token
	int last = 0; // last valid non token string data for concat
	boolean done = false; // determines if we're done.

	// while we'er not done, try finding and replacing
	while (!done) {
	    // search for the pattern...
	    i = src.indexOf(oldPattern, last);
	    // if it's not found from our last point in the src string....
	    if (i == -1) {
		// we're done.
		done = true;
		// if our last point, happens to be before the end of the string
		if (last < src.length())
		    // concat the rest of the string to our dst string
		    dst.append(src.substring(last, (src.length())));
	    } else {
		// we found the pattern
		if (i != last)
		    // if the pattern's not at the very first char of our
		    // searching point....
		    // we need to concat the text up to that point..
		    dst.append(src.substring(last, i));
		
		// update our last var to our current found pattern, plus the
		// lenght of the pattern
		last = i + oldPattern.length();
		// concat the new pattern to the dst string
		dst.append(newPattern);
	    }
	}
	// finally, return the new string
	return dst.toString();
    }

    /**
     * Encode special characters to entities
     * 
     * @param src
     * @return src
     */
    public String encodeAttribute(String src) {
	if (this.attributeEntities == true) {
	    src = replace(src, "&", "&amp;");
	    src = replace(src, "\"", "&quot;");
	    // src = replace(src, "'", "&apos;");
	    src = replace(src, "<", "&lt;");
	    src = replace(src, ">", "&gt;");
	}
	return src;
    }

    /**
     * Encode special characters to entities
     * 
     * @param src
     * @return src
     * 
     */
    public String encodeElement(String src) {
	if (this.elementEntities == true) {
	    src = replace(src, "&", "&amp;");
	    src = replace(src, "\"", "&quot;");
	    // src = replace(src, "'", "&apos;");
	    src = replace(src, "<", "&lt;");
	    src = replace(src, ">", "&gt;");
	}
	return src;
    }

/**
   * Decode special characters to entities
   * @param src
   * @return
   *
     private static String decode(String src) {
    src = replace(src, "&amp;", "&");
    src = replace(src, "&quot;", "\"");
    src = replace(src, "&apos;", "'");
    src = replace(src, "&lt;", "<");
    src = replace(src, "&gt;", ">");
    return src;
     }
   */

    /**
     * Generates a VOTable XML document corresponding to the internal model The
     * result is sent to the standard output
     * 
     * @param votable
     *            object corresponding to the savot internal model
     * @throws IOException
     */
    public void generateDocument(SavotVOTable votable) throws IOException {
	generateDocument(votable, null, null);
    }

    /**
     * Generates a VOTable XML document corresponding to the internal model
     * 
     * @param votable
     *            object corresponding to the savot internal model
     * @param stream
     *            the result is sent to this stream
     * @throws IOException
     */
    public void generateDocument(SavotVOTable votable, OutputStream stream)
	    throws IOException {
	generateDocument(votable, null, stream);
    }

    /**
     * Generates a VOTable XML document corresponding to the internal model
     * 
     * @param votable
     *            object corresponding to the savot internal model
     * @param file
     *            is sent to this file
     * @throws IOException
     */
    public void generateDocument(SavotVOTable votable, String file)
	    throws IOException {
	generateDocument(votable, file, null);
    }

    /**
     * Generates a VOTable XML document corresponding to the internal model
     * 
     * @param votable
     *            SavotVOTable
     * @param file
     *            String
     * @param stream
     *            OutputStream
     */
    @SuppressWarnings("deprecation")
    public void generateDocument(SavotVOTable votable, String file,
	    OutputStream stream) throws IOException {

	if (file == null) {
	    if (stream == null)
		outStream = System.out;
	    else
		outStream = stream;
	   
	    dataBuffWriter = new BufferedWriter(new OutputStreamWriter(outStream));
	} else {
	    outStream = new FileOutputStream(new File(file));
	    dataBuffWriter = new BufferedWriter(new OutputStreamWriter(outStream));
	}

	dataBuffWriter.write(top1);
	if (votable.getAbove() != "") {
	    String comment = "<!-- " + votable.getAbove() + " -->";
	    dataBuffWriter.write(comment);
	}

	// XSL stylesheet
	if (!styleSheet.equals(""))
	    dataBuffWriter.write(("\n<?xml-stylesheet type=" + '"' + "text/xsl" + '"' + " href=" + '"' + styleSheet + '"' + "?>"));

	StringBuffer buftop2 = new StringBuffer();
	if (!votable.getXmlns().equals("") || !votable.getXmlnsxsi().equals("") || !votable.getXsischema().equals("") || !votable.getXmlns().equals("")) {
	    buftop2.append("\n<VOTABLE");
	    if (!votable.getXmlns().equals(""))
		buftop2.append(" xmlns=").append('"').append(votable.getXmlns()).append('"');

	    if (!votable.getXmlnsxsi().equals(""))
		buftop2.append(" xmlns:xsi=").append('"').append(votable.getXmlnsxsi()).append('"');

	    if (!votable.getXsischema().equals("")) 
		buftop2.append(" xsi:schemaLocation=").append('"').append(votable.getXsischema()).append('"');

	    if (!votable.getXsinoschema().equals(""))
		buftop2.append(" xsi:noNamespaceSchemaLocation=").append('"').append(votable.getXsinoschema()).append('"');
	  
	    buftop2.append(" version=");
	}

	if (!votable.getVersion().equals("")) {
	    if (!votable.getId().equals(""))
		buftop2.append('"').append(votable.getVersion()).append('"').append(" ID=").append('"').append(votable.getId()).append('"').append('>');
	    else
		buftop2.append('"').append(votable.getVersion()).append('"').append('>');
	} else {
	    if (!votable.getId().equals(""))
		buftop2.append('"').append("1.2").append('"').append(" ID=").append('"').append(votable.getId()).append('"').append('>');
	    else
		buftop2.append('"').append("1.2").append('"').append('>');
	}

	dataBuffWriter.write(buftop2.toString());

	if (votable.getBelow() != "") {
	    StringBuffer bufComment = new StringBuffer();

	    bufComment.append("<!-- ").append(votable.getBelow()).append(" -->");
	    dataBuffWriter.write(bufComment.toString());
	}

	if (votable.getDescription() != null) {
	    // DESCRIPTION
	    StringBuffer bufDescription = new StringBuffer();
	    if (votable.getDescription() != null && !votable.getDescription().equals("")) {
		if (votable.getAbove() != "")
		    bufDescription.append("\n<!-- ").append(votable.getAbove()).append(" -->");

		bufDescription.append(descriptionbegin);
		if (votable.getBelow() != "") 
		    bufDescription.append("\n").append("<!-- ").append(votable.getBelow()).append(" -->").append("\n");

		bufDescription.append(encodeElement(votable.getDescription())).append(descriptionend);
		dataBuffWriter.write(bufDescription.toString());
	    }
	}

	// deprecated since VOTable 1.1
	if (votable.getDefinitions() != null) {
	    // DEFINITIONS begin
	    dataBuffWriter.write(definitionsbegin);

	    // COOSYS elements
	    CoosysSet coosysSet = votable.getDefinitions().getCoosys();

	    // write COOSYS elements
	    writeCoosys(coosysSet);

	    // PARAM elements
	    ParamSet paramSet = votable.getDefinitions().getParams();

	    // write PARAM elements
	    writeParam(paramSet);

	    // DEFINITIONS end
	    dataBuffWriter.write(definitionsend);
	}

	// COOSYS - deprecated since VOTable 1.2
	CoosysSet coosyset = votable.getCoosys();

	// write COOSYS elements - deprecated since VOTable 1.2
	writeCoosys(coosyset);

	// INFO
	InfoSet infoset = votable.getInfos();

	// write INFO elements
	writeInfo(infoset);

	// PARAM
	ParamSet paramset = votable.getParams();

	// write PARAM elements
	writeParam(paramset);

	// GROUP elements - since VOTable 1.2
	GroupSet groupSet = votable.getGroups();

	// write GROUP elements - since VOTable 1.2
	writeGroup(groupSet);

	// RESOURCE
	ResourceSet resourceset = votable.getResources();
	writeResource(resourceset);

	// INFO - since VOTable 1.2
	InfoSet infosAtEnd = votable.getInfosAtEnd();

	// write INFO elements - since VOTable 1.2
	writeInfo(infosAtEnd);

	dataBuffWriter.write("\n</VOTABLE>\n");
	dataBuffWriter.flush();
    }

    /**
     * Write a description
     * 
     * @param description
     * 
     */
    public void writeDescription(String description) throws IOException {
	// DESCRIPTION
	dataBuffWriter.write(descriptionbegin + encodeElement(description) + descriptionend);
	dataBuffWriter.flush();
    }

    /**
     * Init the Stream for the output
     * 
     * @param file
     *            String
     */
    public void initStream(String file) throws IOException {
	outStream = new FileOutputStream(new File(file));
	dataBuffWriter = new BufferedWriter(new OutputStreamWriter(outStream));
    }

    /**
     * Init the Stream for the output
     * 
     * @param stream
     *            OutputStream
     */
    public void initStream(OutputStream stream) throws IOException {
	if (stream == null)
	    outStream = System.out;
	else
	    outStream = stream;
	dataBuffWriter = new BufferedWriter(new OutputStreamWriter(outStream));
    }

    /**
     * Write a comment
     * 
     * @param comment
     *            String
     */
    public void writeComment(String comment) throws IOException {
	dataBuffWriter.write("<!--" + comment + "-->");
    }

    /**
     * Write a VOTable XML head
     * 
     * @param votable
     */
    @SuppressWarnings("deprecation")
    public void writeDocumentHead(SavotVOTable votable) throws IOException {
	dataBuffWriter.write(top1);

	StringBuffer bufTop2 = new StringBuffer();
	// XSL stylesheet
	if (!styleSheet.equals(""))
	    dataBuffWriter.write(("\n<?xml-stylesheet type=" + '"' + "text/xsl" + '"' + " href=" + '"' + styleSheet + '"' + "?>"));

	if (!votable.getXmlns().equals("") || !votable.getXmlnsxsi().equals("") || !votable.getXsischema().equals("") || !votable.getXmlns().equals("")) {

	    bufTop2.append("\n<VOTABLE");

	    if (!votable.getXmlns().equals(""))
		bufTop2.append(" xmlns=").append('"').append(votable.getXmlns()).append('"');

	    if (!votable.getXmlnsxsi().equals("")) 
		bufTop2.append(" xmlns:xsi=").append('"').append(votable.getXmlnsxsi()).append('"');

	    if (!votable.getXsischema().equals("")) 
		bufTop2.append(" xsi:schemaLocation=").append('"').append(votable.getXsischema()).append('"');

	    if (!votable.getXsinoschema().equals(""))
		bufTop2.append(" xsi:noNamespaceSchemaLocation=").append('"').append(votable.getXsinoschema()).append('"');
	   
	    bufTop2.append(" version=");
	}

	if (!votable.getVersion().equals("")) {
	    if (!votable.getId().equals("")) 
		bufTop2.append('"').append(votable.getVersion()).append('"').append(" ID=").append('"').append(votable.getId()).append('"').append('>');
	    else 
		bufTop2.append('"').append(votable.getVersion()).append('"').append('>');
	} else {
	    if (!votable.getId().equals(""))
		bufTop2.append('"').append("1.2").append('"').append(" ID=").append('"').append(votable.getId()).append('"').append('>');
	     else 
		bufTop2.append('"').append("1.2").append('"').append('>');
	}
	dataBuffWriter.write(bufTop2.toString());

	// deprecated since VOTable 1.1
	if (votable.getDefinitions() != null) {
	    // DEFINITIONS begin
	    dataBuffWriter.write(definitionsbegin);

	    // COOSYS elements
	    CoosysSet coosysSet = votable.getDefinitions().getCoosys();

	    // write COOSYS elements
	    writeCoosys(coosysSet);

	    // PARAM elements
	    ParamSet paramSet = votable.getDefinitions().getParams();

	    // write PARAM elements
	    writeParam(paramSet);

	    // DEFINITIONS end
	    dataBuffWriter.write(definitionsend);
	}
	dataBuffWriter.flush();
    }

    /**
     * Write a VOTable XML end
     * 
     */
    public void writeDocumentEnd() throws IOException {

	dataBuffWriter.write("\n</VOTABLE>\n");
	dataBuffWriter.flush();
    }

    /**
     * Write a COOSYS set
     * 
     * @param coosysSet
     */
    @SuppressWarnings("deprecation")
    public void writeCoosys(CoosysSet coosysSet) throws IOException {
	StringBuffer coosysline = new StringBuffer();

	for (int i = 0; i < coosysSet.getItemCount(); i++) {
	    coosysline.setLength(0);

	    SavotCoosys coosys = (SavotCoosys) coosysSet.getItemAt(i);

	    if (coosys.getAbove().equals("") == false)
		coosysline.append("\n<!-- ").append(coosys.getAbove()).append(" -->");

	    coosysline.append("\n<COOSYS");

	    if (coosys.getId() != null && !coosys.getId().equals("")) 
		coosysline.append(" ID=").append('"').append(coosys.getId()).append('"');

	    if (coosys.getEquinox() != null && !coosys.getEquinox().equals(""))
		coosysline.append(" equinox=").append('"').append(coosys.getEquinox()).append('"');

	    if (coosys.getEpoch() != null && !coosys.getEpoch().equals("")) 
		coosysline.append(" epoch=").append('"').append(coosys.getEpoch()).append('"');

	    if (coosys.getSystem() != null && !coosys.getSystem().equals(""))
		coosysline.append(" system=").append('"').append(coosys.getSystem()).append('"');

	    if (coosys.getContent() != null && !coosys.getContent().equals("")) {
		coosysline.append(">");
		if (coosys.getBelow() != "")
		    coosysline.append("\n<!-- ").append(coosys.getBelow()).append(" -->\n");

		coosysline.append(coosys.getContent());
		dataBuffWriter.write(coosysline.toString());
		dataBuffWriter.write(coosysend);
	    } else { /* no content */
		coosysline.append("/>");
		if (coosys.getBelow() != "")
		    coosysline.append("\n<!-- ").append(coosys.getBelow()).append(" -->\n");

		dataBuffWriter.write(coosysline.toString());
	    }
	}
    }

    /**
     * Write a PARAM set
     * 
     * @param params
     */
    public void writeParam(ParamSet params) throws IOException {
	StringBuffer paramline = new StringBuffer();
	
	if (params == null)
	    return;
	
	for (int i = 0; i < params.getItemCount(); i++) {
	    paramline.setLength(0);
	    SavotParam param = (SavotParam) params.getItemAt(i);

	    if (param.getAbove().equals("") == false)
		paramline.append("\n<!-- ").append(param.getAbove()).append(" -->");

	    paramline.append("\n<PARAM");

	    if (param.getId() != null && !param.getId().equals(""))
		paramline.append(" ID=").append('"').append(encodeAttribute(param.getId())).append('"');

	    if (param.getUnit() != null && !param.getUnit().equals(""))
		paramline.append(" unit=").append('"').append(encodeAttribute(param.getUnit())).append('"');

	    if (param.getDataType() != null && !param.getDataType().equals(""))
		paramline.append(" datatype=").append('"').append(encodeAttribute(param.getDataType())).append('"');

	    if (param.getPrecision() != null && !param.getPrecision().equals("")) 
		paramline.append(" precision=").append('"').append(encodeAttribute(param.getPrecision())).append('"');

	    if (param.getWidth() != null && !param.getWidth().equals(""))
		paramline.append(" width=").append('"').append(encodeAttribute(param.getWidth())).append('"');

	    // since VOTable version 1.2
	    if (param.getXtype() != null && !param.getXtype().equals(""))
		paramline.append(" width=").append('"').append(encodeAttribute(param.getXtype())).append('"');

	    if (param.getRef() != null && !param.getRef().equals("")) 
		paramline.append(" ref=").append('"').append(encodeAttribute(param.getRef())).append('"');

	    if (param.getName() != null && !param.getName().equals(""))
		paramline.append(" name=").append('"').append(encodeAttribute(param.getName())).append('"');

	    if (param.getUcd() != null && !param.getUcd().equals(""))
		paramline.append(" ucd=").append('"').append(encodeAttribute(param.getUcd())).append('"');

	    if (param.getUtype() != null && !param.getUtype().equals(""))
		paramline.append(" utype=").append('"').append(encodeAttribute(param.getUtype())).append('"');

	    if (param.getValue() != null && !param.getValue().equals(""))
		paramline.append(" value=").append('"').append(encodeAttribute(param.getValue())).append('"');

	    if (param.getArraySize() != null && !param.getArraySize().equals(""))
		paramline.append(" arraysize=").append('"').append(encodeAttribute(param.getArraySize())).append('"');

	    if (param.getValues() != null || param.getLinks().getItemCount() != 0 || (param.getDescription() != null && !param.getDescription().equals(""))) {
		paramline.append(">");

		if (param.getBelow().equals("") == false)
		    paramline.append("\n<!-- ").append(param.getBelow()).append(" -->\n");

		// write DESCRIPTION element
		if (param.getDescription() != null && !param.getDescription().equals(""))
		    paramline.append(descriptionbegin).append(param.getDescription()).append(descriptionend);

		dataBuffWriter.write(paramline.toString());

		// write VALUES element
		writeValues(param.getValues());

		// write LINK elements
		writeLink(param.getLinks());

		// write PARAM end
		dataBuffWriter.write(paramend);
	    } else {
		paramline.append("/>");
		if (param.getBelow().equals("") == false)
		    paramline.append("\n<!-- ").append(param.getBelow()).append(" -->\n");
		dataBuffWriter.write(paramline.toString());
	    }
	}
    }

    /**
     * Write a PARAMref set
     * 
     * @param refparams
     */
    public void writeParamRef(ParamRefSet refparams) throws IOException {
	StringBuffer paramline = new StringBuffer();
	  
	if (refparams == null)
	    return;
	     
	for (int i = 0; i < refparams.getItemCount(); i++) {
	    paramline.setLength(0);
	    
	    SavotParamRef paramref = (SavotParamRef) refparams.getItemAt(i);

	    if (paramref.getAbove().equals("") == false)
		paramline.append("\n<!-- ").append(paramref.getAbove()).append(" -->");

	    paramline.append("\n<PARAMref");

	    if (paramref.getRef() != null && !paramref.getRef().equals(""))
		paramline.append(" ref=").append('"').append(encodeAttribute(paramref.getRef())).append('"');
	    
	    if (paramref.getUcd() != null && !paramref.getUcd().equals("")) 
		paramline.append(" ucd=").append('"').append(encodeAttribute(paramref.getUcd())).append('"');
	    
	    if (paramref.getUtype() != null && !paramref.getUtype().equals("")) 
		paramline.append(" utype=").append('"').append(encodeAttribute(paramref.getUtype())).append('"');
	    
	    paramline.append("/>");

	    if (paramref.getBelow().equals("") == false)
		paramline.append("\n<!-- ").append(paramref.getBelow()).append(" -->\n");

	    dataBuffWriter.write(paramline.toString());
	}
    }

    /**
     * Write a LINK set
     * 
     * @param linkSet
     */
    public void writeLink(LinkSet linkSet) throws IOException {
	StringBuffer linkline = new StringBuffer();
	
	for (int k = 0; k < linkSet.getItemCount(); k++) {

	    	linkline.setLength(0);
		
		SavotLink link = (SavotLink) linkSet.getItemAt(k);

		if (((SavotLink) linkSet.getItemAt(k)).getAbove() != "")
		    linkline.append("\n<!-- ").append(((SavotLink) linkSet.getItemAt(k)).getAbove()).append(" -->");

		linkline.append("\n<LINK");

		if (link.getId() != null && !link.getId().equals(""))
		    linkline.append(" ID=").append('"').append(encodeAttribute(link.getId())).append('"');

		if (link.getContentRole() != null && !link.getContentRole().equals("")) 
		    linkline.append(" content-role=").append('"').append(encodeAttribute(link.getContentRole())).append('"');

		if (link.getContentType() != null && !link.getContentType().equals(""))
		    linkline.append(" content-type=").append('"').append(encodeAttribute(link.getContentType())).append('"');

		if (link.getTitle() != null && !link.getTitle().equals("")) 
		    linkline.append(" title=").append('"').append(encodeAttribute(link.getTitle())).append('"');

		if (link.getValue() != null && !link.getValue().equals(""))
		    linkline.append(" value=").append('"').append(encodeAttribute(link.getValue())).append('"');

		if (link.getHref() != null && !link.getHref().equals(""))
		    linkline.append(" href=").append('"').append(encodeAttribute(link.getHref()) + '"');

		if (link.getGref() != null && !link.getGref().equals(""))
		    linkline.append(" gref=").append('"').append(encodeAttribute(link.getGref()) + '"');

		if (link.getAction() != null && !link.getAction().equals(""))
		    linkline.append(" action=").append('"').append(encodeAttribute(link.getAction()) + '"');

		if (link.getContent() != null && !link.getContent().equals("")) {
		    linkline.append(">");
		    if (link.getBelow() != "")
			linkline.append("\n<!-- ").append(link.getBelow() +" -->\n");
		    linkline.append(link.getContent());
		    dataBuffWriter.write(linkline.toString());
		    dataBuffWriter.write(linkend);
		} else { /* no content */
		    linkline.append("/>");
		    if (link.getBelow() != "")
			linkline.append("\n<!-- " + link.getBelow() + " -->\n");
		    dataBuffWriter.write(linkline.toString());
		}
	    }
    }

    /**
     * Write an INFO set
     * 
     * @param infoSet
     */
    public void writeInfo(InfoSet infoSet) {
	StringBuffer info = new StringBuffer();
	
	try {
	    if (infoSet != null) {
		int infocount = infoSet.getItemCount();
		info.setLength(0);

		for (int j = 0; j < infocount; j++) {

		    // INFO
		    info.setLength(0);

		    if (((SavotInfo) infoSet.getItemAt(j)).getAbove() != "")
			info.append("\n<!-- ").append(((SavotInfo) infoSet.getItemAt(j)).getAbove()).append(" -->");

		    info.append("\n<INFO");

		    if (((SavotInfo) infoSet.getItemAt(j)).getId().equals("") == false)
			info.append(" ID=").append('"').append(encodeAttribute(((SavotInfo)infoSet.getItemAt(j)).getId())).append('"');

		    if (((SavotInfo) infoSet.getItemAt(j)).getName().equals("") == false)
			info.append(" name=").append('"').append(encodeAttribute(((SavotInfo)infoSet.getItemAt(j)).getName())).append('"');

		    if (((SavotInfo) infoSet.getItemAt(j)).getValue().equals("") == false)
			info.append(" value=").append('"').append(encodeAttribute(((SavotInfo)infoSet.getItemAt(j)).getValue())).append('"');

		    // since VOTable version 1.2
		    if (((SavotInfo) infoSet.getItemAt(j)).getXtype().equals("") == false)
			info.append(" xtype=").append('"').append(encodeAttribute(((SavotInfo)infoSet.getItemAt(j)).getXtype())).append('"');

		    // since VOTable version 1.2
		    if (((SavotInfo) infoSet.getItemAt(j)).getRef().equals("") == false)
			info.append(" ref=").append('"').append(encodeAttribute(((SavotInfo)infoSet.getItemAt(j)).getRef())).append('"');

		    // since VOTable version 1.2
		    if (((SavotInfo) infoSet.getItemAt(j)).getUnit().equals("") == false) 
			info.append(" unit=").append('"').append(encodeAttribute(((SavotInfo)infoSet.getItemAt(j)).getUnit())).append('"');

		    // since VOTable version 1.2
		    if (((SavotInfo) infoSet.getItemAt(j)).getUcd().equals("") == false)
			info.append(" ucd=").append('"').append(encodeAttribute(((SavotInfo)infoSet.getItemAt(j)).getUcd())).append('"');

		    // since VOTable version 1.2
		    if (((SavotInfo) infoSet.getItemAt(j)).getUtype().equals("") == false)
			info.append(" utype=").append('"').append(encodeAttribute(((SavotInfo) infoSet.getItemAt(j)).getUtype())).append('"');

		    if (((SavotInfo) infoSet.getItemAt(j)).getBelow() != "") 
			info.append("\n<!-- ").append(((SavotInfo) infoSet.getItemAt(j)).getBelow()).append(" -->\n");

		    // only for VOTable version before 1.2
		    if (((SavotInfo) infoSet.getItemAt(j)).getContent() != null
			    && !((SavotInfo) infoSet.getItemAt(j)).getContent().equals(""))
			info.append(">").append(((SavotInfo) infoSet.getItemAt(j)).getContent()).append("</INFO>");
		    else { // from VOTable 1.2
			if (((SavotInfo) infoSet.getItemAt(j)).getDescription() != null && !((SavotInfo) infoSet.getItemAt(j)).getDescription().equals("") || ((SavotInfo) infoSet.getItemAt(j)).getValues() != null || ((SavotInfo) infoSet.getItemAt(j)).getLinks().getItemCount() != 0) {

			    info.append(">");

			    if (((SavotInfo) infoSet.getItemAt(j)).getBelow() != "")
				info.append("\n<!-- ").append(((SavotInfo) infoSet.getItemAt(j)).getBelow()).append(" -->\n");

			    if (((SavotInfo) infoSet.getItemAt(j)).getDescription() != null && !((SavotInfo) infoSet.getItemAt(j)).getDescription().equals("")) {
				if (((SavotInfo) infoSet.getItemAt(j)).getAbove() != "")
				    info.append("\n<!-- ").append(((SavotInfo) infoSet.getItemAt(j)).getAbove()).append(" -->");
				
				info.append("\n<DESCRIPTION>");
				if (((SavotInfo) infoSet.getItemAt(j)).getBelow() != "")
				    info.append("\n").append("<!-- ").append(((SavotInfo) infoSet.getItemAt(j)).getBelow()).append(" -->").append("\n");
				
				info.append(encodeElement(((SavotInfo)infoSet.getItemAt(j)).getDescription())).append("</DESCRIPTION>");
			    }

			    // VALUES element
			    if (((SavotInfo) infoSet.getItemAt(j)).getValues() != null) {
				// write VALUES element
				writeValues(((SavotInfo) infoSet.getItemAt(j)).getValues());
			    }

			    // LINK elements
			    if (((SavotInfo) infoSet.getItemAt(j)).getLinks().getItemCount() != 0) {
				LinkSet links = (LinkSet) ((SavotInfo) infoSet.getItemAt(j)).getLinks();
				// write LINK elements
				writeLink(links);
			    }
			    info.append("</INFO>");
			} else {
			    info.append("/>");
			}
		    }
		    dataBuffWriter.write(info.toString());
		}
	    }
	} catch (Exception e) {
	    System.err.println("writeInfo : " + e);
	}
    }

    /**
     * Write a FIELD set
     * 
     * @param fieldSet
     */
    public void writeField(FieldSet fieldSet) {
	StringBuffer fieldline = new StringBuffer();
	    
	try {

	    for (int m = 0; m < fieldSet.getItemCount(); m++) {
		SavotField field = (SavotField) fieldSet.getItemAt(m);
		fieldline.setLength(0);
		
		if (field.getAbove().equals("") == false)
		    fieldline.append("\n<!-- ").append(field.getAbove()).append(" -->");

		fieldline.append("\n<FIELD");
		
		if (field.getId() != null && field.getId().equals("") == false)
		    fieldline.append(" ID=").append('"').append(encodeAttribute(field.getId())).append('"');

		if (field.getName() != null && field.getName().equals("") == false)
		    fieldline.append(" name=").append('"').append(encodeAttribute(field.getName())).append('"');

		if (field.getDataType() != null && field.getDataType().equals("") == false)
		    fieldline.append(" datatype=").append('"').append(encodeAttribute(field.getDataType())).append('"');

		if (field.getPrecision() != null && field.getPrecision().equals("") == false)
		    fieldline.append(" precision=").append('"').append(encodeAttribute(field.getPrecision())).append('"');

		if (field.getWidth() != null && field.getWidth().equals("") == false)
		    fieldline.append(" width=").append('"').append(encodeAttribute(field.getWidth())).append('"');

		// since VOTable version 1.2
		if (field.getXtype() != null && field.getXtype().equals("") == false)
		    fieldline.append(" width=").append('"').append(encodeAttribute(field.getXtype())).append('"');

		if (field.getRef() != null && field.getRef().equals("") == false)
		    fieldline.append(" ref=").append('"').append(encodeAttribute(field.getRef())).append('"');

		if (field.getUcd() != null && field.getUcd().equals("") == false)
		    fieldline.append(" ucd=").append('"').append(encodeAttribute(field.getUcd())).append('"');

		if (field.getUtype() != null && field.getUtype().equals("") == false)
		    fieldline.append(" utype=").append('"').append(encodeAttribute(field.getUtype())).append('"');

		if (field.getArraySize() != null && field.getArraySize().equals("") == false)
		    fieldline.append(" arraysize=").append('"').append(encodeAttribute(field.getArraySize())).append('"');

		if (field.getType() != null && field.getType().equals("") == false)
		    fieldline.append(" type=").append('"').append(encodeAttribute(field.getType())).append('"');

		if (field.getUnit() != null && field.getUnit().equals("") == false)
		    fieldline.append(" unit=").append('"').append(encodeAttribute(field.getUnit())).append('"');

		if ((field.getDescription() != null && !field.getDescription().equals("")) || field.getValues() != null || field.getLinks().getItemCount() != 0) {
		    fieldline.append(">");
		    if (field.getBelow().equals("") == false)
			fieldline.append("\n<!-- ").append(field.getBelow()).append(" -->\n");

		    if (field.getDescription() != null && field.getDescription().equals("") == false) {
			if (field.getAbove().equals("") == false) {
			    fieldline.delete(0, fieldline.length()-1);
			    fieldline.append("\n<!-- ").append(field.getAbove()).append(" -->");
			}
			fieldline.append("\n<DESCRIPTION>");
			if (field.getBelow().equals("") == false)
			    fieldline.append("\n").append("<!-- ").append(field.getBelow()).append(" -->").append("\n");

			fieldline.append(encodeElement(field.getDescription())).append("</DESCRIPTION>");
		    }
		    dataBuffWriter.write(fieldline.toString());

		    // VALUES element
		    if (field.getValues() != null)
			// write VALUES element
			writeValues(field.getValues());

		    // LINK elements
		    if (field.getLinks().getItemCount() != 0) {
			LinkSet links = (LinkSet) field.getLinks();
			// write LINK elements
			writeLink(links);
		    }
		    dataBuffWriter.write(fieldend);
		} else {
		    fieldline.append("/>");
		    dataBuffWriter.write(fieldline.toString());
		}
	    }
	} catch (Exception e) {
	    System.err.println("writeField : " + e);
	}
    }

    /**
     * Write a FIELD set
     * 
     * @param fieldRefSet
     */
    public void writeFieldRef(FieldRefSet fieldRefSet) {
	StringBuffer fieldline = new StringBuffer();

	try {
	    fieldline.setLength(0);
	    
	    for (int m = 0; m < fieldRefSet.getItemCount(); m++) {
		SavotFieldRef fieldref = (SavotFieldRef) fieldRefSet.getItemAt(m);

		if (fieldref.getAbove().equals("") == false)
		    fieldline.append("\n<!-- ").append(fieldref.getAbove()).append(" -->");

		fieldline.append("\n<FIELDref");

		if (fieldref.getRef() != null && fieldref.getRef().equals("") == false) 
		    fieldline.append(" ref=").append('"').append(encodeAttribute(fieldref.getRef())).append('"');

		if (fieldref.getUcd() != null && fieldref.getUcd().equals("") == false) 
		    fieldline.append(" ucd=").append('"').append(encodeAttribute(fieldref.getUcd())).append('"');

		if (fieldref.getUtype() != null && fieldref.getUtype().equals("") == false)
		    fieldline.append(" utype=").append('"').append(encodeAttribute(fieldref.getUtype())).append('"');

		fieldline.append("/>");
		dataBuffWriter.write(fieldline.toString());
	    }
	} catch (Exception e) {
	    System.err.println("writeFieldref : " + e);
	}
    }

    /**
     * Write a STREAM element
     * 
     * @param stream
     */
    public void writeStream(SavotStream stream) {
	StringBuffer streamline = new StringBuffer();
	
	try {
	    streamline.setLength(0);
	    
	    if (stream.getAbove().equals("") == false)
		streamline.append("\n<!-- ").append(stream.getAbove()).append(" -->");

	    streamline.append("\n<STREAM");

	    if (stream.getType() != null && stream.getType().equals("") == false)
		streamline.append(" type=").append('"').append(encodeAttribute(stream.getType())).append('"');

	    if (stream.getHref() != null && stream.getHref().equals("") == false)
		streamline.append(" href=").append('"').append(encodeAttribute(stream.getHref())).append('"');

	    if (stream.getActuate() != null && stream.getActuate().equals("") == false)
		streamline.append(" actuate=").append('"').append(encodeAttribute(stream.getActuate())).append('"');

	    if (stream.getEncoding() != null && stream.getEncoding().equals("") == false)
		streamline.append(" encoding=").append('"').append(encodeAttribute(stream.getEncoding())).append('"');

	    if (stream.getExpires() != null && stream.getExpires().equals("") == false)
		streamline.append(" expires=").append('"').append(encodeAttribute(stream.getExpires())).append('"');

	    if (stream.getRights() != null && stream.getRights().equals("") == false)
		streamline.append(" rights=").append('"').append(encodeAttribute(stream.getRights())).append('"');

	    streamline.append(">");

	    if (stream.getBelow().equals("") == false)
		streamline.append("\n<!-- ").append(stream.getBelow()).append(" -->\n");

	    dataBuffWriter.write(streamline.toString());
	    if (stream.getContent() != null && stream.getContent().equals("") == false) {
		dataBuffWriter.write(stream.getContent());
	    }

	    dataBuffWriter.write(streamend);
	} catch (Exception e) {
	    System.err.println("writeStream : " + e);
	}
    }

    /**
     * Write a BINARY element
     * 
     * @param binary
     */
    public void writeBinary(SavotBinary binary) {
	StringBuffer binaryline = new StringBuffer();
	
	try {
	     binaryline.setLength(0);

	    if (binary.getStream() != null) {
		if (binary.getAbove().equals("") == false) {
		    binaryline.append("\n<!-- ").append(binary.getAbove()).append(" -->");
		    dataBuffWriter.write(binaryline.toString());
		}

		dataBuffWriter.write(binarybegin);
		if (binary.getBelow().equals("") == false) {
		    binaryline.delete(0, binaryline.length()-1);
		    binaryline.append("\n<!-- ").append(binary.getBelow()).append(" -->");
		    dataBuffWriter.write(binaryline.toString());
		}
		writeStream(binary.getStream());
		dataBuffWriter.write(binaryend);
	    }
	} catch (Exception e) {
	    System.err.println("writeBinary : " + e);
	}
    }

    /**
     * Write a VALUES element
     * 
     * @param values
     */
    public void writeValues(SavotValues values) {
	StringBuffer valuesline = new StringBuffer();
	
	try {
	    if (values == null)
		return;
	    valuesline.setLength(0);
	    if (values.getAbove().equals("") == false)
		valuesline.append("\n<!-- ").append(values.getAbove()).append(" -->");

	    valuesline.append("\n<VALUES");

	    if (values.getId() != null && values.getId().equals("") == false)
		valuesline.append(" ID=").append('"').append(encodeAttribute(values.getId())).append('"');

	    if (values.getType() != null && values.getType().equals("") == false)
		valuesline.append(" type=").append('"').append(encodeAttribute(values.getType())).append('"');

	    if (values.getNull() != null && values.getNull().equals("") == false)
		valuesline.append(" null=").append('"').append(encodeAttribute(values.getNull())).append('"');

	    if (values.getRef() != null && values.getRef().equals("") == false)
		valuesline.append(" ref=").append('"').append(encodeAttribute(values.getRef())).append('"');
	 
	    if (values.getInvalid() != null && values.getInvalid().equals("") == false)
		valuesline.append(" invalid=").append('"').append(encodeAttribute(values.getInvalid())).append('"');

	    valuesline.append(">");

	    if (values.getBelow().equals("") == false) 
		valuesline.append("\n<!-- ").append(values.getBelow()).append(" -->\n");

	    dataBuffWriter.write(valuesline.toString());
	    valuesline.delete(0, valuesline.length()-1);

	    // MIN element
	    if (values.getMin() != null) {
		SavotMin min = values.getMin();
		writeMin(min);
	    }

	    // MAX element
	    if (values.getMax() != null) {
		SavotMax max = values.getMax();
		writeMax(max);
	    }

	    // OPTION elements
	    if (values.getOptions() != null) {
		OptionSet options = (OptionSet) values.getOptions();
		// write OPTION elements
		writeOption(options);
	    }
	    dataBuffWriter.write(valuesend);
	} catch (Exception e) {
	    System.err.println("writeValues : " + e);
	}
    }

    /**
     * Write a FITS element
     * 
     * @param fits
     */
    public void writeFits(SavotFits fits) {
	StringBuffer fitsline = new StringBuffer();

	try {
	    if (fits.getAbove().equals("") == false)
		fitsline.append("\n<!-- ").append(fits.getAbove()).append(" -->");

	    fitsline.append("\n<FITS");

	    if (fits.getExtnum() != null && fits.getExtnum().equals("") == false)
		fitsline.append(" extnum=").append('"').append(encodeAttribute(fits.getExtnum())).append('"');

	    fitsline.append(">");

	    if (fits.getBelow() != "") 
		fitsline.append("\n<!-- ").append(fits.getBelow()).append(" -->\n");

	    dataBuffWriter.write(fitsline.toString());

	    // STREAM element
	    if (fits.getStream() != null) {
		SavotStream stream = (SavotStream) fits.getStream();
		// write STREAM element
		writeStream(stream);
	    }
	    dataBuffWriter.write(fitsend);
	} catch (Exception e) {
	    System.err.println("writeFits : " + e);
	}
    }

    /**
     * Write a MIN element
     * 
     * @param min
     */
    public void writeMin(SavotMin min) {
	StringBuffer minline = new StringBuffer();
	
	try {
	    if (min.getAbove().equals("") == false)
		minline.append("\n<!-- ").append(min.getAbove()).append(" -->");

	    minline.append("\n<MIN");

	    if (min.getValue() != null && min.getValue().equals("") == false) 
		minline.append(" value=").append('"').append(encodeAttribute(min.getValue())).append('"');

	    if (min.getInclusive() != null && min.getInclusive().equals("") == false) 
		minline.append(" inclusive=").append('"').append(encodeAttribute(min.getInclusive())).append('"');

	    if (min.getContent() != null && min.getContent().equals("") == false) {
		minline.append(">");
		if (min.getBelow().equals("") == false)
		    minline.append("\n<!-- ").append(min.getBelow()).append(" -->\n");
	
		minline.append(min.getContent());
		dataBuffWriter.write(minline.toString());
		dataBuffWriter.write(minend);
	    } else { /* no content */
		minline.append("/>");
		if (min.getBelow().equals("") == false) 
		    minline.append("\n<!-- ").append(min.getBelow()).append(" -->\n");
		dataBuffWriter.write(minline.toString());
	    }
	} catch (Exception e) {
	    System.err.println("writeMin : " + e);
	}
    }

    /**
     * Write a MAX element
     * 
     * @param max
     */
    public void writeMax(SavotMax max) {
	StringBuffer maxline = new StringBuffer();
	
	try {
	    if (max.getAbove().equals("") == false)
		maxline.append("\n<!-- ").append(max.getAbove()).append(" -->");

	    maxline.append("\n<MAX");

	    if (max.getValue() != null && max.getValue().equals("") == false)
		maxline.append(" value=").append('"').append(encodeAttribute(max.getValue())).append('"');

	    if (max.getInclusive() != null && max.getInclusive().equals("") == false)
		maxline.append(" inclusive=").append('"').append(encodeAttribute(max.getInclusive())).append('"');
	  
	    if (max.getContent() != null && max.getContent().equals("") == false) {
		maxline.append(">");
		if (max.getBelow().equals("") == false)
		    maxline.append("\n<!-- ").append(max.getBelow()).append(" -->\n");
		maxline.append(max.getContent());
		dataBuffWriter.write(maxline.toString());
		dataBuffWriter.write(maxend);
	    } else { /* no content */
		maxline.append("/>");
		if (max.getBelow().equals("") == false)
		    maxline.append("\n<!-- ").append(max.getBelow()).append(" -->\n");
		dataBuffWriter.write(maxline.toString());
	    }
	} catch (Exception e) {
	    System.err.println("writeMax : " + e);
	}
    }

    /**
     * Write an OPTION set
     * 
     * @param optionSet
     */
    public void writeOption(OptionSet optionSet) {
	StringBuffer optionline = new StringBuffer();

	try {
	    for (int m = 0; m < optionSet.getItemCount(); m++) {
		optionline.setLength(0);
		SavotOption option = (SavotOption) optionSet.getItemAt(m);

		if (option.getAbove().equals("") == false)
		    optionline.append("\n<!-- ").append(option.getAbove()).append(" -->");

		optionline.append("\n<OPTION");

		if (option.getName() != null && option.getName().equals("") == false)
		    optionline.append(" name=").append('"').append(encodeAttribute(option.getName())).append('"');

		if (option.getValue() != null && option.getValue().equals("") == false)
		    optionline.append(" value=").append('"').append(encodeAttribute(option.getValue())).append('"');

		// write recursive options
		if (option.getOptions().getItemCount() != 0) {
		    optionline.append(">");
		    if (option.getBelow().equals("") == false)
			optionline.append("\n<!-- ").append(option.getBelow()).append(" -->\n");
		    OptionSet options = option.getOptions();
		    dataBuffWriter.write(optionline.toString());
		    writeOption(options);
		    dataBuffWriter.write(optionend);
		} else {
		    optionline.append("/>");
		    if (option.getBelow().equals("") == false)
			optionline.append("\n<!-- ").append(option.getBelow()).append(" -->\n");
		    dataBuffWriter.write(optionline.toString());
		}
	    }
	} catch (Exception e) {
	    System.err.println("writeOption : " + e);
	}
    }

    /**
     * Write a GROUP set
     * 
     * @param groupSet
     */
    public void writeGroup(GroupSet groupSet) {
	StringBuffer groupline = new StringBuffer();

	try {
	    for (int m = 0; m < groupSet.getItemCount(); m++) {
		groupline.setLength(0);
		SavotGroup group = (SavotGroup) groupSet.getItemAt(m);

		if (group.getAbove().equals("") == false)
		    groupline.append("\n<!-- ").append(group.getAbove()).append(" -->");

		groupline.append("\n<GROUP");

		if (group.getId() != null && group.getId().equals("") == false) 
		    groupline.append(" ID=").append('"').append(encodeAttribute(group.getId())).append('"');

		if (group.getName() != null && group.getName().equals("") == false) 
		    groupline.append(" name=").append('"').append(encodeAttribute(group.getName())).append('"');

		if (group.getRef() != null && group.getRef().equals("") == false)
		    groupline.append(" ref=").append('"').append(encodeAttribute(group.getRef())).append('"');

		if (group.getUcd() != null && group.getUcd().equals("") == false)
		    groupline.append(" ucd=").append('"').append(encodeAttribute(group.getUcd())).append('"');

		if (group.getUtype() != null && group.getUtype().equals("") == false)
		    groupline.append(" utype=").append('"').append(encodeAttribute(group.getUtype())).append('"');

		groupline.append(">");

		if (group.getBelow().equals("") == false)
		    groupline.append("\n<!-- ").append(group.getBelow()).append(" -->\n");

		// write DESCRIPTION element
		if (group.getDescription() != null && group.getDescription().equals("") == false) {

		    if (group.getAbove().equals("") == false) {
			groupline.delete(0, groupline.length()-1);
			groupline.append("\n<!-- ").append(group.getAbove()).append(" -->");
		    }
		    groupline.append("\n<DESCRIPTION>");
		    if (group.getBelow().equals("") == false)
			groupline.append("\n").append("<!-- ").append(group.getBelow()).append(" -->").append("\n");
		
		    groupline.append(encodeElement(group.getDescription())).append("</DESCRIPTION>");
		}

		dataBuffWriter.write(groupline.toString());

		// write FIELDref elements
		if (group.getFieldsRef().getItemCount() != 0) {
		    FieldRefSet reffields = group.getFieldsRef();
		    writeFieldRef(reffields);
		}

		// write PARAMref elements
		if (group.getParamsRef().getItemCount() != 0) {
		    ParamRefSet refgroups = group.getParamsRef();
		    writeParamRef(refgroups);
		}

		// write PARAM elements
		if (group.getParams().getItemCount() != 0) {
		    ParamSet groups = group.getParams();
		    writeParam(groups);
		}

		// write recursive groups
		if (group.getGroups().getItemCount() != 0) {
		    GroupSet groups = group.getGroups();
		    writeGroup(groups);
		}
		dataBuffWriter.write(groupend);
	    }
	} catch (Exception e) {
	    System.err.println("writeGroup : " + e);
	}
    }

    /**
     * Write a TABLE begin
     * 
     * @param table
     *            SavotTable
     */
    public void writeTableBegin(SavotTable table) {
	StringBuffer tableline = new StringBuffer();
	
	try {
	    // RESOURCE
	    tableline.setLength(0);
	    tableline.append("\n<TABLE");

	    if (table.getId() != null && table.getId().equals("") == false)
		tableline.append(" ID=").append('"').append(encodeAttribute(table.getId())).append('"');

	    if (table.getName() != null && table.getName().equals("") == false)
		tableline.append(" name=").append('"').append(encodeAttribute(table.getName())).append('"');

	    if (table.getRef() != null && table.getRef().equals("") == false)
		tableline.append(" ref=").append('"').append(encodeAttribute(table.getRef())).append('"');

	    if (table.getUcd() != null && table.getUcd().equals("") == false)
		tableline.append(" ucd=").append('"').append(encodeAttribute(table.getUcd())).append('"');

	    if (table.getUtype() != null && table.getUtype().equals("") == false)
		tableline.append(" utype=").append('"').append(encodeAttribute(table.getUtype())).append('"');

	    if (table.getNrows() != null && table.getNrows().equals("") == false) 
		tableline.append(" nrows=").append('"').append(encodeAttribute(table.getNrows())).append('"');

	    tableline.append(">");
	    dataBuffWriter.write(tableline.toString());

	    // Description
	    if (table.getDescription() != null
		    && table.getDescription().equals("") == false) {
		writeDescription(table.getDescription());
	    }
	} catch (Exception e) {
	    System.err.println("writeResourceBegin : " + e);
	    e.printStackTrace();
	}
    }

    /**
     * Write a TABLE end
     */
    public void writeTableEnd() {
	try {
	    // </TABLE>
	    dataBuffWriter.write(tableend);
	} catch (Exception e) {
	    System.err.println("writeTableEnd : " + e);
	    e.printStackTrace();
	}
    }

    /**
     * Write a RESOURCE begin
     * 
     * @param resource
     *            SavotResource
     */
    public void writeResourceBegin(SavotResource resource) {
	StringBuffer line = new StringBuffer();
	
	try {
	    // RESOURCE
	    line.append("\n<RESOURCE");

	    if (resource.getName().equals("") == false) 
		line.append(" name=").append('"').append(encodeAttribute(resource.getName())).append('"');

	    if (resource.getId().equals("") == false)
		line.append(" ID=").append('"').append(encodeAttribute(resource.getId())).append('"');

	    if (resource.getUtype().equals("") == false)
		line.append(" utype=").append('"').append(encodeAttribute(resource.getUtype())).append('"');

	    if (resource.getType().equals("") == false)
		line.append(" type=").append('"').append(encodeAttribute(resource.getType())).append('"');
	   
	    line.append(">");
	    dataBuffWriter.write(line.toString());
	} catch (Exception e) {
	    System.err.println("writeResourceBegin : " + e);
	    e.printStackTrace();
	}
    }

    /**
     * Write a RESOURCE end
     */
    public void writeResourceEnd() {
	try {
	    // </RESOURCE>
	    dataBuffWriter.write(resourceend);
	} catch (Exception e) {
	    System.err.println("writeResourceEnd : " + e);
	    e.printStackTrace();
	}
    }

    /**
     * Write a TABLEDATA begin
     */
    public void writeTableDataBegin() {
	try {
	    // </TABLEDATA>
	    dataBuffWriter.write(tabledatabegin);
	} catch (Exception e) {
	    System.err.println("writeTableDataBegin : " + e);
	    e.printStackTrace();
	}
    }

    /**
     * Write a TABLEDATA end
     */
    public void writeTableDataEnd() {
	try {
	    // </TABLEDATA>
	    dataBuffWriter.write("\n");
	    dataBuffWriter.write(tabledataend);
	} catch (Exception e) {
	    System.err.println("writeTableDataEnd : " + e);
	    e.printStackTrace();
	}
    }

    /**
     * Write a DATA begin
     */
    public void writeDataBegin() {
	try {
	    // </DATA>
	    dataBuffWriter.write(databegin);
	} catch (Exception e) {
	    System.err.println("writeDataBegin : " + e);
	    e.printStackTrace();
	}
    }

    /**
     * Write a DATA end
     */
    public void writeDataEnd() {
	try {
	    // </DATA>
	    dataBuffWriter.write("\n");
	    dataBuffWriter.write(dataend);
	} catch (Exception e) {
	    System.err.println("writeDataEnd : " + e);
	    e.printStackTrace();
	}
    }

    /**
     * Write a TR
     */
    public void writeTR(SavotTR tr) {
	try {
	    TDSet tds = tr.getTDSet();
	    // <TR>
	    dataBuffWriter.write(trbegin);

	    for (int r = 0; r < tds.getItemCount(); r++) {
		// <TD>
		dataBuffWriter.write(tdbegin1);

		dataBuffWriter.write(tdbegin2);

		if (elementEntities)
		    dataBuffWriter.write(encodeElement((String) tds.getContent(r)));
		else 
		    dataBuffWriter.write(tds.getContent(r));
		// </TD>
		dataBuffWriter.write(tdend);
	    }
	    // </TR>
	    dataBuffWriter.write(trend);
	} catch (Exception e) {
	    System.err.println("writeTREnd : " + e);
	    e.printStackTrace();
	}
    }

    /**
     * Write a RESOURCE set
     * 
     * @param resourceset
     *            ResourceSet
     */
    public void writeResource(ResourceSet resourceset) throws IOException {
	if (resourceset != null) {
	    int resourcecount = resourceset.getItemCount();

	StringBuffer resource = new StringBuffer();

	    for (int i = 0; i < resourcecount; i++) {
		// RESOURCE
		resource.setLength(0);

		if (((SavotResource) resourceset.getItemAt(i)).getAbove().equals("") == false) 
		    resource.append("\n<!-- ").append(((SavotResource) resourceset.getItemAt(i)).getAbove()).append(" -->");
	
		resource.append("\n<RESOURCE");

		if (((SavotResource) resourceset.getItemAt(i)).getName().equals("") == false)
		    resource.append(" name=").append('"').append(encodeAttribute(((SavotResource) resourceset.getItemAt(i)).getName())).append('"');

		if (((SavotResource) resourceset.getItemAt(i)).getId().equals("") == false) 
		    resource.append(" ID=").append('"').append(encodeAttribute(((SavotResource) resourceset.getItemAt(i)).getId())).append('"');

		if (((SavotResource) resourceset.getItemAt(i)).getUtype().equals("") == false)
		    resource.append(" utype=").append('"').append(encodeAttribute(((SavotResource) resourceset.getItemAt(i)).getUtype())).append('"');

		if (((SavotResource) resourceset.getItemAt(i)).getType().equals("") == false)
		    resource.append(" type=").append('"').append(encodeAttribute(((SavotResource) resourceset.getItemAt(i)).getType())).append('"');

		resource.append(">");

		if (((SavotResource) resourceset.getItemAt(i)).getBelow().equals("") == false)
		    resource.append("\n<!-- ").append(((SavotResource) resourceset.getItemAt(i)).getBelow()).append(" -->\n");

		dataBuffWriter.write(resource.toString());

		// DESCRIPTION
		if (((SavotResource) resourceset.getItemAt(i)).getDescription().equals("") == false) {
		    StringBuffer description = new StringBuffer();

		    description.append("\n<DESCRIPTION>").append(encodeElement(((SavotResource) resourceset.getItemAt(i)).getDescription())).append("</DESCRIPTION>");
		    dataBuffWriter.write(description.toString());
		}

		// INFO
		InfoSet infoset = ((SavotResource) resourceset.getItemAt(i)).getInfos();

		// write INFO elements
		writeInfo(infoset);

		// COOSYS elements
		CoosysSet coosysSet = ((SavotResource) resourceset.getItemAt(i)).getCoosys();

		// write COOSYS elements
		writeCoosys(coosysSet);

		// GROUP elements - since VOTable 1.2
		GroupSet groupSet = ((SavotResource) resourceset.getItemAt(i)).getGroups();

		// write GROUP elements - since VOTable 1.2
		writeGroup(groupSet);

		// PARAM elements
		ParamSet params = ((SavotResource) resourceset.getItemAt(i)).getParams();

		// write PARAM elements
		writeParam(params);

		// LINK elements
		LinkSet linkSet = ((SavotResource) resourceset.getItemAt(i)).getLinks();

		// write LINK elements
		writeLink(linkSet);

		// TABLE elements
		TableSet tableSet = ((SavotResource) resourceset.getItemAt(i)).getTables();
		    
		StringBuffer tableline = new StringBuffer();

		for (int k = 0; k < tableSet.getItemCount(); k++) {
		    SavotTable table = (SavotTable) tableSet.getItemAt(k);
		    tableline.delete(0, tableline.length());

		    if (table.getAbove().equals("") == false)
			tableline.append("\n<!-- ").append(((SavotTable) table).getAbove()).append(" -->");

		    tableline.append("\n<TABLE");

		    if (table.getId() != null && table.getId().equals("") == false)
			tableline.append(" ID=").append('"').append(encodeAttribute(table.getId())).append('"');

		    if (table.getName() != null && table.getName().equals("") == false)
			tableline.append(" name=").append('"').append(encodeAttribute(table.getName())).append('"');

		    if (table.getRef() != null && table.getRef().equals("") == false)
			tableline.append(" ref=").append('"').append(encodeAttribute(table.getRef())).append('"');

		    if (table.getUcd() != null && table.getUcd().equals("") == false)
			tableline.append(" ucd=").append('"').append(encodeAttribute(table.getUcd())).append('"');

		    if (table.getUtype() != null && table.getUtype().equals("") == false)
			tableline.append(" utype=").append('"').append(encodeAttribute(table.getUtype())).append('"');
		    
		    if (table.getNrows() != null && table.getNrows().equals("") == false)
			tableline.append(" nrows=").append('"').append(encodeAttribute(table.getNrows())).append('"');

		    tableline.append(">");

		    if (((SavotTable) table).getBelow().equals("") == false)
			tableline.append("\n<!-- ").append(((SavotTable) table).getBelow()).append(" -->\n");

		    // DESCRIPTION
		    if (table.getDescription().equals("") == false)
			tableline.append("\n<DESCRIPTION>").append(encodeElement(table.getDescription())).append("</DESCRIPTION>");

		    dataBuffWriter.write(tableline.toString());

		    // FIELD elements
		    FieldSet fieldSet = (FieldSet) table.getFields();

		    // write FIELD elements
		    writeField(fieldSet);
		    
		    // PARAM elements
		    ParamSet paramSet = (ParamSet) table.getParams();

		    // write PARAM elements
		    writeParam(paramSet);

		    // GROUP elements
		    GroupSet groupSet1 = (GroupSet) table.getGroups();

		    // write GROUP elements
		    writeGroup(groupSet1);

		    // LINK elements
		    linkSet = (LinkSet) table.getLinks();
		    
		    // write LINK elements
		    writeLink(linkSet);

		    if (table.getData() != null) {
			// <DATA>
		 
			    dataBuffWriter.write(databegin);
			    SavotData data = table.getData();
			    if (data.getTableData() != null) {
				// <TABLEDATA>
				dataBuffWriter.write(tabledatabegin);
				SavotTableData tableData = data.getTableData();
				TRSet trs = (TRSet) tableData.getTRs();
				StringBuffer rowbuffer = new StringBuffer();
				
				for (int p = 0; p < trs.getItemCount(); p++) {
				    TDSet tds = trs.getTDSet(p);

				    // <TR>
				    dataBuffWriter.write(trbegin);
				    rowbuffer.delete(0, rowbuffer.length());
				    
				    for (int r = 0; r < tds.getItemCount(); r++) {
					
//					if (((SavotTD) tds.getItemAt(r)).getAbove().equals("") == false)
//					    rowbuffer.append("\n<!-- ").append(((SavotTD) tds.getItemAt(r)).getAbove()).append(" -->\n");

					// <TD>
					rowbuffer.append(tdbegin1);

					//dataBuffWriter.write(tdbegin2);
					rowbuffer.append(tdbegin2);
								
//					if (((SavotTD) tds.getItemAt(r)).getBelow().equals("") == false)
//					    rowbuffer.append("\n<!-- ").append(((SavotTD) tds.getItemAt(r)).getBelow() + " -->\n");
					if (elementEntities)
					    rowbuffer.append(encodeElement(tds.getContent(r)));
					else 
					    rowbuffer.append(tds.getContent(r));

					// </TD>
					rowbuffer.append(tdend);
				    }
				    // </TR>
				    dataBuffWriter.write(rowbuffer.toString());
				    rowbuffer.setLength(0);
				    dataBuffWriter.write(trend);
				}
				// </TABLE>
				dataBuffWriter.write(tabledataend);
			    }

			    // write BINARY element
			    if (data.getBinary() != null)
				writeBinary(data.getBinary());

			    // write FITS element
			    if (data.getFits() != null)
				writeFits(data.getFits());

			    // </DATA>
			    dataBuffWriter.write(dataend);
			}

			// INFO (at End) elements - since VOTable 1.2
			InfoSet infosAtEnd = (InfoSet) table.getInfosAtEnd();

			// write INFO (at End) elements - since VOTable 1.2
			writeInfo(infosAtEnd);

			// </TABLE>
			dataBuffWriter.write(tableend);

		    }
		    if (((SavotResource) resourceset.getItemAt(i)).getResources().getItemCount() != 0)
			writeResource(((SavotResource) resourceset.getItemAt(i)).getResources());

		    // INFO (at End) elements - since VOTable 1.2
		    InfoSet infosAtEnd = ((SavotResource) resourceset.getItemAt(i)).getInfosAtEnd();

		    // write INFO (at End) elements - since VOTable 1.2
		    writeInfo(infosAtEnd);

		    // </RESOURCE>
		    dataBuffWriter.write(resourceend);
		}
	    }
    }
}