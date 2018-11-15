package gui.tests;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Main extends JFrame {
	private static final long serialVersionUID = 1L;

	  public Main() {
		  
		this.setSize(400, 400);
		
	    JButton button1 = new JButton("Button1");
	    button1.setName("Button1");
	    button1.setBounds(50,50,100,50);

	    JButton button2 = new JButton("ButtonRed");
	    button2.setName("ButtonRed");
	    button2.setBackground(Color.red);
	    button2.setForeground(Color.black);
	    button2.setVisible(true);
	    button2.setBounds(50,100,100,50);

	    JTextArea text = new JTextArea();
	    text.setName("TestArea");
	    text.setBounds(200,65,100,50);
	    
	    this.add(text);
	    this.add(button1);
	    this.add(button2);
	    
	    this.setLayout(null);
	    this.setVisible(true);
	  }

}
