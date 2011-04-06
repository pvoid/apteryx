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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.accounts.Terminal;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TerminalsArrayAdapter extends ArrayAdapter<Terminal>
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
    
    Terminal terminal = getItem(position);
    if(terminal!=null)
    {
      TextView agent_name = (TextView)view.findViewById(R.id.agent_name);
      TextView name = (TextView)view.findViewById(R.id.list_title);
      TextView printer_status = (TextView)view.findViewById(R.id.printer_status);
      TextView cashbin_status = (TextView)view.findViewById(R.id.cachebin_status);
      TextView cash = (TextView)view.findViewById(R.id.terminal_cash);
      TextView lastActivity = (TextView)view.findViewById(R.id.last_activity);
      ImageView icon = (ImageView)view.findViewById(R.id.icon);
      
      lastActivity.setVisibility(View.GONE);
      
      if(terminal.id()==null)
      {
        agent_name.setText(Html.fromHtml("<b>"+terminal.agentName+"</b> ("+terminal.cash+")"));
        agent_name.setVisibility(View.VISIBLE);
        name.setVisibility(View.GONE);
        printer_status.setVisibility(View.GONE);
        cashbin_status.setVisibility(View.GONE);
        cash.setVisibility(View.GONE);
        icon.setVisibility(View.GONE);
        view.setEnabled(false);
      }
      else
      {
        agent_name.setVisibility(View.GONE);
        name.setVisibility(View.VISIBLE);
        cash.setVisibility(View.VISIBLE);
        icon.setVisibility(View.VISIBLE);
        view.setEnabled(true);
        
        name.setText(terminal.Address());
        String state;
        
        if(!terminal.printer_state.equalsIgnoreCase("OK"))
        {
          state = "<b>"+_Context.getString(R.string.printer)+": </b>" + terminal.printer_state; 
          printer_status.setText(Html.fromHtml(state));
          printer_status.setVisibility(View.VISIBLE);
        }
        else
          printer_status.setVisibility(View.GONE);
        
        if(!terminal.cashbin_state.equalsIgnoreCase("OK"))
        {
          state = "<b>"+_Context.getString(R.string.cachebin)+": </b>" + terminal.cashbin_state;
          cashbin_status.setText(Html.fromHtml(state));
          cashbin_status.setVisibility(View.VISIBLE);
        }
        else
          cashbin_status.setVisibility(View.GONE);
        
        cash.setText(Html.fromHtml("<b>"+_Context.getString(R.string.fullinfo_cash)+" </b>"+terminal.cash));
        
  
        int icon_id;
        switch(terminal.State())
        {
          case Terminal.STATE_OK:
            icon_id = R.drawable.terminal_active;
            break;
          case Terminal.STATE_WARRNING:
            icon_id = R.drawable.terminal_pending;
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            TimeZone timezone = TimeZone.getTimeZone("Europe/Moscow");
            format.setTimeZone(timezone);
            Date dt;
            try
            {
              dt = format.parse(terminal.lastActivity);
              lastActivity.setText(DateUtils.getRelativeTimeSpanString(dt.getTime(),System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));
              lastActivity.setVisibility(View.VISIBLE);
            }
            catch (ParseException e)
            {
              e.printStackTrace();
            }
            break;
          default:
            icon_id = R.drawable.terminal_inactive;
        }
        icon.setImageResource(icon_id);
      }
    }
     
    return(view);
  }
}
