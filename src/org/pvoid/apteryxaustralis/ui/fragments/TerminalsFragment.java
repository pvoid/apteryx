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

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpContentProvider;
import org.pvoid.apteryxaustralis.ui.TerminalInfoActivity;
import org.pvoid.apteryxaustralis.ui.widgets.AgentHeader;

public class TerminalsFragment extends ListFragment
{
  public static final String ARGUMENT_AGENT = "agent";

  private final TerminalsObserver _mTerminalsObserver = new TerminalsObserver(new Handler());
  private AgentHeader _mHeader;

  private long getGroupId()
  {
    final Bundle arguments = getArguments();
    if(arguments==null)
      return 0;
    return arguments.getLong(ARGUMENT_AGENT);
  }

  private String getWhereClause()
  {
    long id = getGroupId();
    if(id==0)
      return null;
    else
      return OsmpContentProvider.Terminals.COLUMN_AGENTID + "=" + Long.toString(id);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState)
  {
    super.onViewCreated(view, savedInstanceState);
    final Activity activity = getActivity();
    final ListView list = getListView();
    _mHeader = new AgentHeader(activity);
    setListAdapter(null);
    _mHeader.loadAgentData(getGroupId());
    list.addHeaderView(_mHeader,null,false);
    setListAdapter(new TerminalsCursorAdapter(activity,getWhereClause(), R.layout.record_terminal));
    final ContentResolver resolver = activity.getContentResolver();
    resolver.registerContentObserver(OsmpContentProvider.Terminals.CONTENT_URI, true, _mTerminalsObserver);
//////////
    final ContentValues values = new ContentValues();
    values.put(OsmpContentProvider.Agents.COLUMN_SEEN,1);
    resolver.update(OsmpContentProvider.Agents.CONTENT_URI,
                    values,
                    OsmpContentProvider.Agents.COLUMN_AGENT+"=?",
                    new String[] {Long.toString(getGroupId())});
  }

  @Override
  public void onDestroyView()
  {
    super.onDestroyView();
    getActivity().getContentResolver().unregisterContentObserver(_mTerminalsObserver);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id)
  {
    Intent intent = new Intent(getActivity(), TerminalInfoActivity.class);
    intent.putExtra(TerminalInfoFragment.EXTRA_TERMINAL,id);
    intent.putExtra(TerminalInfoActivity.EXTRA_AGENT,getGroupId());
    startActivity(intent);
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
      ((TerminalsCursorAdapter)getListAdapter()).refresh();
      _mHeader.loadAgentData(getGroupId());
    }
  }
}
