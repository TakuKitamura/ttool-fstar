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
 * Class TMLModeling
 * Creation: 21/11/2005
 * @version 1.0 21/11/2005
 * @author Ludovic APVRILLE
 * @see
 */

package tmltranslator;

import java.util.*;
import myutil.*;
//import compiler.expression.*;


public class TMLModeling {

    private ArrayList<TMLTask> tasks;
    private ArrayList<TMLChannel> channels;
    private ArrayList<TMLRequest> requests;
    private ArrayList<TMLEvent> events;
	
	private boolean optimized = false;
	
	private String[] ops = {">", "<", "+", "-", "*", "/", "[", "]", "(", ")", ":", "=", "==", ","};
	
	private int hashCode;
	private boolean hashCodeComputed = false;
    
    public TMLModeling() {
        init();
    }
	
	public TMLModeling(boolean reset) {
        init();
		if (reset) {
			DIPLOElement.resetID();
		}
    }

    private void init() {
        /*tasks = new LinkedList();
        channels = new LinkedList();
        requests = new LinkedList();
        events = new LinkedList();*/
		tasks = new ArrayList<TMLTask>();
		channels = new ArrayList<TMLChannel>();
		events = new ArrayList<TMLEvent>();
		requests = new ArrayList<TMLRequest>();
		
    }
    
    public void addTask(TMLTask task) {
        tasks.add(task);
    }
    
    public void addChannel(TMLChannel channel) {
        channels.add(channel);
    }
    
    public void addRequest(TMLRequest request) {
        requests.add(request);
    }
    
    public void addEvent(TMLEvent event) {
        events.add(event);
    }

    public boolean checkConsistency() {
        return true;
    }
	
	public TMLTask findTMLTask(TMLActivityElement _elt) {
		 TMLTask tmp;
        for(int i=0; i<tasks.size(); i++) {
            tmp = (TMLTask)(tasks.get(i));
            if (tmp.has(_elt)) {
                return tmp;
            }
        }
        return null;
	}
	
	private void computeHashCode() {
		TMLTextSpecification tmltxt = new TMLTextSpecification("spec.tml");
		String s = tmltxt.toTextFormat(this);
		hashCode = s.hashCode();
		System.out.println("TML hashcode = " + hashCode); 
	}
	
	public int getHashCode() {
		if (!hashCodeComputed) {
			computeHashCode();
			hashCodeComputed = true;
		}
		return hashCode;
	}
    
    public String toString() {
        String s = tasksToString();
        s += channelsToString();
        s += eventsToString();
        s += requestsToString();
        return s;
    }
    
    public String tasksToString() {
        String s = "Tasks: ";
        return s+listToString(tasks) + "\n";
    }
    
    public String channelsToString() {
        String s = "Channels: ";
        return s+listToString(channels) + "\n";
    }
    
    public String eventsToString() {
        String s = "Events: ";
        return s+listToString(events) + "\n";
    }
    
    public String requestsToString() {
        String s = "Requests: ";
        return s+listToString(requests) + "\n";
    }
    
    
    public String listToString(ArrayList ll) {
        String s="";
        ListIterator iterator = ll.listIterator();
        TMLElement tmle;
        
        while(iterator.hasNext()) {
            tmle = (TMLElement)(iterator.next());
            s += tmle.getName() + " ";
        }
        return s;
    }
    
    public boolean hasSameChannelName(TMLChannel _channel) {
        TMLChannel channel;
        ListIterator iterator = channels.listIterator();
        while(iterator.hasNext()) {
            channel = (TMLChannel)(iterator.next());
            if (channel.getName().compareTo(_channel.getName()) == 0) {
                return true;
            }
        } 
        
        return false;
    }
    
    public boolean hasAlmostSimilarChannel(TMLChannel _channel) {
        TMLChannel channel;
        ListIterator iterator = channels.listIterator();
        while(iterator.hasNext()) {
            channel = (TMLChannel)(iterator.next());
            if (channel.getName().compareTo(_channel.getName()) == 0) {
                if (channel.getSize() != _channel.getSize()) {
                    return true;
                }
                if (channel.getType() != _channel.getType()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean hasSameRequestName(TMLRequest _request) {
        TMLRequest request;
        ListIterator iterator = requests.listIterator();
        while(iterator.hasNext()) {
            request = (TMLRequest)(iterator.next());
            if (request.getName().compareTo(_request.getName()) == 0) {
                return true;
            }
        } 
        return false;
    }
    
    public TMLRequest getRequestNamed(String name) {
        TMLRequest request;
        ListIterator iterator = requests.listIterator();
        while(iterator.hasNext()) {
            request = (TMLRequest)(iterator.next());
            //System.out.println("Request=" +request.getName() + " name=" + name);
            if (request.getName().compareTo(name) == 0) {
                return request;
            }
        }
        return null;
    }
    
    public boolean hasAlmostSimilarRequest(TMLRequest _request) {
        TMLRequest request;
        int i;
        
        ListIterator iterator = requests.listIterator();
        
        while(iterator.hasNext()) {
            request = (TMLRequest)(iterator.next());
            if (request.getName().compareTo(_request.getName()) == 0) {
                // must verify whether a param is different or not.
                if (request.getNbOfParams() != _request.getNbOfParams()) {
                    return true;
                }
                
                for(i=0; i<request.getNbOfParams(); i++) {
                    if (request.getType(i).getType() != _request.getType(i).getType()) {
                        return true;
                    }
                }
                
                if (request.getDestinationTask() != _request.getDestinationTask()) {
                    return true;
                }
            }
        } 
        return false;
    }
    
    public boolean hasSameEventName(TMLEvent _event) {
        TMLEvent event;
        ListIterator iterator = events.listIterator();
        while(iterator.hasNext()) {
            event = (TMLEvent)(iterator.next());
            if (event.getName().compareTo(_event.getName()) == 0) {
                return true;
            }
        } 
        return false;
    }
    
    public boolean hasAlmostSimilarEvent(TMLEvent _event) {
        TMLEvent event;
        int i;
        
        ListIterator iterator = events.listIterator();
        
        while(iterator.hasNext()) {
            event = (TMLEvent)(iterator.next());
            if (event.getName().compareTo(_event.getName()) == 0) {
                // must verify whether a param is different or not.
                if (event.getNbOfParams() != _event.getNbOfParams()) {
                    return true;
                }
                
                for(i=0; i<event.getNbOfParams(); i++) {
                    if (event.getType(i).getType() != _event.getType(i).getType()) {
                        return true;
                    }
                }
            }
        } 
        return false;
    }
    
    public TMLTask getTMLTaskByName(String _name) {
        TMLTask task;
        ListIterator iterator = tasks.listIterator();
        while(iterator.hasNext()) {
            task = (TMLTask)(iterator.next());
            if (task.getName().compareTo(_name) == 0) {
                return task;
            }
        } 
        return null;
    }
	
	public TMLElement getCommunicationElementByName(String _name) {
		TMLChannel ch = getChannelByName(_name);
		if (ch != null) {
			return ch;
		}
		
		TMLEvent evt = getEventByName(_name);
		if (evt != null) {
			return evt;
		}
		
		return getRequestByName(_name);
	}
    
    public TMLChannel getChannelByName(String _name) {
        TMLChannel ch;
        ListIterator iterator = channels.listIterator();
        while(iterator.hasNext()) {
            ch = (TMLChannel)(iterator.next());
            if (ch.getName().compareTo(_name) == 0) {
                return ch;
            }
        } 
        return null;
    }
    
    public TMLEvent getEventByName(String _name) {
        TMLEvent evt;
        ListIterator iterator = events.listIterator();
        while(iterator.hasNext()) {
            evt = (TMLEvent)(iterator.next());
            if (evt.getName().compareTo(_name) == 0) {
                return evt;
            }
        } 
        return null;
    }
    
    public TMLRequest getRequestByName(String _name) {
        TMLRequest req;
        ListIterator iterator = requests.listIterator();
        while(iterator.hasNext()) {
            req = (TMLRequest)(iterator.next());
            if (req.getName().compareTo(_name) == 0) {
                return req;
            }
        } 
        return null;
    }
    
    public ArrayList<TMLTask> getTasks() {
        return tasks;
    }
    
     public ListIterator getListIteratorTasks() {
        return tasks.listIterator();
    }
	
	 public ArrayList<TMLChannel> getChannels() {
        return channels;
    }
	
	 public ArrayList<TMLEvent> getEvents() {
        return events;
    }
	
	 public ArrayList<TMLRequest> getRequests() {
        return requests;
    }
    
     public ListIterator getListIteratorChannels() {
        return channels.listIterator();
    }
    
     public ListIterator getListIteratorEvents() {
        return events.listIterator();
    }
    
    public ListIterator getListIteratorRequests() {
        return requests.listIterator();
    }
	
	public ArrayList<TMLChannel> getChannels(TMLTask t) {
		TMLChannel ch;
		ArrayList<TMLChannel> list = new ArrayList<TMLChannel>();
		ListIterator iterator = getListIteratorChannels();
		while(iterator.hasNext()) {
			ch = (TMLChannel)(iterator.next());
			if ((ch.origin == t) || (ch.destination == t)) {
				list.add(ch);
			}
		}
		return list;
	}
	
	public ArrayList<TMLEvent> getEvents(TMLTask t) {
		TMLEvent evt;
		ArrayList<TMLEvent> list = new ArrayList<TMLEvent>();
		ListIterator iterator = getListIteratorEvents();
		while(iterator.hasNext()) {
			evt= (TMLEvent)(iterator.next());
			if ((evt.origin == t) || (evt.destination == t)) {
				list.add(evt);
			}
		}
		return list;
	}
	
	public ArrayList<TMLRequest> getRequests(TMLTask t) {
		TMLRequest request;
		ArrayList<TMLRequest> list = new ArrayList<TMLRequest>();
		ListIterator iterator = getListIteratorRequests();
		while(iterator.hasNext()) {
			request = (TMLRequest)(iterator.next());
			if ((request.getDestinationTask() == t) || (request.isAnOriginTask(t))) {
				list.add(request);
			}
		}
		return list;
	}
	
    
    public LinkedList getRequestsToMe(TMLTask task) {
        TMLRequest req;
        
        LinkedList ll = new LinkedList();
        
        // Must search each task for SendRequest operator, check the request for destination class
        ListIterator iterator= getListIteratorRequests();
        
        while (iterator.hasNext()) {
            req = (TMLRequest)(iterator.next());
            if (req.getDestinationTask() == task) {
                ll.add(req);
            }
        }
        
        return ll;
    }
	
	public void prefixAllNamesWith(String _prefix) {
		for(TMLChannel channel: channels) {
			channel.prefixName(_prefix);
		}
		
		for(TMLEvent evt: events) {
			evt.prefixName(_prefix);
		}
		
		for(TMLRequest req: requests) {
			req.prefixName(_prefix);
		}
		
		for(TMLTask task: tasks) {
			task.prefixName(_prefix);
		}
	}
	
	public void mergeWith(TMLModeling tmlm) {
		channels.addAll(tmlm.getChannels());
		events.addAll(tmlm.getEvents());
		requests.addAll(tmlm.getRequests());
		tasks.addAll(tmlm.getTasks());
	}
	
	public void sortByName() {
		ArrayList<TMLTask> ttasks = new ArrayList<TMLTask>();
		ArrayList<TMLChannel> tchannels = new ArrayList<TMLChannel>();
		ArrayList<TMLRequest> trequests = new ArrayList<TMLRequest>();
		ArrayList<TMLEvent> tevents = new ArrayList<TMLEvent>();
		
		if (channels.size() > 1) {
			while(channels.size() > 0) {
				TMLChannel ch = channels.get(0);
				for(TMLChannel channel: channels) {
					if (channel.getName().compareTo(ch.getName()) < 0) {
						ch = channel;
					}
				}
				tchannels.add(ch);
				channels.remove(ch);
			}
			channels = tchannels;
		}
		
		if (events.size() > 1) {
			while(events.size() > 0) {
				TMLEvent evt = events.get(0);
				for(TMLEvent event: events) {
					if (event.getName().compareTo(evt.getName()) < 0) {
						evt = event;
					}
				}
				tevents.add(evt);
				events.remove(evt);
			}
			events = tevents;
		}
		
		if (requests.size() > 1) {
			while(requests.size() > 0) {
				TMLRequest req = requests.get(0);
				for(TMLRequest request: requests) {
					if (request.getName().compareTo(req.getName()) < 0) {
						req = request;
					}
				}
				trequests.add(req);
				requests.remove(req);
			}
			requests = trequests;
		}
		
		if (tasks.size() > 1) {
			while(tasks.size() > 0) {
				TMLTask t = tasks.get(0);
				for(TMLTask task: tasks) {
					if (task.getName().compareTo(t.getName()) < 0) {
						t = task;
					}
				}
				ttasks.add(t);
				tasks.remove(t);
			}
			tasks = ttasks;
		}
	}
	
	public String printSummary(ArrayList<TMLError> warnings) {
		String ret = "Optimization warnings:\n";
		int cpt = 1;
		for(TMLError error: warnings) {
			ret += "Warning #" + cpt + " (task " + error.task.getName() + "): " + error.message + "\n";
			cpt ++;
		}
		ret += warnings.size() + " optimization warning(s)\n";
		return ret;
	}
	
	public ArrayList<TMLError> optimize() {
		ArrayList<TMLError> warnings = new ArrayList<TMLError>();
		if (!optimized) {
			System.out.println("Optimizing TML modeling");
			optimized = true;
			for(TMLTask task: tasks) {
				optimize(task, warnings);
			}
		}
		return warnings;
	}
	
	public void optimize(TMLTask task, ArrayList<TMLError> warnings) {
		TMLActivity activity = task.getActivityDiagram();
		optimizeVariables(task, activity, warnings);
		optimizeMergeEXECs(activity);
		optimizeMergeDELAYSs(activity);
	}
	
	/**
	 *  Search for unused or constant variables.
	 *  Ununsed variables are removed from the class.
	 *  Constant variables are also removed from the class, and each time
	 *  they are used, they are replaced by their respective value.
	 */
	public void optimizeVariables(TMLTask task,  TMLActivity activity, ArrayList<TMLError> warnings) {
		int i;
		int usage;
		TMLAttribute attr; 
		ArrayList<TMLAttribute> attributes = task.getAttributes();
		String name;
		TMLError error;
		
		//System.out.println("Analyzing attributes in " + task.getName());
		for(i=0; i<attributes.size(); i++) {
			attr = attributes.get(i);
			//System.out.println("Analyzing p=" + p.getName() + " i=" + i);
			
			if (attr.isNat() || attr.isBool()) {
				//System.out.println("Getting usage");
				name = attr.getName();
				if ((name.compareTo("arg1__req") != 0) && (name.compareTo("arg2__req") != 0) && (name.compareTo("arg3__req") != 0)){
					usage = getUsageOfAttribute(task, activity, attr);
					//System.out.println("End getting usage");
					if (usage == 0) {
						error = new TMLError(TMLError.WARNING_BEHAVIOR);
						error.message = "Attribute " + attr.getName() + " of TML task " + task.getName() + " is never used -> removing it";
						error.task = task;
						warnings.add(error);
						//System.out.println("Attribute " + attr.getName() + " of TML task " + task.getName() + " is never used -> removing it");
						attributes.remove(i);
						i=-1;
					} else if (usage ==1) {
						error = new TMLError(TMLError.WARNING_BEHAVIOR);
						error.message = "Attribute " + attr.getName() + " of TML task " + task.getName() + " is constant -> putting its value in expression where it is used, and removing it from the task";
						error.task = task;
						warnings.add(error);
						//System.out.println("Attribute " + attr.getName() + " of TML task " + task.getName() + " is constant -> putting its value in expression where it is used, and removing it from the task");
						replaceAttributeWithItsValue(task, activity, attr);
						attributes.remove(i);
						i=-1;
					}
				}
			}
		}
		//System.out.println("End analyzing attributes in " + task.getName());
	}
	
	// Returns how a given parameter is used in a class
	// 0 -> never read nor written (i.e. never used)
	// 1 -> read but never written (i.e. constant value)
	// 2 -> read and written (i.e. regular variable)
	public int getUsageOfAttribute(TMLTask task, TMLActivity activity, TMLAttribute attr) {
		// We go through the ad
		// For each component where the param may be involved, we search for it..
		
		TMLActivityElement element;
		TMLActionState tmlas;
		TMLActivityElementEvent tmlaee;
		TMLChoice choice;
		TMLActivityElementWithAction tmlaewa;
		TMLActivityElementWithIntervalAction tmlint;
		TMLForLoop tmlloop;
		TMLActivityElementChannel tmlaec;
		TMLSendRequest tmlasr;
		TMLSendEvent tmlne; 
		TMLRandom tmlrandom;
		int i, j;
		int usage = 0;
		int index;
		String s, s0, s1;
		String name = " " + attr.getName() +  " ";
		String namebis = attr.getName() +  " ";
		String nameter = attr.getName();
		
		for(i=0; i<activity.nElements(); i++) {
			element = activity.get(i);
			//System.out.println("Before element: " + element + " usage=" + usage);
			
			if (element instanceof TMLActionState) {
				tmlas = (TMLActionState)element;
				s = tmlas.getAction().trim();
				index = s.indexOf('=');
				if (index != -1) {
					s0 = s.substring(0, index);
					s0 = " " + s0.trim() + " "; 
					//System.out.println("s0=" + s0 + " name=" + name);
					if (s0.compareTo(name) == 0) {
						return 2;
					}
					
					if (usage == 0) {
						s1 = s.substring(index, s.length());
						usage = analyzeStringWithParam(s1, name);
					}
				}
			}  else if (element instanceof TMLRandom) {
				tmlrandom = (TMLRandom)element;
				s = tmlrandom.getVariable().trim();
				s0 =  " " + s.trim() + " "; 
				if (s0.compareTo(name) == 0) {
					return 2;
				}
				
				if (usage == 0) {
					s1 = tmlrandom.getMinValue().trim();
					usage = analyzeStringWithParam(s1, name);
				}
				
				if (usage == 0) {
					s1 = tmlrandom.getMaxValue().trim();
					usage = analyzeStringWithParam(s1, name);
				}
				
			}  else if (element instanceof TMLNotifiedEvent) {
				tmlaee = (TMLActivityElementEvent)element;
				s = " " + tmlaee.getVariable() + " ";
				index = analyzeStringWithParam(s, name);
				if (index == 1) {
					return 2;
				}
				
			} else if (element instanceof TMLWaitEvent) {
				tmlaee = (TMLActivityElementEvent)element;
				s = " " + tmlaee.getAllParams() + " ";
				index = analyzeStringWithParam(s, name);
				if (index == 1) {
					return 2;
				}
				
			} else if (element instanceof TMLForLoop) {
				tmlloop = (TMLForLoop)element;
				
				s = tmlloop.getInit().trim();
				index = s.indexOf('=');
				if (index != -1) {
					s0 = s.substring(0, index);
					s0 = " " + s0.trim() + " "; 
					if (s0.compareTo(name) == 0) {
						return 2;
					}
					
					if (usage == 0) {
						s1 = s.substring(index, s.length());
						usage = analyzeStringWithParam(s1, name);
					}
				}
				
				if (usage == 0) {
					s = tmlloop.getIncrement().trim();
					index = s.indexOf('=');
					if (index != -1) {
						s0 = s.substring(0, index);
						s0 = " " + s0.trim() + " "; 
						if (s0.compareTo(name) == 0) {
							return 2;
						}
						
						if (usage == 0) {
							s1 = s.substring(index, s.length());
							usage = analyzeStringWithParam(s1, name);
						}
					}
					
					if (usage == 0) {
						//System.out.println("Analyzing condition of loop");
						usage = analyzeStringWithParam(tmlloop.getCondition(), name);
					}
				}
				
			} else if (usage == 0) {
				
				if (element instanceof TMLChoice) {
					choice = (TMLChoice)element;
					for(j=0; j<choice.getNbGuard(); j++) {
						if (usage == 0) {
							usage = analyzeStringWithParam(choice.getGuard(j), name);
						}
					}
					
				} else if (element instanceof TMLActivityElementWithAction) {
					tmlaewa = (TMLActivityElementWithAction)element;
					usage = analyzeStringWithParam(tmlaewa.getAction(), name);
					
				} else if (element instanceof TMLActivityElementWithIntervalAction) {
					tmlint = (TMLActivityElementWithIntervalAction)element;
					usage = analyzeStringWithParam(tmlint.getMinDelay(), name);
					if (usage == 0) {
						usage = analyzeStringWithParam(tmlint.getMaxDelay(), name);
					}
					
				} else if (element instanceof TMLActivityElementChannel) {
					tmlaec = (TMLActivityElementChannel)element;
					usage = analyzeStringWithParam(tmlaec.getNbOfSamples(), name);
					
				} else if (element instanceof TMLSendRequest) {
					tmlasr = (TMLSendRequest)element;
					for(j=0; j<tmlasr.getNbOfParams(); j++) {
						if (usage == 0) {
							usage = analyzeStringWithParam(tmlasr.getParam(j), name);
						}
					}
				} else if (element instanceof TMLSendEvent) {
					tmlne = (TMLSendEvent)element;
					for(j=0; j<tmlne.getNbOfParams(); j++) {
						if (usage == 0) {
							usage = analyzeStringWithParam(tmlne.getParam(j), name);
						}
					}
				} 
			}
			//System.out.println("After element: " + element + " usage=" + usage);
		}
		return usage;
	}
	
	public int analyzeStringWithParam(String s, String name) {
		s = Conversion.replaceAllChar(s, ' ', "");
		s = removeAllActionOps(s);
		s = " " + s + " ";
		int index = s.indexOf(name);
		if (index > -1) {
			return 1;
		}
		return 0;
	}
	
	public String removeAllActionOps(String s) {
		return Conversion.removeAllActionOps(ops, s, " ");
	}
	
	public void replaceAttributeWithItsValue(TMLTask task, TMLActivity activity, TMLAttribute attr) {
		// We go through the ad
		// For each component where the param may be involved, we search for it..
		
		TMLActivityElement element;
		TMLActivityElementWithAction tmlaewa;
		TMLActivityElementEvent tmlaee;
		TMLActivityElementChannel tmlaec;
		TMLActivityElementWithIntervalAction tmlint;
		TMLChoice choice;
		TMLForLoop tmlloop;
		TMLSendRequest tmlasr;
		TMLRandom tmlrandom;
		int i, j;
		int usage = 0;
		int index;
		String s, s0, s1;
		String name = " " + attr.getName() +  " ";
		String namebis = attr.getName() +  " ";
		
		for(i=0; i<activity.nElements(); i++) {
			element = activity.get(i);
			if (element instanceof TMLActivityElementWithAction) {
				tmlaewa = (TMLActivityElementWithAction)element;
				tmlaewa.setAction(putAttributeValueInString(tmlaewa.getAction(), attr));
				
			} if (element instanceof TMLRandom) {
				tmlrandom = (TMLRandom)element;
				tmlrandom.setMinValue(putAttributeValueInString(tmlrandom.getMinValue(), attr));
				tmlrandom.setMaxValue(putAttributeValueInString(tmlrandom.getMaxValue(), attr));
				
			} else if (element instanceof TMLChoice) {
				choice = (TMLChoice)element;
				for(j=0; j<choice.getNbGuard(); j++) {
					if (choice.getGuard(j) != null) {
						choice.setGuardAt(j, putAttributeValueInString(choice.getGuard(j), attr));
					}
				}
				
			//ExecCInterval and ExecIInterval 
			} else if (element instanceof TMLActivityElementWithIntervalAction) {
				tmlint = (TMLActivityElementWithIntervalAction)element;
				tmlint.setMinDelay(putAttributeValueInString(tmlint.getMinDelay(), attr));
				tmlint.setMaxDelay(putAttributeValueInString(tmlint.getMaxDelay(), attr));
				
			} else if (element instanceof TMLForLoop) {
				tmlloop = (TMLForLoop)element;
				tmlloop.setInit(putAttributeValueInString(tmlloop.getInit(), attr));
				tmlloop.setCondition(putAttributeValueInString(tmlloop.getCondition(), attr));
				tmlloop.setIncrement(putAttributeValueInString(tmlloop.getIncrement(), attr));
				
			} else if (element instanceof TMLActivityElementChannel) {
				tmlaec = (TMLActivityElementChannel)element;
				tmlaec.setNbOfSamples(putAttributeValueInString(tmlaec.getNbOfSamples(), attr));
				
			} else if (element instanceof TMLActivityElementEvent) {
				tmlaee = (TMLActivityElementEvent)element;
				for(j=0; j<tmlaee.getNbOfParams(); j++) {
					tmlaee.setParam(putAttributeValueInString(tmlaee.getParam(j), attr), j);
				}
				
			} else if (element instanceof TMLSendRequest) {
				tmlasr = (TMLSendRequest)element;
				for(j=0; j<tmlasr.getNbOfParams(); j++) {
					tmlasr.setParam(putAttributeValueInString(tmlasr.getParam(j), attr), j);
				}
			}
		}
	}
	
	
	public String putAttributeValueInString(String s, TMLAttribute attr) {
		String newValue;
		
		if (attr.hasInitialValue()) {
			newValue = attr.getInitialValue();
		} else {
			newValue = attr.getDefaultInitialValue();
		}
		
		return Conversion.putVariableValueInString(ops, s, attr.getName(), newValue);
	}
	
	
	/**
	 *  Concatenate EXECI and EXEC operations
	 */
	 public void optimizeMergeEXECs(TMLActivity activity) {
		 TMLActivityElement elt0, elt1;
		 String action0, action1;
		 TMLExecIInterval int0, int1;
		 TMLExecCInterval exec0, exec1;
		 
		 for(int i=0; i<activity.nElements(); i++) {
			 elt0 = activity.get(i);
			 if ((elt0 instanceof TMLExecI) && (elt0.getNbNext() == 1)) {
				 elt1 = elt0.getNextElement(0);
				 if (elt1 instanceof TMLExecI) {
					 // Concate both elements 
					 concateActivityElementWithActions(activity, (TMLActivityElementWithAction)elt0, (TMLActivityElementWithAction)elt1);
					 
					 // We restart from the beginning
					 i = -1;
				 }
				 if (elt1 instanceof TMLExecIInterval) {
					 // We delete the second i.e. elt1
					 activity.removeElement(elt0);
					 
					 // We link the first one to the nexts of the second one
					 setNewNexts(activity, elt0, elt1);
					 
					 // We modify the value of elt1
					 int1 = (TMLExecIInterval)elt1;
					 int1.setMinDelay(addActions(((TMLActivityElementWithAction)elt0).getAction(), int1.getMinDelay()));
					 int1.setMaxDelay(addActions(((TMLActivityElementWithAction)elt0).getAction(), int1.getMaxDelay()));
					 
					 i = -1;
				 }
			 } 
			 
			  if ((elt0 instanceof TMLExecIInterval) && (elt0.getNbNext() == 1)) {
				 elt1 = elt0.getNextElement(0);
				 if (elt1 instanceof TMLExecI) {
					 // We delete the second i.e. elt1
					activity.removeElement(elt1);
					
					// We link the first one to the nexts of the second one
					elt0.setNexts(elt1.getNexts());
					
					// We modify the value of elt0
					 int1 = (TMLExecIInterval)elt0;
					 int1.setMinDelay(addActions(((TMLActivityElementWithAction)elt1).getAction(), int1.getMinDelay()));
					 int1.setMaxDelay(addActions(((TMLActivityElementWithAction)elt1).getAction(), int1.getMaxDelay()));
					
					 i = -1;
				 }
				 
				 if (elt1 instanceof TMLExecIInterval) {
					 // We delete the second i.e. elt1
					activity.removeElement(elt1);
					
					// We link the first one to the nexts of the second one
					elt0.setNexts(elt1.getNexts());
					
					// We modify the value of elt0
					 int1 = (TMLExecIInterval)elt1;
					 int0 = (TMLExecIInterval)elt0;
					 int0.setMinDelay(addActions(int0.getMinDelay(), int1.getMinDelay()));
					 int0.setMaxDelay(addActions(int0.getMaxDelay(), int1.getMaxDelay()));
					
					 i = -1;
				 }
			 }
			 
			 if ((elt0 instanceof TMLExecC) && (elt0.getNbNext() == 1)) {
				 elt1 = elt0.getNextElement(0);
				 if (elt1 instanceof TMLExecC) {
					 // Concate both elements 
					 concateActivityElementWithActions(activity, (TMLActivityElementWithAction)elt0, (TMLActivityElementWithAction)elt1);
					 
					 // We restart from the beginning
					 i = -1;
				 }
				 
				  if (elt1 instanceof TMLExecCInterval) {
					 // We delete the second i.e. elt1
					 activity.removeElement(elt0);
					 
					 // We link the first one to the nexts of the second one
					 setNewNexts(activity, elt0, elt1);
					 
					 // We modify the value of elt1
					 exec1 = (TMLExecCInterval)elt1;
					 exec1.setMinDelay(addActions(((TMLActivityElementWithAction)elt0).getAction(), exec1.getMinDelay()));
					 exec1.setMaxDelay(addActions(((TMLActivityElementWithAction)elt0).getAction(), exec1.getMaxDelay()));
					 
					 i = -1;
				 }
			 }
			 
			 if ((elt0 instanceof TMLExecCInterval) && (elt0.getNbNext() == 1)) {
				 elt1 = elt0.getNextElement(0);
				 if (elt1 instanceof TMLExecC) {
					 // We delete the second i.e. elt1
					activity.removeElement(elt1);
					
					// We link the first one to the nexts of the second one
					elt0.setNexts(elt1.getNexts());
					
					// We modify the value of elt0
					 exec1 = (TMLExecCInterval)elt0;
					 exec1.setMinDelay(addActions(((TMLActivityElementWithAction)elt1).getAction(), exec1.getMinDelay()));
					 exec1.setMaxDelay(addActions(((TMLActivityElementWithAction)elt1).getAction(), exec1.getMaxDelay()));
					
					 i = -1;
				 }
				 
				 if (elt1 instanceof TMLExecCInterval) {
					 // We delete the second i.e. elt1
					activity.removeElement(elt1);
					
					// We link the first one to the nexts of the second one
					elt0.setNexts(elt1.getNexts());
					
					// We modify the value of elt0
					 exec1 = (TMLExecCInterval)elt1;
					 exec0 = (TMLExecCInterval)elt0;
					 exec0.setMinDelay(addActions(exec0.getMinDelay(), exec1.getMinDelay()));
					 exec0.setMaxDelay(addActions(exec0.getMaxDelay(), exec1.getMaxDelay()));
					
					 i = -1;
				 }
			 }
			 
			 
			 
		 }
	 }
	 
	 /**
	 *  Concatenate Delay operations
	 */
	 public void optimizeMergeDELAYSs(TMLActivity activity) {
		 TMLActivityElement elt0, elt1;
		 String action0, action1;
		 TMLDelay del0, del1;
		 
		 
		 for(int i=0; i<activity.nElements(); i++) {
			 elt0 = activity.get(i);
			 if ((elt0 instanceof TMLDelay) && (elt0.getNbNext() == 1)) {
				 elt1 = elt0.getNextElement(0);
				 if (elt1 instanceof TMLDelay) {
					 del1 = (TMLDelay)elt1;
					 del0 = (TMLDelay)elt0;
					 
					 if (del1.getUnit().equals(del0.getUnit())) {
						 // We delete the second i.e. elt1
						 activity.removeElement(elt1);
						 
						 // We link the first one to the nexts of the second one
						 elt0.setNexts(elt1.getNexts());
						 
						 // We modify the value of elt0
						 del0.setMinDelay(addActions(del0.getMinDelay(), del1.getMinDelay()));
						 del0.setMaxDelay(addActions(del0.getMaxDelay(), del1.getMaxDelay()));
						 
						 i = -1;
					 }
				 } 
			 }
		 }
	 }
	 
	
	 public void setNewNexts(TMLActivity activity, TMLActivityElement elt0, TMLActivityElement elt1) {
		 if (elt0 == elt1) {
			 return;
		 }
		 
		 TMLActivityElement elt;
		 for(int i=0; i<activity.nElements(); i++) {
			 elt = activity.get(i);
			 if (elt != elt0) {
				 elt.setNewNext(elt0, elt1);
			 }
		 }
	 }
	
	public void concateActivityElementWithActions(TMLActivity activity, TMLActivityElementWithAction elt0, TMLActivityElementWithAction elt1) {
		// We delete the second i.e. elt1
		activity.removeElement(elt1);
		
		// We link the first one to the nexts of the second one
		elt0.setNexts(elt1.getNexts());
		
		// We modify the value of elt0
		elt0.setAction(addActions(elt0.getAction(), elt1.getAction()));
	}
	
	
	public String addActions(String action0, String action1) {
		try {
			int val0 = Integer.decode(action0).intValue();
			int val1 = Integer.decode(action1).intValue();
			return "" + (val0 + val1);
		} catch (Exception e) {
			return "(" + action0 + ")+(" + action1 + ")";
		}
	}
  
}