/*******************************************************************************************
  
    A different implementation of a flood fill algorythm, intended to be used statically.
    Accepts a boolean array and starting location; false is impassable,
    true is passable. Retuns an array. Not thread safe (will collide with self)
  
    Copyright 2019 Michael Widler
    Free for private or public use. No warranty is implied or expressed.
  
*******************************************************************************************/

package WidlerSuite;


public class FloodFill implements WSConstants
{
	private static boolean[][] returnArea;
    private static boolean[][] alreadySearched;
    private static int mode = RECT_MODE;
    private static boolean searchDiagonal = false;
	
    // primary method. Returns a boolean array of where true is filled, and false is what was not.
	public static boolean[][] fill(boolean area[][], int x, int y)
	{	
		returnArea = new boolean[area.length][area[0].length];
        alreadySearched = new boolean[area.length][area[0].length];
		
		floodFill(area, x, y);
		
		return returnArea;
	}
	
    // alternate calling of primary method
	public static boolean[][] fill(boolean area[][], Coord loc)
	{
		return fill(area, loc.x, loc.y);
	}
	
    // fills the array with true, using false as the boundary
	// walks the original map, while filling the new one
	private static void floodFill(boolean area[][], int x, int y)
	{
        // reject invalid or already-searched cells
		if(isInBounds(x, y) && !alreadySearched[x][y])
        {
            // mark as searched, mark as true or false, add neighbors to recursion
            alreadySearched[x][y] = true;
			if(area[x][y] == true)
			{
				returnArea[x][y] = true;
                int[][] searchPattern = null;
                if(mode == HEX_MODE)
                    if(y % 2 == 1)
                        searchPattern = HEX_ODD_ROW;
                    else
                        searchPattern = HEX_EVEN_ROW;
                else // RECT_MODE
                    if(searchDiagonal)
                        searchPattern = RECT_DIAG;
                    else
                        searchPattern = RECT_ORTHO;
				for(int[] cell : searchPattern)
                    floodFill(area, x + cell[0], y + cell[1]);
			}
		}
	}
    
    // checks if the passed tile location is in the display bounds
    public static boolean isInBounds(int x, int y)
    {
        if(x >= 0 && y >= 0 && x < returnArea.length && y < returnArea[0].length)
            return true;
        return false;
    }
	
	// returns a String representing the passed boolean array
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
	
	// not necessary, but often useful
	public static void invertMap(boolean[][] map)
	{
		for(int x = 0; x < map.length; x++)
		for(int y = 0; y < map[0].length; y++)
			map[x][y] = !map[x][y];
	}
	
	// test method
	public static void main(String args[])
	{
		boolean demo[][] = new boolean[20][20];
		mode = HEX_MODE;
		for(int x = 0; x < 20; x++)
		{
			for(int y = 0; y < 20; y++)
			{
				demo[x][y] = true;
			}
		}
        
        for(int x = 5; x <= 11; x++)
        {
            demo[x][5] = false;
            demo[x][11] = false;
        }
        for(int y = 5; y <= 11; y++)
        {
            demo[5][y] = false;
            demo[11][y] = false;
        }
		
		demo[1][1] = false;
		demo[1][3] = false;
		
		System.out.println(getVisual(demo));
		
		boolean[][] map = fill(demo, 7, 7);
		
		System.out.println(getVisual(map));
	}
}