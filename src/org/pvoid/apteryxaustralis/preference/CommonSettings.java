package org.pvoid.apteryxaustralis.preference;

import java.util.ArrayList;
import java.util.List;

import org.pvoid.apteryxaustralis.Consts;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.UpdateStatusService;
import org.pvoid.apteryxaustralis.Utils;
import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.Accounts;
import org.pvoid.apteryxaustralis.ui.AddAccountActivity;

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

public class CommonSettings extends PreferenceActivity
{
  private CheckBoxPreference _Autocheck;
  private ListPreference _Intervals;
  private CheckBoxPreference _UseVibro;
  private RingtonePreference _Ringtone;
  
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.layout.settings);
    
    _Autocheck = (CheckBoxPreference)findPreference("autocheck");
    _Intervals = (ListPreference)findPreference("interval");
    _UseVibro = (CheckBoxPreference) findPreference("usevibro");
    _Ringtone = (RingtonePreference) findPreference("usesound");
    _Ringtone.setRingtoneType(RingtoneManager.TYPE_NOTIFICATION);
    
    SharedPreferences preferences = getSharedPreferences(Consts.APTERYX_PREFS, MODE_PRIVATE);
    
    InitializeAutoupdate(preferences.getBoolean(Consts.PREF_AUTOCHECK, false));
    InitializeInterval(preferences.getInt(Consts.PREF_INTERVAL, Consts.INTERVALS[Consts.DEFAULT_INTERVAL]));
    InitializeVibration();
    InitializeSound(preferences.getString(Consts.PREF_SOUND, ""));
    InitializeAgents();
  }

  private void InitializeAgents()
  {
/////// В последующем переделать
    Accounts accounts = new Accounts(this);
    ArrayList<Account> adapter = new ArrayList<Account>();
    accounts.GetAccounts(adapter);
///////
    PreferenceCategory category = (PreferenceCategory)findPreference("accounts");
    AddAccount add_account = new AddAccount(this);
    add_account.setOnPreferenceClickListener(new OnPreferenceClickListener()
    {
      @Override
      public boolean onPreferenceClick(Preference preference)
      {
        Intent intent = new Intent(CommonSettings.this, AddAccountActivity.class);
        startActivity(intent);
        return false;
      }
    });
    category.addPreference(add_account);
    for(Account account : adapter)
    {
      AccountPreference accountPreference = new AccountPreference(this, account.Id, account.Title);
      category.addPreference(accountPreference);
    }
  }
  
  private void InitializeSound(String sound_uri)
  {
    setSoundSummary(sound_uri);
    _Ringtone.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
    {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue)
      {
        SharedPreferences prefs = getSharedPreferences(Consts.APTERYX_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(Consts.PREF_SOUND,(String)newValue);
        edit.commit();
        return(CommonSettings.this.setSoundSummary((String)newValue));
      }
    });
  }

  private void InitializeVibration()
  {
    _UseVibro.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
    {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue)
      {
        SharedPreferences prefs = getSharedPreferences(Consts.APTERYX_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(Consts.PREF_USEVIBRO, newValue == Boolean.TRUE);
        edit.commit();
        return true;
      }
    });
  }
/**
 * 
 * @param interval
 */
  private void InitializeInterval(int interval)
  {
    String intervalText = Integer.toString(interval);
    int index = _Intervals.findIndexOfValue(intervalText);
    if(index>-1)
    {
      _Intervals.setSummary(_Intervals.getEntries()[index]);
      _Intervals.setValue(intervalText);
    }
    //////
    _Intervals.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
    {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue)
      {
        int interval = Integer.parseInt((String)newValue);
        if(interval!=0)
        {
          SharedPreferences prefs = getSharedPreferences(Consts.APTERYX_PREFS, MODE_PRIVATE);
          SharedPreferences.Editor edit = prefs.edit();
          edit.putInt(Consts.PREF_INTERVAL, interval);
          edit.commit();
          //////
          int index = _Intervals.findIndexOfValue((String)newValue);
          if(index>-1)
          {
            _Intervals.setSummary(_Intervals.getEntries()[index]);
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
      _Autocheck.setChecked(true);
    }
    else
    {
      _Intervals.setEnabled(false);
      _UseVibro.setEnabled(false);
      _Ringtone.setEnabled(false);
    }
////////
    _Autocheck.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
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
        SharedPreferences prefs = getSharedPreferences(Consts.APTERYX_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(Consts.PREF_AUTOCHECK, checked);
        edit.commit();
      ///////
        _Autocheck.setChecked(checked);
        _Intervals.setEnabled(checked);
        _UseVibro.setEnabled(checked);
        _Ringtone.setEnabled(checked);
        return true;
      }
    });
  }
  /**
   * Устанавливает текстовое имя звука в описание опции  
   * @param uriString  uri выбранного звука
   * @return
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
      _Ringtone.setSummary(summary);
      return(true);
    }
    return(false);
  }
}
