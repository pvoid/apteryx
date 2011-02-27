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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.types.Agent;
import org.pvoid.apteryxaustralis.types.Payment;
import org.pvoid.apteryxaustralis.types.Terminal;
import org.pvoid.apteryxaustralis.types.TerminalStatus;
import org.pvoid.apteryxaustralis.storage.Storage;
import org.pvoid.apteryxaustralis.ui.widgets.FullInfoItem;
import org.pvoid.apteryxaustralis.ui.widgets.StateLine;

import java.util.Calendar;
import java.util.TimeZone;

public class TerminalInfoActivity extends Activity
{
  private static final int MENU_REFRESH = 0;
  private static final int DIALOG_PROGRESS = 0;

  private static String DATE_FORMAT = "dd MMM yyyy kk:mm";

  private long _mAccountId;
  private long _mTerminalId;

  private class RefreshTask extends AsyncTask<Void,Void,TerminalStatus>
  {
    @Override
    protected TerminalStatus doInBackground(Void... voids)
    {

      return null;
    }

    @Override
    protected void onPostExecute(TerminalStatus status)
    {
      if(status!=null)
      {
        setTerminalStatus(status);
        dismissDialog(DIALOG_PROGRESS);
        removeDialog(DIALOG_PROGRESS);
      }
    }
  }

  private void setTerminalStatus(TerminalStatus status)
  {
    float density = getResources().getDisplayMetrics().density;
    TextView text = (TextView)findViewById(R.id.time);
    text.setText(DateUtils.getRelativeTimeSpanString(status.getRequestDate(),
                                                     System.currentTimeMillis(),
                                                     0,
                                                     DateUtils.FORMAT_ABBREV_ALL));


    ImageView icon = (ImageView)findViewById(R.id.status_icon);
    String printer_state = status.getPrinterErrorId();
    String note_state = status.getNoteErrorId();

    if(!TextUtils.isEmpty(note_state) && !note_state.equals("OK"))
    {
      icon.setImageResource(R.drawable.ic_terminal_inactive);
    }
    else if(!TextUtils.isEmpty(printer_state) && !printer_state.equals("OK"))
    {
      icon.setImageResource(R.drawable.ic_terminal_printer_error);
    }
    else
      switch(status.getCommonState(this))
      {
        case TerminalStatus.STATE_COMMON_ERROR:
          icon.setImageResource(R.drawable.ic_terminal_inactive);
          // TODO: Разные иконки для принтера и для остального*/
          break;
        case TerminalStatus.STATE_COMMON_WARNING:
          icon.setImageResource(R.drawable.ic_terminal_pending);
          break;
        default:
          icon.setImageResource(R.drawable.ic_terminal_active);
      }

    Iterable<String> states = status.getStates(this);
    LinearLayout statesContainer = (LinearLayout)findViewById(R.id.states);
    boolean first = true;
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                                                     ViewGroup.LayoutParams.WRAP_CONTENT);
    params.topMargin = (int) (density * 4 + 0.5f);
    for(String state : states)
    {
      StateLine line = new StateLine(this);
      if(first)
        first=false;
      line.setText(state);

      statesContainer.addView(line,params);
    }

    View line = findViewById(R.id.sep_line);
    line.setVisibility(first?View.GONE:View.VISIBLE);
    statesContainer.setVisibility(first?View.GONE:View.VISIBLE);
////////// Дата последней активности
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeZone(TimeZone.getDefault());
    calendar.setTimeInMillis(status.getLastActivityDate());
    FullInfoItem info = (FullInfoItem)findViewById(R.id.last_activity);
    info.setText(DateFormat.format(DATE_FORMAT,calendar.getTime()));
////////// Уровень сигнала
    info = (FullInfoItem)findViewById(R.id.signal_level);
    info.setText(Integer.toString(status.getSignalLevel()));
////////// Баланс у сотового оператора
    info = (FullInfoItem)findViewById(R.id.balance);
    info.setText(Float.toString(status.getSimProviderBalance()));
  }

  private void setLastPayment(Payment payment)
  {
    long time = payment.getDateInTerminal();
    FullInfoItem info = (FullInfoItem)findViewById(R.id.last_payment);
    if(time<=0)
      info.setVisibility(View.GONE);
    else
    {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeZone(TimeZone.getDefault());
      calendar.setTimeInMillis(time);
      info.setText(DateFormat.format(DATE_FORMAT,calendar.getTime()));
      info.setVisibility(View.VISIBLE);
    }
  }

  private void setTerminalInfo(Terminal terminal, TerminalStatus status, Payment payment, Agent agent)
  {
    TextView text = (TextView)findViewById(R.id.name);
    text.setText(terminal.getDisplayName());

    setTerminalStatus(status);
    setLastPayment(payment);

    FullInfoItem info = (FullInfoItem)findViewById(R.id.address);
    info.setText(terminal.getAddress());
  }

  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.terminal_info);

    _mTerminalId = getIntent().getLongExtra("id",-1);
    if(_mTerminalId!=-1)
    {
      Terminal terminal = new Terminal(_mTerminalId);
      TerminalStatus status = new TerminalStatus(_mTerminalId);
      Payment payment = new Payment(_mTerminalId);
      Agent agent = new Agent();

      if(Storage.getTerminalInfo(this,_mTerminalId,terminal,status,payment,agent))
      {
        _mAccountId = agent.getAccount();
        setTerminalInfo(terminal, status, payment, agent);
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuItem item = menu.add(0, MENU_REFRESH, Menu.FIRST, R.string.refresh);
    item.setIcon(R.drawable.ic_menu_refresh);
    return true;
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item)
  {
    boolean result = super.onMenuItemSelected(featureId, item);
    if(!result)
    {
      int itemId = item.getItemId();
      switch(itemId)
      {
        case MENU_REFRESH:
          showDialog(DIALOG_PROGRESS);
          (new RefreshTask()).execute();
          return true;
      }
    }
    return result;
  }

  @Override
  protected Dialog onCreateDialog(int id)
  {
    switch(id)
    {
      case DIALOG_PROGRESS:
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(R.string.status_refresh));
        dialog.setIndeterminate(true);
        return dialog;
    }
    return null;
  }
}