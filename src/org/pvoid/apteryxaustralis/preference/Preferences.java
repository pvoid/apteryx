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

package org.pvoid.apteryxaustralis.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences
{
  private static final String APTERYX_PREFS = "apteryx-settings";
  private static final String PREF_INTERVAL = "apteryx.update-interval";
  private static final String PREF_AUTOCHECK = "apteryx.autoupdate";
  private static final String PREF_USEVIBRO = "apteryx.usevibro";
  private static final String PREF_SOUND = "apteryx.sound";
  private static final String PREF_GET_PAYMENTS = "apteryx.payments";
  private static final String PREF_ACTIVITY_TIMEOUT = "apteryx.activity-timeout";
  private static final String PREF_PAYMENT_TIMEOUT = "apteryx.payment-timeout";

  private static final int DEFAULT_INTERVAL = 3600000;
  private static final int DEFAULT_ACTIVITY_TIMEOUT = 2400000;
  private static final int DEFAULT_PAYMENT_TIMEOUT = 1800000;

  private static final int VALUE_UNKNOWN = -1;
  private static final int VALUE_TRUE = 1;
  private static final int VALUE_FALSE = 0;

  private static int _mReceivePayments = VALUE_UNKNOWN;
  private static int _mAutoUpdate = VALUE_UNKNOWN;
  private static int _mUpdateInterval = VALUE_UNKNOWN;
  private static int _mUseVibration = VALUE_UNKNOWN;
  private static String _mSound = null;
  private static int _mActivityTimeout = VALUE_UNKNOWN;
  private static int _mPaymentTimeout = VALUE_UNKNOWN;

  static public boolean getReceivePayments(Context context)
  {
    if(_mReceivePayments==VALUE_UNKNOWN)
    {
      SharedPreferences preferences = context.getSharedPreferences(APTERYX_PREFS,Context.MODE_PRIVATE);
      if(preferences.getBoolean(PREF_GET_PAYMENTS,false))
        _mReceivePayments = VALUE_TRUE;
      else
        _mReceivePayments = VALUE_FALSE;
    }
    return _mReceivePayments==VALUE_TRUE;
  }

  static public void setReceivePayments(Context context, boolean receivePayments)
  {
    if(receivePayments)
      _mReceivePayments = VALUE_TRUE;
    else
      _mReceivePayments = VALUE_FALSE;
////////
    SharedPreferences preferences = context.getSharedPreferences(APTERYX_PREFS,Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putBoolean(PREF_GET_PAYMENTS,receivePayments);
    editor.commit();
  }

  static public boolean getAutoUpdate(Context context)
  {
    if(_mAutoUpdate==VALUE_UNKNOWN)
    {
      SharedPreferences preferences = context.getSharedPreferences(APTERYX_PREFS,Context.MODE_PRIVATE);
      if(preferences.getBoolean(PREF_AUTOCHECK,false))
        _mAutoUpdate = VALUE_TRUE;
      else
        _mAutoUpdate = VALUE_FALSE;
    }
    return _mAutoUpdate==VALUE_TRUE;
  }

  static public void setAutoUpdate(Context context, boolean autoUpdate)
  {
    if(autoUpdate)
      _mAutoUpdate = VALUE_TRUE;
    else
      _mAutoUpdate = VALUE_FALSE;
////////
    SharedPreferences preferences = context.getSharedPreferences(APTERYX_PREFS,Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putBoolean(PREF_AUTOCHECK,autoUpdate);
    editor.commit();
  }

  static public int getUpdateInterval(Context context)
  {
    if(_mUpdateInterval==VALUE_UNKNOWN)
    {
      SharedPreferences preferences = context.getSharedPreferences(APTERYX_PREFS,Context.MODE_PRIVATE);
      _mUpdateInterval = preferences.getInt(PREF_INTERVAL,DEFAULT_INTERVAL);
    }
    return _mUpdateInterval;
  }

  static public void setUpdateInterval(Context context, int interval)
  {
    _mUpdateInterval = interval;
    SharedPreferences preferences = context.getSharedPreferences(APTERYX_PREFS,Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putInt(PREF_INTERVAL,interval);
    editor.commit();
  }

  static public boolean getUseVibration(Context context)
  {
    if(_mAutoUpdate==VALUE_UNKNOWN)
    {
      SharedPreferences preferences = context.getSharedPreferences(APTERYX_PREFS,Context.MODE_PRIVATE);
      if(preferences.getBoolean(PREF_USEVIBRO,false))
        _mUseVibration = VALUE_TRUE;
      else
        _mUseVibration = VALUE_FALSE;
    }
    return _mUseVibration==VALUE_TRUE;
  }

  static public void setUseVibration(Context context, boolean useVibration)
  {
    if(useVibration)
      _mUseVibration = VALUE_TRUE;
    else
      _mUseVibration = VALUE_FALSE;
////////
    SharedPreferences preferences = context.getSharedPreferences(APTERYX_PREFS,Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putBoolean(PREF_USEVIBRO,useVibration);
    editor.commit();
  }

  static public String getSound(Context context)
  {
    if(_mSound == null)
    {
      SharedPreferences preferences = context.getSharedPreferences(APTERYX_PREFS,Context.MODE_PRIVATE);
      _mSound = preferences.getString(PREF_SOUND,"");
    }
    return _mSound;
  }

  static public void setSound(Context context, String soundUrl)
  {
    _mSound = soundUrl;
////////
    SharedPreferences preferences = context.getSharedPreferences(APTERYX_PREFS,Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(PREF_SOUND,soundUrl);
    editor.commit();
  }

  static public int getActivityTimeout(Context context)
  {
    if(_mActivityTimeout==VALUE_UNKNOWN)
    {
      SharedPreferences preferences = context.getSharedPreferences(APTERYX_PREFS,Context.MODE_PRIVATE);
      _mActivityTimeout = preferences.getInt(PREF_ACTIVITY_TIMEOUT,DEFAULT_ACTIVITY_TIMEOUT);
    }
    return _mActivityTimeout;
  }

  static public void setActivityTimeout(Context context, int timeout)
  {
    _mActivityTimeout = timeout;
////////
    SharedPreferences preferences = context.getSharedPreferences(APTERYX_PREFS,Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putInt(PREF_ACTIVITY_TIMEOUT,timeout);
    editor.commit();
  }

  static public int getPaymentTimeout(Context context)
  {
    if(_mPaymentTimeout==VALUE_UNKNOWN)
    {
      SharedPreferences preferences = context.getSharedPreferences(APTERYX_PREFS,Context.MODE_PRIVATE);
      _mPaymentTimeout = preferences.getInt(PREF_PAYMENT_TIMEOUT,DEFAULT_PAYMENT_TIMEOUT);
    }
    return _mPaymentTimeout;
  }

  static public void setPaymentTimeout(Context context, int timeout)
  {
    _mPaymentTimeout = timeout;
////////
    SharedPreferences preferences = context.getSharedPreferences(APTERYX_PREFS,Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putInt(PREF_PAYMENT_TIMEOUT,timeout);
    editor.commit();
  }
}
