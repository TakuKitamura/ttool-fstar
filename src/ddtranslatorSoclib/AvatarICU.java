/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */


package ddtranslatorSoclib;
import java.util.*;

public class AvatarICU extends AvatarComponent{
 
    private String ICUName ; 
    private  int index ;
    private int nbIRQ;
    
    public AvatarICU(String _ICUName,int _index, int _nbIRQ){

      ICUName = _ICUName;
      index = _index;
      nbIRQ = _nbIRQ;

    }
	String	getICUName(){
	return ICUName;
	}

	int getIndex(){
	return index;
	}

	int getNbIRQ(){
	return nbIRQ;
	}

}
 
