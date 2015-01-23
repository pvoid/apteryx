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
import android.text.TextUtils;

import org.pvoid.apteryx.data.agents.Agent;
import org.pvoid.apteryx.data.persons.Person;
import org.pvoid.apteryx.data.persons.PersonsManager;
import org.pvoid.apteryx.settings.SettingsManager;
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
        // TODO: filter.addAction(SettingsManager.ACTION_AGENT_CHANGED);
        lbm.registerReceiver(mReceiver, filter);
        updateCurrentInfo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.unregisterReceiver(mReceiver);
    }

    private void updateCurrentInfo() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ObjectGraph graph = ((GraphHolder) getApplication()).getGraph();
        SettingsManager settingsManager = graph.get(SettingsManager.class);
        String login = settingsManager.getActiveLogin();
        String agentId = settingsManager.getActiveAgent();
        if (login == null) {
            toolbar.setTitle(null);
            toolbar.setSubtitle(null);
            return;
        }
        PersonsManager personsManager = graph.get(PersonsManager.class);
        Person person = personsManager.getPerson(login);
        if (person == null) {
            toolbar.setTitle(null);
            toolbar.setSubtitle(null);
            return;
        }
        Agent agents[] = personsManager.getAgents(person.getLogin());
        if (agents == null) {
            toolbar.setTitle(null);
            toolbar.setSubtitle(null);
            return;
        }
        for (Agent agent : agents) {
            if (TextUtils.equals(agentId, agent.getId())) {
                toolbar.setTitle(agent.getName());
            }
        }
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
                /* TODO: case SettingsManager.ACTION_AGENT_CHANGED: */{
                    updateCurrentInfo();
                    break;
                }
            }
        }
    }
}
