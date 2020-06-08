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

package ui.avatarsmd;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;

import avatartranslator.AvatarTransition;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import myutil.GraphicLib;
import ui.AvatarMethod;
import ui.ColorManager;
import ui.Expression;
import ui.GTURTLEModeling;
import ui.MalformedModelingException;
import ui.RangeExpression;
import ui.TAttribute;
import ui.TDiagramPanel;
import ui.TGCWithoutInternalComponent;
import ui.TGComponent;
import ui.TGConnectingPoint;
import ui.TGConnectingPointComment;
import ui.TGState;
import ui.WithAttributes;
import ui.util.IconManager;
import ui.window.JDialogAvatarTransition;

/**
 * Class AvatarSMDTransitionInfo
 * Internal component that represents a set of parameter for a transition
 * e.g., guard, after, compute, set of actions
 * Creation: 12/04/2010
 *
 * @author Ludovic APVRILLE
 * @version 1.0 12/04/2010
 */
public class AvatarSMDTransitionInfo extends TGCWithoutInternalComponent implements WithAttributes {

	private static final String NULL_EXPR = "";

    private static final String ZERO_EXPR = "0";

	private static final String NULL_GUARD_EXPR = "[ ]";

	static final String DISABLED_GUARD_EXPR = NULL_GUARD_EXPR;
	
	static final String DISABLED_DELAY_EXPR = NULL_EXPR;

	static final String DISABLED_PROBABILITY_EXPR = NULL_EXPR;

	static final String DISABLED_ACTION_EXPR = NULL_EXPR;
//    private static String FILE_INFO = "(user files specified)";
    //   private static String CODE_INFO = "(user code specified)";

	private final Expression guard;
    
    private final RangeExpression afterDelay;
    private final RangeExpression computeDelay;
    private final Expression extraDelay1;
    private final Expression extraDelay2;
    private final Expression delayDistributionLaw;


//    protected String guard;
//    protected String afterMin;
//    protected String afterMax;
//    protected String computeMin;
//    protected String computeMax;
    private final Expression probability;
    
    private Vector<Expression> listOfActions;
   // protected Vector<String> listOfActions;

//    protected String[] filesToInclude;
//    protected String[] codeToInclude;

    protected int minWidth = 10;
    protected int minHeight = 15;
    protected int h;

    protected int highlightedExpr;
    protected Graphics mygraphics;

    protected String defaultValue;

    public AvatarSMDTransitionInfo(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        moveable = true;
        editable = true;
        removable = false;

        guard = new Expression( NULL_GUARD_EXPR, NULL_GUARD_EXPR, null );
        afterDelay = new RangeExpression( NULL_EXPR, NULL_EXPR, NULL_EXPR, "after (%s, %s)", null , "after(%s)");
        extraDelay1 = new Expression( NULL_EXPR, NULL_EXPR, "extraDelay1=%s" );
        extraDelay2 = new Expression( NULL_EXPR, NULL_EXPR, "extraDelay2=%s" );
        delayDistributionLaw = new Expression( ZERO_EXPR, ZERO_EXPR, "delayDistributionLaw=%s" );
        computeDelay = new RangeExpression( NULL_EXPR, NULL_EXPR, NULL_EXPR, "computeFor (%s, %s)", null, "computeFor (%s)");
        probability = new Expression( NULL_EXPR, NULL_EXPR, "weight=%s" );

//        guard = "[ ]";
//        afterMin = "";
//        afterMax = "";
//        computeMin = "";
//        computeMax = "";
//        filesToInclude = null;
//        codeToInclude = null;


        nbConnectingPoint = 4;
        connectingPoint = new TGConnectingPoint[4];
        connectingPoint[0] = new TGConnectingPointComment(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointComment(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[2] = new TGConnectingPointComment(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[3] = new TGConnectingPointComment(this, 0, 0, true, true, 0.0, 0.5);

        listOfActions = new Vector<>();
        //listOfActions = new Vector<String>();

        myImageIcon = IconManager.imgic302;
    }

    public Vector<String> getListOfActions() {
    	final Vector<String> actions = new Vector<String>( listOfActions.size() );
    	
    	for ( final Expression action : listOfActions ) {
    		actions.add( action.getText() );
    	}
    	
        return actions;
//        return listOfActions;
    }
    
    public void setGuard(String s){
    	guard.setText( s );
    }

    public void setProbability(String s) {
        probability.setText( s );
    }
    
    public void addAction(String s){
    	listOfActions.add( new Expression( s ) );
//        listOfActions.add(s);
    }

    @Override
    public void internalDrawing(Graphics g) {
        if (!tdp.isScaled()) {
            mygraphics = g;
        }
        mygraphics = g;
        int step = 0;
      //  String s;
        h = g.getFontMetrics().getHeight();
        for (int j = 0; j < nbConnectingPoint; j++) {
            connectingPoint[j].setCdY(-h + 1);
        }

//        ColorManager.setColor(g, getState(), 0);
        // Issue #69
        final int inc = getExpressionTextHeight();
//        int inc = h;


        boolean atLeastOneThing = false;

        if ( !guard.isNull() ) {
            atLeastOneThing = true;

            final String formattedExpr = guard.toString();
        	final int textWidth = g.getFontMetrics().stringWidth( formattedExpr );
            
            if (tdp.isDrawingMain()) {
                width = Math.max( textWidth, width );
                width = Math.max(minWidth, width);
            }

            // Issue #69
            if ( !guard.isEnabled() && getFather().isContainedInEnabledState() ) {
            	g.setColor( ColorManager.DISABLED_FILLING );
            	g.fillRoundRect( x, y + step - inc + 2, textWidth + 2, inc + 2, 5, 5 );
            }
            
        	g.setColor( guard.isEnabled() ? ColorManager.AVATAR_GUARD : ColorManager.DISABLED );
            g.drawString( formattedExpr, x, y + step) ;

        	step += inc;
        }

        if ( !afterDelay.isNull() ) {
            atLeastOneThing = true;

            String formattedExpr = afterDelay.toString();
            final int formattedLaw = Integer.decode(delayDistributionLaw.getText());
            String law = "";
            if ((formattedLaw>=0) && (formattedLaw < AvatarTransition.DISTRIBUTION_LAWS_SHORT.length))
                law = AvatarTransition.DISTRIBUTION_LAWS_SHORT[formattedLaw];
            formattedExpr = formattedExpr + " " + law;
            formattedExpr = formattedExpr.trim();

            final int textWidth = g.getFontMetrics().stringWidth( formattedExpr);
            
            if (tdp.isDrawingMain()) {
                width = Math.max( textWidth, width );
                width = Math.max(minWidth, width);
            }

            // Issue #69
            if ( !afterDelay.isEnabled() && getFather().isContainedInEnabledState() ) {
            	g.setColor( ColorManager.DISABLED_FILLING );
            	g.fillRoundRect( x, y + step - inc + 2, textWidth + 2, inc + 2, 5, 5 );
            }

            g.setColor( afterDelay.isEnabled() ? ColorManager.AVATAR_TIME : ColorManager.DISABLED );
            g.drawString( formattedExpr, x, y + step );
            
            step += inc;
//            }
//            else {
//            	final String s = "after (" + afterMinDelayExp + ")";
//                
//            	g.drawString(s, x, y + step);
//                
//            	atLeastOneThing = true;
//                
//                if (tdp.isDrawingMain()) {
//                    width = Math.max(g.getFontMetrics().stringWidth(s), width);
//                    width = Math.max(minWidth, width);
//                }
//                
//                step += inc;
//            }
        }

        if ( !computeDelay.isNull() ) {
        	atLeastOneThing = true;
            
            final String formattedExpr = computeDelay.toString();
        	final int textWidth = g.getFontMetrics().stringWidth( formattedExpr );

        	if (tdp.isDrawingMain()) {
                width = Math.max( textWidth, width );
                width = Math.max(minWidth, width);
            }

            // Issue #69
            if ( !computeDelay.isEnabled() && getFather().isContainedInEnabledState() ) {
            	g.setColor( ColorManager.DISABLED_FILLING );
            	g.fillRoundRect( x, y + step - inc + 2, textWidth + 2, inc + 2, 5, 5 );
            }

            g.setColor( computeDelay.isEnabled() ? ColorManager.AVATAR_TIME : ColorManager.DISABLED );
           	g.drawString( formattedExpr, x, y + step );
            
        	step += inc;
        }

        if ( !probability.isNull() ) {
        	atLeastOneThing = true;
            final String formattedExpr = probability.toString();
        	final int textWidth = g.getFontMetrics().stringWidth( formattedExpr );
            
            if (tdp.isDrawingMain()) {
                width = Math.max( textWidth, width );
                width = Math.max(minWidth, width);
            }

            // Issue #69
            if ( !probability.isEnabled() && getFather().isContainedInEnabledState() ) {
            	g.setColor( ColorManager.DISABLED_FILLING );
            	g.fillRoundRect( x, y + step - inc + 2, textWidth + 2, inc + 2, 5, 5 );
            }
            
        	g.setColor( probability.isEnabled() ? ColorManager.AVATAR_GUARD : ColorManager.DISABLED );
            g.drawString( formattedExpr, x, y + step) ;

        	step += inc;
//            s = "weight=" + probability;
//            g.drawString(s, x, y + step);
//            atLeastOneThing = true;
//            if (tdp.isDrawingMain()) {
//                width = Math.max(g.getFontMetrics().stringWidth(s), width);
//                width = Math.max(minWidth, width);
//            }
//            step += inc;
        }

     //   g.setColor(ColorManager.AVATAR_ACTION);

        for( final Expression action : listOfActions ) {
        	if ( !action.isNull() ) {
	        	atLeastOneThing = true;
	        	
	        	final String formattedExpr = action.toString();
	        	final int textWidth = g.getFontMetrics().stringWidth( formattedExpr );
	            
	            if (tdp.isDrawingMain()) {
	                width = Math.max( textWidth, width );
	                width = Math.max( minWidth, width);
	            }

	            // Issue #69
	            if ( !action.isEnabled() && getFather().isContainedInEnabledState() ) {
	            	g.setColor( ColorManager.DISABLED_FILLING );
	            	g.fillRoundRect( x, y + step - inc + 2, textWidth + 2, inc + 2, 5, 5 );
	            }
                
	        	g.setColor( action.isEnabled() ? ColorManager.AVATAR_ACTION : ColorManager.DISABLED );
	        	g.drawString( formattedExpr, x, y + step );

	            step += inc;
            }
        }
//        for (int i = 0; i < listOfActions.size(); i++) {
//            s = listOfActions.get(i);
//            if (s.length() > 0) {
//                g.drawString(s, x, y + step);
//                atLeastOneThing = true;
//                if (tdp.isDrawingMain()) {
//                    width = Math.max(g.getFontMetrics().stringWidth(s), width);
//                    width = Math.max(minWidth, width);
//                }
//                step += inc;
//            }
//        }

        /*g.setColor(ColorManager.AVATAR_CODE);

          if (hasFilesToInclude()) {
          atLeastOneThing = true;
          g.drawString(FILE_INFO, x, y + step);
          step += inc;
          if (!tdp.isScaled()) {
          width = Math.max(g.getFontMetrics().stringWidth(FILE_INFO), width);
          width = Math.max(minWidth, width);
          }
          }

          if (hasCodeToInclude()) {
          atLeastOneThing = true;
          g.drawString(CODE_INFO, x, y + step);
          step += inc;
          if (!tdp.isScaled()) {
          width = Math.max(g.getFontMetrics().stringWidth(CODE_INFO), width);
          width = Math.max(minWidth, width);
          }
          }*/


        if (tdp.isDrawingMain()) {
            height = Math.max(step, minHeight);
        }

        if (!atLeastOneThing) {
            width = minWidth;
        }

        //ColorManager.setColor(g, state, 0);
        if ((getState() == TGState.POINTER_ON_ME) || (getState() == TGState.POINTED) || (getState() == TGState.MOVING)) {
            ColorManager.setColor( g, state, 0, isEnabled() );

            final Rectangle rectangle = new Rectangle( x - 1, y - h + 2, width + 2, height + 2 );
            int indexOfPointedExpr = -2;

            if ( inc != 0 && isOnMe( tdp.currentX, tdp.currentY ) == this ) {

                final int exprWidth = getWidthExprOfSelectedExpression();
                indexOfPointedExpr = getPointedExpressionOrder() - 1;
            	rectangle.y = y + indexOfPointedExpr * inc + 2;
            	rectangle.width = exprWidth + 2;
            	rectangle.height = inc + 2;
            }

            g.drawRoundRect( rectangle.x, rectangle.y, rectangle.width, rectangle.height, 5, 5 );
            //TraceManager.addDev("Draw pointed req   highlighted expr=" + highlightedExpr);

            if (!tdp.isScaled()) {
                //TraceManager.addDev("Highlighted expr=" + highlightedExpr);
                highlightedExpr = indexOfPointedExpr + 1;
                //TraceManager.addDev("NEW Highlighted expr=" + highlightedExpr);
            }

            //  g.drawRoundRect(x - 1, y - h + 2, width + 2, height + 2, 5, 5);
        }
    }


    private int getWidthExprOfSelectedExpression() {
        if (mygraphics == null) {
            return -1;
        }
        //TraceManager.addDev("currentX = " + tdp.currentX +  " currentY=" + tdp.currentY);
        // Issue #69: Draw rectangle only around the pointed line
        final Expression selExpr = getSelectedExpression();
        final int exprWidth;

        if ( selExpr == null ) {
            exprWidth = width;
        }
        else {
            exprWidth  = mygraphics.getFontMetrics().stringWidth( selExpr.toString() );
            ColorManager.setColor( mygraphics, state, 0, selExpr.isEnabled() );
        }

        return exprWidth;
    }

    private int getExpressionTextHeight() {
    	return h;
    }
    
    private Integer getPointedExpressionOrder() {
        int h = getExpressionTextHeight();
    	if ( h != 0 ) {
    		return ( tdp.currentY + 10 - y ) / h;
    	}
    	
    	return null;
    }

    @Override
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y - h + 2, width, height)) {
            return this;
        }
        return null;
    }


    @Override
    public boolean hasAnUpdateOnPointedComponent() {
        Integer indexI = getPointedExpressionOrder();
        if (indexI == null) {
            return false;
        }
        int index = indexI.intValue();

        //TraceManager.addDev("CurrentIndex: " + highlightedExpr + " newIndex:" + index);
        return index != highlightedExpr;

        //return true;
        //return (getPointedExpressionOrder() != pointedExpOrder);
    }

    @Override
    public boolean isInRectangle(int x1, int y1, int width, int height) {
        return !((getX() < x1) || (getY() < y1) || ((getX() + this.width) > (x1 + width)) || ((getY() + this.height) > (y1 + height)));
    }

    @Override
    public boolean editOndoubleClick(JFrame frame) {
        List<TAttribute> attributes = tdp.getMGUI().getAllAttributes();
        List<AvatarMethod> methods = tdp.getMGUI().getAllMethods();
        JDialogAvatarTransition jdat = new JDialogAvatarTransition(	frame, 
																	"Setting transition parameters",
																	getGuard(),
																	getAfterMinDelay(),
																	getAfterMaxDelay(),
                                                                    getDelayDistributionLaw(),
                                                                    getExtraDelay1(),
                                                                    getExtraDelay2(),
//																	getComputeMinDelay(),
//																	getComputeMaxDelay(),
																	listOfActions,
																	attributes,
																	methods,
																	getProbability() );
        //JDialogAvatarTransition jdat = new JDialogAvatarTransition(frame, "Setting transition parameters", guard, afterMin, afterMax, computeMin, computeMax, listOfActions, attributes, methods, filesToInclude, codeToInclude, probability);
        //  jdat.setSize(600, 550);
        GraphicLib.centerOnParent(jdat, 600, 550);
        jdat.setVisible(true); // blocked until dialog has been closed


        if (jdat.hasBeenCancelled()) {
            return false;
        }

        guard.setText( jdat.getGuard().trim() );

        int index = getGuard().indexOf('[');
        if (index == -1) {
        	guard.setText( "[ " + guard + " ]" );
        }

        //TraceManager.addDev("Max delay:" + jdat.getAfterMax().trim());
        afterDelay.getMinExpression().setText( jdat.getAfterMin().trim() );
        afterDelay.getMaxExpression().setText( jdat.getAfterMax().trim() );
        String retExtra = jdat.getExtraDelay1().trim();
        try {
            double extra1 = Double.parseDouble(retExtra);
            extraDelay1.setText(retExtra );
        } catch (Exception e) {
        }
        retExtra = jdat.getExtraDelay2().trim();
        try {
            double extra2 = Double.parseDouble(retExtra);
            extraDelay2.setText(retExtra );
        } catch (Exception e) {
        }


        delayDistributionLaw.setText( "" + jdat.getDistributionLaw() );

        /*if (AvatarTransition.NB_OF_EXTRA_ATTRIBUTES[jdat.getDistributionLaw()] > 0 ) {
            if (extraDelay1.getText().length() == 0) {
                extraDelay1.setText(afterDelay.getMinExpression().getText());
            }
            if (extraDelay2.getText().length() == 0) {
                extraDelay2.setText(afterDelay.getMinExpression().getText());
            }
        }*/

//        computeDelay.getMinExpression().setText( jdat.getComputeMin().trim() );
//        computeDelay.getMaxExpression().setText( jdat.getComputeMax().trim() );
        listOfActions.clear();
        listOfActions.addAll( jdat.getActions() );
//        guard = jdat.getGuard().trim();
//
//        int index = guard.indexOf('[');
//        if (index == -1) {
//            guard = "[ " + guard + " ]";
//        }
//
//        afterMin = jdat.getAfterMin().trim();
//        afterMax = jdat.getAfterMax().trim();
//        computeMin = jdat.getComputeMin().trim();
//        computeMax = jdat.getComputeMax().trim();
//        filesToInclude = jdat.getFilesToInclude();
//        codeToInclude = jdat.getCodeToInclude();
        probability.setText( jdat.getProbability().trim() );
        
        return true;
    }
    
    private String toXML( 	final String tag,
    						final Expression expression ) {
    	return "<" + tag + " value=\"" + GTURTLEModeling.transformString( expression.getText() ) + "\" enabled=\"" + expression.isEnabled() + "\"/>" + System.lineSeparator();
    }

    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append( toXML( "guard", guard ) );
        sb.append( toXML( "afterMin", afterDelay.getMinExpression() ) );
        sb.append( toXML( "afterMax", afterDelay.getMaxExpression() ) );
        sb.append( toXML( "extraDelay1", extraDelay1 ) );
        sb.append( toXML( "extraDelay2", extraDelay2 ) );
        sb.append( toXML( "delayDistributionLaw", delayDistributionLaw ) );
        sb.append( toXML( "computeMin", computeDelay.getMinExpression() ) );
        sb.append( toXML( "computeMax", computeDelay.getMaxExpression() ) );
        sb.append( toXML( "probability", probability ) );
//        sb.append("<guard value=\"");
//        sb.append(GTURTLEModeling.transformString(guard));
//        sb.append("\" />\n");
//
//        sb.append("<afterMin value=\"");
//        sb.append(GTURTLEModeling.transformString(afterMin));
//        sb.append("\" />\n");
//
//        sb.append("<afterMax value=\"");
//        sb.append(GTURTLEModeling.transformString(afterMax));
//        sb.append("\" />\n");
//
//        sb.append("<computeMin value=\"");
//        sb.append(GTURTLEModeling.transformString(computeMin));
//        sb.append("\" />\n");
//
//        sb.append("<computeMax value=\"");
//        sb.append(GTURTLEModeling.transformString(computeMax));
//        sb.append("\" />\n");

//        sb.append("<probability value=\"");
//        sb.append(GTURTLEModeling.transformString(probability));
//        sb.append("\" />\n");

        for( final Expression action : listOfActions ) {
        	sb.append( toXML( "actions", action )  );
        }
//
//        for (int i = 0; i < listOfActions.size(); i++) {
//            sb.append("<actions value=\"");
//            sb.append(GTURTLEModeling.transformString(listOfActions.get(i)));
//            sb.append("\" />\n");
//        }
//
//        if (filesToInclude != null) {
//            for (int i = 0; i < filesToInclude.length; i++) {
//                sb.append("<filesToIncludeLine value=\"");
//                sb.append(GTURTLEModeling.transformString(filesToInclude[i]));
//                sb.append("\" />\n");
//            }
//        }
//
//        if (codeToInclude != null) {
//            for (int i = 0; i < codeToInclude.length; i++) {
//                sb.append("<codeToIncludeLine value=\"");
//                sb.append(GTURTLEModeling.transformString(codeToInclude[i]));
//                sb.append("\" />\n");
//            }
//        }

        sb.append("</extraparam>\n");
        
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
        //
//        String tmpFilesToInclude = "";
//        String tmpCodeToInclude = "";

        try {
            listOfActions = new Vector<Expression>();
//            listOfActions = new Vector<String>();
            NodeList nli;
            Node n1, n2;
            Element elt;
            String s;
            for (int i = 0; i < nl.getLength(); i++) {
                n1 = nl.item(i);
                //
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();

                    // Issue #17 copy-paste error on j index
                    for (int j = 0; j < nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("guard")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                	guard.setText( s );

                                    // Issue #69 loading enabling parameters
                                    s = elt.getAttribute("enabled");
                                    
                                    if ( s != null && !s.isEmpty() ) {
                                        guard.setEnabled( Boolean.parseBoolean( s ) );
                                    }
//                                    guard = s;
                                }
                            }
                            if (elt.getTagName().equals("afterMin")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                    afterDelay.getMinExpression().setText( s );

                                    // Issue #69 loading enabling parameters
                                    s = elt.getAttribute( "enabled" );
                                    
                                    if ( s != null && !s.isEmpty() ) {
                                    	afterDelay.getMinExpression().setEnabled( Boolean.parseBoolean( s ) );
                                    }
//                                    afterMin = s;
                                }
                            }
                            if (elt.getTagName().equals("afterMax")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                    afterDelay.getMaxExpression().setText( s );

                                    // Issue #69 loading enabling parameters
                                    s = elt.getAttribute("enabled");
                                    
                                    if ( s != null && !s.isEmpty() ) {
                                    	afterDelay.getMaxExpression().setEnabled( Boolean.parseBoolean( s ) );
                                    }
//                                    afterMax = s;
                                }
                            }

                            if (elt.getTagName().equals("extraDelay1")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                    extraDelay1.setText( s );

                                    // Issue #69 loading enabling parameters
                                    s = elt.getAttribute("enabled");

                                    if ( s != null && !s.isEmpty() ) {
                                        extraDelay1.setEnabled(Boolean.parseBoolean(s));
                                    }
//
                                }
                            }

                            if (elt.getTagName().equals("extraDelay2")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                    extraDelay2.setText( s );

                                    // Issue #69 loading enabling parameters
                                    s = elt.getAttribute("enabled");

                                    if ( s != null && !s.isEmpty() ) {
                                        extraDelay2.setEnabled(Boolean.parseBoolean(s));
                                    }
//
                                }
                            }

                            if (elt.getTagName().equals("delayDistributionLaw")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                    delayDistributionLaw.setText( s );

                                    // Issue #69 loading enabling parameters
                                    s = elt.getAttribute("enabled");


                                    if ( s != null && !s.isEmpty() ) {
                                        delayDistributionLaw.setEnabled(Boolean.parseBoolean(s));
                                    }

//
                                }
                            }

                            if (elt.getTagName().equals("computeMin")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                    computeDelay.getMinExpression().setText( s );

                                    // Issue #69 loading enabling parameters
                                    s = elt.getAttribute("enabled");
                                    
                                    if ( s != null && !s.isEmpty() ) {
                                    	computeDelay.getMinExpression().setEnabled( Boolean.parseBoolean( s ) );
                                    }
                                    //computeMin = s;
                                }
                            }
                            if (elt.getTagName().equals("computeMax")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                    computeDelay.getMaxExpression().setText( s );

                                    // Issue #69 loading enabling parameters
                                    s = elt.getAttribute("enabled");
                                    
                                    if ( s != null && !s.isEmpty() ) {
                                    	computeDelay.getMaxExpression().setEnabled( Boolean.parseBoolean( s ) );
                                    }
                                    //computeMax = s;
                                }
                            }
                            if (elt.getTagName().equals("probability")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                	probability.setText( s );

                                    // Issue #69 loading enabling parameters
                                    s = elt.getAttribute("enabled");
                                    
                                    if ( s != null && !s.isEmpty() ) {
                                    	probability.setEnabled( Boolean.parseBoolean( s ) );
                                    }
//                                    probability = s;
                                }
                            }
                            if (elt.getTagName().equals("actions")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                	final Expression action = new Expression( s );
                                    listOfActions.add( action );

                                    // Issue #69 loading enabling parameters
                                    s = elt.getAttribute("enabled");
                                    
                                    if ( s != null && !s.isEmpty() ) {
                                    	action.setEnabled( Boolean.parseBoolean( s ) );
                                    }
                                    //listOfActions.add(s);
                                }
                            }

                            /*if (elt.getTagName().equals("filesToIncludeLine")) {
                            //
                            s = elt.getAttribute("value");
                            if (s.equals("null")) {
                            s = "";
                            }
                            tmpFilesToInclude += GTURTLEModeling.decodeString(s) + "\n";
                            }

                            if (elt.getTagName().equals("codeToIncludeLine")) {
                            //
                            s = elt.getAttribute("value");
                            if (s.equals("null")) {
                            s = "";
                            }
                            tmpCodeToInclude += GTURTLEModeling.decodeString(s) + "\n";
                            }*/
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException( e );
        }
//
//
//        if (tmpFilesToInclude.trim().length() == 0) {
//            filesToInclude = null;
//        } else {
//            filesToInclude = Conversion.wrapText(tmpFilesToInclude);
//        }
//        if (tmpCodeToInclude.trim().length() == 0) {
//            codeToInclude = null;
//        } else {
//            codeToInclude = Conversion.wrapText(tmpCodeToInclude);
//        }
    }

    public String getGuard() {
        return guard.getText();
//        return guard;
    }
    
    /**
     * Issue #69
     * @return Effective expression - DISABLED_GUARD_EXPR ([])
     */
    public String getEffectiveGuard() {
    	return guard.getEffectiveExpression( DISABLED_GUARD_EXPR );
    }

    public String getAfterMinDelay() {
        return afterDelay.getMinExpression().getText();
//        return afterMin;
    }

    /**
     * Issue #69
     * @return Effective After Min expression - DISABLED_DELAY_EXPR ("")
     */
    public String getEffectiveAfterMinDelay() {
        return afterDelay.getMinExpression().getEffectiveExpression( DISABLED_DELAY_EXPR );
    }

    public String getAfterMaxDelay() {
        return afterDelay.getMaxExpression().getText();
        //return afterMax;
    }

    public int getDelayDistributionLaw() {
        return Integer.decode(delayDistributionLaw.getText());
    }

    public String getExtraDelay1() {
        return extraDelay1.getText();
    }

    public String getExtraDelay2() {
        return extraDelay2.getText();
    }


    /**
     * Issue #69
     * @return Effective After Max expression - DISABLED_DELAY_EXPR ("")
     */
    public String getEffectiveAfterMaxDelay() {
        return afterDelay.getMaxExpression().getEffectiveExpression( DISABLED_DELAY_EXPR );
    }

    public String getComputeMinDelay() {
        return computeDelay.getMinExpression().getText();
        //return computeMin;
    }

    /**
     * Issue #69
     * @return Effective Compute Min expression - DISABLED_DELAY_EXPR ("")
     */
    public String getEffectiveComputeMinDelay() {
        return computeDelay.getMinExpression().getEffectiveExpression( DISABLED_DELAY_EXPR );
    }

    public String getComputeMaxDelay() {
        return computeDelay.getMaxExpression().getText();
        //return computeMax;
    }

    /**
     * Issue #69
     * @return Effective Compute Max expression - DISABLED_DELAY_EXPR ("")
     */
    public String getEffectiveComputeMaxDelay() {
        return computeDelay.getMaxExpression().getEffectiveExpression( DISABLED_DELAY_EXPR );
    }

    public String getProbability() {
        return probability.getText();
    }

    public String getEffectiveProbability() {
        return probability.getEffectiveExpression( DISABLED_PROBABILITY_EXPR );
    }

    public void setTimes(String minDelay, String maxDelay, String minCompute, String maxCompute) {
		computeDelay.getMinExpression().setText( minCompute );
		computeDelay.getMaxExpression().setText( maxCompute );
		afterDelay.getMinExpression().setText( minDelay );
		afterDelay.getMaxExpression().setText(maxDelay );
//        computeMin = minCompute;
//        computeMax = maxCompute;
//        afterMin = minDelay;
//        afterMax = maxDelay;
    }

    public Vector<String> getActions() {
    	final Vector<String> actionExpressions = new Vector<String>( listOfActions.size() );
    	
    	for ( final Expression action : listOfActions ) {
    		actionExpressions.add( action.getText() ); 
    	}
    	
    	return actionExpressions;
//        return listOfActions;
    }

    /**
     * Issue #69
     * @return List of effective actions
     */
    public Vector<String> getEffectiveActions() {
    	final Vector<String> actionExpressions = new Vector<String>( listOfActions.size() );
    	
    	for ( final Expression action : listOfActions ) {
    		actionExpressions.add( action.getEffectiveExpression( DISABLED_ACTION_EXPR ) ); 
    	}
    	
    	return actionExpressions;
    }
//
//    public boolean hasFilesToInclude() {
//        return ((filesToInclude != null) && (filesToInclude.length > 0));
//    }
//
//    public boolean hasCodeToInclude() {
//        return ((codeToInclude != null) && (codeToInclude.length > 0));
//    }
//
//    public String getFilesToInclude() {
//        if (filesToInclude == null) {
//            return null;
//        }
//        String ret = "";
//        for (int i = 0; i < filesToInclude.length; i++) {
//            ret += filesToInclude[i] + "\n";
//        }
//        return ret;
//    }
//
//    public String getCodeToInclude() {
//        if (codeToInclude == null) {
//            return null;
//        }
//        String ret = "";
//        for (int i = 0; i < codeToInclude.length; i++) {
//            ret += codeToInclude[i] + "\n";
//        }
//        return ret;
//    }

    @Override
    public String getAttributes() {
        String attr = "";
        if ( !guard.isNull())
            attr += " guard: " + getGuard();
        if (!afterDelay.isNull() )
            attr += " delay: [" + afterDelay.getMinExpression().getText() + "," + afterDelay.getMaxExpression().getText() + "] ";
        if ( !probability.isNull() )
            attr += " weight:" + getProbability();

        for(final Expression action : listOfActions) {
            attr += " / " + action.getText();
        }

        return attr;
    }
    
    @Override
    public AvatarSMDConnector getFather() {
    	return (AvatarSMDConnector) super.getFather();
    }

    @Override
    public void setFather( final TGComponent _father ) {
    	if ( _father != null && !( _father instanceof AvatarSMDConnector ) ) {
    		throw new IllegalArgumentException( "Father should be an instance of '" + AvatarSMDConnector.class.getName() + "'." );
    	}
    	
    	super.setFather( _father );
    }

	/* Issue #69
	 * (non-Javadoc)
	 * @see ui.AbstractCDElement#canBeDisabled()
	 */
	@Override
    public boolean canBeDisabled() {
		final AvatarSMDConnector transitionCon = getFather();
		
		if ( transitionCon == null ) {
			return false;
		}
		
    	return transitionCon.canBeDisabledContainer();
    }
	
	@Override
	public void setEnabled( final boolean enabled ) {
		final Expression selExpr = getSelectedExpression();
		
		if ( selExpr == null ) {
			guard.setEnabled( enabled );
			afterDelay.setEnabled( enabled );
			computeDelay.setEnabled( enabled );
			probability.setEnabled( enabled );
			
			for ( final Expression actionExpr : listOfActions ) {
				actionExpr.setEnabled( enabled );
			}
		}
		else {
			selExpr.setEnabled( enabled );
		}
	}
	
	@Override
	public boolean isEnabled() {
		return isEnabledCheckNull( false ); 
	}
	
	public boolean isEnabledNotNull() {
		return isEnabledCheckNull( true );
	}
	
	private boolean isEnabledCheckNull( final boolean checkNull ) {
		
		// Used by the UI to toggle enabled
		final Expression selExpr = getSelectedExpression();
	
		if ( selExpr != null ) {
			return selExpr.isEnabled();
		}

		for ( final Expression actExpr : listOfActions ) {
			if ( actExpr.isEnabled() ) {
				return true;
			}
		}
		
		return 	( guard.isEnabled() && ( !checkNull || !guard.isNull() ) ) ||
				( afterDelay.isEnabled() && ( !checkNull || !afterDelay.isNull() ) ) ||
				( computeDelay.isEnabled() && ( !checkNull || !computeDelay.isNull() ) ) ||
				( probability.isEnabled() && ( !checkNull || !probability.isNull() ) );
	}
	
	public boolean isNull() {
		for ( final Expression actExpr : listOfActions ) {
			if ( !actExpr.isNull() ) {
				return false;
			}
		}
		
		return guard.isNull() && afterDelay.isNull() && computeDelay.isNull() && probability.isNull();
	}
	
	private List<Expression> getNonNullExpressions() {
		final List<Expression> expressions = new ArrayList<Expression>();
		
		if ( !guard.isNull() ) {
			expressions.add( guard );
		}

		if ( !afterDelay.isNull() ) {
			expressions.add( afterDelay );
		}

		if ( !computeDelay.isNull() ) {
			expressions.add( computeDelay );
		}

		if ( !probability.isNull() ) {
			expressions.add( probability );
		}

		for ( final Expression expr : listOfActions ) {
			if ( !expr.isNull() ) {
				expressions.add( expr );
			}
		}
		
		return expressions;
	}
	
	private Expression getSelectedExpression() {
		final Integer selectedExpressionIndex = getPointedExpressionOrder();
		
		if ( selectedExpressionIndex == null || selectedExpressionIndex < 0 ) {
			return null;
		}
		
		final List<Expression> expressions = getNonNullExpressions();
		
		if ( selectedExpressionIndex >= expressions.size() ) {
			return null;
		}
			
		return expressions.get( selectedExpressionIndex );
	}
}
