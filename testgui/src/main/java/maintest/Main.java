package maintest;

import static javax.swing.SwingUtilities.invokeAndWait;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class Main extends JFrame {
  private static final long serialVersionUID = 1L;

  public Main() {

    JButton button1 = new JButton("Button1");
    button1.setBounds(50,50,100,50);

    JButton button2 = new JButton("Button2");
    button2.setBounds(50,100,100,50);

    JTextField text = new JTextField("Enter text here");
    text.setBounds(200,65,100,50);
    
    add(text);
    add(button1);
    add(button2);
  }

  public static void main(String[] args) throws InvocationTargetException, InterruptedException {
    invokeAndWait(new Runnable() {
      @Override
      public void run() {
        JFrame frame = new Main();
        frame.setSize(400, 400);
        frame.setLayout(null);
        frame.setVisible(true);
      }
    });
  }
}


