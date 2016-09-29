package net.tofweb.starlite;

public interface BlockManager {

	boolean isBlocked(Cell cell);

	void insertCell(Cell cell);

}
