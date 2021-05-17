package textcollage;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Movement implements MouseListener, MouseMotionListener{
	private int X, Y;
	
	public Movement(Component... pns) {
		for (Component panel : pns) {
			panel.addMouseListener(this);
			panel.addMouseMotionListener(this);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		e.getComponent().setLocation((e.getX()+e.getComponent().getX())-X, (e.getY()+e.getComponent().getY())-Y);
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		X = e.getX();
		Y = e.getY();
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
