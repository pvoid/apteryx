package org.pvoid.apteryxaustralis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

public class UpdateStatusService extends Service
{
  private static final Class[] _StartForegroundSignature = new Class[] { int.class, Notification.class};
  private static final Class[] _StopForegroundSignature = new Class[] { boolean.class};

  private Method _StartForeground;
  private Method _StopForeground;
  private AlarmManager _AlarmManager;
  
  private static boolean _ServiceRuning = false; 
  
  NotificationManager _NotifyManager;
  
  @Override
  public void onCreate()
  {
    super.onCreate();
    try
    {
      _StartForeground = getClass().getMethod("startForeground",_StartForegroundSignature);
      _StopForeground = getClass().getMethod("stopForeground",_StopForegroundSignature);
    }
    catch(NoSuchMethodException e)
    {
      _StartForeground = _StopForeground = null;
    }
    
    _NotifyManager = (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);
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
  
  private void handleStart()
  {
    _ServiceRuning = true;
////////
    SharedPreferences prefs = getSharedPreferences(Consts.APTERYX_PREFS, Context.MODE_PRIVATE);
    long interval = prefs.getInt(Consts.PREF_INTERVAL, Consts.INTERVALS[Consts.DEFAULT_INTERVAL]);
    if(interval==0)
      return;
///////
    Intent intent = new Intent(this,StatesReceiver.class);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
    _AlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
    _AlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+interval,pendingIntent);
/////// Иконку поставим
    if(_StartForeground!=null)
    {
      Object[] args = new Object[2];
      args[0] = Integer.valueOf(Consts.NOTIFICATION_ICON);
      args[1] = Notifyer.GetIcon(this);
      
      try
      {
        _StartForeground.invoke(this, args);
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
      return;
    }
////////
    setForeground(true);
    _NotifyManager.notify(Consts.NOTIFICATION_ICON, Notifyer.GetIcon(this));
  }
  
  private void handleStop()
  {
    _ServiceRuning = false;
////////
    if(_AlarmManager!=null)
    {
      Intent intent = new Intent(this, StatesReceiver.class);
      _AlarmManager.cancel(PendingIntent.getBroadcast(this, 0, intent, 0));
    }
////////
    if(_StopForeground!=null)
    {
      Object[] args = new Object[1];
      args[0] = Boolean.TRUE;
      
      try
      {
        _StopForeground.invoke(this, args);
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
      return;
    }
//////
    _NotifyManager.cancel(Consts.NOTIFICATION_ICON);
    setForeground(false);
  }
  
  @Override
  public IBinder onBind(Intent arg0)
  {
    return(null);
  }
  
  public static boolean Executed()
  {
    return(_ServiceRuning);
  }
}
