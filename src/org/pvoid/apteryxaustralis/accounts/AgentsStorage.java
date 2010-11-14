package org.pvoid.apteryxaustralis.accounts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

public class AgentsStorage extends Storage<Agent>
{
	private ArrayList<Agent> _AgentsByName = new ArrayList<Agent>();
  private static final AgentsStorage _Storage = new AgentsStorage();
  
  private final static Comparator<Agent> _Comparator = new Comparator<Agent>()
  {
    @Override
    public int compare(Agent object1, Agent object2)
    {
      return (int)(object1.Id() - object2.Id());
    }
  };
  
  private final static Comparator<Agent> _CompareByName = new Comparator<Agent>()
	{
		@Override
		public int compare(Agent object1, Agent object2)
		{
			return object1.getName().compareTo(object2.getName());
		}
	};
  
	private final Iterable<Agent> _AgentsByNameIterable = new Iterable<Agent>()
	{
		@Override
		public Iterator<Agent> iterator()
		{
			if(_AgentsByName!=null)
				return(_AgentsByName.iterator());
			return null;
		}
	};
	
  public static AgentsStorage Instance()
  {
    return(_Storage);
  }
  
  @Override
  protected String FileName()
  {
    return("agents.data");
  }

  @Override
  protected Agent EmptyItem(long id)
  {
    return(new Agent(id));
  }

  public List<Agent> Agents()
  {
    //ArrayList
    return(null);
  }

  @Override
  protected Comparator<Agent> comparator()
  {
    return _Comparator;
  }
  
  @Override
  public boolean Restore(Context context)
  {
  	boolean result = super.Restore(context);
  	if(result)
  	{
  		_AgentsByName.clear();
  		_AgentsByName.addAll(_Items);
  		Collections.sort(_AgentsByName,_CompareByName);
  	}
  	return result;
  }
  
  public Iterable<Agent> getAgentsByName()
  {
  	return _AgentsByNameIterable;
  }
  
  // TODO: Функция конвертации из старого
}
