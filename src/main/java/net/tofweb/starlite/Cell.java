package net.tofweb.starlite;

/*
 * @author daniel beard
 * http://danielbeard.wordpress.com
 * http://github.com/paintstripper
 *
 * Copyright (C) 2012 Daniel Beard
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Lynn Owens
 * https://github.com/LynnOwens
 */
public class Cell implements Comparable<Cell> {

	public static final Double BILLIONTH = 0.000001;
	public static final double DEFAULT_COST = 1.0;

	private int x = 0;
	private int y = 0;
	private int z = 0;
	private Costs key = new Costs(0.0, 0.0);

	public Cell() {
		super();
	}

	// public Cell(int x, int y, int z) {
	// super();
	// this.x = x;
	// this.y = y;
	// this.z = z;
	// }
	//
	// public Cell(int x, int y, int z, Costs k) {
	// this.x = x;
	// this.y = y;
	// this.z = z;
	// this.key = k;
	// }

	public Cell(Cell other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
		this.key = other.key;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public Costs getKey() {
		return key;
	}

	public void setKey(Costs key) {
		this.key = key;
	}

	/*
	 * Is this Cell closer than the parameter Cell?
	 */
	public boolean isCloser(Cell otherCell) {
		if (key.getCostPlusHeuristic() + BILLIONTH < otherCell.getKey().getCostPlusHeuristic())
			return true;
		else if (key.getCostPlusHeuristic() - BILLIONTH > otherCell.getKey().getCostPlusHeuristic())
			return false;
		return key.getCost() < otherCell.getKey().getCost();
	}

	/**
	 * Required for PriorityQueue
	 * 
	 * @param o
	 * @return
	 */
	public int compareTo(Cell o) {
		if (key.getCostPlusHeuristic() - BILLIONTH > o.key.getCostPlusHeuristic())
			return 1;
		else if (key.getCostPlusHeuristic() < o.key.getCostPlusHeuristic() - BILLIONTH)
			return -1;
		if (key.getCost() > o.key.getCost())
			return 1;
		else if (key.getCost() < o.key.getCost())
			return -1;
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
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
		Cell other = (Cell) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Cell [x=" + x + ", y=" + y + ", z=" + z + ", key=" + key + "]";
	}

}
