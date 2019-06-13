/*******************************************************************************************
  
A portable A* pathing class.
Accepts an array of boolean values, or difficulty mulitipliers for the map.
Internally keeps decimal data as int types, to help with speed
   
Procedure:
   OL = open list, CL = closed list, H = estimated distance to target,
   G = distance traveled so far
      
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
Returns a Vector of Coords; either from the origin to the path, or empty if no path
was found.
    
Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.
*******************************************************************************************/

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
    
   public static int MAX_LOOPS = 5000;
   public static double HEURISTIC_MULTIPLER = 11.0;
    
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
    
   // Primary function. Attempts to make a path.
   public Vector<Coord> path(boolean[][] pm, Coord start, Coord end)
   {
      setMap(pm);
      mainLoop(start, end);
      return getPath(start, end);
   }
   public Vector<Coord> path(boolean[][] pm, int startX, int startY, int endX, int endY){return path(pm, new Coord(startX, startY), new Coord(endX, endY));}
    
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
   public Vector<Coord> getPath(int originX, int originY, int terminusX, int terminusY)
   {
      return getPath(new Coord(originX, originY), new Coord(terminusX, terminusY));
   }
    
   // main work loop. See description at beginning of document.
   private void mainLoop(Coord origin, Coord terminus)
   {
      openList = new AStarOpenList(origin, getDistHeur(origin, terminus));
      int loops = 0;
      iteration += 1;
      int[][] adjTiles;
      while(openList.pathExists(terminus) == false && openList.size() > 0 && loops < MAX_LOOPS)
      {
         // pop the list
         AStarNode curNode = openList.pop();
            
         // get list of adjacent tile directions
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
               // did we find the end?
               if(curLoc.equals(terminus))
               {
                  openList.pushToFront(new AStarNode(curLoc, curNode, 0, locInfo[2]));
               }
               // else is this already on the openlist?
               else if(openList.contains(curLoc))
               {
                  openList.update(curLoc, curNode, locInfo[2]);
               }
               // final else
               else
               {
                  openList.push(new AStarNode(curLoc, curNode, getDistHeur(curLoc, terminus), locInfo[2]));
               }
               // mark as closed
               closedMap[curLoc.x][curLoc.y] = iteration;
               loops += 1;
            }
         }
      }
   }
}