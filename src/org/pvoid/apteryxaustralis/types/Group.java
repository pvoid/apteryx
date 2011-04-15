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

package org.pvoid.apteryxaustralis.types;

public class Group// implements Parcelable
{
  public long id;
  public String name;
  public long accountId;
  public double balance;
  public double overdraft;
  public long   lastUpdate;

/*  @Override
  public int describeContents()
  {
    return 0;
  }
  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeLong(id);
    dest.writeString(name);
  }
  
  public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>()
  {
    @Override
    public Group[] newArray(int size)
    {
      return(new Group[size]);
    }
    
    @Override
    public Group createFromParcel(Parcel source)
    {
      Group group = new Group();
      group.id = source.readLong();
      group.name = source.readString();
      return(group);
    }
  };*/
  
  public String toString()
  {
    return(name);
  }
}
