package ui.interactivesimulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXParserHandler extends DefaultHandler {

	private Vector<SimulationTransaction> trans;
	private SimulationTransaction st = new SimulationTransaction();

	// List to hold Employees object
	// private List<Employee> empList = null;
	// private Employee emp = null;
	private StringBuilder data = null;

	// getter method for employee list
	public Vector<SimulationTransaction> getStList() {
		return trans;
	}

	boolean bAge = false;
	boolean bName = false;
	boolean bGender = false;
	boolean bRole = false;

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
                if (index == -1){
                    st.taskName = "Unknown";
                    st.command = commandT;
                } else {
                    st.taskName = commandT.substring(0, index).trim();
                    st.command = commandT.substring(index+1, commandT.length()).trim();
                }
            }

            //TraceManager.addDev("Command handled");
            st.startTime = attributes.getValue("starttime");
            st.endTime = attributes.getValue("endtime");
            String taskId= attributes.getValue("id");
            
            st.length = attributes.getValue("length");
            st.virtualLength = attributes.getValue("virtuallength");
            st.channelName = attributes.getValue("ch");

			// initialize list
			
			 if (trans == null) {
                 trans = new Vector<SimulationTransaction>();
             }
			trans.add(st);

		}

		// create the data container
		data = new StringBuilder();
		
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		/*
		 * if (qName.equals("transinfo")) { // add Employee object to list
		 * trans.add(st); }
		 */
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		data.append(new String(ch, start, length));
	}
}