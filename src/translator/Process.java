/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class Process
 * Creation: 11/12/2003
 * @version 1.0 10/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package translator;

import java.util.*;

public class Process {
    protected String name;
    protected Vector gateList;
    protected Vector paramList;
    protected String body;
    protected int languageID;
    
    protected static final String BEGIN_DEC = "process ";
    protected static final String SEP1G = "[";
    protected static final String SEP2G = ", ";
    protected static final String SEP3G = "]";
    protected static final String SEP1P = "(";
    protected static final String SEP2P = ", ";
    protected static final String SEP3P = ")";
    protected static final String END_DEC = " : noexit := \n";
    protected static final String END_DEC_LOTOS = " : exit := \n";
    protected static final String END_PROC = "\nendproc";
    
    protected static final String HIDE = "hide ";
    protected static final String SEP1H = ", ";
    protected static final String HIDE_END = " in\n(";
    protected static final String HIDE_END_PROC = "\n)";
    
    public Process(String _name, Vector _gateList, Vector _paramList, int _languageID) {
        name = _name;
        gateList = _gateList;
        paramList = _paramList;
        languageID = _languageID;
    }
    
    public void setLanguageID(int _languageID) {
        languageID = _languageID;
    }
    
    public String getName() {
        return name;
    }
    
    public void setBody(String s) {
        body = s;
    }
    
    public void setGateList(Vector v) {
        gateList = v;
    }
    
    public Vector getGateList() {
        return gateList;
    }
    
    public void addGate(Gate g) {
        gateList.addElement(g);
    }
    
    public Vector getParamList() {
        return paramList;
    }
    
    public String toString() {
        String head =   BEGIN_DEC + name;
        if (hasNonInternalGates()) {
            head = head + SEP1G + listNonInternalGates() + SEP3G;
        }
        if(hasParameters()) {
            head = head + SEP1P + listParameters() + SEP3P;
        }
        if(hasInternalGates()){
            if (languageID == TURTLETranslator.LOTOS) {
                head = head + END_DEC_LOTOS + HIDE + listInternalGates() + HIDE_END + body + HIDE_END_PROC + END_PROC;
            } else {
                head = head + END_DEC + HIDE + listInternalGates() + HIDE_END + body + HIDE_END_PROC + END_PROC;
            }
        } else {
            if (languageID == TURTLETranslator.LOTOS) {
                head = head + END_DEC_LOTOS + body + END_PROC;
            } else {
                head = head + END_DEC + body + END_PROC;
            }
        }
        return head;
    }
    
    public String getCallToMe() {
        String s = name;
        if (hasNonInternalGates()) {
            s = s + SEP1G + listNonInternalGates() + SEP3G;
        }
        if(hasParameters()) {
            s = s + SEP1P + listCallParameters() + SEP3P;
        }
        return s;
    }
    
    
    public String getCallToMe(Param p, String expr) {
        String s = name;
        if (hasNonInternalGates()) {
            s = s + SEP1G + listNonInternalGates() + SEP3G;
        }
        if(hasParameters()) {
            s = s + SEP1P + listCallParameters(p, expr) + SEP3P;
        }
        return s;
    }
    
    public String getCallToMe(Param []p, String []expr) {
        String s = name;
        if (hasNonInternalGates()) {
            s = s + SEP1G + listNonInternalGates() + SEP3G;
        }
        if(hasParameters()) {
            s = s + SEP1P + listCallParameters(p, expr) + SEP3P;
        }
        return s;
    }
    
    public String getHighLevelCallToMe(int languageID) {
        String s = name;
        if (hasNonInternalGates()) {
            s = s + SEP1G + listNonInternalGates() + SEP3G;
        }
        if(hasParameters()) {
            s = s + SEP1P + listInitParameters(languageID) + SEP3P;
        }
        return s;
    }
    
    public boolean hasNonInternalGates() {
        if  (gateList == null) {
            return false;
        }
        
        Gate g;
        for(int i=0; i<gateList.size(); i++) {
            g = (Gate)(gateList.elementAt(i));
            if (!g.isInternal()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasInternalGates() {
        if  (gateList == null) {
            return false;
        }
        
        Gate g;
        for(int i=0; i<gateList.size(); i++) {
            g = (Gate)(gateList.elementAt(i));
            if (g.isInternal()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasParameters() {
        return ((paramList != null) && (paramList.size()>0));
    }
    
    public String listNonInternalGates() {
        String s = "";
        boolean find = false;
        Gate g;
        
        for(int i=0; i<gateList.size(); i++) {
            g = (Gate)(gateList.elementAt(i));
            if (!g.isInternal()) {
                if (!find) {
                    find = true;
                    s = s + g.getLotosName();
                } else {
                    s = s + SEP2G + g.getLotosName();
                }
            }
        }
        return s;
    }
    
    public String listInternalGates() {
        String s = "";
        boolean find = false;
        Gate g;
        
        for(int i=0; i<gateList.size(); i++) {
            g = (Gate)(gateList.elementAt(i));
            if (g.isInternal()) {
                if (!find) {
                    find = true;
                    s = s + g.getLotosName();
                } else {
                    s = s + SEP2G + g.getLotosName();
                }
            }
        }
        return s;
    }
    
    public String listParameters() {
        String s = "";
        boolean find = false;
        Param par;
        
        for(int i=0; i<paramList.size(); i++) {
            par = (Param)(paramList.elementAt(i));
            if (!find) {
                find = true;
                s = s + par.getLotosTranslation();
            } else {
                s = s + SEP2P + par.getLotosTranslation();
            }
        }
        return s;
    }
    
    public String listCallParameters() {
        String s = "";
        boolean find = false;
        Param par;
        
        for(int i=0; i<paramList.size(); i++) {
            par = (Param)(paramList.elementAt(i));
            if (!find) {
                find = true;
                s = s + par.getLotosName();
            } else {
                s = s + SEP2P + par.getLotosName();
            }
        }
        return s;
    }
    
    public String listCallParameters(Param p, String expr) {
        String s = "";
        boolean find = false;
        Param par;
        
        for(int i=0; i<paramList.size(); i++) {
            par = (Param)(paramList.elementAt(i));
            if (!find) {
                find = true;
                if (par == p) {
                    s = s + expr;
                } else {
                    s = s + par.getLotosName();
                }
            } else {
                if (par == p) {
                    s = s + SEP2P + expr;
                } else {
                    s = s + SEP2P + par.getLotosName();
                }
            }
        }
        return s;
    }
    
    public String listCallParameters(Param []p, String []expr) {
        String s = "";
        boolean find = false;
        Param par;
        int j;
        
        for(int i=0; i<paramList.size(); i++) {
            par = (Param)(paramList.elementAt(i));
            find = false;
            for(j=0; j<p.length; j++) {
                if (p[j] == par) {
                    find = true;
                    if (i == 0) {
                        s = s + expr[j];
                    } else {
                        s = s + SEP2P + expr[j];
                    }
                }
            }
            //System.out.println("s=" + s);
            if (!find) {
                if (i == 0) {
                    s = s + par.getLotosName();
                } else {
                    s = s + SEP2P + par.getLotosName();
                }
            }
            //System.out.println("s=" + s);
        }
        return s;
    }
    
    public String listInitParameters(int languageID) {
        String s = "";
        boolean find = false;
        Param par;
        
        for(int i=0; i<paramList.size(); i++) {
            par = (Param)(paramList.elementAt(i));
            if (!find) {
                find = true;
                s = s + TURTLETranslator.modifyAction(par.getValue(), languageID); //"0";
            } else {
                s = s + SEP2P + TURTLETranslator.modifyAction(par.getValue(), languageID);//"0";
            }
        }
        return s;
    }
    
    
} // Class

