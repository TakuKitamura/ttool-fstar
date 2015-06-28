/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.apache.commons.io.FileUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Contain all the functions, which are using file
 * @author Marie FORRAT & Angeliki AKTYPI
 */
public class File_management {

    /**
     * Download the zipped xml file from the National Vulnerability Database's website (https://nvd.nist.gov/)
     * if the file doesn't exist in the working directory of the project.
     * After extracting the xml file delete the zipped one.
     * @param filename name of the file on the website
     */
    public static void DownloadFile(String filename) throws MalformedURLException, IOException {

        File file = new File(filename);

        /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=  */
        /*   If the file already exists return else download it from the      */
        /*            the web site: https://nvd.nist.gov/                     */
        if (file.exists()) {
            return;
        }

        try {
            //set the URL of the file to be downloaded
            URL url = new URL("http://static.nvd.nist.gov/feeds/xml/cve/" + filename + ".zip");

            System.out.println("File: " + filename + " does not exists");
            System.out.println("Downloading file: " + url.toString());

            //create the new connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //set up some the connection and connect
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            //creat the zip file 
            String zip_filename = filename+".zip";
            File zip_file = new File(zip_filename);

            //write the downloaded data into the zip file
            FileOutputStream fileOutput = new FileOutputStream(zip_file);

            //reading the data from the url
            InputStream inputStream = urlConnection.getInputStream();

            //total size of the file
            int totalSize = urlConnection.getContentLength();
            //variable to store total downloaded bytes
            int downloadedSize = 0;

            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0; //used to store a temporary size of the buffer

            //read through the input buffer and write the contents to the file
            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                //add the data in the buffer to the file in the file output stream
                fileOutput.write(buffer, 0, bufferLength);
                //add up the size so we know how much is downloaded
                downloadedSize += bufferLength;
            }
            //close the output stream when done
            fileOutput.close();

            unZipFile(zip_filename);

            //keep only the xml files (the extracted file from the zip) 
            zip_file.delete();

            //catch some possible errors...
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unZipFile(String zipFile){

        byte[] buffer = new byte[1024];

        try{

            //get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null){

                String fileName = ze.getName();
                File newFile = new File(fileName);


                System.out.println("Unzipping file: " + newFile.getAbsoluteFile());


                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            //System.out.println("Done");

        } catch(IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Store the result of one query on an xml file to be sent to a client
     * @param rs result of a sql query 
     * @return the xml file
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws IOException
     */
    public static File StoreResultsInFile(ResultSet rs)
            throws TransformerConfigurationException, TransformerException, IOException {

        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
            Element results = doc.createElement("Results");
            doc.appendChild(results);

            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            /*                       Execute query                           */
            /* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();

            while (rs.next()) {
                Element row = doc.createElement("Row");
                results.appendChild(row);
                for (int ii = 1; ii <= colCount; ii++) {
                    String columnName = rsmd.getColumnName(ii);
                    Object value = rs.getObject(ii);
                    Element node = doc.createElement(columnName);
                    node.appendChild(doc.createTextNode(value.toString()));
                    row.appendChild(node);
                }
            }

        } catch (ParserConfigurationException | DOMException | SQLException e) {
        }

        DOMSource domSource = new DOMSource(doc);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();

        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

        // we want to pretty format the XML output
        // note : this is broken in jdk1.5 beta!
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        java.io.StringWriter sw = new java.io.StringWriter();
        StreamResult sr = new StreamResult(sw);
        transformer.transform(domSource, sr);
        String out = new SimpleDateFormat("dd-MM-yy/hh-mm-ss").format(new Date());
        String xmlContent = sr.getWriter().toString();
        //boolean success = (new File(System.getProperty("user.dir")+"\\results")).mkdir();
        //File file = new File(System.getProperty("user.dir")+"\\results\\"+out+".xml");
        boolean success = (new File(System.getProperty("user.dir")+"/results")).mkdirs();
        File file = new File(System.getProperty("user.dir")+"/results/"+out+".xml");
        if (success=false) {
            System.out.println("The folder structure does not exist");
        }else {
            String encode = "UTF-8";
            FileUtils.writeStringToFile(file, xmlContent, encode);
        }

        File xmlFile = FileUtils.getFile(file);

        return xmlFile;
    }

    /**
     * Parse a xml file, which contain CVE to retrieve all the information and fill a database with all the retrieve information
     * The structure of the CVE has to be the same as the structure you can find in the cve from https://nvd.nist.gov/ 
     * @param filename name of the xml file you want to parse
     * @param database database you want to fill with data from the file
     */
    public static void ParsingXML(String filename, web.crawler.Database_creation database) {
        LinkedList<String> list_id = new LinkedList<>();
        LinkedList<String> list_pub_date = new LinkedList<>();
        LinkedList<String> list_score = new LinkedList<>();
        LinkedList<String> list_complex = new LinkedList<>();
        LinkedList<String> list_sum = new LinkedList<>();
        LinkedList<String> list_mod_date = new LinkedList<>();
        LinkedList<String> list_access_vector = new LinkedList<>();
        LinkedList<String> list_authentication = new LinkedList<>();
        LinkedList<String> list_conf_impact = new LinkedList<>();
        LinkedList<String> list_int_impact = new LinkedList<>();
        LinkedList<String> list_avail_impact = new LinkedList<>();
        LinkedList<String> list_generated_on_date = new LinkedList<>();
        LinkedList<String> list_cwe_id = new LinkedList<>();
        LinkedList<String> list_ref_type = new LinkedList<>();
        LinkedList<String> list_ref_source = new LinkedList<>();
        LinkedList<String> list_ref_link = new LinkedList<>();
        LinkedList<String> list_ref_cve = new LinkedList<>();
        LinkedList<String> list_soft_cve = new LinkedList<>();
        LinkedList<String> list_soft_name = new LinkedList<>();

        try {

            DownloadFile(filename);
            System.out.println("Extracting data from file: " + filename);
            File fXmlFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = (Document) dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            // For all the CVE that are in the file
            NodeList nList = doc.getElementsByTagName("entry");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    //Store all the information in several lists by chcekcing first if the information is available:

                    list_id.add(eElement.getAttribute("id"));

                    if (eElement.getElementsByTagName("cvss:score").item(0) != null) {
                        list_score.add(eElement.getElementsByTagName("cvss:score").item(0).getTextContent());
                    } else {
                        list_score.add("-1");
                    }

                    if (eElement.getElementsByTagName("cvss:access-complexity").item(0) != null) {
                        list_complex.add(eElement.getElementsByTagName("cvss:access-complexity").item(0).getTextContent());
                    } else {
                        list_complex.add("not available");
                    }

                    if (eElement.getElementsByTagName("cvss:access-vector").item(0) != null) {
                        list_access_vector.add(eElement.getElementsByTagName("cvss:access-vector").item(0).getTextContent());
                    } else {
                        list_access_vector.add("not available");
                    }

                    if (eElement.getElementsByTagName("cvss:authentication").item(0) != null) {
                        list_authentication.add(eElement.getElementsByTagName("cvss:authentication").item(0).getTextContent());
                    } else {
                        list_authentication.add("not available");
                    }

                    if (eElement.getElementsByTagName("cvss:confidentiality-impact").item(0) != null) {
                        list_conf_impact.add(eElement.getElementsByTagName("cvss:confidentiality-impact").item(0).getTextContent());
                    } else {
                        list_conf_impact.add("not available");
                    }

                    if (eElement.getElementsByTagName("cvss:integrity-impact").item(0) != null) {
                        list_int_impact.add(eElement.getElementsByTagName("cvss:integrity-impact").item(0).getTextContent());
                    } else {
                        list_int_impact.add("not available");
                    }

                    if (eElement.getElementsByTagName("cvss:availability-impact").item(0) != null) {
                        list_avail_impact.add(eElement.getElementsByTagName("cvss:availability-impact").item(0).getTextContent());
                    } else {
                        list_avail_impact.add("not available");
                    }

                    if (eElement.getElementsByTagName("cvss:generated-on-datetime").item(0) != null) {
                        list_generated_on_date.add(eElement.getElementsByTagName("cvss:generated-on-datetime").item(0).getTextContent());
                    } else {
                        list_generated_on_date.add("not available");
                    }

                    if (eElement.getElementsByTagName("vuln:cwe").item(0) != null) {
                        list_cwe_id.add(eElement.getElementsByTagName("vuln:cwe").item(0).getAttributes().getNamedItem("id").getNodeValue());
                    } else {
                        list_cwe_id.add("not available");
                    }

                    NodeList nList1 = eElement.getElementsByTagName("vuln:references");

                    for (int i = 0; i < nList1.getLength(); i++) {

                        list_ref_cve.add(eElement.getAttribute("id"));

                        if (eElement.getElementsByTagName("vuln:references").item(i) != null) {
                            list_ref_type.add(eElement.getElementsByTagName("vuln:references").item(i).getAttributes().getNamedItem("reference_type").getNodeValue());
                        } else {
                            list_ref_type.add("not available");
                        }

                        if (eElement.getElementsByTagName("vuln:source").item(i) != null) {
                            list_ref_source.add(eElement.getElementsByTagName("vuln:source").item(i).getTextContent());
                        } else {
                            list_ref_source.add("not available");
                        }

                        if (eElement.getElementsByTagName("vuln:reference").item(i) != null) {
                            list_ref_link.add(eElement.getElementsByTagName("vuln:reference").item(i).getAttributes().getNamedItem("href").getNodeValue());
                        } else {
                            list_ref_link.add("not available");
                        }

                    }

                    NodeList nList2 = eElement.getElementsByTagName("vuln:product");

                    for (int j = 0; j < nList2.getLength(); j++) {

                        list_soft_cve.add(eElement.getAttribute("id"));

                        if (eElement.getElementsByTagName("vuln:product").item(j) != null) {
                            list_soft_name.add(eElement.getElementsByTagName("vuln:product").item(j).getTextContent());
                        } else {
                            list_soft_name.add("not available");
                        }
                    }

                    list_pub_date.add(eElement.getElementsByTagName("vuln:published-datetime").item(0).getTextContent());
                    list_mod_date.add(eElement.getElementsByTagName("vuln:last-modified-datetime").item(0).getTextContent());
                    list_sum.add(eElement.getElementsByTagName("vuln:summary").item(0).getTextContent());

                }
            }

            /**
             * Insert all the information retrieve from the file (that are now store in several list) in the database
             */
            PreparedStatement preparedStmt = database.getconn().prepareStatement("INSERT INTO VULNERABILITIES(CVE_ID,PUB_DATE,MOD_DATE,SCORE,ACCESS_VECTOR,ACCESS_COMPLEXITY,AUTHENTICATION,CONFIDENTIALITY_IMPACT,INTEGRITY_IMPACT,AVAILABILITY_IMPACT,GEN_DATE,CWE_ID,SUMMARY)" + "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?)");
            PreparedStatement preparedStmt1 = database.getconn().prepareStatement("INSERT INTO REFERENCESS(CVE_ID,REF_TYPE,SOURCE,LINK)" + "VALUES ( ?, ?, ?, ?)");
            PreparedStatement preparedStmt2 = database.getconn().prepareStatement("INSERT INTO SOFTWARES(CVE_ID,NAME)" + "VALUES ( ?, ?)");

            System.out.println("Inserting " + list_id.size() + " data into VULNERABILITIES table ...");

            for (int i = 0; i < list_id.size(); i++) {

                preparedStmt.setString(1, list_id.get(i));
                preparedStmt.setString(2, list_pub_date.get(i));
                preparedStmt.setString(3, list_mod_date.get(i));
                preparedStmt.setFloat(4, Float.valueOf(list_score.get(i)));
                preparedStmt.setString(5, list_access_vector.get(i));
                preparedStmt.setString(6, list_complex.get(i));
                preparedStmt.setString(7, list_authentication.get(i));
                preparedStmt.setString(8, list_conf_impact.get(i));
                preparedStmt.setString(9, list_int_impact.get(i));
                preparedStmt.setString(10, list_avail_impact.get(i));
                preparedStmt.setString(11, list_generated_on_date.get(i));
                preparedStmt.setString(12, list_cwe_id.get(i));
                preparedStmt.setString(13, list_sum.get(i));
                preparedStmt.executeUpdate();
            }

            System.out.println("Inserting " + list_ref_type.size() + " data into REFERENCESS table ...");

            for (int d = 0; d < list_ref_type.size(); d++) {

                preparedStmt1.setString(1, list_ref_cve.get(d));
                preparedStmt1.setString(2, list_ref_type.get(d));
                preparedStmt1.setString(3, list_ref_source.get(d));
                preparedStmt1.setString(4, list_ref_link.get(d));
                preparedStmt1.executeUpdate();
            }

            System.out.println("Inserting " + list_soft_name.size() + " data into SOFTWARES table ...");

            for (int f = 0; f < list_soft_name.size(); f++) {

                preparedStmt2.setString(1, list_soft_cve.get(f));
                preparedStmt2.setString(2, list_soft_name.get(f));
                preparedStmt2.executeUpdate();
            }

            preparedStmt.close();
            preparedStmt1.close();
            preparedStmt2.close();

            System.out.println("Number of vulnerabilities inserted in the database: " + list_id.size());
            System.out.println();
            database.setTotalRecordsInDatabase(database.getTotalRecordsInDatabase() + list_id.size());
        } catch (ParserConfigurationException | SAXException | IOException | DOMException | SQLException | NumberFormatException e) {
        }
    }

}