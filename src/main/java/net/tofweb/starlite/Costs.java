package net.tofweb.starlite;

public class Costs {

	private Double costPlusHeuristic;
	private Double cost;

	public Costs(Double object1, Double object2) {
		this.costPlusHeuristic = object1;
		this.cost = object2;
	}

	public Double getCostPlusHeuristic() {
		return costPlusHeuristic;
	}

	public Double getCost() {
		return cost;
	}

	public void setCostPlusHeuristic(Double object1) {
		this.costPlusHeuristic = object1;
	}

	public void setCost(Double object2) {
		this.cost = object2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((costPlusHeuristic == null) ? 0 : costPlusHeuristic.hashCode());
		result = prime * result + ((cost == null) ? 0 : cost.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Costs other = (Costs) obj;
		if (costPlusHeuristic == null) {
			if (other.costPlusHeuristic != null)
				return false;
		} else if (!costPlusHeuristic.equals(other.costPlusHeuristic))
			return false;
		if (cost == null) {
			if (other.cost != null)
				return false;
		} else if (!cost.equals(other.cost))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Costs [costPlusHeuristic=" + costPlusHeuristic + ", cost=" + cost + "]";
	}

}
