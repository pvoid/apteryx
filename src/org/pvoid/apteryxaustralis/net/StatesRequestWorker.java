package org.pvoid.apteryxaustralis.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.Agent;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class StatesRequestWorker
{
  private final TerminalsProcessData _Terminals;
  private final HashMap<Long, ArrayList<Agent>> _Filter;

  public StatesRequestWorker(TerminalsProcessData terminals,HashMap<Long, ArrayList<Agent>> filter)
  {
    _Terminals = terminals;
    _Filter = filter;
  }
  
  public boolean Work(Account... accounts)
  {
    _Terminals.Clear();
    
    if(accounts.length==0)
    {
      return(true);
    }
    
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
      _Terminals.SetAgentsFilter(_Filter.get(account.Id));
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