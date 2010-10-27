package org.pvoid.apteryxaustralis.accounts;

public class TerminalsStatusesStorage extends Storage<TerminalStatus>
{
  private static TerminalsStatusesStorage _Storage = new TerminalsStatusesStorage();
  
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

}
