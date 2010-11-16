package org.pvoid.apteryxaustralis.ui;


import java.util.List;

import org.pvoid.apteryxaustralis.Consts;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.AccountsStorage;
import org.pvoid.apteryxaustralis.accounts.Agent;
import org.pvoid.apteryxaustralis.accounts.AgentsStorage;
import org.pvoid.apteryxaustralis.accounts.Terminal;
import org.pvoid.apteryxaustralis.accounts.TerminalListRecord;
import org.pvoid.apteryxaustralis.accounts.TerminalStatus;
import org.pvoid.apteryxaustralis.accounts.TerminalsStatusesStorage;
import org.pvoid.apteryxaustralis.accounts.TerminalsStorage;
import org.pvoid.apteryxaustralis.net.StatusRefreshTask;
import org.pvoid.apteryxaustralis.preference.CommonSettings;
import org.pvoid.common.views.SlideBand;
import org.pvoid.common.views.SlideBand.OnCurrentViewChangeListner;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnCurrentViewChangeListner// implements IStatesRespnseHandler, OnItemClickListener
{
  private static final int SETTINGS_MENU_ID = Menu.FIRST+1; 
  private static final int REFRESH_MENU_ID = Menu.FIRST+2;
  
  private static final int DIALOG_RESTORE = 0;
  private static final int DIALOG_REFRESH = 1;
  
  private AgentListView _AgentView;
  private SlideBand _Band;
  private TextView _AgentName;
  
  private class RestoreTask extends AsyncTask<Void, Integer, Boolean>
  {
    private String _AgentNameValue = null;
    @Override
    protected Boolean doInBackground(Void... params)
    {
      AccountsStorage.Instance().Restore(MainActivity.this);
      AgentsStorage.Instance().Restore(MainActivity.this);
      TerminalsStorage.Instance().Restore(MainActivity.this);
      TerminalsStatusesStorage.Instance().Restore(MainActivity.this);
/////////////
      for(Agent agent : AgentsStorage.Instance().getAgentsByName())
      {
        if(_AgentNameValue==null)
          _AgentNameValue = agent.getName();
        
        List<Terminal> terminals = TerminalsStorage.Instance().TerminalsForAgent(agent);
        /*if(terminals.size()==0)
          continue;*/
        
        _AgentView = new AgentListView(MainActivity.this, agent.Id());          
        TerminalsArrayAdapter items = new TerminalsArrayAdapter(MainActivity.this, R.layout.terminal);
        for(Terminal terminal : terminals)
        {
        	TerminalStatus status = TerminalsStatusesStorage.Instance().Find(terminal.Id());
          items.add(new TerminalListRecord(terminal, status));
        }
        
        _AgentView.setAdapter(items);
        _Band.addView(_AgentView);
      }
/////////////
      return(true);
    }
    
    @Override
    protected void onPostExecute(Boolean result)
    {
      LinearLayout layout = (LinearLayout) findViewById(R.id.mainscreen);
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0);
      params.weight=1;
/////////////
      if(_AgentNameValue!=null)
      {
        _AgentName.setText(_AgentNameValue);
      }
/////////////
      layout.addView(_Band,params);
      dismissDialog(DIALOG_RESTORE);
    }
  };
  
  private Thread _RefreshThread = new Thread()
  {
		@Override
    public void run()
    {
			boolean refreshed = false;
			for(Account account : AccountsStorage.Instance())
			{
				List<TerminalStatus> statuses = StatusRefreshTask.GetStatuses(account.getLogin(), account.getPassword(), Long.toString(account.getTerminalId()));
				if(statuses!=null)
				{
					TerminalsStatusesStorage.Instance().Add(statuses);
					refreshed = true;
				}
			}
/////////////
			if(refreshed)
				TerminalsStatusesStorage.Instance().Serialize(MainActivity.this);
/////////////
			for(int index=0,length=_Band.getChildCount();index<length;++index)
			{
				View view = _Band.getChildAt(index);
				if(view!=null)
					view.postInvalidate();
			}
/////////////
			dismissDialog(DIALOG_REFRESH);
    }
  };
  
  public BroadcastReceiver UpdateMessageReceiver = new BroadcastReceiver()
  {
    @Override
    public void onReceive(Context context, Intent intent)
    {
      //MainActivity.this.RefreshStates();
    }
  };
  
  @Override
  public void onResume()
  {
    super.onResume();
    
    IntentFilter filter = new IntentFilter(Consts.REFRESH_BROADCAST_MESSAGE);
    registerReceiver(UpdateMessageReceiver, filter);
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
////////
    _AgentName = (TextView)findViewById(R.id.agent_name);
    _Band = new SlideBand(this);
    _Band.setOnCurrentViewChangeListner(this);
    showDialog(DIALOG_RESTORE);
    (new RestoreTask()).execute();
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
        RefreshStatuses();
        break;
    }
    return(super.onOptionsItemSelected(item));
  }
  
  @Override
  protected Dialog onCreateDialog(int id)
  {
    final ProgressDialog dialog = new ProgressDialog(this);
    switch(id)
    {
      case DIALOG_RESTORE:
        dialog.setMessage(getText(R.string.data_restore));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        break;
      case DIALOG_REFRESH:
      	dialog.setMessage(getText(R.string.status_refresh));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        break;
    }
    return(dialog);
  }

  @Override
  public void CurrentViewChanged(View v)
  {
    Agent agent = AgentsStorage.Instance().Find(((AgentListView)v).getAgentId());
    if(agent!=null)
      _AgentName.setText(agent.getName());
    else
      _AgentName.setText("");
  }
  
  public void agentsListClick(View v)
  {
  	Intent intent = new Intent(this, AgentsList.class);
  	startActivityForResult(intent, Consts.ACTIVITY_SELECT_AGENT);
  }
  
  @Override
  public void onActivityResult(int requestCode,int resultCode, Intent intent)
  {
    if(resultCode==Consts.ACTIVITY_SELECT_AGENT)
    {
      int position = intent.getIntExtra(Consts.RESULT_AGENT_POSITION, -1);
      if(position>-1)
      {
      	_Band.setCurrentView(position);
      }
    }
  }
  
  public void RefreshStatuses()
  {
  	Thread.State state = _RefreshThread.getState();
  	if(state == Thread.State.NEW || state == Thread.State.TERMINATED)
  	{
  		showDialog(DIALOG_REFRESH);
  		_RefreshThread.start();
  	}
  }
  
  /*private TerminalsProcessData _Terminals;
  private ListView _TerminalsList;
  private TerminalsArrayAdapter _TerminalsAdapter;
  private Accounts _Accounts;
  
  private boolean _Refreshing;
  private Object _RefreshLock;*/
  
  /*
  
  // TODO: Перетащить сортировку и наполнение в AsyncTask 
  private static final Comparator<TerminalInfoOld> _TerminalComparer = new Comparator<TerminalInfoOld>()
  {
    @Override
    public int compare(TerminalInfoOld object1, TerminalInfoOld object2)
    {
      int result = (int)(object1.agentId - object2.agentId);
      
      if(result!=0)
        return(result);
      
      if(object1.Address()==null)
        return(-1);
      if(object2.Address()==null)
        return(1);
      
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
////////
    _Accounts = new Accounts(this);    
    AccountsStorage.Instance().Restore(this);
    AccountsStorage.Instance().ConvertFromOld(_Accounts,this);
////////
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    setContentView(R.layout.main);
    setProgressBarIndeterminateVisibility(false);
    _Terminals = new TerminalsProcessData();
    _TerminalsAdapter = new TerminalsArrayAdapter(this, R.layout.terminal);
    _TerminalsList = (ListView)findViewById(R.id.terminals_list);
    _Refreshing = false;
    _RefreshLock = new Object();
    if(_TerminalsList!=null)
    {
      _TerminalsList.setAdapter(_TerminalsAdapter);
      _TerminalsList.setOnItemClickListener(this);
    }
    
    ViewFlipper fliper = (ViewFlipper)findViewById(R.id.balances_flipper);
    fliper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_animation_in));
    fliper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_animation_out));
    
    SharedPreferences prefs = getSharedPreferences(Consts.APTERYX_PREFS, MODE_PRIVATE);
    if(prefs.getBoolean(Consts.PREF_AUTOCHECK, false))
    {
      Intent serviceIntent = new Intent(this,UpdateStatusService.class);
      startService(serviceIntent);
    }
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
    RestoreStates();
    Notifyer.HideNotification(this);
  }
  
  
  
  private void ShowPreferencesActivity()
  {
     
  }
  
  private void RefreshStates()
  {
    synchronized (_RefreshLock)
    {
      _Refreshing = true;
      setProgressBarIndeterminateVisibility(true);
      ArrayList<Account> accounts = new ArrayList<Account>();
      HashMap<Long, ArrayList<Agent>> agents = new HashMap<Long, ArrayList<Agent>>();
      _Accounts.GetAccounts(accounts);
      if(accounts.size()>0)
      {
        for(Account account : accounts)
        {
          ArrayList<Agent> agents_line = new ArrayList<Agent>();
          _Accounts.GetAgents(account.Id(), agents_line);
          if(agents_line.size()>0)
            agents.put(account.Id(), agents_line);
        }
        
        Account[] ac = new Account[accounts.size()];
        (new StatesRequestTask(this,agents, _Terminals)).execute(accounts.toArray(ac));
      }
      else
        ShowSettingsAlarm();
    }
  }
  
  private void ShowSettingsAlarm()
  {
    ViewFlipper flipper = (ViewFlipper)findViewById(R.id.balances_flipper);
    flipper.setVisibility(View.GONE);
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
  
  private void ShowStateInfo()
  {
    SharedPreferences prefs = getSharedPreferences(Consts.APTERYX_PREFS, MODE_PRIVATE);
    ViewFlipper flipper = (ViewFlipper)findViewById(R.id.balances_flipper);
//////
    if(_Terminals.hasAccounts())
    {
      DrawBalances(flipper);
      flipper.setVisibility(View.VISIBLE);
      
      long time = prefs.getLong(Consts.PREF_LASTUPDATE, 0);
      if(time!=0)
      {
        setTitle(getString(R.string.last_update) + " (" + 
                 DateUtils.formatSameDayTime(time, System.currentTimeMillis(), DateFormat.DEFAULT, DateFormat.DEFAULT)
                 +")");
      }
    }
    else
    {
      flipper.setVisibility(View.GONE);
      Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
    }
  }
  
  private void RestoreStates()
  {
    synchronized (_RefreshLock)
    {
      if(_Refreshing)
        return;
    }
//////
    if(!_Accounts.HasAccounts())
    {
      ShowSettingsAlarm();
      return;
    }
///////
    _Accounts.GetTerminals(_Terminals);
    DrawTerminals();
///////
    ShowStateInfo();
  }
  
  
  @Override
  public void onActivityResult(int requestCode,int resultCode, Intent intent)
  {
    if(resultCode==Consts.RESULT_RELOAD)
    {
      RefreshStates();
    }
  }
  
  private void DrawTerminals()
  {
    _TerminalsAdapter.clear();
    for(String terminal_key : _Terminals)
    {
      _TerminalsAdapter.add(_Terminals.at(terminal_key));
    }
    
    HashMap<Long, String> agents = _Terminals.Agents();
    for(Long agentId : agents.keySet())
    {
      /*Terminal terminal = new Terminal(null, null);
      terminal.agentId = agentId;
      terminal.agentName = agents.get(agentId);
      terminal.cash = _Terminals.AgentCash(agentId);
      terminal.State(0);
      _TerminalsAdapter.add(terminal);*//*
    }
    
    _TerminalsAdapter.sort(_TerminalComparer);
  }
  
  @Override
  public void onSuccessRequest()
  {
    synchronized (_RefreshLock)
    {
      _Refreshing = false;
    }
//////
    DrawTerminals();
    _Accounts.SaveStates(_Terminals);
    setProgressBarIndeterminateVisibility(false);
//////
    ShowStateInfo();
  }
  
  @Override
  public void onRequestError()
  {
    synchronized (_RefreshLock)
    {
      _Refreshing = false;
    }
//////
    setProgressBarIndeterminateVisibility(false);
    Toast.makeText(this, R.string.network_error, 300).show();
  }
  @Override
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
  {
    TerminalInfoOld terminal = _TerminalsAdapter.getItem(position);
    if(terminal!=null && terminal.id()!=null)
    {
      /*Intent intent = new Intent(this, FullInfo.class);
      intent.putExtra("terminal", terminal);
      startActivity(intent);*//*
    }
  }
  
  public void DrawBalances(ViewFlipper flipper)
  {
    int childs = flipper.getChildCount();
    int index = 0;
///////
    ArrayList<Account> accounts = new ArrayList<Account>();
    _Accounts.GetAccounts(accounts);
///////
    TextView view;
    for(Account account : accounts)
    {
      if(index<childs)
      {
        view = (TextView)flipper.getChildAt(index);
        ++index;
      }
      else
      {
        view = new TextView(this);
        flipper.addView(view, LayoutParams.WRAP_CONTENT);
      }

      view.setText(Html.fromHtml("<b>"+account.getTitle()+"</b><br>"+_Terminals.Balance(account.Id())));
    }
    
    if(accounts.size()>1)
      flipper.startFlipping();
    else
      flipper.stopFlipping();
  }*/
}