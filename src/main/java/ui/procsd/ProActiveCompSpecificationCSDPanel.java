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

package ui.procsd;

import ui.MainGUI;
import ui.TToolBar;

import java.util.Vector;

public class ProActiveCompSpecificationCSDPanel extends ProactiveCSDPanel {

    /*
     * the component in the main diagram that is designed in this panel
     */

    public ProActiveCompSpecificationCSDPanel(MainGUI mgui, TToolBar _ttb, String name) {
        super(mgui, _ttb);
        this.setName(name);

    }

    /*
     * returns the component disigned within this diagram
     */
    public ProCSDComponent getProCSDComponent() {

        for (int k = 0; k < componentList.size(); k++) {
            if (componentList.get(k) instanceof ProCSDComponent)
                return (ProCSDComponent) componentList.get(k);
        }
        return null;
    }

    /*
     * Creates the blackBox corresponding to the component given as parameter the
     * black box contains: - copies of the ports of comp with their interfaces -
     * copies of the attributes of comp
     */

    public ProCSDComponent createBlackBoxComp(ProCSDComponent comp) {
        ProCSDComponent designComp = new ProCSDComponent(comp.getX(), comp.getY(), comp.getMinHeight(),
                comp.getMaxHeight(), comp.getMinWidth(), comp.getMaxHeight(), false, null, this);

        designComp.setWidth(comp.getWidth());
        designComp.setHeight(comp.getHeight());

        designComp.setValue(comp.getValue() + "Design");

        Vector<ProCSDPort> ports = comp.getPortsList();
        for (int k = 0; k < ports.size(); k++) {
            ProCSDPort p = ports.get(k);
            ProCSDPort newPort = null;
            if (p instanceof ProCSDInPort)
                newPort = new ProCSDInPort(p.getX(), p.getY(), p.getMinHeight(), p.getMaxHeight(), p.getMinWidth(),
                        p.getMaxWidth(), false, designComp, this);
            else if (p instanceof ProCSDOutPort)
                newPort = new ProCSDOutPort(p.getX(), p.getY(), p.getMinHeight(), p.getMaxHeight(), p.getMinWidth(),
                        p.getMaxWidth(), false, designComp, this);

            if (newPort != null) {
                newPort.setValue(p.getValue());
                // newPort is a part of the designComponent
                // p is the part of an instance comp
                // p will have the portCode of newPort
                p.setPortCode(newPort.getPortCode());

                designComp.addSwallowedTGComponent(newPort, newPort.getX(), newPort.getY());

                ProCSDInterface pI = p.getMyInterface();
                if (pI != null) {
                    // ProCSDInterface newInterface = new
                    // ProCSDInterface(pI.getX(),pI.getY(),pI.getMinHeight(),pI.getMaxHeight(),pI.getMinWidth(),pI.getMaxWidth(),false,null,this);
                    // newInterface.setValue(pI.getValue());
                    // newInterface.setManda(pI.isMandatory());
                    // newInterface.setMessages(pI.getMyMessages());

                    // ProCSDInterface newInterface=new ProCSDInterface(pI,this);
                    // this.addBuiltComponent(newInterface);
                    // newPort.connectInterface(newInterface);

                    // TGConnectingPoint point1=newPort.getTGConnectingPointAtIndex(0);
                    // TGConnectingPoint point2=newInterface.getTGConnectingPointAtIndex(0);
                    // TGConnectorPortInterface connector=new
                    // TGConnectorPortInterface(0,0,0,0,0,0,false,null,this,point1,point2,new
                    // Vector());

                    // this.addBuiltComponent(connector);
                } // if interface !=null
            } // if newPort!=null
        }

        designComp.setAttributes(comp.getMyAttributes());

        this.addBuiltComponent(designComp);

        return designComp;
    }

}
