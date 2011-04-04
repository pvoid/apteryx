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
import org.pvoid.apteryxaustralis.types.DateFormat;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.preference.Preferences;
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
  private Agent _mAgent;

  public TerminalsArrayAdapter(Context context, Agent agent, int resource, int textViewResourceId)
  {
    super(context, resource, textViewResourceId);
    _mAgent = agent;
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
      Payment payment_record = null;

      if(Preferences.getReceivePayments(getContext()))
        payment_record = listRecord.getPayment();

      if(status_record==null)
      {
      	status.setVisibility(View.GONE);
      	icon.setImageResource(R.drawable.ic_terminal_unknown);
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
        {
          int image = R.drawable.ic_terminal_active;
          long paymentDate = 0;
          if(payment_record!=null)
            paymentDate = payment_record.getActualDate();
          StringBuffer statusText = new StringBuffer();
          switch(status_record.getCommonState(getContext()))
          {
            case TerminalStatus.STATE_COMMON_ERROR:
              image = R.drawable.ic_terminal_inactive;
              String statusMessage = status_record.getErrorText(context,false);
              if(!TextUtils.isEmpty(statusMessage))
                statusText.append(statusMessage);
              // TODO: Разные иконки для принтера и для остального
              break;
            case TerminalStatus.STATE_COMMON_WARNING:
              image = R.drawable.ic_terminal_pending;
              break;
            default:
              if(payment_record!=null && paymentDate>0 &&
                 (System.currentTimeMillis() - payment_record.getActualDate())>Preferences.getPaymentTimeout(getContext()))
                image = R.drawable.ic_terminal_pending;
          }
          status.setVisibility(View.VISIBLE);

          if(statusText.length()==0)
          {
            if(image == R.drawable.ic_terminal_inactive)
            {
              statusText.append(getContext().getString(R.string.last_activity));
              statusText.append(' ');
              statusText.append(DateFormat.formatDateSmart(getContext(), status_record.getLastActivityDate()));
            }
            else if(paymentDate>0 && payment_record!=null)
            {
              statusText.append(getContext().getString(R.string.last_payment));
              statusText.append(' ');
              statusText.append(DateFormat.formatDateSmart(getContext(),paymentDate));
            }
            else
            {
              statusText.append(getContext().getString(R.string.fullinfo_cash));
              statusText.append(' ');
              statusText.append(status_record.getCash());
            }
          }
          status.setText(statusText.toString());
          icon.setImageResource(image);
        }
      }
    }
     
    return(view);
  }

  public long getAgentId()
  {
    return _mAgent.getId();
  }

  public String getAgentName()
  {
    return _mAgent.getName();
  }

  public long getAgentUpdateTime()
  {
    return _mAgent.getUpdateDate();
  }

  public float getBalance()
  {
    return _mAgent.getBalance();
  }

  public float getOverdraft()
  {
    return _mAgent.getOverdraft();
  }
}
