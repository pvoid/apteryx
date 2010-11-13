package org.pvoid.apteryxaustralis.accounts;

import java.util.Comparator;
import java.util.List;

public class AgentsStorage extends Storage<Agent>
{
  private static final AgentsStorage _Storage = new AgentsStorage();
  
  private final static Comparator<Agent> _Comparator = new Comparator<Agent>()
  {
    @Override
    public int compare(Agent object1, Agent object2)
    {
      return (int)(object1.Id() - object2.Id());
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
  
  // TODO: Функция конвертации из старого
}
