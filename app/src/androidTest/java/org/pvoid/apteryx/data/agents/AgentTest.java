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

package org.pvoid.apteryx.data.agents;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pvoid.apteryx.data.persons.Person;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class AgentTest {
    @Test
    public void constructAndCloneChech() throws Exception {
        Agent agent = new Agent("ID", "PARENT_ID", "INN", "JUR_ADDRESS", "PHYS_ADDRESS", "NAME",
                "CITY", "FISCAL_MODE", "KMM", "TAX");
        Assert.assertEquals("ID", agent.getId());
        Assert.assertEquals("PARENT_ID", agent.getParentId());
        Assert.assertEquals("INN", agent.getINN());
        Assert.assertEquals("JUR_ADDRESS", agent.getJurAddress());
        Assert.assertEquals("PHYS_ADDRESS", agent.getPhysAddress());
        Assert.assertEquals("NAME", agent.getName());
        Assert.assertEquals("CITY", agent.getCity());
        Assert.assertEquals("FISCAL_MODE", agent.getFiscalMode());
        Assert.assertEquals("KMM", agent.getKMM());
        Assert.assertEquals("TAX", agent.getTaxRegnum());
        Assert.assertNull(agent.getPersonLogin());
        Person person = Mockito.mock(Person.class);
        Mockito.when(person.getLogin()).thenReturn("PERSON_LOGIN");
        Agent clone = agent.cloneForPerson(person);
        Assert.assertNotSame(agent, clone);
        Assert.assertEquals("ID", clone.getId());
        Assert.assertEquals("PARENT_ID", clone.getParentId());
        Assert.assertEquals("INN", clone.getINN());
        Assert.assertEquals("JUR_ADDRESS", clone.getJurAddress());
        Assert.assertEquals("PHYS_ADDRESS", clone.getPhysAddress());
        Assert.assertEquals("NAME", clone.getName());
        Assert.assertEquals("CITY", clone.getCity());
        Assert.assertEquals("FISCAL_MODE", clone.getFiscalMode());
        Assert.assertEquals("KMM", clone.getKMM());
        Assert.assertEquals("TAX", clone.getTaxRegnum());
        Assert.assertEquals("PERSON_LOGIN", clone.getPersonLogin());
    }

    @Test
    public void equalsCheck() throws Exception {
        Agent agent = new Agent("ID", "PARENT_ID", "INN", "JUR_ADDRESS", "PHYS_ADDRESS", "NAME",
                "CITY", "FISCAL_MODE", "KMM", "TAX");
        //noinspection ObjectEqualsNull
        Assert.assertFalse(agent.equals(null));
        Assert.assertFalse(agent.equals(new Object()));
        Assert.assertFalse(agent.equals(new Agent("ID2", "PARENT_ID", "INN", "JUR_ADDRESS",
                "PHYS_ADDRESS", "NAME", "CITY", "FISCAL_MODE", "KMM", "TAX")));
        Assert.assertTrue(agent.equals(new Agent("ID", "PARENT_ID1", "INN1", "JUR_ADDRESS1",
                "PHYS_ADDRESS1", "NAME1", "CITY1", "FISCAL_MODE1", "KMM1", "TAX1")));
        Assert.assertTrue(agent.equals(agent));
        Assert.assertEquals("ID".hashCode(), agent.hashCode());
    }
}
