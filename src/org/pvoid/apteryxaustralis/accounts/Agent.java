package org.pvoid.apteryxaustralis.accounts;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class Agent extends Preserved implements Parcelable, Serializable
{
  private static final long serialVersionUID = 1930822646830029307L;
  
  public String Name;
  public String Phone;
  
  public Agent(long id)
  {
    super(id);
  }
  
  public Agent(long id, String name, String phone)
  {
    super(id);
    Name = name;
    Phone = phone;
  }
  
  @Override
  public int describeContents()
  {
    return 0;
  }
  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeLong(Id);
    dest.writeString(Name);
    dest.writeString(Phone);
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
      agent.Name = source.readString();
      agent.Phone = source.readString();
      return(agent);
    }
  };
  
  public String toString()
  {
    return(Name);
  }
}
