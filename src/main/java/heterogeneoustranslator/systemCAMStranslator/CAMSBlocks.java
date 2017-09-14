/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
 * 
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * 
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */




package heterogeneoustranslator.systemCAMStranslator;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Vector;
import ui.*;

/**
* Class CAMSBlocks
* Simulation Block for SystemC-AMS Diagrams
* Creation: 28/08/2017
* @version 1.0 28/08/2017
* @author Côme DEMARIGNY
 */

public class CAMSBlocks{

    private String name;
    private int nbOfIn;
    private int nbOfOut;
    private int nbOfHybridIn;
    private int nbOfHybridOut;
    private LinkedList<TAttribute> myAttributes;
    private LinkedList<CAMSSignal> mySignals;
    private String [] processCode;

    
    public CAMSBlocks(String _name, int _nbOfIn, int _nbOfOut, int _nbOfHybridIn, int _nbOfHybridOut, LinkedList<TAttribute> _myAttributes, LinkedList<CAMSSignal> _mySignals, String [] _processCode){
	name=_name;
	nbOfIn=_nbOfIn;
	nbOfOut=_nbOfOut;
	nbOfHybridIn=_nbOfHybridIn;
	nbOfHybridOut=_nbOfHybridOut;
	myAttributes=_myAttributes;
	mySignals=_mySignals;
	processCode=_processCode;
    
    }

    public void setBlockName(String newName){
	name = newName;
    }

    public void setNbOfIn(int newIn){
	nbOfIn = newIn;
    }

    public void setNbOfOut(int newOut){
	nbOfOut = newOut;
    }

   public void setNbOfHybridIn(int newIn){
	nbOfHybridIn = newIn;
    }

    public void setNbOfHybridOut(int newOut){
	nbOfHybridOut = newOut;
    }

    public void setMyAttributes(LinkedList<TAttribute> newMyAttributes){
	myAttributes = newMyAttributes;
    }

    public void setMySignals(LinkedList<CAMSSignal> newMySignals){
	mySignals = newMySignals;
    }

    public void setProcessCode(String[] newProcessCode){
	processCode = newProcessCode;
    }

    public String getBlockName(){
	return name;
    }

    public int getNbOfIn() {
        return nbOfIn;
    }

    public int getNbOfOut() {
        return nbOfOut;
    }
    public int getNbOfHybridIn() {
        return nbOfHybridIn;
    }

    public int getNbOfHybridOut() {
        return nbOfHybridOut;
    }
    public int getTotalIn() {
        return nbOfIn + nbOfHybridIn;
    }

    public int getTotalOut() {
        return nbOfOut + nbOfHybridOut;
    }

    public LinkedList<TAttribute> getMyAttributes(){
	return myAttributes;
    }
    
    public LinkedList<CAMSSignal> getMySignals(){
	return mySignals;
    }

    public String[] getProcessCode(){
	return processCode;
    }

}