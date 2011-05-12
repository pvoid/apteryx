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
import java.util.Hashtable;

import android.content.*;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.*;
import org.pvoid.apteryxaustralis.*;
import org.pvoid.apteryxaustralis.storage.ICommandResult;
import org.pvoid.apteryxaustralis.storage.States;
import org.pvoid.apteryxaustralis.types.Account;
import org.pvoid.apteryxaustralis.types.Group;
import org.pvoid.apteryxaustralis.preference.Preferences;
import org.pvoid.apteryxaustralis.storage.IStorage;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpStorage;
import org.pvoid.apteryxaustralis.types.ITerminal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import org.pvoid.apteryxaustralis.preference.AddAccountActivity;
import org.pvoid.apteryxaustralis.preference.CommonSettings;
import org.pvoid.apteryxaustralis.types.TerminalAction;
import org.pvoid.common.views.SlideBand;

public class MainActivity extends Activity implements OnClickListener,
                                                      AdapterView.OnItemClickListener,
                                                      SlideBand.OnCurrentViewChangeListener,
                                                      AdapterView.OnItemLongClickListener,
                                                      ICommandResult
{
  private static final int SETTINGS_MENU_ID = Menu.FIRST+1;
  private static final int REFRESH_MENU_ID = Menu.FIRST+2;
  
  private IStorage _mStorage;
  private States _mStates;

  private int _mSpinnerCount = 0;
  private Animation _mSpinnerAnimation;
  private ArrayList<TerminalsArrayAdapter> _mGroups;
  private SlideBand _mSlider;
  private AlertDialog _mAgentsDialog;
  private GroupsArrayAdapter _mDialogAdapter;
  /**
   * Получатель события изменения данных о статусах терминалов
   */
  public BroadcastReceiver UpdateMessageReceiver = new BroadcastReceiver()
  {
    @Override
    public void onReceive(Context context, Intent intent)
    {
      (new LoadFromStorageTask()).execute();
    }
  };
  /**
   * Компаратор для сортировки терминалов
   *
   *   1) по статусу
   *   2) по заголовку
   */
  private static final Comparator<ITerminal> _mTerminalComparator = new Comparator<ITerminal>()
  {
    @Override
    public int compare(ITerminal left, ITerminal right)
    {
      int res = right.getState() - left.getState();
      if(res!=0)
        return(res);
      return left.getTitle().compareTo(right.getTitle());
    }
  };
  /**
   * Activity создается. Настроим внешний вид.
   * @param savedInstanceState предыдущее состаяние
   */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    _mSlider = (SlideBand) findViewById(R.id.groups);
    _mSlider.setOnCurrentViewChangeListener(this);
    _mSpinnerAnimation = AnimationUtils.loadAnimation(this,R.anim.rotation);
/////////
    _mGroups = new ArrayList<TerminalsArrayAdapter>();
    _mStorage = new OsmpStorage(this);
    _mStates = new States(this);
/////////
    if(Preferences.getAutoUpdate(this))
    {
      Intent serviceIntent = new Intent(this,UpdateStatusService.class);
      startService(serviceIntent);
    }
/////////
    //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
  }
  /**
   * Activity снова видима. Обновим данные
   */
  @Override
  public void onResume()
  {
    super.onResume();
/////////
    if(_mStorage.isEmpty())
      ShowSettingsAlarm();
    else
      (new LoadFromStorageTask()).execute();
/////////
    IntentFilter filter = new IntentFilter(StatesReceiver.REFRESH_BROADCAST_MESSAGE);
    registerReceiver(UpdateMessageReceiver, filter);
  }
  /**
   * Activity более не видна, приостановим breadcast-receiver
   */
  @Override
  public void onPause()
  {
    super.onPause();
    unregisterReceiver(UpdateMessageReceiver);
  }
  /**
   * Восстановление Activity, чистмм уведомления
   */
  @Override
  public void onStart()
  {
    super.onStart();
    Notifier.hideNotification(this,true);
  }
  /**
   * Создаем меню
   * @param menu собственно само меню
   * @return true если его можно показывать
   */
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
  /**
   * Отображение или крытие круглого бесконечного пргресса. Можно взывать рекурсивно, скроется только когда
   * число устновок будет равно числу скрытий
   * @param visible показать или спрятать вертушку
   */
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
  /**
   * Открывает Activity с настройками
   */
  private void ShowPreferencesActivity()
  {
    Intent intent = new Intent(this,CommonSettings.class);
    startActivityForResult(intent, 0); 
  }
  /**
   * Обновление данных из БД. Операция долгая, запускать строго в фоне
   */
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
        TerminalsArrayAdapter adapter = null;
/////////////////
        for(int i=0,len=_mGroups.size();i<len;++i)
        {
          if(_mGroups.get(i).getGroup().id== group.id)
          {
            adapter = _mGroups.get(i);
            break;
          }
        }
////////////////
        if(adapter==null)
        {
          adapter = new TerminalsArrayAdapter(this, group);
          _mGroups.add(adapter);
        }
        else
        {
          adapter.setGroup(group);
        }
//////////////// Вытащим терминалы
        _mStorage.getTerminals(account.id, group, adapter);
/////////////// Сортируем
        if(!adapter.isEmpty())
          adapter.sort(_mTerminalComparator);
      }
    }
/////////////// Вытащим статусы групп
    Hashtable<Long,Integer> states = new Hashtable<Long,Integer>();
    if(_mStates.getGroupsStates(states))
    {
      for(TerminalsArrayAdapter adapter : _mGroups)
      {
        if(states.containsKey(adapter.getGroup().id))
          adapter.setState(states.get(adapter.getGroup().id));
        else
          adapter.setState(ITerminal.STATE_OK);
      }
    }

  }
  /**
   * Заполнение адаптеров данными. Запускать строго в UI потоке, иначе упадет
   */
  private void fillAgents()
  {
    int index = 0;
    for(TerminalsArrayAdapter adapter : _mGroups)
    {
      if(adapter.isEmpty())
        continue;

      //adapter.sort(_mTerminalComparator);

      if(_mSlider.getChildCount()<=index)
      {
        ListView list = new ListView(this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);
        list.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        _mSlider.addView(list);
      }
      else
      {
        ListView list = ((ListView)_mSlider.getChildAt(index));
        if(list.getAdapter()!=adapter)
          list.setAdapter(adapter);
        else
          adapter.notifyList();
      }
      ++index;
    }
/////////
    while(index<_mSlider.getChildCount())
      _mSlider.removeViewAt(_mSlider.getChildCount()-1);
  }
  /**
   * Отоюражение данных по агунту в списке
   * @param view текущая активная вьюшка
   */
  private void setCurrentAgentInfo(View view)
  {
    ListView list = (ListView)view;
    if(list==null)
      list = (ListView)_mSlider.getCurrentView();
    if(list==null)
      return;

    TerminalsArrayAdapter adapter = (TerminalsArrayAdapter)list.getAdapter();
    if(adapter==null)
      return;
    Group group = adapter.getGroup();
//////////
    TextView text = (TextView) findViewById(R.id.agent_name);
    text.setText(group.name);
//////////
    text = (TextView) findViewById(R.id.agent_balance);
    StringBuilder balance = new StringBuilder(getText(R.string.balance));
    balance.append(": ").append(group.balance);
    if(group.overdraft!=0)
      balance.append("  ").append(getString(R.string.overdraft)).append(": ").append(group.overdraft);
    else
      balance.append(" / ").append(adapter.getCash());
    text.setText(balance.toString());
//////////
    text = (TextView) findViewById(R.id.agent_update_time);
    text.setText(getString(R.string.refreshed) + " " +
                        DateUtils.getRelativeTimeSpanString(group.lastUpdate,
                                                            System.currentTimeMillis(),
                                                            DateUtils.SECOND_IN_MILLIS,
                                                            DateUtils.FORMAT_ABBREV_ALL));
////////// пометим что мы уже видели статусы этого агента
    _mStates.updateGroupState(group.id,ITerminal.STATE_OK);
    adapter.setState(ITerminal.STATE_OK);
  }
  /**
   * Щелчок по кнопке со списком агентов
   * @param view сама кнопка вызывающая список агентов
   */
  @SuppressWarnings("unused")
  public void agentsListClick(View view)
  {
    GroupsArrayAdapter adapter;
    if(_mAgentsDialog==null)
    {
      AlertDialog.Builder dialog = new AlertDialog.Builder(this);
      ArrayList<Group> agents = new ArrayList<Group>();
      _mStorage.getGroups(agents);

      _mDialogAdapter = new GroupsArrayAdapter(this);
      for(Group agent : agents)
      {

        _mDialogAdapter.add(agent);
      }
      dialog.setAdapter(_mDialogAdapter,this);
      dialog.setTitle(R.string.agents_list);
      _mAgentsDialog =  dialog.create();
    }
///////////// проставим состояния
    Hashtable<Long,Integer> states = new Hashtable<Long,Integer>();
    if(_mStates.getGroupsStates(states))
      for(int index=0,length=_mDialogAdapter.getCount();index<length;++index)
      {
        Group agent = _mDialogAdapter.getItem(index);
        if(states.containsKey(agent.id))
          agent.state = states.get(agent.id);
        else
          agent.state = ITerminal.STATE_OK;
      }
/////////////
    _mAgentsDialog.show();
    _mAgentsDialog.getListView().setSelection(_mSlider.getCurrentViewIndex());
  }
  /**
   * Отображение диалога с необходимостью настроек
   */
  private void ShowSettingsAlarm()
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.error);
    builder.setMessage(getString(R.string.add_account_message))
           .setPositiveButton(R.string.add_account,new OnClickListener()
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
  /**
   * Выбран пункт меню
   * @param item выбранный пункт
   * @return true если мы отработали выбор
   */
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
  /**
   * Выбор агента в выпадающем списке агентов
   * @param dialogInterface диалог
   * @param index           порядковый номер выбранного агента
   */
  @Override
  public void onClick(DialogInterface dialogInterface, int index)
  {
    _mSlider.setCurrentView(index);
  }
  /**
   * Выбор терминала из спика
   * @param adapterView список терминала
   * @param view        вьюшка с терминалом
   * @param index       порядковый индекс терминала
   * @param id          идентификатор терминала
   */
  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int index, long id)
  {
    ITerminal terminal = (ITerminal) ((TerminalsArrayAdapter) adapterView.getAdapter()).getItem(index);
    Intent intent = new Intent(this,FullInfo.class);
    intent.putExtra(FullInfo.TERMINAL_EXTRA,terminal.getId());
    startActivity(intent);
  }
  /**
   * Изменена текущая отображаемая вьюшка в листалке
   * @param v вьюшка с терминалами
   */
  @Override
  public void CurrentViewChanged(View v)
  {
    setCurrentAgentInfo(v);
  }
  /**
   * Долгий щелчок по элементу в списке терминалов. Вываливает всплывающее меню с командами
   * @param adapterView список
   * @param view        вьюшка с терминалом
   * @param index       индеск терминала
   * @param l           идентификатор. не используется
   * @return возвращает true если мы обработали щелчок
   */
  @Override
  public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l)
  {
    final ITerminal terminal = (ITerminal) ((TerminalsArrayAdapter) adapterView.getAdapter()).getItem(index);
    ArrayList<TerminalAction> actions = new ArrayList<TerminalAction>();
////////
    terminal.getActions(this, actions);
    if(actions.size()==0)
      return false;
////////
    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    final ArrayAdapter<TerminalAction> actionsList = new ArrayAdapter<TerminalAction>(this, android.R.layout.select_dialog_item);
    for(TerminalAction action : actions)
      actionsList.add(action);
    dialog.setTitle(terminal.getTitle());
    dialog.setAdapter(actionsList, new OnClickListener()
      {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
          TerminalAction action = actionsList.getItem(which);
          setSpinnerVisibility(true);
          terminal.runAction(_mStorage,action.id,MainActivity.this);
        }
      });
      dialog.setCancelable(true);
      dialog.show();
      return(true);
  }
  /**
   * Результат запуска команды на терминале
   * @param success признак усеха
   * @param message сообщение
   * @param title   человечье имя терминала
   */
  @Override
  public void onCommandResult(boolean success, int message, String title)
  {
    setSpinnerVisibility(false);
    Toast.makeText(this,title + ": " + getString(message),Toast.LENGTH_LONG).show();
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
        setCurrentAgentInfo(_mSlider.getCurrentView());
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
      if(StatesReceiver.refreshData(MainActivity.this)>-1)
      {
        refreshData();
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
      if(result == Boolean.TRUE)
      {
        fillAgents();
        setCurrentAgentInfo(_mSlider.getCurrentView());
      }
      setSpinnerVisibility(false);
    }
  }
  /*
  public class ExceptionHandler implements Thread.UncaughtExceptionHandler
  {
    public String joinStackTrace(Throwable e)
    {
      StringWriter writer = null;
      try
      {
        writer = new StringWriter();
        joinStackTrace(e, writer);
        return writer.toString();
      }
      finally
      {
        if(writer != null)
          try
          {
            writer.close();
          }
          catch(IOException e1)
          {
            // ignore
          }
      }
    }

    public void joinStackTrace(Throwable e, StringWriter writer)
    {
      PrintWriter printer = null;
      try
      {
        printer = new PrintWriter(writer);

        while (e != null)
        {
          printer.println(e);
          StackTraceElement[] trace = e.getStackTrace();
          for(StackTraceElement aTrace : trace)
            printer.println("\tat " + aTrace);

          e = e.getCause();
          if (e != null)
              printer.println("Caused by:\r\n");
        }
      }
      finally
      {
        if(printer != null)
            printer.close();
      }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
      e.printStackTrace();
/////////
      Intent sendIntent;
      sendIntent = new Intent(Intent.ACTION_SEND);
      sendIntent.putExtra(Intent.EXTRA_SUBJECT,"Apteryx промахнулся");
      sendIntent.putExtra(Intent.EXTRA_TEXT,joinStackTrace(e));
      sendIntent.putExtra(Intent.EXTRA_EMAIL,new String[] {"apteryx.project@gmail.com"});
      sendIntent.setType("text/plain");
      MainActivity.this.startActivity(sendIntent);
      e.printStackTrace();
      android.os.Process.killProcess(android.os.Process.myPid());
    }
  }*/
}