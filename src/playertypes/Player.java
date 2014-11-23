package playertypes;

public abstract class Player {
	protected String name;
	protected int cost;
	
	public abstract double getProjection();
	public abstract double getFantasyPts();
	public abstract Position getPosition();
	public abstract void loadProjections(String data);
	
	public String getName() {
		return this.name;
	}
	
	public int getCost() {
		return this.cost;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
}
