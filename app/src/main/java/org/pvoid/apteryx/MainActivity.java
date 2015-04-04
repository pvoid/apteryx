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

package org.pvoid.apteryx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import org.pvoid.apteryx.data.agents.Agent;
import org.pvoid.apteryx.data.persons.Person;
import org.pvoid.apteryx.data.persons.PersonsManager;
import org.pvoid.apteryx.net.NetworkService;
import org.pvoid.apteryx.net.RequestExecutor;
import org.pvoid.apteryx.views.DrawerFragment;

import dagger.ObjectGraph;

public class MainActivity extends ActionBarActivity implements DrawerFragment.DrawerListener {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawer;
    private AccountChangedBroadcastReceiver mReceiver = new AccountChangedBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        RequestExecutor executor =((ApteryxApplication) getApplication()).getGraph().get(RequestExecutor.class);
        bindService(new Intent(this, NetworkService.class), executor, BIND_AUTO_CREATE);
    }

    private void initializeViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawer.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mDrawerToggle.syncState();
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(PersonsManager.ACTION_CURRENT_PERSON_CHANGED);
        filter.addAction(PersonsManager.ACTION_CURRENT_AGENT_CHANGED);
        lbm.registerReceiver(mReceiver, filter);
        updateCurrentInfo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RequestExecutor executor =((ApteryxApplication) getApplication()).getGraph().get(RequestExecutor.class);
        unbindService(executor);
    }

    private void updateCurrentInfo() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ObjectGraph graph = ((GraphHolder) getApplication()).getGraph();
        PersonsManager personsManager = graph.get(PersonsManager.class);
        Person person = personsManager.getCurrentPerson();
        Agent agent = personsManager.getCurrentAgent();
        if (person == null || agent == null) {
            toolbar.setTitle(null);
            toolbar.setSubtitle(null);
            return;
        }
        toolbar.setTitle(agent.getName());
        toolbar.setSubtitle(person.getName());
    }

    @Override
    public void hideDrawer() {
        mDrawer.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawer.closeDrawers();
            }
        }, 300);
    }

    private class AccountChangedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            switch (intent.getAction()) {
                case PersonsManager.ACTION_CURRENT_PERSON_CHANGED:
                case PersonsManager.ACTION_CURRENT_AGENT_CHANGED: {
                    updateCurrentInfo();
                    break;
                }
            }
        }
    }
}
