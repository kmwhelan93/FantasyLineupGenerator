package stats;

/**
 * Deals with calculating Fantasy Points from stats
 * 
 * From DraftKings: 
 * Offensive players will accumulate points as follows: 
 * Passing TD = +4PTs 
 * 25 Passing Yards = +1PT (+0.04PT/ per yard is awarded) 
 * 300+ Yard Passing Game = +3PTs 
 * Interception = -1PT 
 * 10 Rushing Yards = +1PT (+0.1PT per yard is awarded) 
 * Rushing TD = +6PTs 
 * 100+ Yard Rushing Game = +3PTs 
 * 10 Receiving Yards = +1PT (+0.1PT per yard is awarded) 
 * Reception = +1PT
 * Receiving TD = +6PTs 
 * 100+ Yard Receiving Game = +3PTs 
 * Punt/Kickoff Return for TD = +6PTs 
 * Fumble Lost = -1PT 
 * 2 Point Conversion (Pass, Run, or Catch) = +2PTs 
 * Offensive Fumble Recovery TD = +6PTs 
 * Defense/Special Teams will accumulate points as follows: 
 * Sack = +1PT 
 * Interception = +2PTs 
 * Fumble Recovery = +2PTs 
 * Kickoff Return TD = +6PTs 
 * Punt Return TD = +6PTs
 * Interception Return TD = +6PTs 
 * Fumble Recovery TD = +6PTs 
 * Blocked Punt or FG Return TD = +6PTs 
 * Safety = +2PTs 
 * Blocked Kick = +2PTs 
 * 0 Points Allowed = +10PTs 
 * 1-6 Points Allowed = +7PTs 
 * 7-13 Points Allowed = +4PTs 
 * 14-20 Points Allowed = +1PT 
 * 21-27 Points Allowed = 0PTs 
 * 28-34 Points Allowed = -1PT 
 * 35+ Points Allowed = -4PTs 
 * The following scoring plays will result in Points Allowed (PA) by your Defense/Special Teams (DST): 
 * Rushing TDs, Passing TDs, Punt Return TDs, Kick Return TDs and Blocked Punt TDs 2pt conversions
 * Extra-points Field-goals Note: Points Allowed (PA) only includes points
 * surrendered while DST is on the field - doesn't include points given up by
 * team's offense (i.e. points off offensive turnovers).
 * 
 * @author Student
 * 
 */
public class StatLine {
	// create a static method for each kind of input IE WR, TE etc
	// all call same method with some parameters missing
	public static double getQBPts(double passYds, double passTds,
			double passInts, double rushYds, double rushTds, double fumbles) {
		return getPts(passYds, passTds, passInts, rushYds, rushTds, 0, 0, 0,
				fumbles);
	}

	public static double getRBPts(double rushYds, double rushTds,
			double recAtt, double recYds, double recTds, double fumbles) {
		return getPts(0, 0, 0, rushYds, rushTds, recAtt, recYds, recTds,
				fumbles);
	}

	public static double getWRPts(double rushYds, double rushTds,
			double recAtt, double recYds, double recTds, double fumbles) {
		return getPts(0, 0, 0, rushYds, rushTds, recAtt, recYds, recTds,
				fumbles);
	}

	public static double getTEPts(double recAtt, double recYds, double recTds,
			double fumbles) {
		return getPts(0, 0, 0, 0, 0, recAtt, recYds, recTds, fumbles);
	}

	private static double getPts(double passYds, double passTds,
			double passInts, double rushYds, double rushTds, double recAtt,
			double recYds, double recTds, double fumbles) {
		return StatLine.getPts(passYds, passTds, passInts, rushYds, rushTds, recAtt, recYds, recTds, 0, fumbles, 0);
	}
	
	public static double getPts(double passYds, double passTds,
			double passInts, double rushYds, double rushTds, double recAtt,
			double recYds, double recTds, double twoPcs, double fumbles, double miscTds) {
		double points = 0;
		points += passYds / 25.0;
		points += passTds * 4;
		points += passInts * -1;
		points += rushYds / 10.0;
		points += rushTds * 6;
		points += recAtt;
		points += recYds / 10.0;
		points += recTds * 6;
		points += fumbles * -1;
		
		// TODO weight this bonus for projections
		// IE projection of 299 should get the bonus half the time it is simulated
		if (passYds >= 300)
			points += 3;
		if (rushYds >= 100)
			points += 3;
		if (recYds >= 100)
			points += 3;
		
		points += twoPcs*2;
		points += miscTds*6;
		
		return points;
	}
}
