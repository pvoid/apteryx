package org.pvoid.apteryxaustralis.accounts;

public class AgentsStorage extends Storage<Agent>
{
  private static final AgentsStorage _Storage = new AgentsStorage();
  
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

  // TODO: Функция конвертации из старого
}
