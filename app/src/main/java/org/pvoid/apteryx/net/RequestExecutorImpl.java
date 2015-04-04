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

package org.pvoid.apteryx.net;

import android.content.ComponentName;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.pvoid.apteryx.util.log.Loggers;
import org.slf4j.Logger;

import java.util.Queue;

import dagger.internal.ArrayQueue;

public class RequestExecutorImpl implements RequestExecutor {

    private static final Logger LOG = Loggers.getLogger(Loggers.Network);

    @NonNull
    private final Queue<Message> mRequests = new ArrayQueue<>();
    @Nullable
    private volatile Messenger mMessenger = null;

    @Override
    public void execute(@NonNull OsmpRequest request, @Nullable ResultCallback callback) {
        Message message = Message.obtain(null, callback);
        message.what = NetworkService.MSG_SEND_REQUEST;
        message.obj = request;

        Messenger messenger = mMessenger;
        if (messenger != null) {
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                LOG.error("Can't send request", e);
                message.recycle();
            }
        } else {
            mRequests.add(message);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Message msg;
        mMessenger = new Messenger(service);
        while ((msg = mRequests.poll()) != null) {
            try {
                mMessenger.send(msg);
            } catch (RemoteException e) {
                LOG.error("Can't send request", e);
                msg.recycle();
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mMessenger = null;
        Message msg;
        while ((msg = mRequests.poll()) != null) {
            msg.recycle();
        }
    }
}
