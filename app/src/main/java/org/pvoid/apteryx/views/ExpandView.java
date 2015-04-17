/*
 * Copyright (C) 2010-2015  Dmitry "PVOID" Petuhov
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

package org.pvoid.apteryx.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.pvoid.apteryx.R;

public class ExpandView extends LinearLayout {

    private static final int DURATION = 300;

    private ImageView mIcon;
    private TextView mText;
    private boolean mIsExpanded = false;
    private CharSequence mTextMore;
    private CharSequence mTextLess;
    private OnExpandListener mOnExpandListener;

    public ExpandView(Context context) {
        super(context);
        initView(context);
    }

    public ExpandView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.expandViewStyle);
        initView(context);
        initAttrs(context, attrs, 0);
    }

    public ExpandView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initAttrs(context, attrs, defStyleAttr);
    }

    private void initView(@NonNull Context context) {
        inflate(context, R.layout.view_expand_view, this);
        mIcon = (ImageView) findViewById(R.id.expand_view_image);
        mText = (TextView) findViewById(R.id.expand_view_text);
        setOnClickListener(new LineClickListener());
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandView, defStyleAttr,
                R.style.Widget_ExpandView);
        for (int index = 0, length = a.getIndexCount(); index < length; ++index) {
            int attr = a.getIndex(index);
            switch (attr) {
                case R.styleable.ExpandView_textMore:
                    mTextMore = a.getText(attr);
                    break;
                case R.styleable.ExpandView_textLess:
                    mTextLess = a.getText(attr);
                    break;
            }
        }
        a.recycle();

        if (!TextUtils.isEmpty(mTextMore)) {
            mText.setText(mTextMore);
        }
    }

    public void setOnExpandListener(OnExpandListener onExpandListener) {
        mOnExpandListener = onExpandListener;
    }

    private class LineClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (mIsExpanded) {
                mIcon.animate().rotation(0).setDuration(DURATION).start();
                mText.setText(mTextMore);
                mIsExpanded = false;
                if (mOnExpandListener != null) {
                    mOnExpandListener.onCollapse();
                }
            } else {
                mIcon.animate().rotation(180).setDuration(DURATION).start();
                mText.setText(mTextLess);
                mIsExpanded = true;
                if (mOnExpandListener != null) {
                    mOnExpandListener.onExpand();
                }
            }
        }
    }

    public interface OnExpandListener {
        void onExpand();
        void onCollapse();
    }
}
