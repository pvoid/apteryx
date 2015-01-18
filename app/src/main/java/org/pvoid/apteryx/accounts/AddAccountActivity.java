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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.pvoid.apteryx.GraphHolder;
import org.pvoid.apteryx.R;
import org.pvoid.apteryx.data.persons.Person;
import org.pvoid.apteryx.data.persons.PersonsManager;
import org.pvoid.apteryx.util.LogHelper;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AddAccountActivity extends Activity implements AddAccountFragment.AddAccuntListener {

    private final AccountVerifiedReceiver mReceiver = new AccountVerifiedReceiver();
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_account);
        getFragmentManager().beginTransaction()
                            .add(R.id.fragment_holder, AddAccountFragment.newInstance(null, null))
                            .commitAllowingStateLoss();

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(PersonsManager.ACTION_PERSON_VERIFIED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    public void onAddAccount(@NonNull String login, @NonNull String password, @NonNull String terminal) {
        final View focus = getCurrentFocus();
        final ShowProgressRunnable runnable = new ShowProgressRunnable();
        int duration = getResources().getInteger(R.integer.fragment_animation_duration);
        if (focus != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(focus.getWindowToken(), 0);
            getWindow().getDecorView().postDelayed(runnable, duration);
        } else {
            runnable.run();
        }

        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(password.getBytes());
            BigInteger i = new BigInteger(1, m.digest());
            password = String.format("%1$032X", i).toLowerCase();
        }
        catch (NoSuchAlgorithmException e) {
            LogHelper.error("Account", "Can't create password hash: %1$s", e.getMessage());
            return;
        }
        mHandler.postDelayed(new AccountVerifyRunnable(login, password, terminal), duration);
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

    private class AccountVerifyRunnable implements Runnable {

        private final String mLogin;
        private final String mPassword;
        private final String mTerminal;

        private AccountVerifyRunnable(String login, String password, String terminal) {
            mLogin = login;
            mPassword = password;
            mTerminal = terminal;
        }

        @Override
        public void run() {
            final Person person = new Person(mLogin, mPassword, mTerminal);
            PersonsManager manager = ((GraphHolder) getApplication()).getGraph().get(PersonsManager.class);
            manager.add(person);
            manager.verify(person);
        }
    }

    private class AccountVerifiedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }
}
