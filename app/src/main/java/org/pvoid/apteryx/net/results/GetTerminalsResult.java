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

package org.pvoid.apteryx.net.results;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.pvoid.apteryx.data.terminals.Terminal;
import org.pvoid.apteryx.data.terminals.TerminalType;
import org.pvoid.apteryx.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

public class GetTerminalsResult extends Result {

    private static final String ATTR_ID = "trm_id";
    private static final String ATTR_SERIAL = "trm_serial";
    private static final String ATTR_TYPE_ID = "ttp_id";
    private static final String ATTR_INFO = "trm_info";
    private static final String ATTR_WHO = "trm_who_added";
    private static final String ATTR_DISPLAY = "trm_display";
    private static final String ATTR_WORK_TIME = "trm_work_time";
    private static final String ATTR_AGENT_ID = "agt_id";

    private static final String ATTR_CITY = "city";
    private static final String ATTR_CITY_ID = "trm_city_id";
    private static final String ATTR_DISPLAY_ADDRESS = "address";
    private static final String ATTR_MAIN_ADDRESS = "main_address";
    private static final String ATTR_DISTRICT = "trm_district_id";
    private static final String ATTR_STREET_ID = "trm_street_id";
    private static final String ATTR_TIP_MESTA = "trm_tip_mesta";
    private static final String ATTR_METRO_ID = "trm_metro_id";

    @Nullable
    private final Terminal[] mTerminals;

    public GetTerminalsResult(@NonNull ResponseTag root) {
        super(root);
        List<Terminal> terminals = null;
        ResponseTag row;
        try {
            while ((row = root.nextChild()) != null) {
                if ("row".equals(row.getName())) {
                    final String id = row.getAttribute(ATTR_ID);
                    final String agentId = row.getAttribute(ATTR_AGENT_ID);
                    final String typeId = row.getAttribute(ATTR_TYPE_ID);
                    final String displayName = row.getAttribute(ATTR_DISPLAY);
                    if (id == null || agentId == null || typeId == null || displayName == null) {
                        continue;
                    }
                    if (terminals == null) {
                        terminals = new ArrayList<>();
                    }

                    Terminal terminal = new Terminal(id, agentId, TerminalType.fromString(typeId),
                            row.getAttribute(ATTR_SERIAL), displayName, row.getAttribute(ATTR_WHO),
                            row.getAttribute(ATTR_WORK_TIME));
                    final String city = row.getAttribute(ATTR_CITY);
                    final String cityId = row.getAttribute(ATTR_CITY_ID);
                    if (!TextUtils.isEmpty(city) && !TextUtils.isEmpty(cityId)) {
                        try {
                            terminal.setCity(Integer.parseInt(cityId), city);
                        } catch (NumberFormatException e) {
                            LogHelper.error("Network", "Error while parsing city id");
                        }
                    }
                    final String address = row.getAttribute(ATTR_DISPLAY_ADDRESS);
                    if (!TextUtils.isEmpty(address)) {
                        terminal.setAddress(address, row.getAttribute(ATTR_MAIN_ADDRESS));
                    }

                    terminals.add(terminal);
                }
            }
        } catch (ResponseTag.TagReadException e) {
            LogHelper.error("Network", "Error while reading getTerminals result: %1$s", e.getMessage());
        }
        if (terminals == null) {
            mTerminals = null;
        } else {
            mTerminals = terminals.toArray(new Terminal[terminals.size()]);
        }
    }

    @Nullable
    public Terminal[] getTerminals() {
        return mTerminals;
    }

    public static class Factory implements ResultFactory {
        @Nullable
        @Override
        public Result create(@NonNull ResponseTag tag) {
            return new GetTerminalsResult(tag);
        }
    }
}
