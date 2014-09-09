package piedpipers.group1;

import java.util.*;

import piedpipers.sim.Point;

public class Player extends piedpipers.sim.Player {
	static int npipers;

	static double pspeed = 0.49;
	static double mpspeed = 0.09;

	static int magnet = 0;
	static double[] angle;
	private boolean comeback;
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

	public Point move(Point[] pipers, Point[] rats) {
		npipers = pipers.length;
		System.out.println(npipers);
		double fraction = 360/npipers;
		angle = new double[npipers];
		for(int i=0 ; i< npipers; i++)
		{
			System.out.println("In the loop..!!");
			angle[i] = fraction*i;
			System.out.println(angle[i]);
		}
		System.out.println("Done..!!");
		gateLocation = new Point(dimension/2, dimension/2); //assume there is only one magnet
		magnetLocation = new Point(3*dimension/4, dimension/2);
		Point current = pipers[id];
		System.out.println("PRINTING MAGNET LOCATION....." + magnetLocation.x +  "," + magnetLocation.y);
		
		if(current.x < magnetLocation.x){
			this.music = false;
			double dist = distance(current, magnetLocation);
			double ox = (magnetLocation.x - current.x) / dist * pspeed;
			double oy = (magnetLocation.y - current.y) / dist * pspeed;
			current.x += ox;
			current.y += oy;
			return current;
		}
		/*
		if(current.x < gateLocation.x){
			this.music = false;
			double dist = distance(current, gateLocation);
			double ox = (gateLocation.x - current.x) / dist * pspeed;
			double oy = (gateLocation.y - current.y) / dist * pspeed;
			current.x += ox;
			current.y += oy;
			return current;
		}
		*/
		if (this.id == magnet) {
			if (current.x < magnetLocation.x)
				{
				this.music = false;
				double dist = distance(current, magnetLocation);
				double ox = (magnetLocation.x - current.x) / dist * pspeed;
				double oy = (magnetLocation.y - current.y) / dist * pspeed;
				current.x += ox;
				current.y += oy;
				return current;
			}
			else {
				this.music = true;
				return current;
			}
		}

		else {
			if (comeback){
				this.music = true;
				if (true) { //close enuf to magnet to drop off all rats
					comeback = false;
				}
				return current;
			}
			else {
				this.music = false;
				current.x += pspeed * Math.sin(angle[id] * Math.PI / 180);
				current.y += pspeed * Math.cos(angle[id] * Math.PI / 180);
				if (false) { //implement condition later
					comeback = true;
				}
				return current; //change later
			}
		}
	}

}