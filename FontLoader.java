package WidlerSuite;

import javax.imageio.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.LineMetrics;

public class FontLoader
{  
   public static String load(String fontFileName)
   {
      // Loads the named font file, which must be a truetype font, and returns whether or not the attempt was successful.
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
   
   public static int getCharWidth(String fontName, int pointSize)
   {
      // return the pixel width of the widest character
      Font font = new Font(fontName, Font.PLAIN, pointSize);
      Canvas c = new Canvas();
      FontMetrics fm = c.getFontMetrics(font);
      return fm.stringWidth((char)9580 + "");
   }
   
   public static int getCharHeight(String fontName, int pointSize)
   {
      // return the pixel height of the tallest character
      Font font = new Font(fontName, Font.PLAIN, pointSize);
      Canvas c = new Canvas();
      FontMetrics fm = c.getFontMetrics(font);
      LineMetrics lm = fm.getLineMetrics((char)9580 + "", c.getGraphics());
      return (int)lm.getHeight();
     // return (int)(fm.getAscent());
   }
}