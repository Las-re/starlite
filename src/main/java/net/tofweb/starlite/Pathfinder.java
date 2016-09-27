package net.tofweb.starlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/*
 * @author daniel beard
 * http://danielbeard.wordpress.com
 * http://github.com/paintstripper
 * https://github.com/daniel-beard/DStarLiteJava
 *
 * Copyright (C) 2012 Daniel Beard
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * @author Lynn Owens
 * https://github.com/LynnOwens
 */
public class Pathfinder {

	public static final double DEFAULT_CELL_COST = 1.0;
	public static final double M_SQRT2 = Math.sqrt(2.0);

	private List<Cell> path = new ArrayList<Cell>();
	private double kM = 0.0;
	private Cell startCell = new Cell();
	private Cell goalCell = new Cell();
	private int maxSteps = 80000;
	private PriorityQueue<Cell> blockedCells = new PriorityQueue<Cell>(); //
	private HashMap<Cell, CellInfo> cellHash = new HashMap<Cell, CellInfo>();
	private HashMap<Cell, Float> openHash = new HashMap<Cell, Float>();
	private boolean isPathFound;

	public Pathfinder(Cell startCell, Cell goalCell) {
		super();

		this.startCell = startCell;
		this.goalCell = goalCell;

		CellInfo goalCellInfo = new CellInfo();
		cellHash.put(goalCell, goalCellInfo);

		CellInfo startCellInfo = new CellInfo();
		double totalPathCost = heuristic(startCell, goalCell);
		startCellInfo.setRhs(totalPathCost);
		startCellInfo.setG(totalPathCost);
		cellHash.put(startCell, startCellInfo);

		startCell = calculateKey(startCell);
	}

	/**
	 * Find a path to the goal
	 * 
	 * Returns true if a path is found, false otherwise
	 * 
	 * @return
	 */
	public boolean pathfind() {
		path.clear();

		if (!canPath()) {
			return false;
		}

		LinkedList<Cell> potentialNextCells = new LinkedList<Cell>();
		Cell currentCell = startCell;

		if (getG(startCell) == Double.POSITIVE_INFINITY) {
			return false;
		}

		while (currentCell.neq(goalCell)) {
			path.add(currentCell);
			potentialNextCells = getSuccessors(currentCell);

			if (potentialNextCells.isEmpty()) {
				return false;
			}

			double minimumCost = Double.POSITIVE_INFINITY;
			Cell minimumCell = new Cell();

			for (Cell potentialNextCell : potentialNextCells) {

				if (isBlocked(potentialNextCell)) {
					continue;
				}

				double costToMove = calcCostToMove(currentCell, potentialNextCell);
				double euclideanDistance = euclideanDistance(potentialNextCell, goalCell)
						+ euclideanDistance(startCell, potentialNextCell);
				costToMove += getG(potentialNextCell);

				// If the cost to move is essentially zero ...
				if (isClose(costToMove, minimumCost)) {
					if (0 > euclideanDistance) {
						minimumCost = costToMove;
						minimumCell = potentialNextCell;
					}
				} else if (costToMove < minimumCost) {
					minimumCost = costToMove;
					minimumCell = potentialNextCell;
				}
			}

			potentialNextCells.clear();
			currentCell = new Cell(minimumCell);
		}

		path.add(goalCell);

		return true;
	}

	public void blockCell(Cell blockedCell) {
		if ((blockedCell.eq(startCell)) || (blockedCell.eq(goalCell))) {
			return;
		}

		double cost = -1;
		makeNewCell(blockedCell);
		cellHash.get(blockedCell).setCost(cost);
		updateVertex(blockedCell);
	}

	public List<Cell> getPath() {
		return path;
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
		double cost = Math.min(getRHS(state), getG(state));

		Costs key = state.getKey();
		key.setCostPlusHeuristic(cost + heuristic(state, startCell) + kM);
		key.setCost(cost);

		return state;
	}

	/*
	 * Returns the rhs value for the provided state
	 */
	private double getRHS(Cell state) {
		if (state == goalCell) {
			return 0;
		}

		if (cellHash.get(state) == null) {
			return heuristic(state, goalCell);
		}

		return cellHash.get(state).getRhs();
	}

	/*
	 * Returns the g value for the provided state
	 */
	private double getG(Cell u) {
		if (cellHash.get(u) == null) {
			return heuristic(u, goalCell);
		}

		return cellHash.get(u).getG();
	}

	/*
	 * Pretty self explanatory, the heuristic we use is the 8-way distance
	 * scaled by a constant DEFAULT_CELL_COST (should be set to <= min cost)
	 */
	private double heuristic(Cell a, Cell b) {
		return euclideanDistance(a, b) * DEFAULT_CELL_COST;
	}

	private double euclideanDistance(Cell aState, Cell bState) {
		float x = aState.getX() - bState.getX();
		float y = aState.getY() - bState.getY();
		float z = aState.getZ() - bState.getZ();
		return Math.sqrt(x * x + y * y + z * z);
	}

	private double calcCostToMove(Cell a, Cell b) {
		// int xd = Math.abs(a.getX() - b.getX());
		// int yd = Math.abs(a.getY() - b.getY());
		// int zd = Math.abs(a.getZ() - b.getZ());
		// double scale = 1;
		//
		// if (xd + yd + zd > 1) {
		// scale = M_SQRT2;
		// }
		//
		// if (cellHash.containsKey(a) == false) {
		// return scale * DEFAULT_CELL_COST;
		// }
		//
		// return scale * cellHash.get(a).getCost();
		return euclideanDistance(a, b);
	}

	/*
	 * As per [S. Koenig,2002] except for two main modifications:
	 * 
	 * 1. We stop planning after a number of steps, 'maxsteps' we do this
	 * because this algorithm can plan forever if the start is surrounded by
	 * obstacles 2. We lazily remove states from the open list so we never have
	 * to iterate through it.
	 * 
	 * Returns false if there is no path to goal
	 */
	private boolean canPath() {
		LinkedList<Cell> states = new LinkedList<Cell>();

		if (blockedCells.isEmpty()) {
			return false;
		}

		int numSteps = 0;
		startCell = calculateKey(startCell);

		while (!blockedCells.isEmpty()) {
			Cell potentiallyBlockedState = blockedCells.poll();

			if (potentiallyBlockedState.lt(startCell) || (getRHS(startCell) != getG(startCell))) {

				if (numSteps++ > maxSteps) {
					throw new RuntimeException("Maximum number of iterations hit: " + maxSteps);
				}

				Cell currentState = potentiallyBlockedState;

				openHash.remove(currentState);

				Cell previousState = new Cell(currentState);
				currentState = calculateKey(currentState);

				if (previousState.lt(currentState)) {
					addBlockedCell(currentState);
				} else if (getG(currentState) > getRHS(currentState)) {
					setG(currentState, getRHS(currentState));
					states = getPredecessors(currentState);

					// Apparently to indicate paths into the blocked state are
					// inadvisable
					for (Cell state : states) {
						updateVertex(state);
					}
				} else {
					setG(currentState, Double.POSITIVE_INFINITY);
					states = getPredecessors(currentState);

					// Apparently to indicate paths into the blocked state are
					// inadvisable
					for (Cell state : states) {
						updateVertex(state);
					}

					updateVertex(currentState);
				}
			}
		}

		return true;
	}

	/*
	 * Returns a list of successor states for state u, since this is an 8-way
	 * graph this list contains all of a cells neighbours. Unless the cell is
	 * occupied, in which case it has no successors.
	 */
	private LinkedList<Cell> getSuccessors(Cell state) {
		LinkedList<Cell> successors = new LinkedList<Cell>();
		Cell tempState;

		if (isBlocked(state)) {
			// We cannot move into this cell
			// Therefore it has no successor states
			return successors;
		}

		// Generate the successors, starting at the immediate right and moving
		// in a clockwise manner
		tempState = new Cell(state.getX() + 1, state.getY(), state.getZ(), new Costs(-1.0, -1.0));
		successors.addFirst(tempState);

		tempState = new Cell(state.getX(), state.getY() + 1, state.getZ(), new Costs(-1.0, -1.0));
		successors.addFirst(tempState);

		tempState = new Cell(state.getX() - 1, state.getY(), state.getZ(), new Costs(-1.0, -1.0));
		successors.addFirst(tempState);

		tempState = new Cell(state.getX(), state.getY() - 1, state.getZ(), new Costs(-1.0, -1.0));
		successors.addFirst(tempState);

		// Up one z level
		tempState = new Cell(state.getX(), state.getY(), state.getZ() + 1, new Costs(-1.0, -1.0));
		successors.addFirst(tempState);

		// Down one z level
		tempState = new Cell(state.getX(), state.getY(), state.getZ() - 1, new Costs(-1.0, -1.0));
		successors.addFirst(tempState);

		return successors;
	}

	/*
	 * Returns a list of all the predecessor states for state u. Since this is
	 * for an 8-way connected graph, the list contains all the neighbours for
	 * state u. Occupied neighbours are not added to the list
	 */
	private LinkedList<Cell> getPredecessors(Cell state) {
		LinkedList<Cell> predecessors = new LinkedList<Cell>();
		Cell tempState;

		tempState = new Cell(state.getX() + 1, state.getY(), state.getZ(), new Costs(-1.0, -1.0));
		if (!isBlocked(tempState)) {
			predecessors.addFirst(tempState);
		}

		tempState = new Cell(state.getX(), state.getY() + 1, state.getZ(), new Costs(-1.0, -1.0));
		if (!isBlocked(tempState)) {
			predecessors.addFirst(tempState);
		}

		tempState = new Cell(state.getX() - 1, state.getY(), state.getZ(), new Costs(-1.0, -1.0));
		if (!isBlocked(tempState)) {
			predecessors.addFirst(tempState);
		}

		tempState = new Cell(state.getX(), state.getY() - 1, state.getZ(), new Costs(-1.0, -1.0));
		if (!isBlocked(tempState)) {
			predecessors.addFirst(tempState);
		}

		tempState = new Cell(state.getX(), state.getY(), state.getZ() + 1, new Costs(-1.0, -1.0));
		if (!isBlocked(tempState)) {
			predecessors.addFirst(tempState);
		}

		tempState = new Cell(state.getX(), state.getY(), state.getZ() - 1, new Costs(-1.0, -1.0));
		if (!isBlocked(tempState)) {
			predecessors.addFirst(tempState);
		}

		return predecessors;
	}

	/*
	 * As per [S. Koenig, 2002]
	 */
	private void updateVertex(Cell state) {
		LinkedList<Cell> successors = new LinkedList<Cell>();

		if (state.neq(goalCell)) {
			successors = getSuccessors(state);
			double tmp = Double.POSITIVE_INFINITY;
			double tmp2;

			for (Cell successor : successors) {
				tmp2 = getG(successor) + calcCostToMove(state, successor);
				if (tmp2 < tmp) {
					tmp = tmp2;
				}
			}

			if (!isClose(getRHS(state), tmp)) {
				setRHS(state, tmp);
			}
		}

		if (!isClose(getG(state), getRHS(state))) {
			addBlockedCell(state);
		}
	}

	/*
	 * Sets the G value for state u
	 */
	private void setG(Cell state, double g) {
		makeNewCell(state);
		cellHash.get(state).setG(g);
	}

	/*
	 * Sets the rhs value for state u
	 */
	private void setRHS(Cell state, double rhs) {
		makeNewCell(state);
		cellHash.get(state).setRhs(rhs);
	}

	/*
	 * Checks if a cell is in the hash table, if not it adds it in.
	 */
	private void makeNewCell(Cell state) {
		if (cellHash.get(state) != null) {
			return;
		}

		CellInfo tmp = new CellInfo();
		double costToGoal = heuristic(state, goalCell);
		tmp.setRhs(costToGoal);
		tmp.setG(costToGoal);
		cellHash.put(state, tmp);
	}

	/*
	 * Inserts state u into openList and openHash
	 */
	private void addBlockedCell(Cell cell) {
		float csum;
		cell = calculateKey(cell);
		csum = cell.getKey().hashCode();

		openHash.put(cell, csum);
		blockedCells.add(cell);
	}

	/*
	 * Returns true if the cell is occupied (non-traversable), false otherwise.
	 * Non-traversable are marked with a cost < 0
	 */
	private boolean isBlocked(Cell state) {
		if (cellHash.get(state) == null) {
			return false;
		}

		return (cellHash.get(state).getCost() < 0);
	}

	/*
	 * Returns true if x and y are within 10E-5, false otherwise
	 */
	private boolean isClose(double var1, double var2) {
		if (var1 == Double.POSITIVE_INFINITY && var2 == Double.POSITIVE_INFINITY) {
			return true;
		}

		return (Math.abs(var1 - var2) < 0.00001);
	}

}
