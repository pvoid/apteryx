package org.pvoid.apteryx;

import java.util.List;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;

import org.pvoid.apteryx.accounts.Terminal;

public class Notifyer
{
  public static void Notify(Context context, List<Terminal> terminals)
  {
    NotificationManager nm = (NotificationManager)context.getSystemService(Service.NOTIFICATION_SERVICE);
    
  }
}
