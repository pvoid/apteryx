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

package org.pvoid.apteryxaustralis.net;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.pvoid.apteryxaustralis.accounts.Agent;

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
  
