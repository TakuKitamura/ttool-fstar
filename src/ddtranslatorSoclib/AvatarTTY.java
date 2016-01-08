/* * @version 1.0 07/07/2015
   * @author  Julien Henon, Daniela Genius */


package ddtranslatorSoclib;
import java.util.*;
import ddtranslatorSoclib.*;

public class AvatarTTY extends AvatarComponent {	

    private int index = 0;
    private String ttyName;
    private int no_tty;
    private int no_target;
	
    public AvatarTTY(String _ttyName,int _index, int _no_tty){
      index =  _index;
      ttyName =  _ttyName;
      no_tty = _no_tty;
    }

    public String getTTYName(){
      return ttyName;
    }
    
    public int getIndex(){
      return index;
    }

    public int getNo_tty(){
      return no_tty;
    }

    public int getNo_target(){
      return no_target;
    }

    public void setNo_tty(int _no_tty){
        no_tty = _no_tty;
    }
    
    public void setNo_target(int _no_target){
	no_target = _no_target;
    }

}

