package org.pvoid.apteryxaustralis.accounts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TerminalsStorage extends Storage<Terminal>
{
  private static TerminalsStorage _Storage = new TerminalsStorage();
  private final static Comparator<Terminal> _Comparator = new Comparator<Terminal>()
  {
    @Override
    public int compare(Terminal object1, Terminal object2)
    {
      return (int)(object1.Id() - object2.Id());
    }
  };
  
  public static TerminalsStorage Instance()
  {
    return(_Storage);
  }
  
  @Override
  protected Terminal EmptyItem(long id)
  {
    return(new Terminal(id));
  }

  @Override
  protected String FileName()
  {
    return("terminals.data");
  }

  public List<Terminal> TerminalsForAgents(List<Agent> agents)
  {
    ArrayList<Terminal> terminals = new ArrayList<Terminal>();
////////
    long ids[] = new long[agents.size()];
    int index = 0;
    for(Agent agent : agents)
    {
      ids[index++] = agent.Id();
    }
    Arrays.sort(ids);
////////
    for(Terminal terminal : this)
    {
      if(Arrays.binarySearch(ids,terminal.getAgentId())>=0)
        terminals.add(terminal);
    }
////////
    return(terminals);
  }
  
  public List<Terminal> TerminalsForAgent(Agent agent)
  {
    ArrayList<Terminal> terminals = new ArrayList<Terminal>();
    for(Terminal terminal : this)
    {
      if(agent.Id() == terminal.getAgentId())
        terminals.add(terminal);
    }
    return(terminals);
  }

  @Override
  protected Comparator<Terminal> comparator()
  {
    return _Comparator;
  }
}
