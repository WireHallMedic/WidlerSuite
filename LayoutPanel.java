/*******************************************************************************************
  
A panel which manages the layout (size and location) of child panels passed with that info.
Size and locations are kept as proportions of this panel; for example, if a component is 
added with x = 0.0, y=0.5, h = 0.5, w = 0.5, it would occupy the bottom left quarter of this
panel.
Automatically resizes component when parent frame is resized.
  
Copyright 2020 Michael Widler
Free for private or public use. No warranty is implied or expressed.
  
*******************************************************************************************/

package WidlerSuite;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class LayoutPanel extends JPanel implements ComponentListener
{
   private Vector<Component> componentList;
   private Vector<double[]> layoutDataList;
   protected JFrame parentFrame;
   
   // main constructor
   public LayoutPanel(JFrame pf)
   {
      super();
      setLayout(null);
      componentList = new Vector<Component>();
      layoutDataList = new Vector<double[]>();
      parentFrame = pf;
      setSize(parentFrame.getWidth(), parentFrame.getHeight());
      parentFrame.addComponentListener(this);
      parentFrame.add(this);
   }
   
   // add JPanels with layout info
   public void add(Component c, double w, double h, double x, double y)
   {
      super.add(c);
      componentList.add(c);
      double[] valArr = {w, h, x, y};
      layoutDataList.add(valArr);
   }
   
   // set existing component
   public void setValues(Component c, double w, double h, double x, double y)
   {
      int index = componentList.indexOf(c);
      if(index > -1)
      {
         double[] valArr = {w, h, x, y};
         layoutDataList.setElementAt(valArr, index);
         resizeComponents(false);
      }
   }
   
   // remove existing component
   public void remove(Component c)
   {
      int index = componentList.indexOf(c);
      if(index > -1)
      {
         componentList.removeElementAt(index);
         layoutDataList.removeElementAt(index);
      }
   }
   
   // set the child components
   public void resizeComponents(){resizeComponents(true);}
   public void resizeComponents(boolean repaint)
   {
      for(int i = 0; i < componentList.size(); i++)
      {
         double[] vals = layoutDataList.elementAt(i);
         arrange(componentList.elementAt(i), vals[0], vals[1], vals[2], vals[3]);
      }
      if(repaint)
         repaint();
   }
   
   // set a component to a location and size as relative to parent size
   private void arrange(Component child, double w, double h, double x, double y)
   {
      child.setSize((int)(getWidth() * w), (int)(getHeight() * h));
      child.setLocation((int)(getWidth() * x), (int)(getHeight() * y));
   }
   
   // update metrics when resized
   public void componentResized(ComponentEvent ce){resizeComponents();}
   public void componentMoved(ComponentEvent ce){}
   public void componentHidden(ComponentEvent ce){}
   public void componentShown(ComponentEvent ce){repaint();}
}