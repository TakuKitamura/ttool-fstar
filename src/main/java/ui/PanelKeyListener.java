package ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * KeyListener for TDiagramPanel
 * @author Fabien Tessier
 */

public class PanelKeyListener implements KeyListener {
	
	private TDiagramPanel tdp;
	
	public PanelKeyListener(TDiagramPanel t) {
		tdp = t;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.isShiftDown() && (tdp.mode == TDiagramPanel.SELECTED_COMPONENTS || tdp.mode == TDiagramPanel.MOVING_SELECTED_COMPONENTS)) {
			tdp.showSelectionZone = true;
			tdp.mode = TDiagramPanel.MOVING_SELECTED_COMPONENTS;
            tdp.setMovingSelectedComponents();
            tdp.repaint();
			if (arg0.getKeyCode() == KeyEvent.VK_UP) {
	            tdp.upComponents();
			}
			if (arg0.getKeyCode() == KeyEvent.VK_DOWN) {
	            tdp.downComponents();
			}
			if (arg0.getKeyCode() == KeyEvent.VK_LEFT) {
	            tdp.leftComponents();
			}
			if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
	            tdp.rightComponents();
			}
		}
		if (arg0.getKeyCode() == KeyEvent.VK_UP && arg0.isShiftDown())
			tdp.upComponent();
		if (arg0.getKeyCode() == KeyEvent.VK_DOWN && arg0.isShiftDown())
			tdp.downComponent();
		if (arg0.getKeyCode() == KeyEvent.VK_LEFT && arg0.isShiftDown())
			tdp.leftComponent();
		if (arg0.getKeyCode() == KeyEvent.VK_RIGHT && arg0.isShiftDown())
			tdp.rightComponent();
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (tdp.mode == TDiagramPanel.ADDING_CONNECTOR) {
                tdp.mode = TDiagramPanel.NORMAL;
                tdp.stopAddingConnector(true);
                tdp.getGUI().setEditMode();
                tdp.repaint();
            } 
			if (tdp.select){
            	tdp.getTdmm().setSelection(-1, -1);
                tdp.getGUI().setEditMode();
                tdp.repaint();
            }
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if ( !arg0.isShiftDown() && tdp.mode == TDiagramPanel.MOVING_SELECTED_COMPONENTS ) {
			tdp.showSelectionZone(tdp.currentX, tdp.currentY);
			tdp.mode = TDiagramPanel.SELECTED_COMPONENTS;
            tdp.setStopMovingSelectedComponents();
            tdp.getGUI().changeMade(tdp, TDiagramPanel.MOVE_COMPONENT);
            tdp.repaint();
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}
