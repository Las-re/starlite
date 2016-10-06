package net.tofweb.starlite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Integration {

	@Test
	public void test() {

		CellSpace space = new CellSpace();

		// Cell goalCell = space.makeNewCell(1, 1, -1);
		space.setGoalCell(1, 1, -1);
		space.setStartCell(10, 7, 7);

		CostBlockManager blockManager = new CostBlockManager(space);
		blockManager.blockCell(space.makeNewCell(6, 6, 3));
		blockManager.blockCell(space.makeNewCell(6, 5, 4));

		// The following traps the pathfinder
		// blockManager.blockCell(space.makeNewCell(11, 7, 7));
		// blockManager.blockCell(space.makeNewCell(10, 8, 7));
		// blockManager.blockCell(space.makeNewCell(9, 7, 7));
		// blockManager.blockCell(space.makeNewCell(10, 6, 7));
		// blockManager.blockCell(space.makeNewCell(10, 7, 8));
		// blockManager.blockCell(space.makeNewCell(10, 7, 6));

		Pathfinder pathfinder = new Pathfinder(blockManager);

		// get and print the path
		Path path = pathfinder.findPath();

		assertTrue(path.isComplete());
		assertNotNull(path);

		Integer size = path.size();
		assertTrue(24 == size);

		Cell end = path.get(--size);
		assertEquals(1, end.getX());
		assertEquals(1, end.getY());
		assertEquals(-1, end.getZ());
	}

}
