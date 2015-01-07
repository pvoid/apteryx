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

package org.pvoid.apteryx.net.results;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GetPersonInfoResultTest {
    @Test
    public void constructorCheck() throws Exception {
        ResponseTag personTag = Mockito.mock(ResponseTag.class);
        Mockito.when(personTag.getName()).thenReturn("person");
        Mockito.when(personTag.getAttribute("id")).thenReturn("ID");
        Mockito.when(personTag.getAttribute("login")).thenReturn("LOGIN");
        Mockito.when(personTag.getAttribute("name")).thenReturn("NAME");
        Mockito.when(personTag.getAttribute("agent")).thenReturn("AGENT_ID");
        Mockito.when(personTag.getAttribute("enabled")).thenReturn("true");
        ResponseTag personInfoTag = Mockito.mock(ResponseTag.class);
        Mockito.when(personInfoTag.getName()).thenReturn("getPersonInfo");
        Mockito.when(personInfoTag.nextChild()).thenReturn(personTag);

        GetPersonInfoResult result = new GetPersonInfoResult(personInfoTag);
        Assert.assertEquals("ID", result.getId());
        Assert.assertEquals("LOGIN", result.getLogin());
        Assert.assertEquals("NAME", result.getPersonName());
        Assert.assertEquals("AGENT_ID", result.getAgentId());
        Assert.assertTrue(result.isEnabled());

        Mockito.when(personInfoTag.nextChild()).thenReturn(null);
        result = new GetPersonInfoResult(personInfoTag);
        Assert.assertNull(result.getId());
        Assert.assertNull(result.getLogin());
        Assert.assertNull(result.getPersonName());
        Assert.assertNull(result.getAgentId());
        Assert.assertFalse(result.isEnabled());

        ResponseTag invalid = Mockito.mock(ResponseTag.class);
        Mockito.when(invalid.getName()).thenReturn("invalid");
        Mockito.when(personInfoTag.nextChild()).thenReturn(invalid);
        result = new GetPersonInfoResult(personInfoTag);
        Assert.assertNull(result.getId());
        Assert.assertNull(result.getLogin());
        Assert.assertNull(result.getPersonName());
        Assert.assertNull(result.getAgentId());
        Assert.assertFalse(result.isEnabled());

        Mockito.when(personInfoTag.nextChild()).thenThrow(new ResponseTag.TagReadException("Boo!"));
        result = new GetPersonInfoResult(personInfoTag);
        Assert.assertNull(result.getId());
        Assert.assertNull(result.getLogin());
        Assert.assertNull(result.getPersonName());
        Assert.assertNull(result.getAgentId());
        Assert.assertFalse(result.isEnabled());
    }
}
