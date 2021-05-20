/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT enstr.fr
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

package tmltranslator.modelcompiler;

import tmltranslator.TMLTask;

/**
 * Class BaseBuffer Creation: 11/02/2014
 * 
 * @version 1.0 11/02/2014
 * @author Andrea ENRICI
 */
public class BaseBuffer extends Buffer {

  public static final int BASE_ADDRESS_INDEX = 1;
  protected static final String BASE_ADDRESS_TYPE = "uint32_t*";

  public static final String DECLARATION = "extern struct BASE_BUFFER_TYPE {" + CR + TAB + BASE_ADDRESS_TYPE + SP
      + "base_address" + SC + CR + "};";

  protected String baseAddressValue = DEFAULT_NUM_VAL + USER_TO_DO;

  // private String context = "BASE_BUFFER_CONTEXT";

  public BaseBuffer(String _name, TMLTask _task) {
    type = "BASE_BUFFER_TYPE";
    name = _name;
    task = _task;
  }

  @Override
  public String getInitCode() {
    StringBuffer s = new StringBuffer();
    s.append(TAB + name + ".base_address = " + "(" + BASE_ADDRESS_TYPE + ")" + baseAddressValue + ";" + CR);
    return s.toString();
  }

  @Override
  public String toString() {
    StringBuffer s = new StringBuffer(super.toString());
    if (bufferParameters != null) {
      s.append(TAB2 + "base_address = " + bufferParameters.get(BASE_ADDRESS_INDEX) + SC + CR);
    } else {
      s.append(TAB2 + "base_address = " + DEFAULT_NUM_VAL + USER_TO_DO + ";" + CR);
    }
    return s.toString();
  }
  //
  // public String getContext() {
  // return context;
  // }
} // End of class
