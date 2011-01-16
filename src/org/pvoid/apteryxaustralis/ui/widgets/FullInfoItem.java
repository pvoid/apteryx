package org.pvoid.apteryxaustralis.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.pvoid.apteryxaustralis.R;

public class FullInfoItem extends View
{
  public FullInfoItem(Context context)
  {
    super(context);
    setupUI(context,null);
  }

  public FullInfoItem(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    setupUI(context,attrs);
  }

  public FullInfoItem(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    setupUI(context,attrs);
  }

  protected void setupUI(Context context, AttributeSet attrs)
  {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.fullinfo_item,(ViewGroup)getParent(),true);
    /*TextView text = (TextView) findViewById(R.id.item_name);
    text.setText("Это имя");
    text = (TextView) findViewById(R.id.item_value);
    text.setText("А это значение");*/
  }
}
