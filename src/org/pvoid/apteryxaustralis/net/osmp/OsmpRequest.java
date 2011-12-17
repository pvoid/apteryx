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

package org.pvoid.apteryxaustralis.net.osmp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import org.pvoid.apteryxaustralis.net.IRequest;
import org.pvoid.apteryxaustralis.net.Request;
import org.pvoid.apteryxaustralis.storage.AccountsProvider;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpContentProvider;
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

public class OsmpRequest implements IRequest
{
  public static final String LOGIN    = "login";
  public static final String PASSWORD = "password";
  public static final String TERMINAL = "terminal";
  public static final String ACCOUNT_ID = "id";

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
  /**
   * Создает шапку запроса на базе данных по аккаунту
   * @param request      билдер для текста запроса
   * @param accountData  данные по аккаунту
   */
  static private void startRequestNew(StringBuilder request, Bundle accountData)
  {
    request.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
                    "<request>"+
                    "<auth login=\"");
    request.append(accountData.getString(LOGIN));
    request.append("\" sign=\"");
    request.append(accountData.getString(PASSWORD));
    request.append("\" signAlg=\"MD5\"/><client terminal=\"");
    request.append(accountData.getString(TERMINAL));
    request.append("\" software=\"Dealer v0\" timezone=\"GMT");
//////////
    if(_sOffset>0)
      request.append('+');
    request.append(_sOffset);
    request.append("\"/>");
  }

  static private void startRequestOld(StringBuilder request, Bundle accountData,int requestType, boolean fullRequest)
  {
    request.append("<?xml version=\"1.0\" encoding=\"windows-1251\"?><request><protocol-version>3.00</protocol-version>")
           .append("<request-type>").append(requestType).append("</request-type>")
           .append("<terminal-id>").append(accountData.getString(TERMINAL)).append("</terminal-id>")
           .append("<extra name=\"login\">").append(accountData.getString(LOGIN)).append("</extra>")
           .append("<extra name=\"password-md5\">").append(accountData.getString(PASSWORD)).append("</extra>")
           .append("<extra name=\"client-software\">Dealer v1.9</extra>");
////////
    if(fullRequest)
    {
      request.append("<extra name=\"cashs\">true</extra><extra name=\"statistics\">true</extra>");
    }
    request.append("</request>");
  }
  /**
   * Проверяет аккаунт. В случае если все хорошо, добавляет его
   * @param context     контекст исполнения. Для вызова провйдера
   * @param accountData данные по аккаунту
   * @return код возвращенный сервером
   */
  public int checkAccount(Context context, Bundle accountData)
  {
    StringBuilder data = new StringBuilder();
    startRequestNew(data,accountData);
/////// попросим также информацию об агенте
    data.append("<agents><getAgentInfo/><getAgents/></agents></request>");
/////// и что же нам ответили
    Request.Response response = Request.Send(_sNewApiURL,data.toString(),"utf-8");
    if(response==null)
      return Request.RES_ERR_NETWORK_ERROR;
///////
    if(response.code!=200)
      return -response.code;
///////
    ResponseParser parser = new ResponseParser();
    if(!parseResponse(parser,response))
      return Request.RES_ERR_INCORRECT_RESPONSE;
///////
    if(parser.getAccountResult()==0)
    {
      final ContentValues values = new ContentValues();
      final ResponseParser.Account account = parser.getAccount();
      final List<ResponseParser.Group> groups = parser.getGroups();
      final ContentResolver resolver = context.getContentResolver();
/////////// Добавляем аккаунт
      accountData.putString(ACCOUNT_ID,Long.toString(account.id));
      values.put(AccountsProvider.Accounts.COLUMN_ID,   account.id);
      values.put(AccountsProvider.Accounts.COLUMN_TITLE,account.title);
      values.put(AccountsProvider.Accounts.COLUMN_LOGIN,accountData.getString(LOGIN));
      values.put(AccountsProvider.Accounts.COLUMN_PASSWORD,accountData.getString(PASSWORD));
      values.put(AccountsProvider.Accounts.COLUMN_CUSTOM1,accountData.getString(TERMINAL));
      if(resolver.insert(AccountsProvider.Accounts.CONTENT_URI, values)!=null)
        resolver.notifyChange(AccountsProvider.Accounts.CONTENT_URI,null);
/////////// Если нет ни одной группы, добавим сам аккаунт
      if(groups.size()==0)
      {
        final ResponseParser.Group group = new ResponseParser.Group();
        group.id = account.id;
        group.name = account.title;
        groups.add(group);
      }
/////////// Добавляем группы
      final long lastUpdateTime = System.currentTimeMillis();
      boolean groupsUpdated = false;
      for(ResponseParser.Group group : groups)
      {
        values.clear();
        values.put(OsmpContentProvider.Agents.COLUMN_ACCOUNT,     account.id);
        values.put(OsmpContentProvider.Agents.COLUMN_AGENT,       group.id);
        values.put(OsmpContentProvider.Agents.COLUMN_AGENT_NAME,  group.name);
        values.put(OsmpContentProvider.Agents.COLUMN_BALANCE,     group.balance);
        values.put(OsmpContentProvider.Agents.COLUMN_LAST_UPDATE, lastUpdateTime);
        values.put(OsmpContentProvider.Agents.COLUMN_OVERDRAFT,   group.overdraft);
        values.put(OsmpContentProvider.Agents.COLUMN_STATE,       0);
        values.put(OsmpContentProvider.Agents.COLUMN_SEEN,        0);
        if(resolver.insert(OsmpContentProvider.Agents.CONTENT_URI,values)!=null)
          groupsUpdated = true;
      }
//////////
      if(groupsUpdated)
        resolver.notifyChange(OsmpContentProvider.Agents.CONTENT_URI,null);
    }
///////
    return parser.getAccountResult();
  }

  public int getBalances(Context context, Bundle accountData)
  {
    final StringBuilder data = new StringBuilder();
    final ContentResolver resolver = context.getContentResolver();
    final ResponseParser parser = new ResponseParser();
    startRequestNew(data,accountData);
    data.append("<agents>");
/////// добавим в запрос агентов, для получения балансов
    final Cursor cursor = resolver.query(OsmpContentProvider.Agents.CONTENT_URI,
                                         new String[] {OsmpContentProvider.Agents.COLUMN_AGENT},
                                         OsmpContentProvider.Agents.COLUMN_ACCOUNT+"=?",
                                         new String[] {accountData.getString(ACCOUNT_ID)},
                                         null);
    try
    {
      if(cursor!=null)
        while(cursor.moveToNext())
        {
          parser.addGroup(cursor.getLong(0));
          data.append("<getBalance><target-agent>").append(cursor.getString(0)).append("</target-agent></getBalance>");
        }
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
    data.append("</agents></request>");
/////// и что же нам ответили
    Request.Response response = Request.Send(_sNewApiURL,data.toString(),"utf-8");
    if(response==null)
      return Request.RES_ERR_NETWORK_ERROR;
///////
    if(response.code!=200)
      return -response.code;
///////

    if(!parseResponse(parser,response))
      return Request.RES_ERR_INCORRECT_RESPONSE;
/////// Обновим данные
    final List<ResponseParser.Group> groups = parser.getGroups();
    final ContentValues values = new ContentValues();
    boolean notifyObserver = false;
    for(ResponseParser.Group group : groups)
    {
      values.put(OsmpContentProvider.Agents.COLUMN_BALANCE,group.balance);
      values.put(OsmpContentProvider.Agents.COLUMN_OVERDRAFT,group.overdraft);
      if(resolver.update(OsmpContentProvider.Agents.CONTENT_URI,
                      values,
                      OsmpContentProvider.Agents.COLUMN_AGENT+"=?",
                      new String[] {Long.toString(group.id)})>0)
        notifyObserver = true;
    }
///////
    if(notifyObserver)
      resolver.notifyChange(OsmpContentProvider.Agents.CONTENT_URI,null);
///////
    return 0;
  }

  public static int rebootTerminal(Bundle accountData, long terminalId)
  {
    StringBuilder data = new StringBuilder();
    startRequestNew(data,accountData);
    data.append("<terminals><rebootTerminal><target-terminal>")
        .append(terminalId)
        .append("</target-terminal></rebootTerminal></terminals></request>");
    Request.Response response = Request.Send(_sNewApiURL,data.toString(),"utf-8");
    if(response==null)
      return Request.RES_ERR_NETWORK_ERROR;
///////
    if(response.code!=200)
      return -response.code;
    return Request.STATE_OK;
  }

  /*static protected int switchOffTerminal(Account account, long terminalId)
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
  }*/

  public int getTerminals(Context context, Bundle accountData)
  {
    StringBuilder data = new StringBuilder();
    startRequestOld(data, accountData, 16, true);
//////////
    Request.Response response = Request.Send(_sOldApiURL,data.toString(),"windows-1251");
    if(response==null)
      return Request.RES_ERR_NETWORK_ERROR;
/////////
    if(response.code!=200)
      return -response.code;
///////
    ResponseParser parser = new ResponseParser();
    if(!parseResponse(parser,response))
      return Request.RES_ERR_INCORRECT_RESPONSE;
///////
    final List<ResponseParser.Terminal> terminals = parser.getTerminals();
    final ContentValues values = new ContentValues();
    final ContentResolver resolver = context.getContentResolver();
///////
    boolean notifyObserver = false;
    for(ResponseParser.Terminal terminal : terminals)
    {
      values.clear();
      values.put(OsmpContentProvider.Terminals.COLUMN_ID,terminal.id());
      values.put(OsmpContentProvider.Terminals.COLUMN_ADDRESS,terminal.Address());
      values.put(OsmpContentProvider.Terminals.COLUMN_STATE,terminal.State());
      values.put(OsmpContentProvider.Terminals.COLUMN_MS,terminal.ms);
      values.put(OsmpContentProvider.Terminals.COLUMN_ACCOUNTID,accountData.getString(ACCOUNT_ID));
      values.put(OsmpContentProvider.Terminals.COLUMN_PRINTERSTATE,terminal.printer_state);
      values.put(OsmpContentProvider.Terminals.COLUMN_CASHBINSTATE,terminal.cashbin_state);
      values.put(OsmpContentProvider.Terminals.COLUMN_CASH,terminal.cash);
      values.put(OsmpContentProvider.Terminals.COLUMN_LASTACTIVITY,terminal.lastActivity);
      values.put(OsmpContentProvider.Terminals.COLUMN_LASTPAYMENT,terminal.lastPayment);
      values.put(OsmpContentProvider.Terminals.COLUMN_BONDS,terminal.bondsCount);
      values.put(OsmpContentProvider.Terminals.COLUMN_BALANCE,terminal.balance);
      values.put(OsmpContentProvider.Terminals.COLUMN_SIGNALLEVEL,terminal.signalLevel);
      values.put(OsmpContentProvider.Terminals.COLUMN_SOFTVERSION,terminal.softVersion);
      values.put(OsmpContentProvider.Terminals.COLUMN_PRINTERMODEL,terminal.printerModel);
      values.put(OsmpContentProvider.Terminals.COLUMN_CASHBINMODEL,terminal.cashbinModel);
      values.put(OsmpContentProvider.Terminals.COLUMN_BONDS10,terminal.bonds10count);
      values.put(OsmpContentProvider.Terminals.COLUMN_BONDS50,terminal.bonds50count);
      values.put(OsmpContentProvider.Terminals.COLUMN_BONDS100,terminal.bonds100count);
      values.put(OsmpContentProvider.Terminals.COLUMN_BONDS500,terminal.bonds500count);
      values.put(OsmpContentProvider.Terminals.COLUMN_BONDS1000,terminal.bonds1000count);
      values.put(OsmpContentProvider.Terminals.COLUMN_BONDS5000,terminal.bonds5000count);
      values.put(OsmpContentProvider.Terminals.COLUMN_BONDS10000,terminal.bonds10000count);
      values.put(OsmpContentProvider.Terminals.COLUMN_PAYSPERHOUR,terminal.paysPerHour);
      values.put(OsmpContentProvider.Terminals.COLUMN_AGENTID,terminal.agentId);
      values.put(OsmpContentProvider.Terminals.COLUMN_AGENTNAME,terminal.agentName);
      if(resolver.insert(OsmpContentProvider.Terminals.CONTENT_URI,values)!=null)
        notifyObserver = true;
    }
///////
    if(notifyObserver)
    {
      resolver.notifyChange(OsmpContentProvider.Terminals.CONTENT_URI,null);
      resolver.notifyChange(OsmpContentProvider.Agents.CONTENT_URI,null);
    }
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
      e.printStackTrace();
    }
    catch(SAXException e)
    {
      e.printStackTrace();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
////////
    return false;
  }
}
