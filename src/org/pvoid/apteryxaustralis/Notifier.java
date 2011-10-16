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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.RemoteViews;

import org.pvoid.apteryxaustralis.net.Request;
import org.pvoid.apteryxaustralis.preference.Preferences;
import org.pvoid.apteryxaustralis.ui.MainActivity;

public class Notifier
{
  public static final int NOTIFICATION_ICON = 1;

  private static final Object _mLocker = new Object();
  private static Notification _mNotification;

  public static Notification getIcon(Context context)
  {
    synchronized (_mLocker)
    {
      if(_mNotification ==null)
      {
        _mNotification = new Notification(R.drawable.ic_terminal_active,context.getText(R.string.service_starte),System.currentTimeMillis());
        _mNotification.setLatestEventInfo(context,
                                         context.getText(R.string.app_name),
                                         context.getText(R.string.update_service),
                                         PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class) , 0));
        _mNotification.flags|=Notification.FLAG_NO_CLEAR/* | Notification.FLAG_FOREGROUND_SERVICE*/;
      }

      return(_mNotification);
    }
  }
  
  public static void showNotification(Context context, int level)
  {
    Notification notification = getIcon(context);
    switch(level)
    {
      case Request.STATE_WARNING:
        notification.icon = R.drawable.ic_terminal_pending;
        break;
      case Request.STATE_ERROR:
        notification.icon = R.drawable.ic_terminal_printer_error;
        break;
      case Request.STATE_ERROR_CRITICAL:
        notification.icon = R.drawable.ic_terminal_inactive;
        break;
    }
    notification.tickerText = context.getText(R.string.terminals_errors);

    if(Preferences.getUseVibration(context))
      notification.defaults |= Notification.DEFAULT_VIBRATE;

    String sound = Preferences.getSound(context);
    if(!TextUtils.isEmpty(sound))
    {
      notification.sound = Uri.parse(sound);
    }

    notification.when = System.currentTimeMillis();
    _mNotification.setLatestEventInfo(context,
                                      context.getText(R.string.app_name),
                                      context.getText(R.string.terminals_errors),
                                      PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class) , 0));
    NotificationManager nm = (NotificationManager)context.getSystemService(Service.NOTIFICATION_SERVICE);
    nm.notify(NOTIFICATION_ICON, notification);
  }
  
  public static void hideNotification(Context context, boolean checkService)
  {
    NotificationManager nm = (NotificationManager)context.getSystemService(Service.NOTIFICATION_SERVICE);
    if(UpdateStatusService.Executed() || !checkService)
    {
      Notification notification = getIcon(context);
      notification.icon = R.drawable.ic_terminal_active;
      RemoteViews view = notification.contentView;
      view.setTextViewText(R.id.notify_text, context.getText(R.string.update_service));
      view.setImageViewResource(R.id.notify_icon, R.drawable.ic_terminal_active);

      _mNotification.setLatestEventInfo(context,
                                      context.getText(R.string.app_name),
                                      context.getText(R.string.update_service),
                                      PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class) , 0));

      nm.notify(NOTIFICATION_ICON, notification);
    }
    else
      nm.cancel(NOTIFICATION_ICON);
  }
}
