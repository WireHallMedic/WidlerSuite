/**********************************************************************************
Contains a list of steps (changes in position) and impulses (changes in speed) to be 
applied to an UnboundString target (or child class). Passed to a RoguePanel with add(), 
which then passes it off to the appropriate manager.

Instances of MovementScript are kicked before their targets are, and do not kick their
targets; targets should still be on either the locking or nonlocking list.

Copyright 2019 Michael Widler
Free for private or public use. No warranty is implied or expressed.

**********************************************************************************/

package WidlerSuite;

import java.util.*;
import java.awt.event.*;

public class MovementScript implements ActionListener
{
	private UnboundString target;
	private Vector<MovementScriptStep> stepList;
	private boolean loops;
	private boolean expiresTargetOnEnd;
	private boolean expired;
   private boolean nonlockTargetOnEnd;     // move target to nonlocking list
   private int age;


	public UnboundString getTarget(){return target;}
	public Vector<MovementScriptStep> getStepList(){return stepList;}
	public boolean loops(){return loops;}
	public boolean expiresTargetOnEnd(){return expiresTargetOnEnd;}
   public int length(){return stepList.size();}
   public boolean nonlocksTargetOnEnd(){return nonlockTargetOnEnd;}


	public void setTarget(UnboundString t){target = t;}
	public void setStepList(Vector<MovementScriptStep> s){stepList = s;}
	public void setLoops(boolean l){loops = l;}
	public void setExpiresTargetOnEnd(boolean e){expiresTargetOnEnd = e;}
	public void setExpired(boolean e){expired = e;}
   public void setNonlocksTargetOnEnd(boolean n){nonlockTargetOnEnd = n;}

   
   // constructor
   public MovementScript(UnboundString us)
   {
      target = us;
      stepList = new Vector<MovementScriptStep>();
      loops = false;
      expiresTargetOnEnd = false;
      expired = false;
      nonlockTargetOnEnd = false;
      age = 0;
   }
   
   // check if the script is expired, either manually or by age
   public boolean isExpired()
   {
      return expired || (age > stepList.size() && loops == false);
   }
   
   // sets the length of the script, in ticks
   public void setLength(int newLength)
   {
      // trim to fit
      if(newLength < length())
      {
         stepList.setSize(newLength);
      }
      // extend to fit
      else if(newLength > length())
      {
         while(newLength >=length())
            stepList.add(new MovementScriptStep());
      }
   }
   
   // set the impulse at a particular time
   public void setImpulse(int tickIndex, double newXImpulse, double newYImpulse)
   {
      if(stepList.size() <= tickIndex)
         setLength(tickIndex + 1);
      stepList.elementAt(tickIndex).xImpulse = newXImpulse;
      stepList.elementAt(tickIndex).yImpulse = newYImpulse;
   }
   
   // set the step at a particular time
   public void setOffset(int tickIndex, double newXOffsetAdj, double newYOffsetAdj)
   {
      if(stepList.size() <= tickIndex)
         setLength(tickIndex + 1);
      stepList.elementAt(tickIndex).xOffsetAdj = newXOffsetAdj;
      stepList.elementAt(tickIndex).yOffsetAdj = newYOffsetAdj;
   }
   
   // set both the impulse and step at a particular tick
   public void setStep(int tickIndex, double newXImpulse, double newYImpulse, double newXOffsetAdj, double newYOffsetAdj)
   {
      if(stepList.size() <= tickIndex)
         setLength(tickIndex + 1);
      stepList.elementAt(tickIndex).xImpulse = newXImpulse;
      stepList.elementAt(tickIndex).yImpulse = newYImpulse;
      stepList.elementAt(tickIndex).xOffsetAdj = newXOffsetAdj;
      stepList.elementAt(tickIndex).yOffsetAdj = newYOffsetAdj;
   }
   
   // copy a step into the list
   public void setStep(int tickIndex, MovementScriptStep newStep)
   {
      setStep(tickIndex, newStep.xImpulse, newStep.yImpulse, newStep.xOffsetAdj, newStep.yOffsetAdj);
   }
   
   // clear a particular step
   public void clearStep(int tickIndex)
   {
      if(stepList.size() <= tickIndex)
         setLength(tickIndex + 1);
      stepList.elementAt(tickIndex).clear();
   }
   
   // kicked by timer
   public void actionPerformed(ActionEvent ae)
   {
      // avoid falling off the end
      if(age < stepList.size())
      {
         MovementScriptStep step = stepList.elementAt(age);
         target.adjustXSpeed(step.xImpulse);
         target.adjustYSpeed(step.yImpulse);
         target.adjustXOffset(step.xOffsetAdj);
         target.adjustYOffset(step.yOffsetAdj);
      }
      
      // increment, loop if necessary
      age++;
      if(loops && age >= stepList.size())
      {
         age = 0;
      }
      
      // expire target if appropriate one tick after final step
      if(age > stepList.size() && expiresTargetOnEnd)
      {
         target.forceExpire();
      }
   }
}