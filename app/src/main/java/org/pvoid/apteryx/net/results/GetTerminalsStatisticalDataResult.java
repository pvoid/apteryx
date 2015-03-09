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

import org.pvoid.apteryx.data.terminals.TerminalStats;
import org.pvoid.apteryx.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class GetTerminalsStatisticalDataResult extends Result {

    private static final String ATTR_TERMINAL_ID = "trmId"; // идентификатор терминала;
    private static final String ATTR_AGENT_ID = "agtId"; // идентификатор агента;
    private static final String ATTR_SYSTEM_UPTIME = "systemUpTime"; // время работы ОС терминала;
    private static final String ATTR_UPTIME = "progUpTime"; // время работы ПО терминала;
    private static final String ATTR_PAYS_PER_HOUR = "paysPerHour"; // среднее число запросов на проведение платежей в час;
    private static final String ATTR_BILLS_PER_PAY = "billsPerPay"; // среднее число платежей, передаваемых в одном запросе;
    private static final String ATTR_CARD_READER_USED_HR = "CardReaderUsedHour"; // количество успешных считываний карт в картридере за текущий час;
    private static final String ATTR_CARD_READER_USED_DAY = "CardReaderUsedDay"; // количество успешных считываний карт в картридере за текущие сутки;
    private static final String ATTR_TIME_TO_CASHIN_FULL = "timeToCashinFull"; // оставшееся время до заполнения купюроприемника;
    private static final String ATTR_TIME_TO_CASHIN_SERVICE = "timeToCashinService"; // оставшееся время до обслуживания купюроприемника;
    private static final String ATTR_TIME_TO_PRINTER_PAPER_OUT = "timeToPrinterPaperOut"; // оставшееся время до окончания бумаги в принтере;
    private static final String ATTR_TIME_TO_PRINTER_SERVICE = "timeToPrinterService"; // оставшееся время до обслуживания принтера.

    @Nullable
    private final TerminalStats[] mStats;

    /*package*/ GetTerminalsStatisticalDataResult(@NonNull ResponseTag root) {
        super(root);
        ResponseTag row;
        List<TerminalStats> stats = null;
        try {
            while ((row = root.nextChild()) != null) {
                if (!"row".equals(row.getName())) {
                    continue;
                }
                final String terminalId = row.getAttribute(ATTR_TERMINAL_ID);
                final String agentId = row.getAttribute(ATTR_AGENT_ID);
                if (TextUtils.isEmpty(terminalId) || TextUtils.isEmpty(agentId)) {
                    continue;
                }
                TerminalStats stat = new TerminalStats(terminalId, agentId,
                        StringUtils.parseInt(row.getAttribute(ATTR_SYSTEM_UPTIME), 0),
                        StringUtils.parseInt(row.getAttribute(ATTR_UPTIME), 0),
                        StringUtils.parseFloat(row.getAttribute(ATTR_PAYS_PER_HOUR), 0),
                        StringUtils.parseFloat(row.getAttribute(ATTR_BILLS_PER_PAY), 0),
                        StringUtils.parseInt(row.getAttribute(ATTR_CARD_READER_USED_HR), 0),
                        StringUtils.parseInt(row.getAttribute(ATTR_CARD_READER_USED_DAY), 0),
                        StringUtils.parseLong(row.getAttribute(ATTR_TIME_TO_CASHIN_FULL), 0),
                        StringUtils.parseLong(row.getAttribute(ATTR_TIME_TO_CASHIN_SERVICE), 0),
                        StringUtils.parseLong(row.getAttribute(ATTR_TIME_TO_PRINTER_PAPER_OUT), 0),
                        StringUtils.parseLong(row.getAttribute(ATTR_TIME_TO_PRINTER_SERVICE), 0)
                );

                if (stats == null) {
                    stats = new ArrayList<>();
                }
                stats.add(stat);
            }
        } catch (ResponseTag.TagReadException e) {
            LOG.error("Error while parsing getTerminalsStatisticalData() result", e);
        }

        if (stats == null) {
            mStats = null;
        } else {
            mStats = stats.toArray(new TerminalStats[stats.size()]);
        }
    }

    @Nullable
    public TerminalStats[] getStats() {
        return mStats;
    }

    public static class Factory implements ResultFactory {
        @Nullable
        @Override
        public Result create(@NonNull ResponseTag tag) {
            return new GetTerminalsStatisticalDataResult(tag);
        }
    }
}
