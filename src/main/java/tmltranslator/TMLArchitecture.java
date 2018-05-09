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

import java.util.ArrayList;
import java.util.List;

/**
 * Class TMLArchitecture
 * Creation: 05/09/2007
 *
 * @author Ludovic APVRILLE
 * @version 1.1 19/05/2008
 */
public class TMLArchitecture {
    private List<HwNode> hwnodes;
    private List<HwLink> hwlinks; // Between buses and other component

    private int masterClockFrequency = 200; // in MHz

    private int hashCode;
    private boolean hashCodeComputed = false;


    public TMLArchitecture() {
        init();
    }

    private void init() {
        hwnodes = new ArrayList<HwNode>();
        hwlinks = new ArrayList<HwLink>();
    }

    private void computeHashCode() {
        TMLArchiTextSpecification architxt = new TMLArchiTextSpecification(/*"spec.tarchi"*/);
        String s = architxt.toTextFormat(this);
        hashCode = s.hashCode();
        //System.out.println("TARCHI hashcode = " + hashCode);
    }

    public int getHashCode() {
        if (!hashCodeComputed) {
            computeHashCode();
            hashCodeComputed = true;
        }

        return hashCode;
    }

    public void setMasterClockFrequency(int value) {
        masterClockFrequency = value;
    }

    public int getMasterClockFrequency() {
        return masterClockFrequency;
    }


    public void addHwNode(HwNode _node) {
        hwnodes.add(_node);
    }

    public List<HwNode> getHwNodes() {
        return hwnodes;
    }

    public HwCPU getFirstCPU() {
        for (HwNode node : hwnodes) {
            if (node instanceof HwCPU) {
                return (HwCPU) node;
            }
        }
        return null;
    }

    public HwBus getFirstBus() {
        for (HwNode node : hwnodes) {
            if (node instanceof HwBus) {
                return (HwBus) node;
            }
        }
        return null;
    }

    public HwMemory getFirstMemory() {
        for (HwNode node : hwnodes) {
            if (node instanceof HwMemory) {
                return (HwMemory) node;
            }
        }
        return null;
    }


    public boolean hasCPU() {
        for (HwNode node : hwnodes) {
            if (node instanceof HwCPU) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBus() {
        for (HwNode node : hwnodes) {
            if (node instanceof HwBus) {
                return true;
            }
        }
        return false;
    }

    public int getNbOfBusses() {
        int cpt = 0;
        for (HwNode node : hwnodes) {
            if (node instanceof HwBus) {
                cpt++;
            }
        }
        return cpt;
    }

    public boolean hasMemory() {
        for (HwNode node : hwnodes) {
            if (node instanceof HwMemory) {
                return true;
            }
        }
        return false;
    }

    public int getNbOfMemories() {
        int cpt = 0;
        for (HwNode node : hwnodes) {
            if (node instanceof HwMemory) {
                cpt++;
            }
        }
        return cpt;
    }

    public List<HwNode> getCPUs() {
        List<HwNode> cpus = new ArrayList<HwNode>();

        for (HwNode node : hwnodes) {
            if (node instanceof HwCPU) {
                cpus.add(node);
            }
        }

        return cpus;
    }

    public String[] getCPUIDs() {
        String[] list = new String[getNbOfCPU()];
        int cpt = 0;

        for (HwNode node : hwnodes) {
            if (node instanceof HwCPU) {
                list[cpt] = node.getName() + " (" + node.getID() + ")";
                cpt++;
            }
        }
        return list;
    }

    public String[] getCPUandHwAIDs() {
        String[] list = new String[getNbOfCPUandHwA()];
        int cpt = 0;

        for (HwNode node : hwnodes) {
            if ((node instanceof HwCPU) || (node instanceof HwA)) {
                list[cpt] = node.getName() + " (" + node.getID() + ")";
                cpt++;
            }
        }
        return list;
    }

    public String[] getBusIDs() {
        String[] list = new String[getNbOfBus()];
        int cpt = 0;

        for (HwNode node : hwnodes) {
            if (node instanceof HwBus) {
                list[cpt] = node.getName() + " (" + node.getID() + ")";
                cpt++;
            }
        }
        return list;
    }

    public String[] getMemIDs() {
        String[] list = new String[getNbOfMem()];
        int cpt = 0;

        for (HwNode node : hwnodes) {
            if (node instanceof HwMemory) {
                list[cpt] = node.getName() + " (" + node.getID() + ")";
                cpt++;
            }
        }
        return list;
    }


    public int getNbOfCPU() {
        int cpt = 0;
        for (HwNode node : hwnodes) {
            if (node instanceof HwCPU) {
                cpt++;
            }
        }
        return cpt;
    }

    public int getNbOfCPUandHwA() {
        int cpt = 0;
        for (HwNode node : hwnodes) {
            if ((node instanceof HwCPU) || (node instanceof HwA)) {
                cpt++;
            }
        }
        return cpt;
    }

    public int getNbOfBus() {
        int cpt = 0;
        for (HwNode node : hwnodes) {
            if (node instanceof HwBus) {
                cpt++;
            }
        }
        return cpt;
    }

    public int getNbOfMem() {
        int cpt = 0;
        for (HwNode node : hwnodes) {
            if (node instanceof HwMemory) {
                cpt++;
            }
        }
        return cpt;
    }

    public void addHwLink(HwLink _link) {
        hwlinks.add(_link);
    }

    public List<HwLink> getHwLinks() {
        return hwlinks;
    }

    public HwNode getHwNodeByName(String _name) {
        for (HwNode node : hwnodes) {
            if (node.getName().equals(_name)) {
                return node;
            }
        }
        return null;
    }

    public List<HwBridge> getFirewalls() {
        List<HwBridge> firewalls = new ArrayList<HwBridge>();

        for (HwNode node : hwnodes) {
            if (node instanceof HwBridge) {
                HwBridge bridge = (HwBridge) node;

                if (bridge.isFirewall) {
                    firewalls.add(bridge);
                }
            }
        }

        return firewalls;
    }

    public HwCPU getHwCPUByName(String _name) {
        for (HwNode node : hwnodes) {
            if (node.getName().equals(_name)) {
                if (node instanceof HwCPU) {
                    return (HwCPU) node;
                }
            }
        }
        return null;
    }

    public HwBus getHwBusByName(String _name) {
        for (HwNode node : hwnodes) {
            if (node.getName().equals(_name)) {
                if (node instanceof HwBus) {
                    return (HwBus) node;
                }
            }
        }
        return null;
    }

    public HwMemory getHwMemoryByName(String _name) {
        for (HwNode node : hwnodes) {
            if (node.getName().equals(_name)) {
                if (node instanceof HwMemory) {
                    return (HwMemory) node;
                }
            }
        }
        return null;
    }

    public HwLink getHwLinkByName(String _name) {
        for (HwLink link : hwlinks) {
            if (link.getName().equals(_name)) {
                return link;
            }
        }
        return null;
    }

    public List<HwLink> getLinkByHwNode(HwNode node) {
        List<HwLink> tempList = new ArrayList<HwLink>();

        for (HwLink link : hwlinks) {
            if (link.hwnode == node) tempList.add(link);
        }

        return tempList;
    }

    public HwLink getHwLinkByHwNode(HwNode node) {
        for (HwLink link : hwlinks) {
            if (link.hwnode == node) {
                return link;
            }
        }
        return null;
    }

    public boolean isNodeConnectedToBus(HwNode node, HwBus bus) {
        for (HwLink link : hwlinks) {
            if (node == link.hwnode && bus == link.bus) return true;
        }
        return false;
    }

    public List<HwLink> getLinkByBus(HwBus bus) {
        List<HwLink> tempList = new ArrayList<HwLink>();

        for (HwLink link : hwlinks) {
            if (link.bus == bus) tempList.add(link);
        }

        return tempList;
    }

    public int getArchitectureComplexity() {
        // CPU complexity depends on its data size
        // Bus complexity depends on its data size
        int complexity = 0;

        for (HwNode node : hwnodes) {
            if (node instanceof HwCPU) {
                HwCPU cpu = (HwCPU) node;
                complexity += cpu.nbOfCores * cpu.byteDataSize * cpu.pipelineSize;
                //TraceManager.addDev("complexity CPU= " + complexity);
            }

            if (node instanceof HwBus) {
                HwBus bus = (HwBus) node;
                complexity += bus.byteDataSize * bus.pipelineSize;
                //TraceManager.addDev("complexity bus= " + complexity);
            }
        }

        //TraceManager.addDev("Complexity = " + complexity);

        return complexity;
    }

    public void replaceFirewall(HwBridge firewall, HwCPU newCPU) {
        hwnodes.remove(firewall);
        addHwNode(newCPU);
        for (HwLink link : hwlinks) {
            if (link.hwnode == firewall) {
                link.hwnode = newCPU;
            }
        }
    }

    public String toXML() {
        String s = "<TMLARCHITECTURE freq=\"" + masterClockFrequency + "\" >\n";
        for (HwNode node : hwnodes) {
            s += node.toXML();
        }
        for (HwLink link : hwlinks) {
            s += link.toXML();
        }
        s += "</TMLARCHITECTURE>";
        return s;
    }

    public boolean areConnected(HwNode node1, HwNode node2) {
        for (HwLink link : hwlinks) {
            if (link.areConnected(node1, node2)) {
                return true;
            }
        }
        return false;
    }
}
