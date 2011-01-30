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
    int nameId =  attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android","text",-1);
    if(nameId>-1)
    {
      String name = context.getResources().getString(nameId);
      if(!TextUtils.isEmpty(name))
        label.setText(name);
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
}
