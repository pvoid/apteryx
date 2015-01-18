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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.pvoid.apteryx.ApteryxApplication;
import org.pvoid.apteryx.R;
import org.pvoid.apteryx.data.terminals.TerminalsManager;
import org.pvoid.apteryx.settings.SettingsManager;

import dagger.ObjectGraph;

public class TerminalsFragment extends Fragment {

    @Nullable private TerminalsAdapter mAdapter;
    @NonNull private final LocalReceiver mReceiver = new LocalReceiver();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_terminals, container, false);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.terminals_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        mAdapter = new TerminalsAdapter(inflater.getContext());
        recyclerView.setAdapter(mAdapter);
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
        filter.addAction(SettingsManager.ACTION_ACCOUNT_CHANGED);
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
        final TerminalsManager terminalsManager = graph.get(TerminalsManager.class);
        final SettingsManager settingsManager = graph.get(SettingsManager.class);
        final String login = settingsManager.getActiveLogin();
        if (login != null && mAdapter != null) {
            mAdapter.setTerminals(terminalsManager.getTerminals(login));
        }
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            switch (intent.getAction()) {
                case SettingsManager.ACTION_ACCOUNT_CHANGED:
                case TerminalsManager.ACTION_CHANGED:
                    refillAdapter();
                    break;
            }
        }
    }
}
