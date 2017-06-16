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




package web.crawler;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.ImageTerminal;
import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Tag;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static javax.imageio.ImageIO.write;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;


/**
   * Class Datavisualisation
   * Implement all the graphs that can be generated according to data in a database db,
   *  used in the constructor of the class
   * Creation: 2015
   * @version 2.0 24/03/2016
   * @author  Marie FORRAT, Angeliki AKTYPI, Ludovic APVRILLE
   * @see ui.MainGUI
 */
public class DataVisualisation {

    private web.crawler.DatabaseCreation database;

    /**
     *
     * @param db database you want to do the query on
     */
    public DataVisualisation(web.crawler.DatabaseCreation db) {
        this.database = db;
    }

    /**
     * Generate an histogram graph by searching the number of time the vulnerability is conerning a keyword(parameter) since 3 years ago
     * @param keyword the keyword you want to do statistic with
     * @throws IOException
     * @throws SQLException
     * @throws AWTException
     */
    public void Histogram(String keyword) throws IOException, SQLException, AWTException {
        ResultSet rs;
        String querySQL;
        /**
         * Do all the query necessary to have the data
         */



	keyword = keyword.toLowerCase().trim();

	if (keyword.length() ==0) {
	    throw new IOException("Empty keyword");
	}

	keyword = (keyword.split(" "))[0];
	

        String thisyear = new SimpleDateFormat("yyyy").format(new Date());
        String year1 = Integer.toString(Integer.valueOf(thisyear)-1);
        String year2 = Integer.toString(Integer.valueOf(thisyear)-2);
        String year3 = Integer.toString(Integer.valueOf(thisyear)-3);
        String year4 = Integer.toString(Integer.valueOf(thisyear)-4);
        querySQL = "SELECT * FROM SOFTWARES WHERE NAME  LIKE ? AND CVE_ID LIKE  ?";


        PreparedStatement prep = this.database.getconn().prepareStatement(querySQL);
        prep.setString(1,"%"+keyword+"%");
        prep.setString(2,"%"+year3+"%");
        rs = prep.executeQuery();

        int Records = 0;
        while (rs.next()) {
            Records++;
        }
        PreparedStatement prep1 = this.database.getconn().prepareStatement(querySQL);
        prep1.setString(1,"%"+keyword+"%");
        prep1.setString(2,"%"+year2+"%");
        rs = prep1.executeQuery();

        int Records1 = 0;
        while (rs.next()) {
            Records1++;
        }
        PreparedStatement prep2 = this.database.getconn().prepareStatement(querySQL);
        prep2.setString(1,"%"+keyword+"%");
        prep2.setString(2,"%"+year1+"%");
        rs = prep2.executeQuery();

        int Records2 = 0;
        while (rs.next()) {
            Records2++;
        }
        PreparedStatement prep3 = this.database.getconn().prepareStatement(querySQL);
        prep3.setString(1,"%"+keyword+"%");
        prep3.setString(2,"%"+thisyear+"%");
        rs = prep3.executeQuery();

        int Records3 = 0;
        while (rs.next()) {
            Records3++;
        }
        /**
         * Preparation to save the plot after its generation
         */
        ImageTerminal png = new ImageTerminal();
        File file = new File(Message.PIC_SRC_HIST);
        try {
            file.createNewFile();
            png.processOutput(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            System.err.print(ex);
        } catch (IOException ex) {
            System.err.print(ex);
        }
        /**
         * Generate the plot
         */
        JavaPlot p = new JavaPlot();
        p.setTerminal(png);
        int y4=Integer.parseInt(year4);
        int y3=Integer.parseInt(year3);
        int y2=Integer.parseInt(year2);
        int y1=Integer.parseInt(year1);
        int y0=Integer.parseInt(thisyear);
        int[][] pointpoint = {{y4, 0}, {y3, Records}, {y2, Records1}, {y1, Records2}, {y0, Records3}};
        PlotStyle myPlotStyle = new PlotStyle();
        myPlotStyle.setStyle(Style.HISTEPS);
        myPlotStyle.setLineType(3);
        DataSetPlot s = new DataSetPlot(pointpoint);
        myPlotStyle.setLineWidth(2);
        s.setPlotStyle(myPlotStyle);
        s.setTitle("Number of CVEs for "+keyword+" since 3 years ago");

        p.addPlot(s);
        p.getAxis("x").setBoundaries(2011, 2016);
        double[][] pointpoint1 = {{2011, 0}, {2012.5, Records}, {2013.5, Records1}, {2014.5, Records2}, {2015.5, Records3}};

        PlotStyle myPlotStyle1 = new PlotStyle();
        myPlotStyle1.setStyle(Style.IMPULSES);
        DataSetPlot s1 = new DataSetPlot(pointpoint1);
        myPlotStyle1.setLineType(3);
        myPlotStyle1.setLineWidth(2);
        s1.setPlotStyle(myPlotStyle1);
        s1.setTitle("");
        p.addPlot(s1);
        p.plot();

        /**
         * Save the plot
         */
        try {
            ImageIO.write(png.getImage(), "png", file);
        } catch (IOException ex) {
            System.err.print(ex);
        }

    }

    /**
     * Generate the opencloud graph, generate different size for keyword according to their importance in the database
     * @param argumentsfromclient list of keyword to use for generating the graph
     * @throws IOException
     * @throws SQLException
     * @throws AWTException
     */
    public void OpenCloud(String argumentsfromclient) throws IOException, SQLException, AWTException {

        ResultSet rs;
        String querySQL;
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        Cloud cloud = new Cloud();
        cloud.setMaxWeight(25.0);
        ArrayList<String> arguments= new ArrayList <String>(Arrays.asList(argumentsfromclient.toLowerCase().split(" ")));
        for (int i = 0; i < arguments.size(); i++) {
            querySQL = "SELECT * FROM SOFTWARES WHERE NAME LIKE ?";
            // rs = database.executestatement(querySQL);

            PreparedStatement prep = this.database.getconn().prepareStatement(querySQL);
            prep.setString(1,"%"+arguments.get(i)+"%");
            rs = prep.executeQuery();
	    //System.out.println("prep=" + prep);
            int Records = 0;
            while (rs.next()) {
                //System.out.println(Records1);
                Records++;
            }
            //System.out.println("arg=" + arguments.get(i) + " # of occurences=" + Records);
            cloud.addTag(new Tag(arguments.get(i), Records));
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
        write(bi, "png", new File(Message.PIC_SRC_STAT));
        frame.setDefaultCloseOperation(HIDE_ON_CLOSE);//TO not close the program while exiting the Jframe
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));// TO close the jframe after the screeshot
    }

    private BufferedImage getJframeScreenShot(JFrame panel) {
        BufferedImage bi = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        panel.paint(bi.getGraphics());
        return bi;
    }
}
