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

package org.pvoid.apteryx.data.terminals;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Terminal {
    private static final String ATTR_FULL_ADDRESS = "full_address";

    private static final String ATTR_ARENDA = "arenda";
    private static final String ATTR_FISCAL_MODE = "fiscal_mode";
    private static final String ATTR_KKM = "kkm_registration_number";
    private static final String ATTR_LABEL = "label";
    private static final String ATTR_TAX_NUM = "taxpayer_regnum";
    private static final String ATTR_ADDRESS = "trm_address";
    private static final String ATTR_AGENT_NAME = "trm_agt_display";
    private static final String ATTR_CONTACT_PERS = "trm_contact_pers";
    private static final String ATTR_PHONE = "trm_phone";
    private static final String ATTR_UNION_CODE = "union_code";
    private static final String ATTR_URL = "url";

    @NonNull private final String mId;
    @NonNull private final TerminalType mType;
    @Nullable private final String mSerial;
    @NonNull private final String mDisplayName;
    @Nullable private final String mWhoAdded;
    @Nullable private final String mWorkTime;
    @NonNull private final String mAgentId;

    @Nullable private String mCity;
    private int mCityId;
    @Nullable private String mAddress;
    @Nullable private String mMainAddress;

    @Nullable private String mPersonId;

    @Nullable private TerminalState mState;
    @Nullable private TerminalStats mStats;

    public Terminal(@NonNull String id, @NonNull String agentId, @NonNull TerminalType type, @Nullable String serial,
                    @NonNull String displayName, @Nullable String whoAdded,
                    @Nullable String workTime) {
        mId = id;
        mAgentId = agentId;
        mType = type;
        mSerial = serial;
        mDisplayName = displayName;
        mWhoAdded = whoAdded;
        mWorkTime = workTime;
    }

    public void setCity(int cityId, @NonNull String city) {
        mCity = city;
        mCityId = cityId;
    }

    public void setAddress(@NonNull String address, @Nullable String mainAddress) {
        mAddress = address;
        mMainAddress = mainAddress;
    }

    @Nullable
    public String getCity() {
        return mCity;
    }

    public int getCityId() {
        return mCityId;
    }

    @Nullable
    public String getDisplayAddress() {
        return mAddress;
    }

    @Nullable
    public String getMainAddress() {
        return mMainAddress;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public TerminalType getType() {
        return mType;
    }

    @Nullable
    public String getSerial() {
        return mSerial;
    }

    @NonNull
    public String getDisplayName() {
        return mDisplayName;
    }

    @Nullable
    public String getWhoAdded() {
        return mWhoAdded;
    }

    @Nullable
    public String getWorkTime() {
        return mWorkTime;
    }

    @NonNull
    public String getAgentId() {
        return mAgentId;
    }

    @Nullable
    public String getPersonId() {
        return mPersonId;
    }

    public void setPersonId(@Nullable String personId) {
        mPersonId = personId;
    }

    @Nullable
    public TerminalState getState() {
        return mState;
    }

    @Nullable
    public TerminalStats getStats() {
        return mStats;
    }

    public void setState(@Nullable TerminalState state) {
        mState = state;
    }

    public void setStats(@Nullable TerminalStats stats) {
        mStats = stats;
    }
}
