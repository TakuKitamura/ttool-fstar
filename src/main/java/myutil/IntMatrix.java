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

import java.util.BitSet;
import java.util.LinkedList;

/**
 * Class IntMatrix
 * Creation: 06/02/2012
 * Version 2.0 06/02/2012
 *
 * @author Ludovic APVRILLE
 */
public class IntMatrix implements Runnable {
    public int[][] matrice;
    public int sizeRow;
    public int sizeColumn;
    private String[] nameOfRows;

    private long percentageCompletion;
    private boolean mustGo;
    private boolean noMultiplier;
    private boolean interrupted;
    private boolean finished;

    private boolean withHeuristics;

    public BitSet[] bitSetOfMatrix;

    public IntMatrix(int _sizeRow, int _sizeColumn) {
        matrice = new int[_sizeRow][_sizeColumn];
        sizeRow = _sizeRow;
        sizeColumn = _sizeColumn;
        nameOfRows = new String[sizeRow];
        for (int i = 0; i < sizeRow; i++) {
            nameOfRows[i] = "" + i;
        }

    }

    public long getPercentageCompetion() {
        return percentageCompletion;
    }

    public void setValue(int i, int j, int value) {
        matrice[i][j] = value;
    }

    public void setLineValues(int line, int[] values) {
        for (int j = 0; j < sizeColumn; j++) {
            matrice[line][j] = values[j];
        }
    }

    public int getValue(int i, int j) {
        try {
            return matrice[i][j];
        } catch (Exception e) {
        }
        return -1;
    }

    public int getNbOfLines() {
        return sizeRow;
    }

    public String getNameOfLine(int i) {
        return nameOfRows[i];
    }

    public void putShortNames() {
        for (int i = 0; i < sizeRow; i++) {
            nameOfRows[i] = "" + i;
        }
    }

    public int getValueOfLine(int i) {
        int cpt = 0;
        for (int j = 0; j < sizeColumn; j++) {
            cpt += matrice[i][j];
        }
        return cpt;
    }

    public int getValueOfLineFromColumn(int columnIndex, int i) {
        int cpt = 0;
        for (int j = columnIndex; j < sizeColumn; j++) {
            cpt += matrice[i][j];
        }
        return cpt;
    }

    public void setNameOfLine(int line, String name) {
        try {
            nameOfRows[line] = name;
        } catch (Exception e) {
        }
    }

    public void setNamesOfLine(String[] names) {
        try {
            for (int i = 0; i < sizeRow; i++) {
                nameOfRows[i] = names[i];
            }
        } catch (Exception e) {
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("Size:" + sizeRow + "x" + sizeColumn + ":\n");
        for (int i = 0; i < sizeRow; i++) {
            sb.append("Row #" + i + " / " + nameOfRows[i] + ": ");
            for (int j = 0; j < sizeColumn; j++) {
                sb.append(" " + matrice[i][j]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void swapLines(int line0, int line1) {
        int tmp;
        for (int j = 0; j < sizeColumn; j++) {
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
        for (int j = 0; j < sizeColumn; j++) {
            matrice[line0][j] = matrice[line0][j] / diviser;
        }
    }

    // Combine line line0 minus m times line1 ; line0 gets the result 
    public void linearCombination(int line0, int line1, int m) {
        //TraceManager.addDev("combination l0 = " + line0 + " line1 = " + line1 + " multiplier m=" + m);
        for (int j = 0; j < sizeColumn; j++) {
            matrice[line0][j] = matrice[line0][j] - (m * matrice[line1][j]);
        }
    }

    // Returns the line number at which there is the first non nul element of column col0
    // Returns -1 in case none is found
    public int rgpivot(int col0, int nbOfLines) {
        int i = col0;
        while ((i < (nbOfLines)) && (matrice[i][col0] == 0)) {
            i++;
        }
        if (matrice[i][col0] == 0) {
            return 0;
        } else {
            return i;
        }
    }

    public IntMatrix clone() {
        IntMatrix im = new IntMatrix(sizeRow, sizeColumn);
        for (int i = 0; i < sizeRow; i++) {
            for (int j = 0; j < sizeColumn; j++) {
                im.matrice[i][j] = matrice[i][j];
            }
            setNameOfLine(i, nameOfRows[i]);
        }

        return im;
    }

    public void concatAfter(IntMatrix im) {
        int tmp = sizeColumn;
        sizeColumn += im.sizeColumn;
        int[][] newMatrice = new int[sizeRow][sizeColumn];
        for (int i = 0; i < sizeRow; i++) {
            for (int j = 0; j < sizeColumn; j++) {
                if (j < tmp) {
                    newMatrice[i][j] = matrice[i][j];
                } else {
                    newMatrice[i][j] = im.matrice[i][j - tmp];
                }
            }
        }
        matrice = newMatrice;
    }

    public int[] getLine(int lineIndex) {
        int[] line = new int[sizeColumn];
        for (int j = 0; j < sizeColumn; j++) {
            line[j] = matrice[lineIndex][j];
        }
        return line;
    }

    public String lineToString(int[] line) {
        String s = "";
        for (int i = 0; i < line.length; i++) {
            s += line[i] + " ";
        }
        return s;
    }

    public String namesOfRowToString() {
        String s = "";
        for (int i = 0; i < nameOfRows.length; i++) {
            s += nameOfRows[i] + "\n";
        }
        return s;
    }


    // Add a line at the end of the matrix
    public void addLine(int[] newLine, String nameOfRow) {
        sizeRow++;
        int[][] newMatrice = new int[sizeRow][sizeColumn];
        for (int j = 0; j < sizeColumn; j++) {
            for (int i = 0; i < sizeRow - 1; i++) {
                newMatrice[i][j] = matrice[i][j];
            }
            newMatrice[sizeRow - 1][j] = newLine[j];
        }
        matrice = newMatrice;

        String[] newNameOfRows = new String[sizeRow];
        for (int k = 0; k < sizeRow - 1; k++) {
            newNameOfRows[k] = nameOfRows[k];
        }
        if (nameOfRow == null) {
            newNameOfRows[sizeRow - 1] = "" + (sizeRow - 1);
        } else {
            newNameOfRows[sizeRow - 1] = nameOfRow;
        }
        nameOfRows = newNameOfRows;
    }


    public void addLines(LinkedList<IntLine> lines) {
        int oldSizeRow = sizeRow;
        sizeRow += lines.size();
        int[][] newMatrice = new int[sizeRow][sizeColumn];
        String[] newNameOfRows = new String[sizeRow];
        for (int i = 0; i < oldSizeRow; i++) {
            for (int j = 0; j < sizeColumn; j++) {
                newMatrice[i][j] = matrice[i][j];
            }
            newNameOfRows[i] = nameOfRows[i];
        }
        matrice = newMatrice;
        nameOfRows = newNameOfRows;

        int cpt = oldSizeRow;
        for (IntLine il : lines) {
            matrice[cpt] = il.line;
            nameOfRows[cpt] = il.nameOfLine;
            cpt++;
        }


    }


    public void addLinesBitSet(LinkedList<IntLine> lines) {
        int oldSizeRow = sizeRow;
        sizeRow += lines.size();
        int[][] newMatrice = new int[sizeRow][sizeColumn];
        String[] newNameOfRows = new String[sizeRow];
        BitSet[] newBitSetOfMatrix = new BitSet[sizeRow];
        for (int i = 0; i < oldSizeRow; i++) {
            for (int j = 0; j < sizeColumn; j++) {
                newMatrice[i][j] = matrice[i][j];
            }
            newNameOfRows[i] = nameOfRows[i];
            newBitSetOfMatrix[i] = bitSetOfMatrix[i];
        }
        matrice = newMatrice;
        nameOfRows = newNameOfRows;
        bitSetOfMatrix = newBitSetOfMatrix;

        int cpt = oldSizeRow;
        for (IntLine il : lines) {
            matrice[cpt] = il.line;
            nameOfRows[cpt] = il.nameOfLine;
            bitSetOfMatrix[cpt] = il.bs;
            cpt++;
        }


    }


    public void removeLine(int lineIndex) {
        sizeRow--;
        int[][] newMatrice = new int[sizeRow][sizeColumn];
        for (int i = 0; i < sizeRow + 1; i++) {
            for (int j = 0; j < sizeColumn; j++) {
                if (i < lineIndex) {
                    newMatrice[i][j] = matrice[i][j];
                } else {
                    if (i > lineIndex) {
                        newMatrice[i - 1][j] = matrice[i][j];
                    }
                }
            }
        }
        matrice = newMatrice;

        String[] newNameOfRows = new String[sizeRow];
        int dec = 0;
        for (int k = 0; k < sizeRow + 1; k++) {
            if (k != lineIndex) {
                newNameOfRows[k - dec] = nameOfRows[k];
            } else {
                dec++;
            }
        }
        nameOfRows = newNameOfRows;
    }

    public void makeID() {
        for (int i = 0; i < sizeRow; i++) {
            for (int j = 0; j < sizeColumn; j++) {
                if (i == j) {
                    matrice[i][j] = 1;
                } else {
                    matrice[i][j] = 0;
                }
            }
        }
    }


    // noMultiplier indicates whether names of lines may contain the "*" sign, or not.
    public void Farkas(boolean noMultiplier) {
        int sizeColumBeforeConcat = sizeColumn;
        IntMatrix idMat = new IntMatrix(sizeRow, sizeRow);
        idMat.makeID();
        concatAfter(idMat);
        //int[] lined1, lined2, lined;
        int[] lined;
        int gcd;
        int l, i;
        String s0, s1;
        String nameOfNewLine;
        int cpt;
        int total = 0;
        int tmpSizeRow;
        LinkedList<IntLine> ll = new LinkedList<IntLine>();

        for (int j = 0; j < sizeColumBeforeConcat; j++) {
            // Loop on lines to add line combinations
            tmpSizeRow = sizeRow;
            for (i = 0; i < tmpSizeRow - 1; i++) {

                //lined1 = getLine(i);
                //tmpSizeRow = sizeRow;
                for (int k = i + 1; k < tmpSizeRow; k++) {
                    percentageCompletion = total++;
                    if (mustGo == false) {
                        interrupted = true;
                        return;
                    }
                    //TraceManager.addDev("Computing k=" + k + " " + sizeRow + "x" + sizeColumn);
                    //lined2 = getLine(k);

                    // lines d1 and 2 have opposite signs?
                    //if (((lined1[j] < 0) && (lined2[j]>0)) || ((lined1[j]>0) && (lined2[j]<0))) {
                    if (((matrice[i][j] < 0) && (matrice[k][j] > 0)) || ((matrice[i][j] > 0) && (matrice[k][j] < 0))) {
                        lined = new int[sizeColumn];
                        for (l = 0; l < lined.length; l++) {
                            lined[l] = Math.abs(matrice[k][j]) * matrice[i][l] + Math.abs(matrice[i][j]) * matrice[k][l];
                        }
                        if (Math.abs(matrice[k][j]) == 1) {
                            s0 = nameOfRows[i] + " + ";
                        } else {
                            if (noMultiplier) {
                                s0 = nameOfRows[i] + " + ";
                                for (cpt = Math.abs(matrice[k][j]); cpt > 1; cpt--) {
                                    s0 += nameOfRows[i] + " + ";
                                }
                            } else {
                                s0 = "" + Math.abs(matrice[k][j]) + "*(" + nameOfRows[i] + ") + ";
                            }
                        }

                        if (Math.abs(matrice[i][j]) == 1) {
                            s1 = nameOfRows[k];
                        } else {
                            if (noMultiplier) {
                                s1 = nameOfRows[k];
                                for (cpt = Math.abs(matrice[i][j]); cpt > 1; cpt--) {
                                    s1 += " +" + nameOfRows[k];
                                }
                            } else {
                                s1 = "" + Math.abs(matrice[i][j]) + "*(" + nameOfRows[k] + ") + ";
                            }
                        }

                        //TraceManager.addDev("Name of line=" + s0 + "/" + s1);
                        nameOfNewLine = s0 + s1;
                        gcd = MyMath.gcd(lined);
                        //TraceManager.addDev("gcd =" + gcd + " of line =" + lineToString(lined) + " i.e.:" + nameOfNewLine);

                        if (gcd != 0) {
                            for (l = 0; l < lined.length; l++) {
                                lined[l] = lined[l] / gcd;
                            }
                        }

                        ll.add(new IntLine(lined, nameOfNewLine));
                        //addLine(lined, nameOfNewLine);
                        //TraceManager.addDev("matafterline=\n" + toString() + "\n\n");
                    }
                }
            }

            TraceManager.addDev("Adding lines, size=" + sizeRow + "x" + sizeColumn);
            addLines(ll);
            ll.clear();

            TraceManager.addDev("Removing lines, size=" + sizeRow + "x" + sizeColumn);
            // Remove lines whose element #j is not 0
            for (i = 0; i < sizeRow; i++) {
                if (matrice[i][j] != 0) {
                    removeLine(i);
                    //TraceManager.addDev("matafterremove " + i + "=\n" + toString() + "\n\n");
                    i--;
                }
            }
            TraceManager.addDev("Lines removed, size=" + sizeRow + "x" + sizeColumn);

            //TraceManager.addDev("----------------\nD"+ (j+1) +"=\n" + toString() + "\n\n");

        }

        // Remove m first columns


    }

    // noMultiplier indicates whether names of lines may contain the "*" sign, or not.
    public void FarkasForInvariants(boolean noMultiplier) {
        int sizeColumBeforeConcat = sizeColumn;
        IntMatrix idMat = new IntMatrix(sizeRow, sizeRow);
        idMat.makeID();
        concatAfter(idMat);
        //TraceManager.addDev("matconcat=\n" + toString() + "\n\n");
        //int[] lined1, lined2, lined;
        int[] lined;
        int gcd;
        int l, i;
        String s0, s1;
        String nameOfNewLine;
        int cpt;
        long total = 0;
        int tmpSizeRow;
        LinkedList<IntLine> ll = new LinkedList<IntLine>();


        for (int j = 0; j < sizeColumBeforeConcat; j++) {
            // Loop on lines to add line combinations
            tmpSizeRow = sizeRow;

            for (i = 0; i < tmpSizeRow - 1; i++) {
                percentageCompletion = (long) (j * 10000.0 / sizeColumBeforeConcat) + (long) (10000.0 * i / tmpSizeRow / sizeColumBeforeConcat);
                //lined1 = getLine(i);
                //tmpSizeRow = sizeRow;
                for (int k = i + 1; k < tmpSizeRow; k++) {

                    if (mustGo == false) {
                        interrupted = true;
                        return;
                    }
                    //TraceManager.addDev("Computing k=" + k + " " + sizeRow + "x" + sizeColumn);
                    //lined2 = getLine(k);

                    // lines d1 and 2 have opposite signs?
                    //if (((lined1[j] < 0) && (lined2[j]>0)) || ((lined1[j]>0) && (lined2[j]<0))) {
                    if (((matrice[i][j] < 0) && (matrice[k][j] > 0)) || ((matrice[i][j] > 0) && (matrice[k][j] < 0))) {
                        lined = new int[sizeColumn];
                        for (l = 0; l < lined.length; l++) {
                            lined[l] = Math.abs(matrice[k][j]) * matrice[i][l] + Math.abs(matrice[i][j]) * matrice[k][l];
                        }
                        if (Math.abs(matrice[k][j]) == 1) {
                            s0 = nameOfRows[i] + "+";
                        } else {
                            if (noMultiplier) {
                                s0 = nameOfRows[i] + "+";
                                for (cpt = Math.abs(matrice[k][j]); cpt > 1; cpt--) {
                                    s0 += nameOfRows[i] + "+";
                                }
                            } else {
                                s0 = "" + Math.abs(matrice[k][j]) + "*(" + nameOfRows[i] + ") + ";
                            }
                        }

                        if (Math.abs(matrice[i][j]) == 1) {
                            s1 = nameOfRows[k];
                        } else {
                            if (noMultiplier) {
                                s1 = nameOfRows[k];
                                for (cpt = Math.abs(matrice[i][j]); cpt > 1; cpt--) {
                                    s1 += "+" + nameOfRows[k];
                                }
                            } else {
                                s1 = "" + Math.abs(matrice[i][j]) + "*(" + nameOfRows[k] + ") + ";
                            }
                        }

                        //TraceManager.addDev("Name of line=" + s0 + "/" + s1);
                        nameOfNewLine = s0 + s1;
                        gcd = MyMath.gcd(lined);
                        //TraceManager.addDev("gcd =" + gcd + " of line =" + lineToString(lined) + " i.e.:" + nameOfNewLine);

                        if (gcd != 0) {
                            for (l = 0; l < lined.length; l++) {
                                lined[l] = lined[l] / gcd;
                            }
                        }

                        ll.add(new IntLine(lined, nameOfNewLine));
                        //addLine(lined, nameOfNewLine);
                        //TraceManager.addDev("matafterline=\n" + toString() + "\n\n");
                    }
                }
            }

            TraceManager.addDev("Adding lines, size=" + sizeRow + "x" + sizeColumn);
            addLines(ll);
            ll.clear();

            TraceManager.addDev("Removing lines, size=" + sizeRow + "x" + sizeColumn);
            // Remove lines whose element #j is not 0
            int nbToRemoved = 0;
            for (i = 0; i < sizeRow; i++) {
                if (matrice[i][j] != 0) {
                    nbToRemoved++;
                    //removeLine(i);
                    //TraceManager.addDev("matafterremove " + i + "=\n" + toString() + "\n\n");
                    //i--;
                }
            }

            TraceManager.addDev("# of lines to be removed: " + nbToRemoved);
            if (nbToRemoved > 0) {
                int index = 0;
                int[][] newMat = new int[sizeRow - nbToRemoved][sizeColumn];
                String[] newNames = new String[sizeRow - nbToRemoved];
                for (int ii = 0; ii < sizeRow; ii++) {
                    if (matrice[ii][j] == 0) {
                        //TraceManager.addDev("Copying lines to be removed: " + nbToRemoved);
                        // line copy to index
                        for (int jj = 0; jj < sizeColumn; jj++) {
                            newMat[index][jj] = matrice[ii][jj];
                        }
                        newNames[index] = nameOfRows[ii];
                        index++;
                    }
                }
                matrice = newMat;
                sizeRow -= nbToRemoved;
                nameOfRows = newNames;
            }


            TraceManager.addDev("Lines removed, size=" + sizeRow + "x" + sizeColumn);

            //TraceManager.addDev("----------------\nD"+ (j+1) +"=\n" + toString() + "\n\n");

        }

        // Remove m first columns


    }


    // noMultiplier indicates whether names of lines may contain the "*" sign, or not.
    public void FarkasForInvariantsBitSet(boolean withHeuristics) {
        int sizeColumBeforeConcat = sizeColumn;
        IntMatrix idMat = new IntMatrix(sizeRow, sizeRow);
        idMat.makeID();
        concatAfter(idMat);
        //TraceManager.addDev("matconcat=\n" + toString() + "\n\n");
        //int[] lined1, lined2, lined;
        int[] lined;
        int gcd;
        int l, i;
        String s0, s1;
        String nameOfNewLine;
        int cpt;
        long total = 0;
        int tmpSizeRow;
        LinkedList<IntLine> ll = new LinkedList<IntLine>();
        bitSetOfMatrix = new BitSet[sizeRow];
        BitSet bs, bs1, bs2;

        for (int bi = 0; bi < sizeRow; bi++) {
            bitSetOfMatrix[bi] = new BitSet(sizeRow);
            bitSetOfMatrix[bi].set(bi);
        }


        for (int j = 0; j < sizeColumBeforeConcat; j++) {
            // Loop on lines to add line combinations
            tmpSizeRow = sizeRow;

            for (i = 0; i < tmpSizeRow - 1; i++) {
                percentageCompletion = (long) (j * 10000.0 / sizeColumBeforeConcat) + (long) (10000.0 * i / tmpSizeRow / sizeColumBeforeConcat);
                //lined1 = getLine(i);
                //tmpSizeRow = sizeRow;
                for (int k = i + 1; k < tmpSizeRow; k++) {

                    if (mustGo == false) {
                        interrupted = true;
                        return;
                    }
                    //TraceManager.addDev("Computing k=" + k + " " + sizeRow + "x" + sizeColumn);
                    //lined2 = getLine(k);

                    // lines d1 and 2 have opposite signs?
                    //if (((lined1[j] < 0) && (lined2[j]>0)) || ((lined1[j]>0) && (lined2[j]<0))) {
                    if (((matrice[i][j] < 0) && (matrice[k][j] > 0)) || ((matrice[i][j] > 0) && (matrice[k][j] < 0))) {

                        // Take the bit set of the two lines
                        // Lines not yet taken into account?
                        if (!withHeuristics || (!(bitSetOfMatrix[i].intersects(bitSetOfMatrix[k])))) {

                            lined = new int[sizeColumn];
                            for (l = 0; l < lined.length; l++) {
                                lined[l] = Math.abs(matrice[k][j]) * matrice[i][l] + Math.abs(matrice[i][j]) * matrice[k][l];
                            }

                            gcd = MyMath.gcd(lined);
                            //TraceManager.addDev("gcd =" + gcd + " of line =" + lineToString(lined) + " i.e.:" + nameOfNewLine);

                            if (gcd != 0) {
                                for (l = 0; l < lined.length; l++) {
                                    lined[l] = lined[l] / gcd;
                                }
                            }
                            bs = ((BitSet) (bitSetOfMatrix[i].clone()));
                            bs.or(bitSetOfMatrix[k]);

                            ll.add(new IntLine(lined, bs));

                        }
                        //addLine(lined, nameOfNewLine);
                        //TraceManager.addDev("matafterline=\n" + toString() + "\n\n");
                    }
                }
            }

            TraceManager.addDev("Adding lines, size=" + sizeRow + "x" + sizeColumn);
            addLinesBitSet(ll);
            ll.clear();

            TraceManager.addDev("Removing lines, size=" + sizeRow + "x" + sizeColumn);
            // Remove lines whose element #j is not 0
            int nbToRemoved = 0;
            for (i = 0; i < sizeRow; i++) {
                if (matrice[i][j] != 0) {
                    nbToRemoved++;
                    //removeLine(i);
                    //TraceManager.addDev("matafterremove " + i + "=\n" + toString() + "\n\n");
                    //i--;
                }
            }

            TraceManager.addDev("# of lines to be removed: " + nbToRemoved);
            if (nbToRemoved > 0) {
                int index = 0;
                int[][] newMat = new int[sizeRow - nbToRemoved][sizeColumn];
                String[] newNames = new String[sizeRow - nbToRemoved];
                BitSet[] newBitSet = new BitSet[sizeRow - nbToRemoved];
                for (int ii = 0; ii < sizeRow; ii++) {
                    if (matrice[ii][j] == 0) {
                        //TraceManager.addDev("Copying lines to be removed: " + nbToRemoved);
                        // line copy to index
                        for (int jj = 0; jj < sizeColumn; jj++) {
                            newMat[index][jj] = matrice[ii][jj];
                        }
                        newNames[index] = nameOfRows[ii];
                        newBitSet[index] = bitSetOfMatrix[ii];
                        index++;
                    }
                }
                matrice = newMat;
                sizeRow -= nbToRemoved;
                nameOfRows = newNames;
                bitSetOfMatrix = newBitSet;
            }


            TraceManager.addDev("Lines removed, size=" + sizeRow + "x" + sizeColumn);

            //TraceManager.addDev("----------------\nD"+ (j+1) +"=\n" + toString() + "\n\n");

        }

        // Remove m first columns


    }

    public synchronized void startFarkas(boolean _noMultiplier, boolean _withHeuristics) {
        noMultiplier = _noMultiplier;
        withHeuristics = _withHeuristics;
        Thread t = new Thread(this);
        mustGo = true;
        finished = false;
        interrupted = false;
        t.start();
    }

    public synchronized void stopComputation() {
        mustGo = false;
        notifyAll();
    }

    public boolean wasInterrupted() {
        return interrupted;
    }

    public synchronized void callFinished() {
        mustGo = false;
        notifyAll();
    }

    public synchronized boolean isFinished() {
        return finished;
    }

    public void run() {
        try {
            FarkasForInvariantsBitSet(withHeuristics);
        } catch (Error e) {
            TraceManager.addDev("Exception when executing Farkas algorithm: " + e.getMessage());
            interrupted = true;
        }
        if (!interrupted) {
            stopComputation();
        }
        finished = true;
        TraceManager.addDev("Farkas thread completed");
    }


}
