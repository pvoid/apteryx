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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.pvoid.apteryx.R;

import java.util.ArrayList;
import java.util.List;

public class TerminalState {

    public static final int FLAG_STATE_ASO_MONITOR_DISABLED = 0x00001;  // Монитор АСО выключен
    public static final int FLAG_STATE_ASO_APP_MODIFIED     = 0x00004;  // Модифицировано приложение АСО
    public static final int FLAG_STATE_UPDATING_FILES       = 0x00010;  // Терминал проверяет и обновляет файлы
    public static final int FLAG_STATE_UPDATING_ADVERTS     = 0x00020;  // Терминал проверяет и обновляет рекламный плейлист
    public static final int FLAG_STATE_UPDATING_PROVIDERS   = 0x00040;  // Терминал обновляет список провайдеров
    public static final int FLAG_STATE_UPDATING_NUMBERS     = 0x00080;  // Терминал обновляет номерные емкости
    public static final int FLAG_STATE_UPDATING_CONFIGS     = 0x00100;  // Терминал обновляет конфигурацию
    public static final int FLAG_STATE_PROXY                = 0x00400;  // Автомат работает через прокси-сервер
    public static final int FLAG_STATE_DANGEROUS_SOFTWARE   = 0x00800;  // Обнаружено стороннее ПО, которое может вызвать сбой модемного соединения
    public static final int FLAG_STATE_LOCAL_NETWORK        = 0x01000;  // Автомат работает в локальной сети
    public static final int FLAG_STATE_DUAL_DISPLAY         = 0x02000;  // Автомат оснащен вторым монитором
    public static final int FLAG_STATE_STOPPED_BY_SERVER    = 0x04000;  // Остановлен по сигналу сервера или из-за отсутствия денег на счету агента
    public static final int FLAG_STATE_HDD_WARNINGS         = 0x08000;  // Проблемы с жестким диском
    public static final int FLAG_STATE_INFO_ERRORS          = 0x10000;  // Отсутствуют или неверно заполнены один или несколько реквизитов для терминала
    public static final int FLAG_STATE_NOTE_ABSENT          = 0x20000;  // C автомата был снят купюроприемник
    public static final int FLAG_STATE_PAPER_WARNING        = 0x40000;  // В принтере скоро закончится бумага
    public static final int FLAG_STATE_GUARD_TIMER          = 0x80000;  // Работает сторожевой таймер
    public static final int FLAG_STATE_HARDWARE_ABSENT      = 0x100000; // Автомат остановлен из-за того, что при старте не обнаружено оборудование (купюроприемник или принтер)
    public static final int FLAG_STATE_DOWNLOADING_UPDATES  = 0x200000; // Автомат загружает с сервера обновление приложения
    public static final int FLAG_STATE_UI_CONFIG_ERROR      = 0x400000; // Автомат остановлен из-за ошибки в конфигурации интерфейса
    public static final int FLAG_STATE_HARDWARE_ERROR       = 0x800000; // Автомат остановлен из-за ошибок купюроприемника или принтера

    private static final MachineState STATES[] = new MachineState[] {
        new MachineState(MachineState.LEVEL_ERROR, FLAG_STATE_STOPPED_BY_SERVER, R.string.state_stopped_by_server),
        new MachineState(MachineState.LEVEL_ERROR, FLAG_STATE_HARDWARE_ERROR, R.string.state_hardware_error),
        new MachineState(MachineState.LEVEL_ERROR, FLAG_STATE_HARDWARE_ABSENT, R.string.state_hardware_absent),
        new MachineState(MachineState.LEVEL_ERROR, FLAG_STATE_UI_CONFIG_ERROR, R.string.state_ui_config_error),

        new MachineState(MachineState.LEVEL_WARN, FLAG_STATE_DANGEROUS_SOFTWARE, R.string.state_dangerous_software),
        new MachineState(MachineState.LEVEL_WARN, FLAG_STATE_HDD_WARNINGS, R.string.state_hdd_warnings),
        new MachineState(MachineState.LEVEL_WARN, FLAG_STATE_NOTE_ABSENT, R.string.state_note_absent),
        new MachineState(MachineState.LEVEL_WARN, FLAG_STATE_PAPER_WARNING, R.string.state_paper_warning),

        new MachineState(MachineState.LEVEL_INFO, FLAG_STATE_INFO_ERRORS, R.string.state_info_errors),
        new MachineState(MachineState.LEVEL_INFO, FLAG_STATE_ASO_APP_MODIFIED, R.string.state_aso_app_modified),
        new MachineState(MachineState.LEVEL_INFO, FLAG_STATE_DOWNLOADING_UPDATES, R.string.state_downloading_updates),
        new MachineState(MachineState.LEVEL_INFO, FLAG_STATE_UPDATING_FILES, R.string.state_updating_files),
        new MachineState(MachineState.LEVEL_INFO, FLAG_STATE_UPDATING_CONFIGS, R.string.state_updating_configs),
        new MachineState(MachineState.LEVEL_INFO, FLAG_STATE_UPDATING_PROVIDERS, R.string.state_updating_providers),
        new MachineState(MachineState.LEVEL_INFO, FLAG_STATE_UPDATING_NUMBERS, R.string.state_updating_numbers),
        new MachineState(MachineState.LEVEL_INFO, FLAG_STATE_UPDATING_ADVERTS, R.string.state_updating_adverts),
        new MachineState(MachineState.LEVEL_INFO, FLAG_STATE_ASO_MONITOR_DISABLED, R.string.state_aso_monitor_disabled),
        new MachineState(MachineState.LEVEL_INFO, FLAG_STATE_PROXY, R.string.state_proxy),
        new MachineState(MachineState.LEVEL_INFO, FLAG_STATE_LOCAL_NETWORK, R.string.state_local_network),
        new MachineState(MachineState.LEVEL_INFO, FLAG_STATE_DUAL_DISPLAY, R.string.state_dual_display),
        new MachineState(MachineState.LEVEL_INFO, FLAG_STATE_GUARD_TIMER, R.string.state_guard_timer)
    };

    private static final String STATE_OK = "OK";

    @NonNull private final String mId;
    @NonNull private final String mAgentId;
    private long mLastActivity;
    private long mLastPayment;
    private int mMachineStatus;
    @Nullable private String mNoteError;
    @Nullable private String mPrinterError;
    @Nullable private String mCardReaderStatus;
    @Nullable private String mSignalLevel;
    private float mSimBalance;
    private int mDoorAlarmCount;
    private int mDoorOpenCount;
    private int mEvent;
    private String mEventText;

    private boolean mHasError;
    private boolean mHasWarning;
    private boolean mHasNoteError;
    private boolean mHasPrinterError;

    public TerminalState(@NonNull String id, @NonNull String agentId, long lastActivity,
                         long lastPayment, int machineStatus, @Nullable String noteError,
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

        mHasNoteError = !TextUtils.isEmpty(noteError) && !TextUtils.equals(STATE_OK, noteError);
        mHasPrinterError = !TextUtils.isEmpty(printerError) && !TextUtils.equals(STATE_OK, printerError);
        if (mHasNoteError || mHasPrinterError) {
            mHasError = true;
        } else {
            for (MachineState state : STATES) {
                if ((state.flag & machineStatus) != 0) {
                    if (state.level == MachineState.LEVEL_ERROR) {
                        mHasError = true;
                    } else if (state.level == MachineState.LEVEL_WARN) {
                        mHasWarning = true;
                    }
                    break;
                }
                if (state.level == MachineState.LEVEL_INFO) {
                    break;
                }
            }
        }
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

    public boolean hasNoteError() {
        return mHasNoteError;
    }

    public boolean hasPrinterError() {
        return mHasPrinterError;
    }

    public boolean hasErrors() {
        return mHasError;
    }

    public boolean hasWarnings() {
        return mHasWarning;
    }

    @Nullable
    public CharSequence getMessage(@NonNull Context context) {
        if (mHasNoteError) {
            return mNoteError;
        }
        if (mHasPrinterError) {
            return mPrinterError;
        }
        for (MachineState state : STATES) {
            if ((state.flag & mMachineStatus) != 0) {
                return context.getString(state.text);
            }
            if (state.level == MachineState.LEVEL_INFO) {
                break;
            }
        }
        return null;
    }

    @Nullable
    public String getPrinterError() {
        return mPrinterError;
    }

    @NonNull
    public String getNoteError() {
        return mNoteError;
    }

    @NonNull
    public MachineState[] getStates() {
        List<MachineState> states = new ArrayList<>();
        for (MachineState state : STATES) {
            if ((state.flag & mMachineStatus) != 0) {
                states.add(state);
            }
        }
        return states.toArray(new MachineState[states.size()]);
    }
}
