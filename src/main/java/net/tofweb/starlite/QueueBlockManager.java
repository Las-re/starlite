package net.tofweb.starlite;

public class QueueBlockManager extends BlockManager {

	public QueueBlockManager(CellSpace space) {
		super(space);
	}

	public boolean isBlocked(Cell state) {
		CellSpace space = super.getSpace();
		CellInfo info = space.getInfo(state);
		if (info == null) {
			return false;
		}

		return (info.getCost() < 0);
	}

	/**
	 * Used to be updateCell, called from Main
	 * 
	 * @param blockedCell
	 */
	public void blockCell(Cell blockedCell) {
		CellSpace space = super.getSpace();

		if ((blockedCell.equals(space.getStartCell())) || (blockedCell.equals(space.getGoalCell()))) {
			return;
		}

		double cost = -1;
		space.makeNewCell(blockedCell);
		space.updateCellCost(blockedCell, cost);
	}

}
