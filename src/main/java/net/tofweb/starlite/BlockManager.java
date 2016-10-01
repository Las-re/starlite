package net.tofweb.starlite;

public interface BlockManager {

	boolean isBlocked(Cell cell);

	void setSpace(CellSpace space);

	// void insertCell(Cell cell);

}
