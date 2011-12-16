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

package org.pvoid.apteryxaustralis.storage.osmp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.TextFormat;
import org.pvoid.apteryxaustralis.types.StatusLine;

import java.util.List;

public class OsmpContentProvider extends ContentProvider
{
  public static final String AUTHORITY = "org.pvoid.apteryxaustralis.storage.osmp";
  private static final int AGENTS_REQUEST = 1;
  private static final int TERMINALS_REQUEST = 2;


  public static interface Agents
  {
    static final String MIMETYPE    = "vnd.org.pvoid.osmp.agent";
    static final Uri    CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/agents");
    static final String TABLE_NAME = "agents";
    static final String COLUMN_ACCOUNT = "account";
    static final String COLUMN_AGENT = "_id";
    static final String COLUMN_AGENT_NAME = "agent_name";
    static final String COLUMN_BALANCE = "agent_balance";
    static final String COLUMN_OVERDRAFT = "agent_overdraft";
    static final String COLUMN_LAST_UPDATE = "last_update";
    static final String COLUMN_STATE = "state";
    static final String COLUMN_SEEN = "seen";
    static final String COLUMN_CASH = "cash";
  }

  public static interface Terminals
  {
    static final String MIMETYPE    = "vnd.org.pvoid.osmp.terminal";
    static final Uri    CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/terminals");
    static final String TABLE_NAME          = "terminals";
    static final String COLUMN_ID           = "_id";
    static final String COLUMN_ADDRESS      = "address";
    static final String COLUMN_STATE        = "state";
    static final String COLUMN_MS           = "ms";
    static final String COLUMN_PRINTERSTATE = "printer_state";
    static final String COLUMN_CASHBINSTATE = "cashbin_state";
    static final String COLUMN_CASH         = "cash";
    static final String COLUMN_LASTACTIVITY = "last_activity";
    static final String COLUMN_LASTPAYMENT  = "last_payment";
    static final String COLUMN_BONDS        = "bonds_count";
    static final String COLUMN_BALANCE      = "balance";
    static final String COLUMN_SIGNALLEVEL  = "signal_level";
    static final String COLUMN_SOFTVERSION  = "soft_version";
    static final String COLUMN_PRINTERMODEL = "printer_model";
    static final String COLUMN_CASHBINMODEL = "cashbin_model";
    static final String COLUMN_BONDS10      = "bonds_10";
    static final String COLUMN_BONDS50      = "bonds_50";
    static final String COLUMN_BONDS100     = "bonds_100";
    static final String COLUMN_BONDS500     = "bonds_500";
    static final String COLUMN_BONDS1000    = "bonds_1000";
    static final String COLUMN_BONDS5000    = "bonds_5000";
    static final String COLUMN_BONDS10000   = "bonds_10000";
    static final String COLUMN_PAYSPERHOUR  = "pays_per_hour";
    static final String COLUMN_AGENTID      = "agent_id";
    static final String COLUMN_AGENTNAME    = "agent_name";
    static final String COLUMN_ACCOUNTID    = "account_id";
    static final String COLUMN_FINAL_STATE  = "final_state";
  }

  protected final static int OSMP_STATE_OK = 0;
  protected final static int OSMP_STATE_WARRNING = 2;
  protected final static int OSMP_STATE_ERROR = 1;

  public static final int STATE_OK = 0;
  public static final int STATE_WARNING = 1;
  public static final int STATE_ERROR = 2;
  public static final int STATE_ERROR_CRITICAL = 3;

  final static int STATE_PRINTER_STACKER_ERROR = 1;// Автомат остановлен из-за ошибок купюроприемника или принтера
  final static int STATE_INTERFACE_ERROR = 2; //Автомат остановлен из-за ошибки в конфигурации интерфейса.
  // Новый интерфейс загружается с сервера
  final static int STATE_UPLOADING_UPDATES = 4; // Автомат загружает с сервера обновление приложения
  final static int STATE_DEVICES_ABSENT = 8; // Автомат остановлен из-за того, что при старте не обнаружено
  // оборудование (купюроприемник или принтер)
  final static int STATE_WATCHDOG_TIMER = 0x10; // Работает сторожевой таймер
  final static int STATE_PAPER_COMING_TO_END = 0x20; // В принтере скоро закончится бумага
  final static int STATE_STACKER_REMOVED = 0x40; // C автомата был снят купюроприемник
  final static int STATE_ESSENTIAL_ELEMENTS_ERROR = 0x80; // Отсутствуют или неверно заполнены один или
  // несколько реквизитов для терминала
  final static int STATE_HARDDRIVE_PROBLEMS = 0x100; //256 Проблемы с жестким диском
  final static int STATE_STOPPED_DUE_BALANCE = 0x200; // Остановлен по сигналу сервера или из-за отсутствия денег на счету агента
  final static int STATE_HARDWARE_OR_SOFTWARE_PROBLEM  = 0x400; // Остановлен из-за проблем с железом или интерфейса
  final static int STATE_HAS_SECOND_MONITOR  = 0x800; // Автомат оснащен вторым монитором.
  final static int STATE_ALTERNATE_NETWORK_USED  = 0x1000; // Автомат использует альтернативную сеть
  final static int STATE_UNAUTHORIZED_SOFTWARE  = 0x2000; // Используется ПО, вызывающее сбои в работе автомата
  final static int STATE_PROXY_SERVER  = 0x4000; // Автомат работает через прокси
  final static int STATE_UPDATING_CONFIGURATION = 0x10000; // Терминал обновляет конфигурацию
  final static int STATE_UPDATING_NUMBERS = 0x20000; // Терминал обновляет номерные емкости.
  final static int STATE_UPDATING_PROVIDERS  = 0x40000; // Терминал обновляет список провайдеров.
  final static int STATE_UPDATING_ADVERT     = 0x80000; // Терминал проверяет и обновляет рекламный плэйлист.
  final static int STATE_UPDATING_FILES = 0x100000; // Терминал проверяет и обновляет файлы.
  final static int STATE_FAIR_FTP_IP = 0x200000; // Подменен IP-адрес FTP сервера
  final static int STATE_ASO_MODIFIED = 0x400000; // Модифицировано приложение АСО.
  final static int STATE_INTERFACE_MODIFIED = 0x800000; // Модифицирован интерфейс
  final static int STATE_ASO_ENABLED = 0x1000000; // Монитор АСО выключен.

  private static final UriMatcher _sUriMather;
  static
  {
    _sUriMather = new UriMatcher(UriMatcher.NO_MATCH);
    _sUriMather.addURI(AUTHORITY,"agents",AGENTS_REQUEST);
    _sUriMather.addURI(AUTHORITY,"terminals",TERMINALS_REQUEST);
  }
  private OsmpContentStorage _mStorage;

  @Override
  public boolean onCreate()
  {
    _mStorage = new OsmpContentStorage(getContext());
    return true;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
  {
    switch(_sUriMather.match(uri))
    {
      case AGENTS_REQUEST:
      {
        final SQLiteDatabase db = _mStorage.getReadableDatabase();

        final StringBuilder sql = new StringBuilder("select ");
        boolean first = true;
////////////
        for(String column : projection)
        {
          if(!first)
            sql.append(",");
          else
            first = false;
          if(Agents.COLUMN_CASH.equals(column))
            sql.append("sum(t.").append(Terminals.COLUMN_CASH).append(")");
          else
            sql.append("a.").append(column);
        }
        sql.append(", a.").append(Agents.COLUMN_AGENT).append(" as ").append(Agents.COLUMN_AGENT);
        sql.append(" from ").append(Agents.TABLE_NAME).append(" a");
        sql.append(" inner join ").append(Terminals.TABLE_NAME).append(" t on a.")
                                    .append(Agents.COLUMN_AGENT).append("=t.").append(Terminals.COLUMN_AGENTID);
        if(selection!=null)
          sql.append(" where ").append(selection.replace(Agents.COLUMN_AGENT,"a."+Agents.COLUMN_AGENT));
        sql.append(" group by a.").append(Agents.COLUMN_AGENT);
        if(sortOrder!=null)
          sql.append(" order by a.").append(sortOrder);

        return db.rawQuery(sql.toString(),selectionArgs);
      }
      case TERMINALS_REQUEST:
      {
        final SQLiteDatabase db = _mStorage.getReadableDatabase();
        return db.query(Terminals.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
      }
    }
    return null;
  }

  @Override
  public String getType(Uri uri)
  {
    switch(_sUriMather.match(uri))
    {
      case AGENTS_REQUEST:
        return Agents.MIMETYPE;
      case TERMINALS_REQUEST:
        return Terminals.MIMETYPE;
    }
    return null;
  }

  @Override
  public Uri insert(Uri uri, ContentValues contentValues)
  {
    switch(_sUriMather.match(uri))
    {
      case AGENTS_REQUEST:
      {
        final SQLiteDatabase db = _mStorage.getWritableDatabase();
        if(db.insert(Agents.TABLE_NAME,null,contentValues)!=-1)
          return uri;
      }
      case TERMINALS_REQUEST:
      {
        final SQLiteDatabase db = _mStorage.getWritableDatabase();
//////////////
        int oldState = 0;
        final Cursor cursor = db.query(Terminals.TABLE_NAME,
                                       new String[] {Terminals.COLUMN_FINAL_STATE},
                                       Terminals.COLUMN_ID+"=?",
                                       new String[] {contentValues.getAsString(Terminals.COLUMN_ID)},
                                       null,null,null);
        try
        {
          if(cursor.moveToFirst())
            oldState = cursor.getInt(0);
        }
        finally
        {
          if(cursor!=null)
            cursor.close();
        }
//////////////
        final int state = getState(contentValues.getAsInteger(Terminals.COLUMN_STATE),
                                                              contentValues.getAsString(Terminals.COLUMN_PRINTERSTATE),
                                                              contentValues.getAsLong(Terminals.COLUMN_LASTACTIVITY));
        final long agentId = contentValues.getAsLong(Terminals.COLUMN_AGENTID);
        contentValues.put(Terminals.COLUMN_FINAL_STATE,state);
        boolean result = db.update(Terminals.TABLE_NAME, contentValues, Terminals.COLUMN_ID + "=?", new String[]{contentValues.getAsString(Terminals.COLUMN_ID)})>0;
        if(!result)
          result = db.insert(Terminals.TABLE_NAME,null,contentValues)!=-1;
//////////////
        if(!result)
          break;
        if(state>oldState)
          updateGroup(db,agentId,state);
        return uri;
      }
    }
    return null;
  }

  private boolean updateGroup(SQLiteDatabase db, long groupId, int state)
  {

    final ContentValues values = new ContentValues();
    values.put(Agents.COLUMN_STATE,state);
    values.put(Agents.COLUMN_SEEN,0);
    boolean result = db.update(Agents.TABLE_NAME,values,
              Agents.COLUMN_AGENT+"=? AND ("+Agents.COLUMN_STATE+"<? OR "+Agents.COLUMN_STATE+"=? AND "+Agents.COLUMN_SEEN+"=1)",
              new String[]{Long.toString(groupId),Integer.toString(state),Integer.toString(state)})>0;

    if(result)
      Log.e("Apteryx","State "+state+" for group " + groupId);

    return result;
  }

  @Override
  public int delete(Uri uri, String s, String[] strings)
  {
    // TODO: Удаление данных
    return 0;
  }

  @Override
  public int update(Uri uri, ContentValues contentValues, String whereClause, String[] selectionArgs)
  {
    switch(_sUriMather.match(uri))
    {
      case AGENTS_REQUEST:
      {
        final SQLiteDatabase db = _mStorage.getWritableDatabase();
        final int result = db.update(Agents.TABLE_NAME,contentValues,whereClause,selectionArgs);
        if(result>0)
          getContext().getContentResolver().notifyChange(Agents.CONTENT_URI,null);
        return result;
      }
      case TERMINALS_REQUEST:
      {
        final SQLiteDatabase db = _mStorage.getWritableDatabase();
        if(db.replace(Terminals.TABLE_NAME,null,contentValues)!=-1)
          return 1;
        return -1;
      }
    }
    return -1;
  }

  public static String getTerminalStatus(Context context, String cashbinState, String printerState, int ms, int state, float cash, long lastPayment, long lastActivity)
    {
  ////////// Ошибки принтера или купюроприемника вперед
      if(!"OK".equals(cashbinState))
        return context.getString(R.string.cachebin) + ": " + cashbinState;

      if(!"OK".equals(printerState))
        return context.getString(R.string.printer) + ": " + printerState;
  ////////// Потом проверим флаги. Сначала ошибки железа
      if((ms & STATE_PRINTER_STACKER_ERROR) !=0)
        return context.getString(R.string.STATE_PRINTER_STACKER_ERROR);
      if((ms & STATE_STACKER_REMOVED) !=0)
        return context.getString(R.string.STATE_STACKER_REMOVED);
      if((ms & STATE_HARDDRIVE_PROBLEMS) !=0)
        return context.getString(R.string.STATE_HARDDRIVE_PROBLEMS);
      if((ms & STATE_DEVICES_ABSENT) !=0)
        return context.getString(R.string.STATE_DEVICES_ABSENT);
      if((ms & STATE_HARDWARE_OR_SOFTWARE_PROBLEM) !=0)
        return context.getString(R.string.STATE_HARDWARE_OR_SOFTWARE_PROBLEM);
  ////////// Потом вероятные угрозы
      if((ms & STATE_ASO_MODIFIED) !=0)
        return context.getString(R.string.STATE_ASO_MODIFIED);
      if((ms & STATE_INTERFACE_MODIFIED) !=0)
        return context.getString(R.string.STATE_INTERFACE_MODIFIED);
      /*if((ms & STATE_FAIR_FTP_IP) !=0)
        return context.getString(R.string.STATE_FAIR_FTP_IP);*/
      if((ms & STATE_UNAUTHORIZED_SOFTWARE) !=0)
        return context.getString(R.string.STATE_UNAUTHORIZED_SOFTWARE);
  ////////// Ошибки настройки
      if((ms & STATE_INTERFACE_ERROR) !=0)
        return context.getString(R.string.STATE_INTERFACE_ERROR);
      if((ms & STATE_STOPPED_DUE_BALANCE) !=0)
        return context.getString(R.string.STATE_STOPPED_DUE_BALANCE);
  ///////// Ну и прочее
      if((ms & STATE_PAPER_COMING_TO_END) !=0)
        return context.getString(R.string.STATE_PAPER_COMING_TO_END);

      StringBuilder status = new StringBuilder();
      switch(state)
      {
        case OSMP_STATE_OK:
          status.append(context.getString(R.string.fullinfo_cash)).append(' ').append(TextFormat.formatMoney(cash, true));
          break;
        case OSMP_STATE_WARRNING:
          status.append(context.getString(R.string.last_payment))
                .append(' ')
                .append(TextFormat.formatDateSmart(context, lastPayment));
          break;
        case OSMP_STATE_ERROR:
          status.append(context.getString(R.string.last_activity))
                .append(' ')
                .append(TextFormat.formatDateSmart(context, lastActivity));
          break;
      }
      return status.toString();
    }

  public static int getState(int state, String printerState, long lastActivity)
    {
      switch(state)
      {
        case OsmpContentProvider.OSMP_STATE_OK:
          return STATE_OK;
        case OSMP_STATE_WARRNING:
          return STATE_WARNING;
        default:
          if("OK".equals(printerState) || System.currentTimeMillis() - lastActivity>60*60*1000)
            return STATE_ERROR_CRITICAL;
          return STATE_ERROR;
      }
    }

  public static void getStatuses(Context context, String cashbinState, String printerState, int ms, List<StatusLine> statuses)
    {
      if(!"OK".equals(printerState))
        statuses.add(new StatusLine(printerState,StatusLine.STATE_ERROR));

      if(!"OK".equals(cashbinState))
        statuses.add(new StatusLine(cashbinState,StatusLine.STATE_ERROR));

      /*if(ms & STATE_PRINTER_STACKER_ERROR != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_PRINTER_STACKER_ERROR)));*/

      if((ms & STATE_INTERFACE_ERROR) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_INTERFACE_ERROR),StatusLine.STATE_ERROR));

      if((ms & STATE_UPLOADING_UPDATES) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_UPLOADING_UPDATES),StatusLine.STATE_OK));

      if((ms & STATE_DEVICES_ABSENT) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_DEVICES_ABSENT),StatusLine.STATE_ERROR));

      if((ms & STATE_WATCHDOG_TIMER) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_WATCHDOG_TIMER),StatusLine.STATE_OK));

      if((ms & STATE_PAPER_COMING_TO_END) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_PAPER_COMING_TO_END),StatusLine.STATE_ERROR));

      if((ms & STATE_STACKER_REMOVED) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_STACKER_REMOVED),StatusLine.STATE_ERROR));

      if((ms & STATE_ESSENTIAL_ELEMENTS_ERROR) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_ESSENTIAL_ELEMENTS_ERROR),StatusLine.STATE_ERROR));

      if((ms & STATE_HARDDRIVE_PROBLEMS) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_HARDDRIVE_PROBLEMS),StatusLine.STATE_ERROR));

      if((ms & STATE_STOPPED_DUE_BALANCE) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_STOPPED_DUE_BALANCE),StatusLine.STATE_ERROR));

      if((ms & STATE_HARDWARE_OR_SOFTWARE_PROBLEM) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_HARDWARE_OR_SOFTWARE_PROBLEM),StatusLine.STATE_ERROR));

      if((ms & STATE_HAS_SECOND_MONITOR) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_HAS_SECOND_MONITOR),StatusLine.STATE_OK));

      if((ms & STATE_ALTERNATE_NETWORK_USED) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_ALTERNATE_NETWORK_USED),StatusLine.STATE_ERROR));

      if((ms & STATE_UNAUTHORIZED_SOFTWARE) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_UNAUTHORIZED_SOFTWARE),StatusLine.STATE_ERROR));

      if((ms & STATE_PROXY_SERVER) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_PROXY_SERVER),StatusLine.STATE_OK));

      if((ms & STATE_UPDATING_CONFIGURATION) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_UPDATING_CONFIGURATION),StatusLine.STATE_OK));

      if((ms & STATE_UPDATING_NUMBERS) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_UPDATING_NUMBERS),StatusLine.STATE_OK));

      if((ms & STATE_UPDATING_PROVIDERS) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_UPDATING_PROVIDERS),StatusLine.STATE_OK));

      if((ms & STATE_UPDATING_ADVERT) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_UPDATING_ADVERT),StatusLine.STATE_OK));

      if((ms & STATE_UPDATING_FILES) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_UPDATING_FILES),StatusLine.STATE_OK));

      if((ms & STATE_FAIR_FTP_IP) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_FAIR_FTP_IP),StatusLine.STATE_ERROR));

      if((ms & STATE_ASO_MODIFIED) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_ASO_MODIFIED),StatusLine.STATE_ERROR));

      if((ms & STATE_INTERFACE_MODIFIED) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_INTERFACE_MODIFIED),StatusLine.STATE_ERROR));

      if((ms & STATE_ASO_ENABLED) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_ASO_ENABLED),StatusLine.STATE_ERROR));
    }
}
