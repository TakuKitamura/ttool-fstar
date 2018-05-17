/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * Daniela Genius, Lip6, UMR 7606 
 * 
 * ludovic.apvrille AT enst.fr
 * daniela.genius@lip6.fr
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




/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */

package ddtranslatorSoclib;
import java.util.LinkedList;

public class AvatarCoproMWMR extends AvatarComponent{

    private String coprocName;
    private int srcid ;
    private int tgtid ;
    private int plaps ;
    private int fifoToCoprocDepth;
    private int fifoFromCoprocDepth;
    private int nToCopro;
    private int nFromCopro;
    private int nConfig;
    private int nStatus;
    private boolean useLLSC; 
    private int coprocType;
   

    private AvatarConnectingPoint[] connectingsPoints;
    private int nbConnectingPoint = 16 ;

    private LinkedList<AvatarTask> tasksMapped;// DG 21.09.

    public AvatarCoproMWMR(String _coprocName,int srcid, int _srcid, int _tgtid, int _plaps, int _fifoToCoprocDepth,int _fifoFromCoprocDepth, int _nToCopro, int _nFromCopro, int _nConfig, int _nStatus, boolean _useLLSC, int _coprocType)
    {
      coprocName = _coprocName;
      srcid =  _srcid;
      tgtid = _tgtid;
	plaps = _plaps ;
      fifoToCoprocDepth = _fifoToCoprocDepth;
      fifoFromCoprocDepth = _fifoFromCoprocDepth;
      nToCopro = _nToCopro;
      nFromCopro = _nFromCopro;
      nConfig = _nConfig;
      nStatus = _nStatus;
      useLLSC = _useLLSC;
      coprocType = _coprocType;
      
      connectingsPoints = new AvatarConnectingPoint[nbConnectingPoint] ;
      tasksMapped = new LinkedList<AvatarTask>(); //DG 21.09.
    }
    
    AvatarConnectingPoint[] getAvatarConnectingPoints(){
      return connectingsPoints;
    }
    
    int  getnbConnectingPoint(){
      return nbConnectingPoint;
    }

    void setConnectingPoint(int _indexConnectingPoint, AvatarConnector _connector){
      connectingsPoints[_indexConnectingPoint].setConnector(_connector);
    }
    
	public String putCoprocName(String coprocName){
	    String MyCoprocName = coprocName;
	return MyCoprocName;
	}

	public  String getCoprocName(){
	return coprocName;
	}

	public int getSrcid(){
	return srcid;
	}

	public int getTgtid(){
	return tgtid;
	}

	public int getPlaps(){
	return plaps;
	}

	public int getFifoToCoprocDepth(){
	return fifoToCoprocDepth;
	}

	public int getFifoFromCoprocDepth(){
	return fifoFromCoprocDepth;
	}

	public int getNToCopro(){
	return nToCopro;
	}

	public int getNFromCopro(){
	return nFromCopro;
	}
	public int getNConfig(){
	return nConfig;
	}

	public int getNStatus(){
	return nStatus;
	}

	public boolean getUseLLSC(){
	return useLLSC;
	}

    public int getCoprocType(){
	return coprocType;
	}
    //DG 21.09.
    public LinkedList<AvatarTask> getAllTasks(){
      return tasksMapped;
    }
    public void addTask(AvatarTask task){
      tasksMapped.add(task);
    }


}
