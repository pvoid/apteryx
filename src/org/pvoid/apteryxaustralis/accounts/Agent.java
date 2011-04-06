/*
 * Copyright (C) 2010-2011  Dmitry Petuhov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pvoid.apteryxaustralis.accounts;

import android.os.Parcel;
import android.os.Parcelable;

public class Agent implements Parcelable
{
  public long Id;
  public String Name;
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
      Agent agent = new Agent();
      agent.Id = source.readLong();
      agent.Name = source.readString();
      return(agent);
    }
  };
  
  public String toString()
  {
    return(Name);
  }
}
