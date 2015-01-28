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

import java.util.Map;

public class GetTerminalsStatisticalDataCommand implements Command {

    public static final String NAME = "getTerminalsStatisticalData";

    @NonNull
    @Override
    public String getName() {
        return NAME;
    }

    @Nullable
    @Override
    public Map<String, String> getParams() {
        return null;
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
    }

    public static final Creator<GetTerminalsStatisticalDataCommand> CREATOR = new Creator<GetTerminalsStatisticalDataCommand>() {
        @Override
        public GetTerminalsStatisticalDataCommand createFromParcel(Parcel source) {
            return new GetTerminalsStatisticalDataCommand();
        }

        @Override
        public GetTerminalsStatisticalDataCommand[] newArray(int size) {
            return new GetTerminalsStatisticalDataCommand[size];
        }
    };
}
