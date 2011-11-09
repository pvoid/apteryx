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

package org.pvoid.apteryxaustralis.ui.widgets;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.TextFormat;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpContentProvider;

public class AgentHeader extends FrameLayout
{
  @SuppressWarnings("unused")
  public AgentHeader(Context context)
  {
    super(context);
    setupUI(context);
  }
  @SuppressWarnings("unused")
  public AgentHeader(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    setupUI(context);
  }
  @SuppressWarnings("unused")
  public AgentHeader(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    setupUI(context);
  }

  private void setupUI(Context context)
  {
    View.inflate(context, R.layout.agent_header, this);
  }

  public void loadAgentData(long agent)
  {
    final ContentResolver resolver = getContext().getContentResolver();
/////////// Вытащим баланс и все остальное
    final Cursor cursor = resolver.query(
       OsmpContentProvider.Agents.CONTENT_URI,
       new String[] {OsmpContentProvider.Agents.COLUMN_BALANCE, OsmpContentProvider.Agents.COLUMN_OVERDRAFT, OsmpContentProvider.Agents.COLUMN_CASH},
       OsmpContentProvider.Agents.COLUMN_AGENT+"=?", new String[]{Long.toString(agent)},null
    );
    try
    {
      if(cursor.moveToFirst())
      {
        TextView text = (TextView)findViewById(R.id.agent_balance);
        if(text!=null)
          text.setText(TextFormat.formatMoney(cursor.getFloat(0),false));

        text = (TextView)findViewById(R.id.agent_overdraft);
        if(text!=null)
          text.setText(TextFormat.formatMoney(cursor.getFloat(1),false));

        text = (TextView)findViewById(R.id.agent_cash);
        if(text!=null)
          text.setText(TextFormat.formatMoney(cursor.getInt(2),false));
      }
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
  }
}
