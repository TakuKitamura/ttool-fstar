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

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static web.crawler.FileManagement.ParsingXML;

/**
 * Class WebCrawler Implement of a webcrawler for CVEs Creation: 2015
 * 
 * @version 2.0 24/03/2016
 * @author Marie FORRAT, Angeliki AKTYPI, Ludovic APVRILLE
 * @see ui.MainGUI
 */
public class WebCrawler {

  public DatabaseCreation database;

  public static final int PORT = 8244;

  private String pathToFiles;

  public WebCrawler(String _pathToFiles) {
    pathToFiles = _pathToFiles + java.io.File.separator;
  }

  public void UpdateDatabase(String[] FileNames) throws IOException, SQLException {

    /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
    /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
    /* Update Database */
    /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    if (database.getReferencesSqlFile().exists() & database.getVulnerabilitesSqlFile().exists()
        & database.getSoftwaresSqlFile().exists()) {

      Path FilePath = Paths.get(pathToFiles + FileNames[0]);
      BasicFileAttributes view = Files.getFileAttributeView(FilePath, BasicFileAttributeView.class).readAttributes();
      // FileTime time = view.creationTime();
      FileTime time = view.lastModifiedTime();
      SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
      String dateCreated = df.format(time.toMillis());
      System.out.println("The last update of the database was on " + dateCreated + "\n");

      System.out.println("Do you want to update the database?\n");
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      String answer = br.readLine().toLowerCase();

      switch (answer) {

        case "yes":

          File thisyearfile = new File(FileNames[0]);
          thisyearfile.delete();

          File lastyearfile = new File(FileNames[1]);
          lastyearfile.delete();

          File beforelastyearfile = new File(FileNames[2]);
          beforelastyearfile.delete();

          File beforebeforelastyearfile = new File(FileNames[3]);
          beforebeforelastyearfile.delete();

          database.deleteReferencesSqlFile();
          database.deleteVulnerabilitesSqlFile();
          database.deleteSoftwaresSqlFile();

          TraceManager.addDev("The database files have been deleted!!\n");
          UpdateDatabase(FileNames);
          break;

        case "no":

          database.CreateDatabaseFromSQLFile();

          TraceManager.addDev("\nDatabase has been restored from the following files:\n"
              + database.getReferencesSqlFile().toString() + "\n" + database.getVulnerabilitesSqlFile().toString()
              + "\n" + database.getSoftwaresSqlFile().toString() + "\n");

          TraceManager.addDev("Total records insert in the database: " + database.getTotalRecordsInDatabase() + "\n\n");

          break;

        default:

          TraceManager.addDev("\nPlease enter Yes or No\n");
          UpdateDatabase(FileNames);
          break;
      }

    } else {
      /* Read XML file and store the informations in the database */
      for (String xmlFile : FileNames) {
        ParsingXML(xmlFile, pathToFiles, database);
      }
      System.out.println("Total records insert in the database: " + database.getTotalRecordsInDatabase() + "\n\n");
      /* Store myDatabase in file myDatabase.sql */
      database.StoreDatabaseInFile();
    }

  }

  public void start() throws Exception {

    if (pathToFiles == null) {
      pathToFiles = "";
    }

    String thisyear = new SimpleDateFormat("yyyy").format(new Date());

    String FileNames[] = { "nvdcve-2.0-" + thisyear + ".xml", "nvdcve-2.0-" + (Integer.valueOf(thisyear) - 1) + ".xml",
        "nvdcve-2.0-" + (Integer.valueOf(thisyear) - 2) + ".xml",
        "nvdcve-2.0-" + (Integer.valueOf(thisyear) - 3) + ".xml", };
    // Database_creation database = new Database_creation();
    database = new DatabaseCreation(pathToFiles);
    /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
    /* Establish connection with server and create database */
    /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
    database.CreateDatabase();

    UpdateDatabase(FileNames);

    /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
    /* Data Visualization */
    /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
    DataVisualisation datavisual = new DataVisualisation(database);

    // datavisual.Histogram("linux");
    /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
    /* Execute SQL queries to the Database */
    /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
    DatabaseQuery dbq = new DatabaseQuery(database);
    // dbq.MakeQueryOnTheDatabase();

    /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
    /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
    /* Server's Protocol Initialization */
    /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
    SSLServerSocket sslServerSocket = null;
    try {
      // ServerSocket server = new ServerSocket(1234);
      SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
      sslServerSocket = (SSLServerSocket) factory.createServerSocket(PORT);

      System.out.println("Server has been created successfully on port " + PORT + "\n");

      while (true) { // Allow a client to connect
        // Use multithread
        // If a client asks to connect, then accept it
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
    TraceManager.addDev("\n\n\nClosing connection with the database");
  }
}
