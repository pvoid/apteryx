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

import android.content.Context;
import android.text.format.DateFormat;

import java.util.Calendar;

public class TextFormat
{
  public static String formatDateSmart(Context context, long time)
  {
    Calendar current = Calendar.getInstance();
    Calendar date = Calendar.getInstance();
    date.setTimeInMillis(time);
    int dayToday = current.get(Calendar.DAY_OF_YEAR);
    int day = date.get(Calendar.DAY_OF_YEAR);
    if(day == dayToday)
      return DateFormat.format("kk:mm", time).toString();
    StringBuilder result = new StringBuilder();
    if(dayToday - day == 1)
      result.append(context.getString(R.string.yesterday));
    else if(dayToday - day == -1)
      result.append(context.getString(R.string.tomorrow));
    else
    {
      if(current.get(Calendar.YEAR)==date.get(Calendar.YEAR))
        result.append("dd MMM");
      else
        result.append("dd MMM yyyy");
    }
    result.append(" kk:mm");
    return DateFormat.format(result.toString(), time).toString();
  }

  public static String formatMoney(double money, boolean uint)
  {
    StringBuilder result = new StringBuilder();
    int index;
    if(!uint)
    {
      result.append(String.format("%1$.2f",money));
      index = result.length() - 6;
    }
    else
    {
      result.append(String.format("%1$.0f",money));
      index = result.length() - 3;
    }
    while(index>0)
    {
      result.insert(index,' ');
      index-=4;
    }
    return result.toString();
  }
}
