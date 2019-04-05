/*

Constants for WidlerSuite

*/
package WidlerSuite;

public interface WSConstants
{

    public static final int RECT_MODE = 0;
    public static final int HEX_MODE = 1;
    
    // adjacency lists: x, y, stepCost
    public static final int[][] rectOrtho = {{-1, 0, 10}, {0, -1, 10}, {1, 0, 10}, {0, 1, 10}};
    public static final int[][] rectDiag = {{-1, 0, 10}, {0, -1, 10}, {1, 0, 10}, {0, 1, 10},
                                              {-1, -1, 14}, {-1, 1, 14}, {1, -1, 14}, {1, 1, 14}};
                                          //  NE            E           SE          SW           W            NW
    public static final int[][] hexEvenRow = {{0, -1, 10}, {1, 0, 10}, {0, 1, 10}, {-1, 1, 10}, {-1, 0, 10}, {-1, -1, 10}};
    public static final int[][] hexOddRow  = {{1, -1, 10}, {1, 0, 10}, {1, 1, 10}, {0, 1, 10},  {-1, 0, 10}, {0, -1, 10}};
    public static final int NE = 0;
    public static final int E  = 1;
    public static final int SE = 2;
    public static final int SW = 3;
    public static final int W  = 4;
    public static final int NW = 5;
    
    public static final int[][] RECT_FILL_LIST_ORTHO = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    public static final int[][] RECT_FILL_LIST_DIAG =  {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
    public static final int[][] HEX_FILL_LIST_ODD =    {{hexOddRow[E][0],   hexOddRow[E][1]},
                                                        {hexOddRow[NW][0],  hexOddRow[NW][1]},
                                                        {hexOddRow[SW][0],  hexOddRow[SW][1]}};
    public static final int[][] HEX_FILL_LIST_EVEN =   {{hexEvenRow[E][0],  hexEvenRow[E][1]},
                                                        {hexEvenRow[NW][0], hexEvenRow[NW][1]},
                                                        {hexEvenRow[SW][0], hexEvenRow[SW][1]}};
    
    // display settings
    public static final double GRAVITY = .04;           // tiles per tick
    public static final int FRAMES_PER_SECOND = 24;
}