package playertypes;

import stats.StatLine;

public class TE extends Player {
	
	private double projRecAtt;
	private double projRecYds;
	private double projRecTds;
	private double projFumbles;
	
	private double recAtt;
	private double recYds;
	private double recTds;
	private double fumbles;
	
	public TE (String name) {
		super(name);
	}
	
	@Override
	public void loadProjections(String data) {
		String[] d = data.split("\t");
		this.projRecAtt = Double.parseDouble(d[2]);
		this.projRecYds = Double.parseDouble(d[3]);
		this.projRecTds = Double.parseDouble(d[4]);
		this.projFumbles = Double.parseDouble(d[5]);
	}

	@Override
	public double getProjection() {
		return StatLine.getTEPts(projRecAtt, projRecYds, projRecTds, projFumbles);
	}

	public double getFantasyPts() {
		return StatLine.getTEPts(recAtt, recYds, recTds, fumbles);
	}

	@Override
	public Position getPosition() {
		return Position.TE;
	}
}
