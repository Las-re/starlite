package net.tofweb.starlite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class GeometryTest {

	@Test
	public void testEuclideanDistance() {
		// Math.sqrt(x * x + y * y + z * z);

		Cell cellA = makeSpacelessCell(1, 1, 1);
		Cell cellB = makeSpacelessCell(1, 1, 2);
		assertEquals(Double.valueOf(1), Geometry.euclideanDistance(cellA, cellB));

		cellB = makeSpacelessCell(3, 4, 5);
		// -2 * -2 + -3 * -3 + -4 * -4
		// 4 + 9 + 16 = 29 ... sqrt = 5.385164807134504
		assertEquals(Double.valueOf(5.385164807134504), Geometry.euclideanDistance(cellA, cellB));

		assertNull(null, Geometry.euclideanDistance(null, cellB));
		assertNull(null, Geometry.euclideanDistance(cellA, null));
		assertNull(null, Geometry.euclideanDistance(null, null));
	}

	private Cell makeSpacelessCell(int x, int y, int z) {
		Cell cell = new Cell();
		cell.setX(x);
		cell.setY(y);
		cell.setZ(z);

		return cell;
	}
}
