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

package tmltranslator.tomappingsystemc2;

import myutil.FileException;
import myutil.FileUtils;
import myutil.TraceManager;

import java.io.File;

/**
 * Class Penalties Management of penalty file Creation: 23/07/2018
 *
 * @author Ludovic APVRILLE
 * @version 1.0 23/07/2018
 */
public class Penalties {
  public static final String FILE_NAME = "penalties.h";
  private static final String NOT_ACTIVATED = "#undef PENALTIES_ENABLED";
  private static final String ACTIVATED = "#define PENALTIES_ENABLED";
  private static final String FILE_HEADER = "// DO NOT EDIT: AUTOMATICALLY GENERATED";

  private String pathToFile;

  public Penalties(String pathToFile) {
    this.pathToFile = pathToFile;
  }

  // Return 0 in case no change, 1 if changes were made
  // -1 in case of error
  public int handlePenalties(boolean mustHandlePenalties) {
    // Load file and check for current status
    String fullPath = pathToFile + File.separator + FILE_NAME;
    String data = "";
    boolean mustChange = false;

    try {
      data = FileUtils.loadFile(fullPath);
      int indexU = data.indexOf(NOT_ACTIVATED);
      int indexD = data.indexOf(ACTIVATED);

      // No penalty
      if ((indexD == -1) && (indexU == -1)) {
        mustChange = true;
      } else if ((indexD > -1) && (indexU > -1)) {
        mustChange = true;
      } else {
        if (indexD > -1) {
          mustChange = mustHandlePenalties == false;
        } else {
          mustChange = mustHandlePenalties == true;
        }
      }
    } catch (FileException e) {
      mustChange = true;
    }

    // TraceManager.addDev("Changing penalty file? " + mustChange);

    // Set new value if necessary
    if (!mustChange) {
      // TraceManager.addDev("No need to change the source file");
      return 0;
    }

    data = FILE_HEADER + "\n";
    if (mustHandlePenalties) {
      data += ACTIVATED;
    } else {
      data += NOT_ACTIVATED;
    }

    try {
      FileUtils.saveFile(fullPath, data);
    } catch (FileException e) {
      return -1;
    }

    return 1;
  }

}
