package org.pvoid.apteryx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class OnBootReceiver extends BroadcastReceiver
{

  @Override
  public void onReceive(Context context, Intent intent)
  {
    if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()))
    {
      SharedPreferences prefs = context.getSharedPreferences(Consts.APTERYX_PREFS, Context.MODE_PRIVATE);
      if(prefs.getBoolean(Consts.PREF_AUTOCHECK, false))
      {
        Intent serviceIntent = new Intent(context, UpdateStatusService.class);
        context.startService(serviceIntent);
      }
    }
  }

}
