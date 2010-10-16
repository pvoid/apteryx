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

  // TODO: Функция конвертации из старого
}
