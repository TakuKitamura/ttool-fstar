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
    private int accessIndex;
    private int accessOffset;
    private AvatarStateMachineElement state;
    private String s;
    private boolean isState;
    private boolean error;
    
    
    public AvatarExpressionAttribute(AvatarSpecification spec, String s) {
        this.s = s;
        isState = false;
        
        error = !initAttributes(spec);
    }
    
    public AvatarExpressionAttribute(AvatarBlock block, String s) {
        this.s = s;
        isState = false;
        
        error = !initAttributes(block);
    }
    
    public AvatarExpressionAttribute(AvatarBlock block, AvatarStateMachineElement asme) {
        this.s = asme.name;
        isState = true;
        state = asme;
        error = false;
        accessIndex = -1;
        accessOffset = -1;
        block = null;
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
        
        if (block == null) {
            return false;
        }
        
        blockIndex = spec.getBlockIndex(block);
        
        int attributeIndex = block.getIndexOfAvatarAttributeWithName(fieldString);
        
        accessOffset = -1;
        
        if (attributeIndex == -1) {
            // state?
            state = block.getStateMachine().getStateWithName(fieldString);
            if (state == null) {
                return false;
            }
            isState = true;
            accessIndex = block.getStateMachine().getIndexOfState((AvatarStateElement) state);
        } else {
            int offset = block.getBooleanOffset();
            if (offset == -1 || attributeIndex < offset) {
                accessIndex = attributeIndex + SpecificationBlock.ATTR_INDEX;
            } else {
                accessIndex = SpecificationBlock.ATTR_INDEX + offset + ((attributeIndex - offset) / 32);
                accessOffset = (attributeIndex - offset) % 32;
            }
        }
        return true;
    }
    
    private boolean initAttributes(AvatarBlock block) {
        //Extract Attribute
        if (block == null) {
            return false;
        }
        
        this.block = block;
        this.blockIndex = -1; //not initialized
        
        int attributeIndex = block.getIndexOfAvatarAttributeWithName(s);
        
        accessOffset = -1;
        
        if (attributeIndex == -1) {
            // state?
            state = block.getStateMachine().getStateWithName(s);
            if (state == null) {
                return false;
            }
            isState = true;
            accessIndex = block.getStateMachine().getIndexOfState((AvatarStateElement) state);
        } else {
            int offset = block.getBooleanOffset();
            if (offset == -1 || attributeIndex < offset) {
                accessIndex = attributeIndex + SpecificationBlock.ATTR_INDEX;
            } else {
                accessIndex = SpecificationBlock.ATTR_INDEX + offset + ((attributeIndex - offset) / 32);
                accessOffset = (attributeIndex - offset) % 32;
            }
        }
        return true;
    }
    
    public boolean hasError() {
        return error == true;
    }
 
    public int getValue(SpecificationState ss) {
        int value;
        
        if (isState) {
            if (ss.blocks == null || accessIndex == -1) {
                return 0;
            }
            if (ss.blocks[blockIndex].values[SpecificationBlock.STATE_INDEX] == accessIndex) {
                return 1;
            } else {
                return 0;
            }
        }
        
        if (accessOffset == -1) {
            value = ss.blocks[blockIndex].values[accessIndex];
        } else {
            value = (ss.blocks[blockIndex].values[accessIndex] >> accessOffset) & 1;
        }
        
        return value;
    }
    
    public int getValue(SpecificationState ss, AvatarStateMachineElement asme) {
        int value;
        
        if (isState) {
            return (state == asme) ? 1 : 0;
        }
        
        if (accessOffset == -1) {
            value = ss.blocks[blockIndex].values[accessIndex];
        } else {
            value = (ss.blocks[blockIndex].values[accessIndex] >> accessOffset) & 1;
        }
        
        return value;
    }
    
    public int getValue(SpecificationBlock sb) {
        int value;
        
        if (isState) {
            if (sb == null || accessIndex == -1) {
                return 0;
            }     
            if (sb.values[SpecificationBlock.STATE_INDEX] == accessIndex) {
                return 1;
            } else {
                return 0;
            }
        }
        
        if (accessOffset == -1) {
            value = sb.values[accessIndex];
        } else {
            value = (sb.values[accessIndex] >> accessOffset) & 1;
        }
        
        return value;
    }
    
    public int getValue(int[] attributesValues) {
        int value;
        
        if (isState) {
            return 0;
        }
        
        //Cancel offset based on Specification Blocks
        if (accessOffset == -1) {
            value = attributesValues[accessIndex - SpecificationBlock.ATTR_INDEX];
        } else {
            value = (attributesValues[accessIndex - SpecificationBlock.ATTR_INDEX] >> accessOffset) & 1;
        }
        
        return value;
    }
    
    public void setValue(SpecificationState ss, int value) {        
        if (isState) {
            return;
        }
        
        if (accessOffset == -1) {
            ss.blocks[blockIndex].values[accessIndex] = value;
        } else {
            ss.blocks[blockIndex].values[accessIndex] ^= ((-(value & 1))^ ss.blocks[blockIndex].values[accessIndex]) & (1 << accessOffset);
        }
    }
    
    public void setValue(SpecificationBlock sb, int value) {      
        if (isState) {
            return;
        }
                
        if (accessOffset == -1) {
            sb.values[accessIndex] = value;
        } else {
            sb.values[accessIndex] ^= ((-(value & 1))^ sb.values[accessIndex]) & (1 << accessOffset);
        }
    }
    
    //Link state to access index in the state machine
    public void linkState() {
        if (isState) {
            if (block != null) {
                accessIndex = block.getStateMachine().getIndexOfState((AvatarStateElement) state);
            } else {
                accessIndex = -1;
            }
        }
    }
    
    
    public int getAttributeType() {
        if (isState) {
            return AvatarExpressionSolver.IMMEDIATE_BOOL;
        } else if (accessOffset == -1) {
            if (block.getAttribute(accessIndex - SpecificationBlock.ATTR_INDEX).getType() == AvatarType.BOOLEAN) {
                return AvatarExpressionSolver.IMMEDIATE_BOOL;
            } else {
                return AvatarExpressionSolver.IMMEDIATE_INT;
            }
        } else {
            return AvatarExpressionSolver.IMMEDIATE_BOOL;
        }
    }
    
    
    public boolean isState() {
        return isState;
    }
    
    public String toString() {
        return s;
    }
    
}
