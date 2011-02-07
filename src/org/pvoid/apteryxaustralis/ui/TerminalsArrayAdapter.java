package org.pvoid.apteryxaustralis.ui;

import android.text.TextUtils;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.accounts.Agent;
import org.pvoid.apteryxaustralis.accounts.TerminalListRecord;
import org.pvoid.apteryxaustralis.accounts.TerminalStatus;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TerminalsArrayAdapter extends ArrayAdapter<TerminalListRecord>
{
  private long _mAgentId;
  private String _mAgentName;

  public TerminalsArrayAdapter(Context context, Agent agent, int resource, int textViewResourceId)
  {
    super(context, resource, textViewResourceId);
    _mAgentId = agent.getId();
    _mAgentName = agent.getName();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    Context context = getContext();
    View view = convertView;
    if(view==null)
    {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.terminal, null);
    }
    
    TerminalListRecord terminal = getItem(position);
    if(terminal!=null)
    {
      TextView name = (TextView)view.findViewById(R.id.list_title);
      name.setText(terminal.toString());
      TextView status = (TextView) view.findViewById(R.id.status);
      ImageView icon = (ImageView)view.findViewById(R.id.icon);
      TerminalStatus status_record = terminal.getStatus();
      if(status_record==null)
      {
      	status.setVisibility(View.GONE);
      	icon.setImageResource(R.drawable.ic_terminal_active);
      }
      else
      {
        String printer_state = status_record.getPrinterErrorId();
        String note_state = status_record.getNoteErrorId();

        if(!TextUtils.isEmpty(note_state) && !note_state.equals("OK"))
        {
          icon.setImageResource(R.drawable.ic_terminal_inactive);
          status.setVisibility(View.VISIBLE);
          status.setText(note_state);
        }
        else if(!TextUtils.isEmpty(printer_state) && !printer_state.equals("OK"))
        {
          icon.setImageResource(R.drawable.ic_terminal_printer_error);
          status.setVisibility(View.VISIBLE);
          status.setText(printer_state);
        }
        else
          switch(status_record.getCommonState())
          {
            case TerminalStatus.STATE_COMMON_ERROR:
              icon.setImageResource(R.drawable.ic_terminal_inactive);
              status.setVisibility(View.VISIBLE);
              status.setText(status_record.getErrorText(context,false));
              // TODO: Разные иконки для принтера и для остального
              break;
            case TerminalStatus.STATE_COMMON_WARNING:
              icon.setImageResource(R.drawable.ic_terminal_pending);
              break;
            default:
              status.setText(DateUtils.getRelativeTimeSpanString(status_record.getLastActivityDate(),System.currentTimeMillis(), 0, DateUtils.FORMAT_ABBREV_RELATIVE));
              status.setVisibility(View.VISIBLE);
              icon.setImageResource(R.drawable.ic_terminal_active);
          }
      }
    }
     
    return(view);
  }

  public long getAgentId()
  {
    return _mAgentId;
  }

  public String getAgentName()
  {
    return _mAgentName;
  }
}
