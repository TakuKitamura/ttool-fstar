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
 * Class ProcessThread
 * For execution of processes
 * Creation: 2005
 * @version 1.1 08/03/2005
 * @author Ludovic APVRILLE
 * @see
 */

package myutil;


import java.io.*;


public class ProcessThread extends Thread {
    private String cmd;
    private MasterProcessInterface mpi;
    BufferedReader proc_in, proc_err;
    private Process proc;
    private boolean isStarted = false;
    private boolean go = true;
    private ErrorThread et;
    
    
    public ProcessThread(String _cmd, MasterProcessInterface _mpi) {
        cmd = _cmd;
        mpi = _mpi;
    }
    
    public boolean isStarted() {
        return isStarted;
    }
    
    public void stopProcess() {
        go = false;
        proc.destroy();
        proc_in = null;
        if (et != null) {
            et.stopProcess();
        }
    }
    
    public void run() {
        isStarted = true;
        System.out.println("Starting process for command " + cmd);
        proc = null;
        //BufferedReader in = null;
        String str = null;
        //PrintStream out = null;
        
        try {
            System.out.println("Going to start command " + cmd);
            
            proc = Runtime.getRuntime().exec(cmd);
            
            proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            proc_err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            
            et = new ErrorThread(proc_err, mpi);
            et.start();
            
            while ( ((str = proc_in.readLine()) != null)  && (go == true) && (mpi.hasToContinue())){    
                System.out.println("Out " + str);
                mpi.appendOut(str+"\n");             
            }
            
            et.stopProcess();
            
        } catch (Exception e) {
            System.out.println("Exception [" + e.getMessage() + "] occured when executing " + cmd);
        }
        System.out.println("Ending command " + cmd);
        
        if (proc != null) {
            proc.destroy();
        }
        
        isStarted = false;
        
    }
}
