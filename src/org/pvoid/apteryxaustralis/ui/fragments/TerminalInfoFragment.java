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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.TextFormat;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpContentProvider;
import org.pvoid.apteryxaustralis.ui.widgets.TerminalHeader;

public class TerminalInfoFragment extends ListFragment
{
  public static final String EXTRA_TERMINAL = "id";

  private TerminalInfoAdapter     _mInfo;
  private long                    _mAccount;
  private long                    _mTerminalId;
  private TerminalHeader          _mHeader;
  private final TerminalsObserver _mObserver = new TerminalsObserver(new Handler());

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState)
  {
    super.onViewCreated(view, savedInstanceState);
    final Context context = getActivity();
    _mInfo = new TerminalInfoAdapter(context);
    _mHeader = new TerminalHeader(context);
    _mHeader.setEnabled(false);
    final ListView list = getListView();
    setListAdapter(null);
    list.setHeaderDividersEnabled(false);
    list.addHeaderView(_mHeader,null,true);
    setListAdapter(_mInfo);
    _mAccount = getArguments().getLong(EXTRA_TERMINAL,0);
    if(_mAccount!=0)
      loadTerminalInfo(_mAccount);
    getActivity().getContentResolver().registerContentObserver(OsmpContentProvider.Terminals.CONTENT_URI,
                                                               true,
                                                               _mObserver);
  }

  @Override
  public void onDestroyView()
  {
    super.onDestroyView();
    getActivity().getContentResolver().unregisterContentObserver(_mObserver);
  }

  public void refresh()
  {
//////// Вытащим данные
    final Cursor cursor = getActivity().getContentResolver().query(
       OsmpContentProvider.Terminals.CONTENT_URI,
       new String[]
           {
            OsmpContentProvider.Terminals.COLUMN_CASH,
            OsmpContentProvider.Terminals.COLUMN_LASTPAYMENT,
            OsmpContentProvider.Terminals.COLUMN_LASTACTIVITY,
            OsmpContentProvider.Terminals.COLUMN_PAYSPERHOUR,
            OsmpContentProvider.Terminals.COLUMN_BALANCE,
            OsmpContentProvider.Terminals.COLUMN_SIGNALLEVEL,
            OsmpContentProvider.Terminals.COLUMN_SOFTVERSION,
            OsmpContentProvider.Terminals.COLUMN_BONDS,
            OsmpContentProvider.Terminals.COLUMN_BONDS10,
            OsmpContentProvider.Terminals.COLUMN_BONDS50,
            OsmpContentProvider.Terminals.COLUMN_BONDS100,
            OsmpContentProvider.Terminals.COLUMN_BONDS500,
            OsmpContentProvider.Terminals.COLUMN_BONDS1000,
            OsmpContentProvider.Terminals.COLUMN_BONDS5000,
            OsmpContentProvider.Terminals.COLUMN_PRINTERMODEL,
            OsmpContentProvider.Terminals.COLUMN_CASHBINMODEL,
            OsmpContentProvider.Terminals.COLUMN_ACCOUNTID,
            OsmpContentProvider.Terminals.COLUMN_CASHBINSTATE,
            OsmpContentProvider.Terminals.COLUMN_PRINTERSTATE,
            OsmpContentProvider.Terminals.COLUMN_MS
           },
       OsmpContentProvider.Terminals.COLUMN_ID + "=?",
       new String[] {Long.toString(_mTerminalId)},
       null
    );
    try
    {
      if(cursor.moveToFirst())
      {
        _mAccount = cursor.getLong(16);
        _mInfo.add(new TerminalInfo(getString(R.string.fullinfo_cash), TextFormat.formatMoney(cursor.getFloat(0), true)));

        _mInfo.add(new TerminalInfo(getString(R.string.fullinfo_last_payment), TextFormat.formatDateSmart(getActivity(), cursor.getLong(1))));
        _mInfo.add(new TerminalInfo(getString(R.string.fullinfo_last_activity), TextFormat.formatDateSmart(getActivity(), cursor.getLong(2))));
        _mInfo.add(new TerminalInfo(getString(R.string.fullinfo_pays_per_hour),cursor.getString(3)));
        _mInfo.add(new TerminalInfo(getString(R.string.fullinfo_balance),cursor.getString(4)));
        _mInfo.add(new TerminalInfo(getString(R.string.fullinfo_signal_level),cursor.getString(5)));

        _mInfo.add(new TerminalInfo(getString(R.string.fullinfo_soft_version),cursor.getString(6)));

        _mInfo.add(new TerminalInfo(getString(R.string.fullinfo_bonds),cursor.getString(7)));
        _mInfo.add(new TerminalInfo(getString(R.string.fullinfo_bonds10),cursor.getString(8)));
        _mInfo.add(new TerminalInfo(getString(R.string.fullinfo_bonds50),cursor.getString(9)));
        _mInfo.add(new TerminalInfo(getString(R.string.fullinfo_bonds100),cursor.getString(10)));
        _mInfo.add(new TerminalInfo(getString(R.string.fullinfo_bonds500),cursor.getString(11)));
        _mInfo.add(new TerminalInfo(getString(R.string.fullinfo_bonds1000),cursor.getString(12)));
        _mInfo.add(new TerminalInfo(getString(R.string.fullinfo_bonds5000),cursor.getString(13)));

        _mInfo.add(new TerminalInfo(getString(R.string.fullinfo_printer),cursor.getString(14)));
        _mInfo.add(new TerminalInfo(getString(R.string.fullinfo_cashbin),cursor.getString(15)));

        _mHeader.setData(cursor.getString(17),cursor.getString(18),cursor.getInt(19));
      }
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
  }

  public void loadTerminalInfo(long id)
  {
    _mInfo.clear();
    _mTerminalId = id;
    refresh();
  }

  private static class TerminalInfo
  {
    protected final String title;
    protected final String value;

    TerminalInfo(String title, String value)
    {
      this.title = title;
      this.value = value;
    }

    @Override
    public String toString()
    {
      return title;
    }
  }

  private class TerminalInfoAdapter extends ArrayAdapter<TerminalInfo>
  {
    public TerminalInfoAdapter(Context context)
    {
      super(context, R.layout.terminal_info, R.id.title);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
      convertView = super.getView(position, convertView, parent);
      if(convertView!=null)
      {
        final TextView text = (TextView) convertView.findViewById(R.id.value);
        if(text!=null)
          text.setText(getItem(position).value);
      }
      return convertView;
    }

    @Override
    public boolean areAllItemsEnabled()
    {
      return true;
    }

    @Override
    public boolean isEnabled(int position)
    {
      return false;
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
      loadTerminalInfo(_mTerminalId);
    }
  }
}