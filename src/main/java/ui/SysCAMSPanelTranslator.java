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

package ui;

import syscamstranslator.*;
import ui.syscams.*;

import java.util.*;

import javax.swing.DefaultListModel;

/**
 * Class SysCAMSPanelTranslator
 * Translation of semantics of SystemC-AMS Diagrams
 * Creation: 19/05/2018
 * @version 1.0 19/05/2018
 * @author Irina Kit Yan LEE
 */

public class SysCAMSPanelTranslator {

	private List<TGComponent> tgcComponents;

	private List<SysCAMSTComponent> syscamsComponents;
	private List<SysCAMSTConnector> syscamsConnectors;

	public SysCAMSPanelTranslator(SysCAMSComponentTaskDiagramPanel _syscamsDiagramPanel) {
		tgcComponents = _syscamsDiagramPanel.getComponentList();

		syscamsComponents = new LinkedList<SysCAMSTComponent>();
		syscamsConnectors = new LinkedList<SysCAMSTConnector>();

		MakeListOfComponent(_syscamsDiagramPanel);
	}

	private void MakeListOfComponent(SysCAMSComponentTaskDiagramPanel syscamsDiagramPanel) {

		Map<TGComponent, SysCAMSTComponent> syscamsMap = new HashMap<TGComponent, SysCAMSTComponent>();

		TGComponent tgc;
		Iterator<TGComponent> iterator1 = tgcComponents.listIterator();
		Iterator<TGComponent> iterator2 = tgcComponents.listIterator();
		List<TGComponent> list = new ArrayList<TGComponent>();

		while(iterator1.hasNext()) {
			tgc = iterator1.next();
			if (!(tgc instanceof TGConnector)) {
				list.add(tgc);
			}
		}
		while(iterator2.hasNext()) {
			tgc = iterator2.next();
			if (tgc instanceof TGConnector) {
				list.add(tgc);
			}
		}	

		for (TGComponent dp : list) {
			if (dp instanceof SysCAMSBlockGPIO2VCI) {
				SysCAMSBlockGPIO2VCI blockGPIO2VCI = (SysCAMSBlockGPIO2VCI) dp;

				SysCAMSTBlockGPIO2VCI syscamsBlockGPIO2VCI = new SysCAMSTBlockGPIO2VCI();

				List<SysCAMSPortDE> portsDE = blockGPIO2VCI.getAllInternalPortsDE();
				for (int i = 0; i < portsDE.size(); i++) {
					SysCAMSPortDE portDE = portsDE.get(i);

					String portName = portDE.getPortName();
					String type = portDE.getDEType();
					int origin = portDE.getOrigin();
					boolean sensitive = portDE.getSensitive();
					String sensitiveMethod = portDE.getSensitiveMethod();

					SysCAMSTPortDE syscamsPortDE = new SysCAMSTPortDE(portName, origin, type, sensitive, sensitiveMethod, syscamsBlockGPIO2VCI);

					syscamsMap.put(portDE, syscamsPortDE);
					syscamsBlockGPIO2VCI.addPortDE(syscamsPortDE);
					syscamsComponents.add(syscamsPortDE);
				}	
				syscamsMap.put(blockGPIO2VCI, syscamsBlockGPIO2VCI);
				//System.out.println("@@@ GPIO block put in map @@@");
				syscamsComponents.add(syscamsBlockGPIO2VCI);
			} else if (dp instanceof SysCAMSBlockDE) {
				SysCAMSBlockDE blockDE = (SysCAMSBlockDE) dp;

				String blockDEName = blockDE.getValue();
//				int periodBlock = blockDE.getPeriod();
//				String time = blockDE.getTime();
//				String nameFn = blockDE.getNameFn();
//				String code = blockDE.getCode();
//				DefaultListModel<String> listStruct = blockDE.getListStruct();
//				String nameTemplate = blockDE.getNameTemplate();
//				String typeTemplate = blockDE.getTypeTemplate();
//				DefaultListModel<String> listTypedef = blockDE.getListTypedef();

				SysCAMSTBlockDE syscamsBlockDE = new SysCAMSTBlockDE(blockDEName, "", "", null, "", "", "", null, null);

				List<SysCAMSPortDE> portsDE = blockDE.getAllInternalPortsDE();
				for (int i = 0; i < portsDE.size(); i++) {
					SysCAMSPortDE portDE = portsDE.get(i);

					String portName = portDE.getPortName();
//					int periodPort = portDE.getPeriod();
//					String time = portDE.getTime();
//					int rate = portDE.getRate();
//					int delay = portDE.getDelay();
					int nbits = portDE.getNbits();//DG
					String type = portDE.getDEType();
					int origin = portDE.getOrigin();
					boolean sensitive = portDE.getSensitive();
					String sensitiveMethod = portDE.getSensitiveMethod();

					SysCAMSTPortDE syscamsPortDE = new SysCAMSTPortDE(portName, origin, type, sensitive, sensitiveMethod, syscamsBlockDE);

					syscamsMap.put(portDE, syscamsPortDE);
					syscamsBlockDE.addPortDE(syscamsPortDE);
					syscamsComponents.add(syscamsPortDE);
				}
			
				syscamsMap.put(blockDE, syscamsBlockDE);
				//	System.out.println("@@@ DE block put in map @@@");
				syscamsComponents.add(syscamsBlockDE);
			} else if (dp instanceof SysCAMSCompositeComponent) {
				SysCAMSCompositeComponent cluster = (SysCAMSCompositeComponent) dp;

				String clusterName = cluster.getValue();

				SysCAMSTCluster syscamsCluster = new SysCAMSTCluster(clusterName);

				List<SysCAMSBlockTDF> blocksTDF = cluster.getAllBlockTDFComponents();
				List<SysCAMSBlockDE> blocksDE = cluster.getAllBlockDEComponents();
				for (int i = 0; i < blocksTDF.size(); i++) {
					SysCAMSBlockTDF blockTDF = blocksTDF.get(i);

					String blockTDFName = blockTDF.getValue();
					double periodBlock = blockTDF.getPeriod();
					String timeBlock = blockTDF.getTime();
                    if (timeBlock.equals("s")) {
                        timeBlock = timeBlock + "ec";
                    } else if (timeBlock.equals("\u03BCs")) {
                        timeBlock = "us";
                    }
					String processCode = blockTDF.getProcessCode();
                    String constructorCode = blockTDF.getConstructorCode();
					DefaultListModel<String> listStruct = blockTDF.getListStruct();
					String nameTemplate = blockTDF.getNameTemplate();
					String typeTemplate = blockTDF.getTypeTemplate();
                    String valueTemplate = blockTDF.getValueTemplate();
					DefaultListModel<String> listTypedef = blockTDF.getListTypedef();

					SysCAMSTBlockTDF syscamsBlockTDF = new SysCAMSTBlockTDF(blockTDFName, periodBlock, timeBlock, processCode, constructorCode, listStruct, nameTemplate, typeTemplate, valueTemplate, listTypedef, syscamsCluster);				

					List<SysCAMSPortTDF> portsTDF = blockTDF.getAllInternalPortsTDF();
					for (int j = 0; j < portsTDF.size(); j++) {
						SysCAMSPortTDF portTDF = portsTDF.get(j);

						String portName = portTDF.getPortName();
						double periodPort = portTDF.getPeriod();
						String time = portTDF.getTime();
                        if (time.equals("s")) {
                            time = time + "ec";
                        } else if (time.equals("\u03BCs")) {
                            time = "us";
                        }
						int rate = portTDF.getRate();
						int delay = portTDF.getDelay();
						String type = portTDF.getTDFType();
						int origin = portTDF.getOrigin();

						SysCAMSTPortTDF syscamsPortTDF = new SysCAMSTPortTDF(portName, periodPort, time, rate, delay, origin, type, syscamsBlockTDF);

						syscamsMap.put(portTDF, syscamsPortTDF);
						syscamsBlockTDF.addPortTDF(syscamsPortTDF);
						syscamsComponents.add(syscamsPortTDF);
					}
					List<SysCAMSPortConverter> portsConverter = blockTDF.getAllInternalPortsConv();
					for (int j = 0; j < portsConverter.size(); j++) {
						SysCAMSPortConverter portConverter = portsConverter.get(j);

						String portName = portConverter.getPortName();
						double periodPort = portConverter.getPeriod();
						String time = portConverter.getTime();
                        if (time.equals("s")) {
                            time = time + "ec";
                        } else if (time.equals("\u03BCs")) {
                            time = "us";
                        }
			int rate = portConverter.getRate();
			int nbits = portConverter.getNbits();//DG
						int delay = portConverter.getDelay();
						String type = portConverter.getConvType();
						int origin = portConverter.getOrigin();

						SysCAMSTPortConverter syscamsPortConverter = new SysCAMSTPortConverter(portName, periodPort, time, rate, delay, origin, type, syscamsBlockTDF);

						syscamsMap.put(portConverter, syscamsPortConverter);
						syscamsBlockTDF.addPortConverter(syscamsPortConverter);
						syscamsComponents.add(syscamsPortConverter);
					}
					syscamsMap.put(blockTDF, syscamsBlockTDF);
					//System.out.println("@@@ TDF block put in map @@@");
					syscamsCluster.addBlockTDF(syscamsBlockTDF);
					syscamsComponents.add(syscamsBlockTDF);
				}
				for (int i = 0; i < blocksDE.size(); i++) {
					SysCAMSBlockDE blockDE = blocksDE.get(i);
					
					String blockDEName = blockDE.getValue();
//					int periodBlock = blockDE.getPeriod();
//					String time = blockDE.getTime();
					String nameFn = blockDE.getNameFn();
					String code = blockDE.getCode();
					DefaultListModel<String> listStruct = blockDE.getListStruct();
					String nameTemplate = blockDE.getNameTemplate();
					String typeTemplate = blockDE.getTypeTemplate();
                    String valueTemplate = blockDE.getValueTemplate();
					DefaultListModel<String> listTypedef = blockDE.getListTypedef();

					SysCAMSTBlockDE syscamsBlockDE = new SysCAMSTBlockDE(blockDEName, nameFn, code, listStruct, nameTemplate, typeTemplate, valueTemplate, listTypedef, syscamsCluster);

					List<SysCAMSPortDE> portsDE = blockDE.getAllInternalPortsDE();
					for (int j = 0; j < portsDE.size(); j++) {
						SysCAMSPortDE portDE = portsDE.get(j);

						String portName = portDE.getPortName();
//						int periodPort = portDE.getPeriod();
//						String time = portDE.getTime();
//						int rate = portDE.getRate();
//						int delay = portDE.getDelay();
						String type = portDE.getDEType();
						int origin = portDE.getOrigin();
						boolean sensitive = portDE.getSensitive();
						String sensitiveMethod = portDE.getSensitiveMethod();

						SysCAMSTPortDE syscamsPortDE = new SysCAMSTPortDE(portName, origin, type, sensitive, sensitiveMethod, syscamsBlockDE);

						syscamsMap.put(portDE, syscamsPortDE);
						syscamsBlockDE.addPortDE(syscamsPortDE);
						syscamsComponents.add(syscamsPortDE);
					}	
					syscamsMap.put(blockDE, syscamsBlockDE);
					syscamsCluster.addBlockDE(syscamsBlockDE);
					syscamsComponents.add(syscamsBlockDE);
				}
				syscamsMap.put(cluster, syscamsCluster);
				syscamsComponents.add(syscamsCluster);
			} else if (dp instanceof SysCAMSPortConnector) {
				SysCAMSPortConnector connector = (SysCAMSPortConnector) dp;

				TGConnectingPoint connectingPoint1 = connector.get_p1();
				TGConnectingPoint connectingPoint2 = connector.get_p2();	
				
				String name = connector.getValue();

				TGComponent owner_p1 = syscamsDiagramPanel.getComponentToWhichBelongs(connectingPoint1);
				TGComponent owner_p2 = syscamsDiagramPanel.getComponentToWhichBelongs(connectingPoint2);

				SysCAMSTComponent avowner_p1 = syscamsMap.get(owner_p1);	
				SysCAMSTComponent avowner_p2 = syscamsMap.get(owner_p2);

				SysCAMSTConnectingPoint avConnectingPoint1 = new SysCAMSTConnectingPoint(avowner_p1);
				SysCAMSTConnectingPoint avConnectingPoint2 = new SysCAMSTConnectingPoint(avowner_p2);

				SysCAMSTConnector avconnector = new SysCAMSTConnector(avConnectingPoint1, avConnectingPoint2, name);			
				syscamsConnectors.add(avconnector);
			}
		}
	}

	public SysCAMSSpecification getSysCAMSSpecification() {
		return new SysCAMSSpecification(syscamsComponents, syscamsConnectors);
	}
}
