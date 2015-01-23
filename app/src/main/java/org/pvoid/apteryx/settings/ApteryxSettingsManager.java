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

package org.pvoid.apteryx.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

/* package */ class ApteryxSettingsManager implements SettingsManager {

    private static final String FILE_NAME = "apteryx";
    private static final String PREF_ACTIVE_LOGIN = "active_login";
    private static final String PREF_ACTIVE_AGENT = "active_agent";

    private final SharedPreferences mPreferences;
    private final LocalBroadcastManager mLBM;

    /* package */ ApteryxSettingsManager(@NonNull Context context) {
        mPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        mLBM = LocalBroadcastManager.getInstance(context);
    }

    @Nullable
    @Override
    public String getActiveLogin() {
        return mPreferences.getString(PREF_ACTIVE_LOGIN, null);
    }

    @Override
    public void setActiveLogin(@Nullable String login, @Nullable String agent) {
        mPreferences.edit().putString(PREF_ACTIVE_LOGIN, login)
                .putString(PREF_ACTIVE_AGENT, agent).apply();
    }

    @Nullable
    @Override
    public String getActiveAgent() {
        return mPreferences.getString(PREF_ACTIVE_AGENT, null);
    }

    @Override
    public void setActiveAgent(@Nullable String agent) {
        mPreferences.edit().putString(PREF_ACTIVE_AGENT, agent).apply();
    }
}
