package WidlerSuite;

import java.util.*;
/*
    An implementation of shadowcasting FoV for hex modes. Checks if the center of a tile has LoS to six points on the
    target tile.
    
    Basic procedure:
        The cell's neighbors are added to the process list (checking if in bounds, in range and not already added for each)
        If a cell is in shadow, it is skipped
        The cell is marked as visible
        If the cell is not transparent, it registers a shadow.
*/
public class ShadowFoVHex implements WSConstants
{                   
    private boolean[][] transparencyMap;    // which tiles are visible
    private int[][] visibilityMap;          // which tiles can be seen from most recent calcFoV()
    private int[][] addedMap;               // which tiles have been added to processList
    private int width;
    private int height;
    private int flag;                       // used to avoid unnecessary reassignment to refresh visibilityMap
    private Vector<Coord> processList;      // processing is essentially a queue, rather than recursive (FIFO)
    private Vector<Shadow> shadowList;
    private int listIndex;                  // avoiding the cost of removing items from the front of the vector
    private double[][] CORNER_LIST =  {{-.5, -.5}, {.5, -.5}, {-.5, .5}, {.5, .5}};  // for calculating shadow angles
    private double[][] TILE_CHECK_LIST  = {{-.25, -.5}, {.25, -.5}, {-.25, .5}, {.25, .5}, {-.5, 0}, {.5, 0}};  // for calculating if a tile is shadowed
    
    // constructor
    public ShadowFoVHex(boolean[][] transMap)
    {
        reset(transMap);
    }
    
    public void reset(boolean[][] transMap)
    {
        transparencyMap = transMap; // note: shallow copy
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
    
    // Calculate lit squares from a given location and radius
    public void calcFoV(int xLoc, int yLoc, int radius)
    {
        flag += 1;
        if(flag == Integer.MAX_VALUE)
        {
            reset(transparencyMap);
        }
            
        shadowList = new Vector<Shadow>();
        processList = new Vector<Coord>();
        listIndex = 0;
        int maxDistMetric = radius * radius;
        Coord origin = new Coord(xLoc, yLoc);
        processOrigin(origin, maxDistMetric);
        if(processList.size() > 0)
        {
            while(processList.size() > listIndex)
            {
                processCell(origin, processList.elementAt(listIndex), maxDistMetric);
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
    private void processCell(Coord origin, Coord target, int maxDistMetric)
    {
        // add neighbors
        queueNeighbors(origin, target, maxDistMetric);
        
        // if in shadow, return
        if(isShadowed(origin, target))
            return;
        
        // mark cell as visible
        visibilityMap[target.x][target.y] = flag;
        
        // if cell blocks LoS, cast a shadow
        if(transparencyMap[target.x][target.y] == false)
            shadowList.add(new Shadow(origin, target));
    }
    
    // checks five points (diamond plus center) against the shadow list. 
    private boolean isShadowed(Coord origin, Coord target)
    {
        if(shadowList.size() == 0)
            return false;
            
        double x = MathTools.getHexX(target) - MathTools.getHexX(origin);
        double y = target.y - origin.y;
        double[] angleList = new double[TILE_CHECK_LIST.length];
        for(int i = 0; i < TILE_CHECK_LIST.length; i++)
        {
            angleList[i] = MathTools.getAngle(x + TILE_CHECK_LIST[i][0], y + TILE_CHECK_LIST[i][1]);
        }
        
        int blockedPoints = 0;
        for(double theta : angleList)
        {
            for(Shadow shadow : shadowList)
            {
                if(shadow.shades(theta, origin, target))
                {
                    blockedPoints++;
                    break;
                }
            }
        }
        if(blockedPoints == angleList.length)
            return true;
        return false;
    }
    
    // the origin is always visible and does not cast shadows
    private void processOrigin(Coord origin, int maxDistMetric)
    {
        // mark cell as visible
        visibilityMap[origin.x][origin.y] = flag;
        addedMap[origin.x][origin.y] = flag;
        queueNeighbors(origin, origin, maxDistMetric);
    }
    
    // add neighbors if they are not yet added, in range, and in bounds
    private void queueNeighbors(Coord origin, Coord curCell, int maxDistMetric)
    {
        int[][] hexArr = HEX_EVEN_ROW;
        if(curCell.y % 2 == 1)
            hexArr = HEX_ODD_ROW;
        for(int[] hex : hexArr)
        {
            Coord c = new Coord(curCell.x + hex[0], curCell.y + hex[1]);
            if(isInBounds(c) &&
               maxDistMetric >= MathTools.getDistanceMetric(origin, c) &&
               addedMap[c.x][c.y] != flag)
            {
                queueCell(c);
            }
        }
    }

    
    // returns the angle to the center of the tile
    private double getAngle(Coord origin, Coord target)
    {
        double x = MathTools.getHexX(target) - MathTools.getHexX(origin);
        double y = target.y - origin.y;
        return MathTools.getAngle(x, y);
    }
    
    // private class for storing shadows
    private class Shadow
    {
        public double lower;
        public double upper;
        
        public Shadow(Coord origin, Coord target)
        {
            setUpperAndLower(origin, target);
        }
        
        // returns true if an angle lies within the shadow
        public boolean shades(double angle, Coord origin, Coord target)
        {            
            double subangle = angle - MathTools.FULL_CIRCLE;
            return (upper >= angle && lower <= angle) || (upper >= subangle && lower <= subangle);
        }
    
        // finds the two highest and lowest angled points
        private void setUpperAndLower(Coord origin, Coord target)
        {
            double x = MathTools.getHexX(target) - MathTools.getHexX(origin);
            double y = target.y - origin.y;
            double big;
            double little;
            
            // generate list of angles to the four corners
            double[] cornerAngle = new double[4];
            for(int i = 0; i < 4; i++)
            {
                cornerAngle[i] = MathTools.getAngle(x + CORNER_LIST[i][0], y + CORNER_LIST[i][1]);
            }
            
            // adjust for anything that straddles zero radians
            little = Math.min(Math.min(cornerAngle[0], cornerAngle[1]), Math.min(cornerAngle[2], cornerAngle[3]));
            for(int i = 0; i < 4; i++)
            {
                // if an angle is more than pi radians different from the smallest, make it negative
                if(Math.abs(cornerAngle[i] - little) > MathTools.HALF_CIRCLE)
                {
                    cornerAngle[i] -= MathTools.FULL_CIRCLE;
                }
            }
            
            // set
            this.lower = Math.min(Math.min(cornerAngle[0], cornerAngle[1]), Math.min(cornerAngle[2], cornerAngle[3]));
            this.upper = Math.max(Math.max(cornerAngle[0], cornerAngle[1]), Math.max(cornerAngle[2], cornerAngle[3]));
        }
    }

}