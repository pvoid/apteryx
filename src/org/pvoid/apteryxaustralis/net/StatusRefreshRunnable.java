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

package org.pvoid.apteryxaustralis.net;

import org.pvoid.apteryxaustralis.accounts.ReportsSection;
import org.pvoid.apteryxaustralis.accounts.TerminalStatus;

import java.util.Iterator;

public class StatusRefreshRunnable implements Runnable
{
  private final String _mLogin;
  private final String _mPassword;
  private final String _mTerminal;
  private Iterable<TerminalStatus> _mStatuses = null;

  public StatusRefreshRunnable(String terminal, String login, String password)
  {
    _mLogin = login;
    _mPassword = password;
    _mTerminal = terminal;
  }

	public static Iterable<TerminalStatus> GetStatuses(String login, String passwordHash, String terminal)
	{
		Request request = new Request(login, passwordHash, terminal);
		request.getTerminalsStatus();
		Response response = request.getResponse();
		if(response==null)
			return null;
////////
		ReportsSection section = response.Reports();
		if(section==null)
			return null;
////////
		return section.getTerminalsStatus();
	}

  public static TerminalStatus GetStatus(String login, String passwordHash, String terminal, long terminalId)
  {
    Request request = new Request(login, passwordHash, terminal);
		request.getTerminalStatus(terminalId);
		Response response = request.getResponse();
		if(response==null)
			return null;
////////
		ReportsSection section = response.Reports();
		if(section==null)
			return null;
////////
    Iterable<TerminalStatus> statuses = section.getTerminalsStatus();
    if(statuses!=null)
    {
      Iterator<TerminalStatus> status = statuses.iterator();
      if(status.hasNext())
        return status.next();
    }
		return null;
  }
	
	@Override
	public void run()
	{
    _mStatuses = GetStatuses(_mLogin,_mPassword,_mTerminal);
	}

  public Iterable<TerminalStatus> getStatuses()
  {
    return _mStatuses;
  }
}
