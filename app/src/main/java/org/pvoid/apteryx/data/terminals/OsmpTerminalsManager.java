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

package org.pvoid.apteryx.data.terminals;

import android.content.Context;
import android.support.annotation.NonNull;

import org.pvoid.apteryx.annotations.GuardedBy;
import org.pvoid.apteryx.data.Storage;
import org.pvoid.apteryx.data.persons.Person;
import org.pvoid.apteryx.net.NetworkService;
import org.pvoid.apteryx.net.OsmpInterface;
import org.pvoid.apteryx.net.OsmpRequest;
import org.pvoid.apteryx.net.OsmpResponse;
import org.pvoid.apteryx.net.ResultReceiver;
import org.pvoid.apteryx.net.commands.GetTerminalsCommand;
import org.pvoid.apteryx.net.results.GetTerminalsResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/* package */ class OsmpTerminalsManager implements TerminalsManager {
    @NonNull private final Context mContext;
    @NonNull private final Storage mStorage;
    private final ReentrantLock mLock = new ReentrantLock();
    @GuardedBy("mLock")
    private final Map<String, Map<String, Terminal>> mTerminals = new HashMap<>();

    public OsmpTerminalsManager(@NonNull Context context, @NonNull Storage storage) {
        mContext = context.getApplicationContext();
        mStorage = storage;
    }

    @Override
    public void store(@NonNull String person, @NonNull Terminal... terminals) {
        mLock.lock();
        try {
            Map<String, Terminal> t = mTerminals.get(person);
            if (t == null) {
                t = new HashMap<>();
                mTerminals.put(person, t);
            }
            for (Terminal terminal : terminals) {
                t.put(terminal.getId(), terminal);
            }
        } finally {
            mLock.unlock();
        }
        mStorage.storeTerminals(person, terminals);
    }

    @Override
    public void sync(@NonNull Person person) {
        OsmpRequest.Builder builder = new OsmpRequest.Builder(person);
        builder.getInterface(OsmpInterface.Terminals).add(new GetTerminalsCommand(true));
        OsmpRequest request = builder.create();
        if (request != null) {
            NetworkService.executeRequest(mContext, request, new TerminalsResultReceiver(person));
        }
    }

    private class TerminalsResultReceiver implements ResultReceiver {

        private final String mPersonLogin;

        TerminalsResultReceiver(@NonNull Person person) {
            mPersonLogin = person.getLogin();
        }

        @Override
        public void onResponse(@NonNull OsmpResponse response) {
            OsmpResponse.Results results = response.getInterface(OsmpInterface.Terminals);
            if (results != null) {
                GetTerminalsResult terminalsResult = results.get(GetTerminalsCommand.NAME);
                if (terminalsResult != null && terminalsResult.getTerminals() != null) {
                    store(mPersonLogin, terminalsResult.getTerminals());
                }
            }
        }

        @Override
        public void onError() {

        }
    }
}
