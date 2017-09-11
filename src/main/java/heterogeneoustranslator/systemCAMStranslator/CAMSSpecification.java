/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * Daniela Genius, Lip6, UMR 7606 
 * 
 * ludovic.apvrille AT enst.fr
 * daniela.genius@lip6.fr
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





/* * @version 1.0 04/09/2017
   * @author  Côme Demarigny */

package heterogeneoustranslator.systemCAMStranslator;

import java.util.LinkedList;
import java.util.List;

public class CAMSSpecification{
	

    private List<CAMSBlocks> blocks;
    private List<CAMSSignal> signals;
    private List<CAMSConnection> connections;
		

    public CAMSSpecification( List<CAMSBlocks> _blocks, List<CAMSSignal> _signals, List<CAMSConnection> _connections, int _nb_target, int _nb_init){
	blocks = _blocks ;
	signals = _signals ;
	connections = _connections ;
        nb_target = _nb_target;
        nb_init = _nb_init;
    }
    
    public List<CAMSBlock> getBlock(){
	return blocks;
    }

    public List<CAMSSignals> getSignals(){
	return signals;
    }

    public List<CAMSConnection> getConnection(){
	return connections;
    }

    public List<CAMSBlocks> getUnconnectedBlocks(){
	List<CAMSBlocks> unconnectedBlocks;
	for(CAMSBlocks block : blocks){
	    for(CAMSConnections connection : connections){
		if (block == connection.getInputBlock() || block == connection.getOutputBlock()){
		    break;
		}
		else {
		    unconnectedBlocks.add(block);
		}
	    }
	}
	return unconnectedBlocks;
    }

    public list<CAMSSignals> getunconnectedSignals(){
	list<CAMSSignals> unconnectedSignals;
	return unconnectedSignals;
    }


}
