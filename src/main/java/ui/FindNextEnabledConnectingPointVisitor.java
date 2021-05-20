package ui;

import java.util.Set;

public class FindNextEnabledConnectingPointVisitor extends TrackingCDElementVisitor {

  private final Set<TGConnector> disabledConnectors;

  private TGConnectingPoint enabledConnectingPoint;

  private TGConnectingPoint previousEnabledConnectingPoint;

  public FindNextEnabledConnectingPointVisitor(final Set<TGConnector> disabledConnectors) {
    super();

    this.disabledConnectors = disabledConnectors;
    enabledConnectingPoint = null;
  }

  @Override
  public boolean visit(final CDElement element) {
    if (!super.visit(element)) {
      return false;
    }

    if (element instanceof TGConnector) {
      final TGConnector connector = (TGConnector) element;

      if (pruneConnector(connector)) {
        disabledConnectors.add((TGConnector) element);

        return true;
      }

      enabledConnectingPoint = previousEnabledConnectingPoint;

      return false;
    }

    if (element instanceof TGConnectingPoint) {
      final TGConnectingPoint point = (TGConnectingPoint) element;
      final CDElement father = point.getFather();
      previousEnabledConnectingPoint = point;

      if (!pruneElement(father)) {
        enabledConnectingPoint = point;

        return false;
      }
    }

    return true;
  }

  protected boolean pruneElement(final CDElement diagramElement) {
    return !diagramElement.isEnabled() || !(diagramElement instanceof TGComponent)
        || diagramElement instanceof TGConnector;
  }

  protected boolean pruneConnector(final TGConnector connector) {
    return true;
  }

  public TGConnectingPoint getEnabledComponentPoint() {
    return enabledConnectingPoint;
  }

  public Set<TGConnector> getDisabledConnectors() {
    return disabledConnectors;
  }
}
