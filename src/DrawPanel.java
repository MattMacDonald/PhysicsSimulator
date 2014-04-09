import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JFrame;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.awt.image.BufferStrategy;

public class DrawPanel{
	private JFrame frame;
	public Physics physics;
	
	public DrawPanel(JFrame frame) {
		this.frame = frame;
		CopyOnWriteArrayList<Particle> allParticles = new CopyOnWriteArrayList<Particle>();
		Point center = new Point(frame.getWidth() / 2, frame.getHeight() / 2);
		/**
		allParticles.add(new Particle(new Point(300, 400)));
		allParticles.add(new Particle(new Point(450, 400)));
		Point point1 = new Point();
		point1.setLocation(frame.getWidth() * Math.random(), frame.getHeight() * Math.random());
		allParticles.get(0).setVelocity(Particle.makeDirection(allParticles.get(0).getLocation(), center), 10);
		allParticles.get(1).setVelocity(Particle.makeDirection(allParticles.get(1).getLocation(), center), 10);
		*/
		for(int i = 0; i < 20; i++){
			Point point = new Point();
			point.setLocation(frame.getWidth() * Math.random(), frame.getHeight() * Math.random());
			
			Particle particle = new Particle(point);
			double direction = Particle.makeDirection(point, center);
			particle.setVelocity(direction, 10);
			
			allParticles.add(particle);
		}
		
		physics = new Physics(allParticles);
	}
	
	public void draw(){
		BufferStrategy bs = frame.getBufferStrategy();
		Graphics g = bs.getDrawGraphics();
		g.clearRect(0, 0, frame.getHeight(), frame.getHeight());
		for(int i = 0; i < physics.getAllParticles().size(); ++i){
			Particle p = physics.getAllParticles().get(i);
			//System.out.println(p + "'s xspeed: " + p.getXSpeed() + ", yspeed: " + p.getYSpeed());
			physics.doPhysics(p);
			p.draw(g);
			p.move();
		}
		g.dispose();
		bs.show();
	}
}