package avatartranslator;

import avatartranslator.modelchecker.SpecificationBlock;
import avatartranslator.modelchecker.SpecificationState;

public interface AvatarExpressionAttributeInterface {

  public int getValue(SpecificationState ss);

  public int getValue(SpecificationBlock sb);

  public int getValue(int[] attributesValues);

  public void setValue(SpecificationState ss, int value);

  public void setValue(SpecificationBlock sb, int value);

}
