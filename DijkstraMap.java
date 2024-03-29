/***************************************************************************

An implementation of the idea found at:
http:  www.roguebasin.com/index.php?title=The_Incredible_Power_of_Dijkstra_Maps
Takes a boolean array to initially create the map, and goals are set with Coords.
There can be multiple goals.

Note that distances are essentially kept stored at *10 in order to use ints; that
is, an orthogonal (or hexagonal) step is 10, and a diagonal step is 14.

OUT_OF_BOUNDS is the value used initially (as it's arbitrarily large enough to be higher than
any calculated value), as well as the value for out of range requests.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

***************************************************************************/

package WidlerSuite;


import java.util.*;

public class DijkstraMap implements WSConstants
{
   protected boolean[][] passMap;
   protected int[][] map;
   protected Vector<Coord> goalList;
   protected int mode;
   protected boolean searchDiagonal = false;
   protected static final int OUT_OF_BOUNDS = 1000000;
   protected static final int OUT_OF_BOUNDS_PLUS_ONE = OUT_OF_BOUNDS + 1;
   
   public boolean[][] getPassMap(){return passMap;}
   public int[][] getMap(){return map;}
   public Vector<Coord> getGoalList(){return goalList;}
   public boolean getSearchDiagonal(){return searchDiagonal;}
   
   public void setPassMap(boolean[][] p){passMap = p;}
   public void setGoalList(Vector<Coord> g){goalList = g;}
   public void setMode(int m){mode = m;}
   public void setSearchDiagonal(boolean d){searchDiagonal = d;}
   
   // standard constructor
   public DijkstraMap(boolean[][] pm)
   {
      passMap = new boolean[pm.length][pm[0].length];
      for(int x = 0; x < passMap.length; x++)
      for(int y = 0; y < passMap[0].length; y++)
         passMap[x][y] = pm[x][y];
   
      goalList = new Vector<Coord>();
      mode = RECT_MODE;
   }
   
   // adds a goal to the list
   public void addGoal(Coord c){addGoal(c.x, c.y);}
   public void addGoal(int x, int y)
   {
      goalList.add(new Coord(x, y));
   }
   
   // resets the goal list
   public void clearGoalList()
   {
      goalList = new Vector<Coord>();
   }
   
   // returns the value of a passed cell
   public int getValue(Coord c){return getValue(c.x, c.y);}
   public int getValue(int x, int y)
   {
      if(isInBounds(x, y))
         return map[x][y];
      return OUT_OF_BOUNDS;
   }
   
   // returns the value of a passed cell, expressed in number of steps
   public int getStepValue(Coord c){return getStepValue(c.x, c.y);}
   public int getStepValue(int x, int y)
   {
      return (getValue(x, y) + 5) / 10;
   }
   
   // returns the location of the lowest adjacent cell (or all equally low adjacent cells)
   public Vector<Coord> getLowestAdjacent(Coord c){return getLowestAdjacent(c.x, c.y);}
   public Vector<Coord> getLowestAdjacent(int xLoc, int yLoc)
   {
      int bestVal = OUT_OF_BOUNDS_PLUS_ONE;
      Vector<Coord> list = new Vector<Coord>();
      Vector<Coord> locList = new Vector<Coord>();
      Vector<Integer> valueList = new Vector<Integer>();
      // generate adjacent cells and their values
      for(Coord adj : getAdjCells(xLoc, yLoc))
      {
         locList.add(adj);
         valueList.add(getValue(adj.x, adj.y));
      }
      
      // find best value
      for(int value : valueList)
      {
         if(value < bestVal)
            bestVal = value;
      }
      
      // find all tiles matching best value
      for(int i = 0; i < locList.size(); i++)
      {
         if(valueList.elementAt(i) == bestVal)
            list.add(locList.elementAt(i));
      }
      return list;
   }
   
   // returns the location of the highest adjacent cell (or all equally high cells)
   public Vector<Coord> getHighestAdjacent(Coord c){return getHighestAdjacent(c.x, c.y);}
   public Vector<Coord> getHighestAdjacent(int xLoc, int yLoc)
   {
      int bestVal = -1;
      Vector<Coord> list = new Vector<Coord>();
      Vector<Coord> locList = new Vector<Coord>();
      Vector<Integer> valueList = new Vector<Integer>();
      // generate adjacent cells and their values
      for(Coord adj : getAdjCells(xLoc, yLoc))
      {
         locList.add(adj);
         valueList.add(getValue(adj.x, adj.y));
      }
      
      // find best value
      for(int value : valueList)
      {
         if(value > bestVal && value != OUT_OF_BOUNDS)
            bestVal = value;
      }
      
      // find all tiles matching best value
      for(int i = 0; i < locList.size(); i++)
      {
         if(valueList.elementAt(i) == bestVal)
            list.add(locList.elementAt(i));
      }
      return list;
   }
   
   // readies for another search on the same map
   public void resetMap()
   {
      map = new int[passMap.length][passMap[0].length];
      for(int x = 0; x < map.length; x++)
      for(int y = 0; y < map[0].length; y++)
      {
         map[x][y] = OUT_OF_BOUNDS;
      }
   }
   
   // process the entire map
   public void process()
   {
      resetMap();
      partiallyProcess(0, 0, map.length - 1, map[0].length - 1, true);
   }
   
   // processes just a rectangular portion of the map (which can be all of it)
   public void partiallyProcess(int startX, int startY, int endX, int endY)
   {partiallyProcess(startX, startY, endX, endY, false);}
   public void partiallyProcess(int startX, int startY, int endX, int endY, boolean mapIsSet)
   {
      if(!mapIsSet)
         resetMap();
   
      // set floors and ceilings
      startX = Math.max(0, startX);
      startY = Math.max(0, startY);
      endX = Math.min(map.length, endX);
      endY = Math.min(map[0].length, endY);
   
      // set the goals
      for(Coord goal : goalList)
      {
         if(isInBounds(goal))
         {
            map[goal.x][goal.y] = 0;
         }
      }
   
      // process the map until no changes are made
      boolean changeWasMade = false;
      do
      {
         changeWasMade = false;
         for(int x = startX; x < endX; x++)
         for(int y = startY; y < endY; y++)
         {
            if(passMap[x][y] == false)
               continue;
   
            if(processCell(x, y))
               changeWasMade = true;
         }
      }
      while(changeWasMade);
   }
   
   // process each adjacent (defined by mode and processDiagonal) cell
   protected boolean processCell(int cellX, int cellY)
   {
      boolean changeWasMade = false;
      int[][] adjList = getAdjSteps(cellX, cellY);
      Coord adj;
      for(int i = 0; i < adjList.length; i++)
      {
         adj = new Coord(adjList[i][0] + cellX, adjList[i][1] + cellY);
         if(isInBounds(adj))
         if(map[cellX][cellY] > map[adj.x][adj.y] + adjList[i][2])
         {
            map[cellX][cellY] = map[adj.x][adj.y] + adjList[i][2];
            changeWasMade = true;
         }
      }
      return changeWasMade;
   }
   
   // returns a list of steps adjacent to this one, based on mode and possibly processDiagonal
   protected int[][] getAdjSteps(Coord c){return getAdjSteps(c.x, c.y);}
   protected int[][] getAdjSteps(int x, int y)
   {
      int[][] stepList = null;
      if(mode == HEX_MODE)
      {
         if(y % 2 == 0)
            stepList = HEX_EVEN_ROW;
         else
            stepList = HEX_ODD_ROW;
      }
      else
      {
         if(searchDiagonal)
            stepList = RECT_DIAG;
         else
            stepList = RECT_ORTHO;
      }
      return stepList;
   }
   
   // returns a list of cells adjacent to this one, based on mode and possibly processDiagonal
   protected Vector<Coord> getAdjCells(Coord c){return getAdjCells(c.x, c.y);}
   protected Vector<Coord> getAdjCells(int x, int y)
   {
      int[][] stepList = getAdjSteps(x, y);
      Vector<Coord> adjList = new Vector<Coord>();
      for(int[] step : stepList)
      {
         adjList.add(new Coord(step[0] + x, step[1] + y));
      }
      return adjList;
   }
   
   // makes sure the passed index is within the boundaries
   protected boolean isInBounds(Coord c){return isInBounds(c.x, c.y);}
   protected boolean isInBounds(int x, int y)
   {
      return x >= 0 && y >= 0 && x < passMap.length && y < passMap[0].length;
   }
}