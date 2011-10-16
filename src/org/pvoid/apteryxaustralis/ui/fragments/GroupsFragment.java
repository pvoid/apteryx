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

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.pvoid.apteryxaustralis.TextFormat;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpContentProvider;
import org.pvoid.apteryxaustralis.R;

public class GroupsFragment extends ListFragment
{
  private final GroupsObserver _mObserver = new GroupsObserver(new Handler());

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState)
  {
    setListAdapter(new GroupsAdapter(getActivity()));
    getActivity().getContentResolver().registerContentObserver(OsmpContentProvider.Agents.CONTENT_URI,true,_mObserver);
    super.onViewCreated(view, savedInstanceState);
  }

  @Override
  public void onDestroyView()
  {
    super.onDestroyView();
    getActivity().getContentResolver().unregisterContentObserver(_mObserver);
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

  private class GroupsAdapter extends CursorAdapter
  {
    public GroupsAdapter(Context context)
    {
      super(context, context.getContentResolver().query(OsmpContentProvider.Agents.CONTENT_URI,
                                                        new String[]
                                                        {
                                                          OsmpContentProvider.Agents.COLUMN_AGENT,
                                                          OsmpContentProvider.Agents.COLUMN_AGENT_NAME,
                                                          OsmpContentProvider.Agents.COLUMN_STATE,
                                                          OsmpContentProvider.Agents.COLUMN_SEEN,
                                                          OsmpContentProvider.Agents.COLUMN_BALANCE,
                                                          OsmpContentProvider.Agents.COLUMN_OVERDRAFT
                                                        },
                                                        null,
                                                        null,
                                                        OsmpContentProvider.Agents.COLUMN_AGENT_NAME), true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
      final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      final View view = inflater.inflate(R.layout.record_group,parent,false);
      bindView(view,context,cursor);
      return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
      TextView text = (TextView) view.findViewById(R.id.agent_name);
      if(text!=null)
        text.setText(cursor.getString(1));
      ////////
      text = (TextView) view.findViewById(R.id.agent_balance);
      if(text!=null)
        text.setText(TextFormat.formatMoney(cursor.getDouble(4),false));
      ////////
      final double overdraft = cursor.getDouble(5);
      text = (TextView) view.findViewById(R.id.agent_overdraft);
      if(text!=null)
        if(overdraft!=0)
        {
          text.setText(TextFormat.formatMoney(overdraft,false));
          text.setVisibility(View.VISIBLE);
        }
        else
          text.setVisibility(View.GONE);
      text = (TextView) view.findViewById(R.id.agent_overdraft_title);
      if(text!=null)
        if(overdraft!=0)
          text.setVisibility(View.VISIBLE);
        else
          text.setVisibility(View.GONE);
      ////////
      ImageView image = (ImageView) view.findViewById(R.id.agent_state);
      if(image!=null)
        image.getDrawable().setLevel(cursor.getInt(2));
    }

    public void refresh()
    {
      getCursor().requery();
    }
  }
}
