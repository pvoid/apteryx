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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.*;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.UpdateStatusService;
import org.pvoid.apteryxaustralis.storage.AccountsProvider;
import org.pvoid.apteryxaustralis.types.Account;

public class CommonSettings extends PreferenceActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, DialogInterface.OnClickListener
{
  private final static int REQUEST_NEW_ACCOUNT = 1;
  private final static int REQUEST_EDIT_ACCOUNT = 2;

  public final static int RESULT_REFRESH = 1;
  public final static int RESULT_RELOAD = 2;

  private CheckBoxPreference _mAutocheck;
  private ListPreference _mIntervals;
  private ListPreference _mWarnLevels;
  private CheckBoxPreference _mUseVibro;
  private ArrayAdapter<String> _mCommands;
  private PreferenceCategory _mAccountsCategory;
  private RingtonePreference _mRingtone;
  private boolean _mResultIsReload = false;
  /**
   * Обработчик щелчков по пунктам контекстного меню аккаунта
   */
  private final Preference.OnPreferenceClickListener accountClickListener = new Preference.OnPreferenceClickListener()
  {
    @Override
    public boolean onPreferenceClick(final Preference preference)
    {
      AlertDialog.Builder dialog = new AlertDialog.Builder(CommonSettings.this);
      dialog.setAdapter(_mCommands, new DialogInterface.OnClickListener()
      {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
          switch(which)
          {
            case 0:
              EditPreference(preference);
              break;
            case 1:
              DeletePreference(preference);
              break;
          }
        }
      });
      dialog.setCancelable(true);
      dialog.show();
      return(true);
    }
  };
  /**
   * Активити создана
   * @param savedInstanceState преддущее состояние
   */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.settings);
/////////
    _mRingtone = (RingtonePreference) findPreference("sound");
    _mAutocheck = (CheckBoxPreference)findPreference("autocheck");
    _mIntervals = (ListPreference)findPreference("interval");
    _mUseVibro = (CheckBoxPreference) findPreference("vibro");
    _mWarnLevels = (ListPreference) findPreference("notify_level");
///////// Устанавливаем текущий используемый звук
    _mRingtone.setOnPreferenceChangeListener(this);
    setSoundSummary(Preferences.getSound(this));
///////// Устанавливаем текущее состояние автообновление
    if(Preferences.getAutoUpdate(this))
    {
      _mAutocheck.setChecked(true);
    }
    else
    {
      _mIntervals.setEnabled(false);
      _mUseVibro.setEnabled(false);
      _mRingtone.setEnabled(false);
      _mWarnLevels.setEnabled(false);
    }
    _mAutocheck.setOnPreferenceChangeListener(this);
//////// Устанавливаем интервалы и выбираем текущий
    String intervalText = Integer.toString(Preferences.getUpdateInterval(this));
    int index = _mIntervals.findIndexOfValue(intervalText);
    if(index>-1)
    {
      _mIntervals.setSummary(_mIntervals.getEntries()[index]);
      _mIntervals.setValue(intervalText);
    }
    _mIntervals.setOnPreferenceChangeListener(this);
////////
    initializeVibration();
    initializeWarnLevel();
    initializeAccounts();
  }

  @Override
  public boolean onPreferenceClick(Preference preference)
  {
    AddAccountDialog dialog = new AddAccountDialog(this);
    dialog.setOnAddClickListener(this);
    dialog.show();
    return false;
  }

  public boolean onPreferenceChange(Preference preference, Object value)
  {
    if(preference == _mRingtone)
    {
      Preferences.setSound(this,(String)value);
      return(setSoundSummary((String)value));
    }
///////
if(_mUseVibro == preference)
    {
      Preferences.setUseVibration(this,(Boolean)value);
      return true;
    }
/////////
    if(_mIntervals == preference)
    {
      int interval = Integer.parseInt((String)value);
      if(interval!=0)
      {
        Preferences.setUpdateInterval(this,interval);
        //////
        Intent serviceIntent = new Intent(this,UpdateStatusService.class);
        this.stopService(serviceIntent);
        this.startService(serviceIntent);
        //////
        int index = _mIntervals.findIndexOfValue((String)value);
        if(index>-1)
        {
          _mIntervals.setSummary(_mIntervals.getEntries()[index]);
        }
        //////
        return(true);
      }
      return(false);
    }
/////////
    if(_mAutocheck == preference)
    {
      boolean checked = false;
      Intent serviceIntent = new Intent(this,UpdateStatusService.class);
      if(value == Boolean.TRUE)
      {
        checked = true;
        this.startService(serviceIntent);
      }
      else
        this.stopService(serviceIntent);
//////////
      Preferences.setAutoUpdate(this, checked);
//////////
      _mAutocheck.setChecked(checked);
      _mIntervals.setEnabled(checked);
      _mUseVibro.setEnabled(checked);
      _mRingtone.setEnabled(checked);
      _mWarnLevels.setEnabled(checked);
      return true;
    }
//////////
    if(_mWarnLevels==preference)
    {
      int level = Integer.parseInt((String)value);
      //////
      Preferences.setWarnLevel(this,level);
      //////
      int index = _mWarnLevels.findIndexOfValue((String)value);
      if(index>-1)
      {
        _mWarnLevels.setSummary(_mWarnLevels.getEntries()[index]);
      }
      //////
      return(true);
    }
//////////
    return false;
  }

  private boolean setSoundSummary(String uriString)
   {
     String summary = null;
     if(TextUtils.isEmpty(uriString))
     {
       summary = getString(R.string.no_sound);
     }
     else
     {
       Uri uri = Uri.parse(uriString);
       if(uri.equals(Settings.System.DEFAULT_NOTIFICATION_URI))
       {
         summary = getString(R.string.default_sound);
       }
       else
       {
         Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
         if(ringtone!=null)
           summary = ringtone.getTitle(this);
       }
     }

     if(summary!=null)
     {
       _mRingtone.setSummary(summary);
       return(true);
     }
     return(false);
   }

  private void initializeWarnLevel()
  {
    String levelText = Integer.toString(Preferences.getWarnLevel(this));
    int index = _mWarnLevels.findIndexOfValue(levelText);
    if(index>-1)
    {
      _mWarnLevels.setSummary(_mWarnLevels.getEntries()[index]);
      _mWarnLevels.setValue(levelText);
    }
    //////
    _mWarnLevels.setOnPreferenceChangeListener(this);
  }

  private void initializeVibration()
  {
    _mUseVibro.setChecked(Preferences.getUseVibration(this));
    _mUseVibro.setOnPreferenceChangeListener(this);
  }

  private void initializeAccounts()
  {
    _mAccountsCategory = (PreferenceCategory)findPreference("accounts");
    AddAccount add_account = new AddAccount(this);
    add_account.setOnPreferenceClickListener(this);
    _mAccountsCategory.addPreference(add_account);
////////
    Cursor accounts = managedQuery(AccountsProvider.Accounts.CONTENT_URI,
                                   new String[] {AccountsProvider.Accounts.COLUMN_ID,AccountsProvider.Accounts.COLUMN_TITLE},
                                   null,null,AccountsProvider.Accounts.COLUMN_TITLE+" ASC");
    try
    {
    if(accounts!=null )
      while(accounts.moveToNext())
      {
        AccountPreference accountPreference = new AccountPreference(this, accounts.getLong(0), accounts.getString(1));
        accountPreference.setOnPreferenceClickListener(accountClickListener);
        _mAccountsCategory.addPreference(accountPreference);
      }
    }
    finally
    {
      if(accounts!=null)
        accounts.close();
    }
//////// Команды управления
    _mCommands = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item);
    _mCommands.add(getString(R.string.edit));
    _mCommands.add(getString(R.string.delete));
  }

  protected void onActivityResult (int requestCode, int resultCode, Intent data)
  {
    if(requestCode==REQUEST_NEW_ACCOUNT && resultCode==RESULT_OK)
    {
      Account account = data.getParcelableExtra(AddAccountActivity.EXTRA_ACCOUNT);

      AccountPreference accountPreference = new AccountPreference(this, account.id, account.title);
      accountPreference.setOnPreferenceClickListener(accountClickListener);
      _mAccountsCategory.addPreference(accountPreference);
      if(!_mResultIsReload)
        setResult(RESULT_REFRESH);
    }
    else if(requestCode==REQUEST_EDIT_ACCOUNT && resultCode==RESULT_OK && !_mResultIsReload)
    {
      setResult(RESULT_REFRESH);
    }
    else
      super.onActivityResult(requestCode,resultCode,data);
  }

  private void EditPreference(Preference preference)
  {
    AccountPreference accountPreference = (AccountPreference)preference;
    Intent intent = new Intent(this,AddAccountActivity.class);
    intent.putExtra(AddAccountActivity.EXTRA_ACCOUNT_ID,accountPreference.getId());
    startActivityForResult(intent,REQUEST_EDIT_ACCOUNT);
  }

  private void DeletePreference(Preference preference)
  {
    //_mStorage.deleteAccount(((AccountPreference)preference).getId());
    _mAccountsCategory.removePreference(preference);
    setResult(RESULT_RELOAD);
    _mResultIsReload = true;
  }

  @Override
  public void onClick(DialogInterface dialogInterface, int type)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
