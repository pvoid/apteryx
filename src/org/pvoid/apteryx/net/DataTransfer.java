package org.pvoid.apteryx.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.pvoid.apteryx.Consts;

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
