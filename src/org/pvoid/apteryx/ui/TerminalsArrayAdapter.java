package org.pvoid.apteryx.ui;

import org.pvoid.apteryx.R;
import org.pvoid.apteryx.accounts.Terminal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
      TextView name = (TextView)view.findViewById(R.id.list_title);
      name.setText(terminal.Address());
      
      TextView status = (TextView)view.findViewById(R.id.printer_status);
      String state = "<b>"+_Context.getString(R.string.printer)+": </b>" + terminal.printer_state; 
      status.setText(Html.fromHtml(state));
      
      status = (TextView)view.findViewById(R.id.cachebin_status);
      state = "<b>"+_Context.getString(R.string.cachebin)+": </b>" + terminal.cashbin_state;
      status.setText(Html.fromHtml(state));
      
      ImageView icon = (ImageView)view.findViewById(R.id.icon);
      int icon_id;
      switch(terminal.State())
      {
        case Terminal.STATE_OK:
          icon_id = R.drawable.icon;
          break;
        case Terminal.STATE_WARRNING:
          icon_id = R.drawable.yellow;
          break;
        default:
          icon_id = R.drawable.red;
      }
      
      Bitmap bitmap = BitmapFactory.decodeResource(_Context.getResources(), icon_id);
      icon.setImageBitmap(bitmap);
    }
    
    return(view);
  }
}
