/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.crawler;

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.xml.transform.TransformerException;
import static web.crawler.File_management.StoreResultsInFile;


public class Database_query {
    
private web.crawler.Database_creation database;
    
    public Database_query(web.crawler.Database_creation db) {
        this.database = db;
    }

    public void MakeQueryOnTheDatabase() throws IOException, SQLException, AWTException, TransformerException {

        //  open up standard input                
        BufferedReader br;

        String query;
        String[] Keywords;
        String querySQL;
        ResultSet rs;

        /* Buffer */
        br = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("\n\n\n");
        System.out.print("Insert your query. ");
        System.out.print("Keywords must be separated by spaces\n"
                + "To EXIT press <ENTER>");

        while (true) {

            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            /*             User inputs the requested keywords                */
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            System.out.print("\n\nInsert keywords: ");
            /* Read from console                                             */
            query = br.readLine();
            if (query.equals("")) {
                break;
            }
            /* Define Keywords */
            Keywords = query.split(" ");
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            /*                      Construct query                          */
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            /* Select from the basic table                                   */
            querySQL = "SELECT * FROM SOFTWARES ";
            /*  Including the keywords in the query                          */
            querySQL += "WHERE NAME LIKE " + "'%" + Keywords[0] + "%'";
            for (int i = 1; i < Keywords.length; i++) {
                querySQL += " OR NAME LIKE " + "'%" + Keywords[i] + "%'";
            }
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            /*                      Clear console                            */
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            /*                Print results in xml file                      */
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            rs = this.database.executestatement(querySQL);
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            StoreResultsInFile(rs);

            this.database.ClearOutputs();
        }
    }

    public File GetCVEwithKeywords(ArrayList<String> argumentsfromclient) throws IOException, SQLException, AWTException, TransformerException {
        int Records;
        ResultSet rs;
        ArrayList<String> query;
        String querySQL;
        
        query = argumentsfromclient;

        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        /*                      Construct query                          */
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        /* Select columes from the tables                                */
        querySQL = "SELECT SOFTWARES.CVE_ID, SOFTWARES.\"NAME\", VULNERABILITIES.SUMMARY \n"
                + "FROM ROOT.SOFTWARES \n\tINNER JOIN ROOT.VULNERABILITIES \n"
                + "\t\tON SOFTWARES.CVE_ID = VULNERABILITIES.CVE_ID \n";
        /*  Including the arguments in the query                         */
        querySQL += "WHERE VULNERABILITIES.SUMMARY LIKE ? "
                + "AND SOFTWARES.CVE_ID LIKE ? "
                + "AND SOFTWARES.\"NAME\" LIKE ? \n"
                + "FETCH FIRST ? ROWS ONLY";

        System.out.println(querySQL);
        
        PreparedStatement prep = this.database.getconn().prepareStatement(querySQL);
        
        prep.setString(1, "%"+query.get(0)+"%"); 
        prep.setString(2, "%"+query.get(1)+"%"); 
        prep.setString(3, "%"+query.get(2)+"%"); 
        prep.setInt(4,Integer.valueOf(query.get(3))); 
        
        rs = prep.executeQuery();
        
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

        //String xmlcontent = StoreResultsInFile(rs);
        File xmlfile = StoreResultsInFile(rs);

        return xmlfile;
    }

    public File GetinfofromCVE(String argumentfromclient) throws IOException, SQLException, AWTException, TransformerException {
        int Records;
        //  open up standard input                
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
        /*                      Construct query                          */
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        /* Select from the basic table                                   */
        querySQL = "SELECT ROOT.VULNERABILITIES.*, ROOT.REFERENCESS.REF_TYPE, "
                 + "ROOT.REFERENCESS.\"SOURCE\", ROOT.REFERENCESS.\"LINK\", "
                 + "ROOT.SOFTWARES.\"NAME\" \n"
                 + "FROM ROOT.REFERENCESS \n"
                 + "\tINNER JOIN ROOT.SOFTWARES \n"
                 + "\t\tON REFERENCESS.CVE_ID = SOFTWARES.CVE_ID \n"
                 + "\tINNER JOIN ROOT.VULNERABILITIES \n"
                 + "\t\tON REFERENCESS.CVE_ID = VULNERABILITIES.CVE_ID \n";
        /*  Including the keywords in the query                          */
        querySQL += "WHERE SOFTWARES.CVE_ID LIKE ?";
        
        System.out.println(querySQL);
        
        PreparedStatement prep = this.database.getconn().prepareStatement(querySQL);
        
        prep.setString(1, "%"+query+"%"); 
                
        rs = prep.executeQuery();
        
        //rs = this.database.executestatement(querySQL);
        
        //String xmlcontent = StoreResultsInFile(rs);
        File xmlfile = StoreResultsInFile(rs);
        
        return xmlfile;

    }
}
