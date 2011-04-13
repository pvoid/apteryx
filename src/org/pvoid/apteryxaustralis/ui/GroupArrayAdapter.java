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

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.pvoid.apteryxaustralis.DateFormat;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.accounts.Group;
import org.pvoid.apteryxaustralis.accounts.Terminal;

public class GroupArrayAdapter extends ArrayAdapter<Terminal>
{
  Group _mGroup;

  public GroupArrayAdapter(Context context, Group group)
  {
    super(context, R.layout.terminal,R.id.list_title);
    _mGroup = group;
  }

  public long getGroupId()
  {
    if(_mGroup==null)
      return 0;
////////
    return _mGroup.id;
  }

  public String getGroupName()
  {
    if(_mGroup==null)
      return null;
////////
    return _mGroup.name;
  }

  public double getGroupBalance()
  {
    if(_mGroup==null)
      return 0;
////////
    return _mGroup.balance;
  }

  public double getGroupOverdraft()
  {
    if(_mGroup==null)
      return 0;
////////
    return _mGroup.overdraft;
  }

  public long getLastUpdateTime()
  {
    if(_mGroup==null)
      return 0;
////////
    return _mGroup.lastUpdate;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    Context context = getContext();
    View view = convertView;
    if(view==null)
    {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.terminal, null);
    }

    Terminal terminal = getItem(position);
    if(terminal!=null)
    {
      TextView name = (TextView)view.findViewById(R.id.list_title);
      name.setText(terminal.toString());
      TextView status = (TextView) view.findViewById(R.id.status);
      ImageView icon = (ImageView)view.findViewById(R.id.icon);
      int image = R.drawable.ic_terminal_active;
      String stateText = null;

      switch(terminal.State())
      {
        case Terminal.STATE_OK:
          stateText = Integer.toString(terminal.cash);
          break;
        case Terminal.STATE_WARRNING:
          image = R.drawable.ic_terminal_pending;
          stateText = getContext().getString(R.string.last_payment) + ' ' + DateFormat.formatDateSmart(getContext(),terminal.lastPayment);
          break;
        case Terminal.STATE_ERROR:
          image = R.drawable.ic_terminal_inactive;
          stateText = getContext().getString(R.string.last_activity)+ ' ' + DateFormat.formatDateSmart(getContext(),terminal.lastActivity);
          break;
      }

      if(!"OK".equals(terminal.cashbin_state))
        stateText = terminal.cashbin_state;

      if(!"OK".equals(terminal.printer_state))
      {
        image = R.drawable.ic_terminal_printer_error;
        stateText = terminal.printer_state;
      }

      status.setText(stateText);
      icon.setImageResource(image);
    }
    return view;
  }

  public void setGroup(Group group)
  {
    _mGroup = group;
  }
}
