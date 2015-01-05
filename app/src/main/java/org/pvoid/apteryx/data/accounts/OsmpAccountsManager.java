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

package org.pvoid.apteryx.data.accounts;

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
import org.pvoid.apteryx.net.commands.GetAgentInfoCommand;
import org.pvoid.apteryx.net.commands.GetAgentsCommand;
import org.pvoid.apteryx.net.results.GetAgentInfoResult;
import org.pvoid.apteryx.net.results.GetAgentsResult;
import org.pvoid.apteryx.util.LogHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* package */ class OsmpAccountsManager implements AccountsManager {

    private static final String TAG = "AccountManager";

    @NonNull private final Context mContext;
    @NonNull private final Storage mStorage;
    @NonNull private final Lock mLock = new ReentrantLock();
    private final Map<String, Account> mAccounts = new HashMap<>();

    /* package */ OsmpAccountsManager(@NonNull Context context, @NonNull Storage storage) {
        mStorage = storage;
        mContext = context.getApplicationContext();
    }

    @Override
    public boolean add(@NonNull Account account) {
        mLock.lock();
        try {
            if (mAccounts.containsKey(account.getLogin())) {
                return false;
            }
            mAccounts.put(account.getLogin(), account);
        } finally {
            mLock.unlock();
        }
        mStorage.storeAccount(account);
        return true;
    }

    @Override
    public void verify(@NonNull Account account) {
        OsmpRequest.Builder builder = new OsmpRequest.Builder(account);
        builder.getInterface(OsmpInterface.Agents).add(new GetAgentInfoCommand());
        builder.getInterface(OsmpInterface.Agents).add(new GetAgentsCommand());
        OsmpRequest request = builder.create();
        if (request != null) {
            NetworkService.executeRequest(mContext, request,
                    new VerificatioResultReceiver(account.getLogin()));
        }
    }

    private class VerificatioResultReceiver implements ResultReceiver {

        @NonNull private final String mLogin;

        private VerificatioResultReceiver(@NonNull String login) {
            mLogin = login;
        }

        @Override
        public void onResponse(@NonNull OsmpResponse response) {
            OsmpResponse.Results results = response.getInterface(OsmpInterface.Agents);
            if (results == null) {
                LogHelper.error(TAG, "Error while verifying account. Can't get <agents> session.");
                return;
            }
            GetAgentInfoResult info = results.get(GetAgentInfoCommand.NAME);
            Account account = null;
            mLock.lock();
            try {
                account = mAccounts.get(mLogin);
                final String name = info.getAgentName();
                final String id = info.getAgentId();
                if (name != null && id != null) {
                    account = account.cloneVerified(name, id);
                    mAccounts.put(mLogin, account);
                } else {
                    LogHelper.error(TAG, "Account name is NULL");
                }
            } finally {
                mLock.unlock();
            }
            mStorage.updateAccount(account);

            Intent intent = new Intent(ACTION_VERIFIED);
            intent.putExtra(EXTRA_ACCOUNT, account);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

            GetAgentsResult agentsResult = results.get(GetAgentsCommand.NAME);
            if (agentsResult != null && agentsResult.getAgents() != null) {
                mStorage.storeAgents(agentsResult.getAgents());
            }
        }

        @Override
        public void onError() {
            LogHelper.error(TAG, "Error while verifying account.");
        }
    }
}
