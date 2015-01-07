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
    private static final String ATTR_DISPLAY_ADDRESS = "address";
    private static final String ATTR_ARENDA = "arenda";
    private static final String ATTR_CITY = "city";
    private static final String ATTR_FISCAL_MODE = "fiscal_mode";
    private static final String ATTR_FULL_ADDRESS = "full_address";
    private static final String ATTR_KKM = "kkm_registration_number";
    private static final String ATTR_LABEL = "label";
    private static final String ATTR_MAIN_ADDRESS = "main_address";
    private static final String ATTR_PRIORITY = "priority";
    private static final String ATTR_TAX_NUM = "taxpayer_regnum";
    private static final String ATTR_ADDRESS = "trm_address";
    private static final String ATTR_AGENT_NAME = "trm_agt_display";
    private static final String ATTR_APPROVED = "trm_approved";
    private static final String ATTR_BEE_ID = "trm_bee_id";
    private static final String ATTR_BEE_REGION = "trm_bee_region";
    private static final String ATTR_CITY_ID = "trm_city_id";
    private static final String ATTR_CONTACT_PERS = "trm_contact_pers";
    private static final String ATTR_DISTRICT = "trm_district_id";
    private static final String ATTR_DUP = "trm_dup";
    private static final String ATTR_METRO_ID = "trm_metro_id";
    private static final String ATTR_MONITOR_BILLS = "trm_monitor_bills";
    private static final String ATTR_MONITOR_HEALTH = "trm_monitor_health";
    private static final String ATTR_MONITOR_LASTDATE = "trm_monitor_lastdate";
    private static final String ATTR_PHONE = "trm_phone";
    private static final String ATTR_STREET_ABSENT = "trm_street_absent";
    private static final String ATTR_STREET_ID = "trm_street_id";
    private static final String ATTR_TIP_MESTA = "trm_tip_mesta";
    private static final String ATTR_UNION_CODE = "union_code";
    private static final String ATTR_URL = "url";

    private final String mId;
    private final String mTypeId;
    private final String mSerial;
    private final String mDisplayName;
    private final String mWhoAdded;
    private final String mWorkTime;
    private final String mAgentId;

    public Terminal(@NonNull String id, @NonNull String agentId, @NonNull String typeId, @NonNull String serial,
                    @NonNull String displayName, @Nullable String whoAdded,
                    @Nullable String workTime) {
        mId = id;
        mAgentId = agentId;
        mTypeId = typeId;
        mSerial = serial;
        mDisplayName = displayName;
        mWhoAdded = whoAdded;
        mWorkTime = workTime;
    }
}
