package playertypes;

import stats.StatLine;

public class QB extends Player {
	//  Player Name 	 Team 	pass_att 	pass_cmp 	pass_yds 	pass_tds 	pass_ints 	rush_att 	rush_yds 	rush_tds 	fumbles 	fpts
	
	private double projPassYds;
	private double projPassTds;
	private double projPassInts;
	private double projRushYds;
	private double projRushTds;
	private double projFumbles;
	
	private double passYds;
	private double passTds;
	private double passInts;
	private double rushYds;
	private double rushTds;
	private double fumbles;
	
	
	public QB(String name) {
		this.name = name;
	}

	@Override
	public double getProjection() {
		return StatLine.getQBPts(projPassYds, projPassTds, projPassInts, projRushYds, projRushTds, projFumbles);
	}

	@Override
	public double getFantasyPts() {
		// TODO implement based on actual stats
		return StatLine.getQBPts(passYds, passTds, passInts, rushYds, rushTds, fumbles);
	}

	@Override
	public Position getPosition() {
		return Position.QB;
	}

	@Override
	public void loadProjections(String data) {
		String[] d = data.split("\t");
		this.projPassYds = Double.parseDouble(d[4]);
		this.projPassTds = Double.parseDouble(d[5]);
		this.projPassInts = Double.parseDouble(d[6]);
		this.projRushYds = Double.parseDouble(d[8]);
		this.projRushTds = Double.parseDouble(d[9]);
		this.projFumbles = Double.parseDouble(d[10]);
	}
}
