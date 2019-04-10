package WidlerSuite;

/*
    An implementaiton of a shadow casting FoV algorithym. This is my translation of someone else's Python implementation.
    Made to be persistent; just call calcFoV() with a new origin, or after updating the transparency map.
*/
public class ShadowFoV
{
    private boolean[][] transparencyMap;
    private int[][] visibilityMap;
    private int width;
    private int height;
    private int flag;   // used to avoid unnecessary reassignment to refresh visibilityMap
    
    // multipliers for transforming octants
    private static int[][] multipliers = {{1,  0,  0, -1, -1,  0,  0,  1},
                                          {0,  1, -1,  0,  0, -1,  1,  0},
                                          {0,  1,  1,  0,  0, -1, -1,  0},
                                          {1,  0,  0,  1, -1,  0,  0, -1}};
    
    // constructor
    public ShadowFoV(boolean[][] transMap)
    {
        transparencyMap = transMap; // shallow copy
        width = transparencyMap.length;
        height = transparencyMap[0].length;
        visibilityMap = new int[width][height];
        flag = 0;
    }
    
    // checks if a location is in the map bounds
    public boolean isInBounds(int x, int y)
    {
        return x >= 0 && y >= 0 && x < width && y < height;
    }
    
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
    
    // sets a square as visible using the current flag
    public void setVisible(int x, int y)
    {
        if(isInBounds(x, y))
            visibilityMap[x][y] = flag;
    }
    
    private void castLight(int cx, int cy,                       // starting coordinates
                           int row,                              // row number
                           double start, double end, int radius, // terminal slopes, and max light radius
                           int xx, int xy, int yx, int yy)       // multipliers for octant
    {
        if(start < end)
            return;
        
        int RADIUS_SQUARED = radius * radius;
        
        // main loop; iterates out from observer
        for(int j = row; j < radius + 1; j += 1)
        {
            int dx = -j - 1;
            int dy = -j;
            boolean blocked = false;
            double newStart = 0.0;
            
            while(dx <= 0)
            {
                dx += 1;
                
                // translate the dx, dy coordinates into map coordinates
                int x = cx + dx * xx + dy * xy;
                int y = cy + dx * yx + dy * yy;
                
                // calculate the left and right slopes of the square under consideration
                double leftSlope = (dx - .5) / (dy + .5);
                double rightSlope = (dx + .5) / (dy - .5);
                
                if(start < rightSlope) // not in beam yet
                    continue;
                else if (end > leftSlope) // beyond beam
                    break;
                else
                {
                    // observer has LoS to the square; mark accordingly
                    if(dx * dx + dy * dy < RADIUS_SQUARED)
                    {
                        setVisible(x, y);
                    }
                    if(blocked) // we're scanning a row of blocked squares
                    {
                        if(isBlocked(x, y))
                        {
                            newStart = rightSlope;
                            continue;
                        }
                        else
                        {
                            blocked = false;
                            start = newStart;
                        }
                    }
                    else    // have been scanning transparent squares
                    {
                        if(isBlocked(x, y) && j < radius) // start a child
                        {
                            blocked = true;
                            castLight(cx, cy, j + 1, start, leftSlope, radius, xx, xy, yx, yy);
                            newStart = rightSlope;
                        }
                    }
                }
            } // end while loop
            if(blocked)
                break;
        }
    }  // end castLight()
    
    public void calcFoV(int xLoc, int yLoc, int radius)
    // Calculate lit squares from a given location and radius
    {
        flag += 1;
        for(int oct = 0; oct < 8; oct += 1)
        {
            castLight(xLoc, yLoc,     // starting coordinates
                      1,        // row number
                      1.0, 0.0, radius, // bounding slopes and radius
                      multipliers[0][oct], multipliers[1][oct], multipliers[2][oct], multipliers[3][oct]); // octant multipliers
        }
        visibilityMap[xLoc][yLoc] = flag;
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
}