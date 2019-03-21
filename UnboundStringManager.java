/**********************************************************************************
A class for keeping track of unbound strings for a RoguePanel object. Called
internally, there should be a minimal need to directly access this. When the parent
RoguePanel object receives an ActionEvent, it is passed down to this object.

API:
    constructors:
UnboundStringManager(RoguePanel parent)
    setters:
none
    getters:
Vector<UnboundString> getLockList()     // returns the current list of unbound strings which lock the animation
Vector<UnboundString> getNonlockList()  // returns the current list of unbound strings which do not lock the animation
boolean isLocked()                      // returns true if there are any elements in the lock list, else false
    other functions:
void clear()                            // empties the lists
void addLocking(UnboundString us)       // adds an unbound string to the lock list
void add(UnboundString us)              // adds an unbound string to the nonlock list
void addNonlocking(UnboundString us)    // adds an unbound string to the nonlock list


**********************************************************************************/
package WidlerSuite;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class UnboundStringManager implements ActionListener
{
    private Vector<UnboundString> lockList = new Vector<UnboundString>();
    private Vector<UnboundString> nonlockList = new Vector<UnboundString>();
    private RoguePanel parentPanel;
    
    public UnboundStringManager(RoguePanel pp)
    {
        parentPanel = pp;
    }
    
    public void addLocking(UnboundString str){lockList.add(str);}
    public void addNonlocking(UnboundString str){nonlockList.add(str);}
    
    public Vector<UnboundString> getLockList(){return lockList;}
    public Vector<UnboundString> getNonlockList(){return nonlockList;}
    
    public boolean isLocked()
    {
        return lockList.size() > 0;
    }
    
    public void clear()
    {
        lockList = new Vector<UnboundString>();
        nonlockList = new Vector<UnboundString>();
    }
    
    // timer kick
    public void actionPerformed(ActionEvent ae)
    {
        for(int i = 0; i < lockList.size(); i++)
        {
            lockList.elementAt(i).actionPerformed(ae);
            if(lockList.elementAt(i).isExpired())
            {
                lockList.removeElementAt(i);
                i--;
            }
        }
        for(int i = 0; i < nonlockList.size(); i++)
        {
            nonlockList.elementAt(i).actionPerformed(ae);
            if(nonlockList.elementAt(i).isExpired())
            {
                nonlockList.removeElementAt(i);
                i--;
            }
        }
    }
}