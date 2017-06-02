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
 * Class MSC
 * Creation: 16/08/2004
 * @version 1.1 16/08/2004
 * @author Ludovic APVRILLE
 * @see 
 */

package sddescription;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MSC extends HMSCElement {
    private HMSCNode nextNode;
    private List<Evt> evts;
    private List<Order> orders;
    private List<LinkEvts> linkevts;
    private List<TimeConstraint> timeconstraints;
    
    public MSC(String _name, HMSCNode _nextNode) {
        super(_name);
        
        nextNode = _nextNode;   
        evts = new LinkedList<Evt>();
        orders = new LinkedList<Order>();
        linkevts = new LinkedList<LinkEvts>();
        timeconstraints = new LinkedList<TimeConstraint>();
    }
    
    public MSC(String _name) {
        super(_name);
        evts = new LinkedList<Evt>();
        orders = new LinkedList<Order>();
        linkevts = new LinkedList<LinkEvts>();
        timeconstraints = new LinkedList<TimeConstraint>();
    }
    
    public HMSCNode getNextNode() { return nextNode; }  
    public List<Evt> getEvts() { return evts; }
    public List<Order> getOrders() { return orders; }
    public List<LinkEvts> getLinksEvts() { return linkevts; }
    public List<TimeConstraint> getTimeConstraints() { return timeconstraints; }
    
    public void addEvt(Evt evt) {
        evts.add(evt);
    }
    
    public void setNextNode(HMSCNode _nextNode) {
        nextNode = _nextNode;   
    }
    
    public void addOrder(Order order) {
        orders.add(order);
    }
    
    public void addLinkEvts(LinkEvts le) {
        linkevts.add(le);
    }
    
    public void addTimeConstraint(TimeConstraint tc) {
        timeconstraints.add(tc);
    }
	
	public Evt getEvtByID(int _id) {
		Evt evt;
		Iterator<Evt> li = evts.listIterator();
		while(li.hasNext()) {
			evt = li.next();
			if (evt.getID() == _id) {
				return evt;
			}
		}
		return null;
	}
    
    public Evt hasExactlyOnePreviousEvt(Evt evt) {
        Evt evtret = null;
        Order order;
        Iterator<Order> li = orders.listIterator();
        
        while(li.hasNext()) {
            order = li.next();
            if (order.evt2 == evt) {
                if (evtret != null) {
                    // at least two previous events
                    return null;
                }
                evtret = order.evt1;
            }   
        }
        return evtret;
    }
    
    public boolean isEndOfExactlyOneRelativeTC(Evt evt) {
        boolean ret = false;
        TimeConstraint tc;
        Iterator<TimeConstraint> li = timeconstraints.listIterator();
        
        while(li.hasNext()) {
            tc = li.next();
            if (tc.evt2 == evt) {
                if (ret == true) {
                    // at least two tcs
                    return false;
                }
                ret = true;
            }   
        }
        return ret;
    }
	
	// Find all guards before the current evt 
	public String getElseGuard(Evt evt) {
		if (evt.getType() != Evt.ELSE_GUARD) {
			return "";
		}
		
		List<Evt> al = getEffectiveGuardEvtsBefore(evt);
		
		if ((al == null) || (al.size() == 0)) {
			return "";
		}
		
		// Concatenate guards of those events
		String guard = "";
		String g = evt.getActionId();
		for(Evt evt1: al) {
			g = evt1.getActionId().trim();
			if (g.length() > 0) {
				if (guard.length() == 0) {
					guard += evt1.getActionId();
				} else {
					guard = "(" + guard + ") or (" + g + ")";
				}
			}
		}
		if (guard.length() > 0) {
			guard = "not(" + guard + ")";
		}
		
		return guard;
	}
	
	private List<Evt> getEffectiveGuardEvtsBefore(Evt evt) {
		List<Evt> al = new ArrayList<Evt>();
		List<Evt> tmp = getAllPredecesorsOf(evt);
		
		int i;
		Evt evt1;
		
		for(i=0; i<tmp.size(); i++) {
			evt1 = tmp.get(i);
			if ((evt1 != evt) && (evt1.getType() == Evt.GUARD)) {
				// test if no [end] guard which is after evt1 but before evt
				List<Evt> intersec = getIntersecOf(tmp, getAllNextsOf(evt1));
				if (!hasEndGuard(intersec)) {
					al.add(evt1);
				}
			}
		}
		
		return al;
	}
	
	public  Evt getEndGuard(Evt evt) {
		List<Evt> al = getAllNextsOf(evt);
		List<Evt> eal = new ArrayList<Evt>();
		
		//int i;
		
		for(Evt evt1: al) {
			if (evt1.getType() == Evt.END_GUARD) {
				eal.add(evt1);
			}
		}
		
		// Assumes this is the first one of the list
		if (eal.size() == 0) {
			return null;
		}
		
		return eal.get(0);
	}
	
	public Evt getPreviousGuardEvt(Evt evt) {
		List<Evt> al = getAllPredecesorsOf(evt);
		List<Evt> eal = new ArrayList<Evt>();
		
		//int i;
		
		for(Evt evt1: al) {
			if (evt1.isAGuardEvt()) {
				eal.add(evt1);
			}
		}
		
		// Assumes this is the first one of the list
		if (eal.size() == 0) {
			return null;
		}
		
		if (eal.get(0).getType() == Evt.END_GUARD) {
			return null;
		}
		
		return eal.get(0);
	}
	
	
	private boolean hasEndGuard( List<Evt> list) {
		for(Evt evt:list) {
			if (evt.getType() == Evt.END_GUARD) {
				return true;
			}
		}
		return false;
	}
	
	private List<Evt> getAllPredecesorsOf(Evt evt) {
		List<Evt> al = getImmediatePredecessorsOf(evt);
		List<Evt> tmp;
		Evt evt1, evt2;
		int i, j;
		
		if (al.size() == 0) {
			return al;
		}
		
		for(i=0; i<al.size(); i++) {
			evt1 = al.get(i);
			tmp = getImmediatePredecessorsOf(evt1);
			for(j=0; j<tmp.size(); j++) {
				evt2 = tmp.get(j);
				if (!(al.contains(evt2))) {
					al.add(evt2);
				}
			}
		}
		
		return al;
	}
	
	private List<Evt> getImmediatePredecessorsOf(Evt evt) {
		List<Evt> al = new ArrayList<Evt>();
		Order order;
		
		Iterator<Order> iterator = orders.listIterator();
		
		while(iterator.hasNext()) {
			order = iterator.next();
			if (order.evt2 == evt) {
				if (order.evt1 != evt) {
					al.add(order.evt1);
				}
			}
		}
		
		return al;
	}
	
	private List<Evt> getAllNextsOf(Evt evt) {
		List<Evt> al = getAllImmediateNextsOf(evt);
		List<Evt> tmp;
		Evt evt1, evt2;
		int i, j;
		
		if (al.size() == 0) {
			return al;
		}
		
		for(i=0; i<al.size(); i++) {
			evt1 = al.get(i);
			tmp = getAllNextsOf(evt1);
			for(j=0; j<tmp.size(); j++) {
				evt2 = tmp.get(j);
				if (!(al.contains(evt2))) {
					al.add(evt2);
				}
			}
		}	
		return al;
	}
	
	private List<Evt> getAllImmediateNextsOf(Evt evt) {
		List<Evt> al = new ArrayList<Evt>();
		Order order;
		
		Iterator<Order> iterator = orders.listIterator();
		
		while(iterator.hasNext()) {
			order = iterator.next();
			if (order.evt1 == evt) {
				if (order.evt2 != evt) {
					al.add(order.evt2);
				}
			}
		}
		
		return al;
	}
	
	private List<Evt> getIntersecOf( List<Evt> list1, List<Evt> list2) {
		List<Evt> list = new ArrayList<Evt>();
		for(Evt evt:list1) {
			if (list2.contains(evt)) {
				list.add(evt);
			}
		}
		return list;
	}
	
	public int getOrderOfEvt(Evt _evt) {
		// Computes the nb of evts that occur in the instance before
		// the evt given as parameter
		
		List<Evt> metEvts = new ArrayList<Evt>();
		
		
		computeAllEvtsBefore(metEvts, _evt);
		
		return (metEvts.size() - 1);
	}
	
	private void computeAllEvtsBefore( List<Evt> _metEvts, Evt _evt) {
		Iterator<Order> iterator;
		Order order;
		
		_metEvts.add(_evt);
		
		iterator = orders.listIterator();
		while(iterator.hasNext()) {
			order = iterator.next();
			if(order.evt2 == _evt) {
				if (order.evt1.getInstance() == _evt.getInstance()) {
					if (!_metEvts.contains(order.evt1)) {
						computeAllEvtsBefore(_metEvts, order.evt1);
					}
				}
			}
		}
	}
	
	
}
