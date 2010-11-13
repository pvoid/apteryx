package org.pvoid.apteryxaustralis.accounts;

public class Terminal extends Preserved
{
  private static final long serialVersionUID = -3170367640652692520L;
  /////////
  private String _Address;
  private String _DisplayName;
  private long   _AgentId;
  
  public Terminal(long id)
  {
    super(id);
  }

  public String Address()
  {
    return(_Address);
  }
  
  public void setAddress(String address)
  {
    _Address = address;
  }
  
  public String DisplayName()
  {
    return(_DisplayName);
  }
  
  public void setDisplayName(String name)
  {
    _DisplayName = name;
  }
  
  public long getAgentId()
  {
    return(_AgentId);
  }
  
  public void setAgentId(long agent)
  {
    _AgentId = agent;
  }
  
  @Override
  public <T extends Preserved> void Copy(T another)
  {
    Terminal terminal = (Terminal)another;
    _Address = terminal.Address();
    _DisplayName = terminal.DisplayName();
  } 
}
