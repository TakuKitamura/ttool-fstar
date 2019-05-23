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

import myutil.TraceManager;
import tmltranslator.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


/**
 * Class Link
 * Creation: 10/05/2019
 *
 * @author Ludovic Apvrille
 * @version 1.0 10/05/2019
 */
public class Link {
    private int nbOfVCs;
    private TMLModeling tmlm;

    // Routers
    private TranslatedRouter previousRouter;
    private TranslatedRouter nextRouter;


    // Between OUT and IN
    TMLEvent packetOut;
    TMLChannel chOutToIN;
    TMLEvent feedbackPerVC[];

    private String add = "";


    public Link(TMLModeling tmlm, TranslatedRouter previous, TranslatedRouter next, int nbOfVCs) {
        previousRouter = previous;
        nextRouter = next;
        this.nbOfVCs = nbOfVCs;
        this.tmlm = tmlm;

        TraceManager.addDev("Adding link between previous (" + previousRouter.getXPos() + "," + previousRouter.getYPos() +
        ") and next (" + nextRouter.getXPos() + "," + nextRouter.getYPos() + ")");

        if (tmlm ==null) {
            TraceManager.addDev("null modeling");
        }

        generateLinks();
    }

    public Link(TMLModeling tmlm, TranslatedRouter previous, TranslatedRouter next, int nbOfVCs, String add) {
        previousRouter = previous;
        nextRouter = next;
        this.nbOfVCs = nbOfVCs;
        this.tmlm = tmlm;
        this.add = "_" + add;

        TraceManager.addDev("Adding link between previous (" + previousRouter.getXPos() + "," + previousRouter.getYPos() +
                ") and next (" + nextRouter.getXPos() + "," + nextRouter.getYPos() + ")" + " with add=" + add);

        if (tmlm ==null) {
            TraceManager.addDev("null modeling");
        }


        generateLinks();

    }



    public void generateLinks() {

        packetOut = new TMLEvent("evtPktOut__" + getNaming(),
                null, 8, true);
        packetOut.addParam(new TMLType(TMLType.NATURAL));
        packetOut.addParam(new TMLType(TMLType.NATURAL));
        packetOut.addParam(new TMLType(TMLType.NATURAL));
        packetOut.addParam(new TMLType(TMLType.NATURAL));
        tmlm.addEvent(packetOut);

        chOutToIN = new TMLChannel("channelBetweenOUTToIN__" + getNaming(),
                null);
        chOutToIN.setSize(4);
        chOutToIN.setMax(8);
        tmlm.addChannel(chOutToIN);

        feedbackPerVC = new TMLEvent[nbOfVCs];
        for(int i=0; i<nbOfVCs; i++) {
            feedbackPerVC[i] = new TMLEvent("Feedback__" + getNaming(),
                    null, 8, true);
            tmlm.addEvent(feedbackPerVC[i]);
        }
    }

    public String getNaming() {
        return "P_" + previousRouter.getXPos() + "_" + previousRouter.getYPos() +
                "_N_" + nextRouter.getXPos() + "_" + nextRouter.getYPos() + add;
    }



}
