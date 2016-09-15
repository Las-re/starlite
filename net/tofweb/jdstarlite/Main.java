package net.tofweb.jdstarlite;

import java.util.Date;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		// Create pathfinder
		Long start = new Date().getTime();
		DStarLite dsl = new DStarLite();

		// set start and goal nodes
		dsl.init(40135, 35214, -55123, -49452);

		// set impassable nodes
		dsl.updateCell(2, 1, -1);
		dsl.updateCell(2, 0, -1);
		dsl.updateCell(2, 2, -1);
		dsl.updateCell(3, 0, -1);

		// perform the pathfinding
		dsl.pathfind();

		// get and print the path
		List<State> path = dsl.getPath();
		State lastState = null;
		for (State i : path) {
			//System.out.println("x: " + i.x + " y: " + i.y);
			lastState = i;
		}

		Long end = new Date().getTime();
		
		System.out.println("Used " + path.size() + " steps");
		System.out.println("Ended at " + lastState.getX() + ", " + lastState.getY());
		System.out.println("Took " + (end-start) + " miliseconds");
		
		/*
		 * Used 95259 steps
		 * Ended at -55123, -49452
		 * Took 100 miliseconds
		 */
		
	}

}
