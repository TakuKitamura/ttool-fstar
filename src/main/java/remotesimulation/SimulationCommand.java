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




package remotesimulation;

/**
 * Class SimulationCommand
 * Commands that can be sent to the remote simulator
 * Creation: 17/04/2009
 * @version 1.1 17/04/2009
 * @author Ludovic APVRILLE
 */
public class SimulationCommand {
    public String userCommand;
    public String alias;
    public String simulatorCommand;
    public int[] params;
    public String[] paramNames;
    // 0: no parameter;
    // 1: int
    // 2: String
    // 3: optional int
    // 4: optional String
    // 5: String to translate to id
    // 6: int: percentage (between 0 and 100)
    // WARNING: optional parameters must be put at the end of the list
    public String help;


    public SimulationCommand(String _userCommand, String _alias, String _simulatorCommand, int _params[], String _paramNames[], String _help) {
        userCommand = _userCommand;
        alias = _alias;
        simulatorCommand = _simulatorCommand;
        params = _params;
        paramNames = _paramNames;

        help = _help;
    }

    public String getSynopsis() {
        StringBuffer sb = new StringBuffer(userCommand);
        for(int i=0; i<params.length; i++) {
            sb.append(getParamString(i));
        }
        return sb.toString();
    }

    public String getParamString(int i) {

        if (params[i] == 0) {
            return "";
        }

        if (params[i] == 1) {
            return " <int: " + paramNames[i] + ">";
        }

        if (params[i] == 2) {
            return " <string: " + paramNames[i] + ">";
        }

        if (params[i] == 3) {
            return " [int: " + paramNames[i] + "]";
        }

        if (params[i] == 4) {
            return " [string: " + paramNames[i] + "]";
        }

        if (params[i] == 6) {
            return " [int between 0 and 100 (percentage): " + paramNames[i] + "]";
        }

        return " <unknow param>";
    }

    public int getMaxNumberOfParameters() {
        return params.length;
    }

    public int getMinNumberOfParameters() {
        int min = 0;
        for(int i=0; i<params.length; i++) {
            if ((params[i] >0) && (params[i] <3)) {
                min ++;
            }
        }
        return min;
    }

    private boolean checkForInteger(String s) {
        try {
            int i = Integer.decode(s).intValue();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areParametersCompatible(String[] splitCmd) {
	try {
        if ((splitCmd.length - 1) < getMinNumberOfParameters()) {
            return false;
        }

        if ((splitCmd.length - 1) > getMaxNumberOfParameters()) {
            return false;
        }

        for(int i=0; i<params.length; i++) {
            if (params[i] == 1) {
                if (!checkForInteger(splitCmd[i+1])) {
                    return false;
                }
            }

            if (params[i] == 3) {
                if (!checkForInteger(splitCmd[i+1])) {
                    return false;
                }
            }

            if (params[i] == 6) {
                if (!checkForInteger(splitCmd[i+1])) {
                    return false;
                }
            }
        }
        return true;
	} catch (Exception e) {
	    return false;
	}
    }

    public boolean hasAlias() {
        if ((alias == null) || (alias.length() == 0)) {
            return false;
        }

        return !alias.equals(userCommand);
    }

    public String translateCommand(String cmds[]) {
        String ret = simulatorCommand;
        for(int i=1; i<cmds.length; i++) {
            ret += " " + cmds[i];
        }
        return ret;
    }

}
