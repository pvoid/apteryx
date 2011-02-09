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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.accounts.Agent;
import org.pvoid.apteryxaustralis.accounts.Terminal;
import org.pvoid.apteryxaustralis.accounts.TerminalListRecord;
import org.pvoid.apteryxaustralis.accounts.TerminalStatus;
import org.pvoid.apteryxaustralis.net.StatusRefresher;
import org.pvoid.apteryxaustralis.preference.CommonSettings;
import org.pvoid.apteryxaustralis.storage.Storage;
import org.pvoid.common.views.SlideBand;

import java.util.Comparator;
import java.util.TreeMap;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener, SlideBand.OnCurrentViewChangeListener, DialogInterface.OnClickListener
{
  private static final int SETTINGS_MENU_ID = Menu.FIRST+1;
  private static final int REFRESH_MENU_ID = Menu.FIRST+2;

  private static final Object _sRefreshLock = new Object();

  private boolean _mIsEmpty;
  /**
   * Компаратор для сортировки терминалов по статусу
   */
  private static final Comparator<TerminalListRecord> _mComparator = new Comparator<TerminalListRecord>()
  {
    private int getCommonStatus(TerminalStatus status)
    {
      if(!status.getPrinterErrorId().equals("OK") || !status.getNoteErrorId().equals("OK"))
        return TerminalStatus.STATE_COMMON_ERROR;
      return status.getCommonState();
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
      int result = statusB - statusA;
      if(result==0)
      {
        result = a.toString().compareTo(b.toString());
      }
      return result;
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
    setSpinnerVisibility(true);
    _mBand = new SlideBand(this);
    _mBand.setOnCurrentViewChangeListener(this);
    _mStatuses = new TreeMap<Long,TerminalListRecord>();

    (new SetupUITask()).execute();
  }


  @Override
  protected Dialog onCreateDialog(int id)
  {
    if(id == DIALOG_NEED_SETTINGS)
    {
      AlertDialog.Builder dialog = new AlertDialog.Builder(this);
      dialog.setTitle(R.string.app_name);
      dialog.setMessage(R.string.add_account_message);

      dialog.setPositiveButton(R.string.settings,new DialogInterface.OnClickListener()
      {
        @Override
        public void onClick(DialogInterface dialogInterface, int i)
        {
          Intent intent = new Intent(MainActivity.this,CommonSettings.class);
          startActivityForResult(intent, 0);
          dialogInterface.dismiss();
        }
      });
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
        record = new TerminalListRecord(terminal,null);
        Log.w(MainActivity.class.getCanonicalName(),"Recird not found ID#"+terminal.getId());
      }
      adapter.add(record);
    }

    adapter.sort(_mComparator);
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int index, long id)
  {
    TerminalsArrayAdapter adapter = (TerminalsArrayAdapter)adapterView.getAdapter();
    TerminalListRecord record = adapter.getItem(index);
    Intent intent = new Intent(this,TerminalInfo.class);
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
        startActivityForResult(intent, 0);
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
   * Создает списки для отображения терминалов агентов
   */
  private class SetupUITask extends AsyncTask<Void,Void,Boolean>
  {
    @Override
    protected Boolean doInBackground(Void... voids)
    {
      Iterable<TerminalStatus> statuses = Storage.getStatuses(MainActivity.this);
      if(statuses!=null)
        for(TerminalStatus status : statuses)
        {
          _mStatuses.put(status.getId(),new TerminalListRecord(null,status));
        }

      Iterable<Agent> agents = Storage.getAgents(MainActivity.this,Storage.AgentsTable.NAME);
      _mIsEmpty = agents == null;
      if(!_mIsEmpty)
      {
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
        for(Agent agent : agents)
        {
          ListView list = new ListView(MainActivity.this);
          list.setOnItemClickListener(MainActivity.this);
          TerminalsArrayAdapter adapter = new TerminalsArrayAdapter(MainActivity.this,agent,R.layout.terminal,R.id.list_title);
          fillAgentsList(adapter);
          list.setAdapter(adapter);
          _mBand.addView(list,params);
        }
      }
      return true;
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
      LinearLayout layout = (LinearLayout) findViewById(R.id.mainscreen);
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0);
      params.weight=1;
      layout.addView(_mBand,params);
      setAgentTitle(null);
      setSpinnerVisibility(false);
      if(_mBand.getChildCount()>1)
      {
        View button = findViewById(R.id.agent_list_button);
        button.setVisibility(View.VISIBLE);
      }

      if(_mIsEmpty)
        showDialog(DIALOG_NEED_SETTINGS);
    }
  }
  /**
   * Обновление списка терминалов
   */
  private class RefreshTask extends AsyncTask<Void,Void,Boolean>
  {
    @Override
    protected Boolean doInBackground(Void... voids)
    {
      return StatusRefresher.RefreshStates(MainActivity.this,_mStatuses);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
      synchronized(_sRefreshLock)
      {
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
}
