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

package org.pvoid.apteryx.net.commands;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GetTerminalsCommand implements Command {

    public static final String NAME = "getTerminals";
    private static final String PARAM_STATE = "trm-state";

    @Nullable
    private final Map<String, String> mParams;

    public GetTerminalsCommand(boolean onlyActive) {
        if (onlyActive) {
            mParams = new HashMap<>(1);
            mParams.put(PARAM_STATE, "1");
        } else {
            mParams = null;
        }
    }

    @NonNull
    @Override
    public String getName() {
        return NAME;
    }

    @Nullable
    @Override
    public Map<String, String> getParams() {
        return mParams;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mParams != null && mParams.get(PARAM_STATE) != null ? 1 : 0);
    }

    public static final Creator<GetTerminalsCommand> CREATOR = new Creator<GetTerminalsCommand>() {
        @Override
        public GetTerminalsCommand createFromParcel(Parcel source) {
            int state = source.readInt();
            return new GetTerminalsCommand(state == 1);
        }

        @Override
        public GetTerminalsCommand[] newArray(int size) {
            return new GetTerminalsCommand[size];
        }
    };
}
