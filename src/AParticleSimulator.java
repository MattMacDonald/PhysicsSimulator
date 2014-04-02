import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;

public class AParticleSimulator implements Runnable{
	public final static String TITLE = "Matt's Simulator";
	public final static Dimension SCREEN_SIZE = new Dimension(750, 750);
	public final static Double smooth = 1.;
	
	public JFrame frame;
	public DrawPanel panel;
	public Physics physics;
	public JPanel mousepanel;
	public mousehandler mh;
	
	public static void main(String[] args){
		AParticleSimulator particleSim = new AParticleSimulator();
		new Thread(particleSim).start();
	}
	public AParticleSimulator(){
		frame = new JFrame(TITLE);
		frame.setSize(SCREEN_SIZE.width - 1, SCREEN_SIZE.height - 1);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.createBufferStrategy(3);
		
		mousepanel = new JPanel();
		frame.add(mousepanel, BorderLayout.CENTER);
		
		mh = new mousehandler();
		mousepanel.addMouseListener(mh);
		mousepanel.addMouseMotionListener(mh);
		
		panel = new DrawPanel(frame);
		
		run();
	}
	
	private class mousehandler implements MouseListener, MouseMotionListener{

		public void mouseDragged(MouseEvent e) {
			System.out.println("Mouse is dragging");
		}

		public void mouseMoved(MouseEvent e) {
			System.out.println("Mouse moved");
		}

		public void mouseClicked(MouseEvent e) {
			Point point = new Point();
			point.setLocation(e.getX() - 10, e.getY());
			panel.physics.getAllParticles().add(new Particle(point));
			String s = String.format("Clicked at %d, %d", e.getX(), e.getY());
			System.out.println(s);
		}

		public void mousePressed(MouseEvent e) {
			String s = String.format("Pressed at %d %d", e.getX(), e.getY());
			System.out.println(s);
		}
		
		public void mouseReleased(MouseEvent e) {
			String s = String.format("Released at %d %d", e.getX(), e.getY());
			System.out.println(s);
			
		}

		public void mouseEntered(MouseEvent e) {
			System.out.println("mouseEntered");
			
		}

		public void mouseExited(MouseEvent e) {
			System.out.println("mouseExited");
		}
		
	}
	
	public void run(){
		while(true){
			if(frame.getSize() != SCREEN_SIZE){
				frame.setSize(SCREEN_SIZE);;
			}
			panel.draw();
			try{
				Thread.sleep((int)(50));
			}
			catch(InterruptedException ie){
			}
		}
	}
}
