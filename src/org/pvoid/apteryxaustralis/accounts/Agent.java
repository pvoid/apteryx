package org.pvoid.apteryxaustralis.accounts;

import android.os.Parcel;
import android.os.Parcelable;

public class Agent extends Preserved implements Parcelable
{
  private static final long serialVersionUID = 5056483544575722367L;
  private String _Name;
  private String _Phone;
  private long _AccountId;
  
  public Agent(long id)
  {
    super(id);
  }
  
  public Agent(long id, String name, String phone)
  {
    super(id);
    _Name = name;
    _Phone = phone;
  }
  
  public String getName()
  {
    return(_Name);
  }
  
  public void setName(String name)
  {
    _Name = name;
  }
  
  public String getPhone()
  {
    return(_Phone);
  }
  
  public void setPhone(String phone)
  {
    _Phone = phone;
  }
  
  public long getAccount()
  {
    return(_AccountId);
  }
  
  public void setAccount(long accountId)
  {
    _AccountId = accountId;
  }
  
  @Override
  public int describeContents()
  {
    return 0;
  }
  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeLong(Id());
    dest.writeString(_Name);
    dest.writeString(_Phone);
  }
  
  public static final Parcelable.Creator<Agent> CREATOR = new Parcelable.Creator<Agent>()
  {
    @Override
    public Agent[] newArray(int size)
    {
      return(new Agent[size]);
    }
    
    @Override
    public Agent createFromParcel(Parcel source)
    {
      Agent agent = new Agent(source.readLong());
      agent._Name = source.readString();
      agent._Phone = source.readString();
      return(agent);
    }
  };
  
  public String toString()
  {
    return(_Name);
  }

  @Override
  public <T extends Preserved> void Copy(T another)
  {
    Agent agent;
    try
    {
      agent = (Agent)another;
    } 
    catch(ClassCastException e)
    {
      e.printStackTrace();
      return;
    }
    _Name = agent.getName();
    _Phone = agent.getPhone();    
  }
}
