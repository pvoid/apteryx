package org.pvoid.apteryxaustralis;

import java.util.ArrayList;
import java.util.TreeMap;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;
import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.TerminalStatus;
import org.pvoid.apteryxaustralis.net.StatusRefreshRunnable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.pvoid.apteryxaustralis.storage.Storage;

public class StatesReceiver extends BroadcastReceiver
{
	private Context _mContext;
	
	private final Runnable _RefreshStates = new Runnable()
	{
		@Override
		public void run()
		{
      TreeMap<Long,TerminalStatus> statusMap = new TreeMap<Long,TerminalStatus>();
      Iterable<TerminalStatus> stats = Storage.getStatuses(_mContext);
      for(TerminalStatus status : stats)
      {
        statusMap.put(status.getId(),status);
      }
///////////
      Iterable<Account> accounts = Storage.getAccounts(_mContext);
      if(accounts!=null)
      {
        boolean refreshed = false;
        boolean changed = false;
        boolean newTerminals = false;
///////////
        ArrayList<TerminalStatus> invalidStates = new ArrayList<TerminalStatus>();

        for(Account account : accounts)
        {
          Iterable<TerminalStatus> statuses = StatusRefreshRunnable.GetStatuses(account.getLogin(), account.getPassword(), Long.toString(account.getTerminalId()));
          if(statuses!=null)
          {
            for(TerminalStatus status : statuses)
            {
              if(statusMap.containsKey(status.getId()))
              {
                TerminalStatus oldStatus = statusMap.get(status.getId());
                //TODO: Тут проверка смены состояния
                changed = true;
                invalidStates.add(status);
              }
              else
              {
                newTerminals = true;
              }
//////////////////////
              Storage.updateStatus(_mContext,status);
            }
            refreshed = true;
          }
        }
/////////// Запрос на терминалы
        if(newTerminals)
        {

        }
///////////
        if(refreshed)
        {
          Intent broadcastIntent = new Intent(Consts.REFRESH_BROADCAST_MESSAGE);
          _mContext.sendBroadcast(broadcastIntent);
        }

        if(changed)
          Notifyer.ShowNotification(_mContext);
      }
///////////
	    SharedPreferences prefs = _mContext.getSharedPreferences(Consts.APTERYX_PREFS, Context.MODE_PRIVATE);
	    long interval = prefs.getInt(Consts.PREF_INTERVAL, 0);
	    if(interval==0)
	      return;
	    AlarmManager alarmManager = (AlarmManager) _mContext.getSystemService(Context.ALARM_SERVICE);
	    Intent startIntent = new Intent(_mContext,StatesReceiver.class);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(_mContext, 0, startIntent, 0);
	    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+interval,pendingIntent);
    }
	};
	
  @Override
  public void onReceive(Context context, Intent intent)
  {
  	_mContext = context;
  	(new Thread(_RefreshStates)).start();
    /*TerminalsProcessData terminals = new TerminalsProcessData();
    ArrayList<TerminalInfoOld> inactive_terminals = new ArrayList<TerminalInfoOld>();

    Accounts accounts_storage = new Accounts(context);
    ArrayList<Account> accounts = new ArrayList<Account>();
    accounts_storage.GetAccounts(accounts);
    if(accounts.size()>0)
    {
      HashMap<Long, ArrayList<Agent>> agents = new HashMap<Long, ArrayList<Agent>>();
      for(Account account : accounts)
      {
        ArrayList<Agent> agents_line = new ArrayList<Agent>();
        accounts_storage.GetAgents(account.getId(), agents_line);
        if(agents_line.size()>0)
          agents.put(account.getId(), agents_line);
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
    
    
    */
  }

}
