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

public class TerminalStats {
    @NonNull private final String mTerminalId; // trmId – идентификатор терминала;
    @NonNull private final String mAgentId; // agtId – идентификатор агента;
    private int mSystemUpTime; // systemUpTime – время работы ОС терминала;
    private int mUpTime; // progUpTime – время работы ПО терминала;
    private float mPaysPerHour; // paysPerHour – среднее число запросов на проведение платежей в час;
    private float mBillsPerPay; // billsPerPay – среднее число платежей, передаваемых в одном запросе;
    private int mCardReaderUsedHours; // CardReaderUsedHour – количество успешных считываний карт в картридере за текущий час;
    private int mCardReaderUsedDay; // CardReaderUsedDay – количество успешных считываний карт в картридере за текущие сутки;
    private long mTimeToCashinFull; // timeToCashinFull – оставшееся время до заполнения купюроприемника;
    private long mTimeToCashinService; // timeToCashinService – оставшееся время до обслуживания купюроприемника;
    private long mTimeToPrinterPaperOut; // timeToPrinterPaperOut – оставшееся время до окончания бумаги в принтере;
    private long mTimeToPrinterService; // timeToPrinterService – оставшееся время до обслуживания принтера.

    public TerminalStats(@NonNull String terminalId, @NonNull String agentId, int systemUpTime,
                         int upTime, float paysPerHour, float billsPerPay, int cardReaderUsedHours,
                         int cardReaderUsedDay, long timeToCashinFull, long timeToCashinService,
                         long timeToPrinterPaperOut, long timeToPrinterService) {
        mTerminalId = terminalId;
        mAgentId = agentId;
        mSystemUpTime = systemUpTime;
        mUpTime = upTime;
        mPaysPerHour = paysPerHour;
        mBillsPerPay = billsPerPay;
        mCardReaderUsedHours = cardReaderUsedHours;
        mCardReaderUsedDay = cardReaderUsedDay;
        mTimeToCashinFull = timeToCashinFull;
        mTimeToCashinService = timeToCashinService;
        mTimeToPrinterPaperOut = timeToPrinterPaperOut;
        mTimeToPrinterService = timeToPrinterService;
    }

    @NonNull
    public String getTerminalId() {
        return mTerminalId;
    }

    @NonNull
    public String getAgentId() {
        return mAgentId;
    }

    public int getSystemUpTime() {
        return mSystemUpTime;
    }

    public int getUpTime() {
        return mUpTime;
    }

    public float getPaysPerHour() {
        return mPaysPerHour;
    }

    public float getBillsPerPay() {
        return mBillsPerPay;
    }

    public int getCardReaderUsedHours() {
        return mCardReaderUsedHours;
    }

    public int getCardReaderUsedDay() {
        return mCardReaderUsedDay;
    }

    public long getTimeToCashinFull() {
        return mTimeToCashinFull;
    }

    public long getTimeToCashinService() {
        return mTimeToCashinService;
    }

    public long getTimeToPrinterPaperOut() {
        return mTimeToPrinterPaperOut;
    }

    public long getTimeToPrinterService() {
        return mTimeToPrinterService;
    }
}
