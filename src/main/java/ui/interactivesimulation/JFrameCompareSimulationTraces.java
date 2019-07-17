package ui.interactivesimulation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import avatartranslator.AvatarSpecification;
import myutil.GraphicLib;
import myutil.TraceManager;
import ui.MainGUI;
import ui.SimulationTrace;
import ui.util.IconManager;

public class JFrameCompareSimulationTraces extends JFrame implements ActionListener, WindowListener {

	private MainGUI mgui;
	private JButton browse, parse, difference, latencyDetails;
	private JButton openButton, saveButton;
	private JTextArea log;
	private JFileChooser fc;
	private File file;
	private SimulationTrace selectedST;
	private static Vector<SimulationTransaction> transFile1;

	public Vector<SimulationTransaction> getTransFile1() {
		return transFile1;
	}

	public void setTransFile1(Vector<SimulationTransaction> transFile1) {
		this.transFile1 = transFile1;
	}

	public Vector<SimulationTransaction> getTransFile2() {
		return transFile2;
	}

	public void setTransFile2(Vector<SimulationTransaction> transFile2) {
		this.transFile2 = transFile2;
	}

	private Vector<SimulationTransaction> transFile2;
	private SimulationTransaction st = new SimulationTransaction();
	private JTextField file2 = new JTextField();
	private boolean panelAdded = false;
	private static JPanelCompareXmlGraph newContentPane;
	private JComboBox<Object> devicesDropDownCombo1 = new JComboBox<Object>();
	private JComboBox<Object> devicesDropDownCombo2 = new JComboBox<Object>();
	private Vector<Object> transacationsDropDown1, transacationsDropDown2;
	private JComboBox<Object> tracesCombo1, tracesCombo2;

	public JFrameCompareSimulationTraces(MainGUI _mgui, String _title, SimulationTrace sST) {

		super(_title);

		this.selectedST = sST;
		GridLayout myLayout = new GridLayout(3, 1);

		this.setBackground(Color.RED);
		this.setLayout(myLayout);

		// f = _f;
		mgui = _mgui;
		addWindowListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// setIconImage(IconManager.img5100);
		// setBackground(Color.WHITE);

		fc = new JFileChooser();

		JPanel buttonPanel = new JPanel(new GridBagLayout()); // use FlowLayout
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NORTHWEST;

		JTextField file1 = new JTextField();

		JLabel lab1 = new JLabel("First Simulation Traces File ", JLabel.LEFT);
		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.WEST;
		buttonPanel.add(lab1, c);

		JLabel lab2 = new JLabel("Secound Simulation Traces File ", JLabel.LEFT);

		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.WEST;
		buttonPanel.add(lab2, c);

		file1.setEditable(false);
		file1.setBorder(new LineBorder(Color.BLACK));
		file1.setText(selectedST.getFullPath());

		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 2;
		c.weighty = 1;
		buttonPanel.add(file1, c);

		file2.setEditable(false);
		file2.setText("file 2 name");
		file2.setBorder(new LineBorder(Color.BLACK));
		// file2.setSize(1, 1);
		// file2.setPreferredSize(file2.getSize());

		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		buttonPanel.add(file2, c);

		browse = new JButton("Browse");
		browse.addActionListener(this);
		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 1;
		c.weighty = 1;
		buttonPanel.add(browse, c);

		parse = new JButton("parse");
		parse.addActionListener(this);
		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 3;
		c.gridy = 2;
		c.weightx = 5;
		c.weighty = 1;
		buttonPanel.add(parse, c);

		difference = new JButton("difference");
		difference.addActionListener(this);
		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 3;
		c.gridy = 3;
		c.weightx = 5;
		c.weighty = 1;
		buttonPanel.add(difference, c);

		this.add(buttonPanel);

		/*
		 * // For layout purposes, put the buttons in a separate panel
		 * 
		 * buttonPanel.setBackground(Color.yellow);
		 * 
		 * buttonPanel.add(lab1); buttonPanel.add(file1); buttonPanel.add(new
		 * Label("")); buttonPanel.add(lab2); buttonPanel.add(file2);
		 * buttonPanel.add(browse);
		 * 
		 * buttonPanel.add(new Label("")); buttonPanel.add(new Label(""));
		 * 
		 * buttonPanel.add(parse);
		 * 
		 * 
		 * 
		 * this.add(buttonPanel);
		 */
		// JPanelCompareXmlGraph graphPanel = new JPanelCompareXmlGraph();
		// JPanelCompareXmlXYChart xychart = new JPanelCompareXmlXYChart();
		// For layout purposes, put the g in a separate panel
		/// JPanel graphPanel = new JPanel(); // use FlowLayout

		// this.add(graphPanel);

		// Sets the specified boolean to indicate whether or not
		// this textfield should be editable.

		this.pack();
		this.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// System.out.println(e.getActionCommand());
		if (e.getSource() == browse) {
			// Handle open button action.

			int returnVal = fc.showOpenDialog(JFrameCompareSimulationTraces.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				file2.setText(file.getPath());

			}
		} else if (e.getSource() == parse) {
			// Handle open button action.

			// System.out.println("Time to parse");
			// System.out.println(selectedST.getFullPath());
			// System.out.println(file.getPath());
			try {
				int x = parseXML(selectedST.getFullPath(), file.getPath());
				DrawSimulationResults(transFile1, transFile2);

			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (e.getSource() == latencyDetails) {
			//System.out.println("Time for latency analysis");
			JTable table = newContentPane.getTable();

			JFrameShowLatencyDetails showLatencyDetails = new JFrameShowLatencyDetails(transFile1, transFile2,
					devicesDropDownCombo1.getSelectedItem(), tracesCombo1.getSelectedItem(),
					devicesDropDownCombo2.getSelectedItem(), tracesCombo2.getSelectedItem());

		} else if (e.getSource() == difference) {
			// Handle open button action.

			//System.out.println("Time to show the difference");

			newContentPane.showDifference();
			// this.add(newContentPane);
			this.pack();
			this.setVisible(true);
		} else if (e.getSource() == devicesDropDownCombo1) {
			Vector<Object> transacationsDropDown1 = newContentPane
					.loadTransacationsDropDown(devicesDropDownCombo1.getSelectedItem());

			//System.out.println("Time to show the difference" + transacationsDropDown1.size());

			//System.out.println("Time to show the difference" + tracesCombo1.getSelectedIndex());

			final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(transacationsDropDown1);

			tracesCombo1.setModel(model);

		} else if (e.getSource() == devicesDropDownCombo2) {
			Vector<Object> transacationsDropDown2 = newContentPane
					.loadTransacationsDropDown(devicesDropDownCombo2.getSelectedItem());

		//	System.out.println("Time to show the difference" + transacationsDropDown2.size());

		//	System.out.println("Time to show the difference" + tracesCombo2.getSelectedIndex());

			final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(transacationsDropDown2);

			tracesCombo2.setModel(model);

		}
	}

	// Returns the currentY position
	public int parseXML(String file1Path, String file2Path)
			throws SAXException, IOException, ParserConfigurationException {

		if (file1Path.length() == 0 || file2Path.length() == 0)
			throw new RuntimeException("The name of the XML file is required!");

		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxParserFactory.newSAXParser();
		SAXParserHandler handler = new SAXParserHandler();

		saxParser.parse(new File(file1Path), handler);
		transFile1 = handler.getStList();

		handler = new SAXParserHandler();

		saxParser.parse(new File(file2Path), handler);
		transFile2 = handler.getStList();
		//System.out.println("transFile1 :" + transFile1.size());
	//	System.out.println("transFile2 :" + transFile2.size());

		/*
		 * // Print all employees. for (SimulationTransaction st1 : transFile2) {
		 * System.out.println(st1.toString());
		 * 
		 * }
		 */

		// this.pack();
		// this.setVisible(true);

		return 1;

	}

	private void DrawSimulationResults(Vector<SimulationTransaction> transFile1,
			Vector<SimulationTransaction> transFile2) {

		if (panelAdded == true) {

			newContentPane = new JPanelCompareXmlGraph(transFile1, transFile2);
			newContentPane.setOpaque(true); // content panes must be opaque

			newContentPane.updateTable();
			// newContentPane.revalidate();
			// newContentPane.repaint();

			// this.revalidate();
			// this.repaint();

			// this.add(newContentPane);
		//	System.out.println(" rewrite table");

		} else {

			newContentPane = new JPanelCompareXmlGraph(transFile1, transFile2);
			newContentPane.setOpaque(true); // content panes must be opaque

			newContentPane.drawTable();

		//	System.out.println(" New table");
			this.add(newContentPane);

			DrawLatencyPanel();

			// this.setWidgetTopBottom(newContentPane, 32, Unit.PX, 0, Unit.PX);

			panelAdded = true;
		}

		this.pack();
		this.setVisible(true);
	}

	private void DrawLatencyPanel() {
		
		

		JPanel latencyPanel = new JPanel(new GridBagLayout()); // use FlowLayout
		GridBagConstraints c = new GridBagConstraints();
		latencyPanel.setBorder(new javax.swing.border.TitledBorder("Latency for Simulation Traces File"));
		c.fill = GridBagConstraints.NORTHWEST;

		JTextField file1 = new JTextField();

		
		Vector<Object> devicesDropDown1 = newContentPane.loadDevicesDropDown();

		devicesDropDownCombo1 = new JComboBox<Object>(devicesDropDown1);

		Vector<Object> transacationsDropDown1 = newContentPane
				.loadTransacationsDropDown(devicesDropDownCombo1.getSelectedItem());
		tracesCombo1 = new JComboBox<Object>(transacationsDropDown1);

		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.WEST;
		latencyPanel.add(devicesDropDownCombo1, c);

		// String[] choices2 = { "CHOICE 1","CHOICE 2", "CHOICE 3","CHOICE 4","CHOICE
		// 5","CHOICE 6"};

		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		latencyPanel.add(tracesCombo1, c);
		this.add(latencyPanel);

		devicesDropDownCombo1.addActionListener(this);

		Vector<Object> devicesDropDown2 = newContentPane.loadDevicesDropDown();

		devicesDropDownCombo2 = new JComboBox<Object>(devicesDropDown2);

		Vector<Object> transacationsDropDown2 = newContentPane
				.loadTransacationsDropDown(devicesDropDownCombo2.getSelectedItem());
		tracesCombo2 = new JComboBox<Object>(transacationsDropDown2);

		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 1;
		c.gridy = 0;
		// c.weightx = 1;
		// c.weighty = 1;
		latencyPanel.add(devicesDropDownCombo2, c);

		// String[] choices2 = { "CHOICE 1","CHOICE 2", "CHOICE 3","CHOICE 4","CHOICE
		// 5","CHOICE 6"};

		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 1;
		c.gridy = 1;
		// c.weightx = 0;
		// c.weighty =2;
		latencyPanel.add(tracesCombo2, c);
		this.add(latencyPanel);

		devicesDropDownCombo2.addActionListener(this);

		latencyDetails = new JButton("latency Details");
		latencyDetails.addActionListener(this);
		c.fill = GridBagConstraints.NORTHWEST;
		c.gridx = 1;
		c.gridy = 3;
		c.weightx = 1;
		c.weighty = 1;
		latencyPanel.add(latencyDetails, c);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		TraceManager.addDev("Windows closed!");
		close();

	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void close() {

		dispose();
		setVisible(false);

	}

}