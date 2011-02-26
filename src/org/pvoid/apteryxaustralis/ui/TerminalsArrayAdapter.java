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

import android.text.TextUtils;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.types.Agent;
import org.pvoid.apteryxaustralis.types.Payment;
import org.pvoid.apteryxaustralis.types.TerminalListRecord;
import org.pvoid.apteryxaustralis.types.TerminalStatus;

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
    
    TerminalListRecord listRecord = getItem(position);
    if(listRecord!=null)
    {
      TextView name = (TextView)view.findViewById(R.id.list_title);
      name.setText(listRecord.toString());
      TextView status = (TextView) view.findViewById(R.id.status);
      ImageView icon = (ImageView)view.findViewById(R.id.icon);
      TerminalStatus status_record = listRecord.getStatus();
      Payment payment_record = listRecord.getPayment();
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
              long date;
              StringBuffer statusText = new StringBuffer();
              if(payment_record!=null && (date = payment_record.getDateInTerminal())!=0)
              {
                statusText.append(getContext().getString(R.string.last_payment));
                statusText.append(' ');
                statusText.append(DateUtils.getRelativeTimeSpanString(date,
                                                                      System.currentTimeMillis(),
                                                                      DateUtils.SECOND_IN_MILLIS,
                                                                      DateUtils.FORMAT_ABBREV_RELATIVE));
              }
              else
              {
                statusText.append(getContext().getString(R.string.last_activity));
                statusText.append(' ');
                statusText.append(DateUtils.getRelativeTimeSpanString(status_record.getLastActivityDate(),
                                                                      System.currentTimeMillis(),
                                                                      DateUtils.SECOND_IN_MILLIS,
                                                                      DateUtils.FORMAT_ABBREV_RELATIVE));
              }
              status.setText(statusText.toString());
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
