/**********************************************************************************
A class for loading fonts, with some helper functions for checking size.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

**********************************************************************************/

package WidlerSuite;

import javax.imageio.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.LineMetrics;

public class FontLoader
{  
   // Loads the named font file, which must be a truetype font, and returns whether or not 
   // the attempt was successful.
   public static String load(String fontFileName)
   {
      String fontName = null;
      InputStream inStream;
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      // Try and load from file location
      try
      {
         inStream = new BufferedInputStream(new FileInputStream(fontFileName + ".ttf"));
         Font font = Font.createFont(Font.TRUETYPE_FONT, inStream);
         ge.registerFont(font);
         fontName = font.getName();
      }
      catch(Exception ex){}
      
      // try and load from .jar
      if(fontName == null)
      {
         try
         {
            Class cls = Class.forName("RoguePanel");
            inStream = cls.getResourceAsStream("/" + fontFileName + ".ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, inStream);
            ge.registerFont(font);
            fontName = font.getName();
         }
         catch(Exception ex){}
      }
      return fontName;
   }
   
   // Returns the pixel width of the widest standard character in the named font.
   public static int getCharWidth(String fontName, int pointSize)
   {
      Font font = new Font(fontName, Font.PLAIN, pointSize);
      Canvas c = new Canvas();
      FontMetrics fm = c.getFontMetrics(font);
      return Math.max(Math.max(fm.stringWidth("@"), fm.stringWidth("O")), fm.stringWidth("W"));
   }
   
   // Returns the pixel height of the tallest standard character in the named font.
   public static int getCharHeight(String fontName, int pointSize)
   {
      Font font = new Font(fontName, Font.PLAIN, pointSize);
      Canvas c = new Canvas();
      LineMetrics lm = c.getFontMetrics(font).getLineMetrics("@O", c.getGraphics());
      return (int)lm.getAscent();
   }
}