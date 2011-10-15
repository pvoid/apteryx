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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class OsmpContentStorage extends SQLiteOpenHelper
{
  public static final String DB_NAME = "apteryx";
  public static final int DB_VERSION = 6;
  /**
   * Описание таблицы агентов
   */
  private static interface Agents
  {
    static final String TABLE_NAME = "agents";

    static final String COLUMN_ACCOUNT = "account";
    static final String COLUMN_AGENT = "agent_id";
    static final String COLUMN_AGENT_NAME = "agent_name";
    static final String COLUMN_BALANCE = "agent_balance";
    static final String COLUMN_OVERDRAFT = "agent_overdraft";
    static final String COLUMN_LAST_UPDATE = "last_update";

    static final String CREATE_SQL = "create table " + TABLE_NAME +" ("+
                                     COLUMN_ACCOUNT+" text not null," +
                                     COLUMN_AGENT_NAME+" text not null," +
                                     COLUMN_BALANCE+" text not null," +
                                     COLUMN_OVERDRAFT+" text not null," +
                                     COLUMN_LAST_UPDATE+" integer not null,"+
                                     COLUMN_AGENT+" text not null primary key)";

    static final String ACTIVE_AGENTS_QUERY = "select a."+COLUMN_AGENT+","+
                                                     "a."+COLUMN_AGENT_NAME+","+
                                                     "a."+COLUMN_LAST_UPDATE+","+
                                                     "a."+COLUMN_BALANCE+","+
                                                     "a."+COLUMN_OVERDRAFT+
                                              " from "+TABLE_NAME+" a inner join "+Terminals.TABLE_NAME+" t"+
                                              " on a."+COLUMN_AGENT+"=t."+Terminals.COLUMN_AGENTID+
                                              "  group by a."+COLUMN_AGENT;

    static final String ACCOUNT_CLAUSE = COLUMN_ACCOUNT + "=?";
  }
  /**
   * Описание таблицы терминалов
   */
  private static interface Terminals
  {
    static final String TABLE_NAME = "terminals";
//////// Колонки
    static final String COLUMN_ID = "id";
    static final String COLUMN_ADDRESS = "address";
    static final String COLUMN_STATE = "state";
    static final String COLUMN_MS = "ms";
    static final String COLUMN_PRINTERSTATE = "printer_state";
    static final String COLUMN_CASHBINSTATE = "cashbin_state";
    static final String COLUMN_LPD = "lpd";
    static final String COLUMN_CASH = "cash";
    static final String COLUMN_LASTACTIVITY = "last_activity";
    static final String COLUMN_LASTPAYMENT = "last_payment";
    static final String COLUMN_BONDS = "bonds_count";
    static final String COLUMN_BALANCE = "balance";
    static final String COLUMN_SIGNALLEVEL = "signal_level";
    static final String COLUMN_SOFTVERSION = "soft_version";
    static final String COLUMN_PRINTERMODEL = "printer_model";
    static final String COLUMN_CASHBINMODEL = "cashbin_model";
    static final String COLUMN_BONDS10 = "bonds_10";
    static final String COLUMN_BONDS50 = "bonds_50";
    static final String COLUMN_BONDS100 = "bonds_100";
    static final String COLUMN_BONDS500 = "bonds_500";
    static final String COLUMN_BONDS1000 = "bonds_1000";
    static final String COLUMN_BONDS5000 = "bonds_5000";
    static final String COLUMN_BONDS10000 = "bonds_10000";
    static final String COLUMN_PAYSPERHOUR = "pays_per_hour";
    static final String COLUMN_AGENTID = "agent_id";
    static final String COLUMN_AGENTNAME = "agent_name";
    static final String COLUMN_ACCOUNTID = "account_id";
//////// Запросы
    static final String CREATE_SQL = "create table "+TABLE_NAME+" ("+
                                     COLUMN_ID +" integer not null primary key asc,"+
                                     COLUMN_ADDRESS + " text not null,"+
                                     COLUMN_STATE + " integer," +
                                     COLUMN_MS + " integer not null," +
                                     COLUMN_ACCOUNTID + " integer not null,"+
                                     COLUMN_PRINTERSTATE + " text not null,"+
                                     COLUMN_CASHBINSTATE + " text not null," +
                                     COLUMN_LPD + " text not null,"+
                                     COLUMN_CASH + " integer not null,"+
                                     COLUMN_LASTACTIVITY + " text not null,"+
                                     COLUMN_LASTPAYMENT + " text not null,"+
                                     COLUMN_BONDS + " integer not null," +
                                     COLUMN_BALANCE + " text not null,"+
                                     COLUMN_SIGNALLEVEL + " integer not null,"+
                                     COLUMN_SOFTVERSION + " text not null," +
                                     COLUMN_PRINTERMODEL + " text not null,"+
                                     COLUMN_CASHBINMODEL + " text null," +
                                     COLUMN_BONDS10 + " integer not null,"+
                                     COLUMN_BONDS50 + " integer not null,"+
                                     COLUMN_BONDS100 + "  integer not null," +
                                     COLUMN_BONDS500 + " integer not null,"+
                                     COLUMN_BONDS1000 + " integer not null,"+
                                     COLUMN_BONDS5000 + " integer not null," +
                                     COLUMN_BONDS10000 + " integer not null,"+
                                     COLUMN_PAYSPERHOUR + " text not null,"+
                                     COLUMN_AGENTID + " text not null," +
                                     COLUMN_AGENTNAME + " text not null);";
    static final String AGENT_ID_CLAUSE = COLUMN_AGENTID+"=?";
    static final String TERMINAL_ID_CLAUSE = COLUMN_ID+"=?";
  }

/*  private static String ACCOUNT_FROM_AGENT_QUERY = "select c."+OsmpContentProvider.Accounts.COLUMN_ID + ",c." +
                                                               OsmpContentProvider.Accounts.COLUMN_TITLE + ",c." +
                                                               OsmpContentProvider.Accounts.COLUMN_LOGIN + ",c." +
                                                               OsmpContentProvider.Accounts.COLUMN_PASSWORD + ",c." +
                                                               OsmpContentProvider.Accounts.COLUMN_TERMINAL +
                                                   " from " + OsmpContentProvider.Accounts.TABLE_NAME + " c inner join " + Agents.TABLE_NAME+
                                                   " a on c." + OsmpContentProvider.Accounts.COLUMN_ID + "=a." + Agents.COLUMN_ACCOUNT +
                                                   " where a."+Agents.COLUMN_AGENT+"=?";*/

  public OsmpContentStorage(Context context)
  {
    super(context,DB_NAME,null,DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database)
  {
    database.execSQL(Terminals.CREATE_SQL);
    database.execSQL(Agents.CREATE_SQL);
  }

  @Override
  public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
  {
    switch(oldVersion)
    {
      case 2:
        database.execSQL("alter table " + Terminals.TABLE_NAME + " add column " + Terminals.COLUMN_ACCOUNTID + " integer not null default 0");
        database.execSQL("alter table " + Agents.TABLE_NAME + " add column " + Agents.COLUMN_AGENT_NAME + " text not null default ''");
        database.execSQL("alter table " + Agents.TABLE_NAME + " add column " + Agents.COLUMN_BALANCE + " text not null default '0'");
        database.execSQL("alter table " + Agents.TABLE_NAME + " add column " + Agents.COLUMN_OVERDRAFT + " text not null default '0'");
        database.execSQL("drop table balances");
      case 3:
        database.execSQL("alter table " + Terminals.TABLE_NAME + " add column " + Terminals.COLUMN_MS + " integer not null default 0");
      case 4:
        database.execSQL("alter table " + Agents.TABLE_NAME + " add column " + Agents.COLUMN_LAST_UPDATE + " integer not null default '0'");
      case 5:
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        TimeZone timezone = TimeZone.getTimeZone("Europe/Moscow");
        dateFormat.setTimeZone(timezone);
        Cursor cursor = database.query(Terminals.TABLE_NAME,
                                 new String[]{Terminals.COLUMN_ID, Terminals.COLUMN_LASTACTIVITY, Terminals.COLUMN_LASTPAYMENT},
                                 null,null,null,null,null);
//////////////
        ArrayList<ContentValues> values = new ArrayList<ContentValues>();
        if(cursor!=null)
          try
          {
            while(cursor.moveToNext())
            {
              ContentValues value = new ContentValues();
              value.put(Terminals.COLUMN_ID,cursor.getLong(0));
              try
              {
                value.put(Terminals.COLUMN_LASTACTIVITY,dateFormat.parse(cursor.getString(1)).getTime());
                value.put(Terminals.COLUMN_LASTPAYMENT,dateFormat.parse(cursor.getString(2)).getTime());
              }
              catch(ParseException e)
              {
                e.printStackTrace();
                value.put(Terminals.COLUMN_LASTACTIVITY,0);
                value.put(Terminals.COLUMN_LASTPAYMENT,0);
              }
              values.add(value);
            }
          }
          finally
          {
            cursor.close();
          }
//////////////
        for(ContentValues value : values)
        {
          database.update(Terminals.TABLE_NAME,value,Terminals.COLUMN_ID+"=?",new String[] {value.getAsString(Terminals.COLUMN_ID)});
        }
    }
  }
}
