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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.types.Agent;

public class AgentsArrayAdapter extends ArrayAdapter<Agent>
{
  public AgentsArrayAdapter(Context context, int resource, int textViewResourceId)
  {
    super(context, resource, textViewResourceId);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    View result = super.getView(position, convertView, parent);
    if(result!=null)
    {
      TextView text = (TextView) result.findViewById(R.id.agent_balance);
      Agent agent = getItem(position);
      if(text!=null && agent!=null)
        text.setText(getContext().getString(R.string.balance)+": "+Float.toString(agent.getBalance()));
    }
    return result;
  }
}
