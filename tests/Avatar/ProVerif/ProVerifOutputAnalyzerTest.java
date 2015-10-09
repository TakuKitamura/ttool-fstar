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
 * Class ProVerifOutputAnalyzer
 * Creation: 16/09/2010
 * @version 1.0 16/09/2010
 * @author Ludovic APVRILLE
 * @see
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import avatartranslator.AvatarAttribute;
import avatartranslator.AvatarBlock;
import avatartranslator.AvatarSpecification;
import avatartranslator.toproverif.AVATAR2ProVerif;
import proverifspec.ProVerifOutputAnalyzer;

public class ProVerifOutputAnalyzerTest extends TToolTest {

    public ProVerifOutputAnalyzerTest () {
        super ("ProVerif Output Analyzer", false);
    }

    protected void test () {
        BufferedReader br = null;
        try {
            String s="";
            String sCurrentLine;
            br = new BufferedReader(new FileReader("typed.txt"));
            while ((sCurrentLine = br.readLine()) != null) {
                s= s.concat(sCurrentLine+"\n");
            }
            //this.updateDigests);

            AvatarSpecification avspec = new AvatarSpecification ("dummy", null);
            AvatarBlock aliceBlock = new AvatarBlock ("Alice", avspec, null);
            aliceBlock.addAttribute (new AvatarAttribute ("sk__data", null, aliceBlock, null));
            aliceBlock.addAttribute (new AvatarAttribute ("m__data", null, aliceBlock, null));
            aliceBlock.addAttribute (new AvatarAttribute ("m1__data", null, aliceBlock, null));
            aliceBlock.addAttribute (new AvatarAttribute ("secretData", null, aliceBlock, null));
            AvatarBlock bobBlock = new AvatarBlock ("Bob", avspec, null);
            bobBlock.addAttribute (new AvatarAttribute ("m__data", null, bobBlock, null));
            bobBlock.addAttribute (new AvatarAttribute ("m2__data", null, bobBlock, null));
            bobBlock.addAttribute (new AvatarAttribute ("receivedData", null, bobBlock, null));
            avspec.addBlock (aliceBlock);
            avspec.addBlock (bobBlock);

            ProVerifOutputAnalyzer poa = new ProVerifOutputAnalyzer(
                    new AVATAR2ProVerif (avspec) {
                        public String getTrueName (AvatarAttribute attr) {
                            return AVATAR2ProVerif.translateTerm (attr, null);
                        }
                    });

            poa.analyzeOutput(s,true);


            //Test for Reachable Events
            this.updateDigest ("Reachable Events " + poa.getReachableEvents().size());
            for (String str: poa.getReachableEvents()){
                this.updateDigest (str);
            }

            this.updateDigest ("NonReachable Events " + poa.getNonReachableEvents().size());
            for (String str: poa.getNonReachableEvents()){
                this.updateDigest (str);
            }

            this.updateDigest ("Secret Terms " + poa.getSecretTerms().size());
            for (AvatarAttribute attr: poa.getSecretTerms()){
                this.updateDigest ((attr.getBlock ().getName () + "." + attr.getName ()));
            }
            
            this.updateDigest ("Non Secret Terms " + poa.getNonSecretTerms().size());
            for (AvatarAttribute attr: poa.getNonSecretTerms()){
                this.updateDigest ((attr.getBlock ().getName () + "." + attr.getName ()));
            }

            this.updateDigest ("Satisfied Authenticity " +poa.getSatisfiedAuthenticity().size());
            for (String str: poa.getSatisfiedAuthenticity()){
                this.updateDigest (str);
            }
            
            this.updateDigest ("Satisfied Weak Authenticity " +poa.getSatisfiedWeakAuthenticity().size());
            for (String str: poa.getSatisfiedWeakAuthenticity()){
                this.updateDigest (str);
            }

            this.updateDigest ("Non Satisfied Authenticity " +poa.getNonSatisfiedAuthenticity().size());
            for (String str: poa.getNonSatisfiedAuthenticity()){
                this.updateDigest (str);
            }

            this.updateDigest ("Errors " +poa.getErrors().size());
            for (String str: poa.getErrors()){
                this.updateDigest (str);
            }

            this.updateDigest ("Not proved " +poa.getNotProved().size());	
            for (String str: poa.getNotProved()){
                this.updateDigest (str);
            }

            if (!this.testDigest (new byte[] {-126, -51, 48, -20, -15, -101, 107, 0, -46, -110, -80, -56, -4, -120, 37, -47, -20, -55, 114, 111}))
                this.error ("Couldn't analyze ProVerif typed output...");

            s = "";
            br = new BufferedReader(new FileReader("untyped.txt"));
            while ((sCurrentLine = br.readLine()) != null) {
                s= s.concat(sCurrentLine+"\n");
            }

            this.updateDigest ("__________________________________");

            poa = new ProVerifOutputAnalyzer(
                    new AVATAR2ProVerif (avspec) {
                        public String getTrueName (AvatarAttribute attr) {
                            return AVATAR2ProVerif.translateTerm (attr, null);
                        }
                    });
            poa.analyzeOutput(s,false);

            this.updateDigest ("Reachable Events " + poa.getReachableEvents().size());
            for (String str: poa.getReachableEvents()){
                this.updateDigest (str);
            }

            this.updateDigest ("NonReachable Events " + poa.getNonReachableEvents().size());
            for (String str: poa.getNonReachableEvents()){
                this.updateDigest (str);
            }

            this.updateDigest ("Secret Terms " + poa.getSecretTerms().size());
            for (AvatarAttribute attr: poa.getSecretTerms()){
                this.updateDigest ((attr.getBlock ().getName () + "." + attr.getName ()));
            }

            this.updateDigest ("Non Secret Terms " + poa.getNonSecretTerms().size());
            for (AvatarAttribute attr: poa.getNonSecretTerms()){
                this.updateDigest ((attr.getBlock ().getName () + "." + attr.getName ()));
            }

            this.updateDigest ("Satisfied Authenticity " +poa.getSatisfiedAuthenticity().size());
            for (String str: poa.getSatisfiedAuthenticity()){
                this.updateDigest (str);
            }

            this.updateDigest ("Satisfied Weak Authenticity " +poa.getSatisfiedWeakAuthenticity().size());
            for (String str: poa.getSatisfiedWeakAuthenticity()){
                this.updateDigest (str);
            }

            this.updateDigest ("Non Satisfied Authenticity " +poa.getNonSatisfiedAuthenticity().size());
            for (String str: poa.getNonSatisfiedAuthenticity()){
                this.updateDigest (str);
            }

            this.updateDigest ("Errors " +poa.getErrors().size());
            for (String str: poa.getErrors()){
                this.updateDigest (str);
            }

            this.updateDigest ("Not proved " +poa.getNotProved().size());	
            for (String str: poa.getNotProved()){
                this.updateDigest (str);
            }

            if (!this.testDigest (new byte[] {103, 89, 62, 106, -11, 41, -54, -64, -24, 74, 109, -93, 77, -21, 72, -62, 117, -41, -114, 46}))
                this.error ("Couldn't analyze ProVerif untyped output...");

        } catch (IOException e) {
            this.error (e.toString ());
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                this.error (ex.toString ());
            }
        }
    }

    public static void main(String[] args){
        ProVerifOutputAnalyzerTest pvoat = new ProVerifOutputAnalyzerTest ();
        pvoat.runTest ();
    }
}
