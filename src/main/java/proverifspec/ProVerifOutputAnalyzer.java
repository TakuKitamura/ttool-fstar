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

package proverifspec;

import avatartranslator.*;
import avatartranslator.toproverif.AVATAR2ProVerif;
//import com.sun.org.apache.xpath.internal.operations.String;
import common.ConfigurationTTool;
import myutil.FileUtils;
import myutil.TraceManager;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class ProVerifOutputAnalyzer Creation: 16/09/2010
 * 
 * @version 1.0 16/09/2010
 * @author Ludovic APVRILLE
 */
public class ProVerifOutputAnalyzer {
  private final static String typedEvent = "not event(enteringState" + AVATAR2ProVerif.ATTR_DELIM;
  private final static String untypedEvent = "not ev:enteringState" + AVATAR2ProVerif.ATTR_DELIM;
  private final static String typedFalse = ") is false";
  private final static String typedTrue = ") is true";
  private final static String typedCannotBeProved = ") cannot be proved";
  private final static String untypedCannotBeProved = " cannot be proved";
  private final static String untypedFalse = " is false";
  private final static String untypedTrue = " is true";
  private final static String typedSecret = "not attacker(";
  private final static String untypedSecret = "not attacker:";
  private final static String typedStrongAuth = "inj-event(authenticity" + AVATAR2ProVerif.ATTR_DELIM;
  private final static String untypedStrongAuth = "evinj:authenticity" + AVATAR2ProVerif.ATTR_DELIM;
  private final static String typedWeakAuth = "(but event(authenticity" + AVATAR2ProVerif.ATTR_DELIM;
  private final static String untypedWeakAuth = "(but ev:authenticity" + AVATAR2ProVerif.ATTR_DELIM;
  private final static String typedWeakNonAuth = "(even event(authenticity" + AVATAR2ProVerif.ATTR_DELIM;
  private final static String untypedWeakNonAuth = "(even ev:authenticity" + AVATAR2ProVerif.ATTR_DELIM;
  private final static String typedAuthSplit = "==> inj-event(authenticity" + AVATAR2ProVerif.ATTR_DELIM;
  // private final static String typedWeakAuthSplit = "==> event(authenticity" +
  // AVATAR2ProVerif.ATTR_DELIM;
  private final static String untypedAuthSplit = "==> evinj:authenticity" + AVATAR2ProVerif.ATTR_DELIM;
  // private final static String untypedWeakAuthSplit = "==> ev:authenticity" +
  // AVATAR2ProVerif.ATTR_DELIM;

  private ConcurrentHashMap<AvatarPragma, ProVerifQueryResult> results;
  private LinkedList<String> errors;

  private LinkedList<ProVerifOutputListener> listeners;

  private AVATAR2ProVerif avatar2proverif;

  public ProVerifOutputAnalyzer(AVATAR2ProVerif avatar2proverif) {
    this.avatar2proverif = avatar2proverif;
    this.errors = null;
    this.results = null;
    this.listeners = new LinkedList<>();
  }

  public void addListener(ProVerifOutputListener listener) {
    this.listeners.add(listener);
  }

  public void removeListener(ProVerifOutputListener listener) {
    this.listeners.remove(listener);
  }

  private void notifyListeners() {
    for (ProVerifOutputListener listener : this.listeners)
      listener.proVerifOutputChanged();
  }

  public void analyzeOutput(Reader reader, boolean isTyped) {
    BufferedReader bReader = new BufferedReader(reader);
    List<AvatarPragma> pragmas = this.avatar2proverif.getAvatarSpecification().getPragmas();
    String previous = null;
    String str;
    ProVerifQueryAuthResult previousAuthPragma = null;
    LinkedList<String> proverifProcess = new LinkedList<String>();
    ProVerifResultTrace resultTrace = new ProVerifResultTrace(proverifProcess);
    boolean isInTrace = false;

    this.results = new ConcurrentHashMap<AvatarPragma, ProVerifQueryResult>();
    this.errors = new LinkedList<String>();

    Pattern procPattern = Pattern.compile(" *\\{\\d+\\}(.*)");

    try {
      StringBuffer sb = new StringBuffer("");

      // Loop through every line in the output
      while ((str = bReader.readLine()) != null) {
        sb.append(str + "\n");

        if (str.isEmpty())
          continue;

        // TraceManager.addDev("Analyzing trace:" + str);

        Matcher m = procPattern.matcher(str);

        if (isInTrace && (str.startsWith("A more detailed") || str.startsWith("Could not find"))) {
          isInTrace = false;
          resultTrace.finalize();
          continue;
        } else if (!isInTrace && str.startsWith("1. ")) {
          isInTrace = true;
        }

        // Found a trace step
        if (isInTrace) {
          resultTrace.addTraceStep(str);
        }

        else if (m.matches()) {
          proverifProcess.add(m.group(1));
        }

        // Found a line with a RESULT
        else if (str.startsWith("RESULT ")) {
          // Remove 'RESULT ' at the begining
          str = str.substring(7);

          ProVerifQueryResult result = new ProVerifQueryResult(true, true);

          // This concerns an enteringState event
          if (str.startsWith(isTyped ? typedEvent : untypedEvent)) {
            str = str.substring((isTyped ? typedEvent : untypedEvent).length());
            String stateName = null;

            previousAuthPragma = null;

            if (isTyped) {
              if (str.contains(typedTrue)) {
                result.setSatisfied(false);
                stateName = str.split(Pattern.quote(typedTrue))[0];
              } else if (str.contains(typedFalse)) {
                result.setTrace(resultTrace);
                stateName = str.split(Pattern.quote(typedFalse))[0];
              } else if (str.contains(typedCannotBeProved)) {
                result.setProved(false);
                result.setTrace(resultTrace);
                stateName = str.split(Pattern.quote(typedCannotBeProved))[0];
              }
            } else {
              stateName = str.split("\\(")[0];
              if (str.contains(untypedTrue)) {
                result.setSatisfied(false);
              } else if (str.contains(untypedFalse)) {
                result.setTrace(resultTrace);
              } else if (str.contains(untypedCannotBeProved)) {
                result.setTrace(resultTrace);
                result.setProved(false);
              }
            }

            AvatarPragmaReachability reachabilityPragma = this.getAvatarPragmaReachabilityFromString(stateName);
            if (reachabilityPragma != null) {
              this.results.put(reachabilityPragma, result);
              this.notifyListeners();
            }
          }

          // This concerns a confidentiality check
          else if (str.contains(isTyped ? typedSecret : untypedSecret)) {
            String attributeName = str.substring((isTyped ? typedSecret : untypedSecret).length()).split("\\[")[0];

            previousAuthPragma = null;

            if (str.contains(isTyped ? typedFalse : untypedFalse)) {
              result.setTrace(resultTrace);
              result.setSatisfied(false);
            } else if (str.contains(isTyped ? typedCannotBeProved : untypedCannotBeProved)) {
              result.setTrace(resultTrace);
              result.setProved(false);
            }

            AvatarAttribute attribute = this.getAvatarAttributeFromString(attributeName);
            if (attribute != null) {
              for (AvatarPragma pragma : pragmas) {
                if (pragma instanceof AvatarPragmaSecret) {

                  String trueName = this.avatar2proverif.getTrueName(((AvatarPragmaSecret) pragma).getArg());

                  if (trueName != null && trueName.equals(attributeName)) {
                    this.results.put(pragma, result);
                    this.notifyListeners();
                  }
                }
              }
            }
          }

          // This concerns a strong authenticity check
          else if (str.contains(isTyped ? typedStrongAuth : untypedStrongAuth)) {
            str = str.substring((isTyped ? typedStrongAuth : untypedStrongAuth).length());

            String attributeStateName1 = null;
            String attributeStateName2 = null;

            previousAuthPragma = new ProVerifQueryAuthResult(true, true);
            result = previousAuthPragma;

            if (isTyped) {
              attributeStateName1 = str.split("\\(")[0];
              attributeStateName2 = str.split(Pattern.quote(typedAuthSplit))[1].split("\\(")[0];
              if (str.contains(typedFalse)) {
                result.setTrace(resultTrace);
                result.setSatisfied(false);
              } else if (str.contains(typedCannotBeProved)) {
                result.setTrace(resultTrace);
                result.setProved(false);
              }
            } else {
              attributeStateName1 = str.split("\\(")[0];
              attributeStateName2 = str.split(untypedAuthSplit)[1].split("\\(")[0];
              if (str.contains(untypedFalse)) {
                result.setTrace(resultTrace);
                result.setSatisfied(false);
              } else if (str.contains(untypedCannotBeProved)) {
                result.setTrace(resultTrace);
                result.setProved(false);
              }
            }

            AvatarAttribute attribute1 = null;
            AvatarAttribute attribute2 = null;
            AvatarState state1 = null;
            AvatarState state2 = null;

            String[] tmp = attributeStateName1.split(AVATAR2ProVerif.ATTR_DELIM);
            if (tmp.length == 3) {
              attribute1 = this.getAvatarAttributeFromString(tmp[0] + AVATAR2ProVerif.ATTR_DELIM + tmp[1]);
              state1 = this.getAvatarStateFromString(tmp[0] + AVATAR2ProVerif.ATTR_DELIM + tmp[2]);
            }

            tmp = attributeStateName2.split(AVATAR2ProVerif.ATTR_DELIM);
            if (tmp.length == 3) {
              attribute2 = this.getAvatarAttributeFromString(tmp[0] + AVATAR2ProVerif.ATTR_DELIM + tmp[1]);
              state2 = this.getAvatarStateFromString(tmp[0] + AVATAR2ProVerif.ATTR_DELIM + tmp[2]);
            }

            if (attribute1 != null && attribute2 != null && state1 != null && state2 != null) {
              for (AvatarPragma pragma : pragmas) {
                if (pragma instanceof AvatarPragmaAuthenticity) {
                  AvatarPragmaAuthenticity pragmaAuth = (AvatarPragmaAuthenticity) pragma;
                  if (pragmaAuth.getAttrA().getState() == state2 && pragmaAuth.getAttrA().getAttribute() == attribute2
                      && pragmaAuth.getAttrB().getState() == state1
                      && pragmaAuth.getAttrB().getAttribute() == attribute1) {
                    this.results.put(pragma, result);
                    this.notifyListeners();
                    break;
                  }
                }
              }
            }
          }

          // This concerns a satsified weak authenticity check
          else if (str.contains(isTyped ? typedWeakAuth : untypedWeakAuth)) {

            if (previousAuthPragma != null) {
              previousAuthPragma.setWeakSatisfied(true);
              this.notifyListeners();
            }
            previousAuthPragma = null;
          }

          // This concerns a failed weak authenticity check
          else if (str.contains(isTyped ? typedWeakNonAuth : untypedWeakNonAuth)) {

            if (previousAuthPragma != null) {
              previousAuthPragma.setWeakSatisfied(false);
              this.notifyListeners();
            }
            previousAuthPragma = null;
          }

          resultTrace = new ProVerifResultTrace(proverifProcess);
        }

        // Found an error
        else if (str.contains("Error:")) {
          synchronized (errors) {
            this.errors.add(str + ": " + previous);
          }
        }

        previous = str;
      }

      // Write trace to a file.
      int c = (int) (Math.random() * 100000);
      TraceManager.addDev("Saving trace in file " + sb.length() + " bytes in "
          + ConfigurationTTool.ProVerifCodeDirectory + "trace_" + c + ".txt");
      try {
        FileUtils.saveFile(ConfigurationTTool.ProVerifCodeDirectory + "trace_" + c + ".txt", sb.toString());
        TraceManager.addDev("Saving trace in file done");
      } catch (Exception e) {
        TraceManager.addDev("Saving trace in file failed");
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private AvatarAttribute getAvatarAttributeFromString(String name) {
    String[] tmp = name.split(AVATAR2ProVerif.ATTR_DELIM);
    if (tmp.length != 2)
      return null;

    AvatarBlock block = this.avatar2proverif.getAvatarSpecification().getBlockWithName(tmp[0]);
    if (block == null)
      return null;

    return block.getAvatarAttributeWithName(tmp[1]);
  }

  private AvatarPragmaReachability getAvatarPragmaReachabilityFromString(String name) {
    String[] tmp = name.split(AVATAR2ProVerif.ATTR_DELIM);
    if (tmp.length != 2)
      return null;

    AvatarBlock block = this.avatar2proverif.getAvatarSpecification().getBlockWithName(tmp[0]);
    if (block == null)
      return null;

    AvatarState state = block.getStateMachine().getStateWithName(tmp[1]);
    if (state == null)
      return null;

    return new AvatarPragmaReachability("reachability" + AVATAR2ProVerif.ATTR_DELIM + name, null, block, state);
  }

  private AvatarState getAvatarStateFromString(String name) {
    String[] tmp = name.split(AVATAR2ProVerif.ATTR_DELIM);
    if (tmp.length != 2)
      return null;

    AvatarBlock block = this.avatar2proverif.getAvatarSpecification().getBlockWithName(tmp[0]);
    if (block == null)
      return null;

    AvatarState state = block.getStateMachine().getStateWithName(tmp[1]);

    return state;
  }

  public Map<AvatarPragma, ProVerifQueryResult> getResults() {
    return this.results;
  }

  public Map<AvatarPragmaSecret, ProVerifQueryResult> getConfidentialityResults() {
    if (this.results == null)
      return null;

    HashMap<AvatarPragmaSecret, ProVerifQueryResult> resultMap = new HashMap<AvatarPragmaSecret, ProVerifQueryResult>();

    for (AvatarPragma pragma : this.results.keySet()) {
      if (pragma instanceof AvatarPragmaSecret) {
        resultMap.put((AvatarPragmaSecret) pragma, this.results.get(pragma));
      }
    }

    return resultMap;
  }

  public Map<AvatarPragmaReachability, ProVerifQueryResult> getReachabilityResults() {
    if (this.results == null)
      return null;

    Map<AvatarPragmaReachability, ProVerifQueryResult> resultMap = new HashMap<AvatarPragmaReachability, ProVerifQueryResult>();

    for (AvatarPragma pragma : this.results.keySet()) {
      if (pragma instanceof AvatarPragmaReachability) {
        resultMap.put((AvatarPragmaReachability) pragma, this.results.get(pragma));
      }
    }

    return resultMap;
  }

  public Map<AvatarPragmaAuthenticity, ProVerifQueryAuthResult> getAuthenticityResults() {
    if (this.results == null)
      return null;

    Map<AvatarPragmaAuthenticity, ProVerifQueryAuthResult> resultMap = new HashMap<AvatarPragmaAuthenticity, ProVerifQueryAuthResult>();

    for (AvatarPragma pragma : this.results.keySet()) {
      if (pragma instanceof AvatarPragmaAuthenticity) {
        resultMap.put((AvatarPragmaAuthenticity) pragma, (ProVerifQueryAuthResult) this.results.get(pragma));
      }
    }

    return resultMap;
  }

  public List<String> getErrors() {
    synchronized (errors) {
      return errors;
    }
  }
}
