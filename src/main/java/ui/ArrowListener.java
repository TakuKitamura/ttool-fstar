package ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ArrowListener implements KeyListener {
	
	private TDiagramPanel tdp;
	
	public ArrowListener(TDiagramPanel t) {
		tdp = t;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_UP)
			tdp.upComponent();
		if (arg0.getKeyCode() == KeyEvent.VK_DOWN)
			tdp.downComponent();
		if (arg0.getKeyCode() == KeyEvent.VK_LEFT)
			tdp.leftComponent();
		if (arg0.getKeyCode() == KeyEvent.VK_RIGHT)
			tdp.rightComponent();			
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

}
