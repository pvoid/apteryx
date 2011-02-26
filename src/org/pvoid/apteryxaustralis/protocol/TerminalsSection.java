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

package org.pvoid.apteryxaustralis.protocol;

import java.util.ArrayList;
import java.util.List;

import org.pvoid.apteryxaustralis.net.IResponseParser;
import org.pvoid.apteryxaustralis.types.Terminal;
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
        e.printStackTrace();
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
