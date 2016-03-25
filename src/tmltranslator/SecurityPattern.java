package tmltranslator;

public class SecurityPattern {

public String name;
public int keySize;
public int MACSize=0;

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
