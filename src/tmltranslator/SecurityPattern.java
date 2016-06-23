package tmltranslator;
import avatartranslator.*;

public class SecurityPattern {

public String name;
public String type;
public int overhead=0;
public int size=0;
public int time=0;
public String originTask;
public AvatarState state1;
public AvatarState state2;
public String nonce;
public SecurityPattern(String _name, String _type, String _overhead, String _size, String _time, String _nonce){
    this.name=_name; 
    this.type=_type;
    this.nonce=_nonce;
    if (!_overhead.equals("")){
	this.overhead = Integer.valueOf(_overhead);
    }
    if (!_size.equals("")){
	this.size = Integer.valueOf(_size);
    }
    if (!_time.equals("")){
	this.time=Integer.valueOf(_time);
    }
}

}
