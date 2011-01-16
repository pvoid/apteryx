package org.pvoid.apteryxaustralis.accounts;

public class TerminalListRecord
{
  private long _mId;
	private String _mName;
	private TerminalStatus _mStatus;
	
	public TerminalListRecord(Terminal terminal, TerminalStatus status)
	{
    _mId = terminal.getId();
		_mName = terminal.getDisplayName();
		_mStatus = status;
	}

  public long getId()
  {
    return _mId;
  }

	public TerminalStatus getStatus()
	{
		return _mStatus;
	}
	
	@Override
	public String toString()
	{
		return _mName;
	}
}
