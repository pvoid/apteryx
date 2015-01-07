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
import android.os.Handler;

import org.fest.reflect.core.Reflection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pvoid.apteryx.data.agents.Agent;
import org.pvoid.apteryx.data.persons.Person;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowHandler;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class DataStorageTest {
    @Test
    public void tableCreateCheck() throws Exception {
        DataStorage storage = new DataStorage(Robolectric.application);
        Assert.assertEquals(DataStorage.DB_NAME, storage.getDatabaseName());
        SQLiteDatabase db = storage.getReadableDatabase();
        Assert.assertEquals(DataStorage.DB_VERSION, db.getVersion());

        Cursor cursor = db.rawQuery("pragma table_info(persons)", null);
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
        Assert.assertEquals("name", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("agent_id", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("verified", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("enabled", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertFalse(cursor.moveToNext());
        cursor.close();

        cursor = db.rawQuery("pragma table_info(agents)", null);
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("agent_id", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("parent_id", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("person_login", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("inn", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("jur_address", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("phys_address", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("name", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("city", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("fiscal_mode", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("kmm", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("tax_regnum", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertFalse(cursor.moveToNext());
        cursor.close();

        db.close();
    }

    @Test
    public void storePersonCheck() throws Exception {
        DataStorage storage = new DataStorage(Robolectric.application);
        Person person = Mockito.mock(Person.class);
        Mockito.when(person.getLogin()).thenReturn("LOGIN");
        Mockito.when(person.getPasswordHash()).thenReturn("PASSWORD");
        Mockito.when(person.getTerminal()).thenReturn("TERMINAL");
        Mockito.when(person.getName()).thenReturn(null);
        Mockito.when(person.getAgentId()).thenReturn(null);
        Mockito.when(person.isVerified()).thenReturn(false);
        Mockito.when(person.isEnabled()).thenReturn(true);
        storage = Mockito.spy(storage);
        storage.storePerson(person);
        Mockito.verify(storage, Mockito.never()).getWritableDatabase();
        Handler handler = Reflection.field("mHandler").ofType(Handler.class).in(storage).get();
        ShadowHandler shadowHandler = Robolectric.shadowOf(handler);
        Assert.assertTrue(shadowHandler.hasMessages(1, person));
    }

    @Test
    public void storePersonImplCheck() throws Exception {
        DataStorage storage = new DataStorage(Robolectric.application);
        Person person = Mockito.mock(Person.class);
        Mockito.when(person.getLogin()).thenReturn("LOGIN");
        Mockito.when(person.getPasswordHash()).thenReturn("PASSWORD");
        Mockito.when(person.getTerminal()).thenReturn("TERMINAL");
        Mockito.when(person.getName()).thenReturn(null);
        Mockito.when(person.getAgentId()).thenReturn(null);
        Mockito.when(person.isVerified()).thenReturn(false);
        Mockito.when(person.isEnabled()).thenReturn(true);
        storage.storePersonImpl(person);
        SQLiteDatabase db = storage.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM persons;", null);
        Assert.assertNotNull(cursor);
        Assert.assertEquals(1, cursor.getCount());
        Assert.assertTrue(cursor.moveToFirst());
        Assert.assertEquals("LOGIN", cursor.getString(0));
        Assert.assertEquals("PASSWORD", cursor.getString(1));
        Assert.assertEquals("TERMINAL", cursor.getString(2));
        Assert.assertNull(cursor.getString(3));
        Assert.assertNull(cursor.getString(4));
        Assert.assertEquals(0, cursor.getInt(5));
        Assert.assertEquals(1, cursor.getInt(6));
        cursor.close();
        db.close();

        Mockito.when(person.getName()).thenReturn("NAME");
        Mockito.when(person.getAgentId()).thenReturn("AGENT");
        Mockito.when(person.isVerified()).thenReturn(true);
        Mockito.when(person.isEnabled()).thenReturn(false);
        storage.storePersonImpl(person);
        db = storage.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM persons;", null);
        Assert.assertNotNull(cursor);
        Assert.assertEquals(1, cursor.getCount());
        Assert.assertTrue(cursor.moveToFirst());
        Assert.assertEquals("LOGIN", cursor.getString(0));
        Assert.assertEquals("PASSWORD", cursor.getString(1));
        Assert.assertEquals("TERMINAL", cursor.getString(2));
        Assert.assertEquals("NAME", cursor.getString(3));
        Assert.assertEquals("AGENT", cursor.getString(4));
        Assert.assertEquals(1, cursor.getInt(5));
        Assert.assertEquals(0, cursor.getInt(6));
        cursor.close();
        db.close();

        Mockito.when(person.getLogin()).thenReturn("LOGIN1");
        Mockito.when(person.getPasswordHash()).thenReturn("PASSWORD1");
        Mockito.when(person.getTerminal()).thenReturn("TERMINAL1");
        Mockito.when(person.getName()).thenReturn("NAME1");
        Mockito.when(person.getAgentId()).thenReturn("AGENT1");
        Mockito.when(person.isVerified()).thenReturn(true);
        Mockito.when(person.isEnabled()).thenReturn(true);
        storage.storePersonImpl(person);
        db = storage.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM persons;", null);
        Assert.assertNotNull(cursor);
        Assert.assertEquals(2, cursor.getCount());
        Assert.assertTrue(cursor.moveToFirst());
        Assert.assertEquals("LOGIN", cursor.getString(0));
        Assert.assertEquals("PASSWORD", cursor.getString(1));
        Assert.assertEquals("TERMINAL", cursor.getString(2));
        Assert.assertEquals("NAME", cursor.getString(3));
        Assert.assertEquals("AGENT", cursor.getString(4));
        Assert.assertEquals(1, cursor.getInt(5));
        Assert.assertEquals(0, cursor.getInt(6));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("LOGIN1", cursor.getString(0));
        Assert.assertEquals("PASSWORD1", cursor.getString(1));
        Assert.assertEquals("TERMINAL1", cursor.getString(2));
        Assert.assertEquals("NAME1", cursor.getString(3));
        Assert.assertEquals("AGENT1", cursor.getString(4));
        Assert.assertEquals(1, cursor.getInt(5));
        Assert.assertEquals(1, cursor.getInt(6));
        cursor.close();
        db.close();
    }

    @Test
    public void storeAgentsCheck() throws Exception {
        Agent[] agents = new Agent[2];
        DataStorage storage = new DataStorage(Robolectric.application);
        storage = Mockito.spy(storage);
        storage.storeAgents(agents);
        Mockito.verify(storage, Mockito.never()).getWritableDatabase();
        Handler handler = Reflection.field("mHandler").ofType(Handler.class).in(storage).get();
        ShadowHandler shadowHandler = Robolectric.shadowOf(handler);
        Assert.assertTrue(shadowHandler.hasMessages(2, agents));
    }

    @Test
    public void storeAgentsImplCheck() throws Exception {
        DataStorage storage = new DataStorage(Robolectric.application);
        Agent agent = Mockito.mock(Agent.class);
        Mockito.when(agent.getId()).thenReturn("ID");
        Mockito.when(agent.getParentId()).thenReturn("PARENT_ID");
        Mockito.when(agent.getINN()).thenReturn("INN");
        Mockito.when(agent.getJurAddress()).thenReturn("JYR_ADDRESS");
        Mockito.when(agent.getPhysAddress()).thenReturn("PHYS_ADDRESS");
        Mockito.when(agent.getName()).thenReturn("NAME");
        Mockito.when(agent.getCity()).thenReturn("CITY");
        Mockito.when(agent.getFiscalMode()).thenReturn("FISCAL_MODE");
        Mockito.when(agent.getKMM()).thenReturn("KMM");
        Mockito.when(agent.getTaxRegnum()).thenReturn("TAX_REGNUM");
        Mockito.when(agent.getPersonLogin()).thenReturn("PERSON_LOGIN");
        Mockito.when(agent.isValid()).thenReturn(false);
        Agent agents[] = new Agent[] {agent, null};
        storage.addAgentsImpl(agents);

        SQLiteDatabase db = storage.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from agents", null);
        Assert.assertNotNull(cursor);
        Assert.assertEquals(0, cursor.getCount());
        cursor.close();
        db.close();

        Mockito.when(agent.isValid()).thenReturn(true);
        storage.addAgentsImpl(agents);
        db = storage.getReadableDatabase();
        cursor = db.rawQuery("select * from agents", null);
        Assert.assertNotNull(cursor);
        Assert.assertEquals(1, cursor.getCount());
        cursor.moveToFirst();
        Assert.assertEquals("ID", cursor.getString(0));
        Assert.assertEquals("PARENT_ID", cursor.getString(1));
        Assert.assertEquals("PERSON_LOGIN", cursor.getString(2));
        Assert.assertEquals("INN", cursor.getString(3));
        Assert.assertEquals("JYR_ADDRESS", cursor.getString(4));
        Assert.assertEquals("PHYS_ADDRESS", cursor.getString(5));
        Assert.assertEquals("NAME", cursor.getString(6));
        Assert.assertEquals("CITY", cursor.getString(7));
        Assert.assertEquals("FISCAL_MODE", cursor.getString(8));
        Assert.assertEquals("KMM", cursor.getString(9));
        Assert.assertEquals("TAX_REGNUM", cursor.getString(10));
        cursor.close();
        db.close();

        Mockito.when(agent.getName()).thenReturn("NAME2");
        storage.addAgentsImpl(agents);
        db = storage.getReadableDatabase();
        cursor = db.rawQuery("select * from agents", null);
        Assert.assertNotNull(cursor);
        Assert.assertEquals(1, cursor.getCount());
        cursor.moveToFirst();
        Assert.assertEquals("ID", cursor.getString(0));
        Assert.assertEquals("PARENT_ID", cursor.getString(1));
        Assert.assertEquals("PERSON_LOGIN", cursor.getString(2));
        Assert.assertEquals("INN", cursor.getString(3));
        Assert.assertEquals("JYR_ADDRESS", cursor.getString(4));
        Assert.assertEquals("PHYS_ADDRESS", cursor.getString(5));
        Assert.assertEquals("NAME2", cursor.getString(6));
        Assert.assertEquals("CITY", cursor.getString(7));
        Assert.assertEquals("FISCAL_MODE", cursor.getString(8));
        Assert.assertEquals("KMM", cursor.getString(9));
        Assert.assertEquals("TAX_REGNUM", cursor.getString(10));
        cursor.close();
        db.close();

        Mockito.when(agent.getName()).thenReturn("NAME");
        Mockito.when(agent.getId()).thenReturn("ID2");
        storage.addAgentsImpl(agents);
        db = storage.getReadableDatabase();
        cursor = db.rawQuery("select * from agents", null);
        Assert.assertNotNull(cursor);
        Assert.assertEquals(2, cursor.getCount());
        cursor.moveToFirst();
        Assert.assertEquals("ID", cursor.getString(0));
        Assert.assertEquals("PARENT_ID", cursor.getString(1));
        Assert.assertEquals("PERSON_LOGIN", cursor.getString(2));
        Assert.assertEquals("INN", cursor.getString(3));
        Assert.assertEquals("JYR_ADDRESS", cursor.getString(4));
        Assert.assertEquals("PHYS_ADDRESS", cursor.getString(5));
        Assert.assertEquals("NAME2", cursor.getString(6));
        Assert.assertEquals("CITY", cursor.getString(7));
        Assert.assertEquals("FISCAL_MODE", cursor.getString(8));
        Assert.assertEquals("KMM", cursor.getString(9));
        Assert.assertEquals("TAX_REGNUM", cursor.getString(10));
        cursor.moveToNext();
        Assert.assertEquals("ID2", cursor.getString(0));
        Assert.assertEquals("PARENT_ID", cursor.getString(1));
        Assert.assertEquals("PERSON_LOGIN", cursor.getString(2));
        Assert.assertEquals("INN", cursor.getString(3));
        Assert.assertEquals("JYR_ADDRESS", cursor.getString(4));
        Assert.assertEquals("PHYS_ADDRESS", cursor.getString(5));
        Assert.assertEquals("NAME", cursor.getString(6));
        Assert.assertEquals("CITY", cursor.getString(7));
        Assert.assertEquals("FISCAL_MODE", cursor.getString(8));
        Assert.assertEquals("KMM", cursor.getString(9));
        Assert.assertEquals("TAX_REGNUM", cursor.getString(10));
        cursor.close();
        db.close();
    }
}
