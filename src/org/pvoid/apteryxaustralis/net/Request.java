package org.pvoid.apteryxaustralis.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
  @SuppressWarnings("unused")
  private static final SSLInitializer _Initializer = new SSLInitializer();
  
  public Request(String login, String passwordHash, String terminal)
  {
   //Теперь можно спокойно отерывать любые https соединения
    //
    
    
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
  
  public Response getResponse()
  {
    if(_AgentInterface.length()>0)
    {
      _Request.append("<agents>");
      _Request.append(_AgentInterface);
      _Request.append("</agents>");
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
