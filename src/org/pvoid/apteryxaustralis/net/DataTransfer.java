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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.pvoid.apteryxaustralis.Consts;

import android.os.Bundle;

public class DataTransfer
{
  static protected String Load(String url,String data)
  {
    try 
    {
      URL request = new URL(url);
      URLConnection connection = request.openConnection();
      connection.setDoOutput(true);
      OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
      writer.write(data);
      writer.flush();
      writer.close();
      
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"windows-1251"));
      String line;
      StringBuilder result = new StringBuilder();
      while((line = reader.readLine())!=null)
      {
        result.append(line);
      }
      reader.close();
      return (result.toString());
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return(null);
  }
  
  static private String FormatRequest(String login, String password, String terminal, int requestType, boolean fullRequest)
  {
    String request = "<?xml version=\"1.0\" encoding=\"windows-1251\"?>"+
    "<request>"+
    "<protocol-version>3.00</protocol-version>"+
    "<request-type>"+requestType+"</request-type>"+
    "<terminal-id>"+terminal+"</terminal-id>"+
    "<extra name=\"login\">"+login+"</extra>"+
    "<extra name=\"password-md5\">"+password+"</extra>"+
    "<extra name=\"client-software\">Dealer v1.9</extra>";
    
    if(fullRequest)
    {
      request += "<extra name=\"cashs\">true</extra>" +
                 "<extra name=\"statistics\">true</extra>";
    }
    request += "</request>"; 
    return (request);
  }
  
  static public void TestAccount(String login, String password, String terminal, IResponseHandler handler)
  {
    Bundle async_params = new Bundle();
    async_params.putString(RequestTask.DATA,FormatRequest(login,password,terminal,18,false));
    (new RequestTask(handler)).execute(async_params);
  }
  
  static public String RefreshStates(String login, String password, String terminal)
  {
    return(Load(Consts.URL,FormatRequest(login, password, terminal, 16, true)));
  }
}
