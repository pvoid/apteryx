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
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import org.pvoid.apteryx.data.agents.Agent;
import org.pvoid.apteryx.data.persons.Person;
import org.pvoid.apteryx.data.terminals.Terminal;
import org.pvoid.apteryx.data.terminals.TerminalState;
import org.pvoid.apteryx.data.terminals.TerminalStats;
import org.pvoid.apteryx.data.terminals.TerminalType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* package */ class DataStorage extends SQLiteOpenHelper implements Storage {

    private static final String TAG = "Storage";

    /* package */ static final String DB_NAME = "apteryx";
    /* package */ static final int DB_VERSION = 1;

    private static interface PersonsTable {
        static final String NAME = "persons";
        static final String COLUMN_LOGIN = "login";
        static final String COLUMN_PASSWORD = "password";
        static final String COLUMN_TERMINAL = "terminal";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_AGENT_ID = "agent_id";
        static final String COLUMN_ENABLED = "enabled";
        static final String COLUMN_VERIFIED = "verified";
        static final String[] ALL_COLUMNS = new String[] {
            COLUMN_LOGIN, COLUMN_PASSWORD, COLUMN_TERMINAL, COLUMN_NAME,
            COLUMN_AGENT_ID, COLUMN_ENABLED, COLUMN_VERIFIED
        };
        static final int COLUMN_LOGIN_INDEX = 0;
        static final int COLUMN_PASSWORD_INDEX = 1;
        static final int COLUMN_TERMINAL_INDEX = 2;
        static final int COLUMN_NAME_INDEX = 3;
        static final int COLUMN_AGENT_ID_INDEX = 4;
        static final int COLUMN_ENABLED_INDEX = 5;
        static final int COLUMN_VERIFIED_INDEX = 6;
    }

    private static interface AgentsTable {
        static final String NAME = "agents";
        static final String COLUMN_AGENT_ID = "agent_id";
        static final String COLUMN_PARENT_ID = "parent_id";
        static final String COLUMN_PERSON_LOGIN = "person_login";
        static final String COLUMN_INN = "inn";
        static final String COLUMN_JUR_ADDRESS = "jur_address";
        static final String COLUMN_PHYS_ADDRESS = "phys_address";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_CITY = "city";
        static final String COLUMN_FISCAL_MODE = "fiscal_mode";
        static final String COLUMN_KMM = "kmm";
        static final String COLUMN_TAX_REGNUM = "tax_regnum";
        static final String[] ALL_COLUMNS = new String[]{
            COLUMN_AGENT_ID, COLUMN_PARENT_ID, COLUMN_PERSON_LOGIN, COLUMN_INN,
            COLUMN_JUR_ADDRESS, COLUMN_PHYS_ADDRESS, COLUMN_NAME, COLUMN_CITY,
            COLUMN_FISCAL_MODE, COLUMN_KMM, COLUMN_TAX_REGNUM
        };
        static final int COLUMN_AGENT_ID_INDEX = 0;
        static final int COLUMN_PARENT_ID_INDEX = 1;
        static final int COLUMN_PERSON_LOGIN_INDEX = 2;
        static final int COLUMN_INN_INDEX = 3;
        static final int COLUMN_JUR_ADDRESS_INDEX = 4;
        static final int COLUMN_PHYS_ADDRESS_INDEX = 5;
        static final int COLUMN_NAME_INDEX = 6;
        static final int COLUMN_CITY_INDEX = 7;
        static final int COLUMN_FISCAL_MODE_INDEX = 8;
        static final int COLUMN_KMM_INDEX = 9;
        static final int COLUMN_TAX_REGNUM_INDEX = 10;
    }

    private static interface TerminalsTable {
        static final String NAME = "terminals";
        static final String COLUMN_ID = "id";
        static final String COLUMN_TYPE = "type";
        static final String COLUMN_SERIAL = "serial";
        static final String COLUMN_NAME = "display_name";
        static final String COLUMN_WHO = "who_added";
        static final String COLUMN_WORK_TIME = "work_time";
        static final String COLUMN_AGENT_ID = "agent_id";
        static final String COLUMN_CITY = "city";
        static final String COLUMN_CITY_ID = "city_id";
        static final String COLUMN_DISPLAY_ADDRESS = "display_address";
        static final String COLUMN_MAIN_ADDRESS = "main_address";
        static final String COLUMN_PERSON_ID = "person_id";
        static final String[] ALL_COLUMNS = new String[]{
            COLUMN_ID, COLUMN_TYPE, COLUMN_SERIAL, COLUMN_NAME, COLUMN_WHO, COLUMN_WORK_TIME,
            COLUMN_AGENT_ID, COLUMN_CITY, COLUMN_CITY_ID, COLUMN_DISPLAY_ADDRESS,
            COLUMN_MAIN_ADDRESS, COLUMN_PERSON_ID
        };
        static final int COLUMN_ID_INDEX = 0;
        static final int COLUMN_TYPE_INDEX = 1;
        static final int COLUMN_SERIAL_INDEX = 2;
        static final int COLUMN_NAME_INDEX = 3;
        static final int COLUMN_WHO_INDEX = 4;
        static final int COLUMN_WORK_TIME_INDEX = 5;
        static final int COLUMN_AGENT_ID_INDEX = 6;
        static final int COLUMN_CITY_INDEX = 7;
        static final int COLUMN_CITY_ID_INDEX = 8;
        static final int COLUMN_DISPLAY_ADDRESS_INDEX = 9;
        static final int COLUMN_MAIN_ADDRESS_INDEX = 10;
        static final int COLUMN_PERSON_ID_INDEX = 11;
    }

    private static interface TerminalsStateTable {
        static final String NAME = "terminals_state";
        static final String COLUMN_TERMINAL_ID = "id";
        static final String COLUMN_AGENT_ID = "agent_id";
        static final String COLUMN_LAST_ACTIVITY = "last_activity";
        static final String COLUMN_LAST_PAYMENT = "last_payment";
        static final String COLUMN_STATUS = "machine_status";
        static final String COLUMN_NOTE_ERROR = "note_error";
        static final String COLUMN_PRINTER_ERROR = "printer_error";
        static final String COLUMN_CARD_READER_STATUS = "card_reader_status";
        static final String COLUMN_SIGNAL_LEVEL = "signal_level";
        static final String COLUMN_SIM_BALANCE = "sim_balance";
        static final String COLUMN_DOOR_ALARM = "door_alarm";
        static final String COLUMN_DOOR_OPEN = "door_open";
        static final String COLUMN_EVENT = "event";
        static final String COLUMN_EVENT_TEXT = "event_text";
        static final String[] ALL_COLUMNS = new String[] {
            COLUMN_TERMINAL_ID, COLUMN_AGENT_ID, COLUMN_LAST_ACTIVITY, COLUMN_LAST_PAYMENT,
            COLUMN_STATUS, COLUMN_NOTE_ERROR, COLUMN_PRINTER_ERROR, COLUMN_CARD_READER_STATUS,
            COLUMN_SIGNAL_LEVEL, COLUMN_SIM_BALANCE, COLUMN_DOOR_ALARM, COLUMN_DOOR_OPEN,
            COLUMN_EVENT, COLUMN_EVENT_TEXT
        };
        static final int COLUMN_TERMINAL_ID_INDEX = 0;
        static final int COLUMN_AGENT_ID_INDEX = 1;
        static final int COLUMN_LAST_ACTIVITY_INDEX = 2;
        static final int COLUMN_LAST_PAYMENT_INDEX = 3;
        static final int COLUMN_STATUS_INDEX = 4;
        static final int COLUMN_NOTE_ERROR_INDEX = 5;
        static final int COLUMN_PRINTER_ERROR_INDEX = 6;
        static final int COLUMN_CARD_READER_STATUS_INDEX = 7;
        static final int COLUMN_SIGNAL_LEVEL_INDEX = 8;
        static final int COLUMN_SIM_BALANCE_INDEX = 9;
        static final int COLUMN_DOOR_ALARM_INDEX = 10;
        static final int COLUMN_DOOR_OPEN_INDEX = 11;
        static final int COLUMN_EVENT_INDEX = 12;
        static final int COLUMN_EVENT_TEXT_INDEX = 13;
    }

    private static interface TerminalsStatsTable {
        static final String NAME = "terminals_stat";
        static final String COLUMN_TERMINAL_ID = "terminal_id";
        static final String COLUMN_AGENT_ID = "agent_id";
        static final String COLUMN_SYSTEM_UPTIME = "system_up_time";
        static final String COLUMN_UPTIME = "up_time";
        static final String COLUMN_PAY_PER_HR = "pays_per_hour";
        static final String COLUMN_BILL_PER_PAY = "bills_per_pay";
        static final String COLUMN_CARD_READER_USED_HR = "card_reader_used_hours";
        static final String COLUMN_CARD_READER_USED_DAY = "card_reader_used_day";
        static final String COLUMN_TIME_TO_CACHIN_FULL = "time_to_cachin_full";
        static final String COLUMN_TIME_TO_CACHIN_SERVICE = "time_to_cachin_service";
        static final String COLUMN_TIME_TO_PRINTER_OUT = "time_to_printer_out";
        static final String COLUMN_TIME_TO_PRINTER_SERVICE = "time_to_printer_service";

        static final String[] ALL_COLUMNS = {
            COLUMN_TERMINAL_ID, COLUMN_AGENT_ID, COLUMN_SYSTEM_UPTIME, COLUMN_UPTIME,
            COLUMN_PAY_PER_HR, COLUMN_BILL_PER_PAY, COLUMN_CARD_READER_USED_HR,
            COLUMN_CARD_READER_USED_DAY, COLUMN_TIME_TO_CACHIN_FULL, COLUMN_TIME_TO_CACHIN_SERVICE,
            COLUMN_TIME_TO_PRINTER_OUT, COLUMN_TIME_TO_PRINTER_SERVICE
        };

        static final int COLUMN_TERMINAL_ID_INDEX = 0;
        static final int COLUMN_AGENT_ID_INDEX = 1;
        static final int COLUMN_SYSTEM_UPTIME_INDEX = 2;
        static final int COLUMN_UPTIME_INDEX = 3;
        static final int COLUMN_PAY_PER_HR_INDEX = 4;
        static final int COLUMN_BILL_PER_PAY_INDEX = 5;
        static final int COLUMN_CARD_READER_USED_HR_INDEX = 6;
        static final int COLUMN_CARD_READER_USED_DAY_INDEX = 7;
        static final int COLUMN_TIME_TO_CACHIN_FULL_INDEX = 8;
        static final int COLUMN_TIME_TO_CACHIN_SERVICE_INDEX = 9;
        static final int COLUMN_TIME_TO_PRINTER_OUT_INDEX = 10;
        static final int COLUMN_TIME_TO_PRINTER_SERVICE_INDEX = 11;
    }

    private static final int MSG_STORE_PERSON = 1;
    private static final int MSG_ADD_AGENTS = 2;
    private static final int MSG_GET_PERSONS = 3;
    private static final int MSG_STORE_TERMINALS = 4;
    private static final int MSG_GET_TERMINALS = 5;
    private static final int MSG_GET_AGENTS = 6;
    private static final int MSG_STORE_TERMINAL_STATUS = 7;
    private static final int MSG_GET_TERMINAL_STATUS = 8;
    private static final int MSG_STORE_TERMINAL_STATS = 9;
    private static final int MSG_GET_TERMINAL_STATS = 10;

    private final HandlerThread mThread;
    private final Handler mHandler;

    /* package */ DataStorage(@NonNull Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mThread = new HandlerThread("DbWorker");
        mThread.start();
        mHandler = new DbHandler(mThread.getLooper());
    }

    @Override
    public void storePerson(@NonNull Person person) {
        Message msg = mHandler.obtainMessage(MSG_STORE_PERSON, person);
        mHandler.sendMessage(msg);
    }

    @Nullable
    @Override
    public Person[] getPersons() throws ExecutionException, InterruptedException {
        ResultFuture<Person> future = new ResultFuture<>();
        Message msg = mHandler.obtainMessage(MSG_GET_PERSONS, future);
        mHandler.sendMessage(msg);
        return future.get();
    }

    @Override
    public void storeAgents(@NonNull Agent... agents) {
        Message msg = mHandler.obtainMessage(MSG_ADD_AGENTS, agents);
        mHandler.sendMessage(msg);
    }

    @Nullable
    @Override
    public Agent[] getAgents() throws ExecutionException, InterruptedException {
        ResultFuture<Agent> future = new ResultFuture<>();
        Message msg = mHandler.obtainMessage(MSG_GET_AGENTS, future);
        mHandler.sendMessage(msg);
        return future.get();
    }

    @Override
    public void storeTerminals(@NonNull String personId, @NonNull Terminal... terminals) {
        Message msg = mHandler.obtainMessage(MSG_STORE_TERMINALS, new Pair<>(personId, terminals));
        mHandler.sendMessage(msg);
    }

    @Nullable
    @Override
    public Terminal[] getTerminals() throws ExecutionException, InterruptedException {
        ResultFuture<Terminal> future = new ResultFuture<>();
        Message msg = mHandler.obtainMessage(MSG_GET_TERMINALS, future);
        mHandler.sendMessage(msg);
        return future.get();
    }

    @Override
    public void storeTerminalStatuses(@NonNull TerminalState[] statuses) {
        Message msg = mHandler.obtainMessage(MSG_STORE_TERMINAL_STATUS, statuses);
        mHandler.sendMessage(msg);
    }

    @Override
    public void storeTerminalStatistics(@NonNull TerminalStats[] statistics) {
        Message msg = mHandler.obtainMessage(MSG_STORE_TERMINAL_STATS, statistics);
        mHandler.sendMessage(msg);
    }

    public void shutdown() {
        mThread.quit();
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
                        AgentsTable.COLUMN_TAX_REGNUM + " TEXT);"
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /* package */ void storePersonImpl(@NonNull Person person) {
        SQLiteDatabase db = getWritableDatabase();
        //noinspection TryFinallyCanBeTryWithResources
        try {
            ContentValues values = new ContentValues();
            values.put(PersonsTable.COLUMN_LOGIN, person.getLogin());
            values.put(PersonsTable.COLUMN_PASSWORD, person.getPasswordHash());
            values.put(PersonsTable.COLUMN_TERMINAL, person.getTerminal());
            values.put(PersonsTable.COLUMN_NAME, person.getName());
            values.put(PersonsTable.COLUMN_AGENT_ID, person.getAgentId());
            values.put(PersonsTable.COLUMN_ENABLED, person.isEnabled());
            values.put(PersonsTable.COLUMN_VERIFIED, person.isVerified());
            db.replace(PersonsTable.NAME, null, values);
        } finally {
            db.close();
        }
    }

    @Nullable
    /* package */ Person[] getPersonsImpl() {
        SQLiteDatabase db = getReadableDatabase();
        //noinspection TryFinallyCanBeTryWithResources
        try {
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
        } finally {
            db.close();
        }
    }

    /* package */ void addAgentsImpl(@NonNull Agent[] agents) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        //noinspection TryFinallyCanBeTryWithResources
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
                db.replace(AgentsTable.NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    @Nullable
    /* package */ Agent[] getAgentsImpl() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        //noinspection TryFinallyCanBeTryWithResources
        try {
            cursor = db.query(AgentsTable.NAME, AgentsTable.ALL_COLUMNS, null, null, null,null, null);
            if (cursor == null) {
                return null;
            }
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
                        cursor.getString(AgentsTable.COLUMN_TAX_REGNUM_INDEX));
            }
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    /* package */ void addTerminalsImpl(@NonNull String personId, @NonNull Terminal[] terminals) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        //noinspection TryFinallyCanBeTryWithResources
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
            db.close();
        }
    }

    @Nullable
    /* package */ Terminal[] getTerminalsImpl() {
        SQLiteDatabase db = getReadableDatabase();
        //noinspection TryFinallyCanBeTryWithResources
        try {
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
        } finally {
            db.close();
        }
    }

    /* package */ void storeTerminalStatusesImpl(TerminalState[] statuses) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        //noinspection TryFinallyCanBeTryWithResources
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
            db.close();
        }
    }

    @Nullable
    /* package */ TerminalState[] getTerminalStatusesImpl() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        //noinspection TryFinallyCanBeTryWithResources
        try {
            cursor = db.query(TerminalsStateTable.NAME, TerminalsStateTable.ALL_COLUMNS, null, null, null, null, null);
            if (cursor == null) {
                return null;
            }
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
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    /* package */ void storeTerminalStatisticsImpl(TerminalStats[] stats) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        //noinspection TryFinallyCanBeTryWithResources
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
            db.close();
        }
    }

    @Nullable
    /* package */ TerminalStats[] getTerminalStatisticsImpl() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TerminalsStatsTable.NAME, TerminalsStatsTable.ALL_COLUMNS, null, null, null, null, null);
            if (cursor == null) {
                return null;
            }
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
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    private class DbHandler extends Handler {
        public DbHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_STORE_PERSON:
                    if (msg.obj != null) {
                        storePersonImpl((Person) msg.obj);
                    }
                    break;
                case MSG_ADD_AGENTS:
                    if (msg.obj != null) {
                        addAgentsImpl((Agent[]) msg.obj);
                    }
                    break;
                case MSG_GET_PERSONS:
                    if (msg.obj != null) {
                        //noinspection unchecked
                        ((ResultFuture<Person>) msg.obj).setResult(getPersonsImpl());
                    }
                    break;
                case MSG_STORE_TERMINALS:
                    if (msg.obj != null) {
                        //noinspection unchecked
                        Pair<String, Terminal[]> data = (Pair<String, Terminal[]>) msg.obj;
                        addTerminalsImpl(data.first, data.second);
                    }
                    break;
                case MSG_GET_TERMINALS:
                    if (msg.obj != null) {
                        //noinspection unchecked
                        ((ResultFuture<Terminal>) msg.obj).setResult(getTerminalsImpl());
                    }
                    break;
                case MSG_GET_AGENTS:
                    if (msg.obj != null) {
                        //noinspection unchecked
                        ((ResultFuture<Agent>) msg.obj).setResult(getAgentsImpl());
                    }
                    break;
                case MSG_STORE_TERMINAL_STATUS:
                    if (msg.obj != null) {
                        storeTerminalStatusesImpl((TerminalState[]) msg.obj);
                    }
                    break;
                case MSG_STORE_TERMINAL_STATS:
                    if (msg.obj != null) {
                        storeTerminalStatisticsImpl((TerminalStats[]) msg.obj);
                    }
                    break;
            }
        }
    }

    private static class ResultFuture<T> implements Future<T[]> {

        private final CountDownLatch mLatch = new CountDownLatch(1);
        @Nullable private T[] mResult;

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        public void setResult(@Nullable T[] result) {
            mResult = result;
            mLatch.countDown();
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        @Nullable
        public T[] get() throws InterruptedException, ExecutionException {
            mLatch.await();
            return mResult;
        }

        @Override
        @Nullable
        public T[] get(long timeout, @NonNull TimeUnit unit) throws InterruptedException,
                ExecutionException, TimeoutException {
            if (mLatch.await(timeout, unit)) {
                return mResult;
            }
            throw new TimeoutException();
        }
    }
}
