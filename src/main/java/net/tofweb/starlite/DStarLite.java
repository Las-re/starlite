package net.tofweb.starlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
public class DStarLite {

	public static final double DEFAULT_CELL_COST = 1.0;
	public static final double M_SQRT2 = Math.sqrt(2.0);

	private List<State> path = new ArrayList<State>();
	private double k_m;
	private State startState = new State();
	private State goalState = new State();
	private State lastState = new State();
	private int maxSteps = 80000;
	private PriorityQueue<State> blockedStates = new PriorityQueue<State>(); //
	private HashMap<State, CellInfo> cellHash = new HashMap<State, CellInfo>();
	private HashMap<State, Float> openHash = new HashMap<State, Float>();

	/**
	 * Initialize the pathfinder
	 * 
	 * @param sX
	 *            starting x coord
	 * @param sY
	 *            starting y coord
	 * @param gX
	 *            goal x coord
	 * @param gY
	 *            goal y coord
	 */
	public void init(int sX, int sY, int sZ, int gX, int gY, int gZ) {
		cellHash.clear();
		path.clear();
		openHash.clear();

		while (!blockedStates.isEmpty()) {
			blockedStates.poll();
		}

		k_m = 0;

		startState.setX(sX);
		startState.setY(sY);
		startState.setZ(sZ);

		goalState.setX(gX);
		goalState.setY(gY);
		goalState.setZ(gZ);

		CellInfo goalCell = new CellInfo();
		cellHash.put(goalState, goalCell);

		CellInfo startCell = new CellInfo();
		double totalPathCost = heuristic(startState, goalState);
		startCell.setRhs(totalPathCost);
		startCell.setG(totalPathCost);
		cellHash.put(startState, startCell);

		startState = calculateKey(startState);

		lastState = startState;
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
	private State calculateKey(State state) {
		double cost = Math.min(getRHS(state), getG(state));

		Pair<Double, Double> key = state.getKey();
		key.setFirst(cost + heuristic(state, startState) + k_m);
		key.setSecond(cost);

		return state;
	}

	/*
	 * Returns the rhs value for the provided state
	 */
	private double getRHS(State state) {
		if (state == goalState) {
			return 0;
		}

		if (cellHash.get(state) == null) {
			return heuristic(state, goalState);
		}

		return cellHash.get(state).getRhs();
	}

	/*
	 * Returns the g value for the provided state
	 */
	private double getG(State u) {
		if (cellHash.get(u) == null) { // 3,2,1 rhs is infinity but cost isn't
			return heuristic(u, goalState);
		}

		return cellHash.get(u).getG(); // FIXME Mine should return Infinity but
										// doesn't
	}

	/*
	 * Pretty self explanatory, the heuristic we use is the 8-way distance
	 * scaled by a constant DEFAULT_CELL_COST (should be set to <= min cost)
	 */
	private double heuristic(State a, State b) {
		return euclideanDistance(a, b) * DEFAULT_CELL_COST;
	}

	/*
	 * -7, -4, 3 17, 6, 1 = 26.07681
	 * 
	 * @param aState
	 * 
	 * @param bState
	 * 
	 * @return
	 */
	private double euclideanDistance(State aState, State bState) {
		int x1 = aState.getX();
		int x2 = bState.getX();
		int y1 = aState.getY();
		int y2 = bState.getY();
		int z1 = aState.getZ();
		int z2 = bState.getZ();

		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2));
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

		boolean canPath = computeShortestPath();
		if (!canPath) {
			return false;
		}

		LinkedList<State> potentialNextStates = new LinkedList<State>();
		State currentState = startState;

		if (getG(startState) == Double.POSITIVE_INFINITY) {
			// We can't even get home
			return false;
		}

		while (currentState.neq(goalState)) {
			path.add(currentState);
			potentialNextStates = new LinkedList<State>();
			potentialNextStates = getSuccessors(currentState);

			if (potentialNextStates.isEmpty()) {
				// Hit a dead end
				return false;
			}

			double minimumCost = Double.POSITIVE_INFINITY;
			State minimumState = new State();

			for (State successorState : potentialNextStates) {
				
				if (occupied(successorState)) {
					continue;
				}
				
				double cost = calcCostToMove(currentState, successorState); // 1.0
				double euclideanDistance = trueDist(successorState, goalState) + trueDist(startState, successorState); // 1.4142135623730951
																														// +
																														// 1.4142135623730951
				cost += getG(successorState); // 1.7320508075688772

				// If the cost to move is essentially zero ...
				if (isClose(cost, minimumCost)) {
					if (0 > euclideanDistance) {
						minimumCost = cost;
						minimumState = successorState;
					}
				} else if (cost < minimumCost) {
					minimumCost = cost; // Falling in here where it shouldnt
					minimumState = successorState;
				}
			}

			potentialNextStates.clear();
			currentState = new State(minimumState);
		}

		path.add(goalState);

		return true;
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
	private boolean computeShortestPath() {
		LinkedList<State> states = new LinkedList<State>();

		if (blockedStates.isEmpty()) {
			return false;
		}

		int numSteps = 0;
		startState = calculateKey(startState);

		while (!blockedStates.isEmpty()) {
			State potentiallyBlockedState = blockedStates.poll();
			
			if (potentiallyBlockedState.lt(startState) || (getRHS(startState) != getG(startState))) {

				if (numSteps++ > maxSteps) {
					throw new RuntimeException("Maximum number of iterations hit: " + maxSteps);
				}

				State currentState = potentiallyBlockedState;

				openHash.remove(currentState);

				State previousState = new State(currentState);
				currentState = calculateKey(currentState);

				if (previousState.lt(currentState)) {
					addOpenCandidate(currentState);
				} else if (getG(currentState) > getRHS(currentState)) {
					setG(currentState, getRHS(currentState));
					states = getPredecessors(currentState);

					// Apparently to indicate paths into the blocked state are
					// inadvisable
					for (State state : states) {
						updateVertex(state);
					}
				} else {
					setG(currentState, Double.POSITIVE_INFINITY);
					states = getPredecessors(currentState);

					// Apparently to indicate paths into the blocked state are
					// inadvisable
					for (State state : states) {
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
	private LinkedList<State> getSuccessors(State state) {
		LinkedList<State> successors = new LinkedList<State>();
		State tempState;

		if (occupied(state)) {
			// We cannot move into this cell
			// Therefore it has no successor states
			return successors;
		}

		// Generate the successors, starting at the immediate right and moving
		// in a clockwise manner
		tempState = new State(state.getX() + 1, state.getY(), state.getZ(), new Pair<Double, Double>(-1.0, -1.0));
		successors.addFirst(tempState);

		tempState = new State(state.getX(), state.getY() + 1, state.getZ(), new Pair<Double, Double>(-1.0, -1.0));
		successors.addFirst(tempState);

		tempState = new State(state.getX() - 1, state.getY(), state.getZ(), new Pair<Double, Double>(-1.0, -1.0));
		successors.addFirst(tempState);

		tempState = new State(state.getX(), state.getY() - 1, state.getZ(), new Pair<Double, Double>(-1.0, -1.0));
		successors.addFirst(tempState);

		// Up one z level
		tempState = new State(state.getX(), state.getY(), state.getZ() + 1, new Pair<Double, Double>(-1.0, -1.0));
		successors.addFirst(tempState);

		// Down one z level
		tempState = new State(state.getX(), state.getY(), state.getZ() - 1, new Pair<Double, Double>(-1.0, -1.0));
		successors.addFirst(tempState);

		return successors;
	}

	/*
	 * Returns a list of all the predecessor states for state u. Since this is
	 * for an 8-way connected graph, the list contains all the neighbours for
	 * state u. Occupied neighbours are not added to the list
	 */
	private LinkedList<State> getPredecessors(State state) {
		LinkedList<State> predecessors = new LinkedList<State>();
		State tempState;

		tempState = new State(state.getX() + 1, state.getY(), state.getZ(), new Pair<Double, Double>(-1.0, -1.0));
		if (!occupied(tempState)) {
			predecessors.addFirst(tempState);
		}

		tempState = new State(state.getX(), state.getY() + 1, state.getZ(), new Pair<Double, Double>(-1.0, -1.0));
		if (!occupied(tempState)) {
			predecessors.addFirst(tempState);
		}

		tempState = new State(state.getX() - 1, state.getY(), state.getZ(), new Pair<Double, Double>(-1.0, -1.0));
		if (!occupied(tempState)) {
			predecessors.addFirst(tempState);
		}

		tempState = new State(state.getX(), state.getY() - 1, state.getZ(), new Pair<Double, Double>(-1.0, -1.0));
		if (!occupied(tempState)) {
			predecessors.addFirst(tempState);
		}

		tempState = new State(state.getX(), state.getY(), state.getZ() + 1, new Pair<Double, Double>(-1.0, -1.0));
		if (!occupied(tempState)) {
			predecessors.addFirst(tempState);
		}

		tempState = new State(state.getX(), state.getY(), state.getZ() - 1, new Pair<Double, Double>(-1.0, -1.0));
		if (!occupied(tempState)) {
			predecessors.addFirst(tempState);
		}

		return predecessors;
	}

	/*
	 * Update the position of the agent/robot. This does not force a replan.
	 */
	// TODO: Update to z
	public void updateStart(int x, int y, int z) {
		startState.setX(x);
		startState.setY(y);
		startState.setZ(z);

		k_m += heuristic(lastState, startState);

		startState = calculateKey(startState);
		lastState = startState;
	}

	/*
	 * This is somewhat of a hack, to change the position of the goal we first
	 * save all of the non-empty nodes on the map, clear the map, move the goal
	 * and add re-add all of the non-empty cells. Since most of these cells are
	 * not between the start and goal this does not seem to hurt performance too
	 * much. Also, it frees up a good deal of memory we are probably not going
	 * to use.
	 */
	// TODO: Update to z
	public void updateGoal(int x, int y, int z) {
		List<Pair<IPoint2, Double>> toAdd = new ArrayList<Pair<IPoint2, Double>>();
		Pair<IPoint2, Double> tempPoint;

		for (Map.Entry<State, CellInfo> entry : cellHash.entrySet()) {
			if (!isClose(entry.getValue().getCost(), DEFAULT_CELL_COST)) {
				tempPoint = new Pair<IPoint2, Double>(
						new IPoint2(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ()),
						entry.getValue().getCost());
				toAdd.add(tempPoint);
			}
		}

		cellHash.clear();
		openHash.clear();

		while (!blockedStates.isEmpty()) {
			blockedStates.poll();
		}

		k_m = 0;

		goalState.setX(x);
		goalState.setY(y);
		goalState.setZ(z);

		CellInfo tmp = new CellInfo();
		tmp.setRhs(0);
		tmp.setG(0);
		tmp.setCost(DEFAULT_CELL_COST);

		cellHash.put(goalState, tmp);

		tmp = new CellInfo();
		double totalPathCost = heuristic(startState, goalState);
		tmp.setRhs(totalPathCost);
		tmp.setG(totalPathCost);
		tmp.setCost(DEFAULT_CELL_COST);
		cellHash.put(startState, tmp);
		startState = calculateKey(startState);

		lastState = startState;

		Iterator<Pair<IPoint2, Double>> iterator = toAdd.iterator();
		while (iterator.hasNext()) {
			tempPoint = iterator.next();
			updateCell(tempPoint.first().getX(), tempPoint.first().getY(), tempPoint.first().getZ(),
					tempPoint.second());
		}
	}

	/*
	 * As per [S. Koenig, 2002]
	 */
	private void updateVertex(State state) {
		LinkedList<State> successors = new LinkedList<State>();

		if (state.neq(goalState)) {
			successors = getSuccessors(state);
			double tmp = Double.POSITIVE_INFINITY;
			double tmp2;

			for (State successor : successors) {
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
			addOpenCandidate(state); 
		}
	}

	/*
	 * Sets the G value for state u
	 */
	private void setG(State state, double g) {
		makeNewCell(state);
		cellHash.get(state).setG(g);
	}

	/*
	 * Sets the rhs value for state u
	 */
	private void setRHS(State state, double rhs) {
		makeNewCell(state);
		cellHash.get(state).setRhs(rhs);
	}

	/*
	 * Checks if a cell is in the hash table, if not it adds it in.
	 */
	private void makeNewCell(State state) {
		if (cellHash.get(state) != null) {
			return;
		}

		CellInfo tmp = new CellInfo();
		double costToGoal = heuristic(state, goalState);
		tmp.setRhs(costToGoal);
		tmp.setG(costToGoal);
		cellHash.put(state, tmp);
	}

	/*
	 * updateCell as per [S. Koenig, 2002]
	 */
	public void updateCell(int x, int y, int z, double cost) {
		State state = new State();
		state.setX(x);
		state.setY(y);
		state.setZ(z);

		if ((state.eq(startState)) || (state.eq(goalState))) {
			return;
		}

		makeNewCell(state);
		cellHash.get(state).setCost(cost);
		updateVertex(state);
	}

	/*
	 * Inserts state u into openList and openHash
	 */
	private void addOpenCandidate(State state) {
		float csum;
		state = calculateKey(state);
		csum = keyHashCode(state);

		/*
		 * Return if cell is already in list
		 * 
		 * This should be uncommented except it introduces a bug, I suspect that
		 * there is a bug somewhere else and having duplicates in the openList
		 * queue hides the problem...
		 */
		// if ((cur != openHash.end()) && (close(csum, cur->second))) return;

		openHash.put(state, csum);
		blockedStates.add(state);
	}

	/*
	 * Returns the key hash code for the state u, this is used to compare a
	 * state that has been updated
	 */
	private float keyHashCode(State state) {
		return (float) (state.getKey().first() + 1193 * state.getKey().second());
	}

	/*
	 * Returns true if the cell is occupied (non-traversable), false otherwise.
	 * Non-traversable are marked with a cost < 0
	 */
	private boolean occupied(State state) {
		// if the cellHash does not contain the State u
		if (cellHash.get(state) == null) {
			return false;
		}

		return (cellHash.get(state).getCost() < 0);
	}

	/*
	 * Euclidean cost between state a and state b
	 */
	private double trueDist(State a, State b) {
		float x = a.getX() - b.getX();
		float y = a.getY() - b.getY();
		float z = a.getZ() - b.getZ();
		return Math.sqrt(x * x + y * y + z * z);
	}

	/*
	 * Returns the cost of moving from state a to state b. This could be either
	 * the cost of moving off state a or onto state b, we went with the former.
	 * This is also the 8-way cost.
	 */
	private double calcCostToMove(State a, State b) {
		int xd = Math.abs(a.getX() - b.getX());
		int yd = Math.abs(a.getY() - b.getY());
		int zd = Math.abs(a.getZ() - b.getZ());
		double scale = 1;

		if (xd + yd + zd > 1) {
			scale = M_SQRT2;
		}

		if (cellHash.containsKey(a) == false) {
			return scale * DEFAULT_CELL_COST;
		}

		return scale * cellHash.get(a).getCost();
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

	public List<State> getPath() {
		return path;
	}

}
