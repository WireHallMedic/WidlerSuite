/*

A static class for building borders, using the boxdrawing characters in the [0xB3, 0xDA] range of cp437
style character sets. It is possible to give input for which there is no visually consistent output, due
to the limits of the box drawing tile subset (for example, three consecutive cells -=-).

Uses bit masking, which is why the constants look so odd.

Implemented by receiving a 2d int array, representing tiles. Cells with 1 or 2 have that many lines,
all other numbers are ignored. Actual tile is determined by examining the four surrounding cells, 
and picking the one that has the correct number of connections on each side. A 2d int array is returned,
with either 0 or the hex code for the tile in each index.

*/

package WidlerSuite;

import javax.swing.*;   // for test method
import java.awt.*;      // for test method

public class BorderBuilder
{
   public static final  int U1 = 1;
   public static final  int U2 = 2;
   public static final  int R1 = 4;
   public static final  int R2 = 8;
   public static final  int D1 = 16;
   public static final  int D2 = 32;
   public static final  int L1 = 64;
   public static final  int L2 = 128;
   
   // first row
   public static final int TILE_B3 = U1 | D1;
   public static final int TILE_B4 = U1 | D1 | L1;
   public static final int TILE_B5 = U1 | D1 | L2;
   public static final int TILE_B6 = U2 | D2 | L1;
   public static final int TILE_B7 = D2 | L1;
   public static final int TILE_B8 = D1 | L2;
   public static final int TILE_B9 = U2 | D2 | L2;
   public static final int TILE_BA = U2 | D2;
   public static final int TILE_BB = D2 | L2;
   public static final int TILE_BC = U2 | L2;
   public static final int TILE_BD = U2 | L1;
   public static final int TILE_BE = U1 | L2;
   public static final int TILE_BF = D1 | L1;
   
   // second row
   public static final int TILE_C0 = U1 | R1;
   public static final int TILE_C1 = U1 | R1 | L1;
   public static final int TILE_C2 = R1 | D1 | L1;
   public static final int TILE_C3 = U1 | R1 | D1;
   public static final int TILE_C4 = R1 | L1;
   public static final int TILE_C5 = U1 | R1 | D1 | L1;
   public static final int TILE_C6 = U1 | R2 | D1;
   public static final int TILE_C7 = U2 | R1 | D2;
   public static final int TILE_C8 = U2 | R2;
   public static final int TILE_C9 = R2 | D2;
   public static final int TILE_CA = U2 | R2 | L2;
   public static final int TILE_CB = R2 | D2 | L2;
   public static final int TILE_CC = U2 | R2 | D2;
   public static final int TILE_CD = R2 | L2;
   public static final int TILE_CE = U2 | R2 | D2 | L2;
   public static final int TILE_CF = U1 | R2 | L2;
   
   // third row
   public static final int TILE_D0 = U2 | R1 | L1;
   public static final int TILE_D1 = R2 | D1 | L2;
   public static final int TILE_D2 = R1 | D2 | L1;
   public static final int TILE_D3 = U2 | R1;
   public static final int TILE_D4 = U1 | R2;
   public static final int TILE_D5 = R2 | D1;
   public static final int TILE_D6 = R1 | D2;
   public static final int TILE_D7 = U2 | R1 | D2 | L1;
   public static final int TILE_D8 = U1 | R2 | D1 | L2;
   public static final int TILE_D9 = U1 | L1;
   public static final int TILE_DA = R1 | D1;
   
   // make an array based on array input
   public static int[][] getBorderTiles(int[][] origArr)
   {
      int[][] borderArr = new int[origArr.length][origArr[0].length];
      for(int x = 0; x < borderArr.length; x++)
      for(int y = 0; y < borderArr[0].length; y++)
      {
         borderArr[x][y] = getTileIndexFromNeighbors(x, y, origArr);
         if(borderArr[x][y] == 0 && (origArr[x][y] == 1 || origArr[x][y] == 2))
            borderArr[x][y] = catchCornerCase(x, y, origArr);
      }
      return borderArr;
   }
   
   // get the value of a tile, or zero if that tile is outside the array
   private static int getIndividualIndexValue(int x, int y, int[][] arr)
   {
      if(x < 0 || y < 0 || x >= arr.length || y >= arr[0].length)
         return 0;
      return arr[x][y];
   }
   
   // get the bitmask generated from the four orthogonal neighbors
   private static int getValueFromNeighbors(int x, int y, int[][] arr)
   {
      // early exit if this tile should be blank
      if(getIndividualIndexValue(x, y, arr) == 0)
         return 0;
      int returnVal = 0;
      // up
      switch(getIndividualIndexValue(x, y - 1, arr))
      {
         case 1 : returnVal = returnVal | U1; break;
         case 2 : returnVal = returnVal | U2; break;
      }
      // right
      switch(getIndividualIndexValue(x + 1, y, arr))
      {
         case 1 : returnVal = returnVal | R1; break;
         case 2 : returnVal = returnVal | R2; break;
      }
      // down
      switch(getIndividualIndexValue(x, y + 1, arr))
      {
         case 1 : returnVal = returnVal | D1; break;
         case 2 : returnVal = returnVal | D2; break;
      }
      // left
      switch(getIndividualIndexValue(x - 1, y, arr))
      {
         case 1 : returnVal = returnVal | L1; break;
         case 2 : returnVal = returnVal | L2; break;
      }
      return returnVal;
   }
   
   // turn the bitmask into an integer index
   private static int getTileIndex(int bitMask)
   {
      int tile = 0x00;
      switch(bitMask)
      {
         // first row
         case TILE_B3:  tile = 0xB3; break;
         case TILE_B4:  tile = 0xB4; break;
         case TILE_B5:  tile = 0xB5; break;
         case TILE_B6:  tile = 0xB6; break;
         case TILE_B7:  tile = 0xB7; break;
         case TILE_B8:  tile = 0xB8; break;
         case TILE_B9:  tile = 0xB9; break;
         case TILE_BA:  tile = 0xBA; break;
         case TILE_BB:  tile = 0xBB; break;
         case TILE_BC:  tile = 0xBC; break;
         case TILE_BD:  tile = 0xBD; break;
         case TILE_BE:  tile = 0xBE; break;
         case TILE_BF:  tile = 0xBF; break;
         
         // second row
         case TILE_C0:  tile = 0xC0; break;
         case TILE_C1:  tile = 0xC1; break;
         case TILE_C2:  tile = 0xC2; break;
         case TILE_C3:  tile = 0xC3; break;
         case TILE_C4:  tile = 0xC4; break;
         case TILE_C5:  tile = 0xC5; break;
         case TILE_C6:  tile = 0xC6; break;
         case TILE_C7:  tile = 0xC7; break;
         case TILE_C8:  tile = 0xC8; break;
         case TILE_C9:  tile = 0xC9; break;
         case TILE_CA:  tile = 0xCA; break;
         case TILE_CB:  tile = 0xCB; break;
         case TILE_CC:  tile = 0xCC; break;
         case TILE_CD:  tile = 0xCD; break;
         case TILE_CE:  tile = 0xCE; break;
         case TILE_CF:  tile = 0xCF; break;
         
         // third row
         case TILE_D0:  tile = 0xD0; break;
         case TILE_D1:  tile = 0xD1; break;
         case TILE_D2:  tile = 0xD2; break;
         case TILE_D3:  tile = 0xD3; break;
         case TILE_D4:  tile = 0xD4; break;
         case TILE_D5:  tile = 0xD5; break;
         case TILE_D6:  tile = 0xD6; break;
         case TILE_D7:  tile = 0xD7; break;
         case TILE_D8:  tile = 0xD8; break;
         case TILE_D9:  tile = 0xD9; break;
         case TILE_DA:  tile = 0xDA; break;         
      }
      return tile;
   }
   
   // set a tile based on its neighbors
   private static int getTileIndexFromNeighbors(int x, int y, int[][] arr)
   {
      return getTileIndex(getValueFromNeighbors(x, y, arr));
   }
   
   // some setups require examining how many lines are expected
   private static int catchCornerCase(int x, int y, int[][] origArr)
   {
      int[][] tempArr = new int[3][3];
      int borderVal = origArr[x][y];
      tempArr[1][1] = borderVal;
      if(getIndividualIndexValue(x, y - 1, origArr) > 0)
         tempArr[1][0] = borderVal;
      if(getIndividualIndexValue(x + 1, y, origArr) > 0)
         tempArr[2][1] = borderVal;
      if(getIndividualIndexValue(x, y + 1, origArr) > 0)
         tempArr[1][2] = borderVal;
      if(getIndividualIndexValue(x - 1, y, origArr) > 0)
         tempArr[0][1] = borderVal;
      return getTileIndexFromNeighbors(1, 1, tempArr);
   }
   
   // testing/demo method
   public static void main(String[] args)
   {
      JFrame frame = new JFrame();
      frame.setSize(1200, 1200);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      TilePalette palette = new TilePalette("WidlerSuite/MyFont_16x16.png", 16, 16);
      RogueTilePanel rtp = new RogueTilePanel(40, 40, palette);
      
      int[][] borderTemplateArr = {
         {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 1, 1, 1, 1, 2, 0, 0, 0, 0},
         {1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0},
         {1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 1, 1, 1, 2, 0, 0, 0, 0},
         {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0},
         {1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0},
         {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 1, 0, 2, 0, 0, 2, 0, 0, 0, 0},
         {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 1, 1, 1, 1, 2, 0, 0, 0, 0},
         {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
         {2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
         {2, 2, 2, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
         {2, 2, 2, 2, 2, 2, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
         {2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
         {2, 0, 2, 2, 2, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
         {2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
         {2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0},
         {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 0},
         {2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 0},
         {2, 1, 1, 0, 0, 0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 2, 2, 2, 2, 1, 0, 0, 0, 0},
         {2, 1, 1, 1, 1, 1, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 0, 0, 0, 2, 0, 1, 0, 0, 0, 0},
         {2, 0, 1, 0, 1, 0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 1, 1, 1, 2, 0, 1, 0, 0, 0, 0},
         {2, 0, 1, 1, 1, 0, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 0, 0, 0, 2, 0, 1, 0, 0, 0, 0},
         {2, 0, 1, 0, 1, 0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0},
         {2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
         {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
         {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
         {1, 2, 2, 0, 0, 0, 2, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
         {1, 2, 2, 2, 2, 2, 2, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
         {1, 0, 2, 0, 2, 0, 2, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
         {1, 0, 2, 2, 2, 0, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
         {1, 0, 2, 0, 2, 0, 2, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
         {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
      
      int[][] borderArr = BorderBuilder.getBorderTiles(borderTemplateArr);
      
      for(int x = 0; x < borderArr.length; x++)
      for(int y = 0; y < borderArr[0].length; y++)
      {
         if(borderArr[x][y] != 0)
            rtp.setTile(x, y, borderArr[x][y], Color.CYAN, Color.BLACK);
      }
      
      frame.add(rtp);
      frame.setVisible(true);
   }

}