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

package org.pvoid.apteryxaustralis.preference;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Button;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.accounts.Account;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;
import org.pvoid.apteryxaustralis.storage.IStorage;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpStorage;

public class AddAccountActivity extends Activity
{
  public static final String EXTRA_ACCOUNT_ID = "id";
  public static final String EXTRA_ACCOUNT = "account";

  private EditText _mLoginEdit;
  private EditText _mPasswordEdit;
  private EditText _mTerminalEdit;
  private long _mId;
  private Account _mAccount;

  private OsmpStorage _mStorage;

  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.account);
///////
    _mStorage = new OsmpStorage(this);
    _mLoginEdit = (EditText)findViewById(R.id.login);
    _mPasswordEdit = (EditText)findViewById(R.id.password);
    _mTerminalEdit = (EditText)findViewById(R.id.terminal);
    
    Bundle extra = getIntent().getExtras();
    if(extra!=null && extra.containsKey(EXTRA_ACCOUNT_ID))
    {
      _mId = extra.getLong(EXTRA_ACCOUNT_ID);
      Account account = _mStorage.getAccount(_mId);
      if(account!=null)
      {
        _mLoginEdit.setText(account.login);
        _mLoginEdit.setEnabled(false);
        _mTerminalEdit.setText(account.terminal);
        _mTerminalEdit.setEnabled(false);
        _mPasswordEdit.requestFocus();
        Button add = (Button) findViewById(R.id.add);
        add.setText(R.string.change_password);
      }
      else
        _mId = 0;
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

  @SuppressWarnings("unused")
  public void CheckAccount(View view)
  {
////////
    String login = _mLoginEdit.getText().toString();
    if(TextUtils.isEmpty(login))
    {
      Toast.makeText(this, getString(R.string.empty_login), 200).show();
      _mLoginEdit.requestFocus();
      return;
    }
    String password = _mPasswordEdit.getText().toString();
    String passwordHash;
    if(TextUtils.isEmpty(password))
    {
      Toast.makeText(this, getString(R.string.empty_password), 200).show();
      _mPasswordEdit.requestFocus();
      return;
    }
    else
    {
      try
      {
        MessageDigest m=MessageDigest.getInstance("MD5");
        m.reset();
        m.update(password.getBytes(),0,password.length());
        BigInteger i = new BigInteger(1,m.digest());
        passwordHash = String.format("%1$032X", i).toLowerCase();
      }
      catch (NoSuchAlgorithmException e)
      { 
        //TODO: Наверное надо сообщить что MD5 нет
        return;
      }
    }
////////
    String terminalId = _mTerminalEdit.getText().toString();
    if(TextUtils.isEmpty(terminalId))
    {
      Toast.makeText(this, getString(R.string.empty_terminal), 200).show();
      _mTerminalEdit.requestFocus();
      return;
    }
////////
    _mAccount = new Account(0,null, login, passwordHash, terminalId);
    (new AddAccountTask()).execute(_mAccount);
  }

  private class AddAccountTask extends AsyncTask<Account,Void,Integer>
  {
    @Override
    protected void onPreExecute()
    {
      super.onPreExecute();
      showDialog(0);
    }

    @Override
    protected Integer doInBackground(Account... accounts)
    {
      return _mStorage.addAccount(accounts[0]);
    }

    @Override
    protected void onPostExecute(Integer result)
    {
      dismissDialog(0);
      removeDialog(0);

      if(result== IStorage.RES_OK)
      {
        Intent intent = new Intent();
        intent.putExtra("account",_mAccount);
        setResult(RESULT_OK,intent);
        finish();
      }
    }
  }
}
