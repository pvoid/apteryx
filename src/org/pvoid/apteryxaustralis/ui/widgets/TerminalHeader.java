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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpContentProvider;
import org.pvoid.apteryxaustralis.types.StatusLine;

import java.util.ArrayList;
import java.util.List;

public class TerminalHeader extends FrameLayout
{
  public TerminalHeader(Context context)
  {
    super(context);
    setupUI(context);
  }

  private void setupUI(Context context)
  {
    View.inflate(context, R.layout.terminal_header,this);
  }

  public void setData(String cashBinState, String printerState, int ms)
  {
    final List<StatusLine> statuses = new ArrayList<StatusLine>();
    final Context context = getContext();
    OsmpContentProvider.getStatuses(context,cashBinState,printerState,ms,statuses);
/////////
    ViewGroup root = (ViewGroup) getChildAt(0);
    root.removeAllViews();
    if(statuses.size()>0)
    {
      root.setVisibility(View.VISIBLE);
      for(StatusLine status : statuses)
      {
        final StateLine state = new StateLine(getContext());
        state.setText(status.text);
        root.addView(state);
      }
    }
    else
      root.setVisibility(View.GONE);
  }
}
