package org.pvoid.apteryxaustralis.ui;

import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.Consts;
import org.pvoid.apteryxaustralis.UpdateStatusService;
import org.pvoid.common.adapters.CustomItemsAdapter;
import org.pvoid.common.views.options.OptionView;
import org.pvoid.common.views.options.OptionViewCheckbox;
import org.pvoid.common.views.options.OptionViewSound;
import org.pvoid.common.views.options.OptionViewSpiner;
import org.pvoid.common.views.options.OptionViewSpiner.OptionViewSpinerItem;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

public class CommonSettings extends ListActivity// implements OnClickListener,OnItemSelectedListener
{
  private OptionViewCheckbox _Autocheck;
  private OptionViewSpiner _Interval;
  private OptionViewCheckbox _UseVibration;
  private OptionViewSound _Sound;
  
  private class OptionsListAdapter extends CustomItemsAdapter
  {
    @Override
    protected void Populate()
    {
      AddItem(_Autocheck);
      AddItem(_Interval);
      AddItem(_UseVibration);
      AddItem(_Sound);
    }
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
////////
    SharedPreferences prefs = getSharedPreferences(Consts.APTERYX_PREFS, MODE_PRIVATE);
    boolean enabled = prefs.getBoolean(Consts.PREF_AUTOCHECK, false);    
////////
    _Autocheck = new OptionViewCheckbox(R.string.settings_autocheck, this);
    _Autocheck.setValue(enabled);
    _Autocheck.setOnChangeListener(new OptionView.OnChangeListener()
    {
      @Override
      public void onChange(OptionView option)
      {
        boolean enabled = _Autocheck.getValue();
        SharedPreferences prefs = getSharedPreferences(Consts.APTERYX_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(Consts.PREF_AUTOCHECK, enabled);
        edit.commit();
      ///////
        Intent serviceIntent = new Intent(CommonSettings.this,UpdateStatusService.class);
        if(enabled)
          startService(serviceIntent);
        else
          stopService(serviceIntent);
      ///////
        _Interval.setEnabled(enabled);
        _UseVibration.setEnabled(enabled);
        _Sound.setEnabled(enabled);
      }
    });
    
    _Interval = new OptionViewSpiner(R.string.settings_interval, this);
    _Interval.setOnChangeListener(new OptionView.OnChangeListener()
    {
      @Override
      public void onChange(OptionView option)
      {
        OptionViewSpinerItem selected = _Interval.getValue();
        if(selected!=null)
        {
          SharedPreferences prefs = getSharedPreferences(Consts.APTERYX_PREFS, MODE_PRIVATE);
          SharedPreferences.Editor edit = prefs.edit();
          edit.putInt(Consts.PREF_INTERVAL, selected.getInteger());
          edit.commit();
        }
      }
    });
    _Interval.setEnabled(enabled);
////////
    OptionViewSpinerItem[] items = new OptionViewSpiner.OptionViewSpinerItem[Consts.INTERVAL_NAMES.length];
    int interval = prefs.getInt(Consts.PREF_INTERVAL, 10800000);
    int selected_index = -1;
    for(int i=0;i<Consts.INTERVAL_NAMES.length;++i)
    {
      items[i] = _Interval.new OptionViewSpinerItem(getString(Consts.INTERVAL_NAMES[i]), Consts.INTERVALS[i]);
      if(Consts.INTERVALS[i] == interval)
        selected_index = i;
    }
    _Interval.SetItems(items);
    if(selected_index>-1)
      _Interval.SetSelectedItem(items[selected_index]);
////////
    _UseVibration = new OptionViewCheckbox(R.string.settings_usevibro, this);
    _UseVibration.setValue(prefs.getBoolean(Consts.PREF_USEVIBRO, false));
    _UseVibration.setOnChangeListener(new OptionView.OnChangeListener()
    {
      @Override
      public void onChange(OptionView option)
      {
        SharedPreferences prefs = getSharedPreferences(Consts.APTERYX_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(Consts.PREF_USEVIBRO, _UseVibration.getValue());
        edit.commit();
      }
    });
    _UseVibration.setEnabled(enabled);
////////
    _Sound = new OptionViewSound(R.string.settings_melody, this);
    String uri = prefs.getString(Consts.PREF_SOUND,"");
    if(uri.length()==0)
      _Sound.DontUseSound();
    else if(uri.equalsIgnoreCase("default"))
      _Sound.UseDefaultSound();
    else
      _Sound.setValue(Uri.parse(uri));
    _Sound.setOnChangeListener(new OptionView.OnChangeListener()
    {
      @Override
      public void onChange(OptionView option)
      {
        SharedPreferences prefs = getSharedPreferences(Consts.APTERYX_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        if(_Sound.NoSound())
          edit.putString(Consts.PREF_SOUND,"");
        else if(_Sound.DefaultSound())
          edit.putString(Consts.PREF_SOUND,"default");
        else
          edit.putString(Consts.PREF_SOUND,_Sound.getValue().toString());
        edit.commit();
      }
    });
    _Sound.setEnabled(enabled);
////////
    setListAdapter(new OptionsListAdapter());
  }
}
