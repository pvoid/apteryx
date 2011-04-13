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

package org.pvoid.apteryxaustralis.ui;

import java.util.ArrayList;
import java.util.Comparator;

import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;
import org.pvoid.apteryxaustralis.*;
import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.Group;
import org.pvoid.apteryxaustralis.preference.Preferences;
import org.pvoid.apteryxaustralis.storage.IStorage;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpStorage;
import org.pvoid.apteryxaustralis.accounts.Terminal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import org.pvoid.apteryxaustralis.preference.AddAccountActivity;
import org.pvoid.apteryxaustralis.preference.CommonSettings;
import org.pvoid.common.views.SlideBand;

public class MainActivity extends Activity implements OnClickListener
{
  private static final int SETTINGS_MENU_ID = Menu.FIRST+1;
  private static final int REFRESH_MENU_ID = Menu.FIRST+2;
  
  private IStorage _mStorage;

  private int _mSpinnerCount = 0;
  private Animation _mSpinnerAnimation;
  private ArrayList<GroupArrayAdapter> _mGroups;
  private SlideBand _mSlider;
  private AlertDialog _mAgentsDialog;
  
  public BroadcastReceiver UpdateMessageReceiver = new BroadcastReceiver()
  {
    @Override
    public void onReceive(Context context, Intent intent)
    {
      (new LoadFromStorageTask()).execute();
    }
  };
  
  private static final Comparator<Terminal> _mTerminalComparator = new Comparator<Terminal>()
  {
    @Override
    public int compare(Terminal object1, Terminal object2)
    {
      if(object1.State() == object2.State())
        return object1.Address().compareToIgnoreCase(object2.Address());

      if(object1.State()==0)
        return(1);
      if(object2.State()==0)
        return(-1);
      return(object1.State()-object2.State());
    }
  };
  
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    _mSlider = (SlideBand) findViewById(R.id.groups);
    _mSpinnerAnimation = AnimationUtils.loadAnimation(this,R.anim.rotation);
/////////
    _mGroups = new ArrayList<GroupArrayAdapter>();
    _mStorage = new OsmpStorage(this);
/////////
    if(Preferences.getAutoUpdate(this))
    {
      Intent serviceIntent = new Intent(this,UpdateStatusService.class);
      startService(serviceIntent);
    }
  }
  
  @Override
  public void onResume()
  {
    super.onResume();
/////////
    (new LoadFromStorageTask()).execute();
/////////
    IntentFilter filter = new IntentFilter(StatesReceiver.REFRESH_BROADCAST_MESSAGE);
    registerReceiver(UpdateMessageReceiver, filter);
  }
  
  @Override
  public void onPause()
  {
    super.onPause();
    unregisterReceiver(UpdateMessageReceiver);
  }
  
  @Override
  public void onStart()
  {
    super.onStart();
    //RestoreStates();
    Notifyer.HideNotification(this);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    boolean result = super.onCreateOptionsMenu(menu);
    if(result)
    {
      MenuItem item = menu.add(Menu.NONE, REFRESH_MENU_ID, Menu.NONE, R.string.refresh);
      item.setIcon(R.drawable.menu_refresh);
      
      item = menu.add(Menu.NONE, SETTINGS_MENU_ID, Menu.NONE, R.string.settings);
      item.setIcon(android.R.drawable.ic_menu_preferences);
    }
    return(result);
  }

  private void setSpinnerVisibility(boolean visible)
  {
    View spinner = findViewById(R.id.refresh_spinner);
    if(!visible)
    {
      if(_mSpinnerCount>0)
        --_mSpinnerCount;
    }
    else
      ++_mSpinnerCount;

    if(_mSpinnerCount==0)
    {
      spinner.clearAnimation();
      spinner.setVisibility(View.GONE);
    }
    else
    {
      spinner.setVisibility(View.VISIBLE);
      spinner.startAnimation(_mSpinnerAnimation);
    }
  }

  private void ShowPreferencesActivity()
  {
    Intent intent = new Intent(this,CommonSettings.class);
    startActivityForResult(intent, 0); 
  }

  protected void refreshData()
  {
    ArrayList<Account> accounts = new ArrayList<Account>();
    ArrayList<Group> groups = new ArrayList<Group>();
    _mStorage.getAccounts(accounts);
////////
    for(Account account : accounts)
    {
      groups.clear();
      _mStorage.getGroups(account.id, groups);
      for(Group group : groups)
      {
        GroupArrayAdapter adapter = null;
/////////////////
        for(int i=0,len=_mGroups.size();i<len;++i)
        {
          if(_mGroups.get(i).getGroupId()== group.id)
          {
            adapter = _mGroups.get(i);
            break;
          }
        }
////////////////
        if(adapter==null)
        {
          adapter = new GroupArrayAdapter(this, group);
          _mGroups.add(adapter);
        }
        else
        {
          adapter.setGroup(group);
        }
//////////////// Вытащим терминалы
        _mStorage.getTerminals(account.id, group, adapter);
      }
    }
  }

  private void fillAgents()
  {
    int index = 0;
    for(GroupArrayAdapter adapter : _mGroups)
    {
      adapter.sort(_mTerminalComparator);

      if(_mSlider.getChildCount()<=index)
      {
        ListView list = new ListView(this);
        list.setAdapter(adapter);
        list.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
        _mSlider.addView(list);
      }
    }
  }

  private void setCurrentAgentInfo()
  {
    ListView list = (ListView)_mSlider.getCurrentView();
    if(list==null)
      return;
    GroupArrayAdapter group = (GroupArrayAdapter)list.getAdapter();
    if(group==null)
      return;
//////////
    TextView text = (TextView) findViewById(R.id.agent_name);
    text.setText(group.getGroupName());
//////////
    text = (TextView) findViewById(R.id.agent_balance);
    StringBuilder balance = new StringBuilder(getText(R.string.balance));
    balance.append(": ").append(group.getGroupBalance());
    if(group.getGroupOverdraft()!=0)
      balance.append("  ").append(getString(R.string.overdraft)).append(": ").append(group.getGroupOverdraft());
    text.setText(balance.toString());
//////////
    text = (TextView) findViewById(R.id.agent_update_time);
    text.setText(getString(R.string.refreshed) + " " +
                        DateUtils.getRelativeTimeSpanString(group.getLastUpdateTime(),
                                                            System.currentTimeMillis(),
                                                            DateUtils.SECOND_IN_MILLIS,
                                                            DateUtils.FORMAT_ABBREV_ALL));
  }

   /**
   * Щелчок по кнопке со списком агентов
   * @param view сама кнопка вызывающая список агентов
   */
  public void agentsListClick(View view)
  {
    if(_mAgentsDialog==null)
    {
      AlertDialog.Builder dialog = new AlertDialog.Builder(this);
      ArrayList<Group> agents = new ArrayList<Group>();
      _mStorage.getGroups(agents);
      GroupsArrayAdapter adapter = new GroupsArrayAdapter(this,R.layout.agent_dialog_item,R.id.agent_name);
      for(Group agent : agents)
        adapter.add(agent);
      dialog.setAdapter(adapter,this);
      dialog.setTitle(R.string.agents_list);
      _mAgentsDialog =  dialog.create();
    }
/////////////
    _mAgentsDialog.show();
    _mAgentsDialog.getListView().setSelection(_mSlider.getCurrentViewIndex());
  }

  private void ShowSettingsAlarm()
  {
    setTitle(R.string.app_name);
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(getString(R.string.add_account_message))
           .setPositiveButton(R.string.settings,new OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                Intent intent = new Intent(MainActivity.this,AddAccountActivity.class);
                startActivityForResult(intent, 0);
              }
            })
           .show();
  }

  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case SETTINGS_MENU_ID:
        ShowPreferencesActivity();
        break;
      case REFRESH_MENU_ID:
        (new RefreshDataTask()).execute();
        break;
    }
    return(super.onOptionsItemSelected(item));
  }

  @Override
  public void onClick(DialogInterface dialogInterface, int index)
  {
    _mSlider.setCurrentView(index);
  }
  /**
   * Фоновое обновление данных из БД
   */
  private class LoadFromStorageTask extends AsyncTask<Void,Integer,Boolean>
  {
    @Override
    protected void onPreExecute()
    {
      setSpinnerVisibility(true);
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
      refreshData();
      return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
      if(aBoolean)
      {
        fillAgents();
        setCurrentAgentInfo();
      }
      setSpinnerVisibility(false);
    }
  }
  // TODO: Lock на обновление
  private class RefreshDataTask extends AsyncTask<Void,Integer,Boolean>
  {
    @Override
    protected void onPreExecute()
    {
      setSpinnerVisibility(true);
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
      ArrayList<Account> accounts = new ArrayList<Account>();
      _mStorage.getAccounts(accounts);
      for(Account account : accounts)
      {
        if(_mStorage.refresh(account)!=IStorage.RES_OK)
          return false;
      }
      refreshData();
      return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
      if(aBoolean)
      {
        fillAgents();
        setCurrentAgentInfo();
      }
      setSpinnerVisibility(false);
    }
  }
}