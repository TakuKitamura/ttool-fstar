/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class JKeyword
 * Creation: 03/03/2005
 * @version 1.1 03/03/2005
 * @author Ludovic APVRILLE
 * @see
 */

package translator.tojava;

public class JKeyword {
    private final static String [] words = {"abstract", "default", "goto", "package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements", 
	"protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", "final",
         "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while", "continue", "for", "new", "switch"};

   public final static String INDENT = "    ";
   public final static String END_OP = ";";
   public final static String END_OP_N = ";\n";
   public final static String ATTRIBUTE_SEP = ",";
   public final static String ATTRIBUTE_AFFECT = "="; 
   
   public final static String START_CODE = "{";
   public final static String START_CODE_N = "{\n";
   public final static String STOP_CODE = "}";
   public final static String STOP_CODE_N = "}\n";
   
   public static final String PRIVATE = "private" ;
    public static final String PUBLIC = "public" ;
    public static final String PROTECTED = "protected" ;
    public static final String INTEGER = "int" ;
    public static final String LONG = "long" ;
    public static final String BOOLEAN = "boolean" ;
    public static final String STATIC = "static" ;
    public static final String VOID = "void" ;
         
    public static boolean isAKeyword(String s) {
        for(int i=0; i<words.length; i++) {
            if (words[i].equals(s)) {
                return true;
            }
        }
        return false;
    } 
}