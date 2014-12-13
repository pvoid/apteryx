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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pvoid.apteryx.net.results.ResponseTag;
import org.pvoid.apteryx.net.results.Result;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class OsmpResponseTest {
    @Test
    public void responseParseCheck() throws Exception {
        final String XML =
            "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
            "<response result=\"0\" result-description=\"All OK\">" +
                "<terminals>" +
                    "<action1>data and data</action1>" +
                    "<action2>data and data</action2>" +
                    "<action3>data and data</action3>" +
                "</terminals>" +
                "<unknown>" +
                "</unknown>" +
            "</response>";

        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(new ByteArrayInputStream(XML.getBytes()), "UTF-8");

        ResultFactories factories = Mockito.mock(ResultFactories.class);
        Result result1 = Mockito.mock(Result.class);
        Result result2 = Mockito.mock(Result.class);
        Mockito.when(factories.build(Mockito.any(ResponseTag.class))).thenReturn(result1, null, result2);
        OsmpResponseReader reader = new OsmpResponseReader(parser);
        OsmpResponse response = new OsmpResponse(reader.next(), factories);
        Assert.assertEquals(0, response.getResult());
        Assert.assertEquals("All OK", response.getResultDescription());

        for (OsmpInterface i : OsmpInterface.values()) {
            if (i == OsmpInterface.Terminals) {
                List<Result> results = response.getInterface(i);
                Assert.assertNotNull(results);
                Assert.assertEquals(2, results.size());
                Assert.assertSame(result1, results.get(0));
                Assert.assertSame(result2, results.get(1));
                continue;
            }
            Assert.assertNull("Reesult should be null for: " + i.name(), response.getInterface(i));
        }
    }

    @Test(expected = ResponseTag.TagReadException.class)
    public void invalidRootTagCheck() throws Exception {
        final String XML =
                "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
                "<ooops/>";
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(new ByteArrayInputStream(XML.getBytes()), "UTF-8");

        ResultFactories factories = Mockito.mock(ResultFactories.class);
        OsmpResponseReader reader = new OsmpResponseReader(parser);
        new OsmpResponse(reader.next(), factories);
    }

    @Test(expected = ResponseTag.TagReadException.class)
    public void invalidResultCodeCheck() throws Exception {
        final String XML =
                "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
                "<response result=\"ooops!\"/>";
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(new ByteArrayInputStream(XML.getBytes()), "UTF-8");

        ResultFactories factories = Mockito.mock(ResultFactories.class);
        OsmpResponseReader reader = new OsmpResponseReader(parser);
        new OsmpResponse(reader.next(), factories);
    }

    @Test
    public void emptyResponseCheck() throws Exception {
        final String XML =
                "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
                "<response/>";
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(new ByteArrayInputStream(XML.getBytes()), "UTF-8");

        ResultFactories factories = Mockito.mock(ResultFactories.class);
        OsmpResponseReader reader = new OsmpResponseReader(parser);
        OsmpResponse response = new OsmpResponse(reader.next(), factories);
        Assert.assertNotNull(response);
        Assert.assertEquals(0, response.getResult());
        for (OsmpInterface i : OsmpInterface.values()) {
            Assert.assertNull("Reesult should be null for: " + i.name(), response.getInterface(i));
        }
    }
}
