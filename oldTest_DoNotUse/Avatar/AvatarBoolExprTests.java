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
 * Class AvatarBoolExprTests
 * Creation: 29/03/2017
 * @version 1.0 29/03/2017
 * @author Ludovic APVRILLE
 * @see
 */


import java.util.LinkedList;
import java.util.HashMap;
import java.util.Vector;

import ui.TAttribute;
import myutil.*;

public class AvatarBoolExprTests extends TToolTest {

    public AvatarBoolExprTests () {
        super ("AvatarBoolExpr", false);
    }

    protected void test () {
	BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
	boolean b = bee.getResultOfWithIntExpr("f==t");
	TraceManager.addDev("Error:? " + bee.hasError());
	this.updateDigest("Invalid expr " + bee.hasError());
	
         //Avatar Specification Tests

        this.updateDigest("Tests finished");
        // this.printDigest();

	printDigest();
	
        /*if (!this.testDigest (new byte[] {-75, -74, 67, -89, -35, 97, -29, -67, -79, 8, -88, 104, 19, -20, 60, -45, 83, -105, -47, -98}))
	  this.error ("Unexpected result when testing BoolExpressionEvaluator...");*/
    }
    public static void main(String[] args){
        AvatarBoolExprTests abet = new AvatarBoolExprTests ();
        abet.runTest ();
    }
}
