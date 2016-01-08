/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */

package ddtranslatorSoclib;
import java.util.*;

public class AvatarCrossbar extends AvatarComponent{

    private  String crossbarName;
    private int nbOfAttachedInitiators;
    private int nbOfAttachedTargets;
    private int cluster_index;
    private int cluster_address;

    public AvatarCrossbar(String _crossbarName, int _nbOfAttachedInitiators,int _nbOfAttachedTargets, int _cluster_index, int _cluster_address ){

      crossbarName =  _crossbarName;
      nbOfAttachedInitiators = _nbOfAttachedInitiators;
      nbOfAttachedTargets =  _nbOfAttachedTargets ;
      cluster_index= _cluster_index ;
      cluster_address = _cluster_address ;

    }

    public String getCrossbarName(){
	return crossbarName;
    }

    public int getNbOfAttachedInitiators(){
	return nbOfAttachedInitiators;
    }

    public int getNbOfAttachedTargets(){
	return nbOfAttachedTargets;
    }

    public int getClusterIndex(){
	return cluster_index;
    }

    public int getClusterAddress(){
	return cluster_address;
    }

    public void setNbOfAttachedInitiators(int nb){
      nbOfAttachedInitiators = nb;
    }

    public void setnbOfAttachedTargets(int nb){
      nbOfAttachedTargets = nb;
    }
 
}
