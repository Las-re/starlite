package net.tofweb.starlite;

public class QueueBlockManager implements BlockManager {

	// private PriorityQueue<Cell> blockedCells = new PriorityQueue<Cell>();
	// private HashMap<Cell, Float> openHash = new HashMap<Cell, Float>();
	private CellSpace space;

	public boolean isBlocked(Cell state) {
		CellInfo info = space.getInfo(state);
		if (info == null) {
			return false;
		}

		return (info.getCost() < 0);
	}

	public CellSpace getSpace() {
		return space;
	}

	public void setSpace(CellSpace space) {
		this.space = space;
	}

}
