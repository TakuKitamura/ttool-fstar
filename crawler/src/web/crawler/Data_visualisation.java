/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.crawler;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import myutil.externalSearch.Message;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Tag;
import javax.imageio.ImageIO;
import static javax.imageio.ImageIO.write;

public class Data_visualisation {

    private web.crawler.Database_creation database;
    
    public Data_visualisation(web.crawler.Database_creation db) {
        this.database = db;
    }
    
    public void Histogram() throws IOException, SQLException, AWTException {
        ResultSet rs;
        String querySQL;
        
        querySQL = "SELECT * FROM VULNERABILITIES WHERE ACCESS_COMPLEXITY  LIKE '%LOW%' ";
        rs = database.executestatement(querySQL);
        int Records = 0;
        while (rs.next()) {
            //System.out.println(Records);
            Records++;
        }
        
        querySQL = "SELECT * FROM VULNERABILITIES WHERE ACCESS_COMPLEXITY  LIKE '%MEDIUM%' ";
        rs = database.executestatement(querySQL);
        int Records1 = 0;
        while (rs.next()) {
            // System.out.println(Records1);
            Records1++;
        }
        querySQL = "SELECT * FROM VULNERABILITIES WHERE ACCESS_COMPLEXITY  LIKE '%HIGH%' ";
        rs = database.executestatement(querySQL);
        int Records2 = 0;
        while (rs.next()) {
            // System.out.println(Records2);
            Records2++;
        }
        
        JavaPlot p = new JavaPlot();
        int[][] pointpoint = {{0, 0}, {1, Records}, {2, Records1}, {3, Records2}, {4, 0}};
        PlotStyle myPlotStyle = new PlotStyle();
        myPlotStyle.setStyle(Style.FSTEPS);
        DataSetPlot s = new DataSetPlot(pointpoint);
        myPlotStyle.setLineWidth(1);
        s.setPlotStyle(myPlotStyle);
        s.setTitle("Complexity of the vulnerabilities, 1 is low 2 is medium and 3 is high");
        
        p.addPlot(s);
        
        PlotStyle myPlotStyle1 = new PlotStyle();
        myPlotStyle1.setStyle(Style.IMPULSES);
        DataSetPlot s1 = new DataSetPlot(pointpoint);
        myPlotStyle.setLineWidth(1);
        s1.setPlotStyle(myPlotStyle1);
        p.addPlot(s1);
        p.plot();
    }
    
    public void OpenCloud(ArrayList<String> argumentsfromclient) throws IOException, SQLException, AWTException {
        
        ResultSet rs;
        String querySQL;
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        Cloud cloud = new Cloud();
        cloud.setMaxWeight(25.0);
        
        for (int i = 0; i < argumentsfromclient.size(); i++) {
            querySQL = "SELECT * FROM SOFTWARES WHERE NAME  LIKE '%" + argumentsfromclient.get(i) + "%' ";
            rs = database.executestatement(querySQL);
            int Records = 0;
            while (rs.next()) {
                // System.out.println(Records1);
                Records++;
            }
            //System.out.println(argumentsfromclient.get(i) + Records);
            cloud.addTag(new Tag(argumentsfromclient.get(i), Records));
        }
        
        for (Tag tag : cloud.tags()) {
            final JLabel label = new JLabel(tag.getName());
            label.setOpaque(false);
            label.setBorder(new EmptyBorder(0, 20, 0, 0));
            label.setFont(label.getFont().deriveFont((float) tag.getWeight() * 10));
            panel.add(label);
        }
        frame.pack();
        frame.add(panel);
        frame.setSize(1000, 800);
        frame.setVisible(true);
        BufferedImage bi = getJframeScreenShot(frame);
        write(bi, "png", new File(Message.PIC_SRC));
    }
    
    private BufferedImage getJframeScreenShot(JFrame panel) {
        BufferedImage bi = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        panel.paint(bi.getGraphics());
        return bi;
    }
}
