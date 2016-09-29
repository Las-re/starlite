package net.tofweb.starlite;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

	private List<Cell> path = new ArrayList<Cell>();
	private CellSpace space = new CellSpace();
	private QueueBlockManager observer = new QueueBlockManager();

	public Pathfinder(Cell startCell, Cell goalCell) {
		super();
		space.setGoalCell(goalCell);
		space.setStartCell(startCell);
		observer.setSpace(space);
	}

	/**
	 * Used to be updateCell, called from Main
	 * 
	 * @param blockedCell
	 */
	public void blockCell(Cell blockedCell) {
		if ((blockedCell.equals(space.getStartCell())) || (blockedCell.equals(space.getGoalCell()))) {
			return;
		}

		double cost = -1;
		space.makeNewCell(blockedCell);
		space.updateCellCost(blockedCell, cost);
		updateVertex(blockedCell);
	}

	/**
	 * Find a path to the goal
	 * 
	 * Returns true if a path is found, false otherwise
	 * 
	 * @return
	 */
	public boolean findPath() {
		path.clear();

		LinkedList<Cell> potentialNextCells = new LinkedList<Cell>();
		Cell currentCell = space.getStartCell();

		if (space.getG(space.getStartCell()) == Double.POSITIVE_INFINITY) {
			return false;
		}

		boolean isTrapped = false;
		while (!currentCell.equals(space.getGoalCell()) && !isTrapped) {
			isTrapped = true;
			path.add(currentCell);
			potentialNextCells = getSuccessors(currentCell);

			if (potentialNextCells.isEmpty()) {
				return false;
			}

			double minimumCost = Double.POSITIVE_INFINITY;
			Cell minimumCell = new Cell();

			for (Cell potentialNextCell : potentialNextCells) {

				if (observer.isBlocked(potentialNextCell)) {
					continue;
				} else {
					isTrapped = false;
				}

				double costToMove = Geometry.calcCostToMove(currentCell, potentialNextCell);
				double euclideanDistance = Geometry.euclideanDistance(potentialNextCell, space.getGoalCell())
						+ Geometry.euclideanDistance(space.getStartCell(), potentialNextCell);
				costToMove += space.getG(potentialNextCell);

				// If the cost to move is essentially zero ...
				if (space.isClose(costToMove, minimumCost)) {
					if (0 > euclideanDistance) {
						minimumCost = costToMove;
						minimumCell = potentialNextCell;
					}
				} else if (costToMove < minimumCost) {
					minimumCost = costToMove;
					minimumCell = potentialNextCell;
				}
			}

			if (!isTrapped) {
				potentialNextCells.clear();
				currentCell = new Cell(minimumCell);
			}
		}

		if (!isTrapped)
			path.add(space.getGoalCell());

		return !isTrapped;
	}

	/*
	 * Returns a list of successor states for state u, since this is an 8-way
	 * graph this list contains all of a cells neighbours. Unless the cell is
	 * occupied, in which case it has no successors.
	 */
	public LinkedList<Cell> getSuccessors(Cell state) {
		LinkedList<Cell> successors = new LinkedList<Cell>();
		Cell tempState;

		if (observer.isBlocked(state)) {
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
	public LinkedList<Cell> getPredecessors(Cell state) {
		LinkedList<Cell> predecessors = new LinkedList<Cell>();
		Cell tempState;

		tempState = new Cell(state.getX() + 1, state.getY(), state.getZ(), new Costs(-1.0, -1.0));
		if (!observer.isBlocked(tempState)) {
			predecessors.addFirst(tempState);
		}

		tempState = new Cell(state.getX(), state.getY() + 1, state.getZ(), new Costs(-1.0, -1.0));
		if (!observer.isBlocked(tempState)) {
			predecessors.addFirst(tempState);
		}

		tempState = new Cell(state.getX() - 1, state.getY(), state.getZ(), new Costs(-1.0, -1.0));
		if (!observer.isBlocked(tempState)) {
			predecessors.addFirst(tempState);
		}

		tempState = new Cell(state.getX(), state.getY() - 1, state.getZ(), new Costs(-1.0, -1.0));
		if (!observer.isBlocked(tempState)) {
			predecessors.addFirst(tempState);
		}

		tempState = new Cell(state.getX(), state.getY(), state.getZ() + 1, new Costs(-1.0, -1.0));
		if (!observer.isBlocked(tempState)) {
			predecessors.addFirst(tempState);
		}

		tempState = new Cell(state.getX(), state.getY(), state.getZ() - 1, new Costs(-1.0, -1.0));
		if (!observer.isBlocked(tempState)) {
			predecessors.addFirst(tempState);
		}

		return predecessors;
	}

	/*
	 * As per [S. Koenig, 2002]
	 */
	private void updateVertex(Cell state) {
		LinkedList<Cell> successors = new LinkedList<Cell>();

		if (!state.equals(space.getGoalCell())) {
			successors = getSuccessors(state);
			double tmp = Double.POSITIVE_INFINITY;
			double tmp2;

			for (Cell successor : successors) {
				tmp2 = space.getG(successor) + Geometry.calcCostToMove(state, successor);
				if (tmp2 < tmp) {
					tmp = tmp2;
				}
			}

			if (!space.isClose(space.getRHS(state), tmp)) {
				space.setRHS(state, tmp);
			}
		}

		if (!space.isClose(space.getG(state), space.getRHS(state))) {
			observer.insertCell(state);
		}
	}

	public List<Cell> getPath() {
		return path;
	}

	public void setPath(List<Cell> path) {
		this.path = path;
	}

	public CellSpace getSpace() {
		return space;
	}

	public void setSpace(CellSpace space) {
		this.space = space;
	}

}
