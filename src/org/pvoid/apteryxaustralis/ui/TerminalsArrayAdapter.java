package org.pvoid.apteryxaustralis.ui;

import org.pvoid.apteryxaustralis.R;
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
  private Context _Context;
  
  public TerminalsArrayAdapter(Context context, int resource)
  {
    super(context, resource);
    _Context = context;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    View view = convertView;
    if(view==null)
    {
      LayoutInflater inflater = (LayoutInflater)_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
      	icon.setImageResource(R.drawable.terminal_active);
      }
      else
      {
      	String printer_state = status_record.getPrinterErrorId();
      	String note_state = status_record.getNoteErrorId();
      	if(printer_state!=null && !printer_state.equalsIgnoreCase("OK"))
      	{
      		status.setText(printer_state);
      		status.setVisibility(View.VISIBLE);
      		icon.setImageResource(R.drawable.terminal_inactive);
      	}
      	else if(note_state!=null && !note_state.equalsIgnoreCase("OK"))
      	{
      		status.setText(note_state);
      		status.setVisibility(View.VISIBLE);
      		icon.setImageResource(R.drawable.terminal_inactive);
      	}
      	else
      	{
      		status.setText(DateUtils.getRelativeTimeSpanString(status_record.getLastActivityDate(),System.currentTimeMillis(), 0, DateUtils.FORMAT_ABBREV_RELATIVE));
      		status.setVisibility(View.VISIBLE);
      		icon.setImageResource(R.drawable.terminal_active);
      	}
      }
    }
     
    return(view);
  }
}
