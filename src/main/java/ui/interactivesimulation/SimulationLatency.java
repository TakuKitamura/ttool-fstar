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

package ui.interactivesimulation;

import avatartranslator.AvatarPragmaLatency;
import java.util.List;
import java.util.ArrayList;

/**
 * Class SimulationTransaction Transaction as defined by the simulation engine
 * Creation: 20/05/2016
 * 
 * @version 1.0 20/05/2016
 * @author Ludovic APVRILLE
 */
public class SimulationLatency {

  private String trans1;
  private String trans2;
  private String minTime = "N/A";
  private String maxTime = "N/A";
  private String avTime = "N/A";
  private String stDev = "N/A";
  private List<AvatarPragmaLatency> pragmas = new ArrayList<AvatarPragmaLatency>();

  public SimulationLatency() {
  }

  public void setTransaction1(String t) {
    trans1 = t;
  }

  public void setTransaction2(String t) {
    trans2 = t;
  }

  public void setMinTime(String time) {
    minTime = time;
  }

  public void setMaxTime(String time) {
    maxTime = time;
  }

  public void setAverageTime(String time) {
    avTime = time;
  }

  public void setStDev(String dev) {
    stDev = dev;
  }

  public String getTransaction1() {
    return trans1;
  }

  public String getTransaction2() {
    return trans2;
  }

  public String getMinTime() {
    return minTime;
  }

  public String getMaxTime() {
    return maxTime;
  }

  public String getAverageTime() {
    return avTime;
  }

  public String getStDev() {
    return stDev;
  }

  public List<AvatarPragmaLatency> getPragmas() {
    return pragmas;
  }

  public void addPragma(AvatarPragmaLatency ap) {
    pragmas.add(ap);
  }

}
