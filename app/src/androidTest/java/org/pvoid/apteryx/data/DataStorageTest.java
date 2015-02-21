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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pvoid.apteryx.data.agents.Agent;
import org.pvoid.apteryx.data.persons.Person;
import org.pvoid.apteryx.data.terminals.Terminal;
import org.pvoid.apteryx.data.terminals.TerminalCash;
import org.pvoid.apteryx.data.terminals.TerminalState;
import org.pvoid.apteryx.data.terminals.TerminalStats;
import org.pvoid.apteryx.data.terminals.TerminalType;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.DataOutputStream;
import java.io.IOException;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class DataStorageTest {
    @Test
    public void helperCheck() throws Exception {
        DataStorage.DbHelper helper = new DataStorage.DbHelper(Robolectric.application);
        Assert.assertEquals(DataStorage.DB_NAME, helper.getDatabaseName());
        SQLiteDatabase db = helper.getReadableDatabase();
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

        cursor = db.rawQuery("pragma table_info(terminals)", null);
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("id", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("type", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("serial", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("display_name", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("who_added", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("work_time", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("agent_id", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("city", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("city_id", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("display_address", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("main_address", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("person_id", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertFalse(cursor.moveToNext());
        cursor.close();

        cursor = db.rawQuery("pragma table_info(terminals_state)", null);
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("id", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("agent_id", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("last_activity", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("last_payment", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("machine_status", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("note_error", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("printer_error", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("card_reader_status", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("signal_level", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("sim_balance", cursor.getString(1));
        Assert.assertEquals("REAL", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("door_alarm", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("door_open", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("event", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("event_text", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertFalse(cursor.moveToNext());
        cursor.close();

        cursor = db.rawQuery("pragma table_info(terminals_stat)", null);
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("terminal_id", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("agent_id", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("system_up_time", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("up_time", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("pays_per_hour", cursor.getString(1));
        Assert.assertEquals("REAL", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("bills_per_pay", cursor.getString(1));
        Assert.assertEquals("REAL", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("card_reader_used_hours", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("card_reader_used_day", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("time_to_cachin_full", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("time_to_cachin_service", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("time_to_printer_out", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("time_to_printer_service", cursor.getString(1));
        Assert.assertEquals("INTEGER", cursor.getString(2));
        Assert.assertFalse(cursor.moveToNext());
        cursor.close();

        cursor = db.rawQuery("pragma table_info(terminals_cash)", null);
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("terminal_id", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("agent_id", cursor.getString(1));
        Assert.assertEquals("TEXT", cursor.getString(2));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("cash", cursor.getString(1));
        Assert.assertEquals("BLOB", cursor.getString(2));
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
        storage.storePerson(person);
        SQLiteDatabase db = storage.getReadableDatabase();
        Assert.assertNotNull(db);
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

        Mockito.when(person.getName()).thenReturn("NAME");
        Mockito.when(person.getAgentId()).thenReturn("AGENT");
        Mockito.when(person.isVerified()).thenReturn(true);
        Mockito.when(person.isEnabled()).thenReturn(false);
        storage.storePerson(person);
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

        Mockito.when(person.getLogin()).thenReturn("LOGIN1");
        Mockito.when(person.getPasswordHash()).thenReturn("PASSWORD1");
        Mockito.when(person.getTerminal()).thenReturn("TERMINAL1");
        Mockito.when(person.getName()).thenReturn("NAME1");
        Mockito.when(person.getAgentId()).thenReturn("AGENT1");
        Mockito.when(person.isVerified()).thenReturn(true);
        Mockito.when(person.isEnabled()).thenReturn(true);
        storage.storePerson(person);
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
    }

    @Test
    public void getPersonsCheck() throws Exception {
        DataStorage storage = new DataStorage(Robolectric.application);
        ContentValues values = new ContentValues();

        Person persons[] = storage.getPersons();
        Assert.assertNotNull(persons);
        Assert.assertEquals(0, persons.length);

        SQLiteDatabase db = storage.getWritableDatabase();

        values.put("login", "LOGIN0");
        values.put("password", "STRONG_PASSWORD");
        values.put("terminal", "TERMINAL0");
        values.put("name", "NAME0");
        values.put("agent_id", "AGENT0");
        values.put("enabled", false);
        values.put("verified", true);
        db.replace("persons", null, values);
        values.put("login", "LOGIN1");
        values.put("password", "WEAK_PASSWORD");
        values.put("terminal", "TERMINAL1");
        values.put("name", "NAME1");
        values.put("agent_id", "AGENT1");
        values.put("enabled", true);
        values.put("verified", false);
        db.replace("persons", null, values);

        persons = storage.getPersons();
        Assert.assertNotNull(persons);
        Assert.assertEquals(2, persons.length);
        Assert.assertEquals("LOGIN0", persons[0].getLogin());
        Assert.assertEquals("STRONG_PASSWORD", persons[0].getPasswordHash());
        Assert.assertEquals("TERMINAL0", persons[0].getTerminal());
        Assert.assertEquals("NAME0", persons[0].getName());
        Assert.assertEquals("AGENT0", persons[0].getAgentId());
        Assert.assertEquals(false, persons[0].isEnabled());
        Assert.assertEquals(true, persons[0].isVerified());
        Assert.assertEquals("LOGIN1", persons[1].getLogin());
        Assert.assertEquals("WEAK_PASSWORD", persons[1].getPasswordHash());
        Assert.assertEquals("TERMINAL1", persons[1].getTerminal());
        Assert.assertEquals("NAME1", persons[1].getName());
        Assert.assertEquals("AGENT1", persons[1].getAgentId());
        Assert.assertEquals(true, persons[1].isEnabled());
        Assert.assertEquals(false, persons[1].isVerified());
    }

    @Test
    public void storeAgentsCheck() throws Exception {
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
        storage.storeAgents(agents);

        SQLiteDatabase db = storage.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from agents", null);
        Assert.assertNotNull(cursor);
        Assert.assertEquals(0, cursor.getCount());
        cursor.close();

        Mockito.when(agent.isValid()).thenReturn(true);
        storage.storeAgents(agents);
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

        Mockito.when(agent.getName()).thenReturn("NAME2");
        storage.storeAgents(agents);
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

        Mockito.when(agent.getName()).thenReturn("NAME");
        Mockito.when(agent.getId()).thenReturn("ID2");
        storage.storeAgents(agents);
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
    }

    @Test
    public void getAgentsCheck() throws Exception {
        DataStorage storage = new DataStorage(Robolectric.application);
        ContentValues values = new ContentValues();

        Agent[] agents = storage.getAgents();
        Assert.assertNotNull(agents);
        Assert.assertEquals(0, agents.length);

        SQLiteDatabase db = storage.getWritableDatabase();
        values.put("agent_id", "AGENT0");
        values.put("parent_id", "AGENT_PARENT0");
        values.put("person_login", "PERSON0");
        values.put("inn", "INN0");
        values.put("jur_address", "JUR_ADDRESS0");
        values.put("phys_address", "PHYS_ADDRESS0");
        values.put("name", "NAME0");
        values.put("city", "CITY0");
        values.put("fiscal_mode", "FISCAL_MODE0");
        values.put("kmm", "KMM0");
        values.put("tax_regnum", "TAX_REGNUM0");
        db.replace("agents", null, values);

        agents = storage.getAgents();
        Assert.assertNotNull(agents);
        Assert.assertEquals(1, agents.length);
        Assert.assertEquals("AGENT0", agents[0].getId());
        Assert.assertEquals("AGENT_PARENT0", agents[0].getParentId());
        Assert.assertEquals("PERSON0", agents[0].getPersonLogin());
        Assert.assertEquals("INN0", agents[0].getINN());
        Assert.assertEquals("JUR_ADDRESS0", agents[0].getJurAddress());
        Assert.assertEquals("PHYS_ADDRESS0", agents[0].getPhysAddress());
        Assert.assertEquals("NAME0", agents[0].getName());
        Assert.assertEquals("CITY0", agents[0].getCity());
        Assert.assertEquals("FISCAL_MODE0", agents[0].getFiscalMode());
        Assert.assertEquals("KMM0", agents[0].getKMM());
        Assert.assertEquals("TAX_REGNUM0", agents[0].getTaxRegnum());
    }

    @Test
    public void storeTerminalsCheck() throws Exception {
        DataStorage storage = new DataStorage(Robolectric.application);
        Terminal terminal = Mockito.mock(Terminal.class);
        Mockito.when(terminal.getId()).thenReturn("ID");
        Mockito.when(terminal.getAgentId()).thenReturn("AGENT_ID");
        Mockito.when(terminal.getWorkTime()).thenReturn("WORK_TIME");
        Mockito.when(terminal.getWhoAdded()).thenReturn("WHO_ADDED");
        Mockito.when(terminal.getDisplayName()).thenReturn("DISPLAY_NAME");
        Mockito.when(terminal.getSerial()).thenReturn("SERIAL");
        Mockito.when(terminal.getType()).thenReturn(TerminalType.Windows);
        Mockito.when(terminal.getMainAddress()).thenReturn("MAIN_ADDRESS");
        Mockito.when(terminal.getDisplayAddress()).thenReturn("DISPLAY_ADDRESS");
        Mockito.when(terminal.getCityId()).thenReturn(1000);
        Mockito.when(terminal.getCity()).thenReturn("CITY");
        storage.storeTerminals("PERSON_ID", terminal, null);

        SQLiteDatabase db = storage.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM terminals", null);
        Assert.assertNotNull(cursor);
        Assert.assertEquals(1, cursor.getCount());
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("ID", cursor.getString(0));
        Assert.assertEquals(TerminalType.Windows.id, cursor.getInt(1));
        Assert.assertEquals("SERIAL", cursor.getString(2));
        Assert.assertEquals("DISPLAY_NAME", cursor.getString(3));
        Assert.assertEquals("WHO_ADDED", cursor.getString(4));
        Assert.assertEquals("WORK_TIME", cursor.getString(5));
        Assert.assertEquals("AGENT_ID", cursor.getString(6));
        Assert.assertEquals("CITY", cursor.getString(7));
        Assert.assertEquals(1000, cursor.getInt(8));
        Assert.assertEquals("DISPLAY_ADDRESS", cursor.getString(9));
        Assert.assertEquals("MAIN_ADDRESS", cursor.getString(10));
        Assert.assertEquals("PERSON_ID", cursor.getString(11));
        cursor.close();

        Mockito.when(terminal.getDisplayName()).thenReturn("DISPLAY_NAME2");
        storage.storeTerminals("PERSON_ID", terminal);
        db = storage.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM terminals", null);
        Assert.assertNotNull(cursor);
        Assert.assertEquals(1, cursor.getCount());
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("ID", cursor.getString(0));
        Assert.assertEquals(TerminalType.Windows.id, cursor.getInt(1));
        Assert.assertEquals("SERIAL", cursor.getString(2));
        Assert.assertEquals("DISPLAY_NAME2", cursor.getString(3));
        Assert.assertEquals("WHO_ADDED", cursor.getString(4));
        Assert.assertEquals("WORK_TIME", cursor.getString(5));
        Assert.assertEquals("AGENT_ID", cursor.getString(6));
        Assert.assertEquals("CITY", cursor.getString(7));
        Assert.assertEquals(1000, cursor.getInt(8));
        Assert.assertEquals("DISPLAY_ADDRESS", cursor.getString(9));
        Assert.assertEquals("MAIN_ADDRESS", cursor.getString(10));
        Assert.assertEquals("PERSON_ID", cursor.getString(11));
        cursor.close();

        Mockito.when(terminal.getId()).thenReturn("ID2");
        Mockito.when(terminal.getDisplayName()).thenReturn("DISPLAY_NAME3");
        storage.storeTerminals("PERSON_ID", terminal);
        db = storage.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM terminals", null);
        Assert.assertNotNull(cursor);
        Assert.assertEquals(2, cursor.getCount());
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("ID", cursor.getString(0));
        Assert.assertEquals(TerminalType.Windows.id, cursor.getInt(1));
        Assert.assertEquals("SERIAL", cursor.getString(2));
        Assert.assertEquals("DISPLAY_NAME2", cursor.getString(3));
        Assert.assertEquals("WHO_ADDED", cursor.getString(4));
        Assert.assertEquals("WORK_TIME", cursor.getString(5));
        Assert.assertEquals("AGENT_ID", cursor.getString(6));
        Assert.assertEquals("CITY", cursor.getString(7));
        Assert.assertEquals(1000, cursor.getInt(8));
        Assert.assertEquals("DISPLAY_ADDRESS", cursor.getString(9));
        Assert.assertEquals("MAIN_ADDRESS", cursor.getString(10));
        Assert.assertEquals("PERSON_ID", cursor.getString(11));
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("ID2", cursor.getString(0));
        Assert.assertEquals(TerminalType.Windows.id, cursor.getInt(1));
        Assert.assertEquals("SERIAL", cursor.getString(2));
        Assert.assertEquals("DISPLAY_NAME3", cursor.getString(3));
        Assert.assertEquals("WHO_ADDED", cursor.getString(4));
        Assert.assertEquals("WORK_TIME", cursor.getString(5));
        Assert.assertEquals("AGENT_ID", cursor.getString(6));
        Assert.assertEquals("CITY", cursor.getString(7));
        Assert.assertEquals(1000, cursor.getInt(8));
        Assert.assertEquals("DISPLAY_ADDRESS", cursor.getString(9));
        Assert.assertEquals("MAIN_ADDRESS", cursor.getString(10));
        Assert.assertEquals("PERSON_ID", cursor.getString(11));
        cursor.close();
    }

    @Test
    public void getTerminalsCheck() throws Exception {
        DataStorage storage = new DataStorage(Robolectric.application);
        ContentValues values = new ContentValues();

        Terminal terminals[] = storage.getTerminals();
        Assert.assertNotNull(terminals);
        Assert.assertEquals(0, terminals.length);

        SQLiteDatabase db = storage.getWritableDatabase();
        values.put("id", "ID0");
        values.put("type", 18);
        values.put("serial", "SERIAL0");
        values.put("display_name", "DISPLAY0");
        values.put("who_added", "WHO0");
        values.put("work_time", "WORK_TIME0");
        values.put("agent_id", "AGENT0");
        values.put("city", "CITY0");
        values.put("city_id", 1000);
        values.put("display_address", "DISPLAY_ADDRESS0");
        values.put("main_address", "MAIN_ADDRESS0");
        values.put("person_id", "PERSON0");
        db.replace("terminals", null, values);

        terminals = storage.getTerminals();
        Assert.assertNotNull(terminals);
        Assert.assertEquals(1, terminals.length);
        Assert.assertEquals("ID0", terminals[0].getId());
        Assert.assertEquals(TerminalType.Linux, terminals[0].getType());
        Assert.assertEquals("SERIAL0", terminals[0].getSerial());
        Assert.assertEquals("DISPLAY0", terminals[0].getDisplayName());
        Assert.assertEquals("WHO0", terminals[0].getWhoAdded());
        Assert.assertEquals("WORK_TIME0", terminals[0].getWorkTime());
        Assert.assertEquals("AGENT0", terminals[0].getAgentId());
        Assert.assertEquals("CITY0", terminals[0].getCity());
        Assert.assertEquals(1000, terminals[0].getCityId());
        Assert.assertEquals("DISPLAY_ADDRESS0", terminals[0].getDisplayAddress());
        Assert.assertEquals("MAIN_ADDRESS0", terminals[0].getMainAddress());
        Assert.assertEquals("PERSON0", terminals[0].getPersonId());
    }

    @Test
    public void storeTerminalStatusesCheck() throws Exception {
        TerminalState status = Mockito.mock(TerminalState.class);
        Mockito.when(status.getId()).thenReturn("ID0");
        Mockito.when(status.getAgentId()).thenReturn("AGENT_ID0");
        Mockito.when(status.getLastActivity()).thenReturn(10000l);
        Mockito.when(status.getLastPayment()).thenReturn(20000l);
        Mockito.when(status.getMachineStatus()).thenReturn(100);
        Mockito.when(status.getNoteError()).thenReturn("NOTE0");
        Mockito.when(status.getPrinterError()).thenReturn("PRINTER0");
        Mockito.when(status.getCardReaderStatus()).thenReturn("READER0");
        Mockito.when(status.getSignalLevel()).thenReturn("SIG_LEVEL0");
        Mockito.when(status.getSimBalance()).thenReturn(100.20f);
        Mockito.when(status.getDoorAlarmCount()).thenReturn(20);
        Mockito.when(status.getDoorOpenCount()).thenReturn(30);
        Mockito.when(status.getEvent()).thenReturn(15);
        Mockito.when(status.getEventText()).thenReturn("EVENT0");
        TerminalState failStatus = Mockito.mock(TerminalState.class);
        RuntimeException ex = new RuntimeException("Booo!");
        Mockito.when(failStatus.getId()).thenThrow(ex);

        DataStorage storage = new DataStorage(Robolectric.application);
        try {
            storage.storeTerminalStates(status, failStatus);
        } catch (RuntimeException e) {
            Assert.assertEquals(ex, e);
        }
        SQLiteDatabase db = storage.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from terminals_state", null);
        Assert.assertEquals(0, cursor.getCount());
        cursor.close();

        storage.storeTerminalStates(status, null);
        db = storage.getReadableDatabase();
        cursor = db.rawQuery("select * from terminals_state", null);
        Assert.assertEquals(1, cursor.getCount());
        Assert.assertEquals(14, cursor.getColumnCount());
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("ID0", cursor.getString(0));
        Assert.assertEquals("AGENT_ID0", cursor.getString(1));
        Assert.assertEquals(10000l, cursor.getLong(2));
        Assert.assertEquals(20000l, cursor.getLong(3));
        Assert.assertEquals(100, cursor.getInt(4));
        Assert.assertEquals("NOTE0", cursor.getString(5));
        Assert.assertEquals("PRINTER0", cursor.getString(6));
        Assert.assertEquals("READER0", cursor.getString(7));
        Assert.assertEquals("SIG_LEVEL0", cursor.getString(8));
        Assert.assertEquals(100.20f, cursor.getFloat(9), 0);
        Assert.assertEquals(20, cursor.getInt(10));
        Assert.assertEquals(30, cursor.getInt(11));
        Assert.assertEquals(15, cursor.getInt(12));
        Assert.assertEquals("EVENT0", cursor.getString(13));
        Assert.assertFalse(cursor.moveToNext());
        cursor.close();
    }

    @Test
    public void getTerminalStatusesCheck() throws Exception {
        DataStorage storage = new DataStorage(Robolectric.application);
        ContentValues values = new ContentValues();

        SQLiteDatabase db = storage.getWritableDatabase();
        values.put("id", "ID0");
        values.put("agent_id", "AGENT_ID0");
        values.put("last_activity", 1000l);
        values.put("last_payment", 5000l);
        values.put("machine_status", 500);
        values.put("note_error", "NOTE_ERROR0");
        values.put("printer_error", "PRINTER_ERROR0");
        values.put("signal_level", "LEVEL0");
        values.put("sim_balance", 2.4f);
        values.put("door_alarm", 10);
        values.put("door_open", 15);
        values.put("event", 30);
        values.put("event_text", "EVENT_TEXT0");
        db.replace("terminals_state", null, values);
        values.put("id", "ID1");
        values.put("agent_id", "AGENT_ID1");
        values.put("last_activity", 1001l);
        values.put("last_payment", 5001l);
        values.put("machine_status", 501);
        values.put("note_error", "NOTE_ERROR1");
        values.put("printer_error", "PRINTER_ERROR1");
        values.put("signal_level", "LEVEL1");
        values.put("sim_balance", 5.4f);
        values.put("door_alarm", 11);
        values.put("door_open", 16);
        values.put("event", 31);
        values.put("event_text", "EVENT_TEXT1");
        db.replace("terminals_state", null, values);

        TerminalState statuses[] = storage.getTerminalStates();
        Assert.assertNotNull(statuses);
        Assert.assertEquals(2, statuses.length);
        TerminalState status = statuses[0];
        Assert.assertNotNull(status);
        Assert.assertEquals("ID0", status.getId());
        Assert.assertEquals("AGENT_ID0", status.getAgentId());
        Assert.assertEquals(1000l, status.getLastActivity());
        Assert.assertEquals(5000l, status.getLastPayment());
        Assert.assertEquals(500, status.getMachineStatus());
        Assert.assertEquals("NOTE_ERROR0", status.getNoteError());
        Assert.assertEquals("PRINTER_ERROR0", status.getPrinterError());
        Assert.assertEquals("LEVEL0", status.getSignalLevel());
        Assert.assertEquals(2.4f, status.getSimBalance(), 0);
        Assert.assertEquals(10, status.getDoorAlarmCount());
        Assert.assertEquals(15, status.getDoorOpenCount());
        Assert.assertEquals(30, status.getEvent());
        Assert.assertEquals("EVENT_TEXT0", status.getEventText());

        status = statuses[1];
        Assert.assertNotNull(status);
        Assert.assertEquals("ID1", status.getId());
        Assert.assertEquals("AGENT_ID1", status.getAgentId());
        Assert.assertEquals(1001l, status.getLastActivity());
        Assert.assertEquals(5001l, status.getLastPayment());
        Assert.assertEquals(501, status.getMachineStatus());
        Assert.assertEquals("NOTE_ERROR1", status.getNoteError());
        Assert.assertEquals("PRINTER_ERROR1", status.getPrinterError());
        Assert.assertEquals("LEVEL1", status.getSignalLevel());
        Assert.assertEquals(5.4f, status.getSimBalance(), 0);
        Assert.assertEquals(11, status.getDoorAlarmCount());
        Assert.assertEquals(16, status.getDoorOpenCount());
        Assert.assertEquals(31, status.getEvent());
        Assert.assertEquals("EVENT_TEXT1", status.getEventText());
    }

    @Test
    public void storeTerminalStatsCheck() throws Exception {
        TerminalStats stat = Mockito.mock(TerminalStats.class);
        Mockito.when(stat.getTerminalId()).thenReturn("TERMINAL_ID0");
        Mockito.when(stat.getAgentId()).thenReturn("AGENT_ID0");
        Mockito.when(stat.getSystemUpTime()).thenReturn(10000);
        Mockito.when(stat.getUpTime()).thenReturn(20000);
        Mockito.when(stat.getPaysPerHour()).thenReturn(1.20f);
        Mockito.when(stat.getBillsPerPay()).thenReturn(3.2f);
        Mockito.when(stat.getCardReaderUsedHours()).thenReturn(2);
        Mockito.when(stat.getCardReaderUsedDay()).thenReturn(100);
        Mockito.when(stat.getTimeToCashinFull()).thenReturn(4000l);
        Mockito.when(stat.getTimeToCashinService()).thenReturn(800l);
        Mockito.when(stat.getTimeToPrinterPaperOut()).thenReturn(5000l);
        Mockito.when(stat.getTimeToPrinterService()).thenReturn(300l);
        TerminalStats failStat = Mockito.mock(TerminalStats.class);
        RuntimeException ex = new RuntimeException("Booo!");
        Mockito.when(failStat.getTerminalId()).thenThrow(ex);

        DataStorage storage = new DataStorage(Robolectric.application);
        try {
            storage.storeTerminalStats(stat, failStat);
        } catch (RuntimeException e) {
            Assert.assertEquals(ex, e);
        }
        SQLiteDatabase db = storage.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from terminals_stat", null);
        Assert.assertEquals(0, cursor.getCount());
        cursor.close();

        storage.storeTerminalStats(stat, null);
        db = storage.getReadableDatabase();
        cursor = db.rawQuery("select * from terminals_stat", null);
        Assert.assertEquals(1, cursor.getCount());
        Assert.assertEquals(12, cursor.getColumnCount());
        Assert.assertTrue(cursor.moveToNext());
        Assert.assertEquals("TERMINAL_ID0", cursor.getString(0));
        Assert.assertEquals("AGENT_ID0", cursor.getString(1));
        Assert.assertEquals(10000, cursor.getInt(2));
        Assert.assertEquals(20000, cursor.getInt(3));
        Assert.assertEquals(1.20f, cursor.getFloat(4), 0);
        Assert.assertEquals(3.2f, cursor.getFloat(5), 0);
        Assert.assertEquals(2, cursor.getInt(6));
        Assert.assertEquals(100, cursor.getInt(7));
        Assert.assertEquals(4000l, cursor.getLong(8));
        Assert.assertEquals(800l, cursor.getLong(9));
        Assert.assertEquals(5000l, cursor.getLong(10));
        Assert.assertEquals(300l, cursor.getLong(11));
        Assert.assertFalse(cursor.moveToNext());
        cursor.close();
    }

    @Test
    public void getTerminalStatisticsCheck() throws Exception {
        DataStorage storage = new DataStorage(Robolectric.application);
        ContentValues values = new ContentValues();

        SQLiteDatabase db = storage.getWritableDatabase();
        values.put("terminal_id", "TERMINAL_ID0");
        values.put("agent_id", "AGENT_ID0");
        values.put("system_up_time", 1000);
        values.put("up_time", 2000);
        values.put("pays_per_hour", 2.4f);
        values.put("bills_per_pay", 5.8f);
        values.put("card_reader_used_hours", 300);
        values.put("card_reader_used_day", 450);
        values.put("time_to_cachin_full", 1000l);
        values.put("time_to_cachin_service", 2000l);
        values.put("time_to_printer_out", 1500l);
        values.put("time_to_printer_service", 2500l);
        db.replace("terminals_stat", null, values);
        values.put("terminal_id", "TERMINAL_ID1");
        values.put("agent_id", "AGENT_ID1");
        values.put("system_up_time", 1001);
        values.put("up_time", 2001);
        values.put("pays_per_hour", 3.2f);
        values.put("bills_per_pay", 6.1f);
        values.put("card_reader_used_hours", 301);
        values.put("card_reader_used_day", 451);
        values.put("time_to_cachin_full", 1001l);
        values.put("time_to_cachin_service", 2001l);
        values.put("time_to_printer_out", 1501l);
        values.put("time_to_printer_service", 2501l);
        db.replace("terminals_stat", null, values);

        TerminalStats stats[] = storage.getTerminalStats();
        Assert.assertNotNull(stats);
        Assert.assertEquals(2, stats.length);
        TerminalStats stat = stats[0];
        Assert.assertEquals("TERMINAL_ID0", stat.getTerminalId());
        Assert.assertEquals("AGENT_ID0", stat.getAgentId());
        Assert.assertEquals(1000, stat.getSystemUpTime());
        Assert.assertEquals(2000, stat.getUpTime());
        Assert.assertEquals(2.4f, stat.getPaysPerHour(), 0);
        Assert.assertEquals(5.8f, stat.getBillsPerPay(), 0);
        Assert.assertEquals(300, stat.getCardReaderUsedHours());
        Assert.assertEquals(450, stat.getCardReaderUsedDay());
        Assert.assertEquals(1000l, stat.getTimeToCashinFull());
        Assert.assertEquals(2000l, stat.getTimeToCashinService());
        Assert.assertEquals(1500l, stat.getTimeToPrinterPaperOut());
        Assert.assertEquals(2500l, stat.getTimeToPrinterService());
        stat = stats[1];
        Assert.assertEquals("TERMINAL_ID1", stat.getTerminalId());
        Assert.assertEquals("AGENT_ID1", stat.getAgentId());
        Assert.assertEquals(1001, stat.getSystemUpTime());
        Assert.assertEquals(2001, stat.getUpTime());
        Assert.assertEquals(3.2f, stat.getPaysPerHour(), 0);
        Assert.assertEquals(6.1f, stat.getBillsPerPay(), 0);
        Assert.assertEquals(301, stat.getCardReaderUsedHours());
        Assert.assertEquals(451, stat.getCardReaderUsedDay());
        Assert.assertEquals(1001l, stat.getTimeToCashinFull());
        Assert.assertEquals(2001l, stat.getTimeToCashinService());
        Assert.assertEquals(1501l, stat.getTimeToPrinterPaperOut());
        Assert.assertEquals(2501l, stat.getTimeToPrinterService());
    }

    @Test
    public void storeTerminalsCashCheck() throws Exception {
        TerminalCash cash = Mockito.mock(TerminalCash.class);
        TerminalCash fail = Mockito.mock(TerminalCash.class);
        Mockito.when(cash.getTerminalId()).thenReturn("TERMINAL0");
        Mockito.when(cash.getAgentId()).thenReturn("AGENT0");
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] args = invocationOnMock.getArguments();
                DataOutputStream out = (DataOutputStream) args[0];
                out.write(new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
                return null;
            }
        }).when(cash).store(Mockito.any(DataOutputStream.class));
        Mockito.doThrow(new IOException()).when(fail).store(Mockito.any(DataOutputStream.class));

        DataStorage storage = new DataStorage(Robolectric.application);
        storage.storeTerminalsCash(cash, null, fail);

        SQLiteDatabase db = storage.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM terminals_cash", null);
        Assert.assertEquals(1, cursor.getCount());
        cursor.moveToFirst();
        Assert.assertEquals("TERMINAL0", cursor.getString(0));
        Assert.assertEquals("AGENT0", cursor.getString(1));
        Assert.assertArrayEquals(new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, cursor.getBlob(2));
        cursor.close();
    }

    @Test
    public void getTerminalsCashCheck() throws Exception {
        DataStorage storage = new DataStorage(Robolectric.application);

    }
}
