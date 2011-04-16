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

package org.pvoid.apteryxaustralis.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.pvoid.apteryxaustralis.R;

public class FullInfoItem extends RelativeLayout
{
  private static final int LABEL_ID = 1;
  private static final int VALUE_ID = 2;

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
    final float scale = context.getResources().getDisplayMetrics().density;

    TextView label = new TextView(context);
    LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    params.addRule(RelativeLayout.ALIGN_PARENT_TOP,TRUE);
    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,TRUE);
    int margin = (int)(5*scale+0.5f);
    params.setMargins(margin,margin,margin,0);
    if(attrs!=null)
    {
      int nameId =  attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android","text",-1);
      if(nameId>-1)
      {
        String name = context.getResources().getString(nameId);
        if(!TextUtils.isEmpty(name))
          label.setText(name);
      }
    }
    label.setId(LABEL_ID);

    TypedArray styles = context.obtainStyledAttributes(R.styleable.text);
    if(styles!=null)
    {
      int resId = styles.getResourceId(R.styleable.text_android_textAppearanceSmall,-1);
      if(resId!=-1)
        label.setTextAppearance(context,resId);
      label.setTextColor(styles.getColor(R.styleable.text_android_textColorPrimary, Color.WHITE));
    }
    addView(label, params);

    TextView text = new TextView(context);
    params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,TRUE);
    params.addRule(RelativeLayout.BELOW, label.getId());
    params.setMargins(margin,margin,margin,0);
    text.setId(VALUE_ID);
    if(styles!=null)
    {
      int resId = styles.getResourceId(R.styleable.text_android_textAppearanceMedium,-1);
      if(resId!=-1)
        text.setTextAppearance(context,resId);
      text.setTextColor(styles.getColor(R.styleable.text_android_textColorPrimary, Color.WHITE));
    }
    addView(text,params);

    View line = new View(context);
    line.setBackgroundResource(R.drawable.list_line);
    params = new LayoutParams(LayoutParams.WRAP_CONTENT,(int)(scale+0.5f));
    params.addRule(RelativeLayout.BELOW,text.getId());
    params.setMargins(0,(int)(5*scale+0.5f),0,0);
    addView(line, params);
  }

  public void setText(String text)
  {
    TextView view = (TextView)findViewById(VALUE_ID);
    if(view!=null)
      view.setText(text);
  }

  public void setText(CharSequence text)
  {
    TextView view = (TextView)findViewById(VALUE_ID);
    if(view!=null)
      view.setText(text);
  }

  public void setLabel(String text)
  {
    TextView view = (TextView)findViewById(LABEL_ID);
    if(view!=null)
      view.setText(text);
  }
}
