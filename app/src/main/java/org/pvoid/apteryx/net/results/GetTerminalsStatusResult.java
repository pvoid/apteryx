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

import org.pvoid.apteryx.data.terminals.TerminalState;
import org.pvoid.apteryx.util.LogHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GetTerminalsStatusResult extends Result {

    private static final String ATTR_AGENT_ID = "agtId";
    private static final String ATTR_LAST_ACTIVITY = "lastActivityTime";
    private static final String ATTR_LAST_PAYMENT = "lastPaymentTime";
    private static final String ATTR_MACHINE_STATUS = "machineStatus";
    private static final String ATTR_NOTE_ERROR_ID = "noteErrorId";
    private static final String ATTR_PRINTER_ERROR_ID = "printerErrorId";
    private static final String ATTR_CARD_READER_STATUS = "CardReaderStatus";
    private static final String ATTR_SIGNEL_LEVEL = "signalLevel";
    private static final String ATTR_SIM_BALANCE = "simProviderBalance";
    private static final String ATTR_TERMINAL_ID = "trmId";
    private static final String ATTR_DOOR_ALARM = "wdtDoorAlarmCount";
    private static final String ATTR_DOOR_OPEN = "wdtDoorOpenCount";
    private static final String ATTR_EVENT = "wdtEvent";
    private static final String ATTR_EVENT_TEXT = "wdtEventText";

    /*package*/ GetTerminalsStatusResult(@NonNull ResponseTag root) {
        super(root);
        ResponseTag row;
        List<TerminalState> states = null;
        try {
            while ((row = root.nextChild()) != null) {
                if (!"row".equals(row.getName())) {
                    continue;
                }
                final String agentId = row.getAttribute(ATTR_AGENT_ID);
                final String terminalId = row.getAttribute(ATTR_TERMINAL_ID);
                float simBalance = -1.f;
                int doorAlarm = -1;
                int doorOpen = -1;
                int event = -1;
                if (TextUtils.isEmpty(agentId) || TextUtils.isEmpty(terminalId)) {
                    continue;
                }

                try {
                    doorAlarm = Integer.parseInt(row.getAttribute(ATTR_DOOR_ALARM));
                } catch (NumberFormatException e) {
                    // nope
                }

                try {
                    doorOpen = Integer.parseInt(row.getAttribute(ATTR_DOOR_OPEN));
                } catch (NumberFormatException e) {
                    // nope
                }

                try {
                    event = Integer.parseInt(row.getAttribute(ATTR_EVENT));
                } catch (NumberFormatException e) {
                    // nope
                }

                try {
                    simBalance = Float.parseFloat(row.getAttribute(ATTR_SIM_BALANCE));
                } catch (NumberFormatException e) {
                    // nope
                }

                if (states == null) {
                    states = new ArrayList<>();
                }

                states.add(new TerminalState(terminalId, agentId, parseDateTime(row.getAttribute(ATTR_LAST_ACTIVITY)),
                        parseDateTime(row.getAttribute(ATTR_LAST_PAYMENT)),
                        parseStateFlags(row.getAttribute(ATTR_MACHINE_STATUS)),
                        row.getAttribute(ATTR_NOTE_ERROR_ID),
                        row.getAttribute(ATTR_PRINTER_ERROR_ID),
                        row.getAttribute(ATTR_CARD_READER_STATUS),
                        row.getAttribute(ATTR_SIGNEL_LEVEL),
                        simBalance, doorAlarm, doorOpen, event, row.getAttribute(ATTR_EVENT_TEXT)
                ));
            }
        } catch (ResponseTag.TagReadException e) {
            LogHelper.error("Network", "Error while reading getTerminals result: %1$s", e.getMessage());
        }
    }

    /* package */ static int parseStateFlags(@Nullable String flags) {
        int result = 0;
        if (flags == null || flags.length() != 24) {
            return result;
        }

        for (int index = 0; index < 24; ++index) {
            char c = flags.charAt(index);
            if (!Character.isDigit(c)) {
                return 0;
            }
            result <<= 1;
            if ( c == '1') {
                result |= 1;
            }
        }

        return result;
    }

    /* package */ static long parseDateTime(@Nullable String date) {
        if (!TextUtils.isEmpty(date)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.ENGLISH);
            try {
                return format.parse(date).getTime();
            } catch (ParseException e) {
                LogHelper.error("Network", "Can't parse date in terminal status", e);
            }
        }
        return -1;
    }

    public static class Factory implements ResultFactory {
        @Nullable
        @Override
        public Result create(@NonNull ResponseTag tag) {
            return new GetTerminalsStatusResult(tag);
        }
    }
}
