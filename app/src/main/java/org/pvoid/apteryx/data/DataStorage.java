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

import org.pvoid.apteryx.data.accounts.Account;

/* package */ class DataStorage extends SQLiteOpenHelper implements Storage {

    /* package */ static final String DB_NAME = "apteryx";
    /* package */ static final int DB_VERSION = 1;

    private static final String TABLE_ACCOUNTS_NAME = "accounts";
    private static final String TABLE_ACCOUNTS_COLUMN_ID = "_id";
    private static final String TABLE_ACCOUNTS_COLUMN_LOGIN = "login";
    private static final String TABLE_ACCOUNTS_COLUMN_PASSWORD = "password";
    private static final String TABLE_ACCOUNTS_COLUMN_TERMINAL = "terminal";
    private static final String TABLE_ACCOUNTS_COLUMN_TITLE = "title";
    private static final String TABLE_ACCOUNTS_COLUMN_VERIFIED = "verified";

    private static final int MSG_ADD_ACCOUNT = 1;

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
                        TABLE_ACCOUNTS_COLUMN_VERIFIED + " INTEGER)"
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
            values.put(TABLE_ACCOUNTS_COLUMN_VERIFIED, account.isVerified() ? 1 : 0);
            db.insert(TABLE_ACCOUNTS_NAME, null, values);
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
            }
        }
    }
}
