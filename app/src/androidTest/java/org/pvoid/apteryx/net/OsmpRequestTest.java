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

package org.pvoid.apteryx.net;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pvoid.apteryx.data.accounts.Account;
import org.pvoid.apteryx.net.commands.Command;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import okio.Buffer;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class OsmpRequestTest {
    @Test
    public void buildTest() throws Exception {
        final String XML =
            "<?xml version=\"1.0\" encoding=\"windows-1251\"?>" +
            "<request>" +
            "<auth login=\"LOGIN\" signAlg=\"MD5\" sign=\"HASH\"/>" +
            "<client terminal=\"TERMINAL\" software=\"Dealer v0\" serial=\"\"/>" +
            "<terminals>" +
            "<getConfig mode=\"async\"></getConfig>" +
            "<requestProcessList>" +
            "<target-terminal>1111111</target-terminal>" +
            "<e-mail>mail@qiwi.ru</e-mail>" +
            "</requestProcessList>" +
            "</terminals>" +
            "</request>";


        Account account = Mockito.mock(Account.class);
        Mockito.when(account.getLogin()).thenReturn("LOGIN");
        Mockito.when(account.getPasswordHash()).thenReturn("HASH");
        Mockito.when(account.getTerminal()).thenReturn("TERMINAL");

        Command getConfig = Mockito.mock(Command.class);
        Mockito.when(getConfig.getName()).thenReturn("getConfig");
        Mockito.when(getConfig.isAsync()).thenReturn(true);

        Command requestProcessList = Mockito.mock(Command.class);
        Mockito.when(requestProcessList.getName()).thenReturn("requestProcessList");
        Map<String, String> params = new HashMap<String, String>() {{
            put("target-terminal", "1111111");
            put("e-mail", "mail@qiwi.ru");
        }};
        Mockito.when(requestProcessList.getParams()).thenReturn(params);

        OsmpRequest.Builder builder = new OsmpRequest.Builder(account);

        builder.getInterface(OsmpInterface.Terminals).add(getConfig);
        builder.getInterface(OsmpInterface.Terminals).add(requestProcessList);

        OsmpRequest request = builder.create();
        Assert.assertNotNull(request);
        RequestBody body = request.createBody();
        Assert.assertNotNull(body);

        byte[] expected = XML.getBytes("windows-1251");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        buffer.copyTo(out);
        Assert.assertEquals(XML, out.toString("windows-1251"));

        Assert.assertEquals(expected.length, body.contentLength());
        Assert.assertEquals(MediaType.parse("application/xml; charset=windows-1251"), body.contentType());
    }
}
