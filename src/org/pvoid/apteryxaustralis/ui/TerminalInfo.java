package org.pvoid.apteryxaustralis.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.Agent;
import org.pvoid.apteryxaustralis.accounts.Terminal;
import org.pvoid.apteryxaustralis.accounts.TerminalStatus;
import org.pvoid.apteryxaustralis.net.StatusRefreshRunnable;
import org.pvoid.apteryxaustralis.storage.Storage;
import org.pvoid.apteryxaustralis.ui.widgets.FullInfoItem;
import org.pvoid.apteryxaustralis.ui.widgets.StateLine;

public class TerminalInfo extends Activity
{
  private static final int MENU_REFRESH = 0;
  private static final int DIALOG_PROGRESS = 0;

  private long _mAccountId;
  private long _mTerminalId;

  private class RefreshTask extends AsyncTask<Void,Void,TerminalStatus>
  {
    @Override
    protected TerminalStatus doInBackground(Void... voids)
    {
      Account account = Storage.getAccount(TerminalInfo.this, _mAccountId);
      if(account!=null)
      {
        return StatusRefreshRunnable.GetStatus(account.getLogin(),
                                               account.getPassword(),
                                               Long.toString(account.getTerminalId()),
                                               _mTerminalId);
      }
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
    TextView text = (TextView)findViewById(R.id.time);
    text.setText(DateUtils.getRelativeTimeSpanString(status.getRequestDate(),
                                                     System.currentTimeMillis(),
                                                     0,
                                                     DateUtils.FORMAT_ABBREV_RELATIVE));


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
      switch(status.getCommonState())
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
    for(String state : states)
    {
      StateLine line = new StateLine(this);
      if(first)
      {
        first=false;
        line.setBackgroundColor(Color.RED);
      }
      else
        line.setBackgroundColor(Color.RED);
      line.setText(state);
      statesContainer.addView(line,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
    }
////////// Дата последней активности
    FullInfoItem info = (FullInfoItem)findViewById(R.id.last_activity);
    info.setText(DateUtils.getRelativeTimeSpanString(status.getLastActivityDate(),
                                                     System.currentTimeMillis(),
                                                     0,
                                                     DateUtils.FORMAT_ABBREV_RELATIVE));
////////// Уровень сигнала
    info = (FullInfoItem)findViewById(R.id.signal_level);
    info.setText(Integer.toString(status.getSignalLevel()));
////////// Баланс у сотового оператора
    info = (FullInfoItem)findViewById(R.id.balance);
    info.setText(Float.toString(status.getSimProviderBalance()));
  }

  private void setTerminalInfo(Terminal terminal, TerminalStatus status, Agent agent)
  {
    TextView text = (TextView)findViewById(R.id.name);
    text.setText(terminal.getDisplayName());

    setTerminalStatus(status);

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
      Agent agent = new Agent();

      if(Storage.getTerminalInfo(this,_mTerminalId,terminal,status,agent))
      {
        _mAccountId = agent.getAccount();
        setTerminalInfo(terminal, status, agent);
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