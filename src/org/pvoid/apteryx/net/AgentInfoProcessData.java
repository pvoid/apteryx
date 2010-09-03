package org.pvoid.apteryx.net;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AgentInfoProcessData extends DefaultHandler
{
  private static final int TAG_NONE = 0;
  private static final int TAG_CODE = 1;
  private static final int TAG_LEVEL = 2;
  private static final int TAG_AGENTNAME = 3;
  private static final int TAG_AGENTID = 4;
  
  private String _AgentName;
  private String _AgentId;
  private String _TreeLevel;
  private StringBuilder _Text;
  private int _Code;
  private int _TagCode;
  private boolean _IsAgent;
  private boolean _IsReady;
  
  public AgentInfoProcessData()
  {
    _Text = new StringBuilder();
  }
  
  @Override
  public void startDocument()
  {
    _TagCode = TAG_NONE;
    _Text.delete(0, _Text.length());
    _IsAgent = false;
    _IsReady = false;
  }
  
  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
    if(localName.compareToIgnoreCase("agent")==0 && !_IsReady)
    {
      _AgentName = null;
      _AgentId = null;
      _TreeLevel = null;
      _IsAgent = true;
    }
    else if(localName.compareToIgnoreCase("field")==0 && _IsAgent)
    {
      _Text.delete(0, _Text.length());
      String name = attributes.getValue("name");
      if(name.compareToIgnoreCase("LEV")==0)
      {
        _TagCode = TAG_LEVEL;
      }
      else if(name.compareToIgnoreCase("AGT_FULL_NAME")==0)
      {
        _TagCode = TAG_AGENTNAME;
      }
      else if(name.compareToIgnoreCase("AGT_ID")==0)
      {
        _TagCode = TAG_AGENTID;
      }
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
      case TAG_AGENTID:
        _AgentId = _Text.toString();
        break;
      case TAG_AGENTNAME:
        _AgentName = _Text.toString();
        break;
      case TAG_LEVEL:
        _TreeLevel = _Text.toString();
        break;
    }
    
    if(localName.compareToIgnoreCase("agent")==0)
    {
      if(_TreeLevel.equals("1"))
      {
        _IsReady = true;
      }
      _IsAgent = false;
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
  
  public String AgentId()
  {
    return(_AgentId);
  }
}

