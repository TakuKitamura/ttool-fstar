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




package sddescription;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class HMSCNode
 * Creation: 16/08/2004
 * @version 1.2 07/10/2004
 * @author Ludovic APVRILLE
 */
public class HMSCNode extends HMSCElement {
    
    public static final int START = 0;
    public static final int STOP = 1;
    public static final int CHOICE = 2;
    public static final int PARALLEL = 3;
    public static final int PREEMPT = 4;
    public static final int SEQUENCE = 5;
    
    private int type;
    private List<MSC> nextMSCs;
    protected List<HMSCNode> nextNodes;
    private List<MSC> nextInformationMSCs;
    private List<HMSCNode> nextInformationNodes;
    private List<String> nextMSCGuards;
    private List<String> nextNodeGuards;
    
    public HMSCNode(String _name, int _type) {
        super(_name);
        type = _type;
        nextMSCs = new LinkedList<MSC>();
        nextNodes = new LinkedList<HMSCNode>();
        nextInformationMSCs = new LinkedList<MSC>();
        nextInformationNodes = new LinkedList<HMSCNode>();
        nextMSCGuards = new LinkedList<String>();
        nextNodeGuards = new LinkedList<String>();
    }
    
    public void addNextNode(HMSCNode c) {
        nextNodes.add(c);
    }
    
    public void addNextMSC(MSC m) {
        nextMSCs.add(m);
    }
    
    public int sizeMSCGuard() {
        return nextMSCGuards.size();
    }
    
    public void addMSCGuard(String s) {
        //System.out.println("Adding MSC guard " + s + " on " + name);
        nextMSCGuards.add(s);
    }
    
    public String getMSCGuard(int index) {
        return nextMSCGuards.get(index);
    }
    
    public int sizeNodeGuard() {
        return nextNodeGuards.size();
    }
    
    public void addNodeGuard(String s) {
        //System.out.println("Adding Node guard " + s + " on " + name);
        nextNodeGuards.add(s);
    }
    
    public String getNodeGuard(int index) {
        return nextNodeGuards.get(index);
    }
    
    public String getName() { return name; }
    
     public int getType() { return type; }
    
    
    public List<HMSCNode> getNextNodes() {
        return nextNodes;
    }
    
    public List<MSC> getNextMSCs() {
        return nextMSCs;
    } 
    
    public List<HMSCNode> getInformationNextNodes() {
        return nextInformationNodes;
    }
    
     public List<MSC> getInformationNextMSCs() {
        return nextInformationMSCs;
    }
    
    public String toString() {
       // List ll;
        int index = 0;
        HMSCNode n;
        MSC msc;
        String s = "";
        
        s += "Name=" + getName();
        s += "\n\tnextNodes= ";
        
        final Iterator<HMSCNode> nodeIterator = getNextNodes().listIterator();
        while( nodeIterator.hasNext()) {
            n = nodeIterator.next();
            s += n.getName() + " ";
            if (sizeNodeGuard() > index) {
                s += "guard= " + getNodeGuard(index) +  " ";
            }
            index ++;
        }
        
        s += "\n\tnextMSCs= ";
        index = 0;
        final Iterator<MSC> mscIterator = getNextMSCs().listIterator();
        while( mscIterator.hasNext()) {
            msc = mscIterator.next();
            s += msc.getName() + " ";
            if (sizeMSCGuard() > index) {
                s += "guard= " + getMSCGuard(index) + " ";
            }
            index ++;
        }
        //s+="\n";
        return s;
    }
}