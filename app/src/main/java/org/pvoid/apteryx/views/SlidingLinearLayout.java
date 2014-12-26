/*
 * Copyright (C) 2010-2014  Dmitry "PVOID" Petuhov
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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SlidingLinearLayout extends LinearLayout {
    public SlidingLinearLayout(Context context) {
        super(context);
    }

    public SlidingLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlidingLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public float getYFraction()
    {
        float height = getHeight();
        if (height == 0) {
            height = getResources().getDisplayMetrics().heightPixels;
        }
        return getY() / height;
    }

    public void setYFraction(float yFraction) {
        float height = getHeight();
        if (height == 0) {
            height = getResources().getDisplayMetrics().heightPixels;
        }
        setY(yFraction * height);
    }
}
