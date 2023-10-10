/**********************************************************************************
A class for keeping track of unbound strings for a RogueTilePanel object, and other
miscellaneous animation functions. Called internally, there should be no need to
directly instantiate this. When the parent RogueTilePanel object receives an ActionEvent,
it is passed down to this object. Defaults to 30 frames per second.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

**********************************************************************************/
package WidlerSuite;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class TileAnimationManager implements ActionListener
{
   protected Vector<UnboundTile> lockList;
   protected Vector<UnboundTile> nonlockList;
   protected Vector<MovementScript> scriptList;
   protected RogueTilePanel parentPanel;
   protected boolean unpaused = true;
   
   // animation flags and indexes
   protected boolean slowBlink = true;
   protected boolean mediumBlink = true;
   protected boolean fastBlink = true;
   protected int slowPulseIndex = 0;
   protected int mediumPulseIndex = 0;
   protected int fastPulseIndex = 0;
   protected int masterTickIndex = 0;
   protected int tickThrottle = 1;
   
   // speeds and related inner variables
   protected int slowBlinkTicks = 30;      // ticks for slow blink to change
   protected int mediumBlinkTicks = 20;    // ticks for medium blink to change
   protected int fastBlinkTicks = 10;      // ticks for fast blink to change
   protected int blinkTickIndex = 0;
   protected int maxBlinkTickIndex = 6000;
   
   protected int pulseTicks = 3;           // ticks to update the pulses
   protected int pulseTickIndex = 0;
   protected int maxPulseIndex = 20;       // should be a multiple of 6 or might not hit highest index
   protected int slowPulseStep = 1;
   protected int mediumPulseStep = 2;
   protected int fastPulseStep = 3;
   protected int maxPulseTickIndex = 6;
   
   public void pause(){unpaused = false;}
   public void unpause(){unpaused = true;}
   
   public boolean slowBlink(){return slowBlink;}
   public boolean mediumBlink(){return mediumBlink;}
   public boolean fastBlink(){return fastBlink;}
   public int slowPulse(){return slowPulseIndex;}
   public int mediumPulse(){return mediumPulseIndex;}
   public int fastPulse(){return fastPulseIndex;}
   
   // constructor, requires the RogueTilePanel that it is managing
   public TileAnimationManager(RogueTilePanel pp)
   {
      parentPanel = pp;
      setMaxTickIndex();
      clear();
   }
   
   
   // change size of unbound tiles on the fly
   public void setSizeMultiplier(double sizeMult)
   {
      for(UnboundTile ut: lockList)
      {
         ut.setSizeMultiplierAndRender(sizeMult);
      }
      for(UnboundTile ut: nonlockList)
      {
         ut.setSizeMultiplierAndRender(sizeMult);
      }
   }
   
   // ticks and pulses only increment every n ticks, where n = tickThrottle
   public void setThrottle(int t)
   {
      tickThrottle = t;
   }
   
   // adding unbound tiles
   public void addLocking(UnboundTile str){lockList.add(str);}
   public void addNonlocking(UnboundTile str){nonlockList.add(str);}
   public void addScript(MovementScript scr){scriptList.add(scr);}
   
   // getting lists
   public Vector<UnboundTile> getLockList(){return lockList;}
   public Vector<UnboundTile> getNonlockList(){return nonlockList;}
   public Vector<MovementScript> getScriptList(){return scriptList;}
   
   // check if should wait until animation is completed to proceed
   public boolean isLocked()
   {
      return lockList.size() > 0;
   }
   
   // clears the lists and indexes
   public void clear()
   {
      lockList = new Vector<UnboundTile>();
      nonlockList = new Vector<UnboundTile>();
      scriptList = new Vector<MovementScript>();
   }
   
   // remove an unbound string
   public void remove(UnboundTile element)
   {
      lockList.remove(element);
      nonlockList.remove(element);
   }
   
   // remove an movement script 
   public void remove(MovementScript element)
   {
      scriptList.remove(element);
   }
   
   // set where the tick index resets
   protected void setMaxTickIndex()
   {
      maxBlinkTickIndex = slowBlinkTicks * mediumBlinkTicks * fastBlinkTicks;
   }
   
   // set where the tick index resets
   protected void setMaxPulseTickIndex()
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
         processScriptList(ae);
         processUTList(lockList, ae);
         processUTList(nonlockList, ae);
      }
   }
   
   // update each script in the list
   protected void processScriptList(ActionEvent ae)
   {
      MovementScript curScript;
      for(int i = 0; i < scriptList.size(); i++)
      {
         curScript = scriptList.elementAt(i);
         curScript.actionPerformed(ae);
         if(curScript.isExpired())
         {
            // only unlock targets if no other active script targets them and also unlocks on end
            if(curScript.nonlocksTargetOnEnd())
            {
               boolean shouldUnlock = true;
               for(int j = 0; j < scriptList.size(); j++)
               {
                  if(i != j &&
                     scriptList.elementAt(j).getTarget() == curScript.getTarget() &&
                     scriptList.elementAt(j).nonlocksTargetOnEnd())
                  {
                     shouldUnlock = false;
                     break;
                  }
               }
               if(shouldUnlock)
               {
                  UnboundTile target = (UnboundTile)curScript.getTarget();
                  remove(target);
                  addNonlocking(target);
               }
            }
            scriptList.removeElementAt(i);
            i--;
         }
      }
   }
   
   // update each unbound string in a list. try/catch for concurrency protection
   protected void processUTList(Vector<UnboundTile> list, ActionEvent ae)
   {
      try{
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
      catch(Exception ex){}
      
   }
   
   // update the pulses and blinks
   protected void increment()
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
