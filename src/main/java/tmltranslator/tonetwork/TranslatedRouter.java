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

import tmltranslator.*;
import ui.TGComponent;

import java.util.Vector;


/**
 * Class TaskINForVC
 * Creation: 17/01/2019
 *
 * @author Ludovic Apvrille
 * @version 1.0 17/01/2019
 */
public class TranslatedRouter<E>  {
    private final int NB_OF_PORTS = 5;
    private final int CHANNEL_SIZE = 4;
    private final int CHANNEL_MAX = 8;

    private int nbOfVCs, xPos, yPos, nbOfApps;
    private TMLMapping<E> map;
    private Vector<TMLEvent> pktins;

    private Vector<TMLTask> dispatchers;



    public TranslatedRouter(int nbOfApps, int nbOfVCs, int xPos, int yPos) {
        this.nbOfVCs = nbOfVCs;
        this.nbOfApps = nbOfApps;
        this.xPos = xPos;
        this.yPos = yPos;
    }


    private String getInfo() {
        return "__R" + xPos + "_" + yPos;
    }

    public void makeRouter() {
        int i, j;
        TMLTask t;

        // A router is made upon tasks, hardware components and a mapping i.e. a TMLMapping
        TMLModeling<E> tmlm = new TMLModeling<>();
        TMLArchitecture tmla = new TMLArchitecture();
        map = new TMLMapping<E>(tmlm, tmla, false);

        // MUX for the different writing tasks





        // VC DISPATCHERS
        // One dispatcher per port
        // A dispatcher outputs to VCs tasks
        dispatchers = new Vector<>();
        for(i=0; i<NB_OF_PORTS; i++) {
            //TaskINForDispatch dt = new TaskINForDispatch(nbOfVCs);
            //dispatchers.add(dt);
        }

        // PORT DISPATCHER // according to destination


        // Create all channels
        // For each input VC, we have to create a channel
        for (i=0; i<NB_OF_PORTS*nbOfVCs; i++) {
            TMLChannel channel = new TMLChannel("Channel" + i + getInfo(), this);
            channel.setSize(CHANNEL_SIZE);
            channel.setMax(CHANNEL_MAX);
            channel.setType(TMLChannel.BRBW);
            tmlm.addChannel(channel);
        }


        // Create all events
        // pktins: between tasks



        // Feedbacks between OUTVC and INVC





        // Create all tasks

        // Create the architecture

        // Map tasks

        // Map channels

    }



}
