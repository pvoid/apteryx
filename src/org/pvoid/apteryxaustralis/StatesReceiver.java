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
import android.database.Cursor;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import org.pvoid.apteryxaustralis.net.ContentLoader;
import org.pvoid.apteryxaustralis.preference.Preferences;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpContentProvider;

import java.util.HashMap;

public class StatesReceiver extends BroadcastReceiver
{
  private static final String[] PROJECTION = new String[]
                                                 {
                                                  OsmpContentProvider.Agents.COLUMN_AGENT,
                                                  OsmpContentProvider.Agents.COLUMN_STATE
                                                 };

  public static StateResult refreshData(Context context)
  {
    StateResult  result = new StateResult();
    result.result = 0;
    result.notify = false;
    final HashMap<Long, Integer> statuses = new HashMap<Long, Integer>();
    Cursor cursor = getAgentsStatuses(context);
    try
    {
      if(cursor!=null)
        while(cursor.moveToNext())
        {
          int state = cursor.getInt(1);
          if(state==OsmpContentProvider.STATE_OK)
            continue;
          //////
          statuses.put(cursor.getLong(0),state);
        }
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
/////////
    ContentLoader.checkStates(context);
/////////
    cursor = getAgentsStatuses(context);
    try
    {
      if(cursor!=null)
        while(cursor.moveToNext())
        {
          long agentId = cursor.getLong(0);
          int state = cursor.getInt(1);
          if(!statuses.containsKey(agentId))
          {
            if(state==OsmpContentProvider.STATE_OK)
              continue;
            statuses.put(agentId,state);
            result.notify = true;
          }
          //////
          int prev_state = statuses.get(agentId);
          if(prev_state!=state)
            result.notify = true;
          statuses.put(agentId,state);
        }
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
    ////// а теперь узнаем статус
    for(long agentId : statuses.keySet())
    {
      int state = statuses.get(agentId);
      if(state>result.result)
        result.result = state;
    }
    //////
    return result;
  }

  private static Cursor getAgentsStatuses(Context context)
  {
    return context.getContentResolver().query(OsmpContentProvider.Agents.CONTENT_URI,
                                              PROJECTION,
                                              OsmpContentProvider.Agents.COLUMN_STATE+"<>0 AND "+ OsmpContentProvider.Agents.COLUMN_SEEN+"=0",
                                              null,null);
  }

  private static class ReceiveThread extends Thread
  {
    private final Context _mContext;
    private final PowerManager.WakeLock _mWakeLock;

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
        StateResult state = refreshData(_mContext);
        /*Intent broadcastIntent = new Intent(REFRESH_BROADCAST_MESSAGE);
        _mContext.sendBroadcast(broadcastIntent);*/

        Log.d("APTERYX","State " + state.result + " notify: " + state.notify);
        
        if(state.result == OsmpContentProvider.STATE_OK)
          Notifier.hideNotification(_mContext,false);
        if(state.result>OsmpContentProvider.STATE_OK && state.result>=Preferences.getWarnLevel(_mContext) && state.notify)
          Notifier.showNotification(_mContext,state.result);

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

  private final static class StateResult
  {
    int     result;
    boolean notify;
  }
}
