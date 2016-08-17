package tmltranslator;
import avatartranslator.*;

public class SecurityPattern {

public String name;
public String type;
public int overhead=0;
public int size=0;
public int encTime=0;
public int decTime=0;
public String originTask;
public AvatarState state1;
public AvatarState state2;
public String nonce;
public String formula;
public String key;
public SecurityPattern(String _name, String _type, String _overhead, String _size, String _enctime, String _dectime, String _nonce, String _formula, String _key){
    this.name=_name; 
    this.type=_type;
    this.nonce=_nonce;
    this.formula=_formula;
    this.key=_key;
    if (!_overhead.equals("")){
	this.overhead = Integer.valueOf(_overhead);
    }
    if (!_size.equals("")){
	this.size = Integer.valueOf(_size);
    }
    if (!_dectime.equals("")){
	this.decTime=Integer.valueOf(_dectime);
    }
    if (!_enctime.equals("")){
	this.encTime=Integer.valueOf(_enctime);
    }
}

}
