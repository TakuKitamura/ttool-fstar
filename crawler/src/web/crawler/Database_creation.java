/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.crawler;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.derby.drda.NetworkServerControl;

/**
 * All the function necessary for the creation of the database, creation of tables in the database and the storage of this databse
 * @author Marie FORRAT & Angeliki AKTYPI
 */
public class Database_creation {

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
    public static File VulnerabilitesSqlFile = new File(DatabasePath + "//vulnerabilites.sql");

    /**
     *Path to retrieve a former reference table
     */
    public static File ReferencesSqlFile = new File(DatabasePath + "//references.sql");

    /**
     *Path to retrieve a former software table
     */
    public static File SoftwaresSqlFile = new File(DatabasePath + "//softwares.sql");

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
     * @return
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
    public void CreateDatabase() throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception {

        /**
         * Information for creating the database
         */
        String url = "jdbc:derby://localhost:1527/MyDatabase;create=true;user=root;password=1234";
        //String url = "jdbc:derby://localhost:1527/";
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
            System.out.println("Connecting with database...");
            conn = DriverManager.getConnection(url + dbName, username, password);
            System.out.println("Connection with the database established\n");

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

        ps = conn.prepareStatement("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,?)");
        ps.setString(1, null);
        ps.setString(2, "VULNERABILITIES");
        ps.setString(3, VulnerabilitesSqlFile.toString());
        ps.setString(4, ";");
        ps.setString(5, "%");
        ps.setString(6, null);
        ps.setInt(7, 0);
        ps.execute();

        ps = conn.prepareStatement("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,?)");
        ps.setString(1, null);
        ps.setString(2, "REFERENCESS");
        ps.setString(3, ReferencesSqlFile.toString());
        ps.setString(4, ";");
        ps.setString(5, "%");
        ps.setString(6, null);
        ps.setInt(7, 0);
        ps.execute();

        ps = conn.prepareStatement("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,?)");
        ps.setString(1, null);
        ps.setString(2, "SOFTWARES");
        ps.setString(3, SoftwaresSqlFile.toString());
        ps.setString(4, ";");
        ps.setString(5, "%");
        ps.setString(6, null);
        ps.setInt(7, 0);
        ps.execute();
    }

    /**
     * Sabe all the tables in a file to use them later
     * @throws SQLException
     */
    public void StoreDatabaseInFile() throws SQLException {
        PreparedStatement ps;

        /* If myDatabase.sql file already exists then delete it!             */
        if (VulnerabilitesSqlFile.exists()) {
            VulnerabilitesSqlFile.delete();
        }

        /* Store Table VULNERABILITIES                                       */
        ps = conn.prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
        ps.setString(1, null);
        ps.setString(2, "VULNERABILITIES");
        ps.setString(3, VulnerabilitesSqlFile.toString());
        ps.setString(4, ";");
        ps.setString(5, null);
        ps.setString(6, "UTF-8");
        ps.execute();
        System.out.println("Table: VULNERABILITIES is stored in file: " + VulnerabilitesSqlFile.toString());

        /* If myDatabase.sql file already exists then delete it!             */
        if (ReferencesSqlFile.exists()) {
            ReferencesSqlFile.delete();
        }

        /* Store Table REFERENCESS                                           */
        ps = conn.prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
        ps.setString(1, null);
        ps.setString(2, "REFERENCESS");
        ps.setString(3, ReferencesSqlFile.toString());
        ps.setString(4, ";");
        ps.setString(5, null);
        ps.setString(6, "UTF-8");
        ps.execute();
        System.out.println("Table: REFERENCESS is stored in file: " + ReferencesSqlFile.toString());

        /* If myDatabase.sql file already exists then delete it!             */
        if (SoftwaresSqlFile.exists()) {
            SoftwaresSqlFile.delete();
        }

        /* Store Table REFERENCESS                                           */
        ps = conn.prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
        ps.setString(1, null);
        ps.setString(2, "SOFTWARES");
        ps.setString(3, SoftwaresSqlFile.toString());
        ps.setString(4, ";");
        ps.setString(5, null);
        ps.setString(6, "UTF-8");
        ps.execute();
        System.out.println("Table: SOFTWARES is stored in file: " + SoftwaresSqlFile.toString());

    }

}
