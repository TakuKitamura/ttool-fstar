/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.fr
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




package tmltranslator.tmlcp;

import tmltranslator.TMLAttribute;
import tmltranslator.TMLElement;

import java.util.ArrayList;
import java.util.List;

/**
   * Class TMLCPSequenceDiagram. A sequence diagram is simply represented as a set of instances. Each instance is associated to
   * variables, actions, messages and a mapped unit (the architecture unit where the instance is mapped to).
   * Creation: 18/02/2014
   * @version 1.2 04/11/2014
   * @author Ludovic APVRILLE, Andrea ENRICI
 */
public class TMLCPSequenceDiagram  extends TMLElement {

    private List<TMLSDInstance> instancesList;

//    private int hashCode;
//    private boolean hashCodeComputed = false;


    public TMLCPSequenceDiagram( String _name, Object _referenceObject )        {
        super( _name, _referenceObject );
        init();
    }

		// Constructor to be called from the parser, no reference object
    public TMLCPSequenceDiagram( String _name )        {
        super( _name, null );
        init();
    }

    private void init() {
        //globalVariables = new ArrayList<TMLAttribute>();
        instancesList = new ArrayList<TMLSDInstance>();
        /*messages = new ArrayList<TMLSDMessage>();
          actions = new ArrayList<TMLSDAction>();
          items = new ArrayList<TMLSDItem>();*/
    }

    public void addInstance( TMLSDInstance _inst )      {
        instancesList.add( _inst );
    }

    public List<TMLSDInstance> getInstances()      {
        return instancesList;
    }

    //return the list of all TMLAttributes declared for all instances
    public ArrayList<TMLAttribute> getAttributes()      {

        ArrayList<TMLAttribute> attributesList = new ArrayList<TMLAttribute>();

        for( TMLSDInstance instance: instancesList )    {
            attributesList.addAll( instance.getAttributes() );
        }
        return attributesList;
    }

    public ArrayList<TMLSDMessage> getMessages()        {

        ArrayList<TMLSDMessage> messagesList = new ArrayList<TMLSDMessage>();

        for( TMLSDInstance instance: instancesList )    {
            messagesList.addAll( instance.getMessages() );
        }
        return messagesList;
    }

    public ArrayList<TMLSDAction> getActions()  {

        ArrayList<TMLSDAction> actionsList = new ArrayList<TMLSDAction>();

        for( TMLSDInstance instance: instancesList )    {
            actionsList.addAll( instance.getActions() );
        }
        return actionsList;
    }

    //An event is either an action or a message
    public ArrayList<TMLSDEvent> getEvents()    {

        ArrayList<TMLSDEvent> eventsList = new ArrayList<TMLSDEvent>();

        for( TMLSDInstance instance: instancesList )    {
            eventsList.addAll( instance.getEvents() );
        }
        return eventsList;
    }


    public String toString() {
        String s = "*** Sequence diagram " + getName() + "\n";
        for( tmltranslator.tmlcp.TMLSDInstance instance: getInstances() )      {
            s += "\t" + "--- Instance " + instance.toString() + "\n";
            for( tmltranslator.tmlcp.TMLSDAction action: instance.getActions() )    {
                s += "\t\t" + " +++ Action " + action.toString() + "\n";
            }
            for( tmltranslator.TMLAttribute attribute: instance.getAttributes() )   {
                s += "\t\t" + " +++ Attribute " + attribute.toString() + "\n";
            }
            for( tmltranslator.tmlcp.TMLSDMessage message: instance.getMessages() ) {
                s += "\t\t" + " +++ Message " + message.toString() + "\n";
            }
        }
        return s;
    }

}       //End of class
