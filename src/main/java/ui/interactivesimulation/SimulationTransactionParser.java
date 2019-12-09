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

package ui.interactivesimulation;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class SimulationTransactionParser Parse simulation traces
 * 
 * Creation: 19/07/2019
 * 
 * @author Maysam ZOOR
 */

public class SimulationTransactionParser extends DefaultHandler {

    private Vector<SimulationTransaction> trans;
    private SimulationTransaction st = new SimulationTransaction();

    private StringBuilder data = null;

    public Vector<SimulationTransaction> getStList() {
        return trans;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (qName.equals("transinfo")) {

            st = new SimulationTransaction();
            st.nodeType = attributes.getValue("deviceid");

            try {
                st.uniqueID = new Integer(attributes.getValue("uniqueid"));
            } catch (Exception e) {

            }

            st.deviceName = attributes.getValue("devicename");
            String commandT = attributes.getValue("command");
            if (commandT != null) {
                int index = commandT.indexOf(": ");
                if (index == -1) {
                    st.taskName = "Unknown";
                    st.command = commandT;
                } else {
                    st.taskName = commandT.substring(0, index).trim();
                    st.command = commandT.substring(index + 1, commandT.length()).trim();
                }
            }

            st.startTime = attributes.getValue("starttime");
            st.endTime = attributes.getValue("endtime");
            st.length = attributes.getValue("length");
            st.virtualLength = attributes.getValue("virtuallength");
            st.channelName = attributes.getValue("ch");
            st.id = attributes.getValue("id");
            st.runnableTime = attributes.getValue("runnableTime");

            if (trans == null) {
                trans = new Vector<SimulationTransaction>();
            }
            trans.add(st);

        }

        data = new StringBuilder();

    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        data.append(new String(ch, start, length));
    }
}