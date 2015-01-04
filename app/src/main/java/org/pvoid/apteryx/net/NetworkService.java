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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import org.pvoid.apteryx.GraphHolder;

import dagger.ObjectGraph;

public class NetworkService extends Service {

    private final static String EXTRA_REQUEST = "request";
    private final static String EXTRA_RESULT_INTENT = "intent";

    public final static String EXTRA_STATE = "state";
    public final static String EXTRA_RESULT = "result";

    public final static int STATE_OK = 0;
    public final static int STATE_ERROR = 1;

    private final RequestExecutor mExecutor;

    public NetworkService() {
        ObjectGraph graph = ((GraphHolder)getApplication()).getGraph();
        mExecutor = graph.get(RequestExecutor.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final OsmpRequest request = intent.getParcelableExtra(EXTRA_REQUEST);
        final Intent resultIntent = intent.getParcelableExtra(EXTRA_RESULT_INTENT);
        mExecutor.execute(request, new ResultReceiverWrap(startId, resultIntent));
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ResultReceiverWrap implements ResultReceiver {

        private final int mStartId;
        private final Intent mResultIntent;

        private ResultReceiverWrap(int startId, Intent resultIntent) {
            mStartId = startId;
            mResultIntent = resultIntent;
        }

        @Override
        public void onResponse(OsmpResponse response) {
            mResultIntent.putExtra(EXTRA_STATE, STATE_OK);
            mResultIntent.putExtra(EXTRA_RESULT, response);
            stopSelf(mStartId);
        }

        @Override
        public void onError() {
            mResultIntent.removeExtra(EXTRA_RESULT);
            mResultIntent.putExtra(EXTRA_STATE, STATE_ERROR);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(mResultIntent);
            stopSelf(mStartId);
        }
    }
}
