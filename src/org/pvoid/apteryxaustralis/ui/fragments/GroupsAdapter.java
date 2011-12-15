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
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.TextFormat;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpContentProvider;

public class GroupsAdapter extends CursorAdapter
{
  final int _mLayoutResource;

  public GroupsAdapter(Context context, int layoutResource)
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
    _mLayoutResource = layoutResource;
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent)
  {
    final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    return inflater.inflate(_mLayoutResource,parent,false);
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor)
  {
    if(view instanceof TextView)
    {
      ((TextView)view).setText(cursor.getString(1));
      return;
    }
    ////////
    TextView text = (TextView) view.findViewById(R.id.agent_name);
    if(text!=null)
      text.setText(cursor.getString(1));
    ////////
    text = (TextView) view.findViewById(R.id.agent_balance);
    if(text!=null)
      text.setText(TextFormat.formatMoney(cursor.getDouble(4), false));
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
      image.getDrawable().setLevel(cursor.getInt(3)==0 ? cursor.getInt(2) : 0);
  }

  @Override
  public View newDropDownView(Context context, Cursor cursor, ViewGroup parent)
  {
    final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final View view = inflater.inflate(R.layout.record_group,parent,false);
    bindView(view,context,cursor);
    return view;
  }

  public void refresh()
  {
    getCursor().requery();
  }
}
