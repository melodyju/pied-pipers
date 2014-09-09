package piedpipers.firstTry;

import java.util.*;

import piedpipers.sim.Point;

public class Player extends piedpipers.sim.Player {
	static int npipers;

	static double pspeed = 0.49;
	static double mpspeed = 0.09;

	private boolean magnet;
	private Point magnetLocation;

	public Player() {
		super();
		if (this.id == 0) { //assume there are multiple pipers for now
			magnet = true;
			magnetLocation = new Point(dimension/2, dimension/2); //assume there is only one magnet
		}
		else {
			magnet = false;
			magnetLocation = null;
		}
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
		
		Point current = pipers[id];

		if (magnet) {
			if (!current.equals(magnetLocation)) {
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
			return current; //change later
		}
	}

}