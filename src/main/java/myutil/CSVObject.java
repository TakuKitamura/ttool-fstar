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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.UUID;

/**
 * Class CSVObject
 * Creation: 16/04/2021
 * Version 2.0 16/04/2021
 * @author Ludovic APVRILLE
 */
public class CSVObject  {
  public ArrayList<String[]> lines;

    public CSVObject () {
    }
   
   public CSVObject (String _toParse) {
	   parse(_toParse);
   }
   
   public boolean parse(String _toParse) {
       if (_toParse == null) {
           return false;
       }

       _toParse = _toParse.trim();
       if (_toParse.length() == 0) {
           return false;
       }

       String[] allLines = _toParse.split(System.getProperty("line.separator"));

       if (lines == null) {
           lines = new ArrayList<>(allLines.length);
       } else {
           lines.clear();
       }



       int lineLength = 0;
       for(int i=0; i<allLines.length; i++) {
           String [] elt = allLines[i].split(",");
           if (i == 0) {
               lineLength = elt.length;
           } else {
               if (elt.length != lineLength) {
                   lines = null;
                   return false;
               }
           }
           lines.add(elt);
       }

       // Remove spaces
       removeSpaces();

       return true;

   }

   private void removeSpaces() {
        for(String[] ss: lines) {
            for(int i=0; i<ss.length; i++) {
                ss[i] = ss[i].trim();
            }
        }
   }


   public int getNbOfLines() {
        if (lines == null) {
            return -1;
        }
        return lines.size();
   }

    public int getNbOfEltsPerLine() {
        if (lines == null) {
            return -1;
        }

        if (lines.size() == 0) {
            return -1;
        }

        return lines.get(0).length;
    }



   public String get(int line, int col) {
       if (lines == null) {
           return null;
       }

       if (line >= lines.size()) {
           return null;
       }

       String[] selectedLine = lines.get(line);
       if (col >= selectedLine.length) {
           return null;
       }

       return selectedLine[col];
   }

   public int getInt(int line, int col) throws NumberFormatException {
        String val = get(line, col);
        return Integer.decode(val);
   }

   public UUID getUUID(int line, int col) throws IllegalArgumentException {
       String val = get(line, col);
       if (val == null) {
           return null;
       }
       UUID uuid = UUID.fromString(val);
       return uuid;
   }
   
  
}
