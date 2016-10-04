package net.tofweb.starlite;

public abstract class BlockManager {

	protected CellSpace space;

	public BlockManager(CellSpace space) {
		super();
		this.space = space;
	}

	public abstract boolean isBlocked(Cell cell);

	public CellSpace getSpace() {
		return space;
	}

}
