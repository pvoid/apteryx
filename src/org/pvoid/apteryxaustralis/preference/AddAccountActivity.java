package org.pvoid.apteryxaustralis.preference;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.Consts;
import org.pvoid.apteryxaustralis.Utils;
import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.AccountsStorage;
import org.pvoid.apteryxaustralis.accounts.Agent;
import org.pvoid.apteryxaustralis.accounts.AgentsSection;
import org.pvoid.apteryxaustralis.accounts.AgentsStorage;
import org.pvoid.apteryxaustralis.accounts.ReportsSection;
import org.pvoid.apteryxaustralis.accounts.Terminal;
import org.pvoid.apteryxaustralis.accounts.TerminalStatus;
import org.pvoid.apteryxaustralis.accounts.TerminalsSection;
import org.pvoid.apteryxaustralis.accounts.TerminalsStatusesStorage;
import org.pvoid.apteryxaustralis.accounts.TerminalsStorage;
import org.pvoid.apteryxaustralis.net.ErrorCodes;
import org.pvoid.apteryxaustralis.net.IResponseHandler;
import org.pvoid.apteryxaustralis.net.Request;
import org.pvoid.apteryxaustralis.net.RequestTask;
import org.pvoid.apteryxaustralis.net.Response;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class AddAccountActivity extends Activity implements IResponseHandler
{
  private String _Login;
  private String _Password;
  private String _TerminalId;
  
  private EditText _LoginEdit;
  private EditText _PasswordEdit;
  private EditText _TerminalEdit;
  private long _Id;
  
  
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);

    setContentView(R.layout.addaccount);
///////
    _LoginEdit = (EditText)findViewById(R.id.login);
    _PasswordEdit = (EditText)findViewById(R.id.password);
    _TerminalEdit = (EditText)findViewById(R.id.terminal);
    
    Bundle extra = getIntent().getExtras();
    if(extra!=null && extra.containsKey(Consts.COLUMN_ID))
    {
      _Id = extra.getLong(Consts.COLUMN_ID);
      _LoginEdit.setText(extra.getString(Consts.COLUMN_LOGIN));
      _TerminalEdit.setText(extra.getString(Consts.COLUMN_TERMINAL));
      _Password = extra.getString(Consts.COLUMN_PASSWORD);
    }
    else
      _Id = 0;
  }
  
  @Override
  protected Dialog onCreateDialog(int id)
  {
    final ProgressDialog dialog = new ProgressDialog(this);
    dialog.setMessage(getText(R.string.auth_process));
    dialog.setIndeterminate(true);
    dialog.setCancelable(false);
    return(dialog);
  }
  
  public void CheckAccount(View view)
  {
////////
    _Login = _LoginEdit.getText().toString();
    if(Utils.isEmptyString(_Login))
    {
      Toast.makeText(this, getString(R.string.empty_login), 200).show();
      return;
    }
    String password = _PasswordEdit.getText().toString();
    if(Utils.isEmptyString(password))
    {
      if(_Id==0)
      {
        Toast.makeText(this, getString(R.string.empty_password), 200).show();
        return;
      }
    }
    else
    {
      try
      {
        MessageDigest m=MessageDigest.getInstance("MD5");
        m.reset();
        m.update(password.getBytes(),0,password.length());
        BigInteger i = new BigInteger(1,m.digest());
        _Password = String.format("%1$032X", i).toLowerCase();
      }
      catch (NoSuchAlgorithmException e)
      { 
        //TODO: Наверное надо сообщить что MD5 нет
        return;
      }
    }
////////
    _TerminalId = _TerminalEdit.getText().toString();
    if(Utils.isEmptyString(_TerminalId))
    {
      Toast.makeText(this, getString(R.string.empty_terminal), 200).show();
      return;
    }
////////
    showDialog(0);
    Request request = new Request(_Login, _Password, _TerminalId);
    request.getAgentInfo();
    request.getAgents();
    request.getTerminals();
    request.getTerminalsStatus();
    (new RequestTask(this)).execute(request);
  }
  
  public void onResponse(Response response)
  {
    dismissDialog(0);    
    if(response==null)
    {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage(getString(R.string.network_error))
             .setPositiveButton("Ok",null)
             .setTitle(R.string.add_account)
             .show();
      return;
    }
    
    if(response.OsmpCode()!=0)
    {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage(ErrorCodes.Message(response.OsmpCode()))
             .setPositiveButton("Ok",null)
             .setTitle(R.string.add_account)
             .show();
      return;
    }
    
    AgentsSection agentsSection = response.Agents(); 
    Agent agent;
    if(agentsSection!=null && (agent = agentsSection.GetAgentInfo())!=null)
    {
      Account account = new Account(agent.Id(), agent.getName(), _Login, _Password, Long.parseLong(_TerminalId));
      AccountsStorage.Instance().AddUnique(account);
      AccountsStorage.Instance().Serialize(this);
      
      List<Agent> agents = agentsSection.getAgents();
      AgentsStorage.Instance().AddUnique(agents);
      AgentsStorage.Instance().Serialize(this);
      
      TerminalsSection terminalsSection = response.Terminals();
      if(terminalsSection!=null)
      {
        List<Terminal> terminals = terminalsSection.getTerminals();
        TerminalsStorage.Instance().AddUnique(terminals);
        TerminalsStorage.Instance().Serialize(this);
      }
      
      ReportsSection reportsSection = response.Reports();
      if(reportsSection!=null)
      {
      	List<TerminalStatus> status = reportsSection.getTerminalsStatus();
      	TerminalsStatusesStorage.Instance().AddUnique(status);
      	TerminalsStatusesStorage.Instance().Serialize(this);
      }

      Intent result = new Intent();
      result.putExtra(Consts.EXTRA_ACCOUNTID, agent.Id());
      setResult(RESULT_OK, result);
      finish();
    }
  }
}
