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
   // Loads the named font file, which must be a truetype font, and returns the system name, or
   // null if unsuccessful
   public static String load(String fontFileName)
   {
      String fontName = null;
      try
      {
         InputStream inStream = new BufferedInputStream(new FileInputStream(fontFileName + ".ttf"));
         Font font = Font.createFont(Font.TRUETYPE_FONT, inStream);
         GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         ge.registerFont(font);
         fontName = font.getName();
      }
      catch(Exception ex)
      {
         System.out.println("Could not load " + fontFileName + ". " + ex.toString()); 
      }
      return fontName;
   }
   
   // Returns the pixel width of the widest character in the named font.
   public static int getCharWidth(String fontName, int pointSize)
   {
      Font font = new Font(fontName, Font.PLAIN, pointSize);
      Canvas c = new Canvas();
      FontMetrics fm = c.getFontMetrics(font);
      return fm.stringWidth((char)9580 + "");
   }
   
   // Returns the pixel height of the tallest character in the named font.
   public static int getCharHeight(String fontName, int pointSize)
   {
      Font font = new Font(fontName, Font.PLAIN, pointSize);
      Canvas c = new Canvas();
      FontMetrics fm = c.getFontMetrics(font);
      LineMetrics lm = fm.getLineMetrics((char)9580 + "", c.getGraphics());
      return (int)lm.getHeight();
   }
}