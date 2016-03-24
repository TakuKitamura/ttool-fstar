/*Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Daniel Knorreck,
Ludovic Apvrille, Renaud Pacalet
 *
 * ludovic.apvrille AT telecom-paristech.fr
 *
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 */

#ifndef JenkinsHashH
#define JenkinsHashH

#define rot(x,k) (((x)<<(k)) | ((x)>>(32-(k))))

#define imix(a,b,c) \
{ \
  b -= a;  c ^= rot(b, 4);  c += b; \
  a -= c;  b ^= rot(a, 19);  b += a; \
  c -= b;  a ^= rot(c, 16);  a += c; \
  b -= a;  c ^= rot(b,8);  c += b; \
  a -= c;  b ^= rot(a,6);  b += a; \
  c -= b;  a ^= rot(c, 4);  a += c; \
}

#define mix(a,b,c) \
{ \
  a -= c;  a ^= rot(c, 4);  c += b; \
  b -= a;  b ^= rot(a, 6);  a += c; \
  c -= b;  c ^= rot(b, 8);  b += a; \
  a -= c;  a ^= rot(c,16);  c += b; \
  b -= a;  b ^= rot(a,19);  a += c; \
  c -= b;  c ^= rot(b, 4);  b += a; \
}

#define ifinal(a,b,c) \
{ \
  b += rot(a,14); b ^= a;\
  a += rot(c,4);  a ^= c;\
  c += rot(b,16); c ^= b;\
  b += rot(a,25); b ^= a;\
  a += rot(c,11); a ^= c;\
  c += rot(b,14); c ^= b;\
}

#define final(a,b,c) \
{ \
  c ^= b; c -= rot(b,14); \
  a ^= c; a -= rot(c,11); \
  b ^= a; b -= rot(a,25); \
  c ^= b; c -= rot(b,16); \
  a ^= c; a -= rot(c,4);  \
  b ^= a; b -= rot(a,14); \
  c ^= b; c -= rot(b,24); \
}

///Class which encapsulates a comment concerning the control flow or task execution
class HashAlgo{
public:	
	
	HashAlgo()/*: _bufferSize(0), _buffer(0), _bufferPos(0)*/{
		init(0,0);
	}
	
	HashAlgo(HashValueType iInitVal, HashValueType iLength /*, iBufferSize=0*/)/*: _bufferSize(iBufferSize), _buffer(0), _bufferPos(0)*/{
		init(iInitVal, iLength);
		//if (_bufferSize!=0) _buffer = new HashValueType[_bufferSize];
		//memset(_buffer, 0, _bufferSize*sizeof(HashValueType));
	}
	
	~HashAlgo(){
		//if (_bufferSize!=0) delete [] _buffer;
	}
	
	void init(HashValueType iInitVal, HashValueType iLength){
		_a = _b = _c = 0xdeadbeef + (((HashValueType)iLength)<<2) + iInitVal;
		_state = 0;
		_finalized = false;
	}
	
	void addValue(HashValueType iVal){
		//std::cout << "++++++++++++++++++++ Added value: " << iVal << std::endl;
		_finalized = false;
		switch(_state){
		  case 0: _a+= iVal; break;
		  case 1: _b+= iVal; break;
		  case 2: _c+= iVal; mix(_a,_b,_c);
		}
		_state = (_state==2)?0 : _state+1;
	}
	
	/*void removeValue(HashValueType iVal){
		if(_finalized){
  			ifinal(_a, _b, _c);
			_finalized=false;
		}
		switch(_state){
			case 1: _a-= iVal; break;
			case 2: _b-= iVal; break;
			case 0: imix(_a,_b,_c); _c-= iVal; 
		 }
		_state = (_state==0)?2 : _state-1;
	}*/
	
	HashValueType getHash() const{
		if (!_finalized){
			_result = _c;
			if(_state!=0){
				HashValueType aA = _a, aB=_b;
				final(aA, aB, _result);
				_finalized=true;
			}
		}
		return _result;
	}

  protected:
	HashValueType _a,_b,_c;
	mutable HashValueType _result;
	char _state;
	mutable bool _finalized;
	//unsigned int _bufferSize;
	//HashValueType * _buffer;
	//unsigned int _bufferPos;
};
#endif
