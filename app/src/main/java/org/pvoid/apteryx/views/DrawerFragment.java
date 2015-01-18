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

package org.pvoid.apteryx.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pvoid.apteryx.GraphHolder;
import org.pvoid.apteryx.R;
import org.pvoid.apteryx.accounts.AddAccountActivity;
import org.pvoid.apteryx.data.agents.Agent;
import org.pvoid.apteryx.data.persons.Person;
import org.pvoid.apteryx.data.persons.PersonsManager;
import org.pvoid.apteryx.settings.SettingsManager;
import org.pvoid.apteryx.util.LogHelper;
import org.pvoid.apteryx.views.accounts.AccountsAdapter;
import org.pvoid.apteryx.views.accounts.AgentsAdapter;

import dagger.ObjectGraph;

public class DrawerFragment extends Fragment implements View.OnClickListener, DialogInterface.OnClickListener, AgentsAdapter.OnAgentSelectedListener {
    private static final String TAG = "DrawerFragment";

    @NonNull private final AccountsChangeReceiver mReceiver = new AccountsChangeReceiver();
    private PersonsManager mPersonsManager;
    private SettingsManager mSettingsManager;
    @Nullable private AccountsAdapter mAccountsAdapter;
    @Nullable private AgentsAdapter mAgentsAdapter;
    private String mCurrentLogin;

    public DrawerFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drawer, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ObjectGraph graph = ((GraphHolder) activity.getApplication()).getGraph();
        mPersonsManager = graph.get(PersonsManager.class);
        mSettingsManager = graph.get(SettingsManager.class);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Context context = view.getContext();
        mAccountsAdapter = new AccountsAdapter(context);
        mAgentsAdapter = new AgentsAdapter(context);
        mAgentsAdapter.setAgentSelectListener(this);

        final View currentAccount = view.findViewById(R.id.account_switcher);
        currentAccount.setOnClickListener(this);
        RecyclerView agentsList = (RecyclerView) view.findViewById(R.id.agents_list);
        agentsList.setLayoutManager(new LinearLayoutManager(context));
        agentsList.setAdapter(mAgentsAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        final View rootView = getView();
        if (rootView == null || mAccountsAdapter == null) {
            LogHelper.debug(TAG, "Wrong fragment call onResume()");
            return;
        }

        updateFragment();

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter filter = new IntentFilter();
        filter.addAction(PersonsManager.ACTION_PERSONS_CHANGED);
        filter.addAction(PersonsManager.ACTION_AGENTS_CHANGED);
        lbm.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getActivity());
        lbm.unregisterReceiver(mReceiver);
    }

    private void updateFragment() {
        Person person = null;
        if (mCurrentLogin != null) {
            person = mPersonsManager.getPerson(mCurrentLogin);
        }
        fillPersons();
        updateCurrentAccount(person);
        fillAgents();
    }

    private void updateCurrentAccount(@Nullable Person person) {
        View rootView = getView();
        if (rootView == null) {
            return;
        }
        final TextView accountName = (TextView) rootView.findViewById(R.id.current_account_name);
        final TextView accountLogin = (TextView) rootView.findViewById(R.id.current_account_login);

        if (mAccountsAdapter != null && mAccountsAdapter.getCount() > 1) {
            if (person == null) {
                person = mAccountsAdapter.getItem(0);
            }
            mCurrentLogin = person.getLogin();
            accountName.setText(person.getName());
            accountLogin.setText(person.getLogin());

            mSettingsManager.setActiveLogin(mCurrentLogin, person.getAgentId());
            return;
        }

        accountName.setVisibility(View.GONE);
        accountLogin.setText(R.string.empty_account);
    }

    private void fillAgents() {
        if(mAgentsAdapter == null || mPersonsManager == null) {
            return;
        }
        Agent[] agents = mPersonsManager.getAgents(mCurrentLogin);
        mAgentsAdapter.setAgents(agents);
    }

    private void fillPersons() {
        if (mPersonsManager == null || mAccountsAdapter == null) {
            return;
        }
        Person[] persons = mPersonsManager.getPersons();
        mAccountsAdapter.setPersons(persons);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_switcher: {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setSingleChoiceItems(mAccountsAdapter,
                        mAccountsAdapter.findPersonIndex(mCurrentLogin), this);
                builder.create().show();
                break;
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();

        if (mAccountsAdapter != null) {
            final Person person = mAccountsAdapter.getItem(which);
            updateCurrentAccount(person);
            fillAgents();
        }
    }

    @Override
    public void onAgentSelected(@NonNull Agent agent) {
        if (mSettingsManager != null) {
            mSettingsManager.setActiveAgent(agent.getId());
        }
        Activity activity = getActivity();
        if (activity instanceof DrawerListener) {
            ((DrawerListener) activity).hideDrawer();
        }
    }

    private class AccountsChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            View rootView = getView();
            if (intent == null || rootView == null) {
                return;
            }
            switch (intent.getAction()) {
                case PersonsManager.ACTION_PERSONS_CHANGED:
                    updateFragment();
                    break;
                case PersonsManager.ACTION_AGENTS_CHANGED:
                    fillAgents();
                    break;
            }
        }
    }

    public interface DrawerListener {
        void hideDrawer();
    }
}
