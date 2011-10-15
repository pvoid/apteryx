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
import org.pvoid.apteryxaustralis.types.Account;
import org.pvoid.apteryxaustralis.types.Group;
import org.pvoid.apteryxaustralis.net.Request;
import org.pvoid.apteryxaustralis.storage.IStorage;
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
  private static final URL _sOldApiURL;
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
/////// Создадим URL для запросов к прежнему API
    try
    {
      url = new URL("http://xml1.osmp.ru/term2/xml.jsp");
    }
    catch(MalformedURLException e)
    {
      e.printStackTrace();
      url = null;
    }
    _sOldApiURL = url;
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
//////////
    if(_sOffset>0)
      request.append('+');
    request.append(_sOffset);
    request.append("\"/>");
  }

  static private void startRequestOld(StringBuilder request,Account account,int requestType, boolean fullRequest)
  {
    request.append("<?xml version=\"1.0\" encoding=\"windows-1251\"?><request><protocol-version>3.00</protocol-version>")
           .append("<request-type>").append(requestType).append("</request-type>")
           .append("<terminal-id>").append(account.terminal).append("</terminal-id>")
           .append("<extra name=\"login\">").append(account.login).append("</extra>")
           .append("<extra name=\"password-md5\">").append(account.passwordHash).append("</extra>")
           .append("<extra name=\"client-software\">Dealer v1.9</extra>");
////////
    if(fullRequest)
    {
      request.append("<extra name=\"cashs\">true</extra><extra name=\"statistics\">true</extra>");
    }
    request.append("</request>");
  }

  static protected int checkAccount(Account account, List<Group> groups)
  {
    StringBuilder data = new StringBuilder();
    startRequestNew(data,account);
/////// попросим также информацию об агенте
    data.append("<agents><getAgentInfo/><getAgents/></agents></request>");
/////// и что же нам ответили
    Request.Response response = Request.Send(_sNewApiURL,data.toString(),"utf-8");
    if(response==null)
      return IStorage.RES_ERR_NETWORK_ERROR;
///////
    if(response.code!=200)
      return -response.code;
///////
    ResponseParser parser = new ResponseParser();
    parser.setAccount(account).setGroups(groups);
    if(!parseResponse(parser,response))
      return IStorage.RES_ERR_INCORRECT_RESPONSE;
///////
    return parser.getAccountResult();
  }

  static protected int getBalances(Account account, List<Group> groups)
  {
    StringBuilder data = new StringBuilder();
    startRequestNew(data,account);
    data.append("<agents>");
/////// добавим в запрос агентов, для получения балансов
    for(Group group : groups)
    {
      data.append("<getBalance><target-agent>").append(group.id).append("</target-agent></getBalance>");
    }
    data.append("</agents></request>");
/////// и что же нам ответили
    Request.Response response = Request.Send(_sNewApiURL,data.toString(),"utf-8");
    if(response==null)
      return IStorage.RES_ERR_NETWORK_ERROR;
///////
    if(response.code!=200)
      return -response.code;
///////
    ResponseParser parser = new ResponseParser();
    parser.setGroups(groups);
    if(!parseResponse(parser,response))
      return IStorage.RES_ERR_INCORRECT_RESPONSE;
///////
    return 0;

  }

  static protected int rebootTerminal(Account account, long terminalId)
  {
    if(account==null)
      return IStorage.RES_ERR_INVALID_ACCOUNT;
///////
    StringBuilder data = new StringBuilder();
    startRequestNew(data,account);
    data.append("<terminals><rebootTerminal><target-terminal>")
        .append(terminalId)
        .append("</target-terminal></rebootTerminal></terminals></request>");
    Log.v(OsmpRequest.class.getSimpleName(),data.toString());
    Request.Response response = Request.Send(_sNewApiURL,data.toString(),"utf-8");
    if(response==null)
      return IStorage.RES_ERR_NETWORK_ERROR;
///////
    if(response.code!=200)
      return -response.code;
    return IStorage.RES_OK;
  }

  static protected int switchOffTerminal(Account account, long terminalId)
  {
    StringBuilder data = new StringBuilder();
    startRequestNew(data,account);
    data.append("<terminals><disableTerminal><target-terminal>")
        .append(terminalId)
        .append("</target-terminal></disableTerminal></terminals></request>");
    Log.v(OsmpRequest.class.getSimpleName(),data.toString());
    Request.Response response = Request.Send(_sNewApiURL,data.toString(),"utf-8");
    if(response==null)
      return IStorage.RES_ERR_NETWORK_ERROR;
///////
    if(response.code!=200)
      return -response.code;
    return IStorage.RES_OK;
  }

  static protected int getTerminals(Account account, List<Terminal> terminals)
  {
    StringBuilder data = new StringBuilder();
    startRequestOld(data, account, 16, true);
//////////
    Request.Response response = Request.Send(_sOldApiURL,data.toString(),"windows-1251");
    if(response==null)
      return IStorage.RES_ERR_NETWORK_ERROR;
/////////
    if(response.code!=200)
      return -response.code;
///////
    ResponseParser parser = new ResponseParser();
    parser.setTerminals(terminals);
    if(!parseResponse(parser,response))
      return IStorage.RES_ERR_INCORRECT_RESPONSE;
///////
    return 0;
  }

  protected static boolean parseResponse(ResponseParser parser, Request.Response response)
  {
    try
    {
      SAXParser saxParser = _sSAXFactory.newSAXParser();
      InputSource source = new InputSource();
      ByteArrayInputStream stream;
      try
      {
        stream = new ByteArrayInputStream(response.data.getBytes("UTF-8"));
      }
      catch (UnsupportedEncodingException e)
      {
        e.printStackTrace();
        return false;
      }
      source.setByteStream(stream);
      source.setEncoding("UTF-8");
      saxParser.parse(source,parser);
////////
      return true;
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
////////
    return false;
  }
}
