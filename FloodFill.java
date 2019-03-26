/***************************************************
//
//	A different implementation of a flood fill algorythm.
// Accepts a boolean array, then returns a seperate
// array with only the filled area true; all else is
// false.
//	Note: Not thread safe (will collide with itself)
//
***************************************************/

package WidlerSuite;


public class FloodFill implements WSConstants
{
	private static boolean[][] returnArea;
    // only half the adjacent tiles are needed
    private static int[][] RECT_FILL_LIST =     {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private static int[][] HEX_FILL_LIST_ODD =  {{hexOddRow[E][0],   hexOddRow[E][1]},
                                                 {hexOddRow[NW][0],  hexOddRow[NW][1]},
                                                 {hexOddRow[SW][0],  hexOddRow[SW][1]}};
    private static int[][] HEX_FILL_LIST_EVEN = {{hexEvenRow[E][0],  hexEvenRow[E][1]},
                                                 {hexEvenRow[NW][0], hexEvenRow[NW][1]},
                                                 {hexEvenRow[SW][0], hexEvenRow[SW][1]}};
	
	public static boolean[][] fill(boolean area[][], int x, int y)
	{	
		returnArea = new boolean[area.length][area[0].length];
		boolean[][] newArea = new boolean[area.length][area[0].length];
		
		for(int w = 0; w < area.length; w++)
		for(int h = 0; h < area[0].length; h++)
			newArea[w][h] = !area[w][h];
		
		floodFill(newArea, x, y);
		
		return returnArea;
	}
	
	public static boolean[][] fill(boolean area[][], Coord loc)
	{
		return fill(area, loc.x, loc.y);
	}
	
	
	private static void floodFill(boolean area[][], int x, int y)
	//	fills the array with true, using true as the boundary
	// start on or adjacent to false
	{
		try
		{
			if(area[x][y] == false)
			{
				area[x][y] = true;
				returnArea[x][y] = true;
				floodFill(area, x + 1, y);
				floodFill(area, x - 1, y);
				floodFill(area, x, y + 1);
				floodFill(area, x, y - 1);
			}
		}
		catch(ArrayIndexOutOfBoundsException arrEx) {}
	}
	
	
	public static String getVisual(boolean arr[][])
	{
		String value = "";
		String printout = "";
		for(int x = 0; x < arr.length; x++)
		{
			for(int y = 0; y < arr[0].length; y++)
			{
				if(arr[x][y] == true)
					value = ".";
				else
					value = "#";
				printout = printout + value; 
			}
			printout = printout + "\n";
		}
		return printout;
	}
	
	
	public static void invertMap(boolean[][] map)
	// not necessary, but often useful
	{
		for(int x = 0; x < map.length; x++)
		for(int y = 0; y < map[0].length; y++)
			map[x][y] = !map[x][y];
	}
	
	
	public static void main(String args[])
	{
		boolean demo[][] = new boolean[20][20];
		
		for(int x = 0; x < 20; x++)
		{
			for(int y = 0; y < 20; y++)
			{
				demo[x][y] = true;
			}
		}
		demo[5][5] = false;
		demo[6][5] = false;
		demo[7][4] = false;
		demo[8][5] = false;
		demo[9][5] = false;
		demo[5][9] = false;
		demo[6][9] = false;
		demo[7][10] = false;
		demo[8][9] = false;
		demo[9][9] = false;
		demo[5][6] = false;
		demo[9][6] = false;
		demo[5][7] = false;
		demo[9][7] = false;
		demo[5][8] = false;
		demo[9][8] = false;
		
		demo[1][1] = false;
		demo[1][3] = false;
		
		
		System.out.println(getVisual(demo));
		
		boolean[][] map = fill(demo, 7, 7);
		
		System.out.println(getVisual(map));
	}
}