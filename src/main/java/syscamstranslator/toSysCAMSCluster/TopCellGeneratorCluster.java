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

/* Generator of the top cell for simulation with SystemC-AMS */

package syscamstranslator.toSysCAMSCluster;

import syscamstranslator.*;
import java.io.*;
import java.util.LinkedList;

/**
 * Class TopCellGeneratorCluster
 * Save the components and connectors in files
 * Creation: 14/05/2018
 * @version 1.0 14/05/2018
 * @author Irina Kit Yan LEE
 * @version 1.1 30/07/2018
 * @author Irina Kit Yan LEE, Rodrigo CORTES PORTO
 * @version 1.2 12/07/2019
 * @author Irina Kit Yan LEE, Rodrigo CORTES PORTO, Daniela GENIUS
*/

public class TopCellGeneratorCluster {
	public static SysCAMSSpecification syscams;

	private final static String GENERATED_PATH1 = "generated_CPP" + File.separator;
	private final static String GENERATED_PATH2 = "generated_H" + File.separator;

	private final static String CR = "\n";
	private final static String CR2 = "\n\n";
    
	public TopCellGeneratorCluster(SysCAMSSpecification sys) {
		syscams = sys;
	}

	public String generateTopCell(SysCAMSTCluster c, LinkedList<SysCAMSTConnector> connectors) {
		if (c == null) {
			System.out.println("***Warning: require at least one cluster***");
		}       	
		String top = HeaderCluster.getClusterHeader(c) + ClusterCode.getClusterCode(c, connectors);
		return (top);
	}

    public void saveFile(String path, Boolean standalone) {
		SysCAMSTCluster cluster = TopCellGeneratorCluster.syscams.getCluster();
		LinkedList<SysCAMSTConnector> connectors = TopCellGeneratorCluster.syscams.getAllConnectors();
		FileWriter fw;
		String top;

		try {
			// Save file .cpp
			System.err.println(path + GENERATED_PATH1 + cluster.getClusterName() + "_tdf.h");
			System.err.println(path + cluster.getClusterName() + "_tdf.h");		
			fw = new FileWriter(path + GENERATED_PATH1 + "/" + cluster.getClusterName() + "_tdf.h");
		
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
				System.err.println(path + GENERATED_PATH2 + t.getName() + "_tdf.h");
				System.err.println(path + t.getName() + "_tdf.h");			
				    fw = new FileWriter(path + GENERATED_PATH2 + "/" + t.getName() + "_tdf.h");
				headerTDF = HeaderCluster.getPrimitiveHeaderTDF(t);
				fw.write(headerTDF);
				codeTDF = PrimitiveCodeCluster.getPrimitiveCodeTDF(t);			
				codeTDF = codeTDF + "#endif"+ CR;
				fw.write(codeTDF);
				fw.close();
			
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		for (SysCAMSTBlockDE t : de) {
			try {
				System.err.println(path + GENERATED_PATH2 + t.getName() + "_tdf.h");	System.err.println(path + GENERATED_PATH2 + t.getName() + "_tdf.h");		       	
			
				fw = new FileWriter(path + GENERATED_PATH2 + "/" + t.getName() + "_tdf.h");
				
				headerDE = HeaderCluster.getPrimitiveHeaderDE(t);
				fw.write(headerDE);
				codeDE = PrimitiveCodeCluster.getPrimitiveCodeDE(t);			
				codeDE = codeDE + "#endif "+ CR;
				fw.write(codeDE);
				fw.close();
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
