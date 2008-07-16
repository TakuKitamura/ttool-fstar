/**Copyright or  or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

 /**
 * Class TClassChannelBRBW
 * Creation: 26/10/2006
 * @version 1.0 26/10/2006
 * @author Ludovic APVRILLE
 * @see 
 */

package translator;
 

public class TClassChannelBRBW extends TClass {
  protected String channelName;
    
    public TClassChannelBRBW(String name, String _channelName) {
      super(name, true);
      channelName = _channelName;
    }

    public void makeTClass(int max) {
        //System.out.println("toto1");
        
        Gate read, write;
        ADActionStateWithGate acread, acwrite;
        ADChoice choice;
        ADActionStateWithParam adap1, adap2;   
        //ADStop adstop;
        ADJunction adj;
       
        ActivityDiagram ad = new ActivityDiagram();
        
        Param sample = new Param("samples", Param.NAT, "0");
        addParameter(sample);
        
        Param maxSample = new Param("max_samples", Param.NAT, "" + max);
        addParameter(maxSample);
        
        /*Param nb = new Param("nb", Param.NAT, "0");
        addParameter(nb);*/
        
        read = addNewGateIfApplicable("rd__" + channelName);
        write = addNewGateIfApplicable("wr__" + channelName);
        
        adj = new ADJunction();
        ad.getStartState().addNext(adj);
        ad.add(adj);
        
        choice = new ADChoice();
        choice.addGuard("[samples < max_samples]");
        choice.addGuard("[samples > 0]");
        adj.addNext(choice);
        ad.add(choice);
        
        acwrite = new ADActionStateWithGate(write);
        //acwrite.setActionValue("?nb:nat");
        acwrite.setActionValue("");
        choice.addNext(acwrite);
        ad.add(acwrite);
        
        adap1 = new ADActionStateWithParam(sample);
        adap1.setActionValue("samples + 1");
        adap1.addNext(adj);
        ad.add(adap1);
        acwrite.addNext(adap1);
        
        acread = new ADActionStateWithGate(read);
        acread.setActionValue("");
        ad.add(acread);
        choice.addNext(acread);
        
        adap2 = new ADActionStateWithParam(sample);
        adap2.setActionValue("samples - 1");
        adap2.addNext(adj);
        ad.add(adap2);
        acread.addNext(adap2);
        
        setActivityDiagram(ad);
    }
}  