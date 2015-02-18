/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

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
   * Class TMLTask
   * Creation: 17/11/2005
   * @version 1.0 17/11/2005
   * @author Ludovic APVRILLE
   * @see
   */

package tmltranslator;

import myutil.*;

import java.util.*;


public class TMLTask extends TMLElement {
    private TMLActivity activity;
    private boolean isRequested = false;
    private TMLRequest request;
    private ArrayList<TMLAttribute> attributes;
    private boolean mustExit = false;
    private int priority;


    public TMLTask(String name, Object referenceToClass, Object referenceToActivityDiagram) {
        super(name, referenceToClass);
        //TraceManager.addDev("Creating new TMLTask:" + name);
        activity = new TMLActivity(name+"activity_diagram", referenceToActivityDiagram);
        attributes = new ArrayList<TMLAttribute>();
    }

    public void setRequested(boolean _b) {
        isRequested = _b;
    }

    public void setPriority(int _priority) {
        priority = _priority;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isRequested() {
        return isRequested;
    }

    public void setRequest(TMLRequest _request) {
        request = _request;
    }

    public TMLRequest getRequest() {
        return request;
    }

    public void addAttribute(TMLAttribute _tmla) {
        attributes.add(_tmla);
    }

    public ArrayList<TMLAttribute> getAttributes() {
        return attributes;
    }

    public boolean hasCommand(int commandID) {
        TMLActivityElement tmlae;
        for(int i=0; i<activity.nElements(); i++) {
            tmlae = activity.get(i);
            if (tmlae.getID() == commandID) {
                return true;
            }
        }
        return false;
    }

    public String[] makeCommandIDs () {
        String[] list = new String[activity.nElements()];
        TMLActivityElement tmlae;
        for(int i=0; i<activity.nElements(); i++) {
            tmlae = activity.get(i);
            list[i] = tmlae.getName() + " (" + tmlae.getID() + ")";
        }
        return list;
    }

    public String[] makeVariableIDs () {
        String[] list = new String[attributes.size()];
        int cpt = 0;

        for(TMLAttribute att: attributes) {
            list[cpt] = att.getName() + " (" + att.getID() + ")";
            cpt ++;
        }
        return list;
    }

    public TMLAttribute getAttributeByName(String _name) {
        for(TMLAttribute attribute:attributes) {
            if (attribute.getName().compareTo(_name) == 0) {
                return attribute;
            }
        }
        return null;
    }

    public ArrayList<TMLReadChannel> getReadChannels()  {

        ArrayList<TMLReadChannel> list = new ArrayList<TMLReadChannel>();
        for( int i = 0; i < getActivityDiagram().nElements(); i++ )     {
            if( getActivityDiagram().get(i) instanceof TMLReadChannel ) {
                list.add( (TMLReadChannel) getActivityDiagram().get(i) );
                //TraceManager.addDev( "Element: " + task.getActivityDiagram().get(i).toString() );
            }
        }
        return list;
    }

    public ArrayList<TMLWriteChannel> getWriteChannels()        {

        ArrayList<TMLWriteChannel> list = new ArrayList<TMLWriteChannel>();
        for( int i = 0; i < getActivityDiagram().nElements(); i++ )     {
            if( getActivityDiagram().get(i) instanceof TMLWriteChannel )        {
                list.add( (TMLWriteChannel) getActivityDiagram().get(i) );
                //TraceManager.addDev( "Element: " + task.getActivityDiagram().get(i).toString() );
            }
        }
        return list;
    }

    public String getID0()      {

        if( getReadChannels().size() > 0 )      {
            return getReadChannels().get(0).toString().split("__")[1];
        }
        else    {
            return "";
        }
    }

    public String getOD0()      {

        if( getWriteChannels().size() > 0 )     {
            return getWriteChannels().get(0).toString().split("__")[1];
        }
        else    {
            return "";
        }
    }

    public String getXOD()      {
        return getName().split( "__" )[1];
    }


    public TMLActivity getActivityDiagram() {
        return activity;
    }

    public void setExit(boolean b) {
        mustExit = b;
    }

    public boolean exits() {
        return mustExit;
    }

    public boolean has(TMLActivityElement _elt) {
        if (activity == null) {
            return false;
        }

        return activity.contains(_elt);
    }

    public String getNameExtension() {
        return "task__";
    }

    public int getMaximumSelectEvtSize() {
        return activity.getMaximumSelectEvtSize();
    }

    public String getAttributeString() {
        String ret = "";
        for(TMLAttribute attribute:attributes) {
            ret += attribute.toString() + " / " ;
        }
        return ret;
    }

    public boolean hasTMLRandom() {
        TMLActivityElement element;
        for (int i=0; i<activity.nElements(); i++) {
            element = activity.get(i);
            if (element instanceof TMLRandom) {
                return true;
            }
        }
        return false;
    }

    public void removeAllRandomSequences() {
        activity.removeAllRandomSequences(this);
    }

    public Vector<String> getAllAttributesStartingWith(String _name) {
        Vector<String> v = new Vector<String>();
        for(TMLAttribute attribute:attributes) {
            if (attribute.getName().startsWith(_name)) {
                v.add(attribute.getName());
            }
        }
        return v;
    }

    public Vector<TMLAttribute> getAllTMLAttributesStartingWith(String _name) {
        Vector<TMLAttribute> v = new Vector<TMLAttribute>();
        for(TMLAttribute attribute:attributes) {
            if (attribute.getName().startsWith(_name)) {
                v.add(attribute);
            }
        }
        return v;
    }

    public int computeMaxID() {
        int max = getID();
        if (activity != null) {
            max = Math.max(max, activity.computeMaxID());
        }
        return max;
    }

    public void computeCorrespondance(TMLElement [] _correspondance) {
        _correspondance[getID()] = this;
        if (activity != null) {
            activity.computeCorrespondance(_correspondance);
        }

    }

    public void replaceReadChannelWith(TMLChannel oldChan, TMLChannel newChan) {
	activity.replaceReadChannelWith(oldChan, newChan);
    }

    public void replaceWriteChannelWith(TMLChannel oldChan, TMLChannel newChan) {
	activity.replaceWriteChannelWith(oldChan, newChan);
    }

}
