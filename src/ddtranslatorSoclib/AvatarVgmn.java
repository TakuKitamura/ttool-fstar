/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */

package ddtranslatorSoclib;
import java.util.*;

public class AvatarVgmn extends AvatarComponent{

    private  String vgmnName;
    private int nbOfAttachedInitiators;
    private int nbOfAttachedTargets;
    private int fifoDepth;
    private int minLatency;

    public AvatarVgmn(String _vgmnName, int _nbOfAttachedInitiators,int _nbOfAttachedTargets, int _fifoDepth, int _minLatency ){

      vgmnName =  _vgmnName;
      nbOfAttachedInitiators = _nbOfAttachedInitiators;
      nbOfAttachedTargets =  _nbOfAttachedTargets ;
      fifoDepth = _fifoDepth ;
      minLatency = _minLatency ;

    }

    public String getVgmnName(){
	return vgmnName;
    }

    public int getNbOfAttachedInitiators(){
	return nbOfAttachedInitiators;
    }

    public int getNbOfAttachedTargets(){
	return nbOfAttachedTargets;
    }

    public int getFifoDepth(){
	return fifoDepth;
    }

    public int getMinLatency(){
	return minLatency;
    }

    public void setFifoDepth(int nb){
	fifoDepth=nb;
    }

    public void setMinLatency(int nb){
	minLatency=nb;
    }

    public void setNbOfAttachedInitiators(int nb){
      nbOfAttachedInitiators = nb;
    }

    public void setnbOfAttachedTargets(int nb){
      nbOfAttachedTargets = nb;
    }
 
}
