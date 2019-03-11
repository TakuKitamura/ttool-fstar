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


package tmltranslator.tonetwork;

import myutil.Conversion;
import myutil.FileException;
import myutil.FileUtils;
import myutil.TraceManager;
import tmltranslator.*;

import java.util.*;


/**
 * Class TMAP2Network
 * Creation: 07/01/2019
 *
 * @author Ludovic Apvrille
 * @version 1.0 07/01/2019
 */
public class TMAP2Network  {

    private TMLModeling<?> tmlmodeling;
    private TMLMapping<?> tmlmapping;

    private boolean debug;
    private boolean optimize;

    private int nbOfVCs = 2;
    private int nocSize = 2;
    private TranslatedRouter[][] routers;

    public TMAP2Network(TMLMapping<?> _tmlmapping, int nocSize) {
        tmlmapping = _tmlmapping;
        routers = new TranslatedRouter[nbOfVCs][nbOfVCs];
        this.nocSize = nocSize;
    }

    /* List of assumptions:
        - Only one router set (i.e. no router, then bus, then router) between two tasks
        - Channels must be mapped on at least one route to be taken into account
     */
    public void removeAllRouterNodes() {
        // Make all routers
        for(int i=0; i<nocSize; i++) {
            for(int j=0; j<nocSize; j++) {
                // We must find the number of apps connected on this router
                int nbOfApps = 2;

                TranslatedRouter tr = new TranslatedRouter(nbOfApps, nbOfVCs, i, j);
                routers[i][j] = tr;
                tr.makeRouter();
            }
        }

        // Connect their feedback

        // Make their routing

        // Integrate into the TMLMapping

        // Connect channels to the NoC

        

        // A bridge is put with the same position as the router as to allow classical paths not to use the router

        // We consider all channels mapped on at least one router.
    }

}
