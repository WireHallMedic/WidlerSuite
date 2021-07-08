/*

An implementation for bagging; give it a range of doubles, tell it how many
copies of each you want, and it makes a deck of them to be popped one at a time.

*/

package WidlerSuite;

import java.util.*;

public class DoubleDeck
{
   private Vector<Double> deck;
   private int _copies;
   private double[] _values;
   private SquirrelRNG rng;
   
   // constructor to make n copies of a passed list of doubles
   public DoubleDeck(double[] values, int copies)
   {
      rng = new SquirrelRNG((int)System.currentTimeMillis());
      deck = null;
      _values = new double[values.length];
      for(int i = 0; i < values.length; i++)
         _values[i] = values[i];
      _copies = copies;
      shuffle();
   }
   
   // deck of a single copy of the passed values
   public DoubleDeck(double[] values)
   {
      this(values, 1);
   }
   
   // shuffle the deck (full set of values) with seed
   public void shuffle(int seed)
   {
      rng.setSeed(seed);
      shuffle();
   }
   
   // shuffle the deck without reseeding
   public void shuffle()
   {
      Vector<Double> orderedList = new Vector<Double>();
      deck = new Vector<Double>();
      
      // create all the elements in one list
      for(int i = 0; i < _values.length; i++)
      for(int j = 0; j < _copies; j++)
         orderedList.add(new Double(_values[i]));
      
      // randomly add them to a different list
      int index = 0;
      while(orderedList.size() > 0)
      {
         index = (int)((double)orderedList.size() * rng.nextDouble());
         deck.add(orderedList.elementAt(index));
         orderedList.removeElementAt(index);
      }
   }
   
   // pop the top entry
   public double pop()
   {
      if(deck.size() == 0)
         shuffle();
      double val = deck.elementAt(0).doubleValue();
      deck.removeElementAt(0);
      return val;
   }
   
   // testing/demo method
   public static void main(String[] args)
   {
      double[] list = {.1, .2, .3, .4, .5};
      DoubleDeck d = new DoubleDeck(list, 2);
      for(int i = 0; i < 3; i++)
      {
         for(int j = 0; j < 10; j++)
         {
            System.out.print(d.pop() + " ");
         }
         System.out.println();
      }
   }
}