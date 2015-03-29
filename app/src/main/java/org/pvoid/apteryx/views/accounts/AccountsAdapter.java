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

package org.pvoid.apteryx.views.accounts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import org.pvoid.apteryx.R;
import org.pvoid.apteryx.annotations.OnMainThread;
import org.pvoid.apteryx.data.persons.Person;

@OnMainThread
public class AccountsAdapter extends BaseAdapter {

    private static final int INVALID_INDEX = -1;

    private static final int VIEW_TYPE_PERSON_ITEM = 0;
    private static final int VIEW_TYPE_PERSON_ADD = 1;

    @NonNull
    private final LayoutInflater mInflater;
    private Person[] mPersons;

    public AccountsAdapter(@NonNull Context context) {
        super();
        mInflater = LayoutInflater.from(context);
        mPersons = null;
    }

    public void setPersons(@Nullable Person[] persons) {
        mPersons = persons;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPersons == null ? 1 : mPersons.length + 1;
    }

    @Override
    public Person getItem(int position) {
        if (mPersons != null && position < mPersons.length) {
            return mPersons[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == VIEW_TYPE_PERSON_ADD) {
            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
            textView.setText(R.string.add_new_account);
            return convertView;
        }


        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_single_choice, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        final Person person = mPersons[position];
        if (person.getState() == Person.State.Valid) {
            textView.setText(person.getName());
        } else {
            textView.setText(person.getLogin());
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (mPersons != null && position < mPersons.length) {
            return VIEW_TYPE_PERSON_ITEM;
        }
        return VIEW_TYPE_PERSON_ADD;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public int findPersonIndex(@NonNull String login) {
        for (int index = 0; index < mPersons.length; ++index) {
            final Person person = mPersons[index];
            if (TextUtils.equals(login, person.getLogin())) {
                return index;
            }
        }
        return INVALID_INDEX;
    }
}
