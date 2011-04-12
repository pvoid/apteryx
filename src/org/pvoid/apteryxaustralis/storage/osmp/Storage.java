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

import java.util.List;

import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.Group;
import org.pvoid.apteryxaustralis.accounts.Terminal;
import org.pvoid.apteryxaustralis.net.TerminalsProcessData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class Storage
{
  public static final String DB_NAME = "apteryx";
  public static final int DB_VERSION = 4;
  /**
   * Описание таблицы аккаунтов
   */
  private static interface Accounts
  {
    static final String TABLE_NAME = "accounts";
//////// Колонки
    static final String COLUMN_ID = "id";
    static final String COLUMN_TITLE = "title";
    static final String COLUMN_LOGIN = "login";
    static final String COLUMN_PASSWORD = "password";
    static final String COLUMN_TERMINAL = "terminal";
//////// Запросы
    static final String CREATE_SQL = "create table " + TABLE_NAME + " (" +
                                     COLUMN_ID + " text primary key," +
                                     COLUMN_TITLE + " text not null," +
                                     COLUMN_LOGIN + " text not null," +
                                     COLUMN_PASSWORD + " text not null," +
                                     COLUMN_TERMINAL + " text not null);";
    static final String ID_CLAUSE = COLUMN_ID+"=?";
  }
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

    static final String CREATE_SQL = "create table " + TABLE_NAME +" ("+
                                     COLUMN_ACCOUNT+" text not null," +
                                     COLUMN_AGENT_NAME+" text not null," +
                                     COLUMN_BALANCE+" text not null," +
                                     COLUMN_OVERDRAFT+" text not null," +
                                     COLUMN_AGENT+" text not null)";

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
  }

  private DatabaseHelper _database;
  private Context _context;
  
  private static class DatabaseHelper extends SQLiteOpenHelper
  {
    public DatabaseHelper(Context context)
    {
      super(context,DB_NAME,null,DB_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db)
    {
      db.execSQL(Accounts.CREATE_SQL);
      db.execSQL(Terminals.CREATE_SQL);
      db.execSQL(Agents.CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
      switch(oldVersion)
      {
        case 2:
          db.execSQL("alter table " + Terminals.TABLE_NAME + " add column " + Terminals.COLUMN_ACCOUNTID + " integer not null default 0");
          db.execSQL("alter table " + Agents.TABLE_NAME + " add column " + Agents.COLUMN_AGENT_NAME + " text not null default ''");
          db.execSQL("alter table " + Agents.TABLE_NAME + " add column " + Agents.COLUMN_BALANCE + " text not null default '0'");
          db.execSQL("alter table " + Agents.TABLE_NAME + " add column " + Agents.COLUMN_OVERDRAFT + " text not null default '0'");
          db.execSQL("drop table balances");
        case 3:
          db.execSQL("alter table " + Terminals.TABLE_NAME + " add column " + Terminals.COLUMN_MS + " integer not null default 0");
      }
    }
  }
  
  private SQLiteDatabase OpenWrite()
  {
    _database = new DatabaseHelper(_context);
    return(_database.getWritableDatabase());
  }
  
  private SQLiteDatabase OpenRead()
  {
    _database = new DatabaseHelper(_context);
    return(_database.getReadableDatabase());
  }
  
  public Storage(Context context)
  {
    _context = context;
  }
  
  public boolean addAccount(long id, String title, String login, String password, String terminal)
  {
    ContentValues values = new ContentValues();
    values.put(Accounts.COLUMN_ID, Long.toString(id));
    values.put(Accounts.COLUMN_TITLE, title);
    values.put(Accounts.COLUMN_LOGIN, login);
    values.put(Accounts.COLUMN_PASSWORD, password);
    values.put(Accounts.COLUMN_TERMINAL, terminal);
    SQLiteDatabase db = OpenWrite();
    boolean result = db.insert(Accounts.TABLE_NAME, null, values)!=-1;
    _database.close();
    return(result);
  }
  
  public void EditAccount(long id,String title, String login, String password, String terminal)
  {
    ContentValues values = new ContentValues();
    values.put(Accounts.COLUMN_TITLE, title);
    values.put(Accounts.COLUMN_LOGIN, login);
    values.put(Accounts.COLUMN_PASSWORD, password);
    values.put(Accounts.COLUMN_TERMINAL, terminal);
    SQLiteDatabase db = OpenWrite();
    db.update(Accounts.TABLE_NAME, values, Accounts.ID_CLAUSE, new String[] {Long.toString(id)});
    _database.close();
  }
  
  public void DeleteAccount(long id)
  {
    SQLiteDatabase db = OpenWrite();
    db.delete(Accounts.TABLE_NAME, Accounts.ID_CLAUSE, new String[] {Long.toString(id)});
    // TODO: Удалить агентов и терминалы
    _database.close();
  }
/**
 * Возвращает все имеющиеся учетные записи
 * @param adapter Куда записать
 */
  public void getAccounts(final List<Account> adapter)
  {
    SQLiteDatabase db = OpenRead();
    Cursor cursor = db.query(true, Accounts.TABLE_NAME, new String[] {Accounts.COLUMN_ID,
                                                                      Accounts.COLUMN_TITLE,
                                                                      Accounts.COLUMN_LOGIN,
                                                                      Accounts.COLUMN_PASSWORD,
                                                                      Accounts.COLUMN_TERMINAL},
                             null, null, null, null, null, null);
    if(cursor!=null)
    {
      if(cursor.moveToFirst())
      {
        do
        {
          adapter.add(new Account(Long.parseLong(cursor.getString(0)),
                                  cursor.getString(1),
                                  cursor.getString(2), 
                                  cursor.getString(3), 
                                  cursor.getString(4)));
        }
        while(cursor.moveToNext());
      }
      cursor.close();
    }
    _database.close();
  }
  
  public void saveTerminals(long accountId, final List<Terminal> terminals)
  {
    ContentValues values = new ContentValues();
    
    SQLiteDatabase db = OpenWrite();
    db.delete(Terminals.TABLE_NAME, Terminals.COLUMN_ACCOUNTID + "=?", new String[] {Long.toString(accountId)});
    for(Terminal terminal : terminals )
    {
      values.put(Terminals.COLUMN_ID, terminal.id());
      values.put(Terminals.COLUMN_ADDRESS, terminal.Address());
      values.put(Terminals.COLUMN_STATE, terminal.State());
      values.put(Terminals.COLUMN_PRINTERSTATE,terminal.printer_state);
      values.put(Terminals.COLUMN_CASHBINSTATE,terminal.cashbin_state);
      values.put(Terminals.COLUMN_LPD,terminal.lpd);
      values.put(Terminals.COLUMN_CASH,terminal.cash);
      values.put(Terminals.COLUMN_LASTACTIVITY,terminal.lastActivity);
      values.put(Terminals.COLUMN_LASTPAYMENT,terminal.lastPayment);
      values.put(Terminals.COLUMN_BONDS,terminal.bondsCount);
      values.put(Terminals.COLUMN_BALANCE,terminal.balance);
      values.put(Terminals.COLUMN_SIGNALLEVEL,terminal.signalLevel);
      values.put(Terminals.COLUMN_SOFTVERSION,terminal.softVersion);
      values.put(Terminals.COLUMN_PRINTERMODEL,terminal.printerModel);
      values.put(Terminals.COLUMN_CASHBINMODEL,terminal.cashbinModel);
      values.put(Terminals.COLUMN_BONDS10,terminal.bonds10count);
      values.put(Terminals.COLUMN_BONDS50,terminal.bonds50count);
      values.put(Terminals.COLUMN_BONDS100,terminal.bonds100count);
      values.put(Terminals.COLUMN_BONDS500,terminal.bonds500count);
      values.put(Terminals.COLUMN_BONDS1000,terminal.bonds1000count);
      values.put(Terminals.COLUMN_BONDS5000,terminal.bonds5000count);
      values.put(Terminals.COLUMN_BONDS10000,terminal.bonds10000count);
      values.put(Terminals.COLUMN_PAYSPERHOUR,terminal.paysPerHour);
      values.put(Terminals.COLUMN_AGENTID,terminal.agentId);
      values.put(Terminals.COLUMN_AGENTNAME,terminal.agentName);
      values.put(Terminals.COLUMN_ACCOUNTID,accountId);
      values.put(Terminals.COLUMN_MS,terminal.ms);
      
      db.insert(Terminals.TABLE_NAME, null, values);
    }
///////
    /*Set<Long> keys = terminals.Accounts();
    values.clear();
    db.delete(Consts.BALANCES_TABLE, null, null);
    for(Long agentId : keys)
    {
      values.put(Consts.COLUMN_AGENTID, agentId);
      values.put(Consts.COLUMN_BALANCE, terminals.Balance(agentId));
      values.put(Consts.COLUMN_OVERDRAFT, terminals.Overdraft(agentId));
      
      db.insert(Consts.BALANCES_TABLE, null, values);
    }*/
    _database.close();
  }
  
  public boolean hasAccounts()
  {
    boolean result = false;
    SQLiteDatabase db = OpenRead();
    Cursor cursor = db.rawQuery("select count(*) from "+Accounts.TABLE_NAME, null);
    if(cursor!=null)
    {
      if(cursor.moveToFirst())
      {
        if(cursor.getInt(0)>0)
          result = true;
      }
      cursor.close();
    }
    _database.close();
    return(result);
  }
  
  public void getTerminals(final long agentId, final List<Terminal> terminals)
  {
    SQLiteDatabase db = OpenRead();
    Cursor cursor = db.query(Terminals.TABLE_NAME, new String[] {Terminals.COLUMN_ID,
                                                                 Terminals.COLUMN_ADDRESS,
                                                                 Terminals.COLUMN_STATE,
                                                                 Terminals.COLUMN_PRINTERSTATE,
                                                                 Terminals.COLUMN_CASHBINSTATE,
                                                                 Terminals.COLUMN_LPD,
                                                                 Terminals.COLUMN_CASH,
                                                                 Terminals.COLUMN_LASTACTIVITY,
                                                                 Terminals.COLUMN_LASTPAYMENT,
                                                                 Terminals.COLUMN_BONDS,
                                                                 Terminals.COLUMN_BALANCE,
                                                                 Terminals.COLUMN_SIGNALLEVEL,
                                                                 Terminals.COLUMN_SOFTVERSION,
                                                                 Terminals.COLUMN_PRINTERMODEL,
                                                                 Terminals.COLUMN_CASHBINMODEL,
                                                                 Terminals.COLUMN_BONDS10,
                                                                 Terminals.COLUMN_BONDS50,
                                                                 Terminals.COLUMN_BONDS100,
                                                                 Terminals.COLUMN_BONDS500,
                                                                 Terminals.COLUMN_BONDS1000,
                                                                 Terminals.COLUMN_BONDS5000,
                                                                 Terminals.COLUMN_BONDS10000,
                                                                 Terminals.COLUMN_PAYSPERHOUR,
                                                                 Terminals.COLUMN_AGENTID,
                                                                 Terminals.COLUMN_AGENTNAME},
                             Terminals.AGENT_ID_CLAUSE,new String[] {Long.toString(agentId)},null,null,null,null);
    if(cursor!=null)
    {
      if(cursor.moveToFirst())
      {
        do
        {
          Terminal terminal = new Terminal(cursor.getLong(0), cursor.getString(1));
          terminal.State(cursor.getInt(2));
          terminal.printer_state = cursor.getString(3);
          terminal.cashbin_state = cursor.getString(4);
          terminal.lpd = cursor.getString(5);
          terminal.cash = cursor.getInt(6);
          terminal.lastActivity = cursor.getString(7);
          terminal.lastPayment = cursor.getString(8);
          terminal.bondsCount = cursor.getInt(9);
          terminal.balance = cursor.getString(10);
          terminal.signalLevel = cursor.getInt(11);
          terminal.softVersion = cursor.getString(12);
          terminal.printerModel = cursor.getString(13);
          terminal.cashbinModel = cursor.getString(14);
          terminal.bonds10count = cursor.getInt(15);
          terminal.bonds50count = cursor.getInt(16);
          terminal.bonds100count = cursor.getInt(17);
          terminal.bonds500count = cursor.getInt(18);
          terminal.bonds1000count = cursor.getInt(19);
          terminal.bonds5000count = cursor.getInt(20);
          terminal.bonds10000count = cursor.getInt(21);
          terminal.paysPerHour = cursor.getString(22);
          terminal.agentId = Long.parseLong(cursor.getString(23));
          terminal.agentName = cursor.getString(24);
          terminals.add(terminal);
        }
        while(cursor.moveToNext());
      }
      cursor.close();      
    }
    _database.close();
  }
  
  public boolean CheckStates(final TerminalsProcessData terminals,List<Terminal> states)
  {
    Boolean result = false;
    SQLiteDatabase db = OpenRead();
    Cursor cursor = db.query(Terminals.TABLE_NAME, new String[] {Terminals.COLUMN_ID, Terminals.COLUMN_STATE},
                             null,null,null,null,null,null);
    if(cursor!=null)
    {
      if(cursor.moveToFirst())
      {
        do
        {
          long terminal_id = cursor.getLong(0);
          int terminal_state = cursor.getInt(1);
          Terminal terminal = terminals.at(terminal_id);
          if(terminal!=null)
          {
            if(terminal.State() != Terminal.STATE_OK && terminal_state==Terminal.STATE_OK)
            {
              result = true;
              states.add(terminal);
            }
          }
        }
        while(cursor.moveToNext());
      }
      cursor.close();
    }
    _database.close();
    return(result);
  }
  
  public boolean saveAgents(long account, List<Group> groups)
  {
    SQLiteDatabase db = OpenWrite();
    ContentValues values = new ContentValues();
    values.put(Agents.COLUMN_ACCOUNT, Long.toString(account));
    
    for(Group group : groups)
    {
      values.put(Agents.COLUMN_AGENT, group.id);
      values.put(Agents.COLUMN_AGENT_NAME, group.name);
      values.put(Agents.COLUMN_BALANCE, group.balance);
      values.put(Agents.COLUMN_OVERDRAFT, group.overdraft);
      db.insert(Agents.TABLE_NAME, null, values);
    }
    _database.close();
    return(true);
  }
  
  public void ClearAgents(long account)
  {
    SQLiteDatabase db = OpenWrite();
    db.delete(Agents.TABLE_NAME, Agents.ACCOUNT_CLAUSE,new String[] {Long.toString(account)});
    _database.close();
  }
  
  public void getAgents(long account, List<Group> groups)
  {
    String clause = null;
    String[] args = null;
//////////
    if(account!=0)
    {
      clause = Agents.ACCOUNT_CLAUSE;
      args = new String[] {Long.toString(account)};
    }

    SQLiteDatabase db = OpenRead();
    Cursor cursor = db.query(Agents.TABLE_NAME,
                             new String[] {Agents.COLUMN_AGENT, Agents.COLUMN_AGENT_NAME, Agents.COLUMN_BALANCE, Agents.COLUMN_OVERDRAFT},
                             clause,
                             args,
                             null, null, null);
    if(cursor!=null)
    {
      if(cursor.moveToFirst())
      {
        do
        {
          Group group = new Group();
          group.id = cursor.getLong(0);
          group.name = cursor.getString(1);
          group.balance = cursor.getDouble(2);
          group.overdraft = cursor.getDouble(3);
          groups.add(group);
        }
        while(cursor.moveToNext());
      }
      cursor.close();
    }
    _database.close();
  }
}
