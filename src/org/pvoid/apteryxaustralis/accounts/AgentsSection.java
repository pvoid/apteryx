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
  private final int STATE_NONE = 0;
  private final int STATE_AGENTINFO = 1;
  private final int STATE_AGENTSINFO = 2;
  
  private int _CurrentState;
  private ArrayList<Agent> _Agents;
  private Agent _CurrentAgent;
  
  public static AgentsSection getParser()
  {
    return(new AgentsSection());
  }

  @Override
  public void ElementStart(String tagName, Attributes attributes)
  {
    if(tagName.equalsIgnoreCase("getAgentInfo"))
    {
      _CurrentState = STATE_AGENTINFO;
      return;
    }
////////
    if(tagName.equalsIgnoreCase("getAgents"))
    {
      _CurrentState = STATE_AGENTSINFO;
      return;
    }
////////
    if(tagName.equalsIgnoreCase("agent"))
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
    if(tagName.equalsIgnoreCase("row") && _CurrentState == STATE_AGENTSINFO)
    {
      String id = attributes.getValue("agt_id");
      String name = attributes.getValue("agt_name");
      if(_Agents==null)
        _Agents = new ArrayList<Agent>();
      try
      {
        Agent agent = new Agent(Long.parseLong(id),name,null);
        agent.setAccount(_CurrentAgent.Id());
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
  public void ElementEnd(String innerText)
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
            return((int)( ((Agent)object1).Id() - ((Agent)object2).Id()) );
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
