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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import org.pvoid.apteryxaustralis.preference.Preferences;

public class UpdateStatusService extends Service
{
  private static final Class[] _sStartForegroundSignature = new Class[] { int.class, Notification.class};
  private static final Class[] _sStopForegroundSignature = new Class[] { boolean.class};
  private static final Class[] _sSetForegroundSignature = new Class[] {boolean.class};

  private static final Method _sStartForeground;
  private static final Method _sStopForeground;
  private static final Method _sSetForeground;
  private static volatile boolean _sServiceRunning = false;

  private AlarmManager _mAlarmManager;
  private NotificationManager _mNotifyManager;

  static
  {
    final Class cl = UpdateStatusService.class;
    Method method;
    try
    {
      method = cl.getMethod("startForeground", _sStartForegroundSignature);
    }
    catch(NoSuchMethodException e)
    {
      method = null;
    }
    _sStartForeground = method;
////////////
    try
    {
      method = cl.getMethod("stopForeground", _sStopForegroundSignature);
    }
    catch(NoSuchMethodException e)
    {
      method = null;
    }
    _sStopForeground = method;
////////////
    try
    {
      method = cl.getMethod("setForeground", _sSetForegroundSignature);
    }
    catch(NoSuchMethodException e)
    {
      method = null;
    }
    _sSetForeground = method;
  }

  @Override
  public void onCreate()
  {
    super.onCreate();
    _mNotifyManager = (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);
  }
  
  @Override
  public void onDestroy()
  {
    super.onDestroy();
    handleStop();
  }
  
  @Override
  public void onStart(Intent intent, int startId)
  {
    super.onStart(intent, startId);
    handleStart();
  }
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId)
  {
    handleStart();
    return(START_STICKY);
  }

  private void makeForeground(boolean foreground)
  {
    if(_sStartForeground !=null && _sStopForeground !=null)
    {
      if(foreground)
      {
        final Object[] args = new Object[2];
        args[0] = Notifier.NOTIFICATION_ICON;
        args[1] = Notifier.getIcon(this);

        try
        {
          _sStartForeground.invoke(this, args);
        }
        catch (IllegalArgumentException e)
        {
          e.printStackTrace();
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
      else
      {
        final Object[] args = new Object[1];
        args[0] = Boolean.TRUE;

        try
        {
          _sStopForeground.invoke(this, args);
        }
        catch (IllegalArgumentException e)
        {
          e.printStackTrace();
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
    }
    else if(_sSetForeground !=null)
    {
      final Object[] args = new Object[1];
      args[0] = foreground;
      try
      {
        _sSetForeground.invoke(this, args);
        if(foreground)
          _mNotifyManager.notify(Notifier.NOTIFICATION_ICON, Notifier.getIcon(this));
        else
          _mNotifyManager.cancel(Notifier.NOTIFICATION_ICON);
      }
      catch (IllegalArgumentException e)
      {
        e.printStackTrace();
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
  }

  private void handleStart()
  {
    _sServiceRunning = true;
////////
    long interval = Preferences.getUpdateInterval(this);
    if(interval==0)
      return;
///////
    Intent intent = new Intent(this,StatesReceiver.class);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
    _mAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
    _mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+interval,pendingIntent);
/////// Иконку поставим
    makeForeground(true);
  }
  
  private void handleStop()
  {
    _sServiceRunning = false;
////////
    if(_mAlarmManager !=null)
    {
      Intent intent = new Intent(this, StatesReceiver.class);
      _mAlarmManager.cancel(PendingIntent.getBroadcast(this, 0, intent, 0));
    }
////////
    makeForeground(false);
  }
  
  @Override
  public IBinder onBind(Intent arg0)
  {
    return(null);
  }
  
  public static boolean Executed()
  {
    return(_sServiceRunning);
  }
}
