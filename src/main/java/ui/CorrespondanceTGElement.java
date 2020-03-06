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





package ui;

import avatartranslator.AvatarBlock;
import avatartranslator.AvatarAMSInterface;
import avatartranslator.AvatarLibraryFunction;
import req.ebrdd.EBRDDComponent;
import req.ebrdd.EBRDDGeneralComponent;
import sddescription.Evt;
import sddescription.HMSCElement;
import sddescription.HMSCNode;
import sddescription.MSC;
import tepe.TEPEComponent;
import tmltranslator.DIPLOElement;
import tmltranslator.HwNode;
import tmltranslator.TMLActivityElement;
import translator.ADComponent;
import translator.TClass;
import ui.cd.TCDTClass;
import ui.util.CorrespondanceElement;

import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Class CorrespondanceTGElement
 * Correspondance between data of a Turtle modeling and graphical elements
 * Creation: 11/12/2003
 * @version 1.0 11/12/2003
 * @author Ludovic APVRILLE
 */
public class CorrespondanceTGElement implements CorrespondanceElement<TGComponent> {
    private Vector<TGComponent> tg; //tgelement
    private Vector<String> names; //prename
    private Vector<Object> data; // turtle modeling elements
    private Vector<String> panelNames; //to look for an element only in it's panel
                               //It is more natural than using indexes and easyer to use in a recursive context  
    
    public CorrespondanceTGElement() {
        tg = new Vector<>();
        data = new Vector<Object>();
        names = new Vector<String>();
        panelNames=new Vector<String>();
    }
    
    public Vector<TGComponent> getTG() { return tg;}
    public Vector<String> getNames() { return names;}
    public Vector<Object> getData() { return data;}
    public Vector<String> getPanelNames() { return panelNames;}
    
    public void merge(CorrespondanceTGElement ce) {
           tg.addAll(ce.getTG());
           names.addAll(ce.getNames());
           data.addAll(ce.getData());
           panelNames.addAll(ce.getPanelNames());
    }

    @Override
    public void addCor(Object o, TGComponent tgc) {
        addCor(o, tgc, "");
        }
    
    public void addCor(Object o, TGComponent tgc, String preName) {
        data.addElement(o);
        tg.addElement(tgc);
        names.add(preName);
    }
    
    
    
    public TGComponent getTG(Object o) {
        int index = data.indexOf(o);
        if ((index != -1) && (tg.size() > index)) {
            return	tg.elementAt(index);
        }
        return null;
    }
	
	public Object getObject(TGComponent tgc) {
        int index = tg.indexOf(tgc);
        if ((index != -1) && (tg.size() > index)) {
            return	data.elementAt(index);
        }
        return null;
    }
    
    
    public void addCorInPanel(Object o, TGComponent tgc, String panelName)
    {
    	data.addElement(o);
        tg.addElement(tgc);
        panelNames.addElement(panelName);    	
    }
    
    
    /*
     * Returns the ADComponent coresponding to the first TGComponent founded named "name"
     *  @author Emil Salageanu
     */
    public ADComponent getADComponentByName(String name, String panelName)
    {
    	
    	for (int i=0;i<tg.size();i++)
    	{
    		
    		TGComponent tgc = tg.get(i);
    		String tgcPanelName = panelNames.get(i);
           
    		if (tgc.getValue()!=null)
    		if (tgc.getValue().equals(name)&& tgcPanelName.equals(panelName)) 
    			{ Object o =data.elementAt(i);
    			  if (o instanceof ADComponent) return (ADComponent)o;
    			}
    	}
     return null;
    }
    
    public ADComponent getADComponentInPanel(TGComponent t, String panelName)
    {
    	for (int i=0;i<tg.size();i++)
    	{
    		TGComponent tgc = tg.get(i);
    		String tgcPanelName = panelNames.get(i);
    		if (tgc.equals(t)&& tgcPanelName.equals(panelName)) 
    			{ Object o =data.elementAt(i);
    			  if (o instanceof ADComponent) return (ADComponent)o;
    			}
    	}
     return null;
    }
    
    public TClass getTClass(Object o) {
        int index = tg.indexOf(o);
        if ((index != -1) && (data.size() > index)) {
            return	(TClass)(data.elementAt(index));
        }
        return null;
    }
	
	 public TGComponent getTGFromObject(Object o) {
        int index = tg.indexOf(o);
        if ((index != -1) && (data.size() > index)) {
            return	(TGComponent)(data.elementAt(index));
        }
        return null;
    }
    
     public TGComponent getTGAt(int index) {
        if ((index != -1) && (tg.size() > index)) {
            return tg.elementAt(index);
        }
        return null;
    }
    
    public TClass getTClass(TCDTClass t) {
        int index = tg.indexOf(t);
         if ((index != -1) && (tg.size() > index)) {
             Object o = data.elementAt(index);
             if (o instanceof TClass) {
                return (TClass)o;
             }
            return null;
        }
        return null;
    }
	
	public AvatarBlock getAvatarBlock(TGComponent _tgc) {
        int index = tg.indexOf(_tgc);
         if ((index != -1) && (tg.size() > index)) {
             Object o = data.elementAt(index);
             if (o instanceof AvatarBlock) {
                return (AvatarBlock)o;
             }
            return null;
        }
        return null;
    }

    	public AvatarAMSInterface getAvatarAMSInterface(TGComponent _tgc) {
        int index = tg.indexOf(_tgc);
         if ((index != -1) && (tg.size() > index)) {
             Object o = data.elementAt(index);
             if (o instanceof AvatarAMSInterface) {
                return (AvatarAMSInterface)o;
             }
            return null;
        }
        return null;
    }

    public AvatarLibraryFunction getAvatarLibraryFunction (TGComponent _tgc) {
        int index = tg.indexOf (_tgc);
        if (index == -1)
            return null;

        Object o = data.elementAt (index);
        if (o instanceof AvatarLibraryFunction)
            return (AvatarLibraryFunction) o;

        return null;
    }
    
    public HMSCNode getNodeAt(int index) {
        if ((index != -1) && (data.size() > index)) {
            return	(HMSCNode)(data.elementAt(index));
        }
        return null;
    }
    
    public int getSize() {
        return tg.size();
    }
    
	public ArrayList<ADComponent> getADComponentCorrespondance(ArrayList<TGComponent> _list) {
		if (_list == null) {
			return null;
		}
		
		ArrayList<ADComponent> listAD = new ArrayList<ADComponent>();
		ArrayList<ADComponent> listADs;
		
		for(TGComponent tgc:_list) {
			listADs = getADComponents(tgc);
			//
			if (listADs.size() > 0) {
				listAD.addAll(listADs);
			}
		}
		
		//
		return listAD;
	}
	
	public ArrayList<TMLActivityElement> getTMLActivityElementCorrespondance(ArrayList<TGComponent> _list) {
		if (_list == null) {
			return null;
		}
		
		ArrayList<TMLActivityElement> listED = new ArrayList<TMLActivityElement>();
		TMLActivityElement elt;
		for(TGComponent tgc:_list) {
			elt = getTMLActivityElement(tgc);
			if (elt != null) {
				listED.add(elt);
				//
			}
		}
		
		return listED;
	}
	
	
    public ADComponent getADComponent(TGComponent tgc) {
        int index = tg.indexOf(tgc);
        if ((index != -1) && (data.size() > index)) {
            Object o = data.elementAt(index);
            if (o instanceof ADComponent) {
                return (ADComponent)o;
            }
        }
        return null;
    }
	
	public EBRDDGeneralComponent getEBRDDGeneralComponent(TGComponent tgc) {
        int index = tg.indexOf(tgc);
        if ((index != -1) && (data.size() > index)) {
            Object o = data.elementAt(index);
            if (o instanceof EBRDDGeneralComponent) {
                return (EBRDDGeneralComponent)o;
            }
        }
        return null;
    }
	
	public EBRDDComponent getEBRDDComponent(TGComponent tgc) {
        int index = tg.indexOf(tgc);
        if ((index != -1) && (data.size() > index)) {
            Object o = data.elementAt(index);
            if (o instanceof EBRDDComponent) {
                return (EBRDDComponent)o;
            }
        }
        return null;
    }
	
	public ArrayList<ADComponent> getADComponents(TGComponent tgc) {
		ArrayList<ADComponent>  list = new ArrayList<ADComponent>();
		TGComponent tmptgc;
		Object o;
		
        int index = tg.indexOf(tgc);
		if (index == -1) {
			return list;
		}
		
		for(int i=0; i<tg.size(); i++) {
			tmptgc = tg.elementAt(i);
			if (tmptgc == tgc) {
				o = data.elementAt(i);
				list.add((ADComponent)o);
			}
		}
       
        return list;
    }
	
	public HwNode getHwNode(TGComponent tgc) {
        int index = tg.indexOf(tgc);
        if ((index != -1) && (data.size() > index)) {
            Object o = data.elementAt(index);
            if (o instanceof HwNode) {
                return (HwNode)o;
            }
        }
        return null;
    }
	
	public TMLActivityElement getTMLActivityElement(TGComponent tgc) {
        int index = tg.indexOf(tgc);
        if ((index != -1) && (data.size() > index)) {
            Object o = data.elementAt(index);
            if (o instanceof TMLActivityElement) {
                return (TMLActivityElement)o;
            }
        }
        return null;
    }
    
    public Evt getEvt(TGComponent tgc) {
        int index = tg.indexOf(tgc);
        if ((index != -1) && (data.size() > index)) {
            Object o = data.elementAt(index);
            if (o instanceof Evt) {
                return (Evt)o;
            }
        }
        return null;
    }
    
    public Evt getSendingMsgEvt(TGComponent tgc) {
        int index = 0;
        Evt evt;
        Object o;
        
        while((index = tg.indexOf(tgc, index)) > -1) {
            o = data.elementAt(index);
            if (o instanceof Evt) {
                evt = (Evt)o;
                if (evt.isSendingEvt()) {
                    return evt;
                } else {
                    index ++;
                }
            }
        }
        
        return null;
    }
    
     public Evt getReceivingMsgEvt(TGComponent tgc) {
        int index = 0;
        Evt evt;
        Object o;
        
        while((index = tg.indexOf(tgc, index)) > -1) {
            o = data.elementAt(index);
            if (o instanceof Evt) {
                evt = (Evt)o;
                if (evt.isReceivingEvt()) {
                    return evt;
                } else {
                    index++;
                }
            }
        }
        
        return null;
    }
    
    public HMSCNode getHMSCNode(TGComponent tgc) {
        int index = tg.indexOf(tgc);
        if ((index != -1) && (data.size() > index)) {
            Object o = data.elementAt(index);
            if (o instanceof HMSCNode) {
                return (HMSCNode)o;
            }
        }
        return null;
    }
    
    public MSC getMSCNode(TGComponent tgc) {
        int index = tg.indexOf(tgc);
        if ((index != -1) && (data.size() > index)) {
            Object o = data.elementAt(index);
            if (o instanceof MSC) {
                return (MSC)o;
            }
        }
        return null;
    }
    
    public HMSCElement getHMSCElement(TGComponent tgc) {
        int index = tg.indexOf(tgc);
        if ((index != -1) && (data.size() > index)) {
            Object o = data.elementAt(index);
            if (o instanceof HMSCElement) {
                return (HMSCElement)o;
            }
        }
        return null;
    }
	
    
    
    public ADComponent getADComponentByIndex(TGComponent tgc, int indexFound) {
        int index = tg.indexOf(tgc);
        while(indexFound > 0) {
            index = tg.indexOf(tgc, index + 1);
            if (index == -1) {
                return null;
            }
            indexFound --;
        }
        if ((index != -1) && (data.size() > index)) {
            Object o = data.elementAt(index);
            if (o instanceof ADComponent) {
                return (ADComponent)o;
            }
        }
        return null;
    }
    
    
     public HMSCElement getHMSCElementByIndex(TGComponent tgc, int indexFound) {
        int index = tg.indexOf(tgc);
        while(indexFound > 0) {
            index = tg.indexOf(tgc, index + 1);
            if (index == -1) {
                return null;
            }
            indexFound --;
        }
        if ((index != -1) && (data.size() > index)) {
            Object o = data.elementAt(index);
            if (o instanceof HMSCElement) {
                return (HMSCElement)o;
            }
        }
        return null;
    }
	
	@Override
    public void useDIPLOIDs() {
		ArrayList<TGComponent> list = new ArrayList<TGComponent>();
		Object o0, o1;
		DIPLOElement de;
		TGComponent tgc;
		for (int i=0; i<data.size(); i++) {
			o0 = data.get(i);
			if (o0 instanceof DIPLOElement) {
				o1 = tg.get(i);
				if ((o1 != null) && !(list.contains(o1))){
					de = (DIPLOElement)(o0);
					//
					tgc = (TGComponent)(o1);
					tgc.setDIPLOID(de.getID());
					list.add(tgc);
				}
			}
		}
	}
	
	public void makeTEPEIDs() {
		ArrayList<TGComponent> list = new ArrayList<TGComponent>();
		Object o0, o1;
		TEPEComponent te;
		TGComponent tgc;
		for (int i=0; i<data.size(); i++) {
			o0 = data.get(i);
			if (o0 instanceof TEPEComponent) {
				o1 = tg.get(i);
				if ((o1 != null) && !(list.contains(o1))){
					te = (TEPEComponent)(o0);
					//
					tgc = (TGComponent)(o1);
					tgc.setTEPEID(te.getID());
					list.add(tgc);
				}
			}
		}
	}
	
	public void removeBreakpoint(Point p) {
		Object o0, o1;
		TGComponent tgc;
		DIPLOElement de;
		
		for (int i=0; i<data.size(); i++) {
			o0 = data.get(i);
			if (o0 instanceof DIPLOElement) {
				de = (DIPLOElement)o0;
				if (de.getID() == p.y) {
					o1 = tg.get(i);
					if (o1 != null) {
						tgc = (TGComponent)(o1);
						tgc.setBreakpoint(false);
						return;
					}
				}
			}
		}
	}
	
	public void addBreakpoint(Point p) {
		Object o0, o1;
		TGComponent tgc;
		DIPLOElement de;
		
		for (int i=0; i<data.size(); i++) {
			o0 = data.get(i);
			if (o0 instanceof DIPLOElement) {
				de = (DIPLOElement)o0;
				if (de.getID() == p.y) {
					o1 = tg.get(i);
					if (o1 != null) {
						tgc = (TGComponent)(o1);
						tgc.setBreakpoint(true);
						return;
					}
				}
			}
		}
	}
    
}
