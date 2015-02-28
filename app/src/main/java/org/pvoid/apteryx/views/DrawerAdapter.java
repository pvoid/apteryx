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
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pvoid.apteryx.R;
import org.pvoid.apteryx.data.agents.Agent;
import org.pvoid.apteryx.data.persons.Person;

import java.util.Arrays;
import java.util.Comparator;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {

    private static final int VIEW_TYPE_ACCOUNT = 0;
    private static final int VIEW_AGENTS_HEADER = 1;
    private static final int VIEW_AGENT = 2;

    @NonNull private final LayoutInflater mInflater;
    @NonNull private final AgentsComparator mComparator = new AgentsComparator();
    @Nullable private Person mCurrentAccount;
    @Nullable private Agent[] mAgents;
    private int mAgentsCount = 0;
    private int mSelectedPosition;
    @Nullable private OnAccountSwitcherClickedListener mSwitcherClickedListener;
    @Nullable private OnAgentSelectedListener mOnAgentSelectedListener;
    private final int mColorError;
    private final int mColorWarn;

    public DrawerAdapter(@NonNull Context context) {
        mInflater = LayoutInflater.from(context);
        Resources resources = context.getResources();
        mColorError = resources.getColor(R.color.terminal_state_error_background);
        mColorWarn = resources.getColor(R.color.terminal_state_warn_background);
    }

    public void setSwitcherClickedListener(@Nullable OnAccountSwitcherClickedListener switcherClickedListener) {
        mSwitcherClickedListener = switcherClickedListener;
    }

    public void setOnAgentSelectedListener(@Nullable OnAgentSelectedListener onAgentSelectedListener) {
        mOnAgentSelectedListener = onAgentSelectedListener;
    }

    public void setCurrentAccount(@Nullable Person currentAccount, @Nullable Agent[] agents) {
        if (mCurrentAccount != null && mCurrentAccount.equals(currentAccount)) {
            return;
        }
        mCurrentAccount = currentAccount;
        if (agents != null) {
            mAgents = new Agent[agents.length];
            int index = 0;
            for (Agent agent : agents) {
                if (agent.getTerminalsCount() == 0) {
                    continue;
                }
                mAgents[index++] = agent;
            }
            mAgentsCount = index;
            if (mAgentsCount > 0) {
                Arrays.sort(mAgents, 0, mAgentsCount - 1, mComparator);
            }
        } else {
            mAgents = null;
            mAgentsCount = 0;
        }
        notifyDataSetChanged();
    }

    public void setCurrentAgent(@Nullable Agent agent) {
        if (agent == null || mAgents == null) {
            mSelectedPosition = -1;
            notifyDataSetChanged();
            return;
        }

        for (int index = 0; index < mAgentsCount; ++index) {
            if (agent.equals(mAgents[index])) {
                mSelectedPosition = index;
                notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ACCOUNT:
                return new AccountViewHolder(mInflater.inflate(R.layout.view_account_switcher, parent, false));
            case VIEW_AGENTS_HEADER:
                return new DrawerViewHolder(mInflater.inflate(R.layout.view_agents_header, parent, false));
            case VIEW_AGENT:
                return new AgentViewHolder(mInflater.inflate(R.layout.view_agent_item, parent, false));
        }
        throw new IllegalArgumentException("Unknown view type");
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder holder, int position) {
        switch(getItemViewType(position)) {
            case VIEW_TYPE_ACCOUNT: {
                AccountViewHolder accountHolder = (AccountViewHolder) holder;
                if (mCurrentAccount == null) {
                    accountHolder.name.setText(R.string.empty_account);
                } else {
                    accountHolder.name.setText(mCurrentAccount.getName());
                }
                break;
            }
            case VIEW_AGENT: {
                position -= 2;
                AgentViewHolder agentHolder = (AgentViewHolder) holder;
                if (mAgents != null) {
                    final Agent agent = mAgents[position];

                    CharSequence text = agent.getName();
                    if (text != null && position == mSelectedPosition) {
                        SpannableString t = new SpannableString(text);
                        t.setSpan(new StyleSpan(Typeface.BOLD), 0, t.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        text = t;
                    }
                    agentHolder.title.setText(text);
                    agentHolder.position = position;
                    switch (agent.getState()) {
                        case Error:
                            agentHolder.errorMark.setBackgroundColor(mColorError);
                            break;
                        case Warn:
                            agentHolder.errorMark.setBackgroundColor(mColorWarn);
                            break;
                        case Ok:
                            agentHolder.errorMark.setBackgroundColor(Color.TRANSPARENT);
                            break;
                    }
                    if (position == mAgentsCount - 1) {
                        agentHolder.line.setVisibility(View.GONE);
                    } else {
                        agentHolder.line.setVisibility(View.VISIBLE);
                    }
                }
                break;
            }

        }
    }

    @Override
    public int getItemCount() {
        if (mAgentsCount == 0) {
            return 1;
        }
        return 2 + mAgentsCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_ACCOUNT;
        }
        if (position == 1) {
            return VIEW_AGENTS_HEADER;
        }
        return VIEW_AGENT;
    }

    public class DrawerViewHolder extends RecyclerView.ViewHolder {
        public DrawerViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class AccountViewHolder extends DrawerViewHolder implements View.OnClickListener {

        @NonNull final TextView name;

        public AccountViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.current_account_name);
            View switcher = itemView.findViewById(R.id.account_switcher);
            switcher.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            OnAccountSwitcherClickedListener listener = mSwitcherClickedListener;
            if (listener != null) {
                listener.onAccountSwitcherClicked();
            }
        }
    }

    private class AgentViewHolder extends DrawerViewHolder implements View.OnClickListener {

        @NonNull final TextView title;
        @NonNull final View errorMark;
        @NonNull final View line;
        int position = -1;

        public AgentViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.agent_name);
            errorMark = itemView.findViewById(R.id.agent_error_mark);
            line = itemView.findViewById(R.id.agent_line);
        }

        @Override
        public void onClick(View v) {
            if (mAgents == null) {
                return;
            }
            OnAgentSelectedListener listener = mOnAgentSelectedListener;
            if (listener != null) {
                listener.onAgentSelected(mAgents[position]);
            }
        }
    }

    public interface OnAccountSwitcherClickedListener {
        void onAccountSwitcherClicked();
    }

    public interface OnAgentSelectedListener {
        void onAgentSelected(@NonNull Agent agent);
    }

    private static class AgentsComparator implements Comparator<Agent> {
        @Override
        public int compare(Agent left, Agent right) {
            if (left == null || left.getName() == null) {
                return right != null && right.getName() != null ? -1 : 0;
            }
            if (right == null || right.getName() == null) {
                return 1;
            }
            return left.getName().compareTo(right.getName());
        }
    }
}
