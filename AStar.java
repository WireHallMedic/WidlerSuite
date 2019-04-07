/*****************************************************************
//
//	A* Pathing Class
//	A portable A* class.  Requires Coord.
//	Accepts an array of boolean values, or difficulty mulitipliers 
//		for the map.
//	Internally keeps decimal data as int types, to help with speed
//	Once generated, cannot be modified
//
//	Attempts to increase speed of AStarPath by only creating nodes 
//	for cells which are used.
//
*****************************************************************/

/*
OL = open list, CL = closed list, H = estimated distance to target,
G = distance traveled so far
	
Procedure:
	1) Add origin node to OL
	2) Node P = the node which has the lowest H+G on the OL.
	3) For each passable cell adjacent to P that is not on the CL,
		if new cell == target, end.
		newG = P.G + entry cost of this node.
		If not on OL: Add to OL.  G = newG
		If on OL: if G > newG, G = newG, parent = P
		else do nothing
   4) Add P to CL.  Go to step 2.

Once you end, then follow the parents back for the target node to find the path.


*/
package WidlerSuite;

import java.util.Vector;


public class AStar implements WSConstants
{
    private boolean[][] passMap;
    private int[][] closedMap;
    private AStarOpenList openList;
    private int iteration;
    private int width;
    private int height;
    private int mode = RECT_MODE;
    private boolean searchDiagonal = true;
    
    public static final int MAX_LOOPS = 5000;
    public static final double HEURISTIC_MULTIPLER = 11.0;
    
    public void setMode(int m){mode = m;}
    public void setSearchDiagonal(boolean sd){searchDiagonal = sd;}
    
    // constructor
    public AStar()
    {
        passMap = new boolean[0][0];
        closedMap = new int[0][0];
        openList = new AStarOpenList();
        iteration = 0;
        width = 0;
        height = 0;
    }
    
    public Vector<Coord> path(boolean[][] pm, Coord start, Coord end)
    {
        setMap(pm);
        mainLoop(start, end);
        return getPath(start, end);
    }
    
    // makes a deep copy of a boolean map, and sets internal values accordingly
    public void setMap(boolean[][] pm)
    {
        if(width != pm.length || height != pm[0].length)
        {
            width = pm.length;
            height = pm[0].length;
            closedMap = new int[width][height];
            iteration = 0;
        }
        passMap = new boolean[width][height];
        for(int x = 0; x < width; x++)
        for(int y = 0; y < height; y++)
            passMap[x][y] = pm[x][y];
    }
    
    // Returns the distance heuristic. This is the primary tuning point; path optimization is improved by decreasing the multiplier,
    // but this increases the number of cycles needed.
    private static int getDistHeur(Coord origin, Coord terminus)
    {
        int x = origin.x - terminus.x;
        int y = origin.y - terminus.y;
        return (int)(Math.sqrt((x * x) + (y * y)) * HEURISTIC_MULTIPLER);
    }
    
    // check to stay in bounds
    private boolean isInBounds(Coord c)
    {
        return c.x >= 0 && c.y >= 0 && c.x < width && c.y < height;
    }
    
    // traces the path, then returns it
    public Vector<Coord> getPath(Coord origin, Coord terminus)
    {
        Vector<Coord> pathToOrigin = new Vector<Coord>();
        Vector<Coord> pathToTerminus = new Vector<Coord>();
        // run the thing
        if(openList.pathExists(terminus))
        {
            AStarNode curNode = openList.peek();
            pathToOrigin.add(curNode.getLoc());
            while(curNode.getLoc().equals(origin) == false)
            {
                curNode = curNode.getParentNode();
                pathToOrigin.add(curNode.getLoc());
            }
            for(int i = pathToOrigin.size() - 2; i >= 0; i -= 1)
            {
                pathToTerminus.add(pathToOrigin.elementAt(i));
            }
        }
        return pathToTerminus;
    }
    
    public Vector<Coord> findPath(int originX, int originY, int terminusX, int terminusY)
    {
        return getPath(new Coord(originX, originY), new Coord(terminusX, terminusY));
    }
    
    private void mainLoop(Coord origin, Coord terminus)
    {
        openList = new AStarOpenList(origin, getDistHeur(origin, terminus));
        int loops = 0;
        iteration += 1;
        int[][] adjTiles;
        while(openList.pathExists(terminus) == false && openList.size() > 0 && loops < MAX_LOOPS)
        {
            AStarNode curNode = openList.pop();
            if(mode == RECT_MODE)
            {
                if(searchDiagonal)
                    adjTiles = RECT_DIAG;
                else
                    adjTiles = RECT_ORTHO;
            }
            else
            {
                if(curNode.getLoc().y % 2 == 0)
                    adjTiles = HEX_EVEN_ROW;
                else 
                    adjTiles = HEX_ODD_ROW;
            }
            for(int[] locInfo : adjTiles)
            {
                Coord curLoc = new Coord(curNode.getLoc().x + locInfo[0], curNode.getLoc().y + locInfo[1]);
                if(isInBounds(curLoc) && closedMap[curLoc.x][curLoc.y] != iteration && passMap[curLoc.x][curLoc.y])
                {
                    if(curLoc.equals(terminus))
                    {
                        openList.pushToFront(new AStarNode(curLoc, curNode, 0, locInfo[2]));
                    }
                    else if(openList.contains(curLoc))
                    {
                        openList.update(curLoc, curNode, locInfo[2]);
                    }
                    else
                    {
                        openList.push(new AStarNode(curLoc, curNode, getDistHeur(curLoc, terminus), locInfo[2]));
                    }
                    closedMap[curLoc.x][curLoc.y] = iteration;
                    loops += 1;
                }
            }
        }
    }
    
    
    // test function
    public static void main(String[] args)
    {
        boolean[][] testMap = new boolean[40][10];
        String[][] outMap = new String[40][10];
        for(int x = 0; x < 40; x++)
        for(int y = 0; y < 10; y++)
        {
            testMap[x][y] = true;
            outMap[x][y] = ".";
        }
        for(int y = 3; y < 8; y++)
        {
            testMap[20][y] = false;
            outMap[20][y] = "#";
        }
        outMap[0][5] = "@";
        outMap[39][5] = ">";
        AStar aStar = new AStar();
        Vector<Coord> path = aStar.path(testMap, new Coord(0, 5), new Coord(39, 5));
        for(Coord loc : path)
        {
            outMap[loc.x][loc.y] = "X";
        }
        for(int y = 0; y < 10; y++)
        {
            for(int x = 0; x < 40; x++)
            {
                System.out.print(outMap[x][y]);
            }
            System.out.println();
        }
    }

}