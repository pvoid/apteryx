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

import java.util.TreeMap;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;
import org.pvoid.apteryxaustralis.preference.Preferences;
import org.pvoid.apteryxaustralis.types.TerminalListRecord;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.pvoid.apteryxaustralis.types.TerminalStatus;
import org.pvoid.apteryxaustralis.net.Receiver;
import org.pvoid.apteryxaustralis.preference.CommonSettings;
import org.pvoid.apteryxaustralis.storage.Storage;

public class StatesReceiver extends BroadcastReceiver
{
  public static final String REFRESH_BROADCAST_MESSAGE = "org.pvoid.apteryx.StatusUpdatedMessage";
	private Context _mContext;
	
	private final Runnable _RefreshStates = new Runnable()
	{
		@Override
		public void run()
		{
      Log.d(StatesReceiver.class.getSimpleName(),"Receive task start");
      // WTF: Почему тут такой широкий ти используется?
      TreeMap<Long,TerminalListRecord> tree = new TreeMap<Long,TerminalListRecord>();
      Iterable<TerminalStatus> lastStatuses = Storage.getStatuses(_mContext);
      if(lastStatuses!=null)
        for(TerminalStatus status : lastStatuses)
          tree.put(status.getId(),new TerminalListRecord(null,status,null));
      Receiver.RefreshStates(_mContext, tree);
      if(Preferences.getReceivePayments(_mContext))
        Receiver.RefreshPayments(_mContext);

      Iterable<TerminalStatus> statuses = Storage.getStatuses(_mContext);
      if(statuses!=null)
      {
        boolean notify = false;
        for(TerminalStatus status : statuses)
        {
          long id = status.getId();
          if(tree.containsKey(id))
          {
            TerminalListRecord record = tree.get(id);
            TerminalStatus lastStatus = record.getStatus();
            if(status.getCommonState() == TerminalStatus.STATE_COMMON_ERROR &&
                lastStatus.getCommonState() != TerminalStatus.STATE_COMMON_ERROR)
            {
              notify = true;
              break;
            }
          }
          else if(status.getCommonState() == TerminalStatus.STATE_COMMON_ERROR)
          {
            notify = true;
            break;
          }
        }

        if(notify)
          Notifier.ShowNotification(_mContext,Notifier.ERROR_COMMON);
        else
          Notifier.ShowNotification(_mContext,Notifier.NO_ERROR);

        Intent broadcastIntent = new Intent(REFRESH_BROADCAST_MESSAGE);
          _mContext.sendBroadcast(broadcastIntent);
      }



      Log.d(StatesReceiver.class.getSimpleName(),"Receive task end");
///////////
	    long interval = Preferences.getUpdateInterval(_mContext);
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
  }
}
