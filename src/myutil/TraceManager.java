/**Copyright or  or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 * Class TraceManager
 * Creation: 07/04/2010
 * @version 1.1 07/04/2010
 * @author Ludovic APVRILLE
 * @see
 */

package myutil;

public class TraceManager {
    public final static int TO_CONSOLE = 0;
    public final static int TO_FILE = 1;
    public final static int TO_BUFFER = 2;
    public final static int TO_DEVNULL = 3;

    public static int userPolicy = TO_CONSOLE;
    public static int devPolicy = TO_CONSOLE;
    public static int errPolicy = TO_CONSOLE;

    public static void addDev(String _s) {
        switch(devPolicy) {
            case TO_CONSOLE:
                System.out.println(_s);
                break;
            case TO_DEVNULL:
                break;
            default:
        }
    }

    public static void addUser(String _s) {
        switch(userPolicy) {
            case TO_CONSOLE:
                System.out.println(_s);
                break;
            default:
                System.out.println(_s);
        }
    }

    public static void addError(String _s) {
        switch(errPolicy) {
            case TO_CONSOLE:
                System.err.println(_s);
                break;
            default:
                System.err.println(_s);
        }
    }


} // Class TraceManager
