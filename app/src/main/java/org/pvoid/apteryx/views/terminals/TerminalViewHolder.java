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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.pvoid.apteryx.R;

public class TerminalViewHolder extends RecyclerView.ViewHolder {

    public final TextView title;
    public final TextView address;

    public TerminalViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.terminal_title);
        address = (TextView) itemView.findViewById(R.id.terminal_address);
    }
}
