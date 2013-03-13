import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

 
public class MainMicrowave extends JFrame implements Feeder {
	
    private MicrowavePanel mp;
    
    public MainMicrowave() {
    	super("Microwave demonstration");
    	setSize(800, 600);
    	setVisible(true);
    	DatagramServer ds = new DatagramServer();
    	ds.setFeeder(this);
    	
    	initComponents();
    	ds.runServer();
    }
    
    public void initComponents() {
    	setLayout(new BorderLayout());
    	mp = new MicrowavePanel();
    	mp.setPreferredSize(new Dimension(800,600));
    	add(mp, BorderLayout.CENTER);
    	mp.revalidate();
    }
    
    public void setMessage(String msg) {
    	if (mp == null) {
    		return;
    	}
    	
    	int index;
    	String s;
    	int duration;
    	System.out.println("Got message:" + msg);
    	try {
			if (msg.startsWith("Duration ")) {
				s = msg.substring(9, msg.length());
				
				duration = Integer.decode(s.trim()).intValue();
				mp.setDuration(duration);
				mp.setStart(false);
				//System.out.println("Setting new duration :" + duration);
				mp.setCookingFinished(false);
			} else if (msg.startsWith("Start ")) {
				s = msg.substring(6, msg.length());
				
				duration = Integer.decode(s.trim()).intValue();
				mp.setDuration(duration);
				mp.setStart(true);
				mp.setCookingFinished(false);
				//System.out.println("Setting new duration (start): " + duration);
			} else if (msg.startsWith("Magnetron_ON")) {
				mp.setMagnetronON(true);
				mp.setCookingFinished(false);
			} else if (msg.startsWith("Magnetron_OFF")) {
				mp.setMagnetronON(false);
				mp.setCookingFinished(false);
			} else if (msg.startsWith("Open Door")) {
				mp.setDoorOpened(true);
				mp.setCookingFinished(false);
			} else if (msg.startsWith("Close Door")) {
				mp.setDoorOpened(false);
				mp.setCookingFinished(false);
			} else if (msg.startsWith("Dring")) {
				mp.setCookingFinished(true);
			}
    	} catch (Exception e) {
    		System.out.println("Exception when computing message: " + e.getMessage());
    	}
    	
    	mp.repaint();
    }

    	
    public static void main(String[] args) {
    	MainMicrowave mmw = new MainMicrowave();
	} 
	
	
	
	
    
}