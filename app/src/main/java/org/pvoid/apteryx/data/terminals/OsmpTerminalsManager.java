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
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import org.pvoid.apteryx.annotations.GuardedBy;
import org.pvoid.apteryx.data.Storage;
import org.pvoid.apteryx.data.persons.Person;
import org.pvoid.apteryx.net.NetworkService;
import org.pvoid.apteryx.net.OsmpInterface;
import org.pvoid.apteryx.net.OsmpRequest;
import org.pvoid.apteryx.net.OsmpResponse;
import org.pvoid.apteryx.net.ResultReceiver;
import org.pvoid.apteryx.net.commands.GetTerminalsCashCommand;
import org.pvoid.apteryx.net.commands.GetTerminalsCommand;
import org.pvoid.apteryx.net.commands.GetTerminalsStatisticalDataCommand;
import org.pvoid.apteryx.net.commands.GetTerminalsStatusCommand;
import org.pvoid.apteryx.net.results.GetTerminalsCashResult;
import org.pvoid.apteryx.net.results.GetTerminalsResult;
import org.pvoid.apteryx.net.results.GetTerminalsStatisticalDataResult;
import org.pvoid.apteryx.net.results.GetTerminalsStatusResult;
import org.pvoid.apteryx.util.ArrayUtils;
import org.pvoid.apteryx.util.SearchComparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/* package */ class OsmpTerminalsManager implements TerminalsManager {
    @NonNull private final Context mContext;
    @NonNull private final Storage mStorage;
    private final ReentrantLock mLock = new ReentrantLock();
    // TODO: use normal tree with indexes
    @GuardedBy("mLock") private final Map<String, Terminal> mTerminalsById = new HashMap<>();
    @GuardedBy("mLock") private Terminal[] mTerminalsByAgent;
    private final TerminalByAgentComparator mCompareByAgent = new TerminalByAgentComparator();
    private final TerminalByAgentSearchComparator mSearchByAgent = new TerminalByAgentSearchComparator();

    public OsmpTerminalsManager(@NonNull Context context, @NonNull Storage storage) {
        mContext = context.getApplicationContext();
        mStorage = storage;

        Terminal[] terminals = mStorage.getTerminals();
        if (terminals != null) {
            mTerminalsByAgent = new Terminal[terminals.length];
            for (int index = 0; index < terminals.length; ++index) {
                Terminal terminal = terminals[index];
                mTerminalsById.put(terminal.getId(), terminal);
                mTerminalsByAgent[index] = terminal;
            }
            Arrays.sort(mTerminalsByAgent, mCompareByAgent);
        }
        TerminalState states[] = mStorage.getTerminalStates();
        if (states != null) {
            for (TerminalState state : states) {
                Terminal terminal = mTerminalsById.get(state.getId());
                if (terminal != null) {
                    terminal.setState(state);
                }
            }
        }
        TerminalStats stats[] = mStorage.getTerminalStats();
        if (stats != null) {
            for (TerminalStats stat : stats) {
                Terminal terminal = mTerminalsById.get(stat.getTerminalId());
                if (terminal != null) {
                    terminal.setStats(stat);
                }
            }
        }
        TerminalCash cashes[] = mStorage.getTerminalsCash();
        if (cashes != null) {
            for (TerminalCash cash :cashes) {
                Terminal terminal = mTerminalsById.get(cash.getTerminalId());
                if (terminal != null) {
                    terminal.setCash(cash);
                }
            }
        }
    }

    private void storeTerminals(@NonNull String person, @NonNull Terminal... terminals) {
        mLock.lock();
        try {
            for (Terminal terminal : terminals) {
                mTerminalsById.put(terminal.getId(), terminal);
            }
            Terminal[] t = new Terminal[mTerminalsByAgent.length + terminals.length];
            System.arraycopy(mTerminalsByAgent, 0, t, 0, mTerminalsByAgent.length);
            System.arraycopy(terminals, 0, t, mTerminalsByAgent.length, terminals.length);
            mTerminalsByAgent = t;
            Arrays.sort(mTerminalsByAgent, mCompareByAgent);
        } finally {
            mLock.unlock();
        }
        mStorage.storeTerminals(person, terminals);
    }

    private void storeStates(@NonNull TerminalState... states) {
        mLock.lock();
        try {
            for (TerminalState state : states) {
                Terminal terminal = mTerminalsById.get(state.getId());
                if (terminal != null) {
                    terminal.setState(state);
                }
            }
        } finally {
            mLock.unlock();
        }
        mStorage.storeTerminalStates(states);
    }

    private void storeStats(@NonNull TerminalStats... stats) {
        mLock.lock();
        try {
            for (TerminalStats stat : stats) {
                Terminal terminal = mTerminalsById.get(stat.getTerminalId());
                if (terminal != null) {
                    terminal.setStats(stat);
                }
            }
        } finally {
            mLock.unlock();
        }
        mStorage.storeTerminalStats(stats);
    }

    private void storeCash(@NonNull TerminalCash... cashs) {
        mLock.lock();
        try {
            for (TerminalCash cash : cashs) {
                Terminal terminal = mTerminalsById.get(cash.getTerminalId());
                if (terminal != null) {
                    terminal.setCash(cash);
                }
            }
        } finally {
            mLock.unlock();
        }
        mStorage.storeTerminalsCash(cashs);
    }

    @Override
    @NonNull
    public Terminal[] getTerminals(@Nullable String agentId) {
        if (TextUtils.isEmpty(agentId)) {
            return mTerminalsByAgent;
        }
        mLock.lock();
        try {
            int start = ArrayUtils.binarySearchLeft(mTerminalsByAgent, agentId, mSearchByAgent);
            if (start == -1) {
                return new Terminal[0];
            }
            int end = ArrayUtils.binarySearchRight(mTerminalsByAgent, agentId, mSearchByAgent);
            Terminal result[] = new Terminal[end - start + 1];
            System.arraycopy(mTerminalsByAgent, start, result, 0, result.length);
            return result;
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void syncFull(@NonNull Person person) {
        OsmpRequest.Builder builder = new OsmpRequest.Builder(person);
        builder.getInterface(OsmpInterface.Terminals).add(new GetTerminalsCommand(true));
        builder.getInterface(OsmpInterface.Reports).add(new GetTerminalsStatusCommand());
        builder.getInterface(OsmpInterface.Reports).add(new GetTerminalsStatisticalDataCommand());
        builder.getInterface(OsmpInterface.Reports).add(new GetTerminalsCashCommand());
        OsmpRequest request = builder.create();
        if (request != null) {
            NetworkService.executeRequest(mContext, request, new TerminalsListReceiver(person));
        }
    }

    @Override
    public void sync(@NonNull Person person) {
        OsmpRequest.Builder builder = new OsmpRequest.Builder(person);
        builder.getInterface(OsmpInterface.Reports).add(new GetTerminalsStatusCommand());
        builder.getInterface(OsmpInterface.Reports).add(new GetTerminalsStatisticalDataCommand());
        builder.getInterface(OsmpInterface.Reports).add(new GetTerminalsCashCommand());
        OsmpRequest request = builder.create();
        if (request != null) {
            NetworkService.executeRequest(mContext, request, new TerminalsStateReceiver());
        }
    }

    private class TerminalsListReceiver implements ResultReceiver {

        private final String mPersonLogin;

        TerminalsListReceiver(@NonNull Person person) {
            mPersonLogin = person.getLogin();
        }

        @Override
        public void onResponse(@NonNull OsmpResponse response) {
            boolean notify = false;
            OsmpResponse.Results results = response.getInterface(OsmpInterface.Terminals);
            if (results != null) {
                GetTerminalsResult terminalsResult = results.get(GetTerminalsCommand.NAME);
                if (terminalsResult != null && terminalsResult.getTerminals() != null) {
                    storeTerminals(mPersonLogin, terminalsResult.getTerminals());
                    notify = true;
                }
            }
            results = response.getInterface(OsmpInterface.Reports);
            if (results != null) {
                GetTerminalsStatusResult statusResult = results.get(GetTerminalsStatusCommand.NAME);
                if (statusResult != null && statusResult.getStates() != null) {
                    storeStates(statusResult.getStates());
                }
                GetTerminalsStatisticalDataResult statsResult = results.get(GetTerminalsStatisticalDataCommand.NAME);
                if (statsResult != null && statsResult.getStats() != null) {
                    storeStats(statsResult.getStats());
                }
                GetTerminalsCashResult cashResult = results.get(GetTerminalsCashCommand.NAME);
                if (cashResult != null && cashResult.getCash() != null) {
                    storeCash(cashResult.getCash());
                }
            }

            if (notify) {
                LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mContext);
                lbm.sendBroadcast(new Intent(ACTION_CHANGED));
            }
        }

        @Override
        public void onError() {

        }
    }

    private class TerminalsStateReceiver implements ResultReceiver {
        @Override
        public void onResponse(@NonNull OsmpResponse response) {
            OsmpResponse.Results results = response.getInterface(OsmpInterface.Reports);
            if (results != null) {
                boolean notify = false;
                GetTerminalsStatusResult statusResult = results.get(GetTerminalsStatusCommand.NAME);
                if (statusResult != null && statusResult.getStates() != null) {
                    storeStates(statusResult.getStates());
                    notify = true;
                }
                GetTerminalsStatisticalDataResult statsResult = results.get(GetTerminalsStatisticalDataCommand.NAME);
                if (statsResult != null && statsResult.getStats() != null) {
                    storeStats(statsResult.getStats());
                    notify = true;
                }
                GetTerminalsCashResult cashResult = results.get(GetTerminalsCashCommand.NAME);
                if (cashResult != null && cashResult.getCash() != null) {
                    storeCash(cashResult.getCash());
                    notify = true;
                }
                if (notify) {
                    LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mContext);
                    lbm.sendBroadcast(new Intent(ACTION_CHANGED));
                }
            }
        }

        @Override
        public void onError() {

        }
    }

    private static class TerminalByAgentComparator implements Comparator<Terminal> {
        @Override
        public int compare(Terminal left, Terminal right) {
            if (left == right) {
                return 0;
            }
            if (left == null) {
                return -1;
            } else if (right == null) {
                return 1;
            }

            return left.getAgentId().compareTo(right.getAgentId());
        }
    }

    private static class TerminalByAgentSearchComparator implements SearchComparator<String, Terminal> {
        @Override
        public int compare(String needle, Terminal value) {
            return needle.compareTo(value.getAgentId());
        }
    }
}
