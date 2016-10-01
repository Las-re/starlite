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

		CellSpace space = new CellSpace(startCell, endCell);

		QueueBlockManager blockManager = new QueueBlockManager();

		Pathfinder pathfinder = new Pathfinder(space, blockManager);
		pathfinder.blockCell(new Cell(6, 6, 3));
		pathfinder.blockCell(new Cell(6, 5, 4));

		// set impassable nodes
		// CellSpace space = pathfinder.getSpace();
		// space.blockCell(new Cell(6, 6, 6));

		// space.blockCell(new Cell(2, 1, 1));
		// space.blockCell(new Cell(2, 2, 1));

		// space.blockCell(new Cell(11, 7, 7));
		// space.blockCell(new Cell(10, 8, 7));
		// space.blockCell(new Cell(9, 7, 7));
		// space.blockCell(new Cell(10, 6, 7));
		// space.blockCell(new Cell(10, 7, 8));
		// space.blockCell(new Cell(10, 7, 6));

		// perform the pathfinding
		assertTrue(pathfinder.findPath());

		// get and print the path
		List<Cell> path = pathfinder.getPath();
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
