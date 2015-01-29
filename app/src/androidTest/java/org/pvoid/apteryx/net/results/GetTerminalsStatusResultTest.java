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
import org.pvoid.apteryx.data.terminals.TerminalState;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.TimeZone;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GetTerminalsStatusResultTest {
    @Test
    public void statusParseCheck() throws Exception {
        Assert.assertEquals(0, GetTerminalsStatusResult.parseStateFlags(null));
        Assert.assertEquals(0, GetTerminalsStatusResult.parseStateFlags(""));
        Assert.assertEquals(0, GetTerminalsStatusResult.parseStateFlags("001000"));
        Assert.assertEquals(0, GetTerminalsStatusResult.parseStateFlags("000000000000A00000000000"));
        Assert.assertEquals(TerminalState.FLAG_STATE_ASO_MONITOR_DISABLED | TerminalState.FLAG_STATE_UPDATING_PROVIDERS
                        | TerminalState.FLAG_STATE_DANGEROUS_SOFTWARE | TerminalState.FLAG_STATE_HARDWARE_ERROR,
                GetTerminalsStatusResult.parseStateFlags("100000000000100001000001"));
    }

    @Test
    public void parseDateCheck() throws Exception {
        Assert.assertEquals(-1, GetTerminalsStatusResult.parseDateTime(null));
        Assert.assertEquals(-1, GetTerminalsStatusResult.parseDateTime(""));
        Assert.assertEquals(-1, GetTerminalsStatusResult.parseDateTime("afsdsfsf"));

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+03:00"));
        calendar.set(2015, 0, 29, 22, 54, 48);
        calendar.set(Calendar.MILLISECOND, 0);
        // NOTE: Can't test this on Robolectric
        //Assert.assertEquals(calendar.getTimeInMillis(), GetTerminalsStatusResult.parseDateTime("2015-01-29T22:54:48+03:00"));
    }
}
