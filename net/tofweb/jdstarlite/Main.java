package net.tofweb.jdstarlite;

import java.util.List;

public class Main {

	public static void main(String[] args) {
		// Create pathfinder
		DStarLite pf = new DStarLite();

		// set start and goal nodes
		//pf.init(0, 1, 3, 1);
		pf.init(40135, 35214, -55123, -49452);

		// set impassable nodes
		pf.updateCell(2, 1, -1);
		pf.updateCell(2, 0, -1);
		pf.updateCell(2, 2, -1);
		pf.updateCell(3, 0, -1);

		// perform the pathfinding
		pf.replan();

		// get and print the path
		List<State> path = pf.getPath();
		for (State i : path) {
			System.out.println("x: " + i.x + " y: " + i.y); 
		}

	}

}
