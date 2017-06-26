import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;


public class MainPressureController extends JFrame implements Feeder, ChangeListener {

    static final int PRESSURE_MIN = 1;
    static final int PRESSURE_MAX = 30;
    static final int PRESSURE_INIT = 15;    //initial pressure

    static final String ALARM_ON = "ALARM ON";
    static final String ALARM_OFF = "alarm off";

    private PressureControllerPanel mp;
    private DatagramServer ds;

    private JSlider pressureValue;
    private JLabel alarm;

    public MainPressureController() {
        super("Pressure Controller demonstration");
        setSize(800, 600);
        setVisible(true);
        ds = new DatagramServer();
        ds.setFeeder(this);

        initComponents();
        ds.runServer();
    }

    public void initComponents() {
        setLayout(new BorderLayout());
	pressureValue = new JSlider(JSlider.HORIZONTAL, PRESSURE_MIN, PRESSURE_MAX, PRESSURE_INIT);
	pressureValue.addChangeListener(this);

	//Turn on labels at major tick marks.
	pressureValue.setMajorTickSpacing(5);
	pressureValue.setMinorTickSpacing(1);
	pressureValue.setPaintTicks(true);
	pressureValue.setPaintLabels(true);

	Font font = new Font("Serif", Font.ITALIC, 15);
	pressureValue.setFont(font);
	
	add(pressureValue, BorderLayout.NORTH);

	alarm = new JLabel(ALARM_OFF);
	add(alarm, BorderLayout.SOUTH);


        //mp = new PressureControllerPanel();
        //mp.addMouseListener(this);
	// mp.setPreferredSize(new Dimension(800,600));
        //add(mp, BorderLayout.CENTER);
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

    public void stateChanged(ChangeEvent e) {
    }

    /*public void mouseClicked(MouseEvent e){
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
    public void mouseReleased(MouseEvent e){}*/

    public static void main(String[] args) {
        MainPressureController mmw = new MainPressureController();
    }

}
