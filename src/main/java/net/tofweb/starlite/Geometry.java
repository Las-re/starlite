package net.tofweb.starlite;

/**
 * A utility which does the basic Euclidean calculation of distance between two
 * 3D points.
 *
 */
public class Geometry {

	/**
	 * Get the Euclidean distance between the two specified Cells.
	 * 
	 * @param aState
	 * @param bState
	 * @return
	 */
	public static double euclideanDistance(Cell aState, Cell bState) {
		float x = aState.getX() - bState.getX();
		float y = aState.getY() - bState.getY();
		float z = aState.getZ() - bState.getZ();
		return Math.sqrt(x * x + y * y + z * z);
	}

}
