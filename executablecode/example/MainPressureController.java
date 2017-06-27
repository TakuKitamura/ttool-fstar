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

    private DatagramServer ds;

    private JSlider pressureValue;
    private JLabel alarm;

    public MainPressureController() {
        super("Pressure Controller demonstration");
        setSize(400, 150);
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
	alarm.setHorizontalAlignment(JLabel.CENTER);
	alarm.setVerticalAlignment(JLabel.CENTER);
	add(alarm, BorderLayout.CENTER);


        //mp = new PressureControllerPanel();
        //mp.addMouseListener(this);
	// mp.setPreferredSize(new Dimension(800,600));
        //add(mp, BorderLayout.CENTER);
        //mp.revalidate();
    }

    public void setMessage(String msg) {
 
        int index;
        String s;
        int duration;
        System.out.println("Got message:" + msg);
        try {
            if (msg.startsWith("+")) {
		alarm.setText(ALARM_ON);
            } else if (msg.startsWith("-")) {
                alarm.setText(ALARM_OFF);
            } 
        } catch (Exception e) {
            System.out.println("Exception when computing message: " + e.getMessage());
        }

    }

    public void stateChanged(ChangeEvent e) {
	System.out.println("Value of pressure changed:" + pressureValue.getValue());
	if (ds != null) {
	    ds.sendDatagramTo("PRESSURE=" + pressureValue.getValue());
	}
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
