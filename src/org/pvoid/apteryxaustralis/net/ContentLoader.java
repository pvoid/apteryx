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

package org.pvoid.apteryxaustralis.net;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import org.pvoid.apteryxaustralis.net.osmp.OsmpRequest;
import org.pvoid.apteryxaustralis.storage.AccountsProvider;

import java.util.Vector;

public class ContentLoader
{
  public static final String LOADING_STARTED  = "org.pvoid.apteryxaustralis.ContentLoadingBroadcast";
  public static final String LOADING_FINISHED = "org.pvoid.apteryxaustralis.ContentLoadedBroadcast";

  private static final String KEY_ACTION = "key_action";
  private static final String KEY_TERMINAL = "key_terminal";
  private static final int ACTION_NONE   = 0;
  private static final int ACTION_REBOOT = 1;

  private static final OsmpRequest    _sOsmpRequest = new OsmpRequest();
  private static volatile boolean     _sLoading = false;
  private static final Vector<Bundle> _sTasks = new Vector<Bundle>(2);
  private static Context              _sContext;
  private static final RequestThread  _sThread = new RequestThread();
  private static final Intent         _sLoadingIntent = new Intent(LOADING_STARTED);
  private static final Intent         _sLoadedIntent = new Intent(LOADING_FINISHED);
  static
  {
    _sThread.start();
  }
  /**
   * Добавляет новый аккаунт
   * @param context     контекст исполнения
   * @param accountData данные по аккаунту
   * @return код результата
   */
  public static int addAccount(Context context, Bundle accountData)
  {
    synchronized(_sOsmpRequest)
    {
      int result = _sOsmpRequest.checkAccount(context,accountData);
      if(result==0)
        result = _sOsmpRequest.getBalances(context,accountData);
      if(result==0)
        result = _sOsmpRequest.getTerminals(context,accountData);
      ////////
      return result;
    }
  }

  public static void refresh(Context context, Bundle accountData)
  {
    if(_sContext ==null)
      _sContext = context.getApplicationContext();
//////////
    synchronized(_sTasks)
    {
      _sTasks.add(accountData);
      _sTasks.notify();
    }
  }

  public static void rebootTerminal(Context context, Bundle accountData, long terminalId)
  {
    accountData.putLong(KEY_TERMINAL,terminalId);
    accountData.putInt(KEY_ACTION,ACTION_REBOOT);
    if(_sContext ==null)
      _sContext = context.getApplicationContext();
//////////
    synchronized(_sTasks)
    {
      _sTasks.add(accountData);
      _sTasks.notify();
    }
  }

  public static void refresh(Context context)
  {
    refresh(context, null);
  }

  public static boolean getAccountData(Context context, long accountId, Bundle bundle)
  {
    Cursor cursor = context.getContentResolver().query(AccountsProvider.Accounts.CONTENT_URI,
                                                       new String[] {AccountsProvider.Accounts.COLUMN_LOGIN,
                                                                     AccountsProvider.Accounts.COLUMN_PASSWORD,
                                                                     AccountsProvider.Accounts.COLUMN_CUSTOM1},
                                                       AccountsProvider.Accounts.COLUMN_ID+"=?",new String[] {Long.toString(accountId)},
                                                       null);
    try
    {
      if(cursor.moveToFirst())
      {
        bundle.putString(OsmpRequest.ACCOUNT_ID,Long.toString(accountId));
        bundle.putString(OsmpRequest.LOGIN,cursor.getString(0));
        bundle.putString(OsmpRequest.PASSWORD,cursor.getString(1));
        bundle.putString(OsmpRequest.TERMINAL,cursor.getString(2));
        return true;
      }
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
    return false;
  }

  public static void checkStates(Context context)
  {
    if(_sContext ==null)
      _sContext = context.getApplicationContext();
    //---
    synchronized(_sTasks)
    {
      _sContext.sendBroadcast(_sLoadingIntent);
      _sLoading = true;
      internalRefresh();
      _sLoading = false;
      _sContext.sendBroadcast(_sLoadedIntent);
    }
  }

  private static int internalRefresh(Bundle accountData)
  {
    int result = _sOsmpRequest.getBalances(_sContext, accountData);
    if(result==0)
      result = _sOsmpRequest.getTerminals(_sContext,accountData);
    return result;
  }

  private static int internalRefresh()
  {
    final Cursor cursor = _sContext.getContentResolver().query(AccountsProvider.Accounts.CONTENT_URI,
                                                             new String[] {AccountsProvider.Accounts.COLUMN_ID,
                                                                           AccountsProvider.Accounts.COLUMN_LOGIN,
                                                                           AccountsProvider.Accounts.COLUMN_PASSWORD,
                                                                           AccountsProvider.Accounts.COLUMN_CUSTOM1},
                                                             null,null,null);
    Bundle accountData = new Bundle();
    int result = 0;
    try
    {
      while(cursor.moveToNext())
      {
        accountData.putString(OsmpRequest.ACCOUNT_ID,cursor.getString(0));
        accountData.putString(OsmpRequest.LOGIN,cursor.getString(1));
        accountData.putString(OsmpRequest.PASSWORD,cursor.getString(2));
        accountData.putString(OsmpRequest.TERMINAL,cursor.getString(3));
        if((result = internalRefresh(accountData))!=0)
          break;
      }
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
    return result;
  }

  public static boolean isLoading()
  {
    return _sLoading;
  }

  private static class RequestThread extends Thread
  {
    public RequestThread()
    {
      super();
      setName("ContentLoader");
      setDaemon(true);
    }

    @Override
    public void run()
    {
      while(true)
      {
        synchronized(_sTasks)
        {
          if(_sTasks.size()>0)
          {
            _sContext.sendBroadcast(_sLoadingIntent);
            _sLoading = true;
            do
            {
              final Bundle data = _sTasks.remove(0);
              if(data==null)
                internalRefresh();
              else
              {
                if(data.getInt(KEY_ACTION,ACTION_NONE)==ACTION_REBOOT)
                  OsmpRequest.rebootTerminal(data,data.getLong(KEY_TERMINAL,0));
                else
                  internalRefresh(data);
              }
            }
            while(_sTasks.size()>0);
            _sLoading = false;
            _sContext.sendBroadcast(_sLoadedIntent);
          }
          try
          {
            _sTasks.wait();
          }
          catch(InterruptedException e)
          {
            break;
          }
        }
      }
    }
  }
}
