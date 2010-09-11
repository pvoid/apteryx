package org.pvoid.apteryx;

import java.util.ArrayList;

import org.pvoid.apteryx.accounts.Account;
import org.pvoid.apteryx.accounts.Accounts;
import org.pvoid.apteryx.accounts.Terminal;
import org.pvoid.apteryx.net.ErrorCodes;
import org.pvoid.apteryx.net.StatesRequestWorker;
import org.pvoid.apteryx.net.TerminalsProcessData;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.widget.Toast;

public class StatesReceiver extends BroadcastReceiver
{
  @Override
  public void onReceive(Context context, Intent intent)
  {
    TerminalsProcessData terminals = new TerminalsProcessData();
    ArrayList<Terminal> inactive_terminals = new ArrayList<Terminal>();

    Accounts accounts_storage = new Accounts(context);
    ArrayList<Account> accounts = new ArrayList<Account>();
    accounts_storage.GetAccounts(accounts);
    Account[] ac = new Account[accounts.size()];
    StatesRequestWorker worker = new StatesRequestWorker(terminals);
    if(worker.Work(accounts.toArray(ac)))
    {
      if(terminals.Success())
      {
        if(accounts_storage.CheckStates(terminals, inactive_terminals))
          Notifyer.ShowNotification(context, inactive_terminals);
        accounts_storage.SaveStates(terminals);
      }
      else
        Toast.makeText(context, context.getString(ErrorCodes.Message(terminals.Status())), Toast.LENGTH_LONG);
    }
    
    SharedPreferences prefs = context.getSharedPreferences(Consts.APTERYX_PREFS, Context.MODE_PRIVATE);
    long interval = prefs.getInt(Consts.PREF_INTERVAL, 0);
    if(interval==0)
      return;
    AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    Intent startIntent = new Intent(context,StatesReceiver.class);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, startIntent, 0);
    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+interval,pendingIntent);
  }

}
