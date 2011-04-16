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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpStorage;
import org.pvoid.apteryxaustralis.types.ITerminal;
import org.pvoid.apteryxaustralis.types.InfoLine;
import org.pvoid.apteryxaustralis.types.StatusLine;
import org.pvoid.apteryxaustralis.ui.widgets.FullInfoItem;
import org.pvoid.apteryxaustralis.ui.widgets.StateLine;

import java.util.ArrayList;

public class FullInfo extends Activity
{
  public static final String TERMINAL_EXTRA = "terminal";

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    float density = getResources().getDisplayMetrics().density;
    super.onCreate(savedInstanceState);
    setContentView(R.layout.terminal_info);
////////
    long id = getIntent().getLongExtra(TERMINAL_EXTRA,0);
    if(id==0)
    {
      finish();
      return;
    }
////////
    OsmpStorage storage = new OsmpStorage(this);
    ITerminal terminal = storage.getTerminal(id);
////////
    ImageView icon = (ImageView)findViewById(R.id.status_icon);
    switch(terminal.getState())
    {
      case ITerminal.STATE_OK:
        icon.setImageResource(R.drawable.ic_terminal_active);
        break;
      case ITerminal.STATE_WARRNING:
        icon.setImageResource(R.drawable.ic_terminal_pending);
        break;
      case ITerminal.STATE_ERROR:
        icon.setImageResource(R.drawable.ic_terminal_printer_error);
        break;
      case ITerminal.STATE_ERROR_CRITICAL:
        icon.setImageResource(R.drawable.ic_terminal_inactive);
        break;
    }
/////////
    TextView text = (TextView) findViewById(R.id.name);
    text.setText(terminal.getTitle());
/////////
    final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                                                           ViewGroup.LayoutParams.WRAP_CONTENT);
    LinearLayout layout = (LinearLayout) findViewById(R.id.states);;
    ArrayList<StatusLine> states = new ArrayList<StatusLine>();
    terminal.getStatuses(this,states);
    if(!states.isEmpty())
    {
      params.topMargin = (int) (density * 4 + 0.5f);

      for(StatusLine state : states)
      {
        StateLine line = new StateLine(this);
        line.setText(state.text);
        layout.addView(line,params);
      }
    }
    else
    {
      View view = findViewById(R.id.sep_line);
      view.setVisibility(View.GONE);
      layout.setVisibility(View.GONE);
    }
/////////
    ArrayList<InfoLine> info = new ArrayList<InfoLine>();
    terminal.getInfo(this,info);
    if(!info.isEmpty())
    {
      layout = (LinearLayout) findViewById(R.id.info);
      params.topMargin = 0;
      params.leftMargin = params.rightMargin = (int) (5*density);
      for(InfoLine line : info)
      {
        FullInfoItem item = new FullInfoItem(this);
        item.setLabel(line.title);
        item.setText(line.value);
        layout.addView(item,params);
      }
    }
    else
    {
      View view = findViewById(R.id.sep_line);
      view.setVisibility(View.GONE);
    }
  }
}
