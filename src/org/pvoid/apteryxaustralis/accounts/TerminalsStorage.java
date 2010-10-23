package org.pvoid.apteryxaustralis.accounts;

public class TerminalsStorage extends Storage<Terminal>
{
  private static TerminalsStorage _Storage = new TerminalsStorage();
  
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

}
