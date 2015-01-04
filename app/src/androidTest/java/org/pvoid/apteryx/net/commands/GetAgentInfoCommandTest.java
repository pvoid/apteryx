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

package org.pvoid.apteryx.net.commands;

import android.os.Parcel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GetAgentInfoCommandTest {
    @Test
    public void commandCheck() throws Exception {
        Command command = new GetAgentInfoCommand();
        Assert.assertEquals("getAgentInfo", command.getName());
        Assert.assertNull(command.getParams());
        Assert.assertFalse(command.isAsync());
    }

    @Test
    public void parcelCheck() throws Exception {
        Command command = new GetAgentInfoCommand();
        Parcel parcel = Parcel.obtain();
        Assert.assertEquals(0, command.describeContents());
        command.writeToParcel(parcel, command.describeContents());
        int position = parcel.dataPosition();
        parcel.setDataPosition(0);
        Command result = GetAgentInfoCommand.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(position, parcel.dataPosition());
        GetAgentInfoCommand commands[] = GetAgentInfoCommand.CREATOR.newArray(5);
        Assert.assertEquals(5, commands.length);
    }
}
