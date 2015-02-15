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
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pvoid.apteryx.R;
import org.pvoid.apteryx.data.terminals.Terminal;
import org.pvoid.apteryx.data.terminals.TerminalCash;
import org.pvoid.apteryx.data.terminals.TerminalState;
import org.pvoid.apteryx.views.terminals.filters.TerminalsFilter;

import java.text.SimpleDateFormat;

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
            holder.bind(terminal);
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
        public final TextView lastActivity;
        public final TextView cash;

        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.terminal_title);
            address = (TextView) itemView.findViewById(R.id.terminal_address);
            lastActivity = (TextView) itemView.findViewById(R.id.terminal_last_activity);
            cash = (TextView) itemView.findViewById(R.id.terminals_cash);
        }

        void bind(@NonNull Terminal terminal) {
            title.setText(terminal.getDisplayName());
            address.setText(terminal.getDisplayAddress());
            bindState(terminal.getState());
            bindCash(terminal.getCash());
        }

        void bindCash(@Nullable TerminalCash terminalCash) {
            if (terminalCash == null) {
                cash.setText("-");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (TerminalCash.CashItem item : terminalCash.getCash()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(item.getAmmount()).append(' ').append(item.getCurrency().getCodeName());
            }
            cash.setText(sb);
        }

        void bindState(@Nullable TerminalState state) {
            if (state == null) {
                lastActivity.setVisibility(View.GONE);
                return;
            }

            if (state.getLastActivity() != 0) {
                CharSequence str = DateUtils.getRelativeTimeSpanString(state.getLastActivity(),
                        System.currentTimeMillis(), DateUtils.WEEK_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE | DateUtils.FORMAT_ABBREV_ALL |
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR);
                lastActivity.setText(str);
                lastActivity.setVisibility(View.VISIBLE);
            } else {
                lastActivity.setVisibility(View.GONE);
            }
        }
    }
}
