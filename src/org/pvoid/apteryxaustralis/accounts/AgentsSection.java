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

package org.pvoid.apteryxaustralis.accounts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.pvoid.apteryxaustralis.net.IResponseParser;
import org.xml.sax.Attributes;

import android.util.Log;

public class AgentsSection implements IResponseParser
{
  private static final int STATE_NONE = 0;
  private static final int STATE_AGENT_INFO = 1;
  private static final int STATE_AGENTS_INFO = 2;
  
  private int _CurrentState;
  private ArrayList<Agent> _Agents = null;
  private Agent _CurrentAgent;
  
  public static AgentsSection getParser()
  {
    return(new AgentsSection());
  }

  @Override
  public void ElementStart(String tagName, Attributes attributes)
  {
    if(tagName.equals("getAgentInfo"))
    {
      _CurrentState = STATE_AGENT_INFO;
      return;
    }
////////
    if(tagName.equals("getAgents"))
    {
      _CurrentState = STATE_AGENTS_INFO;
      return;
    }
////////
    if(tagName.equals("agent"))
    {
      String id = attributes.getValue("id");
      String name = attributes.getValue("name");
      String phone = attributes.getValue("phone");
      try
      {
        _CurrentAgent = new Agent(Long.parseLong(id),name,phone);
      }
      catch(NumberFormatException e)
      {
        Log.e("Apteryx", "Invalid agent id:"+id);
        e.printStackTrace();
      }
      return;
    }
/////////
    if(tagName.equals("row") && _CurrentState == STATE_AGENTS_INFO)
    {
      String id = attributes.getValue("agt_id");
      String name = attributes.getValue("agt_name");
      if(_Agents==null)
        _Agents = new ArrayList<Agent>();
      try
      {
        Agent agent = new Agent(Long.parseLong(id),name,null);
        agent.setAccount(_CurrentAgent.getId());
        _Agents.add(agent);
      }
      catch(NumberFormatException e)
      {
        Log.e("Apteryx", "Invalid agent id:"+id);
        e.printStackTrace();
      }
      return;
    }
  }

  @Override
  public void ElementEnd(String name, String innerText)
  {
    // TODO Auto-generated method stub
  }

  @Override
  public void SectionStart()
  {
    _CurrentState = STATE_NONE;
  }

  @Override
  public void SectionEnd()
  {
    if(_Agents!=null)
      Arrays.sort(_Agents.toArray(), new Comparator<Object>()
        {
          @Override
          public int compare(Object object1, Object object2)
          {
            return((int)( ((Agent)object1).getId() - ((Agent)object2).getId()) );
          }
        });
  }
  
  public Agent GetAgentInfo()
  {
    return(_CurrentAgent);
  }
  
  public List<Agent> getAgents()
  {
    return(_Agents);
  }
}
