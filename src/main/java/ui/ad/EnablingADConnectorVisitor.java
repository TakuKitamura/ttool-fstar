package ui.ad;

import ui.CDElement;
import ui.TGConnectingPoint;
import ui.TGConnector;
import ui.TrackingCDElementVisitor;

public class EnablingADConnectorVisitor extends TrackingCDElementVisitor {

  private final boolean enabled;

  public EnablingADConnectorVisitor(final boolean enabled) {
    super();

    this.enabled = enabled;
  }

  @Override
  public boolean visit(final CDElement element) {
    if (!super.visit(element)) {
      return false;
    }

    // Do not disable the stop
    if (element instanceof TADStopState) {
      return false;
    }

    if (element.canBeDisabled() && !(element instanceof TGConnector)) {
      element.setEnabled(enabled);
    }

    if (element instanceof TGConnectingPoint) {
      final TGConnectingPoint point = (TGConnectingPoint) element;
      final CDElement father = point.getFather();

      // Only continue if the point does not belongs to the inside of the loop because
      // it is managed by
      // the For itself
      if (father instanceof TADForLoop) {
        return point != ((TADForLoop) father).getInsideLoopConnectingPoint();
      }

      // Disabling a sequence results in disabling all its branches so only continue
      // if we are at entrance
      // if ( father instanceof TADSequence ) {
      // return point == ( (TADSequence) father ).getEnterSequenceConnectionPoint();
      // }
    }

    return true;
  }
}
