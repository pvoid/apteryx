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
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pvoid.apteryx.data.Storage;
import org.pvoid.apteryx.data.terminals.TerminalsManager;
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
                .registerReceiver(receiver, new IntentFilter(PersonsManager.ACTION_CHANGED));
        Mockito.reset(receiver);
        OsmpPersonsManager manager = new OsmpPersonsManager(Robolectric.application, storage, tm);
        Person[] persons = manager.getPersons();
        Assert.assertNotNull(persons);
        Assert.assertEquals(0, persons.length);
        Mockito.verify(receiver, Mockito.times(1)).onReceive(Mockito.any(Context.class),
                Mockito.eq(new Intent(PersonsManager.ACTION_CHANGED)));

        Mockito.when(storage.getPersons()).thenThrow(new InterruptedException(""));
        Mockito.reset(receiver);
        manager = new OsmpPersonsManager(Robolectric.application, storage, tm);
        persons = manager.getPersons();
        Assert.assertNotNull(persons);
        Assert.assertEquals(0, persons.length);
        Mockito.verify(receiver, Mockito.times(1)).onReceive(Mockito.any(Context.class),
                Mockito.eq(new Intent(PersonsManager.ACTION_CHANGED)));

        Person sourcePersons[] = new Person[0];
        Mockito.reset(storage, receiver);
        Mockito.when(storage.getPersons()).thenReturn(sourcePersons);
        manager = new OsmpPersonsManager(Robolectric.application, storage, tm);
        persons = manager.getPersons();
        Assert.assertNotNull(persons);
        Assert.assertEquals(0, persons.length);
        Mockito.verify(receiver, Mockito.times(1)).onReceive(Mockito.any(Context.class),
                Mockito.eq(new Intent(PersonsManager.ACTION_CHANGED)));

        sourcePersons = new Person[] {
            Mockito.mock(Person.class), Mockito.mock(Person.class), Mockito.mock(Person.class)
        };
        Mockito.reset(receiver);
        Mockito.when(sourcePersons[0].getLogin()).thenReturn("LOGIN0");
        Mockito.when(sourcePersons[1].getLogin()).thenReturn("LOGIN1");
        Mockito.when(sourcePersons[2].getLogin()).thenReturn("LOGIN2");
        Mockito.when(storage.getPersons()).thenReturn(sourcePersons);
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
                Mockito.eq(new Intent(PersonsManager.ACTION_CHANGED)));
    }

    @Test
    public void personAddCheck() throws Exception {
        Storage storage = Mockito.mock(Storage.class);
        TerminalsManager tm = Mockito.mock(TerminalsManager.class);
        OsmpPersonsManager manager = new OsmpPersonsManager(Robolectric.application, storage, tm);
        BroadcastReceiver receiver = Mockito.mock(BroadcastReceiver.class);
        LocalBroadcastManager.getInstance(Robolectric.application)
                .registerReceiver(receiver, new IntentFilter(PersonsManager.ACTION_CHANGED));
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
                Mockito.eq(new Intent(PersonsManager.ACTION_CHANGED)));

        Mockito.reset(storage, receiver);
        Assert.assertFalse(manager.add(person));
        Mockito.verify(storage, Mockito.never()).storePerson(Mockito.same(person));
        Mockito.verify(receiver, Mockito.never()).onReceive(Mockito.any(Context.class),
                Mockito.eq(new Intent(PersonsManager.ACTION_CHANGED)));

    }
}
