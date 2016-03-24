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
import java.sql.SQLException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import static web.crawler.File_management.ParsingXML;

/**
 * Main program
 * @author Marie FORRAT & Angeliki AKTYPI
 */






public class WebCrawler {

    /**
     *create the database 
     */
    public static Database_creation database;
    
    /**
     * main program
     * @param args the command line arguments
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     * @throws java.awt.AWTException
     * 
     */
    
    public static void UpdateDatabase(String[] FileNames) throws IOException, SQLException{
    
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        /*                         Update Database                           */
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
                
        if (database.ReferencesSqlFile.exists() & database.VulnerabilitesSqlFile.exists() & database.SoftwaresSqlFile.exists()) {
            
            Path FilePath = Paths.get(FileNames[0]);
            BasicFileAttributes view = Files.getFileAttributeView(FilePath, BasicFileAttributeView.class).readAttributes();
            //FileTime time = view.creationTime();
            FileTime time = view.lastModifiedTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            String dateCreated = df.format(time.toMillis());
            System.out.println("The last update of the database was on " + dateCreated + "\n");
        
            System.out.println("Do you want to update the database?\n");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String answer = br.readLine();
            
            switch (answer){
                
                case "Yes":
                    
                    File thisyearfile = new File(FileNames[0]);
                    thisyearfile.delete();
                    File lastyearfile = new File(FileNames[1]);
                    lastyearfile.delete();
                    File beforelastyearfile = new File(FileNames[2]);
                    beforelastyearfile.delete();
                    File beforebeforelastyearfile = new File(FileNames[3]);
                    beforebeforelastyearfile.delete();
                    database.ReferencesSqlFile.delete();
                    database.VulnerabilitesSqlFile.delete();
                    database.SoftwaresSqlFile.delete();
                    
                    System.out.println("The files have been deleted!!\n");
                    UpdateDatabase(FileNames);
                    break;
                    
                case "No":
                    
                    database.CreateDatabaseFromSQLFile();
                
                    System.out.println("\nDatabase is restored from files:\n"
                        + database.ReferencesSqlFile.toString() + "\n"
                        + database.VulnerabilitesSqlFile.toString() + "\n"
                        + database.SoftwaresSqlFile.toString() + "\n");

                    System.out.println("Total records insert in the database: " + database.getTotalRecordsInDatabase()+ "\n\n");
                    
                    break;
                    
                default:
                    
                    System.out.println("\nPlease enter Yes or No\n");
                    UpdateDatabase(FileNames);
                    break;                
            }
                       
        } else {
            /* Read XML file and store the informations in the database          */
            for (String xmlFile : FileNames) {
                ParsingXML(xmlFile, database);
            }
            System.out.println("Total records insert in the database: " + database.getTotalRecordsInDatabase() + "\n\n");
            /* Store myDatabase in file myDatabase.sql                           */
            database.StoreDatabaseInFile();
        }
    
}
    
    public static void main(String args[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException, IOException, AWTException, Exception {

        /**
         * The name of the file, for example "nvdcve-2.0-2015.xml", from https://nvd.nist.gov/, which data we want to inport in our database
         */
        String thisyear = new SimpleDateFormat("yyyy").format(new Date());
        
        String FileNames[] = {
            "nvdcve-2.0-"+thisyear+".xml",
            "nvdcve-2.0-"+(Integer.valueOf(thisyear)-1)+".xml",
            "nvdcve-2.0-"+(Integer.valueOf(thisyear)-2)+".xml",
            "nvdcve-2.0-"+(Integer.valueOf(thisyear)-3)+".xml",
        };
        //Database_creation database = new Database_creation();
        database = new Database_creation();
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        /*       Establish connection with server and create database        */
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        database.CreateDatabase();
        
        UpdateDatabase(FileNames);
                
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        /*                       Data Visualization                          */
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        Data_visualisation datavisual = new Data_visualisation(database);
       
        // datavisual.Histogram("linux");
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
        try {
            ServerSocket server = new ServerSocket(1234);
            System.out.println("Server has been created successfully\n");

            while (true) { //Allow a client to connect
                //Use multithread
                //If a client asks to connect, then accept it
                new ThreadSocket(server.accept(), dbq).start();
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
