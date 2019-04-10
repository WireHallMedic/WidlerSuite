/*******************************************************************************************
  
  	Part of the A* section of WidlerSuite. Used to track nodes for AStar and AStarOpenList.
    Direct use of this class is generally not necessary.
  
    Copyright 2019 Michael Widler
    Free for private or public use. No warranty is implied or expressed.
*******************************************************************************************/

package WidlerSuite;

import java.util.*;

public class AStarNode
{
	private int f;
	private int g;
	private int h;
	private AStarNode parentNode;
    private Coord loc;


	public int getF(){return f;}
	public int getG(){return g;}
	public int getH(){return h;}
	public AStarNode getParentNode(){return parentNode;}
    public Coord getLoc(){return new Coord(loc);}

    // constructor
    public AStarNode(Coord l, int distToEnd){this(l, null, distToEnd, 0);}
    public AStarNode(Coord l, AStarNode parent, int distToEnd, int stepDist)
    {
        loc = l;
        parentNode = parent;
        h = distToEnd;
        g = 0;
        if(parentNode != null)
            g = parent.g + stepDist;
        calcF();
    }
    
    // does what it says on the tin
    public void calcF()
    {
        f = g + h;
    }
    
    // compare which route is shorter, and update if necessary
    public void update(AStarNode prospectiveParent, int stepDist)
    {
        if(parentNode == null || g > prospectiveParent.g + stepDist)
        {
            parentNode = prospectiveParent;
            g = prospectiveParent.g + stepDist;
            calcF();
        }
    }
    
    // returns the path from this back to a node with no parent (ie, the origin)
    public Vector<Coord> traceToStart()
    {
        Vector<Coord> path = new Vector<Coord>();
        AStarNode curNode = this;
        do
        {
            path.add(curNode.loc);
            curNode = curNode.parentNode;
        }   while(curNode.parentNode != null);
        return path;
    }
}