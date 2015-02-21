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
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pvoid.apteryx.R;
import org.pvoid.apteryx.data.Currency;
import org.pvoid.apteryx.data.terminals.Terminal;
import org.pvoid.apteryx.data.terminals.TerminalCash;
import org.pvoid.apteryx.data.terminals.TerminalState;
import org.pvoid.apteryx.views.terminals.filters.TerminalsFilter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TerminalsAdapter extends RecyclerView.Adapter<TerminalsAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    @Nullable private Terminal[] mTerminals;
    @Nullable private TerminalsFilter mFilter;
    private final NumberFormat mCashFormat;
    private final int mCurrencyColor;

    public TerminalsAdapter(@NonNull Context context) {
        mInflater = LayoutInflater.from(context);
        mCurrencyColor = context.getResources().getColor(R.color.card_date_color);

        mCashFormat = NumberFormat.getInstance(Locale.ENGLISH);
        mCashFormat.setGroupingUsed(true);
        if (mCashFormat instanceof DecimalFormat) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
            symbols.setDecimalSeparator('.');
            symbols.setGroupingSeparator(' ');
            ((DecimalFormat) mCashFormat).setDecimalFormatSymbols(symbols);
        }
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
        public final TextView lastPayment;
        public final TextView cash;
        public final TextView error;
        public final TextView warn;

        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.terminal_title);
            address = (TextView) itemView.findViewById(R.id.terminal_address);
            lastActivity = (TextView) itemView.findViewById(R.id.terminal_last_activity);
            lastPayment = (TextView) itemView.findViewById(R.id.terminal_last_payment);
            cash = (TextView) itemView.findViewById(R.id.terminals_cash);
            error = (TextView) itemView.findViewById(R.id.terminal_state_error);
            warn = (TextView) itemView.findViewById(R.id.terminal_state_warn);
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
            SpannableStringBuilder sb = new SpannableStringBuilder();
            for (TerminalCash.CashItem item : terminalCash.getCash()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                final Currency currency = item.getCurrency();
                mCashFormat.setMinimumFractionDigits(currency.getmFractionDigits());
                sb.append(mCashFormat.format(item.getAmmount()));
                int length = sb.length();
                sb.append(currency.getCodeName());
                sb.setSpan(new ForegroundColorSpan(mCurrencyColor), length, sb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            cash.setText(sb);
        }

        void bindState(@Nullable TerminalState state) {
            if (state == null) {
                lastActivity.setVisibility(View.GONE);
                error.setVisibility(View.GONE);
                warn.setVisibility(View.GONE);
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

            if (state.getLastPayment() != 0) {
                CharSequence str = DateUtils.getRelativeTimeSpanString(state.getLastPayment(),
                        System.currentTimeMillis(), DateUtils.WEEK_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE | DateUtils.FORMAT_ABBREV_ALL |
                                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR);
                lastPayment.setText(str);
                lastPayment.setVisibility(View.VISIBLE);
            } else {
                lastPayment.setVisibility(View.GONE);
            }

            if (state.hasErrors()) {
                error.setText(state.getMessage());
                error.setVisibility(View.VISIBLE);
                warn.setVisibility(View.GONE);
            } else if (state.hasWarnings()) {
                error.setVisibility(View.GONE);
                warn.setText(state.getMessage());
                warn.setVisibility(View.VISIBLE);
            } else {
                error.setVisibility(View.GONE);
                warn.setVisibility(View.GONE);
            }
        }
    }
}
