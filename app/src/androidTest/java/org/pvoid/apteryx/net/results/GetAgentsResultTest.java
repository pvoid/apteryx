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
import org.pvoid.apteryx.data.agents.Agent;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GetAgentsResultTest {
    @Test
    public void constructorCheck() throws Exception {
        ResponseTag root = Mockito.mock(ResponseTag.class);
        GetAgentsResult result = new GetAgentsResult(root);
        Assert.assertNull(result.getAgents());

        ResponseTag agentTag1 = Mockito.mock(ResponseTag.class);
        Mockito.when(agentTag1.getName()).thenReturn("row");
        Mockito.when(agentTag1.getAttribute("agt_id")).thenReturn("AGENT0");

        ResponseTag agentTag2 = Mockito.mock(ResponseTag.class);
        Mockito.when(agentTag2.getName()).thenReturn("row");
        Mockito.when(agentTag2.getAttribute("agt_id")).thenReturn("AGENT1");

        ResponseTag notAgentTag = Mockito.mock(ResponseTag.class);
        Mockito.when(notAgentTag.getName()).thenReturn("bar");

        Mockito.when(root.nextChild()).thenReturn(agentTag1, notAgentTag, agentTag2, null);
        result = new GetAgentsResult(root);
        Agent[] agents = result.getAgents();
        Assert.assertNotNull(agents);
        Assert.assertEquals(2, agents.length);
        Assert.assertEquals("AGENT0", agents[0].getId());
        Assert.assertEquals("AGENT1", agents[1].getId());

        Mockito.when(root.nextChild())
                .thenReturn(agentTag1)
                .thenThrow(new ResponseTag.TagReadException(""))
                .thenReturn(agentTag2);
        result = new GetAgentsResult(root);
        agents = result.getAgents();
        Assert.assertNotNull(agents);
        Assert.assertEquals(1, agents.length);
        Assert.assertEquals("AGENT0", agents[0].getId());
    }
}
