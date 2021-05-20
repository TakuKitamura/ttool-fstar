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

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Class TMAP2Network Creation: 07/01/2019
 *
 * @author Ludovic Apvrille
 * @version 1.0 07/01/2019
 */
public class TMAP2Network<E> {

    public final static boolean DAEMON = false;

    private TMLMapping<?> tmlmapping;
    private HwNoC noc;

    private boolean debug;
    private boolean optimize;

    private int nbOfVCs = 2;
    private int nocSize = 2;
    private TranslatedRouter[][] routers;

    public final static int NORTH = 0;
    public final static int SOUTH = 1;
    public final static int WEST = 2;
    public final static int EAST = 3;
    public final static int DOMAIN = 4;

    public final static String[] PORT_NAME = { "North", "South", "West", "East", "Internal" };

    private HashMap<TMLChannel, String> IDsOfChannels;

    public TMAP2Network(TMLMapping<?> _tmlmapping, int nocSize) {
        tmlmapping = _tmlmapping;
        routers = new TranslatedRouter[nocSize][nocSize];
        this.nocSize = nocSize;
    }

    public String getChannelID(TMLChannel ch) {
        return IDsOfChannels.get(ch);
    }

    public Point getChannelDstXY(TMLChannel ch) {
        if (noc == null) {
            return null;
        }
        TMLTask t = ch.getDestinationTask();
        HwExecutionNode mappedOn = tmlmapping.getHwNodeOf(t);
        return noc.placementMap.get(mappedOn.getName());
    }

    public void putTMLChannelID(TMLChannel ch, String id) {
        IDsOfChannels.put(ch, "" + id);
    }

    public TMLMapping<?> getTMLMapping() {
        return tmlmapping;
    }

    public static boolean hasRouterAt(int myX, int myY, int routerPosition, int nocSize) {
        if (routerPosition == DOMAIN) {
            return true;
        }

        int decX = 0;
        int decY = 0;

        switch (routerPosition) {
            case NORTH:
                decY = -1;
                break;
            case SOUTH:
                decY = 1;
                break;
            case WEST:
                decX = -1;
                break;
            case EAST:
                decX = 1;

        }

        myX = myX + decX;
        myY = myY + decY;

        if ((myX < 0) || (myY < 0)) {
            return false;
        }

        if ((myX >= nocSize) || (myY >= nocSize)) {
            return false;
        }

        return true;
    }

    public TranslatedRouter getRouterAt(int xPos, int yPos) {
        if (routers == null) {
            return null;
        }

        if ((xPos < 0) || (xPos >= nocSize) || (yPos < 0) || (yPos >= nocSize)) {
            return null;
        }

        return routers[xPos][yPos];
    }

    public int getFrom(int port) {
        switch (port) {
            case NORTH:
                return SOUTH;
            case SOUTH:
                return NORTH;
            case WEST:
                return EAST;
            case EAST:
                return WEST;
        }
        return NORTH;
    }

    public TranslatedRouter getRouterFrom(int xPos, int yPos, int routerPosition) {
        int decX = 0;
        int decY = 0;

        switch (routerPosition) {
            case NORTH:
                decY = -1;
                break;
            case SOUTH:
                decY = 1;
                break;
            case WEST:
                decX = -1;
                break;
            case EAST:
                decX = 1;

        }

        xPos = xPos + decX;
        yPos = yPos + decY;

        return getRouterAt(xPos, yPos);

    }

    /*
     * List of assumptions: - Only one router set (i.e. no router, then bus, then
     * router) between two tasks - Channels must be mapped on at least one route to
     * be taken into account
     */
    public String removeAllRouterNodes() {
        int i, j;

        // TMLModeling<E> tmlm = new TMLModeling<>();
        // TMLArchitecture tmla = new TMLArchitecture();
        // tmlmapping = new TMLMapping<E>(tmlm, tmla, false);

        TMLArchitecture tmla = tmlmapping.getTMLArchitecture();
        TMLModeling<?> tmlm = tmlmapping.getTMLModeling();

        // *** we have to redo the architecture:
        // we assume that each processor is connected directly to the NoC via a first
        // bus
        // so, each CPU gets one memory, on bus connecting the mem and the NoC.
        // all local channels are mapped on this memory, otherwise they
        // use the bus

        // So, from the initial archi, we keep only the HwExecutionNodes and the NoC
        noc = tmla.getHwNoC();
        if (noc == null) {
            return "No NoC in the architecture";
        }

        tmla.removeAllNonHwExecutionNodes();

        // Then, for each HwExecNode, we add one bus and one memory
        // and we create the corresponding link
        tmla.getHwLinks().clear();
        List<HwNode> newList = new ArrayList<HwNode>();
        int nbOfHwExecutionNode = 0;
        for (HwNode node : tmla.getHwNodes()) {
            if (node instanceof HwExecutionNode) {
                nbOfHwExecutionNode++;
                // newList.add(node);

                HwBus bus = new HwBus(node.getName() + "__bus");
                bus.arbitration = HwBus.PRIORITY_BASED;
                HwMemory mem = new HwMemory(node.getName() + "__mem");
                newList.add(bus);
                newList.add(mem);

                HwLink cpuToBus = new HwLink(node.getName() + "__tocpu");
                cpuToBus.setNodes(bus, node);
                tmla.addHwLink(cpuToBus);

                HwLink memToBus = new HwLink(node.getName() + "__tomem");
                memToBus.setNodes(bus, mem);
                tmla.addHwLink(memToBus);
            }
        }

        for (HwNode node : newList) {
            tmla.addHwNode(node);
        }
        newList = null;

        // Check for no more CPU than gridsize
        // Put a random place to non placed CPU
        // All CPUs must be placed
        if (nbOfHwExecutionNode > (nocSize * nocSize)) {
            return "Too many processors for the NoC size";
        }

        // Check for placementMap
        if (noc.placementMap == null) {
            noc.makePlacement("", noc.size);
        }

        for (HwNode node : tmla.getHwNodes()) {
            if (node instanceof HwExecutionNode) {
                Point p = noc.placementMap.get(node.getName());
                if (p == null) {
                    // Processor not mapped on grid
                    // Find an available place
                    if (!(noc.map(node.getName()))) {
                        return "Could not map " + node.getName() + " on the NoC";
                    }
                }
            }
        }
        // the NoC is fully mapped. Let's print it!
        TraceManager.addDev("\nNoc:\n" + noc.toString() + "\n\n");

        // We need to update mapping information
        // First, wee keep only the task mapping
        // then, we map to the local memory only channels between tasks on the same CPU
        // Other tasks, i.e. communicating thu the NoC, are put in a special list
        tmlmapping.emptyCommunicationMapping();
        List<TMLChannel> channelsCommunicatingViaNoc = new ArrayList<>();
        List<tmltranslator.TMLChannel> allChannels = tmlm.getChannels();
        for (TMLChannel chan : allChannels) {
            HwNode originNode = tmlmapping.getHwNodeOf(chan.getOriginTask());
            HwNode destinationNode = tmlmapping.getHwNodeOf(chan.getDestinationTask());
            if (originNode == destinationNode) {
                // Channel mapped on the same node
                // We map it to the corresponding mem and bus
                HwNode bus = tmla.getHwNodeByName(originNode.getName() + "__bus");
                HwNode mem = tmla.getHwNodeByName(originNode.getName() + "__mem");
                if (bus != null)
                    tmlmapping.addCommToHwCommNode(chan, (HwCommunicationNode) bus);
                if (mem != null)
                    tmlmapping.addCommToHwCommNode(chan, (HwCommunicationNode) mem);
            } else {
                channelsCommunicatingViaNoc.add(chan);
            }
        }

        // *** Create routers
        Vector<HwExecutionNode> fakeCPUs = new Vector<>();
        for (i = 0; i < nocSize; i++) {
            for (j = 0; j < nocSize; j++) {
                // We must find the number of apps connected on this router
                HwExecutionNode hwExecNode = null;
                String s = noc.getHwExecutionNode(i, j);
                if (s != null)
                    hwExecNode = (HwExecutionNode) (tmla.getHwNodeByName(s));
                if (hwExecNode == null) {
                    HwCPU missingCPU = new HwCPU("EmptyCPUForDomain_x" + i + "_y" + j);
                    tmla.addHwNode(missingCPU);
                    hwExecNode = missingCPU;
                    fakeCPUs.add(missingCPU);
                }

                TranslatedRouter tr = new TranslatedRouter<>(this, tmlmapping, noc, channelsCommunicatingViaNoc,
                        nbOfVCs, i, j, hwExecNode);
                routers[i][j] = tr;
            }
        }

        // Create routers around
        // tmlmodeling = tmlmapping.getTMLModeling();
        // tmlarchi = tmlmapping.getTMLArchitecture();

        // *** Create links and update routers accordingly
        // For each router, I consider all routers that are around the considered one
        for (i = 0; i < nocSize; i++) {
            for (j = 0; j < nocSize; j++) {
                for (int k = 0; k < DOMAIN; k++) {

                    // NORTH?
                    if ((i > 0) && (k == NORTH)) {
                        // There is a north router
                        // link to next
                        if (routers[i][j].playingTheRoleOfPrevious[k] == null) {
                            Link to = new Link(tmla, tmlm, routers[i][j], routers[i - 1][j], nbOfVCs);
                            routers[i][j].playingTheRoleOfPrevious[k] = to;
                            routers[i - 1][j].playingTheRoleOfNext[getFrom(k)] = to;
                        }

                    }

                    // SOUTH?
                    if ((i < nocSize - 1) && (k == SOUTH)) {
                        // There is a south router
                        // link to next
                        if (routers[i][j].playingTheRoleOfPrevious[k] == null) {
                            Link to = new Link(tmla, tmlm, routers[i][j], routers[i + 1][j], nbOfVCs);
                            routers[i][j].playingTheRoleOfPrevious[k] = to;
                            routers[i + 1][j].playingTheRoleOfNext[getFrom(k)] = to;
                        }
                    }

                    // EAST?
                    if ((j < nocSize - 1) && (k == EAST)) {
                        // There is an east router
                        // link to next
                        if (routers[i][j].playingTheRoleOfPrevious[k] == null) {
                            Link to = new Link(tmla, tmlm, routers[i][j], routers[i][j + 1], nbOfVCs);
                            routers[i][j].playingTheRoleOfPrevious[k] = to;
                            routers[i][j + 1].playingTheRoleOfNext[getFrom(k)] = to;
                        }

                    }

                    // WEST?
                    if ((j > 0) && (k == WEST)) {
                        // There is an east router
                        // link to next
                        if (routers[i][j].playingTheRoleOfPrevious[k] == null) {
                            Link to = new Link(tmla, tmlm, routers[i][j], routers[i][j - 1], nbOfVCs);
                            routers[i][j].playingTheRoleOfPrevious[k] = to;
                            routers[i][j - 1].playingTheRoleOfNext[getFrom(k)] = to;
                        }

                    }

                }
            }
        }

        // Associate an id to all channels and a dest
        int id = 0;
        IDsOfChannels = new HashMap<>();
        for (TMLChannel ch : tmlm.getChannels()) {
            IDsOfChannels.put(ch, "" + id);
            id++;
        }

        // Make internal channels & events of routers
        for (i = 0; i < nocSize; i++) {
            for (j = 0; j < nocSize; j++) {
                // We must find the number of apps connected on this router
                routers[i][j].makeOutputEventsChannels();
            }
        }

        // Make all routers
        for (i = 0; i < nocSize; i++) {
            for (j = 0; j < nocSize; j++) {
                // We must find the number of apps connected on this router
                routers[i][j].makeRouter();
            }
        }

        // Integrate into the TMLMapping
        for (i = 0; i < nocSize; i++) {
            for (j = 0; j < nocSize; j++) {
                // We must find the number of apps connected on this router
                HwExecutionNode node = routers[i][j].getHwExecutionNode();
                HwBus bus;
                if (fakeCPUs.contains(node)) {
                    bus = new HwBus(node.getName() + "__bus");
                    HwMemory mem = new HwMemory(node.getName() + "__mem");
                    tmla.addHwNode(bus);
                    tmla.addHwNode(mem);

                    HwLink cpuToBus = new HwLink(node.getName() + "__tocpu");
                    cpuToBus.setNodes(bus, node);
                    tmla.addHwLink(cpuToBus);

                    HwLink memToBus = new HwLink(node.getName() + "__tomem");
                    memToBus.setNodes(bus, mem);
                    tmla.addHwLink(memToBus);

                } else {
                    bus = tmla.getHwBusByName(node.getName() + "__bus");
                    // TraceManager.addDev("Found bus=" + bus);
                }
                // TraceManager.addDev("Using bus=" + bus + " name=" + bus.getName());
                routers[i][j].makeHwArchitectureAndMapping(node, bus);
            }
        }

        // Handling origin channels
        for (i = 0; i < nocSize; i++) {
            for (j = 0; j < nocSize; j++) {
                routers[i][j].makeOriginChannels();
            }
        }

        // Handling destination channels
        for (i = 0; i < nocSize; i++) {
            for (j = 0; j < nocSize; j++) {
                routers[i][j].makeDestinationChannels();
            }
        }

        // Post processing of routers
        for (i = 0; i < nocSize; i++) {
            for (j = 0; j < nocSize; j++) {
                routers[i][j].postProcessing();
            }
        }

        // Removing useless channels i.e. channels routed by the router
        // The ones in channelID
        // We indeed assume all channels go to the NoC
        for (TMLChannel ch : IDsOfChannels.keySet()) {
            tmlm.removeChannel(ch);
        }

        // Printing routers
        for (i = 0; i < nocSize; i++) {
            for (j = 0; j < nocSize; j++) {
                // TraceManager.addDev(routers[i][j].toString() + "\n");
            }
        }

        return null; // That's all folks!
    }

}
