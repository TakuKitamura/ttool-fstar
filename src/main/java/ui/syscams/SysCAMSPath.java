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

package ui.syscams;

import ui.TGComponent;
import java.util.ArrayList;

/**
 * Class SysCAMSPath
 * Notion of Path. To be used to analyze the correctness of paths in the model
 * Creation: 07/05/2018
 * @version 1.0 07/05/2018
 * @author Irina Kit Yan LEE
 */

public class SysCAMSPath  {

    public ArrayList<SysCAMSPrimitivePort> producerPorts;
    public ArrayList<SysCAMSPrimitivePort> consumerPorts;

    // Facilities
    public ArrayList<SysCAMSFork> forks;
    public ArrayList<SysCAMSJoin> joins;

    private boolean errorOfConnection = false;

    private int errorNumber;
    private TGComponent faultyComponent;


    private String[] errors = {"Fork and Join operators in the same path",
                               "Must have at least one sender",
                               "Must have at least one receiver",
                               "More than one sender in a path with a fork",
                               "Senders and receivers are not of the same kind",
                               "One of more element of the path is badly connected",
                               "Events are not compatible with fork/join",
                               "Requests are not compatible with fork/join",
                               "Events/requests must all have the same parameters",
                               "Channels and events can have only one input and one output"
    };

    public SysCAMSPath() {
        producerPorts = new ArrayList<SysCAMSPrimitivePort>();
        consumerPorts = new ArrayList<SysCAMSPrimitivePort>();
        forks = new ArrayList<SysCAMSFork>();
        joins = new ArrayList<SysCAMSJoin>();
    }

    public void addComponent(TGComponent _tgc) {
        if (_tgc instanceof SysCAMSPrimitivePort) {
            SysCAMSPrimitivePort p = (SysCAMSPrimitivePort)_tgc;
            if (p.getOrigin() == 1) {
                producerPorts.add(p);
            } else {
                consumerPorts.add(p);
            }
        }
        if (_tgc instanceof SysCAMSFork) {
            forks.add((SysCAMSFork)_tgc);
        }
        if (_tgc instanceof SysCAMSJoin) {
            joins.add((SysCAMSJoin)_tgc);
        }
    }

    public void setErrorOfConnection(boolean _err) {
        errorOfConnection = _err;
    }

    public boolean getErrorOfConnection() {
        return errorOfConnection;
    }

    public boolean contains(TGComponent tgc) {
        if (producerPorts.contains(tgc)) {
            return true;
        }
        if (consumerPorts.contains(tgc)) {
            return true;
        }
        if (forks.contains(tgc)) {
            return true;
        }
        return joins.contains(tgc);
    }

    public void mergeWith(SysCAMSPath _path) {
        producerPorts.addAll(_path.producerPorts);
        consumerPorts.addAll(_path.consumerPorts);
        forks.addAll(_path.forks);
        joins.addAll(_path.joins);
        setErrorOfConnection(getErrorOfConnection() || _path.getErrorOfConnection());
    }

    public boolean hasError() {
        return (errorNumber != -1);
    }

    public String getErrorMessage() {
        if (hasError()) {
            return errors[errorNumber];
        }
        return "";
    }

    public TGComponent getFaultyComponent() {
        return faultyComponent;
    }

    public void checkRules() {
        errorNumber = -1;

        //rule0: fork or join, but not both
        if ((forks.size() > 0) && (joins.size() >0)) {
            faultyComponent = forks.get(0);
            errorNumber = 0;
        }

        //rule1: Must have at least one producer
        if (producerPorts.size() == 0) {
            errorNumber = 1;
            if ((consumerPorts != null) && (consumerPorts.size() > 0)) {
                faultyComponent = consumerPorts.get(0);
            }
        }

        //rule2: Must have at least one receiver
        if (consumerPorts.size() == 0) {
            errorNumber = 2;
            if ((producerPorts != null) && (producerPorts.size() > 0)) {
                faultyComponent = producerPorts.get(0);
            }
        }

        //rule3: If fork: must have only one producer
        if ((forks.size() > 0) && (producerPorts.size() >1)) {
            errorNumber = 3;
            faultyComponent = forks.get(0);
        }

        //rule4: producers and consumers must be of the same type
        if ((consumerPorts.size()>0) && (producerPorts.size()>0)) {
            int type = consumerPorts.get(0).getPortType();
            for (SysCAMSPrimitivePort porto: producerPorts) {
                if (porto.getPortType() != type) {
                    errorNumber = 4;
                    faultyComponent = porto;
                    break;
                }
            }
            for (SysCAMSPrimitivePort porti: consumerPorts) {
                if (porti.getPortType() != type) {
                    errorNumber = 4;
                    faultyComponent = porti;
                    break;
                }
            }
        }

        //rule5: Error of connection
        if (errorOfConnection) {
            errorNumber = 5;
        }

        //rule7: requests cannot be connected through fork or join
        if ((forks.size() > 0) || (joins.size() >0)) {
            // Look for event, either at origin, or at destination
            for (SysCAMSPrimitivePort porto: producerPorts) {
                if (porto.getPortType() == 2) {
                    errorNumber = 7;
                    faultyComponent = porto;
                    break;
                }
            }
            for (SysCAMSPrimitivePort porti: consumerPorts) {
                if (porti.getPortType() == 2) {
                    errorNumber = 7;
                    faultyComponent = porti;
                    break;
                }
            }
        }

        //rule8: all events/requests with the same parameters
//        if ((forks.size() > 0) || (joins.size() >0)) {
//            if (producerPorts != null && producerPorts.size() > 0) {
//                SysCAMSPrimitivePort referencePort = producerPorts.get(0);
//                if (referencePort != null) {
//                    if ((referencePort.getPortType() == 1) ||(referencePort.getPortType() == 2)) {
//                        // Event or request found
//                        // We now check that they are all compatible with the reference
//                        for (SysCAMSPrimitivePort porto: producerPorts) {
//                            if (!(porto.hasSameParametersThan(referencePort))) {
//                                errorNumber = 8;
//                                faultyComponent = porto;
//                                break;
//                            }
//                        }
//
//                        for (SysCAMSPrimitivePort porti: consumerPorts) {
//                            if (!(porti.hasSameParametersThan(referencePort))) {
//                                errorNumber = 8;
//                                faultyComponent = porti;
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//        }

        // rule9: if no fork, no join, no request: one to one communication
        boolean foundReq = true;
        if ((forks.size() == 0) && (joins.size() == 0)) {
            if (producerPorts != null && producerPorts.size() > 0) {
                SysCAMSPrimitivePort referencePort = producerPorts.get(0);
                if (referencePort != null) {
                    if (referencePort.getPortType() != 2) {
                        foundReq = false;
                    }
                }
            }
            if (!foundReq) {
                if (producerPorts.size() > 1) {
                    errorNumber = 9;
                    faultyComponent = producerPorts.get(1);
                } else if (consumerPorts.size() > 1) {
                    errorNumber = 9;
                    faultyComponent = consumerPorts.get(1);
                }
            }
        }
    }

    public void setColor() {
        // For each channel facility,
        // set the inp and outp primitive ports if possible (otherwise, null)
        // if no error: set conflict to false
        // If error -> set the conflict to true

        for (SysCAMSFork fork: forks) {
            if (producerPorts.size() > 0) {
                fork.setOutPort(producerPorts.get(0));
            } else {
                fork.setOutPort(null);
            }

            if (consumerPorts.size() > 0) {
                fork.setInPort(consumerPorts.get(0));
            } else {
                fork.setInPort(null);
            }

            if (hasError()) {
                fork.setConflict(hasError(), errors[errorNumber]);
            } else {
                fork.setConflict(false, "");
            }
        }

        for (SysCAMSJoin join: joins) {
            if (producerPorts.size() > 0) {
                join.setOutPort(producerPorts.get(0));
            } else {
                join.setOutPort(null);
            }

            if (consumerPorts.size() > 0) {
                join.setInPort(consumerPorts.get(0));
            } else {
                join.setInPort(null);
            }
            if (hasError()) {
                join.setConflict(hasError(), errors[errorNumber]);
            } else {
                join.setConflict(false, "");
            }
        }

        for (SysCAMSPrimitivePort pport: producerPorts) {
            if (hasError()) {
                pport.setConflict(hasError(), errors[errorNumber]);
            } else {
                pport.setConflict(false, "");
            }
        }
        for (SysCAMSPrimitivePort cport: consumerPorts) {
            if (hasError()) {
                cport.setConflict(hasError(), errors[errorNumber]);
            } else {
                cport.setConflict(false, "");
            }
        }
    }
}