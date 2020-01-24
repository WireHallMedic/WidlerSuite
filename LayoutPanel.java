/*******************************************************************************************
  
A panel which manages the layout (size and location) of child panels passed with that info.
  
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
   protected Component parentComponent;
   
   // main constructor
   public LayoutPanel(JFrame pc)
   {
      super();
      super.setLayout(null);
      componentList = new Vector<Component>();
      layoutDataList = new Vector<double[]>();
      parentComponent = pc;
      setSize(pc.getWidth(), pc.getHeight());
      setVisible(false);
      pc.addComponentListener(this);
      pc.add(this);
   }
   
   // overwrite parent method
   @Override
   public void setLayout(LayoutManager l)
   {
      throw new RuntimeException("LayoutPanel cannot accept a LayoutManager object");
   }
   
   // add JPanels with layout info
   public void add(Component p, double w, double h, double x, double y)
   {
      super.add(p);
      componentList.add(p);
      double[] valArr = {w, h, x, y};
      layoutDataList.add(valArr);
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