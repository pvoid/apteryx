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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.*;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.UpdateStatusService;
import org.pvoid.apteryxaustralis.net.ContentLoader;
import org.pvoid.apteryxaustralis.net.Request;
import org.pvoid.apteryxaustralis.storage.AccountsProvider;

public class CommonSettings extends PreferenceActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, DialogInterface.OnClickListener
{
  private CheckBoxPreference _mAutocheck;
  private ListPreference _mIntervals;
  private ListPreference _mWarnLevels;
  private CheckBoxPreference _mUseVibro;
  private ArrayAdapter<String> _mCommands;
  private PreferenceCategory _mAccountsCategory;
  private RingtonePreference _mRingtone;
  private AddAccountDialog _mAddAccountDialog = null;
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
   * ContentObserver для мониторинга списка аккаунтов
   */
  private final AccountsObserver _mAccountsObserver = new AccountsObserver(new Handler());
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
//////// Инициализируем использование вибрации
    _mUseVibro.setChecked(Preferences.getUseVibration(this));
    _mUseVibro.setOnPreferenceChangeListener(this);
//////// Инициализируем уровень оповещения
    String levelText = Integer.toString(Preferences.getWarnLevel(this));
    int index1 = _mWarnLevels.findIndexOfValue(levelText);
    if(index1 >-1)
    {
      _mWarnLevels.setSummary(_mWarnLevels.getEntries()[index1]);
      _mWarnLevels.setValue(levelText);
    }
    _mWarnLevels.setOnPreferenceChangeListener(this);
//////// Инициализируем список аккаунтов
    _mAccountsCategory = (PreferenceCategory)findPreference("accounts");
    AddAccount add_account = new AddAccount(this);
    add_account.setOnPreferenceClickListener(this);
    _mAccountsCategory.addPreference(add_account);
////////
    fillAccountsList();
//////// Команды управления
    _mCommands = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item);
    _mCommands.add(getString(R.string.edit));
    _mCommands.add(getString(R.string.delete));
  }
  /**
   * Заполняет список аккаунтов
   */
  private void fillAccountsList()
  {
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
  }
  /**
   * Activity восстановлена
   */
  @Override
  protected void onResume()
  {
    super.onResume();
    getContentResolver().registerContentObserver(AccountsProvider.Accounts.CONTENT_URI,true,_mAccountsObserver);
  }
  /**
   * Activity бюольше не видна
   */
  @Override
  protected void onPause()
  {
    super.onPause();
    getContentResolver().unregisterContentObserver(_mAccountsObserver);
  }
  /**
   * Обработка щелчка по строчке "Добавить аккаунт"
   * @param preference строка по которой щелкнули
   * @return в нашем случае всегда false
   */
  @Override
  public boolean onPreferenceClick(Preference preference)
  {
    if(_mAddAccountDialog==null)
    {
      _mAddAccountDialog = new AddAccountDialog(this);
      _mAddAccountDialog.setOnAddClickListener(this);
    }
    _mAddAccountDialog.show();
    return false;
  }
  /**
   * Изменено значение настройки
   * @param preference изменяемая настройка
   * @param value новое значение
   * @return возвращает true если мы обработали изменение
   */
  public boolean onPreferenceChange(Preference preference, Object value)
  {
/////// Медлодия уведомления
    if(preference == _mRingtone)
    {
      Preferences.setSound(this,(String)value);
      return(setSoundSummary((String)value));
    }
/////// Использование вибры
    if(_mUseVibro == preference)
    {
      Preferences.setUseVibration(this,(Boolean)value);
      return true;
    }
///////// Интервал обновления
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
///////// Автоматической проверкой
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
////////// Уровень оповещения
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
////////// Это не наше
    return false;
  }
  /**
   * Устанавливает текстовое описание звука
   * @param uriString урл звука
   * @return true если все прошло успешно
   */
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
  /**
   * Редактируем аккаунт
   * @param preference Строка с аккаунтом
   */
  private void EditPreference(Preference preference)
  {
    /*AccountPreference accountPreference = (AccountPreference)preference;
    Intent intent = new Intent(this,AddAccountActivity.class);
    intent.putExtra(AddAccountActivity.EXTRA_ACCOUNT_ID,accountPreference.getId());
    startActivityForResult(intent,REQUEST_EDIT_ACCOUNT);*/
    // TODO: Редактирование аккаунта
    throw new RuntimeException("Not implemented");
  }
  /**
   * Удаление аккаунта
   * @param preference строка с аккаунтом
   */
  private void DeletePreference(Preference preference)
  {
    //_mStorage.deleteAccount(((AccountPreference)preference).getId());
    //_mAccountsCategory.removePreference(preference);
    // TODO: Удаление аккаунта
    throw new RuntimeException("Not implemented");
  }
  /**
   * Обработка нажатия на кнопку "Добавить" в диалоге
   * @param dialogInterface диалог
   * @param type            тип нажатой кнопки
   */
  @Override
  public void onClick(DialogInterface dialogInterface, int type)
  {
    Bundle accountData = ((AddAccountDialog)dialogInterface).getAccountData();
    (new AddAccountTask()).execute(accountData);
  }
  /**
   * Отработка необходимости создать диалог
   * @param id идентификатор диалога
   * @return возвращает диалог
   */
  @Override
  protected Dialog onCreateDialog(int id)
  {
    if(id==0)
    {
      ProgressDialog dialog = new ProgressDialog(this);
      dialog.setIndeterminate(true);
      dialog.setMessage(getString(R.string.auth_process));
      return dialog;
    }
    return super.onCreateDialog(id);
  }
  /**
   * ContentObserver для наблюдения за списком аккаунтов
   */
  private class AccountsObserver extends ContentObserver
  {
    public AccountsObserver(Handler handler)
    {
      super(handler);
    }

    @Override
    public void onChange(boolean selfChange)
    {
      while(_mAccountsCategory.getPreferenceCount()>1)
      {
        Preference preference = _mAccountsCategory.getPreference(1);
        _mAccountsCategory.removePreference(preference);
      }
      //---
      fillAccountsList();
    }
  }
  /**
   * Асинхронное задание для добавления аккаунта
   */
  private class AddAccountTask extends AsyncTask<Bundle,Void,Integer>
  {
    @Override
    protected void onPreExecute()
    {
      showDialog(0);
    }

    @Override
    protected Integer doInBackground(Bundle... bundles)
    {
      if(bundles.length!=1)
        return null;
      //---
      return ContentLoader.addAccount(CommonSettings.this, bundles[0]);
    }

    @Override
    protected void onPostExecute(Integer result)
    {
      dismissDialog(0);
      removeDialog(0);
      ////////
      if(result==null)
        return;
      ////////
      int textId = 0;
      if(result == Request.RES_ERR_NETWORK_ERROR)
        textId = R.string.network_error;
      else if(result<Request.RES_ERR_CUSTOM_FIRST)
        textId = R.string.network_error;//_mStorage.errorMessage(-result+IStorage.RES_ERR_CUSTOM_FIRST);

      if(textId>0)
        Toast.makeText(CommonSettings.this, textId, Toast.LENGTH_SHORT).show();
    }
  }
}
