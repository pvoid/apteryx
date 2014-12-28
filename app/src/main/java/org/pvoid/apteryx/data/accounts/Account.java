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

package org.pvoid.apteryx.data.accounts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Account {
    @NonNull
    private final String mLogin;
    @NonNull
    private final String mPasswordHash;
    @NonNull
    private final String mTerminal;

    @Nullable
    private final String mTitle;

    private final boolean mIsVerified;

    public Account(@NonNull String login, @NonNull String passwordHash, @NonNull String terminal) {
        mLogin = login;
        mPasswordHash = passwordHash;
        mTerminal = terminal;
        mTitle = null;
        mIsVerified = false;
    }

    private Account(@NonNull Account src, @NonNull String title) {
        mLogin = src.mLogin;
        mPasswordHash = src.mPasswordHash;
        mTerminal = src.mTerminal;
        mTitle = title;
        mIsVerified = true;
    }

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

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    public boolean isVerified() {
        return mIsVerified;
    }

    public Account cloneVerified(@NonNull String title) {
        return new Account(this, title);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (o == null || !Account.class.equals(o.getClass())) {
            return false;
        }
        return mLogin.equals(((Account) o).getLogin());
    }

    @Override
    public int hashCode() {
        return mLogin.hashCode();
    }
}
