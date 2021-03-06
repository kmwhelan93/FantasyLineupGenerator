package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import playertypes.*;
import stats.StatLine;
import lineup.LineUp;


public class FindOptimalLineups {
	// CONFIGURATION THINGS
	private static final String WEEK = "13";
	private static final int medianValue = 160;
	private static final int budget = 50000;
	// Minimum projected value to be included in initial list
	private static final int minProj = 2;
	// tweak this number to change the number of combinations that are tried.
	private static final int cutOffNum = 6;
	
	// CLASS SPECIFIC THINGS	
	private static ArrayList<Player> qbs;
	private static ArrayList<Player> rbs;
	private static ArrayList<Player> wrs;
	private static ArrayList<Player> tes;
	private static ArrayList<Player> flexes;
	private static ArrayList<Player> dsts;
	
	private static ArrayList<LineUp> lineUps = new ArrayList<LineUp>();
	private static HashMap<Integer, Integer> maxCostMap;
	private static ArrayList<ArrayList<Player>> playerMatrix;
	// The minimum cost a player can be by position
	// index 0: QB, 1: Flex, 2: WR, 3: RB, 4: TE, 5: DST
	private static int[] minCosts = {5000, 3000, 3000, 3000, 3000, 2400};
	
	public static void main (String[] args) throws FileNotFoundException {
		// read in lists of players
		qbs = FindOptimalLineups.<QB>loadPlayersWProjections("QB");
		rbs = FindOptimalLineups.<RB>loadPlayersWProjections("RB");
		wrs = FindOptimalLineups.<WR>loadPlayersWProjections("WR");
		tes = FindOptimalLineups.<TE>loadPlayersWProjections("TE");
		dsts = FindOptimalLineups.loadDSTWProjections();
		
		// Populate a HashMap that maps player names to their actual object
		HashMap<String, Player> everyone = new HashMap<String, Player>();
		addToMap(everyone, qbs);
		addToMap(everyone, rbs);
		addToMap(everyone, wrs);
		addToMap(everyone, tes);
		addToMap(everyone, dsts);
		
		// Read from DraftKings salaries and update costs for players
		addCosts(everyone);
		// populate results for all players
		try {
			populateStats(everyone);
		} catch (Exception e) {
			System.out.println("Stats were not populated correctly");
		}
		
		purgePoorValueRatios();

		flexes = new ArrayList<Player>();
		flexes.addAll(rbs);
		flexes.addAll(wrs);
		flexes.addAll(tes);
		
		// Array of maximum costs by position
		// index 0 has max cost of a QB, 1: Flex, 2: WR, 3:RB, 4: TE, 5: DST
		int[] maxCosts = createMaxCostArray();
		
		// Since there are 9 players and only 6 positions, we need to map a player in lineup index to their position
		// For example, 1 maps to 0 (QB), 2 maps to 1 (Flex), 3,4,5 all map to 2 (WR), 6,7 map to 3(RB), 8 maps to 4(TE), 9 maps to 5(DST)
		maxCostMap = createMaxCostMap();
		
		// This is needed for the generateLineUps method to programmatically choose the correct arraylist of players based on depth
		playerMatrix = new ArrayList<ArrayList<Player>>();
		playerMatrix.add(qbs);
		playerMatrix.add(flexes);
		playerMatrix.add(wrs);
		playerMatrix.add(rbs);
		playerMatrix.add(tes);
		playerMatrix.add(dsts);
		

		System.out.println("Generating lineups!");
		// The core of everything. The recursive call to generate the lineups.
		// TODO make this not recursive to save the stack.
		generateLineUps(1, new LineUp(budget), maxCosts);
		
		

		System.out.println("DONE");
		System.out.println(lineUps.size());
		Collections.sort(lineUps);
		for (int i = 0; i < 50; i++) {
			System.out.println("index " + (i) + ": " + lineUps.get(i));
		}
		for (int i = 0; i < 50; i++) {
			System.out.println("index " + (i * 100) + ": " + lineUps.get(i*100));
		}
		for (int i = 0; i < 200; i++) {
			lineUps.get(i).printCustom();
			System.out.println();
		}
		System.out.println("PLAYERS IN COMBINATIONS");
		System.out.println(qbs);
		System.out.println(rbs);
		System.out.println(wrs);
		System.out.println(tes);
		System.out.println(dsts);
		
		System.out.println("For median value " + medianValue + ":");
		printWinningPercentage(200000, medianValue);
		printWinningPercentage(20000, medianValue);
		printWinningPercentage(2000, medianValue);
		printWinningPercentage(200, medianValue);
		printWinningPercentage(20, medianValue);
	}
	
	public static void printWinningPercentage(int freq, int threshold) {
		int winners = 0;
		for (int i = 0; i < freq; i++) {
			if (lineUps.get(i).getPts() > threshold) {
				winners++;
			}
		}
		System.out.println("Top " + freq + " lineups won " + (double) winners * 100.0 / freq + "% of the time");
	}
	
	public static void populateStats(HashMap<String, Player> everyone) throws FileNotFoundException {
		//PLAYER, TEAM POS		TYPE	ACTION		OPP	STATUS ET		C/A	YDS	TD	INT		RUSH	YDS	TD		REC	YDS	TD	TAR		2PC	FUML	TD		PTS
		Scanner sc = new Scanner(new File("week" + WEEK + "/FantasyStats.txt"));
		sc.nextLine();
		sc.nextLine();
		while (sc.hasNextLine()) {
			String l = sc.nextLine();
			String[] line = l.split("\t");
			String name = line[0].split(",")[0];
//			for (int i = 0; i < line.length; i++) {
//				System.out.println(i + " " + line[i]);
//			}
			double passYds = Double.parseDouble(line[9]);
			double passTds = Double.parseDouble(line[10]);
			double passInts = Double.parseDouble(line[11]);
			double rushYds = Double.parseDouble(line[14]);
			double rushTds = Double.parseDouble(line[15]);
			double recAtt = Double.parseDouble(line[17]);
			double recYds = Double.parseDouble(line[18]);
			double recTds = Double.parseDouble(line[19]);
			double twoPcs = Double.parseDouble(line[22]);
			double fumbles = Double.parseDouble(line[23]);
			double miscTds = Double.parseDouble(line[24]);
			double pts = StatLine.getPts(passYds, passTds, passInts, rushYds, rushTds, recAtt, recYds, recTds, twoPcs, fumbles, miscTds);
			if (everyone.containsKey(name)) {
				everyone.get(name).setPts(pts);
			}
		}
		
		populateDefenseStats(everyone);
		
		// check which ones are missing
//		for (Player p : everyone.values()) {
//			if (p.getPts() == .001) {
//				System.out.println(p + " is missing points");
//			}
//		}
	}
	
	public static void populateDefenseStats(HashMap<String, Player> everyone) throws FileNotFoundException {
		Scanner sc = new Scanner(new File("week" + WEEK + "/FantasyDSTStats.txt"));
		sc.nextLine();
		sc.nextLine();
		while (sc.hasNextLine()) {
			String l = sc.nextLine();
			String[] line = l.split("\t");
			String name = line[0].split(" ")[0];
			if (everyone.containsKey(name)) {
				everyone.get(name).setPts(Double.parseDouble(line[26]));
			}
//			for (int i = 0; i < line.length; i++) {
//				System.out.println(i + " " + line[i]);
//			}
		}
	}
	
	public static void purgePoorValueRatios() {
		Player cheapQB = getLowCostHighValue(qbs, 5500);
		qbs = new ArrayList<Player>(qbs.subList(0, cutOffNum));
		if (!qbs.contains(cheapQB) && cheapQB != null) {
			qbs.add(cheapQB);
		}
		Player cheapRB = getLowCostHighValue(rbs, 3200);
		rbs = new ArrayList<Player>(rbs.subList(0, cutOffNum * 2));
		if (!rbs.contains(cheapRB) && cheapRB != null) {
			rbs.add(cheapRB);
		}
		Player cheapWR = getLowCostHighValue(wrs, 3200);
		wrs = new ArrayList<Player>(wrs.subList(0, cutOffNum * 3));
		if (!wrs.contains(cheapWR) && cheapWR != null) {
			wrs.add(cheapWR);
		}
		Player cheapTE = getLowCostHighValue(tes, 3200);
		tes = new ArrayList<Player>(tes.subList(0, cutOffNum));
		if (!tes.contains(cheapTE) && cheapTE != null) {
			tes.add(cheapTE);
		}
		Player cheapDST = getLowCostHighValue(dsts, 2800);
		dsts = new ArrayList<Player>(dsts.subList(0, cutOffNum));
		if (!dsts.contains(cheapDST) && cheapDST != null) {
			dsts.add(cheapDST);
		}
	}
	
	public static ArrayList<Player> purgeWithExceptions(ArrayList<Player> players, int atLeastOneLessThanThisCost) {
		return null;
	}
	
	public static Player getLowCostHighValue(ArrayList<Player> players, int maxCost) {
		Player bestPlayer = null;
		double bestValue = 0;
		for (Player p : players) {
			if (p.getCost() <= maxCost && p.getCost() > 0 && p.getValue() > bestValue) {
				bestPlayer = p;
				bestValue = p.getValue();
			}
		}
		return bestPlayer;
	}
	
	public static int maxCost(ArrayList<Player> players) {
		int maxCost = 0;
		for (Player p : players) {
			System.out.println(p);
			if (p.getCost() > maxCost) {
				maxCost = p.getCost();
			}
		}
		return maxCost;
	}
	
	public static void generateLineUps(int depth, LineUp curLineUp, int[] maxCosts) {
		if (depth == 10) {
			if (curLineUp.getRemainingBudget() < 0)
				return;
			if (lineUps.size() % 10000 == 0) {
				//System.out.println(lineUps.size());
			}
			lineUps.add(curLineUp);
			return;
		}
		ArrayList<Player> toAdd;
		int maxCostIndex = maxCostMap.get(depth);
		toAdd = playerMatrix.get(maxCostIndex);
		
		cont: for (Player p : toAdd) {
			if (depth == 1) {
				System.out.println(p);
			}
			if (p.getCost() <= maxCosts[maxCostIndex]) {
				int[] copyMaxCosts = Arrays.copyOf(maxCosts, 6);
				copyMaxCosts[maxCostIndex] = p.getCost();
				if (maxCostIndex == 1) {
					if (p.getPosition().equals(Position.WR))
						copyMaxCosts[2] = p.getCost();
					if (p.getPosition().equals(Position.RB))
						copyMaxCosts[3] = p.getCost();
					if (p.getPosition().equals(Position.TE))
						copyMaxCosts[4] = p.getCost();
				}
				int maxRemaining = 0;
				int minRemaining = 0;
				for (int index = depth; index <= 9; index++) {
					int costIndex = maxCostMap.get(index);
					maxRemaining += maxCosts[costIndex];
					minRemaining += minCosts[costIndex];
				}
				int remBudg = curLineUp.getRemainingBudget();
				if (maxRemaining < remBudg) {
					continue cont;
				}
				if (minRemaining > remBudg) {
					continue cont;
				}
				if (remBudg < 0) {
					continue cont;
				}
				// recurse!
				LineUp copy = new LineUp(curLineUp);
				if (!copy.addPlayer(p)) {
					continue;
				}
				generateLineUps(depth + 1, copy, copyMaxCosts);
				
			}
		}
	}
	
	 
	
	/**
	 * TODO put this in a separate class
	 * @param type
	 * @return
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Player> ArrayList<Player> loadPlayersWProjections(String type) throws FileNotFoundException {
		ArrayList<Player> players = new ArrayList<Player>();
		String fileName = "week" + WEEK + "/FantasyPros_Fantasy_Football_Rankings_" + type + ".xls";
		Scanner sc = new Scanner(new File(fileName));
		for (int i = 0; i < 6; i ++) {
			sc.nextLine();
		}
		while(sc.hasNextLine()) {
			String line = sc.nextLine();
			String[] arr = line.split("\t");
			T player;
			// This is hacky. TODO come up with better way of instantiating T
			if (type.equals("QB"))
				player = (T) new QB(arr[0]);
			else if (type.equals("WR"))
				player = (T) new WR(arr[0]);
			else if (type.equals("RB"))
				player = (T) new RB(arr[0]);
			else 
				player = (T) new TE(arr[0]);
			player.loadProjections(line);
			if (player.getProjection() > minProj) {
				players.add(player);
			}
		}
		return players;
	}
	
	public static ArrayList<Player> loadDSTWProjections() throws FileNotFoundException {
		ArrayList<Player> retVal = new ArrayList<Player>();
//		Scanner sc = new Scanner(new File("week" + WEEK + "/ESPN_Projections_DST.txt"));
//		while (sc.hasNextLine()) {
//			String[] line = sc.nextLine().split("\t");
//			DST dst = new DST(line[0], Double.parseDouble(line[1]));
//			retVal.add(dst);
//		}
		Scanner sc = new Scanner(new File("week" + WEEK + "/Defense Projections.txt"));
		sc.nextLine();
		sc.nextLine();
		while (sc.hasNextLine()) {
			String[] line = sc.nextLine().split("\t");
			DST dst = new DST(line[0].split(" ")[0], Double.parseDouble(line[15]));
			retVal.add(dst);
		}
		return retVal;
	}
	
	public static void addCosts(HashMap<String, Player> everyone) throws FileNotFoundException {
		Scanner sc = new Scanner(new File("week" + WEEK + "/DKSalaries.csv"));
		sc.nextLine();
		while (sc.hasNextLine()) {
			String[] line = sc.nextLine().replace("\"", "").split(",");
			String name = line[1].trim();
			int cost = Integer.parseInt(line[2].trim());
			if (everyone.containsKey(name)) {
				everyone.get(name).setCost(cost);
			}
		}
		remove0s(qbs);
		remove0s(rbs);
		remove0s(wrs);
		remove0s(tes);
		remove0s(dsts);
		// check which ones don't have a cost
		int count = 0;
		for (Player p : everyone.values()) {
			if (p.getCost() == 0) {
				//System.out.println(p + " has a cost of 0... setting to 3000");
				p.setCost(3000);
				count++;
			}
				
		}
	}
	
	public static void remove0s(ArrayList<Player> players) {
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			if (p.getCost() == 0) {
				players.remove(i);
				i--;
				System.out.println(p + " has cost of 0... removing from list");
			}
		}
	}
	
	public static int[] createMaxCostArray() {
		int[] maxCosts = new int[6];
		maxCosts[0] = maxCost(qbs);
		maxCosts[1] = maxCost(flexes);
		maxCosts[2] = maxCost(wrs);
		maxCosts[3] = maxCost(rbs);
		maxCosts[4] = maxCost(tes);
		System.out.println(dsts);
		maxCosts[5] = maxCost(dsts);
		return maxCosts;
	}
	
	public static HashMap<Integer, Integer> createMaxCostMap() {
		HashMap<Integer, Integer> maxCostMap = new HashMap<Integer, Integer>();
		maxCostMap.put(1, 0);
		maxCostMap.put(2, 1);
		maxCostMap.put(3, 2);
		maxCostMap.put(4, 2);
		maxCostMap.put(5, 2);
		maxCostMap.put(6, 3);
		maxCostMap.put(7, 3);
		maxCostMap.put(8, 4);
		maxCostMap.put(9, 5);
		return maxCostMap;
	}
	
	public static <T extends Player> void addToMap(HashMap<String, Player> everyone, ArrayList<T> players) {
		for (Player p : players) {
			everyone.put(p.getName(), p);
			checkForExceptions(everyone, p);
		}
	}
	public static void checkForExceptions(HashMap<String, Player> everyone, Player p) {
		if (p.getName().equals("Cecil Shorts")) {
			everyone.put("Cecil Shorts III", p);
		} else if (p.getName().equals("Ty Hilton")) {
			everyone.put("T.Y. Hilton", p);
		} else if (p.getName().equals("Roy Helu")) {
			everyone.put("Roy Helu Jr.", p);
		} else if (p.getName().equals("Steve Smith")) {
			everyone.put("Steve Smith Sr.", p);
		}else if (p.getName().equals("Timothy Wright")) {
			everyone.put("Tim Wright", p);
		} else if (p.getName().equals("Christopher Ivory")) {
			everyone.put("Chris Ivory", p);
		}  else if (p.getName().equals("Louis Murphy")) {
			everyone.put("Louis Murphy Jr.", p);
		} else if (p.getName().equals("Steve Johnson")) {
			everyone.put("Stevie Johnson", p);
		}
	}
}
