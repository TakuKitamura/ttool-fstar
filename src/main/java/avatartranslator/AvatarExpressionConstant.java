package avatartranslator;

import avatartranslator.modelchecker.SpecificationBlock;
import avatartranslator.modelchecker.SpecificationState;

public class AvatarExpressionConstant implements AvatarExpressionAttributeInterface {
    int value;
    
    
    public AvatarExpressionConstant(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
 

    @Override
    public int getValue(SpecificationState ss) {
        return value;
    }

    @Override
    public int getValue(SpecificationBlock sb) {
        return value;
    }

    @Override
    public int getValue(int[] attributesValues) {
        return value;
    }

    @Override
    public void setValue(SpecificationState ss, int value) {
    }

    @Override
    public void setValue(SpecificationBlock sb, int value) {
    }

}
