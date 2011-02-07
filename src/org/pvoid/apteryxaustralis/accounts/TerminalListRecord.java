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
