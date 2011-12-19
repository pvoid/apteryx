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
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.RefreshableActivity;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpContentProvider;
import org.pvoid.apteryxaustralis.ui.fragments.TerminalInfoFragment;
import org.pvoid.apteryxaustralis.ui.fragments.TerminalsCursorAdapter;

public class TerminalInfoActivity extends RefreshableActivity implements ViewPager.OnPageChangeListener
{
  public final static String EXTRA_AGENT = "agent";
          
  TerminalsCursorAdapter     _mTerminals;
  private final Handler      _mUiHandler = new Handler();
  protected ViewPager        _mPager;
  final TerminalsObserver    _mObserver = new TerminalsObserver(_mUiHandler);

  private long getGroupId()
  {
    final Bundle arguments = getIntent().getExtras();
    if(arguments==null)
      return 0;
    return arguments.getLong(EXTRA_AGENT);
  }

  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_terminal_info);
//////////
    final long agentId = getGroupId();
    final long terminalId = getIntent().getLongExtra(TerminalInfoFragment.EXTRA_TERMINAL,0);
    _mTerminals = new TerminalsCursorAdapter(this, OsmpContentProvider.Terminals.COLUMN_AGENTID + "=" + Long.toString(agentId), R.layout.record_terminal_actionbar);
    _mPager = (ViewPager) findViewById(R.id.pages);
    if(_mPager==null)
    {
      throw new Error("Cant' find pager view with ID #pages");
    }
    _mPager.setAdapter(new InfoAdapter(getSupportFragmentManager()));
    _mPager.setOnPageChangeListener(this);
///////// Установим переключалку терминалов

    for(int index=0, count = _mTerminals.getCount();index<count;++index)
    {
      if(terminalId==_mTerminals.getItemId(index))
      {
        _mPager.setCurrentItem(index);
        setListNavigationMode(_mTerminals,index);
      }
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
    _mPager.setCurrentItem(itemPosition,true);
    return true;
  }

  @Override
  public void onPageScrolled(int index, float v, int i1)
  {
  }

  @Override
  public void onPageSelected(int index)
  {
    setSelectedNavigationItem(index);
  }

  @Override
  public void onPageScrollStateChanged(int i)
  {
  }

  private class InfoAdapter extends FragmentPagerAdapter
  {
    public InfoAdapter(FragmentManager fm)
    {
      super(fm);
    }

    @Override
    public Fragment getItem(int index)
    {
      Bundle arguments = new Bundle();
      arguments.putLong(TerminalInfoFragment.EXTRA_TERMINAL,_mTerminals.getItemId(index));
      Fragment fragment = new TerminalInfoFragment();
      fragment.setArguments(arguments);
      return fragment;
    }

    @Override
    public int getCount()
    {
      return _mTerminals.getCount();
    }
  }

  /*private class RefreshTask extends Thread
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
      ContentLoader.refresh(TerminalInfoActivity.this, bundle);
      _mUiHandler.post(_mStopRefreshRunnable);
    }
  }*/

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
      _mPager.getAdapter().notifyDataSetChanged();
    }
  }
}