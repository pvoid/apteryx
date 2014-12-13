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

package org.pvoid.apteryx.data;

import android.support.annotation.NonNull;

public class Account {
    @NonNull
    private final String mLogin;
    @NonNull
    private final String mPasswordHash;
    @NonNull
    private final String mTerminal;

    @NonNull
    public String getLogin() {
        return mLogin;
    }

    @NonNull
    public String getPasswordHash() {
        return mPasswordHash;
    }

    @NonNull
    public String getTerminal() {
        return mTerminal;
    }

    public Account(@NonNull String login, @NonNull String passwordHash, @NonNull String terminal) {
        mLogin = login;
        mPasswordHash = passwordHash;
        mTerminal = terminal;
    }
}