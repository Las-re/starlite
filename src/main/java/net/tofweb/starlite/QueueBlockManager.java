package net.tofweb.starlite;

import java.util.HashMap;
import java.util.PriorityQueue;

public class QueueBlockManager implements BlockManager {

	private PriorityQueue<Cell> blockedCells = new PriorityQueue<Cell>();
	private HashMap<Cell, Float> openHash = new HashMap<Cell, Float>();
	private CellSpace space;
	private double kM = 0.0;

	public void insertCell(Cell cell) {
		float csum;
		cell = calculateKey(cell);
		csum = cell.getKey().hashCode();

		openHash.put(cell, csum);
		blockedCells.add(cell);
	}

	public boolean isBlocked(Cell state) {
		CellInfo info = space.getInfo(state);
		if (info == null) {
			return false;
		}

		return (info.getCost() < 0);
	}

	/*
	 * CalculateKey As per [S. Koenig, 2002]
	 * 
	 * Key of a node is a value that is going to be used to sort the open list
	 * 
	 * Key is a tuple value = [min(g(x), rhs(x)+h(x)); min(g(x), rhs(s)] These
	 * keys are compared lexiographically so ...
	 * 
	 * u < v if u.first < v.first OR ( u.first == v.first AND u.second <
	 * v.second )
	 */
	private Cell calculateKey(Cell state) {
		double cost = Math.min(space.getRHS(state), space.getG(state));

		Costs key = state.getKey();
		key.setCostPlusHeuristic(cost + Geometry.heuristic(state, space.getStartCell()) + kM);
		key.setCost(cost);

		return state;
	}

	public CellSpace getSpace() {
		return space;
	}

	public void setSpace(CellSpace space) {
		this.space = space;
	}

}
