/*******************************************************************************************
//
//  After calling the constructor, each call to getNext() returns the next Coord in an
//  increasing spiral. Part of calling the constructor is defining what tiles can be searched;
//  unsearchable tiles also block the search (that is, this is a step-by-step flood fill).
//
//  getNext() returns null if no tiles remain to be searched.
//
//  The intended use is to find something that may or may not exist, when a location isn't
//  known, or when you want to find the closest instance of a particular thing.
//
//  The algorithm only processes a couple tiles ahead of where getNext() is to maintain
//  performance and limit unnecessary memory usage.
//
//  Copyright 2019 Michael Widler
//  Free for private or public use. No warranty is implied or expressed.
//
*******************************************************************************************/


package WidlerSuite;

import java.util.*;

public class SpiralSearch implements WSConstants
{
   private boolean[][] searchArea;
   private boolean[][] alreadySearched;
   private int mode = RECT_MODE;
   private boolean searchDiagonal = false;
   private CoordQueue coordQueue;
   
   
   // Constructor. Sets the paramaters of the search, and primes the queue.
   public SpiralSearch(boolean area[][], int startX, int startY, int tileMode, boolean diag)
   {
      searchArea = new boolean[area.length][area[0].length];
      alreadySearched = new boolean[area.length][area[0].length];
      mode = tileMode;
      searchDiagonal = diag;
      
      for(int x = 0; x < area.length; x++)
      for(int y = 0; y < area[0].length; y++)
         searchArea[x][y] = area[x][y];
      coordQueue = new CoordQueue(new Coord(startX, startY));
      alreadySearched[startX][startY] = true;
      
      search(startX, startY);
   }
   
   public SpiralSearch(boolean area[][], Coord startLoc, int tileMode, boolean diag){this(area, startLoc.x, startLoc.y, tileMode, diag);}
   public SpiralSearch(boolean area[][], Coord startLoc){this(area, startLoc.x, startLoc.y, RECT_MODE, SEARCH_DIAGONAL);}
   public SpiralSearch(boolean area[][], int startX, int startY){this(area, startX, startY, RECT_MODE, SEARCH_DIAGONAL);}
   
   // returns the next coord in the queue
   public Coord getNext()
   {
      Coord c = null;
      if(coordQueue.size() > 0)
      {
         c = coordQueue.pop();
         search(c.x, c.y);
      }
      return c;
   }
   
   // checks if the passed tile location is in the display bounds
   private boolean isInBounds(int x, int y)
   {
      if(x >= 0 && y >= 0 && x < searchArea.length && y < searchArea[0].length)
         return true;
      return false;
   }
   
   // marks the current tile as searched, adds adjacent to queue
   private void search(int x, int y)
   {
      int x2;
      int y2;
      
      int[][] searchPattern = getSearchPattern(y);
      for(int[] cell : searchPattern)
      {
         x2 = x + cell[0];
         y2 = y + cell[1];
         if(isInBounds(x2, y2) && !alreadySearched[x2][y2] && searchArea[x2][y2])
         {
            coordQueue.push(new Coord(x2, y2));
            alreadySearched[x2][y2] = true;
         }
      }
   }
   
   // returns the search patter to use based on mode and location
   private int[][] getSearchPattern(int y)
   {
      int[][] searchPattern = null;
      if(mode == HEX_MODE)        // hex mode
         if(y % 2 == 1)
            searchPattern = HEX_ODD_ROW;
         else
            searchPattern = HEX_EVEN_ROW;
      else                        // rect mode
         if(searchDiagonal)
            searchPattern = RECT_DIAG;
         else
            searchPattern = RECT_ORTHO;
      return searchPattern;
   }
   
   
   //////////////////////////////////////////////////////////
   
   // a private class for maintaining a queue of tiles to search. New tiles are added to the end, pops come off the beginning.
   // Pretty standard linked list stay at O(1) for pushing and popping, as we never need to search.
   private class CoordQueue
   {
      private int size;       // number of elements in the queue
      private Link head;      // first element
      
      // empty constructor
      public CoordQueue()
      {
         size = 0;
         head = null;
      }
      
      // constructor with initial Coord
      public CoordQueue(Coord d)
      {
         this();
         addToEmpty(d);
      }
      
      // returns the size of the queue
      public int size()
      {
         return size;
      }
      
      // pushes a Coord into an empty queue
      public void addToEmpty(Coord d)
      {
         head = new Link(d);
         size = 1;
      }
      
      // push a new Coord to the back of the queue
      public void push(Coord d)
      {
         if(size == 0)
            addToEmpty(d);
         else
         {
            head.insert(d);
            size++;
         }
      }
      
      // pops the first Coord
      public Coord pop()
      {
         if(size == 0)
            return null;
         Coord topData = head.data;
         Link newHead = head.next;
         head.remove();
         head = newHead;
         size -= 1;
         if(size == 0)
            head = null;
         return topData;
      }
      
      // a wrapper to hold the Coords in the linked list
      private class Link
      {
         public Coord data;
         public Link prev;
         public Link next;
         
         public Link(Coord d){this(d, null, null);}
         public Link(Coord _data, Link _prev, Link _next)
         {
            data = _data;
            prev = _prev;
            next = _next;
            if(prev == null)
               prev = this;
            if(next == null)
               next = this;
         }
      
         // remove this link from the list, updating neighbors
         public void remove()
         {
            next.prev = this.prev;
            prev.next = this.next;
         }
         
         // insert a new link with the passed Coord immedeatly ahead of this link
         public void insert(Coord d)
         {
            Link newLink = new Link(d, this.prev, this);
            this.prev.next = newLink;
            this.prev = newLink;
         }
      }
   }
}
