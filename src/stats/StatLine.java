package stats;

/**
 * Deals with calculating Fantasy Points from stats
 * @author Student
 *
 */
public class StatLine {
	// create a static method for each kind of input IE WR, TE etc
	// all call same method with some parameters missing
	public static double getQBPts(
			double passYds,
			double passTds,
			double passInts,
			double rushYds,
			double rushTds,
			double fumbles) {
		return getPts(
				passYds,
				passTds,
				passInts,
				rushYds,
				rushTds,
				0,
				0,
				0,
				fumbles);
	}
	
	public static double getRBPts(
			double rushYds,
			double rushTds,
			double recAtt,
			double recYds,
			double recTds,
			double fumbles) {
		return getPts(
				0,
				0,
				0,
				rushYds,
				rushTds,
				recAtt,
				recYds,
				recTds,
				fumbles);
	}
	
	public static double getWRPts(
			double rushYds,
			double rushTds,
			double recAtt,
			double recYds,
			double recTds,
			double fumbles) {
		return getPts(
				0,
				0,
				0,
				rushYds,
				rushTds,
				recAtt,
				recYds,
				recTds,
				fumbles);
	}
	
	public static double getTEPts(
			double recAtt,
			double recYds,
			double recTds,
			double fumbles) {
		return getPts(
				0,
				0,
				0,
				0,
				0,
				recAtt,
				recYds,
				recTds,
				fumbles);
	}
	
	private static double getPts(
			double passYds,
			double passTds,
			double passInts,
			double rushYds,
			double rushTds,
			double recAtt,
			double recYds,
			double recTds,
			double fumbles) {
		// TODO implement
		return 0;
	}
}
