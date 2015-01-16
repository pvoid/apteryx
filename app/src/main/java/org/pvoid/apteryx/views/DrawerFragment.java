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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import org.pvoid.apteryx.GraphHolder;
import org.pvoid.apteryx.R;
import org.pvoid.apteryx.accounts.AddAccountActivity;
import org.pvoid.apteryx.data.persons.Person;
import org.pvoid.apteryx.data.persons.PersonsManager;
import org.pvoid.apteryx.settings.SettingsManager;

import dagger.ObjectGraph;

public class DrawerFragment extends Fragment implements View.OnClickListener, DialogInterface.OnClickListener {
    public DrawerFragment() {
        super();
    }

    private PersonsManager mPersonsManager;
    private SettingsManager mSettingsManager;
    private AccountsAdapter mAccountsAdapter;
    private String mCurrentLogin;

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
        mAccountsAdapter = new AccountsAdapter(view.getContext());
        final TextView currentAccount = (TextView) view.findViewById(R.id.current_account);
        currentAccount.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Person[] persons = mPersonsManager.getPersons();
        mAccountsAdapter.setPersons(persons);

        final View rootView = getView();
        if (rootView == null) {
            return;
        }
        final TextView currentAccount = (TextView) rootView.findViewById(R.id.current_account);
        mCurrentLogin = mSettingsManager.getActiveLogin();
        if (mCurrentLogin != null) {
            int index = mAccountsAdapter.findPersonIndex(mCurrentLogin);
            if (index != -1) {
                currentAccount.setText(mAccountsAdapter.getItem(index).getName());
                return;
            }
        }
        currentAccount.setText(R.string.empty_account);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.current_account: {
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

        final Person person = mAccountsAdapter.getItem(which);
        if (person == null) {
            final Context context = getActivity();
            if (context != null) {
                startActivity(new Intent(context, AddAccountActivity.class));
            }
            return;
        }

        View root = getView();
        if (root != null) {
            TextView currentAccount = (TextView) root.findViewById(R.id.current_account);
            currentAccount.setText(person.getName());
            mCurrentLogin = person.getLogin();
            mSettingsManager.setActiveLogin(mCurrentLogin);
        }
    }
}
