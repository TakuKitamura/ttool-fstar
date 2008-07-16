/**Copyright or � or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

 ludovic.apvrille AT enst.fr

 This software is a computer program whose purpose is to allow the 
 edition of TURTLE analysis, design and deployment diagrams, to 
 allow the generation of RT-LOTOS or Java code from this diagram, 
 and at last to allow the analysis of formal validation traces 
 obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
 from INRIA Rhone-Alpes.

 This software is governed by the CeCILL  license under French law and
 abiding by the rules of distribution of free software.  You can  use, 
 modify and/ or redistribute the software under the terms of the CeCILL
 license as circulated by CEA, CNRS and INRIA at the following URL
 "http://www.cecill.info". 

 As a counterpart to the access to the source code and  rights to copy,
 modify and redistribute granted by the license, users are provided only
 with a limited warranty  and the software's author,  the holder of the
 economic rights,  and the successive licensors  have only  limited
 liability. 

 In this respect, the user's attention is drawn to the risks associated
 with loading,  using,  modifying and/or developing or reproducing the
 software by the user in light of its specific status of free software,
 that may mean  that it is complicated to manipulate,  and  that  also
 therefore means  that it is reserved for developers  and  experienced
 professionals having in-depth computer knowledge. Users are therefore
 encouraged to load and test the software's suitability as regards their
 requirements in conditions enabling the security of their systems and/or 
 data to be ensured and,  more generally, to use and operate it in the 
 same conditions as regards security. 

 The fact that you are presently reading this means that you have had
 knowledge of the CeCILL license and that you accept its terms.

 /**
 * Class ProCSDComponent
 * Node. To be used in Proactive Composite Structure Diagram
 * Creation: 10/07/2006
 * @version 1.0 07/08/2006
 * @author Ludovic APVRILLE, Emil Salageanu
 * @see
 */

package ui.procsd;

import java.awt.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;


import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import myutil.*;
import ui.*;

import ui.cd.TCDTClass;
import ui.cd.TCDTObject;
import ui.oscd.TOSClass;
import ui.prosmd.ProSMDStartState;
import ui.prosmd.ProSMDStopState;
import ui.prosmd.ProSMDSubmachine;
import ui.prosmd.ProactiveSMDPanel;
import ui.tmlcd.TMLTaskOperator;
import ui.window.*;

public class ProCSDComponent extends TGCWithInternalComponent implements
		SwallowTGComponent, SwallowedTGComponent, ActionListener {

	private int textY2 = 30;

	
	private Font bigFont;

	private Vector myAttributes;

	private boolean showAttributes = false;
	

	
	private int newPortCode=0;
	
	//private ProActiveCompSpecificationCSDPanel myDesignPanel;
	private String myDesignPanel;
	
	private boolean marked;
	
//  Edited by PV - BEGIN
	private ProactiveSMDPanel mySMD = null;
	// Edited by PV - END

	
	public ProCSDComponent(int _x, int _y, int _minX, int _maxX, int _minY,
			int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
		super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

		width = 150;
		height = 100;
		minWidth = 30;
		minHeight = 30;
		maxWidth = 10000;
		maxHeight = 20000;

		addTGConnectingPointsComment();

		nbInternalTGComponent = 0;

		moveable = true;
		editable = true;
		removable = true;
		userResizable = true;

		name = "Component ";
		value = "ID";

		myImageIcon = IconManager.imgic700;
		myAttributes = new Vector();
		bigFont = new Font("big Font", Font.BOLD, 16);
	}

	
	public void setAttributes(Collection attribs)
	{
		myAttributes=new Vector(attribs);
	}

	public String getNewPortCode()
	{
		newPortCode++;
		return this.value+newPortCode;
	}
	
	public void internalDrawing(Graphics g) {
		
	
		
		  if (this.x<=0)
			  this.x=1;
		  if (this.y<=0)
			  this.y=1;
		
				this.updateMembrane();
				
		
		
	
		
		if (getThisCompDesign()!=null)
		{
			g.setColor(Color.LIGHT_GRAY);
		    g.fill3DRect(x,y,width,height,false);
		    g.setColor(Color.WHITE);
		    g.fill3DRect(x+3,y+3,width-6,height-6,true);
		    g.setColor(Color.BLACK);
		
			
			
		}
		else{
		g.drawRect(x, y, width, height);
		}
		// g.setColor(Color.LIGHT_GRAY);

		// g.fill3DRect(x+2,y+2,width-2,height-2,false);
		// g.setColor(Color.BLACK);
		// String
		
		//Added by Solange for the icon
		g.drawImage(IconManager.img1, x + width-22, y + 3, Color.WHITE, null);
		
		
		if (this.getMySMD()!=null)
		{
			g.drawImage(IconManager.imgic18.getImage(),x+width-44,y+3,Color.WHITE,null);
		}
		
		//
		int w = g.getFontMetrics().stringWidth(value);

		Font f = g.getFont();

		if (((ProactiveCSDPanel) tdp).getMyFont() == ProactiveCSDPanel.BIG_FONT)
		 {
	    	g.setFont(bigFont);
		    g.drawString(value, x + (width - w) / 2, y + textY2);
		    g.setFont(f);
		 }
		else 	
			g.drawString(value, x + (width - w) / 2, y + textY2);
	}

	public TGComponent isOnOnlyMe(int _x, int _y) {

		if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
			return this;
		}
		return null;
	}

	/*
	 * computes the aria this component can move into, depending on it's father
	 * size
	 */
	public void resizeWithFather() {
		if ((father != null) && (father instanceof ProCSDComponent)) {
			// System.out.println("cdRect comp");
			setCdRectangle(0, father.getWidth() - getWidth(), 0, father
					.getHeight()
					- getHeight());
			// setCd(Math.min(x, father.getWidth() - getWidth()), Math.min(y,
			// father.getHeight() - getHeight()));
			setMoveCd(x, y);
		}

	}

	public String getComponentID() {
		return value;
	}

	public String getParentsNamesValues() {
		if (hasFather() && (getFather()) instanceof ProCSDComponent)
			return ((ProCSDComponent) getFather()).getParentsNamesValues()
					+ getFather().getValue() + " / ";
		else
			return "";

	}

	public boolean editOndoubleClick(JFrame frame) {	
		//if this comp is specified in a ProActiveCompSpecPanel 
		//we select this panel
		ProActiveCompSpecificationCSDPanel specPanel=this.getMyDesignPanel();
		if (specPanel!=null) 
		{
			tdp.getGUI().selectTab(specPanel);
			return true;
		}
		//if this comp has a state machine
		//we select it's Panel
		
		ProactiveSMDPanel proSMDPanel=this.getMySMD();
		if (proSMDPanel!=null)
		{
			tdp.getGUI().selectTab(proSMDPanel); 
		    return true;
		}				    				
		return false;
	}

	
	public int getType() {
		return TGComponentManager.PROCSD_COMPONENT;
	}
		
	public void addSwallowedTGComponent(TGComponent tgc, int x, int y) {
		// System.out.println("Add swallow component");
		// Choose its position

		// Make it an internal component
		// It's one of my son
		tgc.setFather(this);
		tgc.setDrawingZone(true);

		// Set its coordinates
		if (tgc instanceof ProCSDComponent) {
			((ProCSDComponent) tgc).resizeWithFather();
		}

		// else unknown*/

		// add it
		addInternalComponent(tgc, 0);
	}

	public void removeSwallowedTGComponent(TGComponent tgc) {
		removeInternalComponent(tgc);
	}

	public Vector getComponentList() {
		Vector v = new Vector();
		for (int i = 0; i < nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof ProCSDComponent) {
				v.add(tgcomponent[i]);
			}
		}
		return v;
	}

	public Vector<ProCSDPort> getPortsList() {
		Vector<ProCSDPort> v = new Vector<ProCSDPort>();
		for (int i = 0; i < nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof ProCSDPort) {
				v.add((ProCSDPort)tgcomponent[i]);
			}
		}
		return v;
	}

	
	
	public void hasBeenResized() {
		this.resizeWithFather();
		for (int i = 0; i < nbInternalTGComponent; i++) {
			tgcomponent[i].resizeWithFather();

		}

	}

	public void setValue(String v) {
		String oldValue = value;
		value = v;
		ProactiveDesignPanel pd = (ProactiveDesignPanel) tdp.getGUI()
				.getCurrentTURTLEPanel();
		ProactiveSMDPanel psmdp = pd.getSMDPanel(oldValue);

		// if there is a state machine coresponding to this component, we also
		// change the name of this machine
		if (psmdp != null) {
			pd.renamePanel(psmdp, v);
		}
		repaint = true;

	}

	/*
	 * set the font type for instance can be normal or big
	 */
	

	public LinkedList getMyAttributes() {
		return new LinkedList(myAttributes);
	}

	// Attributes will be seen on the screen. Unimplemented
	public void showAttributes(boolean show) {
		showAttributes = show;
	}

	public void addActionToPopupMenu(JPopupMenu componentMenu,
			ActionListener menuAL, int x, int y) {

		
		
		
		componentMenu.addSeparator();
		JMenuItem ren = new JMenuItem("rename");
		ren.addActionListener(this);
		componentMenu.addSeparator();
		JMenuItem sm = new JMenuItem("create state machine");
		sm.addActionListener(this);
		
		JMenuItem compDesign = new JMenuItem("design component in a new diagram");
		compDesign.addActionListener(this);
		
		JMenuItem compChooseDesign = new JMenuItem("chooose digram for this comp design");
		compChooseDesign.addActionListener(this);
		
		JMenuItem attr = new JMenuItem("edit attributes");
		attr.addActionListener(this);
		
		componentMenu.addSeparator();
		
		JMenuItem saveasLib = new JMenuItem("save as library");
        saveasLib.addActionListener(this);
        componentMenu.add(saveasLib);
        
        JMenuItem addSubComp = new JMenuItem("add subcomponent from library");
        addSubComp.addActionListener(this);
        componentMenu.add(addSubComp);
        
        componentMenu.addSeparator();
        JMenuItem addCutComp = new JMenuItem("cut");
        addCutComp.addActionListener(this);
        componentMenu.add(addCutComp);
        
        JMenuItem addCopyComp = new JMenuItem("copy");
        addCopyComp.addActionListener(this);
        componentMenu.add(addCopyComp);
        
        
        JMenuItem addPasteSubComp = new JMenuItem("paste");
        addPasteSubComp.addActionListener(this);
        componentMenu.add(addPasteSubComp);
        
        componentMenu.addSeparator();
        
    
		componentMenu.add(ren);
		componentMenu.add(sm);
		componentMenu.add(compDesign);
		componentMenu.add(compChooseDesign);
		
		componentMenu.add(attr);

		componentMenu.addSeparator();
        
		JMenuItem print = new JMenuItem("pretty print");
        print.addActionListener(this);
        componentMenu.add(print);
        
        JMenuItem update = new JMenuItem("update membrane");
        update.addActionListener(this);
        componentMenu.add(update);
        
//      Edited by PV - BEGIN
		componentMenu.addSeparator();
		JMenuItem setAsActive = new JMenuItem("Set as Active");
		setAsActive.addActionListener(this);
		componentMenu.add(setAsActive);
	 	// Edited by PV - END
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("rename")) {
			String oldValue = value;
			String text = getName() + ": ";
			if (hasFather()) {
				// text = getTopLevelName() + " / " + text;
				// text=getTopFather().getValue() + " / " + text;
				text = getParentsNamesValues() + text;
			}
			String s = (String) JOptionPane.showInputDialog(tdp, text,
					"setting value", JOptionPane.PLAIN_MESSAGE,
					IconManager.imgic101, null, getValue());

			if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
			
				if (!TAttribute.isAValidId(s, false, false)) {
					JOptionPane
							.showMessageDialog(
									tdp,
									"Could not change the name of the Component: the new name is not a valid name",
									"Error", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				setValue(s);
			}

		}

		if (e.getActionCommand().equals("create state machine")) {
			tdp.getGUI().createProActiveSMD(tdp.tp, value);
			tdp.getGUI().selectTab(value);

		}
		
		if (e.getActionCommand().equals("design component in a new diagram")) {
			if (!this.editOndoubleClick(this.tdp.getGUI().frame))
			{
			ProactiveDesignPanel pdp=(ProactiveDesignPanel)tdp.getGUI().getCurrentTURTLEPanel();
			ProActiveCompSpecificationCSDPanel myPanel=pdp.addProActiveCompSpecificationPanel(this.value);
			
			ProCSDComponent designComp=myPanel.createBlackBoxComp(this);
			
			
			
		//	myPanel.addBuiltComponent(designComp);
			this.setMyDesignPanel(this.value);
			}
			//tdp.getGUI().selectTab(value);
		}

		if (e.getActionCommand().equals("update membrane")) {
			this.updateMembrane();
		}

		
		
		if (e.getActionCommand().equals("chooose digram for this comp design")) {
			
			ProactiveDesignPanel pdp=(ProactiveDesignPanel)tdp.getGUI().getCurrentTURTLEPanel();
			
			String s = (String) JOptionPane.showInputDialog(tdp, "Choose a component diagram",
					"Diagram name", JOptionPane.PLAIN_MESSAGE,
					IconManager.imgic101, null, "");

			//TODO : verify valid diagram
			if ((s != null) && (s.length() > 0) ) 
			{
			if (true) {
					JOptionPane
							.showMessageDialog(
									tdp,
									"In Construction",
									"Error", JOptionPane.INFORMATION_MESSAGE);
					//return;
				}
			this.setMyDesignPanel(s);
			//ProActiveCompSpecificationCSDPanel myPanel=pdp.addProActiveCompSpecificationPanel(s);
			}
			
			
			
			
			
			
			
			//tdp.getGUI().selectTab(value);
		}
		
		if (e.getActionCommand().equals("edit attributes")) {
			JDialogAttribute dialog = new JDialogAttribute(myAttributes,
					new Vector(), tdp.getGUI().getFrame(),
					"Setting attributes for this component", "Attribute");
			dialog.addAccess("-");
			dialog.addType("Natural");
			// must see in GProactiveDesign for bool params
			// dialog.addType("Boolean");
			dialog.enableInitialValue(true);
			dialog.setSize(650, 375);

			GraphicLib.centerOnParent(dialog);
			//dialog.show(); // blocked until dialog has been closed
              dialog.setVisible(true);
		}

		
		
		 if (e.getActionCommand().equals("save as library")) {
		   
			 tdp.getGUI().gtm.generateLists((ProactiveDesignPanel)tdp.tp);
			 ((ProactiveCSDPanel)tdp).selectComponents(false);	 
	       selectComponent(true);
		   this.tdp.saveAsLibrary();
		   ((ProactiveCSDPanel)tdp).selectComponents(false);	 
		 }
		 
		
		
		 if (e.getActionCommand().equals("copy")) {
			 tdp.getGUI().gtm.generateLists((ProactiveDesignPanel)tdp.tp);
			 ((ProactiveCSDPanel)tdp).selectComponents(false);
			   selectComponent(true);
			   this.tdp.makeCopy();
			   ((ProactiveCSDPanel)tdp).selectComponents(false);	 
		 }
		 
		 if (e.getActionCommand().equals("cut")) {
			 tdp.getGUI().gtm.generateLists((ProactiveDesignPanel)tdp.tp);
			 ((ProactiveCSDPanel)tdp).selectComponents(false); 
			 selectComponent(true);
			   this.tdp.makeCut();
			   this.tdp.removeComponent(this);
			   ((ProactiveCSDPanel)tdp).selectComponents(false);	 
		       
		 }
		
		 if (e.getActionCommand().equals("pretty print")) {
			 System.out.println(this.prettyPrint());  
		 }
		
		 
		if (e.getActionCommand().equals("add subcomponent from library"))
			{
			((ProactiveCSDPanel)tdp).setTopFather(this);
			
			tdp.insertLibrary(0,0);
			
			((ProactiveCSDPanel)tdp).setTopFather(null);
			tdp.getGUI().gtm.generateLists((ProactiveDesignPanel)tdp.tp);
			}

		
		if (e.getActionCommand().equals("paste"))
		{
			
			((ProactiveCSDPanel)tdp).setTopFather(this);
		
			tdp.makePaste(0,0);
		
		((ProactiveCSDPanel)tdp).setTopFather(null);
		tdp.getGUI().gtm.generateLists((ProactiveDesignPanel)tdp.tp);
		}
	
		
		if (e.getActionCommand().equals("Set as Active")) {
			setAsActive();
		}
		 
	}
	
	
	
	/*
	 * Creates a component who is the black box of this component
	 * The new Component will have the ports of this comp with their Interfaces
	 * and the attributes of this component.
	 * 
	 */
	/*
	public ProCSDComponent createBlackBoxComp(TDiagramPanel panel)
	{
		ProCSDComponent designComp=new ProCSDComponent(this.getX(),this.getY(),this.getMinHeight(),this.getMaxHeight(),this.getMinWidth(),this.getMaxHeight(),false,null,panel);
		
		
		designComp.setValue(this.value+"Design");
		
		
		Vector<ProCSDPort> ports=this.getPortsList();
		for (int k=0;k<ports.size();k++)
		{
			ProCSDPort p=ports.get(k);
			ProCSDPort newPort=null;
			if (p instanceof ProCSDInPort)
					newPort=new ProCSDInPort(p.getX(),p.getY(),p.getMinHeight(),p.getMaxHeight(),p.getMinWidth(),p.getMaxWidth(),false,designComp,panel);
			else
				if (p instanceof ProCSDOutPort)
					newPort=new ProCSDOutPort(p.getX(),p.getY(),p.getMinHeight(),p.getMaxHeight(),p.getMinWidth(),p.getMaxWidth(),false,designComp,panel);
					
			if (newPort!=null)
			{
				newPort.setValue(p.getValue());
				designComp.addSwallowedTGComponent(newPort,newPort.getX(),newPort.getY());
				
				ProCSDInterface pI=p.getMyInterface();
				if (pI!=null)
				{
					ProCSDInterface newInterface = new ProCSDInterface(pI.getX(),pI.getY(),pI.getMinHeight(),pI.getMaxHeight(),pI.getMinWidth(),pI.getMaxWidth(),false,null,panel);
					newInterface.setValue(pI.getValue());
					newInterface.setManda(pI.isMandatory());
					newInterface.setMessages(pI.getMyMessages());
					TGConnectingPoint point1=newPort.getTGConnectingPointAtIndex(0);
					TGConnectingPoint point2=newInterface.getTGConnectingPointAtIndex(0);
					TGConnectorPortInterface connector=new TGConnectorPortInterface(0,0,0,0,0,0,false,null,panel,point1,point2,new Vector());
					panel.addBuiltComponent(newInterface);
					panel.addBuiltComponent(connector);										
				}//if interface !=null								
			}//if newPort!=null	
		}
	
		
		designComp.setAttributes(this.myAttributes);
		
		panel.addBuiltComponent(designComp);
		
		
		
		
		return designComp;
	}
	
	*/

	/*
	 * public void myActionWhenRemoved() {
	 * 
	 * 
	 * 
	 * ProactiveDesignPanel pd = (ProactiveDesignPanel)
	 * tdp.getGUI().getCurrentTURTLEPanel(); ProactiveSMDPanel
	 * psmdp=pd.getSMDPanel(value);
	 * 
	 * if (psmdp!=null) { pd.renamePanel(psmdp,value+"_DELETED"); }
	 *  // ProactiveDesignPanel pd = (ProactiveDesignPanel)
	 * tdp.getGUI().getCurrentTURTLEPanel(); //marche pas:
	 * pd.removeSMDPanel(value);
	 * 
	 *  }
	 */

	protected String translateExtraParam() {
		TAttribute a;

		StringBuffer sb = new StringBuffer("<extraparam>\n");
		for (int i = 0; i < myAttributes.size(); i++) {
			// System.out.println("Attribute:" + i);
			a = (TAttribute) (myAttributes.elementAt(i));
			// System.out.println("Attribute:" + i + " = " + a.getId());

			sb.append("<Attribute access=\"");
			sb.append(a.getAccess());
			sb.append("\" id=\"");
			sb.append(a.getId());
			sb.append("\" type=\"");
			sb.append(a.getType());
			sb.append("\" initialValue=\"");
			sb.append(a.getInitialValue());

			sb.append("\" typeOther=\"");
			sb.append(a.getTypeOther());
			sb.append("\" />\n");
		}

		sb.append("<ShowAttributes visible=\"");
		if (showAttributes)
			sb.append("1");
		else
			sb.append("0");
		sb.append("\" />\n");

		if (myDesignPanel!=null)
		sb.append("<DesignPanel name=\""+myDesignPanel+"\"/>");
		
		sb.append("<LastPortCode code=\"");
        sb.append(newPortCode);
        sb.append("\" />\n");
		        
		sb.append("</extraparam>\n");
		return new String(sb);
	}

	public void loadExtraParam(NodeList nl, int decX, int decY, int decId)
			throws MalformedModelingException {
		try {
			NodeList nli;
			Node n1, n2;
			Element elt;
			int access, type;
			String typeOther;
			String id, valueAtt;

			// Ruteo, by Solange
			// System.out.println("Loading attributes");
			// System.out.println(nl.toString());
            //
			 
			for (int i = 0; i < nl.getLength(); i++) {
				n1 = nl.item(i);
				// System.out.println(n1);
				if (n1.getNodeType() == Node.ELEMENT_NODE) {
					nli = n1.getChildNodes();
					for (int j = 0; j < nli.getLength(); j++) {
						n2 = nli.item(j);
						// System.out.println(n2);
						if (n2.getNodeType() == Node.ELEMENT_NODE) {
							elt = (Element) n2;
							if (elt.getTagName().equals("Attribute")) {
								// System.out.println("Analyzing attribute");
								access = Integer.decode(
										elt.getAttribute("access")).intValue();
								type = Integer.decode(elt.getAttribute("type"))
										.intValue();
								try {
									typeOther = elt.getAttribute("typeOther");
								} catch (Exception e) {
									typeOther = "";
								}
								id = elt.getAttribute("id");
								valueAtt = elt.getAttribute("initialValue");

								if (valueAtt.equals("null")) {
									valueAtt = "";
								}
								if ((TAttribute.isAValidId(id, false, false))
										&& (TAttribute.isAValidInitialValue(
												type, valueAtt))) {
									// System.out.println("Adding attribute " +
									// id + " typeOther=" + typeOther);
									TAttribute ta = new TAttribute(access, id,
											valueAtt, type, typeOther);
									// ta.setInitialValue(valueAtt);
									myAttributes.addElement(ta);
								}
							}
							if (elt.getTagName().equals("ShowAttributes")) {
								int visible = Integer.decode(
										elt.getAttribute("visible")).intValue();
								if (visible == 1)
									showAttributes(true);
								else
									showAttributes(false);
							}
							
							if (elt.getTagName().equals("DesignPanel")) {
								String dp=elt.getAttribute("name");
								if ((dp!=null)&&(!dp.equals("")))
										myDesignPanel=dp;
							}
							
							
							if (elt.getTagName().equals("LastPortCode")) {
                           	 newPortCode = (new Integer (elt.getAttribute("code"))).intValue();                                
                           }

						}
					}
				}
			}

		} catch (Exception e) {
			throw new MalformedModelingException();
		}

	} //load extra params

		
	
	
	public void selectComponent(boolean value) {
      this.selected=value;
		Vector<ProCSDPort> v = this.getPortsList();
		if ((v!=null)&&(v.size()>0))
		{
	
			for (int i=0;i<v.size();i++)
				{
					ProCSDPort p=v.get(i);
																				
					if (p instanceof ProCSDOutPort)
				       {
							TGConnectorProCSD bindIn= p.getBindingIn();
							   if (bindIn!=null)
							   {
								   bindIn.select(value);								   
							   }
								
						TGConnectorProCSD bindOut= p.getBindingOut();
						if (bindOut!=null)
						{
							if ((bindOut.getMyPort1().getFather()!=this)&& (bindOut.getMyPort1().getFather().isSelected()==value))
							 bindOut.select(value);
						
						if ((bindOut.getMyPort2().getFather()!=this)&& (bindOut.getMyPort2().getFather().isSelected()==value))
							 bindOut.select(value);
						}
						
				       }
				       else
				    	   if (p instanceof ProCSDInPort)
				    	   {
				    		   TGConnectorProCSD bindOut= p.getBindingOut();
							   if (bindOut!=null)
							   {
								   bindOut.select(value);
							   }
				    	
				    	   
							   TGConnectorProCSD bindIn= p.getBindingIn();
						if (bindIn!=null)
						{
							   if ((bindIn.getMyPort1().getFather()!=this)&& (bindIn.getMyPort1().getFather().isSelected()==value))
									 bindIn.select(value);
								
								if ((bindIn.getMyPort2().getFather()!=this)&& (bindIn.getMyPort2().getFather().isSelected()==value))
									 bindIn.select(value);
						}		
				    	   
				    	   
				    	   }
					 ProCSDInterface in=p.getMyInterface();
					 if (in!=null)
					 {
						 in.select(value);
						 in.getMyConnector().select(value);
						 
					 }
				}
			}//if we have ports 
		
		Vector compList=this.getComponentList();
		for (int k=0;k<compList.size();k++)
		{
			ProCSDComponent pcomp=(ProCSDComponent)compList.get(k);
			pcomp.selectComponent(value);
		}
		
		}	
     	
	
	
//	public StringBuffer saveComponentInXML(boolean isTop) {
//        StringBuffer sb = new StringBuffer();
//        if (isTop)
//        	sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
//        
//        sb.append("<PROCSDCOMPONENT type=");
//        sb.append("\""+getType()+"\"");
//        sb.append(" id=");
//        sb.append("\""+getId()+"\"");
//        sb.append(">\n");
//      
//        if ((!isTop)&&(this.getFather()!=null)) {
//            sb.append(translateFatherInformation());
//        }
//        sb.append(translateCDParam());
//        sb.append(translateSizeParam());
//        sb.append(translateCDRectangleParam());
//        sb.append(translateNameValue());
//        sb.append(translateConnectingPoints());
//        sb.append(translateJavaCode());
//        sb.append(translateExtraParam());
//       
//        //we write ports now
//        //for each port we write:
//        //an interface
//        //a connector to this interface
//        //the binding to a subcomponent of this component
//        
//     
//       
//        Vector<ProCSDPort> v = this.getPortsList();
//		if ((v!=null)&&(v.size()>0))
//		{
//			sb.append("<PORTS>\n");
//			for (int i=0;i<v.size();i++)
//				{
//					sb.append("<PORT>\n");					
//					ProCSDPort p=v.get(i);
//					sb.append(p.saveInXML());
//					
//					//the binding of the port
//					 if (p instanceof ProCSDOutPort)
//				       {
//							TGConnectorProCSD bindIn= p.getBindingIn();
//							   if (bindIn!=null)
//							   {
//								   sb.append("<BINDING>\n");
//								   sb.append(bindIn.saveInXML());
//								   bindIn.select(true);
//								   sb.append("</BINDING>\n");
//							   }
//				       }
//				       else
//				    	   if (p instanceof ProCSDInPort)
//				    	   {
//				    		   TGConnectorProCSD bindOut= p.getBindingOut();
//							   if (bindOut!=null)
//							   {
//								   sb.append("<BINDING>\n");
//								   bindOut.saveInXML();
//								   bindOut.select(true);
//								   sb.append("</BINDING>\n");
//							   }
//				    	   }
//					
//					 //the interface associated to this port if there is one
//					 ProCSDInterface in=p.getMyInterface();
//						if (in!=null)
//							{
//							sb.append("<INTERFACE>\n");
//							sb.append(in.saveInXML());
//							//in.select(true);
//							//in.getMyConnector().select(true);
//						    sb.append(in.getMyConnector().saveInXML()+"\n");
//						    sb.append("</INTERFACE>\n");
//							}
//					 
//					sb.append("</PORT>\n");
//				}
//			sb.append("</PORTS> \n");
//		}//if we have ports 	
//     
//      //  sb.append(translateSubComponents());
//        sb.append("<PROCSDSUBCOMPONENTS>\n");
//        Vector comps=this.getComponentList();
//		 for (int k=0;k<comps.size();k++)
//		 {
//			 ProCSDComponent pcomp=(ProCSDComponent)comps.get(k);
//			 sb.append(pcomp.saveComponentInXML(false));
//		 }
//		
//        sb.append("</PROCSDSUBCOMPONENTS>\n");
//		sb.append("</PROCSDCOMPONENT>\n");
//     
//		
//		return sb;
//    }
	
	
//	public String getAllInfo()
//	{
//		if (true) return new String(this.saveComponentInXML(true));
//		String out=new String();
//		String outXML=new String();
//		outXML+=this.saveInXML();
//		
//		out+="<"+super.toString()+"> \n";
//		
//		out+="Ports:\n";
//		Vector<ProCSDPort> v = this.getPortsList();
//		for (int i=0;i<v.size();i++)
//		{
//			ProCSDPort p=v.get(i);
//			out+=p.toString()+"\n";
//			ProCSDInterface in=p.getMyInterface();
//			if (in!=null)
//				{
//				out+="		Interface: "+in.toString()+"\n";
//			    out+="		Connected through "+in.getMyConnector()+"\n";
//				}
//			else 
//				out+=("		No interface connected\n");
//		
//
//	//in order not to save the same binding 2 times
//	//we only consider bindings which goes to subcomponenets
//			//for out port :the in bidings
//			//for in ports the out bindings
//			
//       if (p instanceof ProCSDOutPort)
//       {
//			TGConnectorProCSD bindIn= p.getBindingIn();
//			   if (bindIn!=null)
//			   {
//				   out+="		Binding in: "+bindIn.toString()+"\n";
//			   }
//       }
//       else
//    	   if (p instanceof ProCSDInPort)
//    	   {
//    		   TGConnectorProCSD bindOut= p.getBindingOut();
//			   if (bindOut!=null)
//			   {
//				   out+="		Binding out: "+bindOut.toString()+"\n";
//			   }
//    	   }
//		 out+="<SubComponents>\n";
//		 
//		 Vector comps=this.getComponentList();
//		 for (int k=0;k<comps.size();k++)
//		 {
//			 ProCSDComponent pcomp=(ProCSDComponent)comps.get(k);
//			 out+=pcomp.getAllInfo();
//		 }
//		 
//		 out+="</SubCompoenents>\n";
//		   
//		}
//		out+="</"+super.toString()+"> \n";
//		
//		out+="************XML*********************\n";
//		out+=outXML;
//		
//		
//		return out;
//	}
//	

	
	
	
	public String prettyPrint()
	{
		String out="";
		String diag="this diagram";
		if (this.getMyDesignPanel()!=null) diag=this.getMyDesignPanel().getName();
		if (this.myDesignPanel!=null) diag+=" myDesignPanel var="+myDesignPanel;
		
		out+="   **PROCSDCOMPONENT**\n";
		out+="Name "+this.value+" design diagram: " +diag+"\n";
		out+="---> Ports List \n";
		Vector v=this.getPortsList();
		  for (int i=0;i<v.size();i++)
		  {
			  ProCSDPort port=(ProCSDPort)v.get(i);
			  out+=port.prettyPrint()+"\n";
		  }		
		out+="<-----Ports List \n";
		
		if (getMySMD()!=null)
		out+="My SMD: "+getMySMD().getName()+"\n";
		
		Vector comps=this.getComponentList();
		if (comps.size()>0)
		{
		out+="---->SubComponents of "+this.value+"--->\n";
		  for (int i=0;i<comps.size();i++)
		  {
			  ProCSDComponent cmp=(ProCSDComponent)comps.get(i);
			  out+=cmp.prettyPrint();
		  }
		  out+="<----SubComponents of "+this.value+"<---\n";
		}
		  
		  return out;		
	}


	public ProActiveCompSpecificationCSDPanel getMyDesignPanel() {
		//return myDesignPanel;
	   
		String myPanelName=this.value;
		ProActiveCompSpecificationCSDPanel panel=null;
		if ((this.myDesignPanel!=null)&&(!this.myDesignPanel.equals("")))
		 myPanelName=this.myDesignPanel; 
		panel = ((ProactiveDesignPanel)this.tdp.tp).getCompSpecPanel(myPanelName);
		if (this.tdp==panel)
			return panel=null;
		return panel;
		
	}


	public void setMyDesignPanel(String name) {
		this.myDesignPanel = name;
	}
	
	
	public ProCSDPort getPortByName(String portName)
	 {
	 	Vector<ProCSDPort> ports=this.getPortsList();
				for (int ip=0;ip<ports.size();ip++)
				{
					  ProCSDPort p=ports.get(ip);
					  if (p.getValue().equals(portName))
					  return p;	
				}
	 return null;
	 }
	
	public ProCSDPort getPortByPortCode(String portCode)
	 {
	 	Vector<ProCSDPort> ports=this.getPortsList();
				for (int ip=0;ip<ports.size();ip++)
				{
					  ProCSDPort p=ports.get(ip);
					  if (p.getPortCode().equals(portCode))
					  return p;	
				}
	 return null;
	 }
	
	
	
	
	
/*
 * return the full component  (its a grapghical and logical component)
 * corresponding to this component (the inside of this component)
 */
     public ProCSDComponent getThisCompDesign()
		{
		  if (this.getMyDesignPanel()==null)
		  	return null;
		  else
		   if (this.getMyDesignPanel().getProCSDComponent()==null)
		   return null;
		   else
		    { 	ProCSDComponent compDesign=null;
				compDesign = (ProCSDComponent)(this.getMyDesignPanel().getProCSDComponent());
	            return compDesign;		
	    	}
		}



	public ProactiveSMDPanel getMySMD() {
		return ((ProactiveDesignPanel)this.tdp.tp).getSMDPanel(this.value);
	}



	public void updateMembrane()
	{
	 	ProCSDComponent designComp=this.getThisCompDesign();
		if (designComp==null) return;
		
		//now we consider all ports in compDesign
		Vector<ProCSDPort> portsDesign = designComp.getPortsList();
		for (int k=0;k<portsDesign.size();k++)
		{
			ProCSDPort pDesign=portsDesign.get(k);
			ProCSDPort port=this.getPortByPortCode(pDesign.getPortCode());
			if (port!=null)
			{
				if (!port.myType().equals(pDesign.myType()))
				{
					this.removeInternalComponent(port);
					this.tdp.removeComponent(port);
					port=null;
				}
			}
			if (port==null)
			{
				//we have to create a port and put it to this component
				ProCSDPort p=null;
				
				
				
				//int xDif=this.width-pDesign.getFather().getWidth();
				//int yDif=this.height-pDesign.getFather().getHeight();
				
				int px=this.getX()+pDesign.getX()-pDesign.getFather().getX();
				int py=this.getY()+pDesign.getY()-pDesign.getFather().getY();
				
				if (pDesign instanceof ProCSDInPort)				 
					p=new ProCSDInPort(px,py,pDesign.getMinHeight(),pDesign.getMaxHeight(),pDesign.getMinWidth(),pDesign.getMaxWidth(),true,this,this.tdp);
				else
					p=new ProCSDOutPort(px,py,pDesign.getMinHeight(),pDesign.getMaxHeight(),pDesign.getMinWidth(),pDesign.getMaxWidth(),true,this,this.tdp);
				
				p.setCd(px,py);
				p.setValue(pDesign.getValue());
				p.setPortCode(pDesign.getPortCode());
					//updateMembrane();
				this.addSwallowedTGComponent(p,this.getWidth(),this.getHeight());
			}
			
			
		}
		
		//second weconsider all ports of this comp 
		
		Vector<ProCSDPort> ports = this.getPortsList();
		for (int k=0;k<ports.size();k++)
		{
			ProCSDPort p=ports.get(k);
			ProCSDPort pDesign=designComp.getPortByPortCode(p.getPortCode());
		 	if (pDesign==null)
		 	{
		 		
		 		this.tdp.removeComponent(p);
		 		this.removeInternalComponent(p);
		 	}
		 	else
		 	{
		 		//name has changed
		 		if (!pDesign.getValue().equals(p.getValue()))
		 			p.setValue(pDesign.getValue());
		 		
		 		ProCSDInterface designInterface=pDesign.getMyInterface();
		 		ProCSDInterface pInterface=p.getMyInterface();

		 		
		 		if (designInterface==null)
		 		{
		 			if (pInterface!=null) 
		 				p.setMyInterface(null);
		 			this.tdp.removeComponent(pInterface);
		 		}
		 		else
		 		{//design interface is not null
		 			if (pInterface==null)
		 			{
		 				//We have to add the interface
		 				pInterface=new ProCSDInterface(designInterface,this.tdp);
		 				int Ix=p.getX()+designInterface.getX()-pDesign.getX();
		 				int Iy=p.getY()+designInterface.getY()-pDesign.getY();
		 				
		 				if (Ix<0) Ix=0;
		 				if (Iy<0) Iy=0;
		 				
		 			//	pInterface.setCd(p.getX()+50,p.getY()+50);
		 				pInterface.setCd(Ix,Iy);
		 				this.tdp.addBuiltComponent(pInterface);
		 				p.connectInterface(pInterface);
		 			}
		 			else
		 			{
		 				if (!pInterface.equals(designInterface))
		 				{
		 					pInterface.setValue(designInterface.getValue());
		 					pInterface.setManda(designInterface.isMandatory());
		 					pInterface.setMessages(designInterface.getMyMessages());
		 					//System.out.println("not equals .. ");
		 				}
		 			
		 			}
		 		}	
		 		
		 		
		 	}//pDesign!=null
		}//for all ports
		
	
		
		
	}

	
	
	public void mark(boolean b)
	{
		marked=b;
	}
	
	public boolean isMarked()
	{
		return marked;
	}
	
	
	public void setWidth(int width)
	{
		this.width=width;
		
	}
	
	public void setHeight(int height)
	{
		this.height=height;
	}
	
	// Edited by PV - BEGIN
	public void setAsActive() {
		
		String s = ((ProactiveDesignPanel)tdp.tp).addSMD(value);
		 mySMD = ((ProactiveDesignPanel)tdp.tp).getSMDPanel(s);
		
		
		ProSMDStartState start_state = (ProSMDStartState)TGComponentManager.addComponent(110, 10, TGComponentManager.PROSMD_START_STATE,tdp);
		mySMD.addBuiltComponent(start_state);
		
		
		ProSMDSubmachine initActive_submachine = (ProSMDSubmachine)TGComponentManager.addComponent(100, 70, TGComponentManager.PROSMD_SUBMACHINE,tdp);
		initActive_submachine.setName("initActive");
		initActive_submachine.setValue("initActive");
		initActive_submachine.resize(70, 20);
		mySMD.addBuiltComponent(initActive_submachine);

		
		TGConnectingPoint p1 = start_state.getTGConnectingPointAtIndex(0);
		TGConnectingPoint p2 = initActive_submachine.getTGConnectingPointAtIndex(0);
		TGConnector conn01 = TGComponentManager.addConnector(p1.getX(), p2.getY(), TGComponentManager.CONNECTOR_PROSMD, tdp, p1, p2, new Vector());
		mySMD.addBuiltConnector(conn01);
		
		
		ProSMDSubmachine runActive_submachine = (ProSMDSubmachine)TGComponentManager.addComponent(100, 110, TGComponentManager.PROSMD_SUBMACHINE,tdp);
		runActive_submachine.setName("runActive");
		runActive_submachine.setValue("runActive");
		runActive_submachine.setAs("fifo");
		runActive_submachine.resize(70, 20);
		mySMD.addBuiltComponent(runActive_submachine);
		
		
		p1 = initActive_submachine.getTGConnectingPointAtIndex(1);
		p2 = runActive_submachine.getTGConnectingPointAtIndex(0);
		TGConnector conn02 = TGComponentManager.addConnector(p1.getX(), p2.getY(), TGComponentManager.CONNECTOR_PROSMD, tdp, p1, p2, new Vector());
		mySMD.addBuiltConnector(conn02);
		
		
		ProSMDSubmachine endActive_submachine = (ProSMDSubmachine)TGComponentManager.addComponent(100, 170, TGComponentManager.PROSMD_SUBMACHINE,tdp);
		endActive_submachine.setName("endActive");
		endActive_submachine.setValue("endActive");
		endActive_submachine.resize(70, 20);
		mySMD.addBuiltComponent(endActive_submachine);
		
		
		p1 = runActive_submachine.getTGConnectingPointAtIndex(1);
		p2 = endActive_submachine.getTGConnectingPointAtIndex(0);
		TGConnector conn03 = TGComponentManager.addConnector(p1.getX(), p2.getY(), TGComponentManager.CONNECTOR_PROSMD, tdp, p1, p2, new Vector());
		mySMD.addBuiltConnector(conn03);
		
		
		ProSMDStopState stop_state = (ProSMDStopState)TGComponentManager.addComponent(110, 240, TGComponentManager.PROSMD_STOP_STATE,tdp);
		mySMD.addBuiltComponent(stop_state);
		
		
		p1 = endActive_submachine.getTGConnectingPointAtIndex(1);
		p2 = stop_state.getTGConnectingPointAtIndex(0);
		TGConnector conn04 = TGComponentManager.addConnector(p1.getX(), p2.getY(), TGComponentManager.CONNECTOR_PROSMD, tdp, p1, p2, new Vector());
		mySMD.addBuiltConnector(conn04);
		
	}
	
	// Edited by PV - END
}
