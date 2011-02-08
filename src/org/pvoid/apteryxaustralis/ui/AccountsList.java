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

import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.Consts;
import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.Accounts;
import org.pvoid.apteryxaustralis.preference.AddAccountActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;

public class AccountsList extends Activity
{
  private ArrayAdapter<Account> _Adapter;
  private Accounts _Accounts;
  
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    /*setContentView(R.layout.accounts);
    _Adapter = new ArrayAdapter<Account>(this, android.R.layout.simple_list_item_1);
    _Accounts = new Accounts(this);
    ListView list = (ListView)findViewById(R.id.accounts_list);
    setAccountsList();
    list.setAdapter(_Adapter);
    registerForContextMenu(list);*/
  }

  private void setAccountsList()
  {
    ArrayList<Account> adapters = new ArrayList<Account>();
    _Accounts.GetAccounts(adapters);
    for(Account account:adapters)
    {
      _Adapter.add(account);
    }
  }
  
  public void AddAccount(View button)
  {
    Intent intent = new Intent(this,AddAccountActivity.class);
    startActivityForResult(intent, Consts.ACTIVITY_ADD_ACCOUNT);
  }
  
  public void onActivityResult(int requestCode,int resultCode, Intent intent)
  {
    switch(requestCode)
    {
      case Consts.ACTIVITY_ADD_ACCOUNT:
        if(resultCode == Consts.RESULT_RELOAD)
        {
          _Adapter.clear();
          setAccountsList();
          if(!isChild())
            setResult(Consts.RESULT_RELOAD);
          else
            getParent().setResult(Consts.RESULT_RELOAD);
        }
        break;
    }
  }
  
  @Override
  public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo)
  {
    /*switch(view.getId())
    {
      case R.id.accounts_list:
        AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(_Adapter.getItem(info.position).Title);
        menu.add(Menu.NONE, Consts.MENU_EDIT, 0, R.string.edit);
        menu.add(Menu.NONE, Consts.MENU_DELETE, 1, R.string.delete);
        break;
    }*/
  }
  
  private void DeleteAccount(final Account account)
  {
    DialogInterface.OnClickListener _DeleteClick = new DialogInterface.OnClickListener()
    {
      @Override
      public void onClick(DialogInterface dialog, int which)
      {
        switch (which)
        {
          case DialogInterface.BUTTON_POSITIVE:
            _Accounts.DeleteAccount(account.getId());
            _Adapter.remove(account);
            if(!isChild())
              setResult(Consts.RESULT_RELOAD);
            else
              getParent().setResult(Consts.RESULT_RELOAD);
            break;
        }
      }
    };
        
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(getString(R.string.delete_confirm)+" \""+account.toString()+"\"?")
           .setPositiveButton(getString(R.string.delete), _DeleteClick)
           .setNegativeButton(getString(R.string.cancel), _DeleteClick)
           .setTitle(R.string.delete)
           .show();
  }
  
  private void EditAccount(final Account account)
  {
    Intent intent = new Intent(this,AddAccountActivity.class);
    intent.putExtra(Consts.COLUMN_ID, account.getId());
    intent.putExtra(Consts.COLUMN_LOGIN, account.getLogin());
    intent.putExtra(Consts.COLUMN_TERMINAL, account.getTerminalId());
    intent.putExtra(Consts.COLUMN_PASSWORD, account.getPassword());
    startActivityForResult(intent, Consts.ACTIVITY_EDIT_ACCOUNT);
  }
  
  public boolean onContextItemSelected(MenuItem item)
  {
    AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
    switch(item.getItemId())
    {
      case Consts.MENU_EDIT:
        EditAccount(_Adapter.getItem(info.position));
        break;
      case Consts.MENU_DELETE:
        DeleteAccount(_Adapter.getItem(info.position));
        break;
    }
    return(true);
  }
}
