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
 * Class ProVerifOutputAnalyzer
 * Creation: 16/09/2010
 * @version 1.0 16/09/2010
 * @author Ludovic APVRILLE
 * @see
 */

package proverifspec;

import java.util.*;

import myutil.*;
import java.io.*;


public class ProVerifOutputAnalyzerTest {
    public static void main(String[] args){
	BufferedReader br = null;
		try {
			String s="";
			String sCurrentLine;
			br = new BufferedReader(new FileReader("proverifspec/typed.txt"));
			while ((sCurrentLine = br.readLine()) != null) {
			    s= s.concat(sCurrentLine+"\n");
			}
			//System.out.println(s);
	
			System.out.println("__________________________________");
	
			ProVerifOutputAnalyzer poa = new ProVerifOutputAnalyzer();
			poa.analyzeOutput(s,true);
					

			//Test for Reachable Events
			System.out.println("Reachable Events " + poa.getReachableEvents().size());
			for (String str: poa.getReachableEvents()){
			    System.out.println(str);
			}
			System.out.println("NonReachable Events " + poa.getNonReachableEvents().size());
			for (String str: poa.getNonReachableEvents()){
			    System.out.println(str);
			}
			System.out.println("Secret Terms " + poa.getSecretTerms().size());
			for (String str: poa.getSecretTerms()){
			    System.out.println(str);
			}
			System.out.println("Non Secret Terms " + poa.getNonSecretTerms().size());
			for (String str: poa.getNonSecretTerms()){
			    System.out.println(str);
			}
			System.out.println("Satisfied Authenticity " +poa.getSatisfiedAuthenticity().size());
			for (String str: poa.getSatisfiedAuthenticity()){
			    System.out.println(str);
			}
			System.out.println("Satisfied Weak Authenticity " +poa.getSatisfiedWeakAuthenticity().size());
			for (String str: poa.getSatisfiedWeakAuthenticity()){
			    System.out.println(str);
			}
			System.out.println("Non Satisfied Authenticity " +poa.getNonSatisfiedAuthenticity().size());
			for (String str: poa.getNonSatisfiedAuthenticity()){
			    System.out.println(str);
			}
			System.out.println("Errors " +poa.getErrors().size());
			for (String str: poa.getErrors()){
			    System.out.println(str);
			}
			System.out.println("Not proved " +poa.getNotProved().size());	
			for (String str: poa.getNotProved()){
			    System.out.println(str);
			}

			//Untyped Tests
			System.out.println("Untyped Tests");


			br = new BufferedReader(new FileReader("proverifspec/untyped.txt"));
			while ((sCurrentLine = br.readLine()) != null) {
			    s= s.concat(sCurrentLine+"\n");
			}
			//System.out.println(s);
	
			System.out.println("__________________________________");
	
			poa.analyzeOutput(s,false);
					

			//Test for Reachable Events
			System.out.println("Reachable Events " + poa.getReachableEvents().size());
			for (String str: poa.getReachableEvents()){
			    System.out.println(str);
			}
			System.out.println("NonReachable Events " + poa.getNonReachableEvents().size());
			for (String str: poa.getNonReachableEvents()){
			    System.out.println(str);
			}
			System.out.println("Secret Terms " + poa.getSecretTerms().size());
			for (String str: poa.getSecretTerms()){
			    System.out.println(str);
			}
			System.out.println("Non Secret Terms " + poa.getNonSecretTerms().size());
			for (String str: poa.getNonSecretTerms()){
			    System.out.println(str);
			}
			System.out.println("Satisfied Authenticity " +poa.getSatisfiedAuthenticity().size());
			for (String str: poa.getSatisfiedAuthenticity()){
			    System.out.println(str);
			}
			System.out.println("Satisfied Weak Authenticity " +poa.getSatisfiedWeakAuthenticity().size());
			for (String str: poa.getSatisfiedWeakAuthenticity()){
			    System.out.println(str);
			}
			System.out.println("Non Satisfied Authenticity " +poa.getNonSatisfiedAuthenticity().size());
			for (String str: poa.getNonSatisfiedAuthenticity()){
			    System.out.println(str);
			}
			System.out.println("Errors " +poa.getErrors().size());
			for (String str: poa.getErrors()){
			    System.out.println(str);
			}
			System.out.println("Not proved " +poa.getNotProved().size());	
			for (String str: poa.getNotProved()){
			    System.out.println(str);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

    }
}
