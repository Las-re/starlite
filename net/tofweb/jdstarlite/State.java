package net.tofweb.jdstarlite;

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
public class State implements Comparable<State> {
	private int x = 0;
	private int y = 0;
	private int z = 0;
	private Pair<Double, Double> key = new Pair<Double, Double>(0.0, 0.0);

	public State() {

	}

	public State(int x, int y, Pair<Double, Double> k) {
		this.x = x;
		this.y = y;
		this.key = k;
	}
	
	public State(int x, int y, int z, Pair<Double, Double> k) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.key = k;
	}

	public State(State other) {
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

	public Pair<Double, Double> getKey() {
		return key;
	}

	public void setKey(Pair<Double, Double> key) {
		this.key = key;
	}

	// Equals
	public boolean eq(final State s2) {
		return ((this.x == s2.x) && (this.y == s2.y) && (this.z == s2.z));
	}

	// Not Equals
	public boolean neq(final State s2) {
		return ((this.x != s2.x) || (this.y != s2.y) || (this.z != s2.z));
	}

	// Less than
	public boolean lt(final State s2) {
		if (key.first() + 0.000001 < s2.key.first())
			return true;
		else if (key.first() - 0.000001 > s2.key.first())
			return false;
		return key.second() < s2.key.second();
	}

	/**
	 * Required for PriorityQueue
	 * 
	 * @param other
	 * @return
	 */
	@Override
	public int compareTo(State other) {
		if (key.first() - 0.00001 > other.key.first())
			return 1;
		else if (key.first() < other.key.first() - 0.00001)
			return -1;
		if (key.second() > other.key.second())
			return 1;
		else if (key.second() < other.key.second())
			return -1;
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
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
		State other = (State) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

}
