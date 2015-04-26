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
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pvoid.apteryx.ApteryxApplication;
import org.pvoid.apteryx.R;
import org.pvoid.apteryx.TerminalInfoActivity;
import org.pvoid.apteryx.data.terminals.MachineState;
import org.pvoid.apteryx.data.terminals.Terminal;
import org.pvoid.apteryx.data.terminals.TerminalCash;
import org.pvoid.apteryx.data.terminals.TerminalState;
import org.pvoid.apteryx.data.terminals.TerminalsManager;
import org.pvoid.apteryx.util.StringUtils;
import org.pvoid.apteryx.views.ExpandableView;

public class TerminalInfoFragment extends Fragment {

    private static final String ARG_TERMINAL_ID = "id";
    private static final int MAX_STATES = 5;

    public static TerminalInfoFragment create(@NonNull String terminalId) {
        Bundle args = new Bundle();
        args.putString(ARG_TERMINAL_ID, terminalId);
        TerminalInfoFragment fragment = new TerminalInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public TerminalInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_terminal_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle args = getArguments();
        final String terminalId;
        if (args == null || (terminalId = args.getString(ARG_TERMINAL_ID)) == null) {
            return;
        }
        ApteryxApplication app = (ApteryxApplication) getActivity().getApplication();
        TerminalsManager terminalsManager = app.getGraph().get(TerminalsManager.class);
        final Terminal terminal = terminalsManager.getTerminal(terminalId);

        if (terminal == null) {
            return;
        }

        CardView card = (CardView) view.findViewById(R.id.terminal_info_card);
        ViewCompat.setTransitionName(card, TerminalInfoActivity.TRANSITION_NAME);
        TextView title = (TextView) view.findViewById(R.id.terminal_title);
        title.setText(terminal.getDisplayName());
        TextView address = (TextView) view.findViewById(R.id.terminal_address);
        address.setText(terminal.getDisplayAddress());
        TextView cashText = (TextView) view.findViewById(R.id.terminal_cash);
        TerminalCash cash = terminal.getCash();
        Spannable sb;
        if (cash != null &&
                (sb = StringUtils.formatCashSummary(cash, getResources().getColor(R.color.card_date_color))).length() > 0) {
            cashText.setText(sb);
        } else {
            cashText.setText("-");
        }
        final TerminalState state = terminal.getState();
        TextView lastActivity = (TextView) view.findViewById(R.id.terminal_last_activity);
        if (state != null) {
            lastActivity.setText(StringUtils.formatFullDate(state.getLastActivity()));
        } else {
            lastActivity.setText("-");
        }
        TextView lastPayment = (TextView) view.findViewById(R.id.terminal_last_payment);
        if (state != null) {
            lastPayment.setText(StringUtils.formatFullDate(state.getLastPayment()));
        } else {
            lastPayment.setText("-");
        }
        ExpandableView statesView = (ExpandableView) view.findViewById(R.id.terminal_states);
        if (state != null) {
            String noteError = state.hasNoteError() ? state.getNoteError() : null;
            String printerError = state.hasPrinterError() ? state.getPrinterError() : null;
            statesView.setContent(new MachineStatesViewBuilder(noteError, printerError, state.getStates()));
        } else {
            statesView.setVisibility(View.GONE);
        }
    }

    private class MachineStatesViewBuilder implements ExpandableView.ViewBuilder {

        private final String mNoteError;
        private final String mPrinterError;
        private final MachineState mStates[];
        private final int mCount;

        private MachineStatesViewBuilder(@Nullable String noteError, @Nullable String printerError, @NonNull MachineState[] states) {
            mNoteError = noteError;
            mPrinterError = printerError;
            mStates = states;
            int count = mStates.length;
            if (mNoteError != null) {
                ++count;
            }
            if (mPrinterError != null) {
                ++count;
            }
            mCount = count;
        }

        @NonNull
        @Override
        public View create(@NonNull Context context, int index, @NonNull ViewGroup parent) {
            TextView line = (TextView) LayoutInflater.from(context).inflate(R.layout.view_state_line, parent, false);
            if (mNoteError != null) {
                if (index == 0) {
                    line.setText(mNoteError);
                    return line;
                }
                --index;
            }
            if (mPrinterError != null) {
                if (index == 0) {
                    line.setText(mPrinterError);
                    return line;
                }
                --index;
            }
            line.setText(mStates[index].text);
            return line;
        }

        @Override
        public int getCount() {
            return mCount;
        }
    }
}
