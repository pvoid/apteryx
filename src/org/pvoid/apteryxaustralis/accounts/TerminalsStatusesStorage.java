package org.pvoid.apteryxaustralis.accounts;

import java.util.Comparator;

public class TerminalsStatusesStorage extends Storage<TerminalStatus>
{
  private static TerminalsStatusesStorage _Storage = new TerminalsStatusesStorage();
  
  private final static Comparator<TerminalStatus> _Comparator = new Comparator<TerminalStatus>()
  {
    @Override
    public int compare(TerminalStatus object1, TerminalStatus object2)
    {
      return (int)(object1.Id() - object2.Id());
    }
  };
  
  public static TerminalsStatusesStorage Instance()
  {
    return(_Storage);
  }
  
  @Override
  protected TerminalStatus EmptyItem(long id)
  {
    return(new TerminalStatus(id));
  }

  @Override
  protected String FileName()
  {
    return("states.data");
  }

  @Override
  protected Comparator<TerminalStatus> comparator()
  {
    return _Comparator;
  }

}
