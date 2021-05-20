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

package tmltranslator;

import java.util.Objects;

/**
 * Class HwLink Creation: 05/09/2007
 *
 * @author Ludovic APVRILLE and Daniel KNORRECK
 * @version 1.0 05/09/2007
 */
public class HwLink implements Comparable<HwLink> {
    public HwBus bus;
    // public HwCommunicationNode bus;
    public HwVGMN vgmn;// DG 10.08.
    public HwCrossbar crossbar;// DG 10.08.

    public HwNode hwnode;
    protected String name;
    protected int priority;

    public HwLink(String _name) {
        name = _name;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int _priority) {
        priority = _priority;
    }

    @Override
    public int compareTo(HwLink o) {
        if (priority > o.getPriority())
            return 1;
        if (priority < o.getPriority())
            return -1;
        return 0;
    }

    public String toXML() {
        String s = "<LINK name=\"" + name + "\" bus=\"" + bus.getName() + "\" hwnode=\"" + hwnode.getName()
                + "\" priority=\"" + priority + "\" />\n";
        return s;
    }

    public void setNodes(HwBus bus, HwNode node) {
        this.bus = bus;
        this.hwnode = node;
    }

    public boolean areConnected(HwNode node1, HwNode node2) {
        if (connectedBusHwNode(node1, node2)) {
            return true;
        }
        return connectedBusHwNode(node2, node1);

    }

    private boolean connectedBusHwNode(HwNode nodeBus, HwNode node) {
        if (hwnode != node) {
            return false;
        }

        return (nodeBus == bus) || (nodeBus == vgmn) || (nodeBus == crossbar);
    }

    public boolean equalSpec(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HwLink hwLink = (HwLink) o;
        return priority == hwLink.priority && bus.getName().equals(hwLink.bus.getName())
                && hwnode.getName().equals(hwLink.hwnode.getName()) && Objects.equals(getName(), hwLink.getName());
    }

}
