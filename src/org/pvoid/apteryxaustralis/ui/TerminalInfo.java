package org.pvoid.apteryxaustralis.ui;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.accounts.Terminal;
import org.pvoid.apteryxaustralis.accounts.TerminalStatus;
import org.pvoid.apteryxaustralis.storage.Storage;

public class TerminalInfo extends Activity
{
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.terminal_info);

    long id = getIntent().getLongExtra("id",-1);
    if(id!=-1)
    {
      Terminal terminal = new Terminal(id);
      TerminalStatus status = new TerminalStatus(id);

      if(Storage.getTerminalInfo(this,id,terminal,status))
      {
        TextView text = (TextView)findViewById(R.id.name);
        text.setText(terminal.getDisplayName());
        text = (TextView)findViewById(R.id.time);
        text.setText(DateUtils.getRelativeTimeSpanString(status.getRequestDate(),
                                                         System.currentTimeMillis(),
                                                         0,
                                                         DateUtils.FORMAT_ABBREV_RELATIVE));
        ImageView icon = (ImageView)findViewById(R.id.status_icon);
        String printer_state = status.getPrinterErrorId();
        String note_state = status.getNoteErrorId();
        text = (TextView)findViewById(R.id.status_message);

        if(!TextUtils.isEmpty(note_state) && !note_state.equals("OK"))
        {
          icon.setImageResource(R.drawable.ic_terminal_inactive);
          text.setVisibility(View.VISIBLE);
          text.setText(note_state);
        }
        else if(!TextUtils.isEmpty(printer_state) && !printer_state.equals("OK"))
        {
          icon.setImageResource(R.drawable.ic_terminal_printer_error);
          text.setVisibility(View.VISIBLE);
          text.setText(printer_state);
        }
        else
          switch(status.getCommonState())
          {
            case TerminalStatus.STATE_COMMON_ERROR:
              icon.setImageResource(R.drawable.ic_terminal_inactive);
              text.setVisibility(View.VISIBLE);
              text.setText(status.getErrorText(this,false));
              // TODO: Разные иконки для принтера и для остального*/
              break;
            case TerminalStatus.STATE_COMMON_WARNING:
              icon.setImageResource(R.drawable.ic_terminal_pending);
              break;
            default:
              icon.setImageResource(R.drawable.ic_terminal_active);
          }
////////// Дата последней активности
/*        text = (TextView)findViewById(R.id.last_activity);
        text.setText(DateUtils.getRelativeTimeSpanString(status.getLastActivityDate(),
                                                         System.currentTimeMillis(),
                                                         0,
                                                         DateUtils.FORMAT_ABBREV_RELATIVE));*/
////////// Уровень сигнала
        /*text = (TextView)findViewById(R.id.signal_level);
        text.setText(Integer.toString(status.getSignalLevel()));
////////// Баланс у сотового оператора
        text = (TextView)findViewById(R.id.balance);
        text.setText(Float.toString(status.getSimProviderBalance()));*/
      }
    }
  }
}