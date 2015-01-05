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

package org.pvoid.apteryx.data.accounts;

public class Agent {
    private final String mId;
    private final String mParentId;
    private final String mINN;
    private final String mJurAddress;
    private final String mPhysAddress;
    private final String mName;
    private final String mCity;
    private final String mFiscalMode;
    private final String mKMM;
    private final String mTaxRegnum;

    public Agent(String id, String parentId, String inn, String jurAddress, String physAddress,
                 String name, String city, String fiscalMode, String kmm, String taxRegnum) {
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
    }

    public String getId() {
        return mId;
    }

    public String getParentId() {
        return mParentId;
    }

    public String getINN() {
        return mINN;
    }

    public String getJurAddress() {
        return mJurAddress;
    }

    public String getPhysAddress() {
        return mPhysAddress;
    }

    public String getName() {
        return mName;
    }

    public String getCity() {
        return mCity;
    }

    public String getFiscalMode() {
        return mFiscalMode;
    }

    public String getKMM() {
        return mKMM;
    }

    public String getTaxRegnum() {
        return mTaxRegnum;
    }
}
