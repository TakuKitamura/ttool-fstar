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

import myutil.TraceManager;
import org.apache.derby.drda.NetworkServerControl;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.*;


/**
* Class DatabaseCreation
* All the function necessary for the creation of the database, creation
* of tables in the database and the storage of this databse
* Management of Avatar block panels
* Creation: 2015
* @version 2.0 25/03/2016
* @author  Marie FORRAT, Angeliki AKTYPI, Ludovic APVRILLE
* @see ui.MainGUI
*/
public class DatabaseCreation {

    /* Global variables */
    private static Connection conn = null;
    private static java.sql.Statement stmt;
    private static java.sql.Statement stmt1;
    private static java.sql.Statement stmt2;
    private int TotalRecordsInDatabase = 0;

    private static String DatabasePath = System.getProperty("user.dir");

    /**
     * Path to retrieve a former vulnerabilities table
     */
    private File vulnerabilitesSqlFile; // = new File(DatabasePath + "//vulnerabilites.sql");

    /**
     *Path to retrieve a former reference table
     */
    private File referencesSqlFile; // = new File(DatabasePath + "//references.sql");

    /**
     *Path to retrieve a former software table
     */
    private File softwaresSqlFile; // = new File(DatabasePath + "//softwares.sql");

    private String dbPath;


    public DatabaseCreation(String _dbPath) {
        dbPath = _dbPath;
        vulnerabilitesSqlFile = new File(dbPath + "//vulnerabilites.sql");
        referencesSqlFile = new File(dbPath + "//references.sql");
        softwaresSqlFile = new File(dbPath + "//softwares.sql");

        try {
            TraceManager.addDev("Path to vuln:" + vulnerabilitesSqlFile.getCanonicalPath());
        } catch (Exception e) {}
    }

    public File getVulnerabilitesSqlFile() {return vulnerabilitesSqlFile;}
    public File getReferencesSqlFile() {return referencesSqlFile;}
    public File getSoftwaresSqlFile() {return softwaresSqlFile;}

    public void deleteVulnerabilitesSqlFile() {vulnerabilitesSqlFile.delete();}
    public void deleteReferencesSqlFile() {referencesSqlFile.delete();}
    public void deleteSoftwaresSqlFile() {softwaresSqlFile.delete();}


    /**
     * Execute an SQL statement on the database. Be careful, this method does not protect of SQL injection
     * @param SQLquery the query you want to execute on the database
     * @return the result of the query
     * @throws SQLException
     */
    public ResultSet executestatement(String SQLquery) throws SQLException {

        ResultSet rs = stmt.executeQuery(SQLquery);
        return rs;
    }

    /**
     * set method to set the total recods in the database
     * @param n integer
     */
    public void setTotalRecordsInDatabase(int n) {
        TotalRecordsInDatabase = n;
    }

    /**
     * get method to get the number of total recods in the table vulnerabilities
     * @return the total number of record in vulnerabilities table
     * @throws SQLException
     */
    public int getTotalRecordsInDatabase() throws SQLException {

        int TotalRecordsInDatabase = 0;
        ResultSet rs = stmt.executeQuery("SELECT * FROM VULNERABILITIES");

        while (rs.next()) {
            TotalRecordsInDatabase++;
        }
        return TotalRecordsInDatabase;
    }

    /**
     * Return the parameter connection of the databse
     * @return connection
     */
    public Connection getconn() {
        return conn;
    }

    /**
     *
     */
    public java.sql.Statement getstmt() {
        return stmt;
    }

    /**
     * Print the table vulnerabilities int the output
     * @throws SQLException
     */
    public void PrintDatabase() throws SQLException {
        int Records = 0;
        ResultSet rs = stmt.executeQuery("SELECT * FROM VULNERABILITIES");

        // Display all the data in the table.
        while (rs.next()) {
            Records++;
            System.out.println("Record: " + Records);
            System.out.println("CVE ID: " + rs.getString(1) + "\t"
                               + "PUB_DATE: " + rs.getString(2) + "\t"
                               + "MOD_DATE: " + rs.getString(3) + "\t"
                               + "SCORE:  " + rs.getString(3) + "\t"
                               + "COMPLEXITY: " + rs.getString(4));

            System.out.println("SUMMARY: " + rs.getString(5) + "\n");
        }
    }

    /**
     * Clear outputs of the console
     * @throws AWTException
     */
    public void ClearOutputs() throws AWTException {
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        /*                      Clear console                            */
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
        Robot pressbot = new Robot();
        pressbot.keyPress(17);   // Holds CTRL key.
        pressbot.keyPress(76);   // Holds L key.
        pressbot.keyRelease(17); // Releases CTRL key.
        pressbot.keyRelease(76); // Releases L key.
        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
    }



    /**
     * Create the database
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws Exception
     */
    public void CreateDatabase() throws Exception {

        /**
         * Information for creating the database
         */
        //We initialize a database in order to connect
        String url = "jdbc:derby://localhost:1527/MyDatabase;create=true;user=root;password=1234";
        //We connect to the initialized database
        String dbName = "MyDatabase";
        String driver = "org.apache.derby.jdbc.ClientDriver";
        String username = "root";
        String password = "1234";

        try {
            Class.forName(driver).newInstance();

            NetworkServerControl server = new NetworkServerControl(InetAddress.getByName("localhost"), 1527);
            server.start(null);
            server.ping();

            /*              STEP 1: Open a connection                        */
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            TraceManager.addDev("Connecting with database...");
            conn = DriverManager.getConnection(url + dbName, username, password);
            TraceManager.addDev("Connection with the database established\n");

            /*        STEP 2: Creating table VULNERABILITIES                 */
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            CreateTable();

        } catch (SQLException e) {
            System.out.println("Database connection has failed.");
            System.out.println("Reason: " + e.getMessage());
        }
    }

    /**
     * Create 3 tables in the database, vulnerabilites, references, softwares
     * @throws SQLException
     */
    public void CreateTable() throws SQLException {
        System.out.println("Creating tables in database...");
        stmt = conn.createStatement();
        stmt1 = conn.createStatement();
        stmt2 = conn.createStatement();
        /* Delete the test table if it exists. Note: This       */
        /* example assumes that the collection VULNERABILITIES  */
        /* exists on the system.                                */
        try {
            stmt.executeUpdate("DROP TABLE VULNERABILITIES");
            stmt1.executeUpdate("DROP TABLE REFERENCESS");
            stmt2.executeUpdate("DROP TABLE SOFTWARES");
        } catch (SQLException e) {
        }

        // Run an SQL statement that creates a table in the database.
        stmt.executeUpdate("CREATE TABLE VULNERABILITIES ("
                           + "CVE_ID VARCHAR(100),"
                           + "PUB_DATE VARCHAR(100),"
                           + "MOD_DATE VARCHAR(100),"
                           + "SCORE FLOAT(4),"
                           + "ACCESS_VECTOR VARCHAR(100),"
                           + "ACCESS_COMPLEXITY VARCHAR(100),"
                           + "AUTHENTICATION VARCHAR(100),"
                           + "CONFIDENTIALITY_IMPACT VARCHAR(100),"
                           + "INTEGRITY_IMPACT VARCHAR(100),"
                           + "AVAILABILITY_IMPACT VARCHAR(100),"
                           + "GEN_DATE VARCHAR(100),"
                           + "CWE_ID VARCHAR(100),"
                           + "SUMMARY VARCHAR(5000))");

        System.out.println("Table VULNERABILITIES created");

        stmt1.executeUpdate("CREATE TABLE REFERENCESS ("
                            + "CVE_ID VARCHAR(100),"
                            + "REF_TYPE VARCHAR(30),"
                            + "SOURCE VARCHAR(30),"
                            + "LINK VARCHAR(1000))");

        System.out.println("Table REFERENCES created");

        stmt2.executeUpdate("CREATE TABLE SOFTWARES ("
                            + "CVE_ID VARCHAR(100),"
                            + "NAME VARCHAR(500))");

        System.out.println("Table SOFTWARES created\n");

    }

    /**
     * import previous table from a file to have a database already fill
     * @throws SQLException
     */
    public void CreateDatabaseFromSQLFile() throws SQLException {
        PreparedStatement ps;

        try {
            ps = conn.prepareStatement("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,?)");
            ps.setString(1, null);
            ps.setString(2, "VULNERABILITIES");
            ps.setString(3, vulnerabilitesSqlFile.getCanonicalPath());
            ps.setString(4, ";");
            ps.setString(5, "%");
            ps.setString(6, null);
            ps.setInt(7, 0);
            ps.execute();

            ps = conn.prepareStatement("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,?)");
            ps.setString(1, null);
            ps.setString(2, "REFERENCESS");
            ps.setString(3, referencesSqlFile.getCanonicalPath());
            ps.setString(4, ";");
            ps.setString(5, "%");
            ps.setString(6, null);
            ps.setInt(7, 0);
            ps.execute();

            ps = conn.prepareStatement("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,?)");
            ps.setString(1, null);
            ps.setString(2, "SOFTWARES");
            ps.setString(3, softwaresSqlFile.getCanonicalPath());
            ps.setString(4, ";");
            ps.setString(5, "%");
            ps.setString(6, null);
            ps.setInt(7, 0);
            ps.execute();
        } catch (IOException exp) {
            TraceManager.addDev("Failure when storing data bin sql files");
        }
    }

    /**
     * Sabe all the tables in a file to use them later
     * @throws SQLException
     */
    public void StoreDatabaseInFile() throws SQLException {
        PreparedStatement ps;

        try {

            /* If myDatabase.sql file already exists then delete it!             */
            if (vulnerabilitesSqlFile.exists()) {
                vulnerabilitesSqlFile.delete();
            }


            TraceManager.addDev("Storing in File:" + vulnerabilitesSqlFile.getCanonicalPath());
            /* Store Table VULNERABILITIES                                       */
            ps = conn.prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            ps.setString(1, null);
            ps.setString(2, "VULNERABILITIES");
            ps.setString(3, vulnerabilitesSqlFile.getCanonicalPath());
            ps.setString(4, ";");
            ps.setString(5, null);
            ps.setString(6, "UTF-8");
            ps.execute();
            System.out.println("Table: VULNERABILITIES is stored in file: " + vulnerabilitesSqlFile.getCanonicalPath());

            /* If myDatabase.sql file already exists then delete it!             */
            if (referencesSqlFile.exists()) {
                referencesSqlFile.delete();
            }

            /* Store Table REFERENCESS                                           */
            ps = conn.prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            ps.setString(1, null);
            ps.setString(2, "REFERENCES");
            ps.setString(3, referencesSqlFile.getCanonicalPath());
            ps.setString(4, ";");
            ps.setString(5, null);
            ps.setString(6, "UTF-8");
            ps.execute();
            System.out.println("Table: REFERENCES is stored in file: " + referencesSqlFile.getCanonicalPath());

            /* If myDatabase.sql file already exists then delete it!             */
            if (softwaresSqlFile.exists()) {
                softwaresSqlFile.delete();
            }

            /* Store Table REFERENCESS                                           */
            ps = conn.prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            ps.setString(1, null);
            ps.setString(2, "SOFTWARES");
            ps.setString(3, softwaresSqlFile.getCanonicalPath());
            ps.setString(4, ";");
            ps.setString(5, null);
            ps.setString(6, "UTF-8");
            ps.execute();
            System.out.println("Table: SOFTWARE is stored in file: " + softwaresSqlFile.getCanonicalPath());

        } catch (IOException exp) {
            TraceManager.addDev("Failure when storing data bin sql files");
        }

    }

}
