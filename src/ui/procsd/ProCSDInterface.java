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
 * Class TGConnectingPointProSMD
 * Definition of connecting points on which connectors of TML activity diagram can be connected
 * Creation: 05/07/2006
 * @version 1.0 05/07/2006
 * @author Emil Salageanu
 * @see
 */
package ui.procsd;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;




import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import myutil.GraphicLib;

import java.util.Collection;
import java.util.Vector;
import java.util.LinkedList;
import ui.cd.TCDCompositionOperator;
import ui.cd.TCDTClass;
import ui.cd.TGConnectingPointCompositionOperator;
import ui.window.JDialogAttribute;
import ui.*;

public class ProCSDInterface extends TCDCompositionOperator implements  ActionListener {
    
	// implements also SwallowedTGComponent ??? to see... 
	private boolean removedFromPanel=false;
	private Vector<TAttribute> myMessages;
     protected Graphics myG;
     protected boolean showMessages=false,lastVisible;
     private JMenuItem showMsgs;
     //Added by Solange
     private JMenuItem mandatory;
     //Added by Solange
     protected boolean manda=false; //by default is not mandatory
    protected boolean shome=true;  //true to show at the begining
    //Added by Solange
    private TGConnectorAttribute myConnector=null;

   
	public ProCSDInterface(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
		super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

	       // System.out.println("constructor"); 
	     
	        myImageIcon = IconManager.imgic2104;
	        
	     //_tdp.finishAddingConnector(_p1);
	        name="ProCSDInterface";
	        value="InterfaceName"; 
	        editable = true;
	        userResizable=true;
	        myMessages=new Vector();
	        
	        // Changed of place by Solange
	        //Antes era showMsgs = new JMenuItem("show messages");
	        
	        
	        nbConnectingPoint = 4;
			connectingPoint = new TGConnectingPoint[nbConnectingPoint];
			connectingPoint[0] = new TGConnectingPointPROCSDInterface(this, 0, height/2, true, false);
			connectingPoint[1] = new TGConnectingPointPROCSDInterface(this, width, height/2, true, false);
			connectingPoint[2] = new TGConnectingPointPROCSDInterface(this, width/2, height, true, false);
			connectingPoint[3] = new TGConnectingPointPROCSDInterface(this, width/2, 0, true, false);
			addGroup(new TGConnectingPointGroup(true));
	                addTGConnectingPointsComment();
	  
	 }
	
	
	public ProCSDInterface(ProCSDInterface pI, TDiagramPanel _tdp)
	{
		 super(pI.getX(),pI.getY(),pI.getMinHeight(),pI.getMaxHeight(),pI.getMinWidth(),pI.getMaxWidth(),false,null,_tdp);

		 this.value=(pI.getValue());
			this.manda=(pI.isMandatory());
			this.setMessages(pI.getMyMessages());
	
	this.width=pI.width;
	this.height=pI.height;
	}
	
	
	public void setMessages(Collection messages)
	{
		this.myMessages=new Vector(messages);
	}
	
	
	//method added by Solange
	public void setMyConnector(TGConnectorAttribute c)
	{
		myConnector=c;
	}
	//
	
	// method added by Solange
	public TGConnectorAttribute getMyConnector()
	{
		return myConnector;
	}
	//
	
	  public void internalDrawing(Graphics g) {
		  
		  if (this.x<=0)
			  this.x=1;
		  if (this.y<=0)
			  this.y=1;
		  
		  if (!shome) {
			  this.select(false);
			  return;
		  }
		  
		  
		    Graphics tmp = myG;
	        if (!tdp.isScaled()) {
	            myG = g;
	        }
	        
	        if ((tmp == null) || (lastVisible != showMessages)){
	            checkMySize();
	        }
	        lastVisible = showMessages;
	        
	        
	        checkMySize();
	        
	        int h  = g.getFontMetrics().getHeight();
	        
	        //Added by Solange
	        if(shome)
	        {
	        g.drawRect(x, y, width, height);	        
	        g.setColor(ColorManager.COMPOSITION_OPERATOR);
	        g.fillRect(x+1, y+1, width-1, height-1);
	        g.drawImage(IconManager.img8, x + width - 20, y + 3, ColorManager.COMPOSITION_OPERATOR, null);
	        ColorManager.setColor(g, getState(), 0);
	        g.setFont((g.getFont()).deriveFont(Font.BOLD));
	        g.drawString(value, x + textX, y + textY);
	        g.setFont((g.getFont()).deriveFont(Font.PLAIN));
	        }
	        // end if shome
	        
	
	/*        
	        
	        //if(manda) changed by Solange
	        if ((manda)&&(shome))
	        {
	        	   if (((ProactiveCSDPanel) tdp).getMyFont() == ProactiveCSDPanel.BIG_FONT)
		       		 {
		                Font f=g.getFont();
		                g.setFont(new Font("16italic",Font.ITALIC,16));
		                g.drawString("Contingency : MANDATORY", x + textX, y + textY + 2*h);
		                g.setFont(f);
		       		 }	
	        	   else{
	        		   g.drawString("Contingency : MANDATORY", x + textX, y + textY + 2*h);
	        	   }
	        }
	        else
	        {
	         if (shome)
	         {
	        	if (((ProactiveCSDPanel) tdp).getMyFont() == ProactiveCSDPanel.BIG_FONT)
	       		 {
	                Font f=g.getFont();
	                g.setFont(new Font("16italic",Font.ITALIC,16));
	                g.drawString("Contingency : OPTIONAL", x + textX, y + textY + 2*h);
	                g.setFont(f);
	       		 }	
       	        else
       	        {
       		     g.drawString("Contingency : OPTIONAL", x + textX, y + textY + 2*h);
       	        }
	         }
	        }
	        
	  */      
	    
	        
	        
	        //if(showMessages)  changed by Solange
	        if ((showMessages)&&(shome)) {
	        	//Changed from TAttribute all over the file, by Solange
	            TAttribute a;
	            for(int i=0; i<myMessages.size(); i++) {
	                a = (TAttribute)(myMessages.elementAt(i));
	              
	                if (((ProactiveCSDPanel) tdp).getMyFont() == ProactiveCSDPanel.BIG_FONT)
	       		 {
	                Font f=g.getFont();
	                g.setFont(new Font("14bold",Font.BOLD,14));
	                g.drawString(a.toString(), x + textX, y + textY + (i+2)* h);
	                g.setFont(f);
	       		 }
	                else 
	                	g.drawString(a.toString(), x + textX, y + textY + (i+2)* h);
	       		 }
	        }
	        //else if (myMessages.size() >0) {
	        //    g.drawString("...", x + textX, y + textY + 2* h);
	       // }
	  
	  }
	 
	  public int getType() {
	        return TGComponentManager.PROCSD_INTERFACE;
	    }
	
	  public LinkedList getMyMessages()
	  {
		  return new LinkedList(myMessages);
	  }
	    public boolean editOndoubleClick(JFrame frame) {
	       
	    	//Changed from JDialogAttribute, by Solange
	        JDialogAttributeProCSD dialog = new JDialogAttributeProCSD(myMessages, new Vector(), frame, "Setting messages of this interface" , "message");
	        //setJDialogOptions(jda);
	        dialog.addAccess("+");
	        //dialog.addAccess("-");
	        dialog.addType("void");
	        dialog.addType("return result");
	        
	        dialog.setSize(650, 375);
	        
	        GraphicLib.centerOnParent(dialog);
	      //  dialog.show(); // blocked until dialog has been closed
	        dialog.setVisible(true);
	        
	        
	        
	       // checkMySize();
	        if (getFather() instanceof TCDTClass) {
	            tdp.updateInstances((TCDTClass)(getFather()));
	        }
	        return true;
	    }
	  
	  
	    public void showMessages(boolean b)
	    {
	    	showMessages=b;
	    	
	    }
	    
	    public void setMandatory(boolean c)
	    {
	    	manda=c;
	    }
	    
	    
	    
	  public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
		   
		  if(showMessages) showMsgs = new JMenuItem("hide messages");
	        else showMsgs = new JMenuItem("show messages");
	        //Added by Solange
	        if(manda) mandatory = new JMenuItem("Contingency : OPTIONAL");
	        else mandatory = new JMenuItem("Contingency : MANDATORY");
	        
	       componentMenu.addSeparator();
	       JMenuItem rotate = new JMenuItem("rename");
	       rotate.addActionListener(this);
	       componentMenu.add(rotate);
	  
	       
	       componentMenu.addSeparator();
	       
	       
	       showMsgs.addActionListener(this);
	       componentMenu.add(showMsgs);
	  
	       componentMenu.addSeparator();
	      
	       mandatory.addActionListener(this);
	       componentMenu.add(mandatory);
	  
	  }
	  
	    public void checkMySize() {
	        calculateMyDesiredSize();
	        //System.out.println("I check my size");
	 
	        
	        TGComponent tgc = getTopFather();
	        
	        if (tgc != null) {
	            tgc.recalculateSize();
	        }
	        
	        if (myG == null) {
	            myG = tdp.getGraphics();
	        }
	        
	    }
	  
	  
	  public void calculateMyDesiredSize() {
	        if (myG == null) {
	            myG = tdp.getGraphics();
	        }
	        
	        if (myG == null) {
	            return;
	        }
	        
	        if ((myMessages.size() == 0) || (!showMessages)) {
	            //System.out.println("Min resize" + toString());
	            minDesiredWidth = minWidth;
	            minDesiredHeight = minHeight;
	            return;
	        }
	        
	        lastVisible = showMessages;
	        //System.out.println("Regular resize" + toString());
	        int desiredWidth = minWidth;
	        int h = myG.getFontMetrics().getHeight();
	        int desiredHeight =  Math.max(minHeight, h * (myMessages.size() -1) + minHeight);
	        
	        TAttribute a;
	        for(int i=0; i<myMessages.size(); i++) {
	            a = (TAttribute)(myMessages.elementAt(i));
	            desiredWidth = Math.max(desiredWidth,  myG.getFontMetrics().stringWidth(a.toString()) + 2 * textX);
	        }
	        
	        minDesiredWidth = desiredWidth;
	        minDesiredHeight = desiredHeight;
	    }
	  
	  

	  
	  public void actionPerformed(ActionEvent e)
	    {
	 	if (e.getActionCommand().equals("rename")) 
	 		{
	 		String oldValue = value;
	        String text = getName() + ": ";
	        if (hasFather()) {
	           text=this.getFather().getValue()+" / "+text;
	        }
	        String s = (String)JOptionPane.showInputDialog(tdp, text,
	        "interface name", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
	        null,
	        getValue());
	        
	        if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
	            if (!TAttribute.isAValidId(s, false, false)) {
	                JOptionPane.showMessageDialog(tdp,
	                "Could not change the name of the Interface: the new name is not a valid name",
	                "Error",
	                JOptionPane.INFORMATION_MESSAGE);
	            }
	            setValue(s);
	        }
	 	}
	  
	    
	 	if (e.getActionCommand().equals("show messages"))
	 	{
	 		this.showMessages(true);
            showMsgs.setText("hide messages");	 		
	 	}
	 	
	 	if (e.getActionCommand().equals("hide messages"))
	 	{
	 		this.showMessages(false);
            showMsgs.setText("show messages");	 		
	 	}
	 	
	 	if (e.getActionCommand().equals("Contingency : MANDATORY"))
	 	{
	 		this.setMandatory(true);
            mandatory.setText("Contingency : OPTIONAL");	 		
	 	}
	 	
	 	if (e.getActionCommand().equals("Contingency : OPTIONAL"))
	 	{
	 		this.setMandatory(false);
            mandatory.setText("Contingency : MANDATORY");	 		
	 	}
	    }

	  
	  
	  
	  
	  protected String translateExtraParam() {
	        //Changed from TAttribute all over the file, by Solange
		    TAttribute a;
			        
	        StringBuffer sb = new StringBuffer("<extraparam>\n");
            //Added because Mandatory is a property of the all Interface, by Solange
	        sb.append("<Mandatory value=\"");
	        if (manda) sb.append("1");
	         else sb.append("0");
	        sb.append("\" />\n");
	        
	        sb.append("<Show value=\"");
	        if (shome) sb.append("1");
	         else sb.append("0");
	        sb.append("\" />\n");
	        
            
	        for(int i=0; i<myMessages.size(); i++) {
	            //System.out.println("Attribute:" + i);
                //Changed from TAttribute, by Solange
	            a = (TAttribute)(myMessages.elementAt(i));
	            //System.out.println("Attribute:" + i + " = " + a.getId());        
	            sb.append("<Attribute access=\"");
	            sb.append(a.getAccess());
	            sb.append("\" id=\"");
	            sb.append(a.getId());
	            sb.append("\" type=\"");
	            sb.append(a.getType());
	            sb.append("\" typeOther=\"");
	            sb.append(a.getTypeOther());	           
	            sb.append("\" />\n");
	        }
	        
	        sb.append("<ShowMessages visible=\"");
	        if (showMessages) sb.append("1");
	         else sb.append("0");
	        sb.append("\" />\n");
	        
	        sb.append("</extraparam>\n");
	        return new String(sb);
	    }
	  
	  public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
	        try {
	            NodeList nli;
	            Node n1, n2;
	            Element elt;
	            int access, type;
	            String typeOther;
	            String id, valueAtt;
	            
	        //    System.out.println("Loading attributes interface");
	        //    System.out.println(nl.toString());
	            
	            for(int i=0; i<nl.getLength(); i++) {
	                n1 = nl.item(i);
	                //System.out.println(n1);
	                if (n1.getNodeType() == Node.ELEMENT_NODE) {
	                    nli = n1.getChildNodes();
	                    for(int j=0; j<nli.getLength(); j++) {
	                        n2 = nli.item(j);
	                        //System.out.println(n2);
	                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
	                            elt = (Element) n2;
	                            
	                            //Added by Solange
	                            if (elt.getTagName().equals("Mandatory")) {
		                              int m = Integer.decode(elt.getAttribute("value")).intValue();
		                               if (m==1) manda=true;
		                               else manda=false;
	                            }
	                
	                            if (elt.getTagName().equals("Show")) {
		                              int m = Integer.decode(elt.getAttribute("value")).intValue();
		                               if (m==1) shome=true;
		                               else shome=false;
	                            }
	                
	                            
	                            if (elt.getTagName().equals("Attribute")) {
	                                //System.out.println("Analyzing attribute");
	                                access = Integer.decode(elt.getAttribute("access")).intValue();
	                                type = Integer.decode(elt.getAttribute("type")).intValue();

	                                try {
	                                    typeOther = elt.getAttribute("typeOther");
	                                } catch (Exception e) {
	                                    typeOther = "";
	                                }
	                                id = elt.getAttribute("id");
	                                valueAtt = "";
	                                
	                                if (valueAtt.equals("null")) {
	                                    valueAtt = "";
	                                }
	                                if ((TAttribute.isAValidId(id, false, false)) && (TAttribute.isAValidInitialValue(type, valueAtt))) {
	                                    //System.out.println("Adding attribute " + id + " typeOther=" + typeOther);
	                                	
	                                    TAttribute ta = new TAttribute(access, id, valueAtt, type, typeOther);
	                                    myMessages.addElement(ta);
	                                }
	                            }
	                            if (elt.getTagName().equals("ShowMessages")) {
	                              int visible = Integer.decode(elt.getAttribute("visible")).intValue();
	                               if (visible==1) showMessages(true);
	                               else showMessages(false);
	                            }
	                            
	                        
	                        
	                        }
	                    }
	                }
	            }
	            
	        } catch (Exception e) {
	            throw new MalformedModelingException();
	        }
	        
	    }
	    // Main Tree

	public boolean isMandatory() {
		return manda;
	}

	public void setManda(boolean manda) {
		this.manda = manda;
	}

	public JMenuItem getMandatory() {
		return mandatory;
	}

	public void setMandatory(JMenuItem mandatory) {
		this.mandatory = mandatory;
	}
	
	  public void myActionWhenRemoved()
	  {
		  removedFromPanel=true;
		  
	  }

	  public boolean isRemovedFromPanel()
	  {
		  return removedFromPanel;
	  }
	  
	  public boolean equals(ProCSDInterface in)
	  {
		  	  
		  if (!this.getValue().equals(in.getValue()))
			  return false;
		  if (!this.manda==in.isMandatory())
			  return false;
		  if (!myMessages.equals(in.getMyMessages()))
			  return false;
		  
		  
		  return true;
	  }
	  
	  public TGComponent isOnOnlyMe(int x1, int y1) {
	    if (shome==false)
	    {
	    	return null;
	    }
	  return super.isOnOnlyMe(x1,y1);
	  }
	  
	  

		  
	  
}