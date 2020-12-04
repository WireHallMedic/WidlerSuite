/*******************************************************************************************
A static class for checking intersections between two line segments.
Points are represented by Coords, and segments are represented by pairs of points.

Adapted from code at https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
by Princi Singh
    
Copyright 2020 Michael Widler
Free for private or public use. No warranty is implied or expressed.
*******************************************************************************************/

package WidlerSuite;

public class LineIntersection
{
   // The main function that returns true if line segment 'p1q1' 
   // and 'p2q2' intersect. 
   public static boolean isIntersecting(Coord p1, Coord q1, Coord p2, Coord q2) 
   { 
      // Find the four orientations needed for general and 
      // special cases 
      int o1 = orientation(p1, q1, p2); 
      int o2 = orientation(p1, q1, q2); 
      int o3 = orientation(p2, q2, p1); 
      int o4 = orientation(p2, q2, q1); 
    
      // General case 
      if(o1 != o2 && o3 != o4) 
         return true; 
    
      // Special Cases 
      // p1, q1 and p2 are colinear and p2 lies on segment p1q1 
      if (o1 == 0 && isOnSegment(p1, q1, p2)) return true; 
    
      // p1, q1 and q2 are colinear and q2 lies on segment p1q1 
      if (o2 == 0 && isOnSegment(p1, q1, q2)) return true; 
    
      // p2, q2 and p1 are colinear and p1 lies on segment p2q2 
      if (o3 == 0 && isOnSegment(p2, q2, p1)) return true; 
    
      // p2, q2 and q1 are colinear and q1 lies on segment p2q2 
      if (o4 == 0 && isOnSegment(p2, q2, q1)) return true; 
    
      return false; // Doesn't fall in any of the above cases 
   }
   public static boolean isIntersecting(int[] p1, int[] q1, int[] p2, int[] q2) 
   {
      return isIntersecting(new Coord(p1[0], p1[1]), new Coord(q1[0], q1[1]), 
                            new Coord(p2[0], p2[1]), new Coord(q2[0], q2[1]));
   }
   
   // Given three colinear points p, q, r, the function checks if 
   // point r lies on line segment 'pq' 
   private static boolean isOnSegment(Coord p, Coord q, Coord r) 
   {
      if(r.x <= Math.max(p.x, q.x) && r.x >= Math.min(p.x, q.x) && 
         r.y <= Math.max(p.y, q.y) && r.y >= Math.min(p.y, q.y)) 
         return true; 
      return false; 
   }
    
   // To find orientation of ordered triplet (p, q, r). 
   // The function returns following values 
   // 0 --> p, q and r are colinear 
   // 1 --> Clockwise 
   // 2 --> Counterclockwise 
   private static int orientation(Coord p, Coord r, Coord q) 
   { 
      // See https://www.geeksforgeeks.org/orientation-3-ordered-points/ 
      // for details of below formula. 
      int val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y); 
    
      if (val == 0) return 0; // colinear 
    
      return (val > 0)? 1: 2; // clock or counterclock wise 
   } 
}