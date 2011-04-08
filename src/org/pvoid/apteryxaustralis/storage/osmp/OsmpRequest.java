/*
 * Copyright (C) 2010-2011  Dmitry Petuhov
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

package org.pvoid.apteryxaustralis.storage.osmp;

import android.util.Log;
import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.Agent;
import org.pvoid.apteryxaustralis.net.Request;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.TimeZone;

public class OsmpRequest
{
  private static final URL _sNewApiURL;
  private static final int _sOffset;
  private static final SAXParserFactory _sSAXFactory = SAXParserFactory.newInstance();

  static
  {
/////// Создадим URL для запросов к новому API
    URL url;
    try
    {
      url = new URL("https://service1.osmp.ru/xmlgate/xml.jsp");
    }
    catch(MalformedURLException e)
    {
      e.printStackTrace();
      url = null;
    }
    _sNewApiURL = url;
/////// Получим локальное смещение временное
    TimeZone zone = TimeZone.getDefault();
    _sOffset = zone.getOffset(System.currentTimeMillis())/3600000;
  }

  static private void startRequestNew(StringBuilder request, Account account)
  {
    request.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
                    "<request>"+
                    "<auth login=\"");
    request.append(account.login);
    request.append("\" sign=\"");
    request.append(account.passwordHash);
    request.append("\" signAlg=\"MD5\"/><client terminal=\"");
    request.append(account.terminal);
    request.append("\" software=\"Dealer v0\" timezone=\"GMT");

    if(_sOffset>0)
      request.append('+');
    request.append(_sOffset);
    request.append("\"/>");
  }

  static public int checkAccount(Account account, List<Agent> agents)
  {
    StringBuilder data = new StringBuilder();
    startRequestNew(data,account);
/////// попросим также информацию об агенте
    data.append("<agents><getAgentInfo/><getAgents/></agents></request>");
/////// и что же нам ответили
    Request.Response response = Request.Send(_sNewApiURL,data.toString(),"utf-8");
    if(response.code!=200)
      return -response.code;
    else
    {
      try
      {
        Log.v(OsmpRequest.class.getSimpleName(),response.data);

        SAXParser parser = _sSAXFactory.newSAXParser();
        InputSource source = new InputSource();
        ByteArrayInputStream stream;
        try
        {
          stream = new ByteArrayInputStream(response.data.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
          e.printStackTrace();
          return -1;
        }
        source.setByteStream(stream);
        source.setEncoding("UTF-8");

        ResponseParser rParser = new ResponseParser(account,agents);

        parser.parse(source,rParser);

        return rParser.getAccountResult();
      }
      catch(ParserConfigurationException e)
      {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
      catch(SAXException e)
      {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
      catch(IOException e)
      {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
      Log.v(OsmpRequest.class.getSimpleName(),response.data);
    }
    return -1;
  }
}
