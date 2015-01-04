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

import android.os.Parcel;
import android.support.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pvoid.apteryx.net.commands.Command;
import org.pvoid.apteryx.net.results.ResponseTag;
import org.pvoid.apteryx.net.results.Result;
import org.pvoid.apteryx.net.results.ResultFactory;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Mockito.when(result1.getName()).thenReturn("result1");
        Result result2 = Mockito.mock(Result.class);
        Mockito.when(result2.getName()).thenReturn("result2");
        Mockito.when(factories.build(Mockito.any(ResponseTag.class))).thenReturn(result1, null, result2);
        OsmpResponseReader reader = new OsmpResponseReader(parser);
        OsmpResponse response = new OsmpResponse(reader.next(), factories);
        Assert.assertEquals(0, response.getResult());
        Assert.assertEquals("All OK", response.getResultDescription());

        for (OsmpInterface i : OsmpInterface.values()) {
            if (i == OsmpInterface.Terminals) {
                OsmpResponse.Results results = response.getInterface(i);
                Assert.assertNotNull(results);
                Assert.assertEquals(2, results.size());
                Assert.assertSame(result1, results.get("result1"));
                Assert.assertSame(result2, results.get("result2"));
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

    @Test
    public void asyncResponseCheck() throws Exception {
        final String XML =
            "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
            "<response result=\"0\">" +
                "<terminals>" +
                    "<checkMessages result=\"0\"/>" +
                "</terminals>" +
                "<agents>" +
                    "<getAgentInfo result=\"0\" quid=\"20\" status=\"1\"/>" +
                "</agents>" +
            "</response>";
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(new ByteArrayInputStream(XML.getBytes()), "UTF-8");
        OsmpResponseReader reader = new OsmpResponseReader(parser);
        OsmpResponse response = new OsmpResponse(reader.next(), new ResultFactories() {
            @Override
            public void register(@NonNull String tagName, @NonNull ResultFactory factory) {

            }

            @Override
            public Result build(@NonNull ResponseTag tag) {
                return new Result(tag) {};
            }
        });
        Assert.assertTrue(response.hasAsyncResponse());
        // command fill check
        final List<Command> commands = new ArrayList<>();
        OsmpRequest.Builder builder = Mockito.mock(OsmpRequest.Builder.class);
        Mockito.when(builder.getInterface(OsmpInterface.Agents)).thenReturn(new OsmpRequest.CommandsList() {
            @Override
            public boolean add(Command command) {
                return commands.add(command);
            }
        });
        response.fillAsyncRequest(builder);
        for (OsmpInterface i : OsmpInterface.values()) {
            if (i == OsmpInterface.Agents) {
                continue;
            }
            Mockito.verify(builder, Mockito.never()).getInterface(Mockito.eq(i));
        }
        Assert.assertEquals(1, commands.size());
        Command command = commands.get(0);
        Assert.assertNotNull(command);
        Assert.assertEquals("getAgentInfo", command.getName());
        Assert.assertFalse(command.isAsync());
        Map<String, String> params = command.getParams();
        Assert.assertNotNull(params);
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("20", params.get("quid"));

        final String XML_UPDATED =
                "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
                "<response result=\"0\">" +
                    "<agents>" +
                        "<getAgentInfo result=\"0\" quid=\"20\" status=\"3\"/>" +
                    "</agents>" +
                "</response>";
        parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(new ByteArrayInputStream(XML_UPDATED.getBytes()), "UTF-8");
        reader = new OsmpResponseReader(parser);
        final Result result = Mockito.mock(Result.class);
        Mockito.when(result.getName()).thenReturn("getAgentInfo");
        Mockito.when(result.isPending()).thenReturn(true);
        OsmpResponse updated = new OsmpResponse(reader.next(), new ResultFactories() {
            @Override
            public void register(@NonNull String tagName, @NonNull ResultFactory factory) {

            }

            @Override
            public Result build(@NonNull ResponseTag tag) {
                return result;
            }
        });
        response.update(updated);
        Assert.assertTrue(response.hasAsyncResponse());
        Mockito.when(result.isPending()).thenReturn(false);
        response.update(updated);
        Assert.assertFalse(response.hasAsyncResponse());
    }

    @Test
    public void restoreCheck() throws Exception {
        ResponseTag agents = Mockito.mock(ResponseTag.class);
        Mockito.when(agents.getName()).thenReturn("agents");
        ResponseTag getAgent = Mockito.mock(ResponseTag.class);
        Mockito.when(getAgent.getName()).thenReturn("getAgent");
        Mockito.when(agents.nextChild()).thenReturn(getAgent, (ResponseTag) null);

        ResponseTag terminals = Mockito.mock(ResponseTag.class);
        Mockito.when(terminals.getName()).thenReturn("terminals");
        ResponseTag getTerminals = Mockito.mock(ResponseTag.class);
        Mockito.when(getTerminals.getName()).thenReturn("getTerminals");
        ResponseTag getTerminalInfo = Mockito.mock(ResponseTag.class);
        Mockito.when(getTerminalInfo.getName()).thenReturn("getTerminalInfo");
        Mockito.when(terminals.nextChild()).thenReturn(getTerminals, getTerminalInfo, null);

        ResponseTag responseTag = Mockito.mock(ResponseTag.class);
        Mockito.when(responseTag.getAttribute("result")).thenReturn("10");
        Mockito.when(responseTag.getAttribute("result-description")).thenReturn("DESCRIPTION");
        Mockito.when(responseTag.getName()).thenReturn("response");
        Mockito.when(responseTag.nextChild()).thenReturn(agents, terminals, null);

        OsmpResponse response = new OsmpResponse(responseTag, new ResultFactories() {
            @Override
            public void register(@NonNull String tagName, @NonNull ResultFactory factory) {
            }

            @Override
            public Result build(@NonNull ResponseTag tag) {
                return new MockResult(tag);
            }
        });

        Assert.assertEquals(0, response.describeContents());
        Parcel parcel = Parcel.obtain();
        response.writeToParcel(parcel, 0);
        int position = parcel.dataPosition();
        parcel.setDataPosition(0);
        OsmpResponse restored = OsmpResponse.CREATOR.createFromParcel(parcel);
        Assert.assertNotNull(restored);
        Assert.assertEquals(position, parcel.dataPosition());
        parcel.recycle();

        Assert.assertNull(restored.getInterface(OsmpInterface.System));
        Assert.assertNull(restored.getInterface(OsmpInterface.Providers));
        Assert.assertNull(restored.getInterface(OsmpInterface.Persons));
        Assert.assertNull(restored.getInterface(OsmpInterface.Reports));
        Assert.assertNull(restored.getInterface(OsmpInterface.AccountingReports));

        OsmpResponse.Results results = restored.getInterface(OsmpInterface.Agents);
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertNotNull(results.get("getAgent"));

        results = restored.getInterface(OsmpInterface.Terminals);
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
        Assert.assertNotNull(results.get("getTerminals"));
        Assert.assertNotNull(results.get("getTerminalInfo"));

        Assert.assertEquals(10, restored.getResult());
        Assert.assertEquals("DESCRIPTION", restored.getResultDescription());

        OsmpResponse responses[] = OsmpResponse.CREATOR.newArray(5);
        Assert.assertNotNull(responses);
        Assert.assertEquals(5, responses.length);
    }

    public static class MockResult extends Result {
        public MockResult(@NonNull ResponseTag root) {
            super(root);
        }

        protected MockResult(@NonNull Parcel source) {
            super(source);
        }

        public static final Creator<MockResult> CREATOR = new Creator<MockResult>() {
            @Override
            public MockResult createFromParcel(Parcel source) {
                return new MockResult(source);
            }

            @Override
            public MockResult[] newArray(int size) {
                return new MockResult[size];
            }
        };
    }
}
