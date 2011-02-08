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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

import org.pvoid.apteryxaustralis.Consts;

public class Request
{
  private StringBuffer _Request = new StringBuffer();
  private StringBuffer _AgentInterface = new StringBuffer();
  private StringBuffer _TerminalsInterface = new StringBuffer();
  private StringBuffer _ReportsInterface = new StringBuffer();
  @SuppressWarnings("unused")
  private static final SSLInitializer _Initializer = new SSLInitializer();
  
  public Request(String login, String passwordHash, String terminal)
  {
    _Request.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
                    "<request>"+
                    "<auth login=\"");
    _Request.append(login);
    _Request.append("\" sign=\"");
    _Request.append(passwordHash);
    _Request.append("\" signAlg=\"MD5\"/><client terminal=\"");
    _Request.append(terminal);
    _Request.append("\" software=\"Dealer v0\" timezone=\"GMT");
    TimeZone zone = TimeZone.getDefault();
    int offset = zone.getOffset(System.currentTimeMillis());
    offset/=3600000;
    if(offset>0)
      _Request.append('+');
    _Request.append(offset);
    _Request.append("\"/>");
  }
  
  public void getAgentInfo()
  {
    _AgentInterface.append("<getAgentInfo/>");
  }
  
  public void getAgents()
  {
    _AgentInterface.append("<getAgents/>");
  }
  
  public void getTerminals()
  {
    _TerminalsInterface.append("<getTerminals />");
  }
  
  public void getTerminalsStatus()
  {
    _ReportsInterface.append("<getTerminalsStatus />");
  }

  public void getTerminalStatus(long id)
  {
    _ReportsInterface.append("<getTerminalsStatus><target-terminal>");
    _ReportsInterface.append(id);
    _ReportsInterface.append("</target-terminal></getTerminalsStatus>");
  }

  public Response getResponse()
  {
    if(_AgentInterface.length()>0)
    {
      _Request.append("<agents>");
      _Request.append(_AgentInterface);
      _Request.append("</agents>");
    }
    if(_TerminalsInterface.length()>0)
    {
      _Request.append("<terminals>");
      _Request.append(_TerminalsInterface);
      _Request.append("</terminals>");
    }
    if(_ReportsInterface.length()>0)
    {
      _Request.append("<reports>");
      _Request.append(_ReportsInterface);
      _Request.append("</reports>");
    }
    _Request.append("</request>");
    try
    {
      HttpsURLConnection connection = (HttpsURLConnection)new URL(Consts.URL).openConnection();
      connection.setDoOutput(true);
      OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
      writer.write(_Request.toString());
      writer.flush();
      writer.close();
///////////
      Response response = new Response(connection);
      connection.disconnect();
      return(response);
    }
    catch (MalformedURLException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (UnsupportedEncodingException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return(null);
  }
}
