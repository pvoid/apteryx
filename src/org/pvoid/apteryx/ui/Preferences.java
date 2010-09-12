package org.pvoid.apteryx.ui;

import org.pvoid.apteryx.Consts;
import org.pvoid.apteryx.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;

public class Preferences extends TabActivity
{
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);    
    
    final TabHost tabs = getTabHost();
    tabs.addTab(tabs.newTabSpec(Consts.TAB_ACCOUNTS)
                    .setIndicator(getString(R.string.accounts))
                    .setContent(new Intent(this,AccountsList.class))
                );
    tabs.addTab(tabs.newTabSpec(Consts.TAB_PREFERENCES)
        .setIndicator(getString(R.string.settings))
        .setContent(new Intent(this,CommonSettings.class))
    );
  }
}
