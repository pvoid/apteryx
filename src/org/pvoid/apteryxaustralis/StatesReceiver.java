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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;
import org.pvoid.apteryxaustralis.net.Request;
import org.pvoid.apteryxaustralis.preference.Preferences;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpStorage;

public class StatesReceiver extends BroadcastReceiver
{
  public static final String REFRESH_BROADCAST_MESSAGE = "org.pvoid.apteryx.StatusUpdatedMessage";

  public static int refreshData(Context context)
  {
    int result = -1;
    OsmpStorage storage = new OsmpStorage(context);
    /*ArrayList<Account> accounts = new ArrayList<Account>();
    //---
    storage.getAccounts(accounts);
    if(accounts.size()>0)
    {
      Hashtable<Long,Integer> states = new Hashtable<Long,Integer>();
      for(Account account : accounts)
      {
        int state = storage.updateAccount(account,states);
        if(state>result)
          result = state;
      }

      States statesStorage = new States(context);
      statesStorage.updateGroupsStates(states);
    }*/

    return result;
  }

  private static class ReceiveThread extends Thread
  {
    private Context _mContext;
    PowerManager.WakeLock _mWakeLock;

    public ReceiveThread(Context context)
    {
      _mContext = context;
      PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
      _mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Apteryx states receiver wake lock");
      _mWakeLock.acquire();
    }

    @Override
    public void run()
    {
      try
      {
        int warn = refreshData(_mContext);
        Intent broadcastIntent = new Intent(REFRESH_BROADCAST_MESSAGE);
        _mContext.sendBroadcast(broadcastIntent);

        if(warn>0 && warn>=Preferences.getWarnLevel(_mContext))
        {
          Notifier.showNotification(_mContext,warn);
        }
        else if(warn== Request.STATE_OK)
          Notifier.hideNotification(_mContext,false);

        long interval = Preferences.getUpdateInterval(_mContext);
        if(interval==0)
          return;
        AlarmManager alarmManager = (AlarmManager)_mContext.getSystemService(Context.ALARM_SERVICE);
        Intent startIntent = new Intent(_mContext,StatesReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(_mContext, 0, startIntent, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+interval,pendingIntent);
      }
      finally
      {
        _mWakeLock.release();
      }
    }
  }

  @Override
  public void onReceive(Context context, Intent intent)
  {
    (new ReceiveThread(context)).start();
  }

}
