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
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import org.pvoid.apteryx.BuildConfig;
import org.pvoid.apteryx.GraphHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dagger.ObjectGraph;

// TODO: add ability to stop request
public class NetworkService extends Service {

    private final static String EXTRA_REQUEST = "request";
    private final static String EXTRA_ID = "id";

    private RequestExecutor mExecutor;
    private static final Map<UUID, ResultReceiver> sReceivers = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        ObjectGraph graph = ((GraphHolder)getApplication()).getGraph();
        mExecutor = graph.get(RequestExecutor.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final OsmpRequest request = intent.getParcelableExtra(EXTRA_REQUEST);
        final UUID id = UUID.fromString(intent.getStringExtra(EXTRA_ID));
        mExecutor.execute(request, new ResultReceiverWrap(id, startId));
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void executeRequest(@NonNull Context context, @NonNull OsmpRequest request,
                                      @NonNull ResultReceiver receiver) {
        UUID id;
        synchronized (sReceivers) {
            if (BuildConfig.DEBUG && sReceivers.containsValue(receiver)) {
                throw new IllegalArgumentException("Receiver already registered");
            }
            id = UUID.randomUUID();
            sReceivers.put(id, receiver);
        }

        final Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(EXTRA_REQUEST, request);
        intent.putExtra(EXTRA_ID, id.toString());
        context.startService(intent);
    }

    private class ResultReceiverWrap implements ResultReceiver {

        private final int mStartId;
        private final UUID mId;

        private ResultReceiverWrap(@NonNull UUID id, int startId) {
            mId = id;
            mStartId = startId;
        }

        @Override
        public void onResponse(@NonNull OsmpResponse response) {
            ResultReceiver receiver;
            synchronized (sReceivers) {
                receiver = sReceivers.remove(mId);
            }
            stopSelf(mStartId);
            if (receiver != null) {
                receiver.onResponse(response);
            }
        }

        @Override
        public void onError() {
            ResultReceiver receiver;
            synchronized (sReceivers) {
                receiver = sReceivers.get(mId);
            }
            stopSelf(mStartId);
            if (receiver != null) {
                receiver.onError();
            }
        }
    }
}
