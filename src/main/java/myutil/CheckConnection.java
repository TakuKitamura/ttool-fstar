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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * CheckConnection check internet connection or remote machine Creation:
 * 17/03/2015
 * 
 * @version 1.0 17/03/2015
 * @author Huy TRUONG
 */
public final class CheckConnection {
    // TODO: input: URL or IPADRESS in STRING FORMAT. //should return true or false

    private static final String default_url_1 = "google.com";
    private static final String default_ip = "8.8.8.8";

    public static final boolean checkInternetConnection() {
        try {
            if (checkConnectionWithAddr(default_url_1) && checkConnectionWithAddr(default_ip)) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } catch (IOException e) {
            return Boolean.FALSE;
        } catch (InterruptedException e) {
            return Boolean.FALSE;
        }
    }

    public static final boolean checkConnectionWithAddr(String addr) throws IOException, InterruptedException {
        Process ping = Runtime.getRuntime().exec("ping -c 2 " + addr);
        int returnVal = ping.waitFor();
        boolean reachable = (returnVal == 0);
        return reachable;
    }

    public static final boolean checkConnectionWithURL(String _url) {
        Boolean isConnect = false;
        URL url;
        try {
            url = new URL(_url);
            URLConnection conn = url.openConnection();
            conn.connect();
            isConnect = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isConnect;
    }

    /*
     * public static final int KO_RESOLVE_DOMAIN = 2; public static final int
     * OK_DEFAULT_URL = 1;
     * 
     * private static final String CHECK_DEFAULT_URL = "www.google.com"; private
     * static final String CHECK_DEAFULT_IP = "8.8.8.8";
     * 
     * 
     * 
     * 
     * public void
     * 
     *//**
        * Check connection to specific ip address
        * 
        * @return
        */
    /*
     * public boolean canConnectToAddr(){ InetAddress addr = new InetAddress()
     * return Boolean.TRUE; }
     * 
     *//**
        * Check connection to default ipaddr : 8.8.8.8
        * 
        * @return
        */
    /*
     * private boolean canConnectToDefaultAddr(){ return Boolean.TRUE; }
     * 
     *//**
        * Check connection to default URL: www.google.com
        * 
        * @return
        */
    /*
     * private boolean canConnectToDefaultURL(){ Boolean isConnect = Boolean.FALSE;
     * URL url; try { url = new URL(CHECK_DEFAULT_URL); URLConnection conn =
     * url.openConnection(); conn.connect(); isConnect= true; } catch
     * (MalformedURLException e) { e.printStackTrace(); } catch (IOException e) {
     * e.printStackTrace(); } return isConnect; }
     * 
     *//**
        * check the connection to specific URL
        * 
        * @param u: string of url
        * @return: TRUE: be able to connect to URL.
        * @return: FALSE: not be able to connect to URL.
        */
    /*
     * public boolean canConnectToURL(String u){ Boolean isConnect = Boolean.FALSE;
     * URL url; try { url = new URL(u); URLConnection conn = url.openConnection();
     * conn.connect(); isConnect= true; } catch (MalformedURLException e) {
     * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
     * 
     * return isConnect; }
     * 
     *//**
        * Check the Internet connection by try to connect to URL "www.google.com" and
        * ip address 8.8.8.8
        *
        * @return: OK_DEFAULT_URL = be able to connect to www.google.com
        * @return: KO_RESOLVE_DOMAIN = be able to connect to ip 8.8.8.8, but can
        *          resolve www.google.com
        *//*
           * public int isInternetConnection(){ if (canConnectToDefaultURL()) return
           * OK_DEFAULT_URL; if (!canConnectToDefaultURL()){ if
           * (canConnectToDefaultAddr()) return KO_RESOLVE_DOMAIN; } return 0; }
           */
}
