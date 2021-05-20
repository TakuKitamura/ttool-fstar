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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Class CPMEC, Model Extension Construct (MEC) class for Communication Patterns
 * Creation: 06/02/2014
 * 
 * @version 1.0 06/02/2014
 * @author Andrea ENRICI
 */
public abstract class CPMEC implements CCodeGenConstants {

  // protected String CR = "\n";
  // protected String TAB = "\t";

  protected String init_code = new String();
  protected String exec_code = new String();
  protected String cleanup_code = new String();

  public static final String[] CP_TYPES = { "Memory Copy", "Single DMA", "Double DMA" };
  public static final String[] TRANSFER_TYPES = { "memory to IP core", "IP core to IP core", "IP core to memory" };

  public static final String SINGLE_DMA = "Single DMA";
  public static final String DOUBLE_DMA = "Double DMA";
  public static final String MEMORY_COPY = "Memory Copy";

  // The number must be the same as the index in cpTypes
  public static final int CPU_MEMORY_COPY_MEC = 0;
  public static final int SINGLE_DMA_MEC = 1;
  public static final int DOUBLE_DMA_MEC = 2;

  public static final int MEM_2_IP = 0;
  public static final int IP_2_IP = 1;
  public static final int IP_2_MEM = 2;

  public static final String DMA_CONTROLLER = "DMA_Controller";
  public static final String SOURCE_STORAGE = "Src_Storage_Instance";
  public static final String DESTINATION_STORAGE = "Dst_Storage_Instance";

  // public static final String USER_TO_DO = "/* USER TO DO */";

  protected static final String DEST_ADDRESS_ATTRIBUTE_NAME = "destinationAddress";
  protected static final String SOURCE_ADDRESS_ATTRIBUTE_NAME = "sourceAddress";
  protected static final String SAMPLES_LOAD_ATTRIBUTE_NAME = "samplesToLoad";
  public static final String[] ORDERED_ATTRIBUTE_NAMES = new String[] { DEST_ADDRESS_ATTRIBUTE_NAME,
      SOURCE_ADDRESS_ATTRIBUTE_NAME, SAMPLES_LOAD_ATTRIBUTE_NAME };

  private final Map<String, String> attributes;

  protected CPMEC(final Collection<String> attributesStr) {
    attributes = initAttributes(attributesStr);
  }

  protected static Map<String, String> initAttributes(final Collection<String> attributesStr) {
    final Map<String, String> attributes = new HashMap<String, String>();

    for (final String assignment : attributesStr) {
      final String[] assignPair = assignment.split(" = ");
      final String key = assignPair[0].trim();
      final String value = assignPair[1].substring(0, assignPair[1].length() - 1);// remove trailing semi-colon

      attributes.put(key, value);
    }

    return attributes;
  }

  public String getExecCode() {
    return exec_code;
  }

  public String getInitCode() {
    return init_code;
  }

  public String getCleanupCode() {
    return cleanup_code;
  }

  protected String getAttributeValue(final String key, final String defaultValue) {
    if (attributes.containsKey(key)) {
      return attributes.get(key);
    }

    return defaultValue + USER_TO_DO;
  }
  // Get the value of an attribute from the TMLCP artifact string
  // protected static String getAttributeValue( String assignement ) {
  // String s = assignement.split(" = ")[1];
  // return s.substring(0, s.length()-1); //remove trailing semi-colon
  // }

  // Issue #38
  public static Vector<String> getSortedAttributeValues(final Collection<String> assignedAttributes,
      final String[] sortedKeys) {
    final Vector<String> values = new Vector<String>();
    final Map<String, String> attributes = initAttributes(assignedAttributes);

    for (final String key : sortedKeys) {
      final String value = attributes.get(key);

      if (value != null) {
        values.add(value);
      }
    }

    return values;
  }

  protected String getMemoryBaseAddress(final int srcMemoryType) {
    switch (srcMemoryType) {
      case Buffer.FEP_BUFFER:
        return "fep_mss";

      case Buffer.ADAIF_BUFFER:
        return "adaif_mss";

      case Buffer.INTERLEAVER_BUFFER:
        return "intl_mss";

      case Buffer.MAPPER_BUFFER:
        return "mapper_mss";

      case Buffer.MAIN_MEMORY_BUFFER:
      default:
        return DEFAULT_NUM_VAL + USER_TO_DO;// "0";
    }
  }
} // End of class
