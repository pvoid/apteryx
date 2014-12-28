/*
 * Copyright (C) 2010-2014  Dmitry "PVOID" Petuhov
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

package org.pvoid.apteryx.net.commands;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

public class GetAgentInfoCommand implements Command {

    public static final String NAME = "getAgentInfo";

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @Override
    @Nullable
    public Map<String, String> getParams() {
        return null;
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
