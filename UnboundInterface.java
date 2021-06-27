/**********************************************************************************
An interface to ensure movement scripts can manipulate their targets

Copyright 2021 Michael Widler
Free for private or public use. No warranty is implied or expressed.

**********************************************************************************/

package WidlerSuite;

public interface UnboundInterface
{
   public void adjustXSpeed(double s);
   public void adjustYSpeed(double s);
   public void adjustXOffset(double p);
   public void adjustYOffset(double p);
   public void forceExpire();
}