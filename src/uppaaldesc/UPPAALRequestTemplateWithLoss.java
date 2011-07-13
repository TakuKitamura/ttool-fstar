/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class UPPAALRequestTemplateWithLoss
 * Creation: 13/07/2011
 * @version 1.0 13/07/2011
 * @author Ludovic APVRILLE
 * @see
 */

package uppaaldesc;

import java.awt.Point;
import tmltranslator.*;


public class UPPAALRequestTemplateWithLoss extends  UPPAALTemplate{
	
	protected UPPAALLocation lossLocation;
	protected UPPAALLocation lossOccuredLocation;

    public UPPAALRequestTemplateWithLoss(String name, TMLRequest request, String defaultSizeValue, int maxNbOfLoss) {
           super();
           
           int i;
           setName(name);
           declaration = "const int maxR = " + defaultSizeValue + ";\nint size = 0;\nint head = 0;\nint tail = 0;\n";
           for(i=0;i<request.getNbOfParams(); i++) {
             declaration += request.getType(i).toString() + " listR" + i + "[maxR];\n\n";
           }

           // Function code: enqueueR
           declaration += "void enqueueR(){\n";
           for(i=0;i<request.getNbOfParams(); i++) {
            declaration += "  listR" + i + "[tail] = tail" + i + "__" + request.getName() + ";\n";
           }
           declaration += "  tail = (tail+1)%maxR;\n  size ++;\n}\n\n";

           // Function code: dequeueR
           declaration += "void dequeueR(){\n";
           for(i=0;i<request.getNbOfParams(); i++) {
             declaration += "  head" + i + "__" + request.getName() + " = listR" + i + "[head];\n";
           }
           declaration += "  head = (head+1)%maxR;\n  size --;\n}\n\n";
		   
		   if (maxNbOfLoss > -1) {
			declaration += "  int nbOfLoss__;\n";
		   }

           // Main state
           initLocation = new UPPAALLocation();
           initLocation.idPoint = new Point(-64, -80);
           initLocation.namePoint = new Point(-80, -56);
           initLocation.name = "main_state";
           locations.add(initLocation);
		   
		   // Loss locations
		lossLocation = new UPPAALLocation();
		lossLocation.idPoint = new Point(-104, -232);
		lossLocation.namePoint = new Point(-160, -272);
		lossLocation.name = "loss_or_not_loss";
		lossLocation.setCommitted();
		locations.add(lossLocation);
		
		lossOccuredLocation = new UPPAALLocation();
		lossOccuredLocation.idPoint = new Point(-104, -176);
		lossOccuredLocation.namePoint = new Point(-144, -160);
		lossOccuredLocation.name = "loss_occured";
		lossOccuredLocation.setCommitted();
		locations.add(lossOccuredLocation);

           // Transition for getting request
           UPPAALTransition tr = new UPPAALTransition();
           tr.sourceLoc = initLocation;
           tr.destinationLoc = lossLocation;
           tr.guard = "size < maxR";
           tr.guardPoint = new Point(-336, -112);
           tr.synchronization = "request__" +  request.getName() + "?";
           tr.synchronizationPoint = new Point(-280, -88);
           //tr.assignment = "enqueueR()";
           //tr.assignmentPoint = new Point(-304, -64);
           tr.points.add(new Point(-208, -232));
           tr.points.add(new Point(-208, 104));
           transitions.add(tr);
           
           // Transition for releasing request
           tr = new UPPAALTransition();
           tr.sourceLoc = initLocation;
           tr.destinationLoc = initLocation;
           tr.guard = "size>0";
           tr.guardPoint = new Point(16, -112);
           tr.synchronization = "wait__" + request.getName() + "!";
           tr.synchronizationPoint = new Point(40, -88);
           tr.assignment = "dequeueR()";
           tr.assignmentPoint = new Point(24, -72);
           tr.points.add(new Point(64, 80));
           tr.points.add(new Point(64, -232));
           transitions.add(tr);
		   
		  // Handling Loss
		// loss
		tr = new UPPAALTransition();
		tr.sourceLoc = lossLocation;
		tr.destinationLoc = lossOccuredLocation;
		tr.synchronization = "req__" + request.getName() + "__loss!";
		if (maxNbOfLoss > -1) {
			tr.guard = " nbOfLoss__ < " + maxNbOfLoss;
		}
		tr.assignment = "nbOfLoss__ = nbOfLoss__ + 1";
		tr.points.add(new Point(-56, -176));
		transitions.add(tr);
		
		tr = new UPPAALTransition();
		tr.sourceLoc = lossOccuredLocation;
		tr.destinationLoc = initLocation;
		tr.points.add(new Point(-176, -136));
		transitions.add(tr);
		
		// no loss
		tr = new UPPAALTransition();
		tr.sourceLoc = lossLocation;
		tr.destinationLoc = initLocation;
		tr.guard = "size < maxR";
		tr.assignment = "enqueueR()";
		tr.synchronization = "req__" + request.getName() + "__noloss!";
		tr.assignmentPoint = new Point(-64, -240);
		tr.points.add(new Point(-16, -200));
		transitions.add(tr);
    }


}