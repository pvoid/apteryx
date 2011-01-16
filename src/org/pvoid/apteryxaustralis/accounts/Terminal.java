package org.pvoid.apteryxaustralis.accounts;

public class Terminal
{
  private long _mId;
  private String _mAddress;
  private String _mDisplayName;
  private long _mAgentId;

  public Terminal(long id, String address, String name,long agent)
  {
    _mId = id;
    _mAddress = address;
    _mDisplayName = name;
    _mAgentId = agent;
  }

  public Terminal(long id)
  {
    _mId = id;
  }

  public long getId()
  {
    return _mId;
  }

  public String getAddress()
  {
    return(_mAddress);
  }
  
  public void setAddress(String address)
  {
    _mAddress = address;
  }
  
  public String getDisplayName()
  {
    return(_mDisplayName);
  }
  
  public void setDisplayName(String name)
  {
    _mDisplayName = name;
  }
  
  public long getAgentId()
  {
    return(_mAgentId);
  }
  
  public void setAgentId(long agent)
  {
    _mAgentId = agent;
  }
}
