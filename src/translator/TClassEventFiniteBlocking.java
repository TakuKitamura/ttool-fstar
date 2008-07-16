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
 * Class TClassEventFiniteBlocking
 * Creation: 26/04/2007
 * @version 1.0 26/04/2007
 * @author Ludovic APVRILLE
 * @see 
 */

package translator;

import java.util.*;

public class TClassEventFiniteBlocking extends TClass implements TClassEventCommon, FIFOFiniteAndGetSizeTClass {
	private int nbPara;
	private LinkedList sendReqGates;
	private LinkedList waitGates;
	private LinkedList sizeGates;
	private int counterW, counterR, counterS;
	private int maxSamples;
	private String eventName;

	public TClassEventFiniteBlocking(String name, String _eventName, int _nbPara, int _maxSamples) {
		super(name, true);
		nbPara = _nbPara;
		//System.out.println("Nb of params:" +nbPara);
		maxSamples = _maxSamples;
		sendReqGates = new LinkedList();
		waitGates = new LinkedList();
		sizeGates = new LinkedList();
		eventName = _eventName;
	}

	public boolean isBlocking() {
		return true;
	}

	public int getNbPara() {
		return nbPara;
	}

	public int getMaxSamples() {
		return maxSamples;
	}

	public Gate getGateWrite() {
		return (Gate)(sendReqGates.get(0));
	}

	public Gate getGateRead() {
		return (Gate)(waitGates.get(0));
	}

	public Gate getGateSize() {
		return (Gate)(sizeGates.get(0));
	}

	public LinkedList getGatesWrite() {
		return sendReqGates;
	}

	public LinkedList getGatesRead() {
		return waitGates;
	}

	public LinkedList getGatesSize() {
		return sizeGates;
	}



	public Gate addWriteGate() {
		Gate g;
		if (counterW == 0) {
			g = addNewGateIfApplicable("notify__" + eventName);
			sendReqGates.add(g);
		} else {
			g = addNewGateIfApplicable("notify__" + eventName + counterW);
			sendReqGates.add(g);
		}
		counterW ++;
		return g;
	}

	public Gate addReadGate() {
		Gate g;
		if (counterR == 0) {
			g = addNewGateIfApplicable("wait__" + eventName);
			waitGates.add(g);
		} else {
			g = addNewGateIfApplicable("wait__" + eventName + counterR);
			waitGates.add(g);
		}
		counterR ++;
		return g;
	}

	public Gate addSizeGate() {
		Gate g;
		if (counterS == 0) {
			g = addNewGateIfApplicable("notified__" + eventName);
			sizeGates.add(g);
		} else {
			g = addNewGateIfApplicable("notified__" + eventName + counterS);
			sizeGates.add(g);
		}
		counterS ++;
		return g;
	}

	public void makeTClass() {
		//System.out.println("toto1");
		//System.out.println("Nb of params:" +nbPara);

		Gate forward_0, forward_1, g;
		ADActionStateWithGate adag;
		//ADActionStateWithGate adagsize1, adagsize2, adagsize3;
		ADActionStateWithParam adac1;
		ADParallel adpar0, adpar1;
		ADStop adstop;
		ADJunction adj1, adj2, adj3, adj4, adj5, adj6, adj7;
		ADChoice adch1, adch2, adch3, adch4, adch6, adch7;
		Param p1, p2, p3, p10, p20, p30, index, index_r, nb, maxs;
		//String value;
		int i;
		ListIterator iterator;
		String action;

		ActivityDiagram ad = new ActivityDiagram();
		setActivityDiagram(ad);

		// Case where not input or output requests...
		if ((sendReqGates.size() == 0) || (waitGates.size() == 0)) {
			adstop = new ADStop();
			ad.add(adstop);
			ad.getStartState().addNext(adstop);
			return;
		}

		p1 = new Param("p0", Param.NAT, "0");
		p2 = new Param("p1", Param.NAT, "0");
		p3 = new Param("p2", Param.NAT, "0");
		p10 = new Param("p00", Param.NAT, "0");
		p20 = new Param("p10", Param.NAT, "0");
		p30 = new Param("p20", Param.NAT, "0");

		if (nbPara > 0) {
			addParameter(p1);
			addParameter(p10);
		}
		if (nbPara > 1) {
			addParameter(p2);
			addParameter(p20);
		}
		if (nbPara > 2) {
			addParameter(p3);
			addParameter(p30);
		}

		nb = new Param("nb", Param.NAT, "0");
		addParameter(nb);

		maxs = new Param("maxs", Param.NAT, ""+maxSamples);
		addParameter(maxs);

		index = new Param("index", Param.NAT, "0");
		addParameter(index);

		index_r = new Param("index_r", Param.NAT, "0");
		addParameter(index_r);

		forward_0 = addNewGateIfApplicable("forward_0");
		forward_1 = addNewGateIfApplicable("forward_1");

		adpar0 = new ADParallel();
		adpar0.setValueGate("[forward_0, forward_1]");
		ad.add(adpar0);
		ad.getStartState().addNext(adpar0);


		// Left branch of the main parallel -> storing data in order
		adj1 = new ADJunction();
		ad.add(adj1);
		adpar0.addNext(adj1);

		adag = new ADActionStateWithGate(forward_0);
		ad.add(adag);
		action = "";
		for(i=0; i<nbPara; i++) {
			action += "?p" + i + "0:nat";
		}
		action += "?index:nat";
		adag.setActionValue(action);
		adj1.addNext(adag);

		adpar1 = new ADParallel();
		adpar1.setValueGate("[]");
		ad.add(adpar1);
		adag.addNext(adpar1);

		adag = new ADActionStateWithGate(forward_1);
		ad.add(adag);
		action = "";
		for(i=0; i<nbPara; i++) {
			action += "!p" + i + "0";
		}
		action += "!index";
		adag.setActionValue(action);
		adpar1.addNext(adag);

		adstop = new ADStop();
		ad.add(adstop);
		adag.addNext(adstop);

		adpar1.addNext(adj1);

		// Second branch -> interaction with external classes

		adj2 = new ADJunction();
		ad.add(adj2);
		adpar0.addNext(adj2);

		adch1 = new ADChoice();
		ad.add(adch1);
		adj2.addNext(adch1);

		// Notify -> to know whether an event is available, or not
		if (sizeGates.size() >0) {
			adch2 = new ADChoice();
			ad.add(adch2);
			adch1.addNext(adch2);
			adch1.addGuard("[]");

			adj3 = new ADJunction();
			ad.add(adj3);
			adj3.addNext(adj2);

			iterator = sizeGates.listIterator();
			while(iterator.hasNext()) {
				g = (Gate)(iterator.next());
				adag = new ADActionStateWithGate(g);
				ad.add(adag);
				adag.setActionValue("!1");
				adch2.addNext(adag);
				adch2.addGuard("[nb>0]");
				adag.addNext(adj3);
				adag = new ADActionStateWithGate(g);
				ad.add(adag);
				adag.setActionValue("!0");
				adch2.addNext(adag);
				adch2.addGuard("[nb==0]");
				adag.addNext(adj3);
			}
		}
		// Sent event
		adch3 = new ADChoice();
		ad.add(adch3);
		adch1.addNext(adch3);
		adch1.addGuard("[]");

		adj4 = new ADJunction();
		ad.add(adj4);

		iterator = sendReqGates.listIterator();
		while(iterator.hasNext()) {
			g = (Gate)(iterator.next());
			adag = new ADActionStateWithGate(g);
			ad.add(adag);
			action = "";
			for(i=0; i<nbPara; i++) {
				action += "?p" + i + "0:nat";
			}
			adag.setActionValue(action);
			adch3.addNext(adag);
			adch3.addGuard("[nb<maxs]");
			adag.addNext(adj4);
		}

		adag = new ADActionStateWithGate(forward_0);
		ad.add(adag);
		action = "";
		for(i=0; i<nbPara; i++) {
			action += "!p" + i + "0";
		}
		action+="!index";
		adag.setActionValue(action);
		adj4.addNext(adag);

		adac1 = new ADActionStateWithParam(index);
		ad.add(adac1);
		adac1.setActionValue("index+1");
		adag.addNext(adac1);

		adch4 = new ADChoice();
		ad.add(adch4);
		adac1.addNext(adch4);

		adj5 = new ADJunction();
		ad.add(adj5);
		adj5.addNext(adj2);

		adj6 = new ADJunction();
		ad.add(adj6);
		adch4.addNext(adj6);
		adch4.addGuard("[nb==maxs]");

		adag = new ADActionStateWithGate(forward_1);
		ad.add(adag);
		action = "";
		for(i=0; i<nbPara; i++) {
			action += "?p" + i + ":nat";
		}
		action += "!index_r";
		adag.setActionValue(action);
		adj6.addNext(adag);

		adac1 = new ADActionStateWithParam(index_r);
		ad.add(adac1);
		adac1.setActionValue("index_r+1");
		adag.addNext(adac1);
		adac1.addNext(adj5);

		adac1 = new ADActionStateWithParam(nb);
		ad.add(adac1);
		adac1.setActionValue("nb+1");
		adch4.addNext(adac1);
		adch4.addGuard("[not(nb==maxs)]");

		adch6 = new ADChoice();
		ad.add(adch6);
		adac1.addNext(adch6);

		adch6.addNext(adj5);
		adch6.addGuard("[not(nb==1)]");
		adch6.addNext(adj6);
		adch6.addGuard("[nb==1]");

		// Wait event branch
		adj7 = new ADJunction();
		ad.add(adj7);

		iterator = waitGates.listIterator();
		while(iterator.hasNext()) {
			g = (Gate)(iterator.next());
			adag = new ADActionStateWithGate(g);
			ad.add(adag);
			action = "";
			for(i=0; i<nbPara; i++) {
				action += "!p" + i + "";
			}
			adag.setActionValue(action);
			adch3.addNext(adag);
			adch3.addGuard("[nb>0]");
			adag.addNext(adj7);
		}

		adac1 = new ADActionStateWithParam(nb);
		ad.add(adac1);
		adac1.setActionValue("nb-1");
		adj7.addNext(adac1);

		adch7 = new ADChoice();
		ad.add(adch7);
		adac1.addNext(adch7);

		adch7.addNext(adj6);
		adch7.addGuard("[not(nb==0)]");
		adch7.addNext(adj5);
		adch7.addGuard("[nb==0]");

	}

}  
