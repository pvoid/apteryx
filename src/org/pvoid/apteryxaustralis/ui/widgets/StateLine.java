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
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.pvoid.apteryxaustralis.R;

public class StateLine extends ViewGroup
{
  private final static String TAG = StateLine.class.getName();

  private static final int TEXT_ID = 1;
  private static final int BULLET_ID = 2;

  private static final int BULLET_PADDING = 4;
  private float _mScaledDensity = 0;
  private float _mDensity = 0;

  public StateLine(Context context)
  {
    super(context);
    createUI(context);
  }

  public StateLine(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    createUI(context);
  }

  public StateLine(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    createUI(context);
  }

  private void createUI(Context context)
  {
    final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    _mScaledDensity = metrics.scaledDensity;
    _mDensity = metrics.density;

    TextView message = new TextView(context);

    TypedArray items = context.obtainStyledAttributes(R.styleable.text);
    int appearance = items.getResourceId(R.styleable.text_android_textAppearanceMedium,-1);
    if(appearance>0)
    {
      message.setTextAppearance(context,appearance);
    }

    message.setId(TEXT_ID);
    message.setTextColor(Color.WHITE);
    addView(message, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

    ImageView bullet = new ImageView(context);
    bullet.setImageResource(R.drawable.list_bullet);
    int sideSize = (int)(8f* _mDensity);
    bullet.setId(BULLET_ID);
    addView(bullet, new LayoutParams(sideSize, sideSize));
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
    ImageView bullet = (ImageView)findViewById(BULLET_ID);
    if(bullet==null)
    {
      Log.e(TAG,"bullet image not found");
      setMeasuredDimension(0,0);
      return;
    }
    TextView text = (TextView) findViewById(TEXT_ID);
    if(text==null)
    {
      Log.e(TAG,"text view not found");
      setMeasuredDimension(0,0);
      return;
    }
    measureChild(bullet,widthMeasureSpec,heightMeasureSpec);
    measureChild(text,widthMeasureSpec,heightMeasureSpec);
    LayoutParams params = text.getLayoutParams();
    int measuredWidth = getChildMeasureSpec(widthMeasureSpec,
                                            bullet.getMeasuredWidth()+getPaddingLeft()+getPaddingRight()+(int)(BULLET_PADDING* _mScaledDensity),
                                            params.width);
    text.measure(measuredWidth, MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY));
    int height = Math.max(bullet.getMeasuredHeight(),text.getMeasuredHeight());
    height = Math.max(height,MeasureSpec.getSize(heightMeasureSpec));
    height+=getPaddingBottom()+getPaddingTop();
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),height);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom)
  {
    top=getPaddingTop();
    left = getPaddingLeft();

    ImageView bullet = (ImageView)findViewById(BULLET_ID);
    TextView text = (TextView) findViewById(TEXT_ID);
    int localTop = (int) (text.getBaseline()-bullet.getMeasuredHeight() - 2* _mDensity);
    bullet.layout(left,localTop,left+bullet.getMeasuredWidth(),localTop+bullet.getMeasuredHeight());
    left+=bullet.getMeasuredWidth()+BULLET_PADDING* _mScaledDensity;
    text.layout(left,top,left+text.getMeasuredWidth(),top+text.getMeasuredHeight());
  }

  public void setText(String state)
  {
    TextView text = (TextView) findViewById(TEXT_ID);
    if(text!=null)
      text.setText(state);
  }
}
