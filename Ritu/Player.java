package piedpipers.group4;

import java.util.*;

import piedpipers.sim.Point;

public class Player extends piedpipers.sim.Player {
	static int npipers;

	static double pspeed = 0.49;
	static double mpspeed = 0.09;
	
	//Flag variables for a piper
	int start1 = 0;
	int round1 = 0;
	
	boolean comeback = false;
	boolean reachedGate = false;
	
	private Point gateLocation;

	public Player() {
		super();
	}

	static double distance(Point a, Point b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}

	public void init() {
		
	}

	//Get all partition Coordinates
	public static Point[] getPartitionCoords(int npipers, int dimension)
	{
		Point[] partCoords = new Point[npipers];
		for(int i =0 ; i<npipers; i++)
		{
			partCoords[i] =new Point((i*dimension/npipers), (i*dimension/npipers) + (dimension/npipers));				
		}
		return partCoords;
	}
	
	//Get Rats in your Partition based on X or Y coordinates
	public static Point[] getRatsinPartition(Point coords, Point[] rats, char c, Point current)
	{	
		System.out.println("Partition Coordinates" + coords.x + coords.y);
		ArrayList<Point> currentRats = new ArrayList();
		//Check each Rat's Dimensions
		for(int j=0; j<rats.length ; j++)
		{
			if( c == 'Y')
			{
				if(rats[j].y >=coords.x && rats[j].y <= coords.y && distance(current, rats[j]) > 10)
					currentRats.add(rats[j]);
			}
			else
			{
				if(rats[j].x >=coords.x && rats[j].x <= coords.y && distance(current, rats[j]) > 10)
					currentRats.add(rats[j]);
			}
		}
		
		//Create Array of appropriate Rats
		Point[] allRats = new Point[currentRats.size()];
		int i = 0;
		for(Point p: currentRats)
		{
			allRats[i] = p;
			i++;
		}
		return allRats;
	}
	
	//Get Target Rat by Distance
	public static Point getRatbyDistance(Point[] myRats, Point current, String str, int dimension)
	{
		Point rat = new Point();
		Point currentRat = new Point();
		if(str.equals("far"))
		{
			//Farthest Rat
			double currentMaxDistance = 0;
			currentRat = current;

			for (int i = 0; i < myRats.length; i++) {
				rat = myRats[i];
				double dist = distance(rat, current);
				if  (dist > currentMaxDistance) {
					currentMaxDistance = dist;
					currentRat = rat;
				}
			}
		}
		else
		{
			//Closest Rat
			double currentMinDistance = 500000;
			currentRat = current;

			for (int i = 0; i < myRats.length; i++) {
				rat = myRats[i];
				double dist = distance(rat, current);
				if  (dist < currentMinDistance && dist > 10 && rat.x > dimension/2) {
					currentMinDistance = dist;
					currentRat = rat;
				}
			}
		}
		return currentRat;
	}
	
	public static boolean noRatsRemain(Point[] pipers, Point[] rats)
	{
		for(int i =0; i<rats.length; i++)
		{
			for(int j=0; j<pipers.length; j++)
			{
				if(distance(rats[i], pipers[j]) > 10)
					return false;
			}
		}
		return true;
	}
	
	public Point move(Point[] pipers, Point[] rats) {
		Point current = pipers[id];
		npipers = pipers.length;
		gateLocation = new Point(dimension/2, dimension/2);
		
		if (current.x < gateLocation.x && !comeback) 
		{	
			this.music = false;
			double dist = distance(current, gateLocation);
			double ox = (gateLocation.x - current.x) / dist * pspeed;
			double oy = (gateLocation.y - current.y) / dist * pspeed;
			current.x += ox;
			current.y += oy;
			return current;		
		}
		else
		{
			//Get the partitions dimensions (Y coordinates)
			Point[] partCoords = new Point[npipers];
			partCoords = getPartitionCoords(npipers, dimension);
			
			Point[] farCoords = new Point[npipers];
			farCoords[0] = new Point(dimension - 10, 0);
			farCoords[1] = new Point(dimension - 10, dimension);
			
			Point[] midCoords = new Point[npipers];
			midCoords[0] = new Point(dimension/2, 0);
			midCoords[1] = new Point(dimension/2, dimension);
			
			//Get the rats in partition of the current piper based on their Y coordinates	
			System.out.println("ID is ..." + id + "No of rats =" + rats.length);
			System.out.println("Partition Coordinate is=" + partCoords[id].x + partCoords[id].y);
			Point[] myRats = getRatsinPartition(partCoords[id], rats, 'Y', current);
			
			Point target = new Point();
			//If this is the start of round 1 then go to the farthest rat i partition
			if(start1 == 0)
			{
				//target = getRatbyDistance(myRats, current, "far");
				target = farCoords[id];
				double dist = distance(current, target);
				if((int)dist <= 10)
				{
					this.music = true;
					start1 = 1;
				}
				else
				{
					double ox = (target.x - current.x) / dist * pspeed;
					double oy = (target.y - current.y) / dist * pspeed;
					current.x += ox;
					current.y += oy;
					return current;	
				}
			}
			/*if(round1 == 0 )
			{
				Point SubPartRats[];
				Point subPartCoord = new Point(dimension*3/4, dimension);
				SubPartRats = getRatsinPartition(subPartCoord, myRats, 'X', current);
				if(SubPartRats.length == 0)
				{
					round1 = 1;
					//comeback = true;
				}
				else
				{
					System.out.println("ROUND 1 Going On...");
					target = getRatbyDistance(SubPartRats, current, "near", dimension);
					System.out.println("TARGET RAT: " + target.x + target.y);
					double dist = distance(current, target);
					double ox = (target.x - current.x) / dist * mpspeed;
					double oy = (target.y - current.y) / dist * mpspeed;
					current.x += ox;
					current.y += oy;
					return current;	
				}							
			}
			*/
					
			round1 = 1;// Test
			//Start Greedy Approach after round 1 and round 2
			if(!comeback && round1 == 1 && start1 == 1)
			{
				if (myRats.length == 0)
				{
					//check if no rats are remaining
					if(noRatsRemain(pipers, rats))
						comeback = true;
					else
					{
						target = getRatbyDistance(rats, current, "near", dimension);
						double dist = distance(current, target);
						double ox = (target.x - current.x) / dist * mpspeed;
						double oy = (target.y - current.y) / dist * mpspeed;
						current.x += ox;
						current.y += oy;
						return current;	
					}
				}
				else
				{
					target = getRatbyDistance(myRats, current, "near", dimension);
					double dist = distance(current, target);
					double ox = (target.x - current.x) / dist * mpspeed;
					double oy = (target.y - current.y) / dist * mpspeed;
					current.x += ox;
					current.y += oy;
					return current;	
				}
			}
			
			//Start moving towards gate if comeback is set
			if(comeback && !reachedGate)
			{
				this.music = true;
				target = new Point(dimension/2, dimension/2);
				double dist = distance(current, target);
				if ((int)dist == 0)
				{
					reachedGate = true;
				}
				else
				{
					double ox = (target.x - current.x) / dist * mpspeed;
					double oy = (target.y - current.y) / dist * mpspeed;
					current.x += ox;
					current.y += oy;
					return current;
				}
			}
			//Move a little inside the left region from the gate
			if(reachedGate)
			{
				this.music = true;
				target = new Point(dimension/2 -10, dimension/2);
				double dist = distance(current, target);
				if((int)dist == 0)
				{
					System.out.println("GO BACK FOR ROUND 2...");
					comeback = false;
					reachedGate = false;
				}
				else
				{
					double ox = (target.x - current.x) / dist * mpspeed;
					double oy = (target.y - current.y) / dist * mpspeed;
					current.x += ox;
					current.y += oy;				
					return current;
				}
			}	
		}
		return current;
	}

}
