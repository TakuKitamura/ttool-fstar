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


import java.util.Vector;

/**
 * Class Terminal
 * Creation: 21/03/2019
 * Version 2.0 21/03/2019
 *
 * @author Ludovic APVRILLE
 */
public class Terminal {
    private final static int MAX_BUFFER_SIZE = 5000;

    private final static int CR = 10;

    private final static int BACKSPACE = 8;
    private final static int DEL = 127;

    private final static int ESC = 27;

    private final static int TAB = 9;

    private Vector<String> buffer;
    private int bufferPointer;
    private int cursorPosition;
    private int promptLength = 0;
    private int maxbufferSize = MAX_BUFFER_SIZE;
    private TerminalProviderInterface terminalProvider;
    private int cpt;
    private String sequence;
    private String os;


    public Terminal() {
        buffer = new Vector<>();
        cpt = 0;
        os = System.getProperty("os.name").toLowerCase();
        System.out.println("Detected OS:" + os);
        os = os.split(" ")[0];
    }

    public void setTerminalProvider(TerminalProviderInterface tp) {
        terminalProvider = tp;
    }

    public String getNextCommand() {

        char x;
        int val = 0;
        cursorPosition = 0;
        bufferPointer = 0;


        printPrompt(cpt);

        String currentBuf = "";
        sequence = null;
        long timeSeq = 0;


        try {
            while (val != 3) {
                val = (RawConsoleInput.read(true));
                x = (char) val;

                //TraceManager.addDev("val=" + val);

                // Special sequence?
                if (sequence == null) {
                    if (val == ESC) {
                        sequence = "";
                        timeSeq = System.currentTimeMillis();
                    }
                } else {
                    //Time check
                    long now = System.currentTimeMillis();
                    if (now - timeSeq > 10) {
                        sequence = null;
                    } else {
                        sequence += x;
                    }
                }

               /*if (sequence != null) {
                   TraceManager.addDev("Sequence=" + sequence);
                   printSequence(sequence);
               }*/

                if ((sequence != null) && (sequence.length() == 2)) {

                    //UP?
                    if ((sequence.charAt(0) == 91) && (sequence.charAt(1) == 65)) {

                        if (buffer.size() > 0) {
                            delCurrent(currentBuf);
                            bufferPointer = (bufferPointer > 0) ? bufferPointer - 1 : bufferPointer;
                            currentBuf = buffer.get(bufferPointer);
                            //printPrompt(cpt);
                            myPrint(currentBuf);
                            cursorPosition = currentBuf.length();
                            sequence = null;
                            val = -1;
                        } else {
                            sequence = null;
                            val = -1;
                        }


                        // DOWN
                    } else if ((sequence.charAt(0) == 91) && (sequence.charAt(1) == 66)) {
                        if (buffer.size() > 0) {
                            //System.out.println("DOWN");
                            delCurrent(currentBuf);
                            bufferPointer = (bufferPointer == (buffer.size() - 1)) ? bufferPointer : bufferPointer + 1;
                            currentBuf = buffer.get(bufferPointer);
                            //printPrompt(cpt);
                            myPrint(currentBuf);
                            cursorPosition = currentBuf.length();
                            sequence = null;
                            val = -1;
                        } else {
                            sequence = null;
                            val = -1;
                        }


                        // BACKWARD
                    } else if ((sequence.charAt(0) == 91) && (sequence.charAt(1) == 68)) {

                        //System.out.println("DOWN");
                        backward();
                        sequence = null;
                        val = -1;

                        // FORWARD
                    } else if ((sequence.charAt(0) == 91) && (sequence.charAt(1) == 67)) {

                        //System.out.println("DOWN");
                        forward(currentBuf);
                        sequence = null;
                        val = -1;
                        // DEL
                        //TraceManager.addDev("DEL");
                    }
                }

                if ((sequence != null) && (sequence.length() == 3)) {

                    // DEL
                    if ((sequence.charAt(0) == 91) && (sequence.charAt(1) == 51) &&
                            (sequence.charAt(2) == 126)) {
                        //TraceManager.addDev("DEL");
                        currentBuf = del(currentBuf);
                        //cursorPosition--;

                        sequence = null;
                        val = -1;
                        //
                    }

                }

                // Usual CHAR
                if ((sequence == null) && (val != -1)) {

                    // CR
                    if (val == CR) {
                        cursorPosition = 0;
                        if (currentBuf.length() == 0) {
                            myPrint("\n");
                            printPrompt(cpt);
                        } else {
                            cpt++;

                            //if (!(os.startsWith("mac"))) {
                            myPrint("\n");
                            //}
                            addToBuffer(currentBuf);
                            return currentBuf;
                        }
                    }

                    //BACKSPACE
                    if ((val == BACKSPACE) || (val == DEL)) {
                        //System.out.println("BACKSPACE/DEL");
                        currentBuf = del(currentBuf);


                        //TAB
                    } else if (val == TAB) {
                        System.out.println("TAB");
                        if (terminalProvider != null) {
                            boolean b = terminalProvider.tabAction(currentBuf);
                            if (b) {
                                printPrompt(cpt);
                            }
                        }

                        // Regular character
                    } else if (val >= 32) {
                        //System.out.print("" + x + "(val=" + val + ");");
                        if (cursorPosition == currentBuf.length()) {
                            myPrint("" + x);
                            currentBuf += x;
                        } else {
                            //System.out.println("Tricky cursor position");
                            int sep = currentBuf.length() - cursorPosition;
                            currentBuf = currentBuf.substring(0, cursorPosition) + x + currentBuf.substring(cursorPosition, currentBuf.length());
                            //TraceManager.addDev("buffer=" + currentBuf);
                            myPrint(currentBuf.substring(cursorPosition, currentBuf.length()));
                            // Must move cursor to previous psition + 1
                            for (int i = 0; i < sep; i++) {
                                backwardCode();
                            }
                            //TraceManager.addDev("buffer=" + currentBuf);
                        }
                        cursorPosition++;

                    }
                }

            }
        } catch (Exception e) {
            TraceManager.addDev("Exception in terminal:" + e.getMessage());
            return null;
        }


        return "";
    }

    private void addToBuffer(String newBuf) {
        // Add at bufferPointer
        // Therefore remove all elements after bufferPointer
        for (int i = buffer.size() - 1; i >= bufferPointer; i--) {
            buffer.removeElementAt(i);
        }

        buffer.add(newBuf);

        if (buffer.size() > maxbufferSize) {
            buffer.removeElementAt(0);
        }

        bufferPointer = buffer.size();

    }


    private String del(String currentBuf) {
        //TraceManager.addDev("DEL");
        if (cursorPosition > 0) {
            if (currentBuf.length() > 0) {
                if (cursorPosition == currentBuf.length()) {
                    myPrint("\b \b");
                    currentBuf = currentBuf.substring(0, currentBuf.length() - 1);
                    cursorPosition--;
                } else {
                    currentBuf = currentBuf.substring(0, cursorPosition - 1) + currentBuf.substring(cursorPosition + 1, currentBuf.length());
                    myPrint("\b" + currentBuf.substring(cursorPosition, currentBuf.length()) + " ");
                    cursorPosition --;
                }
            }
        }
        return currentBuf;
    }

    public void myPrint(String s) {
        //if (os.compareTo("mac") != 0) {
        System.out.print(s);
        //}
        System.out.flush();
    }


    private String delCurrent(String currentBuf) {
        if (buffer.size() > 0) {
            int size = currentBuf.length();
            for (int i = 0; i < size; i++) {
                currentBuf = del(currentBuf);
            }
        }
        return currentBuf;

    }


    public void printPrompt(int cpt) {
        String p = "" + cpt + "> ";
        promptLength = p.length();
        System.out.print(p);
    }

    private void backward() {
        if (cursorPosition == 0) {
            return;
        }
        backwardCode();
        cursorPosition--;
    }

    private void backwardCode() {
        System.out.print("\033[1D");
    }

    private void forward(String currentBuf) {
        if (cursorPosition >= currentBuf.length()) {
            return;
        }
        System.out.print("\033[1C");
        cursorPosition++;
    }


}
