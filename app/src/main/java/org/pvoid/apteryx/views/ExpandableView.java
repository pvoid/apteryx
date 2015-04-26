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
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.pvoid.apteryx.R;

public class ExpandableView extends ViewGroup {

    private static final int DEFAULT_VISIBLE_ITEMS = 5;

    private boolean mIsExpanded;
    @NonNull
    private final ExpandView mExpandView;
    @Nullable
    private ViewBuilder mViewBuilder;
    private boolean mNeedPopulate = false;
    private int mVisibleItems = DEFAULT_VISIBLE_ITEMS;
    private int mHeight;
    private int mFullHeight;

    public ExpandableView(Context context) {
        this(context, null);
    }

    public ExpandableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mExpandView = new ExpandView(context);
        initFromAttrs(context, attrs);

        TypedArray arr = context.obtainStyledAttributes(new int[]{android.R.attr.listPreferredItemHeightSmall});
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                arr.getDimensionPixelSize(0, LayoutParams.WRAP_CONTENT));
        arr.recycle();
        mExpandView.setLayoutParams(lp);

        mExpandView.setOnExpandListener(new ExpandView.OnExpandListener() {
            @Override
            public void onExpand() {
                mIsExpanded = true;
                requestLayout();
            }

            @Override
            public void onCollapse() {
                mIsExpanded = false;
                requestLayout();
            }
        });
    }

    private void initFromAttrs(@NonNull Context context, @NonNull AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableView);
        CharSequence textMore = "";
        CharSequence textLess = "";
        for (int index = 0, length = a.getIndexCount(); index < length; ++index) {
            int attr = a.getIndex(index);
            switch (attr) {
                case R.styleable.ExpandableView_textMore:
                    textMore = a.getText(attr);
                    break;
                case R.styleable.ExpandableView_textLess:
                    textLess = a.getText(attr);
                    break;
            }
        }
        a.recycle();
        mExpandView.setTexts(textMore, textLess);
    }

    private void populate() {
        if (mViewBuilder == null) {
            return;
        }
        for (int index = 0, length = mViewBuilder.getCount(); index < length; ++index) {
            addView(mViewBuilder.create(getContext(), index, this));
        }
        addView(mExpandView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mNeedPopulate) {
            populate();
            mNeedPopulate = false;
        }
        mFullHeight = 0;
        mHeight = 0;
        int width = 0;
        int index;
        for (index = 0; index < getChildCount() - 1; ++index) {
            View child = getChildAt(index);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            mFullHeight += child.getMeasuredHeight();
            if (index < mVisibleItems) {
                mHeight += child.getMeasuredHeight();
            }
            width = Math.max(width, child.getMeasuredWidth());
        }
        measureChild(mExpandView, widthMeasureSpec, heightMeasureSpec);
        if (index <= mVisibleItems) {
            mExpandView.setVisibility(GONE);
        } else {
            mFullHeight += mExpandView.getMeasuredHeight();
            if (mHeight > 0) {
                mHeight += mExpandView.getMeasuredHeight();
            }
        }
        int height = mIsExpanded ? mFullHeight : mHeight;
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                width = MeasureSpec.getSize(widthMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
                width = Math.min(width, MeasureSpec.getSize(widthMeasureSpec));
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                height = MeasureSpec.getSize(heightMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
                height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int top = getPaddingTop();
        for (int index = 0; index < getChildCount() - 1; ++index) {
            View child = getChildAt(index);
            child.layout(getPaddingLeft(), top, getPaddingLeft() + child.getMeasuredWidth(), top + child.getMeasuredHeight());
            top += child.getMeasuredHeight();
            if (!mIsExpanded && index >= mVisibleItems) {
                child.setVisibility(INVISIBLE);
            } else {
                child.setVisibility(VISIBLE);
            }
        }
        top = mIsExpanded ? mFullHeight : mHeight;
        mExpandView.layout(getPaddingLeft(), top - mExpandView.getMeasuredHeight(),
                getPaddingLeft() + mExpandView.getMeasuredWidth(), top);
    }

    public void setContent(@NonNull ViewBuilder viewBuilder) {
        mViewBuilder = viewBuilder;
        mNeedPopulate = true;
        requestLayout();
    }

    public interface ViewBuilder {
        @NonNull View create(@NonNull Context context, int index, @NonNull ViewGroup parent);
        int getCount();
    }

    public static class ExpandView extends LinearLayout {

        private static final int DURATION = 300;

        private ImageView mIcon;
        private TextView mText;
        private boolean mIsExpanded = false;
        private CharSequence mTextMore;
        private CharSequence mTextLess;
        private OnExpandListener mOnExpandListener;

        public ExpandView(Context context) {
            this(context, null);
        }

        public ExpandView(Context context, AttributeSet attrs) {
            this(context, attrs, R.attr.expandViewStyle);
        }

        public ExpandView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            initView(context);
        }

        private void initView(@NonNull Context context) {
            inflate(context, R.layout.view_expand_view, this);
            mIcon = (ImageView) findViewById(R.id.expand_view_image);
            mText = (TextView) findViewById(R.id.expand_view_text);
            setOnClickListener(new LineClickListener());
        }

        public void setTexts(@NonNull CharSequence moreItemsText, @NonNull CharSequence lessItemsText) {
            mTextMore = moreItemsText;
            mTextLess = lessItemsText;
            if (mIsExpanded) {
                mText.setText(mTextLess);
            } else {
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
}
