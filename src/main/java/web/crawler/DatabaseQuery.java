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

import javax.xml.transform.TransformerException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static web.crawler.FileManagement.StoreResultsInFile;

/**
 * Class DatabaseQuery Management of querys to a database Creation: 2015
 * 
 * @version 2.0 25/03/2016
 * @author Marie FORRAT, Angeliki AKTYPI, Ludovic APVRILLE
 * @see ui.MainGUI
 */
public class DatabaseQuery {

    private web.crawler.DatabaseCreation database;

    /**
     * Constructor of the class
     * 
     * @param db the database to do query on
     */
    public DatabaseQuery(web.crawler.DatabaseCreation db) {
        this.database = db;
    }

    public DatabaseCreation getDatabase() {
        return database;
    }

    /**
     * Make query on table software according to keyword from the console Be
     * careful: this function is NOT protected against SQL injection
     * 
     * @throws IOException          : I/O Exception
     * @throws SQLException         : An exception that provides information on a
     *                              database access error
     * @throws AWTException         : Signals that an Abstract Window Toolkit
     *                              exception has occurred
     * @throws TransformerException : Specifies an exceptional condition that
     *                              occurred during the transformation process.
     */
    public void MakeQueryOnTheDatabase() throws IOException, SQLException, AWTException, TransformerException {

        // open up standard input
        BufferedReader br;

        String query;
        String[] Keywords;
        String querySQL;
        ResultSet rs;

        /* Buffer */
        br = new BufferedReader(new InputStreamReader(System.in));
        // Asking the user to enter keywords
        System.out.print("\n\n\n");
        System.out.print("Insert your query. ");
        System.out.print("Keywords must be separated by spaces\n" + "To EXIT press <ENTER>");

        while (true) {

            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            /* User inputs the requested keywords */
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            System.out.print("\n\nInsert keywords: ");
            /* Read from console */
            query = br.readLine();
            if (query.equals("")) {
                break;
            }
            /* Define Keywords */
            Keywords = query.split(" ");
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            /* Construct query */
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            /* Select from the basic table */
            querySQL = "SELECT * FROM SOFTWARES ";
            /* Including the keywords in the query */
            querySQL += "WHERE NAME LIKE " + "'%" + Keywords[0] + "%'";
            for (int i = 1; i < Keywords.length; i++) {
                querySQL += " OR NAME LIKE " + "'%" + Keywords[i] + "%'";
            }
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            /* Print results in xml file */
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            rs = this.database.executestatement(querySQL);
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            StoreResultsInFile(rs);

            this.database.ClearOutputs();
        }
    }

    /**
     * make query on database according to 4 keywords: keyword to search in the
     * summary, years, keyword to search in the vulnerable system table, number of
     * result This function is protected against sql injection because it is using
     * prepared statement
     * 
     * @param argumentsfromclient keyword 1, date, keyword 2, number of result
     * @return xml file containing the result of the query
     * @throws IOException          : I/O Exception
     * @throws SQLException         : An exception that provides information on a
     *                              database access error
     * @throws AWTException         : Signals that an Abstract Window Toolkit
     *                              exception has occurred
     * @throws TransformerException : Specifies an exceptional condition that
     *                              occurred during the transformation process.
     */
    public File GetCVEwithKeywords(ArrayList<String> argumentsfromclient)
            throws IOException, SQLException, AWTException, TransformerException {
        int Records;
        List<ResultSet> resultSets = new ArrayList<>();
        ResultSet rs;
        ArrayList<String> query;
        String querySQL;
        String[] keywords;
        String year = "";
        String system;
        String score;
        String[] score_limits;

        query = argumentsfromclient;
        // for (int i=0; i<5; i++){ System.out.println(query.get(i)); }

        keywords = query.get(0).split("-");

        String thisyear = new SimpleDateFormat("yyyy").format(new Date());

        if (query.get(1).equals("this-year")) {
            year = thisyear;
        } else if (query.get(1).equals("last-year")) {
            year = Integer.toString(Integer.valueOf(thisyear) - 1);
        } else if (query.get(1).equals("all")) {
            year = "%";
        }

        if (query.get(2).equals("all")) {
            system = "%";
        } else {
            system = query.get(2);
        }

        if (query.get(3).equals("all")) {
            score = "0-10";
        } else {
            score = query.get(3);
        }

        score_limits = score.split("-");

        // for (int i=0; i<2; i++){
        // System.out.println(Integer.valueOf(score_limits[i])); }

        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        /* Construct query */
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

        /* Select columes from the tables */
        querySQL = "SELECT VULNERABILITIES.CVE_ID, VULNERABILITIES.SCORE, "
                + "SOFTWARES.\"NAME\", VULNERABILITIES.SUMMARY \n" + "FROM ROOT.SOFTWARES \n"
                + "\tINNER JOIN ROOT.VULNERABILITIES \n" + "\t\tON SOFTWARES.CVE_ID = VULNERABILITIES.CVE_ID \n";

        /* Including the arguments in the query */
        querySQL += "WHERE (VULNERABILITIES.SUMMARY LIKE ? ";
        for (int i = 1; i < keywords.length; i++) {
            querySQL += "OR VULNERABILITIES.SUMMARY LIKE ?";
        }
        querySQL += ") AND VULNERABILITIES.CVE_ID LIKE ? " + "AND SOFTWARES.\"NAME\" LIKE ? "
                + "AND VULNERABILITIES.SCORE BETWEEN ? AND ? \n" + "FETCH FIRST ? ROWS ONLY";

        // System.out.println(querySQL);

        PreparedStatement prep = this.database.getconn().prepareStatement(querySQL);

        int i;
        for (i = 0; i < keywords.length; i++) {
            prep.setString(i + 1, "%" + keywords[i] + "%");
        }
        prep.setString(i + 1, "%" + year + "%");
        prep.setString(i + 2, "%" + system + "%");
        prep.setInt(i + 3, Integer.valueOf(score_limits[0]));
        prep.setInt(i + 4, Integer.valueOf(score_limits[1]));
        prep.setInt(i + 5, Integer.valueOf(query.get(4)));

        // Execute the query
        rs = prep.executeQuery();

        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

        // Store the result in a xml file
        File xmlfile = StoreResultsInFile(rs);

        return xmlfile;
    }

    /**
     * Get all the information concerning one vulnerabilities, found by using its
     * CVE ID This function is protected against sql injection because it is using
     * prepared statement
     * 
     * @param argumentfromclient a cve ID
     * @return xml containing the result of the query
     * @throws IOException          : I/O Exception
     * @throws SQLException         : An exception that provides information on a
     *                              database access error
     * @throws AWTException         : Signals that an Abstract Window Toolkit
     *                              exception has occurred
     * @throws TransformerException : Specifies an exceptional condition that
     *                              occurred during the transformation process.
     */
    public File GetinfofromCVE(String argumentfromclient)
            throws IOException, SQLException, AWTException, TransformerException {
        int Records;
        BufferedReader br;
        ResultSet rs;
        String query;
        String querySQL;
        ArrayList<String> result = new ArrayList<>();
        /* Buffer */
        br = new BufferedReader(new InputStreamReader(System.in));

        query = argumentfromclient;
        /* Define Keywords */
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        /* Construct query */
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        /* Select from the basic table */
        querySQL = "SELECT ROOT.VULNERABILITIES.*, ROOT.REFERENCESS.REF_TYPE, "
                + "ROOT.REFERENCESS.\"SOURCE\", ROOT.REFERENCESS.\"LINK\", " + "ROOT.SOFTWARES.\"NAME\" \n"
                + "FROM ROOT.REFERENCESS \n" + "\tINNER JOIN ROOT.SOFTWARES \n"
                + "\t\tON REFERENCESS.CVE_ID = SOFTWARES.CVE_ID \n" + "\tINNER JOIN ROOT.VULNERABILITIES \n"
                + "\t\tON REFERENCESS.CVE_ID = VULNERABILITIES.CVE_ID \n";
        /* Including the keywords in the query */
        querySQL += "WHERE VULNERABILITIES.CVE_ID LIKE ?";

        PreparedStatement prep = this.database.getconn().prepareStatement(querySQL);

        prep.setString(1, "%" + query + "%");

        // Execute the query
        rs = prep.executeQuery();
        // Store the result in a xml file
        File xmlfile = StoreResultsInFile(rs);

        return xmlfile;

    }
}
