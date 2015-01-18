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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pvoid.apteryx.R;
import org.pvoid.apteryx.data.agents.Agent;

public class AgentsAdapter extends RecyclerView.Adapter<AgentsAdapter.ViewHolder> {

    @NonNull
    private final LayoutInflater mInflater;
    @Nullable
    private Agent[] mAgents;
    @Nullable
    OnAgentSelectedListener mListener;

    public AgentsAdapter(@NonNull Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public AgentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = mInflater.inflate(R.layout.view_agent_item, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(AgentsAdapter.ViewHolder holder, int position) {
        if (mAgents != null && position < mAgents.length) {
            holder.title.setText(mAgents[position].getName());
            holder.position = position;
        }
    }

    @Override
    public int getItemCount() {
        return mAgents != null ? mAgents.length : 0;
    }

    public void setAgents(@Nullable Agent[] agents) {
        mAgents = agents;
        notifyDataSetChanged();
    }

    public void setAgentSelectListener(@Nullable OnAgentSelectedListener listener) {
        mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @NonNull final TextView title;
        int position;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.agent_name);
        }

        @Override
        public void onClick(View v) {
            if (mAgents != null && position > -1 && position < mAgents.length && mListener != null) {
                mListener.onAgentSelected(mAgents[position]);
            }
        }
    }

    public interface OnAgentSelectedListener {
        void onAgentSelected(@NonNull Agent agent);
    }
}
