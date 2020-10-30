/*******************************************************************************************

Checksum functionality using Luhn's Algorithm

This class is intended to be used statically. It applies the principles of Luhn's Algorithm 
(which is intended to be used on large integers) to the int values of character strings.

When converting between chars and ints, we shift +/-32, to stay out of the control characters
at the bottom of the ASCII chart.

It uses a constant, D. In Luhn's algorithm, D is 10.

Luhn's Algorithm validation (char string):
1. Starting from the rightmost digit excluding the checksum, store double the value of every other digit. 
   If this results in a value equal to or greater than D, store the mod d of that value.
2. In the place of each undoubled char, store its int value.
3. Sum the value of each place (including the checksum).
4. If this sum % D is 0, true. Else, false.

Luhn's Algorithm checksum generation (char string)
1. Starting from the rightmost character, store double the value of every other character. 
   If this results in a value equal to or greater than d, store the mod d of that value.
2. In the place of each undoubled char, store its int value.
3. Sum the value of each place.
4. Take the mod D of the sum.
5. Subtract this value from D. If this value == D, the checksum value is 0. Else, this value
   is the checksum value.
6. Convert this into to a char.

This class has two implementations; one for integers, and one for character strings.

Copyright 2020 Michael Widler
Free for private or public use. No warranty is implied or expressed.

*******************************************************************************************/

package WidlerSuite;
import java.util.*;

public class LuhnChecksum
{
   public static final int D = 127; // ASCII space through ~
   
   // converts a char to its value - 32 as an int. Used to stay out of the control characters.
   private static int charToInt(int c)
   {
      return ((int)c) - 32;
   }
   
   // converts an int representation to a char. Used to stay out of the control characters.
   private static char intToChar(int i)
   {
      return (char)(i + 32);
   }
   
   public static String appendChecksum(String str)
   {
      return str + Character.toString(genChecksum(str));
   }
   
   public static char genChecksum(String str)
   {
      int[] val = new int[str.length()];
      int sum = 0;
      int checkSumInt;
      // steps 1 and 2
      for(int i = 0; i < str.length(); i++)
         val[i] = charToInt(str.charAt(i)) % D;
      for(int i = str.length() - 1; i >= 0; i -= 2)
         val[i] = (val[i] * 2) % D;
      // step 3
      for(int i = 0; i < val.length; i++)
         sum += val[i];
      // step 4
      checkSumInt = D - (sum % D);
      // step 5
      if(checkSumInt == D)
         checkSumInt = 0;
      return intToChar(checkSumInt);
   }
   
   public static boolean validate(String str)
   {
      int[] val = new int[str.length()];
      int sum = 0;
      int checkSumInt;
      // steps 1 and 2
      for(int i = 0; i < str.length(); i++)
         val[i] = charToInt(str.charAt(i)) % D;
      // len - 2 to discount the checksum
      for(int i = str.length() - 2; i >= 0; i -= 2)
         val[i] = (val[i] * 2) % D;
      // step 3
      for(int i = 0; i < val.length; i++)
         sum += val[i];
      // step 4
      return sum % D == 0;
   }

   public static void main(String[] args)
   {
      String str1 = "The quick brown fox jumped over the lazy dog's back.";
      String str2 = appendChecksum(str1);
      String str3 = "hTe quick brown fox jumped over the lazy dog's back.";
      String str4 = appendChecksum(str3);
      
      System.out.println("Input:      " + str1);
      System.out.println("Output:     " + str2);
      System.out.println("Validation: " + validate(str2));
      System.out.println("Input:      " + str3);
      System.out.println("Output:     " + str4);
      System.out.println("Validation: " + validate(str4));
      System.out.println("Validation with 'X' appended: " + validate(str4 + "X"));
   }
}