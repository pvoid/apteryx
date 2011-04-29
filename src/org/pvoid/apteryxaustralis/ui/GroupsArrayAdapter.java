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
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.types.Group;
import org.pvoid.apteryxaustralis.types.ITerminal;

public class GroupsArrayAdapter extends ArrayAdapter<Group>
{
  public GroupsArrayAdapter(Context context)
  {
    super(context,R.layout.agent_dialog_item,R.id.agent_name);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    View result = super.getView(position, convertView, parent);
    if(result!=null)
    {
      Group group = getItem(position);
      if(group!=null)
      {
        TextView text = (TextView) result.findViewById(R.id.agent_balance);

        if(text!=null)
          text.setText(getContext().getString(R.string.balance)+": "+Double.toString(group.balance));

        text = (TextView) result.findViewById(R.id.agent_overdraft);
        if(text!=null)
        {
          if(group.overdraft!=0)
          {
            text.setText(getContext().getString(R.string.overdraft)+Double.toString(group.overdraft));
            text.setVisibility(View.VISIBLE);
          }
          else
            text.setVisibility(View.GONE);
        }

        View view = result.findViewById(R.id.state);
        Resources res = getContext().getResources();
        switch(group.state)
        {
          case ITerminal.STATE_OK:
            view.setBackgroundColor(res.getColor(R.color.status_ok));
            break;
          case ITerminal.STATE_WARNING:
            view.setBackgroundColor(res.getColor(R.color.status_warning));
            break;
          case ITerminal.STATE_ERROR:
            view.setBackgroundColor(res.getColor(R.color.status_error));
            break;
          case ITerminal.STATE_ERROR_CRITICAL:
            view.setBackgroundColor(res.getColor(R.color.status_fatal_error));
            break;
        }
      }
    }
    return result;
  }
}
