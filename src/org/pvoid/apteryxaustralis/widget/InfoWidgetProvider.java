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

package org.pvoid.apteryxaustralis.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.ui.MainActivity;

public class InfoWidgetProvider extends AppWidgetProvider
{
  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
  {
    for(int index=appWidgetIds.length-1;index>=0;--index)
    {
      int id = appWidgetIds[index];
      /*Intent intent = new Intent(context, MainActivity.class);
      PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);*/
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.info_widget);
      //views.setOnClickPendingIntent(R.id.button, pendingIntent);
      appWidgetManager.updateAppWidget(id, views);
    }
  }
}
