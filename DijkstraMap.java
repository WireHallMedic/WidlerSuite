/***************************************************************************
//
// An implementation of the idea found at http://www.roguebasin.com/index.php?title=The_Incredible_Power_of_Dijkstra_Maps
// Takes a boolean array to initially create the map, and goals are set with Coords
//
//
***************************************************************************/

package WidlerSuite;


import java.util.*;

public class DijkstraMap implements WSConstants
{
	private boolean[][] passMap;
	private int[][] map;
	private Vector<Coord> goalList;
    private int mode;
    private boolean searchDiagonal = false;


	public boolean[][] getPassMap(){return passMap;}
	public int[][] getMap(){return map;}
	public Vector<Coord> getGoalList(){return goalList;}


	public void setPassMap(boolean[][] p){passMap = p;}
	public void setMap(int[][] m){map = m;}
	public void setGoalList(Vector<Coord> g){goalList = g;}
    public void setMode(int m){mode = m;}
    public void setSearchDiagonal(boolean d){searchDiagonal = d;}
   
   
   public DijkstraMap(boolean[][] pm)
   {
      passMap = new boolean[pm.length][pm[0].length];
      for(int x = 0; x < passMap.length; x++)
      for(int y = 0; y < passMap[0].length; y++)
         passMap[x][y] = pm[x][y];
         
      goalList = new Vector<Coord>();
      mode = RECT_MODE;
   }
   
   public void addGoal(Coord c){addGoal(c.x, c.y);}
   public void addGoal(int x, int y)
   {
      goalList.add(new Coord(x, y));
   }
   
   public void clearGoalList()
   {
      goalList = new Vector<Coord>();
   }
   
   public int getValue(Coord c){return getValue(c.x, c.y);}
   public int getValue(int x, int y)
   {
        if(isInBounds(x, y))
            return map[x][y];
        return 10000;
   }
   
   public Vector<Coord> getLowestAdjacent(Coord c){return getLowestAdjacent(c.x, c.y);}
   public Vector<Coord> getLowestAdjacent(int xLoc, int yLoc)
   {
      int bestVal = 10001;
      Vector<Coord> list = new Vector<Coord>();

      Coord[] adjList = getAdjCells(new Coord(xLoc, yLoc));
      for(Coord adj : adjList)
      {
        if(bestVal > getValue(adj.x, adj.y))
            bestVal = getValue(adj.x, adj.y);
      }
      
      if(bestVal >= 10001)
      {
         list.add(new Coord(xLoc, yLoc));
      }
      else
      {
         for(Coord adj : adjList)
         {
            if(bestVal == getValue(adj.x, adj.y))
            {
               list.add(new Coord(adj.x, adj.y));
            }
         }
      }
      return list;
   }
   
   public Vector<Coord> getHighestAdjacent(Coord c){return getHighestAdjacent(c.x, c.y);}
   public Vector<Coord> getHighestAdjacent(int xLoc, int yLoc)
   {
      int bestVal = -1;
      Vector<Coord> list = new Vector<Coord>();
      
      Coord[] adjList = getAdjCells(new Coord(xLoc, yLoc));
      for(Coord adj : adjList)
      {
        if(getValue(adj.x, adj.y) < 10000)  // ignore walls
        if(bestVal < getValue(adj.x, adj.y))
            bestVal = getValue(adj.x, adj.y);
      }
      
      if(bestVal == -1)
      {
         list.add(new Coord(xLoc, yLoc));
      }
      else
      {
         for(Coord adj : adjList)
         {
            if(bestVal == getValue(adj.x, adj.y))
            {
               list.add(new Coord(adj.x, adj.y));
            }
         }
      }
      return list;
   }
   
   public void resetMap()
   {
      map = new int[passMap.length][passMap[0].length];
      for(int x = 0; x < map.length; x++)
      for(int y = 0; y < map[0].length; y++)
      {
         map[x][y] = 10000;
      }
   }
   
   public void process()
   {
      resetMap();
      partiallyProcess(0, 0, map.length - 1, map[0].length - 1, true);
   }
   
   
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
   
   // process each orthogonally adjacent cell
   public boolean processCell(int cellX, int cellY)
   {
      boolean changeWasMade = false;
      Coord[] adjList = getAdjCells(cellX, cellY);
      int[] stepDist = getStepDist(cellX, cellY);
      Coord adj;
      for(int i = 0; i < adjList.length; i++)
      {
        adj = adjList[i];
        if(isInBounds(adj))
            if(map[cellX][cellY] > map[adj.x][adj.y] + stepDist[i])
            {
                map[cellX][cellY] = map[adj.x][adj.y] + stepDist[i];
                changeWasMade = true;
            }
      }
      return changeWasMade;
   }
   
   private Coord[] getAdjCells(Coord c){return getAdjCells(c.x, c.y);}
   private Coord[] getAdjCells(int x, int y)
   {
        int[][] stepList = null;
        if(mode == HEX_MODE)
        {
            if(y % 2 == 0)
                stepList = hexEvenRow;
            else
                stepList = hexOddRow;
        }
        else
        {
            if(searchDiagonal)
                stepList = rectDiag;
            else
                stepList = rectOrtho;
        }
        Coord[] adjList = new Coord[stepList.length];
        for(int i = 0; i < adjList.length; i++)
        {
            adjList[i] = new Coord(x + stepList[i][0], y + stepList[i][1]);
        }
        return adjList;
   }
   
   
   private int[] getStepDist(Coord c){return getStepDist(c.x, c.y);}
   private int[] getStepDist(int x, int y)
   {
        int[][] stepList = null;
        if(mode == HEX_MODE)
        {
            if(y % 2 == 0)
                stepList = hexEvenRow;
            else
                stepList = hexOddRow;
        }
        else
        {
            if(searchDiagonal)
                stepList = rectDiag;
            else
                stepList = rectOrtho;
        }
        int[] distList = new int[stepList.length];
        for(int i = 0; i < distList.length; i++)
        {
            distList[i] = stepList[i][2];
        }
        return distList;
   }
   
   private boolean isInBounds(Coord c){return isInBounds(c.x, c.y);}
   private boolean isInBounds(int x, int y)
   {
        return x >= 0 && y >= 0 && x < passMap.length && y < passMap[0].length;
   }
   
   
   ///////////////////////////////////////
   
   
   public static void main(String args[])
   {
      boolean[][] boolMap = new boolean[10][10];
      for(int x = 0; x < boolMap.length; x++)
      for(int y = 0; y < boolMap[0].length; y++)
         boolMap[x][y] = true;
      
      boolMap[1][3] = false;
      boolMap[2][6] = false;
      boolMap[4][5] = false;
      boolMap[5][5] = false;
      boolMap[6][5] = false;
      boolMap[8][7] = false;
      
      DijkstraMap dm = new DijkstraMap(boolMap);
      dm.addGoal(5, 4);
      dm.addGoal(9, 9);
      dm.addGoal(0, 4);
      
      dm.partiallyProcess(1, 0, 102, 8);
    //  dm.process();
      
      for(int y = 0; y < boolMap[0].length; y++)
      {
         for(int x = 0; x < boolMap.length; x++)
         {
            if(dm.getValue(x, y) == 1000)
               System.out.print("#");
            else if(dm.getValue(x, y) == 0)
               System.out.print("!");
            else
               System.out.print("" + dm.getValue(x, y));
         }
         System.out.println("");
      }
      
      System.out.println("Highest adjacent to 3, 3: " + dm.getHighestAdjacent(3, 3).toString());
      System.out.println("Lowest adjacent to 3, 3: " + dm.getLowestAdjacent(3, 3).toString());
   }
}