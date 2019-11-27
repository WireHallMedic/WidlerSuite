/**********************************************************************************
An element to be used by MovementScript in manipulating an UnboundString or child
class of same. Direct access by application programmer is generally not necessary.
Basically a struct.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

**********************************************************************************/

package WidlerSuite;

public class MovementScriptStep
{
   public double xImpulse; // in tiles per tick
   public double yImpulse; // in tiles per tick
   public double xOffsetAdj;    // in tiles
   public double yOffsetAdj;    // in tiles
   
   // basic empty constructor
   public MovementScriptStep()
   {
      clear();
   }
   
   // copy constructor
   public MovementScriptStep(MovementScriptStep that)
   {
      this.xImpulse = that.xImpulse;
      this.yImpulse = that.yImpulse;
      this.xOffsetAdj = that.xOffsetAdj;
      this.yOffsetAdj = that.yOffsetAdj;
   }
   
   // clear all data
   public void clear()
   {
      xImpulse = 0.0;
      yImpulse = 0.0;
      xOffsetAdj = 0.0;
      yOffsetAdj = 0.0;
   }
   
   // set speed adjustment. Redundant as data members are public
   public void setImpulse(double x, double y)
   {
      xImpulse = x;
      yImpulse = y;
   }
   
   // set speed adjustment. Redundant as data members are public
   public void setOffsetAdj(double x, double y)
   {
      xOffsetAdj = x;
      yOffsetAdj = y;
   }
}