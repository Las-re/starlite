package net.tofweb.starlite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class Integration {

	@Test
	public void test() {
		DStarLite dsl = new DStarLite();

		// set start and goal nodes
		dsl.init(10, 7, 7, 1, 1, -1);

		// set impassable nodes
		dsl.updateCell(6, 6, 6, -1);
		dsl.updateCell(2, 1, 1, -1);
		dsl.updateCell(2, 2, 1, -1);

		// perform the pathfinding
		dsl.pathfind();

		// get and print the path
		List<State> path = dsl.getPath();
		for (State state : path) {
			System.out.println("x: " + state.getX() + " y: " + state.getY() + " z: " + state.getZ());
		}

		assertNotNull(path);

		Integer size = path.size();
		assertTrue(24 == size);

		State end = path.get(--size);
		assertEquals(1, end.getX());
		assertEquals(1, end.getY());
		assertEquals(-1, end.getZ());
	}

}
