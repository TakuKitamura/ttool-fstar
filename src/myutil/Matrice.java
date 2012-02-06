/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class Matrice
 * Creation: 06/02/2012
 * Version 2.0 06/02/2012
 * @author Ludovic APVRILLE
 * @see
 */

package myutil;

import java.util.*;

public class Matrice {
   private int [][] matrice;
   private int sizeRow;
   private int sizeColumn;
   private String []nameOfRows;
   
   public Matrice(int _sizeRow, int _sizeColumn) {
	   matrice = new int[_sizeRow][_sizeColumn];
	   sizeRow= _sizeRow;
	   sizeColumn = _sizeColumn;
	   nameOfRows = new String[sizeRow];
	   for(int i=0; i<sizeRow; i++) {
	   	   nameOfRows[i] = "" + i;
	   }
   }
   
   public void setValue(int i, int j, int value) {
	   matrice[i][j] = value;
   }
   
   public void setLineValues(int line, int[] values) {
   	   for(int j=0; j<sizeColumn; j++) {
   	   	   matrice[line][j] = values[j];
	   }
   }
   
   public int getValue(int i, int j) {
   	   try {
   	   	   return matrice[i][j];
   	   } catch (Exception e) {}
   	   return -1;
   }
   
   public void setNameOfLine(int line, String name) {
   	   try {
   	   	   nameOfRows[line] = name;
   	   } catch(Exception e) {
   	   }
   }
   
   public void setNamesOfLine(String []names) {
   	   try {
   	   	   for(int i=0; i<sizeRow; i++){
   	   	   	   nameOfRows[i] = names[i];
   	   	   }
   	   } catch(Exception e) {
   	   }
   }
   
   public String toString() {
   	   StringBuffer sb = new StringBuffer("");
   	   for(int i=0; i<sizeRow; i++) {
   	   	   sb.append("Row #" + i + " / " + nameOfRows[i] + ": ");  
   	   	   for(int j=0; j<sizeColumn; j++) {
   	   	   	   sb.append(" " + matrice[i][j]);
   	   	   }
   	   	   sb.append("\n");
   	   }
   	   return sb.toString();
   }
   
   public void swapLines(int line0, int line1) {
   	   int tmp;
   	   for(int j=0; j<sizeColumn; j++){
   	   	   tmp = matrice[line0][j];
   	   	   matrice[line0][j] = matrice[line1][j];
   	   	   matrice[line1][j] = tmp;
   	   }
   	   
   	   String tmpName = nameOfRows[line0];
   	   nameOfRows[line0] = nameOfRows[line1];
   	   nameOfRows[line1] = tmpName;
   }
   
   
   // Apply a division on all elements of a line
   public void unitLine(int line0, int diviser) {
   	   for(int j=0; j<sizeColumn; j++){
   	   	   matrice[line0][j] = matrice[line0][j] / diviser;
   	   }
   }
   
   // Combine line line0 minus m times line1 ; line0 gets the result 
   public void linearCombination(int line0, int line1, int m) {
   	   for(int j=0; j<sizeColumn; j++){
   	   	   matrice[line0][j] = matrice[line0][j] - (m * matrice[line1][j]);
   	   }
   }
   
   // Returns the line number at which there is the first non nul element of column col0
   // Returns -1 in case none is found
   public int rgpivot(int col0, int nbOfLines) {
   	   int i = col0;
   	   while((i<(nbOfLines)) && (matrice[i][col0] == 0)) {
   	   		   i++;
   	   }
   	   if (matrice[i][col0] == 0) {
   	   	   return 0;
   	   } else {
   	   	   return i;
   	   }
   }
   
   
   
   
   
  
}
