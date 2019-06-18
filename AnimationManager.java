/**********************************************************************************
A class for keeping track of unbound strings for a RoguePanel object, and other
miscellaneous animation functions. Called internally, there should be no need to
directly instantiate this. When the parent RoguePanel object receives an ActionEvent,
it is passed down to this object. Defaults to 30 frames per second.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

**********************************************************************************/
package WidlerSuite;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class AnimationManager implements ActionListener
{
   protected Vector<UnboundString> lockList = new Vector<UnboundString>();
   protected Vector<UnboundString> nonlockList = new Vector<UnboundString>();
   protected RoguePanel parentPanel;
   protected static boolean unpaused = true;
   
   // animation flags and indexes
   protected static boolean slowBlink = true;
   protected static boolean mediumBlink = true;
   protected static boolean fastBlink = true;
   protected static int slowPulseIndex = 0;
   protected static int mediumPulseIndex = 0;
   protected static int fastPulseIndex = 0;
   protected static int masterTickIndex = 0;
   protected static int tickThrottle = 1;
   
   // speeds and related inner variables
   protected static int slowBlinkTicks = 30;      // ticks for slow blink to change
   protected static int mediumBlinkTicks = 20;    // ticks for medium blink to change
   protected static int fastBlinkTicks = 10;      // ticks for fast blink to change
   protected static int blinkTickIndex = 0;
   protected static int maxBlinkTickIndex = 6000;
   
   protected static int pulseTicks = 3;           // ticks to update the pulses
   protected static int pulseTickIndex = 0;
   protected static int maxPulseIndex = 20;       // should be a multiple of 6 or might not hit highest index
   protected static int slowPulseStep = 1;
   protected static int mediumPulseStep = 2;
   protected static int fastPulseStep = 3;
   protected static int maxPulseTickIndex = 6;
   
   public static void pause(){unpaused = false;}
   public static void unpause(){unpaused = true;}
   
   public static boolean slowBlink(){return slowBlink;}
   public static boolean mediumBlink(){return mediumBlink;}
   public static boolean fastBlink(){return fastBlink;}
   public static int slowPulse(){return slowPulseIndex;}
   public static int mediumPulse(){return mediumPulseIndex;}
   public static int fastPulse(){return fastPulseIndex;}
   
   // constructor, requires the RoguePanel that it is managing
   public AnimationManager(RoguePanel pp)
   {
      parentPanel = pp;
      setMaxTickIndex();
   }
   
   // ticks and pulses only increment every n ticks, where n = tickThrottle
   public static void setThrottle(int t)
   {
      tickThrottle = t;
   }
   
   // adding unbound strings
   public void addLocking(UnboundString str){lockList.add(str);}
   public void addNonlocking(UnboundString str){nonlockList.add(str);}
   
   // getting lists
   public Vector<UnboundString> getLockList(){return lockList;}
   public Vector<UnboundString> getNonlockList(){return nonlockList;}
   
   // check if should wait until animation is completed to proceed
   public boolean isLocked()
   {
      return lockList.size() > 0;
   }
   
   // clears the lists and indexes
   public void clear()
   {
      lockList = new Vector<UnboundString>();
      nonlockList = new Vector<UnboundString>();
   }
   
   // set where the tick index resets
   protected static void setMaxTickIndex()
   {
      maxBlinkTickIndex = slowBlinkTicks * mediumBlinkTicks * fastBlinkTicks;
   }
   
   // set where the tick index resets
   protected static void setMaxPulseTickIndex()
   {
      maxPulseTickIndex = slowPulseStep * mediumPulseStep * fastPulseStep;
   }
   
   // timer kick
   public void actionPerformed(ActionEvent ae)
   {
      increment();
      
      // only process lists if ununpaused
      if(unpaused)
      {
         processList(lockList, ae);
         processList(nonlockList, ae);
      }
   }
   
   // update each unbound string in a list
   protected void processList(Vector<UnboundString> list, ActionEvent ae)
   {
      for(int i = 0; i < list.size(); i++)
      {
         list.elementAt(i).actionPerformed(ae);
         if(list.elementAt(i).isExpired())
         {
            list.removeElementAt(i);
            i--;
         }
      }
   }
   
   // update the pulses and blinks
   protected static void increment()
   {
      // early exit based on tickThrottle
      masterTickIndex++;
      if(masterTickIndex < tickThrottle)
         return;
      
      // blinks
      blinkTickIndex++;
      if(blinkTickIndex >= maxBlinkTickIndex)
         blinkTickIndex = 0;
      
      if(blinkTickIndex % slowBlinkTicks == 0)
         slowBlink = !slowBlink;
      if(blinkTickIndex % mediumBlinkTicks == 0)
         mediumBlink = !mediumBlink;
      if(blinkTickIndex % fastBlinkTicks == 0)
         fastBlink = !fastBlink;
      
      // pulses
      pulseTickIndex++;
      if(pulseTickIndex >= maxPulseTickIndex)
         pulseTickIndex = 0;
      
      if(pulseTickIndex % pulseTicks == 0)
      {
         slowPulseIndex += slowPulseStep;
         if(slowPulseIndex == 0 || slowPulseIndex > maxPulseIndex - Math.abs(slowPulseStep))
         {
            slowPulseStep = 0 - slowPulseStep;
         }
      
         mediumPulseIndex += mediumPulseStep;
         if(mediumPulseIndex == 0 || mediumPulseIndex > maxPulseIndex - Math.abs(mediumPulseStep))
         {
            mediumPulseStep = 0 - mediumPulseStep;
         }
         
         fastPulseIndex += fastPulseStep;
         if(fastPulseIndex == 0 || fastPulseIndex > maxPulseIndex - Math.abs(fastPulseStep))
         {
            fastPulseStep = 0 - fastPulseStep;
         }
      }
   }
}
