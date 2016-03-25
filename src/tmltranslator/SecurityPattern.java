package tmltranslator;
import avatartranslator.*;

public class SecurityPattern {

public String name;
public int keySize;
public int MACSize=0;
public String originTask;
public AvatarState state1;
public AvatarState state2;

public SecurityPattern(String _name, String _keySize, String _MACSize){
    this.name=_name; 
    if (!_keySize.equals("")){
	this.keySize = Integer.valueOf(_keySize);
    }
    if (!_MACSize.equals("")){
	MACSize=Integer.valueOf(_MACSize);
    }
}

}
