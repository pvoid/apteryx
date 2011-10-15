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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;

public class TextFormat
{
  private static DecimalFormat _sNumberFormat = (DecimalFormat) NumberFormat.getNumberInstance();
  static
  {
    DecimalFormatSymbols symbols = _sNumberFormat.getDecimalFormatSymbols();
    symbols.setDecimalSeparator('.');
    symbols.setGroupingSeparator(' ');
    _sNumberFormat.setDecimalFormatSymbols(symbols);
  }

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
    if(!uint)
    {
      _sNumberFormat.setMaximumFractionDigits(2);
      _sNumberFormat.setMinimumFractionDigits(2);
    }
    else
    {
      _sNumberFormat.setMaximumFractionDigits(0);
      _sNumberFormat.setMinimumFractionDigits(0);
    }
    return _sNumberFormat.format(money);
  }
}
