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
   private Vector<UnboundString> lockList = new Vector<UnboundString>();
   private Vector<UnboundString> nonlockList = new Vector<UnboundString>();
   private RoguePanel parentPanel;
   private static boolean unpaused = true;
   
   // animation flags and indexes
   private static boolean slowBlink = true;
   private static boolean mediumBlink = true;
   private static boolean fastBlink = true;
   private static int slowPulseIndex = 0;
   private static int mediumPulseIndex = 0;
   private static int fastPulseIndex = 0;
   private static int masterTickIndex = 0;
   private static int tickThrottle = 1;
   
   // speeds and related inner variables
   private static int slowBlinkTicks = 30;      // ticks for slow blink to change
   private static int mediumBlinkTicks = 20;    // ticks for medium blink to change
   private static int fastBlinkTicks = 10;      // ticks for fast blink to change
   private static int blinkTickIndex = 0;
   private static int maxBlinkTickIndex = 6000;
   
   private static int pulseTicks = 3;           // ticks to update the pulses
   private static int pulseTickIndex = 0;
   private static int maxPulseIndex = 20;       // should be a multiple of 6 or might not hit highest index
   private static int slowPulseStep = 1;
   private static int mediumPulseStep = 2;
   private static int fastPulseStep = 3;
   private static int maxPulseTickIndex = 6;
   
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
   private static void setMaxTickIndex()
   {
      maxBlinkTickIndex = slowBlinkTicks * mediumBlinkTicks * fastBlinkTicks;
   }
   
   // set where the tick index resets
   private static void setMaxPulseTickIndex()
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
   private void processList(Vector<UnboundString> list, ActionEvent ae)
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
   private static void increment()
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
