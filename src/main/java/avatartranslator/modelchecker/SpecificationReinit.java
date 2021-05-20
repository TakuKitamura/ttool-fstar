package avatartranslator.modelchecker;

public class SpecificationReinit {
  protected SpecificationState initState;
  private boolean result;

  public SpecificationReinit(SpecificationState initState) {
    this.initState = initState;
    this.result = true;
  }

  public SpecificationState getInitState() {
    return initState;
  }

  public boolean getResult() {
    return result;
  }

  public void setResult(boolean result) {
    this.result = result;
  }

}
