/*

Based (heavily) on a GDC talk by Squirrel Eiserloh.
Essentially a hashing function posing as an RNG. Fast, good-quality output.
Is threadsafe if you don't use seeds (just add the seed to the position when
calling sample(int)).

*/

package WidlerSuite;

public class SquirrelRNG
{
   public static final int BIT_NOISE_ONE =   0xB5297A4D;
   public static final int BIT_NOISE_TWO =   0x68E31DA4;
   public static final int BIT_NOISE_THREE = 0x1B56C4E9;
   public static final int BIG_PRIME_NUMBER = 198491317;
   private int _seed = 0;
   private int lastIndexed = 0;
   
   // seeded constructor
   public SquirrelRNG(int seed)
   {
      setSeed(seed);
      lastIndexed = 0;
   }
   
   // unseeded constructor (seeds with current time)
   public SquirrelRNG()
   {
      this((int)System.currentTimeMillis());
   }
   
   // set seed
   public void setSeed(int s)
   {
      _seed = s;
   }
   
   // set seed and get int value at a position
   public int sampleInt(int position, int seed)
   {
      setSeed(seed);
      return sampleInt(position);
   }
   
   // get next double value, based on last value sampled
   public double nextDouble()
   {
      return sampleDouble(++lastIndexed);
   }
   
   // get next int value, based on last value sampled
   public int nextInt()
   {
      return sampleInt(++lastIndexed);
   }
   
   // get value at position offset from existing seed (which may be zero)
   public int sampleInt(int position)
   {
      lastIndexed = position;
      int val = Math.abs(position + _seed);
      val *= BIT_NOISE_ONE;
      val = val ^ (val >>> 8);
      val += BIT_NOISE_TWO;
      val = val ^ (val << 8);
      val *= BIT_NOISE_THREE;
      val = val ^ (val >>> 8);
      val = Math.abs(val);
      return val;
   }
   
   // int to double conversion method
   private static double toDouble(int val)
   {
      return (double)val / (double)Integer.MAX_VALUE;
   }
   
   // get value at position offset from existing seed (which may be zero)
   public double sampleDouble(int position)
   {
      return toDouble(sampleInt(position));
   }
   
   // get int value at 2D position
   public int sample2DInt(int x, int y)
   {
      return sampleInt(x + (BIG_PRIME_NUMBER * y));
   }
   
   // get double value at 2D position
   public double sample2DDouble(int x, int y)
   {
      return toDouble(sample2DInt(x, y));
   }
   
   // set seed and get int value at 2D position
   public int sample2DInt(int x, int y, int seed)
   {
      setSeed(seed);
      return sample2DInt(x, y);
   }
   
   // set seed and get double value at 2D position
   public double sample2DDouble(int x, int y, int seed)
   {
      return toDouble(sample2DInt(x, y, seed));
   }
   
   // testing/demo method
   public static void main(String[] args)
   {
      SquirrelRNG rng = new SquirrelRNG(0);
      double sum = 0.0;
      double val = 0.0;
      int iters = 10000000;
      int[] outArr = new int[10];
      for(int i = 0; i < iters; i++)
      {
         //System.out.println(String.format("%f.5", SquirrelRNG.sample(i)));
         val = rng.sampleDouble(i);
         sum += val;
         outArr[(int)(val * 10.0)]++;
      }
      System.out.println(String.format("Average = %.5f", sum / (double)iters));
      for(int i = 0; i < 10; i++)
         System.out.println(String.format("Bin #%d: %7d", i, outArr[i]));
      
      for(int i = 0; i < 10; i++)
         System.out.println(rng.nextDouble() + "");
      
      for(int x = 0; x < 5; x++)
         {
         for(int y = 0; y < 5; y++)
         {
            System.out.print(String.format("%.3f ", rng.sample2DDouble(x, y)));
         }
         System.out.println();
      }
   }
}