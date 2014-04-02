import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JFrame;

import java.util.ArrayList;
import java.awt.image.BufferStrategy;

public class DrawPanel{
	private JFrame frame;
	public Physics physics;
	
	public DrawPanel(JFrame frame) {
		this.frame = frame;
		ArrayList<Particle> allParticles = new ArrayList<Particle>();
		Point center = new Point(frame.getWidth() / 2, frame.getHeight() / 2);
		for(int i = 0; i < 150; i++){
			Point point = new Point();
			point.setLocation(frame.getWidth() * Math.random(), frame.getHeight() * Math.random());
			
			Particle particle = new Particle(point);
			double direction = particle.makeDirection(point, center);
			particle.setVelocity(direction, 10);
			
			allParticles.add(particle);
		}
		physics = new Physics(allParticles);
	}
	
	public void draw(){
		BufferStrategy bs = frame.getBufferStrategy();
		Graphics g = bs.getDrawGraphics();
		g.clearRect(0, 0, frame.getHeight(), frame.getHeight());
		for(Particle p : this.physics.getAllParticles()){
			//System.out.println(p + "'s xspeed: " + p.getXSpeed() + ", yspeed: " + p.getYSpeed());
			physics.doPhysics(p);
			p.draw(g);
			p.move();
		}
		g.dispose();
		bs.show();
	}
}