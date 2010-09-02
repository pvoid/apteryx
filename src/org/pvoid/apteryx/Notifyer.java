package org.pvoid.apteryx;

import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.widget.RemoteViews;

import org.pvoid.apteryx.accounts.Terminal;
import org.pvoid.apteryx.ui.MainActivity;

public class Notifyer
{
  public static void ShowNotification(Context context, List<Terminal> terminals)
  {
    NotificationManager nm = (NotificationManager)context.getSystemService(Service.NOTIFICATION_SERVICE);
    
    Notification notification = new Notification(R.drawable.icon,"",System.currentTimeMillis());
    
    RemoteViews view = new RemoteViews(context.getPackageName(),R.layout.notify);
    
    StringBuilder bulder = new StringBuilder(2000);
    for(Terminal terminal : terminals)
    {
      bulder.append("<b>");
      bulder.append(terminal.Address());
      bulder.append(": </b>");
      
      if(!terminal.printer_state.equalsIgnoreCase("OK"))
        bulder.append(terminal.printer_state);
      else if(!terminal.cashbin_state.equalsIgnoreCase("OK"))
        bulder.append(terminal.cashbin_state);
      else
        bulder.append(context.getString(R.string.doesnt_response));
      bulder.append("<br>");
    }
    
    view.setTextViewText(R.id.notify_text, Html.fromHtml(bulder.toString()));
    notification.contentView = view;
    
    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class) , 0);
    notification.contentIntent  = contentIntent;
    SharedPreferences prefs = context.getSharedPreferences(Consts.APTERYX_PREFS, Context.MODE_PRIVATE);
    
    if(prefs.getBoolean(Consts.PREF_USEVIBRO, false))
      notification.defaults |= Notification.DEFAULT_VIBRATE;
    
    nm.notify(Consts.NOTIFICATION_ICON, notification);
  }
  
  public static void HideNotification(Context context)
  {
    NotificationManager nm = (NotificationManager)context.getSystemService(Service.NOTIFICATION_SERVICE);
    nm.cancelAll();
  }
}
