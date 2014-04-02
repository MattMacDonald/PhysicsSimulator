import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
public class Particle {
	private double mass, elast, cofric, xspeed, yspeed, tspeed;
	private Point location = new Point();
	private int diameter;
	private Color color;
	private boolean collided;
	private final static double MAX_SPEED = 9001, MIN_SPEED = -9001;
	private final static double MAX_MASS = 200, MIN_MASS = -MAX_MASS;
	private final static double MAX_ELAST = 1, MIN_ELAST = 0;
	private final static double MAX_COFRIC = 1, MIN_COFRIC = 0;
	private final static double MAX_DIRECTION = 360, MIN_DIRECTION = 0;
	private final static int MAX_DIAMETER = 50, MIN_DIAMETER = 2;
	private static final int FRAME_SIZE = 750;

	public final static double dmass = 5, delast = .5, dcofric = .5,
			dxspeed = 75, dyspeed = 75;
	public final static Point dlocation = new Point(750, 750);
	public final static int ddiameter = 10;
	public final static boolean dcollided = false;
	public final static Color dcolor = Color.RED;
	
	public Particle(){
		this(dmass, delast, dcofric, dxspeed * Math.random() - dxspeed / 2, dyspeed * Math.random() - dyspeed / 2, ddiameter, dcollided, dlocation, dcolor);
	}

	public Particle(Point point){
		this(dmass, delast, dcofric, 0, 0, ddiameter, dcollided, point, dcolor);
	}
	
	public Particle(double mass, double elast, double cofric, double x, double y, int diameter, boolean collided, Point location, Color color){
		this.mass = mass;
		this.elast = elast;
		this.cofric = cofric;
		this.color = color;
		this.xspeed = x;
		this.yspeed = y;
		this.tspeed = Math.sqrt(Math.abs(x * x + y * y));
		this.diameter = diameter;
		this.collided = collided;
		this.location.setLocation(location.x + this.diameter, location.y + this.diameter);
	}
	
	public double getXSpeed(){
		return this.xspeed;
	}

	public double getYSpeed(){
		return this.yspeed;
	}
	public double getTSpeed(){
		return this.tspeed;
	}
	public double getMass(){
		return this.mass;
	}
	
	public double getElast(){
		return this.elast;
	}
	
	public double getCofric(){
		return this.cofric;
	}
	
	public int getDiameter(){
		return this.diameter;
	}
	
	public Point getLocation(){
		return this.location;
	}
	
	public Color getColor(){
		return this.color;
	}
	
	public boolean getCollided(){
		return this.collided;
	}
	
	public double getDirection(){
		double direction = Math.toDegrees(Math.atan2(yspeed, xspeed));
		return (direction > 0) ? direction: direction + 360;
	}
	
	public void setSpeed(double x, double y){
		if(x > MIN_SPEED){
			this.xspeed = (x < MAX_SPEED) ? x: MAX_SPEED;
		}
		else{
			this.xspeed = MIN_SPEED;
		}
		if(y > MIN_SPEED){
			this.yspeed = (y < MAX_SPEED) ? y: MAX_SPEED;
		}
		else{
			this.yspeed = MIN_SPEED;
		}
		this.tspeed = Math.sqrt(Math.abs(x * x + y * y));
	}
	
	public void setVelocity(double direction, double speed){
		while(direction < MIN_DIRECTION){
			direction += MAX_DIRECTION;
		}
		direction = direction % MAX_DIRECTION;
		setSpeed(speed * Math.cos(Math.toRadians(direction)),
				speed * Math.sin(Math.toRadians(direction)));
	}
	
	public void setCollided(boolean collided){
		this.collided = collided;
	}
	
	public void setMass(double m){
		if(m > MIN_MASS){
			this.mass = (m < MAX_MASS) ? m: MAX_MASS;
		}
		else{
			this.mass = MIN_MASS;
		}
	}
	
	public void setElast(double e){
		if(e > MIN_ELAST){
			this.elast = (e < MAX_ELAST) ? e: MAX_ELAST;
		}
		else{
			this.mass = MIN_ELAST;
		}
	}
	
	public void setCofric(double c){
		if(c > MIN_COFRIC){
			this.cofric = (c < MAX_COFRIC) ? c: MAX_COFRIC;
		}
		else{
			this.cofric = MIN_COFRIC;
		}
	}
	
	public void setDiameter(int r){
		if(r > MIN_DIAMETER){
			this.diameter = (r < MAX_DIAMETER) ? r: MAX_DIAMETER;
		}
		else{
			this.diameter = MIN_DIAMETER;
		}
	}
	public void setLocation(double x, double y){
		double tempX, tempY;
		if(x > 0){
			tempX = (x < FRAME_SIZE) ? x: FRAME_SIZE;
		}
		else{
			tempX = 0;
		}
		if(y > 0){
			tempY = (y < FRAME_SIZE) ? y: FRAME_SIZE;
		}
		else{
			tempY = 0;
		}
		location.setLocation(tempX, tempY);
	}
	
	public void setColor(Color c){
		this.color = c;
	}
	
	public double makeDirection(Point q1, Point q2){
		ArrayList<Double> nq = normalize(q1, q2);
		double x = nq.get(0);
		double y = nq.get(1);
		if(y > 0){
			return Math.toDegrees(Math.acos(x));
		}
		else{
			return -Math.toDegrees(Math.acos(x));
		}
	}
	
	public void draw(Graphics g){
		Point q1 = new Point(), q2 = new Point();
		
		q1.setLocation(location.x + diameter / 2, location.y + diameter /2);
		q2.setLocation(location.x + xspeed + diameter / 2, location.y + yspeed + diameter /2);
		
		double a = makeDirection(q1, q2);
		a += 90;
		double x = Math.cos(Math.toRadians(a));
		double y = Math.sin(Math.toRadians(a));
		double r = diameter / 2;
		
		g.setColor(color);
		g.fillOval(location.x, location.y, diameter, diameter);
		g.setColor(Color.BLUE);
		g.drawLine(q1.x, q1.y, q2.x, q2.y);
		g.drawLine((int)(q1.x + x * r), (int)(q1.y + y * r), (int)(q2.x + x * r), (int)(q2.y + y * r));
		g.drawLine((int)(q1.x - x * r), (int)(q1.y - y * r), (int)(q2.x - x * r), (int)(q2.y - y * r));
	}
	
	public void move(){
		if(!collided){
			location.setLocation(location.x + xspeed, location.y + yspeed);
		}
		else{
			setCollided(false);
		}
	}
	
	public ArrayList<Double> normalize(Point q1, Point q2){
		ArrayList<Double> point = new ArrayList<Double>();
		double y = q2.y - q1.y;
		double x = q2.x - q1.x;
		double f = Math.sqrt(y * y + x * x);
		point.add(x / f);
		point.add(y / f);
		return point;
	}
}