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

package org.pvoid.apteryx.data.agents;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.pvoid.apteryx.data.persons.Person;

public class Agent {

    public enum State {
        Ok(0),
        Warn(1),
        Error(2);

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
            return Ok;
        }
    }

    @NonNull private final String mId;
    @Nullable private final String mParentId;
    @Nullable private final String mPersonLogin;
    @Nullable private final String mINN;
    @Nullable private final String mJurAddress;
    @Nullable private final String mPhysAddress;
    @NonNull private final String mName;
    @Nullable private final String mCity;
    @Nullable private final String mFiscalMode;
    @Nullable private final String mKMM;
    @Nullable private final String mTaxRegnum;
    private int mTerminalsCount;
    private State mState;

    public Agent(@NonNull String id, @Nullable String parentId, @Nullable String inn,
                 @Nullable String jurAddress, @Nullable String physAddress, @NonNull  String name,
                 @Nullable String city, @Nullable String fiscalMode, @Nullable String kmm,
                 @Nullable String taxRegnum) {
        this(null, id, parentId, inn, jurAddress, physAddress, name, city, fiscalMode, kmm, taxRegnum, 0, State.Ok);
    }

    public Agent(@Nullable String personLogin, @NonNull String id, @Nullable String parentId, @Nullable String inn,
                 @Nullable String jurAddress, @Nullable String physAddress, @NonNull  String name,
                 @Nullable String city, @Nullable String fiscalMode, @Nullable String kmm,
                 @Nullable String taxRegnum, int terminalsCount, State state) {
        mId = id;
        mParentId = parentId;
        mINN = inn;
        mJurAddress = jurAddress;
        mPhysAddress = physAddress;
        mName = name;
        mCity = city;
        mFiscalMode = fiscalMode;
        mKMM = kmm;
        mTaxRegnum = taxRegnum;
        mPersonLogin = personLogin;
        mTerminalsCount = terminalsCount;
        mState = state;
    }

    private Agent(@NonNull Agent src,
                  @SuppressWarnings("NullableProblems") @NonNull String personLogin) {
        mId = src.mId;
        mParentId = src.mParentId;
        mINN = src.mINN;
        mJurAddress = src.mJurAddress;
        mPhysAddress = src.mPhysAddress;
        mName = src.mName;
        mCity = src.mCity;
        mFiscalMode = src.mFiscalMode;
        mKMM = src.mKMM;
        mTaxRegnum = src.mTaxRegnum;
        mTerminalsCount = src.mTerminalsCount;
        mState = src.mState;
        mPersonLogin = personLogin;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getParentId() {
        return mParentId;
    }

    @Nullable
    public String getINN() {
        return mINN;
    }

    @Nullable
    public String getJurAddress() {
        return mJurAddress;
    }

    @Nullable
    public String getPhysAddress() {
        return mPhysAddress;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    @Nullable
    public String getCity() {
        return mCity;
    }

    @Nullable
    public String getFiscalMode() {
        return mFiscalMode;
    }

    @Nullable
    public String getKMM() {
        return mKMM;
    }

    @Nullable
    public String getTaxRegnum() {
        return mTaxRegnum;
    }

    @Nullable
    public String getPersonLogin() {
        return mPersonLogin;
    }

    @NonNull
    public State getState() {
        return mState;
    }

    public int getTerminalsCount() {
        return mTerminalsCount;
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (o == null || !Agent.class.equals(o.getClass())) {
            return false;
        }
        return mId.equals(((Agent) o).mId);
    }

    @NonNull
    public Agent cloneForPerson(@NonNull Person person) {
        return new Agent(this, person.getLogin());
    }

    @NonNull
    public Agent cloneForState(int terminalsCount, @NonNull State state) {
        return new Agent(mPersonLogin, mId, mParentId, mINN, mJurAddress, mPhysAddress, mName, mCity, mFiscalMode,
                mKMM, mTaxRegnum, terminalsCount, state);
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(mPersonLogin) && !TextUtils.isEmpty(mId);
    }
}
