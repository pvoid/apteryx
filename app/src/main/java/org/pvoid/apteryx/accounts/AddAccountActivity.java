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

package org.pvoid.apteryx.accounts;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.pvoid.apteryx.R;

public class AddAccountActivity extends Activity implements AddAccountFragment.AddAccuntListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_account);
        getFragmentManager().beginTransaction()
                            .add(R.id.fragment_holder, AddAccountFragment.newInstance(null, null))
                            .commitAllowingStateLoss();
    }

    @Override
    public void onAddAccount(@NonNull String login, @NonNull String password, @NonNull String terminal) {
        final View focus = getCurrentFocus();
        final ShowProgressRunnable runnable = new ShowProgressRunnable();
        if (focus != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(focus.getWindowToken(), 0);
            getWindow().getDecorView().postDelayed(runnable, 300);
        } else {
            runnable.run();
        }
    }

    private class ShowProgressRunnable implements Runnable {
        @Override
        public void run() {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.account_fragment_in, R.anim.account_fragment_out)
                    .replace(R.id.fragment_holder, new ProgressFragment())
                    .commitAllowingStateLoss();
        }
    }
}
