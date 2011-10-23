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

package org.pvoid.apteryxaustralis.ui.fragments;

import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpContentProvider;
import org.pvoid.apteryxaustralis.ui.TerminalsActivity;

public class GroupsFragment extends ListFragment
{
  private final GroupsObserver _mObserver = new GroupsObserver(new Handler());

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState)
  {
    setListAdapter(new GroupsAdapter(getActivity(),R.layout.record_group));
    getActivity().getContentResolver().registerContentObserver(OsmpContentProvider.Agents.CONTENT_URI,true,_mObserver);
    super.onViewCreated(view, savedInstanceState);
  }

  @Override
  public void onDestroyView()
  {
    super.onDestroyView();
    getActivity().getContentResolver().unregisterContentObserver(_mObserver);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id)
  {
    Intent intent = new Intent(getActivity(), TerminalsActivity.class);
    intent.putExtra(TerminalsFragment.ARGUMENT_AGENT,id);
    startActivity(intent);
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
      GroupsAdapter adapter = (GroupsAdapter) getListAdapter();
      if(adapter!=null)
        adapter.refresh();
    }
  }

}
