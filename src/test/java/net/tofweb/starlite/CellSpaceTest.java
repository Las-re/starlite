package net.tofweb.starlite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

public class CellSpaceTest {

	CellSpace space;

	// Test values
	double costA = 1.0;
	double gA = 12.12435565298214;
	double rhsA = 12.12435565298214;
	double gB = 17.320508075688775;
	double gC = 8.774964387392123;
	Double costPlusHeuristicA = 26.302685732130897;
	double costB = 3.7416573867739413;
	double gD = 3.7416573867739413;
	double rhsD = 4.0;
	double gE = 3.7416573867739413;

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
		assertTrue(gA == space.getG(cell));

		// Null conditions
		assertTrue(0.0 == space.getG(null));
		assertTrue(0.0 == space.getG(new Cell()));

		Cell illegalCell = new Cell();
		illegalCell.setX(100);
		illegalCell.setY(100);
		illegalCell.setZ(100);
		assertNull(space.getInfo(illegalCell));
	}

	@Test
	public void testMakeNewCellIntIntInt() {
		Cell cell = space.makeNewCell(5, 4, 6);
		assertNotNull(cell);

		CellInfo info = space.getInfo(cell);
		assertNotNull(info);
		assertTrue(gC == info.getG());
		assertTrue(gC == info.getRhs());
		assertTrue(costA == info.getCost());
	}

	@Test
	public void testMakeNewCellIntIntIntCosts() {
		Costs k = new Costs(3.14, 21.0);
		Cell cell = space.makeNewCell(7, 8, 9, k);
		assertNotNull(cell);
		assertEquals(costPlusHeuristicA, cell.getKey().getCostPlusHeuristic());
		assertTrue(costB == cell.getKey().getCost());

		CellInfo info = space.getInfo(cell);
		assertNotNull(info);
		assertTrue(gD == info.getG());
		assertTrue(rhsD == info.getRhs());
		assertTrue(costA == info.getCost());
	}

	@Test
	public void testSetStartCell() {
		space.setStartCell(7, 8, 9);
		Cell startCell = space.getStartCell();
		assertNotNull(startCell);
		assertEquals(7, startCell.getX());
		assertEquals(8, startCell.getY());
		assertEquals(9, startCell.getZ());

		CellInfo info = space.getInfo(startCell);
		assertNotNull(info);
		assertTrue(gE == info.getG());
		assertTrue(gE == info.getRhs());
		assertTrue(costA == info.getCost());
	}

	@Test
	public void testSetGoalCell() {
		space.setGoalCell(10, 11, 12);
		Cell goalCell = space.getGoalCell();
		assertNotNull(goalCell);
		assertEquals(10, goalCell.getX());
		assertEquals(11, goalCell.getY());
		assertEquals(12, goalCell.getZ());

		CellInfo info = space.getInfo(goalCell);
		assertNotNull(info);
		assertTrue(0.0 == info.getG());
		assertTrue(0.0 == info.getRhs());
		assertTrue(costA == info.getCost());
	}

	@Test
	public void testIsClose() {
		assertTrue(space.isClose(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
		assertFalse(space.isClose(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
		assertFalse(space.isClose(1.0, 2.0));
		assertTrue(space.isClose(1.0, 1.000009));
		assertFalse(space.isClose(1.0, 1.00001));
	}

	@Test
	public void testGetSuccessors() {
		Cell cell = space.makeNewCell(20, 20, 20);
		LinkedList<Cell> neighbors = space.getPredecessors(cell);
		assertNotNull(neighbors);
		assertTrue(6 == neighbors.size());
		assertEquals(20, neighbors.getFirst().getX());
		assertEquals(20, neighbors.getFirst().getY());
		assertEquals(19, neighbors.getFirst().getZ());
		assertEquals(21, neighbors.getLast().getX());
		assertEquals(20, neighbors.getLast().getY());
		assertEquals(20, neighbors.getLast().getZ());
	}

	@Test
	public void testGetPredecessors() {
		Cell cell = space.makeNewCell(20, 20, 20);
		LinkedList<Cell> neighbors = space.getPredecessors(cell);
		assertNotNull(neighbors);
		assertTrue(6 == neighbors.size());
		assertEquals(20, neighbors.getFirst().getX());
		assertEquals(20, neighbors.getFirst().getY());
		assertEquals(19, neighbors.getFirst().getZ());
		assertEquals(21, neighbors.getLast().getX());
		assertEquals(20, neighbors.getLast().getY());
		assertEquals(20, neighbors.getLast().getZ());
	}

}
