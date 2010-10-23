package org.pvoid.apteryxaustralis.preference;

import android.content.Context;
import android.preference.Preference;

public class AccountPreference extends Preference
{
  private long _Id;
  
  public AccountPreference(Context context, long agentId, String agentName)
  {
    super(context);
    setTitle(agentName);
    _Id = agentId;
  }
  
  public long Id()
  {
    return(_Id);
  }
}
