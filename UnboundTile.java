/**********************************************************************************
An extension of UnboundString for tile-sized elements. Displayed by RoguePanel
a little differently, and always has a background.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

**********************************************************************************/

package WidlerSuite;

import java.awt.*;
import java.awt.event.*;

public class UnboundTile extends UnboundString implements WSConstants
{
   @Override
   public boolean hasBackgroundBox(){return true;}
   
   // minimal constructor
   public UnboundTile(String s)
   {
      this(s, Color.WHITE, 0, 0);
   }
   
   // setting constructors
   public UnboundTile(String s, Color fg, Coord l){this(s, fg, l.x, l.y);}
   public UnboundTile(String s, Color fg, int x, int y)
   {
      super(s, fg, x, y);
      super.setBGColor(Color.BLACK);
      super.setAffectedByGravity(false);
      super.setAffectedByAge(false);
   }
   
   // copy constructor
   public UnboundTile(UnboundTile that)
   {
      super((UnboundString)that);
   }
}