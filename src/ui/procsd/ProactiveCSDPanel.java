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
 * Class ProactiveCSDPanel
 * Panel used for proactive composite structure diagrams
 * Creation: 05/07/2006
 * @version 1.0 07/08/2006
 * @author Ludovic APVRILLE, Emil Salageanu
 * @see
 */

package ui.procsd;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;



import javax.swing.JMenuItem;


import ui.*;
import ui.cd.TCDTClass;
import ui.prosmd.ProactiveSMDPanel;



public class ProactiveCSDPanel extends TDiagramPanel implements ActionListener {
    public static int NORMAL_FONT=1;
    public static int BIG_FONT=2;
    private int fontType=NORMAL_FONT;
    private ProCSDComponent topFather=null;
    
    public  ProactiveCSDPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
        addMouseListener(tdmm);
        addMouseMotionListener(tdmm);
       
    }
    
    
    public boolean actionOnDoubleClick(TGComponent tgc) {
    	return false;
    }
    
    public boolean actionOnAdd(TGComponent tgc) {
        return false;
    }
    public boolean actionOnValueChanged(TGComponent tgc) {
        return false;
    }
    
    public  boolean actionOnRemove(TGComponent tgc) {
        return false;
    }
    
    public String getXMLHead() {
        return "<ProactiveCSDPanel name=\"" + name + "\"" + sizeParam() + " >";
    }
    
    public String getXMLTail() {
        return "</ProactiveCSDPanel>";
    }
    
    public String getXMLSelectedHead() {
        return "<ProactiveCSDPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }
    
    public String getXMLSelectedTail() {
        return "</ProactiveCSDPanelCopy>";
    }
    
    public String getXMLCloneHead() {
        return "<ProactiveCSDPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }
    
    public String getXMLCloneTail() {
        return "</ProactiveCSDPanelCopy>";
    }
    
    
    
    private SwallowTGComponent findSwallowTGComponent_rec(TGComponent tgc, int x, int y)
    {
    	for (int i=0;i<tgc.getChildCount();i++)
    	{
    		TGComponent child=(TGComponent)tgc.getChild(i);
    		if ((child instanceof SwallowTGComponent) && (child.isOnMe(x, y) != null))
    		{
    			return findSwallowTGComponent_rec(child,x,y);
    		}
    	}
    	return (SwallowTGComponent)tgc;
    	
    }
    
    /*
     *  rewrite method to make it recursively find father
     * @see ui.TDiagramPanel#findSwallowTGComponent(int, int)
     */
    public SwallowTGComponent findSwallowTGComponent(int x, int y) {
      
        TGComponent tgc;
        SwallowTGComponent father=null;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if ((tgc instanceof SwallowTGComponent) && (tgc.isOnMe(x, y) != null)) {
               father=findSwallowTGComponent_rec(tgc,x,y);
                
            }
        }
        return ((SwallowTGComponent)father);
    
    }
  
    protected void buildDiagramPopupMenu() {
     super.buildDiagramPopupMenu();
     if (fontType==BIG_FONT)
     {
    	 diagramMenu.addSeparator();
         JMenuItem font = new JMenuItem("normal font");        
         font.addActionListener(this);
         diagramMenu.add(font);
    	 
     }
     else
     {
    	 diagramMenu.addSeparator();
         JMenuItem font = new JMenuItem("big font");        
         font.addActionListener(this);
         diagramMenu.add(font);
     } 
    }
    /*public void loadExtraParameters(Element elt) {
    }*/
    
    public void setMyFont(int t)
    {
    	fontType=t;
    }

    public int getMyFont()
    {
    	return fontType;
    }
    
    
    
//    public void saveProCSDComponentAsLibrary(ProCSDComponent proComp)
//    {
//    	 String data = mgui.gtm.makeXMLFromComponentOfADiagram(this, proComp,getMaxId(), xSel, ySel);
//         mgui.saveAsLibrary(data);
//         return;
//    }
    
//    public StringBuffer saveComponentInXML(TGComponent tgc) {
//        StringBuffer sb = new StringBuffer(getXMLHead());
//        sb.append("\n");
//        sb.append(tgc.saveInXML());
//        sb.append("\n");
//        sb.append(getXMLTail());
//        //System.out.println("sb=\n" + sb);
//        
        
        
//        Vector v=tdp.selectedProCSDComponent();
//        if ((v != null) && (v.size() > 0)) {
//            ProCSDComponent t;
//            ProactiveSMDPanel psmd;
//            for(int i=0; i<v.size(); i++) {
//                t = (ProCSDComponent)(v.elementAt(i));
//                psmd = mgui.getSMDPanel(mgui.getCurrentSelectedIndex(), t.getValue());
//                sb.append(psmd.saveInXML());
//            }
//        }
//        
//        
//        
//        return sb;
//    }
//    
    
    public void actionPerformed(ActionEvent e)
    {
    	if (e.getActionCommand().equals("big font"))
    	{
    		this.setMyFont(BIG_FONT);
    		
    	}

    	if (e.getActionCommand().equals("normal font"))
    	{
    		this.setMyFont(NORMAL_FONT);
    	}
    	
    }

    
     public MainGUI getMainGUI()
     {
    	 return mgui;
     }
   
     //well
     //we overwrite this function like this:
     // the component to be added might be added to the diagram itself or as a child of a component. 
     //but the parameters of this function do not allow us to know wich case are we in 
     //this is why we use a variable of the diagram - curentTopFather 
     
     public void addBuiltComponent(TGComponent tgc) {
         if (tgc == null) return;
         
         if (!(tgc instanceof ProCSDComponent))
        	 super.addBuiltComponent(tgc);
         else
         if (topFather==null){
        	 super.addBuiltComponent(tgc);
         }
         else
         {
        	// super.addBuiltComponent(tgc);
        	 tgc.setFather(topFather);
        	 topFather.addSwallowedTGComponent(tgc,0,0);
         }
     }
 
     public void setTopFather(ProCSDComponent t)
     {
    	 this.topFather=t;
     }
     
     
     /*
      *We need to return the selected child at the highest level, 
      *null if there is none 
      */
     private ProCSDComponent getSelectedChild(ProCSDComponent tgc)
     {
    	    	 
    	 Vector v=tgc.getComponentList();
    	 for (int k=0;k<v.size();k++)
    	 {
    		 ProCSDComponent child=(ProCSDComponent)v.get(k);
    		 if (child.isSelected()) return child;
    	
    		 ProCSDComponent childChild=getSelectedChild(child);
    		  if (childChild!=null) return childChild;
    		 
    	 }
    	     	  	     	 
    	 return null;
     }
     
     
     private StringBuffer componentsInXML(boolean selected) {
         StringBuffer sb = new StringBuffer("");
         StringBuffer s;
         TGComponent tgc;

         //Added by Solange to see the components in the list
         LinkedList ruteoList=componentList;
         //
         Iterator iterator = componentList.listIterator();
                     
         while(iterator.hasNext()) {
             tgc = (TGComponent)(iterator.next());
             if ((selected == false) || (tgc.isSelected())) {
                 s = tgc.saveInXML();
                 if (s == null) {
                     return null;
                 }
                 sb.append(s);
                 sb.append("\n");
             }
             else if (tgc instanceof ProCSDComponent)
             {
            	 //look to see if one of its subcomponents is selected
            	 ProCSDComponent selectedChild=getSelectedChild((ProCSDComponent)tgc);
            	 if (selectedChild!=null)
            	 {
            		 //well .... 
            		 //we have to take some verry strange and bad decisions
            		 //befor we just thwow away all this code and implement another architecture
            		 //we must not save teh father info for this child as this is the main component in the library
            		 TGComponent fatherTmp=selectedChild.getFather();
            		 selectedChild.setFather(null);
            		 s=selectedChild.saveInXML();
            		 sb.append(s);
            		 sb.append("\n");
            		 selectedChild.setFather(fatherTmp);
            	 }//selectedChild!=null 
             }//else if procsdComp
             
         }
         //System.out.println("making copy sb=\n" + sb);
         return sb;
     }
     
     
     
     /*
      *  Overwrite of method saveSelectedInXML
      *  Need to consider a selected subcomponent which is not in the list of components of the diagram
      *  but only stored as a child of a component
      * @see ui.TDiagramPanel#saveSelectedInXML()
      */
     
     public StringBuffer saveSelectedInXML() {
         StringBuffer s = componentsInXML(true);
         if (s == null) {
             return null;
         }
         StringBuffer sb = new StringBuffer(getXMLSelectedHead());
         sb.append("\n");
         sb.append(s);
         sb.append("\n");
         sb.append(getXMLSelectedTail());
         return sb;
     }
     
     /*
      * we use this to select/deselect all components on this diagram
      */
     public void selectComponents(boolean value)
     {
    	 for (int k=0;k<this.componentList.size();k++)
    	 {
    		 TGComponent tgc = (TGComponent)componentList.get(k);
    		 if (tgc instanceof ProCSDComponent)
    		 {
    			 ((ProCSDComponent)tgc).selectComponent(value);
    		 }
    		 else
    			 tgc.select(value);
    	 }
    	 
     }
     //Override Solange's method in TDiagramPanel
     //The methode should have been put here in the first place
     //we keep the method in TDiagramPanel for compatibility
     //but is this one which will be actually called
     public Vector selectedProCSDComponent(ProCSDComponent root) {
         TGComponent tgc, tgcomp;
         ProCSDComponent tgchild;
         TCDTClass t;
         Vector v = new Vector();
         Vector rootChildren=null;
         Iterator iterator=null;
         
         if (root!=null)
         {
           if ((root.isSelected())&&(root.getMySMD()!=null))
           {
        	   v.addElement(root);
        	   return v;
           }
        	 
           rootChildren=root.getComponentList();
           iterator=rootChildren.listIterator();
         }	 
         else        	 
         iterator = componentList.listIterator();
         
         int ruteo=0;
			
         while(iterator.hasNext()) {
             tgc = (TGComponent)(iterator.next());
             if (tgc instanceof ProCSDComponent)
             {
            	 if ((tgc.isSelected()) && ((ProCSDComponent)tgc).getMySMD()!=null)
         		{
         			v.addElement(tgc);
         		}
            	 else
            	 {
            		Vector children=((ProCSDComponent)tgc).getComponentList();
            		for (int q=0;q<children.size();q++)
            		{
            			ProCSDComponent child=(ProCSDComponent)children.get(q);
            			v.addAll(selectedProCSDComponent(child));
            		}
            		 
            	 }//else
             
             }//if an smd found
         }//while iterator has next
         return v;
     }
     
     
   public void updateAllMembranes()
   {
	   	 for (int k=0;k<this.componentList.size();k++)
	    	 {
	    		 TGComponent tgc = (TGComponent)componentList.get(k);
	    		 if (tgc instanceof ProCSDComponent)
	    		 {
	    			 ((ProCSDComponent)tgc).updateMembrane();
	    		 }
	    		 
	    	 }
      }
     
     
     
}