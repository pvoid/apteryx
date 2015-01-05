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

import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GetTerminalsCommandTest {
    @Test
    public void commandCheck() throws Exception {
        GetTerminalsCommand command = new GetTerminalsCommand(false);
        Assert.assertEquals("getTerminals", command.getName());
        Assert.assertNull(command.getParams());
        Assert.assertFalse(command.isAsync());

        command = new GetTerminalsCommand(true);
        Assert.assertEquals("getTerminals", command.getName());
        Map<String, String> params = command.getParams();
        Assert.assertNotNull(params);
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("1", params.get("trm-state"));
        Assert.assertFalse(command.isAsync());
    }

    @Test
    public void restoreCheck() throws Exception {
        GetTerminalsCommand command = new GetTerminalsCommand(false);
        Assert.assertEquals(0, command.describeContents());
        Parcel parcel = Parcel.obtain();
        command.writeToParcel(parcel, 0);
        int position = parcel.dataPosition();
        parcel.setDataPosition(0);
        command = GetTerminalsCommand.CREATOR.createFromParcel(parcel);
        Assert.assertNotNull(command);
        Assert.assertNull(command.getParams());
        Assert.assertEquals(position, parcel.dataPosition());
        parcel.recycle();

        command = new GetTerminalsCommand(true);
        parcel = Parcel.obtain();
        command.writeToParcel(parcel, 0);
        position = parcel.dataPosition();
        parcel.setDataPosition(0);
        command = GetTerminalsCommand.CREATOR.createFromParcel(parcel);
        Assert.assertNotNull(command);
        Map<String, String> params = command.getParams();
        Assert.assertNotNull(params);
        Assert.assertEquals("1", params.get("trm-state"));
        Assert.assertEquals(position, parcel.dataPosition());
        parcel.recycle();

        GetTerminalsCommand[] commands = GetTerminalsCommand.CREATOR.newArray(5);
        Assert.assertNotNull(commands);
        Assert.assertEquals(5, commands.length);
    }
}
