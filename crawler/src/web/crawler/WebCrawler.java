/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.crawler;

import java.awt.AWTException;
import java.io.IOException;
import java.sql.SQLException;
import java.net.ServerSocket;
import java.util.ArrayList;
import myutil.externalSearch.Message;
import static web.crawler.File_management.ParsingXML;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;


public class WebCrawler {

    public static Database_creation database;
    
    /**
     * @param args the command line arguments
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     * @throws java.awt.AWTException
     * 
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException, IOException, AWTException, Exception {

        String FileNames[] = {
            "nvdcve-2.0-2012.xml",
            "nvdcve-2.0-2013.xml",
	    "nvdcve-2.0-2014.xml"
        //    "nvdcve-2.0-2015.xml"
        };
        //Database_creation database = new Database_creation();
        database = new Database_creation();
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        /*       Establish connection with server and create database        */
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        database.CreateDatabase();
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        /*                       Create SQL Database                         */
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        if (database.ReferencesSqlFile.exists() & database.VulnerabilitesSqlFile.exists() & database.SoftwaresSqlFile.exists()) {
            database.CreateDatabaseFromSQLFile();
            System.out.println("Database is restored from files:\n"
                    + database.ReferencesSqlFile.toString() + "\n"
                    + database.VulnerabilitesSqlFile.toString() + "\n"
                    + database.SoftwaresSqlFile.toString() + "\n");

            System.out.println("Total records insert in the database: " + database.CountRowsInDatabase() + "\n\n");
        } else {
            /* Read XML file and store the informations in the database          */
            for (String xmlFile : FileNames) {
                ParsingXML(xmlFile, database);
            }
            System.out.println("Total records insert in the database: " + database.getTotalRecordsInDatabase() + "\n\n");
            /* Store myDatabase in file myDatabase.sql                           */
            database.StoreDatabaseInFile();
        }
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        /*                       Data Visualization                          */
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        Data_visualisation datavisual = new Data_visualisation(database);
        //datavisual.Histogram();
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        /*              Execute SQL queries to the Database                  */
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        Database_query dbq= new Database_query(database);
        // dbq.MakeQueryOnTheDatabase();

        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        /*              Server's Protocol Initialization                     */
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */        
 	SSLServerSocket sslServerSocket = null;
        try {
        //    ServerSocket server = new ServerSocket(1234);
		SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                sslServerSocket = (SSLServerSocket) factory.createServerSocket(12345);

            System.out.println("Server has been created successfully\n");

            while (true) { //Allow a client to connect
                //Use multithread
                //If a client asks to connect, then accept it
		SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                sslSocket.setEnabledCipherSuites(sslServerSocket.getSupportedCipherSuites());

                new ThreadSocket(sslSocket, dbq).start();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Clean-up environment
        database.getstmt().close();
        database.getconn().close();
        System.out.println("\n\n\nClosing connection with the database");
        
    }

}
