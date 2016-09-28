package net.tofweb.starlite;

public class Geometry {

	/*
	 * Pretty self explanatory, the heuristic we use is the 8-way distance
	 * scaled by a constant DEFAULT_CELL_COST (should be set to <= min cost)
	 */
	public static double heuristic(Cell a, Cell b) {
		return euclideanDistance(a, b) * Cell.DEFAULT_COST;
	}

	public static double euclideanDistance(Cell aState, Cell bState) {
		float x = aState.getX() - bState.getX();
		float y = aState.getY() - bState.getY();
		float z = aState.getZ() - bState.getZ();
		return Math.sqrt(x * x + y * y + z * z);
	}

	public static double calcCostToMove(Cell a, Cell b) {
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
}
