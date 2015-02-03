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

package org.pvoid.apteryx.net.results;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.pvoid.apteryx.util.LogHelper;

public abstract class Result {

    private static final String TAG = "Result";
    public static final int INVALID_VALUE = -1;

    public static final int ASYNC_STATE_PENDING = 1;
    public static final int ASYNC_STATE_PROCESSING = 2;
    public static final int ASYNC_STATE_DONE = 3;
    public static final int ASYNC_STATE_FAILED = 4;
    public static final int ASYNC_STATE_TIMEOUT = 5;
    public static final int ASYNC_STATE_DELETED = 6;

    public static final String ATTR_QUEUQ_ID = "quid";
    public static final String ATTR_RESULT = "result";
    public static final String ATTR_STATUS = "status";

    @NonNull
    private final String mName;
    private final int mQueueId;
    private final int mResult;
    private final int mStatus;

    protected Result(@NonNull ResponseTag root) {
        mName = root.getName();
        mQueueId = getIntAttribute(root, ATTR_QUEUQ_ID);
        mResult = getIntAttribute(root, ATTR_RESULT);
        mStatus = getIntAttribute(root, ATTR_STATUS);
    }

    private int getIntAttribute(@NonNull ResponseTag tag, @NonNull String attribute) {
        String val = tag.getAttribute(attribute);
        if (!TextUtils.isEmpty(val)) {
            try {
                return Integer.parseInt(val);
            } catch (NumberFormatException e) {
                LogHelper.warn(TAG, "Cant't convert '$1%s' to number: %2$s", attribute, e.getMessage());
            }
        }
        return INVALID_VALUE;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public int getQueueId() {
        return mQueueId;
    }

    public boolean isAsync() {
        return mQueueId != INVALID_VALUE;
    }

    public boolean isPending() {
        return isAsync() && (mStatus == ASYNC_STATE_PENDING || mStatus == ASYNC_STATE_PROCESSING);
    }

    public boolean isReady() {
        //noinspection SimplifiableIfStatement
        if (isAsync() && mStatus != ASYNC_STATE_DONE) {
            return false;
        }
        return mResult == 0;
    }

    public boolean isFailed() {
        if (isAsync()) {
            switch (mStatus) {
                case ASYNC_STATE_FAILED:
                case ASYNC_STATE_TIMEOUT:
                case ASYNC_STATE_DELETED:
                    return true;
            }
        }
        return mResult != 0; // TODO: change to constant
    }

    public int getResult() {
        return mResult;
    }

    public int getStatus() {
        return mStatus;
    }
}
