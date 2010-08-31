package org.pvoid.apteryx;

import java.util.ArrayList;

import org.pvoid.apteryx.net.StatesRequestWorker;
import org.pvoid.apteryx.net.TerminalsProcessData;
import org.pvoid.apteryx.accounts.Account;
import org.pvoid.apteryx.accounts.Accounts;
import org.pvoid.apteryx.accounts.Terminal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;

public class UpdateStatusService extends Service
{
  private Runnable _Task = new Runnable()
  {
    @Override
    public void run()
    {
      TerminalsProcessData terminals = new TerminalsProcessData();
      ArrayList<Terminal> inactive_terminals = new ArrayList<Terminal>();

      Accounts accounts_storage = new Accounts(UpdateStatusService.this);
      ArrayList<Account> accounts = new ArrayList<Account>();
      accounts_storage.GetAccounts(accounts);
      Account[] ac = new Account[accounts.size()];
      StatesRequestWorker worker = new StatesRequestWorker(terminals);
      if(worker.Work(accounts.toArray(ac)))
      {
        if(accounts_storage.CheckStates(terminals, inactive_terminals))
          Notifyer.ShowNotification(UpdateStatusService.this, inactive_terminals);
      }
      ShceduleCheck(UpdateStatusService.this);
      UpdateStatusService.this.stopSelf();
    }
  };
  
  @Override
  public void onCreate()
  {
    Thread t = new Thread(null,_Task,"TestServiceThread");
    t.start();
  }
  
  private final IBinder _Binder = new Binder() 
  {
    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply,int flags) 
              throws RemoteException 
    {
      return super.onTransact(code, data, reply, flags);
    }
  };
  
  @Override
  public IBinder onBind(Intent arg0)
  {
    return(_Binder);
  }
  
  public static void ShceduleCheck(Context context)
  {
    long interval;
    SharedPreferences prefs = context.getSharedPreferences(Consts.APTERYX_PREFS, Context.MODE_PRIVATE);
    interval = prefs.getInt(Consts.PREF_INTERVAL, 0);
    if(interval==0)
      return;
    
    PendingIntent intent = PendingIntent.getService(context, 0, new Intent(context,UpdateStatusService.class), 0);
    AlarmManager amanager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
    amanager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+interval,intent);
  }
  
  public static void StopChecking(Context context)
  {
    PendingIntent intent = PendingIntent.getService(context, 0, new Intent(context,UpdateStatusService.class), 0);
    AlarmManager amanager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
    amanager.cancel(intent);
  }
}
