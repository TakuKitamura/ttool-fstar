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


package avatartranslator;

import myutil.TraceManager;
import ui.TGComponent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


/**
 * Class AvatarStateMachineElement
 * Creation: 20/05/2010
 *
 * @author Ludovic APVRILLE
 * @version 1.0 20/05/2010
 */
public abstract class AvatarStateMachineElement extends AvatarElement {

    protected List<AvatarStateMachineElement> nexts;
    private AvatarState myState;

    private boolean isCheckable;
    private boolean isChecked;
    private boolean canBeVerified; //Right or not to check liveness / reachability / etc.

    private boolean isHidden = false;

    public AvatarStateMachineElement(	final String _name, 
    									final Object _referenceObject ) {
    	this( _name, _referenceObject, false, false );
    }

    public AvatarStateMachineElement(	String _name,
    									Object _referenceObject,
    									boolean _isCheckable,
    									final boolean _isChecked ) {
        super( _name, _referenceObject );
        
        nexts = new LinkedList<AvatarStateMachineElement>();
        isCheckable = _isCheckable;
        canBeVerified = false;
        isChecked = _isChecked;
    }

    public void setAsVerifiable(boolean _canBeVerified) {
        canBeVerified = _canBeVerified;
    }

    public boolean canBeVerified() {
        return canBeVerified;
    }

    public void setCheckable() {
        this.isCheckable = true;
    }
    public void setNotCheckable() {
        this.isCheckable = false;
    }

    public boolean isCheckable() {
        return this.isCheckable;
    }

    public void setChecked() {
        this.isChecked = true;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void addNext(AvatarStateMachineElement _element) {
        if (_element != null) {
            nexts.add(_element);
        }
    }

    public List<AvatarStateMachineElement> getNexts() {
        return this.nexts;
    }

    public AvatarStateMachineElement getNext(int _index) {
        if (_index < nexts.size()) {
            return nexts.get(_index);
        }
        return null;
    }

    // Returns the next encountered states amons the next (of the next ...)
    // taking only the first next each time
    public AvatarStateElement getNextState(int _maxNbOfIterations) {
        if (this instanceof AvatarStateElement) {
            return (AvatarStateElement) this;
        }

        if (_maxNbOfIterations <= 0) {
            return null;
        }

        if (nexts == null) {
            return null;
        }
        if (nexts.size() < 1) {
            return null;
        }

        return nexts.get(0).getNextState(_maxNbOfIterations);
    }


    public void removeNext(int _index) {
        if (_index < nexts.size()) {
            nexts.remove(_index);
        }
    }

    public void setHidden(boolean _b) {
        isHidden = _b;
    }

    public boolean isHidden() {
        return isHidden;
    }


    public void setState(AvatarState _as) {
        myState = _as;
    }

    public AvatarState getState() {
        return myState;
    }

    public boolean hasInStrictUpperState(AvatarState _as) {
        if (getState() != null) {
            return getState().hasInUpperState(_as);
        }

        return false;
    }

    public boolean hasInUpperState(AvatarState _as) {
        if (getState() == _as) {
            return true;
        }

        if (getState() != null) {
            return getState().hasInUpperState(_as);
        }

        return false;
    }

    public boolean inAnUpperStateOf(AvatarState _state) {
        if (_state == null) {
            return false;
        }

        AvatarState as = getState();
        if (as == null) {
            return true;
        }

        while ((_state = _state.getState()) != null) {
            if (_state == as) {
                return true;
            }
        }

        return false;

    }

    @Override
    public String toString() {
        return toString(null);
    }

    protected String toString(String val) {
        String ret = getExtendedName() + " ID=" + getID();
        if (myState == null) {
            ret += " / top level operator\n";
        } else {
            ret += " / in state " + myState.getName() + " ID=" + myState.getID() + "\n";
        }

        if (val != null) {
            ret += " value:" + val + "\n";
        }

        ret += " nexts= ";
        int cpt = 0;
        for (AvatarStateMachineElement element : nexts) {
            if (element != null) {
                ret += cpt + ":" + element.getName() + "/ ID=" + element.getID() + " ";
                cpt++;
            }
        }

        ret += specificToString();

        return ret;
    }

    public String getExtendedName() {
        return getName();
    }

    public String specificToString() {
        return "";
    }

    public int nbOfNexts() {
        return nexts.size();
    }

    public boolean hasNext(AvatarStateMachineElement _elt) {
        return nexts.contains(_elt);
    }

    public void removeNext(AvatarStateMachineElement _elt) {
        nexts.remove(_elt);
    }

    public void replaceAllNext(AvatarStateMachineElement oldone, AvatarStateMachineElement newone) {
        if (nexts.contains(oldone)) {
            List<AvatarStateMachineElement> oldnexts = nexts;
            nexts = new LinkedList<AvatarStateMachineElement>();
            for (AvatarStateMachineElement elt : oldnexts) {
                if (elt == oldone) {
                    nexts.add(newone);
                } else {
                    nexts.add(oldone);
                }
            }
        }
    }

    public void removeAllNexts() {
        nexts.clear();
    }

    public boolean followedWithAnActionOnASignal() {
        AvatarStateMachineElement element = getNext(0);
        if (element == null) {
            return false;
        }

        return (element instanceof AvatarActionOnSignal);
    }

    public abstract AvatarStateMachineElement basicCloneMe(AvatarStateMachineOwner _block);

    public void fillAdvancedValues(AvatarStateMachineElement asme, HashMap<AvatarStateMachineElement, AvatarStateMachineElement> correspondenceMap,
     AvatarStateMachine mch                              ) {
        // Fill all reference elements
        cloneLinkToReferenceObjects(asme);

        // Fill basic attributes
        asme.setState(getState());
        if (isCheckable()) {
            asme.setCheckable();
        }

        if (isChecked()) {
            asme.setChecked();
        }
        asme.setHidden(isHidden());

        // Fill the nexts
        for (AvatarStateMachineElement next : nexts) {
            AvatarStateMachineElement newNext = correspondenceMap.get(next);
            if (newNext != null) {
                asme.addNext(newNext);
            } else {
                TraceManager.addDev("Null next for " + next.toString());
                if (mch != null) {
                    TraceManager.addDev("State machine contains next? " + mch.elements.contains(next));



                }
            }
        }
    }


    // Guard with an id and not(id)
    public boolean hasElseChoiceType1() {
        if (nexts.size() != 2)
            return false;

        AvatarStateMachineElement elt1, elt2;

        elt1 = getNext(0);
        elt2 = getNext(1);

        if ((!(elt1 instanceof AvatarTransition)) || (!(elt2 instanceof AvatarTransition)))
            return false;

        AvatarTransition at1, at2;

        at1 = (AvatarTransition) elt1;
        at2 = (AvatarTransition) elt2;

        if ((!(at1.isGuarded())) || (!(at2.isGuarded())))
            return false;

        AvatarGuard g1 = at1.getGuard();
        AvatarGuard g2 = at2.getGuard();

        if (g1.isElseGuard() || g2.isElseGuard())
            return true;

        if (g1 instanceof AvatarSimpleGuardDuo && g2 instanceof AvatarSimpleGuardDuo) {
            AvatarSimpleGuardDuo gg1 = (AvatarSimpleGuardDuo) g1;
            AvatarSimpleGuardDuo gg2 = (AvatarSimpleGuardDuo) g2;

            if (gg1.getBinaryOp() != gg2.getBinaryOp()) {
                gg1 = new AvatarSimpleGuardDuo(gg1.getTermA(), gg1.getTermB(), gg2.getBinaryOp());

                String s1, s2;
                s1 = myTrim(gg1.getRealGuard(this).toString());
                s2 = myTrim(gg2.getRealGuard(this).toString());

                return s1.equals(s2);
            }

            return false;
        }

        String s1, s2;
        s1 = myTrim(g1.getRealGuard(this).toString());
        s2 = myTrim(((AvatarComposedGuard) g2).getOpposite().toString());

        return s1.equals(s2);
    }

    private static String myTrim(String toBeTrimmed) {
        int length = toBeTrimmed.length();
        String tmp = toBeTrimmed.trim();
        if (tmp.startsWith("(")) {
            tmp = tmp.substring(1, tmp.length());
        }
        if (tmp.endsWith(")")) {
            tmp = tmp.substring(0, tmp.length() - 1);
        }
        if (tmp.length() != length) {
            return myTrim(tmp);
        }
        return tmp;

    }

    public boolean hasBreakpoint() {
        if (referenceObject == null) {
            return false;
        }

        if (referenceObject instanceof TGComponent) {
            TGComponent tgc = (TGComponent) referenceObject;
            return tgc.getBreakpoint();
        }

        return false;

    }

    public abstract String getNiceName();

    public abstract void translate(AvatarTranslator translator, Object arg);
}
