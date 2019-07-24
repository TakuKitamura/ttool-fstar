/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
  Daniela Genius, Lip6, UMR 7606 

  ludovic.apvrille AT enst.fr
  daniela.genius@lip6.fr

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
*/

/* Generator of the top cell for simulation with SoCLib virtual component 
   library */

/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */

package syscamstranslator.toSysCAMS;

import syscamstranslator.*;
import java.io.*;
import java.util.LinkedList;

/**
 * Class TopCellGenerator
 * Save the components and connectors in files
 * Creation: 14/05/2018
 * @version 1.0 14/05/2018
 * @author Irina Kit Yan LEE
*/

public class TopCellGenerator {
	public static SysCAMSSpecification syscams;

	private final static String GENERATED_PATH1 = "generated_CPP" + File.separator;
	private final static String GENERATED_PATH2 = "generated_H" + File.separator;
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";

	public TopCellGenerator(SysCAMSSpecification sys) {
		syscams = sys;
	}

	public String generateTopCell(SysCAMSTCluster c, LinkedList<SysCAMSTConnector> connectors) {
		if (c == null) {
			System.out.println("***Warning: require at least one cluster***");
		}
		if (TopCellGenerator.syscams.getNbBlockTDF() == 0) {
			System.out.println("***Warning: require at least one TDF block***");
		}
		if (TopCellGenerator.syscams.getNbPortTDF() == 0) {
			System.out.println("***Warning: require at least one TDF port***");
		}
		if (TopCellGenerator.syscams.getNbBlockDE() == 0) {
			System.out.println("***Warning: require at least one DE block***");
		}
		if (TopCellGenerator.syscams.getNbPortDE() == 0) {
			System.out.println("***Warning: require at least one DE port***");
		}
		if (TopCellGenerator.syscams.getNbPortConverter() == 0) {
			System.out.println("***Warning: require at least one converter port***");
		}
		if (TopCellGenerator.syscams.getNbConnectorCluster() == 0) {
			System.out.println("***Warning: require at least one connector***");
		}
		String top = Header.getClusterHeader(c) + ClusterCode.getClusterCode(c, connectors);
		return (top);
	}

    public void saveFile(String path, Boolean standalone) {
		SysCAMSTCluster cluster = TopCellGenerator.syscams.getCluster();
		LinkedList<SysCAMSTConnector> connectors = TopCellGenerator.syscams.getAllConnectorCluster();
		FileWriter fw; 
		String top;

		try {
			// Save file .cpp
			System.err.println(path + GENERATED_PATH1 + cluster.getClusterName() + ".cpp");
			System.err.println(path + cluster.getClusterName() + ".cpp");
			if(standalone==true){
			    //System.out.println("@@@@ topcell standalone @@@@");
			    fw = new FileWriter(path + "/" + cluster.getClusterName() + "_tb.cpp");}
			else{
			    fw = new FileWriter(path + GENERATED_PATH1 + "/" + cluster.getClusterName() + "_tb.cpp");
			}
			fw = new FileWriter(path + "/" + cluster.getClusterName() + "_tb.cpp");
			top = generateTopCell(cluster, connectors);
			fw.write(top);
			fw.close();
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// Save files .h
		saveFileBlock(path, cluster, standalone);
	}

    public void saveFileBlock(String path, SysCAMSTCluster c, Boolean standalone) {
		String headerTDF, headerDE, codeTDF, codeDE;
		LinkedList<SysCAMSTBlockTDF> tdf = c.getBlockTDF();
		LinkedList<SysCAMSTBlockDE> de = c.getBlockDE();
		FileWriter fw; 
		for (SysCAMSTBlockTDF t : tdf) {
			try {
				System.err.println(path + GENERATED_PATH2 + t.getName() + ".h");
				System.err.println(path + t.getName() + ".h"); 		
				if(standalone==true){
				    //System.out.println("@@@@ TDF standalone @@@@");
				    fw = new FileWriter(path + "/" + t.getName() + ".h");}
			else
			    fw = new FileWriter(path + GENERATED_PATH2 + "/" + t.getName() + ".h");
			
				headerTDF = Header.getPrimitiveHeaderTDF(t);
				fw.write(headerTDF);
				codeTDF = PrimitiveCode.getPrimitiveCodeTDF(t);
				//	if(standalone==false)
				// codeTDF = codeTDF + CR + "};" + CR2 + "#endif";
				fw.write(codeTDF);
				fw.close();
			
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		for (SysCAMSTBlockDE t : de) {
			try {
				System.err.println(path + GENERATED_PATH2 + t.getName() + ".h");
				System.err.println(path + t.getName() + ".h");//ajoute DG
				
				if(standalone==true){
				    //System.out.println("@@@@ DE standalone @@@@");
				    fw = new FileWriter(path + "/" + t.getName() + ".h");}
				else
				    fw = new FileWriter(path + GENERATED_PATH2 + "/" + t.getName() + ".h");
				headerDE = Header.getPrimitiveHeaderDE(t);
				fw.write(headerDE);
				codeDE = PrimitiveCode.getPrimitiveCodeDE(t);
				//	if(standalone==false)
				//  codeDE = codeDE + CR + "};" + CR2 + "#endif";//DG
				fw.write(codeDE);
				fw.close();
			
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
