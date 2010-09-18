package org.pvoid.apteryx.ui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

import org.pvoid.apteryx.Consts;
import org.pvoid.apteryx.Notifyer;
import org.pvoid.apteryx.R;
import org.pvoid.apteryx.UpdateStatusService;
import org.pvoid.apteryx.accounts.Account;
import org.pvoid.apteryx.accounts.Accounts;
import org.pvoid.apteryx.accounts.Agent;
import org.pvoid.apteryx.accounts.Terminal;
import org.pvoid.apteryx.net.IStatesRespnseHandler;
import org.pvoid.apteryx.net.StatesRequestTask;
import org.pvoid.apteryx.net.TerminalsProcessData;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MainActivity extends Activity implements IStatesRespnseHandler, OnItemClickListener
{
  private static final int SETTINGS_MENU_ID = Menu.FIRST+1; 
  private static final int REFRESH_MENU_ID = Menu.FIRST+2;
  
  private TerminalsProcessData _Terminals;
  private ListView _TerminalsList;
  private TerminalsArrayAdapter _TerminalsAdapter;
  private Accounts _Accounts;
  
  private boolean _Refreshing;
  private Object _RefreshLock;
  
  public BroadcastReceiver UpdateMessageReceiver = new BroadcastReceiver()
  {
    @Override
    public void onReceive(Context context, Intent intent)
    {
      MainActivity.this.RefreshStates();
    }
  };
  
  // TODO: Перетащить сортировку и наполнение в AsyncTask 
  private static final Comparator<Terminal> _TerminalComparer = new Comparator<Terminal>()
  {
    @Override
    public int compare(Terminal object1, Terminal object2)
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
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    setContentView(R.layout.main);
    setProgressBarIndeterminateVisibility(false);
    _Terminals = new TerminalsProcessData();
    _TerminalsAdapter = new TerminalsArrayAdapter(this, R.layout.terminal);
    _TerminalsList = (ListView)findViewById(R.id.terminals_list);
    _Accounts = new Accounts(this);
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
  public void onResume()
  {
    super.onResume();
    IntentFilter filter = new IntentFilter(Consts.REFRESH_BROADCAST_MESSAGE);
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
    RestoreStates();
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
  
  private void ShowPreferencesActivity()
  {
    Intent intent = new Intent(this,Preferences.class);
    startActivityForResult(intent, 0); 
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
          _Accounts.GetAgents(account.Id, agents_line);
          if(agents_line.size()>0)
            agents.put(account.Id, agents_line);
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
  
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case SETTINGS_MENU_ID:
        ShowPreferencesActivity();
        break;
      case REFRESH_MENU_ID:
        RefreshStates();
        break;
    }
    return(super.onOptionsItemSelected(item));
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
      Terminal terminal = new Terminal(null, null);
      terminal.agentId = agentId;
      terminal.agentName = agents.get(agentId);
      terminal.cash = _Terminals.AgentCash(agentId);
      terminal.State(0);
      _TerminalsAdapter.add(terminal);
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
    Terminal terminal = _TerminalsAdapter.getItem(position);
    if(terminal!=null && terminal.id()!=null)
    {
      Intent intent = new Intent(this, FullInfo.class);
      intent.putExtra("terminal", terminal);
      startActivity(intent);
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

      view.setText(Html.fromHtml("<b>"+account.Title+"</b><br>"+_Terminals.Balance(account.Id)));
    }
    
    if(accounts.size()>1)
      flipper.startFlipping();
    else
      flipper.stopFlipping();
  }
}