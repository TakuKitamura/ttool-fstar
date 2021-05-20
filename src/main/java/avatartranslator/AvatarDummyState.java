package avatartranslator;

/**
 * Issue #69: Created for component disabling. When a state machine element is
 * located between transitions that have guards and actions, we need to keep a
 * node to ensure that these are evaluated / executed in the same sequence
 * leading to the same semantics. We create replace the disabled node with this
 * dummy state that will have no impact on the generation of other
 * specifications (UPPAAL, C Code, Proverif, etc..).
 * 
 * @author dblouin
 *
 */
public class AvatarDummyState extends AvatarState {

  public AvatarDummyState(final String name, final Object _referenceObject) {
    super(name + "_converted_to_dummy_state", _referenceObject);
  }
}
