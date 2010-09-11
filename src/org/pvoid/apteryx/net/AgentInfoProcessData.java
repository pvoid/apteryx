package org.pvoid.apteryx.net;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.pvoid.apteryx.accounts.Agent;

public class AgentInfoProcessData extends DefaultHandler
{
  private static final int TAG_NONE = 0;
  private static final int TAG_CODE = 1;
  
  private StringBuilder _Text;
  private int _Code;
  private int _TagCode;
  private ArrayList<Agent> _Agents;
  
  public AgentInfoProcessData()
  {
    _Text = new StringBuilder();
    _Agents = new ArrayList<Agent>();
  }
  
  @Override
  public void startDocument()
  {
    _TagCode = TAG_NONE;
    _Text.delete(0, _Text.length());
    _Agents.clear();
  }
  
  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
    if(localName.compareToIgnoreCase("agt")==0)
    {
      Agent agent = new Agent();
      agent.Id = Long.parseLong(attributes.getValue("aid"));
      agent.Name = attributes.getValue("an");
      _Agents.add(agent);
    }
    else if(localName.compareToIgnoreCase("result-code")==0)
    {
      _Text.delete(0, _Text.length());
      _TagCode = TAG_CODE;
    }
  }
  
  @Override
  public void characters(char[] ch, int start, int length)
  {
    _Text.append(ch,start,length);
  }
  
  @Override
  public void endElement(String uri, String localName, String qName)
  {
    switch(_TagCode)
    {
      case TAG_CODE:
        _Code = Integer.parseInt(_Text.toString());
        break;
    }
    _TagCode = TAG_NONE;
  }
  
  public int Code()
  {
    return(_Code);
  }
  
  public ArrayList<Agent> Agents()
  {
    return(_Agents);
  }
}
  
