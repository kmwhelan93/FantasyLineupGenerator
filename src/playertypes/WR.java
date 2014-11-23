package playertypes;

import stats.StatLine;

public class WR extends Player {
// Player Name 	 Team 	rush_att 	rush_yds 	rush_tds 	rec_att 	rec_yds 	rec_tds 	fumbles 	fpts 	
	
	private double projRushYds;
	private double projRushTds;
	private double projRecAtt;
	private double projRecYds;
	private double projRecTds;
	private double projFumbles;
	
	private double rushYds;
	private double rushTds;
	private double recAtt;
	private double recYds;
	private double recTds;
	private double fumbles;
	
	public WR(String name) {
		this.name = name;
	}
	
	@Override
	public void loadProjections(String data) {
		String[] d = data.split("\t");
		projRushYds = Double.parseDouble(d[3]);
		this.projRushTds = Double.parseDouble(d[4]);
		this.projRecAtt = Double.parseDouble(d[5]);
		this.projRecYds = Double.parseDouble(d[6]);
		this.projRecTds = Double.parseDouble(d[7]);
		this.projFumbles = Double.parseDouble(d[8]);
	}

	@Override
	public double getProjection() {
		return StatLine.getWRPts(projRushYds, projRushTds, projRecAtt, projRecYds, projRecTds, projFumbles);
	}

	@Override
	public double getFantasyPts() {
		return StatLine.getWRPts(rushYds, rushTds, recAtt, recYds, recTds, fumbles);
	}

	@Override
	public Position getPosition() {
		return Position.WR;
	}
	
}
