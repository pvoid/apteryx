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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.pvoid.apteryx.R;
import org.pvoid.apteryx.annotations.OnMainThread;
import org.pvoid.apteryx.data.persons.Person;

@OnMainThread
public class AccountsAdapter extends BaseAdapter {

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
    public Object getItem(int position) {
        if (mPersons == null) {
            return null;
        }
        return mPersons[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView text = (TextView) convertView.findViewById(android.R.id.text1);
        if (mPersons == null || position >= mPersons.length) {
            text.setText(R.string.add_new_account);
        } else {
            text.setText(mPersons[position].getName());
        }
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView text = (TextView) convertView.findViewById(android.R.id.text1);
        if (mPersons == null || position >= mPersons.length) {
            text.setText(R.string.empty_account);
        } else {
            text.setText(mPersons[position].getName());
        }
        return convertView;
    }
}
