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
    @NonNull private final String mId;
    @NonNull private final String mParentId;
    @Nullable private final String mPersonLogin;
    @NonNull private final String mINN;
    @NonNull private final String mJurAddress;
    @NonNull private final String mPhysAddress;
    @NonNull private final String mName;
    @NonNull private final String mCity;
    @NonNull private final String mFiscalMode;
    @NonNull private final String mKMM;
    @NonNull private final String mTaxRegnum;

    public Agent(@NonNull String id, @NonNull String parentId, @NonNull String inn,
                 @NonNull String jurAddress, @NonNull String physAddress, @NonNull  String name,
                 @NonNull String city, @NonNull String fiscalMode, @NonNull String kmm,
                 @NonNull String taxRegnum) {
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
        mPersonLogin = null;
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
        mPersonLogin = personLogin;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getParentId() {
        return mParentId;
    }

    @NonNull
    public String getINN() {
        return mINN;
    }

    @NonNull
    public String getJurAddress() {
        return mJurAddress;
    }

    @NonNull
    public String getPhysAddress() {
        return mPhysAddress;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public String getCity() {
        return mCity;
    }

    @NonNull
    public String getFiscalMode() {
        return mFiscalMode;
    }

    @NonNull
    public String getKMM() {
        return mKMM;
    }

    @NonNull
    public String getTaxRegnum() {
        return mTaxRegnum;
    }

    @Nullable
    public String getPersonLogin() {
        return mPersonLogin;
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

    public boolean isValid() {
        return !TextUtils.isEmpty(mPersonLogin)  && !TextUtils.isEmpty(mId);
    }
}
