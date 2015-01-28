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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pvoid.apteryx.data.terminals.Terminal;
import org.pvoid.apteryx.data.terminals.TerminalType;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GetTerminalsResultTest {
    @Test
    public void constructorCheck() throws Exception {
        ResponseTag root = Mockito.mock(ResponseTag.class);
        GetTerminalsResult result = new GetTerminalsResult(root);
        Assert.assertNull(result.getTerminals());

        ResponseTag terminalTag1 = Mockito.mock(ResponseTag.class);
        Mockito.when(terminalTag1.getName()).thenReturn("row");
        Mockito.when(root.nextChild()).thenReturn(terminalTag1, (ResponseTag) null);
        result = new GetTerminalsResult(root);
        Assert.assertNull(result.getTerminals());

        Mockito.when(terminalTag1.getAttribute("trm_id")).thenReturn("TERMINAL0");
        Mockito.when(root.nextChild()).thenReturn(terminalTag1, (ResponseTag) null);
        result = new GetTerminalsResult(root);
        Assert.assertNull(result.getTerminals());
        Mockito.when(terminalTag1.getAttribute("agt_id")).thenReturn("AGENT0");
        Mockito.when(root.nextChild()).thenReturn(terminalTag1, (ResponseTag) null);
        result = new GetTerminalsResult(root);
        Assert.assertNull(result.getTerminals());
        Mockito.when(terminalTag1.getAttribute("ttp_id")).thenReturn("2");
        Mockito.when(root.nextChild()).thenReturn(terminalTag1, (ResponseTag) null);
        result = new GetTerminalsResult(root);
        Assert.assertNull(result.getTerminals());
        Mockito.when(terminalTag1.getAttribute("trm_display")).thenReturn("DISPLAY NAME0");
        Mockito.when(root.nextChild()).thenReturn(terminalTag1, (ResponseTag) null);
        result = new GetTerminalsResult(root);
        Terminal[] terminals = result.getTerminals();
        Assert.assertNotNull(terminals);
        Assert.assertEquals(1, terminals.length);
        Assert.assertEquals("TERMINAL0", terminals[0].getId());
        Assert.assertEquals("AGENT0", terminals[0].getAgentId());
        Assert.assertEquals("DISPLAY NAME0", terminals[0].getDisplayName());
        Assert.assertEquals(TerminalType.Web, terminals[0].getType());

        Mockito.when(terminalTag1.getAttribute("city")).thenReturn("CITY");
        ResponseTag terminalTag2 = Mockito.mock(ResponseTag.class);
        Mockito.when(terminalTag2.getName()).thenReturn("row");
        Mockito.when(terminalTag2.getAttribute("trm_id")).thenReturn("TERMINAL1");
        Mockito.when(terminalTag2.getAttribute("agt_id")).thenReturn("AGENT1");
        Mockito.when(terminalTag2.getAttribute("ttp_id")).thenReturn("3");
        Mockito.when(terminalTag2.getAttribute("trm_display")).thenReturn("DISPLAY NAME1");
        Mockito.when(terminalTag2.getAttribute("trm_city_id")).thenReturn("100");
        Mockito.when(terminalTag2.getAttribute("address")).thenReturn("ADDRESS");

        ResponseTag terminalTag3 = Mockito.mock(ResponseTag.class);
        Mockito.when(terminalTag3.getName()).thenReturn("row");
        Mockito.when(terminalTag3.getAttribute("trm_id")).thenReturn("TERMINAL2");
        Mockito.when(terminalTag3.getAttribute("agt_id")).thenReturn("AGENT2");
        Mockito.when(terminalTag3.getAttribute("ttp_id")).thenReturn("1");
        Mockito.when(terminalTag3.getAttribute("trm_display")).thenReturn("DISPLAY NAME2");
        Mockito.when(terminalTag3.getAttribute("city")).thenReturn("CITY");
        Mockito.when(terminalTag3.getAttribute("trm_city_id")).thenReturn("100");

        ResponseTag terminalTag4 = Mockito.mock(ResponseTag.class);
        Mockito.when(terminalTag4.getName()).thenReturn("row");
        Mockito.when(terminalTag4.getAttribute("trm_id")).thenReturn("TERMINAL3");
        Mockito.when(terminalTag4.getAttribute("agt_id")).thenReturn("AGENT3");
        Mockito.when(terminalTag4.getAttribute("ttp_id")).thenReturn("3");
        Mockito.when(terminalTag4.getAttribute("trm_display")).thenReturn("DISPLAY NAME3");
        Mockito.when(terminalTag4.getAttribute("city")).thenReturn("CITY");
        Mockito.when(terminalTag4.getAttribute("trm_city_id")).thenReturn("a");


        Mockito.when(root.nextChild()).thenReturn(terminalTag1, Mockito.mock(ResponseTag.class), terminalTag2, terminalTag3, terminalTag4, null);
        result = new GetTerminalsResult(root);
        terminals = result.getTerminals();
        Assert.assertNotNull(terminals);
        Assert.assertEquals(4, terminals.length);
        Assert.assertEquals("TERMINAL0", terminals[0].getId());
        Assert.assertEquals("AGENT0", terminals[0].getAgentId());
        Assert.assertEquals("DISPLAY NAME0", terminals[0].getDisplayName());
        Assert.assertEquals(TerminalType.Web, terminals[0].getType());
        Assert.assertNull(terminals[0].getDisplayAddress());
        Assert.assertNull(terminals[0].getCity());
        Assert.assertEquals(0, terminals[0].getCityId());
        Assert.assertEquals("TERMINAL1", terminals[1].getId());
        Assert.assertEquals("AGENT1", terminals[1].getAgentId());
        Assert.assertEquals("DISPLAY NAME1", terminals[1].getDisplayName());
        Assert.assertEquals(TerminalType.Windows, terminals[1].getType());
        Assert.assertEquals("ADDRESS", terminals[1].getDisplayAddress());
        Assert.assertNull(terminals[1].getCity());
        Assert.assertEquals(0, terminals[1].getCityId());
        Assert.assertEquals("TERMINAL2", terminals[2].getId());
        Assert.assertEquals("AGENT2", terminals[2].getAgentId());
        Assert.assertEquals("DISPLAY NAME2", terminals[2].getDisplayName());
        Assert.assertEquals(TerminalType.Linudix, terminals[2].getType());
        Assert.assertNull(terminals[2].getDisplayAddress());
        Assert.assertEquals("CITY", terminals[2].getCity());
        Assert.assertEquals(100, terminals[2].getCityId());
        Assert.assertEquals("TERMINAL3", terminals[3].getId());
        Assert.assertEquals("AGENT3", terminals[3].getAgentId());
        Assert.assertEquals("DISPLAY NAME3", terminals[3].getDisplayName());
        Assert.assertEquals(TerminalType.Windows, terminals[3].getType());
        Assert.assertNull(terminals[3].getDisplayAddress());
        Assert.assertNull(terminals[3].getCity());
        Assert.assertEquals(0, terminals[3].getCityId());

        Mockito.when(root.nextChild()).thenReturn(terminalTag1).thenThrow(new ResponseTag.TagReadException("")).thenReturn(terminalTag2);
        result = new GetTerminalsResult(root);
        terminals = result.getTerminals();
        Assert.assertNotNull(terminals);
        Assert.assertEquals(1, terminals.length);
        Assert.assertEquals("TERMINAL0", terminals[0].getId());
        Assert.assertEquals("AGENT0", terminals[0].getAgentId());
        Assert.assertEquals("DISPLAY NAME0", terminals[0].getDisplayName());
        Assert.assertEquals(TerminalType.Web, terminals[0].getType());
        Assert.assertNull(terminals[0].getDisplayAddress());
        Assert.assertNull(terminals[0].getCity());
        Assert.assertEquals(0, terminals[0].getCityId());
    }
}
