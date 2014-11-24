package playertypes;

public class DST extends Player {
	private double proj;
	
	public DST(String name, double proj) {
		super(name);
		this.proj = proj;
	}
	
	@Override
	public void loadProjections(String data) {
		String[] d = data.split("\t");
		this.proj = Double.parseDouble(d[1]);
	}
	
	@Override
	public double getProjection() {
		return proj;
	}
	public double getFantasyPts() {
		// TODO implement
		return 0;
	}
	@Override
	public Position getPosition() {
		return Position.DST;
	}
}
