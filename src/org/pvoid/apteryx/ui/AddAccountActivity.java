package org.pvoid.apteryx.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.pvoid.apteryx.Consts;
import org.pvoid.apteryx.R;
import org.pvoid.apteryx.Utils;
import org.pvoid.apteryx.accounts.Accounts;
import org.pvoid.apteryx.accounts.Agent;
import org.pvoid.apteryx.net.AgentInfoProcessData;
import org.pvoid.apteryx.net.DataTransfer;
import org.pvoid.apteryx.net.IResponseHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
  private static final int REQUEST_MAINAGENT = 1;
  private static final int REQUEST_ACTIVEAGENTS = 2;
  
  private String _Login;
  private String _Password;
  private String _TerminalId;
  
  private EditText _LoginEdit;
  private EditText _PasswordEdit;
  private EditText _TerminalEdit;
  private long _Id;
  
  private AgentInfoProcessData _AgentInfo;
  private Agent _MainAgent;
  
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
    DataTransfer.TestAccount(_Login, _Password, _TerminalId, this);
////////
  }
  
  public void onActivityResult(int requestCode,int resultCode, Intent intent)
  {
    Bundle extras;
    switch(resultCode)
    {
      case RESULT_CANCELED:
        setResult(RESULT_CANCELED,null);
        finish();
        break;
      case RESULT_OK:
        switch(requestCode)
        {
          case REQUEST_MAINAGENT:
            extras = intent.getExtras();
            _MainAgent = extras.getParcelable(Consts.EXTRA_SELECTED_AGENT);
            SelectActiveAgents();
            break;
          case REQUEST_ACTIVEAGENTS:
            extras = intent.getExtras();
            List<Agent> agents = extras.getParcelableArrayList(Consts.EXTRA_SELECTED_AGENTS);
            SaveAccount(agents);
            break;
        }
    }
  }
  
  public void SelectActiveAgents()
  {
    Intent intent = new Intent(this,SelectActiveAgents.class);
    Bundle params = new Bundle();
    params.putParcelableArrayList(Consts.EXTRA_AGENTS, _AgentInfo.Agents());
    intent.putExtras(params);
    startActivityForResult(intent, REQUEST_ACTIVEAGENTS);
  }
  
  public void SaveAccount(List<Agent> agents)
  {
    Accounts accounts = new Accounts(this);
    if(_Id==0)
    {
      accounts.AddAccount(_MainAgent.Id, _MainAgent.Name, _Login, _Password, _TerminalId);
      accounts.SaveAgents(_MainAgent.Id, agents);
    }
    else
      accounts.EditAccount(_Id, _MainAgent.Name, _Login, _Password, _TerminalId);
    
    dismissDialog(0);
    setResult(Consts.RESULT_RELOAD);
    finish();
  }
  
  public void onResponse(String response)
  {
    if(response==null)
    {
      dismissDialog(0);
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage(getString(R.string.network_error))
             .setPositiveButton("Ok",null)
             .setTitle(R.string.add_account)
             .show();
      return;
    }
    SAXParserFactory factory = SAXParserFactory.newInstance();
    try
    {
      SAXParser parser = factory.newSAXParser();
      InputSource source = new InputSource();
      ByteArrayInputStream stream = new ByteArrayInputStream(response.getBytes("UTF-8") );
      source.setByteStream(stream);
      source.setEncoding("UTF-8");
      _AgentInfo = new AgentInfoProcessData();
      parser.parse(source, _AgentInfo);
////////
      if(_AgentInfo.Code()==0)
      {
        ArrayList<Agent> agents = _AgentInfo.Agents();
        if(agents.size()==1)
        {
          _MainAgent = agents.get(0);
          SaveAccount(agents);
          return;
        }
        
        Intent intent = new Intent(this,SelectMainAgent.class);
        Bundle params = new Bundle();
        params.putParcelableArrayList(Consts.EXTRA_AGENTS, agents);
        intent.putExtras(params);
        startActivityForResult(intent, REQUEST_MAINAGENT);        
      }
      else
      {
        dismissDialog(0);
        Toast.makeText(this, getString(R.string.auth_error), 300).show();
      }
    }
    catch (ParserConfigurationException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (SAXException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
