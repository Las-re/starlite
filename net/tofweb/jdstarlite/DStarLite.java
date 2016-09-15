package net.tofweb.jdstarlite;

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
    private State goalState  = new State();
    private State lastState  = new State();
    private int maxSteps = 80000;
    private PriorityQueue<State> openCandidateQueue = new PriorityQueue<State>(); //  
    private HashMap<State, CellInfo> cellHash = new HashMap<State, CellInfo>();
    private HashMap<State, Float> openHash = new HashMap<State, Float>();

    /**
     * Initialize the pathfinder
     * 
     * @param sX starting x coord
     * @param sY starting y coord
     * @param gX goal x coord
     * @param gY goal y coord
     */
    public void init(int sX, int sY, int gX, int gY)
    {
        cellHash.clear();
        path.clear();
        openHash.clear();
        
		while (!openCandidateQueue.isEmpty()) {
			openCandidateQueue.poll();
		}

        k_m = 0;

        startState.setX(sX);
        startState.setY(sY); 
        goalState.setX(gX); 
        goalState.setY(gY);

        CellInfo goalCell = new CellInfo();
        cellHash.put(goalState, goalCell); 

        CellInfo startCell = new CellInfo();  
        double totalPathCost = heuristic(startState,goalState);
        startCell.setRhs(totalPathCost); 
        startCell.setG(totalPathCost); 
        cellHash.put(startState, startCell);
        
        // TODO: Uncertain what this does
        startState = calculateKey(startState);  

        lastState = startState;
    }

    /*
     * CalculateKey
     * As per [S. Koenig, 2002]
     * 
     * Key of a node is a value that is going to be used to sort the open list by
     * 
     * Key is a tuple value = [min(g(x), rhs(x)+h(x)); min(g(x), rhs(s)]
     * These keys are compared lexiographically so ...
     * 
     * u < v if
     * u.first < v.first OR ( u.first == v.first AND u.second < v.second ) 
     */
    private State calculateKey(State state)
    {
        double val = Math.min(getRHS(state), getG(state));

        Pair<Double, Double> key = state.getKey();
        key.setFirst (val + heuristic(state, startState) + k_m);  
        key.setSecond(val);  
 
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
		if (cellHash.get(u) == null) {
			return heuristic(u, goalState);
		}

		return cellHash.get(u).getG();
	}

    /*
     * Pretty self explanatory, the heuristic we use is the 8-way distance
     * scaled by a constant DEFAULT_CELL_COST (should be set to <= min cost)
     */
    private double heuristic(State a, State b)
    {
        return eightCondist(a,b) * DEFAULT_CELL_COST;
    }

    /*
     * Returns the 8-way distance between state a and state b
     */
	private double eightCondist(State aState, State bState) {
		double temp;
		double min = Math.abs(aState.getX() - bState.getX());
		double max = Math.abs(aState.getY() - bState.getY());

		if (min > max) {
			temp = min;
			min = max;
			max = temp;
		}

		return ((M_SQRT2 - 1.0) * min + max);
	}

    /**
     * Find a path to the goal
     * 
     * Returns true if a path is found, false otherwise
     * 
     * @return
     */
    public boolean pathfind()
    {
        path.clear();

        boolean canPath = computeShortestPath();
		if (!canPath) {
			return false;
		}

        LinkedList<State> successorStates = new LinkedList<State>();
        State currentState = startState;

        if (getG(startState) == Double.POSITIVE_INFINITY)
        {
            // We can't even get home
            return false;
        }

        while (currentState.neq(goalState))
        {
            path.add(currentState);
            successorStates = new LinkedList<State>();
            successorStates = getSuccessors(currentState);

            if (successorStates.isEmpty())
            {
                // Hit a dead end
                return false;
            }

            double cmin = Double.POSITIVE_INFINITY;
            double tmin = 0;   
            State smin = new State();

            for (State successorState : successorStates)
            {
                double val  = calcCostToMove(currentState, successorState);
                double val2 = trueDist(successorState,goalState) + trueDist(startState, successorState);
                val += getG(successorState);

                if (close(val,cmin)) {
                    if (tmin > val2) {
                        tmin = val2;
                        cmin = val;
                        smin = successorState;
                    }
                } else if (val < cmin) {
                    tmin = val2;
                    cmin = val;
                    smin = successorState;
                }
            }
            successorStates.clear();
            currentState = new State(smin);
            //cur = smin;
        }
        path.add(goalState);
        
        return true;
    }

    /*
     * As per [S. Koenig,2002] except for two main modifications:
     * 
     * 1.  We stop planning after a number of steps, 'maxsteps' we do this
     *    because this algorithm can plan forever if the start is surrounded  by obstacles
     * 2.  We lazily remove states from the open list so we never have to iterate through it.
     * 
     * Returns false if there is no path to goal
     */
    private boolean computeShortestPath()
    {
        LinkedList<State> states = new LinkedList<State>();

        if (openCandidateQueue.isEmpty()) {
        	// We have no candidates to pursue
        	return false; 
        }

        int numSteps=0;
        startState = calculateKey(startState);
        
		while ((!openCandidateQueue.isEmpty()) && (openCandidateQueue.peek().lt(startState)) || (getRHS(startState) != getG(startState))) {
            // increment step iterator and compare... if it is greater than max then abort
			if (numSteps++ > maxSteps) {
				throw new RuntimeException("Maximum number of iterations hit: " + maxSteps);
			}

            State currentState = null;

            // path is sane if the RHS(start) is NOT the G(start)
            boolean pathIsSane = (getRHS(startState) != getG(startState)); // Determining if we got into this loop by sanity check failing

            // lazy remove
            while(true) {
                if (openCandidateQueue.isEmpty()) {
                	// We have no candidates remaining to pursue
                	return false;
                }
                              
                // Retrieve and remove the best candidate from the queue
                currentState = openCandidateQueue.poll();
                
                if (!(currentState.lt(startState)) && (!pathIsSane)) {
                	// We've gone backwards or something and the path is corrupt
                	// FAIL
                	return false; 
                }

                if (!isValid(currentState)) {
                	continue;  
                }
               
                break;
            }

            openHash.remove(currentState);

            State previousState = new State(currentState);
            currentState = calculateKey(currentState);

            if (previousState.lt(currentState)) { 
            	// We need to consider heading back to where we came from
                addOpenCandidate(currentState);
            } else if (getG(currentState) > getRHS(currentState)) { 
            	// We've gotten closer than we expected
                setG(currentState, getRHS(currentState));
                states = getPredecessors(currentState);
                
                for (State state : states) {
                    updateVertex(state);
                }
            } else {                         
            	// g <= rhs, state has got worse
                setG(currentState, Double.POSITIVE_INFINITY);
                states = getPredecessors(currentState);

                for (State state : states) {
                    updateVertex(state);
                }
                
                updateVertex(currentState);
            }
        } 
		
        return true;
    }

    /*
     * Returns a list of successor states for state u, since this is an
     * 8-way graph this list contains all of a cells neighbours. Unless
     * the cell is occupied, in which case it has no successors.
     * 
     * TODO: 14 way in 3d?  
     */
    private LinkedList<State> getSuccessors(State state)
    {
        LinkedList<State> successors = new LinkedList<State>();
        State tempState;

        if (occupied(state)) {
        	// We cannot move into this cell
        	// Therefore it has no successor states
        	return successors;
        }

        //Generate the successors, starting at the immediate right,
        //Moving in a clockwise manner
        tempState = new State(state.getX() + 1, state.getY(), new Pair(-1.0,-1.0));
        successors.addFirst(tempState);
        tempState = new State(state.getX() + 1, state.getY() + 1, new Pair(-1.0,-1.0));
        successors.addFirst(tempState);
        tempState = new State(state.getX(), state.getY() + 1, new Pair(-1.0,-1.0));
        successors.addFirst(tempState);
        tempState = new State(state.getX() - 1, state.getY() + 1, new Pair(-1.0,-1.0));
        successors.addFirst(tempState);
        tempState = new State(state.getX() - 1, state.getY(), new Pair(-1.0,-1.0));
        successors.addFirst(tempState);
        tempState = new State(state.getX() - 1, state.getY() - 1, new Pair(-1.0,-1.0));
        successors.addFirst(tempState);
        tempState = new State(state.getX(), state.getY() - 1, new Pair(-1.0,-1.0));
        successors.addFirst(tempState);
        tempState = new State(state.getX() + 1, state.getY() - 1, new Pair(-1.0,-1.0));
        successors.addFirst(tempState);

        return successors;
    }

    /*
     * Returns a list of all the predecessor states for state u. Since
     * this is for an 8-way connected graph, the list contains all the
     * neighbours for state u. Occupied neighbours are not added to the list
     */
    private LinkedList<State> getPredecessors(State u)
    {
        LinkedList<State> predecessors = new LinkedList<State>();
        State tempState;

        tempState = new State(u.getX() + 1, u.getY(), new Pair(-1.0,-1.0));
        if (!occupied(tempState)) predecessors.addFirst(tempState);
        tempState = new State(u.getX() + 1, u.getY() + 1, new Pair(-1.0,-1.0));
        if (!occupied(tempState)) predecessors.addFirst(tempState);
        tempState = new State(u.getX(), u.getY() + 1, new Pair(-1.0,-1.0));
        if (!occupied(tempState)) predecessors.addFirst(tempState);
        tempState = new State(u.getX() - 1, u.getY() + 1, new Pair(-1.0,-1.0));
        if (!occupied(tempState)) predecessors.addFirst(tempState);
        tempState = new State(u.getX() - 1, u.getY(), new Pair(-1.0,-1.0));
        if (!occupied(tempState)) predecessors.addFirst(tempState);
        tempState = new State(u.getX() - 1, u.getY() - 1, new Pair(-1.0,-1.0));
        if (!occupied(tempState)) predecessors.addFirst(tempState);
        tempState = new State(u.getX(), u.getY() - 1, new Pair(-1.0,-1.0));
        if (!occupied(tempState)) predecessors.addFirst(tempState);
        tempState = new State(u.getX() + 1, u.getY() - 1, new Pair(-1.0,-1.0));
        if (!occupied(tempState)) predecessors.addFirst(tempState);

        return predecessors;
    }


    /*
     * Update the position of the agent/robot.
     * This does not force a replan.
     */
    public void updateStart(int x, int y)
    {
        startState.setX(x);
        startState.setY(y);

        k_m += heuristic(lastState,startState);

        startState = calculateKey(startState);
        lastState = startState;

    }

    /*
     * This is somewhat of a hack, to change the position of the goal we
     * first save all of the non-empty nodes on the map, clear the map, move the
     * goal and add re-add all of the non-empty cells. Since most of these cells
     * are not between the start and goal this does not seem to hurt performance
     * too much. Also, it frees up a good deal of memory we are probably not
     * going to use.
     */
    public void updateGoal(int x, int y)
    {
        List<Pair<IPoint2, Double> > toAdd = new ArrayList<Pair<IPoint2, Double> >();
        Pair<IPoint2, Double> tempPoint;

        for (Map.Entry<State,CellInfo> entry : cellHash.entrySet()) {
            if (!close(entry.getValue().getCost(), DEFAULT_CELL_COST)) {
                tempPoint = new Pair(
                            new IPoint2(entry.getKey().getX(), entry.getKey().getY()),
                            entry.getValue().getCost());
                toAdd.add(tempPoint);
            }
        }

        cellHash.clear();
        openHash.clear();

        while(!openCandidateQueue.isEmpty())
            openCandidateQueue.poll();

        k_m = 0;

        goalState.setX(x);
        goalState.setY(y);

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

        Iterator<Pair<IPoint2,Double> > iterator = toAdd.iterator();
        while(iterator.hasNext()) {
            tempPoint = iterator.next();
            updateCell(tempPoint.first().getX(), tempPoint.first().getY(), tempPoint.second());
        }


    }

    /*
     * As per [S. Koenig, 2002]
     */
    private void updateVertex(State u)
    {
        LinkedList<State> s = new LinkedList<State>();

        if (u.neq(goalState)) {
            s = getSuccessors(u);
            double tmp = Double.POSITIVE_INFINITY;
            double tmp2;

            for (State i : s) {
                tmp2 = getG(i) + calcCostToMove(u,i);
                if (tmp2 < tmp) tmp = tmp2;
            }
            if (!close(getRHS(u),tmp)) setRHS(u,tmp);
        }

        if (!close(getG(u),getRHS(u))) addOpenCandidate(u);
    }

    /*
     * Returns true if state u is on the open list or not by checking if
     * it is in the hash table.
     */
    private boolean isValid(State u)
    {
        if (openHash.get(u) == null) return false;
        if (!close(keyHashCode(u),openHash.get(u))) return false;
        return true;
    }

    /*
     * Sets the G value for state u
     */
    private void setG(State u, double g)
    {
        makeNewCell(u);
        cellHash.get(u).setG(g);
    }

    /*
     * Sets the rhs value for state u
     */
    private void setRHS(State u, double rhs)
    {
        makeNewCell(u);
        cellHash.get(u).setRhs(rhs);
    }

    /*
     * Checks if a cell is in the hash table, if not it adds it in.
     */
    private void makeNewCell(State u)
    {
        if (cellHash.get(u) != null) return;
        CellInfo tmp = new CellInfo();
        double costToGoal = heuristic(u,goalState);
        tmp.setRhs(costToGoal);
        tmp.setG(costToGoal);
        cellHash.put(u, tmp);
    }

    /*
     * updateCell as per [S. Koenig, 2002]
     */
    public void updateCell(int x, int y, double val)
    {
        State u = new State();
        u.setX(x);
        u.setY(y);

        if ((u.eq(startState)) || (u.eq(goalState))) return;

        makeNewCell(u);
        cellHash.get(u).setCost(val);
        updateVertex(u);
    }

    /*
     * Inserts state u into openList and openHash
     */
    private void addOpenCandidate(State u)
    {
        //iterator cur
        float csum;

        u = calculateKey(u);
        //cur = openHash.find(u);
        csum = keyHashCode(u);

        // return if cell is already in list. TODO: this should be
        // uncommented except it introduces a bug, I suspect that there is a
        // bug somewhere else and having duplicates in the openList queue
        // hides the problem...
        //if ((cur != openHash.end()) && (close(csum,cur->second))) return;

        openHash.put(u, csum);
        openCandidateQueue.add(u);
    }

    /*
     * Returns the key hash code for the state u, this is used to compare
     * a state that has been updated
     */
    private float keyHashCode(State u)
    {
        return (float)(u.getKey().first() + 1193*u.getKey().second());
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
    private double trueDist(State a, State b)
    {
        float x = a.getX()-b.getX();
        float y = a.getY()-b.getY();
        return Math.sqrt(x*x + y*y);
    }

    /*
     * Returns the cost of moving from state a to state b. This could be
     * either the cost of moving off state a or onto state b, we went with the
     * former. This is also the 8-way cost.
     */
    private double calcCostToMove(State a, State b)
    {
        int xd = Math.abs(a.getX() - b.getX());
        int yd = Math.abs(a.getY() - b.getY());
        double scale = 1;

        if (xd + yd > 1) {
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
    private boolean close(double x, double y)
    {
        if (x == Double.POSITIVE_INFINITY && y == Double.POSITIVE_INFINITY) return true;
        return (Math.abs(x-y) < 0.00001);
    }

    public List<State> getPath()
    {
        return path;
    }

}

