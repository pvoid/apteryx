/*
 * Copyright (C) 2010-2014  Dmitry "PVOID" Petuhov
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

package org.pvoid.apteryx.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.pvoid.apteryx.data.agents.Agent;
import org.pvoid.apteryx.data.persons.Person;
import org.pvoid.apteryx.data.terminals.*;
import org.pvoid.apteryx.util.LogHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/* package */ class DataStorage implements Storage {

    private static final String TAG = "Storage";

    /* package */ static final String DB_NAME = "apteryx";
    /* package */ static final int DB_VERSION = 1;

    private interface PersonsTable {
        String NAME = "persons";
        String COLUMN_LOGIN = "login";
        String COLUMN_PASSWORD = "password";
        String COLUMN_TERMINAL = "terminal";
        String COLUMN_NAME = "name";
        String COLUMN_AGENT_ID = "agent_id";
        String COLUMN_ENABLED = "enabled";
        String COLUMN_VERIFIED = "verified";
        String[] ALL_COLUMNS = new String[] {
            COLUMN_LOGIN, COLUMN_PASSWORD, COLUMN_TERMINAL, COLUMN_NAME,
            COLUMN_AGENT_ID, COLUMN_ENABLED, COLUMN_VERIFIED
        };
        int COLUMN_LOGIN_INDEX = 0;
        int COLUMN_PASSWORD_INDEX = 1;
        int COLUMN_TERMINAL_INDEX = 2;
        int COLUMN_NAME_INDEX = 3;
        int COLUMN_AGENT_ID_INDEX = 4;
        int COLUMN_ENABLED_INDEX = 5;
        int COLUMN_VERIFIED_INDEX = 6;
    }

    private interface AgentsTable {
        String NAME = "agents";
        String COLUMN_AGENT_ID = "agent_id";
        String COLUMN_PARENT_ID = "parent_id";
        String COLUMN_PERSON_LOGIN = "person_login";
        String COLUMN_INN = "inn";
        String COLUMN_JUR_ADDRESS = "jur_address";
        String COLUMN_PHYS_ADDRESS = "phys_address";
        String COLUMN_NAME = "name";
        String COLUMN_CITY = "city";
        String COLUMN_FISCAL_MODE = "fiscal_mode";
        String COLUMN_KMM = "kmm";
        String COLUMN_TAX_REGNUM = "tax_regnum";
        String COLUMN_TERMINALS = "terminals_count";
        String COLUMN_STATE = "state";
        String[] ALL_COLUMNS = new String[]{
            COLUMN_AGENT_ID, COLUMN_PARENT_ID, COLUMN_PERSON_LOGIN, COLUMN_INN,
            COLUMN_JUR_ADDRESS, COLUMN_PHYS_ADDRESS, COLUMN_NAME, COLUMN_CITY,
            COLUMN_FISCAL_MODE, COLUMN_KMM, COLUMN_TAX_REGNUM, COLUMN_TERMINALS,
            COLUMN_STATE
        };
        int COLUMN_AGENT_ID_INDEX = 0;
        int COLUMN_PARENT_ID_INDEX = 1;
        int COLUMN_PERSON_LOGIN_INDEX = 2;
        int COLUMN_INN_INDEX = 3;
        int COLUMN_JUR_ADDRESS_INDEX = 4;
        int COLUMN_PHYS_ADDRESS_INDEX = 5;
        int COLUMN_NAME_INDEX = 6;
        int COLUMN_CITY_INDEX = 7;
        int COLUMN_FISCAL_MODE_INDEX = 8;
        int COLUMN_KMM_INDEX = 9;
        int COLUMN_TAX_REGNUM_INDEX = 10;
        int COLUMN_TERMINALS_INDEX = 11;
        int COLUMN_STATE_INDEX = 12;
    }

    private interface TerminalsTable {
        String NAME = "terminals";
        String COLUMN_ID = "id";
        String COLUMN_TYPE = "type";
        String COLUMN_SERIAL = "serial";
        String COLUMN_NAME = "display_name";
        String COLUMN_WHO = "who_added";
        String COLUMN_WORK_TIME = "work_time";
        String COLUMN_AGENT_ID = "agent_id";
        String COLUMN_CITY = "city";
        String COLUMN_CITY_ID = "city_id";
        String COLUMN_DISPLAY_ADDRESS = "display_address";
        String COLUMN_MAIN_ADDRESS = "main_address";
        String COLUMN_PERSON_ID = "person_id";
        String[] ALL_COLUMNS = new String[]{
            COLUMN_ID, COLUMN_TYPE, COLUMN_SERIAL, COLUMN_NAME, COLUMN_WHO, COLUMN_WORK_TIME,
            COLUMN_AGENT_ID, COLUMN_CITY, COLUMN_CITY_ID, COLUMN_DISPLAY_ADDRESS,
            COLUMN_MAIN_ADDRESS, COLUMN_PERSON_ID
        };
        int COLUMN_ID_INDEX = 0;
        int COLUMN_TYPE_INDEX = 1;
        int COLUMN_SERIAL_INDEX = 2;
        int COLUMN_NAME_INDEX = 3;
        int COLUMN_WHO_INDEX = 4;
        int COLUMN_WORK_TIME_INDEX = 5;
        int COLUMN_AGENT_ID_INDEX = 6;
        int COLUMN_CITY_INDEX = 7;
        int COLUMN_CITY_ID_INDEX = 8;
        int COLUMN_DISPLAY_ADDRESS_INDEX = 9;
        int COLUMN_MAIN_ADDRESS_INDEX = 10;
        int COLUMN_PERSON_ID_INDEX = 11;
    }

    private interface TerminalsStateTable {
        String NAME = "terminals_state";
        String COLUMN_TERMINAL_ID = "id";
        String COLUMN_AGENT_ID = "agent_id";
        String COLUMN_LAST_ACTIVITY = "last_activity";
        String COLUMN_LAST_PAYMENT = "last_payment";
        String COLUMN_STATUS = "machine_status";
        String COLUMN_NOTE_ERROR = "note_error";
        String COLUMN_PRINTER_ERROR = "printer_error";
        String COLUMN_CARD_READER_STATUS = "card_reader_status";
        String COLUMN_SIGNAL_LEVEL = "signal_level";
        String COLUMN_SIM_BALANCE = "sim_balance";
        String COLUMN_DOOR_ALARM = "door_alarm";
        String COLUMN_DOOR_OPEN = "door_open";
        String COLUMN_EVENT = "event";
        String COLUMN_EVENT_TEXT = "event_text";
        String[] ALL_COLUMNS = new String[] {
            COLUMN_TERMINAL_ID, COLUMN_AGENT_ID, COLUMN_LAST_ACTIVITY, COLUMN_LAST_PAYMENT,
            COLUMN_STATUS, COLUMN_NOTE_ERROR, COLUMN_PRINTER_ERROR, COLUMN_CARD_READER_STATUS,
            COLUMN_SIGNAL_LEVEL, COLUMN_SIM_BALANCE, COLUMN_DOOR_ALARM, COLUMN_DOOR_OPEN,
            COLUMN_EVENT, COLUMN_EVENT_TEXT
        };
        int COLUMN_TERMINAL_ID_INDEX = 0;
        int COLUMN_AGENT_ID_INDEX = 1;
        int COLUMN_LAST_ACTIVITY_INDEX = 2;
        int COLUMN_LAST_PAYMENT_INDEX = 3;
        int COLUMN_STATUS_INDEX = 4;
        int COLUMN_NOTE_ERROR_INDEX = 5;
        int COLUMN_PRINTER_ERROR_INDEX = 6;
        int COLUMN_CARD_READER_STATUS_INDEX = 7;
        int COLUMN_SIGNAL_LEVEL_INDEX = 8;
        int COLUMN_SIM_BALANCE_INDEX = 9;
        int COLUMN_DOOR_ALARM_INDEX = 10;
        int COLUMN_DOOR_OPEN_INDEX = 11;
        int COLUMN_EVENT_INDEX = 12;
        int COLUMN_EVENT_TEXT_INDEX = 13;
    }

    private interface TerminalsStatsTable {
        String NAME = "terminals_stat";
        String COLUMN_TERMINAL_ID = "terminal_id";
        String COLUMN_AGENT_ID = "agent_id";
        String COLUMN_SYSTEM_UPTIME = "system_up_time";
        String COLUMN_UPTIME = "up_time";
        String COLUMN_PAY_PER_HR = "pays_per_hour";
        String COLUMN_BILL_PER_PAY = "bills_per_pay";
        String COLUMN_CARD_READER_USED_HR = "card_reader_used_hours";
        String COLUMN_CARD_READER_USED_DAY = "card_reader_used_day";
        String COLUMN_TIME_TO_CACHIN_FULL = "time_to_cachin_full";
        String COLUMN_TIME_TO_CACHIN_SERVICE = "time_to_cachin_service";
        String COLUMN_TIME_TO_PRINTER_OUT = "time_to_printer_out";
        String COLUMN_TIME_TO_PRINTER_SERVICE = "time_to_printer_service";

        String[] ALL_COLUMNS = {
            COLUMN_TERMINAL_ID, COLUMN_AGENT_ID, COLUMN_SYSTEM_UPTIME, COLUMN_UPTIME,
            COLUMN_PAY_PER_HR, COLUMN_BILL_PER_PAY, COLUMN_CARD_READER_USED_HR,
            COLUMN_CARD_READER_USED_DAY, COLUMN_TIME_TO_CACHIN_FULL, COLUMN_TIME_TO_CACHIN_SERVICE,
            COLUMN_TIME_TO_PRINTER_OUT, COLUMN_TIME_TO_PRINTER_SERVICE
        };

        int COLUMN_TERMINAL_ID_INDEX = 0;
        int COLUMN_AGENT_ID_INDEX = 1;
        int COLUMN_SYSTEM_UPTIME_INDEX = 2;
        int COLUMN_UPTIME_INDEX = 3;
        int COLUMN_PAY_PER_HR_INDEX = 4;
        int COLUMN_BILL_PER_PAY_INDEX = 5;
        int COLUMN_CARD_READER_USED_HR_INDEX = 6;
        int COLUMN_CARD_READER_USED_DAY_INDEX = 7;
        int COLUMN_TIME_TO_CACHIN_FULL_INDEX = 8;
        int COLUMN_TIME_TO_CACHIN_SERVICE_INDEX = 9;
        int COLUMN_TIME_TO_PRINTER_OUT_INDEX = 10;
        int COLUMN_TIME_TO_PRINTER_SERVICE_INDEX = 11;
    }

    private interface TerminalsCashTable {
        String NAME = "terminals_cash";
        String COLUMN_TERMINAL_ID = "terminal_id";
        String COLUMN_AGENT_ID = "agent_id";
        String COLUMN_CASH = "cash";

        String[] ALL_COLUMS = {
            COLUMN_TERMINAL_ID, COLUMN_AGENT_ID, COLUMN_CASH
        };

        int COLUMN_TERMINAL_ID_INDEX = 0;
        int COLUMN_AGENT_ID_INDEX = 1;
        int COLUMN_CASH_INDEX = 2;
    }

    @NonNull
    private final DbHelper mHelper;
    @Nullable
    private volatile SQLiteDatabase mReadableDatabase;
    @Nullable
    private volatile SQLiteDatabase mWritableDatabase;

    /* package */ DataStorage(@NonNull Context context) {
        mHelper = new DbHelper(context);
    }

    @Override
    public void storePerson(@NonNull Person person) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PersonsTable.COLUMN_LOGIN, person.getLogin());
        values.put(PersonsTable.COLUMN_PASSWORD, person.getPasswordHash());
        values.put(PersonsTable.COLUMN_TERMINAL, person.getTerminal());
        values.put(PersonsTable.COLUMN_NAME, person.getName());
        values.put(PersonsTable.COLUMN_AGENT_ID, person.getAgentId());
        values.put(PersonsTable.COLUMN_ENABLED, person.isEnabled());
        values.put(PersonsTable.COLUMN_VERIFIED, person.isVerified());
        db.replace(PersonsTable.NAME, null, values);
    }

    @Nullable
    @Override
    public Person[] getPersons() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(PersonsTable.NAME, PersonsTable.ALL_COLUMNS, null, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            Person[] result = new Person[cursor.getCount()];
            int index = 0;
            while (cursor.moveToNext()) {
                result[index++] = new Person(cursor.getString(PersonsTable.COLUMN_LOGIN_INDEX),
                        cursor.getString(PersonsTable.COLUMN_PASSWORD_INDEX),
                        cursor.getString(PersonsTable.COLUMN_TERMINAL_INDEX),
                        cursor.getString(PersonsTable.COLUMN_AGENT_ID_INDEX),
                        cursor.getString(PersonsTable.COLUMN_NAME_INDEX),
                        cursor.getInt(PersonsTable.COLUMN_ENABLED_INDEX) == 1,
                        cursor.getInt(PersonsTable.COLUMN_VERIFIED_INDEX) == 1);
            }
            return result;
        } finally {
            cursor.close();
        }
    }

    @Override
    public void storeAgents(@NonNull Agent... agents) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Agent agent : agents) {
                if (agent == null || !agent.isValid()) {
                    continue;
                }
                values.clear();
                values.put(AgentsTable.COLUMN_AGENT_ID, agent.getId());
                values.put(AgentsTable.COLUMN_PARENT_ID, agent.getParentId());
                values.put(AgentsTable.COLUMN_INN, agent.getINN());
                values.put(AgentsTable.COLUMN_JUR_ADDRESS, agent.getJurAddress());
                values.put(AgentsTable.COLUMN_PHYS_ADDRESS, agent.getPhysAddress());
                values.put(AgentsTable.COLUMN_NAME, agent.getName());
                values.put(AgentsTable.COLUMN_CITY, agent.getCity());
                values.put(AgentsTable.COLUMN_FISCAL_MODE, agent.getFiscalMode());
                values.put(AgentsTable.COLUMN_KMM, agent.getKMM());
                values.put(AgentsTable.COLUMN_TAX_REGNUM, agent.getTaxRegnum());
                values.put(AgentsTable.COLUMN_PERSON_LOGIN, agent.getPersonLogin());
                values.put(AgentsTable.COLUMN_TERMINALS, agent.getTerminalsCount());
                values.put(AgentsTable.COLUMN_STATE, agent.getState().code);
                db.replace(AgentsTable.NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Nullable
    @Override
    public Agent[] getAgents() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(AgentsTable.NAME, AgentsTable.ALL_COLUMNS, null, null, null,null, null);
        if (cursor == null) {
            return null;
        }

        try {
            Agent[] result = new Agent[cursor.getCount()];
            int index = 0;
            while (cursor.moveToNext()) {
                result[index++] = new Agent(cursor.getString(AgentsTable.COLUMN_PERSON_LOGIN_INDEX),
                        cursor.getString(AgentsTable.COLUMN_AGENT_ID_INDEX),
                        cursor.getString(AgentsTable.COLUMN_PARENT_ID_INDEX),
                        cursor.getString(AgentsTable.COLUMN_INN_INDEX),
                        cursor.getString(AgentsTable.COLUMN_JUR_ADDRESS_INDEX),
                        cursor.getString(AgentsTable.COLUMN_PHYS_ADDRESS_INDEX),
                        cursor.getString(AgentsTable.COLUMN_NAME_INDEX),
                        cursor.getString(AgentsTable.COLUMN_CITY_INDEX),
                        cursor.getString(AgentsTable.COLUMN_FISCAL_MODE_INDEX),
                        cursor.getString(AgentsTable.COLUMN_KMM_INDEX),
                        cursor.getString(AgentsTable.COLUMN_TAX_REGNUM_INDEX),
                        cursor.getInt(AgentsTable.COLUMN_TERMINALS_INDEX),
                        Agent.State.fromCode(cursor.getInt(AgentsTable.COLUMN_STATE_INDEX)));
            }
            return result;
        } finally {
            cursor.close();
        }
    }

    @Override
    public void storeTerminals(@NonNull String personId, @NonNull Terminal... terminals) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Terminal terminal : terminals) {
                if (terminal == null) {
                    continue;
                }
                values.clear();
                values.put(TerminalsTable.COLUMN_ID, terminal.getId());
                values.put(TerminalsTable.COLUMN_TYPE, terminal.getType().id);
                values.put(TerminalsTable.COLUMN_SERIAL, terminal.getSerial());
                values.put(TerminalsTable.COLUMN_NAME, terminal.getDisplayName());
                values.put(TerminalsTable.COLUMN_WHO, terminal.getWhoAdded());
                values.put(TerminalsTable.COLUMN_WORK_TIME, terminal.getWorkTime());
                values.put(TerminalsTable.COLUMN_AGENT_ID, terminal.getAgentId());
                values.put(TerminalsTable.COLUMN_CITY, terminal.getCity());
                values.put(TerminalsTable.COLUMN_CITY_ID, terminal.getCityId());
                values.put(TerminalsTable.COLUMN_DISPLAY_ADDRESS, terminal.getDisplayAddress());
                values.put(TerminalsTable.COLUMN_MAIN_ADDRESS, terminal.getMainAddress());
                values.put(TerminalsTable.COLUMN_PERSON_ID, personId);
                db.replace(TerminalsTable.NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Nullable
    @Override
    public Terminal[] getTerminals() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TerminalsTable.NAME, TerminalsTable.ALL_COLUMNS, null, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            Terminal result[] = new Terminal[cursor.getCount()];
            int index = 0;
            while (cursor.moveToNext()) {
                result[index] = new Terminal(cursor.getString(TerminalsTable.COLUMN_ID_INDEX),
                        cursor.getString(TerminalsTable.COLUMN_AGENT_ID_INDEX),
                        TerminalType.fromId(cursor.getInt(TerminalsTable.COLUMN_TYPE_INDEX)),
                        cursor.getString(TerminalsTable.COLUMN_SERIAL_INDEX),
                        cursor.getString(TerminalsTable.COLUMN_NAME_INDEX),
                        cursor.getString(TerminalsTable.COLUMN_WHO_INDEX),
                        cursor.getString(TerminalsTable.COLUMN_WORK_TIME_INDEX));
                result[index].setAddress(cursor.getString(TerminalsTable.COLUMN_DISPLAY_ADDRESS_INDEX),
                        cursor.getString(TerminalsTable.COLUMN_MAIN_ADDRESS_INDEX));
                result[index].setCity(cursor.getInt(TerminalsTable.COLUMN_CITY_ID_INDEX),
                        cursor.getString(TerminalsTable.COLUMN_CITY_INDEX));
                result[index].setPersonId(cursor.getString(TerminalsTable.COLUMN_PERSON_ID_INDEX));
                ++index;
            }
            return result;
        } finally {
            cursor.close();
        }
    }

    @Override
    public void storeTerminalStates(@NonNull TerminalState... statuses) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (TerminalState status : statuses) {
                if (status == null) {
                    continue;
                }
                values.put(TerminalsStateTable.COLUMN_TERMINAL_ID, status.getId());
                values.put(TerminalsStateTable.COLUMN_AGENT_ID, status.getAgentId());
                values.put(TerminalsStateTable.COLUMN_LAST_ACTIVITY, status.getLastActivity());
                values.put(TerminalsStateTable.COLUMN_LAST_PAYMENT, status.getLastPayment());
                values.put(TerminalsStateTable.COLUMN_STATUS, status.getMachineStatus());
                values.put(TerminalsStateTable.COLUMN_NOTE_ERROR, status.getNoteError());
                values.put(TerminalsStateTable.COLUMN_PRINTER_ERROR, status.getPrinterError());
                values.put(TerminalsStateTable.COLUMN_CARD_READER_STATUS, status.getCardReaderStatus());
                values.put(TerminalsStateTable.COLUMN_SIGNAL_LEVEL, status.getSignalLevel());
                values.put(TerminalsStateTable.COLUMN_SIM_BALANCE, status.getSimBalance());
                values.put(TerminalsStateTable.COLUMN_DOOR_ALARM, status.getDoorAlarmCount());
                values.put(TerminalsStateTable.COLUMN_DOOR_OPEN, status.getDoorOpenCount());
                values.put(TerminalsStateTable.COLUMN_EVENT, status.getEvent());
                values.put(TerminalsStateTable.COLUMN_EVENT_TEXT, status.getEventText());
                db.replace(TerminalsStateTable.NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Nullable
    @Override
    public TerminalState[] getTerminalStates() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TerminalsStateTable.NAME, TerminalsStateTable.ALL_COLUMNS, null, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            TerminalState[] result = new TerminalState[cursor.getCount()];
            for (int index = 0; cursor.moveToNext(); ++index) {
                result[index] = new TerminalState(cursor.getString(TerminalsStateTable.COLUMN_TERMINAL_ID_INDEX),
                        cursor.getString(TerminalsStateTable.COLUMN_AGENT_ID_INDEX),
                        cursor.getLong(TerminalsStateTable.COLUMN_LAST_ACTIVITY_INDEX),
                        cursor.getLong(TerminalsStateTable.COLUMN_LAST_PAYMENT_INDEX),
                        cursor.getInt(TerminalsStateTable.COLUMN_STATUS_INDEX),
                        cursor.getString(TerminalsStateTable.COLUMN_NOTE_ERROR_INDEX),
                        cursor.getString(TerminalsStateTable.COLUMN_PRINTER_ERROR_INDEX),
                        cursor.getString(TerminalsStateTable.COLUMN_CARD_READER_STATUS_INDEX),
                        cursor.getString(TerminalsStateTable.COLUMN_SIGNAL_LEVEL_INDEX),
                        cursor.getFloat(TerminalsStateTable.COLUMN_SIM_BALANCE_INDEX),
                        cursor.getInt(TerminalsStateTable.COLUMN_DOOR_ALARM_INDEX),
                        cursor.getInt(TerminalsStateTable.COLUMN_DOOR_OPEN_INDEX),
                        cursor.getInt(TerminalsStateTable.COLUMN_EVENT_INDEX),
                        cursor.getString(TerminalsStateTable.COLUMN_EVENT_TEXT_INDEX));
            }
            return result;
        } finally {
            cursor.close();
        }
    }

    @Override
    public void storeTerminalStats(@NonNull TerminalStats... stats) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (TerminalStats stat : stats) {
                if (stat == null) {
                    continue;
                }
                values.put(TerminalsStatsTable.COLUMN_TERMINAL_ID, stat.getTerminalId());
                values.put(TerminalsStatsTable.COLUMN_AGENT_ID, stat.getAgentId());
                values.put(TerminalsStatsTable.COLUMN_SYSTEM_UPTIME, stat.getSystemUpTime());
                values.put(TerminalsStatsTable.COLUMN_UPTIME, stat.getUpTime());
                values.put(TerminalsStatsTable.COLUMN_PAY_PER_HR, stat.getPaysPerHour());
                values.put(TerminalsStatsTable.COLUMN_BILL_PER_PAY, stat.getBillsPerPay());
                values.put(TerminalsStatsTable.COLUMN_CARD_READER_USED_HR, stat.getCardReaderUsedHours());
                values.put(TerminalsStatsTable.COLUMN_CARD_READER_USED_DAY, stat.getCardReaderUsedDay());
                values.put(TerminalsStatsTable.COLUMN_TIME_TO_CACHIN_FULL, stat.getTimeToCashinFull());
                values.put(TerminalsStatsTable.COLUMN_TIME_TO_CACHIN_SERVICE, stat.getTimeToCashinService());
                values.put(TerminalsStatsTable.COLUMN_TIME_TO_PRINTER_OUT, stat.getTimeToPrinterPaperOut());
                values.put(TerminalsStatsTable.COLUMN_TIME_TO_PRINTER_SERVICE, stat.getTimeToPrinterService());
                db.replace(TerminalsStatsTable.NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Nullable
    @Override
    public TerminalStats[] getTerminalStats() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TerminalsStatsTable.NAME, TerminalsStatsTable.ALL_COLUMNS, null, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            TerminalStats result[] = new TerminalStats[cursor.getCount()];
            for (int index = 0; cursor.moveToNext(); ++index) {
                result[index] = new TerminalStats(cursor.getString(TerminalsStatsTable.COLUMN_TERMINAL_ID_INDEX),
                        cursor.getString(TerminalsStatsTable.COLUMN_AGENT_ID_INDEX),
                        cursor.getInt(TerminalsStatsTable.COLUMN_SYSTEM_UPTIME_INDEX),
                        cursor.getInt(TerminalsStatsTable.COLUMN_UPTIME_INDEX),
                        cursor.getFloat(TerminalsStatsTable.COLUMN_PAY_PER_HR_INDEX),
                        cursor.getFloat(TerminalsStatsTable.COLUMN_BILL_PER_PAY_INDEX),
                        cursor.getInt(TerminalsStatsTable.COLUMN_CARD_READER_USED_HR_INDEX),
                        cursor.getInt(TerminalsStatsTable.COLUMN_CARD_READER_USED_DAY_INDEX),
                        cursor.getLong(TerminalsStatsTable.COLUMN_TIME_TO_CACHIN_FULL_INDEX),
                        cursor.getLong(TerminalsStatsTable.COLUMN_TIME_TO_CACHIN_SERVICE_INDEX),
                        cursor.getLong(TerminalsStatsTable.COLUMN_TIME_TO_PRINTER_OUT_INDEX),
                        cursor.getLong(TerminalsStatsTable.COLUMN_TIME_TO_PRINTER_SERVICE_INDEX));
            }
            return result;
        } finally {
            cursor.close();
        }
    }

    @Override
    public void storeTerminalsCash(@NonNull TerminalCash... cashes) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (TerminalCash cash : cashes) {
                if (cash == null) {
                    continue;
                }

                try {
                    cash.store(new DataOutputStream(out));
                } catch (IOException e) {
                    LogHelper.error(TAG, "Can't store cash item", e);
                    continue;
                }
                values.put(TerminalsCashTable.COLUMN_TERMINAL_ID, cash.getTerminalId());
                values.put(TerminalsCashTable.COLUMN_AGENT_ID, cash.getAgentId());
                values.put(TerminalsCashTable.COLUMN_CASH, out.toByteArray());
                db.replace(TerminalsCashTable.NAME, null, values);
                out.reset();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Nullable
    @Override
    public TerminalCash[] getTerminalsCash() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TerminalsCashTable.NAME, TerminalsCashTable.ALL_COLUMS, null, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            TerminalCash[] result = new TerminalCash[cursor.getCount()];
            int index = 0;
            while (cursor.moveToNext()) {
                byte data[] = cursor.getBlob(TerminalsCashTable.COLUMN_CASH_INDEX);
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                try {
                    result[index++] = new TerminalCash(new DataInputStream(in));
                } catch (IOException e) {
                    LogHelper.error(TAG, "Can't read cash data", e);
                }
            }
            return result;
        } finally {
            cursor.close();
        }
    }

    public void shutdown() {
        synchronized (mHelper) {
            mReadableDatabase = null;
            mWritableDatabase = null;
        }
        mHelper.close();
    }

    @NonNull
    /* package */ SQLiteDatabase getReadableDatabase() {
        if (mReadableDatabase == null) {
            synchronized (mHelper) {
                if (mReadableDatabase == null) {
                    mReadableDatabase = mHelper.getReadableDatabase();
                }
            }
        }
        return mReadableDatabase;
    }

    @NonNull
    /* package */ SQLiteDatabase getWritableDatabase() {
        if (mWritableDatabase == null) {
            synchronized (mHelper) {
                if (mWritableDatabase == null) {
                    mWritableDatabase = mHelper.getWritableDatabase();
                }
            }
        }
        return mWritableDatabase;
    }

    /* package */ static class DbHelper extends SQLiteOpenHelper {

        /* package */ DbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + PersonsTable.NAME + "(" +
                            PersonsTable.COLUMN_LOGIN + " TEXT UNIQUE ON CONFLICT REPLACE, " +
                            PersonsTable.COLUMN_PASSWORD + " TEXT, " +
                            PersonsTable.COLUMN_TERMINAL + " TEXT, " +
                            PersonsTable.COLUMN_NAME + " TEXT," +
                            PersonsTable.COLUMN_AGENT_ID + " TEXT," +
                            PersonsTable.COLUMN_VERIFIED + " INTEGER, " +
                            PersonsTable.COLUMN_ENABLED + " INTEGER);"
            );
            db.execSQL("CREATE TABLE " + AgentsTable.NAME + "(" +
                            AgentsTable.COLUMN_AGENT_ID + " TEXT UNIQUE ON CONFLICT REPLACE, " +
                            AgentsTable.COLUMN_PARENT_ID + " TEXT, " +
                            AgentsTable.COLUMN_PERSON_LOGIN + " TEXT, " +
                            AgentsTable.COLUMN_INN + " TEXT, " +
                            AgentsTable.COLUMN_JUR_ADDRESS + " TEXT, " +
                            AgentsTable.COLUMN_PHYS_ADDRESS + " TEXT, " +
                            AgentsTable.COLUMN_NAME + " TEXT, " +
                            AgentsTable.COLUMN_CITY + " TEXT, " +
                            AgentsTable.COLUMN_FISCAL_MODE + " TEXT, " +
                            AgentsTable.COLUMN_KMM + " TEXT, " +
                            AgentsTable.COLUMN_TAX_REGNUM + " TEXT, " +
                            AgentsTable.COLUMN_TERMINALS + " INTEGER, " +
                            AgentsTable.COLUMN_STATE + " INTEGER" +
                            ");"
            );
            db.execSQL("CREATE TABLE " + TerminalsTable.NAME + "(" +
                            TerminalsTable.COLUMN_ID + " TEXT UNIQUE ON CONFLICT REPLACE, " +
                            TerminalsTable.COLUMN_TYPE + " INTEGER, " +
                            TerminalsTable.COLUMN_SERIAL + " TEXT, " +
                            TerminalsTable.COLUMN_NAME + " TEXT, " +
                            TerminalsTable.COLUMN_WHO + " TEXT, " +
                            TerminalsTable.COLUMN_WORK_TIME + " TEXT, " +
                            TerminalsTable.COLUMN_AGENT_ID + " TEXT, " +
                            TerminalsTable.COLUMN_CITY + " TEXT, " +
                            TerminalsTable.COLUMN_CITY_ID + " INTEGER, " +
                            TerminalsTable.COLUMN_DISPLAY_ADDRESS + " TEXT, " +
                            TerminalsTable.COLUMN_MAIN_ADDRESS + " TEXT, " +
                            TerminalsTable.COLUMN_PERSON_ID + " TEXT);"
            );

            db.execSQL("CREATE TABLE " + TerminalsStateTable.NAME + "(" +
                            TerminalsStateTable.COLUMN_TERMINAL_ID + " TEXT UNIQUE ON CONFLICT REPLACE, " +
                            TerminalsStateTable.COLUMN_AGENT_ID + " TEXT, " +
                            TerminalsStateTable.COLUMN_LAST_ACTIVITY + " INTEGER, " +
                            TerminalsStateTable.COLUMN_LAST_PAYMENT + " INTEGER, " +
                            TerminalsStateTable.COLUMN_STATUS + " INTEGER, " +
                            TerminalsStateTable.COLUMN_NOTE_ERROR + " TEXT, " +
                            TerminalsStateTable.COLUMN_PRINTER_ERROR + " TEXT, " +
                            TerminalsStateTable.COLUMN_CARD_READER_STATUS + " TEXT, " +
                            TerminalsStateTable.COLUMN_SIGNAL_LEVEL + " TEXT, " +
                            TerminalsStateTable.COLUMN_SIM_BALANCE + " REAL, " +
                            TerminalsStateTable.COLUMN_DOOR_ALARM + " INTEGER, " +
                            TerminalsStateTable.COLUMN_DOOR_OPEN + " INTEGER, " +
                            TerminalsStateTable.COLUMN_EVENT + " INTEGER, " +
                            TerminalsStateTable.COLUMN_EVENT_TEXT + " TEXT);"
            );

            db.execSQL("CREATE TABLE " + TerminalsStatsTable.NAME + "(" +
                            TerminalsStatsTable.COLUMN_TERMINAL_ID + " TEXT UNIQUE ON CONFLICT REPLACE, " +
                            TerminalsStatsTable.COLUMN_AGENT_ID + " TEXT, " +
                            TerminalsStatsTable.COLUMN_SYSTEM_UPTIME + " INTEGER, " +
                            TerminalsStatsTable.COLUMN_UPTIME + " INTEGER, " +
                            TerminalsStatsTable.COLUMN_PAY_PER_HR + " REAL, " +
                            TerminalsStatsTable.COLUMN_BILL_PER_PAY + " REAL, " +
                            TerminalsStatsTable.COLUMN_CARD_READER_USED_HR + " INTEGER, " +
                            TerminalsStatsTable.COLUMN_CARD_READER_USED_DAY + " INTEGER, " +
                            TerminalsStatsTable.COLUMN_TIME_TO_CACHIN_FULL + " INTEGER, " +
                            TerminalsStatsTable.COLUMN_TIME_TO_CACHIN_SERVICE + " INTEGER, " +
                            TerminalsStatsTable.COLUMN_TIME_TO_PRINTER_OUT + " INTEGER, " +
                            TerminalsStatsTable.COLUMN_TIME_TO_PRINTER_SERVICE + " INTEGER);"
            );

            db.execSQL("CREATE TABLE " + TerminalsCashTable.NAME + "(" +
                            TerminalsCashTable.COLUMN_TERMINAL_ID + " TEXT UNIQUE ON CONFLICT REPLACE, " +
                            TerminalsCashTable.COLUMN_AGENT_ID + " TEXT, " +
                            TerminalsCashTable.COLUMN_CASH + " BLOB);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
