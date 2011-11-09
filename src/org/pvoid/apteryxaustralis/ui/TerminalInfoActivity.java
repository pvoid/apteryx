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

package org.pvoid.apteryxaustralis.ui;

import android.database.ContentObserver;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentManager;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.RefreshableActivity;
import org.pvoid.apteryxaustralis.net.Request;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpContentProvider;
import org.pvoid.apteryxaustralis.ui.fragments.TerminalInfoFragment;
import org.pvoid.apteryxaustralis.ui.fragments.TerminalsCursorAdapter;

public class TerminalInfoActivity extends RefreshableActivity implements ActionBar.OnNavigationListener
{
  TerminalsCursorAdapter     _mTerminals;
  private final Handler      _mUiHandler = new Handler();
  final TerminalInfoFragment _mFragment = new TerminalInfoFragment();
  final TerminalsObserver    _mObserver = new TerminalsObserver(_mUiHandler);
  private final Runnable _mStopRefreshRunnable = new Runnable()
  {
    @Override
    public void run()
    {
      showRefreshProgress(false);
    }
  };

  public void onCreate(Bundle savedInstanceState)
  {
    try
    {
      getWindow().setFormat(PixelFormat.RGBA_8888);
    }
    catch(Exception e)
    {
      // nope
    }
//////////
    super.onCreate(savedInstanceState);
//////////
    final long id = getIntent().getLongExtra(TerminalInfoFragment.EXTRA_TERMINAL,0);
    final FragmentManager man = getSupportFragmentManager();
    man.beginTransaction().add(android.R.id.content, _mFragment).commit();
/////////
    final ActionBar bar = getSupportActionBar();
    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    _mTerminals = new TerminalsCursorAdapter(this, null, R.layout.record_terminal_actionbar);
    bar.setListNavigationCallbacks(_mTerminals, this);
//////// Ищем выбранный терминал
    for(int index=0;index< _mTerminals.getCount();++index)
      if(_mTerminals.getItemId(index) == id)
      {
        bar.setSelectedNavigationItem(index);
        break;
      }
  }

  @Override
  protected void onPause()
  {
    super.onPause();
    getContentResolver().unregisterContentObserver(_mObserver);
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    getContentResolver().registerContentObserver(OsmpContentProvider.Terminals.CONTENT_URI,true,_mObserver);
  }

  @Override
  public boolean onNavigationItemSelected(int itemPosition, long itemId)
  {
    _mFragment.loadTerminalInfo(itemId);
    return true;
  }

  @Override
  protected void refreshInfo()
  {
    (new RefreshTask()).start();
  }

  private class RefreshTask extends Thread
  {
    @Override
    public void run()
    {
      final Bundle bundle = new Bundle();
      if(!getAccountData(_mFragment.getAccount(),bundle))
      {
        _mUiHandler.post(_mStopRefreshRunnable);
        return;
      }
      Request.refresh(TerminalInfoActivity.this, bundle);
      _mUiHandler.post(_mStopRefreshRunnable);
    }
  }

  private class TerminalsObserver extends ContentObserver
  {
    public TerminalsObserver(Handler handler)
    {
      super(handler);
    }

    @Override
    public void onChange(boolean selfChange)
    {
      super.onChange(selfChange);
      _mTerminals.refresh();
      _mFragment.refresh();
    }
  }
}