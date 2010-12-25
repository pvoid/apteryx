package org.pvoid.apteryxaustralis;

import java.util.List;

import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.TerminalStatus;
import org.pvoid.apteryxaustralis.net.StatusRefreshTask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;

public class StatesReceiver extends BroadcastReceiver
{
	private Context _Context;
	
	private Runnable _RefreshStates = new Runnable()
	{
		@Override
		public void run()
		{
			boolean refreshed = false;/*
///////////
			for(Account account : AccountsStorage.Instance())
			{
				List<TerminalStatus> statuses = StatusRefreshTask.GetStatuses(account.getLogin(), account.getPassword(), Long.toString(account.getTerminalId()));
				if(statuses!=null)
				{
					TerminalsStatusesStorage.Instance().UpdateStatuses(statuses);
					refreshed = true;
					//TODO: Проверит что что-то ушло в офф
				}
			}*/
///////////
			if(refreshed)
			{
				Intent broadcastIntent = new Intent(Consts.REFRESH_BROADCAST_MESSAGE);
				_Context.sendBroadcast(broadcastIntent);
			}
///////////
	    SharedPreferences prefs = _Context.getSharedPreferences(Consts.APTERYX_PREFS, Context.MODE_PRIVATE);
	    long interval = prefs.getInt(Consts.PREF_INTERVAL, 0);
	    if(interval==0)
	      return;
	    AlarmManager alarmManager = (AlarmManager)_Context.getSystemService(Context.ALARM_SERVICE);
	    Intent startIntent = new Intent(_Context,StatesReceiver.class);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(_Context, 0, startIntent, 0);
	    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+interval,pendingIntent);
		}
	};
	
  @Override
  public void onReceive(Context context, Intent intent)
  {
  	_Context = context;
  	
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
