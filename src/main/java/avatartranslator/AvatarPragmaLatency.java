/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
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
 */




package avatartranslator;

/**
 * Class AvatarPragmaLatency
 * Creation: 22/09/2017
 * @version 1.0 22/09/2017
 * @author Letitia LI
 */
import java.util.List;

public class AvatarPragmaLatency extends AvatarPragma {
    private AvatarStateMachineElement state1;
    private AvatarBlock block1;
	private AvatarStateMachineElement state2;
	private AvatarBlock block2;
	private int symbolType;
	public static final int lessThan =1;
	public static final int greaterThan=2;
	public static final int query=3;
	private int time;
	private List<String> id1;
	private List<String> id2;
	private String pragmaString="";	

    public AvatarPragmaLatency(String _name, Object _referenceObject, AvatarBlock block1, AvatarStateMachineElement state1, AvatarBlock block2, AvatarStateMachineElement state2,  int symbolType, int time, List<String> id1, List<String> id2, String pragmaString)
    {
        super(_name, _referenceObject);
        this.block1 = block1;
        this.state1 = state1;
        this.block2 = block2;
        this.state2 = state2;
		this.symbolType = symbolType;
		this.time = time;
		this.id1=id1;
		this.id2=id2;
		this.pragmaString=pragmaString;
    }

    public AvatarStateMachineElement getState1()
    {
        return this.state1;
    }

    public AvatarBlock getBlock1()
    {
        return this.block1;
    }

    public AvatarStateMachineElement getState2()
    {
        return this.state2;
    }

    public AvatarBlock getBlock2()
    {
        return this.block2;
    }

	public List<String> getId1(){
		return this.id1;
	}

	public List<String> getId2(){
		return this.id2;
	}

	public int getSymbolType(){
		return this.symbolType;
	}

	public int getTime(){
		return this.time;
	}
	
	public String getPragmaString(){
		return this.pragmaString;
	}

    public String toString()
    {
		String type = this.symbolType==lessThan ? "<" : ">";
        return "Latency(" + this.block1.getName().replaceAll("__", ".") + "." + this.state1.getName() + "," + this.block2.getName().replaceAll("__", ".") + "." + this.state2.getName() +")" + type + this.time; 
    }

    @Override
    public AvatarPragmaLatency advancedClone(AvatarSpecification avspec)
    {
        // !!! Should never be called !!!
        return null;
    }
}
