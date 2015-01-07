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

package org.pvoid.apteryx.data.persons;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import org.pvoid.apteryx.data.Storage;
import org.pvoid.apteryx.net.NetworkService;
import org.pvoid.apteryx.net.OsmpInterface;
import org.pvoid.apteryx.net.OsmpRequest;
import org.pvoid.apteryx.net.OsmpResponse;
import org.pvoid.apteryx.net.ResultReceiver;
import org.pvoid.apteryx.net.commands.GetAgentsCommand;
import org.pvoid.apteryx.net.commands.GetPersonInfoCommand;
import org.pvoid.apteryx.net.results.GetPersonInfoResult;
import org.pvoid.apteryx.util.LogHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* package */ class OsmpPersonsManager implements PersonsManager {

    private static final String TAG = "AccountManager";

    @NonNull private final Context mContext;
    @NonNull private final Storage mStorage;
    @NonNull private final Lock mLock = new ReentrantLock();
    private final Map<String, Person> mPersons = new HashMap<>();

    /* package */ OsmpPersonsManager(@NonNull Context context, @NonNull Storage storage) {
        mStorage = storage;
        mContext = context.getApplicationContext();
    }

    @Override
    public boolean add(@NonNull Person person) {
        mLock.lock();
        try {
            if (mPersons.containsKey(person.getLogin())) {
                return false;
            }
            mPersons.put(person.getLogin(), person);
        } finally {
            mLock.unlock();
        }
        mStorage.storePerson(person);
        return true;
    }

    @Override
    public void verify(@NonNull Person person) {
        OsmpRequest.Builder builder = new OsmpRequest.Builder(person);
        builder.getInterface(OsmpInterface.Persons).add(new GetPersonInfoCommand());
        builder.getInterface(OsmpInterface.Agents).add(new GetAgentsCommand());
        OsmpRequest request = builder.create();

        if (request != null) {
            NetworkService.executeRequest(mContext, request,
                    new VerificatioResultReceiver());
        }
    }

    private class VerificatioResultReceiver implements ResultReceiver {

        @Override
        public void onResponse(@NonNull OsmpResponse response) {
            OsmpResponse.Results results = response.getInterface(OsmpInterface.Persons);
            if (results == null) {
                LogHelper.error(TAG, "Error while verifying account. Can't get <agents> session.");
                return;
            }
            GetPersonInfoResult info = results.get(GetPersonInfoCommand.NAME);
            Person person = null;
            mLock.lock();
            try {
                person = mPersons.get(info.getLogin());
                final String name = info.getPersonName();
                final String agentId = info.getAgentId();

                if (name != null && agentId != null) {
                    person = person.verify(agentId, name, person.isEnabled());
                    mPersons.put(person.getLogin(), person);
                } else {
                    LogHelper.error(TAG, "Account name is NULL");
                }
            } finally {
                mLock.unlock();
            }
            mStorage.storePerson(person);

            Intent intent = new Intent(ACTION_VERIFIED);
            intent.putExtra(EXTRA_PERSON, person);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

//            GetAgentsResult agentsResult = results.get(GetAgentsCommand.NAME);
//            if (agentsResult != null && agentsResult.getAgents() != null) {
//                mStorage.storeAgents(agentsResult.getAgents());
//            }
        }

        @Override
        public void onError() {
            LogHelper.error(TAG, "Error while verifying account.");
        }
    }
}
