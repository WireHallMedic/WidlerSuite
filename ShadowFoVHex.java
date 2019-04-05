package WidlerSuite;

import java.util.*;
/*
    An implementation of shadowcasting FoV for hex modes. Angles are stored at +2pi to avoid straddling 0.0
    
    Basic procedure:
        If a cell is in shadow, it is skipped
        The cell is marked as visible
        If the cell is not transparent, it registers a shadow.
        The cell's neighbors are added to the process list (checking if in bounds, in range and not already added for each)
*/
public class ShadowFoVHex implements WSConstants
{                   
    private boolean[][] transparencyMap;    // which tiles are visible
    private int[][] visibilityMap;          // which tiles can be seen from most recent calcFoV()
    private int[][] addedMap;               // which tiles have been added to processList
    private int width;
    private int height;
    private int flag;                       // used to avoid unnecessary reassignment to refresh visibilityMap
    private Vector<Coord> processList;      // processing needs to be stack-based, rather than recursive
    private Vector<Shadow> shadowList;
    private static double tileRadius = .5;
    private int listIndex;                  // avoid the cost of removing items from the front of the vector
    
    public static void setTileRadius(double tr){tileRadius = tr;}
    
    // constructor
    public ShadowFoVHex(boolean[][] transMap)
    {
        reset(transMap);
    }
    
    public void reset(boolean[][] transMap)
    {
        transparencyMap = transMap; // shallow copy
        width = transparencyMap.length;
        height = transparencyMap[0].length;
        visibilityMap = new int[width][height];
        addedMap = new int[width][height];
        flag = 1;
        listIndex = 0;
    }
    
    // checks if a location is in the map bounds
    public boolean isInBounds(int x, int y)
    {
        return x >= 0 && y >= 0 && x < width && y < height;
    }
    public boolean isInBounds(Coord c){return isInBounds(c.x, c.y);}
    
    // checks if a square blocks LoS
    public boolean isBlocked(int x, int y)
    {
        if(isInBounds(x, y))
            return !transparencyMap[x][y];
        return false;
    }
    
    // checks if a square is visible
    public boolean isVisible(int x, int y)
    {
        return isInBounds(x, y) && visibilityMap[x][y] == flag;
    }
    
    
    public void calcFoV(int xLoc, int yLoc, int radius)
    // Calculate lit squares from a given location and radius
    {
        flag += 1;
        if(flag == Integer.MAX_VALUE)
        {
            reset(transparencyMap);
        }
            
        shadowList = new Vector<Shadow>();
        processList = new Vector<Coord>();
        listIndex = 1;
        int maxDistMetric = radius * radius;
        Coord observer = new Coord(xLoc, yLoc);
        processOrigin(observer, maxDistMetric);
        if(processList.size() > 0)
        {
            while(processList.size() >= listIndex)
            {
                processCell(observer, processList.elementAt(listIndex - 1), maxDistMetric);
                listIndex += 1;
            }
        }
    }
    
    // add cell to list to be processed, mark map
    private void queueCell(Coord cell)
    {
        processList.add(cell);
        addedMap[cell.x][cell.y] = flag;
    }
    
    
    // main process for each cell
    private void processCell(Coord observer, Coord target, int maxDistMetric)
    {
        // if in shadow, return
        for(Shadow shadow : shadowList)
        {
            double angleTo = getAngle(observer, target);
            if(shadow.shades(angleTo, observer, target))
                return;
        }
        
        // mark cell as visible
        visibilityMap[target.x][target.y] = flag;
        
        // if cell blocks LoS, cast a shadow
        if(transparencyMap[target.x][target.y] == false)
            shadowList.add(new Shadow(observer, target));
        
        // add neighbors
        processNeighbors(observer, target, maxDistMetric);
    }
    
    // the origin is always visible and does not cast shadows
    private void processOrigin(Coord observer, int maxDistMetric)
    {
        // mark cell as visible
        visibilityMap[observer.x][observer.y] = flag;
        addedMap[observer.x][observer.y] = flag;
        
        // add neighbors
        processNeighbors(observer, observer, maxDistMetric);
    }
    
    // add neighbors if they are not yet added, in range, and in bounds
    private void processNeighbors(Coord observer, Coord curCell, int maxDistMetric)
    {
        int[][] hexArr = HEX_EVEN_ROW;
        if(curCell.y % 2 == 1)
            hexArr = HEX_ODD_ROW;
        for(int i = 0; i < 6; i++)
        {
            Coord c = new Coord(curCell.x + hexArr[i][0], curCell.y + hexArr[i][1]);
            if(isInBounds(c) &&
               maxDistMetric >= MathTools.getDistanceMetric(observer, c) &&
               addedMap[c.x][c.y] != flag)
            {
                queueCell(c);
            }
        }
    }
    
    public boolean canSee(int x, int y)
    {
        if(isInBounds(x, y))
            return flag == visibilityMap[x][y];
        return false;
    }
    
    public boolean[][] getArray(int startX, int startY, int w, int h)
    {
        boolean[][] visArr = new boolean[w][h];
        for(int x = 0; x < w; x++)
        for(int y = 0; y < h; y++)
        {
            visArr[x][y] = canSee(startX + x, startY + y);
        }
        return visArr;
    }
    
    // returns the angle to the center of the tile
    private double getAngle(Coord origin, Coord target)
    {
        double x = MathTools.getHexX(target) - MathTools.getHexX(origin);
        double y = target.y - origin.y;
        return MathTools.getAngle(x, y) + MathTools.FULL_CIRCLE;
    }
    
    // private class for storing shadows. Angles are stored at +2pi to avoid straddling 0.0
    private class Shadow
    {
        public double lower;
        public double upper;
        
        public Shadow(Coord observer, Coord target)
        {
            double x = MathTools.getHexX(target) - MathTools.getHexX(observer);
            double y = target.y - observer.y;
            double theta = getAngle(observer, target);
            double sweep = getSweep(x, y);
            upper = theta + sweep;
            lower = theta - sweep;
        }
        
        // check if tile is shaded by checking center and two sides
        public boolean shades(double angle, Coord observer, Coord target)
        {            
            double x = MathTools.getHexX(target) - MathTools.getHexX(observer);
            double y = target.y - observer.y;
            double sweep = getSweep(x, y) / 2;
            return //shadowCheck(angle) &&
                   shadowCheck(angle + sweep) &&
                   shadowCheck(angle - sweep);
        }
        
        private boolean shadowCheck(double angle)
        {
            if(upper >= angle && lower <= angle)
                return true;
            return false;
        }
        
        private double getSweep(double x, double y)
        {
            double h = Math.sqrt((x * x) + (y * y));
            return Math.atan(tileRadius / h);
        }
    }

}