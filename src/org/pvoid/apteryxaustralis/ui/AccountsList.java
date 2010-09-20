package org.pvoid.apteryxaustralis.ui;

import java.util.ArrayList;

import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.Consts;
import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.Accounts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AccountsList extends Activity
{
  private ArrayAdapter<Account> _Adapter;
  private Accounts _Accounts;
  
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.accounts);
    _Adapter = new ArrayAdapter<Account>(this, android.R.layout.simple_list_item_1);
    _Accounts = new Accounts(this);
    ListView list = (ListView)findViewById(R.id.accounts_list);
    setAccountsList();
    list.setAdapter(_Adapter);
    registerForContextMenu(list);
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
    switch(view.getId())
    {
      case R.id.accounts_list:
        AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(_Adapter.getItem(info.position).Title);
        menu.add(Menu.NONE, Consts.MENU_EDIT, 0, R.string.edit);
        menu.add(Menu.NONE, Consts.MENU_DELETE, 1, R.string.delete);
        break;
    }
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
            _Accounts.DeleteAccount(account.Id);
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
    intent.putExtra(Consts.COLUMN_ID, account.Id);
    intent.putExtra(Consts.COLUMN_LOGIN, account.Login);
    intent.putExtra(Consts.COLUMN_TERMINAL, account.Terminal);
    intent.putExtra(Consts.COLUMN_PASSWORD, account.PasswordHash);
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
