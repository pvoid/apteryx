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
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import org.pvoid.apteryx.GraphHolder;
import org.pvoid.apteryx.R;
import org.pvoid.apteryx.data.persons.Person;
import org.pvoid.apteryx.data.persons.PersonsManager;

import dagger.ObjectGraph;

public class DrawerFragment extends Fragment {
    public DrawerFragment() {
        super();
    }

    private PersonsManager mPersonsManager;
    private AccountsAdapter mAccountsAdapter;

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
        mAccountsAdapter = new AccountsAdapter(view.getContext());
        Spinner accountsSpinner = (Spinner) view.findViewById(R.id.account_switcher);
        accountsSpinner.setAdapter(mAccountsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Person[] persons = mPersonsManager.getPersons();
        mAccountsAdapter.setPersons(persons);
    }
}
