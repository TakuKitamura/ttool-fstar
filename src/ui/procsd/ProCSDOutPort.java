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
 * Class ProCSDPort
 * Node. To be used in Proactive Composite Structure Diagram
 * Creation: 11/07/2006
 * @version 1.0 11/07/2006
 * @author Ludovic APVRILLE, Emil Salageanu
 * @see
 */

package ui.procsd;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;

public class ProCSDOutPort extends ProCSDPort implements SwallowedTGComponent, ActionListener {
   // private int textY1 = 15;
      private int textY2 = 15;
      private int position=4; // for rotate. must be saved
    //private int derivationx = 20;
    //private int derivationy = 30;
      int interfaceLength=20;
      // Added by Solange
      public boolean temp;

      public ProCSDOutPort(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 20;
        height = 20;
        minWidth = 5;
        minHeight = 5;
         
        //addTGConnectingPointsComment();
        
         nbConnectingPoint = 4;
         connectingPoint = new TGConnectingPointProCSD[nbConnectingPoint];
       
        //up point
        // connectingPoint[0] = new TGConnectingPointProSMD(this, -height/2, width/2, true, true, 0.5, 0.0);
        // connectingPoint[1] = new TGConnectingPointProSMD(this, height/2, 0, true, true, 0.5, 0.0);
        // connectingPoint[2] = new TGConnectingPointProSMD(this, height/2, width, true, true, 0.5, 0.0);
        // connectingPoint[3] = new TGConnectingPointProSMD(this, height, width/2, true, true, 0.5, 0.0);
        //addTGConnectingPointsComment();
         connectingPoint[0] = new TGConnectingPointProCSD(this, 0, 0, true, true, 0.5, 0.0,this);
         connectingPoint[1] = new TGConnectingPointProCSD(this, 0, width, true, true, 0.5, 0.0,this);
         connectingPoint[2] = new TGConnectingPointProCSD(this, -height/2,width/2, true, true, 0.5, 0.0,this);
         connectingPoint[3] = new TGConnectingPointProCSD(this, height/2,width/2, true, true, 0.5, 0.0,this);
         
        moveable = true;
        editable = true;
        removable = true;
        userResizable = false;
        
        name = "Out Port";
        value = "p";
        
        //Delegate port image removed, I put another for not having an error here. By Solange
        myImageIcon = IconManager.imgic2104;
    }
    
    public void internalDrawing(Graphics g) {
        g.setColor(Color.BLUE);
      	g.drawRect(x, y, width, height);
     	
  /* Do not draw the line and the Oval, by Solange  
        if (position==1)
        {
        	g.drawLine(x+width,y+height/2,x+2*width,y);
        	//g.drawLine(x+width,y+height/2,x+2*width,y+height);
        	g.fillOval(x+2*width-5,y-5,10,10);
            //g.drawArc(x+2*width,y+height,10,10,45,180);
        }
        else if (position==2)
        {
        	g.drawLine(x+width/2,y+height,x,y+2*height);
          //g.drawLine(x+width/2,y+height,x+width,y+2*height);
        	g.fillOval(x-5,y+2*height-5,10,10);
          //  g.drawArc(x+width,y+2*height,10,10,45,180);
        } 	
        
        else if (position==3)
        {
        	g.drawLine(x,y+height/2,x-width,y);
          //g.drawLine(x,y+height/2,x-width,y+height);
        	g.fillOval(x-width-5,y-5,10,10);
           // g.drawArc(x-width-10,y+height,10,10,-45,180);
        } 	
        
        
        else if (position==4)
        {
        	g.drawLine(x+width/2,y,x,y-height);
           //g.drawLine(x+width/2,y,x+width,y-height);
        	g.fillOval(x-5,y-height-5,10,10);
           // g.drawArc(x+width-5,y-height-10,10,10,-180,180);
        } 	
        
        */
        
        	g.setColor(Color.LIGHT_GRAY);
        	if(hidden)
    		{
    		 g.fillRect(x+1, y+1, width-1, height-1);
    		 //Draw an icon when the interface is hidden, by Solange
    		 //g.drawImage(IconManager.img0, x + width - 20, y + 3, Color.CYAN, null);
    		 g.drawImage(IconManager.img0, x + width - 19, y+1, Color.LIGHT_GRAY, null);
    		}
        	else g.fillRect(x, y, width, height);
            g.setColor(Color.BLUE);
            
            // String
      
            g.setFont(new Font("Bold",Font.BOLD,14));
            g.setColor(Color.BLACK);
              
            
        int w  = g.getFontMetrics().stringWidth(value);
        g.drawString(value, x + (width - w)/2, y + textY2);
        g.setFont(new Font("Normal",Font.PLAIN,14)) ;
        g.setColor(Color.BLACK);
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        int xx=x;
        int yy=y;
        int ww=width;
        int hh=height;
        
        if (position==1)
    	 ww*=2;
    	
        if (position==2)
       	hh*=2;
        
        if (position==3)
           	{ xx-=ww;
              ww*=2;
           	}  
        if (position==4)
           	{ yy-=hh;
           	  hh*=2;
           	} 
            
    	if (GraphicLib.isInRectangle(_x, _y, xx, yy, ww, hh)) {
            return this;
        }
        return null;
    }
  
    
    
    
    private int min(int tab[])
    {
    	int min=tab[0];
    	for (int i=1; i<tab.length;i++)
    	{
    		if (min>tab[i]) min=tab[i];
    	}
      return min;
    }
    
    private int[] computeXY(int x, int y)
    {
    	int [] tab=new int[2];
    	if (father==null){
    		tab[0]=x;
    		tab[1]=y;
    		return tab;
    	} 
    	//with x y relatives to father
       	int fx=father.getX();
       	int fy=father.getY();
       	
    	
    	int []d=new int[4];
        d[0]=Math.abs(x-fx);
    	d[2]=Math.abs(fx+father.getWidth()-x);
    	d[3]=Math.abs(y-fy);
    	d[1]=Math.abs(fy+father.getHeight()-y);
    	
    	   	
     int min=min(d);
          if (min==d[0]) { x=fx-width/2; }
     else if (min==d[1]) {y=fy+father.getHeight()-height/2;}
     else if (min==d[2]) {x=fx+father.getWidth()-width/2;}
     else if (min==d[3]) {y=fy-height/2;}
     
     
     if (x>fx+father.getWidth()) x=fx+father.getWidth()-width/2;
     if (x<fx) x=fx-width/2;
     if (y>fy+father.getHeight()) y=fy+father.getHeight()-height/2;
     if (y<fy) y=fy-height/2;
         
     tab[0]=x;	
     tab[1]=y;	
  	
     return (tab);
    	
    }
    
     
  
    
    
    //to write:
     public void resizeWithFather() {
        if ((father != null) && (father instanceof ProCSDComponent)) {
            //System.out.println("cdRect comp");
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            //setCd(Math.min(x, father.getWidth() - getWidth()), Math.min(y, father.getHeight() - getHeight()));
             setMoveCd(x, y);
        }
    }
    
    public String getComponentID() {
        return value;
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        String oldValue = value;
        String text = getName() + ": ";
        if (hasFather()) {
           text=this.getFather().getValue()+" / "+text;
        }
        String s = (String)JOptionPane.showInputDialog(frame, text,
        "port name", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
        null,
        getValue());
        
        if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
           
            if (!TAttribute.isAValidId(s, false, false)) {
                JOptionPane.showMessageDialog(frame,
                "Could not change the name of the Component: the new name is not a valid name",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            
         /*  
          * 
          *  a faire: l'unicite
          *  if (!tdp.isTClassNameUnique(s)) {
                JOptionPane.showMessageDialog(frame,
                "Could not change the name of the TClass: the new name is already in use",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
           */ 
           
        /*  
         * a faire : modifier taille
         *   
         *   int size = graphics.getFontMetrics().stringWidth(s) + iconSize + 5;
            minDesiredWidth = Math.max(size, minWidth);
            if (minDesiredWidth != width) {
                //forceSize(w, getHeight());
                //newWidthForSon();
                newSizeForSon(null);
            }
          */
            setValue(s);
            
            
          /*  
            if (tdp.actionOnDoubleClick(this)) {
                return true;
            } else {
                JOptionPane.showMessageDialog(frame,
                "Could not change the name of the TClass: this name is already in use",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                setValue(oldValue);
            }
     */
        }
     
        return false;
    
     
    
    }
    
    
    public int getType() {
        return TGComponentManager.PROCSD_OUT_PORT;
    }
    
    public void setMoveCd(int _x, int _y) {
  
    	//System.out.println("father: "+father.getWidth()+"  "+father.getHeight());
    	//System.out.println("old xy: "+_x+"  "+_y);
    	 int []xy=computeXY(_x,_y);
    	//System.out.println("new xy: "+xy[0]+"  "+xy[1]);
    	//super.setMoveCd(xy[0], xy[1], false);
        x=xy[0];
        y=xy[1];
    	
    }
        
    public void setCd(int _x, int _y) {
    	  
    	//System.out.println("father: "+father.getWidth()+"  "+father.getHeight());
    	//System.out.println("old xy: "+_x+"  "+_y);
    	 int []xy=computeXY(_x,_y);
    	//System.out.println("new xy: "+xy[0]+"  "+xy[1]);
    	//super.setMoveCd(xy[0], xy[1], false);
        x=xy[0];
        y=xy[1];
    	
    }
    
    
    
    
  private void rotate(int pos)
  { //pos=0 ->clockwise
	//pos=i ->rotate to position i;  
	
	  if (pos>0) {
		  
		         this.position=pos;
	             //this.repaint();
		         return;
	  				}
	  if (position<4) position++;
	  else position=1;
	  tdp.repaint();
	  
  }
  
  public String myType()
  {
	  return "outPort";
	  
  }
   



    protected String translateExtraParam() {
//  
//    	StringBuffer sb = new StringBuffer("<extraparam>\n");
//        sb.append("<Rotate position=\"");
//        sb.append(position);
//        sb.append("\" />\n");
//        sb.append("</extraparam>\n");
//        return new String(sb);
    	
        return super.translateExtraParam();
    }
    

    
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
//        //System.out.println("*** load extra synchro ***");
//        try {
//            NodeList nli;
//            Node n1, n2;
//            Element elt;
//            
//            for(int i=0; i<nl.getLength(); i++) {
//                n1 = nl.item(i);
//                //System.out.println(n1);
//                if (n1.getNodeType() == Node.ELEMENT_NODE) {
//                    nli = n1.getChildNodes();
//                    for(int j=0; j<nli.getLength(); j++) {
//                        n2 = nli.item(j);
//                        //System.out.println(n2);
//                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
//                            elt = (Element) n2;
//                            if (elt.getTagName().equals("Rotate")) {
//                            	 position = (new Integer (elt.getAttribute("position"))).intValue();
//                                
//                            }
//                        }
//                    }
//                }
//            }
//            
//        } catch (Exception e) {
//            throw new MalformedModelingException();
//        }
//     
    
    	  super.loadExtraParam(nl,decX,decY,decId);
    
    }
    
    
    public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
    	
    	//Added by Solange
  	    super.addActionToPopupMenu( componentMenu,  menuAL, x,  y);
  	    //
        componentMenu.addSeparator();
        JMenuItem rotate = new JMenuItem("rotate");
        rotate.addActionListener(this);
        componentMenu.add(rotate);
        
        componentMenu.addSeparator();
        JMenuItem rename = new JMenuItem("rename");
        rename.addActionListener(this);
        componentMenu.add(rename);
        
           }
    
    public void actionPerformed(ActionEvent e)
    {
    	//Added by Solange
    	super.actionPerformed(e);
    	//
    	if (e.getActionCommand().equals("rotate")) this.rotate(0);
    	// Added by Solange
    	if (e.getActionCommand().equals("rename")) temp=this.editOndoubleClick(tdp.getGUI().getFrame());
    	    	
    }
    
}
    

