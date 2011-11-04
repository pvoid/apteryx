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
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpContentProvider;

public class TerminalsCursorAdapter extends CursorAdapter
  {
    private final int resourceId;

    public TerminalsCursorAdapter(Context context, String whereClause, int resource)
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
                                                 OsmpContentProvider.Terminals.COLUMN_LASTACTIVITY,
                                                 OsmpContentProvider.Terminals.COLUMN_FINAL_STATE
                                               },
                                               whereClause ,null,
                                               OsmpContentProvider.Terminals.COLUMN_FINAL_STATE +" desc, "+
                                               OsmpContentProvider.Terminals.COLUMN_ADDRESS),
            true);
      resourceId = resource;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
      final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      return inflater.inflate(resourceId,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
      TextView textView = (TextView) view.findViewById(R.id.list_title);
      if(textView!=null)
        textView.setText(cursor.getString(1));
//////////
      textView = (TextView) view.findViewById(R.id.status);
      if(textView!=null)
        textView.setText(OsmpContentProvider.getTerminalStatus(context,
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
      if(image!=null)
        image.setImageLevel(cursor.getInt(9));
    }

    public void refresh()
    {
      getCursor().requery();
    }
  }
