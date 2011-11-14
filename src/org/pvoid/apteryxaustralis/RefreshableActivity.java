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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import org.pvoid.apteryxaustralis.net.ContentLoader;
import org.pvoid.apteryxaustralis.net.osmp.OsmpRequest;
import org.pvoid.apteryxaustralis.preference.CommonSettings;
import org.pvoid.apteryxaustralis.storage.AccountsProvider;

public class RefreshableActivity extends FragmentActivity
{
  private final static int MENU_REFRESH  = 0;
  private final static int MENU_SETTINGS = 1;
  private final static int ANIMATION_INTERVAL = 100;

  private LevelListDrawable _mProgressDrawable = null;
  private final Handler _mHandler = new Handler();
  private final Runnable    _mProgressRunnable = new Runnable()
  {
    @Override
    public void run()
    {
      if(_mProgressDrawable==null)
        return;
      ///////
      int level = (_mProgressDrawable.getLevel() + 1) % 6;
      _mProgressDrawable.setLevel(level);
      _mHandler.postDelayed(this,ANIMATION_INTERVAL);
    }
  };

  private final BroadcastReceiver _mLoadingStateReceiver = new BroadcastReceiver()
  {
    @Override
    public void onReceive(Context context, Intent intent)
    {
      final String action = intent.getAction();
      if(ContentLoader.LOADING_STARTED.equals(action))
      {
        showRefreshProgress(true);
      }
      else if(ContentLoader.LOADING_FINISHED.equals(action))
      {
        showRefreshProgress(false);
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
////////
    IntentFilter filter = new IntentFilter(ContentLoader.LOADING_STARTED);
    filter.addAction(ContentLoader.LOADING_FINISHED);
    registerReceiver(_mLoadingStateReceiver,filter);

  }

  @Override
  protected void onDestroy()
  {
    super.onDestroy();
    unregisterReceiver(_mLoadingStateReceiver);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuItem item = menu.add(Menu.NONE, MENU_REFRESH, Menu.FIRST, R.string.refresh);
    _mProgressDrawable = (LevelListDrawable) getResources().getDrawable(R.drawable.ic_menu_refresh);
    item.setIcon(_mProgressDrawable);
    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    item = menu.add(Menu.NONE, MENU_SETTINGS, Menu.FIRST, R.string.settings);
    item.setIcon(android.R.drawable.ic_menu_preferences);
////////
    showRefreshProgress(ContentLoader.isLoading());
////////
    return super.onCreateOptionsMenu(menu);
  }

  protected void showRefreshProgress(boolean inProgress)
  {
    if(_mProgressDrawable==null)
      return;
////////
    _mHandler.removeCallbacks(_mProgressRunnable);
    _mProgressDrawable.setLevel(0);
////////
    if(inProgress)
      _mHandler.postDelayed(_mProgressRunnable,ANIMATION_INTERVAL);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case MENU_REFRESH:
        refreshInfo();
        return true;
      case MENU_SETTINGS:
        startActivity(new Intent(this, CommonSettings.class));
    }
    return super.onOptionsItemSelected(item);
  }

  protected boolean getAccountData(long accountId, Bundle bundle)
  {
    Cursor cursor = managedQuery(AccountsProvider.Accounts.CONTENT_URI,
                                 new String[] {AccountsProvider.Accounts.COLUMN_LOGIN,
                                               AccountsProvider.Accounts.COLUMN_PASSWORD,
                                               AccountsProvider.Accounts.COLUMN_CUSTOM1},
                                 AccountsProvider.Accounts.COLUMN_ID+"=?",new String[] {Long.toString(accountId)},
                                 null);
    try
    {
      if(cursor.moveToFirst())
      {
        bundle.putString(OsmpRequest.ACCOUNT_ID,Long.toString(accountId));
        bundle.putString(OsmpRequest.LOGIN,cursor.getString(0));
        bundle.putString(OsmpRequest.PASSWORD,cursor.getString(1));
        bundle.putString(OsmpRequest.TERMINAL,cursor.getString(2));
        return true;
      }
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
    return false;
  }

  protected void refreshInfo()
  {
    ContentLoader.refresh(this);
  }
}
