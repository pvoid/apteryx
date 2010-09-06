package org.pvoid.apteryx.ui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import org.pvoid.apteryx.Consts;
import org.pvoid.apteryx.Notifyer;
import org.pvoid.apteryx.R;
import org.pvoid.apteryx.accounts.Account;
import org.pvoid.apteryx.accounts.Accounts;
import org.pvoid.apteryx.accounts.Terminal;
import org.pvoid.apteryx.net.IStatesRespnseHandler;
import org.pvoid.apteryx.net.StatesRequestTask;
import org.pvoid.apteryx.net.TerminalsProcessData;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements IStatesRespnseHandler, OnItemClickListener
{
  private static final int SETTINGS_MENU_ID = Menu.FIRST+1; 
  private static final int REFRESH_MENU_ID = Menu.FIRST+2;
  
  private TerminalsProcessData _Terminals;
  private ListView _TerminalsList;
  private TerminalsArrayAdapter _TerminalsAdapter;
  private Accounts _Accounts;
  
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
    if(_TerminalsList!=null)
    {
      _TerminalsList.setAdapter(_TerminalsAdapter);
      _TerminalsList.setOnItemClickListener(this);
    }
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
    setProgressBarIndeterminateVisibility(true);
    ArrayList<Account> accounts = new ArrayList<Account>();
    _Accounts.GetAccounts(accounts);
    Account[] ac = new Account[accounts.size()];
    (new StatesRequestTask(this, _Terminals)).execute(accounts.toArray(ac));
  }
  
  private void ShowStateInfo()
  {
    SharedPreferences prefs = getSharedPreferences(Consts.APTERYX_PREFS, MODE_PRIVATE);
    long time = prefs.getLong(Consts.PREF_LASTUPDATE, 0);
    if(time!=0)
    {
      setTitle(getString(R.string.last_update) + " (" + 
               DateUtils.formatSameDayTime(time, System.currentTimeMillis(), DateFormat.DEFAULT, DateFormat.DEFAULT)
               +")");
    }
//////
    //RelativeLayout balance_layout = (RelativeLayout)findViewById(R.id.balance_layer);
    if(_Terminals.hasAgents())
    {
      /*balance_layout.setVisibility(View.VISIBLE);
      
      TextView balance = (TextView)findViewById(R.id.full_balance);
      if(balance!=null)
      {
        balance.setText(Html.fromHtml("<b>"+getString(R.string.balance)+"</b>: "+_Terminals.Balance()));
      }
      balance = (TextView)findViewById(R.id.full_overdraft);
      if(balance!=null)
      {
        balance.setText(Html.fromHtml("<b>"+getString(R.string.overdraft)+"</b>: "+_Terminals.Overdraft()));
      }*/
    }
    else
    {
      //balance_layout.setVisibility(View.GONE);
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
  }
  
  private void RestoreStates()
  {
    _Accounts.GetTerminals(_Terminals);
    DrawTerminals();
//////
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
      terminal.State(0);
      _TerminalsAdapter.add(terminal);
    }
    
    _TerminalsAdapter.sort(_TerminalComparer);
  }
  
  @Override
  public void onSuccessRequest()
  {
    DrawTerminals();
    _Accounts.SaveStates(_Terminals);
    setProgressBarIndeterminateVisibility(false);
//////
    ShowStateInfo();
  }
  
  @Override
  public void onRequestError()
  {
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
}