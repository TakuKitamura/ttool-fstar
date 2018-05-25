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




package ui.window;

import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;


/**
 * Class JDialogCryptographicConfiguration
 * Dialog for creating cryptographic configuration for diplodocus security
 * Creation: 15/6/2016
 * @version 1.0 15/6/2016
 * @author Letitia LI
 */
public class JDialogCryptographicConfiguration extends JDialogBase implements ActionListener  {

	private String [] values;

	private int nbString;

	private boolean set = false;

	private JPanel panel1;

	private List<securityAlgorithm> secAlgs;
	private Vector<String> algNames;
	// Panel1
	private JTextField [] texts;
	private JButton inserts[];
	private HashMap<Integer, JComboBox<String>> helps;

	// Main Panel
	private String[] nonces;
	private String[] keys;

	GridBagConstraints c0 = new GridBagConstraints();
	Container c;
	/** Creates new form  */
	// arrayDelay: [0] -> minDelay ; [1] -> maxDelay
	public JDialogCryptographicConfiguration(Frame f, String title, String[] _values, String[] _nonces, String[] _keys) {

		super(f, title, true);

		nbString = 10;

		values=_values;
		nonces=_nonces;
		keys=_keys;
		texts = new JTextField[nbString];
		secAlgs= new ArrayList<securityAlgorithm>();
		algNames=new Vector<String>();
		initComponents();
		myInitComponents();
		pack();
	}


	private void myInitComponents() {
	}

	private void initComponents() {
		//These values are normalized to AES 128 bit as 100
		//Based off of https://joneaves.wordpress.com/2004/04/18/ecc_and_rsa_speed_comparison/
		//https://www.cryptopp.com/benchmarks.html
		//http://www.cs.wustl.edu/~jain/cse567-06/ftp/encryption_perf/index.html
		//https://automationrhapsody.com/md5-sha-1-sha-256-sha-512-speed-performance/
		//Add list of sample security algorithms
		secAlgs.add(new securityAlgorithm("AES", "0","100","100","128","Symmetric Encryption"));  
		secAlgs.add(new securityAlgorithm("Triple-DES", "0","200","200","128","Symmetric Encryption"));
		
		secAlgs.add(new securityAlgorithm("RSA", "0","250","150","128","Asymmetric Encryption"));    
		secAlgs.add(new securityAlgorithm("ECC", "0","315","310","128","Asymmetric Encryption"));    
		
		secAlgs.add(new securityAlgorithm("SHA-256", "0","370","370","128","Hash"));    
		secAlgs.add(new securityAlgorithm("Whirlpool", "0","550","550","128","Hash"));  
		
		secAlgs.add(new securityAlgorithm("Poly-1305", "0","400","400","128","MAC"));    
		secAlgs.add(new securityAlgorithm("HMAC", "0","800","800","128","MAC")); 
		
		for (securityAlgorithm secAlg: secAlgs){
			algNames.add(secAlg.name);
		}
		inserts = new JButton[nbString];
		helps = new HashMap<>();

		c = getContentPane();
		//GridBagLayout gridbag0 = new GridBagLayout();



		setFont(new Font("Helvetica", Font.PLAIN, 14));
		c.setLayout(new BorderLayout());

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		if (values[1].contains("Encryption") || values[1].equals("MAC") || values[1].isEmpty()){
			panel1= new EncryptPanel(this);
		}
		else {
			panel1=new funcPanel(this);
		}
		/*c0.gridwidth = 1;
		c0.gridheight = 10;
		c0.weighty = 1.0;
		c0.weightx = 1.0;
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		c0.fill = GridBagConstraints.BOTH;
		c.add(panel1, c0);*/
		c.add(panel1, BorderLayout.CENTER);

		// main panel;
		/*c0.gridwidth = 1;
		c0.gridheight = 1;
		c0.fill = GridBagConstraints.HORIZONTAL;
		initButtons(c0, c, this);*/
		initButtons(c, this);
        JPanel panelButton = initBasicButtons(this);
        c.add(panelButton, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent evt)  {
		String command = evt.getActionCommand();

		// Compare the action command to the known actions.
		if (command.equals("Save and Close"))  {
			closeDialog();
		} else if (command.equals("Cancel")) {
			cancelDialog();
		} else if (inserts[0] != null) {
			if (evt.getSource() == inserts[0]) {
				texts[1].setText(helps.get(1).getSelectedItem().toString());
				boolean repanel = false;
				if (helps.get(1).getSelectedIndex()==5 && !(panel1 instanceof advPanel)){
					values[1]=helps.get(1).getSelectedItem().toString();
					values[0]=texts[0].getText();
					values[3]=texts[3].getText();
					values[2]=texts[3].getText();
					values[5]="";
					values[4]="";
					values[9]=texts[9].getText();
					repanel=true;
					c.removeAll();
					panel1= new advPanel(this);
				}
				else if ((helps.get(1).getSelectedIndex() >2) && !(panel1 instanceof funcPanel)){
					values[1]=helps.get(1).getSelectedItem().toString();
					values[0]=texts[0].getText();
					values[3]=texts[3].getText();
					values[2]="";
					values[5]="";
					values[6]="";
					values[9]=texts[9].getText();
					repanel=true;
					c.removeAll();
					panel1= new funcPanel(this);
				}
				else if ((helps.get(1).getSelectedIndex() <3) && !(panel1 instanceof EncryptPanel)){
					values[1]=helps.get(1).getSelectedItem().toString();
					values[0]=texts[0].getText();
					values[3]=texts[3].getText();
					values[4]="";
					values[6]="";
					values[9]="";
					c.removeAll();
					repanel=true;
					panel1= new EncryptPanel(this);

				}

				if (repanel){
					c0.gridwidth = 1;
					c0.gridheight = 10;
					c0.weighty = 1.0;
					c0.weightx = 1.0;
					c0.gridwidth = GridBagConstraints.REMAINDER; //end row

					c.add(panel1,c0);
					// main panel;
					c0.gridwidth = 1;
					c0.gridheight = 1;
					c0.fill = GridBagConstraints.HORIZONTAL;
					closeButton = new JButton("Save and Close", IconManager.imgic25);
					//closeButton.setPreferredSize(new Dimension(600, 50));
					closeButton.addActionListener(this);
					c.add(closeButton, c0);
					c0.gridwidth = GridBagConstraints.REMAINDER; //end row
					cancelButton = new JButton("Cancel", IconManager.imgic27);
					cancelButton.addActionListener(this);
					c.add(cancelButton, c0);
					this.invalidate();	   
					this.validate();
					this.repaint();
				}
			}
			if (evt.getSource() == inserts[5]) {
				if (helps.get(5).getSelectedItem()!=null){
					texts[5].setText(helps.get(5).getSelectedItem().toString());
				}
			}
			if (evt.getSource() == inserts[8]) {
				if (helps.get(8).getSelectedItem()!=null){
					texts[8].setText(helps.get(8).getSelectedItem().toString());
				}
			}
			//Using preset algorithm
			if (evt.getSource() == inserts[9]) {
				if (helps.get(9).getSelectedItem()!=null){
					texts[9].setText(helps.get(9).getSelectedItem().toString());
					for (securityAlgorithm secAlg: secAlgs){
						if (secAlg.name.equals(texts[9].getText())){
							//Set algorithm times + overhead
							texts[1].setText(secAlg.type);
							texts[2].setText(secAlg.overhead);
							texts[3].setText(secAlg.encryptCC);
							texts[7].setText(secAlg.decryptCC);
						}	
					}
				}
			}
		}
	}




	public class EncryptPanel extends JPanel {
        private void addEmptyLine(GridBagConstraints gc) {
            gc.weighty = 1.0;
            gc.weightx = 1.0;
            gc.gridwidth = GridBagConstraints.REMAINDER; //end row
            gc.fill = GridBagConstraints.BOTH;
            gc.gridheight = 1;
            add(new JLabel(" "), gc);
        }

		EncryptPanel(JDialogCryptographicConfiguration j){
			GridBagConstraints c1 = new GridBagConstraints();
			GridBagLayout gridbag1 = new GridBagLayout();

			this.setLayout(gridbag1);

			this.setBorder(new javax.swing.border.TitledBorder("Properties"));

			this.setPreferredSize(new Dimension(600, 200));


			c1.gridwidth = 1;
			c1.gridheight = 1;
			c1.weighty = 1.0;
			c1.weightx = 1.0;
			c1.fill = GridBagConstraints.HORIZONTAL;
			c1.anchor = GridBagConstraints.CENTER;
			String[] vals = new String[]{"Symmetric Encryption", "Asymmetric Encryption","MAC", "Hash", "Nonce", "Advanced"}; 
			// String1
			c1.gridwidth = 1;
			add(new JLabel("Cryptographic Configuration Name"),c1);

			addEmptyLine(c1);

			texts[0]=new JTextField(values[0],15);
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			add(texts[0],c1);
			c1.gridwidth=1;
			add(new JLabel("Security Pattern"), c1);
			helps.put(1, new JComboBox<>(vals));
			helps.get(1).setSelectedItem(values[1]);
			add(helps.get(1),c1);
			c1.gridwidth=GridBagConstraints.REMAINDER;
			inserts[0] = new JButton("Use");
			inserts[0].addActionListener(j);
			add(inserts[0], c1);
			texts[1]=new JTextField(values[1], 15);
			add(texts[1], c1);

			addEmptyLine(c1);

			c1.gridwidth = 1;
			add(new JLabel("Overhead"),c1);
			texts[2]=new JTextField(values[2],15);
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			add(texts[2],c1);

			c1.gridwidth = 1;
			add(new JLabel("Encryption Computational Complexity"),c1);
			texts[3]=new JTextField(values[3],15);
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			add(texts[3],c1);

			c1.gridwidth = 1;
			add(new JLabel("Decryption Computational Complexity"),c1);
			texts[7]=new JTextField(values[7],15);
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			add(texts[7],c1);

            addEmptyLine(c1);

			c1.gridwidth = 1;
			add(new JLabel("Nonce"),c1);
			helps.put(5, new JComboBox<>(nonces));
			if (helps.get(5).getItemCount() > 0){
				helps.get(5).setSelectedItem(values[5]);
			}
			add(helps.get(5),c1);
			c1.gridwidth=GridBagConstraints.REMAINDER;
			inserts[5] = new JButton("Use");
			inserts[5].addActionListener(j);
			add(inserts[5], c1);
			texts[5]=new JTextField(values[5], 15);
			add(texts[5], c1);

            addEmptyLine(c1);

			c1.gridwidth = 1;
			add(new JLabel("Encrypted Key"),c1);
			helps.put(8, new JComboBox<>(keys));
			if (helps.get(8).getItemCount() > 0){
				helps.get(8).setSelectedItem(values[8]);
			}
			add(helps.get(8),c1);
			c1.gridwidth=GridBagConstraints.REMAINDER;
			inserts[8] = new JButton("Use");
			inserts[8].addActionListener(j);
			add(inserts[8], c1);
			texts[8]=new JTextField(values[8], 15);
			add(texts[8], c1);

			c1.gridwidth = 1;
			add(new JLabel("Algorithm"),c1);
			helps.put(9, new JComboBox<>(algNames));
			helps.get(9).setSelectedItem(values[9]);
			add(helps.get(9),c1);
			c1.gridwidth=GridBagConstraints.REMAINDER;
			inserts[9] = new JButton("Use");
			inserts[9].addActionListener(j);
			add(inserts[9], c1);
			texts[9]=new JTextField(values[9],15);
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			add(texts[9],c1);

		}



	}

	public class funcPanel extends JPanel {
		funcPanel(JDialogCryptographicConfiguration j){
			GridBagConstraints c1 = new GridBagConstraints();
			GridBagLayout gridbag1 = new GridBagLayout();

			setLayout(gridbag1);

			setBorder(new javax.swing.border.TitledBorder("Properties"));

			setPreferredSize(new Dimension(600, 200));

			c1.weighty = 1.0;
			c1.weightx = 1.0;
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			c1.fill = GridBagConstraints.BOTH;
			c1.gridheight = 1;
			add(new JLabel(" "), c1);

			// second line panel1
			c1.gridwidth = 1;
			c1.gridheight = 1;
			c1.weighty = 1.0;
			c1.weightx = 1.0;
			c1.anchor = GridBagConstraints.CENTER;
			c1.fill = GridBagConstraints.HORIZONTAL;
			c1.anchor = GridBagConstraints.CENTER;
			String[] vals = new String[]{"Symmetric Encryption", "Asymmetric Encryption","MAC", "Hash", "Nonce", "Advanced"}; 
			// String1
			c1.gridwidth = 1;
			add(new JLabel("Cryptographic Configuration Name"),c1);
			texts[0]=new JTextField(values[0],15);
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			add(texts[0],c1);

			c1.gridwidth=1;
			add(new JLabel("Security Pattern"), c1);
			helps.put(1, new JComboBox<>(vals));
			helps.get(1).setSelectedItem(values[1]);
			add(helps.get(1),c1);

			c1.gridwidth=GridBagConstraints.REMAINDER;
			inserts[0] = new JButton("Use");
			inserts[0].addActionListener(j);
			add(inserts[0], c1);
			texts[1]=new JTextField(values[1], 15);
			add(texts[1], c1);

			c1.gridwidth = 1;
			add(new JLabel("Computational Complexity"),c1);
			texts[3]=new JTextField(values[3],15);
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			add(texts[3],c1);

			c1.gridwidth = 1;
			add(new JLabel("Size"),c1);
			texts[4]=new JTextField(values[4],15);
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			add(texts[4],c1);

			c1.gridwidth = 1;
			add(new JLabel("Algorithm"),c1);
			helps.put(9, new JComboBox<>(algNames));
			helps.get(9).setSelectedItem(values[9]);
			add(helps.get(9),c1);
			c1.gridwidth=GridBagConstraints.REMAINDER;
			inserts[9] = new JButton("Use");
			inserts[9].addActionListener(j);
			add(inserts[9], c1);
			texts[9]=new JTextField(values[9],15);
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			add(texts[9],c1);
		}
	}


	public class advPanel extends JPanel {
		advPanel(JDialogCryptographicConfiguration j){
			GridBagConstraints c1 = new GridBagConstraints();
			GridBagLayout gridbag1 = new GridBagLayout();

			setLayout(gridbag1);

			setBorder(new javax.swing.border.TitledBorder("Properties"));

			setPreferredSize(new Dimension(600, 200));

			c1.weighty = 1.0;
			c1.weightx = 1.0;
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			c1.fill = GridBagConstraints.BOTH;
			c1.gridheight = 1;
			add(new JLabel(" "), c1);

			// second line panel1
			c1.gridwidth = 1;
			c1.gridheight = 1;
			c1.weighty = 1.0;
			c1.weightx = 1.0;
			c1.anchor = GridBagConstraints.CENTER;
			c1.fill = GridBagConstraints.HORIZONTAL;
			c1.anchor = GridBagConstraints.CENTER;
			String[] vals = new String[]{"Symmetric Encryption", "Asymmetric Encryption","MAC", "Hash", "Nonce", "Advanced"}; 

			// String1
			c1.gridwidth = 1;
			add(new JLabel("Cryptographic Configuration Name"),c1);
			texts[0]=new JTextField(values[0],15);
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			add(texts[0],c1);

			c1.gridwidth=1;
			add(new JLabel("Security Pattern"), c1);
			helps.put(1, new JComboBox<>(vals));
			helps.get(1).setSelectedItem(values[1]);
			add(helps.get(1),c1);
			c1.gridwidth=GridBagConstraints.REMAINDER;
			inserts[0] = new JButton("Use");
			inserts[0].addActionListener(j);
			add(inserts[0], c1);
			texts[1]=new JTextField(values[1], 15);
			add(texts[1], c1);

			c1.gridwidth = 1;
			add(new JLabel("Overhead"),c1);
			texts[2]=new JTextField(values[2],15);
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			add(texts[2],c1);


			c1.gridwidth = 1;
			add(new JLabel("Computational Complexity"),c1);
			texts[3]=new JTextField(values[3],15);
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			add(texts[3],c1);



			c1.gridwidth=1;
			add(new JLabel("Custom Security Formula"), c1);
			c1.gridwidth = GridBagConstraints.REMAINDER; //end row
			texts[6] = new JTextField(values[6], 15);
			add(texts[6],c1);        
			
		}
	}

	public void closeDialog() {
		set = true;
		dispose();
	}

	public String getString(int i) {
		if (texts[i]!=null){
			return texts[i].getText();
		}
		return "";
	}

	public boolean hasValidString(int i) {
		return texts[i] != null && texts[i].getText().length() > 0;
	}


	public boolean hasBeenSet() {
		return set;
	}

	public void cancelDialog() {
		dispose();
	}

	public class securityAlgorithm {
		String name;
		String overhead;
		String encryptCC;
		String decryptCC;
		String keysize;
		String type;
		public securityAlgorithm(String name, String overhead, String encryptCC, String decryptCC, String keysize, String type){
			this.name=name;
			this.overhead=overhead;
			this.encryptCC = encryptCC;
			this.decryptCC= decryptCC;
			this.keysize= keysize;
			this.type=type;
		}
	}


}
