package org.pvoid.apteryxaustralis.ui;

import org.pvoid.apteryxaustralis.Consts;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.UpdateStatusService;
import org.pvoid.apteryxaustralis.Utils;

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
import android.preference.PreferenceActivity;
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
    
    if(preferences.getBoolean(Consts.PREF_AUTOCHECK, false))
    {
      _Autocheck.setChecked(true);
    }
    else
    {
      _Intervals.setEnabled(false);
      _UseVibro.setEnabled(false);
      _Ringtone.setEnabled(false);
    }
    //////
    int interval = preferences.getInt(Consts.PREF_INTERVAL, Consts.INTERVALS[Consts.DEFAULT_INTERVAL]);
    String intervalText = Integer.toString(interval);
    int index = _Intervals.findIndexOfValue(intervalText);
    if(index>-1)
    {
      _Intervals.setSummary(_Intervals.getEntries()[index]);
      _Intervals.setValue(intervalText);
    }
    //////
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
    //////
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
    //////
    String sound_uri = preferences.getString(Consts.PREF_SOUND, "");
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
