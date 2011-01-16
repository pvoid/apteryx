package org.pvoid.apteryxaustralis.preference;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.Consts;
import org.pvoid.apteryxaustralis.Utils;
import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.Agent;
import org.pvoid.apteryxaustralis.accounts.AgentsSection;
import org.pvoid.apteryxaustralis.accounts.ReportsSection;
import org.pvoid.apteryxaustralis.accounts.TerminalsSection;
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
import org.pvoid.apteryxaustralis.storage.Storage;

public class AddAccountActivity extends Activity implements IResponseHandler
{
  public static final String EXTRA_ACCOUNT_ID = "id";
  public static final String EXTRA_ACCOUNT_TITLE = "title";

  private String _mLogin;
  private String _mPassword;
  private String _mTerminalId;
  
  private EditText _mLoginEdit;
  private EditText _mPasswordEdit;
  private EditText _mTerminalEdit;
  private long _mId;
  
  
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.account);
///////
    _mLoginEdit = (EditText)findViewById(R.id.login);
    _mPasswordEdit = (EditText)findViewById(R.id.password);
    _mTerminalEdit = (EditText)findViewById(R.id.terminal);
    
    Bundle extra = getIntent().getExtras();
    if(extra!=null && extra.containsKey(Consts.COLUMN_ID))
    {
      _mId = extra.getLong(Consts.COLUMN_ID);
      _mLoginEdit.setText(extra.getString(Consts.COLUMN_LOGIN));
      _mTerminalEdit.setText(extra.getString(Consts.COLUMN_TERMINAL));
      _mPassword = extra.getString(Consts.COLUMN_PASSWORD);
    }
    else
      _mId = 0;
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
    _mLogin = _mLoginEdit.getText().toString();
    if(Utils.isEmptyString(_mLogin))
    {
      Toast.makeText(this, getString(R.string.empty_login), 200).show();
      return;
    }
    String password = _mPasswordEdit.getText().toString();
    if(Utils.isEmptyString(password))
    {
      if(_mId ==0)
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
        _mPassword = String.format("%1$032X", i).toLowerCase();
      }
      catch (NoSuchAlgorithmException e)
      { 
        //TODO: Наверное надо сообщить что MD5 нет
        return;
      }
    }
////////
    _mTerminalId = _mTerminalEdit.getText().toString();
    if(Utils.isEmptyString(_mTerminalId))
    {
      Toast.makeText(this, getString(R.string.empty_terminal), 200).show();
      return;
    }
////////
    showDialog(0);
    Request request = new Request(_mLogin, _mPassword, _mTerminalId);
    request.getAgentInfo();
    request.getAgents();
    request.getTerminals();
    request.getTerminalsStatus();
    (new RequestTask(this)).execute(request);
  }
  
  public void onResponse(Response response)
  {
    dismissDialog(0);
    removeDialog(0);

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
    // TODO: утащить это в отдельный поток, так как запись в БД длительна
    AgentsSection agentsSection = response.Agents(); 
    Agent agent;
    if(agentsSection!=null && (agent = agentsSection.GetAgentInfo())!=null)
    {
      Account account = new Account(agent.getId(), agent.getName(), _mLogin, _mPassword, Long.parseLong(_mTerminalId));
      if(Storage.addAccount(this,account))
      {
        List<Agent> agents = agentsSection.getAgents();
        if(agents!=null)
        {
          Storage.addAgents(this,agents);
        }
        else
          Storage.addAgent(this,agent);

        TerminalsSection terminalsSection = response.Terminals();
        if(terminalsSection!=null)
        {
          Storage.addTerminals(this,terminalsSection.getTerminals());
        }

        ReportsSection reportsSection = response.Reports();
        if(reportsSection!=null)
        {
          Storage.addStatuses(this,reportsSection.getTerminalsStatus());
        }

        Intent result = new Intent();
        result.putExtra(EXTRA_ACCOUNT_ID,account.getId());
        result.putExtra(EXTRA_ACCOUNT_TITLE,account.getTitle());
        setResult(RESULT_OK, result);
        finish();
      }
    }
  }
}
