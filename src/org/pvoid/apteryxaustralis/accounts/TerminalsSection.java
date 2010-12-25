package org.pvoid.apteryxaustralis.accounts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.pvoid.apteryxaustralis.net.IResponseParser;
import org.xml.sax.Attributes;

public class TerminalsSection implements IResponseParser
{
  private static final int STATE_NONE = 0;
  private static final int STATE_TERMINALS = 1;
  
  private int _State;
  private ArrayList<Terminal> _Terminals = null;
  
  public static TerminalsSection getParser()
  {
    return(new TerminalsSection());
  }
  
  public TerminalsSection()
  {
    
  }
  
  @Override
  public void SectionStart()
  {
    _State = STATE_NONE;
  }

  @Override
  public void SectionEnd()
  {
    // TODO Auto-generated method stub
  }

  @Override
  public void ElementStart(String name, Attributes attributes)
  {
    if(name.equals("getTerminals"))
    {
      _State = STATE_TERMINALS;
      return;
    }
////////
    if(name.equals("row") && _State==STATE_TERMINALS)
    {
      try
      {
        String id = attributes.getValue("trm_id");
        String agent_id = attributes.getValue("agt_id");
        Terminal terminal = new Terminal(Long.parseLong(id),
                                         attributes.getValue("full_address"),
                                         attributes.getValue("trm_display"),
                                         Long.parseLong(agent_id));
        if(_Terminals==null)
          _Terminals = new ArrayList<Terminal>();
        _Terminals.add(terminal);
      }
      catch(NumberFormatException e)
      {
        
      }
    }
  }

  @Override
  public void ElementEnd(String name, String innerText)
  {
    if(name.equals("getTerminals"))
    {
      _State = STATE_NONE;
      return;
    }
  }
  
  public List<Terminal> getTerminals()
  {
    return(_Terminals);
  }
}
