package org.pvoid.apteryxaustralis.net;

import org.pvoid.apteryxaustralis.accounts.ReportsSection;
import org.pvoid.apteryxaustralis.accounts.TerminalStatus;

public class StatusRefreshRunnable implements Runnable
{
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
	
	@Override
	public void run()
	{
	}
}
