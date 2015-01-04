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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Account implements Parcelable {
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

    private Account(@NonNull Account src, @Nullable String title) {
        mLogin = src.mLogin;
        mPasswordHash = src.mPasswordHash;
        mTerminal = src.mTerminal;
        mTitle = title;
        mIsVerified = true;
    }

    private Account(@NonNull Parcel source) {
        mLogin = source.readString();
        mPasswordHash = source.readString();
        mTerminal = source.readString();
        mTitle = source.readString();
        mIsVerified = source.readByte() != 0;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLogin);
        dest.writeString(mPasswordHash);
        dest.writeString(mTerminal);
        dest.writeString(mTitle);
        dest.writeByte((byte) (mIsVerified ? 1 : 0));
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
}
