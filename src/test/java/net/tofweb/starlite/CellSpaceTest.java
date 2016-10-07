package net.tofweb.starlite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class CellSpaceTest {

	CellSpace space;

	// Test values
	Double costA = 1.0;
	Double gA = 12.12435565298214;
	Double rhsA = 12.12435565298214;

	Double gB = 17.320508075688775;

	@Before
	public void setup() {
		space = new CellSpace();
		space.setGoalCell(10, 10, 10);
		space.setStartCell(-5, -5, -5);
	}

	@Test
	public void testGetInfo() {
		// Happy path
		Cell cell = space.makeNewCell(3, 3, 3);
		CellInfo returnedInfo = space.getInfo(cell);
		assertNotNull(returnedInfo);
		assertTrue(costA == returnedInfo.getCost());
		assertTrue(gA == returnedInfo.getG());
		assertTrue(rhsA == returnedInfo.getRhs());

		// Null condition
		assertNull(space.getInfo(null));

		/*
		 * Illegal argument case - CellSpace managed cells should be made with
		 * makeCell
		 */
		Cell illegalCell = new Cell();
		assertNull(space.getInfo(illegalCell));

		illegalCell.setX(100);
		illegalCell.setY(100);
		illegalCell.setZ(100);
		assertNull(space.getInfo(illegalCell));
	}

	@Test
	public void testUpdateCellCost() {
		Cell cell = space.makeNewCell(3, 3, 3);
		CellInfo returnedInfo = space.getInfo(cell);

		// Existing state, cost = 1
		assertNotNull(returnedInfo);
		assertTrue(1 == returnedInfo.getCost());

		// Happy path, set cost = 2
		space.updateCellCost(cell, 2);
		returnedInfo = space.getInfo(cell);
		assertNotNull(returnedInfo);
		assertTrue(2 == returnedInfo.getCost());

		// Null condition A
		space.updateCellCost(null, 3);
		returnedInfo = space.getInfo(cell);
		assertNotNull(returnedInfo);
		assertTrue(2 == returnedInfo.getCost());
	}

	@Test
	public void testGetG() {
		Cell cell = space.makeNewCell(3, 3, 3);

		// Existing state
		assertEquals(gA, space.getG(cell));

		// Null conditions
		assertNull(space.getG(null));
		assertNull(space.getG(new Cell()));

		Cell illegalCell = new Cell();
		illegalCell.setX(100);
		illegalCell.setY(100);
		illegalCell.setZ(100);
		assertNull(space.getInfo(illegalCell));
	}

	@Test
	public void testMakeNewCellIntIntInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testMakeNewCellIntIntIntCosts() {
		fail("Not yet implemented");
	}

	@Test
	public void testMakeNewCellCell() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetStartCell() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetStartCell() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetGoalCell() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetGoalCell() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsClose() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSuccessors() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPredecessors() {
		fail("Not yet implemented");
	}

}
