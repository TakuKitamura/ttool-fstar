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

package elntranslator.toELN;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import elntranslator.*;

/**
 * Class TopCellGenerator Save the components and connectors in files Creation:
 * 27/07/2018
 * 
 * @version 1.0 27/07/2018
 * @author Irina Kit Yan LEE
 */

public class TopCellGenerator {
  public static ELNSpecification eln;

  private final static String GENERATED_PATH1 = "generated_CPP" + File.separator;
  private final static String GENERATED_PATH2 = "generated_H" + File.separator;

  public TopCellGenerator(ELNSpecification _eln) {
    eln = _eln;
  }

  public String generateTopCell(ELNTCluster cluster, LinkedList<ELNTConnector> connectors) {
    if (TopCellGenerator.eln.getCluster() == null) {
      System.out.println("***Warning: require at least one cluster***");
    }
    if (TopCellGenerator.eln.getNbModule() == 0) {
      System.out.println("***Warning: require at least one module***");
    }
    if (TopCellGenerator.eln.getNbComponentCapacitor() + TopCellGenerator.eln.getNbComponentCurrentSinkTDF()
        + TopCellGenerator.eln.getNbComponentCurrentSourceTDF() + TopCellGenerator.eln.getNbComponentIdealTransformer()
        + TopCellGenerator.eln.getNbComponentIndependentCurrentSource()
        + TopCellGenerator.eln.getNbComponentIndependentVoltageSource() + TopCellGenerator.eln.getNbComponentInductor()
        + TopCellGenerator.eln.getNbComponentResistor() + TopCellGenerator.eln.getNbComponentTransmissionLine()
        + TopCellGenerator.eln.getNbComponentVoltageControlledCurrentSource()
        + TopCellGenerator.eln.getNbComponentVoltageControlledVoltageSource()
        + TopCellGenerator.eln.getNbComponentVoltageSinkTDF() + TopCellGenerator.eln.getNbComponentVoltageSourceTDF()
        + TopCellGenerator.eln.getNbComponentVoltageSinkDE() + TopCellGenerator.eln.getNbComponentVoltageSourceDE()
        + TopCellGenerator.eln.getNbComponentCurrentSinkDE()
        + TopCellGenerator.eln.getNbComponentCurrentSourceDE() == 0) {
      System.out.println("***Warning: require at least one primitive component***");
    }
    if (TopCellGenerator.eln.getNbComponentNodeRef() == 0) {
      System.out.println("***Warning: require at least one node ref***");
    }
    String top = ClusterCode.getClusterCode(cluster, connectors);
    return (top);
  }

  public void saveFile(String path) {
    ELNTCluster cluster = TopCellGenerator.eln.getCluster();
    LinkedList<ELNTConnector> connectorsModule = TopCellGenerator.eln.getAllConnectorsInModule();
    LinkedList<ELNTConnector> connectorsCluster = TopCellGenerator.eln.getAllConnectorsInCluster();

    String top;

    try {
      // Save file .cpp
      System.err.println(path + GENERATED_PATH1 + cluster.getName() + ".cpp");
      FileWriter fw = new FileWriter(path + GENERATED_PATH1 + "/" + cluster.getName() + "_tb.cpp");
      top = generateTopCell(cluster, connectorsCluster);
      fw.write(top);
      fw.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    // Save files .h
    saveFileModule(path, cluster, connectorsModule);
  }

  public void saveFileModule(String path, ELNTCluster cluster, List<ELNTConnector> connectors) {
    LinkedList<ELNTModule> modules = cluster.getModule();

    String code;

    for (ELNTModule module : modules) {
      try {
        System.err.println(path + GENERATED_PATH2 + module.getName() + ".h");
        FileWriter fw = new FileWriter(path + GENERATED_PATH2 + "/" + module.getName() + ".h");
        code = ModuleCode.getModuleCode(module, connectors);
        fw.write(code);
        fw.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}