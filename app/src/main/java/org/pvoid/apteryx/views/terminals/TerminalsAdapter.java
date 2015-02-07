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
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pvoid.apteryx.R;
import org.pvoid.apteryx.data.terminals.Terminal;
import org.pvoid.apteryx.views.terminals.filters.TerminalsFilter;

public class TerminalsAdapter extends RecyclerView.Adapter<TerminalsAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    @Nullable private Terminal[] mTerminals;
    @Nullable private TerminalsFilter mFilter;

    public TerminalsAdapter(@NonNull Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setTerminals(@Nullable Terminal[] terminals) {
        mTerminals = terminals;
        if (mFilter != null && mTerminals != null) {
            mFilter.fill(mTerminals);
        }
        notifyDataSetChanged();
    }

    public void setFilter(@Nullable TerminalsFilter filter) {
        mFilter = filter;
        if (mTerminals != null && mFilter != null) {
            mFilter.fill(mTerminals);
        }
        notifyDataSetChanged();
    }

    @Override
    public TerminalsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView v = (CardView) mInflater.inflate(R.layout.view_terminal_item, parent, false);
        v.setUseCompatPadding(true);
        return new TerminalsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TerminalsAdapter.ViewHolder holder, int position) {
        Terminal terminal = null;
        if (mFilter != null) {
            terminal = mFilter.getAt(position);
        } else if (mTerminals != null) {
            terminal = mTerminals[position];
        }

        if (terminal != null) {
            holder.title.setText(terminal.getDisplayName());
            holder.address.setText(terminal.getDisplayAddress());
        }
    }

    @Override
    public int getItemCount() {
        if (mFilter != null) {
            return mFilter.count();
        }
        return mTerminals == null ? 0 : mTerminals.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final TextView address;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.terminal_title);
            address = (TextView) itemView.findViewById(R.id.terminal_address);

        }
    }
}
