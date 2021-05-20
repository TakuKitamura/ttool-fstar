package ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import myutil.Conversion;
import myutil.TraceManager;
import tmltranslator.SecurityPattern;
import tmltranslator.TMLActionState;
import tmltranslator.TMLActivity;
import tmltranslator.TMLActivityElement;
import tmltranslator.TMLAttribute;
import tmltranslator.TMLChannel;
import tmltranslator.TMLCheckingError;
import tmltranslator.TMLChoice;
import tmltranslator.TMLDelay;
import tmltranslator.TMLEvent;
import tmltranslator.TMLExecC;
import tmltranslator.TMLExecCInterval;
import tmltranslator.TMLExecI;
import tmltranslator.TMLExecIInterval;
import tmltranslator.TMLForLoop;
import tmltranslator.TMLJunction;
import tmltranslator.TMLModeling;
import tmltranslator.TMLNotifiedEvent;
import tmltranslator.TMLRandom;
import tmltranslator.TMLRandomSequence;
import tmltranslator.TMLReadChannel;
import tmltranslator.TMLRequest;
import tmltranslator.TMLSelectEvt;
import tmltranslator.TMLSendEvent;
import tmltranslator.TMLSendRequest;
import tmltranslator.TMLSequence;
import tmltranslator.TMLStartState;
import tmltranslator.TMLStopState;
import tmltranslator.TMLTask;
import tmltranslator.TMLType;
import tmltranslator.TMLWaitEvent;
import tmltranslator.TMLWriteChannel;
import translator.CheckingError;
import ui.ad.TADExec;
import ui.tmlad.TMLADActionState;
import ui.tmlad.TMLADChoice;
import ui.tmlad.TMLADDecrypt;
import ui.tmlad.TMLADDelay;
import ui.tmlad.TMLADDelayInterval;
import ui.tmlad.TMLADEncrypt;
import ui.tmlad.TMLADExecC;
import ui.tmlad.TMLADExecCInterval;
import ui.tmlad.TMLADExecI;
import ui.tmlad.TMLADExecIInterval;
import ui.tmlad.TMLADForEverLoop;
import ui.tmlad.TMLADForLoop;
import ui.tmlad.TMLADForStaticLoop;
import ui.tmlad.TMLADNotifiedEvent;
import ui.tmlad.TMLADRandom;
import ui.tmlad.TMLADReadChannel;
import ui.tmlad.TMLADReadRequestArg;
import ui.tmlad.TMLADSelectEvt;
import ui.tmlad.TMLADSendEvent;
import ui.tmlad.TMLADSendRequest;
import ui.tmlad.TMLADSequence;
import ui.tmlad.TMLADStartState;
import ui.tmlad.TMLADStopState;
import ui.tmlad.TMLADUnorderedSequence;
import ui.tmlad.TMLADWaitEvent;
import ui.tmlad.TMLADWriteChannel;
import ui.tmlad.TMLActivityDiagramPanel;

public class ActivityDiagram2TMLTranslator {

  public static final ActivityDiagram2TMLTranslator INSTANCE = new ActivityDiagram2TMLTranslator();

  private ActivityDiagram2TMLTranslator() {
  }

  public void generateTaskActivityDiagrams(final TMLTask tmltask, final List<CheckingError> checkingErrors,
      final List<CheckingError> warnings, final CorrespondanceTGElement corrTgElement,
      final TMLModeling<TGComponent> tmlm, final Map<String, SecurityPattern> securityPatterns,
      final Map<String, String> table, final List<String> removedChannels, final List<String> removedEvents,
      final List<String> removedRequests, final boolean considerTimeOperators) throws MalformedTMLDesignException {

    // TraceManager.addDev("*********************** Consider time operators: " +
    // considerTimeOperators);

    TMLActivity activity = tmltask.getActivityDiagram();
    TMLActivityDiagramPanel tadp = (TMLActivityDiagramPanel) (activity.getReferenceObject());

    // TraceManager.addDev( "Generating activity diagram of: " + tmltask.getName());

    // search for start state
    List<TGComponent> list = tadp.getComponentList();
    Iterator<TGComponent> iterator = list.listIterator();
    TGComponent tgc;
    TMLADStartState tss = null;
    int cptStart = 0;
    // boolean rndAdded = false;

    while (iterator.hasNext()) {
      tgc = iterator.next();

      if (tgc instanceof TMLADStartState) {
        tss = (TMLADStartState) tgc;
        cptStart++;
      }
    }

    if (tss == null) {
      TMLCheckingError ce = new TMLCheckingError(CheckingError.BEHAVIOR_ERROR,
          "No start state in the TML activity diagram of " + tmltask.getName());
      ce.setTMLTask(tmltask);
      checkingErrors.add(ce);
      return;
    }

    if (cptStart > 1) {
      TMLCheckingError ce = new TMLCheckingError(CheckingError.BEHAVIOR_ERROR,
          "More than one start state in the TML activity diagram of " + tmltask.getName());
      ce.setTMLTask(tmltask);
      checkingErrors.add(ce);
      return;
    }

    // Adding start state
    TMLStartState tmlss = new TMLStartState("start", tss);
    corrTgElement.addCor(tmlss, tss);
    activity.setFirst(tmlss);

    // Creation of other elements
    TMLChannel channel;
    String[] channels;
    TMLEvent event;
    TMLRequest request;

    TMLADRandom tmladrandom;
    TMLRandom tmlrandom;
    TMLActionState tmlaction;
    TMLChoice tmlchoice;
    TMLExecI tmlexeci;
    TMLExecIInterval tmlexecii;
    TMLExecC tmlexecc;
    TMLExecCInterval tmlexecci;
    TMLForLoop tmlforloop;
    TMLReadChannel tmlreadchannel;
    TMLSendEvent tmlsendevent;
    TMLSendRequest tmlsendrequest;
    TMLStopState tmlstopstate;
    TMLWaitEvent tmlwaitevent;
    TMLNotifiedEvent tmlnotifiedevent;
    TMLWriteChannel tmlwritechannel;
    TMLSequence tmlsequence;
    TMLRandomSequence tmlrsequence;
    TMLSelectEvt tmlselectevt;
    TMLDelay tmldelay;
    int staticLoopIndex = 0;
    String sl = "", tmp;
    TMLType tt;
    TMLAttribute tmlt;

    iterator = list.listIterator();
    while (iterator.hasNext()) {
      tgc = iterator.next();
      // Issue #69: Manage component enablement
      if (tgc.isEnabled()) {
        if (tgc.getCheckLatency()) {
          String name = tmltask.getName() + ":" + tgc.getName();
          name = name.replaceAll(" ", "");
          // TraceManager.addDev("To check " + name);
          if (tgc.getValue().contains("(")) {
            tmlm.addCheckedActivity(tgc, name + ":" + tgc.getValue().split("\\(")[0]);
          } else {
            if (tgc instanceof TMLADExecI) {
              tmlm.addCheckedActivity(tgc, ((TMLADExecI) tgc).getDelayValue());
            }
          }
        }
        if (tgc instanceof TMLADActionState) {
          tmlaction = new TMLActionState("action", tgc);
          tmp = ((TMLADActionState) (tgc)).getAction();
          tmp = modifyActionString(tmp);
          tmlaction.setAction(tmp);
          activity.addElement(tmlaction);
          ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
          corrTgElement.addCor(tmlaction, tgc);

        } else if (tgc instanceof TMLADRandom) {
          tmladrandom = (TMLADRandom) tgc;
          tmlrandom = new TMLRandom("random" + tmladrandom.getValue(), tgc);
          tmp = tmladrandom.getVariable();
          tmp = modifyActionString(tmp);
          tmlrandom.setVariable(tmp);
          tmp = tmladrandom.getMinValue();
          tmp = modifyActionString(tmp);
          tmlrandom.setMinValue(tmp);
          tmp = tmladrandom.getMaxValue();
          tmp = modifyActionString(tmp);
          tmlrandom.setMaxValue(tmp);
          tmlrandom.setFunctionId(tmladrandom.getFunctionId());
          activity.addElement(tmlrandom);
          ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
          corrTgElement.addCor(tmlrandom, tgc);

        } else if (tgc instanceof TMLADChoice) {
          tmlchoice = new TMLChoice("choice", tgc);
          // Guards are added at the same time as next activities
          activity.addElement(tmlchoice);
          ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
          corrTgElement.addCor(tmlchoice, tgc);

        } else if (tgc instanceof TMLADSelectEvt) {
          tmlselectevt = new TMLSelectEvt("select", tgc);
          activity.addElement(tmlselectevt);
          ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
          corrTgElement.addCor(tmlselectevt, tgc);

        } else if (tgc instanceof TMLADExecI) {
          tmlexeci = new TMLExecI("execi", tgc);
          if (considerTimeOperators) {
            tmlexeci.setAction(modifyString(((TADExec) tgc).getDelayValue()));
            tmlexeci.setValue(((TADExec) tgc).getDelayValue());
          } else {
            tmlexeci.setAction("0");
            tmlexeci.setValue("0");
          }
          activity.addElement(tmlexeci);
          ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
          corrTgElement.addCor(tmlexeci, tgc);

        } else if (tgc instanceof TMLADExecIInterval) {
          tmlexecii = new TMLExecIInterval("execi", tgc);
          tmlexecii.setValue(tgc.getValue());
          if (considerTimeOperators) {
            tmlexecii.setMinDelay(modifyString(((TMLADExecIInterval) tgc).getMinDelayValue()));
            tmlexecii.setMaxDelay(modifyString(((TMLADExecIInterval) tgc).getMaxDelayValue()));
          } else {
            tmlexecii.setMinDelay("0");
            tmlexecii.setMaxDelay("0");
          }
          activity.addElement(tmlexecii);
          ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
          corrTgElement.addCor(tmlexecii, tgc);

        } else if (tgc instanceof TMLADEncrypt) {
          tmlexecc = new TMLExecC("encrypt_" + ((TMLADEncrypt) tgc).securityContext, tgc);
          activity.addElement(tmlexecc);
          SecurityPattern sp = securityPatterns.get(((TMLADEncrypt) tgc).securityContext);
          if (sp == null) {
            // Throw error for missing security pattern
            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR,
                "Security Pattern " + ((TMLADEncrypt) tgc).securityContext + " not found");
            ce.setTDiagramPanel(tadp);
            ce.setTGComponent(tgc);
            checkingErrors.add(ce);
          } else {
            tmlexecc.securityPattern = sp;
            tmlexecc.setAction(Integer.toString(sp.encTime));
            ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
            tmlm.securityTaskMap.get(sp).add(tmltask);
            corrTgElement.addCor(tmlexecc, tgc);
          }
        } else if (tgc instanceof TMLADDecrypt) {
          tmlexecc = new TMLExecC("decrypt_" + ((TMLADDecrypt) tgc).securityContext, tgc);
          activity.addElement(tmlexecc);
          SecurityPattern sp = securityPatterns.get(((TMLADDecrypt) tgc).securityContext);
          if (sp == null) {
            // Throw error for missing security pattern
            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR,
                "Security Pattern " + ((TMLADDecrypt) tgc).securityContext + " not found");
            ce.setTDiagramPanel(tadp);
            ce.setTGComponent(tgc);
            checkingErrors.add(ce);
          } else {
            tmlexecc.securityPattern = sp;
            tmlexecc.setAction(Integer.toString(sp.decTime));
            ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
            corrTgElement.addCor(tmlexecc, tgc);
            tmlm.securityTaskMap.get(sp).add(tmltask);
          }

        } else if (tgc instanceof TMLADExecC) {
          tmlexecc = new TMLExecC("execc", tgc);
          if (considerTimeOperators) {
            tmlexecc.setValue(((TMLADExecC) tgc).getDelayValue());
            tmlexecc.setAction(modifyString(((TMLADExecC) tgc).getDelayValue()));
          } else {
            tmlexecc.setValue("0");
            tmlexecc.setAction("0");
          }

          activity.addElement(tmlexecc);
          ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
          corrTgElement.addCor(tmlexecc, tgc);

        } else if (tgc instanceof TMLADExecCInterval) {
          tmlexecci = new TMLExecCInterval("execci", tgc);
          if (considerTimeOperators) {
            tmlexecci.setMinDelay(modifyString(((TMLADExecCInterval) tgc).getMinDelayValue()));
            tmlexecci.setMaxDelay(modifyString(((TMLADExecCInterval) tgc).getMaxDelayValue()));
          } else {
            tmlexecci.setMinDelay("0");
            tmlexecci.setMaxDelay("0");
          }
          activity.addElement(tmlexecci);
          ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
          corrTgElement.addCor(tmlexecci, tgc);

        } else if (tgc instanceof TMLADDelay) {
          tmldelay = new TMLDelay("d-delay", tgc);
          if (considerTimeOperators) {
            tmldelay.setMinDelay(modifyString(((TMLADDelay) tgc).getDelayValue()));
            tmldelay.setMaxDelay(modifyString(((TMLADDelay) tgc).getDelayValue()));
          } else {
            tmldelay.setMinDelay("0");
            tmldelay.setMaxDelay("0");
          }
          tmldelay.setUnit(((TMLADDelay) tgc).getUnit());
          tmldelay.setActiveDelay(((TMLADDelay) tgc).getActiveDelayEnable());
          activity.addElement(tmldelay);
          ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
          corrTgElement.addCor(tmldelay, tgc);

        } else if (tgc instanceof TMLADDelayInterval) {
          tmldelay = new TMLDelay("nd-delay", tgc);
          if (considerTimeOperators) {
            tmldelay.setMinDelay(modifyString(((TMLADDelayInterval) tgc).getMinDelayValue()));
            tmldelay.setMaxDelay(modifyString(((TMLADDelayInterval) tgc).getMaxDelayValue()));
          } else {
            tmldelay.setMinDelay("0");
            tmldelay.setMaxDelay("0");
          }
          tmldelay.setUnit(((TMLADDelayInterval) tgc).getUnit());
          tmldelay.setActiveDelay(((TMLADDelayInterval) tgc).getActiveDelayEnableValue());
          activity.addElement(tmldelay);
          ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
          corrTgElement.addCor(tmldelay, tgc);

        } else if (tgc instanceof TMLADForLoop) {
          tmlforloop = new TMLForLoop("loop", tgc);
          tmlforloop.setInit(modifyString(((TMLADForLoop) tgc).getInit()));
          tmp = ((TMLADForLoop) tgc).getCondition();
          /*
           * if (tmp.trim().length() == 0) { tmp = "true"; }
           */
          tmlforloop.setCondition(modifyString(tmp));
          tmlforloop.setIncrement(modifyActionString(((TMLADForLoop) tgc).getIncrement()));

          activity.addElement(tmlforloop);
          ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
          corrTgElement.addCor(tmlforloop, tgc);

        } else if (tgc instanceof TMLADForStaticLoop) {
          sl = "loop__" + staticLoopIndex;
          tt = new TMLType(TMLType.NATURAL);
          tmlt = new TMLAttribute(sl, tt);
          tmlt.initialValue = "0";
          tmltask.addAttribute(tmlt);
          tmlforloop = new TMLForLoop(sl, tgc);
          tmlforloop.setInit(sl + " = 0");
          tmlforloop.setCondition(sl + "<" + modifyString(tgc.getValue()));
          tmlforloop.setIncrement(sl + " = " + sl + " + 1");
          activity.addElement(tmlforloop);
          ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
          corrTgElement.addCor(tmlforloop, tgc);
          staticLoopIndex++;

        } else if (tgc instanceof TMLADForEverLoop) {
          /*
           * sl = "loop__" + staticLoopIndex; tt = new TMLType(TMLType.NATURAL); tmlt =
           * new TMLAttribute(sl, tt); tmlt.initialValue = "0";
           * tmltask.addAttribute(tmlt);
           */
          tmlforloop = new TMLForLoop("infiniteloop", tgc);
          tmlforloop.setInit("");
          tmlforloop.setCondition("");
          tmlforloop.setIncrement("");
          tmlforloop.setInfinite(true);
          activity.addElement(tmlforloop);
          ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
          corrTgElement.addCor(tmlforloop, tgc);
          staticLoopIndex++;

          tmlstopstate = new TMLStopState("Stop after infinite loop", null);
          activity.addElement(tmlstopstate);
          tmlforloop.addNext(tmlstopstate);

        } else if (tgc instanceof TMLADSequence) {
          tmlsequence = new TMLSequence("seq", tgc);
          activity.addElement(tmlsequence);
          corrTgElement.addCor(tmlsequence, tgc);

        } else if (tgc instanceof TMLADUnorderedSequence) {
          tmlrsequence = new TMLRandomSequence("rseq", tgc);
          activity.addElement(tmlrsequence);
          corrTgElement.addCor(tmlrsequence, tgc);

        } else if (tgc instanceof TMLADReadChannel) {
          // Get the channel
          // TMLADReadChannel rd = (TMLADReadChannel) tgc;
          channel = tmlm.getChannelByName(getFromTable(tmltask, ((TMLADReadChannel) tgc).getChannelName(), table));
          /*
           * if (rd.isAttacker()){ channel =
           * tmlm.getChannelByName(getAttackerChannel(((TMLADReadChannel)tgc).
           * getChannelName())); }
           */
          if (channel == null) {
            if (Conversion.containsStringInList(removedChannels, ((TMLADReadChannel) tgc).getChannelName())) {
              UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                  "A call to " + ((TMLADReadChannel) tgc).getChannelName()
                      + " has been removed because the corresponding channel is not taken into account");
              ce.setTDiagramPanel(tadp);
              ce.setTGComponent(tgc);
              warnings.add(ce);
              ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
              activity.addElement(new TMLJunction("void junction", tgc));
            } else {
              UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                  ((TMLADReadChannel) tgc).getChannelName() + " is an unknown channel");
              ce.setTDiagramPanel(tadp);
              ce.setTGComponent(tgc);
              ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
              checkingErrors.add(ce);
            }

          } else {
            tmlreadchannel = new TMLReadChannel("read channel", tgc);
            tmlreadchannel.setNbOfSamples(modifyString(((TMLADReadChannel) tgc).getSamplesValue()));
            tmlreadchannel.setEncForm(((TMLADReadChannel) tgc).getEncForm());
            tmlreadchannel.addChannel(channel);
            // security pattern
            if (securityPatterns.get(((TMLADReadChannel) tgc).getSecurityContext()) != null) {
              tmlreadchannel.securityPattern = securityPatterns.get(((TMLADReadChannel) tgc).getSecurityContext());
              // NbOfSamples will increase due to extra overhead from MAC
              /*
               * int cur=1; try { cur =
               * Integer.valueOf(modifyString(((TMLADReadChannel)tgc).getSamplesValue())); }
               * catch(NumberFormatException e) { } catch(NullPointerException e) { }
               */
              String curS = modifyString(((TMLADReadChannel) tgc).getSamplesValue());
              String addS = "" + tmlreadchannel.securityPattern.overhead;
              // int add = Integer.valueOf(tmlreadchannel.securityPattern.overhead);
              if (!tmlreadchannel.securityPattern.nonce.equals("")) {
                SecurityPattern nonce = securityPatterns.get(tmlreadchannel.securityPattern.nonce);
                if (nonce != null) {
                  // add = Integer.valueOf(nonce.overhead);
                  addS = "" + nonce.overhead;
                }
              }
              // cur = cur+ add;

              // tmlreadchannel.setNbOfSamples(Integer.toString(cur));
              tmlreadchannel.setNbOfSamples(curS + " + " + addS);
            } else if (!((TMLADReadChannel) tgc).getSecurityContext().isEmpty()) {
              // Throw error for missing security pattern
              UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR,
                  "Security Pattern " + ((TMLADReadChannel) tgc).getSecurityContext() + " not found");
              ce.setTDiagramPanel(tadp);
              ce.setTGComponent(tgc);
              checkingErrors.add(ce);
            }
            if (tmltask.isAttacker()) {
              tmlreadchannel.setAttacker(true);
            }
            activity.addElement(tmlreadchannel);
            ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
            corrTgElement.addCor(tmlreadchannel, tgc);
          }
        } else if (tgc instanceof TMLADSendEvent) {
          event = tmlm.getEventByName(getFromTable(tmltask, ((TMLADSendEvent) tgc).getEventName(), table));
          if (event == null) {
            if (Conversion.containsStringInList(removedEvents, ((TMLADSendEvent) tgc).getEventName())) {
              UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                  "A call to " + ((TMLADSendEvent) tgc).getEventName()
                      + " has been removed because the corresponding event is not taken into account");
              ce.setTDiagramPanel(tadp);
              ce.setTGComponent(tgc);
              warnings.add(ce);
              activity.addElement(new TMLJunction("void junction", tgc));
              ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
            } else {
              UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                  ((TMLADSendEvent) tgc).getEventName() + " is an unknown event");
              ce.setTDiagramPanel(tadp);
              ce.setTGComponent(tgc);
              checkingErrors.add(ce);
              ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
            }
          } else {
            tmlsendevent = new TMLSendEvent("send event", tgc);
            tmlsendevent.setEvent(event);

            for (int i = 0; i < ((TMLADSendEvent) tgc).realNbOfParams(); i++) {
              tmp = modifyString(((TMLADSendEvent) tgc).getRealParamValue(i));
              Vector<String> allVariables = tmltask.getAllAttributesStartingWith(tmp + "__");
              if (allVariables.size() > 0) {
                for (int k = 0; k < allVariables.size(); k++) {
                  // TraceManager.addDev("Adding record: " + allVariables.get(k));
                  tmlsendevent.addParam(allVariables.get(k));
                }
              } else {
                // TraceManager.addDev("Adding param: " + tmp);
                tmlsendevent.addParam(tmp);
              }
            }
            if (event.getNbOfParams() != tmlsendevent.getNbOfParams()) {
              TraceManager
                  .addDev("ERROR : event#:" + event.getNbOfParams() + " sendevent#:" + tmlsendevent.getNbOfParams());
              UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                  ((TMLADSendEvent) tgc).getEventName() + ": wrong number of parameters");
              ce.setTDiagramPanel(tadp);
              ce.setTGComponent(tgc);
              checkingErrors.add(ce);
              ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
            } else {
              activity.addElement(tmlsendevent);
              corrTgElement.addCor(tmlsendevent, tgc);
              ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
            }
          }

        } else if (tgc instanceof TMLADSendRequest) {
          request = tmlm.getRequestByName(getFromTable(tmltask, ((TMLADSendRequest) tgc).getRequestName(), table));
          if (request == null) {
            if (Conversion.containsStringInList(removedRequests, ((TMLADSendRequest) tgc).getRequestName())) {
              UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                  "A call to " + ((TMLADSendRequest) tgc).getRequestName()
                      + " has been removed because the corresponding request is not taken into account");
              ce.setTDiagramPanel(tadp);
              ce.setTGComponent(tgc);
              warnings.add(ce);
              activity.addElement(new TMLJunction("void junction", tgc));
              ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
            } else {
              UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                  ((TMLADSendRequest) tgc).getRequestName() + " is an unknown request");
              ce.setTDiagramPanel(tadp);
              ce.setTGComponent(tgc);
              checkingErrors.add(ce);
              ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
            }
          } else {
            tmlsendrequest = new TMLSendRequest("send request", tgc);
            tmlsendrequest.setRequest(request);
            for (int i = 0; i < ((TMLADSendRequest) tgc).realNbOfParams(); i++) {
              tmp = modifyString(((TMLADSendRequest) tgc).getRealParamValue(i));
              Vector<String> allVariables = tmltask.getAllAttributesStartingWith(tmp + "__");
              if (allVariables.size() > 0) {
                for (int k = 0; k < allVariables.size(); k++) {
                  // TraceManager.addDev("Adding record: " + allVariables.get(k));
                  tmlsendrequest.addParam(allVariables.get(k));
                  request.addParamName(allVariables.get(k));
                }
              } else {
                // TraceManager.addDev("Adding param: " + tmp);
                tmlsendrequest.addParam(tmp);
                request.addParamName(tmp);
              }
            }
            if (request.getNbOfParams() != tmlsendrequest.getNbOfParams()) {
              TraceManager.addDev(
                  "ERROR : request#:" + request.getNbOfParams() + " sendrequest#:" + tmlsendrequest.getNbOfParams());
              UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                  ((TMLADSendRequest) tgc).getRequestName() + ": wrong number of parameters");
              ce.setTDiagramPanel(tadp);
              ce.setTGComponent(tgc);
              checkingErrors.add(ce);
              ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
            } else {
              activity.addElement(tmlsendrequest);
              corrTgElement.addCor(tmlsendrequest, tgc);
              ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
            }
          }

        } else if (tgc instanceof TMLADReadRequestArg) {
          request = tmlm.getRequestToMe(tmltask);
          if (request == null) {
            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                "This task is not requested: cannot use \"reading request arg\" operator");
            ce.setTDiagramPanel(tadp);
            ce.setTGComponent(tgc);
            checkingErrors.add(ce);
            ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
          } else {
            tmlaction = new TMLActionState("action reading args", tgc);
            String act = "";
            int cpt = 1;
            for (int i = 0; i < ((TMLADReadRequestArg) tgc).realNbOfParams(); i++) {
              tmp = modifyString(((TMLADReadRequestArg) tgc).getRealParamValue(i));
              Vector<String> allVariables = tmltask.getAllAttributesStartingWith(tmp + "__");

              if (allVariables.size() > 0) {
                for (int k = 0; k < allVariables.size(); k++) {
                  // TraceManager.addDev("Adding record: " + allVariables.get(k));
                  if (cpt != 1) {
                    act += "$";
                  }
                  act += allVariables.get(k) + " = arg" + cpt + "__req";
                  cpt++;
                }
              } else {
                // TraceManager.addDev("Adding param: " + tmp);
                if (cpt != 1) {
                  act += "$";
                }
                act += tmp + " = arg" + cpt + "__req";
                cpt++;
              }
            }
            if (request.getNbOfParams() != (cpt - 1)) {
              // TraceManager.addDev("ERROR : request#:" + request.getNbOfParams() + " read
              // request arg#:" + (cpt-1));
              UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                  "Wrong number of parameters in \"reading request arg\" operator");
              ce.setTDiagramPanel(tadp);
              ce.setTGComponent(tgc);
              checkingErrors.add(ce);
              ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
            } else {
              // TraceManager.addDev("Adding action = " + act);
              tmlaction.setAction(act);
              activity.addElement(tmlaction);
              ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
              corrTgElement.addCor(tmlaction, tgc);
            }
          }

        } else if (tgc instanceof TMLADStopState) {
          tmlstopstate = new TMLStopState("stop state", tgc);
          activity.addElement(tmlstopstate);
          corrTgElement.addCor(tmlstopstate, tgc);

        } else if (tgc instanceof TMLADNotifiedEvent) {
          event = tmlm.getEventByName(getFromTable(tmltask, ((TMLADNotifiedEvent) tgc).getEventName(), table));
          if (event == null) {
            if (removedEvents.size() > 0) {
              if (Conversion.containsStringInList(removedEvents, ((TMLADNotifiedEvent) tgc).getEventName())) {
                UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                    "A call to " + ((TMLADNotifiedEvent) tgc).getEventName()
                        + " has been removed because the corresponding event is not taken into account");
                ce.setTDiagramPanel(tadp);
                ce.setTGComponent(tgc);
                warnings.add(ce);
                activity.addElement(new TMLJunction("void junction", tgc));
                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
              } else {
                UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                    ((TMLADNotifiedEvent) tgc).getEventName() + " is an unknown event");
                ce.setTDiagramPanel(tadp);
                ce.setTGComponent(tgc);
                checkingErrors.add(ce);
                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
              }
            } else {
              UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                  ((TMLADNotifiedEvent) tgc).getEventName() + " is an unknown event");
              ce.setTDiagramPanel(tadp);
              ce.setTGComponent(tgc);
              checkingErrors.add(ce);
              ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
            }
          } else {
            event.setNotified(true);
            tmlnotifiedevent = new TMLNotifiedEvent("notified event", tgc);
            tmlnotifiedevent.setEvent(event);
            tmlnotifiedevent.setVariable(modifyString(((TMLADNotifiedEvent) tgc).getVariable()));
            activity.addElement(tmlnotifiedevent);
            ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
            corrTgElement.addCor(tmlnotifiedevent, tgc);
          }

        } else if (tgc instanceof TMLADWaitEvent) {
          event = tmlm.getEventByName(getFromTable(tmltask, ((TMLADWaitEvent) tgc).getEventName(), table));
          if (event == null) {
            if (removedEvents.size() > 0) {
              if (Conversion.containsStringInList(removedEvents, ((TMLADWaitEvent) tgc).getEventName())) {
                UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                    "A call to " + ((TMLADWaitEvent) tgc).getEventName()
                        + " has been removed because the corresponding event is not taken into account");
                ce.setTDiagramPanel(tadp);
                ce.setTGComponent(tgc);
                warnings.add(ce);
                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
                activity.addElement(new TMLJunction("void junction", tgc));
              } else {
                UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                    ((TMLADWaitEvent) tgc).getEventName() + " is an unknown event");
                ce.setTDiagramPanel(tadp);
                ce.setTGComponent(tgc);
                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
                checkingErrors.add(ce);
              }
            } else {
              UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                  ((TMLADWaitEvent) tgc).getEventName() + " is an unknown event");
              ce.setTDiagramPanel(tadp);
              ce.setTGComponent(tgc);
              ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
              checkingErrors.add(ce);
            }
          } else {
            // TraceManager.addDev("Nb of param of event:" + event.getNbOfParams());
            tmlwaitevent = new TMLWaitEvent("wait event", tgc);
            tmlwaitevent.setEvent(event);
            for (int i = 0; i < ((TMLADWaitEvent) tgc).realNbOfParams(); i++) {
              tmp = modifyString(((TMLADWaitEvent) tgc).getRealParamValue(i));
              Vector<String> allVariables = tmltask.getAllAttributesStartingWith(tmp + "__");
              if (allVariables.size() > 0) {
                for (int k = 0; k < allVariables.size(); k++) {
                  // TraceManager.addDev("Adding record: " + allVariables.get(k));
                  tmlwaitevent.addParam(allVariables.get(k));
                }
              } else {
                // TraceManager.addDev("Adding param: " + tmp);
                tmlwaitevent.addParam(tmp);
              }
            }
            if (event.getNbOfParams() != tmlwaitevent.getNbOfParams()) {
              // TraceManager.addDev("ERROR : event#:" + event.getNbOfParams() + "
              // waitevent#:" + tmlwaitevent.getNbOfParams());
              UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                  ((TMLADWaitEvent) tgc).getEventName() + ": wrong number of parameters");
              ce.setTDiagramPanel(tadp);
              ce.setTGComponent(tgc);
              checkingErrors.add(ce);
              ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
            } else {
              activity.addElement(tmlwaitevent);
              corrTgElement.addCor(tmlwaitevent, tgc);
              ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
            }

          }

        } else if (tgc instanceof TMLADWriteChannel) {
          // Get channels
          // TMLADWriteChannel wr = (TMLADWriteChannel) tgc;
          channels = ((TMLADWriteChannel) tgc).getChannelsByName();
          boolean error = false;
          for (int i = 0; i < channels.length; i++) {
            // TraceManager.addDev("Getting from table " + tmltask.getName() + "/"
            // +channels[i]);
            channel = tmlm.getChannelByName(getFromTable(tmltask, channels[i], table));
            if (channel == null) {
              if (Conversion.containsStringInList(removedChannels, ((TMLADWriteChannel) tgc).getChannelName(i))) {
                UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                    "A call to " + ((TMLADWriteChannel) tgc).getChannelName(i)
                        + " has been removed because the corresponding channel is not taken into account");
                ce.setTDiagramPanel(tadp);
                ce.setTGComponent(tgc);
                warnings.add(ce);
                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
                activity.addElement(new TMLJunction("void junction", tgc));
              } else {
                UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                    ((TMLADWriteChannel) tgc).getChannelName(i) + " is an unknown channel");
                ce.setTDiagramPanel(tadp);
                ce.setTGComponent(tgc);
                ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.UNKNOWN);
                checkingErrors.add(ce);
              }
              error = true;

            }
          }
          if (!error) {
            tmlwritechannel = new TMLWriteChannel("write channel", tgc);
            tmlwritechannel.setNbOfSamples(modifyString(((TMLADWriteChannel) tgc).getSamplesValue()));
            tmlwritechannel.setEncForm(((TMLADWriteChannel) tgc).getEncForm());

            for (int i = 0; i < channels.length; i++) {
              channel = tmlm.getChannelByName(getFromTable(tmltask, channels[i], table));
              tmlwritechannel.addChannel(channel);
            }
            // if (wr.isAttacker()){
            // channel = tmlm.getChannelByName(getAttackerChannel(channels[0]));
            // tmlwritechannel.addChannel(channel);
            // }
            // add sec pattern
            if (securityPatterns.get(((TMLADWriteChannel) tgc).getSecurityContext()) != null) {
              tmlwritechannel.securityPattern = securityPatterns.get(((TMLADWriteChannel) tgc).getSecurityContext());
              String curS = modifyString(((TMLADWriteChannel) tgc).getSamplesValue());
              String addS = "" + tmlwritechannel.securityPattern.overhead;
              // int cur =
              // Integer.valueOf(modifyString(((TMLADWriteChannel)tgc).getSamplesValue()));
              // int add = Integer.valueOf(tmlwritechannel.securityPattern.overhead);
              if (!tmlwritechannel.securityPattern.nonce.equals("")) {
                SecurityPattern nonce = securityPatterns.get(tmlwritechannel.securityPattern.nonce);
                if (nonce != null) {
                  addS = "" + nonce.overhead;
                  // add = Integer.valueOf(nonce.overhead);
                }
              }
              // cur = cur + add;
              // tmlwritechannel.setNbOfSamples(Integer.toString(cur));
              // tmlwritechannel.setNbOfSamples(att.getName());
              tmlwritechannel.setNbOfSamples(curS + " + " + addS);
            } else if (!((TMLADWriteChannel) tgc).getSecurityContext().isEmpty()) {
              // Throw error for missing security pattern
              UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR,
                  "Security Pattern " + ((TMLADWriteChannel) tgc).getSecurityContext() + " not found");
              ce.setTDiagramPanel(tadp);
              ce.setTGComponent(tgc);
              checkingErrors.add(ce);
            }
            if (tmltask.isAttacker()) {
              tmlwritechannel.setAttacker(true);
            }
            activity.addElement(tmlwritechannel);
            ((BasicErrorHighlight) tgc).setStateAction(ErrorHighlight.OK);
            corrTgElement.addCor(tmlwritechannel, tgc);
          }
        }
      }
    }

    // Interconnection between elements
    // TGConnectorTMLAD tgco;
    // TGConnectingPoint p1, p2;
    // TMLActivityElement ae1, ae2;
    // TGComponent /*tgc1, tgc2,*/ tgc3;
    // int j, index;

    final List<TGConnector> connectors = tadp.getConnectors();
    final Set<TGConnector> prunedConectors = new HashSet<TGConnector>();

    for (final TGConnector connector : connectors) {
      if (!prunedConectors.contains(connector)) {
        FindNextEnabledConnectingPointVisitor visitor = new FindNextEnabledConnectingPointVisitor(prunedConectors);
        connector.getTGConnectingPointP1().acceptBackward(visitor);
        final TGConnectingPoint conPoint1 = visitor.getEnabledComponentPoint();

        if (conPoint1 != null) {
          visitor = new FindNextEnabledConnectingPointVisitor(prunedConectors);
          connector.getTGConnectingPointP2().acceptForward(visitor);
          final TGConnectingPoint conPoint2 = visitor.getEnabledComponentPoint();

          if (conPoint2 != null) {
            final TGComponent compo1 = (TGComponent) conPoint1.getFather();
            final TGComponent compo2 = (TGComponent) conPoint2.getFather();

            final TMLActivityElement ae1 = activity.findReferenceElement(compo1);
            final TMLActivityElement ae2 = activity.findReferenceElement(compo2);

            // Special case if "for loop" or if "choice"
            if ((ae1 != null) && (ae2 != null)) {
              if (ae1 instanceof TMLForLoop) {
                final int index = compo1.indexOf(conPoint1) - 1;

                if (index == 0) {
                  ae1.addNext(0, ae2);
                } else {
                  ae1.addNext(ae2);
                }

              } else if (ae1 instanceof TMLChoice) {
                // final int index = compo1.indexOf( conPoint1 ) - 1;
                // TraceManager.addDev("Adding next:" + ae2);
                ae1.addNext(ae2);

                final TMLADChoice choice = (TMLADChoice) compo1;
                final TGCOneLineText guard = choice.getGuardForConnectingPoint(conPoint1);
                // Check validity of guard.
                String tmpG = guard.getValue().trim();
                if (tmpG.substring(0, 1).compareTo("[") != 0) {
                  UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                      "Badly formatted guard: " + tmpG);
                  ce.setTDiagramPanel(tadp);
                  ce.setTGComponent(choice);
                  checkingErrors.add(ce);
                }

                if (tmpG.substring(tmpG.length() - 1, tmpG.length()).compareTo("]") != 0) {
                  UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                      "Badly formatted guard: " + tmpG);
                  ce.setTDiagramPanel(tadp);
                  ce.setTGComponent(choice);
                  checkingErrors.add(ce);
                }

                ((TMLChoice) ae1).addGuard(modifyString(choice.getEffectiveCondition(guard)));
              } else if (ae1 instanceof TMLSequence) {
                final int index = compo1.indexOf(conPoint1) - 1;
                ((TMLSequence) ae1).addIndex(index);
                ae1.addNext(ae2);
                // TraceManager.addDev("Adding " + ae2 + " at index " + index);

              } else if (ae1 instanceof TMLRandomSequence) {
                final int index = compo1.indexOf(conPoint1) - 1;
                ((TMLRandomSequence) ae1).addIndex(index);
                ae1.addNext(ae2);
              } else {
                ae1.addNext(ae2);
              }
            }
          }
        }
      }
    }

    // Check that each "for" has two nexts
    // Check that TMLChoice have compatible guards
    // Check TML select evts
    iterator = list.listIterator();
    while (iterator.hasNext()) {
      tgc = iterator.next();

      // Issue #69: Disabling of AD components
      if (tgc.isEnabled()) {
        if ((tgc instanceof TMLADForLoop) || (tgc instanceof TMLADForStaticLoop)) {
          final TMLActivityElement ae1 = activity.findReferenceElement(tgc);

          if (ae1 != null) {
            if (ae1.getNbNext() != 2) {
              UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                  "Badly formatted for loop: a loop must have an internal behavior, and an exit behavior ");
              ce.setTDiagramPanel(tadp);
              ce.setTGComponent(tgc);
              checkingErrors.add(ce);
            }
          }
        } else if (tgc instanceof TMLADChoice) {
          tmlchoice = (TMLChoice) (activity.findReferenceElement(tgc));
          tmlchoice.orderGuards();

          int nbNonDeter = tmlchoice.nbOfNonDeterministicGuard();
          int nbStocha = tmlchoice.nbOfStochasticGuard();
          if ((nbNonDeter > 0) && (nbStocha > 0)) {
            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                "Badly formatted choice: it has both non-determinitic and stochastic guards");
            ce.setTDiagramPanel(tadp);
            ce.setTGComponent(tgc);
            checkingErrors.add(ce);
          }
          int nb = Math.max(nbNonDeter, nbStocha);
          if (nb > 0) {
            nb = nb + tmlchoice.nbOfElseAndAfterGuards();
            if (nb != tmlchoice.getNbGuard()) {
              UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                  UICheckingError.MESSAGE_CHOICE_BOTH_STOCHASTIC_DETERMINISTIC);// "Badly formatted choice: it has both
                                                                                // non-determinitic/ stochastic and
                                                                                // regular guards");
              ce.setTDiagramPanel(tadp);
              ce.setTGComponent(tgc);
              checkingErrors.add(ce);
            }
          }

          // if (tmlchoice.nbOfNonDeterministicGuard() > 0) {
          /*
           * if (!rndAdded) { TMLAttribute tmlt = new TMLAttribute("rnd__0", new
           * TMLType(TMLType.NATURAL)); tmlt.initialValue = "";
           * tmltask.addAttribute(tmlt); rndAdded = true; }
           */
          // }
          if (tmlchoice.hasMoreThanOneElse()) {
            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                "Choice should have only one [else] guard");
            ce.setTDiagramPanel(tadp);
            ce.setTGComponent(tgc);
            checkingErrors.add(ce);
          } else if (tmlchoice.getElseGuard() > -1) {
            final int index = tmlchoice.getElseGuard();

            if (index == 0) {
              UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                  "Choice should have a regular guard");
              ce.setTDiagramPanel(tadp);
              ce.setTGComponent(tgc);
              checkingErrors.add(ce);
            }
          }
          if (tmlchoice.hasMoreThanOneAfter()) {
            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                "Choice should have only one [after] guard");
            ce.setTDiagramPanel(tadp);
            ce.setTGComponent(tgc);
            checkingErrors.add(ce);
          }
        } else if (tgc instanceof TMLADSelectEvt) {
          tmlselectevt = (TMLSelectEvt) (activity.findReferenceElement(tgc));
          if (!tmlselectevt.isARealSelectEvt()) {
            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR,
                "'Select events'  should be followed by only event receiving operators");
            ce.setTDiagramPanel(tadp);
            ce.setTGComponent(tgc);
            checkingErrors.add(ce);
          }
        }
      }
    }

    // Sorting nexts elements of Sequence
    for (int j = 0; j < activity.nElements(); j++) {
      final TMLActivityElement ae1 = activity.get(j);

      if (ae1 instanceof TMLSequence) {
        ((TMLSequence) ae1).sortNexts();
      } else if (ae1 instanceof TMLRandomSequence) {
        ((TMLRandomSequence) ae1).sortNexts();
      }
    }

    // TraceManager.addDev("Activity:" + tmltask.getActivityDiagram().toXML() );
  }

  private String modifyActionString(String _input) {
    int index = _input.indexOf("++");
    boolean b1, b2;
    String tmp;

    if (index > -1) {
      tmp = _input.substring(0, index).trim();

      b1 = (tmp.substring(0, 1)).matches("[a-zA-Z]");
      b2 = tmp.matches("\\w*");
      if (b1 && b2) {
        return tmp + " = " + tmp + " + 1";
      }
    }

    index = _input.indexOf("--");
    if (index > -1) {
      tmp = _input.substring(0, index).trim();

      b1 = (tmp.substring(0, 1)).matches("[a-zA-Z]");
      b2 = tmp.matches("\\w*");
      if (b1 && b2) {
        return tmp + " = " + tmp + " - 1";
      }
    }

    return modifyString(_input);
  }

  private String modifyString(String _input) {
    return Conversion.replaceAllChar(_input, '.', "__");
  }

  private String getFromTable(final TMLTask task, final String s, final Map<String, String> table) {
    // TraceManager.addDev("TABLE GET: Getting from task=" + task.getName() + "
    // element=" + s);

    if (table == null) {
      return s;
    }

    String ret = table.get(task.getName() + "/" + s);
    // TraceManager.addDev("Returning=" + ret);

    if (ret == null) {
      return s;
    }

    return ret;
  }
}
