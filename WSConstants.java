/*

Constants for WidlerSuite

*/
package WidlerSuite;

public interface WSConstants
{
    public static final int RECT_MODE = 0;
    public static final int HEX_MODE = 1;
    public static final boolean SEARCH_DIAGONAL = true;
    public static final boolean DONT_SEARCH_DIAGONAL = false;
    
    // adjacency lists: x, y, stepCost
    public static final int[][] RECT_ORTHO = {{-1, 0, 10}, {0, -1, 10}, {1, 0, 10}, {0, 1, 10}};
    public static final int[][] RECT_DIAG = {{-1, 0, 10}, {0, -1, 10}, {1, 0, 10}, {0, 1, 10},
                                              {-1, -1, 14}, {-1, 1, 14}, {1, -1, 14}, {1, 1, 14}};
                                          //  NE            E           SE          SW           W            NW
    public static final int[][] HEX_EVEN_ROW = {{0, -1, 10}, {1, 0, 10}, {0, 1, 10}, {-1, 1, 10}, {-1, 0, 10}, {-1, -1, 10}};
    public static final int[][] HEX_ODD_ROW  = {{1, -1, 10}, {1, 0, 10}, {1, 1, 10}, {0, 1, 10},  {-1, 0, 10}, {0, -1, 10}};
    public static final int NE = 0;
    public static final int E  = 1;
    public static final int SE = 2;
    public static final int SW = 3;
    public static final int W  = 4;
    public static final int NW = 5;
    public static final int N  = 6; // not used for hexes
    public static final int S  = 7; // not used for hexes
    
    // display settings
    public static final double GRAVITY = .04;           // tiles per tick
    public static final int FRAMES_PER_SECOND = 24;
}