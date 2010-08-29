package org.pvoid.apteryx.net;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AgentInfoProcessData extends DefaultHandler
{
  private static final int TAG_NONE = 0;
  private static final int TAG_CODE = 1;
  
  private String _AgentName;
  private StringBuilder _Text;
  private int _Code;
  private int _TagCode; 
  
  public AgentInfoProcessData()
  {
    _Text = new StringBuilder();
  }
  
  @Override
  public void startDocument()
  {
    _TagCode = TAG_NONE;
    _Text.delete(0, _Text.length());
  }
  
  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
    if(localName.compareToIgnoreCase("agt")==0)
    {
      _AgentName = attributes.getValue("an");
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
  
  public String AgentName()
  {
    return(_AgentName);
  }
}

