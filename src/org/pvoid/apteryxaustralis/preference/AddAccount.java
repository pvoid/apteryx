package org.pvoid.apteryxaustralis.preference;

import org.pvoid.apteryxaustralis.R;

import android.content.Context;
import android.preference.Preference;

public class AddAccount extends Preference
{
  public AddAccount(Context context)
  {
    super(context);
    setLayoutResource(R.layout.accounts);
  }
}
