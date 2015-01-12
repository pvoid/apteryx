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

package org.pvoid.apteryx.data.persons;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Person implements Parcelable {
    @NonNull private final String mLogin;
    @NonNull private final String mPasswordHash;
    @NonNull private final String mTerminal;
    @Nullable private final String mAgentId;
    @Nullable private final String mName;
    private boolean mIsVerified;
    private boolean mIsEnabled;

    public Person(@NonNull String login, @NonNull String passwordHash, @NonNull String terminal) {
        mLogin = login;
        mPasswordHash = passwordHash;
        mTerminal = terminal;
        mAgentId = null;
        mName = null;
        mIsEnabled = true;
        mIsVerified = false;
    }

    public Person(@NonNull String login, @NonNull String passwordHash, @NonNull String terminal,
                  @Nullable String agentId, @Nullable String name, boolean isEnabled,
                  boolean isVerified) {
        mLogin = login;
        mPasswordHash = passwordHash;
        mTerminal = terminal;
        mAgentId = agentId;
        mName = name;
        mIsEnabled = isEnabled;
        mIsVerified = isVerified;
    }

    private Person(@NonNull Parcel source) {
        mLogin = source.readString();
        mPasswordHash = source.readString();
        mTerminal = source.readString();
        mAgentId = source.readString();
        mName = source.readString();
        mIsVerified = source.readByte() == 1;
        mIsEnabled= source.readByte() == 1;
    }

    private Person(@NonNull Person src, @Nullable String agentId, @Nullable String name,
                   boolean isEnabled) {
        mLogin = src.mLogin;
        mPasswordHash = src.mPasswordHash;
        mTerminal = src.mTerminal;
        mAgentId = agentId;
        mName = name;
        mIsEnabled = isEnabled;
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
    public String getAgentId() {
        return mAgentId;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    public boolean isVerified() {
        return mIsVerified;
    }

    public boolean isEnabled() {
        return mIsEnabled;
    }

    public Person verify(@NonNull String agentId, @NonNull String name, boolean isEnabled) {
        return new Person(this, agentId, name, isEnabled);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (o == null || !Person.class.equals(o.getClass())) {
            return false;
        }
        return mLogin.equals(((Person) o).getLogin());
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
        dest.writeString(mAgentId);
        dest.writeString(mName);
        dest.writeByte((byte) (mIsVerified ? 1 : 0));
        dest.writeByte((byte) (mIsEnabled ? 1 : 0));
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
}
