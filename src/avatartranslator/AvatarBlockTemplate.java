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
 * Class AvatarBlockTemplate
 * Templates of AVATAR blocks (Timers, etc.)
 * Creation: 09/07/2010
 * @version 1.0 09/07/2010
 * @author Ludovic APVRILLE
 * @see
 */


package avatartranslator;

import java.util.*;

import myutil.*;

public class AvatarBlockTemplate  {
    
    public AvatarBlockTemplate() {
    }
	
	public static AvatarBlock getTimerBlock(String _name, Object _reference) {
		AvatarBlock ab = new AvatarBlock(_name, _reference);
		
		AvatarAttribute aa = new AvatarAttribute("value", AvatarType.INTEGER, _reference);
		ab.addAttribute(aa);
		
		AvatarSignal set = new AvatarSignal("set", AvatarSignal.IN, _reference);
		AvatarSignal reset = new AvatarSignal("reset", AvatarSignal.IN, _reference);
		AvatarSignal expire = new AvatarSignal("expire", AvatarSignal.OUT, _reference);
		AvatarAttribute val = new AvatarAttribute("__value", AvatarType.INTEGER,  aa.getReferenceObject());
		set.addParameter(val);
		ab.addSignal(set);
		ab.addSignal(reset);
		ab.addSignal(expire);
		
		AvatarStateMachine asm = ab.getStateMachine();
		AvatarStartState ass = new AvatarStartState("start", _reference);
		
		asm.setStartState(ass);
		asm.addElement(ass);
		
		AvatarState as1 = new AvatarState("wait4set", _reference);
		asm.addElement(as1);
		
		AvatarState as2 = new AvatarState("wait4expire", _reference);
		asm.addElement(as2);
		
		AvatarActionOnSignal aaos1 = new AvatarActionOnSignal("set1", set, _reference);
		aaos1.addValue("value");
		asm.addElement(aaos1);
		
		AvatarActionOnSignal aaos2 = new AvatarActionOnSignal("set2", set, _reference);
		aaos2.addValue("value");
		asm.addElement(aaos2);
		
		AvatarActionOnSignal aaos3 = new AvatarActionOnSignal("reset1", reset, _reference);
		asm.addElement(aaos3);
		
		AvatarActionOnSignal aaos4 = new AvatarActionOnSignal("reset2", reset, _reference);
		asm.addElement(aaos4);
		
		AvatarActionOnSignal aaos5 = new AvatarActionOnSignal("expire", expire, _reference);
		asm.addElement(aaos5);
		
		AvatarTransition at;
		
		// set
		at = makeAvatarEmptyTransitionBetween(asm, ass, as1, _reference);
		at = makeAvatarEmptyTransitionBetween(asm, as1, aaos1, _reference);
		at = makeAvatarEmptyTransitionBetween(asm, aaos1, as2, _reference);
		
		at = makeAvatarEmptyTransitionBetween(asm, as2, aaos2, _reference);
		at = makeAvatarEmptyTransitionBetween(asm, aaos2, as2, _reference);
		
		// expire
		at = makeAvatarEmptyTransitionBetween(asm, as2, aaos5, _reference);
		at.setDelays("value", "value");
		at = makeAvatarEmptyTransitionBetween(asm, aaos5, as1, _reference);
		
		// reset
		at = makeAvatarEmptyTransitionBetween(asm, as1, aaos3, _reference);
		at = makeAvatarEmptyTransitionBetween(asm, aaos3, as1, _reference);
		
		at = makeAvatarEmptyTransitionBetween(asm, as2, aaos4, _reference);
		at = makeAvatarEmptyTransitionBetween(asm, aaos4, as1, _reference);
		
		
		return ab;
		
	}
	
	public static AvatarTransition makeAvatarEmptyTransitionBetween(AvatarStateMachine _asm, AvatarStateMachineElement _elt1, AvatarStateMachineElement _elt2, Object _reference) {
		AvatarTransition at = new AvatarTransition("tr", _reference);
		
		_asm.addElement(at);
		
		_elt1.addNext(at);
		at.addNext(_elt2);
		
		return at;
	}
}