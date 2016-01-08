 /* * @version 1.0 07/07/2015
    * @author  Julien Henon, Daniela Genius */

package ddtranslatorSoclib;
import java.util.*;

public class AvatarBus extends AvatarComponent{

    private  String busName;
    private int nbOfAttachedInitiators;
    private int nbOfAttachedTargets;
    private int fifoDepth;
    private int minLatency;

    public AvatarBus(String _busName, int _nbOfAttachedInitiators,int _nbOfAttachedTargets, int _fifoDepth, int _minLatency ){

      busName =  _busName;
      nbOfAttachedInitiators = _nbOfAttachedInitiators;
      nbOfAttachedTargets =  _nbOfAttachedTargets ;
      fifoDepth = _fifoDepth ;
      minLatency = _minLatency ;

    }

    public String getBusName(){
	return busName;
    }

    public int getNbOfAttachedInitiators(){
	return nbOfAttachedInitiators;
    }

    public int getnbOfAttachedTargets(){
	return nbOfAttachedTargets;
    }

    public int getFifoDepth(){
	return fifoDepth;
    }

    public int getMinLatency(){
	return minLatency;
    }

    public void setNbOfAttachedInitiators(int nb){
      nbOfAttachedInitiators = nb;
    }

    public void setnbOfAttachedTargets(int nb){
      nbOfAttachedTargets = nb;
    }
 
}
