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

import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.UpdateStatusService;
import org.pvoid.apteryxaustralis.Utils;
import org.pvoid.apteryxaustralis.accounts.Account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.RingtonePreference;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import org.pvoid.apteryxaustralis.storage.Storage;

public class CommonSettings extends PreferenceActivity
{
  private final static int REQUEST_NEW_ACCOUNT = 1;
  private final static int REQUEST_EDIT_ACCOUNT = 2;

  public final static int RESULT_REFRESH = RESULT_FIRST_USER+1;
  public final static int RESULT_RELOAD = RESULT_FIRST_USER+2;

  public static final String APTERYX_PREFS = "apteryx-settings";
  public static final String PREF_INTERVAL = "apteryx.update-interval";
  public static final String PREF_AUTOCHECK = "apteryx.autoupdate";
  public static final String PREF_USEVIBRO = "apteryx.usevibro";
  public static final String PREF_SOUND = "apteryx.sound";
  public static final String PREF_LASTUPDATE = "apteryx.lastupdate";

  public static final int   DEFAULT_INTERVAL = 3600000;

  private CheckBoxPreference _mAutocheck;
  private ListPreference _mIntervals;
  private CheckBoxPreference _mUseVibro;
  private RingtonePreference _mRingtone;
  private ArrayAdapter<String> _mCommands;
  private PreferenceCategory _mAccountsCategory;

  private boolean _mResultIsReload = false;
  
  private OnPreferenceClickListener accountClickListener = new OnPreferenceClickListener()
  {
    @Override
    public boolean onPreferenceClick(final Preference preference)
    {
      AlertDialog.Builder dialog = new AlertDialog.Builder(CommonSettings.this);
      dialog.setAdapter(_mCommands, new OnClickListener()
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
  
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.settings);
    
    _mAutocheck = (CheckBoxPreference)findPreference("autocheck");
    _mIntervals = (ListPreference)findPreference("interval");
    _mUseVibro = (CheckBoxPreference) findPreference("usevibro");
    _mRingtone = (RingtonePreference) findPreference("usesound");
    _mRingtone.setRingtoneType(RingtoneManager.TYPE_NOTIFICATION);
    
    SharedPreferences preferences = getSharedPreferences(APTERYX_PREFS, MODE_PRIVATE);
    
    InitializeAutoupdate(preferences.getBoolean(PREF_AUTOCHECK, false));
    InitializeInterval(preferences.getInt(PREF_INTERVAL, DEFAULT_INTERVAL));
    InitializeVibration();
    InitializeSound(preferences.getString(PREF_SOUND, ""));
    InitializeAccounts();
  }

  private void InitializeAccounts()
  {
    _mAccountsCategory = (PreferenceCategory)findPreference("accounts");
    AddAccount add_account = new AddAccount(this);
    add_account.setOnPreferenceClickListener(new OnPreferenceClickListener()
    {
      @Override
      public boolean onPreferenceClick(Preference preference)
      {
        Intent intent = new Intent(CommonSettings.this, AddAccountActivity.class);
        startActivityForResult(intent,REQUEST_NEW_ACCOUNT);
        return false;
      }
    });
    _mAccountsCategory.addPreference(add_account);
////////
    Iterable<Account> accounts = Storage.getAccountsInfo(this);
    if(accounts!=null)
      for(Account account : accounts)
      {
        AccountPreference accountPreference = new AccountPreference(this, account.getId(), account.getTitle());
        accountPreference.setOnPreferenceClickListener(accountClickListener);
        _mAccountsCategory.addPreference(accountPreference);
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
      long id = data.getLongExtra(AddAccountActivity.EXTRA_ACCOUNT_ID, 0);
      String title = data.getStringExtra(AddAccountActivity.EXTRA_ACCOUNT_TITLE);
      
      AccountPreference accountPreference = new AccountPreference(this, id, title);
      accountPreference.setOnPreferenceClickListener(accountClickListener);
      _mAccountsCategory.addPreference(accountPreference);
      if(!_mResultIsReload)
        setResult(RESULT_REFRESH);
    }
    else if(requestCode==REQUEST_EDIT_ACCOUNT && resultCode==RESULT_OK && !_mResultIsReload)
    {
      setResult(RESULT_REFRESH);
    }
  }
  
  private void InitializeSound(String sound_uri)
  {
    setSoundSummary(sound_uri);
    _mRingtone.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
    {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue)
      {
        SharedPreferences prefs = getSharedPreferences(APTERYX_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(PREF_SOUND,(String)newValue);
        edit.commit();
        return(CommonSettings.this.setSoundSummary((String)newValue));
      }
    });
  }

  private void InitializeVibration()
  {
    _mUseVibro.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
    {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue)
      {
        SharedPreferences prefs = getSharedPreferences(APTERYX_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(PREF_USEVIBRO, newValue == Boolean.TRUE);
        edit.commit();
        return true;
      }
    });
  }
/**
 * 
 * @param interval начальный интервал
 */
  private void InitializeInterval(int interval)
  {
    String intervalText = Integer.toString(interval);
    int index = _mIntervals.findIndexOfValue(intervalText);
    if(index>-1)
    {
      _mIntervals.setSummary(_mIntervals.getEntries()[index]);
      _mIntervals.setValue(intervalText);
    }
    //////
    _mIntervals.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
    {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue)
      {
        int interval = Integer.parseInt((String)newValue);
        if(interval!=0)
        {
          SharedPreferences prefs = getSharedPreferences(APTERYX_PREFS, MODE_PRIVATE);
          SharedPreferences.Editor edit = prefs.edit();
          edit.putInt(PREF_INTERVAL, interval);
          edit.commit();
          //////
          int index = _mIntervals.findIndexOfValue((String)newValue);
          if(index>-1)
          {
            _mIntervals.setSummary(_mIntervals.getEntries()[index]);
          }
          //////
          return(true);
        }
        return(false);
      }
    });
  }
  /**
   * Настраивает галочку переключающую автоматическое обновление
   * @param state  Текущее состояние автоматического обновления
   */
  private void InitializeAutoupdate(boolean state)
  {
    if(state)
    {
      _mAutocheck.setChecked(true);
    }
    else
    {
      _mIntervals.setEnabled(false);
      _mUseVibro.setEnabled(false);
      _mRingtone.setEnabled(false);
    }
////////
    _mAutocheck.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
    {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue)
      {
        boolean checked = false; 
        Intent serviceIntent = new Intent(CommonSettings.this,UpdateStatusService.class);
        if(newValue == Boolean.TRUE)
        {
          checked = true;
          startService(serviceIntent);
        }
        else
          stopService(serviceIntent);
      ///////
        SharedPreferences prefs = getSharedPreferences(APTERYX_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(PREF_AUTOCHECK, checked);
        edit.commit();
      ///////
        _mAutocheck.setChecked(checked);
        _mIntervals.setEnabled(checked);
        _mUseVibro.setEnabled(checked);
        _mRingtone.setEnabled(checked);
        return true;
      }
    });
  }
  /**
   * Устанавливает текстовое имя звука в описание опции  
   * @param uriString  uri выбранного звука
   * @return признак того что изменение принято
   */
  private boolean setSoundSummary(String uriString)
  {
    String summary = null;
    if(Utils.isEmptyString(uriString))
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
        Ringtone ringtone = RingtoneManager.getRingtone(CommonSettings.this, uri);
        if(ringtone!=null)
          summary = ringtone.getTitle(CommonSettings.this);
      }
    }
    
    if(summary!=null)
    {
      _mRingtone.setSummary(summary);
      return(true);
    }
    return(false);
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
    Storage.deleteAccount(this,((AccountPreference)preference).getId());
    _mAccountsCategory.removePreference(preference);
    setResult(RESULT_RELOAD);
    _mResultIsReload = true;
  }
}
