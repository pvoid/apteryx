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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OsmpContentStorage extends SQLiteOpenHelper
{
  public static final String DB_NAME = "osmp_storage";
  public static final int DB_VERSION = 6;
  /**
   * Описание таблицы агентов
   */
  private static interface Agents
  {
    static final String CREATE_SQL = "create table " + OsmpContentProvider.Agents.TABLE_NAME +" ("+
                                     OsmpContentProvider.Agents.COLUMN_AGENT + " INTEGER PRIMARY KEY,"+
                                     OsmpContentProvider.Agents.COLUMN_ACCOUNT+" text not null," +
                                     OsmpContentProvider.Agents.COLUMN_AGENT_NAME+" text not null," +
                                     OsmpContentProvider.Agents.COLUMN_BALANCE+" text not null," +
                                     OsmpContentProvider.Agents.COLUMN_OVERDRAFT+" text not null," +
                                     OsmpContentProvider.Agents.COLUMN_LAST_UPDATE+" integer not null,"+
                                     OsmpContentProvider.Agents.COLUMN_STATE + " integer default 0," +
                                     OsmpContentProvider.Agents.COLUMN_SEEN + " integer default 0);";
    static final String CREATE_INDEX = "create index idx_agent_id on " + OsmpContentProvider.Agents.TABLE_NAME + " (" +
                                        OsmpContentProvider.Agents.COLUMN_AGENT + ")";

    static final String ACTIVE_AGENTS_QUERY = "select a."+OsmpContentProvider.Agents.COLUMN_AGENT+","+
                                                     "a."+OsmpContentProvider.Agents.COLUMN_AGENT_NAME+","+
                                                     "a."+OsmpContentProvider.Agents.COLUMN_LAST_UPDATE+","+
                                                     "a."+OsmpContentProvider.Agents.COLUMN_BALANCE+","+
                                                     "a."+OsmpContentProvider.Agents.COLUMN_OVERDRAFT+
                                              " from "+OsmpContentProvider.Agents.TABLE_NAME+" a inner join "+OsmpContentProvider.Terminals.TABLE_NAME+" t"+
                                              " on a."+OsmpContentProvider.Agents.COLUMN_AGENT+"=t."+OsmpContentProvider.Terminals.COLUMN_AGENTID+
                                              "  group by a."+OsmpContentProvider.Agents.COLUMN_AGENT;
  }
  /**
   * Описание таблицы терминалов
   */
  private static interface Terminals
  {
    static final String CREATE_SQL = "create table "+OsmpContentProvider.Terminals.TABLE_NAME+" ("+
                                     OsmpContentProvider.Terminals.COLUMN_ID +" integer not null primary key,"+
                                     OsmpContentProvider.Terminals.COLUMN_ADDRESS + " text not null,"+
                                     OsmpContentProvider.Terminals.COLUMN_STATE + " integer," +
                                     OsmpContentProvider.Terminals.COLUMN_MS + " integer not null," +
                                     OsmpContentProvider.Terminals.COLUMN_ACCOUNTID + " integer not null,"+
                                     OsmpContentProvider.Terminals.COLUMN_PRINTERSTATE + " text not null,"+
                                     OsmpContentProvider.Terminals.COLUMN_CASHBINSTATE + " text not null," +
                                     OsmpContentProvider.Terminals.COLUMN_CASH + " integer not null,"+
                                     OsmpContentProvider.Terminals.COLUMN_LASTACTIVITY + " text not null,"+
                                     OsmpContentProvider.Terminals.COLUMN_LASTPAYMENT + " text not null,"+
                                     OsmpContentProvider.Terminals.COLUMN_BONDS + " integer not null," +
                                     OsmpContentProvider.Terminals.COLUMN_BALANCE + " text not null,"+
                                     OsmpContentProvider.Terminals.COLUMN_SIGNALLEVEL + " integer not null,"+
                                     OsmpContentProvider.Terminals.COLUMN_SOFTVERSION + " text not null," +
                                     OsmpContentProvider.Terminals.COLUMN_PRINTERMODEL + " text not null,"+
                                     OsmpContentProvider.Terminals.COLUMN_CASHBINMODEL + " text null," +
                                     OsmpContentProvider.Terminals.COLUMN_BONDS10 + " integer not null,"+
                                     OsmpContentProvider.Terminals.COLUMN_BONDS50 + " integer not null,"+
                                     OsmpContentProvider.Terminals.COLUMN_BONDS100 + "  integer not null," +
                                     OsmpContentProvider.Terminals.COLUMN_BONDS500 + " integer not null,"+
                                     OsmpContentProvider.Terminals.COLUMN_BONDS1000 + " integer not null,"+
                                     OsmpContentProvider.Terminals.COLUMN_BONDS5000 + " integer not null," +
                                     OsmpContentProvider.Terminals.COLUMN_BONDS10000 + " integer not null,"+
                                     OsmpContentProvider.Terminals.COLUMN_PAYSPERHOUR + " text not null,"+
                                     OsmpContentProvider.Terminals.COLUMN_AGENTID + " text not null," +
                                     OsmpContentProvider.Terminals.COLUMN_AGENTNAME + " text not null);";
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
    database.execSQL(Agents.CREATE_SQL);
    database.execSQL(Agents.CREATE_INDEX);
    database.execSQL(Terminals.CREATE_SQL);
  }

  @Override
  public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
  {
    // nope
  }
}
