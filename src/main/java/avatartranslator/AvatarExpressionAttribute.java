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

import avatartranslator.modelchecker.SpecificationBlock;
import avatartranslator.modelchecker.SpecificationState;

/**
 * Class AvatarExpressionAttribute
 * Avatar Expression Attribute
 * Creation: 17/04/2020
 *
 * @author Alessandro TEMPIA CALVINO
 * @version 1.0 17/04/2020
 */
public class AvatarExpressionAttribute {
    private AvatarBlock block;
    private int blockIndex;
    private int attributeIndex;
    private int accessIndex;
    private String s;
    private boolean isNegated;
    private boolean isNot;
    private boolean error;
    
    
    public AvatarExpressionAttribute(AvatarSpecification spec, String s) {
        this.s = s;
        isNegated = false;
        isNot = false;
        
        if (s.startsWith("not(")) {
            //not(variable)
            isNot = true;
            this.s = s.substring(3).trim();
            while (this.s.startsWith("(") && this.s.endsWith(")")){
                this.s = this.s.substring(1, this.s.length() - 1);
            }
        }
        if (s.startsWith("-")) {
            //not(variable)
            isNegated = true;
            this.s = s.substring(1).trim();
        }
        
        error = !initAttributes(spec);
    }
    
    public AvatarExpressionAttribute(AvatarBlock block, String s) {
        this.s = s;
        isNegated = false;
        isNot = false;
        
        if (s.startsWith("not(")) {
            //not(variable)
            isNot = true;
            this.s = s.substring(3).trim();
            while (this.s.startsWith("(") && this.s.endsWith(")")){
                this.s = this.s.substring(1, this.s.length() - 1);
            }
        }
        if (s.startsWith("-")) {
            //not(variable)
            isNegated = true;
            this.s = s.substring(1).trim();
            while (this.s.startsWith("(") && this.s.endsWith(")")){
                this.s = this.s.substring(1, this.s.length() - 1);
            }
        }
        
        error = !initAttributes(block);
    }
    
    
    private boolean initAttributes(AvatarSpecification spec) {
        //Extract Block and Attribute
        String[] splitS;
        String blockString;
        String fieldString;
        
        if (spec == null) {
            return false;
        }
        
        if (s.matches(".+\\..+")) {
            splitS = s.split("\\.");
            blockString = splitS[0];
            fieldString = splitS[1];
        } else {
            return false;
        }
        
        block = spec.getBlockWithName(blockString);
        
        if (blockIndex == -1) {
            return false;
        }
        
        blockIndex = spec.getBlockIndex(block);
        
        attributeIndex = block.getIndexOfAvatarAttributeWithName(fieldString);
        
        if (attributeIndex == -1) {
            return false;
        }
        accessIndex = attributeIndex + SpecificationBlock.ATTR_INDEX;
        return true;
    }
    
    private boolean initAttributes(AvatarBlock block) {
        //Extract Attribute
        if (block == null) {
            return false;
        }
        
        this.block = block;
        this.blockIndex = -1; //not initialized
        
        attributeIndex = block.getIndexOfAvatarAttributeWithName(s);
        
        if (attributeIndex == -1) {
            return false;
        }
        
        accessIndex = attributeIndex + SpecificationBlock.ATTR_INDEX;
        return true;
    }
    
    public boolean hasError() {
        return error == true;
    }
 
    public int getValue(SpecificationState ss) {
        int value = ss.blocks[blockIndex].values[accessIndex];
        
        if (isNot) {
            value = (value == 0) ? 1 : 0;
        } else if (isNegated) {
            value = -value;
        }
        
        return value;
    }
    
    public int getValue(SpecificationBlock sb) {
        int value = sb.values[accessIndex];
        
        if (isNot) {
            value = (value == 0) ? 1 : 0;
        } else if (isNegated) {
            value = -value;
        }
        
        return value;
    }
    
    public String toString() {
        String res = "";
        if (isNot) {
            res = "not(" + this.s + ")";
        } else if (isNegated) {
            res += "-" + this.s;
        } else {
            res = s;
        }
         
        return res;
    }
    
}
