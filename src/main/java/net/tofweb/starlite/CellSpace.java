package net.tofweb.starlite;

import java.util.HashMap;
import java.util.LinkedList;

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
public class CellSpace {

	private HashMap<Cell, CellInfo> cellHash = new HashMap<Cell, CellInfo>();
	private double kM = 0.0;
	private Cell startCell;
	private Cell goalCell;

	public CellSpace() {
		super();
	}

	public CellSpace(Cell startCell, Cell goalCell) {
		super();
		this.goalCell = goalCell;
		this.startCell = startCell;
	}

	/*
	 * Returns a list of successor states for state u, since this is an 8-way
	 * graph this list contains all of a cells neighbours. Unless the cell is
	 * occupied, in which case it has no successors.
	 */

	/*
	 * As per [S. Koenig, 2002]
	 */
	public void updateVertex(Cell cell) {
		LinkedList<Cell> successors = new LinkedList<Cell>();

		if (!cell.equals(getGoalCell())) {
			successors = getSuccessors(cell);
			double tmp = Double.POSITIVE_INFINITY;
			double tmp2;

			for (Cell successor : successors) {
				tmp2 = getG(successor) + Geometry.calcCostToMove(cell, successor);
				if (tmp2 < tmp) {
					tmp = tmp2;
				}
			}

			if (!isClose(getRHS(cell), tmp)) {
				setRHS(cell, tmp);
			}
		}

		if (!isClose(getG(cell), getRHS(cell))) {
			insertCell(cell);
		}
	}

	public CellInfo getInfo(Cell cell) {
		return cellHash.get(cell);
	}

	/*
	 * Returns the rhs value for the provided state
	 */
	public double getRHS(Cell state) {

		if (goalCell == null) {
			throw new RuntimeException("Goal cell not set");
		}

		if (state == goalCell) {
			return 0;
		}

		if (cellHash.get(state) == null) {
			return Geometry.heuristic(state, goalCell);
		}

		return cellHash.get(state).getRhs();
	}

	public void updateCellCost(Cell cell, double cost) {
		cellHash.get(cell).setCost(cost);
	}

	/*
	 * Returns the g value for the provided state
	 */
	public double getG(Cell u) {

		if (goalCell == null) {
			throw new RuntimeException("Goal cell not set");
		}

		if (cellHash.get(u) == null) {
			return Geometry.heuristic(u, goalCell);
		}

		return cellHash.get(u).getG();
	}

	public void insertCell(Cell cell) {
		// float csum;
		cell = calculateKey(cell);
		// csum = cell.getKey().hashCode();

		// openHash.put(cell, csum);
		// blockedCells.add(cell);
	}

	/*
	 * Sets the G value for state u
	 */
	public void setG(Cell state, double g) {
		makeNewCell(state);
		cellHash.get(state).setG(g);
	}

	/*
	 * Sets the rhs value for state u
	 */
	public void setRHS(Cell state, double rhs) {
		makeNewCell(state);
		cellHash.get(state).setRhs(rhs);
	}

	public LinkedList<Cell> getSuccessors(Cell state) {
		LinkedList<Cell> successors = new LinkedList<Cell>();
		Cell tempState;

		// Generate the successors, starting at the immediate right and moving
		// in a clockwise manner
		tempState = makeNewCell(state.getX() + 1, state.getY(), state.getZ(), new Costs(-1.0, -1.0));
		successors.addFirst(tempState);

		tempState = makeNewCell(state.getX(), state.getY() + 1, state.getZ(), new Costs(-1.0, -1.0));
		successors.addFirst(tempState);

		tempState = makeNewCell(state.getX() - 1, state.getY(), state.getZ(), new Costs(-1.0, -1.0));
		successors.addFirst(tempState);

		tempState = makeNewCell(state.getX(), state.getY() - 1, state.getZ(), new Costs(-1.0, -1.0));
		successors.addFirst(tempState);

		// Up one z level
		tempState = makeNewCell(state.getX(), state.getY(), state.getZ() + 1, new Costs(-1.0, -1.0));
		successors.addFirst(tempState);

		// Down one z level
		tempState = makeNewCell(state.getX(), state.getY(), state.getZ() - 1, new Costs(-1.0, -1.0));
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

		tempState = makeNewCell(state.getX() + 1, state.getY(), state.getZ(), new Costs(-1.0, -1.0));
		predecessors.addFirst(tempState);

		tempState = makeNewCell(state.getX(), state.getY() + 1, state.getZ(), new Costs(-1.0, -1.0));
		predecessors.addFirst(tempState);

		tempState = makeNewCell(state.getX() - 1, state.getY(), state.getZ(), new Costs(-1.0, -1.0));
		predecessors.addFirst(tempState);

		tempState = makeNewCell(state.getX(), state.getY() - 1, state.getZ(), new Costs(-1.0, -1.0));
		predecessors.addFirst(tempState);

		tempState = makeNewCell(state.getX(), state.getY(), state.getZ() + 1, new Costs(-1.0, -1.0));
		predecessors.addFirst(tempState);

		tempState = makeNewCell(state.getX(), state.getY(), state.getZ() - 1, new Costs(-1.0, -1.0));
		predecessors.addFirst(tempState);

		return predecessors;
	}

	public Cell makeNewCell(int x, int y, int z) {
		return makeNewCell(x, y, z, null);
	}

	public Cell makeNewCell(int x, int y, int z, Costs k) {

		Cell state = new Cell();
		state.setX(x);
		state.setY(y);
		state.setZ(z);
		state.setKey(k);

		return makeNewCell(state);
	}

	/*
	 * Checks if a cell is in the hash table, if not it adds it in.
	 */
	public Cell makeNewCell(Cell state) {
		if (cellHash.get(state) != null) {
			return state;
		}

		CellInfo tmp = new CellInfo();

		if (goalCell == null) {
			throw new RuntimeException("Goal cell not set");
		}

		double costToGoal = Geometry.heuristic(state, goalCell);
		tmp.setRhs(costToGoal);
		tmp.setG(costToGoal);
		cellHash.put(state, tmp);

		System.out.println(state);

		Costs key = state.getKey();
		if (key != null && !key.equals(new Costs(-1.0, -1.0))) {
			updateVertex(state);
		}

		calculateKey(state);

		return state;
	}

	/*
	 * Returns true if x and y are within 10E-5, false otherwise
	 */
	public boolean isClose(double var1, double var2) {
		if (var1 == Double.POSITIVE_INFINITY && var2 == Double.POSITIVE_INFINITY) {
			return true;
		}

		return (Math.abs(var1 - var2) < 0.00001);
	}

	public Cell getStartCell() {
		return startCell;
	}

	public void setStartCell(int x, int y, int z) {
		// this.startCell = makeNewCell(startCell);
		Cell cell = new Cell();
		cell.setX(x);
		cell.setY(y);
		cell.setZ(z);
		this.startCell = cell;

		CellInfo startCellInfo = new CellInfo();
		double totalPathCost = Geometry.heuristic(startCell, goalCell);
		startCellInfo.setRhs(totalPathCost);
		startCellInfo.setG(totalPathCost);
		cellHash.put(startCell, startCellInfo);

		this.startCell = calculateKey(startCell); // used to be last
	}

	public Cell getGoalCell() {
		return goalCell;
	}

	public void setGoalCell(int x, int y, int z) {
		Cell cell = new Cell();
		cell.setX(x);
		cell.setY(y);
		cell.setZ(z);

		this.goalCell = cell;
		this.cellHash.put(goalCell, new CellInfo());
	}

	public Cell calculateKey(Cell state) {

		Cell startCell = getStartCell();

		if (startCell == null) {
			throw new RuntimeException("Start cell not set");
		}

		double cost = Math.min(getRHS(state), getG(state));

		Costs key = state.getKey();

		if (key == null) {
			key = new Costs(0.0, 0.0);
		}

		key.setCostPlusHeuristic(cost + Geometry.heuristic(state, startCell) + kM);
		key.setCost(cost);

		return state;
	}
}
