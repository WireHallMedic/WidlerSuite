/***************************************************************************

A simple static class for obfuscating and de-obfuscating (if that's a word)
strings of text.

Uses a simple pattern to shift characters within the Unicode range of ' ' to '~'
by a varying amount; characters outside of this range are left untouched.

Copyright 2020 Michael Widler
Free for private or public use. No warranty is implied or expressed.

***************************************************************************/

package WidlerSuite;


public class Obfuscator
{
   public static final int UPPER_LIMIT = (int)'~';
   public static final int LOWER_LIMIT = (int)' ';
   public static final int RANGE = ((int)UPPER_LIMIT - (int)LOWER_LIMIT) + 1;
   
   // internal method to keep us from getting to big negative numbers
   private static int bindVal(int val)
   {
      return val % RANGE;
   }
   
   // right-shift the charcater through the range, wrapping
   private static char shiftInto(char c, int val)
   {
      int charVal = (int)c;
      if(charVal <= UPPER_LIMIT && charVal >= LOWER_LIMIT)
      {
         charVal += bindVal(val) - LOWER_LIMIT;
         charVal = charVal % RANGE;
         charVal += LOWER_LIMIT;
      }
      return (char)charVal;
   }
   
   // left-shift the character through the range, wrapping
   private static char shiftOutOf(char c, int val)
   {
      int charVal = (int)c;
      if(charVal <= UPPER_LIMIT && charVal >= LOWER_LIMIT)
      {
         charVal -= LOWER_LIMIT;
         charVal -= bindVal(val);
         charVal += RANGE;
         charVal = charVal % RANGE;
         charVal += LOWER_LIMIT;
      }
      return (char)charVal;
   }
   
   // get an obfuscated copy of the string
   public static String obfuscate(String str)
   {
      String newStr = "";
      for(int i = 0; i < str.length(); i++)
      {
         newStr += shiftInto(str.charAt(i), (i + 1) * 7);
      }
      return newStr;
   }
   
   // get a deobfuscated copy of the string
   public static String deobfuscate(String str)
   {
      String newStr = "";
      for(int i = 0; i < str.length(); i++)
      {
         newStr += shiftOutOf(str.charAt(i), (i + 1) * 7);
      }
      return newStr;
   }
   
   // test method
   public static void main(String[] args)
   {
      String str = "The quick brown fox jumped over the lazy dog's back !@#$%^&*()_-+=~tHE QUICK BROWN FOX JUMPED OVER THE LAZY DOG'S BACK";
      System.out.println("Original:\n" + str);
      str = obfuscate(str);
      System.out.println("Obfuscated once\n" + str);
      str = deobfuscate(str);
      System.out.println("Deobfuscated once:\n" + str);
      for(int i = 0; i < 10; i++)
         str = obfuscate(str);
      System.out.println("Obfuscated ten times:\n" + str);
      for(int i = 0; i < 10; i++)
         str = deobfuscate(str);
      System.out.println("Deobfuscated ten times:\n" + str);
   }
}