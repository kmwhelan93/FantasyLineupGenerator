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
import lineup.LineUp;


public class FindOptimalLineups {
	private static final String WEEK = "08";
	private static final int budget = 50000;
	private static final int minProj = 2;
	private static final double minValueRatio = 2.8;
	private static final int cutOffNum = 6;
	private static int countOverMin = 0;
	
	private static ArrayList<Player> qbs;
	private static ArrayList<Player> rbs;
	private static ArrayList<Player> wrs;
	private static ArrayList<Player> tes;
	private static ArrayList<Player> flexes;
	private static ArrayList<Player> dsts;
	
	private static ArrayList<LineUp> lineUps = new ArrayList<LineUp>();
	private static HashMap<Integer, Integer> maxCostMap;
	private static ArrayList<ArrayList<Player>> playerMatrix;
	private static int[] minCosts = {5000, 3000, 3000, 3000, 3000, 2400};
	
	public static void main (String[] args) throws FileNotFoundException {
		// read in lists of players
		qbs = FindOptimalLineups.<QB>loadPlayersWProjections("QB");
		rbs = FindOptimalLineups.<RB>loadPlayersWProjections("RB");
		wrs = FindOptimalLineups.<WR>loadPlayersWProjections("WR");
		tes = FindOptimalLineups.<TE>loadPlayersWProjections("TE");
		dsts = FindOptimalLineups.loadDSTWProjections();
		
		HashMap<String, Player> everyone = new HashMap<String, Player>();
		addToMap(everyone, qbs);
		addToMap(everyone, rbs);
		addToMap(everyone, wrs);
		addToMap(everyone, tes);
		addToMap(everyone, dsts);
		
		addCosts(everyone);
		
		// check which ones don't have a cost
		int count = 0;
		for (Player p : everyone.values()) {
			if (p.getCost() == 0) {
				System.out.println(p + " has a cost of 0... setting to 3000");
				p.setCost(3000);
				count++;
			}
				
		}
		
		purgePoorValueRatios();
		System.out.println("Count over min: " + countOverMin);
		
		flexes = new ArrayList<Player>();
		flexes.addAll(rbs);
		flexes.addAll(wrs);
		flexes.addAll(tes);
		
		int[] maxCosts = new int[6];
		maxCosts[0] = maxCost(qbs);
		maxCosts[1] = maxCost(flexes);
		maxCosts[2] = maxCost(wrs);
		maxCosts[3] = maxCost(rbs);
		maxCosts[4] = maxCost(tes);
		maxCosts[5] = maxCost(dsts);
		
		maxCostMap = new HashMap<Integer, Integer>();
		maxCostMap.put(1, 0);
		maxCostMap.put(2, 1);
		maxCostMap.put(3, 2);
		maxCostMap.put(4, 2);
		maxCostMap.put(5, 2);
		maxCostMap.put(6, 3);
		maxCostMap.put(7, 3);
		maxCostMap.put(8, 4);
		maxCostMap.put(9, 5);
		
		
		
		playerMatrix = new ArrayList<ArrayList<Player>>();
		playerMatrix.add(qbs);
		playerMatrix.add(flexes);
		playerMatrix.add(wrs);
		playerMatrix.add(rbs);
		playerMatrix.add(tes);
		playerMatrix.add(dsts);
		
		
		
		generateLineUps(1, new LineUp(budget), maxCosts);
		
		System.out.println("DONE");
		System.out.println(lineUps.size());
		Collections.sort(lineUps);
		for (int i = 0; i < 50; i++) {
			System.out.println(lineUps.get(i));
		}
	}
	
	public static void purgePoorValueRatios() {
		qbs = new ArrayList<Player>(qbs.subList(0, cutOffNum));
		rbs = new ArrayList<Player>(rbs.subList(0, cutOffNum * 2));
		wrs = new ArrayList<Player>(wrs.subList(0, cutOffNum * 3));
		tes = new ArrayList<Player>(tes.subList(0, cutOffNum));
		dsts = new ArrayList<Player>(dsts.subList(0, cutOffNum));
		//purgePoorValueRatios(dsts, cutOffNum);
	}
	
	public static void purgePoorValueRatios(ArrayList<Player> players, int cutOffNum) {
//		for (int i = 0; i < players.size(); i++) {
//			double valueRatio = players.get(i).getProjection() / players.get(i).getCost() * 1000;
//			if (valueRatio < minValueRatio) {
//				players.remove(i);
//				i--;
//			} else {
//				countOverMin++;
//			}
//		}
	}
	
	public static int maxCost(ArrayList<Player> players) {
		int maxCost = 0;
		for (Player p : players) {
			if (p.getCost() > maxCost) {
				maxCost = p.getCost();
			}
		}
		return maxCost;
	}
	
	public static void generateLineUps(int depth, LineUp curLineUp, int[] maxCosts) {
		if (depth == 9) {
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
					copyMaxCosts[2] = p.getCost();
					copyMaxCosts[3] = p.getCost();
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
		Scanner sc = new Scanner(new File("week" + WEEK + "/ESPN_Projections_DST.txt"));
		while (sc.hasNextLine()) {
			String[] line = sc.nextLine().split("\t");
			DST dst = new DST(line[0], Double.parseDouble(line[1]));
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
		} else if (p.getName().equals("David Nelson")) {
			everyone.put("Jordy Nelson", p);
		} else if (p.getName().equals("Ty Hilton")) {
			everyone.put("T.Y. Hilton", p);
		} else if (p.getName().equals("Roy Helu")) {
			everyone.put("Roy Helu Jr.", p);
		} else if (p.getName().equals("Steve Smith")) {
			everyone.put("Steve Smith Sr.", p);
		} else if (p.getName().equals("Timothy Wright")) {
			everyone.put("Tim Wright", p);
		} else if (p.getName().equals("Christopher Ivory")) {
			everyone.put("Chris Ivory", p);
		}  else if (p.getName().equals("Louis Murphy")) {
			everyone.put("Louis Murphy Jr.", p);
		}
	}
}
