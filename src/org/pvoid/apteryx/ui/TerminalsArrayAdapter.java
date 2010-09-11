package org.pvoid.apteryx.ui;

import org.pvoid.apteryx.R;
import org.pvoid.apteryx.accounts.Terminal;

import android.content.Context;
import android.text.Html;
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
      ImageView icon = (ImageView)view.findViewById(R.id.icon);
      
      if(terminal.id()==null)
      {
        agent_name.setText(Html.fromHtml("<b>"+terminal.agentName+"</b>"));
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
