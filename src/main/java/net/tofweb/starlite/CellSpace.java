package net.tofweb.starlite;

import java.util.HashMap;

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
	private Cell startCell;
	private Cell goalCell;

	public CellInfo getInfo(Cell cell) {
		return cellHash.get(cell);
	}

	/*
	 * Returns the rhs value for the provided state
	 */
	public double getRHS(Cell state) {
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
		if (cellHash.get(u) == null) {
			return Geometry.heuristic(u, goalCell);
		}

		return cellHash.get(u).getG();
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

	/*
	 * Checks if a cell is in the hash table, if not it adds it in.
	 */
	public void makeNewCell(Cell state) {
		if (cellHash.get(state) != null) {
			return;
		}

		CellInfo tmp = new CellInfo();
		double costToGoal = Geometry.heuristic(state, goalCell);
		tmp.setRhs(costToGoal);
		tmp.setG(costToGoal);
		cellHash.put(state, tmp);
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

	public void setStartCell(Cell startCell) {
		this.startCell = startCell;

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

	public void setGoalCell(Cell goalCell) {
		this.goalCell = goalCell;
		this.cellHash.put(goalCell, new CellInfo());
	}

	// FIXME copied pasted
	private Cell calculateKey(Cell state) {
		double cost = Math.min(getRHS(state), getG(state));

		Costs key = state.getKey();
		key.setCostPlusHeuristic(cost + Geometry.heuristic(state, getStartCell()) + 0.0);
		key.setCost(cost);

		return state;
	}
}
