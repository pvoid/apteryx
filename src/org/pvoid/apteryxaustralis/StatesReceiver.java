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

import java.util.ArrayList;

import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.preference.Preferences;
import org.pvoid.apteryxaustralis.storage.IStorage;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpStorage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class StatesReceiver extends BroadcastReceiver
{
  @Override
  public void onReceive(Context context, Intent intent)
  {
    OsmpStorage storage = new OsmpStorage(context);
    ArrayList<Account> accounts = new ArrayList<Account>();
    boolean warn = false;
    storage.getAccounts(accounts);
    if(accounts.size()>0)
    {
      for(Account account : accounts)
      {
        switch(storage.updateAccount(account))
        {
          case IStorage.RES_OK:
            break;
          case IStorage.RES_OK_TERMINAL_ALARM:
            warn = true;
            break;
        }
      }
    }

    Intent broadcastIntent = new Intent(Consts.REFRESH_BROADCAST_MESSAGE);
    context.sendBroadcast(broadcastIntent);

    if(warn)
      Notifyer.ShowNotification(context, null);

    long interval = Preferences.getUpdateInterval(context);
    if(interval==0)
      return;
    AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    Intent startIntent = new Intent(context,StatesReceiver.class);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, startIntent, 0);
    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+interval,pendingIntent);

    /*TerminalsProcessData terminals = new TerminalsProcessData();
    ArrayList<Terminal> inactive_terminals = new ArrayList<Terminal>();

    OsmpStorage storage = new OsmpStorage(context);
    ArrayList<Account> accounts = new ArrayList<Account>();
    storage.getAccounts(accounts);
    if(accounts.size()>0)
    {
      HashMap<Long, ArrayList<Group>> agents = new HashMap<Long, ArrayList<Group>>();
      for(Account account : accounts)
      {
        ArrayList<Group> agents_line = new ArrayList<Group>();
        storage.getAgents(account.id, agents_line);
        if(agents_line.size()>0)
          agents.put(account.id, agents_line);
      }
      Account[] ac = new Account[accounts.size()];
      StatesRequestWorker worker = new StatesRequestWorker(terminals,agents);
      if(worker.Work(accounts.toArray(ac)))
      {
        if(terminals.Success())
        {
          if(storage.CheckStates(terminals, inactive_terminals))
            Notifyer.ShowNotification(context, inactive_terminals);
          storage.saveTerminals(terminals);
        }
        else
          Toast.makeText(context, context.getString(ErrorCodes.Message(terminals.Status())), Toast.LENGTH_LONG);
      }
    }*/
  }

}
