package piedpipers.exp2;

import java.util.*;

import piedpipers.sim.Point;

public class Player extends piedpipers.sim.Player {
	static int npipers;

	static double pspeed = 0.49;
	static double mpspeed = 0.09;

	private double myPartition;
	private int step; //for circle

	private Point target; //for worker only

	private boolean endGame = false;
	private boolean allRatsCaptured = false; 

	public double slice;

	public Player() {
		super();
	}

	static double distance(Point a, Point b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}

	public void init() {
		
	}

	public double[] createPartitions(int npipers) {
		double[] partitions = new double[npipers];
		slice = 180.0/npipers;
		for (int i = 0; i < npipers; i++) {
			partitions[i] = slice * (i + 1);
			//System.out.println(partitions[i]);
		}
		return partitions;
	}

	public Point findCenter(double[] partitions, int index) {
		double r = dimension/3;
		double theta = (partitions[index] + (partitions[index] - slice)) / 2;
		System.out.println("THETA = " + theta);
		double cosineTheta = Math.sin(Math.toRadians(theta));
		double sineTheta = Math.cos(Math.toRadians(theta));
		//x = r cos theta
		//y = r sin theta
		double x = r * cosineTheta + (dimension/2);
		double y = (dimension/2) - r * sineTheta;
		return new Point(x, y);
	}

	public Point[] findRatsInPartition(Point[] allRats, double[] partitions, int index) {
		 
		 ArrayList<Point> ratsInPartition = new ArrayList<Point>();

		 Point gate = new Point(dimension/2, dimension/2);
		 double hypotenuse;
		 double cosineTheta;
		 double theta;

		 for (int i = 0; i < allRats.length; i++) {
		 	if (allRats[i].x > dimension/2) {
		 		hypotenuse = distance(allRats[i], gate);
			 	//System.out.println(hypotenuse);
			 	cosineTheta = (dimension/2 - allRats[i].y) / hypotenuse;
			 	//System.out.println(cosineTheta);
			 	//System.out.println(Math.acos(cosineTheta));
			 	theta = Math.toDegrees(Math.acos(cosineTheta));
			 	//System.out.println(theta);

			 	if (theta > partitions[index] - slice && theta <= partitions[index]) {
			 		ratsInPartition.add(allRats[i]);
			 	}
		 	}
		 }

		 Point[] ratArray = new Point[ratsInPartition.size()];
		 int i = 0;
		 for (Point rat : ratsInPartition) {
		 	ratArray[i] = rat;
		 	i++;
		 }
		 return ratArray;
	}

	public Point findClosestMagnet(Point[] pipers, Point me) {
		Point[] magnetPipers = new Point[pipers.length / 2];
		int n = 0;
		for (int id = 0; id < pipers.length; id += 2) {
			magnetPipers[n] = pipers[id];
			n++;
		}
		Point magnet;
		double currentMinDistance = dimension;
		Point currentMinDistanceMagnet = null;
		for (int i = 0; i < magnetPipers.length; i++) {
			magnet = magnetPipers[i];
			double distanceFromMe = distance(magnet, me);
			if (distanceFromMe < currentMinDistance) {
				currentMinDistance = distanceFromMe;
				currentMinDistanceMagnet = magnet;
			}
		}
		return currentMinDistanceMagnet;
	}

	public boolean freeRoaming(Point[] pipers, Point rat) {
		Point[] magnetPipers = new Point[pipers.length / 2];
		int n = 0;
		for (int id = 0; id < pipers.length; id += 2) {
			magnetPipers[n] = pipers[id];
			n++;
		}
		for (int i = 0; i < magnetPipers.length; i++) {
			if (distance(magnetPipers[i], rat) < 10) {
				return false;
			}
		}
		return true;
	}

	public Point findClosestRat(Point[] allRats, Point[] pipers, Point me) {
		Point rat;
		double currentMinDistance = dimension;
		Point currentMinDistanceRat = null;

		for (int i = 0; i < allRats.length; i++) {
			rat = allRats[i];
			if (rat.x > dimension/2 && freeRoaming(pipers, rat)) {
				double distanceFromMe = distance(rat, me);
				if (distanceFromMe < currentMinDistance) {
					currentMinDistance = distanceFromMe;
					currentMinDistanceRat = rat;
				}
			}
		}

		return currentMinDistanceRat;
	}

	public Point findNewTarget(Point[] myRats, Point partnerLocation) {
		/*
		//METHOD 1
		//find "average dense area." average doesn't include the rats that are already at the partner/magnet.
		Point rat;
		double xtotal = 0;
		double ytotal = 0;
		int count = 0;
		for (int i = 0; i < myRats.length; i++) {
			rat = myRats[i];
			if (distance(rat, partnerLocation) > 10) {
				xtotal += rat.x;
				ytotal += rat.y;
				count++;
			}
		}
		return new Point(xtotal/count, ytotal/count);
		//end
		*/

		/*
		//METHOD 2
		//go to farthest rat from magnet.
		Point rat;
		double currentMaxDistance = 0;
		Point currentMaxDistanceRat = partnerLocation;

		for (int i = 0; i < myRats.length; i++) {
			rat = myRats[i];
			if (distance(rat, partnerLocation) > currentMaxDistance) {
				currentMaxDistance = distance(rat, partnerLocation);
				currentMaxDistanceRat = rat;
			}
		}

		return currentMaxDistanceRat;
		*/

		//METHOD 3
		//go to closest rat outside magnet's sphere of influence
		Point rat;
		double currentMinDistance = dimension;
		Point currentMinDistanceRat = null;

		for (int i = 0; i < myRats.length; i++) {
			rat = myRats[i];
			double distanceFromMagnet = distance(rat, partnerLocation);
			if (distanceFromMagnet > 10 && distanceFromMagnet < currentMinDistance) {
				currentMinDistance = distanceFromMagnet;
				currentMinDistanceRat = rat;
			}
		}

		return currentMinDistanceRat;

	}

	public boolean allRatsCaptured(Point[] pipers, Point[] rats) {
		Point rat;
		for (int i = 0; i < rats.length; i++) {
			rat = rats[i];
			if (rat.x > dimension/2) {
				if (freeRoaming(pipers, rat)) {
					return false;
				}
			}
		}
		return true;
	}

	public Point move(Point[] pipers, Point[] rats) {
		npipers = pipers.length;
		
		Point current = pipers[id];
		Point gate = new Point(dimension/2, dimension/2);
		
		//if on left side of fence, move toward gate
		if (current.x < gate.x) {
			this.music = false;
			double dist = distance(current, gate);
			double ox = (gate.x - current.x) / dist * pspeed;
			double oy = (gate.y - current.y) / dist * pspeed;
			current.x += ox;
			current.y += oy;
			return current;
		}
	
		double[] partitions = createPartitions(npipers / 2);
		myPartition = partitions[id / 2];

		if (id % 2 == 0) { //0, 2, 4, 6
			//magnet
			
			
			//this part takes care of figuring out if the game's over (all rats captured, move toward other side) or not
			if (allRatsCaptured) { //move to gate
				this.music = true;
				Point goal = new Point(dimension/2, dimension/2);
				double dist = distance(current, goal);
				double ox = (goal.x - current.x) / dist * pspeed;
				double oy = (goal.y - current.y) / dist * pspeed;
				current.x += ox;
				current.y += oy;
				return current;
			}

			if (allRatsCaptured(pipers, rats)) { //this is VERY inefficient, change later
				this.allRatsCaptured = true;
				return current;
			}
			//end
			

			Point axis = findCenter(partitions, id/2);
			//System.out.println(id + ", " + id/2 + ", " + axis.x + ", " + axis.y);

			if (current.x < axis.x) {
				this.music = false;
				double dist = distance(current, axis);
				double ox = (axis.x - current.x) / dist * pspeed;
				double oy = (axis.y - current.y) / dist * pspeed;
				current.x += ox;
				current.y += oy;
				return current;
			}

			else {
				this.music = true;
				return current;
			}

			/*
			double slice = 2 * Math.PI / (200 * Math.PI);
			double angle = slice * step;
			double newX = axis.x + 10 * (Math.cos(angle));
			double newY = axis.y + 10 * (Math.sin(angle));
			step++;
			if (step >= (200 * Math.PI)) {
				step = 0;
			}
			return new Point(newX, newY);
			*/
		}
		else { //1, 3, 5, 7
			//worker
			Point partnerLocation = pipers[id - 1];

			
			//this part takes care of figuring out if the game's over (all rats captured, move toward other side) or not
			if (allRatsCaptured) { //move to gate
				this.music = false;
				Point goal = new Point(dimension/2, dimension/2);
				double dist = distance(current, goal);
				double ox = (goal.x - current.x) / dist * pspeed;
				double oy = (goal.y - current.y) / dist * pspeed;
				current.x += ox;
				current.y += oy;
				return current;
			}

			if (allRatsCaptured(pipers, rats)) { //this is VERY inefficient, change later
				this.allRatsCaptured = true;
				return current;
			}
			//end
			

			if (!this.music) { //in mode to find rats.
				if (this.target == null) {
					Point[] myRats = findRatsInPartition(rats, partitions, id/2);
					target = findNewTarget(myRats, partnerLocation);
					if (target == null) {
						endGame = true;
						target = findClosestRat(rats, pipers, current);
						if (target == null) {
							allRatsCaptured = true;
							return current;
						}
					}
				}
				System.out.println(target.x + " " + target.y);
				if (current.x - target.x < 2 && current.y - target.y < 2) {
					this.music = true;
					return current;
				}
				else {
					double dist = distance(current, target);
					double ox = (target.x - current.x) / dist * pspeed;
					double oy = (target.y - current.y) / dist * pspeed;
					current.x += ox;
					current.y += oy;
					return current;
				}
			}

			else { //in mode to go back to partner.
				if (endGame) {
					partnerLocation = findClosestMagnet(pipers, current);
				}
				if ((int)current.x == (int)partnerLocation.x && (int)current.y == (int)partnerLocation.y) {
					this.music = false;
					this.target = null;
					return current;
				}
				else {
					double dist = distance(current, partnerLocation);
					double ox = (partnerLocation.x - current.x) / dist * mpspeed;
					double oy = (partnerLocation.y - current.y) / dist * mpspeed;
					current.x += ox;
					current.y += oy;
					return current;
				}
			}

		}
	}

}
