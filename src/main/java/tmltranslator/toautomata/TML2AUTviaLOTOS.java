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

package tmltranslator.toautomata;

import automata.Automata;
import myutil.FileException;
import myutil.FileUtils;
import myutil.TraceManager;
import tmltranslator.TMLModeling;
import tmltranslator.TMLTask;
import translator.TURTLEModeling;
import translator.TURTLETranslator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class TML2AUTviaLOTOS Creation: 20/10/2006
 * 
 * @version 1.0 20/10/2006
 * @author Ludovic APVRILLE
 */
public class TML2AUTviaLOTOS {

  // private static int gateId;

  private TMLModeling<?> tmlmodeling;
  private TURTLEModeling tm;
  private List<Automata> automatas;
  private List<String> specs; /* name, then spec, then name, then spec. All specs are in LOTOS ! */

  // private boolean debug;

  public static String FC2_EXTENSION = "fc2";
  public static String AUT_EXTENSION = "aut";
  public static String IMM = "imm__";

  public TML2AUTviaLOTOS(TMLModeling<?> _tmlmodeling, TURTLEModeling _tm) {
    tmlmodeling = _tmlmodeling;
    tm = _tm;
    if (_tm == null) {
      TraceManager.addDev("tm = null!");
    }
  }

  // Returns a list of all file names ..
  public List<String> saveInFiles(String path) throws FileException {
    // print();

    Iterator<Automata> iterator = automatas.listIterator();
    Automata aut;
    String name;
    List<String> ll = new LinkedList<String>();

    while (iterator.hasNext()) {
      aut = iterator.next();
      name = aut.getName() + "." + AUT_EXTENSION;
      ll.add(name);
      TraceManager.addDev("File: " + path + aut.getName() + "." + AUT_EXTENSION);
      FileUtils.saveFile(path + aut.getName() + "." + AUT_EXTENSION, aut.toAUT());
    }

    return ll;

  }

  public void print() {
    // Print each automatas
    Iterator<Automata> iterator = automatas.listIterator();
    Automata aut;

    while (iterator.hasNext()) {
      aut = iterator.next();
      TraceManager.addDev("Automata: " + aut.getName());
      TraceManager.addDev(aut.toAUT());
    }
  }

  public void generateLOTOS(boolean _debug) {
    // debug = _debug;
    specs = new LinkedList<String>();

    // Generate one LOTOS spec per TMLTask
    generateLOTOSTMLTasks();

  }

  public void generateLOTOSTMLTasks() {
    TMLTask task;
    Iterator<TMLTask> iterator = tmlmodeling.getTasks().listIterator();

    while (iterator.hasNext()) {
      task = iterator.next();
      specs.add(task.getName());
      specs.add(generateLOTOS(task));
    }
  }

  public List<String> getSpecs() {
    return specs;
  }

  public String generateLOTOS(TMLTask task) {
    TURTLEModeling tmex = new TURTLEModeling();
    tmex.addTClass(tm.getTClassWithName(task.getName()));

    // Generate LOTOS
    TURTLETranslator translator = new TURTLETranslator(tmex);
    return translator.generateLOTOS(true);
  }

}