package ui.avatarsmd;

import java.util.LinkedHashSet;
import java.util.Set;

import ui.CDElement;
import ui.TGComponent;
import ui.TGConnector;
import ui.TrackingCDElementVisitor;

public class FindAvatarSMDComponentsToBeTranslatedVisitor extends TrackingCDElementVisitor {

  private final Set<TGComponent> componentsToBeTranslated;

  private final Set<TGConnector> prunedConnectors;

  public FindAvatarSMDComponentsToBeTranslatedVisitor() {
    componentsToBeTranslated = new LinkedHashSet<TGComponent>();
    prunedConnectors = new LinkedHashSet<TGConnector>();
  }

  @Override
  public boolean visit(final CDElement element) {
    if (!super.visit(element)) {
      return false;
    }

    if (element instanceof TGComponent && !(element instanceof TGConnector)) {
      final TGComponent component = (TGComponent) element;

      if (shouldBeTranslated(component)) {
        componentsToBeTranslated.add((TGComponent) element);
      }
    }

    return true;
  }

  public Set<TGComponent> getComponentsToBeTranslated() {
    return componentsToBeTranslated;
  }

  private boolean shouldBeTranslated(final TGComponent diagramCompo) {

    // Do not translate components contained in disabled components (e.g. composite
    // state machines)
    if (diagramCompo.getFather() != null && !diagramCompo.getFather().isEnabled()) {
      return false;
    }

    if (diagramCompo.isEnabled()) {
      return true;
    }

    // We don't know if the target component can accept more than one input
    // connections so we keep the node
    if (diagramCompo.getInputConnectors().size() > 1) {
      return true;
    }

    // We don't know if the source component can accept more than one output
    // connections so we keep the node
    if (diagramCompo.getOutputConnectors().size() > 1) {
      return true;
    }

    final AvatarSMDConnector inputConnector = (AvatarSMDConnector) diagramCompo.getInputConnectors().get(0);
    final TGComponent previousCompo = (TGComponent) inputConnector.getTGConnectingPointP1().getFather();

    // The previous component in the graph is not translated so we don't know if the
    // new input edge will be
    // null or enabled so we translate the component
    if (!componentsToBeTranslated.contains(previousCompo)) {
      return true;
    }

    final AvatarSMDTransitionInfo inputTransInfo = inputConnector.getAvatarSMDTransitionInfo();

    // The input transition is null or disabled so the component does not need to be
    // translated and the transition
    // can be merged with the output transition
    if (inputTransInfo.isNull() || !inputTransInfo.isEnabledNotNull()) {
      prunedConnectors.add(inputConnector);

      return false;
    }

    if (diagramCompo.getOutputConnectors().isEmpty()) {
      return true;
    }

    final AvatarSMDConnector outputConnector = (AvatarSMDConnector) diagramCompo.getOutputConnectors().get(0);

    final AvatarSMDTransitionInfo outputTransInfo = outputConnector.getAvatarSMDTransitionInfo();

    // The output transition is null or disabled so the component does not need to
    // be translated and the transition
    // can be merged with the input transition
    if (outputTransInfo.isNull() || !inputTransInfo.isEnabledNotNull()) {
      prunedConnectors.add(outputConnector);

      return false;
    }

    return true;
  }

  public Set<TGConnector> getPrunedConnectors() {
    return prunedConnectors;
  }
}
