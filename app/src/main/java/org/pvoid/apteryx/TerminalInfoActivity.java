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

import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import org.pvoid.apteryx.util.log.Loggers;
import org.pvoid.apteryx.views.terminals.TerminalInfoFragment;
import org.slf4j.Logger;

public class TerminalInfoActivity extends ActionBarActivity {

    private static final Logger LOG = Loggers.getLogger(Loggers.UI);

    public static final String EXTRA_TERMINAL_ID = "terminal_id";

    public static final String TRANSITION_NAME = "terminal_title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal_info);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        final String terminalId = getIntent().getStringExtra(EXTRA_TERMINAL_ID);
        if (TextUtils.isEmpty(terminalId)) {
            LOG.error("Can't show terminal info. Terminal id is empty.");
            finish();
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, TerminalInfoFragment.create(terminalId))
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
