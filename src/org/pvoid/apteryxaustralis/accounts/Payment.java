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

public class Payment
{
  private final long _mId;
  private final long _mTerminalId;
  private int _mStatus;
  private float _mFromAmount;
  private float _mToAmount;
  private long _mProviderId;
  private String _mProviderName;
  private long _mDateInTerminal;
  private long _mDateInProcessing;

  public Payment(long id, long terminalId)
  {
    _mId = id;
    _mTerminalId = terminalId;
  }

  public long getId()
  {
    return _mId;
  }

  public long getTerminalId()
  {
    return _mTerminalId;
  }

  public float getFromAmount()
  {
    return _mFromAmount;
  }

  public float getToAmount()
  {
    return _mToAmount;
  }

  public long getProviderId()
  {
    return _mProviderId;
  }

  public String getProviderName()
  {
    return _mProviderName;
  }

  public long getDateInTerminal()
  {
    return _mDateInTerminal;
  }

  public long getDateInProcessing()
  {
    return _mDateInProcessing;
  }

  public void setFromAmount(float fromAmount)
  {
    _mFromAmount = fromAmount;
  }

  public void setToAmount(float toAmount)
  {
    _mToAmount = toAmount;
  }

  public void setProviderId(long providerId)
  {
    _mProviderId = providerId;
  }

  public void setProviderName(String providerName)
  {
    _mProviderName = providerName;
  }

  public void setDateInTerminal(long dateInTerminal)
  {
    _mDateInTerminal = dateInTerminal;
  }

  public void setDateInProcessing(long dateInProcessing)
  {
    _mDateInProcessing = dateInProcessing;
  }

  public int getStatus()
  {
    return _mStatus;
  }

  public void setStatus(int status)
  {
    _mStatus = status;
  }
}
