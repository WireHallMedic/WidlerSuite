/*******************************************************************************************

Constants for WidlerSuite. Used by a variety of classes.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

*******************************************************************************************/

package WidlerSuite;

public interface WSConstants
{
   public static final String VERSION = "v1.1.1";
   // mode settings
   public static final int RECT_MODE = 0;
   public static final int HEX_MODE = 1;
   public static final boolean SEARCH_DIAGONAL = true;
   public static final boolean DONT_SEARCH_DIAGONAL = false;
   
   // adjacency lists: x, y, stepCost
   public static final int[][] RECT_ORTHO = {{-1, 0, 10}, {0, -1, 10}, {1, 0, 10}, {0, 1, 10}};
   public static final int[][] RECT_DIAG = {{-1, 0, 10}, {0, -1, 10}, {1, 0, 10}, {0, 1, 10},
                                            {-1, -1, 14}, {-1, 1, 14}, {1, -1, 14}, {1, 1, 14}};
   //                                          NE           E          SE           SW            W            NW
   public static final int[][] HEX_EVEN_ROW = {{0, -1, 10}, {1, 0, 10}, {0, 1, 10}, {-1, 1, 10}, {-1, 0, 10}, {-1, -1, 10}};
   public static final int[][] HEX_ODD_ROW  = {{1, -1, 10}, {1, 0, 10}, {1, 1, 10}, {0, 1, 10},  {-1, 0, 10}, {0, -1, 10}};
   public static final int NE = 0;
   public static final int E  = 1;
   public static final int SE = 2;
   public static final int SW = 3;
   public static final int W  = 4;
   public static final int NW = 5;
   
   // display settings. Non-final variables intentionally not final.
   public static double GRAVITY = .04;           // tiles per tick
   public static int FRAMES_PER_SECOND = 30;
   public static final double PIXELS_TO_POINTS = .75;
   
   //	angles
   public final static double FULL_CIRCLE = 2 * Math.PI;                // 360 degrees
   public final static double THREE_QUARTER_CIRCLE = 3 * (Math.PI / 2); // 270 degrees
   public final static double HALF_CIRCLE = Math.PI;                    // 180 degrees
   public final static double QUARTER_CIRCLE = Math.PI / 2;             // 90 degrees
   public final static double EIGHTH_CIRCLE = Math.PI / 4;              // 45 degrees
   public final static double SIXTH_CIRCLE = Math.PI / 3;               // 30 degrees
   public final static double TWELFTH_CIRCLE = Math.PI / 6;             // 15 degrees
   
   // My_Font_16x16 departures from cp437
   public static final int MY_FONT_BOLT = 0x01;
   public static final int MY_FONT_BIG_CIRCLE = 0x02;
   public static final int MY_FONT_NOT_EQUAL = 0xFF;
   public static final int MY_FONT_DIE_ONE = 0x80;
   public static final int MY_FONT_DIE_TWO = 0x86;
   public static final int MY_FONT_DIE_THREE = 0x87;
   public static final int MY_FONT_DIE_FOUR = 0x8F;
   public static final int MY_FONT_DIE_FIVE = 0xA6;
   public static final int MY_FONT_DIE_SIX = 0xA7;
}
