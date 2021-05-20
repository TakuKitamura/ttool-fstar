/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 */

package ui.networkmodelloader;

import javax.swing.*;
import java.io.File;
import java.awt.image.*;

/**
 * Class NetworkModel Dialog for managing the loading of network models
 * Creation: 29/05/2017
 * 
 * @version 1.1 29/05/2017
 * @author Ludovic APVRILLE
 * @author Ludovic APVRILLE
 */
public class NetworkModel {

  public String fileName;
  public boolean[] features;
  public boolean[] props;
  public String author = "";
  public String description = "";
  public String image;
  public BufferedImage bi;
  public BufferedImage scaledImg;

  public int x, y, width, height;

  public NetworkModel(String _fileName) {
    // super(_fileName);
    fileName = _fileName;
    features = new boolean[JDialogLoadingNetworkModel.FEATURES.length];
    features[0] = true;
    props = new boolean[JDialogLoadingNetworkModel.PROPS.length];
  }

  public void update() {

  }

  /*
   * public static NetworkModelType stringToNetworkModelType(String type) { type =
   * type.toLowerCase(); if (type.compareTo("software design") == 0) { return
   * NetworkModelType.SOFTWARE_DESIGN; }
   * 
   * if (type.compareTo("partitioning") == 0) { return
   * NetworkModelType.PARTITIONING; }
   * 
   * if (type.compareTo("attack tree") == 0) { return
   * NetworkModelType.ATTACK_TREE; }
   * 
   * if (type.compareTo("security protocol") == 0) { return
   * NetworkModelType.SECURITY_PROTOCOL; }
   * 
   * return NetworkModelType.SOFTWARE_DESIGN; }
   */

}
