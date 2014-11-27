package lineup;

import java.util.ArrayList;

import playertypes.DST;
import playertypes.Player;
import playertypes.QB;
import playertypes.RB;
import playertypes.TE;
import playertypes.WR;

public class LineUp implements Comparable<LineUp> {
	private static final int numPlayers = 9;
	private ArrayList<Player> players;
	
	private int initialBudget;
	
	public LineUp(int budget) {
		this.initialBudget = budget;
		players = new ArrayList<Player>();
	}
	
	public LineUp(LineUp toCopy) {
		this.players = new ArrayList<Player>(toCopy.players);
		this.initialBudget = toCopy.initialBudget;
	}
	
	public int getRemainingBudget() {
		int remainingBudget = initialBudget;
		ArrayList<Player> players = _getPlayers();
		for (Player p : players) {
			remainingBudget -= p.getCost();
		}
		return remainingBudget;
	}
	
	public boolean addPlayer(Player p) {
		if (players.contains(p))
			return false;
		players.add(p);
		return true;
	}
	
	
	@Override
	public String toString() {
		return "LineUp [players=" + players + ", proj=" + this.getProjection() + ", actual=" + this.getPts() + ", remBudg=" + this.getRemainingBudget() + "]";
	}

	public boolean isValid() {
		return getRemainingBudget() >= 0;
	}
	
	public boolean isFull() {
		return _getPlayers().size() == numPlayers;
	}
	
	public double getProjection() {
		ArrayList<Player> players = _getPlayers();
		double proj = 0;
		for (Player p : players) {
			proj += p.getProjection();
		}
		return proj;
	}
	
	public double getPts() {
		ArrayList<Player> players = _getPlayers();
		double pts = 0;
		for (Player p : players) {
			pts += p.getPts();
		}
		return pts;
	}
	
	private ArrayList<Player> _getPlayers() {
		return players;
	}
	public void printCustom() {
		for (Player p : players) {
			System.out.print(p.getName() + " ");
		}
		System.out.println();
	}

	@Override
	public int compareTo(LineUp arg0) {
		return (int) (arg0.getProjection() * 100 - this.getProjection() * 100);
	}
}
