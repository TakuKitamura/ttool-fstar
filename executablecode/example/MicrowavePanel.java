import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;


public class MicrowavePanel extends JPanel  {

    private int duration = 0;
    private boolean magnetronON =false;
    private boolean start = false;
    private boolean doorOpened = false;
    private boolean cookingFinished = false;

    public MicrowavePanel() {
        setBackground(Color.white);
    }

    public void setCookingFinished(boolean finished) {
        cookingFinished = finished;
    }

    public void setDoorOpened(boolean opened) {
        doorOpened = opened;
    }

    public void setStart(boolean on) {
        start = on;
    }

    public void setMagnetronON(boolean isOn) {
        magnetronON = isOn;
    }

    public void setDuration(int _duration) {
        duration = _duration;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        //System.out.println("Salut .. repainting");

        // Foot

        g.setColor(Color.black);
        g.fillRect(100, 500, 100, 30);
        g.fillRect(600, 500, 100, 30);

        // Main oven
        g.setColor(Color.gray);
        g.fillRect(50, 50, 700, 450);
        g.setColor(Color.black);
        g.drawRect(50, 50, 700, 450);


        g.setColor(Color.white);
        g.fillRoundRect(75, 75, 500, 400, 20, 20);


        // Inside of the oven
        g.setColor(Color.black);
        g.fillRoundRect(100, 100, 450, 350, 20, 20);


        if (doorOpened) {
            // Inside the oven
            int dec = -5;
            g.setColor(Color.white);
            g.fillRoundRect(140, 140, 370, 270, 20, 20);
            g.setColor(Color.black);
            g.drawRect(140, 140, 370, 270);
            g.drawRect(200, 190, 250, 170);

            g.drawLine(140, 140, 200, 190);
            g.drawLine(510, 140, 450, 190);
            g.drawLine(510, 410, 450, 360);
            g.drawLine(140, 410, 200, 360);

            //Door
            g.setColor(Color.white);
            g.fillRect(95+dec, 110, 35, 330);
            g.setColor(Color.black);
            g.drawRect(95+dec, 110, 35, 330);
            g.fillRect(95+dec, 110, 10, 330);
            g.setColor(Color.gray);
            g.fillRect(65+dec, 100, 25, 350);
            g.fillRect(65+dec, 150, 35, 30);
            g.fillRect(65+dec, 370, 35, 30);
            g.setColor(Color.black);
            g.drawRect(65+dec, 100, 25, 350);




        } else {

        }

        if (magnetronON) {
            g.setColor(Color.red);
            g.fillRoundRect(150, 150, 350, 250, 20, 20);
        }

        if (doorOpened) {
            // Handle of the door

        } else {
            // Door closed
            g.setColor(Color.gray);
            g.fillRect(460, 100, 50, 350);
            g.setColor(Color.white);
            g.drawRect(460, 100, 50, 350);
        }

        // Panel to print duration & information
        g.setColor(Color.white);
        g.fillRect(625, 100, 100, 50);
        g.setColor(Color.black);
        g.fillRect(630, 105, 90, 40);

        g.setColor(Color.white);
        g.fillRect(625, 160, 100, 150);
        g.setColor(Color.black);
        g.fillRect(630, 165, 90, 140);

        // Panel for start button
        g.setColor(Color.white);
        g.fillRect(625, 330, 100, 40);
        g.setColor(Color.black);
        g.fillRect(630, 335, 90, 30);
        g.setColor(Color.white);
        g.drawString("START", 655, 355);
        g.setColor(Color.black);

        g.setColor(Color.green);
        //System.out.println("Duration=" + duration);
        Font fold = g.getFont();
        Font f = fold.deriveFont(30);
        g.setFont(f);

        g.drawString(""+duration, 690, 130);

        if (magnetronON) {
            g.drawString("Cooking", 650, 220);
        }

        if (start) {
            g.drawString("Start", 650, 190);
        }

        if (cookingFinished) {
            g.drawString("Finished", 650, 250);
        }

        if (doorOpened) {
            g.drawString("Door opened", 634, 280);
        }


    }


}
