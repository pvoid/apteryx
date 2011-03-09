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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.pvoid.apteryxaustralis.net.IResponseParser;
import org.pvoid.apteryxaustralis.types.Agent;
import org.xml.sax.Attributes;

import android.util.Log;

public class AgentsSection implements IResponseParser
{
  private static final int STATE_NONE = 0;
  private static final int STATE_AGENT_INFO = 1;
  private static final int STATE_AGENTS_INFO = 2;
  private static final int STATE_BALANCE_INFO = 3;

  private int _mCurrentState;
  private ArrayList<Agent> _mAgents = null;
  private Agent _mCurrentAgent;
  private ArrayList<BalanceInfo> _mBalances;
  
  public static AgentsSection getParser()
  {
    return(new AgentsSection());
  }

  public static class BalanceInfo
  {
    private float _mBalance;
    private float _mOverdraft;

    public float getBalance()
    {
      return _mBalance;
    }

    public void setBalance(float balance)
    {
      _mBalance = balance;
    }

    public float getOverdraft()
    {
      return _mOverdraft;
    }

    public void setOverdraft(float overdraft)
    {
      _mOverdraft = overdraft;
    }
  }

  @Override
  public void ElementStart(String tagName, Attributes attributes)
  {
    if("getAgentInfo".equals(tagName))
    {
      _mCurrentState = STATE_AGENT_INFO;
      return;
    }
////////
    if("getAgents".equals(tagName))
    {
      _mCurrentState = STATE_AGENTS_INFO;
      return;
    }
////////
    if("agent".equals(tagName))
    {
      String id = attributes.getValue("id");
      String name = attributes.getValue("name");
      String phone = attributes.getValue("phone");
      try
      {
        _mCurrentAgent = new Agent(Long.parseLong(id),name,phone);
      }
      catch(NumberFormatException e)
      {
        Log.e("Apteryx", "Invalid agent id:"+id);
        e.printStackTrace();
      }
      return;
    }
/////////
    if("row".equals(tagName) && _mCurrentState == STATE_AGENTS_INFO)
    {
      String id = attributes.getValue("agt_id");
      String name = attributes.getValue("agt_name");
      if(_mAgents ==null)
        _mAgents = new ArrayList<Agent>();
      try
      {
        Agent agent = new Agent(Long.parseLong(id),name,null);
        agent.setAccount(_mCurrentAgent.getId());
        _mAgents.add(agent);
      }
      catch(NumberFormatException e)
      {
        Log.e("Apteryx", "Invalid agent id:"+id);
        e.printStackTrace();
      }
      return;
    }
/////////
    if("getBalance".equals(tagName))
    {
      _mCurrentState = STATE_BALANCE_INFO;
      if(_mBalances==null)
        _mBalances = new ArrayList<BalanceInfo>();
      _mBalances.add(new BalanceInfo());
    }
  }

  @Override
  public void ElementEnd(String tagName, String innerText)
  {
    if("getAgentInfo".equals(tagName) || "getBalance".equals(tagName))
      _mCurrentState = STATE_NONE;
/////////
    if("balance".equals(tagName) && _mCurrentState == STATE_BALANCE_INFO)
    {
      _mBalances.get(_mBalances.size()-1).setBalance(Float.parseFloat(innerText));
    }
/////////
    if("overdraft".equals(tagName) && _mCurrentState == STATE_BALANCE_INFO)
    {
      _mBalances.get(_mBalances.size()-1).setOverdraft(Float.parseFloat(innerText));
    }
  }

  @Override
  public void SectionStart()
  {
    _mCurrentState = STATE_NONE;
  }

  @Override
  public void SectionEnd()
  {
    if(_mAgents !=null)
      Arrays.sort(_mAgents.toArray(), new Comparator<Object>()
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
    return(_mCurrentAgent);
  }
  
  public List<Agent> getAgents()
  {
    return _mAgents;
  }

  public List<BalanceInfo> getBalances()
  {
    return _mBalances;
  }
}
