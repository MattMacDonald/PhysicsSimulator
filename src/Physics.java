import java.awt.Point;
import java.util.ArrayList;

public class Physics {
	public static final double GRAVITY = .5;

	private ArrayList<Particle> allParticles; // all particles
	private ArrayList<Integer> collided; // particles that have collided and the
											// index of the particle they
											// collided with

	public Physics(ArrayList<Particle> allParticles) {
		this.allParticles = allParticles;
		this.collided = new ArrayList<Integer>();
		for (int i = 0; i < this.allParticles.size(); ++i) {
			collided.add(-1);
		}
	}

	public void doPhysics(Particle p) { // calls all major physics functions on
										// the provided particle
		gravity(p);
		checkBounds(p);
		collider(p);
	}

	public void gravity(Particle p) { // applies gravity by adding to the yspeed
										// of the particle
		if (p.getLocation().y + p.getDiameter() < 749) {
			p.setSpeed(p.getXSpeed(), p.getYSpeed() + GRAVITY);
		}
	}

	public void checkBounds(Particle p) {// check if the particle is within the
											// bounds of the screen
		double tempX = p.getXSpeed(); // possibly changed xspeed value
		double tempY = p.getYSpeed(); // possibly changed yspeed value
		if (p.getLocation().x < 0) {
			if (p.getXSpeed() < 0) {
				tempX = -p.getXSpeed() * p.getElast();
			}
			p.setLocation(p.getDiameter(), p.getLocation().y);
		}
		if (p.getLocation().y < 25) {
			if (p.getYSpeed() < 0) {
				tempY = -p.getYSpeed() * p.getElast();
			}
			p.setLocation(p.getLocation().x, 25 + p.getDiameter());
		}
		if (p.getLocation().x + p.getDiameter() > AParticleSimulator.SCREEN_SIZE.width) {
			if (p.getXSpeed() > 0) {
				tempX = -p.getXSpeed() * p.getElast();
			} else if (p.getXSpeed() == 0) {
				//tempX = -1;
			}
			p.setLocation(749 - p.getDiameter(), p.getLocation().y);
		}
		if (p.getLocation().y + p.getDiameter() > AParticleSimulator.SCREEN_SIZE.height) {
			if (p.getYSpeed() > 0) {
				tempY = -p.getYSpeed() * p.getElast();
			}
			p.setLocation(p.getLocation().x,
					AParticleSimulator.SCREEN_SIZE.width - p.getDiameter());
		}

		p.setSpeed(tempX, tempY); // in case tempX or tempY changed, corrects
									// p.getXSpeed() and p.getYSpeed()
	}

	// calls methods to check for collisions, and to properly handle them
	public void collider(Particle p) {
		
		if (p.getCollided()) { 
			react(p,allParticles.get(collided.get(allParticles.indexOf(p))));
		}
		
		else {
		 
		ArrayList<Double> maycollide = new ArrayList<Double>();

		Point q1, q2 = new Point();
		Point v1 = new Point();
		Point v2 = new Point();

		double d, dr; // d is a range, based on the speeds of the particles, to
						// ignore particles because they cant collide to
		// reduce the ammount of computation per frame
		// dr is the distance between the two particles
		// q1 and q2 are the start and end points respectively of the vector for
		// p
		// v1 and v2 are the start and end points respectively of the vector for
		// b
		q1 = p.getLocation();
		q2.setLocation(p.getLocation().x + p.getXSpeed() + p.getDiameter(),
				p.getLocation().y + p.getYSpeed() + p.getDiameter());

		for (Particle b : allParticles) {
			d = Math.pow((p.getTSpeed() + b.getDiameter() + b.getTSpeed()), 2);
			v1 = b.getLocation();
			v2.setLocation(b.getLocation().x + b.getXSpeed() + b.getDiameter(),
					b.getLocation().y + b.getYSpeed() + b.getDiameter());
			dr = (v1.x - q1.x) * (v1.x - q1.x) + (v1.y - q1.y) * (v1.y - q1.y);
			if (b == p) { // dont collide with your self
				maycollide.add(-2.);
			} else if (dr > d) { // if they are too far apart
				maycollide.add(-1.);
			} else if (dr < (p.getDiameter() + b.getDiameter()) / 2) { // if they overlap
				maycollide.add((double) p.getLocation().x);
			} else {
				maycollide.add(willCollide(p, b));
			}
		}
		double min = 750;
		int index = -1;
		for (int i = 0; i < maycollide.size(); ++i) {
			if (maycollide.get(i) > -1
					&& Math.abs(p.getLocation().x - maycollide.get(i)) < min) {
				min = maycollide.get(i);
				index = i;
			}
		}
		if (index < 0) {
			return;
		}
		collide(p, allParticles.get(index), min);

		// if (index > this.collided.size() ||
		// this.allParticles.indexOf(p) > this.collided.size()) {
		// return;
		// }
		
		while(allParticles.size() > collided.size()){
			collided.add(-1);
		}
		this.collided.set(this.allParticles.indexOf(p), index);
		this.collided.set(index, this.allParticles.indexOf(p));
		}

	}

	// }

	// determines if two particles collide during the next frame, returns the x
	// value of where they collide, -1 if they dont
	public double willCollide(Particle a, Particle b) { // determines if two
														// particles will
														// collide
		Point q1 = new Point(), q2 = new Point(), v1 = new Point(), v2 = new Point();
		double ca = Math.cos(a.getDirection()), sa = Math.sin(a.getDirection());
		double cb = Math.cos(b.getDirection()), sb = Math.sin(b.getDirection());
		double ar = a.getDiameter() / 2, br = b.getDiameter() / 2;

		if (a.getSide(b) == 1) {
			q1.setLocation(a.getLocation().x + ca * ar, a.getLocation().y + sa
					* ar);
			q2.setLocation(a.getLocation().x + ca * ar + a.getXSpeed(),
					a.getLocation().y + sa * ar + a.getYSpeed());
		} else if (a.getSide(b) == -1) {
			q1.setLocation(a.getLocation().x - ca * ar, a.getLocation().y - sa
					* ar);
			q2.setLocation(a.getLocation().x - ca * ar + a.getXSpeed(),
					a.getLocation().y - sa * ar + a.getYSpeed());
		} else { // these first three checks are to determine which side
					// (relative to the velocity vector) b is on, looking from a
			q1.setLocation(a.getLocation().x, a.getLocation().y);
			q2.setLocation(a.getLocation().x + a.getXSpeed(), a.getLocation().y
					+ a.getYSpeed());
		}
		if (b.getSide(a) == 1) {
			v1.setLocation(b.getLocation().x + cb * br, b.getLocation().y + sb
					* br);
			v2.setLocation(b.getLocation().x + cb * br + b.getXSpeed(),
					b.getLocation().y + sb * br + b.getYSpeed());
		} else if (b.getSide(a) == -1) {
			v1.setLocation(b.getLocation().x - cb * br, b.getLocation().y - sb
					* br);
			v2.setLocation(b.getLocation().x - cb * br + b.getXSpeed(),
					b.getLocation().y - sb * br + b.getYSpeed());
		} else {// these next three checks are to determine which side (relative
				// to the velocity vector) a is on, looking from b
			v1.setLocation(b.getLocation().x, b.getLocation().y);
			v2.setLocation(b.getLocation().x + b.getXSpeed(), b.getLocation().y
					+ b.getYSpeed());
		}
		double a1 = 0, a2 = 0, b1 = 0, b2 = 0, x0 = 0; // a1 = slope of first
														// vector, a2 = slope of
														// second vector, b1 =
														// intercept, b2 =
														// intercept, x0 = x
														// location of center
														// collision
		if (q1.x == q2.x) { // is line q vertical?
			if (v1.x == v2.x) { // is line v vertical?
				if (q1.x == v1.x) { // do they share x coordinates?
					return (v1.y >= min(q1.y, q2.y) && v1.y <= max(q1.y, q2.y) || v2.y >= min(
							q1.y, q2.y) && v2.y <= max(q1.y, q2.y)) ? q1.x : -1; // do
																					// their
																					// y
																					// coordinates
																					// overlap?
				} else {
					return -1; // if both vertical but not matching x
								// coordinates
				}
			} else {
				a2 = (v2.y - v1.y) / (v2.x - v1.x); // in the case that only q
													// is vertical, get slope
													// for v
				b2 = v1.y - a2 * v1.x; // y intercept of v
				x0 = q1.x; // x coordinate of intersection (if there is one)
			}
		} else if (v1.x == v2.x) { // q isn't vertical but v still might be
			a1 = (q2.y - q1.y) / (q2.x - q1.x); // get slope for q since v is
												// vertical
			b1 = q1.y - a1 * q1.x; // y intercept of q
			x0 = v1.x; // x coordinate of intersection (if there is one)
		} else { // now we know they both aren't vertical
			a1 = (q2.y - q1.y) / (q2.x - q1.x); // slope for q
			b1 = q1.y - a1 * q1.x; // intercept for q
			a2 = (v2.y - v1.y) / (v2.x - v1.x); // slope for v
			b2 = v1.y - a2 * v1.x; // intercept for v
			if (a1 == a2) { // are they parallel?
				if (b1 == b2) { // are they on the same line?
					return ((v1.x >= min(q1.x, q2.x) && v1.x <= max(q1.x, q2.x)) || (v2.x >= min(
							q1.x, q2.x) && v2.x <= max(q1.x, q2.x))) ? x0 : -1; // do
																				// they
																				// happen
																				// in
																				// the
																				// same
																				// interval
				} else {
					return -1; // nope, no collision here
				}
			} else { // cool they aren't parallel..
				x0 = -(b1 - b2) / (a1 - a2);
			}
		}
		return (x0 >= min(q1.x, q2.x) && x0 <= max(q1.x, q2.x)
				&& x0 >= min(v1.x, v2.x) && x0 <= max(v1.x, v2.x)) ? x0 : -1;
	}

	public double min(double a, double b) { // get smaller of 2 numbers
		return (a < b) ? a : b;
	}

	public double max(double a, double b) { // get larger of 2 numbers
		return (a > b) ? a : b;
	}

	public double dotProd(Particle p, Particle b) { // takes two particles and
													// returns the angle between
													// their vectors
		Point p1 = p.getLocation();
		Point p2 = new Point();
		Point b1 = b.getLocation();
		Point b2 = new Point();

		p2.setLocation(p.getLocation().x + p.getXSpeed() + p.getDiameter() / 2,
				p.getLocation().y + p.getYSpeed() + p.getDiameter() / 2);
		b2.setLocation(b.getLocation().x + b.getXSpeed() + b.getDiameter() / 2,
				b.getLocation().y + b.getYSpeed() + b.getDiameter() / 2);

		ArrayList<Double> np = Particle.normalize(p1, p2);
		ArrayList<Double> nb = Particle.normalize(b1, b2);

		return Math.toDegrees(Math.acos(np.get(0) * nb.get(0) + np.get(1)
				* nb.get(1)));
	}

	public void collide(Particle a, Particle b, double x0) {
		a.setCollided(true);
		b.setCollided(true);
		Point q1 = new Point(), v1 = new Point();
		double ca = Math.cos(a.getDirection()), sa = Math.sin(a.getDirection());
		double cb = Math.cos(b.getDirection()), sb = Math.sin(b.getDirection());
		double ar = a.getDiameter() / 2, br = b.getDiameter() / 2;

		if (a.getSide(b) == 1) {
			q1.setLocation(a.getLocation().x + ca * ar, a.getLocation().y + sa
					* ar);
		} else if (a.getSide(b) == -1) {
			q1.setLocation(a.getLocation().x - ca * ar, a.getLocation().y - sa
					* ar);
		} else { // these first three checks are to determine which side
					// (relative to the velocity vector) b is on, looking from a
			q1.setLocation(a.getLocation().x, a.getLocation().y);
		}
		if (b.getSide(a) == 1) {
			v1.setLocation(b.getLocation().x + cb * br, b.getLocation().y + sb
					* br);
		} else if (b.getSide(a) == -1) {
			v1.setLocation(b.getLocation().x - cb * br, b.getLocation().y - sb
					* br);
		} else {// these next three checks are to determine which side (relative
				// to the velocity vector) a is on, looking from b
			v1.setLocation(b.getLocation().x, b.getLocation().y);
		}

		double da, db, fa, fb, ya, yb;

		if (a.getXSpeed() == 0) {
			if (b.getXSpeed() == 0) {
				return;
			} else {
				db = x0 - v1.x;
				fb = db / b.getXSpeed();
				ya = v1.y + b.getYSpeed() * fb;
				a.setLocation(a.getLocation().x, ya);
				b.setLocation(b.getLocation().x + fb * b.getXSpeed(),
						b.getLocation().y + fb * b.getYSpeed());
			}
		} else if (b.getXSpeed() == 0) {
			da = x0 - q1.x;
			fa = da / a.getXSpeed();
			yb = q1.y + fa * a.getYSpeed();
			a.setLocation(a.getLocation().x + fa * a.getXSpeed(),
					a.getLocation().y + fa * a.getYSpeed());
			b.setLocation(b.getLocation().x, yb);
		} else {
			da = x0 - q1.x;
			db = x0 - v1.x;
			fa = da / a.getXSpeed();
			fb = db / b.getXSpeed();
			a.setLocation(a.getLocation().x + fa * a.getXSpeed(),
					a.getLocation().y + fa * a.getYSpeed());
			b.setLocation(b.getLocation().x + fb * b.getXSpeed(),
					b.getLocation().y + fb * b.getYSpeed());

		}

	}

	public void react(Particle p, Particle b) { // collision reaction, formula derived from site below
												// http://blogs.msdn.com/b/faber/archive/2013/01/09/elastic-collisions-of-balls.aspx
		double phi, theta1, theta2, v1, v2, vp1x, vp1y, vp2x, vp2y, u1x, u1y, u2x, u2y, up1x, up1y, up2x, up2y, m1, m2;
		m1 = p.getMass();
		m2 = b.getMass();
		phi = Particle.makeDirection(p.getLocation(), b.getLocation()) - 360;
		theta1 = p.getDirection();
		theta2 = b.getDirection();
		v1 = p.getTSpeed();
		v2 = b.getTSpeed();
		vp1x = v1 * Math.cos(theta1 - phi);
		vp1y = v1 * Math.sin(theta1 - phi);
		vp2x = v2 * Math.cos(theta2 - phi);
		vp2y = v2 * Math.sin(theta2 - phi);
		up1x = p.getElast() * (((m1 - m2) * vp1x + (m2 + m2) * vp2x) / (m1 + m2));
		up2x = b.getElast() * (((m1 + m1) * vp1x + (m2 - m1) * vp2x) / (m1 + m2));
		up1y = vp1y;
		up2y = vp2y;
		u1x = up1x * Math.cos(phi) + up1y * Math.sin(phi);
		u1y = up1x * Math.sin(phi) + up1y * Math.cos(phi);
		u2x = up2x * Math.cos(phi) + up2y * Math.sin(phi);
		u2y = up2x * Math.sin(phi) + up2y * Math.cos(phi);

		p.setSpeed(u1x, u1y);
		b.setSpeed(u2x, u2y);

	}

	public ArrayList<Particle> getAllParticles() { // gets allParticles
		return this.allParticles;
	}
}
