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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import org.pvoid.apteryx.data.agents.Agent;
import org.pvoid.apteryx.data.persons.Person;

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

    private static final String TABLE_AGENTS_NAME = "agents";
    private static final String TABLE_AGENTS_COLUMN_ID = "_id";
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

    private static final int MSG_STORE_PERSON = 1;
    private static final int MSG_ADD_AGENTS = 2;

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

    @Override
    public void storeAgents(@NonNull Agent... agents) {
        Message msg = mHandler.obtainMessage(MSG_ADD_AGENTS, agents);
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
                        TABLE_AGENTS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        TABLE_AGENTS_COLUMN_AGENT_ID + " TEXT UNIQUE ON CONFLICT FAIL, " +
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

    /* package */ void addAgentsImpl(@NonNull Agent[] agents) {
        SQLiteDatabase db = getWritableDatabase();
        //noinspection TryFinallyCanBeTryWithResources
        try {
            ContentValues values = new ContentValues();
            db.beginTransaction();
            for (Agent agent : agents) {
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
                db.insert(TABLE_AGENTS_NAME, null, values);
            }
            db.endTransaction();
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
            }
        }
    }
}
