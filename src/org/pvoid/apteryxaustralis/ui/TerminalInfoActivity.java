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

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.ui.fragments.TerminalInfoFragment;
import org.pvoid.apteryxaustralis.ui.fragments.TerminalsCursorAdapter;

public class TerminalInfoActivity extends FragmentActivity implements ActionBar.OnNavigationListener
{
  final TerminalInfoFragment _mFragment = new TerminalInfoFragment();

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
    TerminalsCursorAdapter terminals = new TerminalsCursorAdapter(this, null, R.layout.terminal_list);
    bar.setListNavigationCallbacks(terminals,this);
//////// Ищем выбранный терминал
    for(int index=0;index< terminals.getCount();++index)
      if(terminals.getItemId(index) == id)
      {
        bar.setSelectedNavigationItem(index);
        break;
      }
  }

  @Override
  public boolean onNavigationItemSelected(int itemPosition, long itemId)
  {
    _mFragment.loadTerminalInfo(itemId);
    return true;
  }
}