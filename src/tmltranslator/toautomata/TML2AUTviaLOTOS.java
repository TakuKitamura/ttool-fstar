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
 * Class TML2AUTviaLOTOS
 * Creation: 20/10/2006
 * @version 1.0 20/10/2006
 * @author Ludovic APVRILLE
 * @see
 */

package tmltranslator.toautomata;

import java.util.*;

import translator.*;
import tmltranslator.*;
import automata.*;
import myutil.*;


public class TML2AUTviaLOTOS {
    
    //private static int gateId;
    
    private TMLModeling tmlmodeling;
    private TURTLEModeling tm;
    private LinkedList automatas;
    private LinkedList specs; /* name, then spec, then name, then spec. All specs are in LOTOS !*/
    
    private boolean debug;
    
    public static String FC2_EXTENSION = "fc2";
    public static String AUT_EXTENSION = "aut";
    public static String IMM = "imm__";
    
    
    public TML2AUTviaLOTOS(TMLModeling _tmlmodeling, TURTLEModeling _tm) {
        tmlmodeling = _tmlmodeling;
        tm = _tm;
        if (_tm == null) {
           System.out.println("tm = null!");
        }
    }

    // Returns a list of all file names ..
    public LinkedList saveInFiles(String path) throws FileException {
        //print();
        
        ListIterator iterator = automatas.listIterator();
        Automata aut;
        String name;
        LinkedList ll = new LinkedList();
        
        while(iterator.hasNext()) {
            aut = (Automata)(iterator.next());
            name = aut.getName() + "." + AUT_EXTENSION;
            ll.add(name);
            System.out.println("File: " + path + aut.getName() + "." + AUT_EXTENSION);
            FileUtils.saveFile(path + aut.getName() + "." + AUT_EXTENSION, aut.toAUT());
        }
        return ll;
        
    }
    
    public void print() {
        // Print each automatas
        ListIterator iterator = automatas.listIterator();
        Automata aut;
        
        while(iterator.hasNext()) {
            aut = (Automata)(iterator.next());
            System.out.println("Automata: " + aut.getName());
            System.out.println(aut.toAUT());
        }
    }
    
    public void generateLOTOS(boolean _debug) {
        debug = _debug;
        specs = new LinkedList();
        
        // Generate one LOTOS spec per TMLTask
        generateLOTOSTMLTasks();

    }
    
    public void generateLOTOSTMLTasks() {
      TMLTask task;
        ListIterator iterator = tmlmodeling.getTasks().listIterator();
        while(iterator.hasNext()) {
          task = (TMLTask)(iterator.next());
          specs.add(task.getName());
          specs.add(generateLOTOS(task));
        }
    }
    
    public LinkedList getSpecs() {
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