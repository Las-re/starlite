package net.tofweb.starlite;

/**
 * A basic concrete extension of BlockManager.
 *
 */
public class CostBlockManager extends BlockManager {

	/**
	 * Return a CostBlockManager which manages the specified CellSpace.
	 * 
	 * @param space
	 */
	public CostBlockManager(CellSpace space) {
		super(space);
	}

	/**
	 * Mark the specified Cell as impassable.
	 * 
	 * @param blockedCell
	 */
	public void blockCell(Cell blockedCell) {
		CellSpace space = super.getSpace();

		if ((blockedCell.equals(space.getStartCell())) || (blockedCell.equals(space.getGoalCell()))) {
			return;
		}

		space.makeNewCell(blockedCell);
		space.updateCellCost(blockedCell, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.tofweb.starlite.BlockManager#isBlocked(net.tofweb.starlite.Cell)
	 */
	public boolean isBlocked(Cell cell) {
		CellSpace space = super.getSpace();
		CellInfo info = space.getInfo(cell);

		if (info == null) {
			return false;
		}

		return (info.getCost() < 0);
	}

}
