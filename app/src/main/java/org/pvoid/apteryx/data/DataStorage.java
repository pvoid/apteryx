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
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import org.pvoid.apteryx.data.accounts.Account;
import org.pvoid.apteryx.data.accounts.Agent;
import org.pvoid.apteryx.util.LogHelper;

/* package */ class DataStorage extends SQLiteOpenHelper implements Storage {

    private static final String TAG = "Storage";

    /* package */ static final String DB_NAME = "apteryx";
    /* package */ static final int DB_VERSION = 1;

    private static final String TABLE_ACCOUNTS_NAME = "accounts";
    private static final String TABLE_ACCOUNTS_COLUMN_ID = "_id";
    private static final String TABLE_ACCOUNTS_COLUMN_LOGIN = "login";
    private static final String TABLE_ACCOUNTS_COLUMN_PASSWORD = "password";
    private static final String TABLE_ACCOUNTS_COLUMN_TERMINAL = "terminal";
    private static final String TABLE_ACCOUNTS_COLUMN_TITLE = "title";
    private static final String TABLE_ACCOUNTS_COLUMN_AGENT_ID = "agent_id";
    private static final String TABLE_ACCOUNTS_COLUMN_VERIFIED = "verified";

    private static final String TABLE_AGENTS_NAME = "agents";
    private static final String TABLE_AGENTS_COLUMN_ID = "_id";
    private static final String TABLE_AGENTS_COLUMN_AGENT_ID = "agent_id";
    private static final String TABLE_AGENTS_COLUMN_PARENT_ID = "parent_id";
    private static final String TABLE_AGENTS_COLUMN_INN = "inn";
    private static final String TABLE_AGENTS_COLUMN_JUR_ADDRESS = "jur_address";
    private static final String TABLE_AGENTS_COLUMN_PHYS_ADDRESS = "phys_address";
    private static final String TABLE_AGENTS_COLUMN_NAME = "name";
    private static final String TABLE_AGENTS_COLUMN_CITY = "city";
    private static final String TABLE_AGENTS_COLUMN_FISCAL_MODE = "fiscal_mode";
    private static final String TABLE_AGENTS_COLUMN_KMM = "kmm";
    private static final String TABLE_AGENTS_COLUMN_TAX_REGNUM = "tax_regnum";

    private static final int MSG_ADD_ACCOUNT = 1;
    private static final int MSG_UPDATE_ACCOUNT = 2;
    private static final int MSG_ADD_AGENTS = 3;

    private final HandlerThread mThread;
    private final Handler mHandler;

    /* package */ DataStorage(@NonNull Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mThread = new HandlerThread("DbWorker");
        mThread.start();
        mHandler = new DbHandler(mThread.getLooper());
    }

    @Override
    public void storeAccount(@NonNull Account account) {
        Message msg = mHandler.obtainMessage(MSG_ADD_ACCOUNT, account);
        mHandler.sendMessage(msg);
    }

    @Override
    public void updateAccount(@NonNull Account account) {
        Message msg = mHandler.obtainMessage(MSG_UPDATE_ACCOUNT, account);
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
        db.execSQL("CREATE TABLE " + TABLE_ACCOUNTS_NAME + "(" +
                        TABLE_ACCOUNTS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        TABLE_ACCOUNTS_COLUMN_LOGIN + " TEXT UNIQUE ON CONFLICT FAIL, " +
                        TABLE_ACCOUNTS_COLUMN_PASSWORD + " TEXT, " +
                        TABLE_ACCOUNTS_COLUMN_TERMINAL + " TEXT, " +
                        TABLE_ACCOUNTS_COLUMN_TITLE + " TEXT," +
                        TABLE_ACCOUNTS_COLUMN_AGENT_ID + " TEXT," +
                        TABLE_ACCOUNTS_COLUMN_VERIFIED + " INTEGER);"
        );
        db.execSQL("CREATE TABLE " + TABLE_AGENTS_NAME + "(" +
                        TABLE_AGENTS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        TABLE_AGENTS_COLUMN_AGENT_ID + " TEXT UNIQUE ON CONFLICT FAIL, " +
                        TABLE_AGENTS_COLUMN_PARENT_ID + " TEXT, " +
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

    /* package */ void addAccountImpl(@NonNull Account account) {
        SQLiteDatabase db = getWritableDatabase();
        //noinspection TryFinallyCanBeTryWithResources
        try {
            ContentValues values = new ContentValues();
            values.put(TABLE_ACCOUNTS_COLUMN_LOGIN, account.getLogin());
            values.put(TABLE_ACCOUNTS_COLUMN_PASSWORD, account.getPasswordHash());
            values.put(TABLE_ACCOUNTS_COLUMN_TERMINAL, account.getTerminal());
            values.put(TABLE_ACCOUNTS_COLUMN_TITLE, account.getTitle());
            values.put(TABLE_ACCOUNTS_COLUMN_AGENT_ID, account.getAgentId());
            values.put(TABLE_ACCOUNTS_COLUMN_VERIFIED, account.isVerified() ? 1 : 0);
            db.insert(TABLE_ACCOUNTS_NAME, null, values);
        } catch (SQLiteConstraintException e) {
            LogHelper.error(TAG, "Error while adding account: %1$s", e.getMessage());
        } finally {
            db.close();
        }
    }

    /* package */ void updateAccountImpl(@NonNull Account account) {
        SQLiteDatabase db = getWritableDatabase();
        //noinspection TryFinallyCanBeTryWithResources
        try {
            ContentValues values = new ContentValues();
            values.put(TABLE_ACCOUNTS_COLUMN_PASSWORD, account.getPasswordHash());
            values.put(TABLE_ACCOUNTS_COLUMN_TERMINAL, account.getTerminal());
            values.put(TABLE_ACCOUNTS_COLUMN_TITLE, account.getTitle());
            values.put(TABLE_ACCOUNTS_COLUMN_AGENT_ID, account.getAgentId());
            values.put(TABLE_ACCOUNTS_COLUMN_VERIFIED, account.isVerified() ? 1 : 0);
            db.update(TABLE_ACCOUNTS_NAME, values, TABLE_ACCOUNTS_COLUMN_LOGIN + "=?",
                    new String[] {account.getLogin()});
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
                case MSG_ADD_ACCOUNT:
                    if (msg.obj != null) {
                        addAccountImpl((Account) msg.obj);
                    }
                    break;
                case MSG_UPDATE_ACCOUNT:
                    if (msg.obj != null) {
                        updateAccountImpl((Account) msg.obj);
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
