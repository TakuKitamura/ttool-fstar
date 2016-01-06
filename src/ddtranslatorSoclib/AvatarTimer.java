/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */


package ddtranslatorSoclib;
import java.util.*;

public class AvatarTimer extends AvatarComponent{

    private String timerName;
    private int index;
    private int nb_irq;

    public AvatarTimer(String _timerName, int _index, int _nb_irq ){
      
      timerName = _timerName;
      index = _index;
      nb_irq = _nb_irq;
    }

    String getTimerName(){
	return timerName;
    }

    int getIndex(){
	return index;
    }

    int nb_irq(){
	return nb_irq;
    }

}
