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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import org.pvoid.apteryxaustralis.Notifier;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.StatesReceiver;
import org.pvoid.apteryxaustralis.UpdateStatusService;
import org.pvoid.apteryxaustralis.preference.Preferences;
import org.pvoid.apteryxaustralis.types.*;
import org.pvoid.apteryxaustralis.net.Receiver;
import org.pvoid.apteryxaustralis.preference.CommonSettings;
import org.pvoid.apteryxaustralis.storage.Storage;
import org.pvoid.common.views.SlideBand;

import java.util.Comparator;
import java.util.TreeMap;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener, SlideBand.OnCurrentViewChangeListener, DialogInterface.OnClickListener
{
  private static final int SETTINGS_MENU_ID = Menu.FIRST+1;
  private static final int REFRESH_MENU_ID = Menu.FIRST+2;

  private static final int REQUEST_SETTINGS = 1;

  private static final Object _sRefreshLock = new Object();

  private boolean _mIsEmpty = false;
  /**
   * Компаратор для сортировки терминалов по статусу
   */
  private final Comparator<TerminalListRecord> _mComparator = new Comparator<TerminalListRecord>()
  {
    private int getCommonStatus(TerminalStatus status)
    {
      if(!status.getPrinterErrorId().equals("OK") || !status.getNoteErrorId().equals("OK"))
        return TerminalStatus.STATE_COMMON_ERROR;
      return status.getCommonState(MainActivity.this);
    }

    @Override
    public int compare(TerminalListRecord a, TerminalListRecord b)
    {
      TerminalStatus status;
      int statusA = TerminalStatus.STATE_COMMON_NONE;
      int statusB = TerminalStatus.STATE_COMMON_NONE;
////////
      status = a.getStatus();
      if(status!=null)
        statusA = getCommonStatus(status);
////////
      status = b.getStatus();
      if(status!=null)
        statusB = getCommonStatus(status);
////////
      Payment payment;
      if(statusA==TerminalStatus.STATE_COMMON_OK || statusA==TerminalStatus.STATE_COMMON_NONE)
      {
        payment = a.getPayment();
        if(payment!=null)
        {
          if((System.currentTimeMillis() - payment.getDateInTerminal())> Preferences.getPaymentTimeout(MainActivity.this))
            statusA = TerminalStatus.STATE_COMMON_WARNING;
        }
      }
      if(statusB==TerminalStatus.STATE_COMMON_OK || statusB==TerminalStatus.STATE_COMMON_NONE)
      {
        payment = b.getPayment();
        if(payment!=null)
        {
          if((System.currentTimeMillis() - payment.getDateInTerminal())> Preferences.getPaymentTimeout(MainActivity.this))
            statusB = TerminalStatus.STATE_COMMON_WARNING;
        }
      }
////////
      int result = statusB - statusA;
      if(result==0)
      {
        result = a.toString().compareTo(b.toString());
      }
      return result;
    }
  };

  private BroadcastReceiver _mUpdateMessageReceiver = new BroadcastReceiver()
  {
    @Override
    public void onReceive(Context context, Intent intent)
    {
      Log.v("BroadcastReceiver","Task received");
      (new RefreshFromDbTask()).execute();
    }
  };

  private Animation _mSpinnerAnimation;
  private SlideBand _mBand;
  private TreeMap<Long,TerminalListRecord> _mStatuses;
  private AlertDialog _mAgentsDialog;
  private RefreshTask _mCurrentRefreshTask = null;
  private static final int DIALOG_NEED_SETTINGS = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    _mSpinnerAnimation = AnimationUtils.loadAnimation(this,R.anim.rotation);
    _mBand = new SlideBand(this);
    _mBand.setOnCurrentViewChangeListener(this);
    _mStatuses = new TreeMap<Long,TerminalListRecord>();
////////
    LinearLayout layout = (LinearLayout) findViewById(R.id.mainscreen);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0);
    params.weight=1;
    layout.addView(_mBand,params);
///////
    if(Preferences.getAutoUpdate(this))
    {
      Intent serviceIntent = new Intent(MainActivity.this,UpdateStatusService.class);
      startService(serviceIntent);
    }
///////
    IntentFilter filter = new IntentFilter(StatesReceiver.REFRESH_BROADCAST_MESSAGE);
    registerReceiver(_mUpdateMessageReceiver, filter);
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    Notifier.HideNotification(this);
    (new RefreshFromDbTask()).execute();
  }

  @Override
  protected void onDestroy()
  {
    super.onDestroy();
    unregisterReceiver(_mUpdateMessageReceiver);
  }
  /**
   * Создаем диалог. Пока что только диалог о необходимости настроек
   * @param id идентификатор диалога
   * @return Созданный диалог
   */
  @Override
  protected Dialog onCreateDialog(int id)
  {
    if(id == DIALOG_NEED_SETTINGS)
    {
      AlertDialog.Builder dialog = new AlertDialog.Builder(this);
      dialog.setTitle(R.string.app_name);
      dialog.setMessage(R.string.add_account_message);
/////////////
      dialog.setPositiveButton(R.string.settings,new DialogInterface.OnClickListener()
      {
        @Override
        public void onClick(DialogInterface dialogInterface, int i)
        {
          Intent intent = new Intent(MainActivity.this,CommonSettings.class);
          startActivityForResult(intent, REQUEST_SETTINGS);
          dialogInterface.dismiss();
        }
      });
/////////////
      dialog.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener()
      {
        @Override
        public void onClick(DialogInterface dialogInterface, int i)
        {
          dialogInterface.dismiss();
        }
      });
      return dialog.create();
    }
/////////////
    return super.onCreateDialog(id);
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
      Iterable<Agent> agents = Storage.getAgents(this,Storage.AgentsTable.NAME);
      ArrayAdapter<Agent> agentsAdapter = new ArrayAdapter<Agent>(this,android.R.layout.select_dialog_item);
      for(Agent agent : agents)
        agentsAdapter.add(agent);
      dialog.setAdapter(agentsAdapter,this);
      dialog.setTitle(R.string.agents_list);
      _mAgentsDialog =  dialog.create();
    }
/////////////
    _mAgentsDialog.show();
    _mAgentsDialog.getListView().setSelection(_mBand.getCurrentViewIndex());
  }

  private void setSpinnerVisibility(boolean visible)
  {
    View spinner = findViewById(R.id.refresh_spinner);
    if(!visible)
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

  protected void fillAgentsList(TerminalsArrayAdapter adapter)
  {
    Iterable<Terminal> terminals = Storage.getTerminals(MainActivity.this,adapter.getAgentId());
    for(Terminal terminal : terminals)
    {
      TerminalListRecord record = _mStatuses.get(terminal.getId());
      if(record!=null)
        record.setTerminal(terminal);
      else
      {
        record = new TerminalListRecord(terminal,null,null);
        Log.w(MainActivity.class.getCanonicalName(),"Record not found ID#"+terminal.getId());
      }
      adapter.add(record);
    }
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int index, long id)
  {
    TerminalsArrayAdapter adapter = (TerminalsArrayAdapter)adapterView.getAdapter();
    TerminalListRecord record = adapter.getItem(index);
    Intent intent = new Intent(this,TerminalInfoActivity.class);
    intent.putExtra("id",record.getId());
    startActivityForResult(intent,0);
  }

  private void setAgentTitle(TerminalsArrayAdapter adapter)
  {
    if(adapter==null)
    {
      ListView view = (ListView) _mBand.getCurrentView();
      if(view!=null)
        adapter = (TerminalsArrayAdapter)view.getAdapter();
    }
    if(adapter!=null)
    {
      TextView title = (TextView) findViewById(R.id.agent_name);
      title.setText(adapter.getAgentName());
    }
  }

  @Override
  public void CurrentViewChanged(View v)
  {
    setAgentTitle((TerminalsArrayAdapter)((ListView)v).getAdapter());
  }

   @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    boolean result = super.onCreateOptionsMenu(menu);
    if(result)
    {
      MenuItem item = menu.add(Menu.NONE, REFRESH_MENU_ID, Menu.NONE, R.string.refresh);
      item.setIcon(R.drawable.ic_menu_refresh);

      item = menu.add(Menu.NONE, SETTINGS_MENU_ID, Menu.NONE, R.string.settings);
      item.setIcon(android.R.drawable.ic_menu_preferences);
    }
    return(result);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case SETTINGS_MENU_ID:
        Intent intent = new Intent(this,CommonSettings.class);
        startActivityForResult(intent, REQUEST_SETTINGS);
        break;
      case REFRESH_MENU_ID:
        refreshStatuses();
        break;
    }
    return(super.onOptionsItemSelected(item));
  }

  @Override
  public void onClick(DialogInterface dialogInterface, int index)
  {
    _mBand.setCurrentView(index);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    if(requestCode==REQUEST_SETTINGS)
    {
      switch(resultCode)
      {
        case CommonSettings.RESULT_REFRESH:
          (new RefreshFromDbTask()).execute();
          break;
        case CommonSettings.RESULT_RELOAD:
          ViewGroup view = (ViewGroup) findViewById(R.id.mainscreen);
          view.removeView(_mBand);
          (new RefreshFromDbTask()).execute();
          break;
      }
    }
  }

  private ListView createList()
  {
    ListView list = new ListView(MainActivity.this);
    list.setOnItemClickListener(MainActivity.this);
    return list;
  }

  private TerminalsArrayAdapter addAgentToList(Agent agent, int index, boolean replace)
  {
    TerminalsArrayAdapter adapter = new TerminalsArrayAdapter(MainActivity.this,agent,R.layout.terminal,R.id.list_title);
    fillAgentsList(adapter);
    LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);

    if(_mBand.getChildCount()<=index)
    {
      ListView list = createList();
      list.setAdapter(adapter);
      _mBand.addView(list,params);
    }
    else if(replace)
    {
      ListView list = (ListView)_mBand.getChildAt(index);
      list.setAdapter(adapter);
    }
    else
    {
      ListView list = createList();
      list.setAdapter(adapter);
      _mBand.addView(list,index,params);
    }
////////
    return adapter;
  }

  private void refreshStatuses()
  {
    synchronized(_sRefreshLock)
    {
      if(_mCurrentRefreshTask!=null)
        return;
      _mCurrentRefreshTask = new RefreshTask();
    }
    setSpinnerVisibility(true);
    _mCurrentRefreshTask.execute();
  }
  /**
   * Обновление списка терминалов
   */
  private class RefreshTask extends AsyncTask<Void,Void,Boolean>
  {
    @Override
    protected Boolean doInBackground(Void... voids)
    {
      if(Preferences.getReceivePayments(MainActivity.this))
        Receiver.RefreshPayments(MainActivity.this);
      return Receiver.RefreshStates(MainActivity.this, _mStatuses);
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
      synchronized(_sRefreshLock)
      {
        if(result)
          for(int count=_mBand.getChildCount()-1;count>=0;--count)
          {
            ListView child = (ListView) _mBand.getChildAt(count);
            TerminalsArrayAdapter statuses = (TerminalsArrayAdapter) child.getAdapter();
            statuses.sort(_mComparator);
            statuses.notifyDataSetChanged();
          }
        _mCurrentRefreshTask = null;
        setSpinnerVisibility(false);
      }
    }
  }

  private class RefreshFromDbTask extends AsyncTask<Void,Void,Iterable<Agent>>
  {
    @Override
    protected void onPreExecute()
    {
      setSpinnerVisibility(true);
    }

    @Override
    protected Iterable<Agent> doInBackground(Void... voids)
    {
      Iterable<TerminalStatus> statuses = Storage.getStatuses(MainActivity.this);
      if(statuses!=null)
        for(TerminalStatus status : statuses)
        {
          _mStatuses.put(status.getId(),new TerminalListRecord(null,status,null));
        }
      Iterable<Payment> payments = Storage.getPayments(MainActivity.this);
      if(payments!=null)
        for(Payment payment :  payments)
        {
          TerminalListRecord record = _mStatuses.get(payment.getTerminalId());
          if(record!=null)
            record.setPayment(payment);
        }

///////////
      return Storage.getAgents(MainActivity.this,Storage.AgentsTable.NAME);
    }

    @Override
    protected void onPostExecute(Iterable<Agent> agents)
    {
      int index = 0;
      int count = _mBand.getChildCount();
      TerminalsArrayAdapter adapter;
///////////
      for(Agent agent : agents)
      {
        if(index>=count)
        {
          adapter = addAgentToList(agent,index,false);
        }
        else
        {
          ListView list = (ListView)_mBand.getChildAt(index);
          adapter = (TerminalsArrayAdapter)list.getAdapter();
          if(adapter.getAgentId()!=agent.getId())
          {
            addAgentToList(agent,index,false);
            count = _mBand.getChildCount();
          }
        }
/////////////
        adapter.sort(_mComparator);
        adapter.notifyDataSetInvalidated();
        ++index;
      }
///////////
      setAgentTitle((TerminalsArrayAdapter)((ListView)_mBand.getCurrentView()).getAdapter());
      if(_mBand.getChildCount()>1)
      {
        View button = findViewById(R.id.agent_list_button);
        button.setVisibility(View.VISIBLE);
      }
///////////
      setSpinnerVisibility(false);
    }
  }
}
