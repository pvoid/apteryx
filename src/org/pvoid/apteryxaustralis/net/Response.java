package org.pvoid.apteryxaustralis.net;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.pvoid.apteryxaustralis.accounts.AgentsSection;
import org.pvoid.apteryxaustralis.accounts.ReportsSection;
import org.pvoid.apteryxaustralis.accounts.TerminalsSection;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Response extends DefaultHandler
{
  private int _HttpCode;
  private int _OsmpCode;
  private IResponseParser _Parser = null;
  private AgentsSection _Agents;
  private TerminalsSection _Terminals;
  private ReportsSection _Reports;
  
  public Response(HttpsURLConnection connection) throws UnsupportedEncodingException, IOException
  {
    _HttpCode = connection.getResponseCode();
    if(_HttpCode==200)
    {
      String encoding = connection.getContentEncoding();
      if(encoding==null)
      {
        encoding = "utf-8";
      }
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),encoding));
      String line;
      StringBuffer result = new StringBuffer();
      while((line = reader.readLine())!=null)
      {
        result.append(line);
      }
      reader.close();
/////////
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser parser;
      try
      {
        parser = factory.newSAXParser();
      }
      catch (ParserConfigurationException e)
      {
        e.printStackTrace();
        return;
      }
      catch (SAXException e)
      {
        e.printStackTrace();
        return;
      }
      InputSource source = new InputSource();
      ByteArrayInputStream stream;
      try
      {
        stream = new ByteArrayInputStream(result.toString().getBytes("UTF-8"));
      }
      catch (UnsupportedEncodingException e)
      {
        e.printStackTrace();
        return;
      }
      source.setByteStream(stream);
      source.setEncoding("UTF-8");
      try
      {
        parser.parse(source, this);
      }
      catch (SAXException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (IOException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
    if(localName.equals("response"))
    {
      String code = attributes.getValue("result");
      if(code!=null)
      {
        _OsmpCode = Integer.parseInt(code);
      }
    }
    else if(localName.equals("agents"))
    {
      _Agents = AgentsSection.getParser();
      _Parser = _Agents;
      _Parser.SectionStart();
    }
    else if(localName.equals("terminals"))
    {
      _Terminals = TerminalsSection.getParser();
      _Parser = _Terminals;
      _Parser.SectionStart();
    }
    else if(localName.equals("reports"))
    {
      _Reports = ReportsSection.getParser();
      _Parser = _Reports;
      _Parser.SectionStart();
    }
    else if(_Parser!=null)
    {
      _Parser.ElementStart(localName, attributes);
    }
  }
  
  @Override
  public void characters(char[] ch, int start, int length)
  {
    
  }
  
  @Override
  public void endElement(String uri, String localName, String qName)
  {
    if(localName.equals("agents"))
    {
      _Parser.SectionEnd();
      _Parser = null;
    }
    else if(localName.equals("terminals"))
    {
      _Parser.SectionEnd();
      _Parser = null;
    }
  }
  
  public AgentsSection Agents()
  {
    return(_Agents);
  }
  
  public TerminalsSection Terminals()
  {
    return(_Terminals);
  }
  
  public int OsmpCode()
  {
    return(_OsmpCode);
  }
}
