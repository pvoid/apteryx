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
import org.pvoid.apteryxaustralis.net.ContentLoader;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpContentProvider;
import org.pvoid.apteryxaustralis.ui.fragments.GroupsAdapter;
import org.pvoid.apteryxaustralis.ui.fragments.TerminalsFragment;

public class TerminalsActivity extends RefreshableActivity
{
  private final Handler  _mUiHandler = new Handler();
  private final Runnable _mStopRefreshRunnable = new Runnable()
  {
    @Override
    public void run()
    {
      showRefreshProgress(false);
    }
  };
  private GroupsAdapter  _mGroups;
  private final GroupsObserver _mObserver = new GroupsObserver(new Handler());

  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_terminals);
    _mGroups = new GroupsAdapter(this, R.layout.record_group_actionbar);
    final ViewPager pager = (ViewPager) findViewById(R.id.pages);
    pager.setAdapter(new TerminalsPagerAdapter(getSupportFragmentManager()));

    long id = getIntent().getLongExtra(TerminalsFragment.ARGUMENT_AGENT,0);
    if(id==0)
      return;
    for(int index=0;index<_mGroups.getCount();++index)
      if(id==_mGroups.getItemId(index))
      {
        pager.setCurrentItem(index);
        setListNavigationMode(_mGroups,index);
        break;
      }

    /*final FragmentManager man = getSupportFragmentManager();
    TerminalsFragment fragment = new TerminalsFragment();
    fragment.setArguments(getIntent().getExtras());
    man.beginTransaction().add(android.R.id.content,fragment).commit();*/

    /*final ActionBar bar = getSupportActionBar();
    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

    bar.setListNavigationCallbacks(_mGroups,this);*/
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
    _mGroups.refresh();
    getContentResolver().registerContentObserver(OsmpContentProvider.Agents.CONTENT_URI,true, _mObserver);
  }

  @Override
  public boolean onNavigationItemSelected(int itemPosition, long itemId)
  {
    return false;
  }

  @Override
  protected void refreshInfo()
  {
    final Bundle bundle = new Bundle();
    long id = getIntent().getLongExtra(TerminalsFragment.ARGUMENT_AGENT,0);
    if(!getAccountData(id,bundle))
      return;
    ContentLoader.refresh(this, bundle);
  }

  private class GroupsObserver extends ContentObserver
  {
    public GroupsObserver(Handler handler)
    {
      super(handler);
    }

    @Override
    public void onChange(boolean selfChange)
    {
      super.onChange(selfChange);
      _mGroups.refresh();
    }
  }

  private class TerminalsPagerAdapter extends FragmentPagerAdapter
  {
    public TerminalsPagerAdapter(FragmentManager fm)
    {
      super(fm);
    }

    @Override
    public Fragment getItem(int index)
    {
      final Bundle arguments = new Bundle();
      arguments.putLong(TerminalsFragment.ARGUMENT_AGENT,_mGroups.getItemId(index));
      TerminalsFragment fragment = new TerminalsFragment();
      fragment.setArguments(arguments);
      return fragment;
    }

    @Override
    public int getCount()
    {
      return _mGroups.getCount();
    }
  }
}