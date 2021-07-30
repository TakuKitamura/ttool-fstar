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

package avatartranslator;

/**
 * Class AvatarType Avatar type Creation: 20/05/2010
 * 
 * @version 1.0 20/05/2010
 * @author Ludovic APVRILLE
 */
public enum AvatarType {
    // Types of parameters
    BOOLEAN("bool", "false", "f", 0), INTEGER("int", "0", "0", 0), TIMER("timer", "0", "0", 0),

    INT8("int8_t", "0", "0", 0), INT16("int16_t", "0", "0", 0), INT32("int32_t", "0", "0", 0),
    INT64("int64_t", "0", "0", 0), UINT8("uint8_t", "0", "0", 0), UINT16("uint16_t", "0", "0", 0),
    UINT32("uint32_t", "0", "0", 0), UINT64("uint64_t", "0", "0", 0),

    UNDEFINED("undefined", "", "", -1);

    private String name = "";
    private String defaultValue = "";
    private String defaultValueTF = "";
    private int defaultValueInInt = -1;

    AvatarType(String name, String defaultValue, String defaultValueTF, int defaultValueInInt) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.defaultValueTF = defaultValueTF;
        this.defaultValueInInt = defaultValueInInt;
    }

    public static AvatarType getType(String s) {
        if (s.equals("bool") || s.equals("Boolean"))
            return AvatarType.BOOLEAN;
        else if (s.equals("int") || s.equals("Integer"))
            return AvatarType.INTEGER;
        else if (s.equals("int8_t")) {
            return AvatarType.INT8;
        } else if (s.equals("int16_t")) {
            return AvatarType.INT16;
        } else if (s.equals("int32_t")) {
            return AvatarType.INT32;
        } else if (s.equals("int64_t")) {
            return AvatarType.INT64;
        } else if (s.equals("uint8_t")) {
            return AvatarType.UINT8;
        } else if (s.equals("uint16_t")) {
            return AvatarType.UINT16;
        } else if (s.equals("uint32_t")) {
            return AvatarType.UINT32;
        } else if (s.equals("uint64_t")) {
            return AvatarType.UINT64;
        } else if (s.equals("Timer"))
            return AvatarType.TIMER;
        return AvatarType.UNDEFINED;
    }

    public String getStringType() {
        return this.name;
    }

    public String getDefaultInitialValue() {
        return this.defaultValue;
    }

    public String getDefaultInitialValueTF() {
        return this.defaultValueTF;
    }

    public int getDefaultInitialValueInInt() {
        return this.defaultValueInInt;
    }

    /*
     * public AvatarType advancedClone() { AvatarType at = new AvatarType(name,
     * defaultValue, defaultValueTF, defaultValueInt); return at; }
     */
}
