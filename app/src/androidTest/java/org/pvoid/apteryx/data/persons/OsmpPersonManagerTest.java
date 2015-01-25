/*
 * Copyright (C) 2010-2015  Dmitry "PVOID" Petuhov
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

package org.pvoid.apteryx.data.persons;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pvoid.apteryx.data.Storage;
import org.pvoid.apteryx.data.agents.Agent;
import org.pvoid.apteryx.data.terminals.TerminalsManager;
import org.pvoid.apteryx.net.NetworkService;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class OsmpPersonManagerTest {
    @Test
    public void constructorCheck() throws Exception {
        Storage storage = Mockito.mock(Storage.class);
        TerminalsManager tm = Mockito.mock(TerminalsManager.class);
        BroadcastReceiver receiver = Mockito.mock(BroadcastReceiver.class);
        LocalBroadcastManager.getInstance(Robolectric.application)
                .registerReceiver(receiver, new IntentFilter(PersonsManager.ACTION_PERSONS_CHANGED));
        Mockito.reset(receiver);
        OsmpPersonsManager manager = new OsmpPersonsManager(Robolectric.application, storage, tm);
        Person[] persons = manager.getPersons();
        Assert.assertNotNull(persons);
        Assert.assertNull(manager.getAgents("ANY"));
        Assert.assertEquals(0, persons.length);
        Mockito.verify(receiver, Mockito.times(1)).onReceive(Mockito.any(Context.class),
                Mockito.eq(new Intent(PersonsManager.ACTION_PERSONS_CHANGED)));

        Mockito.when(storage.getPersons()).thenThrow(new InterruptedException(""));
        Mockito.reset(receiver);
        manager = new OsmpPersonsManager(Robolectric.application, storage, tm);
        persons = manager.getPersons();
        Assert.assertNotNull(persons);
        Assert.assertEquals(0, persons.length);
        Assert.assertNull(manager.getAgents("ANY"));
        Mockito.verify(receiver, Mockito.times(1)).onReceive(Mockito.any(Context.class),
                Mockito.eq(new Intent(PersonsManager.ACTION_PERSONS_CHANGED)));

        Person sourcePersons[] = new Person[0];
        Agent sourceAgents[] = new Agent[0];
        Mockito.reset(storage, receiver);
        Mockito.when(storage.getPersons()).thenReturn(sourcePersons);
        Mockito.when(storage.getAgents()).thenReturn(sourceAgents);
        manager = new OsmpPersonsManager(Robolectric.application, storage, tm);
        persons = manager.getPersons();
        Assert.assertNotNull(persons);
        Assert.assertEquals(0, persons.length);
        Assert.assertNull(manager.getAgents("ANY"));
        Mockito.verify(receiver, Mockito.times(1)).onReceive(Mockito.any(Context.class),
                Mockito.eq(new Intent(PersonsManager.ACTION_PERSONS_CHANGED)));

        sourcePersons = new Person[] {
            Mockito.mock(Person.class), Mockito.mock(Person.class), Mockito.mock(Person.class)
        };
        Mockito.reset(receiver);
        Mockito.when(sourcePersons[0].getLogin()).thenReturn("LOGIN0");
        Mockito.when(sourcePersons[1].getLogin()).thenReturn("LOGIN1");
        Mockito.when(sourcePersons[2].getLogin()).thenReturn("LOGIN2");
        sourceAgents = new Agent[] {
            Mockito.mock(Agent.class), Mockito.mock(Agent.class), Mockito.mock(Agent.class)
        };
        Mockito.when(sourceAgents[0].getId()).thenReturn("0");
        Mockito.when(sourceAgents[0].getPersonLogin()).thenReturn("LOGIN0");
        Mockito.when(sourceAgents[1].getId()).thenReturn("11");
        Mockito.when(sourceAgents[1].getPersonLogin()).thenReturn("LOGIN2");
        Mockito.when(sourceAgents[2].getId()).thenReturn("10");
        Mockito.when(sourceAgents[2].getPersonLogin()).thenReturn("LOGIN0");

        Mockito.when(storage.getPersons()).thenReturn(sourcePersons);
        Mockito.when(storage.getAgents()).thenReturn(sourceAgents);
        manager = new OsmpPersonsManager(Robolectric.application, storage, tm);
        persons = manager.getPersons();
        Assert.assertNotNull(persons);
        Assert.assertNotSame(sourcePersons, persons);
        Assert.assertEquals(3, persons.length);
        Assert.assertSame(sourcePersons[0], persons[0]);
        Assert.assertSame(sourcePersons[1], persons[1]);
        Assert.assertSame(sourcePersons[2], persons[2]);
        Assert.assertSame(persons, manager.getPersons());
        Mockito.verify(receiver, Mockito.times(1)).onReceive(Mockito.any(Context.class),
                Mockito.eq(new Intent(PersonsManager.ACTION_PERSONS_CHANGED)));
        Assert.assertNull(manager.getAgents("ANY"));
        Agent[] agents = manager.getAgents("LOGIN0");
        Assert.assertNotNull(agents);
        Assert.assertEquals(2, agents.length);
        Assert.assertSame(sourceAgents[0], agents[0]);
        Assert.assertSame(sourceAgents[2], agents[1]);
        Assert.assertNull(manager.getAgents("LOGIN1"));
        agents = manager.getAgents("LOGIN2");
        Assert.assertNotNull(agents);
        Assert.assertEquals(1, agents.length);
        Assert.assertSame(sourceAgents[1], agents[0]);
    }

    @Test
    public void personAddCheck() throws Exception {
        Storage storage = Mockito.mock(Storage.class);
        TerminalsManager tm = Mockito.mock(TerminalsManager.class);
        OsmpPersonsManager manager = new OsmpPersonsManager(Robolectric.application, storage, tm);
        BroadcastReceiver receiver = Mockito.mock(BroadcastReceiver.class);
        LocalBroadcastManager.getInstance(Robolectric.application)
                .registerReceiver(receiver, new IntentFilter(PersonsManager.ACTION_PERSONS_CHANGED));
        Person person = Mockito.mock(Person.class);
        Mockito.when(person.getLogin()).thenReturn("LOGIN");
        Mockito.reset(storage);
        Person[] persons = manager.getPersons();
        Assert.assertNotNull(persons);
        Assert.assertEquals(0, persons.length);
        Assert.assertTrue(manager.add(person));
        Assert.assertNotSame(persons, manager.getPersons());
        persons = manager.getPersons();
        Assert.assertEquals(1, persons.length);
        Assert.assertSame(person, persons[0]);
        Mockito.verify(storage, Mockito.only()).storePerson(Mockito.same(person));
        Mockito.verify(receiver, Mockito.times(1)).onReceive(Mockito.any(Context.class),
                Mockito.eq(new Intent(PersonsManager.ACTION_PERSONS_CHANGED)));

        Mockito.reset(storage, receiver);
        Assert.assertFalse(manager.add(person));
        Mockito.verify(storage, Mockito.never()).storePerson(Mockito.same(person));
        Mockito.verify(receiver, Mockito.never()).onReceive(Mockito.any(Context.class),
                Mockito.eq(new Intent(PersonsManager.ACTION_PERSONS_CHANGED)));

        Person p = manager.getPerson("LOGIN");
        Assert.assertSame(person, p);
    }

    @Test
    public void personVerifyCheck() throws Exception {
        Storage storage = Mockito.mock(Storage.class);
        TerminalsManager tm = Mockito.mock(TerminalsManager.class);
        OsmpPersonsManager manager = new OsmpPersonsManager(Robolectric.application, storage, tm);
        Person person = Mockito.mock(Person.class);
        Mockito.when(person.getLogin()).thenReturn("LOGIN");
        Mockito.when(person.getPasswordHash()).thenReturn("PASSWORD");
        Mockito.when(person.getTerminal()).thenReturn("TERMINAL");
        manager.verify(person);

        Intent intent = Robolectric.getShadowApplication().getNextStartedService();
        Assert.assertNotNull(intent);
        Assert.assertEquals(new ComponentName(Robolectric.application, NetworkService.class),
                intent.getComponent());
    }

    @Test
    public void currentPersonCheck() throws Exception {
        Storage storage = Mockito.mock(Storage.class);
        TerminalsManager tm = Mockito.mock(TerminalsManager.class);
        BroadcastReceiver receiver = Mockito.mock(BroadcastReceiver.class);
        LocalBroadcastManager.getInstance(Robolectric.application)
                .registerReceiver(receiver, new IntentFilter(PersonsManager.ACTION_CURRENT_PERSON_CHANGED));
        OsmpPersonsManager manager = new OsmpPersonsManager(Robolectric.application, storage, tm);
        Assert.assertNull(manager.getCurrentPerson());

        Person[] persons = new Person[] {
            Mockito.mock(Person.class), Mockito.mock(Person.class), Mockito.mock(Person.class)
        };
        Mockito.when(persons[0].getLogin()).thenReturn("LOGIN0");
        Mockito.when(persons[1].getLogin()).thenReturn("LOGIN1");
        Mockito.when(persons[2].getLogin()).thenReturn("LOGIN2");
        Mockito.when(storage.getPersons()).thenReturn(persons);

        manager = new OsmpPersonsManager(Robolectric.application, storage, tm);

        Person person = manager.getCurrentPerson();
        Assert.assertNotNull(person);
        Assert.assertSame(persons[0], person);
        Assert.assertSame(person, manager.getCurrentPerson());

        Mockito.reset(receiver);
        manager.setCurrentPerson("LOGIN100");
        Assert.assertSame(person, manager.getCurrentPerson());
        Mockito.verify(receiver, Mockito.never()).onReceive(Mockito.any(Context.class), Mockito.any(Intent.class));

        Mockito.reset(receiver);
        manager.setCurrentPerson("LOGIN0");
        Assert.assertSame(person, manager.getCurrentPerson());
        Mockito.verify(receiver, Mockito.never()).onReceive(Mockito.any(Context.class), Mockito.any(Intent.class));

        Mockito.reset(receiver);
        manager.setCurrentPerson("LOGIN1");
        Assert.assertSame(persons[1], manager.getCurrentPerson());
        Mockito.verify(receiver, Mockito.times(1)).onReceive(Mockito.any(Context.class), Mockito.any(Intent.class));
    }

    @Test
    public void currentAgentCheck() throws Exception {
        Storage storage = Mockito.mock(Storage.class);
        TerminalsManager tm = Mockito.mock(TerminalsManager.class);
        BroadcastReceiver receiver = Mockito.mock(BroadcastReceiver.class);
        LocalBroadcastManager.getInstance(Robolectric.application)
                .registerReceiver(receiver, new IntentFilter(PersonsManager.ACTION_CURRENT_AGENT_CHANGED));
        OsmpPersonsManager manager = new OsmpPersonsManager(Robolectric.application, storage, tm);
        Assert.assertNull(manager.getCurrentAgent());

        Person[] persons = new Person[] {
            Mockito.mock(Person.class), Mockito.mock(Person.class), Mockito.mock(Person.class)
        };
        Agent[] agents = new Agent[] {
            Mockito.mock(Agent.class), Mockito.mock(Agent.class), Mockito.mock(Agent.class)
        };
        Mockito.when(persons[0].getLogin()).thenReturn("LOGIN0");
        Mockito.when(persons[0].getAgentId()).thenReturn("AGENT0");
        Mockito.when(persons[1].getLogin()).thenReturn("LOGIN1");
        Mockito.when(persons[1].getAgentId()).thenReturn("AGENT1");
        Mockito.when(persons[2].getLogin()).thenReturn("LOGIN2");
        Mockito.when(persons[2].getAgentId()).thenReturn("AGENT3");
        Mockito.when(storage.getPersons()).thenReturn(persons);

        Mockito.when(agents[1].getId()).thenReturn("AGENT0");
        Mockito.when(agents[1].getPersonLogin()).thenReturn("LOGIN0");
        Mockito.when(agents[0].getId()).thenReturn("AGENT2");
        Mockito.when(agents[0].getPersonLogin()).thenReturn("LOGIN2");
        Mockito.when(agents[2].getId()).thenReturn("AGENT8");
        Mockito.when(agents[2].getPersonLogin()).thenReturn("LOGIN0");

        manager = new OsmpPersonsManager(Robolectric.application, storage, tm);
        Assert.assertNull(manager.getCurrentAgent());

        Mockito.when(storage.getAgents()).thenReturn(agents);
        manager = new OsmpPersonsManager(Robolectric.application, storage, tm);
        Agent agent = manager.getCurrentAgent();
        Assert.assertSame(agents[1], agent);
        Assert.assertSame(agent, manager.getCurrentAgent());

        manager.setCurrentPerson("LOGIN1");
        Assert.assertNull(manager.getCurrentAgent());
        manager.setCurrentPerson("LOGIN2");
        Assert.assertNull(manager.getCurrentAgent());

        manager.setCurrentPerson("LOGIN0");

        Mockito.reset(receiver);
        manager.setCurrentAgent("AGENT8");
        agent = manager.getCurrentAgent();
        Assert.assertSame(agents[2], agent);
        Mockito.verify(receiver, Mockito.times(1)).onReceive(Mockito.any(Context.class), Mockito.any(Intent.class));

        Mockito.reset(receiver);
        manager.setCurrentAgent("AGENT3");
        agent = manager.getCurrentAgent();
        Assert.assertSame(agents[2], agent);
        Mockito.verify(receiver, Mockito.never()).onReceive(Mockito.any(Context.class), Mockito.any(Intent.class));

        manager.setCurrentPerson("LOGIN1");
        Mockito.reset(receiver);
        manager.setCurrentAgent("AGENT3");
        Assert.assertNull(manager.getCurrentAgent());
        Mockito.verify(receiver, Mockito.never()).onReceive(Mockito.any(Context.class), Mockito.any(Intent.class));
    }
}
