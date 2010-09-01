package org.pvoid.apteryx.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.pvoid.apteryx.accounts.Account;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class StatesRequestWorker
{
  private final TerminalsProcessData _Terminals;

  public StatesRequestWorker(TerminalsProcessData terminals)
  {
    _Terminals = terminals;      
  }
  
  public boolean Work(Account... accounts)
  {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser;
    try
    {
      parser = factory.newSAXParser();
    }
    catch (ParserConfigurationException e)
    {
      e.printStackTrace();
      return(false);
    }
    catch (SAXException e)
    {
      e.printStackTrace();
      return(false);
    }
//////
    for(Account account : accounts)
    {
      _Terminals.SetAgent(account.Id);
      String response = DataTransfer.RefreshStates(account.Login, account.PasswordHash, account.Terminal);
      if(response==null)
        continue;
      InputSource source = new InputSource();
      ByteArrayInputStream stream;
      try
      {
        stream = new ByteArrayInputStream(response.getBytes("UTF-8"));
      }
      catch (UnsupportedEncodingException e)
      {
        e.printStackTrace();
        continue;
      }
      source.setByteStream(stream);
      source.setEncoding("UTF-8");
      try
      {
        parser.parse(source, _Terminals);
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
    return(true);
  }
}