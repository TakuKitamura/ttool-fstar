package ui.ad;

import ui.CDElement;
import ui.TrackingCDElementVisitor;

public class EnablingADBranchVisitor extends TrackingCDElementVisitor {

  private final boolean enabled;

  public EnablingADBranchVisitor(final boolean enabled) {
    super();

    this.enabled = enabled;
  }

  /*
   * Disable everything of the branch (non-Javadoc)
   * 
   * @see ui.ICDElementVisitor#visit(ui.CDElement)
   */
  @Override
  public boolean visit(final CDElement element) {
    if (!super.visit(element)) {
      return false;
    }

    element.doSetEnabled(enabled);

    return true;
  }
}
