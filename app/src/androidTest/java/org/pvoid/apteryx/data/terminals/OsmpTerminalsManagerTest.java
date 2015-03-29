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

package org.pvoid.apteryx.data.terminals;

import android.support.annotation.NonNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pvoid.apteryx.data.Storage;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class OsmpTerminalsManagerTest {
    @Test
    public void terminalsListCheck() throws Exception {
        MockStorageBuilder builder = new MockStorageBuilder();
        OsmpTerminalsManager manager = new OsmpTerminalsManager(Robolectric.application, builder.create());
        Assert.assertNull(manager.getTerminals(null));

        builder.addTerminal("ID0", "AGENT0");
        builder.addTerminal("ID1", "AGENT0");
        builder.addTerminal("ID2", "AGENT0");
        builder.addTerminal("ID3", "AGENT1");
        builder.addTerminal("ID4", "AGENT1");
        manager = new OsmpTerminalsManager(Robolectric.application, builder.create());
        Terminal terminals[] = manager.getTerminals(null);
        Assert.assertNotNull(terminals.length);
        Assert.assertEquals(5, terminals.length);

        terminals = manager.getTerminals("AGENT0");
        Assert.assertNotNull(terminals.length);
        Assert.assertEquals(3, terminals.length);
        Assert.assertEquals("ID0", terminals[0].getId());
        Assert.assertEquals("ID1", terminals[1].getId());
        Assert.assertEquals("ID2", terminals[2].getId());

        terminals = manager.getTerminals("AGENT1");
        Assert.assertNotNull(terminals.length);
        Assert.assertEquals(2, terminals.length);
        Assert.assertEquals("ID3", terminals[0].getId());
        Assert.assertEquals("ID4", terminals[1].getId());

        terminals = manager.getTerminals("AGENT2");
        Assert.assertNull(terminals);

        builder.addState("ID0");
        builder.addState("ID2");
        builder.addState("ID8"); // should skip this value
        builder.addStat("ID0");
        builder.addStat("ID1");
        builder.addStat("ID6");
        builder.addCash("ID1");
        builder.addCash("ID2");
        builder.addCash("ID10");
        manager = new OsmpTerminalsManager(Robolectric.application, builder.create());
        terminals = manager.getTerminals("AGENT0");
        Assert.assertNotNull(terminals[0].getState());
        Assert.assertNotNull(terminals[0].getStats());
        Assert.assertNull(terminals[0].getCash());
        Assert.assertNull(terminals[1].getState());
        Assert.assertNotNull(terminals[1].getStats());
        Assert.assertNotNull(terminals[1].getCash());
        Assert.assertNotNull(terminals[2].getState());
        Assert.assertNull(terminals[2].getStats());
        Assert.assertNotNull(terminals[2].getCash());
    }

    private static class MockStorageBuilder {
        List<Terminal> mTerminals = new ArrayList<>();
        List<TerminalState> mStates = new ArrayList<>();
        List<TerminalCash> mCashes = new ArrayList<>();
        List<TerminalStats> mStats = new ArrayList<>();

        void addTerminal(@NonNull String id, @NonNull String agent) {
            Terminal terminal = Mockito.mock(Terminal.class);
            Mockito.when(terminal.getId()).thenReturn(id);
            Mockito.when(terminal.getAgentId()).thenReturn(agent);
            Mockito.doCallRealMethod().when(terminal).setState(Mockito.any(TerminalState.class));
            Mockito.doCallRealMethod().when(terminal).getState();
            Mockito.doCallRealMethod().when(terminal).setStats(Mockito.any(TerminalStats.class));
            Mockito.doCallRealMethod().when(terminal).getStats();
            Mockito.doCallRealMethod().when(terminal).setCash(Mockito.any(TerminalCash.class));
            Mockito.doCallRealMethod().when(terminal).getCash();
            mTerminals.add(terminal);
        }

        void addState(@NonNull String id) {
            TerminalState state = Mockito.mock(TerminalState.class);
            Mockito.when(state.getId()).thenReturn(id);
            mStates.add(state);
        }

        void addStat(@NonNull String id) {
            TerminalStats stats = Mockito.mock(TerminalStats.class);
            Mockito.when(stats.getTerminalId()).thenReturn(id);
            mStats.add(stats);
        }

        void addCash(@NonNull String id) {
            TerminalCash cash = Mockito.mock(TerminalCash.class);
            Mockito.when(cash.getTerminalId()).thenReturn(id);
            mCashes.add(cash);
        }

        Storage create() {
            Storage result = Mockito.mock(Storage.class);
            if (!mTerminals.isEmpty()) {
                Mockito.when(result.getTerminals()).thenReturn(mTerminals.toArray(new Terminal[mTerminals.size()]));
            }
            if (!mStates.isEmpty()) {
                Mockito.when(result.getTerminalStates()).thenReturn(mStates.toArray(new TerminalState[mStates.size()]));
            }
            if (!mStats.isEmpty()) {
                Mockito.when(result.getTerminalStats()).thenReturn(mStats.toArray(new TerminalStats[mStats.size()]));
            }
            if (!mCashes.isEmpty()) {
                Mockito.when(result.getTerminalsCash()).thenReturn(mCashes.toArray(new TerminalCash[mCashes.size()]));
            }
            return result;
        }
    }
}
