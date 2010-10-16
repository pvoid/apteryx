package org.pvoid.apteryxaustralis;

import java.util.ArrayList;
import java.util.HashMap;

import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.Accounts;
import org.pvoid.apteryxaustralis.accounts.Agent;
import org.pvoid.apteryxaustralis.accounts.Terminal;
import org.pvoid.apteryxaustralis.net.ErrorCodes;
import org.pvoid.apteryxaustralis.net.StatesRequestWorker;
import org.pvoid.apteryxaustralis.net.TerminalsProcessData;

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
    if(accounts.size()>0)
    {
      HashMap<Long, ArrayList<Agent>> agents = new HashMap<Long, ArrayList<Agent>>();
      for(Account account : accounts)
      {
        ArrayList<Agent> agents_line = new ArrayList<Agent>();
        accounts_storage.GetAgents(account.Id(), agents_line);
        if(agents_line.size()>0)
          agents.put(account.Id(), agents_line);
      }
      Account[] ac = new Account[accounts.size()];
      StatesRequestWorker worker = new StatesRequestWorker(terminals,agents);
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
    }
    
    Intent broadcastIntent = new Intent(Consts.REFRESH_BROADCAST_MESSAGE);
    context.sendBroadcast(broadcastIntent);
    
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
