# starlite
A 3D path-finding implementation in java, based on D* Lite

## D* Lite
D* Lite is a fast pathfinding algorithm designed by Sven Koenig and Maxim Likhachev, see their <a href="http://idm-lab.org/bib/abstracts/papers/aaai02b.pdf">white paper</a>.  It was implemented in java as a 2D solution by <a href="https://github.com/daniel-beard">Daniel Beard</a>.  

## Starlite
Starlite is a 3D implementation of Daniel Beard's 2D implementation.  

## Documentation
Javadoc comments are included in the source code

## Example
CellSpace space = new CellSpace();
space.setGoalCell(1, 1, -1);
space.setStartCell(10, 7, 7);

CostBlockManager blockManager = new CostBlockManager(space);
blockManager.blockCell(space.makeNewCell(6, 6, 3));
blockManager.blockCell(space.makeNewCell(6, 5, 4));

Pathfinder pathfinder = new Pathfinder(blockManager);

Path path = pathfinder.findPath();

## License
Daniel Beard included the MIT licence in his source code.  As part of the re-write, that license has been placed in a separate file at the root of this repository. 
