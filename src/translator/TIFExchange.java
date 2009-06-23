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
* Class TIFExchange
* Creation: 27/06/2007
* @version 1.0 27/06/2007
* @author Ludovic APVRILLE
* @see
*/

package translator;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

import myutil.*;

public class TIFExchange {
	private TURTLEModeling tm;
	
	private String XML_TIF_HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
	private String XML_TIF_TOP = "TIF";
	private String XML_TIF_CLASS = "TCLASS";
	private String XML_TIF_RELATION = "RELATION";
	private String XML_TIF_NL = "\n";
	
	
	public void TIFExhange() {
	}
	
	public void setTURTLEModeling(TURTLEModeling _tm) {
		tm = _tm;
	}
	
	public TURTLEModeling getTURTLEModeling() {
		return tm;
	}
	
	public String saveInXMLTIF() {
		if (tm == null) {
			return "";
		}
		
		StringBuffer sb = new StringBuffer(XML_TIF_HEADER + XML_TIF_NL + XML_TIF_NL);
		sb.append("<" + XML_TIF_TOP + ">" + XML_TIF_NL);
		sb.append(saveClassesInXMLTIF());
		sb.append(saveRelationsInXMLTIF());
		sb.append("</" + XML_TIF_TOP + ">" + XML_TIF_NL);
		return sb.toString();
	}
	
	public boolean loadFromXMLTIF(String xml) throws MalformedTIFException{
		DocumentBuilderFactory dbf;
		DocumentBuilder db;
		
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			dbf = null;
			db = null;
		}
		
		if ((dbf == null) || (db == null)) {
			throw new MalformedTIFException();
		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream(decodeString(xml).getBytes());
		int i;
		tm = new TURTLEModeling();
		
		try {
			// building nodes from xml String
			Document doc = db.parse(bais);
			NodeList nl;
			Node node;
			
			nl = doc.getElementsByTagName(XML_TIF_CLASS);
			
			if (nl == null) {
				return false;
			}
			
			for(i=0; i<nl.getLength(); i++) {
				node = nl.item(i);
				//System.out.println("Node = " + dnd);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					// create design, and get an index for it
					loadTClass(node);
				}
			}
			
			nl = doc.getElementsByTagName(XML_TIF_RELATION);
			
			for(i=0; i<nl.getLength(); i++) {
				node = nl.item(i);
				//System.out.println("Node = " + dnd);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					// create design, and get an index for it
					loadRelation(node);
				}
			}
			
		} catch (IOException e) {
			System.out.println("500 ");
			throw new MalformedTIFException();
		} catch (SAXException saxe) {
			System.out.println("501 " + saxe.getMessage());
			throw new MalformedTIFException();
		}
		return true;
	}

	public String saveClassesInXMLTIF() {
		StringBuffer sb = new StringBuffer("");
		for(int i=0; i<tm.classNb(); i++ ){
			sb.append(saveClassInXMLTIF(tm.getTClassAtIndex(i)));
		}
		return sb.toString();
	}
	
	public String saveClassInXMLTIF(TClass t) {
		int i;
		Param p;
		Gate g;
		StringBuffer sb = new StringBuffer("");
		
		sb.append("<" + XML_TIF_CLASS + ">" + XML_TIF_NL);
		
		sb.append("<name data=\"" + t.getName() + "\" />" + XML_TIF_NL);
		sb.append("<active data=\"" + t.isActive() + "\" />" + XML_TIF_NL);
		sb.append("<type data=\"" + t.getClass().getCanonicalName() + "\" />" + XML_TIF_NL);
		
		// Params
		for(i=0; i<t.paramNb(); i++) {
			p = (Param)(t.getParamList().get(i));
			sb.append(saveParamInXMLTIF(p));
		}
		
		// Gates
		for(i=0; i<t.gateNb(); i++) {
			g = (Gate)(t.getGateList().get(i));
			sb.append(saveGateInXMLTIF(g));
		}
		
		// Activity Diagram
		sb.append(saveActivityDiagramInTIF(t.getActivityDiagram()));
		
		sb.append("</" + XML_TIF_CLASS + ">" + XML_TIF_NL);
		return sb.toString();
	}
	
	public String saveParamInXMLTIF(Param p) {
		if (p == null) {
			return "";
		}
		
		String ret = "<attribute name=\"" + p.getName() + "\" ";
		ret += "type=\"" + p.getType() + "\" "; 
		ret += "value=\"" + p.getValue() + "\" ";
		ret += "access=\"" + p.getAccess() + "\" "; 
		ret += " />" + XML_TIF_NL;
		
		return ret;
	}
	
	public String saveGateInXMLTIF(Gate g) {
		if (g == null) {
			return "";
		}
		
		String ret = "<gate name=\"" + g.getName() + "\" ";
		ret += "type=\"" + g.getType() + "\" "; 
		ret += "internal=\"" + g.isInternal() + "\" ";
		ret += "protocoljava=\"" + g.getProtocolJava() + "\" ";
		ret += "localportjava=\"" + g.getLocalPortJava() + "\" ";
		ret += "destportjava=\"" + g.getDestPortJava() + "\" ";
		ret += "desthostjava=\"" + g.getDestHostJava() + "\" ";
		ret += "localhostjava=\"" + g.getLocalHostJava() + "\" ";
		ret += " />" + XML_TIF_NL;
		
		return ret;
	}
	
	public String saveRelationsInXMLTIF() {
		StringBuffer sb = new StringBuffer("");
		for(int i=0; i<tm.relationNb(); i++ ){
			sb.append(saveRelationInXMLTIF(tm.getRelationAtIndex(i)));
		}
		return sb.toString();
	}
	
	public String saveRelationInXMLTIF(Relation r) {
		int i;
		StringBuffer sb = new StringBuffer("");
		
		sb.append("<" + XML_TIF_RELATION + ">" + XML_TIF_NL);
		
		sb.append("<info type=\"" + r.type + "\" ");
		sb.append("name=\"" + r.getName() + "\" ");
		sb.append("t1name=\"" + r.t1.getName() + "\" ");
		sb.append("t2name=\"" + r.t2.getName() + "\" ");
		sb.append("navigation=\"" + r.navigation + "\" ");
		sb.append(" />" + XML_TIF_NL);
		
		if (r.hasGate()) {
			for(i=0; i<Math.min(r.gatesOfT1.size(), r.gatesOfT1.size()); i++) {
				sb.append("<gates name1=\"" + ((Gate)(r.gatesOfT1.get(i))).getName() +"\" ");
				sb.append("name2=\"" + ((Gate)(r.gatesOfT2.get(i))).getName() +"\" />" + XML_TIF_NL);
			}
		}
	
		sb.append("</" + XML_TIF_RELATION + ">" + XML_TIF_NL);
		return sb.toString();
	}
	
	public String saveActivityDiagramInTIF(ActivityDiagram ad) {
		StringBuffer sb = new StringBuffer("");
		ADComponent adc, tmp;
		ADActionStateWithGate adag;
		ADActionStateWithMultipleParam admp;
		ADActionStateWithParam adpa;
		ADChoice adch;
		ADDelay addelay;
		ADLatency adlatency;
		ADParallel adp;
		ADTimeInterval adti;
		ADTLO adtlo;
		
		int i,j;
		
		
		for(i=0; i<ad.size(); i++) {
			adc = ad.getADComponent(i);
			sb.append("<adcomponent>" + XML_TIF_NL);
			sb.append("<common type=\"" + adc.getClass().getCanonicalName() + "\" ");
			sb.append("id=\"" + adc.hashCode() + "\" />" + XML_TIF_NL);
			
			// Specific information to be saved?
			if (adc instanceof ADActionStateWithGate) {
				adag = (ADActionStateWithGate)adc;
				sb.append("<specific actionvalue=\"" + transformString(adag.getActionValue()) + "\" ");
				sb.append("gate=\"" + adag.getGate().getName() +  "\" ");
				sb.append("limitongate=\"" + transformString(adag.getLimitOnGate()) +  "\" />"+ XML_TIF_NL);
				
			} else if (adc instanceof ADActionStateWithMultipleParam) {
				admp = (ADActionStateWithMultipleParam)adc;
				sb.append("<specific actionvalue=\"" + transformString(admp.getActionValue()) + "\" />"+ XML_TIF_NL);
				
			} else if (adc instanceof ADActionStateWithParam) {
				adpa = (ADActionStateWithParam)adc;
				sb.append("<specific actionvalue=\"" + transformString(adpa.getActionValue()) + "\" ");
				sb.append("param=\"" + adpa.getParam().getName() + "\" />"+ XML_TIF_NL);
				
			} else if (adc instanceof ADChoice) {
				adch = (ADChoice)adc;
				for(j=0; j<adch.getNbGuard(); j++) {
					sb.append("<specific guard=\"" + transformString(adch.getGuard(j)) + "\" />"+ XML_TIF_NL);
				}
				
			} else if (adc instanceof ADDelay) {
				addelay = (ADDelay)adc;
				sb.append("<specific actionvalue=\"" + transformString(addelay.getValue()) + "\" />"+ XML_TIF_NL);
				
			} else if (adc instanceof ADLatency) {
				adlatency = (ADLatency)adc;
				sb.append("<specific actionvalue=\"" + transformString(adlatency.getValue()) + "\" />"+ XML_TIF_NL);
				
			} else if (adc instanceof ADParallel) {
				adp = (ADParallel)adc;
				sb.append("<specific valuegate=\"" + transformString(adp.getValueGate()) + "\" />"+ XML_TIF_NL);
				
			} else if ((adc instanceof ADStart) && (ad.getStartState() == adc)){
				sb.append("<specific start=\"true\" />"+ XML_TIF_NL);
				
			} else if (adc instanceof ADTimeInterval) {
				adti = (ADTimeInterval)adc;
				sb.append("<specific minvalue=\"" + transformString(adti.getMinValue()) + "\" ");
				sb.append("maxvalue=\"" + transformString(adti.getMaxValue()) +  "\" />"+ XML_TIF_NL);
				
			} else if (adc instanceof ADTLO) {
				adtlo = (ADTLO)adc;
				sb.append("<specific action=\"" + transformString(adtlo.getAction()) + "\" ");
				sb.append("latency=\"" + transformString(adtlo.getLatency()) +  "\" ");
				sb.append("delay=\"" + transformString(adtlo.getDelay()) +  "\" ");
				sb.append("gate=\"" + transformString(adtlo.getGate().getName()) +  "\" />"+ XML_TIF_NL);
				
			}
			
			// Nexts
			for(j=0; j<adc.getNbNext(); j++) {
				tmp = adc.getNext(j);
				sb.append("<next id=\"" + tmp.hashCode() + "\" />"+ XML_TIF_NL);
			}
			
			sb.append("</adcomponent>" + XML_TIF_NL);
		}
		
		return sb.toString();
	}
	
	public void loadTClass(Node node1) throws MalformedTIFException{
		NodeList diagramNl = node1.getChildNodes();
		Element elt;
		Node node;
		
		String name="", classname="";
		String tmp;
		boolean active = false;
		String attname, attvalue, atttype, attaccess;
		String gname, gtype, ginternal, gprotocoljava, glocalportjava, gdestportjava, gdesthostjava, glocalhostjava;
		boolean gbinternal;
		int gitype;
		Param p;
		Gate g;
		
		ArrayList<Param> params = new ArrayList<Param>();
		ArrayList<Gate> gates = new ArrayList<Gate>();
		
		try {
			// Gather all informations
			for(int j=0; j<diagramNl.getLength(); j++) {
				//System.out.println("Ndes: " + j);
				node = diagramNl.item(j);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					elt = (Element)node;
					if (elt.getTagName().compareTo("name") == 0) {
						name = elt.getAttribute("data");
					} else if (elt.getTagName().compareTo("active") == 0) { 
						tmp = elt.getAttribute("data");
						active = false;
						if (tmp.compareTo("true") == 0) {
							active = true;
						} 
					} else if (elt.getTagName().compareTo("type") == 0) { 
						classname = elt.getAttribute("data");
						
					// Attributes	
					} else if (elt.getTagName().compareTo("attribute") == 0) { 
						attname = elt.getAttribute("name");
						atttype = elt.getAttribute("type");
						attvalue = elt.getAttribute("value");
						attaccess = elt.getAttribute("access");
						p = new Param(attname, atttype, attvalue);
						p.setAccess(attaccess);
						params.add(p);
						
					// Gates	
					} else if (elt.getTagName().compareTo("gate") == 0) { 
						gname = elt.getAttribute("name");
						gtype = elt.getAttribute("type");
						ginternal = elt.getAttribute("internal");
						gprotocoljava = elt.getAttribute("protocoljava");
						glocalportjava = elt.getAttribute("localportjava");
						gdestportjava = elt.getAttribute("destportjava");
						gdesthostjava = elt.getAttribute("desthostjava");
						glocalhostjava = elt.getAttribute("localhostjava");
						
						if (ginternal.compareTo("true") == 0) {
							gbinternal = true;
						} else {
							gbinternal = false;
						}
						
						gitype = Integer.decode(gtype).intValue();
						g = new Gate(gname, gitype, gbinternal);
						g.setProtocolJava(Integer.decode(gprotocoljava).intValue());
						g.setLocalPortJava(Integer.decode(glocalportjava).intValue());
						g.setDestPortJava(Integer.decode(gdestportjava).intValue());
						g.setDestHostJava(gdesthostjava);
						g.setLocalHostJava(glocalhostjava);
						gates.add(g);
						
					} 
				}
			}
		} catch (Exception e) {
			System.out.println("Exception " + e.getMessage());
			throw new MalformedTIFException();
		}
		
		// Create the tclass
		//System.out.println("TClass name=" + name);
		
		// WARNING: must handle special TClasses
		TClass t = new TClass(name, active);
		ActivityDiagram ad = new ActivityDiagram();
		t.setActivityDiagram(ad);
		
		for(Param pa: params) {
			t.addParameter(pa);
		}
		for(Gate ga: gates) {
			//System.out.println("adding gate:" + ga.getName());
			t.addGate(ga);
		}
		
		makeADComponents(t, node1);
		
		//t.printParams();
		tm.addTClass(t);
		
	}
	
	public void makeADComponents(TClass t, Node node) throws MalformedTIFException {
		NodeList diagramNl = node.getChildNodes();
		Element elt;
		
		int i, j;
		
		ADComponent adc, tmp;
		ADActionStateWithGate adag;
		ADActionStateWithMultipleParam admp;
		ADActionStateWithParam adpa;
		ADChoice adch;
		ADDelay addelay;
		ADLatency adlatency;
		ADParallel adp;
		ADTimeInterval adti;
		ADTLO adtlo;
		
		ArrayList<ADComponent> adcomponents = new ArrayList<ADComponent>();
		ArrayList<String> ids = new ArrayList<String>();
		
		//System.out.println("Node1 = " + node);
		ActivityDiagram ad = t.getActivityDiagram();
		
		try {
			// Create all components
			for(j=0; j<diagramNl.getLength(); j++) {
				node = diagramNl.item(j);
				//System.out.println("Node=" + node);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					elt = (Element)node;
					if (elt.getTagName().compareTo("adcomponent") == 0) {
						makeADComponent(t, adcomponents, ids, node);
					}
				}
			}
			
			// Make links between components
			//System.out.println("Making links");
			for(i=0; i<ad.size(); i++) {
				adc = ad.getADComponent(i);
				//System.out.println("ADC=" + adc + " i=" + i + " size= " + ad.size());
				for(j=0; j<adc.getNbNext(); j++) {
					//System.out.println("nb next=" + adc.getNbNext() + " j=" + j);
					tmp = findRealComponent(adc.getNext(j), adcomponents, ids);
					if (tmp == null) {
						throw new MalformedTIFException("NULL next: " + adc);
					}
					adc.setNextAtIndex(tmp, j);
				}
			}
			
		} catch (Exception e) {
			System.out.println("Exception " + e.getMessage());
			throw new MalformedTIFException();
		}
	}
	
	public ADComponent findRealComponent(ADComponent adc, ArrayList<ADComponent> adcomponents, ArrayList<String> ids) {
		if (!(adc instanceof ADEmpty)) {
			return null;
		}
		
		ADEmpty ade = (ADEmpty)adc;
		String id;
		
		for(int i=0; i<ids.size(); i++) {
			id = ids.get(i);
			if (id.equals(ade.id)) {
				return adcomponents.get(i);
			}
		}
		return null;
	}
	
	public void makeADComponent(TClass t, ArrayList<ADComponent> adcomponents, ArrayList<String> ids, Node node) throws MalformedTIFException {
		NodeList diagramNl = node.getChildNodes();
		Element elt;
		
		ADComponent adc = null, tmp;
		ADActionStateWithGate adag;
		ADActionStateWithMultipleParam admp;
		ADActionStateWithParam adpa;
		ADChoice adch;
		ADDelay addelay;
		ADLatency adlatency;
		ADParallel adp;
		ADTimeInterval adti;
		ADTLO adtlo;
		ADEmpty ade;
		
		String type, id = null;
		String nextid, gate, action, limit, param, guard, minvalue, maxvalue;
		Gate g;
		Param p;
		
		ActivityDiagram ad = t.getActivityDiagram();
		
		//System.out.println("Making adcomponents");
		
		try {
			for(int j=0; j<diagramNl.getLength(); j++) {
				node = diagramNl.item(j);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					elt = (Element)node;
					if (elt.getTagName().compareTo("common") == 0) {
						type = elt.getAttribute("type");
						//System.out.println("Found a component type = " + type);
						id = elt.getAttribute("id");
						adc = newADComponent(type);
						ad.add(adc);
					} else if (elt.getTagName().compareTo("next") == 0) {
						if (adc != null) {
							ade = new ADEmpty();
							ade.id = elt.getAttribute("id");
							adc.addNext(ade);
						} else {
							throw new MalformedTIFException("NULL ADC");
						}
					} else if (elt.getTagName().compareTo("specific") == 0) {
						if (adc == null) {
							throw new MalformedTIFException("NULL ADC");
						}
						
						if (adc instanceof ADActionStateWithGate) {
							gate = elt.getAttribute("gate");
							action = elt.getAttribute("actionvalue");
							limit = elt.getAttribute("limitongate");
							adag = (ADActionStateWithGate)adc;
							adag.setActionValue(action);
							adag.setLimitOnGate(limit);
							g = t.getGateByName(gate);
							if (g == null) {
								throw new MalformedTIFException("NULL Gate: " + gate);
							}
							adag.setGate(g);
							
						} else if (adc instanceof ADActionStateWithMultipleParam) {
							admp = (ADActionStateWithMultipleParam)adc;
							action = elt.getAttribute("actionvalue");
							admp.setActionValue(action);
							
						} else if (adc instanceof ADActionStateWithParam) {
							action = elt.getAttribute("actionvalue");
							param = elt.getAttribute("param");
							adpa = (ADActionStateWithParam)adc;
							p = t.getParamByName(param);
							if (p == null) {
								throw new MalformedTIFException("NULL Param: " + param);
							}
							adpa.setParam(p);
							adpa.setActionValue(action);
							
						} else if (adc instanceof ADChoice) {
							adch = (ADChoice)adc;
							guard = elt.getAttribute("guard");
							adch.addGuard(guard);
							
						} else if (adc instanceof ADDelay) {
							addelay = (ADDelay)adc;
							action = elt.getAttribute("actionvalue");
							addelay.setValue(action);
							
						} else if (adc instanceof ADLatency) {
							adlatency = (ADLatency)adc;
							action = elt.getAttribute("actionvalue");
							adlatency.setValue(action);
							
						} else if (adc instanceof ADParallel) {
							adp = (ADParallel)adc;
							action = elt.getAttribute("valuegate");
							adp.setValueGate(action);
							
						} else if (adc instanceof ADStart) {
							ad.setStartState((ADStart)adc);
							
						} else if (adc instanceof ADTimeInterval) {
							adti = (ADTimeInterval)adc;
							minvalue = elt.getAttribute("minvalue");
							maxvalue = elt.getAttribute("maxvalue");
							adti.setValue(minvalue, maxvalue);
							
						} else if (adc instanceof ADTLO) {
							adtlo = (ADTLO)adc;
							action = elt.getAttribute("action");
							minvalue = elt.getAttribute("latency");
							maxvalue = elt.getAttribute("delay");
							gate = elt.getAttribute("gate");
							
							g = t.getGateByName(gate);
							if (g == null) {
								throw new MalformedTIFException("NULL Gate");
							}
							adtlo.setGate(g);
							adtlo.setLatency(minvalue);
							adtlo.setDelay(maxvalue);
							adtlo.setAction(action);
							
						}
					}
					
				}
			}
			
		} catch (Exception e) {
			System.out.println("Exception " + e.getMessage());
			throw new MalformedTIFException();
		}
		
		if ((adc != null) && (id != null)) {
			adcomponents.add(adc);
			ids.add(id);
		}
	}
	
	public ADComponent newADComponent(String type) {
		//System.out.println("New ADComponent. Type= " + type);
		try {
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			//System.out.println("1");
			Class c = cl.loadClass(type);
			//System.out.println("2");
			return (ADComponent)(c.newInstance());
		} catch (Exception e) {
			System.out.println("Could not create an instance if " + type + " because " + e.getMessage()); 
		}
		return null;
	}
	
	
	public void loadRelation(Node node1) throws MalformedTIFException {
		NodeList diagramNl = node1.getChildNodes();
		Element elt;
		Node node;
		
		String t1name, t2name, navigation;
		boolean nav;
		int type;
		String name1, name2;
		Gate g1, g2;
		TClass t1=null, t2=null;
		Relation r=null;
		
		try {
			// Gather all informations
			for(int j=0; j<diagramNl.getLength(); j++) {
				node = diagramNl.item(j);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					elt = (Element)node;
					if (elt.getTagName().compareTo("info") == 0) {
						type = Integer.decode(elt.getAttribute("type")).intValue();
						t1name = elt.getAttribute("t1name");  
						t2name = elt.getAttribute("t2name");
						navigation = elt.getAttribute("navigation");
						
						t1 = tm.getTClassWithName(t1name);
						t2 = tm.getTClassWithName(t2name);
						
						if (t1 == null) {
							throw new MalformedTIFException("NULL class: " + t1name);
						}
						
						if (t2 == null) {
							throw new MalformedTIFException("NULL class: " + t2name);
						}
						
						nav = navigation.equals("true");
						r = new Relation(type, t1, t2, nav);
						tm.addRelation(r);
						
					} else if (elt.getTagName().compareTo("gates") == 0) {
						name1 = elt.getAttribute("name1");  
						name2 = elt.getAttribute("name2");
						g1 = t1.getGateByName(name1);
						g2 = t2.getGateByName(name2);
						
						if (g1 == null) {
							throw new MalformedTIFException("NULL gate: " + name1);
						}
						
						if (g2 == null) {
							throw new MalformedTIFException("NULL gate: " + name2);
						}
						
						r.addGates(g1, g2);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Exception " + e.getMessage());
			throw new MalformedTIFException();
		}
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
	
	public static String decodeString(String s) throws MalformedTIFException {
		if (s == null)
			return s;
		byte b[] = null;
		try {
			b = s.getBytes("ISO-8859-1");
			return new String(b);
		} catch (Exception e) {
			throw new MalformedTIFException();
		}
	}
	
	
  
}
