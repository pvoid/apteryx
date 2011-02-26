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

package org.pvoid.apteryxaustralis.net;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

public class Request
{
  private static final String URL = "https://service1.osmp.ru/xmlgate/xml.jsp";
  private static final int INTERVAL = 24*60*60*1000;

  private StringBuffer _mRequest = new StringBuffer();
  private StringBuffer _mAgentInterface = new StringBuffer();
  private StringBuffer _mTerminalsInterface = new StringBuffer();
  private StringBuffer _mReportsInterface = new StringBuffer();

  private final String _mLogin;
  private final String _mPassword;
  private final String _mTerminal;
  private final int _mOffset;

  static
  {
    SSLInitializer.Initialize();
  }
  
  public Request(String login, String passwordHash, String terminal)
  {
    _mLogin = login;
    _mPassword = passwordHash;
    _mTerminal = terminal;

    TimeZone zone = TimeZone.getDefault();
    _mOffset = zone.getOffset(System.currentTimeMillis())/3600000;

    Initialize();
  }

  public Request(String login, String passwordHash, long terminal)
  {
    _mLogin = login;
    _mPassword = passwordHash;
    _mTerminal = Long.toString(terminal);

    TimeZone zone = TimeZone.getDefault();
    _mOffset = zone.getOffset(System.currentTimeMillis())/3600000;

    Initialize();
  }

  private void Initialize()
  {
    _mRequest.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
                    "<request>"+
                    "<auth login=\"");
    _mRequest.append(_mLogin);
    _mRequest.append("\" sign=\"");
    _mRequest.append(_mPassword);
    _mRequest.append("\" signAlg=\"MD5\"/><client terminal=\"");
    _mRequest.append(_mTerminal);
    _mRequest.append("\" software=\"Dealer v0\" timezone=\"GMT");

    if(_mOffset>0)
      _mRequest.append('+');
    _mRequest.append(_mOffset);
    _mRequest.append("\"/>");
  }

  public void clear()
  {
    _mRequest.delete(0, _mRequest.length());
    _mAgentInterface.delete(0, _mAgentInterface.length());
    _mReportsInterface.delete(0, _mReportsInterface.length());
    _mTerminalsInterface.delete(0, _mTerminalsInterface.length());

    Initialize();
  }

  public Request getAgentInfo()
  {
    _mAgentInterface.append("<getAgentInfo/>");
    return this;
  }
  
  public Request getAgents()
  {
    _mAgentInterface.append("<getAgents/>");
    return this;
  }
  
  public Request getTerminals()
  {
    _mTerminalsInterface.append("<getTerminals />");
    return this;
  }

  public Request getPayments(long terminalId)
  {
    long dateStart = System.currentTimeMillis();
    Date tillDate = new Date(dateStart);
    dateStart-=INTERVAL;
    Date startDate = new Date(dateStart);
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
    TimeZone timezone = TimeZone.getDefault();
    formatDate.setTimeZone(timezone);
    formatTime.setTimeZone(timezone);
    _mReportsInterface.append("<getPayments mode=\"async\"><date-from>");
    _mReportsInterface.append(formatDate.format(startDate));
    _mReportsInterface.append('T');
    _mReportsInterface.append(formatTime.format(startDate));
    _mReportsInterface.append("</date-from><date-to>");
    _mReportsInterface.append(formatDate.format(tillDate));
    _mReportsInterface.append('T');
    _mReportsInterface.append(formatTime.format(tillDate));
    _mReportsInterface.append("</date-to><terminal>");
    _mReportsInterface.append(terminalId);
    _mReportsInterface.append("</terminal><max-row-count>1</max-row-count></getPayments>");
    return this;
  }

  public Request getPaymentsFromQue(long queId)
  {
    _mReportsInterface.append("<getPayments quid=\"");
    _mReportsInterface.append(queId);
    _mReportsInterface.append("\" mode=\"async\"/>");
    return this;
  }

  public Request getTerminalsStatus()
  {
    _mReportsInterface.append("<getTerminalsStatus />");
    return this;
  }

  public Request getTerminalStatus(long id)
  {
    _mReportsInterface.append("<getTerminalsStatus><target-terminal>");
    _mReportsInterface.append(id);
    _mReportsInterface.append("</target-terminal></getTerminalsStatus>");
    return this;
  }

  public Response getResponse()
  {
    if(_mAgentInterface.length()>0)
    {
      _mRequest.append("<agents>");
      _mRequest.append(_mAgentInterface);
      _mRequest.append("</agents>");
    }
    if(_mTerminalsInterface.length()>0)
    {
      _mRequest.append("<terminals>");
      _mRequest.append(_mTerminalsInterface);
      _mRequest.append("</terminals>");
    }
    if(_mReportsInterface.length()>0)
    {
      _mRequest.append("<reports>");
      _mRequest.append(_mReportsInterface);
      _mRequest.append("</reports>");
    }
    _mRequest.append("</request>");
    try
    {
      System.setProperty("http.keepAlive", "false");
      HttpsURLConnection connection = (HttpsURLConnection)new URL(URL).openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
      writer.write(_mRequest.toString());
      writer.flush();
      writer.close();
///////////
      try
      {
        Response response = new Response(connection);
        return(response);
      }
      finally
      {
        connection.disconnect();
      }
    }
    catch (MalformedURLException e)
    {
      e.printStackTrace();
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return(null);
  }
}
