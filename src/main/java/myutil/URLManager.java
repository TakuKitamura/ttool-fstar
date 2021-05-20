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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class URLManager Creation: 31/05/2017
 *
 * @author Ludovic APVRILLE
 * @version 1.1 31/05/2017
 */
public final class URLManager implements Runnable {

  private String url;
  private boolean busy;
  private String path;
  private CallbackLoaderInterface callback;

  public URLManager() {
    busy = false;
  }

  public synchronized boolean downloadFile(String _path, String _url, CallbackLoaderInterface _callback) {
    if (busy) {
      return false;
    }
    busy = true;

    path = _path;
    url = _url;
    callback = _callback;

    Thread t = new Thread(this);
    t.start();
    return true;

  }

  public void run() {
    try {
      String urlF = getRealURL(url);
      File f = new File(path);
      org.apache.commons.io.FileUtils.copyURLToFile(new URL(urlF), f);
      if (callback != null) {
        callback.loadDone();
      }
    } catch (Exception e) {
      if (callback != null) {
        callback.loadFailed(e);
      }
    }
    busy = false;
  }

  public static String getRealURL(String url) {
    try {
      HttpURLConnection connection;
      URL file = new URL(url);
      connection = (HttpURLConnection) (file.openConnection());
      String redirect = connection.getHeaderField("Location");
      if (redirect != null) {
        return redirect;
      }
    } catch (Exception e) {
      TraceManager.addDev("Exception in getRealURL =" + e.getMessage());
    }
    return url;

  }

  public static String getBaseURL(String url) {
    int index = url.lastIndexOf("/");
    if (index == -1) {
      return url;
    }
    return url.substring(0, index + 1);
  }

  public static BufferedReader getBufferedReader(String url) {
    try {
      String urlR = getRealURL(url);
      HttpURLConnection connection;
      URL file = new URL(urlR);
      connection = (HttpURLConnection) (file.openConnection());
      return new BufferedReader(new InputStreamReader(connection.getInputStream()));
    } catch (Exception e) {
      TraceManager.addDev("Exception in getBufferedReader =" + e.getMessage());
    }
    return null;
  }

  public static BufferedImage getBufferedImageFromURL(String url) {
    TraceManager.addDev("getBufferedImageFromURL with url=" + url);
    try {
      return ImageIO.read(new URL(getRealURL(url)));
    } catch (Exception e) {
      TraceManager.addDev("Exception in getBufferedImageFromURL =" + e.getMessage());
      return null;
    }

  }

}
