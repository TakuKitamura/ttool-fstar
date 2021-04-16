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

import java.io.*;


/**
 * Class FileUtils
 * Creation: 01/12/2003
 *
 * @author Ludovic APVRILLE
 * @version 1.1 01/12/2003
 */
public class FileUtils extends org.apache.commons.io.FileUtils {

    public final static String xml = "xml";
    public final static String rtl = "lot";
    public final static String lib = "lib";
    public final static String img = "png";
    public final static String png = "png";
    public final static String dta = "dta";
    public final static String rg = "rg";
    public final static String aut = "aut";
    public final static String bcg = "bcg";
    public final static String dot = "dot";
    public final static String lot = "lot";
    public final static String tlsa = "tlsa";
    public final static String tif = "tif";
    public final static String svg = "svg";
    public final static String csv = "csv";


    public static String getExtension(File f) {
        return getExtension(f.getName());
    }

    public static String getExtension(String name) {
        String ext = "";
        int i = name.lastIndexOf('.');

        if (i > 0 && i < name.length() - 1) {
            ext = name.substring(i + 1).toLowerCase();
        }
        return ext;
    }


    public static boolean checkPath(String path) {
        return new File(path).isDirectory();
    }


    public static boolean checkFileForSave(File file) throws FileException {
        //     boolean ok = true;
        //    String pb = "";

        if (file == null) {
            return false;
        }

        try {
            if (file != null) {
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        throw new FileException("File could not be created");
                    }
                    if (!file.canWrite()) {
                        throw new FileException("File is write protected");
                    }
                }
            }
        } catch (Exception e) {
            throw new FileException(e.getMessage());
        }
        return true;
    }

    public static boolean checkFileForOpen(File file) {

        if (file == null) {
            return false;
        }

        try {
            if (file != null) {
                if (!file.exists()) {
                    return false;
                }
                if (!file.canRead()) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }


    public static String SHA1(File file) {
        String fileData = loadFileData(file);
        if (fileData == null) {
            //TraceManager.addDev("Null data in SHA1 for file=" + file);
            return null;
        }

        String sha1 = Conversion.toSHA1(fileData);
        return sha1;
    }

    public static String loadFileData(File file) {
        char[] ba;
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF8");
            FileInputStream fis = new FileInputStream(file);
            int nb = fis.available();

            ba = new char[nb];
            isr.read(ba, 0, nb);
            fis.close();
            isr.close();
        } catch (Exception e) {
            return null;
        }
        return new String(ba);
    }

    // extension is given without the "."
    public static String addFileExtensionIfMissing(String fileName, String extension) {
        if (!fileName.endsWith("." + extension)) {
            fileName += "." + extension;
        }
        return fileName;
    }

    // extension is given without the "."
    public static File addFileExtensionIfMissing(File f, String extension) {
        /*if (f == null) {
          TraceManager.addDev(" nullfile");
          } else {
          TraceManager.addDev("non nullfile");
          }*/
        if (!hasExtension(f, extension)) {
            /*if (f == null) {
              TraceManager.addDev(" nullfile");
              } else {
              TraceManager.addDev("non nullfile");
              }*/

            TraceManager.addDev("file=" + f.getAbsolutePath());
            f = new File(f.getAbsolutePath() + "." + extension);
        }

        return f;
    }

    public static String addBeforeFileExtension(String filename, String tobeadded) {
        int index = filename.indexOf(".");

        if (index == -1) {
            return filename + tobeadded;
        }

        filename = filename.substring(0, index) + tobeadded + filename.substring(index, filename.length());
        return filename;
    }

    public static String changeFileExtension(String filename, String extension) {
        int index = filename.indexOf(".");

        if (index == -1) {
            return filename + "." + extension;
        }

        filename = filename.substring(0, index) + "." + extension;
        return filename;
    }

    public static String removeFileExtension(String filename) {
        int index = filename.indexOf(".");

        if (index == -1) {
            return filename;
        }

        filename = filename.substring(0, index);
        return filename;
    }

    public static boolean hasExtension(File f, String extension) {
        if (f == null) {
            return false;
        }
        String fileName = f.getAbsolutePath();
        return fileName.endsWith("." + extension);
    }

    public static String loadFile(String name) throws FileException {
        File f = new File(name);
        String s = null;

        if (checkFileForOpen(f)) {
            try {
                FileInputStream fis = new FileInputStream(f);
                int nb = fis.available();

                byte[] ba = new byte[nb];
                fis.read(ba);
                fis.close();
                s = new String(ba);
            } catch (Exception e) {
                throw new FileException(e.getMessage());
            }
        } else {
            throw new FileException("opening pb");
        }
        return s;
    }

    public static void saveFile(String name, String data) throws FileException {
        File f = new File(name);
        saveFile(f, data);
    }


    public static void saveFile(File f, String data) throws FileException {

        if (checkFileForSave(f)) {
            try {
                if (data == null) {
                    return;
                }
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(data.getBytes());
                fos.close();
                return;
            } catch (Exception e) {
                throw new FileException("Pb when saving file");
            }
        } else {
            throw new FileException("Pb when saving file");
        }
    }

    public static String deleteFiles(String d) {
        return deleteFiles(d, null);
    }

    // d: directory, e : extension
    public static String deleteFiles(String d, String e) {
        File dir = new File(d);
        final String[] list;

        if (e == null) {
            list = dir.list();
        } else {
            final ExtensionFilter filter = new ExtensionFilter(e);
            list = dir.list(filter);
        }

        File file;
        boolean isDeleted;

        String files = "";

        if (list == null) {
            return files;
        }

        if (list.length == 0) return files;

        for (int i = 0; i < list.length; i++) {
            file = new File(d + list[i]);
            isDeleted = file.delete();
            if (isDeleted)
                files += file.toString() + "\n";
        }
        return files;
    }

    public static void mkdir(String path) throws SecurityException {
        File theDir = new File(path);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            theDir.mkdir();
        }

    }


    static class ExtensionFilter implements FilenameFilter {
        private String extension;

        public ExtensionFilter(String extension) {
            this.extension = extension;
        }

        public boolean accept(File dir, String name) {
            return (name.endsWith(extension));
        }
    }

} // Class
