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

public class GetTerminalsResult extends Result {

    private static final String ATTR_ID = "trm_id";
    private static final String ATTR_SERIAL = "trm_serial";
    private static final String ATTR_TYPE_ID = "ttp_id";
    private static final String ATTR_INFO = "trm_info";
    private static final String ATTR_WHO = "trm_who_added";
    private static final String ATTR_DISPLAY = "trm_display";
    private static final String ATTR_WORK_TIME = "trm_work_time";
    private static final String ATTR_AGENT_ID = "agt_id";

    public GetTerminalsResult(@NonNull ResponseTag root) {
        super(root);
    }
}
