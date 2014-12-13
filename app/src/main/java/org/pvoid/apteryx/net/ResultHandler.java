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

package org.pvoid.apteryx.net;

import android.support.annotation.Nullable;

/* package */ class ResultHandler implements RequestHandle {

    @Nullable
    private volatile ResultReceiver mResultReceiver;
    private volatile boolean mCanceled = false;
    private volatile boolean mIsPending = false;

    public ResultHandler(@Nullable ResultReceiver receiver) {
        mResultReceiver = receiver;
    }

    public void onError() {
        mIsPending = false;
        ResultReceiver receiver = mResultReceiver;
        if (receiver != null) {
            receiver.onError();
        }
    }

    public void onSuccess(OsmpResponse response) {
        mIsPending = false;
        ResultReceiver receiver = mResultReceiver;
        if (receiver != null) {
            receiver.onResponse(response);
        }
    }

    public boolean isCanceled() {
        return mCanceled;
    }

    @Override
    public boolean isPending() {
        return !mCanceled && mIsPending;
    }

    @Override
    public void cancel() {
        mCanceled = true;
        mResultReceiver = null;
    }

    public void markPending() {
        mIsPending = true;
    }
}
