package net.tofweb.starlite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class Integration {

	@Test
	public void test() {

		// set start and goal nodes
		Cell startCell = new Cell(10, 7, 7);
		Cell endCell = new Cell(1, 1, -1);
		Pathfinder dsl = new Pathfinder(startCell, endCell);

		// set impassable nodes
		dsl.blockCell(new Cell(6, 6, 6));
		dsl.blockCell(new Cell(2, 1, 1));
		dsl.blockCell(new Cell(2, 2, 1));

		// perform the pathfinding
		dsl.pathfind();

		// get and print the path
		List<Cell> path = dsl.getPath();
		for (Cell state : path) {
			System.out.println("x: " + state.getX() + " y: " + state.getY() + " z: " + state.getZ());
		}

		assertNotNull(path);

		Integer size = path.size();
		assertTrue(24 == size);

		Cell end = path.get(--size);
		assertEquals(1, end.getX());
		assertEquals(1, end.getY());
		assertEquals(-1, end.getZ());
	}

}
