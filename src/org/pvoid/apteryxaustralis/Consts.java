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

import android.app.Activity;

public class Consts
{
  public static final String APTERYX_PREFS = "apteryx-settings";
  public static final String PREF_INTERVAL = "apteryx.update-interval";
  public static final String PREF_AUTOCHECK = "apteryx.autoupdate";
  public static final String PREF_USEVIBRO = "apteryx.usevibro";
  public static final String PREF_SOUND = "apteryx.sound";
  public static final String PREF_LASTUPDATE = "apteryx.lastupdate";
  public static final String URL = "http://xml1.osmp.ru/term2/xml.jsp";
  
  public static final String TAB_ACCOUNTS = "tab_account";
  public static final String TAB_PREFERENCES = "tab_preferences";
  
  public static final int RESULT_RELOAD = Activity.RESULT_FIRST_USER + 1;
  
  public static final int ACTIVITY_ADD_ACCOUNT = 1;
  public static final int ACTIVITY_EDIT_ACCOUNT = 2;


  public static final int MENU_DELETE = 1;
  public static final int MENU_EDIT = 2;
  
  public static final int[] INTERVALS =      new int[] {900000,1800000,3600000,10800000,21600000,43200000,86400000};
  public static final int   DEFAULT_INTERVAL = 2;
  
  public static final int NOTIFICATION_ICON = 1;
  
  public static final String EXTRA_AGENTS = "agents";
  public static final String EXTRA_SELECTED_AGENT = "selected_agent";
  public static final String EXTRA_SELECTED_AGENTS = "selected_agents";
  
  public static final String REFRESH_BROADCAST_MESSAGE = "org.pvoid.apteryx.StatusUpdatedMessage";
}
