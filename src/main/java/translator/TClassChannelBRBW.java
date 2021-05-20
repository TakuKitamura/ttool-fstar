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

package translator;

/**
 * Class TClassChannelBRBW Creation: 26/10/2006
 * 
 * @version 1.0 26/10/2006
 * @author Ludovic APVRILLE
 */
public class TClassChannelBRBW extends TClass {
  protected String channelName;

  public TClassChannelBRBW(String name, String _channelName) {
    super(name, true);
    channelName = _channelName;
  }

  public void makeTClass(int max, boolean _lossy, int _percentage, int _maxNbOfLoss) {
    //

    Gate read, write, loss = null, notloss = null;
    ADActionStateWithGate acread, acwrite, aclost, acnotlost;
    ADChoice choice, choiceLoss, choiceLossPercentage, choiceLoss100;
    ADActionStateWithParam adap1, adap2;
    // ADStop adstop;
    ADJunction adj, adj1, adj2;

    ActivityDiagram ad = new ActivityDiagram();

    Param sample = new Param("samples", Param.NAT, "0");
    addParameter(sample);

    Param maxSample = new Param("max_samples", Param.NAT, "" + max);
    addParameter(maxSample);

    Param pmax = null;
    Param currentLoss = null;

    if ((_lossy) && (_maxNbOfLoss > -1)) {
      pmax = new Param("maxLoss", Param.NAT, "" + _maxNbOfLoss);
      addParameter(pmax);
      currentLoss = new Param("currentLoss", Param.NAT, "0");
      addParameter(currentLoss);
    }

    /*
     * Param nb = new Param("nb", Param.NAT, "0"); addParameter(nb);
     */

    read = addNewGateIfApplicable("rd__" + channelName);
    write = addNewGateIfApplicable("wr__" + channelName);

    if (_lossy) {
      loss = addNewGateIfApplicable("msglost__" + channelName);
      notloss = addNewGateIfApplicable("msgNotLost__" + channelName);
    }

    adj = new ADJunction();
    ad.getStartState().addNext(adj);
    ad.add(adj);

    choice = new ADChoice();
    choice.addGuard("[samples < max_samples]");
    choice.addGuard("[samples > 0]");
    adj.addNext(choice);
    ad.add(choice);

    acwrite = new ADActionStateWithGate(write);
    // acwrite.setActionValue("?nb:nat");
    acwrite.setActionValue("");
    choice.addNext(acwrite);
    ad.add(acwrite);

    /*
     * adap1 = new ADActionStateWithParam(sample);
     * adap1.setActionValue("samples + 1"); adap1.addNext(adj); ad.add(adap1);
     * acwrite.addNext(adap1);
     */

    if (_lossy) {
      if (_maxNbOfLoss > -1) {
        adj1 = new ADJunction();
        adj2 = new ADJunction();
        choiceLossPercentage = new ADChoice();
        choiceLoss = new ADChoice();
        choiceLoss100 = new ADChoice();

        choiceLossPercentage.addGuard("[" + _percentage + " < 100]");
        choiceLossPercentage.addGuard("[" + _percentage + " > 99]");
        choiceLossPercentage.addNext(choiceLoss);
        choiceLossPercentage.addNext(choiceLoss100);

        choiceLoss100.addGuard("[currentLoss < maxLoss]");
        choiceLoss100.addGuard("[not(currentLoss < maxLoss)]");
        choiceLoss100.addNext(adj1);
        choiceLoss100.addNext(adj2);

        choiceLoss.addGuard("[]");
        choiceLoss.addGuard("[currentLoss < maxLoss]");
        choiceLoss.addNext(adj2);
        choiceLoss.addNext(adj1);

        acwrite.addNext(choiceLossPercentage);

        acnotlost = new ADActionStateWithGate(notloss);
        acnotlost.setActionValue("");
        ad.add(acnotlost);
        adj2.addNext(acnotlost);

        aclost = new ADActionStateWithGate(loss);
        aclost.setActionValue("");
        ad.add(aclost);
        adap1 = new ADActionStateWithParam(sample);
        adap1.setActionValue("samples + 1");

        adj1.addNext(aclost);

        adap2 = new ADActionStateWithParam(currentLoss);
        adap2.setActionValue("currentLoss + 1");
        adap2.addNext(adj);
        aclost.addNext(adap2);

        acnotlost.addNext(adap1);
        adap1.addNext(adj);

        ad.add(adj1);
        ad.add(adj2);
        ad.add(choiceLoss);
        ad.add(choiceLossPercentage);
        ad.add(choiceLoss100);
        ad.add(adap1);
        ad.add(adap2);
        ad.add(aclost);
        ad.add(acnotlost);
      } else {
        choiceLoss = new ADChoice();
        choiceLoss.addGuard("[ " + _percentage + " < 100]");
        choiceLoss.addGuard("[ ]");
        acwrite.addNext(choiceLoss);

        acnotlost = new ADActionStateWithGate(notloss);
        acnotlost.setActionValue("");
        ad.add(acnotlost);
        choiceLoss.addNext(acnotlost);

        aclost = new ADActionStateWithGate(loss);
        aclost.setActionValue("");
        ad.add(aclost);

        choiceLoss.addNext(aclost);
        aclost.addNext(adj);

        adap1 = new ADActionStateWithParam(sample);
        adap1.setActionValue("samples + 1");
        acnotlost.addNext(adap1);
        adap1.addNext(adj);

        ad.add(choiceLoss);
        ad.add(adap1);
        ad.add(aclost);
        ad.add(acnotlost);
      }
    } else {
      adap1 = new ADActionStateWithParam(sample);
      adap1.setActionValue("samples + 1");
      adap1.addNext(adj);
      ad.add(adap1);
      acwrite.addNext(adap1);
    }

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