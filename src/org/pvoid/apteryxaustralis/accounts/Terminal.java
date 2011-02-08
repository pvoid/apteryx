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
