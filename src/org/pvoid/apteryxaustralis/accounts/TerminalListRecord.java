package org.pvoid.apteryxaustralis.accounts;

public class TerminalListRecord
{
	private String _Name;
	private TerminalStatus _Status;
	
	public TerminalListRecord(Terminal terminal, TerminalStatus status)
	{
		_Name = terminal.DisplayName();
		_Status = status;
	}
	
	public TerminalStatus getStatus()
	{
		return _Status;
	}
	
	@Override
	public String toString()
	{
		return _Name;
	}
}
