package piedpipers.group4;

import java.util.*;

import piedpipers.sim.Point;

public class Player extends piedpipers.sim.Player {
	static int npipers;

	static double pspeed = 0.49;
	static double mpspeed = 0.09;
	static int norats;

	static int magnet = 0;
	static double[] angle;
	private boolean comeback = false;
	private boolean off = false;
	private boolean reachedmagnet = false;

	public Point gateLocation;
	public Point magnetLocation;

	public Player() {
		super();
	}

	static double distance(Point a, Point b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}

	public void init() {
		int[] thetas = new int[npipers];
		/*for (int i=0; i< npipers; i++) {
			Random random = new Random();
			int theta = random.nextInt(180);
			thetas[i]=theta;
			System.out.println(thetas[i]);
		}*/
	}

	boolean closetoWall (Point current) {
		boolean wall = false;
		if (Math.abs(current.x-dimension)<pspeed ) {
			wall = true;
		}
		if (Math.abs(current.y-dimension)<pspeed) {
			wall = true;
		}
		if (Math.abs(current.x-dimension/2)<pspeed ) {
			wall = true;
		}
		if (Math.abs(current.y)<pspeed) {
			wall = true;
		}
		return wall;
	}

	boolean closetoMagnet(Point current) {
		if (Math.abs(distance(current, magnetLocation)) < 5) {
			return true;
		}
		return false;
	}

	boolean noRatsOutsideRadius(Point[] rats, Point current) {
		for (int i = 0; i < rats.length; i++) {
			if (Math.abs(distance(current, rats[i])) > 9  && rats[i].x > gateLocation.x) {
				return false;
			}
		}
		return true;
	}

	
	public Point move(Point[] pipers, Point[] rats) {
		npipers = pipers.length;
		
		//Assign raial angles to the pipers
		double fraction = 360/npipers;
		angle = new double[npipers];
		for(int i=1 ; i< npipers; i++)
		{
			angle[i] = fraction*i;
			System.out.println("PRINTING anfle ..." +  angle[i]);
		}
		
		gateLocation = new Point(dimension/2, dimension/2); //assume there is only one magnet
		magnetLocation = new Point(3*dimension/4, dimension/2);
		
		
		Point current = pipers[id];
		System.out.println("PRINTING MAGNET LOCATION....." + magnetLocation.x +  "," + magnetLocation.y);
		if(current.x < magnetLocation.x && reachedmagnet == false){
			this.music = false;
			
			if (current.x < gateLocation.x) {
				double dist = distance(current, gateLocation);
				double ox = (gateLocation.x - current.x) / dist * pspeed;
				double oy = (gateLocation.y - current.y) / dist * pspeed;
				current.x += ox;
				current.y += oy;
				return current;
			}
			else {
				double dist = distance(current, magnetLocation);
				double ox = (magnetLocation.x - current.x) / dist * pspeed;
				double oy = (magnetLocation.y - current.y) / dist * pspeed;
				current.x += ox;
				current.y += oy;
				return current;
			}
			
		}
		else
		{
			reachedmagnet = true; 
			if(this.id == magnet) {
				this.music = true;
				if (noRatsOutsideRadius(rats, current)) {
					System.out.println("NO RATS OUTSIDE RADIUS");
					//all rats collected at magnet, magnet back to other side
					Point target = new Point(0, gateLocation.y);
					System.out.println("target = " + target.x + " " + target.y);
					double dist = distance(current, target);
					double ox = mpspeed * (target.x - current.x) / dist;
					double oy = mpspeed * (target.y - current.y) / dist ;
					current.x += ox;
					current.y += oy;
					return current;
				}
				else {
					return current;
				}
					
			}
			else
			{	
				if (off)
				{
					this.music = false;
					return current;
				}
				else 
				{
					if (noRatsOutsideRadius(rats, magnetLocation))
					{
						System.out.println("NOOOO RAATS");
						off = true;
						this.music = false;
						return current;
					}
					else 
					{
						while (true) {
							if (!comeback) {
								if (!closetoWall(current)) {
									this.music = false;
									current.x += pspeed * Math.sin(angle[this.id] * Math.PI / 180);
									current.y += pspeed * Math.cos(angle[this.id] * Math.PI / 180);
									
									return current;
								}
								else {
									this.music = true;
									comeback = true;
								}
							}
							else {
								if (!closetoMagnet(current)) {
									this.music = true;
									double dist = distance(current, magnetLocation);
									double ox = mpspeed * (magnetLocation.x - current.x) / dist;
									double oy = mpspeed * (magnetLocation.y - current.y) / dist ;
									//System.out.println("move toward the left side");
									current.x += ox;								
									current.y += oy;
									return current;
								}
								else {
									this.music = false;
									comeback = false;
								}
							}
						}
					}
			}
				
			}
		
		}
						
	}

}
