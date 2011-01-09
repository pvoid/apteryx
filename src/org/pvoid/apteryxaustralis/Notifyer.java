package org.pvoid.apteryxaustralis;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

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
        _Notification = new Notification(R.drawable.ic_terminal_active,context.getText(R.string.service_starte),System.currentTimeMillis());
        /*RemoteViews view = new RemoteViews(context.getPackageName(),R.layout.notify);
        view.setTextViewText(R.id.notify_text,context.getText(R.string.update_service));*/
        _Notification.setLatestEventInfo(context,
                                         context.getText(R.string.app_name),
                                         context.getText(R.string.update_service),
                                         PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class) , 0));
        /*_Notification.setLatestEventInfo(context, context.getText(R.string.app_name),"", contentIntent);*/
      }
      
      return(_Notification);
    }
  }
  
  public static void ShowNotification(Context context)
  {
    Notification notification = GetIcon(context);
    notification.icon = R.drawable.ic_terminal_inactive;
    notification.tickerText = context.getText(R.string.terminals_errors);
    notification.contentIntent  = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
    SharedPreferences preferences = context.getSharedPreferences(Consts.APTERYX_PREFS, Context.MODE_PRIVATE);
    
    if(preferences.getBoolean(Consts.PREF_USEVIBRO, false))
      notification.defaults |= Notification.DEFAULT_VIBRATE;
    
    String sound = preferences.getString(Consts.PREF_SOUND, "");
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
      notification.icon = R.drawable.ic_terminal_active;
      RemoteViews view = notification.contentView;
      view.setTextViewText(R.id.notify_text, context.getText(R.string.update_service));
      view.setImageViewResource(R.id.notify_icon, R.drawable.ic_terminal_active);
      nm.notify(Consts.NOTIFICATION_ICON, notification);
    }
    else
      nm.cancel(Consts.NOTIFICATION_ICON);
  }
}
