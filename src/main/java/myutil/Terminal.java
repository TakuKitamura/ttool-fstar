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
 * @author Ludovic APVRILLE
 */
public class Terminal {
    private final static int MAX_BUFFER_SIZE = 5000;

    private final static int CR = 10;

    private final static int BACKSPACE = 8;
    private final static int DEL = 127;

    private Vector<String> buffer;
    private int maxbufferSize = MAX_BUFFER_SIZE;
    private TerminalProviderInterface terminalProvider;
    private int cpt;



   public Terminal() {
       buffer = new Vector<>();
       cpt = 0;
   }

   public void setTerminalProvider(TerminalProviderInterface tp) {
       terminalProvider = tp;
   }

   public String getNextCommand() {

       char x = 110;
       int val = 0;


       printPrompt(cpt);
       String currentBuf = "";
       try {
           while(val != 3) {
               val = (RawConsoleInput.read(true));
               x = (char) val;

               if (val == CR) {
                   if (currentBuf.length() == 0) {
                       myPrint("\n");
                       printPrompt(cpt);
                   } else {
                       cpt++;
                       //myPrint("\n");
                       return currentBuf;
                   }
               }

               if ((val == BACKSPACE) || (val == DEL)) {
                   myPrint("\b \b");
                   if (currentBuf.length() > 0) {
                       currentBuf = currentBuf.substring(0, currentBuf.length() - 1);
                   }
               } else if (val >= 32) {
                   //System.out.print("" + x + "(val=" + val + ");");
                   myPrint(""+x);
                   currentBuf += x;
               }

           }
       } catch (Exception e) {
           return null;
       }


    return "";
   }

   public void myPrint(String s) {
       System.out.print(s);
   }

   public void printHistory() {
       int cpt = 1;
       for(String s: buffer) {
           System.out.println("" + cpt+ ":" + s);
           cpt ++;
       }
   }





    public void printPrompt(int cpt) {
	    System.out.print("" + cpt + "> ");
    }




}
