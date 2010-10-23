package org.pvoid.apteryxaustralis;

import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.Html;
import android.widget.RemoteViews;

import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.accounts.TerminalInfoOld;
import org.pvoid.apteryxaustralis.ui.MainActivity;

public class Notifyer
{
  private static final Object _Locker = new Object();
  private static Notification _Notification; 
  
  public static Notification GetIcon(Context context)
  {
    synchronized (_Locker)
    {
      if(_Notification==null)
      {
        _Notification = new Notification(R.drawable.terminal_active,context.getText(R.string.service_starte),System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class) , 0);
        _Notification.contentIntent  = contentIntent;
        RemoteViews view = new RemoteViews(context.getPackageName(),R.layout.notify);
        view.setTextViewText(R.id.notify_text,context.getText(R.string.update_service));
        _Notification.contentView = view;
        /*_Notification.setLatestEventInfo(context, context.getText(R.string.app_name),"", contentIntent);*/
      }
      
      return(_Notification);
    }
  }
  
  public static void ShowNotification(Context context, List<TerminalInfoOld> terminals)
  {
    Notification notification = GetIcon(context);
    notification.icon = R.drawable.terminal_inactive;
    
    StringBuilder bulder = new StringBuilder(2000);
    for(TerminalInfoOld terminal : terminals)
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
    
    RemoteViews view = notification.contentView;
    view.setTextViewText(R.id.notify_text, Html.fromHtml(bulder.toString()));
    view.setImageViewResource(R.id.notify_icon, R.drawable.terminal_inactive);
    
    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class) , 0);
    notification.contentIntent  = contentIntent;
    SharedPreferences prefs = context.getSharedPreferences(Consts.APTERYX_PREFS, Context.MODE_PRIVATE);
    
    if(prefs.getBoolean(Consts.PREF_USEVIBRO, false))
      notification.defaults |= Notification.DEFAULT_VIBRATE;
    
    String sound = prefs.getString(Consts.PREF_SOUND, "");
    if(!Utils.isEmptyString(sound))
    {
      notification.sound = Uri.parse(sound);
    }
    
    NotificationManager nm = (NotificationManager)context.getSystemService(Service.NOTIFICATION_SERVICE);
    nm.notify(Consts.NOTIFICATION_ICON, notification);
  }
  
  public static void HideNotification(Context context)
  {
    NotificationManager nm = (NotificationManager)context.getSystemService(Service.NOTIFICATION_SERVICE);
    if(UpdateStatusService.Executed())
    {
      Notification notification = GetIcon(context);
      notification.icon = R.drawable.terminal_active;
      RemoteViews view = notification.contentView;
      view.setTextViewText(R.id.notify_text, context.getText(R.string.update_service));
      view.setImageViewResource(R.id.notify_icon, R.drawable.terminal_active);
      nm.notify(Consts.NOTIFICATION_ICON, notification);
    }
    else
      nm.cancel(Consts.NOTIFICATION_ICON);
  }
}
