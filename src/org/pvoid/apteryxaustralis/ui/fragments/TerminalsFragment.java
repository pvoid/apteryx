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
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpContentProvider;

public class TerminalsFragment extends ListFragment
{
  public static final String ARGUMENT_AGENT = "agent";

  private final TerminalsObserver _mTerminalsObserver = new TerminalsObserver(new Handler());

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
    setListAdapter(new TerminalsCursorAdapter(getActivity()));
    getActivity().getContentResolver().registerContentObserver(OsmpContentProvider.Terminals.CONTENT_URI,true,_mTerminalsObserver);
  }

  @Override
  public void onDestroyView()
  {
    super.onDestroyView();
    getActivity().getContentResolver().unregisterContentObserver(_mTerminalsObserver);
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
    }
  }

  private class TerminalsCursorAdapter extends CursorAdapter
  {
    public TerminalsCursorAdapter(Context context)
    {
      super(context,
            context.getContentResolver().query(OsmpContentProvider.Terminals.CONTENT_URI,
                                               new String[]
                                               {
                                                 OsmpContentProvider.Terminals.COLUMN_ID,
                                                 OsmpContentProvider.Terminals.COLUMN_ADDRESS,
                                                 OsmpContentProvider.Terminals.COLUMN_PRINTERSTATE,
                                                 OsmpContentProvider.Terminals.COLUMN_CASHBINSTATE,
                                                 OsmpContentProvider.Terminals.COLUMN_STATE,
                                                 OsmpContentProvider.Terminals.COLUMN_MS,
                                                 OsmpContentProvider.Terminals.COLUMN_CASH,
                                                 OsmpContentProvider.Terminals.COLUMN_LASTPAYMENT,
                                                 OsmpContentProvider.Terminals.COLUMN_LASTACTIVITY
                                               },
                                               getWhereClause() ,null,OsmpContentProvider.Terminals.COLUMN_ADDRESS),
            true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
      final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      return inflater.inflate(R.layout.terminal,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
      TextView textView = (TextView) view.findViewById(R.id.list_title);
      textView.setText(cursor.getString(1));
//////////
      textView = (TextView) view.findViewById(R.id.status);
      textView.setText(OsmpContentProvider.getTerminalStatus(getActivity(),
                                                             cursor.getString(3),
                                                             cursor.getString(2),
                                                             cursor.getInt(5),
                                                             cursor.getInt(4),
                                                             cursor.getInt(6),
                                                             cursor.getLong(7),
                                                             cursor.getLong(8)
                                                            ));
//////////
      final ImageView image = (ImageView) view.findViewById(R.id.icon);
      switch(OsmpContentProvider.getState(cursor.getInt(4),cursor.getString(2),cursor.getLong(8)))
      {
        case OsmpContentProvider.STATE_OK:
          image.setImageResource(R.drawable.ic_terminal_active);
          break;
        case OsmpContentProvider.STATE_WARNING:
          image.setImageResource(R.drawable.ic_terminal_pending);
          break;
        case OsmpContentProvider.STATE_ERROR:
          image.setImageResource(R.drawable.ic_terminal_printer_error);
          break;
        case OsmpContentProvider.STATE_ERROR_CRITICAL:
          image.setImageResource(R.drawable.ic_terminal_inactive);
          break;
        default:
          image.setImageResource(R.drawable.ic_terminal_unknown);
          break;
      }
    }

    public void refresh()
    {
      getCursor().requery();
    }
  }
}
