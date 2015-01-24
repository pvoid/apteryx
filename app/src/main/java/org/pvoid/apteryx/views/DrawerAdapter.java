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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pvoid.apteryx.R;
import org.pvoid.apteryx.data.agents.Agent;
import org.pvoid.apteryx.data.persons.Person;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {

    private static final int VIEW_TYPE_ACCOUNT = 0;
    private static final int VIEW_AGENT = 1;

    @NonNull
    private final LayoutInflater mInflater;
    @Nullable private Person mCurrentAccount;
    @Nullable private Agent[] mAgents;
    @Nullable private OnAccountSwitcherClickedListener mSwitcherClickedListener;
    @Nullable private OnAgentSelectedListener mOnAgentSelectedListener;

    public DrawerAdapter(@NonNull Context context) {
        mInflater = LayoutInflater.from(context);
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
        mAgents = agents;
        notifyDataSetChanged();
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ACCOUNT) {
            return new AccountViewHolder(mInflater.inflate(R.layout.view_account_switcher, parent, false));
        }
        return new AgentViewHolder(mInflater.inflate(R.layout.view_agent_item, parent, false));
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder holder, int position) {
        switch(getItemViewType(position)) {
            case VIEW_TYPE_ACCOUNT: {
                AccountViewHolder accountHolder = (AccountViewHolder) holder;
                if (mCurrentAccount == null) {
                    accountHolder.name.setVisibility(View.GONE);
                    accountHolder.login.setText(R.string.empty_account);
                } else {
                    accountHolder.name.setVisibility(View.VISIBLE);
                    accountHolder.name.setText(mCurrentAccount.getName());
                    accountHolder.login.setText(mCurrentAccount.getLogin());
                }
                break;
            }
            case VIEW_AGENT: {
                --position;
                AgentViewHolder agentHoder = (AgentViewHolder) holder;
                if (mAgents != null) {
                    agentHoder.title.setText(mAgents[position].getName());
                    agentHoder.position = position;
                }
                break;
            }

        }
    }

    @Override
    public int getItemCount() {
        int count = 1;
        if (mAgents != null) {
            count += mAgents.length;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_ACCOUNT;
        }
        return VIEW_AGENT;
    }

    public class DrawerViewHolder extends RecyclerView.ViewHolder {
        public DrawerViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class AccountViewHolder extends DrawerViewHolder implements View.OnClickListener {

        @NonNull final TextView login;
        @NonNull final TextView name;

        public AccountViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.current_account_name);
            login = (TextView) itemView.findViewById(R.id.current_account_login);
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
        int position = -1;

        public AgentViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.agent_name);
        }

        @Override
        public void onClick(View v) {
            OnAgentSelectedListener listener = mOnAgentSelectedListener;
            if (listener != null && mAgents != null) {
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
}
