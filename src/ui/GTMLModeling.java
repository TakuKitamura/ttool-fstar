/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
*
* /**
* Class GTMLModeling
* Use to translate graphical TML modeling to  "tmlmodeling"
* Creation: 23/11/2005
* @version 1.0 23/11/2005
* @author Ludovic APVRILLE
* @see
*/


package ui;

import java.util.*;

import ui.tmlad.*;
import ui.tmlcd.*;
import ui.tmlcompd.*;
import ui.tmldd.*;
import tmltranslator.*;
import myutil.*;

public class GTMLModeling  {
    private TMLDesignPanel tmldp;
	private TMLComponentDesignPanel tmlcdp;
    private TMLModeling tmlm;
    private Vector checkingErrors, warnings;
    private LinkedList tasksToTakeIntoAccount;
	private LinkedList componentsToTakeIntoAccount;
    private LinkedList components;
    private LinkedList removedChannels, removedRequests, removedEvents;
	private CorrespondanceTGElement listE;
	private Hashtable<String, String> table;
	
	private TMLArchiPanel tmlap;
	//private ArrayList<HwNode> nodesToTakeIntoAccount;
	private LinkedList nodesToTakeIntoAccount;
	TMLMapping map;
	TMLArchitecture archi;
    
    public GTMLModeling(TMLDesignPanel _tmldp) {
        tmldp = _tmldp;
    }
	
	public GTMLModeling(TMLComponentDesignPanel _tmlcdp) {
        tmlcdp = _tmlcdp;
		table = new Hashtable<String, String>();
    }
	
	public GTMLModeling(TMLArchiPanel _tmlap) {
		tmlap = _tmlap;
	}
    
	public TMLModeling translateToTMLModeling() {
		return translateToTMLModeling(false);
	}
	
    public TMLModeling translateToTMLModeling(boolean onlyTakenIntoAccount) {
        tmlm = new TMLModeling();
        checkingErrors = new Vector();
        warnings = new Vector();
		listE = new CorrespondanceTGElement();
        //boolean b;
        
		if (tmldp != null) {
			components = tmldp.tmltdp.getComponentList();
			if (tasksToTakeIntoAccount == null) {
				tasksToTakeIntoAccount = components;
			}
			removedChannels = new LinkedList();
			removedRequests = new LinkedList();
			removedEvents = new LinkedList();
			
			try {
				addTMLTasks();
				addTMLChannels();
				addTMLEvents();
				addTMLRequests();
				generateTasksActivityDiagrams();
			} catch (MalformedTMLDesignException mtmlde) {
				System.out.println("Modeling error:" + mtmlde.getMessage());
			}
			
			/*TMLTextSpecification spec = new TMLTextSpecification();
			spec.toTextFormat(tmlm);
			System.out.println("TMLModeling=\n" + spec.toString());*/
			
			// Cheking syntax
			TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmlm);
			syntax.checkSyntax();
			
			CheckingError ce;
			int type;
			TGComponent tgc;
			
			if (syntax.hasErrors() >0) {
				for(TMLError error: syntax.getErrors()) {
					//System.out.println("Adding checking error");
					if (error.type == TMLError.ERROR_STRUCTURE) {
						type = CheckingError.STRUCTURE_ERROR;
					} else {
						type = CheckingError.BEHAVIOR_ERROR;
					}
					ce = new CheckingError(type, error.message);
					tgc = listE.getTG(error.element);
					ce.setTDiagramPanel(tgc.getTDiagramPanel());
					ce.setTGComponent(tgc);
					ce.setTMLTask(error.task);
					checkingErrors.add(ce);
					
				}
			}
		} else if (tmlcdp != null) {
			if (onlyTakenIntoAccount) {
				components = componentsToTakeIntoAccount;
			} else {
				components = tmlcdp.tmlctdp.getPrimitiveComponentList();
				if (componentsToTakeIntoAccount == null) {
					componentsToTakeIntoAccount = components;
				}
			}
			
			removedChannels = new LinkedList();
			removedRequests = new LinkedList();
			removedEvents = new LinkedList();
			
			try {
				addTMLComponents();
				addTMLCChannels();
				addTMLCEvents();
				addTMLCRequests();
				generateTasksActivityDiagrams();
			} catch (MalformedTMLDesignException mtmlde) {
				System.out.println("Modeling error:" + mtmlde.getMessage());
			}
		}
		
        return tmlm;
    }
	
	public CorrespondanceTGElement getCorrespondanceTable() {
		return listE;
	}
    
    public void setTasks(Vector tasks) {
        tasksToTakeIntoAccount = new LinkedList(tasks);
    }
	
	public void setComponents(Vector components) {
        componentsToTakeIntoAccount = new LinkedList(components);
    }
	
	public void setNodes(Vector nodes) {
        nodesToTakeIntoAccount = new LinkedList(nodes);
    }
    
    public Vector getCheckingErrors() {
        return checkingErrors;
    }
    
    public Vector getCheckingWarnings() {
        return warnings;
    }
    
    private void addTMLTasks() throws MalformedTMLDesignException {
        TGComponent tgc;
        TMLTask tmlt;
        TMLTaskOperator tmlto;
        TMLActivityDiagramPanel tmladp;
        
        ListIterator iterator = tasksToTakeIntoAccount.listIterator();
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TMLTaskOperator) {
                tmlto = (TMLTaskOperator)tgc;
                tmladp = tmldp.getTMLActivityDiagramPanel(tmlto.getValue());
                if (tmladp == null) {
                    String msg = tmlto.getValue() + " has no activity diagram";
                    CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, msg);
                    ce.setTDiagramPanel(tmldp.tmltdp);
                    ce.setTGComponent(tgc);
                    checkingErrors.add(ce);
                    throw new MalformedTMLDesignException(tmlto.getValue() + " msg");
                }
                tmlt = new TMLTask(tmlto.getValue(), tmlto, tmladp);
				listE.addCor(tmlt, tgc);
                tmlm.addTask(tmlt);
                tmlt.setExit(tmlto.isExit());
                addAttributesTo(tmlt, tmlto);
            }
        }
    }
	
	private void addTMLComponents() throws MalformedTMLDesignException {
        TGComponent tgc;
		TMLCPrimitiveComponent tmlcpc;
        TMLActivityDiagramPanel tmladp;
		TMLTask tmlt;
        
        ListIterator iterator = componentsToTakeIntoAccount.listIterator();
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TMLCPrimitiveComponent) {
                tmlcpc = (TMLCPrimitiveComponent)tgc;
                tmladp = tmlcdp.getReferencedTMLActivityDiagramPanel(tmlcpc.getValue());
                if (tmladp == null) {
                    String msg = " has no activity diagram";
                    CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, msg);
                    ce.setTDiagramPanel(tmlcdp.tmlctdp);
                    ce.setTGComponent(tgc);
                    checkingErrors.add(ce);
                    throw new MalformedTMLDesignException(tmlcpc.getValue() + " msg");
                }
                tmlt = new TMLTask(tmlcpc.getValue(), tmlcpc, tmladp);
				System.out.println("Task added:" + tmlcpc.getValue());
				listE.addCor(tmlt, tgc);
                tmlm.addTask(tmlt);
				tmlt.setExit(false);
                //tmlt.setExit(tmlcpc.isExit());
                addAttributesTo(tmlt, tmlcpc);
            }
        }
    }
    
    private void addTMLChannels() throws MalformedTMLDesignException {
        TGComponent tgc;
        TMLChannelOperator tmlco;
        ListIterator iterator = components.listIterator();
        TMLTaskInterface t1, t2;
        TMLChannel channel;
        TMLTask tt1, tt2;
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TMLChannelOperator) {
                tmlco = (TMLChannelOperator)tgc;
                //System.out.println("Found channel: " + tmlco.getChannelName());
                t1 = tmldp.tmltdp.getTask1ToWhichIamConnected(tmlco);
                t2 = tmldp.tmltdp.getTask2ToWhichIamConnected(tmlco);
                if ((t1 != null) && (t2 != null) && (tasksToTakeIntoAccount.contains(t1)) && (tasksToTakeIntoAccount.contains(t2)) ) {
                    channel = new TMLChannel(tmlco.getChannelName(), tmlco);
                    channel.setSize(tmlco.getChannelSize());
                    channel.setType(tmlco.getChannelType());
                    channel.setMax(tmlco.getChannelMax());
                    if (tmlm.hasSameChannelName(channel)) {
                        if (tmlm.hasAlmostSimilarChannel(channel)) {
                            String msg = " channel " + tmlco.getChannelName() + " is declared several times differently";
                            CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, msg);
                            ce.setTDiagramPanel(tmldp.tmltdp);
                            ce.setTGComponent(tgc);
                            checkingErrors.add(ce);
                            throw new MalformedTMLDesignException(tmlco.getChannelName() + " msg");
                        }
                    } else {
						tt1 = tmlm.getTMLTaskByName(t1.getTaskName());
						tt2 = tmlm.getTMLTaskByName(t2.getTaskName());
						channel.setTasks(tt1, tt2);
						tmlm.addChannel(channel);
						listE.addCor(channel, tgc);
                        //System.out.println("Adding channel " + channel.getName());
                    }
                } else {
                    removedChannels.add(new String(tmlco.getChannelName()));
                    CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Channel " +  tmlco.getChannelName() + " has been removed");
                    ce.setTDiagramPanel(tmldp.tmltdp);
                    ce.setTGComponent(tmlco);
                    warnings.add(ce);
                }
            }
        }
    }
    
    private void addTMLEvents() throws MalformedTMLDesignException {
        TGComponent tgc;
        TMLEventOperator tmleo;
        ListIterator iterator = components.listIterator();
        TMLTaskInterface t1, t2;
        TMLEvent event;
        TType tt;
        TMLType tmlt;
        TMLTask tt1, tt2;
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TMLEventOperator) {
                tmleo = (TMLEventOperator)tgc;
                //System.out.println("Found event: " + tmleo.getEventName());
                t1 = tmldp.tmltdp.getTask1ToWhichIamConnected(tmleo);
                t2 = tmldp.tmltdp.getTask2ToWhichIamConnected(tmleo);
                if ((t1 != null) && (t2 != null) && (tasksToTakeIntoAccount.contains(t1)) && (tasksToTakeIntoAccount.contains(t2)) ) {
                    event = new TMLEvent(tmleo.getEventName(), tmleo, tmleo.getMaxSamples(), tmleo.isBlocking());
                    for(int i=0; i<tmleo.getEventMaxParam(); i++) {
                        tt = tmleo.getParamAt(i);
                        if ((tt != null) && (tt.getType() != TType.NONE)) {
                            tmlt = new TMLType(tt.getType());
                            event.addParam(tmlt);
							//System.out.println("Event " + event.getName() + " add param");
                        }
                    }
                    if (tmlm.hasSameEventName(event)) {
                        if (tmlm.hasAlmostSimilarEvent(event)) {
                            String msg = " event " + tmleo.getEventName() + " is declared several times differently";
                            CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, msg);
                            ce.setTDiagramPanel(tmldp.tmltdp);
                            ce.setTGComponent(tgc);
                            checkingErrors.add(ce);
                            throw new MalformedTMLDesignException(tmleo.getEventName() + " msg");
                        }
                    } else {
						tt1 = tmlm.getTMLTaskByName(t1.getTaskName());
						tt2 = tmlm.getTMLTaskByName(t2.getTaskName());
						event.setTasks(tt1, tt2);
                        tmlm.addEvent(event);
						listE.addCor(event, tgc);
                        //System.out.println("Adding event " + event.getName());
                    }
                } else {
                    removedEvents.add(new String(tmleo.getEventName()));
                    CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Event " +  tmleo.getEventName() + " has been removed");
                    ce.setTDiagramPanel(tmldp.tmltdp);
                    ce.setTGComponent(tmleo);
                    warnings.add(ce);
                }
            }
        }
    }
    
    private void addTMLRequests() throws MalformedTMLDesignException {
        TGComponent tgc;
        TMLRequestOperator tmlro;
        ListIterator iterator = components.listIterator();
        TMLTaskInterface t1, t2;
        TMLRequest request;
        TType tt;
        TMLType tmlt;
		TMLTask task;
		TMLAttribute tmlattr;
		TMLType tmltt;
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TMLRequestOperator) {
                tmlro = (TMLRequestOperator)tgc;
                //System.out.println("Found request: " + tmlro.getRequestName());
                t1 = tmldp.tmltdp.getTask1ToWhichIamConnected(tmlro);
                t2 = tmldp.tmltdp.getTask2ToWhichIamConnected(tmlro);
                if ((t1 != null) && (t2 != null) && (tasksToTakeIntoAccount.contains(t1)) && (tasksToTakeIntoAccount.contains(t2)) ) {
					if ((request = tmlm.getRequestNamed(tmlro.getRequestName())) == null) {
						request = new TMLRequest(tmlro.getRequestName(), tmlro);
						request.setDestinationTask(tmlm.getTMLTaskByName(t2.getTaskName()));
						tmlm.addRequest(request);
						for(int i=0; i<tmlro.getRequestMaxParam(); i++) {
							tt = tmlro.getParamAt(i);
							if ((tt != null) && (tt.getType() != TType.NONE)) {
								tmlt = new TMLType(tt.getType());
								request.addParam(tmlt);
							}
						}
					}
					
					// More: test the compatibility of the request!
					if (request.getDestinationTask() != tmlm.getTMLTaskByName(t2.getTaskName())) {
						String msg = "request " + tmlro.getRequestName() + " is declared several times differently";
						CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, msg);
						ce.setTDiagramPanel(tmldp.tmltdp);
						ce.setTGComponent(tgc);
						checkingErrors.add(ce);
						throw new MalformedTMLDesignException(tmlro.getRequestName() + " msg");
					}
                    request.addOriginTask(tmlm.getTMLTaskByName(t1.getTaskName()));
					
					task = tmlm.getTMLTaskByName(t2.getTaskName());
                    task.setRequested(true);
                    task.setRequest(request);
					
					// Request attributes
					System.out.println("Requests attributes");
					for(int j=0; j<request.getNbOfParams(); j++) {
						tmltt = new TMLType(request.getType(j).getType());
						tmlattr = new TMLAttribute("arg" + (j + 1) + "__req", tmltt);
						System.out.println("Adding " + tmlattr.getName() + " to " + task.getName());
						task.addAttribute(tmlattr);
					}
					
					
                } else {
                    removedRequests.add(new String(tmlro.getRequestName()));
                    CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Event " +  tmlro.getRequestName() + " has been removed");
                    ce.setTDiagramPanel(tmldp.tmltdp);
                    ce.setTGComponent(tmlro);
                    warnings.add(ce);
                }
            }
        }
    }
	
	private void addTMLCChannels() throws MalformedTMLDesignException {
		TGComponent tgc;
        TMLCPrimitiveComponent tmlc;
        ListIterator iterator = components.listIterator();
		ListIterator li, li2;
		LinkedList ports, portstome;
		String name, name1, name2;
		TMLCPrimitivePort port1, port2;
		
		int j;
		
        //TMLTaskInterface t1, t2;
        TMLChannel channel;
        TMLTask tt1, tt2;
        
		System.out.println("*** Adding channels ***");
		
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
			if (tgc instanceof TMLCPrimitiveComponent) {
				tmlc = (TMLCPrimitiveComponent)tgc;
				System.out.println("Component:" + tmlc.getValue());
				ports = tmlc.getAllChannelsOriginPorts();
				System.out.println("Ports size:" + ports.size());
				li = ports.listIterator();
				while(li.hasNext()) {
					port1 = (TMLCPrimitivePort)(li.next());
					portstome = tmlcdp.tmlctdp.getPortsConnectedTo(port1, componentsToTakeIntoAccount);
					System.out.println("Considering port1 = " +port1.getPortName() + " size of connecting ports:" + portstome.size());
					
					ListIterator ite = portstome.listIterator();
					while(ite.hasNext()) {
						System.out.println("port=" + ((TMLCPrimitivePort)(ite.next())).getPortName());
					}
					
					if (portstome.size() != 1) {
						String msg = "port " + port1.getPortName() + " is not correctly connected";
						CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, msg);
						ce.setTDiagramPanel(tmlcdp.tmlctdp);
						ce.setTGComponent(tgc);
						checkingErrors.add(ce);
						throw new MalformedTMLDesignException(msg);
					}
					port2 = (TMLCPrimitivePort)(portstome.get(0));
					
					String []text1 = port1.getPortName().split(",");
					String []text2 = port2.getPortName().split(",");
					
					for (j=0; j<Math.min(text1.length, text2.length); j++) {
						name1 = text1[j].trim();
						name2 = text2[j].trim();
						
						if (name1.equals(name2)) {
							name = name1;
						} else {
							name = name1 + "__" + name2;
						}
						addToTable(port1.getFather().getValue() + "/" + name1, name);
						addToTable(port2.getFather().getValue() + "/" + name2, name);
						
						channel = new TMLChannel(name, port1);
						channel.setSize(port1.getSize());
						channel.setMax(port1.getMax());
						if (port1.isBlocking() && port2.isBlocking()) {
							channel.setType(TMLChannel.BRBW);
						} else if (!port1.isBlocking() && port2.isBlocking()) {
							channel.setType(TMLChannel.BRNBW);
						} else if (!port1.isBlocking() && !port2.isBlocking()) {
							channel.setType(TMLChannel.NBRNBW);
						} else {
							String msg = "Ports " + name1 + " and " + name2 + " are not compatible (NBRBW)";
							CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, msg);
							ce.setTDiagramPanel(tmlcdp.tmlctdp);
							ce.setTGComponent(port1);
							checkingErrors.add(ce);
							throw new MalformedTMLDesignException(msg);
						}
						
						if (tmlm.hasSameChannelName(channel)) {
							if (tmlm.hasAlmostSimilarChannel(channel)) {
								String msg = " channel " + name + " is declared several times differently";
								CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, msg);
								ce.setTDiagramPanel(tmlcdp.tmlctdp);
								ce.setTGComponent(tgc);
								checkingErrors.add(ce);
								throw new MalformedTMLDesignException(msg);
							}
						} else {
							tt1 = tmlm.getTMLTaskByName(port1.getFather().getValue());
							tt2 = tmlm.getTMLTaskByName(port2.getFather().getValue());
							channel.setTasks(tt1, tt2);
							tmlm.addChannel(channel);
							listE.addCor(channel, tgc);
							System.out.println("Adding channel " + channel.getName());
						}
					}
				}
			}
		}
	}
	
	private void addTMLCEvents() throws MalformedTMLDesignException {
		TGComponent tgc;
        TMLCPrimitiveComponent tmlc;
        ListIterator iterator = components.listIterator();
		ListIterator li, li2;
		LinkedList ports, portstome;
		String name;
		TMLCPrimitivePort port1, port2;
		
		int i, j;
		String name1, name2;
		
        //TMLTaskInterface t1, t2;
        TMLEvent event;
        TMLTask tt1, tt2;
		TType tt;
		TMLType tmlt;
        
		System.out.println("*** Adding Events ***");
		
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
			if (tgc instanceof TMLCPrimitiveComponent) {
				tmlc = (TMLCPrimitiveComponent)tgc;
				System.out.println("Component:" + tmlc.getValue());
				ports = tmlc.getAllEventsOriginPorts();
				System.out.println("Ports size:" + ports.size());
				li = ports.listIterator();
				while(li.hasNext()) {
					port1 = (TMLCPrimitivePort)(li.next());
					portstome = tmlcdp.tmlctdp.getPortsConnectedTo(port1, componentsToTakeIntoAccount);
					System.out.println("Considering port1 = " +port1.getPortName() + " size of connecting ports:" + portstome.size());
					
					ListIterator ite = portstome.listIterator();
					while(ite.hasNext()) {
						System.out.println("port=" + ((TMLCPrimitivePort)(ite.next())).getPortName());
					}
					
					if (portstome.size() != 1) {
						String msg = "port " + port1.getPortName() + " is not correctly connected";
						CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, msg);
						ce.setTDiagramPanel(tmlcdp.tmlctdp);
						ce.setTGComponent(tgc);
						checkingErrors.add(ce);
						throw new MalformedTMLDesignException(msg);
					}
					port2 = (TMLCPrimitivePort)(portstome.get(0));
					
					String []text1 = port1.getPortName().split(",");
					String []text2 = port2.getPortName().split(",");
					
					/*for (i=0; i<text1.length; i++) {
						System.out.println("text1[" + i + "] = " + text1[i]);
					}
					
					for (i=0; i<text2.length; i++) {
						System.out.println("text2[" + i + "] = " + text2[i]);
					}*/
					
					for (j=0; j<Math.min(text1.length, text2.length); j++) {
						name1 = text1[j].trim();
						name2 = text2[j].trim();
						//System.out.println("name1=" + name1 + " name2=" + name2);
						if (name1.equals(name2)) {
							name = name1;
						} else {
							name = name1 + "__" + name2;
						}
						addToTable(port1.getFather().getValue() + "/" + name1, name);
						addToTable(port2.getFather().getValue() + "/" + name2, name);
						
						if (port1.isFinite()) {
							event = new TMLEvent(name, port1, port1.getMax(), port1.isBlocking());
						} else {
							event = new TMLEvent(name, port1, -1, port1.isBlocking());
						}
						for(i=0; i<port1.getNbMaxAttribute(); i++) {
							tt = port1.getParamAt(i);
							if ((tt != null) && (tt.getType() != TType.NONE)) {
								tmlt = new TMLType(tt.getType());
								event.addParam(tmlt);
								//System.out.println("Event " + event.getName() + " add param");
							}
						}
						
						if (tmlm.hasSameEventName(event)) {
							if (tmlm.hasAlmostSimilarEvent(event)) {
								String msg = " event " + name + " is declared several times differently";
								CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, msg);
								ce.setTDiagramPanel(tmlcdp.tmlctdp);
								ce.setTGComponent(tgc);
								checkingErrors.add(ce);
								throw new MalformedTMLDesignException(msg);
							}
						} else {
							tt1 = tmlm.getTMLTaskByName(port1.getFather().getValue());
							tt2 = tmlm.getTMLTaskByName(port2.getFather().getValue());
							event.setTasks(tt1, tt2);
							tmlm.addEvent(event);
							listE.addCor(event, tgc);
							System.out.println("Adding event " + event.getName());
						}
					}
				}
			}
		}
	}
	
	private void addTMLCRequests() throws MalformedTMLDesignException {
		TGComponent tgc;
        TMLCPrimitiveComponent tmlc;
        ListIterator iterator = components.listIterator();
		ListIterator li, li2;
		LinkedList ports, portstome;
		String name;
		TMLCPrimitivePort port1, port2, port3;
		
        //TMLTaskInterface t1, t2;
        TMLRequest request;
        TMLTask tt1, tt2;
		TType tt;
		TMLType tmlt;
        
		System.out.println("*** Adding requests ***");
		
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
			if (tgc instanceof TMLCPrimitiveComponent) {
				tmlc = (TMLCPrimitiveComponent)tgc;
				System.out.println("Component:" + tmlc.getValue());
				ports = tmlc.getAllRequestsDestinationPorts();
				System.out.println("Ports size:" + ports.size());
				li = ports.listIterator();
				while(li.hasNext()) {
					port1 = (TMLCPrimitivePort)(li.next());
					portstome = tmlcdp.tmlctdp.getPortsConnectedTo(port1, componentsToTakeIntoAccount);
					System.out.println("Considering port1 = " +port1.getPortName() + " size of connecting ports:" + portstome.size());
					
					ListIterator ite = portstome.listIterator();
					while(ite.hasNext()) {
						System.out.println("port=" + ((TMLCPrimitivePort)(ite.next())).getPortName());
					}
					
					if (portstome.size() == 0) {
						String msg = "port " + port1.getPortName() + " is not correctly connected";
						CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, msg);
						ce.setTDiagramPanel(tmlcdp.tmlctdp);
						ce.setTGComponent(port1);
						checkingErrors.add(ce);
						throw new MalformedTMLDesignException(msg);
					}
					
					for(int i=0; i<portstome.size(); i++) {
						port3 = (TMLCPrimitivePort)(portstome.get(i));
						if (!port3.isOrigin()) {
							String msg = "port " + port1.getPortName() + " is not correctly connected to port " + port3.getName();
							CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, msg);
							ce.setTDiagramPanel(tmlcdp.tmlctdp);
							ce.setTGComponent(port1);
							checkingErrors.add(ce);
							throw new MalformedTMLDesignException(msg);
						}
					}
					
					name = port1.getFather().getValue() + "__" + port1.getPortName();
					
					addToTable(port1.getFather().getValue() + "/" + port1.getPortName(), name);
					
					for(int i=0; i<portstome.size(); i++) {
						port2 = (TMLCPrimitivePort)(portstome.get(i));
						addToTable(port2.getFather().getValue() + "/" + port2.getPortName(), name);
					}
					
					
					request = new TMLRequest(name, port1);
					
                    for(int i=0; i<port1.getNbMaxAttribute(); i++) {
                        tt = port1.getParamAt(i);
                        if ((tt != null) && (tt.getType() != TType.NONE)) {
                            tmlt = new TMLType(tt.getType());
                            request.addParam(tmlt);
							//System.out.println("Event " + event.getName() + " add param");
                        }
                    }
					
					
					if (tmlm.hasSameRequestName(request)) {
                        if (tmlm.hasAlmostSimilarRequest(request)) {
                            String msg = " request " + name + " is declared several times differently";
                            CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, msg);
                            ce.setTDiagramPanel(tmlcdp.tmlctdp);
							ce.setTGComponent(port1);
							checkingErrors.add(ce);
							throw new MalformedTMLDesignException(msg);
                        }
                    } else {
						tt1 = tmlm.getTMLTaskByName(port1.getFather().getValue());
						tt1.setRequested(true);
						tt1.setRequest(request);
						request.setDestinationTask(tt1);
						for(int i=0; i<portstome.size(); i++) {
							port2 = (TMLCPrimitivePort)(portstome.get(i));
							tt2 = tmlm.getTMLTaskByName(port2.getFather().getValue());
							request.addOriginTask(tt2);
						}
						tmlm.addRequest(request);
						listE.addCor(request, tgc);
                        System.out.println("Adding request " + request.getName());
                    }
				}
			}
		}
	}
    
    private void addAttributesTo(TMLTask tmltask, TMLTaskOperator tmlto) {
        Vector attributes = tmlto.getAttributes();
        addAttributesTo(tmltask, attributes);
	}
	
	private void addAttributesTo(TMLTask tmltask, TMLCPrimitiveComponent tmlcpc) {
        Vector attributes = tmlcpc.getAttributes();
        addAttributesTo(tmltask, attributes);
	}
	
	private void addAttributesTo(TMLTask tmltask, Vector attributes) {
        TAttribute ta;
        TMLType tt;
        String name;
        TMLAttribute tmlt;
		TMLRequest req;
        
        for(int i=0; i<attributes.size(); i++) {
            ta = (TAttribute)(attributes.elementAt(i));
            if (ta.getType() == TAttribute.NATURAL) {
                tt = new TMLType(TMLType.NATURAL);
            } else if (ta.getType() == TAttribute.BOOLEAN) {
                tt = new TMLType(TMLType.BOOLEAN);
            } else {
                tt = new TMLType(TMLType.OTHER);
            }
            tmlt = new TMLAttribute(ta.getId(), tt);
            tmlt.initialValue = ta.getInitialValue();
            tmltask.addAttribute(tmlt);
        }
		
    }
    
    private void generateTasksActivityDiagrams() throws MalformedTMLDesignException {
        TMLTask tmltask;
        ListIterator iterator = tmlm.getTasks().listIterator();
        
        while(iterator.hasNext()) {
            tmltask = (TMLTask)(iterator.next());
            generateTaskActivityDiagrams(tmltask);
        }
        
        if (checkingErrors.size() > 0) {
            throw new MalformedTMLDesignException("Error(s) found in activity diagrams");
        }
    }
	
	private String modifyActionString(String _input) {
		int index = _input.indexOf("++");
		boolean b1, b2;
		String tmp;
		
		if(index > -1) {
			tmp = _input.substring(0, index).trim();
			
			b1 = (tmp.substring(0,1)).matches("[a-zA-Z]");
			b2 = tmp.matches("\\w*");
			if (b1 && b2) {
				return tmp + " = " + tmp + " + 1";
			}
		}
		
		index = _input.indexOf("--");
		if(index > -1) {
			tmp = _input.substring(0, index).trim();
			
			b1 = (tmp.substring(0,1)).matches("[a-zA-Z]");
			b2 = tmp.matches("\\w*");
			if (b1 && b2) {
				return tmp + " = " + tmp + " - 1";
			}
		}
		
		return _input;
	}
    
    private void generateTaskActivityDiagrams(TMLTask tmltask) throws MalformedTMLDesignException {
        TMLActivity activity = tmltask.getActivityDiagram();
        TMLActivityDiagramPanel tadp = (TMLActivityDiagramPanel)(activity.getReferenceObject());
		
        // search for start state
        LinkedList list = tadp.getComponentList();
        Iterator iterator = list.listIterator();
        TGComponent tgc;
        TMLADStartState tss = null;
        int cptStart = 0;
        boolean rndAdded = false;
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TMLADStartState) {
                tss = (TMLADStartState) tgc;
                cptStart ++;
            }
        }
        
        if (tss == null) {
            CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "No start state in the TML activity diagram of " + tmltask.getName());
            ce.setTMLTask(tmltask);
            ce.setTDiagramPanel(tadp);
            checkingErrors.add(ce);
            return;
        }
        
        if (cptStart > 1) {
            CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "More than one start state in the TML activity diagram of " + tmltask.getName());
            ce.setTMLTask(tmltask);
            ce.setTDiagramPanel(tadp);
            checkingErrors.add(ce);
            return;
        }
        
        // Adding start state
        TMLStartState tmlss = new TMLStartState("start", tss);
        activity.setFirst(tmlss);
        
        // Creation of other elements
        TMLChannel channel;
        TMLEvent event;
        TMLRequest request;
        
		TMLADRandom tmladrandom;
		TMLRandom tmlrandom;
        TMLActionState tmlaction;
        TMLChoice tmlchoice;
        TMLExecI tmlexeci;
        TMLExecIInterval tmlexecii;  
		TMLExecC tmlexecc;
        TMLExecCInterval tmlexecci;
        TMLForLoop tmlforloop;
        TMLReadChannel tmlreadchannel;
        TMLSendEvent tmlsendevent;
        TMLSendRequest tmlsendrequest;
        TMLStopState tmlstopstate;
        TMLWaitEvent tmlwaitevent;
        TMLNotifiedEvent tmlnotifiedevent;
        TMLWriteChannel tmlwritechannel;
        TMLSequence tmlsequence;
        TMLSelectEvt tmlselectevt;
		int staticLoopIndex = 0;
		String sl = "", tmp;
		TMLType tt;
		TMLAttribute tmlt;
        
        iterator = list.listIterator();
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TMLADActionState) {
                tmlaction = new TMLActionState("action", tgc);
				tmp = ((TMLADActionState)(tgc)).getAction();
				tmp = modifyActionString(tmp);
                tmlaction.setAction(tmp);
                activity.addElement(tmlaction);
				listE.addCor(tmlaction, tgc);
            } else if (tgc instanceof TMLADRandom) {
				tmladrandom = (TMLADRandom)tgc;
                tmlrandom = new TMLRandom("random" + tmladrandom.getValue(), tgc);
				tmp = tmladrandom.getVariable();
				tmp = modifyActionString(tmp);
                tmlrandom.setVariable(tmp);
				tmp = tmladrandom.getMinValue();
				tmp = modifyActionString(tmp);
                tmlrandom.setMinValue(tmp);
				tmp = tmladrandom.getMaxValue();
				tmp = modifyActionString(tmp);
                tmlrandom.setMaxValue(tmp);
				tmlrandom.setFunctionId(tmladrandom.getFunctionId());
                activity.addElement(tmlrandom);
				listE.addCor(tmlrandom, tgc);
			} else if (tgc instanceof TMLADChoice) {
                tmlchoice = new TMLChoice("choice", tgc);
                // Guards are added at the same time as next activities
                activity.addElement(tmlchoice);
				listE.addCor(tmlchoice, tgc);
            } else if (tgc instanceof TMLADSelectEvt) {
                tmlselectevt = new TMLSelectEvt("select", tgc);
                activity.addElement(tmlselectevt);
				listE.addCor(tmlselectevt, tgc);
            } else if (tgc instanceof TMLADExecI) {
                tmlexeci = new TMLExecI("execi", tgc);
                tmlexeci.setAction(((TMLADExecI)tgc).getDelayValue());
                activity.addElement(tmlexeci);
				listE.addCor(tmlexeci, tgc);
            } else if (tgc instanceof TMLADExecIInterval) {
                tmlexecii = new TMLExecIInterval("execi", tgc);
                tmlexecii.setMinDelay(((TMLADExecIInterval)tgc).getMinDelayValue());
                tmlexecii.setMaxDelay(((TMLADExecIInterval)tgc).getMaxDelayValue());
                activity.addElement(tmlexecii);
				listE.addCor(tmlexecii, tgc);
            } else if (tgc instanceof TMLADExecC) {
                tmlexecc = new TMLExecC("execc", tgc);
                tmlexecc.setAction(((TMLADExecC)tgc).getDelayValue());
                activity.addElement(tmlexecc);
				listE.addCor(tmlexecc, tgc);
            } else if (tgc instanceof TMLADExecCInterval) {
                tmlexecci = new TMLExecCInterval("execci", tgc);
                tmlexecci.setMinDelay(((TMLADExecCInterval)tgc).getMinDelayValue());
                tmlexecci.setMaxDelay(((TMLADExecCInterval)tgc).getMaxDelayValue());
                activity.addElement(tmlexecci);
				listE.addCor(tmlexecci, tgc);
            } else if (tgc instanceof TMLADForLoop) {
                tmlforloop = new TMLForLoop("loop", tgc);
                tmlforloop.setInit(((TMLADForLoop)tgc).getInit());
                tmlforloop.setCondition(((TMLADForLoop)tgc).getCondition());
                tmlforloop.setIncrement(modifyActionString(((TMLADForLoop)tgc).getIncrement()));
                activity.addElement(tmlforloop);
				listE.addCor(tmlforloop, tgc);
				
            } else if (tgc instanceof TMLADForStaticLoop) {
				sl = "loop__" + staticLoopIndex;
                tt = new TMLType(TMLType.NATURAL);
				tmlt = new TMLAttribute(sl, tt);
				tmlt.initialValue = "0";
				tmltask.addAttribute(tmlt);
                tmlforloop = new TMLForLoop(sl, tgc);
                tmlforloop.setInit(sl + " = 0");
                tmlforloop.setCondition(sl + "<" + tgc.getValue());
				System.out.println("Condition=" + tmlforloop.getCondition());
                tmlforloop.setIncrement(sl + " = " + sl + " + 1");
                activity.addElement(tmlforloop);
				listE.addCor(tmlforloop, tgc);
				staticLoopIndex++;
				
            } else if (tgc instanceof TMLADSequence) {
                tmlsequence = new TMLSequence("seq", tgc);
                activity.addElement(tmlsequence);
				listE.addCor(tmlsequence, tgc);
				
            } else if (tgc instanceof TMLADReadChannel) {
                // Get the channel
				channel = tmlm.getChannelByName(getFromTable(tmltask, ((TMLADReadChannel)tgc).getChannelName()));
				
                if (channel == null) {
                    if (Conversion.containsStringInList(removedChannels, ((TMLADReadChannel)tgc).getChannelName())) {
                        CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "A call to " + ((TMLADReadChannel)tgc).getChannelName() + " has been removed because the corresponding channel is not taken into account");
                        ce.setTMLTask(tmltask);
                        ce.setTDiagramPanel(tadp);
                        ce.setTGComponent(tgc);
                        warnings.add(ce);
                        activity.addElement(new TMLJunction("void junction", tgc));
                    } else {
                        CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADReadChannel)tgc).getChannelName() + " is an unknown channel");
                        ce.setTMLTask(tmltask);
                        ce.setTDiagramPanel(tadp);
                        ce.setTGComponent(tgc);
                        checkingErrors.add(ce);
                    }
                } else {
                    tmlreadchannel = new TMLReadChannel("read channel", tgc);
                    tmlreadchannel.setNbOfSamples(((TMLADReadChannel)tgc).getSamplesValue());
                    tmlreadchannel.setChannel(channel);
                    activity.addElement(tmlreadchannel);
					listE.addCor(tmlreadchannel, tgc);
                }
            } else if (tgc instanceof TMLADSendEvent) {
                event = tmlm.getEventByName(getFromTable(tmltask, ((TMLADSendEvent)tgc).getEventName()));
                if (event == null) {
                    if (Conversion.containsStringInList(removedEvents, ((TMLADSendEvent)tgc).getEventName())) {
                        CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "A call to " + ((TMLADSendEvent)tgc).getEventName() + " has been removed because the corresponding event is not taken into account");
                        ce.setTMLTask(tmltask);
                        ce.setTDiagramPanel(tadp);
                        ce.setTGComponent(tgc);
                        warnings.add(ce);
                        activity.addElement(new TMLJunction("void junction", tgc));
                    } else {
                        CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADSendEvent)tgc).getEventName() + " is an unknown event");
                        ce.setTMLTask(tmltask);
                        ce.setTDiagramPanel(tadp);
                        ce.setTGComponent(tgc);
                        checkingErrors.add(ce);
                    }
                } else {
					if (event.getNbOfParams() != ((TMLADSendEvent)tgc).realNbOfParams()) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADSendEvent)tgc).getEventName() + ": wrong number of parameters");
                        ce.setTMLTask(tmltask);
                        ce.setTDiagramPanel(tadp);
                        ce.setTGComponent(tgc);
                        checkingErrors.add(ce);
					} else {
						tmlsendevent = new TMLSendEvent("send event", tgc);
						tmlsendevent.setEvent(event);
						for(int i=0; i<((TMLADSendEvent)tgc).nbOfParams(); i++) {
							tmlsendevent.addParam(((TMLADSendEvent)tgc).getParamValue(i));
						}
						activity.addElement(tmlsendevent);
						listE.addCor(tmlsendevent, tgc);
					}
                }
            } else if (tgc instanceof TMLADSendRequest) {
                request = tmlm.getRequestByName(getFromTable(tmltask, ((TMLADSendRequest)tgc).getRequestName()));
                if (request == null) {
                    if (Conversion.containsStringInList(removedRequests, ((TMLADSendRequest)tgc).getRequestName())) {
                        CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "A call to " + ((TMLADSendRequest)tgc).getRequestName() + " has been removed because the corresponding request is not taken into account");
                        ce.setTMLTask(tmltask);
                        ce.setTDiagramPanel(tadp);
                        ce.setTGComponent(tgc);
                        warnings.add(ce);
                        activity.addElement(new TMLJunction("void junction", tgc));
                    } else {
                        CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADSendRequest)tgc).getRequestName() + " is an unknown request");
                        ce.setTMLTask(tmltask);
                        ce.setTDiagramPanel(tadp);
                        ce.setTGComponent(tgc);
                        checkingErrors.add(ce);
                    }
                } else {
					if (request.getNbOfParams() != ((TMLADSendRequest)tgc).realNbOfParams()) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADSendRequest)tgc).getRequestName() + ": wrong number of parameters");
                        ce.setTMLTask(tmltask);
                        ce.setTDiagramPanel(tadp);
                        ce.setTGComponent(tgc);
                        checkingErrors.add(ce);
					} else {
						tmlsendrequest = new TMLSendRequest("send request", tgc);
						tmlsendrequest.setRequest(request);
						for(int i=0; i<((TMLADSendRequest)tgc).nbOfParams(); i++) {
							tmlsendrequest.addParam(((TMLADSendRequest)tgc).getParamValue(i));
						}
						activity.addElement(tmlsendrequest);
						listE.addCor(tmlsendrequest, tgc);
					}
                }
            } else if (tgc instanceof TMLADStopState) {
                tmlstopstate = new TMLStopState("stop state", tgc);
                activity.addElement(tmlstopstate);
				listE.addCor(tmlstopstate, tgc);
				
			} else if (tgc instanceof TMLADNotifiedEvent) {
				event = tmlm.getEventByName(getFromTable(tmltask, ((TMLADNotifiedEvent)tgc).getEventName()));
                if (event == null) {
                    if (Conversion.containsStringInList(removedEvents, ((TMLADNotifiedEvent)tgc).getEventName())) {
                        CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "A call to " + ((TMLADNotifiedEvent)tgc).getEventName() + " has been removed because the corresponding event is not taken into account");
                        ce.setTMLTask(tmltask);
                        ce.setTDiagramPanel(tadp);
                        ce.setTGComponent(tgc);
                        warnings.add(ce);
                        activity.addElement(new TMLJunction("void junction", tgc));
                    } else {
                        CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADNotifiedEvent)tgc).getEventName() + " is an unknown event");
                        ce.setTMLTask(tmltask);
                        ce.setTDiagramPanel(tadp);
                        ce.setTGComponent(tgc);
                        checkingErrors.add(ce);
                    }
                } else {
                    event.setNotified(true);
                    tmlnotifiedevent = new TMLNotifiedEvent("notified event", tgc);
                    tmlnotifiedevent.setEvent(event);
                    tmlnotifiedevent.setVariable(((TMLADNotifiedEvent)tgc).getVariable());
                    activity.addElement(tmlnotifiedevent);
					listE.addCor(tmlnotifiedevent, tgc);
                }
				
            } else if (tgc instanceof TMLADWaitEvent) {
				event = tmlm.getEventByName(getFromTable(tmltask, ((TMLADWaitEvent)tgc).getEventName()));
                if (event == null) {
                    if (Conversion.containsStringInList(removedEvents, ((TMLADWaitEvent)tgc).getEventName())) {
                        CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "A call to " + ((TMLADWaitEvent)tgc).getEventName() + " has been removed because the corresponding event is not taken into account");
                        ce.setTMLTask(tmltask);
                        ce.setTDiagramPanel(tadp);
                        ce.setTGComponent(tgc);
                        warnings.add(ce);
                        activity.addElement(new TMLJunction("void junction", tgc));
                    } else {
                        CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADWaitEvent)tgc).getEventName() + " is an unknown event");
                        ce.setTMLTask(tmltask);
                        ce.setTDiagramPanel(tadp);
                        ce.setTGComponent(tgc);
                        checkingErrors.add(ce);
                    }
                } else {
					if (event.getNbOfParams() != ((TMLADWaitEvent)tgc).realNbOfParams()) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADWaitEvent)tgc).getEventName() + ": wrong number of parameters");
                        ce.setTMLTask(tmltask);
                        ce.setTDiagramPanel(tadp);
                        ce.setTGComponent(tgc);
                        checkingErrors.add(ce);
					} else {
						tmlwaitevent = new TMLWaitEvent("wait event", tgc);
						tmlwaitevent.setEvent(event);
						for(int i=0; i<((TMLADWaitEvent)tgc).nbOfParams(); i++) {
							tmlwaitevent.addParam(((TMLADWaitEvent)tgc).getParamValue(i));
						}
						activity.addElement(tmlwaitevent);
						listE.addCor(tmlwaitevent, tgc);
					}
                }
            } else if (tgc instanceof TMLADWriteChannel) {
                // Get the channel
                channel = tmlm.getChannelByName(getFromTable(tmltask, ((TMLADWriteChannel)tgc).getChannelName()));
                if (channel == null) {
                    if (Conversion.containsStringInList(removedChannels, ((TMLADWriteChannel)tgc).getChannelName())) {
                        CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "A call to " + ((TMLADWriteChannel)tgc).getChannelName() + " has been removed because the corresponding channel is not taken into account");
                        ce.setTMLTask(tmltask);
                        ce.setTDiagramPanel(tadp);
                        ce.setTGComponent(tgc);
                        warnings.add(ce);
                        activity.addElement(new TMLJunction("void junction", tgc));
                    } else {
                        CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, ((TMLADWriteChannel)tgc).getChannelName() + " is an unknown channel");
                        ce.setTMLTask(tmltask);
                        ce.setTDiagramPanel(tadp);
                        ce.setTGComponent(tgc);
                        checkingErrors.add(ce);
                    }
                } else {
                    tmlwritechannel = new TMLWriteChannel("write channel", tgc);
                    tmlwritechannel.setNbOfSamples(((TMLADWriteChannel)tgc).getSamplesValue());
                    tmlwritechannel.setChannel(channel);
                    activity.addElement(tmlwritechannel);
					listE.addCor(tmlwritechannel, tgc);
                }
            }
        }
        
        // Interconnection between elements
        TGConnectorTMLAD tgco;
        TGConnectingPoint p1, p2;
        TMLActivityElement ae1, ae2;
        TGComponent tgc1, tgc2, tgc3;
        int j, index;
        
        iterator = list.listIterator();
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TGConnectorTMLAD) {
                tgco = (TGConnectorTMLAD)tgc;
                p1 = tgco.getTGConnectingPointP1();
                p2 = tgco.getTGConnectingPointP2();
                
                // Identification of connected components
                tgc1 = null; tgc2 = null;
                for(j=0; j<list.size(); j++) {
                    tgc3 = 	(TGComponent)(list.get(j));
                    if (tgc3.belongsToMe(p1)) {
                        tgc1 = tgc3;
                    }
                    if (tgc3.belongsToMe(p2)) {
                        tgc2 = tgc3;
                    }
                }
                
                // Connecting tml modeling components
                if ((tgc1 != null) && (tgc2 != null)) {
                    //ADComponent ad1, ad2;
                    ae1 = activity.findReferenceElement(tgc1);
                    ae2 = activity.findReferenceElement(tgc2);
                    
                    if ((ae1 != null ) && (ae2 != null)) {
                        //Special case if "for loop" or if "choice"
                        
                        if (ae1 instanceof TMLForLoop) {
                            index = tgc1.indexOf(p1) - 1;
                            if (index == 0) {
                                ae1.addNext(0, ae2);
                            } else {
                                ae1.addNext(ae2);
                            }
                        } else if (ae1 instanceof TMLChoice) {
                            index = tgc1.indexOf(p1) - 1;
							//System.out.println("Adding next:" + ae2);
                            ae1.addNext(ae2);
							//System.out.println("Adding guard:" + ((TMLADChoice)tgc1).getGuard(index));
                            ((TMLChoice)ae1).addGuard(((TMLADChoice)tgc1).getGuard(index));
                        } else if (ae1 instanceof TMLSequence) {
                            index = tgc1.indexOf(p1) - 1;
                            ((TMLSequence)ae1).addIndex(index);
                            ae1.addNext(ae2);
							//System.out.println("Adding " + ae2 + " at index " + index);
                        } else {
                            ae1.addNext(ae2);
                        }
                    }
                }
            }
        }
        
        // Check that each "for" has two nexts
        // Check that TMLChoice have compatible guards
        // Check TML select evts
        iterator = list.listIterator();
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TMLADChoice) {
                tmlchoice = (TMLChoice)(activity.findReferenceElement(tgc));
                tmlchoice.orderGuards();
				
				int nbNonDeter = tmlchoice.nbOfNonDeterministicGuard();
				int nbStocha = tmlchoice.nbOfStochasticGuard();
				if ((nbNonDeter > 0) && (nbStocha > 0)) {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formatted choice: it has both non-determinitic and stochastic guards");
                    ce.setTMLTask(tmltask);
                    ce.setTDiagramPanel(tadp);
                    ce.setTGComponent(tgc);
                    checkingErrors.add(ce);
				}
				int nb = Math.max(nbNonDeter, nbStocha);
				if (nb > 0) {
					nb = nb + tmlchoice.nbOfElseAndAfterGuards();
					if (nb != tmlchoice.getNbGuard()) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formatted choice: it has both non-determinitic/ stochastic and regular guards)");
						ce.setTMLTask(tmltask);
						ce.setTDiagramPanel(tadp);
						ce.setTGComponent(tgc);
						checkingErrors.add(ce);
					}
				}
				
				if (tmlchoice.nbOfNonDeterministicGuard() > 0) {
					/*if (!rndAdded) {
						TMLAttribute tmlt = new TMLAttribute("rnd__0", new TMLType(TMLType.NATURAL));
						tmlt.initialValue = "";
						tmltask.addAttribute(tmlt);
						rndAdded = true;
					}*/
				}
                if (tmlchoice.hasMoreThanOneElse()) {
                    CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Choice should have only one [else] guard");
                    ce.setTMLTask(tmltask);
                    ce.setTDiagramPanel(tadp);
                    ce.setTGComponent(tgc);
                    checkingErrors.add(ce);
                } else if ((index = tmlchoice.getElseGuard()) > -1){
                    index = tmlchoice.getElseGuard();
                    if (index == 0) {
                        CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Choice should have a regular guard");
                        ce.setTMLTask(tmltask);
                        ce.setTDiagramPanel(tadp);
                        ce.setTGComponent(tgc);
                        checkingErrors.add(ce);
                    }
                }
                if (tmlchoice.hasMoreThanOneAfter()) {
                    CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Choice should have only one [after] guard");
                    ce.setTMLTask(tmltask);
                    ce.setTDiagramPanel(tadp);
                    ce.setTGComponent(tgc);
                    checkingErrors.add(ce);
                }
            }
            if (tgc instanceof TMLADSelectEvt) {
				tmlselectevt = (TMLSelectEvt)(activity.findReferenceElement(tgc));
				if (!tmlselectevt.isARealSelectEvt()) {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "'Select events'  should be followed by only event receiving operators");
					ce.setTMLTask(tmltask);
					ce.setTDiagramPanel(tadp);
					ce.setTGComponent(tgc);
					checkingErrors.add(ce);
				}
            }
			
        }
		
		// Sorting nexts elements of Sequence
	   for(j=0; j<activity.nElements(); j++) {
		   ae1 = activity.get(j);
		   if (ae1 instanceof TMLSequence) {
			   ((TMLSequence)ae1).sortNexts();
		   }
	   }
    }
	
	public TMLMapping translateToTMLMapping() {
		tmlm = new TMLModeling();
		archi = new TMLArchitecture();
		map = new TMLMapping(tmlm, archi);
		
		checkingErrors = new Vector();
		warnings = new Vector();
		listE = new CorrespondanceTGElement();
		
		makeArchitecture();
		if (!makeTMLModeling()) {
			return null;
		}
		makeMapping();
		
		return map;
	}
	
	private void makeArchitecture() {
		if (nodesToTakeIntoAccount == null) {
			components = tmlap.tmlap.getComponentList();
		} else {
			components = nodesToTakeIntoAccount;
		}
		ListIterator iterator = components.listIterator();
		TGComponent tgc;
		
		TMLArchiCPUNode node;
		TMLArchiHWANode hwanode;
		TMLArchiBUSNode busnode;
		TMLArchiBridgeNode bridgenode;
		TMLArchiMemoryNode memorynode;
		HwCPU cpu;
		HwA hwa;
		HwBus bus;
		HwBridge bridge;
		HwMemory memory;
		
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof TMLArchiCPUNode) {
				node = (TMLArchiCPUNode)tgc;
				cpu = new HwCPU(node.getName());
				cpu.byteDataSize = node.getByteDataSize();
				cpu.pipelineSize = node.getPipelineSize();
				cpu.goIdleTime = node.getGoIdleTime();
				cpu.maxConsecutiveIdleCycles = node.getMaxConsecutiveIdleCycles();
				cpu.taskSwitchingTime = node.getTaskSwitchingTime();
				cpu.branchingPredictionPenalty = node.getBranchingPredictionPenalty();
				cpu.cacheMiss = node.getCacheMiss();
				cpu.schedulingPolicy = node.getSchedulingPolicy();  
				cpu.execiTime = node.getExeciTime();
				cpu.execcTime = node.getExeccTime();
				cpu.clockRatio = node.getClockRatio();
				listE.addCor(cpu, node);
				archi.addHwNode(cpu);
				System.out.println("CPU node added: " + cpu.getName());
			}
			
			if (tgc instanceof TMLArchiHWANode) {
				hwanode = (TMLArchiHWANode)tgc;
				hwa = new HwA(hwanode.getName());
				hwa.byteDataSize = hwanode.getByteDataSize();
				hwa.execiTime = hwanode.getExeciTime();
				hwa.clockRatio = hwanode.getClockRatio();
				listE.addCor(hwa, hwanode);
				archi.addHwNode(hwa);
				System.out.println("HWA node added: " + hwa.getName());
			}
			
			if (tgc instanceof TMLArchiBUSNode) {
				busnode = (TMLArchiBUSNode)tgc;
				bus = new HwBus(busnode.getName());
				bus.byteDataSize = busnode.getByteDataSize();
				bus.pipelineSize = busnode.getPipelineSize();
				bus.arbitration = busnode.getArbitrationPolicy();
				bus.clockRatio = busnode.getClockRatio();
				listE.addCor(bus, busnode);
				archi.addHwNode(bus);
				System.out.println("BUS node added:" + bus.getName());
			}
			
			if (tgc instanceof TMLArchiBridgeNode) {
				bridgenode = (TMLArchiBridgeNode)tgc;
				bridge = new HwBridge(bridgenode.getName());
				bridge.bufferByteSize = bridgenode.getBufferByteDataSize();
				bridge.clockRatio = bridgenode.getClockRatio();
				listE.addCor(bridge, bridgenode);
				archi.addHwNode(bridge);
				System.out.println("Bridge node added:" + bridge.getName());
			}
			
			if (tgc instanceof TMLArchiMemoryNode) {
				memorynode = (TMLArchiMemoryNode)tgc;
				memory = new HwMemory(memorynode.getName());
				memory.byteDataSize = memorynode.getByteDataSize();
				memory.clockRatio = memorynode.getClockRatio();
				listE.addCor(memory, memorynode);
				archi.addHwNode(memory);
				System.out.println("Memory node added:" + memory.getName());
			}
		}
		
		// Links between nodes
		TGComponent tgc1, tgc2;
		TGConnectingPoint p1, p2;
		TMLArchiConnectorNode connector;
		HwLink hwlink;
		HwNode originNode;
		
		iterator = tmlap.tmlap.getComponentList().listIterator();
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof TMLArchiConnectorNode) {
				//System.out.println("Found link");
				connector = (TMLArchiConnectorNode)tgc;
				tgc1 = null; tgc2 = null;
				p1 = connector.getTGConnectingPointP1();
				p2 = connector.getTGConnectingPointP2();
				tgc1 = tgc.getTDiagramPanel().getComponentToWhichBelongs(p1);
				tgc2 = tgc.getTDiagramPanel().getComponentToWhichBelongs(p2);
				if ((tgc1 != null) && (tgc2 != null)) {
					//System.out.println("Not null");
					if (components.contains(tgc1) && components.contains(tgc2)) {
						//System.out.println("Getting closer");
						if (tgc2 instanceof TMLArchiBUSNode) {
							originNode = listE.getHwNode(tgc1);
							bus  = (HwBus)(listE.getHwNode(tgc2));
							if ((originNode != null) && (bus != null)) {
								hwlink = new HwLink("link_" +originNode.getName() + "_to_" + bus.getName());
								hwlink.setPriority(connector.getPriority());
								hwlink.bus = bus;
								hwlink.hwnode = originNode;
								listE.addCor(hwlink, connector);
								archi.addHwLink(hwlink);
								//System.out.println("Link added");
							}
						}
					}
				}
			}
		}
	}
	
	private boolean makeTMLModeling() {
		// Determine all TML Design to be used -> TMLDesignPanels
		ArrayList<TMLDesignPanel> panels = new ArrayList<TMLDesignPanel>();
		ArrayList<TMLComponentDesignPanel> cpanels = new ArrayList<TMLComponentDesignPanel>();
		Vector taskss = new Vector();
		Vector allcomp = new Vector();
		Vector tmp;
		int index;
		
		if (nodesToTakeIntoAccount == null) {
			components = tmlap.tmlap.getComponentList();
		} else {
			components = nodesToTakeIntoAccount;
		}
		ListIterator iterator = components.listIterator();
		
		TGComponent tgc, tgctask;
		TMLArchiNode node;
		ArrayList<TMLArchiArtifact> artifacts;
		String namePanel;
		TMLDesignPanel tmldp;
		TURTLEPanel tup;
		TMLComponentDesignPanel tmlcdp;
		TMLTaskOperator task;
		TMLCPrimitiveComponent pc;
		
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof TMLArchiNode) {
				artifacts = ((TMLArchiNode)(tgc)).getAllTMLArchiArtifacts();
				for(TMLArchiArtifact artifact:artifacts) {
					namePanel = artifact.getReferenceTaskName();
					try {
						tup = (TURTLEPanel)(tmlap.getMainGUI().getTURTLEPanel(namePanel));
						if (tup instanceof TMLDesignPanel) {
							tmldp = (TMLDesignPanel)tup;
							if (panels.contains(tmldp)) {
								index = panels.indexOf(tmldp);
								tmp = (Vector)(taskss.get(index));
							} else {
								panels.add(tmldp);
								tmp = new Vector();
								taskss.add(tmp);
							}
							
							// Search for the corresponding TMLTask
							task = tmldp.getTaskByName(artifact.getTaskName());
							if (task != null) {
								tmp.add(task);
							} else {
								CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Task " + artifact.getTaskName() + " referenced by artifact " + artifact.getValue() + "is unknown");
								//ce.setTMLTask(tmltask);
								ce.setTDiagramPanel(tmlap.tmlap);
								ce.setTGComponent(tgc);
								checkingErrors.add(ce);
							}
							
						} else if (tup instanceof TMLComponentDesignPanel) {
							tmlcdp = (TMLComponentDesignPanel)(tup);
							if (cpanels.contains(tmlcdp)) {
								index = cpanels.indexOf(tmlcdp);
								tmp = (Vector)(taskss.get(index));
							} else {
								cpanels.add(tmlcdp);
								tmp = new Vector();
								taskss.add(tmp);
								
							}
							
							// Search for the corresponding TMLTask
							pc = tmlcdp.getPrimitiveComponentByName(artifact.getTaskName());
							if (pc != null) {
								tmp.add(pc);
								allcomp.add(pc);
							} else {
								CheckingError ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Component " + artifact.getTaskName() + " referenced by artifact " + artifact.getValue() + "is unknown");
								//ce.setTMLTask(tmltask);
								ce.setTDiagramPanel(tmlap.tmlap);
								ce.setTGComponent(tgc);
								checkingErrors.add(ce);
							}
						}
					} catch (Exception e) {
						// Just in case the mentionned panel is not a TML design Panel
					}
				}
			}
		}
		
		//System.out.println("Nb of panels regular:" + panels.size() + " components" + cpanels.size());
		
		// For each panel, construct a TMLModeling
		TMLModeling tmpm;
		GTMLModeling gtml;
		String s;
		index = 0;
		for(TMLDesignPanel panel: panels) {
			gtml =  new GTMLModeling(panel);
			gtml.setTasks((Vector)(taskss.get(index)));
			index ++;
			tmpm = gtml.translateToTMLModeling();
			warnings.addAll(gtml.getCheckingWarnings());
			if (gtml.getCheckingErrors().size() >0) {
				checkingErrors.addAll(gtml.getCheckingErrors());
				return false;
			}
			s = tmlap.getMainGUI().getTitleAt(panel);
			s = s.replaceAll("\\s", "");
			tmpm.prefixAllNamesWith(s + "__");
			//System.out.println("Intermediate TMLModeling: " + tmpm);
			tmlm.mergeWith(tmpm);
		}
		
		if (cpanels.size() > 0) {
			TMLComponentDesignPanel panel = cpanels.get(cpanels.size()-1);
			gtml =  new GTMLModeling(panel);
			gtml.setComponents(allcomp);
			tmpm = gtml.translateToTMLModeling(true);
			warnings.addAll(gtml.getCheckingWarnings());
			if (gtml.getCheckingErrors().size() >0) {
				checkingErrors.addAll(gtml.getCheckingErrors());
				return false;
			}
			s = tmlap.getMainGUI().getTitleAt(panel);
			s = s.replaceAll("\\s", "");
			tmpm.prefixAllNamesWith(s + "__");
			//System.out.println("Intermediate TMLModeling: " + tmpm);
			tmlm.mergeWith(tmpm);
		}
		
		/*for(TMLComponentDesignPanel panel: cpanels) {
			gtml =  new GTMLModeling(panel);
			gtml.setComponents((Vector)(taskss.get(index)));
			index ++;
			tmpm = gtml.translateToTMLModeling(true);
			warnings.addAll(gtml.getCheckingWarnings());
			if (gtml.getCheckingErrors().size() >0) {
				checkingErrors.addAll(gtml.getCheckingErrors());
				return false;
			}
			s = tmlap.getMainGUI().getTitleAt(panel);
			s = s.replaceAll("\\s", "");
			tmpm.prefixAllNamesWith(s + "__");
			//System.out.println("Intermediate TMLModeling: " + tmpm);
			tmlm.mergeWith(tmpm);
		}*/
		
		// Properties of artifacts
		iterator = components.listIterator();
		TMLTask ttask;
		while(iterator.hasNext()) {
			//System.out.println("next");
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof TMLArchiNode) {
				artifacts = ((TMLArchiNode)(tgc)).getAllTMLArchiArtifacts();
				for(TMLArchiArtifact artifact:artifacts) {
					s = artifact.getReferenceTaskName() + "__" + artifact.getTaskName();
					s = s.replaceAll("\\s", "");
					//System.out.println("name=" + s);
					ttask = tmlm.getTMLTaskByName(s);
					if (ttask != null) {
						//System.out.println("not null prio=" + artifact.getPriority());
						ttask.setPriority(artifact.getPriority());
					}
				}
			}
		}
		
		
		System.out.println("TMLModeling: " + tmlm);
		
		return true;
	}
	
	private void makeMapping() {
		if (nodesToTakeIntoAccount == null) {
			components = tmlap.tmlap.getComponentList();
		} else {
			components = nodesToTakeIntoAccount;
		}
		ListIterator iterator = components.listIterator();
		
		TGComponent tgc;
		ArrayList<TMLArchiArtifact> artifacts;
		ArrayList<TMLArchiCommunicationArtifact> artifactscomm;
		HwNode node;
		TMLTask task;
		TMLElement elt;
		String s;
		
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof TMLArchiNode) {
				node = archi.getHwNodeByName(tgc.getName());
				if ((node != null) && (node instanceof HwExecutionNode)) {
					artifacts = ((TMLArchiNode)(tgc)).getAllTMLArchiArtifacts();
					for(TMLArchiArtifact artifact:artifacts) {
						//System.out.println("Exploring artifact " + artifact.getValue());
						s = artifact.getReferenceTaskName();
						s = s.replaceAll("\\s", "");
						s = s + "__" + artifact.getTaskName();
						task = tmlm.getTMLTaskByName(s);
						if (task != null) {
							map.addTaskToHwExecutionNode(task, (HwExecutionNode)node);
						} else {
							System.out.println("Null task");
						}
					}
				} 
				
				// Other nodes (memory, bridge, bus)
			}
			
			if ((tgc instanceof TMLArchiBUSNode) || (tgc instanceof TMLArchiBridgeNode) || (tgc instanceof TMLArchiMemoryNode)) {
				node = archi.getHwNodeByName(tgc.getName());
				if ((node != null) && (node instanceof HwCommunicationNode)) {
					artifactscomm = ((TMLArchiCommunicationNode)(tgc)).getArtifactList();
					for(TMLArchiCommunicationArtifact artifact:artifactscomm) {
						//System.out.println("Exploring artifact " + artifact.getValue());
						s = artifact.getReferenceCommunicationName();
						s = s.replaceAll("\\s", "");
						s = s + "__" + artifact.getCommunicationName();
						elt = tmlm.getCommunicationElementByName(s);
						if (elt != null) {
							map.addCommToHwCommNode(elt, (HwCommunicationNode)node);
						} else {
							System.out.println("Null mapping: no element named" +artifact.getName());
						}
					}
				}
			}
			
		}
	}
	
	public void addToTable(String s1, String s2) {
		System.out.println("Adding to Table s1= "+ s1 + " s2=" + s2);
		table.put(s1, s2);
	}
	
	public String getFromTable(TMLTask task, String s) {
		//System.out.println("Getting from channel task=" + task.getName() + " element=" + s);
		
		if (table == null) {
			return s;
		}
		
		String ret = table.get(task.getName() + "/" + s);
		//System.out.println("Returning=" + ret);
		
		if (ret == null) {
			return s;
		}
		
		return ret;
	}
    
}
