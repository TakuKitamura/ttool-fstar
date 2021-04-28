import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;


public class MainMicrowave extends JFrame implements Feeder, MouseListener {

    private MicrowavePanel mp;
    private DatagramServer ds;

    public MainMicrowave() {
        super("Microwave demonstration");
        setSize(800, 600);
        setVisible(true);
        ds = new DatagramServer();
        ds.setFeeder(this);

        initComponents();
        ds.runServer();
    }

    public void initComponents() {
        setLayout(new BorderLayout());
        mp = new MicrowavePanel();
        mp.addMouseListener(this);
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

    public void mouseClicked(MouseEvent e){
        int x = e.getX();
        int y = e.getY();

        System.out.println("Mouse clicked!!!");

        // START?
        if ((x>630)&&(x<720)&&(y>335)&&(y<365)) {
            System.out.println("Mouse clicked on start");
            if (ds != null) {
                ds.sendDatagramTo("START");
            }
            System.out.println("Action on start sent");
        }

    }
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}

    public static void main(String[] args) {
        MainMicrowave mmw = new MainMicrowave();
    }

}
