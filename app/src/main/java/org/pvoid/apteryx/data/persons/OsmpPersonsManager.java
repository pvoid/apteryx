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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import dagger.ObjectGraph;
import org.pvoid.apteryx.ApteryxApplication;
import org.pvoid.apteryx.annotations.GuardedBy;
import org.pvoid.apteryx.data.Storage;
import org.pvoid.apteryx.data.agents.Agent;
import org.pvoid.apteryx.data.terminals.Terminal;
import org.pvoid.apteryx.data.terminals.TerminalState;
import org.pvoid.apteryx.data.terminals.TerminalsManager;
import org.pvoid.apteryx.net.NetworkService;
import org.pvoid.apteryx.net.OsmpInterface;
import org.pvoid.apteryx.net.OsmpRequest;
import org.pvoid.apteryx.net.OsmpResponse;
import org.pvoid.apteryx.net.ResultReceiver;
import org.pvoid.apteryx.net.commands.GetAgentInfoCommand;
import org.pvoid.apteryx.net.commands.GetAgentsCommand;
import org.pvoid.apteryx.net.commands.GetPersonInfoCommand;
import org.pvoid.apteryx.net.results.GetAgentInfoResult;
import org.pvoid.apteryx.net.results.GetAgentsResult;
import org.pvoid.apteryx.net.results.GetPersonInfoResult;
import org.pvoid.apteryx.util.LogHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* package */ class OsmpPersonsManager implements PersonsManager {

    private static final String TAG = "AccountManager";

    @NonNull private final Context mContext;
    @NonNull private final Storage mStorage;
    @NonNull private final TerminalsManager mTerminalsManager;
    @NonNull private final Lock mLock = new ReentrantLock();
    @GuardedBy("mLock") @NonNull private final NavigableMap<String, Person> mPersons = new TreeMap<>();
    @GuardedBy("mLock") @Nullable private Person[] mPersonsList = null;
    @GuardedBy("mLock") @Nullable private Person mCurrentPerson = null;
    @GuardedBy("mLock") @Nullable private Agent mCurrentAgent = null;
    @GuardedBy("mLock") @NonNull private Map<String, ArrayList<Agent>> mAgents = new HashMap<>();

    /* package */ OsmpPersonsManager(@NonNull Context context, @NonNull Storage storage,
                                     @NonNull TerminalsManager terminalsManager) {
        mStorage = storage;
        mTerminalsManager = terminalsManager;
        mContext = context.getApplicationContext();
        Person[] persons = storage.getPersons();
        if (persons != null && persons.length > 0) {
            mPersonsList = new Person[persons.length];
            for (int index = 0; index < persons.length; ++index) {
                Person person = persons[index];
                mPersons.put(person.getLogin(), person);
                mPersonsList[index] = person;
            }
        }
        Agent[] agents = storage.getAgents();
        if (agents != null && agents.length > 0) {
            for (Agent agent : agents) {
                ArrayList<Agent> a = mAgents.get(agent.getPersonLogin());
                if (a == null) {
                    a = new ArrayList<>();
                    mAgents.put(agent.getPersonLogin(), a);
                }
                a.add(agent);
            }
        }
        notifyPersonsChanged();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context);
        lbm.registerReceiver(new TerminalsChangesReceiver(), new IntentFilter(TerminalsManager.ACTION_CHANGED));
    }

    @Override
    public boolean add(@NonNull Person person) {
        mLock.lock();
        try {
            if (mPersons.containsKey(person.getLogin())) {
                return false;
            }
            mPersons.put(person.getLogin(), person);
            mPersonsList = null;
        } finally {
            mLock.unlock();
        }
        mStorage.storePerson(person);
        notifyPersonsChanged();
        return true;
    }

    @Override
    public void verify(@NonNull Person person) {
        OsmpRequest.Builder builder = new OsmpRequest.Builder(person);
        builder.getInterface(OsmpInterface.Persons).add(new GetPersonInfoCommand());
        builder.getInterface(OsmpInterface.Agents).add(new GetAgentInfoCommand());
        builder.getInterface(OsmpInterface.Agents).add(new GetAgentsCommand());
        OsmpRequest request = builder.create();

        if (request != null) {
            NetworkService.executeRequest(mContext, request,
                    new VerificationResultReceiver());
        }
    }

    @Override
    @NonNull
    public Person[] getPersons() {
        mLock.lock();
        try {
            if (mPersonsList == null) {
                mPersonsList = mPersons.values().toArray(new Person[mPersons.size()]);
            }
        } finally {
            mLock.unlock();
        }
        return mPersonsList;
    }

    @Nullable
    @Override
    public Person getPerson(@NonNull String login) {
        mLock.lock();
        try {
            return mPersons.get(login);
        } finally {
            mLock.unlock();
        }
    }

    @Nullable
    @Override
    public Person getCurrentPerson() {
        mLock.lock();
        try {
            if (mCurrentPerson == null && !mPersons.isEmpty()) {
                mCurrentPerson = mPersons.firstEntry().getValue();
                mCurrentAgent = null;
                LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mContext);
                lbm.sendBroadcast(new Intent(ACTION_CURRENT_PERSON_CHANGED));
            }
            return mCurrentPerson;
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void setCurrentPerson(@NonNull String login) {
        boolean notify = false;
        mLock.lock();
        try {
            Person person = mPersons.get(login);
            if (person != null && person != mCurrentPerson) {
                mCurrentPerson = person;
                mCurrentAgent = null;
                notify = true;
            }
        } finally {
            mLock.unlock();
        }

        if (notify) {
            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mContext);
            lbm.sendBroadcast(new Intent(ACTION_CURRENT_PERSON_CHANGED));
        }
    }

    @Nullable
    @Override
    public Agent getCurrentAgent() {
        mLock.lock();
        try {
            if (mCurrentAgent == null) {
                Person person = getCurrentPerson();
                if (person != null) {
                    List<Agent> agents = mAgents.get(person.getLogin());
                    if (agents != null) {
                        for (Agent agent : agents) {
                            if (TextUtils.equals(agent.getId(), person.getAgentId())) {
                                mCurrentAgent = agent;
                                LocalBroadcastManager.getInstance(mContext)
                                        .sendBroadcast(new Intent(ACTION_CURRENT_AGENT_CHANGED));
                                break;
                            }
                        }
                    }
                }
            }
            return mCurrentAgent;
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void setCurrentAgent(@NonNull String agentId) {
        Person currentPerson = getCurrentPerson();
        if (currentPerson == null) {
            return;
        }

        boolean notify = false;
        mLock.lock();
        try {
            List<Agent> agents = mAgents.get(currentPerson.getLogin());
            if (agents != null) {
                for (Agent agent : agents) {
                    if (TextUtils.equals(agentId, agent.getId())) {
                        mCurrentAgent = agent;
                        notify = true;
                        break;                    }
                }
            }
        } finally {
            mLock.unlock();
        }

        if (notify) {
            LocalBroadcastManager.getInstance(mContext)
                    .sendBroadcast(new Intent(ACTION_CURRENT_AGENT_CHANGED));
        }
    }

    @Nullable
    @Override
    public Agent[] getAgents(@NonNull String login) {
        mLock.lock();
        try {
            List<Agent> agents = mAgents.get(login);
            return agents == null ? null : agents.toArray(new Agent[agents.size()]);
        } finally {
            mLock.unlock();
        }
    }

    private void notifyPersonsChanged() {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mContext);
        lbm.sendBroadcast(new Intent(ACTION_PERSONS_CHANGED));
    }

    private void notifyVerifyResult(boolean success, @Nullable Person person) {
        Intent intent = new Intent(ACTION_PERSON_VERIFIED);
        intent.putExtra(EXTRA_PERSON, person);
        intent.putExtra(EXTRA_STATE, success);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private class VerificationResultReceiver implements ResultReceiver {

        @Override
        public void onResponse(@NonNull OsmpResponse response) {
            OsmpResponse.Results results = response.getInterface(OsmpInterface.Persons);
            if (results == null) {
                LogHelper.error(TAG, "Error while verifying account. Can't get <agents> list.");
                notifyVerifyResult(false, null);
                return;
            }
            GetPersonInfoResult info = results.get(GetPersonInfoCommand.NAME);
            Person person = null;
            mLock.lock();
            try {
                person = mPersons.get(info.getLogin());
                if (person == null) {
                    return;
                }
                final String name = info.getPersonName();
                final String agentId = info.getAgentId();

                if (name != null && agentId != null) {
                    person = person.verify(agentId, name, person.isEnabled());
                    mPersons.put(person.getLogin(), person);
                    mPersonsList = null;
                } else {
                    LogHelper.error(TAG, "Account name is NULL");
                }
            } finally {
                mLock.unlock();
            }
            mStorage.storePerson(person);
            mTerminalsManager.syncFull(person);
            notifyVerifyResult(true, person);

            results = response.getInterface(OsmpInterface.Agents);
            if (results == null) {
                return;
            }

            ArrayList<Agent> agentsList = new ArrayList<>();
            GetAgentInfoResult agentInfoResult = results.get(GetAgentInfoCommand.NAME);
            if (agentInfoResult != null && agentInfoResult.getAgentId() != null
                    && agentInfoResult.getAgentName() != null) {
                agentsList.add(new Agent(agentInfoResult.getAgentId(), null,
                        agentInfoResult.getAgentINN(), agentInfoResult.getAgentAddress(),
                        agentInfoResult.getAgentAddress(), agentInfoResult.getAgentName(),
                        null, null, null, null).cloneForPerson(person));
            }

            GetAgentsResult agentsResult = results.get(GetAgentsCommand.NAME);
            if (agentsResult != null && agentsResult.getAgents() != null) {
                for (Agent agent : agentsResult.getAgents()) {
                    agentsList.add(agent.cloneForPerson(person));
                }
            }

            if (!agentsList.isEmpty()) {
                mLock.lock();
                try {
                    mAgents.put(person.getLogin(), agentsList);
                } finally {
                    mLock.unlock();
                }
                mStorage.storeAgents(agentsList.toArray(new Agent[agentsList.size()]));
                LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mContext);
                lbm.sendBroadcast(new Intent(ACTION_AGENTS_CHANGED));
            }
        }

        @Override
        public void onError() {
            LogHelper.error(TAG, "Error while verifying account.");
            notifyVerifyResult(false, null);
        }
    }

    private class TerminalsChangesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ObjectGraph graph = ((ApteryxApplication) context.getApplicationContext()).getGraph();
            TerminalsManager manager = graph.get(TerminalsManager.class);
            final Terminal[] terminals = manager.getTerminals(null);
            Map<String, Integer> counts = new HashMap<>();
            Map<String, Agent.State> states = new HashMap<>();
            for (Terminal terminal : terminals) {
                final String agentId = terminal.getAgentId();
                Integer count = counts.get(agentId);
                if (count != null) {
                    counts.put(agentId, ++count);
                } else {
                    counts.put(agentId, 1);
                }

                Agent.State state = states.get(agentId);
                TerminalState st = terminal.getState();
                if (st == null) {
                    continue;
                }
                if (st.hasErrors()) {
                    if (state != Agent.State.Error) {
                        states.put(agentId, Agent.State.Error);
                    }
                } else if (st.hasWarnings()) {
                    if (state != Agent.State.Error) {
                        states.put(agentId, Agent.State.Warn);
                    }
                } else if (state == null) {
                    states.put(agentId, Agent.State.Ok);
                }
            }
            List<Agent> changed = new ArrayList<>();
            mLock.lock();
            try {
                for (ArrayList<Agent> agents : mAgents.values()) {
                    for (int index = 0; index < agents.size(); ++index) {
                        final Agent agent = agents.get(index);
                        final Integer count = counts.get(agent.getId());
                        final Agent.State state = states.get(agent.getId());
                        if (count == null) {
                            continue;
                        }
                        if (count != agent.getTerminalsCount() || state != agent.getState()) {
                            Agent newAgent = agent.cloneForState(count, state == null ? Agent.State.Ok : state);
                            agents.set(index, newAgent);
                            changed.add(newAgent);
                        }
                    }
                }
            } finally {
                mLock.unlock();
            }

            if (!changed.isEmpty()) {
                notifyPersonsChanged();
                mStorage.storeAgents(changed.toArray(new Agent[changed.size()]));
            }
        }
    }
}
