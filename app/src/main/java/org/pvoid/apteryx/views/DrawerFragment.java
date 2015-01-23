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

import org.pvoid.apteryx.GraphHolder;
import org.pvoid.apteryx.R;
import org.pvoid.apteryx.accounts.AddAccountActivity;
import org.pvoid.apteryx.data.agents.Agent;
import org.pvoid.apteryx.data.persons.Person;
import org.pvoid.apteryx.data.persons.PersonsManager;
import org.pvoid.apteryx.views.accounts.AccountsAdapter;

import dagger.ObjectGraph;

public class DrawerFragment extends Fragment implements DialogInterface.OnClickListener, DrawerAdapter.OnAccountSwitcherClickedListener {
    private static final String TAG = "DrawerFragment";

    @NonNull private final AccountsChangeReceiver mReceiver = new AccountsChangeReceiver();
    private PersonsManager mPersonsManager;
    @Nullable private DrawerAdapter mDrawerAdapter;
    @Nullable private AccountsAdapter mAccountsAdapter;

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
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Context context = view.getContext();

        RecyclerView drawerItems = (RecyclerView) view.findViewById(R.id.drawer_items);
        mDrawerAdapter = new DrawerAdapter(context);
        drawerItems.setLayoutManager(new LinearLayoutManager(context));
        drawerItems.setAdapter(mDrawerAdapter);
        mDrawerAdapter.setSwitcherClickedListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDrawerAdapter = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        updateFragment();

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter filter = new IntentFilter();
        filter.addAction(PersonsManager.ACTION_PERSONS_CHANGED);
        filter.addAction(PersonsManager.ACTION_CURRENT_PERSON_CHANGED);
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
        if (mDrawerAdapter != null && mPersonsManager != null) {
            Person person = mPersonsManager.getCurrentPerson();
            Agent[] agents = null;
            if (person != null) {
                agents = mPersonsManager.getAgents(person.getLogin());
            }
            mDrawerAdapter.setCurrentAccount(person, agents);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();

        if (mAccountsAdapter != null && mPersonsManager != null) {
            final Person person = mAccountsAdapter.getItem(which);
            if (person != null) {
                mPersonsManager.setCurrentPerson(person.getLogin());
            } else {
                final Context context = getActivity();
                if (context != null) {
                    startActivity(new Intent(context, AddAccountActivity.class));
                }
            }
        }
    }

    @Override
    public void onAccountSwitcherClicked() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        mAccountsAdapter = new AccountsAdapter(activity);
        mAccountsAdapter.setPersons(mPersonsManager.getPersons());
        Person current = mPersonsManager.getCurrentPerson();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setSingleChoiceItems(mAccountsAdapter,
                current == null ? -1 : mAccountsAdapter.findPersonIndex(current.getLogin()),
                this);
        builder.create().show();
    }

//    @Override
//    public void onAgentSelected(@NonNull Agent agent) {
//        if (mSettingsManager != null) {
//            mSettingsManager.setActiveAgent(agent.getId());
//        }
//        Activity activity = getActivity();
//        if (activity instanceof DrawerListener) {
//            ((DrawerListener) activity).hideDrawer();
//        }
//    }

    private class AccountsChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            View rootView = getView();
            if (intent == null || rootView == null) {
                return;
            }
            switch (intent.getAction()) {
                case PersonsManager.ACTION_PERSONS_CHANGED:
                case PersonsManager.ACTION_CURRENT_PERSON_CHANGED:
                    updateFragment();
                    break;
                case PersonsManager.ACTION_AGENTS_CHANGED:
//                    fillAgents();
                    break;
            }
        }
    }

    public interface DrawerListener {
        void hideDrawer();
    }
}
