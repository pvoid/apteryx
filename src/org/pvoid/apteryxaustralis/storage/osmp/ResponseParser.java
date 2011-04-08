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

package org.pvoid.apteryxaustralis.storage.osmp;

import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.Agent;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

public class ResponseParser extends DefaultHandler
{
  private static final byte STATE_NONE = 0;
  private static final byte STATE_AGENT_INFO = 1;
  private static final byte STATE_AGENTS = 2;

  private Account _mAccount;
  private List<Agent> _mAgents;
  private int _mState = STATE_NONE;

  private int _mAccountResult;

  public ResponseParser(Account account, List<Agent> agents)
  {
    _mAccount = account;
    _mAgents = agents;
  }

  public int getAccountResult()
  {
    return _mAccountResult;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
    if("getAgentInfo".equals(localName))
    {
      _mState = STATE_AGENT_INFO;
      _mAccountResult = Integer.parseInt(attributes.getValue("result"));
      return;
    }
////////
    if("getAgents".equals(localName))
    {
      _mState = STATE_AGENTS;
      return;
    }
///////
    if("agent".equals(localName) && _mState==STATE_AGENT_INFO && _mAccount!= null)
    {
      _mAccount.id = Long.parseLong(attributes.getValue("id"));
      _mAccount.title = attributes.getValue("name");
      return;
    }
///////
    if("row".equals(localName) && _mState==STATE_AGENTS && _mAgents!=null)
    {
      Agent agent = new Agent();
      agent.Id = Long.parseLong(attributes.getValue("agt_id"));
      agent.Name = attributes.getValue("agt_name");
      _mAgents.add(agent);
    }

  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException
  {
    if("getAgentInfo".equals(localName))
    {
      _mState = STATE_NONE;
      return;
    }
////////
    if("getAgents".equals(localName))
    {
      _mState = STATE_NONE;
      return;
    }
  }
}
