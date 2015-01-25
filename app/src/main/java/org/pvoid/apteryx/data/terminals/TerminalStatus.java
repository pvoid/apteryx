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

public class TerminalStatus {
    @NonNull private final String mId; // trmId – идентификатор терминала;
    @NonNull private final String mAgentId; // agtId – идентификатор агента;
    private long mLastActivity; // lastActivityTime – время последней активности (время последнего ping, отправленного с данного терминала);
    private long mLastPayment; // lastPaymentTime – время и дата последнего отправленного с терминала платежа;
    private int mMachineStatus; // machineStatus – набор флагов состояния терминала. Подробнее о флагах см.
    @Nullable private String mNoteError; // noteErrorId – текствовое описание ошибки купюроприемника;
    @Nullable private String mPrinterError; // printerErrorId – текстовое описание ошибки принтера;
    @Nullable private String mCardReaderStatus; // CardReaderStatus – состояние работы картридера;
    @Nullable private String mSignalLevel; // signalLevel – уровень сигнала;
    private float mSimBalance; // simProviderBalance – баланс на SIM-карте;
    private int mDoorAlarmCount; // wdtDoorAlarmCount – счетчик тревог двери;
    private int mDoorOpenCount; // wdtDoorOpenCount – счетчик открытий двери;
    private int mEvent; // атрибут содержит в зашифрованном побитовом виде информацию о стостоянии терминала (питании, закрытии двери, состоянии батареи UPS и пр.);
    private String mEventText; // wdtEventText – описание бита состояния терминала (атрибут возвращается, если wdtEvent не равен 0). См. Приложение К.


    public TerminalStatus(@NonNull String id, @NonNull String agentId, long lastActivity,
                          long lastPayment, int machineStatus, String noteError,
                          @Nullable String printerError, @Nullable String cardReaderStatus,
                          @Nullable String signalLevel, float simBalance, int doorAlarmCount,
                          int doorOpenCount, int event, String eventText) {
        mId = id;
        mAgentId = agentId;
        mLastActivity = lastActivity;
        mLastPayment = lastPayment;
        mMachineStatus = machineStatus;
        mNoteError = noteError;
        mPrinterError = printerError;
        mCardReaderStatus = cardReaderStatus;
        mSignalLevel = signalLevel;
        mSimBalance = simBalance;
        mDoorAlarmCount = doorAlarmCount;
        mDoorOpenCount = doorOpenCount;
        mEvent = event;
        mEventText = eventText;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getAgentId() {
        return mAgentId;
    }

    public long getLastActivity() {
        return mLastActivity;
    }

    public long getLastPayment() {
        return mLastPayment;
    }

    public int getMachineStatus() {
        return mMachineStatus;
    }

    @Nullable
    public String getNoteError() {
        return mNoteError;
    }

    @Nullable
    public String getPrinterError() {
        return mPrinterError;
    }

    @Nullable
    public String getCardReaderStatus() {
        return mCardReaderStatus;
    }

    @Nullable
    public String getSignalLevel() {
        return mSignalLevel;
    }

    public float getSimBalance() {
        return mSimBalance;
    }

    public int getDoorAlarmCount() {
        return mDoorAlarmCount;
    }

    public int getDoorOpenCount() {
        return mDoorOpenCount;
    }

    public int getEvent() {
        return mEvent;
    }

    public String getEventText() {
        return mEventText;
    }
}
