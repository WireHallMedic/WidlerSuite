/*******************************************************************************************

A class for layering multiple levels (octaves) of NoiseObjs. The user can control how many layers
are present, how much each layer weighs comparatively (persistence), and how much each layer
changes in frequency (inversely, the distance between samples).

Calls are done the same as for a NoiseObj; by calling getValue(double, double).

Copyright 2019 Michael Widler
Free for protected or public use. No warranty is implied or expressed.

*******************************************************************************************/

package WidlerSuite;

import java.util.*;

public class NoiseChoir
{
	protected NoiseObj[] noise;
	protected int octaves;
	protected double persistence;               // must be 0 - 1, non-inclusive
	protected double frequencyMultiplier;           
   protected double[] weight;
   
   public static int DEFAULT_OCTAVES = 4;
   public static double DEFAULT_PERSISTENCE = 0.5;
   public static double DEFAULT_FREQUENCY_MULTIPLIER = 0.5;


	public NoiseObj[] getNoise(){return noise;}
	public int getOctaves(){return octaves;}
	public double getPersistence(){return persistence;}


	public void setNoise(NoiseObj[] n){noise = n;}
	public void setOctaves(int o){octaves = o;}
	public void setPersistence(double p){persistence = p;}

   // default constructor
   public NoiseChoir()
   {
      this(DEFAULT_OCTAVES, DEFAULT_PERSISTENCE, DEFAULT_FREQUENCY_MULTIPLIER);
   } 
   
   // constructor setting number of octaves, and level of multiplicative persistence
   public NoiseChoir(int oct, double pers)
   {
      this(oct, pers, DEFAULT_FREQUENCY_MULTIPLIER);
   }
   
   // constructor setting the number of octaves
   public NoiseChoir(int oct)
   {
      this(oct, DEFAULT_PERSISTENCE, DEFAULT_FREQUENCY_MULTIPLIER);
   }
   
   // standard constructor
   public NoiseChoir(int oct, double pers, double freqMult)
   {
      octaves = oct;
      persistence = pers;
      frequencyMultiplier = freqMult;
      noise = new NoiseObj[octaves];
      for(int i = 0; i < octaves; i++)
      {
         noise[i] = new NoiseObj();
      }
      weight = new double[octaves];
      weight[0] = 1.0;
      double persistenceSum = 1.0;
      
      // weight each octave
      for(int i = 1; i < octaves; i++)
      {
         weight[i] = weight[i - 1] * persistence;
         persistenceSum += weight[i];
      }
      for(int i = 0; i < octaves; i++)
      {
         weight[i] = weight[i] / persistenceSum;
      }
      
      generate();
   }
   
   // generate the vertex values
   public void generate()
   {
      for(int i = 0; i < octaves; i++)
      {
         noise[i].generate();
      }
   }
   
   // calculate and return the value of a specific point
   public double getValue(double xOff, double yOff)
   {
      double val = 0.0;
      double frequency = 1.0;
      for(int i = 0; i < octaves; i++)
      {
         val += noise[i].getValue(xOff / frequency, yOff / frequency) * weight[i];
         frequency *= frequencyMultiplier;
      }
      return val;
   }
}