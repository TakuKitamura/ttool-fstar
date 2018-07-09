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


package myutil;

import ui.util.DefaultText;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

//import java.awt.*;

/**
 * Class HTMLCodeGeneratorForTables
 * HTML code generator for AbstractTableModel
 * Creation: 20/02/2009
 *
 * @author Ludovic APVRILLE
 * @version 1.0 20/02/2009
 */
public class HTMLCodeGeneratorForTables {

    public HTMLCodeGeneratorForTables() {
    }

    public StringBuffer getHTMLCode(ArrayList<AbstractTableModel> atms, ArrayList<String> titles, String mainTitle) {
        StringBuffer sb = new StringBuffer();
        int max = Math.min(atms.size(), titles.size());


        sb.append("<html>\n");
        sb.append(getHTMLHeader(mainTitle));

        sb.append("<body>\n");
        sb.append("<center><h1>" + mainTitle + "</h1></center>\n");

        for (int i = 0; i < max; i++) {
            sb.append(getHTMLCode(atms.get(i), titles.get(i)));
        }

        sb.append("</body>\n</html>");

        return sb;
    }


    public StringBuffer getHTMLCode(AbstractTableModel atm, String title) {
        int i, j;
        String s;

        StringBuffer sb = new StringBuffer("\n<BR><BR>\n<TABLE BORDER>\n");
        sb.append("<CAPTION><B>" + title + "</B></CAPTION>\n");

        // Top part of table
        sb.append("<TR>");
        for (j = 0; j < atm.getColumnCount(); j++) {
            sb.append("<TH BGCOLOR=\"#C0C0C0\"> " + atm.getColumnName(j) + " </TH> ");
        }
        sb.append("</TR>\n");

        // Table itself
        for (i = 0; i < atm.getRowCount(); i++) {
            sb.append("<TR>");
            for (j = 0; j < atm.getColumnCount(); j++) {
                s = atm.getValueAt(i, j).toString();
                if (s.length() == 0) {
                    s = "-";
                }
                sb.append("<TD> " + s + " </TD> ");
            }
            sb.append("</TR>\n");
        }

        // End of table
        sb.append("</TABLE>\n\n");

        return sb;

    }

    public StringBuffer getHTMLCodeFromSorters(List<TableSorter> tss, List<String> titles, String mainTitle) {
        StringBuffer sb = new StringBuffer();
        int max = Math.min(tss.size(), titles.size());


        sb.append("<html>\n");
        sb.append(getHTMLHeader(mainTitle));

        sb.append("<body>\n");
        sb.append("<center><h1>" + mainTitle + "</h1></center>\n");

        for (int i = 0; i < max; i++) {
            sb.append(getHTMLCodeFromSorter(tss.get(i), titles.get(i)));
        }

        sb.append("</body>\n</html>");

        return sb;
    }


    public StringBuffer getHTMLCodeFromSorter(TableSorter ts, String title) {
        int i, j;
        String s;

        StringBuffer sb = new StringBuffer("\n<BR><BR>\n<TABLE BORDER>\n");
        sb.append("<CAPTION><B>" + title + "</B></CAPTION>\n");

        // Top part of table
        sb.append("<TR>");
        for (j = 0; j < ts.getColumnCount(); j++) {
            sb.append("<TH BGCOLOR=\"#C0C0C0\"> " + ts.getColumnName(j) + " </TH> ");
        }
        sb.append("</TR>\n");

        // Table itself
        for (i = 0; i < ts.getRowCount(); i++) {
            sb.append("<TR>");
            for (j = 0; j < ts.getColumnCount(); j++) {
                s = ts.getValueAt(i, j).toString();
                if (s.length() == 0) {
                    s = "-";
                }
                sb.append("<TD> " + s + " </TD> ");
            }
            sb.append("</TR>\n");
        }

        // End of table
        sb.append("</TABLE>\n\n");

        return sb;

    }

    public String getHTMLHeader(String mainTitle) {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String formattedDate = formatter.format(date);

        String tmpdoc = "";
        tmpdoc += "<!----- Automatically generated by TTool version ";
        tmpdoc += DefaultText.getVersion();
        tmpdoc += " generation date: " + formattedDate;
        tmpdoc += "---->\n";
        tmpdoc += "\n<head>\n<title>";
        tmpdoc += mainTitle;
        tmpdoc += "</title>\n</head>\n";
        return tmpdoc;
    }

}