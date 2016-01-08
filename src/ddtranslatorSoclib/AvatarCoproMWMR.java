/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */

package ddtranslatorSoclib;
import java.util.*;

public class AvatarCoproMWMR extends AvatarComponent{

    private String timerName;
    private int srcid ;
    private int tgtid ;
    private int plaps ;
    private int fifoToCoprocDepth;
    private int fifoFromCoproDepth;
    private int nToCopro;
    private int nFromCopro;
    private int nConfig;
    private int nStatus;
    private boolean useLLSC;

    private AvatarConnectingPoint[] connectingsPoints;
    private int nbConnectingPoint = 16 ;

    public AvatarCoproMWMR(String _timerName,int srcid, int _srcid, int _tgtid, int _plaps, int _fifoToCoprocDepth,int _fifoFromCoproDepth, int _nToCopro, int _nFromCopro, int _nConfig, int _nStatus, boolean _useLLSC)
    {
      timerName = _timerName;
      srcid =  _srcid;
      tgtid = _tgtid;
	plaps = _plaps ;
      fifoToCoprocDepth = _fifoToCoprocDepth;
      fifoFromCoproDepth = _fifoFromCoproDepth;
      nToCopro = _nToCopro;
      nFromCopro = _nFromCopro;
      nConfig = _nConfig;
      nStatus = _nStatus;
      useLLSC = _useLLSC;

      connectingsPoints = new AvatarConnectingPoint[nbConnectingPoint] ;

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
    
	String getTimerName(){
	return timerName;
	}

	int getSrcid(){
	return srcid;
	}

	int getTgtid(){
	return tgtid;
	}

	int getPalpas(){
	return plaps;
	}

	int getFifoToCoProcDepth(){
	return fifoToCoprocDepth;
	}

	int getNToCopro(){
	return nToCopro;
	}

	int getNFromCopro(){
	return nFromCopro;
	}
	int getNConfig(){
	return nConfig;
	}

	int getNStatus(){
	return nStatus;
	}

	boolean getUseLLSC(){
	return useLLSC;
	}

}
