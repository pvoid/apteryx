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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.pvoid.apteryx.R;
import org.pvoid.apteryx.data.terminals.Terminal;

public class TerminalsAdapter extends RecyclerView.Adapter<TerminalViewHolder> {

    private final LayoutInflater mInflater;
    private Terminal[] mTerminals;

    public TerminalsAdapter(@NonNull Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setTerminals(Terminal[] terminals) {
        mTerminals = terminals;
        notifyDataSetChanged();
    }

    @Override
    public TerminalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView v = (CardView) mInflater.inflate(R.layout.view_terminal_item, parent, false);
        v.setUseCompatPadding(true);
        return new TerminalViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TerminalViewHolder holder, int position) {
        holder.title.setText(mTerminals[position].getDisplayName());
        holder.address.setText(mTerminals[position].getDisplayAddress());
    }

    @Override
    public int getItemCount() {
        return mTerminals == null ? 0 : mTerminals.length;
    }
}
