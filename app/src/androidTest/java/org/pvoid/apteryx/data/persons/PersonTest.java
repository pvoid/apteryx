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

import android.os.Parcel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class PersonTest {
    @Test
    public void constructorCheck() throws Exception {
        Person person = new Person("LOGIN", "PASS_HASH", "TERMINAL");
        Assert.assertEquals("LOGIN", person.getLogin());
        Assert.assertEquals("PASS_HASH", person.getPasswordHash());
        Assert.assertEquals("TERMINAL", person.getTerminal());
        Assert.assertNull(person.getName());
        Assert.assertNull(person.getAgentId());
        Assert.assertFalse(person.isVerified());
        Assert.assertTrue(person.isEnabled());

        Person verified = person.verify("AGENT_ID", "NAME", false);
        Assert.assertNotSame(person, verified);
        Assert.assertEquals("LOGIN", verified.getLogin());
        Assert.assertEquals("PASS_HASH", verified.getPasswordHash());
        Assert.assertEquals("TERMINAL", verified.getTerminal());
        Assert.assertEquals("AGENT_ID", verified.getAgentId());
        Assert.assertEquals("NAME", verified.getName());
        Assert.assertTrue(verified.isVerified());
        Assert.assertFalse(verified.isEnabled());
    }

    @Test
    public void storeCheck() throws Exception {
        Person person = new Person("LOGIN", "PASS_HASH", "TERMINAL").verify("AGENT_ID", "NAME", false);
        Assert.assertEquals(0, person.describeContents());
        Parcel parcel = Parcel.obtain();
        person.writeToParcel(parcel, 0);
        int position = parcel.dataPosition();
        parcel.setDataPosition(0);
        Person restored = Person.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(position, parcel.dataPosition());
        parcel.recycle();
        Assert.assertEquals(person.getLogin(), restored.getLogin());
        Assert.assertEquals(person.getPasswordHash(), restored.getPasswordHash());
        Assert.assertEquals(person.getTerminal(), restored.getTerminal());
        Assert.assertEquals(person.getAgentId(), restored.getAgentId());
        Assert.assertEquals(person.getName(), restored.getName());
        Assert.assertEquals(person.isVerified(), restored.isVerified());
        Assert.assertEquals(person.isEnabled(), restored.isEnabled());

        person = new Person("LOGIN", "PASS_HASH", "TERMINAL");
        parcel = Parcel.obtain();
        person.writeToParcel(parcel, 0);
        position = parcel.dataPosition();
        parcel.setDataPosition(0);
        restored = Person.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(position, parcel.dataPosition());
        parcel.recycle();
        Assert.assertEquals(person.getLogin(), restored.getLogin());
        Assert.assertEquals(person.getPasswordHash(), restored.getPasswordHash());
        Assert.assertEquals(person.getTerminal(), restored.getTerminal());
        Assert.assertEquals(person.getAgentId(), restored.getAgentId());
        Assert.assertEquals(person.getName(), restored.getName());
        Assert.assertEquals(person.isVerified(), restored.isVerified());
        Assert.assertEquals(person.isEnabled(), restored.isEnabled());

        Person persons[] = Person.CREATOR.newArray(5);
        Assert.assertNotNull(persons);
        Assert.assertEquals(5, persons.length);
    }

    @Test
    public void equalsCheck() throws Exception {
        Person person = new Person("LOGIN", "PASS_HASH", "TERMINAL");

        Assert.assertEquals("LOGIN".hashCode(), person.hashCode());
        Assert.assertTrue(person.equals(person));
        Assert.assertFalse(person.equals(null));
        Assert.assertFalse(person.equals("Some object"));
        Assert.assertTrue(person.equals(person.verify("AGENT_ID", "NAME", false)));
        Assert.assertTrue(person.equals(new Person("LOGIN", "", "")));
        Assert.assertFalse(person.equals(new Person("LOGIN2", "PASS_HASH", "TERMINAL")));
    }
}
