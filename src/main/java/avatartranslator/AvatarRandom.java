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
 * Class AvatarRandom
 * Creation: 12/07/2010
 * @version 1.0 12/07/2010
 * @author Ludovic APVRILLE
 */
public class AvatarRandom extends AvatarStateMachineElement {
    protected String variable;
    protected String minValue;
    protected String maxValue;


    public final static int RANDOM_UNIFORM_LAW = 0;
    public final static int RANDOM_TRIANGULAR_LAW = 1;
    public final static int RANDOM_GAUSSIAN_LAW = 2;
    public final static int RANDOM_LOG_NORMAL_LAW = 3;
    public final static String[] DISTRIBUTION_LAWS = {"Uniform", "Triangular", "Gaussian", "Log normal"};
    public final static String[] DISTRIBUTION_LAWS_SHORT = {"", " ^", "ĝ", "ln"};

    public final static int[] NB_OF_EXTRA_ATTRIBUTES = {0, 1, 1, 2};
    public final static String[] LABELS_OF_EXTRA_ATTRIBUTES_1 = {"", "triangle top", "standard deviation", "standard deviation"};
    public final static String[] LABELS_OF_EXTRA_ATTRIBUTES_2 = {"", "", "", "mean"};
    protected int functionId;
    protected String extraAttribute1;
    protected String extraAttribute2;

    public AvatarRandom(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
    }

    public String getVariable() {
        return variable;
    }

    public String getMinValue() {
        return minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public int getFunctionId() {
        return functionId;
    }

    public String getExtraAttribute1() {
        return extraAttribute1;
    }

    public String getExtraAttribute2() {
        return extraAttribute2;
    }

    public void setVariable(String _variable) {
        variable = _variable;
    }

    public void setValues(String _minValue, String _maxValue) {
        minValue = _minValue;
        maxValue = _maxValue;
    }

    public void setFunctionId(int _functionId) {
        functionId = _functionId;
    }

    public void setExtraAttribute1(String _extraAttribute1) {
        extraAttribute1 = _extraAttribute1;
    }
    public void setExtraAttribute2(String _extraAttribute2) {
        extraAttribute2 = _extraAttribute2;
    }

    public String getNiceName() {
        return "Random between " + minValue + " and " + maxValue + " stored in " + variable;
    }

    public void translate (AvatarTranslator translator, Object arg) {
        translator.translateRandom (this, arg);
    }

    public AvatarStateMachineElement basicCloneMe(AvatarStateMachineOwner _block) {
	 AvatarRandom ar = new AvatarRandom(getName() + "_clone", getReferenceObject());

	 ar.setVariable(variable);
	 ar.setValues(minValue, maxValue);
	 ar.setFunctionId(functionId);
	 ar.setExtraAttribute1(extraAttribute1);
	 ar.setExtraAttribute2(extraAttribute2);

	 return ar;
    }
}
