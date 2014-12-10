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
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class OsmpResponseReaderTest {
    @Test(timeout = 3000)
    public void tagReadingCheck() throws Exception {
        final String XML_CLOSED =
            "<?xml version=\"1.0\" encoding=\"windows-1251\"?>" +
            "<client terminal=\"111\" software=\"Dealer v0\" serial=\"\"/>" +
            "<providers>" +
                "<interruptPayment>\n" +
                    "<payment id=\"12345\"/>\n" +
                "</interruptPayment>\n" +
            "</providers>";
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(new ByteArrayInputStream(XML_CLOSED.getBytes()), "UTF-8");

        OsmpResponseReader reader = new OsmpResponseReader(parser);
        OsmpResponseReader.Tag tag = reader.next();
        Assert.assertNotNull(tag);
        Assert.assertEquals("client", tag.name);
        Assert.assertEquals("111", tag.attribute("terminal"));
        Assert.assertEquals("Dealer v0", tag.attribute("software"));
        Assert.assertEquals("", tag.attribute("serial"));
        Assert.assertNull(tag.attribute("unknown"));

        tag = reader.next();
        Assert.assertNotNull(tag);
        Assert.assertEquals("providers", tag.name);
        Assert.assertNull(tag.attribute("any"));

        Assert.assertNull(reader.next());
        // check non closed document
        final String XML_NONCLOSED =
                "<?xml version=\"1.0\" encoding=\"windows-1251\"?>" +
                    "<providers/>" +
                    "<auth>";
        parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(new ByteArrayInputStream(XML_NONCLOSED.getBytes()), "UTF-8");
        reader = new OsmpResponseReader(parser);
        tag = reader.next();
        Assert.assertNotNull(tag);
        Assert.assertEquals("providers", tag.name);
        tag = reader.next();
        Assert.assertNotNull(tag);
        Assert.assertEquals("auth", tag.name);
        Assert.assertNull(reader.next());
    }

    @Test(timeout = 3000)
    public void childReadCheck() throws Exception {
        final String XML =
            "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
            "<request>\n" +
                "<auth login=\"login\" sign=\"sign\" signAlg=\"MD5\"/>\n" +
                "<client terminal=\"111\" software=\"Dealer v0\" serial=\"\"/>\n" +
                "<terminals>\n" +
                    "<setFiscalMode>\n" +
                        "<kkm-reg-num>123456789012</kkm-reg-num>\n" +
                        "<taxpayer-reg-num>1234567#12345</taxpayer-reg-num>\n" +
                    "</setFiscalMode>\n" +
                "</terminals>\n" +
            "</request>";
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(new ByteArrayInputStream(XML.getBytes()), "UTF-8");

        OsmpResponseReader reader = new OsmpResponseReader(parser);
        OsmpResponseReader.Tag tag = reader.next();
        Assert.assertNotNull(tag);
        Assert.assertEquals("request", tag.name);
        OsmpResponseReader.Tag child = tag.nextChild();
        Assert.assertNotNull(child);
        Assert.assertEquals("auth", child.name);
        Assert.assertNull(child.nextChild());
        child = tag.nextChild();
        Assert.assertNotNull(child);
        Assert.assertEquals("client", child.name);
        Assert.assertNull(child.nextChild());
        child = tag.nextChild();
        Assert.assertNotNull(child);
        Assert.assertEquals("terminals", child.name);
        child = child.nextChild();
        Assert.assertNotNull(child);
        Assert.assertEquals("setFiscalMode", child.name);
        OsmpResponseReader.Tag subChild = child.nextChild();
        Assert.assertNotNull(subChild);
        Assert.assertEquals("kkm-reg-num", subChild.name);
        subChild = child.nextChild();
        Assert.assertNotNull(subChild);
        Assert.assertEquals("taxpayer-reg-num", subChild.name);
        Assert.assertNull(child.nextChild());
        Assert.assertNull(tag.nextChild());
        Assert.assertNull(reader.next());
    }

    @Test
    public void childInterruptedReadCheck() throws Exception {
        final String XML =
                "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
                "<request>\n" +
                    "<terminals>\n" +
                        "<kkm-reg-num>123456789012</kkm-reg-num>\n" +
                        "<taxpayer-reg-num>1234567#12345</taxpayer-reg-num>\n" +
                    "</terminals>\n" +
                    "<client />" +
                "</request>";
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(new ByteArrayInputStream(XML.getBytes()), "UTF-8");

        OsmpResponseReader reader = new OsmpResponseReader(parser);
        OsmpResponseReader.Tag tag = reader.next();
        Assert.assertNotNull(tag);
        Assert.assertEquals("request", tag.name);
        OsmpResponseReader.Tag child = tag.nextChild();
        Assert.assertNotNull(child);
        Assert.assertEquals("terminals", child.name);
        Assert.assertNotNull(child.nextChild());
        OsmpResponseReader.Tag nextChild = tag.nextChild();
        Assert.assertNotNull(nextChild);
        Assert.assertEquals("client", nextChild.name);
    }

    @Test
    public void tagTextReadCheck() throws Exception {
        final String XML =
                "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
                "<terminals>\n" +
                    "<kkm-reg-num>123456789012</kkm-reg-num>\n" +
                    "<taxpayer-reg-num>1234567#12345</taxpayer-reg-num>\n" +
                "</terminals>";
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(new ByteArrayInputStream(XML.getBytes()), "UTF-8");
        OsmpResponseReader reader = new OsmpResponseReader(parser);
        OsmpResponseReader.Tag tag = reader.next();
        Assert.assertNotNull(tag);
        Assert.assertEquals("terminals", tag.name);
        Assert.assertNull(tag.text);

        OsmpResponseReader.Tag child = tag.nextChild();
        Assert.assertNotNull(child);
        Assert.assertEquals("kkm-reg-num", child.name);
        Assert.assertEquals("123456789012", child.text);
        child = tag.nextChild();
        Assert.assertNotNull(child);
        Assert.assertEquals("taxpayer-reg-num", child.name);
        Assert.assertEquals("1234567#12345", child.text);
    }
}
