package org.pvoid.apteryx.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.pvoid.apteryx.Consts;
import org.pvoid.apteryx.R;
import org.pvoid.apteryx.Utils;
import org.pvoid.apteryx.accounts.Accounts;
import org.pvoid.apteryx.net.AgentInfoProcessData;
import org.pvoid.apteryx.net.DataTransfer;
import org.pvoid.apteryx.net.IResponseHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddAccountActivity extends Activity implements IResponseHandler
{
  private String _Login;
  private String _Password;
  private String _TerminalId;
  
  //private EditText _TitleEdit;
  private EditText _LoginEdit;
  private EditText _PasswordEdit;
  private EditText _TerminalEdit;
  private String _Id;
  
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.addaccount);
///////
    /*TextView label = (TextView)findViewById(R.id.title_label);
    _TitleEdit = (EditText)findViewById(R.id.title);*/
    _LoginEdit = (EditText)findViewById(R.id.login);
    _PasswordEdit = (EditText)findViewById(R.id.password);
    _TerminalEdit = (EditText)findViewById(R.id.terminal);
    
    Bundle extra = getIntent().getExtras();
    if(extra!=null && extra.containsKey(Consts.COLUMN_ID))
    {
      _Id = extra.getString(Consts.COLUMN_ID);
      _LoginEdit.setText(extra.getString(Consts.COLUMN_LOGIN));
      _TerminalEdit.setText(extra.getString(Consts.COLUMN_TERMINAL));
      _Password = extra.getString(Consts.COLUMN_PASSWORD);
    }
    else
      _Id = null;
    /*if(_Id==0)
    {
      label.setVisibility(View.GONE);
      _TitleEdit.setVisibility(View.GONE);
    }*/
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
  
  public void AddAccount(View view)
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
      if(_Id==null)
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
      AgentInfoProcessData agentInfo = new AgentInfoProcessData();
      parser.parse(source, agentInfo);
////////
      if(agentInfo.Code()==0)
      {
        Accounts accounts = new Accounts(this);
        if(_Id==null)
          accounts.AddAccount(agentInfo.AgentId(), agentInfo.AgentName(), _Login, _Password, _TerminalId);
        else
          accounts.EditAccount(_Id, agentInfo.AgentName(), _Login, _Password, _TerminalId);
        dismissDialog(0);
        setResult(Consts.RESULT_RELOAD);
        finish();
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
