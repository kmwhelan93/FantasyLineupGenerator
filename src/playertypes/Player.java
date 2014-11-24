package playertypes;

import java.text.DecimalFormat;

public abstract class Player implements Comparable<Player> {
	private DecimalFormat df = new DecimalFormat("0.00");
	
	protected String name;
	protected int cost;
	protected double pts;
		
	public abstract double getProjection();
	//public abstract double getFantasyPts();
	public abstract Position getPosition();
	public abstract void loadProjections(String data);
	
	public Player(String name) {
		this.name = name;
		pts = .001;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getCost() {
		return this.cost;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public double getPts() {
		return pts;
	}
	
	public void setPts(double pts) {
		this.pts = pts;
	}
	
	@Override
	public String toString() {
		return "Player [name=" + name + ", cost=" + cost + 
				", proj=" + df.format(this.getProjection()) + 
				", actual=" + df.format(this.getPts()) + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(Player arg0) {
		double valueRatio = this.getProjection() / this.getCost() * 10000;
		double argValueRatio = arg0.getProjection() / arg0.getCost() * 10000;
		return (int) (argValueRatio - valueRatio);
	}
	
}
