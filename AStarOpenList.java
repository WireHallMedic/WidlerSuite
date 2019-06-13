/*******************************************************************************************
  
Part of the A* section of WidlerSuite. A linked list used as a priority queue for node
processing.
Direct use of this class is generally not necessary.
  
Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.
*******************************************************************************************/

package WidlerSuite;

import java.util.*;

public class AStarOpenList
{
   private int size;
   private Link head;
    
   // empty constructor
   public AStarOpenList()
   {
      size = 0;
      head = null;
   }
    
   // main constructor
   public AStarOpenList(Coord l, int distToEnd)
   {
      this();
      addToEmpty(new AStarNode(l, distToEnd));
   }
    
   // returns the size of the queue
   public int size()
   {
      return size;
   }
    
   // adds a new link based on the passed node; assumes queue is empty.
   public void addToEmpty(AStarNode node)
   {
      head = new Link(node);
      size = 1;
   }   
    
   // checks if any of the links contain a specific node index
   public boolean contains(Coord c)
   {
      if(size == 0)
         return false;
      Link curLink = head;
      if(curLink.node.getLoc().equals(c))
         return true;
      curLink = curLink.next;
      while(curLink != head)
      {
         if(curLink.node.getLoc().equals(c))
            return true;
         curLink = curLink.next;
      }
      return false;
   }
    
   // potentially updates a node with a new parent if that route is shorter
   public void update(Coord loc, AStarNode possibleParent, int stepDist)
   {
      Link curLink = head;
      do
      {
         if(curLink.node.getLoc().equals(loc))
         {
            curLink.node.update(possibleParent, stepDist);
            break;
         }
         curLink = curLink.next;
      }   while(curLink != head);
   }
    
   // push a new node into the list, maintaining a sort by f.
   public void push(AStarNode newNode)
   {
      if(size == 0)
         addToEmpty(newNode);
      else
      {
         Link curLink = head;
         boolean isInserted = false;
         do
         {
            if(newNode.getH() < curLink.node.getH())
            {
               curLink.insertAhead(newNode);
               isInserted = true;
               break;
            }
         }   while(curLink != head);
         if(isInserted == false)
         {
            head.insertAhead(newNode);
         }
         size++;
      }
   }
    
   // push a new node into the list, forcibily setting it as the head
   public void pushToFront(AStarNode newNode)
   {
      if(size == 0)
         addToEmpty(newNode);
      else
      {
         head.insertAhead(newNode);
         size++;
      }
   }
    
   // pops the top (ie, lowest f) node from the list
   public AStarNode pop()
   {
      if(size == 0)
         return null;
      AStarNode topNode = head.node;
      Link newHead = head.next;
      head.remove();
      head = newHead;
      size -= 1;
      if(size == 0)
         head = null;
      return topNode;
   }
    
   // checks the top node without popping it
   public AStarNode peek()
   {
      if(size == 0)
         return null;
      return head.node;
   }
    
   // returns true if the top node contains the passed location
   public boolean pathExists(Coord terminus)
   {
      return size > 0 && head.node.getLoc().equals(terminus);
   }
    
   // a wrapper to hold the nodes in a linked list
   private class Link
   {
      public AStarNode node;
      public Link prev;
      public Link next;
     
      public Link(AStarNode n){this(n, null, null);}
      public Link(AStarNode _node, Link _prev, Link _next)
      {
         node = _node;
         prev = _prev;
         next = _next;
         if(prev == null)
            prev = this;
         if(next == null)
            next = this;
      }
     
      // remove this link from the list
      public void remove()
      {
         next.prev = this.prev;
         prev.next = this.next;
      }
     
      // insert a new link with the passed node immedeatly behind this link
      public void insertBehind(AStarNode n)
      {
         Link newLink = new Link(n, this, this.next);
         this.next.prev = newLink;
         this.next = newLink;
      }
        
      // insert a new link with the passed node immedeatly ahead of this link
      public void insertAhead(AStarNode n)
      {
         Link newLink = new Link(n, this.prev, this);
         this.prev.next = newLink;
         this.prev = newLink;
      }
   }
}