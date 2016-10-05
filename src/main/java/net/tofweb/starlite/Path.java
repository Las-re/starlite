package net.tofweb.starlite;

import java.util.LinkedList;

public class Path extends LinkedList<Cell> {

	private static final long serialVersionUID = -5572661613938583005L;
	private boolean isComplete = false;

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

}
