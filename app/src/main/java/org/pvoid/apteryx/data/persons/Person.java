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

    public enum State {
        Unchecked(0),
        Invalid(1),
        Blocked(2),
        Valid(3);

        public final int code;

        State(int code) {
            this.code = code;
        }

        public static State fromCode(int code) {
            for (State state : values()) {
                if (state.code == code) {
                    return state;
                }
            }
            return State.Unchecked;
        }
    }

    @NonNull private final String mLogin;
    @NonNull private final String mPasswordHash;
    @NonNull private final String mTerminal;
    @Nullable private final String mAgentId;
    @Nullable private final String mName;
    @NonNull private State mState;

    public Person(@NonNull String login, @NonNull String passwordHash, @NonNull String terminal) {
        mLogin = login;
        mPasswordHash = passwordHash;
        mTerminal = terminal;
        mAgentId = null;
        mName = null;
        mState = State.Unchecked;
    }

    public Person(@NonNull String login, @NonNull String passwordHash, @NonNull String terminal,
                  @Nullable String agentId, @Nullable String name, @NonNull State state) {
        mLogin = login;
        mPasswordHash = passwordHash;
        mTerminal = terminal;
        mAgentId = agentId;
        mName = name;
        mState = state;
    }

    private Person(@NonNull Parcel source) {
        mLogin = source.readString();
        mPasswordHash = source.readString();
        mTerminal = source.readString();
        mAgentId = source.readString();
        mName = source.readString();
        mState = State.fromCode(source.readInt());
    }

    private Person(@NonNull Person src, @Nullable String agentId, @Nullable String name,
                   @NonNull State state) {
        mLogin = src.mLogin;
        mPasswordHash = src.mPasswordHash;
        mTerminal = src.mTerminal;
        mAgentId = agentId;
        mName = name;
        mState = state;
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

    @NonNull
    public State getState() {
        return mState;
    }

    @NonNull
    public Person cloneWithState(@Nullable String agentId, @Nullable String name, @NonNull State state) {
        return new Person(this, agentId, name, state);
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
        dest.writeInt(mState.code);
    }

    @Override
    public String toString() {
        return mName != null ? mName : mLogin;
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
