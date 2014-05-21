package tests.sax;

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
import cds.astrores.sax.AstroresSAXConsumer;
import java.util.ArrayList;
import java.util.Vector;

/**
 * <p>
 * Astrores SAX Sample, this sample shows how to use the Astrores SAX parser
 * </p>
 * @author Andre Schaaff  
 */
public class AstroresSAXSample implements AstroresSAXConsumer {

    public AstroresSAXSample() {
    }

    // attributes is a Vector containing couples of (attribute name, attribute
    // value)
    // exemple : (attributes.elementAt(0), attributes.elementAt(1)),
    // (attributes.elementAt(2), attributes.elementAt(3)), ...
    // start elements
    public void startAstrores(@SuppressWarnings("rawtypes") Vector attributes) {
    }

    public void startDescription() {
    }

    public void startResource(@SuppressWarnings("rawtypes") ArrayList attributes) {
    }

    public void startTable(@SuppressWarnings("rawtypes") ArrayList attributes) {
    }

    public void startField(@SuppressWarnings("rawtypes") ArrayList attributes) {
    }

    public void startFieldref(@SuppressWarnings("rawtypes") ArrayList attributes) {
    }

    public void startValues(@SuppressWarnings("rawtypes") ArrayList attributes) {
    }

    public void startStream(@SuppressWarnings("rawtypes") ArrayList attributes) {
    }

    public void startTR() {
        System.out.println("Start new ROW");
    }

    public void startTD(@SuppressWarnings("rawtypes") ArrayList attributes) {
    }

    public void startData() {
    }

    public void startBinary() {
    }

    public void startFits(@SuppressWarnings("rawtypes") ArrayList attributes) {
    }

    public void startTableData() {
    }

    public void startParam(@SuppressWarnings("rawtypes") ArrayList attributes) {
    }

    public void startParamRef(@SuppressWarnings("rawtypes") ArrayList attributes) {
    }

    public void startLink(@SuppressWarnings("rawtypes") ArrayList attributes) {
    }

    public void startInfo(@SuppressWarnings("rawtypes") ArrayList attributes) {
    }

    public void startMin(@SuppressWarnings("rawtypes") ArrayList attributes) {
    }

    public void startMax(@SuppressWarnings("rawtypes") ArrayList attributes) {
    }

    public void startOption(@SuppressWarnings("rawtypes") ArrayList attributes) {
    }

    public void startGroup(@SuppressWarnings("rawtypes") ArrayList attributes) {
    }

    public void startCoosys(@SuppressWarnings("rawtypes") ArrayList attributes) {
    }

    public void startDefinitions() {
    }

    // end elements
    @Override
    public void endAstrores() {
    }

    public void endDescription() {
    }

    public void endResource() {
    }

    public void endTable() {
    }

    public void endField() {
    }

    public void endFieldref() {
    }

    public void endValues() {
    }

    public void endStream() {
    }

    public void endTR() {
    }

    public void endTD() {
    }

    public void endData() {
    }

    public void endBinary() {
    }

    public void endFits() {
    }

    public void endTableData() {
    }

    public void endParam() {
    }

    public void endParamRef() {
    }

    public void endLink() {
    }

    public void endInfo() {
    }

    public void endMin() {
    }

    public void endMax() {
    }

    public void endOption() {
    }

    public void endGroup() {
    }

    public void endCoosys() {
    }

    public void endDefinitions() {
    }

    // TEXT
    public void textTD(String text) {
        System.out.println(text);
    }

    public void textMin(String text) {
    }

    public void textMax(String text) {
    }

    public void textCoosys(String text) {
    }

    public void textLink(String text) {
    }

    public void textOption(String text) {
    }

    public void textGroup(String text) {
    }

    public void textInfo(String text) {
    }

    public void textDescription(String text) {
    }

    // document
    @Override
    public void startDocument() {
    }

    @Override
    public void endDocument() {
    }

    @Override
    public void startAstrores(@SuppressWarnings("rawtypes") ArrayList attributes) {
        // TODO Auto-generated method stub

    }
}
