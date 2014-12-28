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

package org.pvoid.apteryx.net.results;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GetAgentInfoResultTest {
    @Test
    public void constructorCheck() throws Exception {
        ResponseTag tag = Mockito.mock(ResponseTag.class);
        Mockito.when(tag.getName()).thenReturn("getAgentInfo");
        GetAgentInfoResult result = new GetAgentInfoResult(tag);
        Assert.assertNull(result.getAgentAddress());
        Assert.assertNull(result.getAgentContactEmail());
        Assert.assertNull(result.getAgentContactPhone());
        Assert.assertNull(result.getAgentFIO());
        Assert.assertNull(result.getAgentId());
        Assert.assertNull(result.getAgentInfo());
        Assert.assertNull(result.getAgentINN());
        Assert.assertNull(result.getAgentName());
        Assert.assertNull(result.getAgentPhone());
        Assert.assertNull(result.getAgentUrl());

        Mockito.when(tag.nextChild()).thenThrow(new ResponseTag.TagReadException(""));
        result = new GetAgentInfoResult(tag);
        Assert.assertNull(result.getAgentAddress());
        Assert.assertNull(result.getAgentContactEmail());
        Assert.assertNull(result.getAgentContactPhone());
        Assert.assertNull(result.getAgentFIO());
        Assert.assertNull(result.getAgentId());
        Assert.assertNull(result.getAgentInfo());
        Assert.assertNull(result.getAgentINN());
        Assert.assertNull(result.getAgentName());
        Assert.assertNull(result.getAgentPhone());
        Assert.assertNull(result.getAgentUrl());

        ResponseTag bold = Mockito.mock(ResponseTag.class);
        Mockito.when(bold.getName()).thenReturn("bold");
        Mockito.reset(tag);
        Mockito.when(tag.getName()).thenReturn("getAgentInfo");
        Mockito.when(tag.nextChild()).thenReturn(bold);
        result = new GetAgentInfoResult(tag);
        Assert.assertNull(result.getAgentAddress());
        Assert.assertNull(result.getAgentContactEmail());
        Assert.assertNull(result.getAgentContactPhone());
        Assert.assertNull(result.getAgentFIO());
        Assert.assertNull(result.getAgentId());
        Assert.assertNull(result.getAgentInfo());
        Assert.assertNull(result.getAgentINN());
        Assert.assertNull(result.getAgentName());
        Assert.assertNull(result.getAgentPhone());
        Assert.assertNull(result.getAgentUrl());

        ResponseTag agent = Mockito.mock(ResponseTag.class);
        Mockito.when(agent.getName()).thenReturn("agent");
        Mockito.when(agent.getAttribute("address")).thenReturn("Улица, дом, корпус");
        Mockito.when(agent.getAttribute("fio")).thenReturn("Горшкова Оксана Сергеевна");
        Mockito.when(agent.getAttribute("id")).thenReturn("2");
        Mockito.when(agent.getAttribute("info")).thenReturn(" ");
        Mockito.when(agent.getAttribute("inn")).thenReturn("123");
        Mockito.when(agent.getAttribute("name")).thenReturn("ООО Валсент Ладога");
        Mockito.when(agent.getAttribute("phone")).thenReturn("89010000000");
        Mockito.when(agent.getAttribute("www")).thenReturn("server.com");
        Mockito.when(agent.getAttribute("cnt-phone")).thenReturn("89010000001");
        Mockito.when(agent.getAttribute("cnt-email")).thenReturn("mail@server.com");
        Mockito.when(tag.nextChild()).thenReturn(agent);
        result = new GetAgentInfoResult(tag);
        Assert.assertEquals("Улица, дом, корпус", result.getAgentAddress());
        Assert.assertEquals("mail@server.com", result.getAgentContactEmail());
        Assert.assertEquals("89010000001", result.getAgentContactPhone());
        Assert.assertEquals("Горшкова Оксана Сергеевна", result.getAgentFIO());
        Assert.assertEquals("2", result.getAgentId());
        Assert.assertEquals(" ", result.getAgentInfo());
        Assert.assertEquals("123", result.getAgentINN());
        Assert.assertEquals("ООО Валсент Ладога", result.getAgentName());
        Assert.assertEquals("89010000000", result.getAgentPhone());
        Assert.assertEquals("server.com", result.getAgentUrl());
    }
}
