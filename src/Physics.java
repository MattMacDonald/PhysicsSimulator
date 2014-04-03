import java.awt.Point;
import java.util.ArrayList;

public class Physics {
	public static final double GRAVITY = .5;

	private ArrayList<Particle> allParticles;
	private ArrayList<Integer> collided;

	public Physics(ArrayList<Particle> allParticles) {
		this.allParticles = allParticles;
		this.collided = new ArrayList<Integer>();
		for(int i = 0; i < this.allParticles.size(); ++i){
			collided.add(-1);
		}
	}
	
	public void doPhysics(Particle p){ // calls all major physics functions on the provided particle
		gravity(p);
		checkBounds(p);
		collider(p);
	}

	public void gravity(Particle p) { // applies gravity by adding to the yspeed of the particle
		if (p.getLocation().y + p.getDiameter() < 745) {
			p.setSpeed(p.getXSpeed(), p.getYSpeed() + GRAVITY);
		}
	}

	public void checkBounds(Particle p) {//check if the particle is within the bounds of the screen
		double tempX = p.getXSpeed();
		double tempY = p.getYSpeed();
		if (p.getLocation().x < 0) {
			if (p.getXSpeed() < 0) {
				tempX = -p.getXSpeed() * p.getElast();
			} else if (p.getXSpeed() == 0) {
				p.setLocation(p.getDiameter(), p.getLocation().y);
			}
		}
		if (p.getLocation().y < 25) {
			if (p.getYSpeed() < 0) {
				tempY = -p.getYSpeed() * p.getElast();
			} else if (p.getYSpeed() == 0) {
				p.setLocation(p.getLocation().x, 25 + p.getDiameter());
			}
		}
		if (p.getLocation().x + p.getDiameter() > 750) {
			if (p.getXSpeed() > 0) {
				tempX = -p.getXSpeed() * p.getElast();
			} else if (p.getXSpeed() == 0) {
				p.setLocation(749 - p.getDiameter(), p.getLocation().y);
			}
		}
		if (p.getLocation().y + p.getDiameter() > 750) {
			if (p.getYSpeed() > 0) {
				tempY = -p.getYSpeed() * p.getElast();
			} else if (p.getYSpeed() == 0) {
				p.setLocation(p.getLocation().x, 749 - p.getDiameter());
			}
		}
		
		p.setSpeed(tempX, tempY);
	}

	// calls methods to check for collisions, and to properly handle them
	public void collider(Particle p) {
		/**
		if (p.getCollided()) {
			react(p, allParticles.get(collided.get(allParticles.indexOf(p))));
		}

		else {
		*/
			ArrayList<Double> maycollide = new ArrayList<Double>();

			Point q1, q2 = new Point();
			Point v1 = new Point();
			Point v2 = new Point();

			double d, dr;

			q1 = p.getLocation();
			q2.setLocation(p.getLocation().x + p.getXSpeed() + p.getDiameter(),
					p.getLocation().y + p.getYSpeed() + p.getDiameter());

			for (Particle b : allParticles) {
				d = Math.pow((p.getTSpeed() + b.getDiameter() + b.getTSpeed()),
						2);
				v1 = b.getLocation();
				v2.setLocation(
						b.getLocation().x + b.getXSpeed() + b.getDiameter(),
						b.getLocation().y + b.getYSpeed() + b.getDiameter());
				dr = (v1.x - q1.x) * (v1.x - q1.x) + (v1.y - q1.y)
						* (v1.y - q1.y);
				if (b == p) {
					maycollide.add(-2.);
				} else if (dr > d) {
					// System.out.println("too far");
					maycollide.add(-1.);
				} else if (dr < (p.getDiameter() + b.getDiameter()) / 2  - 2) {
					maycollide.add((double) p.getLocation().x);
					break;
				} else {
					// System.out.println(willCollide(q1, q2, v1, v2));
					maycollide.add(willCollide(q1, q2, v1, v2));
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
			// System.out.println("index: " + index + ", min: " + min);
			if (index < 0) {
				return;
			}
			collide(p, allParticles.get(index), min);

//			if (index > this.collided.size() ||
//				this.allParticles.indexOf(p) > this.collided.size()) {
//				return;
//			}
			/**
			this.collided.ensureCapacity(allParticles.size());
			this.allParticles.ensureCapacity(this.collided.size());
			this.collided.set(this.allParticles.indexOf(p), index);
			this.collided.set(index, this.allParticles.indexOf(p));
			*/
		}
	// }

	// determines if two particles collide during the next frame, returns the x
	// value of where they collide, -1 if they dont
	public double willCollide(Point q1, Point q2, Point v1, Point v2) {
		double a1, a2, b1, b2, x0 = 0;
		if (q1.x == q2.x) {
			if (v1.x == v2.x) {
				if (q1.x == v1.x) {
					return (v1.y >= min(q1.y, q2.y) && v1.y <= max(q1.y, q2.y) || v2.y >= min(
							q1.y, q2.y) && v2.y <= max(q1.y, q2.y)) ? q1.x : -1;
				} else {
					return -1;
				}
			} else {
				a2 = (v2.y - v1.y) / (v2.x - v1.x);
				b2 = v1.y - a2 * v1.x;
				x0 = q1.x;
			}
		} else if (v1.x == v2.x) {
			a1 = (q2.y - q1.y) / (q2.x - q1.x);
			b1 = q1.y - a1 * q1.x;
			x0 = v1.x;
		} else {
			a1 = (q2.y - q1.y) / (q2.x - q1.x);
			b1 = q1.y - a1 * q1.x;
			a2 = (v2.y - v1.y) / (v2.x - v1.x);
			b2 = v1.y - a2 * v1.x;
			if (a1 == a2) {
				if (b1 == b2) {
					return (v1.x >= min(q1.x, q2.x) && v1.x <= max(q1.x, q2.x) || v2.x >= min(
							q1.x, q2.x) && v2.x <= max(q1.x, q2.x)) ? x0 : -1;
				} else {
					return -1;
				}
			} else {
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

		p2.setLocation(p.getLocation().x + p.getXSpeed() + p.getDiameter(),
				p.getLocation().y + p.getYSpeed() + p.getDiameter());
		b2.setLocation(b.getLocation().x + b.getXSpeed() + b.getDiameter(),
				b.getLocation().y + b.getYSpeed() + b.getDiameter());

		ArrayList<Double> np = p.normalize(p2, p1);
		ArrayList<Double> nb = b.normalize(b2, b1);

		return Math.toDegrees(Math.acos(np.get(0) * nb.get(0) + np.get(1)
				* nb.get(1)));
	}

	/**
	 * public ArrayList<Point> getRect(Particle p){ Point q1 = new Point(), q2 =
	 * new Point();
	 * 
	 * q1.setLocation(p.getLocation().x + p.getDiameter() / 2, p.getLocation().y
	 * + p.getDiameter() /2); q2.setLocation(p.getLocation().x + p.getXSpeed() +
	 * p.getDiameter() / 2, p.getLocation().y + p.getYSpeed() + p.getDiameter()
	 * /2);
	 * 
	 * double a = p.makeDirection(q1, q2); double a1 = a + 90; double x =
	 * Math.cos(Math.toRadians(a)); double y = Math.sin(Math.toRadians(a));
	 * double r = p.getDiameter() / 2; ArrayList<Point> corners = new
	 * ArrayList<Point>();
	 * 
	 * Point c1 = new Point(), c2 = new Point(), c3 = new Point(), c4 = new
	 * Point(); c1.setLocation(q1.x + x * r, q1.y + y * r); c2.setLocation(q2.x
	 * + x * r, q2.y + y * r); c3.setLocation(q2.x - x * r, q2.y - y * r);
	 * c4.setLocation(q1.x - x * r, q1.y - y * r);
	 * 
	 * corners.add(c1); corners.add(c2); corners.add(c3); corners.add(c4);
	 * 
	 * return corners;
	 * 
	 * }
	 */

	public void collide(Particle p, Particle b, double x0) {
		double pxl, bxl;
		// /**
		p.setCollided(true);
		b.setCollided(true);
		double t = (p.getDiameter() + b.getDiameter()) / 2;
		double dotd = dotProd(p, b); // angle between p, b
		double pdir = Math.toRadians(180 + p.getDirection());	//p vector direction
		double bdir = Math.toRadians(180 + b.getDirection()); //b vector direction
		double theta = Math.toRadians(dotd/2) + Math.random(); //angle between dist vector and p
		double tantheta = Math.tan(theta); // tan(theta)
		double dist = (t * tantheta) / (1 + (tantheta * tantheta)); //the distance along theta from x0 to the normal between p and b == t
		double px = (t * tantheta * tantheta) / (1 + (tantheta * tantheta)); // distance from end of dist vector to p
		double bx = t - px; // distance from end of dist vector to b
		double adist = theta + min(pdir, bdir); // the angle realative to xy plane for dist vector
		if(px < bx){
			pxl = x0 + ((Math.cos(adist) * dist) + (Math.cos(adist + 90) * px)); // the x value of the combined vectors dist and px subtracted from x0
			bxl = x0 + ((Math.cos(adist) * dist) + (Math.cos(adist - 90) * bx)); // the x value of the combined vectors dist and bx subtracted from x0
		}
		else{
			pxl = x0 + ((Math.cos(adist) * dist) + (Math.cos(adist - 90) * px)); // the x value of the combined vectors dist and px subtracted from x0
			bxl = x0 + ((Math.cos(adist) * dist) + (Math.cos(adist + 90) * bx)); // the x value of the combined vectors dist and bx subtracted from x0
		}
		double fp = (pxl - p.getLocation().x) / (p.getXSpeed()); // fraction of p vector traveled
		double fb = (bxl - b.getLocation().x) / (b.getXSpeed()); // fraction of b vector traveled
		p.setLocation(pxl, p.getLocation().y + fp * p.getYSpeed()); // set the location to the calculated x value and the fraction of the y value that keeps p on p vector
		b.setLocation(bxl, b.getLocation().y + fb * b.getYSpeed()); // set the location to the calculated x value and the fraction of the y value that keeps b on b vector
		// System.out.println("fp: " + fp + ", fb: " + fb + ", pxl: " + pxl +
		// ", bxl: " + bxl);
		p.setVelocity(30 * Math.random() - 15, -p.getXSpeed());
		b.setVelocity(30 * Math.random() - 15, -b.getXSpeed());
		// System.out.println("p's direction: " + p.getDirection() +
		// ", tspeed: " + p.getTSpeed()
		// + ", xspeed: " + p.getXSpeed() + ", yspeed: " + p.getYSpeed());

		// System.out.println("b's direction: " + b.getDirection() +
		// ", tspeed: " + b.getTSpeed()
		// + ", xspeed: " + b.getXSpeed() + ", yspeed: " + b.getYSpeed());

		// y = t*tan(theta)/1 + tan2(theta)
		// x = t* tan^2/ (1 + tan2 )

		// shift down the line starting at x0 with angle dotd, and find where
		// the
		// magnitude of the normal to dotd == the radius of p + the radius of d
		// */

	}

	public void react(Particle p, Particle b) {
		double phi, theta, px, py, bx, by;
	}
	
	public ArrayList<Particle> getAllParticles() {
		return this.allParticles;
	}
}
