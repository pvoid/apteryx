/*
 * Copyright (C) 2010-2011  Dmitry Petuhov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pvoid.apteryxaustralis;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.SpinnerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WrappedActionBar
{
  public interface OnNavigationListener
  {
    boolean onNavigationItemSelected(int itemPosition, long itemId);
  }

  private static final Class[] _sSetDrawableParams = new Class[]       {Drawable.class};
  private static final Class[] _sSetNavigationModeParams = new Class[] {Integer.TYPE};
  private static final Class[] _sSetListNavigationCallbackParams = new Class[] {SpinnerAdapter.class, ActionBar.OnNavigationListener.class};
  private final Object _mActionBar;
  private final Method _mSetDrawableMethod;
  private final Method _mSetNavigationMode;
  private final Method _mSetListNavigationCallback;
  
  public WrappedActionBar(Activity activity) throws IllegalArgumentException
  {
    super();
///////
    try
    {
      Method getBarMethod = activity.getClass().getMethod("getActionBar",new Class[] {});
      _mActionBar = getBarMethod.invoke(activity);
      if(_mActionBar!=null)
      {
        final Class cls = _mActionBar.getClass();
        _mSetDrawableMethod = cls.getMethod("setBackgroundDrawable", _sSetDrawableParams);
        _mSetNavigationMode = cls.getMethod("setNavigationMode", _sSetNavigationModeParams);
        _mSetListNavigationCallback = cls.getMethod("setListNavigationCallbacks", _sSetListNavigationCallbackParams);
      }
      else
      {
        _mSetDrawableMethod = null;
        _mSetNavigationMode = null;
        _mSetListNavigationCallback = null;
      }
    }
    catch (NoSuchMethodException e)
    {
      e.printStackTrace();
      throw new IllegalArgumentException();
    }
    catch (InvocationTargetException e)
    {
      e.printStackTrace();
      throw new IllegalArgumentException();
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
      throw new IllegalArgumentException();
    }
  }
  
  public void setBackgroundDrawable(Drawable drawable)
  {
    if(_mSetDrawableMethod==null || _mActionBar==null)
      return;
///////
    try
    {
      _mSetDrawableMethod.invoke(_mActionBar, drawable);
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }
    catch (InvocationTargetException e)
    {
      e.printStackTrace();
    }
  }

  public void setNavigationMode(int mode)
  {
    if(_mSetNavigationMode==null || _mActionBar==null)
      return;
///////
    try
    {
      _mSetNavigationMode.invoke(_mActionBar, mode);
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }
    catch (InvocationTargetException e)
    {
      e.printStackTrace();
    }
  }

  public void setListNavigationCallbacks(SpinnerAdapter adapter, OnNavigationListener listener)
  {
    if(_mSetListNavigationCallback==null || _mActionBar==null)
      return;
///////
    try
    {
      _mSetListNavigationCallback.invoke(_mActionBar,adapter,new FakeOnNavigationListener(listener));
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }
    catch (InvocationTargetException e)
    {
      e.printStackTrace();
    }
  }

  private static class FakeOnNavigationListener implements ActionBar.OnNavigationListener
  {
    private final OnNavigationListener _mListener;

    public FakeOnNavigationListener(OnNavigationListener listener)
    {
      super();
      _mListener = listener;
    }

    @Override
    public boolean onNavigationItemSelected(int index, long id)
    {
      return _mListener.onNavigationItemSelected(index,id);
    }
  }
}
