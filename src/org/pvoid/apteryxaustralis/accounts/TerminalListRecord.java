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

import android.text.TextUtils;

public class TerminalListRecord
{
  private Terminal _mTerminal;
	private TerminalStatus _mStatus;
	
	public TerminalListRecord(Terminal terminal, TerminalStatus status)
	{
    _mTerminal = terminal;
		_mStatus = status;
	}

  public long getId()
  {
    if(_mTerminal!=null)
      return _mTerminal.getId();
    if(_mStatus!=null)
      return _mStatus.getId();
    return 0;
  }

	public TerminalStatus getStatus()
	{
		return _mStatus;
	}
	
	@Override
	public String toString()
	{
    if(_mTerminal==null)
      return "";

    String name = _mTerminal.getDisplayName();
    if(TextUtils.isEmpty(name))
      name = _mTerminal.getAddress();

		return name;
	}

  public void setTerminal(Terminal terminal)
  {
    _mTerminal = terminal;
  }
}
