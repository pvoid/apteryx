package org.pvoid.apteryxaustralis.net;

import java.util.List;

import org.pvoid.apteryxaustralis.accounts.ReportsSection;
import org.pvoid.apteryxaustralis.accounts.TerminalStatus;
import org.pvoid.apteryxaustralis.accounts.TerminalsStatusesStorage;

import android.os.AsyncTask;

public class StatusRefreshTask extends AsyncTask<String, Integer, Boolean>
{
	public static List<TerminalStatus> GetStatuses(String login, String passwordHash, String terminal)
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
	protected Boolean doInBackground(String... args)
	{
		if(args.length!=3)
			return Boolean.FALSE;
////////
		List<TerminalStatus> statuses = GetStatuses(args[0], args[1], args[2]);
		if(statuses==null)
			return Boolean.FALSE;
  	TerminalsStatusesStorage.Instance().Add(statuses);
////////
		return Boolean.TRUE;
	}
}
