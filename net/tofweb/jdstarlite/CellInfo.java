package net.tofweb.jdstarlite;

/*
 * @author Lynn Owens
 * https://github.com/LynnOwens
 */
public class CellInfo {
	private double g;
	private double rhs;
	private double cost;
	
	public CellInfo() {
		super();
		this.cost = DStarLite.DEFAULT_CELL_COST;
	}

	/**
	 * Cost thus far from the start to current node
	 * 
	 * @return
	 */
	public double getG() {
		return g;
	}

	public void setG(double g) {
		this.g = g;
	}

	/**
	 * Right Hand Side, heuristic.  g + estimated cost to move to this cell
	 * 
	 * @return
	 */
	public double getRhs() {
		return rhs;
	}

	public void setRhs(double rhs) {
		this.rhs = rhs;
	}

	/**
	 * The cost of the cell
	 * 
	 * @return
	 */
	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "CellInfo [g=" + g + ", rhs=" + rhs + ", cost=" + cost + "]";
	}

}
