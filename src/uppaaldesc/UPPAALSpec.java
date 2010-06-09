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
* Class UPPAALSpec
* Creation: 03/11/2006
* @version 1.0 03/11/2006
* @author Ludovic APVRILLE
* @see
*/

package uppaaldesc;

import java.util.*;

import myutil.*;


public class UPPAALSpec {
	private String header = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
	private String globalDeclaration = "";
	private LinkedList templates;
	private String instanciations = "";
	private String fullSpec;
	
	
    public UPPAALSpec() {
		templates = new LinkedList();
    }
	
	public String getStringSpec() {
		return fullSpec;
	}
	
    public String makeSpec() {
		ListIterator iterator = templates.listIterator();
		UPPAALTemplate template;
		
		globalDeclaration = "<declaration>\n//Global declarations\n" + Conversion.transformToXMLString(globalDeclaration) + "</declaration>\n";
		StringBuffer templatesString = new StringBuffer("");
		
		while(iterator.hasNext()) {
			//System.out.println("Template!");
			template = (UPPAALTemplate)(iterator.next());
			templatesString.append(template.makeTemplate());
		}
		
		instanciations = "<system>\n//Instanciation \n" + Conversion.transformToXMLString(instanciations) + "</system>\n";
		
		fullSpec = header + "<nta>\n" + globalDeclaration + templatesString + instanciations + "</nta>\n";
		return fullSpec;
    }
	
    public LinkedList getTemplates() {
		return templates;
    }
    
    public UPPAALTemplate getTemplateByName(String name) {
		UPPAALTemplate template;
		ListIterator ite = templates.listIterator();
		
		while(ite.hasNext()){
			template = (UPPAALTemplate)(ite.next());
			if (template.getName().compareTo(name) == 0) {
				return template;
			}
		}
		return null;
    }
	
    public void addGlobalDeclaration(String s) {
		globalDeclaration += s;
    }
    
    public void addInstanciation(String s) {
		instanciations += s;
    }
    
    public String getFullSpec() {
		return fullSpec;
    }
    
    public void addTemplate(UPPAALTemplate template) {
		templates.add(template);
    }
	
	public void enhanceGraphics() {
		ListIterator iterator = templates.listIterator();
		UPPAALTemplate template;
		
		
		while(iterator.hasNext()) {
			//System.out.println("Template!");
			template = (UPPAALTemplate)(iterator.next());
			template.enhanceGraphics();
		}
	}
	
	public void optimize() {
		ListIterator iterator = templates.listIterator();
		UPPAALTemplate template;
		
		
		while(iterator.hasNext()) {
			//System.out.println("Template!");
			template = (UPPAALTemplate)(iterator.next());
			template.optimize();
		}
	}
}