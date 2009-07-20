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
* Class SDExchange
* Creation: 17/07/2009
* @version 1.0 17/07/2009
* @author Ludovic APVRILLE
* @see
*/

package sddescription;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

import myutil.*;

public class SDExchange {
	private MSC msc;
	private HMSC hmsc;
	
	private String XML_SD_HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
	private String XML_SD_TOP = "MSC";
	private String XML_SD_INSTANCE = "INSTANCE";
	private String XML_SD_EVENT = "EVENT";
	private String XML_SD_EVENT_LINKS = "EVT_LINKS";
	private String XML_SD_EVENT_LINK = "LINK";
	private String XML_SD_NL = "\n";
	
	
	public void SDExchange() {
	}
	
	
	public MSC getMSC() {
		return msc;
	}
	
	public HMSC getHMSC() {
		return hmsc;
	}
	
	
	public void createHMSC(MSC _msc) {
		HMSCNode startNode = new HMSCNode("start", HMSCNode.START);
		hmsc = new HMSC("GeneratedHMSC", startNode);
		startNode.addNextMSC(_msc);
		HMSCNode stopNode = new HMSCNode("stop", HMSCNode.STOP);
		_msc.setNextNode(stopNode);
		
	}
	
	public boolean loadFromXMLSD(String xml) throws MalformedSDException{
		DocumentBuilderFactory dbf;
		DocumentBuilder db;
		Element elt;
		
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			dbf = null;
			db = null;
		}
		
		if ((dbf == null) || (db == null)) {
			throw new MalformedSDException();
		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream(decodeString(xml).getBytes());
		int i;
		String tmp;
		msc = null;
		
		try {
			// building nodes from xml String
			Document doc = db.parse(bais);
			NodeList nl;
			Node node;
			
			nl = doc.getElementsByTagName(XML_SD_TOP);
			
			if (nl == null) {
				return false;
			}
			
			elt = (Element)(nl.item(0));
			tmp = elt.getAttribute("name");
			if (tmp == null) {
				msc = new MSC("msc");
			} else {
				System.out.println("Name of SD = " + tmp);
				msc = new MSC(tmp);
			}
			
			createHMSC(msc);
			
			for(i=0; i<nl.getLength(); i++) {
				node = nl.item(i);
				//System.out.println("Node = " + dnd);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					loadMSCNode(node, hmsc, msc);
				}
			}
			
			orderEvents(hmsc, msc);
			linkEvents(hmsc, msc);
			
		} catch (IOException e) {
			System.out.println("500 ");
			throw new MalformedSDException();
		} catch (SAXException saxe) {
			System.out.println("501 " + saxe.getMessage());
			throw new MalformedSDException();
		}
		return true;
	}
	
	private boolean loadMSCNode(Node node1, HMSC _hmsc, MSC _msc) {
		NodeList diagramNl = node1.getChildNodes();
		Element elt;
		Node node;
		NodeList listData = null;
		
		String tmp;
		int val;
		int j;
		int nbOfFaces = 1; // default value;
		
		int[] colors;
		
		Instance instance;
		
		try {
			// Searching for instances
			for(j=0; j<diagramNl.getLength(); j++) {
				node = diagramNl.item(j);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					elt = (Element)node;
					
					// Data
					if (elt.getTagName().compareTo(XML_SD_INSTANCE) == 0) {
						tmp = elt.getAttribute("name");
						if (tmp != null) {
							instance = _hmsc.getInstance(tmp);
							if (instance != null) {
								System.out.println("Duplicate name for instance " + tmp + ": ignoring second instance");
							} else {
								instance = _hmsc.getCreateInstanceIfNecessary(tmp);
								loadInstance(elt.getChildNodes(), _hmsc, _msc, instance);
							}
						} else {
							System.out.println("Instance without a name: skipping");
						}
						//listData = elt.getElementsByTagName("WidgetData");
					}
					
					if (elt.getTagName().compareTo(XML_SD_EVENT_LINKS) == 0) {
						loadLinks(elt.getChildNodes(), _hmsc, _msc);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Exception in SD " + e.getMessage());
			return false;
		}
		
		return true;
		
    }
	
	private boolean loadInstance(NodeList nl, HMSC _hmsc, MSC _msc, Instance _instance) {
		try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            String type, value, id;
			Evt evt1;
			boolean validType;
			int stype = 0;
			int sid;
            
            System.out.println("Loading instance " + _instance.getName());
            //System.out.println(nl.toString());
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
				if (n1.getNodeType() == Node.ELEMENT_NODE) {
					elt = (Element) n1;
					if (elt.getTagName().equals(XML_SD_EVENT)) {
						System.out.println("Found one event in instance " + _instance.getName());
						type = elt.getAttribute("type");
						value = elt.getAttribute("value");
						id = elt.getAttribute("id");
						System.out.println("Evt type=" + type + " value=" + value + " id=" + id);
						if ((type != null) && (value != null)) {
							type = type.toUpperCase();
							validType = false;
							if (type.equals("SEND_SYNC")) {
								validType = true;
								stype = Evt.SEND_SYNC;
							} else if (type.equals("RECV_SYNC")) {
								validType = true;
								stype = Evt.RECV_SYNC;
							} else if (type.equals("VARIABLE_SET")) {
								validType = true;
								stype = Evt.VARIABLE_SET;
							} 
							if (validType) {
								evt1 = new Evt(stype, value, _instance);
								if (id != null) {
									try {
										sid = Integer.parseInt(id);
										evt1.setID(sid);
									} catch (NumberFormatException nfe) {
									}
								}
								System.out.println("Adding evt type=" + type + " value=" + value + " id=" + id);
								_msc.addEvt(evt1);
							}
						}
					}
				}
			}
    
        } catch (Exception e) {
            return false;
        }
		return true;
	}
	
	private boolean loadLinks(NodeList nl, HMSC _hmsc, MSC _msc) {
		try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            String id1, id2;
			Evt evt1;
			Evt evt2;
			int sid1, sid2;
			LinkEvts le;
            
            System.out.println("Loading links ");
            //System.out.println(nl.toString());
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
				if (n1.getNodeType() == Node.ELEMENT_NODE) {
					elt = (Element) n1;
					if (elt.getTagName().equals(XML_SD_EVENT_LINK)) {
						id1 = elt.getAttribute("id1");
						id2 = elt.getAttribute("id2");
						System.out.println("Evt link id1=" + id1 + " id2=" + id2);
						if ((id1 != null) && (id2 != null)) {
							try {
								sid1 = Integer.parseInt(id1);
								sid2 = Integer.parseInt(id2);
								evt1 = _msc.getEvtByID(sid1);
								evt2 = _msc.getEvtByID(sid2);
								if ((evt1 != null) && (evt2 != null)) {
									le = new LinkEvts(evt1, evt2);
									if (!le.areCompatible(evt1, evt2)) {
										System.out.println("The two events are not compatible. Skipping");
									} else {
										_msc.addLinkEvts(le);
										System.out.println("Adding linkEvts");
									}
								} else {
									System.out.println("Error in xml specification: evts not found. Skipping");
								}
							} catch (Exception e) {
							}
							
						}
						
					}
				}
			}
    
        } catch (Exception e) {
            return false;
        }
		return true;
	}
	
	public boolean orderEvents(HMSC _hmsc, MSC _msc) {
		Evt previousEvt = null;
		Evt evt;
		Instance instance;
		// For each instance, look for event sof that instance, and order them
		ListIterator li2;
		ListIterator li1 = _hmsc.getInstances().listIterator();
		Order order;
		
		System.out.println("Ordering events");
		while(li1.hasNext()) {
			instance = (Instance)(li1.next());
			if (instance != null) {
				System.out.println("Ordering events of instance " + instance.getName());
				previousEvt = null;
				li2 = _msc.getEvts().listIterator();
				while(li2.hasNext()) {
					evt = (Evt)(li2.next());
					if (evt.getInstance() == instance) {
						if (previousEvt != null) {
							order = new Order(previousEvt, evt);
							_msc.addOrder(order);
							System.out.println("New order between " + previousEvt.getID() + " and " + evt.getID());
						}
						previousEvt = evt;
					}
				}
			}
		}
		return true;
	}
	
	public boolean linkEvents(HMSC _hmsc, MSC _msc) {
		return true;
	}

	
	public static String transformString(String s) {
		if (s != null) {
			s = Conversion.replaceAllChar(s, '&', "&amp;");
			s = Conversion.replaceAllChar(s, '<', "&lt;");
			s = Conversion.replaceAllChar(s, '>', "&gt;");
			s = Conversion.replaceAllChar(s, '"', "&quot;");
			s = Conversion.replaceAllChar(s, '\'', "&apos;");
		}
		return s;
	}
	
	public static String decodeString(String s) throws MalformedSDException {
		if (s == null)
			return s;
		byte b[] = null;
		try {
			b = s.getBytes("ISO-8859-1");
			return new String(b);
		} catch (Exception e) {
			throw new MalformedSDException();
		}
	}
	
	
  
}
