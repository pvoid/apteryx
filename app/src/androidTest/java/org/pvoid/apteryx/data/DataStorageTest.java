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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pvoid.apteryx.data.accounts.Account;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class DataStorageTest {
    @Test
    public void tableCreateCheck() throws Exception {
        DataStorage storage = new DataStorage(Robolectric.application);
        Assert.assertEquals(DataStorage.DB_NAME, storage.getDatabaseName());
        SQLiteDatabase db = storage.getReadableDatabase();
        Assert.assertEquals(DataStorage.DB_VERSION, db.getVersion());

        Cursor cursor = db.rawQuery("pragma table_info(accounts)", null);
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("_id", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("login", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("password", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("terminal", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("title", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("verified", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertFalse(cursor.moveToNext());
        db.close();
    }

    @Test
    public void addAccountCheck() throws Exception {
        DataStorage storage = new DataStorage(Robolectric.application);
        Account account = new Account("LOGIN", "PASSWORD", "TERMINAL");
        storage.addAccountImpl(account);

        SQLiteDatabase db = storage.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, login, password, terminal, title, verified FROM accounts", null);
        Assert.assertNotNull(cursor);
        Assert.assertEquals(1, cursor.getCount());
        Assert.assertTrue(cursor.moveToFirst());
        Assert.assertEquals(1, cursor.getInt(0));
        Assert.assertEquals("LOGIN", cursor.getString(1));
        Assert.assertEquals("PASSWORD", cursor.getString(2));
        Assert.assertEquals("TERMINAL", cursor.getString(3));
        Assert.assertNull(cursor.getString(4));
        Assert.assertEquals(0, cursor.getInt(5));
        cursor.close();
        db.close();
    }
}
