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

package syscamstranslator;

import java.util.LinkedList;
import java.util.List;

/**
 * Class SysCAMSSpecification
 * List and number of all the elements in a SystemC-AMS diagram
 * Creation: 14/05/2018
 * @version 1.0 14/05/2018
 * @author Irina Kit Yan LEE, Rodrigo CORTES PORTO
 */

public class SysCAMSSpecification{
	private List<SysCAMSTComponent> components;
	private List<SysCAMSTConnector> connectors;

	public SysCAMSSpecification(List<SysCAMSTComponent> _components, List<SysCAMSTConnector> _connectors){
		components = _components ;
		connectors = _connectors ;
	}

	public List<SysCAMSTComponent> getComponents(){
		return components;
	}

	public List<SysCAMSTConnector> getConnectors(){
		return connectors;
	}

	public LinkedList<SysCAMSTBlockTDF> getAllBlockTDF(){
		LinkedList<SysCAMSTBlockTDF> blocksTDF = new LinkedList<SysCAMSTBlockTDF>();
		for (SysCAMSTComponent blockTDF : components) {
			if (blockTDF instanceof SysCAMSTBlockTDF) {
				blocksTDF.add((SysCAMSTBlockTDF) blockTDF);
			}
		}
		return blocksTDF;
	}

	public LinkedList<SysCAMSTBlockDE> getAllBlockDE(){
		LinkedList<SysCAMSTBlockDE> blocksDE = new LinkedList<SysCAMSTBlockDE>();
		for (SysCAMSTComponent blockDE : components) {
			if (blockDE instanceof SysCAMSTBlockDE) {
				blocksDE.add((SysCAMSTBlockDE) blockDE);
			}
		}
		return blocksDE;
	}

	public LinkedList<SysCAMSTBlockGPIO2VCI> getAllBlockGPIO2VCI(){
		LinkedList<SysCAMSTBlockGPIO2VCI> blocksGPIO2VCI = new LinkedList<SysCAMSTBlockGPIO2VCI>();
		for (SysCAMSTComponent blockGPIO2VCI : components) {
			if (blockGPIO2VCI instanceof SysCAMSTBlockGPIO2VCI) {
				blocksGPIO2VCI.add((SysCAMSTBlockGPIO2VCI) blockGPIO2VCI);
			}
		}
		return blocksGPIO2VCI;
	}

	public SysCAMSTCluster getCluster(){
		for (SysCAMSTComponent comp : components) {
			if (comp instanceof SysCAMSTCluster) {
				return (SysCAMSTCluster) comp;
			}
		}
		return null;
	}

	public LinkedList<SysCAMSTPortConverter> getAllPortConverter(){
		LinkedList<SysCAMSTPortConverter> portsConv = new LinkedList<SysCAMSTPortConverter>();
		for (SysCAMSTComponent portConv : components) {
			if (portConv instanceof SysCAMSTPortConverter) {
				portsConv.add((SysCAMSTPortConverter) portConv);
			}
		}
		return portsConv;
	}

	public LinkedList<SysCAMSTPortTDF> getAllPortTDF(){
		LinkedList<SysCAMSTPortTDF> portsTDF = new LinkedList<SysCAMSTPortTDF>();
		for (SysCAMSTComponent portTDF : components) {
			if (portTDF instanceof SysCAMSTPortTDF) {
				portsTDF.add((SysCAMSTPortTDF) portTDF);
			}
		}
		return portsTDF;
	}

	public LinkedList<SysCAMSTPortDE> getAllPortDE(){
		LinkedList<SysCAMSTPortDE> portsDE = new LinkedList<SysCAMSTPortDE>();
		for (SysCAMSTComponent portDE : components) {
			if (portDE instanceof SysCAMSTPortDE) {
				portsDE.add((SysCAMSTPortDE) portDE);
			}
		}
		return portsDE;
	}

	public LinkedList<SysCAMSTConnector> getAllConnectorCluster(){
		LinkedList<SysCAMSTConnector> cons = new LinkedList<SysCAMSTConnector>();
		for (SysCAMSTConnector con : connectors) {
			if (con instanceof SysCAMSTConnector) {
				if (con.get_p1().getComponent() instanceof SysCAMSTPortTDF && con.get_p2().getComponent() instanceof SysCAMSTPortTDF) {
					cons.add(con);
				} else if (con.get_p1().getComponent() instanceof SysCAMSTPortConverter && con.get_p2().getComponent() instanceof SysCAMSTPortDE) {
					if (((SysCAMSTPortDE) con.get_p2().getComponent()).getBlockDE() != null) {
						if ((((SysCAMSTPortDE) con.get_p2().getComponent()).getBlockDE()).getCluster() != null) {
							cons.add(con);
						}
					}
				} else if (con.get_p2().getComponent() instanceof SysCAMSTPortConverter && con.get_p1().getComponent() instanceof SysCAMSTPortDE) {
					if (((SysCAMSTPortDE) con.get_p1().getComponent()).getBlockDE() != null) {
						if ((((SysCAMSTPortDE) con.get_p1().getComponent()).getBlockDE()).getCluster() != null) {
							cons.add(con);
						}
					}
				} else if (con.get_p1().getComponent() instanceof SysCAMSTPortDE && con.get_p2().getComponent() instanceof SysCAMSTPortDE) {
					if (((SysCAMSTPortDE) con.get_p1().getComponent()).getBlockDE() != null && ((SysCAMSTPortDE) con.get_p2().getComponent()).getBlockDE() != null) {
						if ((((SysCAMSTPortDE) con.get_p1().getComponent()).getBlockDE()).getCluster() != null && (((SysCAMSTPortDE) con.get_p2().getComponent()).getBlockDE()).getCluster() != null) {
							cons.add(con);
						}
					}
				} 
			}
		}
		return cons;
	}
    
    public LinkedList<SysCAMSTConnector> getAllConnectorsCluster4Soclib(){
		LinkedList<SysCAMSTConnector> cons = new LinkedList<SysCAMSTConnector>();
		for (SysCAMSTConnector con : connectors) {
			if (con instanceof SysCAMSTConnector) {
				if (con.get_p1().getComponent() instanceof SysCAMSTPortTDF && con.get_p2().getComponent() instanceof SysCAMSTPortTDF) {
					cons.add(con);
				} else if (con.get_p1().getComponent() instanceof SysCAMSTPortConverter && con.get_p2().getComponent() instanceof SysCAMSTPortDE) {
					if (((SysCAMSTPortDE) con.get_p2().getComponent()).getBlockDE() != null) {
						if ((((SysCAMSTPortDE) con.get_p2().getComponent()).getBlockDE()).getCluster() != null) {
							cons.add(con);
						}
					} else if (((SysCAMSTPortDE) con.get_p2().getComponent()).getBlockGPIO2VCI() != null) {
                        cons.add(con);
                    }
				} else if (con.get_p2().getComponent() instanceof SysCAMSTPortConverter && con.get_p1().getComponent() instanceof SysCAMSTPortDE) {
					if (((SysCAMSTPortDE) con.get_p1().getComponent()).getBlockDE() != null) {
						if ((((SysCAMSTPortDE) con.get_p1().getComponent()).getBlockDE()).getCluster() != null) {
							cons.add(con);
						}
					} else if (((SysCAMSTPortDE) con.get_p1().getComponent()).getBlockGPIO2VCI() != null) {
                        cons.add(con);
                    }
				} else if (con.get_p1().getComponent() instanceof SysCAMSTPortDE && con.get_p2().getComponent() instanceof SysCAMSTPortDE) {
					if (((SysCAMSTPortDE) con.get_p1().getComponent()).getBlockDE() != null && ((SysCAMSTPortDE) con.get_p2().getComponent()).getBlockDE() != null) {
						if ((((SysCAMSTPortDE) con.get_p1().getComponent()).getBlockDE()).getCluster() != null && (((SysCAMSTPortDE) con.get_p2().getComponent()).getBlockDE()).getCluster() != null) {
							cons.add(con);
						}
					}
				} 
			}
		}
		return cons;
	}
    
    public LinkedList<SysCAMSTConnector> getAllConnectorsCluster4Matrix(){
        LinkedList<SysCAMSTConnector> cons = new LinkedList<SysCAMSTConnector>();
        for (SysCAMSTConnector con : connectors) {
            if (con instanceof SysCAMSTConnector) {
                if (con.get_p1().getComponent() instanceof SysCAMSTPortTDF && con.get_p2().getComponent() instanceof SysCAMSTPortTDF) {
                    cons.add(con);
                }
            }
        }
        return cons;
    }
    
    public LinkedList<SysCAMSTConnector> getAllConnectorsTdfDe(){
        LinkedList<SysCAMSTConnector> cons = new LinkedList<SysCAMSTConnector>();
        for (SysCAMSTConnector con : connectors) {
            if (con instanceof SysCAMSTConnector) {
                if (con.get_p1().getComponent() instanceof SysCAMSTPortTDF && con.get_p2().getComponent() instanceof SysCAMSTPortTDF) {
                    cons.add(con);
                } else if (con.get_p1().getComponent() instanceof SysCAMSTPortConverter && con.get_p2().getComponent() instanceof SysCAMSTPortDE) {
                    if (((SysCAMSTPortDE) con.get_p2().getComponent()).getBlockDE() != null) {
                        if ((((SysCAMSTPortDE) con.get_p2().getComponent()).getBlockDE()).getCluster() != null) {
                            cons.add(con);
                        }
                    } else if (((SysCAMSTPortDE) con.get_p2().getComponent()).getBlockGPIO2VCI() != null) {
                        cons.add(con);
                    }
                } else if (con.get_p2().getComponent() instanceof SysCAMSTPortConverter && con.get_p1().getComponent() instanceof SysCAMSTPortDE) {
                    if (((SysCAMSTPortDE) con.get_p1().getComponent()).getBlockDE() != null) {
                        if ((((SysCAMSTPortDE) con.get_p1().getComponent()).getBlockDE()).getCluster() != null) {
                            cons.add(con);
                        }
                    } else if (((SysCAMSTPortDE) con.get_p1().getComponent()).getBlockGPIO2VCI() != null) {
                        cons.add(con);
                    }
                }
            } 
        }
        return cons;
    }

	public int getNbBlockTDF(){
		return (getAllBlockTDF()).size();
	}

	public int getNbBlockDE(){
		return (getAllBlockDE()).size();
	}

	public int getNbBlockGPIO2VCI(){
		return (getAllBlockGPIO2VCI()).size();
	}

	public int getNbPortConverter(){
		return (getAllPortConverter()).size();
	}

	public int getNbPortTDF(){
		return (getAllPortTDF()).size();
	}

	public int getNbPortDE(){
		return (getAllPortDE()).size();
	}

	public int getNbConnectorCluster(){
		return (getAllConnectorCluster()).size();
	}
}
