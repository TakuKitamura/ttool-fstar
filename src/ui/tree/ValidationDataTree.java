/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

   ludovic.apvrille AT enst.fr

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

   /**
   * Class ValidationDataTree
   * Creation: 22/12/2003
   * Version 1.0 22/12/2003
   * @author Ludovic APVRILLE
   * @see
   */

package ui.tree;

import myutil.GenericTree;
import ui.MainGUI;

public class ValidationDataTree implements GenericTree {

    private MainGUI mgui;
    private String name = "Validation";
    private TClassesValidationDataTree tvdt;
    private SyntaxAnalysisTree mcvdt;
    private CorrespondanceValidationDataTree cvdt;
    private InvariantDataTree idt;
    private GraphTree gt;

    public ValidationDataTree(MainGUI _mgui) {
        mgui = _mgui;
        tvdt = new TClassesValidationDataTree(mgui);
        mcvdt = new SyntaxAnalysisTree(mgui);
        cvdt = new CorrespondanceValidationDataTree();
        idt = new InvariantDataTree(mgui);
        gt = new GraphTree(mgui);
    }

    // TREE MANAGEMENT

    public String toString() {
        return name;
    }

    public int getChildCount() {
        //System.out.println("Get child count validation");
        return 5;
    }

    public Object getChild(int index) {
        //System.out.println("Get child validation");
        switch (index) {
        case 0:
            return tvdt;
        case 1:
            return mcvdt;
        case 2:
            return cvdt;
        case 3:
            return gt;
        case 4:
            return idt;
        }
        return null;
    }

    public int getIndexOfChild(Object child) {
        //System.out.println("Get index of child validation");
        if (child instanceof TClassesValidationDataTree) {
            return 0;
        }       else if (child instanceof SyntaxAnalysisTree) {
            return 1;
        } else if (child instanceof CorrespondanceValidationDataTree) {
            return 2;
        } else if (child instanceof GraphTree) {
            return 3;
        } else if (child instanceof InvariantDataTree) {
            return 4;
        }
        return -1;
    }

}
