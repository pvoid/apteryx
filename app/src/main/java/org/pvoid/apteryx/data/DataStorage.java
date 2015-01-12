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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* package */ class DataStorage extends SQLiteOpenHelper implements Storage {

    private static final String TAG = "Storage";

    /* package */ static final String DB_NAME = "apteryx";
    /* package */ static final int DB_VERSION = 1;

    private static final String TABLE_PERSONS_NAME = "persons";
    private static final String TABLE_PERSONS_COLUMN_LOGIN = "login";
    private static final String TABLE_PERSONS_COLUMN_PASSWORD = "password";
    private static final String TABLE_PERSONS_COLUMN_TERMINAL = "terminal";
    private static final String TABLE_PERSONS_COLUMN_NAME = "name";
    private static final String TABLE_PERSONS_COLUMN_AGENT_ID = "agent_id";
    private static final String TABLE_PERSONS_COLUMN_ENABLED = "enabled";
    private static final String TABLE_PERSONS_COLUMN_VERIFIED = "verified";
    private static final String[] TABLE_PERSONS_COLUMNS = new String[] {
        TABLE_PERSONS_COLUMN_LOGIN, TABLE_PERSONS_COLUMN_PASSWORD,
        TABLE_PERSONS_COLUMN_TERMINAL, TABLE_PERSONS_COLUMN_NAME,
        TABLE_PERSONS_COLUMN_AGENT_ID, TABLE_PERSONS_COLUMN_ENABLED,
        TABLE_PERSONS_COLUMN_VERIFIED
    };

    private static final String TABLE_AGENTS_NAME = "agents";
    private static final String TABLE_AGENTS_COLUMN_AGENT_ID = "agent_id";
    private static final String TABLE_AGENTS_COLUMN_PARENT_ID = "parent_id";
    private static final String TABLE_AGENTS_COLUMN_PERSON_LOGIN = "person_login";
    private static final String TABLE_AGENTS_COLUMN_INN = "inn";
    private static final String TABLE_AGENTS_COLUMN_JUR_ADDRESS = "jur_address";
    private static final String TABLE_AGENTS_COLUMN_PHYS_ADDRESS = "phys_address";
    private static final String TABLE_AGENTS_COLUMN_NAME = "name";
    private static final String TABLE_AGENTS_COLUMN_CITY = "city";
    private static final String TABLE_AGENTS_COLUMN_FISCAL_MODE = "fiscal_mode";
    private static final String TABLE_AGENTS_COLUMN_KMM = "kmm";
    private static final String TABLE_AGENTS_COLUMN_TAX_REGNUM = "tax_regnum";

    private static final String TABLE_TERMINALS_NAME = "terminals";
    private static final String TABLE_TERMINALS_COLUMN_ID = "id";
    private static final String TABLE_TERMINALS_COLUMN_TYPE = "type";
    private static final String TABLE_TERMINALS_COLUMN_SERIAL = "serial";
    private static final String TABLE_TERMINALS_COLUMN_NAME = "display_name";
    private static final String TABLE_TERMINALS_COLUMN_WHO = "who_added";
    private static final String TABLE_TERMINALS_COLUMN_WORK_TIME = "work_time";
    private static final String TABLE_TERMINALS_COLUMN_AGENT_ID = "agent_id";
    private static final String TABLE_TERMINALS_COLUMN_CITY = "city";
    private static final String TABLE_TERMINALS_COLUMN_CITY_ID = "city_id";
    private static final String TABLE_TERMINALS_COLUMN_DISPLAY_ADDRESS = "display_address";
    private static final String TABLE_TERMINALS_COLUMN_MAIN_ADDRESS = "main_address";
    private static final String TABLE_TERMINALS_COLUMN_PERSON_ID = "person_id";


    private static final int MSG_STORE_PERSON = 1;
    private static final int MSG_ADD_AGENTS = 2;
    private static final int MSG_GET_PERSONS = 3;
    private static final int MSG_STORE_TERMINALS = 4;

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
        PersonsFuture future = new PersonsFuture();
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

    public void shutdown() {
        mThread.quit();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PERSONS_NAME + "(" +
                        TABLE_PERSONS_COLUMN_LOGIN + " TEXT UNIQUE ON CONFLICT REPLACE, " +
                        TABLE_PERSONS_COLUMN_PASSWORD + " TEXT, " +
                        TABLE_PERSONS_COLUMN_TERMINAL + " TEXT, " +
                        TABLE_PERSONS_COLUMN_NAME + " TEXT," +
                        TABLE_PERSONS_COLUMN_AGENT_ID + " TEXT," +
                        TABLE_PERSONS_COLUMN_VERIFIED + " INTEGER, " +
                        TABLE_PERSONS_COLUMN_ENABLED + " INTEGER);"
        );
        db.execSQL("CREATE TABLE " + TABLE_AGENTS_NAME + "(" +
                        TABLE_AGENTS_COLUMN_AGENT_ID + " TEXT UNIQUE ON CONFLICT REPLACE, " +
                        TABLE_AGENTS_COLUMN_PARENT_ID + " TEXT, " +
                        TABLE_AGENTS_COLUMN_PERSON_LOGIN + " TEXT, " +
                        TABLE_AGENTS_COLUMN_INN + " TEXT, " +
                        TABLE_AGENTS_COLUMN_JUR_ADDRESS + " TEXT, " +
                        TABLE_AGENTS_COLUMN_PHYS_ADDRESS + " TEXT, " +
                        TABLE_AGENTS_COLUMN_NAME + " TEXT, " +
                        TABLE_AGENTS_COLUMN_CITY + " TEXT, " +
                        TABLE_AGENTS_COLUMN_FISCAL_MODE + " TEXT, " +
                        TABLE_AGENTS_COLUMN_KMM + " TEXT, " +
                        TABLE_AGENTS_COLUMN_TAX_REGNUM + " TEXT);"
        );
        db.execSQL("CREATE TABLE " + TABLE_TERMINALS_NAME + "(" +
                        TABLE_TERMINALS_COLUMN_ID + " TEXT UNIQUE ON CONFLICT REPLACE, " +
                        TABLE_TERMINALS_COLUMN_TYPE + " INTEGER, " +
                        TABLE_TERMINALS_COLUMN_SERIAL + " TEXT, " +
                        TABLE_TERMINALS_COLUMN_NAME + " TEXT, " +
                        TABLE_TERMINALS_COLUMN_WHO + " TEXT, " +
                        TABLE_TERMINALS_COLUMN_WORK_TIME + " TEXT, " +
                        TABLE_TERMINALS_COLUMN_AGENT_ID + " TEXT, " +
                        TABLE_TERMINALS_COLUMN_CITY + " TEXT, " +
                        TABLE_TERMINALS_COLUMN_CITY_ID + " INTEGER, " +
                        TABLE_TERMINALS_COLUMN_DISPLAY_ADDRESS + " TEXT, " +
                        TABLE_TERMINALS_COLUMN_MAIN_ADDRESS + " TEXT, " +
                        TABLE_TERMINALS_COLUMN_PERSON_ID + " TEXT);"
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
            values.put(TABLE_PERSONS_COLUMN_LOGIN, person.getLogin());
            values.put(TABLE_PERSONS_COLUMN_PASSWORD, person.getPasswordHash());
            values.put(TABLE_PERSONS_COLUMN_TERMINAL, person.getTerminal());
            values.put(TABLE_PERSONS_COLUMN_NAME, person.getName());
            values.put(TABLE_PERSONS_COLUMN_AGENT_ID, person.getAgentId());
            values.put(TABLE_PERSONS_COLUMN_ENABLED, person.isEnabled());
            values.put(TABLE_PERSONS_COLUMN_VERIFIED, person.isVerified());
            db.replace(TABLE_PERSONS_NAME, null, values);
        } finally {
            db.close();
        }
    }

    @Nullable
    /* package */ Person[] getPersonsImpl() {
        SQLiteDatabase db = getReadableDatabase();
        //noinspection TryFinallyCanBeTryWithResources
        try {
            Cursor cursor = db.query(TABLE_PERSONS_NAME, TABLE_PERSONS_COLUMNS, null, null, null, null, null);
            if (cursor == null) {
                return null;
            }
            try {
                Person[] result = new Person[cursor.getCount()];
                int index = 0;
                while (cursor.moveToNext()) {
                    result[index++] = new Person(cursor.getString(0), cursor.getString(1),
                            cursor.getString(2), cursor.getString(4), cursor.getString(3),
                            cursor.getInt(5) == 1, cursor.getInt(6) == 1);
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
                values.put(TABLE_AGENTS_COLUMN_AGENT_ID, agent.getId());
                values.put(TABLE_AGENTS_COLUMN_PARENT_ID, agent.getParentId());
                values.put(TABLE_AGENTS_COLUMN_INN, agent.getINN());
                values.put(TABLE_AGENTS_COLUMN_JUR_ADDRESS, agent.getJurAddress());
                values.put(TABLE_AGENTS_COLUMN_PHYS_ADDRESS, agent.getPhysAddress());
                values.put(TABLE_AGENTS_COLUMN_NAME, agent.getName());
                values.put(TABLE_AGENTS_COLUMN_CITY, agent.getCity());
                values.put(TABLE_AGENTS_COLUMN_FISCAL_MODE, agent.getFiscalMode());
                values.put(TABLE_AGENTS_COLUMN_KMM, agent.getKMM());
                values.put(TABLE_AGENTS_COLUMN_TAX_REGNUM, agent.getTaxRegnum());
                values.put(TABLE_AGENTS_COLUMN_PERSON_LOGIN, agent.getPersonLogin());
                db.replace(TABLE_AGENTS_NAME, null, values);
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
                values.put(TABLE_TERMINALS_COLUMN_ID, terminal.getId());
                values.put(TABLE_TERMINALS_COLUMN_TYPE, terminal.getType().id);
                values.put(TABLE_TERMINALS_COLUMN_SERIAL, terminal.getSerial());
                values.put(TABLE_TERMINALS_COLUMN_NAME, terminal.getDisplayName());
                values.put(TABLE_TERMINALS_COLUMN_WHO, terminal.getWhoAdded());
                values.put(TABLE_TERMINALS_COLUMN_WORK_TIME, terminal.getWorkTime());
                values.put(TABLE_TERMINALS_COLUMN_AGENT_ID, terminal.getAgentId());
                values.put(TABLE_TERMINALS_COLUMN_CITY, terminal.getCity());
                values.put(TABLE_TERMINALS_COLUMN_CITY_ID, terminal.getCityId());
                values.put(TABLE_TERMINALS_COLUMN_DISPLAY_ADDRESS, terminal.getDisplayAddress());
                values.put(TABLE_TERMINALS_COLUMN_MAIN_ADDRESS, terminal.getMainAddress());
                values.put(TABLE_TERMINALS_COLUMN_PERSON_ID, personId);
                db.replace(TABLE_TERMINALS_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
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
                        ((PersonsFuture) msg.obj).setPersons(getPersonsImpl());
                    }
                    break;
                case MSG_STORE_TERMINALS:
                    if (msg.obj != null) {
                        //noinspection unchecked
                        Pair<String, Terminal[]> data = (Pair<String, Terminal[]>) msg.obj;
                        addTerminalsImpl(data.first, data.second);
                    }
                    break;
            }
        }
    }

    private static class PersonsFuture implements Future<Person[]> {

        private final CountDownLatch mLatch = new CountDownLatch(1);
        @Nullable private Person[] mPersons;

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        public void setPersons(@Nullable Person[] persons) {
            mPersons = persons;
            mLatch.countDown();
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        @Nullable
        public Person[] get() throws InterruptedException, ExecutionException {
            mLatch.await();
            return mPersons;
        }

        @Override
        @Nullable
        public Person[] get(long timeout, @NonNull TimeUnit unit) throws InterruptedException,
                ExecutionException, TimeoutException {
            if (mLatch.await(timeout, unit)) {
                return mPersons;
            }
            throw new TimeoutException();
        }
    }
}
