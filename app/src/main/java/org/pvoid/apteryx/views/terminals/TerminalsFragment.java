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

package org.pvoid.apteryx.views.terminals;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.pvoid.apteryx.ApteryxApplication;
import org.pvoid.apteryx.BuildConfig;
import org.pvoid.apteryx.GraphHolder;
import org.pvoid.apteryx.R;
import org.pvoid.apteryx.accounts.AddAccountActivity;
import org.pvoid.apteryx.data.agents.Agent;
import org.pvoid.apteryx.data.persons.Person;
import org.pvoid.apteryx.data.persons.PersonsManager;
import org.pvoid.apteryx.data.terminals.TerminalsManager;
import org.pvoid.apteryx.views.terminals.filters.BaseTerminalsFilter;

import dagger.ObjectGraph;

public class TerminalsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Nullable private TerminalsAdapter mAdapter;
    @NonNull private final LocalReceiver mReceiver = new LocalReceiver();

    private final View.OnClickListener mNewAccountListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getActivity(), AddAccountActivity.class));
        }
    };
    private final View.OnClickListener mEditAccountListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), AddAccountActivity.class);
            intent.putExtra(AddAccountActivity.EXTRA_LOGIN, (String) v.getTag());
            startActivity(intent);
        }
    };
    private final View.OnClickListener mSyncAccountListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            final Application application = activity.getApplication();
            final ObjectGraph graph = ((ApteryxApplication) application).getGraph();
            final PersonsManager persons = graph.get(PersonsManager.class);
            final String login = (String) v.getTag();
            if (login == null) {
                return;
            }
            final Person person = persons.getPerson(login);
            if (person == null) {
                return;
            }
            persons.verify(person);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_terminals, container, false);
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.terminals_refresh_view);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.refresh_progress_color_1,
                R.color.refresh_progress_color_2, R.color.refresh_progress_color_3);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.terminals_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        mAdapter = new TerminalsAdapter(inflater.getContext());
        mAdapter.setFilter(new BaseTerminalsFilter());
        recyclerView.setAdapter(mAdapter);

        TextView hintText = (TextView) root.findViewById(R.id.add_account_hint);
        hintText.setMovementMethod(LinkMovementMethod.getInstance());
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        refillAdapter();
    }

    @Override
    public void onStart() {
        super.onStart();
        final LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getActivity());
        final IntentFilter filter = new IntentFilter();
        filter.addAction(PersonsManager.ACTION_CURRENT_AGENT_CHANGED);
        filter.addAction(TerminalsManager.ACTION_CHANGED);
        lbm.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        final LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getActivity());
        lbm.unregisterReceiver(mReceiver);
    }

    private void refillAdapter() {
        Application application = getActivity().getApplication();
        ObjectGraph graph = ((ApteryxApplication) application).getGraph();
        final View root = getView();
        if (root == null) {
            return;
        }
        final TerminalsManager terminalsManager = graph.get(TerminalsManager.class);
        final PersonsManager personsManager = graph.get(PersonsManager.class);
        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.terminals_refresh_view);
        final View accountError = root.findViewById(R.id.empty_account_error);
        final Button button = (Button) root.findViewById(R.id.add_account_button);
        final TextView hintText = (TextView) root.findViewById(R.id.add_account_hint);
        final Person person = personsManager.getCurrentPerson();
        if (person == null) {
            refreshLayout.setVisibility(View.INVISIBLE);
            accountError.setVisibility(View.VISIBLE);
            String hint = getString(R.string.account_empty_message, BuildConfig.CREATE_ACCOUNT_INFO_URL);
            hintText.setText(Html.fromHtml(hint));
            button.setOnClickListener(mNewAccountListener);
            button.setText(R.string.add_new_account);
            return;
        }

        if (person.getState() != Person.State.Valid) {
            refreshLayout.setVisibility(View.INVISIBLE);
            accountError.setVisibility(View.VISIBLE);
            switch (person.getState()) {
                case Unchecked:
                    hintText.setText(R.string.account_not_synchronized);
                    button.setTag(person.getLogin());
                    button.setText(R.string.synchronize_account);
                    button.setOnClickListener(mSyncAccountListener);
                    break;
                case Invalid:
                    hintText.setText(R.string.account_invalid);
                    button.setTag(person.getLogin());
                    button.setText(R.string.edit_account);
                    button.setOnClickListener(mEditAccountListener);
                    break;
                case Blocked:
                    hintText.setText(R.string.account_blocked);
                    button.setVisibility(View.INVISIBLE);
                    break;
            }
            return;
        }

        final Agent agent = personsManager.getCurrentAgent();
        if (agent != null && agent.getPersonLogin() !=  null && mAdapter != null) {
            mAdapter.setTerminals(terminalsManager.getTerminals(agent.getId()));
            RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.terminals_list);
            recyclerView.scrollToPosition(0);
            refreshLayout.setVisibility(View.VISIBLE);
            accountError.setVisibility(View.INVISIBLE);
        }
    }

    private void stopRefreshAnimation() {
        final View root = getView();
        if (root == null) {
            return;
        }
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.terminals_refresh_view);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        final ObjectGraph graph = ((GraphHolder) activity.getApplication()).getGraph();
        final TerminalsManager manager = graph.get(TerminalsManager.class);
        final PersonsManager personsManager = graph.get(PersonsManager.class);
        Person person = personsManager.getCurrentPerson();
        if (person != null) {
            manager.sync(person, true);
        } else {
            stopRefreshAnimation();
        }
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            switch (intent.getAction()) {
                case TerminalsManager.ACTION_CHANGED:
                    stopRefreshAnimation();
                    // fall throe
                case PersonsManager.ACTION_CURRENT_AGENT_CHANGED:
                    refillAdapter();
                    break;
            }
        }
    }
}
