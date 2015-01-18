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

    private static final String PERSONS_TABLE_NAME = "persons";
    private static final String PERSONS_COLUMN_LOGIN = "login";
    private static final String PERSONS_COLUMN_PASSWORD = "password";
    private static final String PERSONS_COLUMN_TERMINAL = "terminal";
    private static final String PERSONS_COLUMN_NAME = "name";
    private static final String PERSONS_COLUMN_AGENT_ID = "agent_id";
    private static final String PERSONS_COLUMN_ENABLED = "enabled";
    private static final String PERSONS_COLUMN_VERIFIED = "verified";
    private static final String[] TABLE_PERSONS_COLUMNS = new String[] {
        PERSONS_COLUMN_LOGIN, PERSONS_COLUMN_PASSWORD,
        PERSONS_COLUMN_TERMINAL, PERSONS_COLUMN_NAME,
        PERSONS_COLUMN_AGENT_ID, PERSONS_COLUMN_ENABLED,
        PERSONS_COLUMN_VERIFIED
    };
    private static final int PERSONS_COLUMN_LOGIN_INDEX = 0;
    private static final int PERSONS_COLUMN_PASSWORD_INDEX = 1;
    private static final int PERSONS_COLUMN_TERMINAL_INDEX = 2;
    private static final int PERSONS_COLUMN_NAME_INDEX = 3;
    private static final int PERSONS_COLUMN_AGENT_ID_INDEX = 4;
    private static final int PERSONS_COLUMN_ENABLED_INDEX = 5;
    private static final int PERSONS_COLUMN_VERIFIED_INDEX = 6;

    private static final String AGENTS_TABLE_NAME = "agents";
    private static final String AGENTS_COLUMN_AGENT_ID = "agent_id";
    private static final String AGENTS_COLUMN_PARENT_ID = "parent_id";
    private static final String AGENTS_COLUMN_PERSON_LOGIN = "person_login";
    private static final String AGENTS_COLUMN_INN = "inn";
    private static final String AGENTS_COLUMN_JUR_ADDRESS = "jur_address";
    private static final String AGENTS_COLUMN_PHYS_ADDRESS = "phys_address";
    private static final String AGENTS_COLUMN_NAME = "name";
    private static final String AGENTS_COLUMN_CITY = "city";
    private static final String AGENTS_COLUMN_FISCAL_MODE = "fiscal_mode";
    private static final String AGENTS_COLUMN_KMM = "kmm";
    private static final String AGENTS_COLUMN_TAX_REGNUM = "tax_regnum";

    private static final String TERMINALS_TABLE_NAME = "terminals";
    private static final String TERMINALS_COLUMN_ID = "id";
    private static final String TERMINALS_COLUMN_TYPE = "type";
    private static final String TERMINALS_COLUMN_SERIAL = "serial";
    private static final String TERMINALS_COLUMN_NAME = "display_name";
    private static final String TERMINALS_COLUMN_WHO = "who_added";
    private static final String TERMINALS_COLUMN_WORK_TIME = "work_time";
    private static final String TERMINALS_COLUMN_AGENT_ID = "agent_id";
    private static final String TERMINALS_COLUMN_CITY = "city";
    private static final String TERMINALS_COLUMN_CITY_ID = "city_id";
    private static final String TERMINALS_COLUMN_DISPLAY_ADDRESS = "display_address";
    private static final String TERMINALS_COLUMN_MAIN_ADDRESS = "main_address";
    private static final String TERMINALS_COLUMN_PERSON_ID = "person_id";
    private static final String[] TABLE_TERMINALS_COLUMNS = new String[] {
            TERMINALS_COLUMN_ID, TERMINALS_COLUMN_TYPE, TERMINALS_COLUMN_SERIAL,
            TERMINALS_COLUMN_NAME, TERMINALS_COLUMN_WHO, TERMINALS_COLUMN_WORK_TIME,
            TERMINALS_COLUMN_AGENT_ID, TERMINALS_COLUMN_CITY, TERMINALS_COLUMN_CITY_ID,
            TERMINALS_COLUMN_DISPLAY_ADDRESS, TERMINALS_COLUMN_MAIN_ADDRESS,
            TERMINALS_COLUMN_PERSON_ID
    };
    private static final int TERMINALS_COLUMN_ID_INDEX = 0;
    private static final int TERMINALS_COLUMN_TYPE_INDEX = 1;
    private static final int TERMINALS_COLUMN_SERIAL_INDEX = 2;
    private static final int TERMINALS_COLUMN_NAME_INDEX = 3;
    private static final int TERMINALS_COLUMN_WHO_INDEX = 4;
    private static final int TERMINALS_COLUMN_WORK_TIME_INDEX = 5;
    private static final int TERMINALS_COLUMN_AGENT_ID_INDEX = 6;
    private static final int TERMINALS_COLUMN_CITY_INDEX = 7;
    private static final int TERMINALS_COLUMN_CITY_ID_INDEX = 8;
    private static final int TERMINALS_COLUMN_DISPLAY_ADDRESS_INDEX = 9;
    private static final int TERMINALS_COLUMN_MAIN_ADDRESS_INDEX =10;
    private static final int TERMINALS_COLUMN_PERSON_ID_INDEX = 11;

    private static final int MSG_STORE_PERSON = 1;
    private static final int MSG_ADD_AGENTS = 2;
    private static final int MSG_GET_PERSONS = 3;
    private static final int MSG_STORE_TERMINALS = 4;
    private static final int MSG_GET_TERMINALS = 5;

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

    public void shutdown() {
        mThread.quit();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + PERSONS_TABLE_NAME + "(" +
                        PERSONS_COLUMN_LOGIN + " TEXT UNIQUE ON CONFLICT REPLACE, " +
                        PERSONS_COLUMN_PASSWORD + " TEXT, " +
                        PERSONS_COLUMN_TERMINAL + " TEXT, " +
                        PERSONS_COLUMN_NAME + " TEXT," +
                        PERSONS_COLUMN_AGENT_ID + " TEXT," +
                        PERSONS_COLUMN_VERIFIED + " INTEGER, " +
                        PERSONS_COLUMN_ENABLED + " INTEGER);"
        );
        db.execSQL("CREATE TABLE " + AGENTS_TABLE_NAME + "(" +
                        AGENTS_COLUMN_AGENT_ID + " TEXT UNIQUE ON CONFLICT REPLACE, " +
                        AGENTS_COLUMN_PARENT_ID + " TEXT, " +
                        AGENTS_COLUMN_PERSON_LOGIN + " TEXT, " +
                        AGENTS_COLUMN_INN + " TEXT, " +
                        AGENTS_COLUMN_JUR_ADDRESS + " TEXT, " +
                        AGENTS_COLUMN_PHYS_ADDRESS + " TEXT, " +
                        AGENTS_COLUMN_NAME + " TEXT, " +
                        AGENTS_COLUMN_CITY + " TEXT, " +
                        AGENTS_COLUMN_FISCAL_MODE + " TEXT, " +
                        AGENTS_COLUMN_KMM + " TEXT, " +
                        AGENTS_COLUMN_TAX_REGNUM + " TEXT);"
        );
        db.execSQL("CREATE TABLE " + TERMINALS_TABLE_NAME + "(" +
                        TERMINALS_COLUMN_ID + " TEXT UNIQUE ON CONFLICT REPLACE, " +
                        TERMINALS_COLUMN_TYPE + " INTEGER, " +
                        TERMINALS_COLUMN_SERIAL + " TEXT, " +
                        TERMINALS_COLUMN_NAME + " TEXT, " +
                        TERMINALS_COLUMN_WHO + " TEXT, " +
                        TERMINALS_COLUMN_WORK_TIME + " TEXT, " +
                        TERMINALS_COLUMN_AGENT_ID + " TEXT, " +
                        TERMINALS_COLUMN_CITY + " TEXT, " +
                        TERMINALS_COLUMN_CITY_ID + " INTEGER, " +
                        TERMINALS_COLUMN_DISPLAY_ADDRESS + " TEXT, " +
                        TERMINALS_COLUMN_MAIN_ADDRESS + " TEXT, " +
                        TERMINALS_COLUMN_PERSON_ID + " TEXT);"
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
            values.put(PERSONS_COLUMN_LOGIN, person.getLogin());
            values.put(PERSONS_COLUMN_PASSWORD, person.getPasswordHash());
            values.put(PERSONS_COLUMN_TERMINAL, person.getTerminal());
            values.put(PERSONS_COLUMN_NAME, person.getName());
            values.put(PERSONS_COLUMN_AGENT_ID, person.getAgentId());
            values.put(PERSONS_COLUMN_ENABLED, person.isEnabled());
            values.put(PERSONS_COLUMN_VERIFIED, person.isVerified());
            db.replace(PERSONS_TABLE_NAME, null, values);
        } finally {
            db.close();
        }
    }

    @Nullable
    /* package */ Person[] getPersonsImpl() {
        SQLiteDatabase db = getReadableDatabase();
        //noinspection TryFinallyCanBeTryWithResources
        try {
            Cursor cursor = db.query(PERSONS_TABLE_NAME, TABLE_PERSONS_COLUMNS, null, null, null, null, null);
            if (cursor == null) {
                return null;
            }
            try {
                Person[] result = new Person[cursor.getCount()];
                int index = 0;
                while (cursor.moveToNext()) {
                    result[index++] = new Person(cursor.getString(PERSONS_COLUMN_LOGIN_INDEX),
                            cursor.getString(PERSONS_COLUMN_PASSWORD_INDEX),
                            cursor.getString(PERSONS_COLUMN_TERMINAL_INDEX),
                            cursor.getString(PERSONS_COLUMN_AGENT_ID_INDEX),
                            cursor.getString(PERSONS_COLUMN_NAME_INDEX),
                            cursor.getInt(PERSONS_COLUMN_ENABLED_INDEX) == 1,
                            cursor.getInt(PERSONS_COLUMN_VERIFIED_INDEX) == 1);
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
                values.put(AGENTS_COLUMN_AGENT_ID, agent.getId());
                values.put(AGENTS_COLUMN_PARENT_ID, agent.getParentId());
                values.put(AGENTS_COLUMN_INN, agent.getINN());
                values.put(AGENTS_COLUMN_JUR_ADDRESS, agent.getJurAddress());
                values.put(AGENTS_COLUMN_PHYS_ADDRESS, agent.getPhysAddress());
                values.put(AGENTS_COLUMN_NAME, agent.getName());
                values.put(AGENTS_COLUMN_CITY, agent.getCity());
                values.put(AGENTS_COLUMN_FISCAL_MODE, agent.getFiscalMode());
                values.put(AGENTS_COLUMN_KMM, agent.getKMM());
                values.put(AGENTS_COLUMN_TAX_REGNUM, agent.getTaxRegnum());
                values.put(AGENTS_COLUMN_PERSON_LOGIN, agent.getPersonLogin());
                db.replace(AGENTS_TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
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
                values.put(TERMINALS_COLUMN_ID, terminal.getId());
                values.put(TERMINALS_COLUMN_TYPE, terminal.getType().id);
                values.put(TERMINALS_COLUMN_SERIAL, terminal.getSerial());
                values.put(TERMINALS_COLUMN_NAME, terminal.getDisplayName());
                values.put(TERMINALS_COLUMN_WHO, terminal.getWhoAdded());
                values.put(TERMINALS_COLUMN_WORK_TIME, terminal.getWorkTime());
                values.put(TERMINALS_COLUMN_AGENT_ID, terminal.getAgentId());
                values.put(TERMINALS_COLUMN_CITY, terminal.getCity());
                values.put(TERMINALS_COLUMN_CITY_ID, terminal.getCityId());
                values.put(TERMINALS_COLUMN_DISPLAY_ADDRESS, terminal.getDisplayAddress());
                values.put(TERMINALS_COLUMN_MAIN_ADDRESS, terminal.getMainAddress());
                values.put(TERMINALS_COLUMN_PERSON_ID, personId);
                db.replace(TERMINALS_TABLE_NAME, null, values);
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
            Cursor cursor = db.query(TERMINALS_TABLE_NAME, TABLE_TERMINALS_COLUMNS, null, null, null, null, null);
            if (cursor == null) {
                return null;
            }
            try {
                Terminal result[] = new Terminal[cursor.getCount()];
                int index = 0;
                while (cursor.moveToNext()) {
                    result[index] = new Terminal(cursor.getString(TERMINALS_COLUMN_ID_INDEX),
                            cursor.getString(TERMINALS_COLUMN_AGENT_ID_INDEX),
                            TerminalType.fromId(cursor.getInt(TERMINALS_COLUMN_TYPE_INDEX)),
                            cursor.getString(TERMINALS_COLUMN_SERIAL_INDEX),
                            cursor.getString(TERMINALS_COLUMN_NAME_INDEX),
                            cursor.getString(TERMINALS_COLUMN_WHO_INDEX),
                            cursor.getString(TERMINALS_COLUMN_WORK_TIME_INDEX));
                    result[index].setAddress(cursor.getString(TERMINALS_COLUMN_DISPLAY_ADDRESS_INDEX),
                            cursor.getString(TERMINALS_COLUMN_MAIN_ADDRESS_INDEX));
                    result[index].setCity(cursor.getInt(TERMINALS_COLUMN_CITY_ID_INDEX),
                            cursor.getString(TERMINALS_COLUMN_CITY_INDEX));
                    result[index].setPersonId(cursor.getString(TERMINALS_COLUMN_PERSON_ID_INDEX));
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
