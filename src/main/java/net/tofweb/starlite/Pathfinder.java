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

	public Pathfinder(Cell startCell, Cell goalCell) {
		super();
		space.setGoalCell(goalCell);
		space.setStartCell(startCell);
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
			potentialNextCells = space.getSuccessors(currentCell);

			if (potentialNextCells.isEmpty()) {
				return false;
			}

			double minimumCost = Double.POSITIVE_INFINITY;
			Cell minimumCell = new Cell();

			for (Cell potentialNextCell : potentialNextCells) {

				if (space.isBlocked(potentialNextCell)) {
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
