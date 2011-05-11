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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import android.util.Log;
import org.pvoid.apteryxaustralis.types.Account;
import org.pvoid.apteryxaustralis.types.Group;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class Storage
{
  public static final String DB_NAME = "apteryx";
  public static final int DB_VERSION = 6;

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

  private static String ACCOUNT_FROM_AGENT_QUERY = "select c."+Accounts.COLUMN_ID + ",c." +
                                                               Accounts.COLUMN_TITLE + ",c." +
                                                               Accounts.COLUMN_LOGIN + ",c." +
                                                               Accounts.COLUMN_PASSWORD + ",c." +
                                                               Accounts.COLUMN_TERMINAL +
                                                   " from " + Accounts.TABLE_NAME + " c inner join " + Agents.TABLE_NAME+
                                                   " a on c." + Accounts.COLUMN_ID + "=a." + Agents.COLUMN_ACCOUNT +
                                                   " where a."+Agents.COLUMN_AGENT+"=?";

  private DatabaseHelper _mDatabase;
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
      Log.d(Storage.class.getSimpleName(),"Old db version: " + oldVersion + " new db version: " + newVersion);
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
        case 4:
          db.execSQL("alter table " + Agents.TABLE_NAME + " add column " + Agents.COLUMN_LAST_UPDATE + " integer not null default '0'");
        case 5:
          SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
          TimeZone timezone = TimeZone.getTimeZone("Europe/Moscow");
          dateFormat.setTimeZone(timezone);
          Cursor cursor = db.query(Terminals.TABLE_NAME,
                                   new String[]{Terminals.COLUMN_ID, Terminals.COLUMN_LASTACTIVITY, Terminals.COLUMN_LASTPAYMENT},
                                   null,null,null,null,null);
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
          for(ContentValues value : values)
          {
            db.update(Terminals.TABLE_NAME,value,Terminals.COLUMN_ID+"=?",new String[] {value.getAsString(Terminals.COLUMN_ID)});
          }
      }
    }
  }
  
  private synchronized  SQLiteDatabase OpenWrite()
  {
    if(_mDatabase==null)
      _mDatabase = new DatabaseHelper(_context);
    return(_mDatabase.getWritableDatabase());
  }
  
  private synchronized SQLiteDatabase OpenRead()
  {
    if(_mDatabase==null)
      _mDatabase = new DatabaseHelper(_context);
    return(_mDatabase.getReadableDatabase());
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
    try
    {
      boolean result = db.insert(Accounts.TABLE_NAME, null, values)!=-1;
      return(result);
    }
    finally
    {
      db.close();
    }
  }

  public Account getAccount(long id)
  {
    SQLiteDatabase db = OpenRead();
    try
    {
      Cursor cursor = db.query(Accounts.TABLE_NAME, new String[] {Accounts.COLUMN_ID,
                                                                        Accounts.COLUMN_TITLE,
                                                                        Accounts.COLUMN_LOGIN,
                                                                        Accounts.COLUMN_PASSWORD,
                                                                        Accounts.COLUMN_TERMINAL},
                               Accounts.COLUMN_ID+"=?", new String[] {Long.toString(id)}, null, null, null, null);
      if(cursor!=null)
      {
        try
        {
          if(cursor.moveToFirst())
          {
            return new Account(Long.parseLong(cursor.getString(0)),
                               cursor.getString(1),
                               cursor.getString(2),
                               cursor.getString(3),
                               cursor.getString(4));
          }
        }
        finally
        {
          cursor.close();
        }
      }
    }
    finally
    {
      db.close();
    }
    return null;
  }

  public void EditAccount(long id,String title, String login, String password, String terminal)
  {
    ContentValues values = new ContentValues();
    values.put(Accounts.COLUMN_TITLE, title);
    values.put(Accounts.COLUMN_LOGIN, login);
    values.put(Accounts.COLUMN_PASSWORD, password);
    values.put(Accounts.COLUMN_TERMINAL, terminal);
    SQLiteDatabase db = OpenWrite();
    try
    {
      db.update(Accounts.TABLE_NAME, values, Accounts.ID_CLAUSE, new String[] {Long.toString(id)});
    }
    finally
    {
      db.close();
    }
  }
  
  public void DeleteAccount(long id)
  {
    SQLiteDatabase db = OpenWrite();
    try
    {
      db.delete(Accounts.TABLE_NAME, Accounts.ID_CLAUSE, new String[] {Long.toString(id)});
      // TODO: Удалить агентов и терминалы
    }
    finally
    {
      db.close();
    }
  }
/**
 * Возвращает все имеющиеся учетные записи
 * @param adapter Куда записать
 */
  public void getAccounts(final List<Account> adapter)
  {
    SQLiteDatabase db = OpenRead();
    try
    {
      Cursor cursor = db.query(Accounts.TABLE_NAME, new String[] {Accounts.COLUMN_ID,
                                                                        Accounts.COLUMN_TITLE,
                                                                        Accounts.COLUMN_LOGIN,
                                                                        Accounts.COLUMN_PASSWORD,
                                                                        Accounts.COLUMN_TERMINAL},
                               null, null, null, null, null, null);
      if(cursor!=null)
        try
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
        }
        finally
        {
          cursor.close();
        }
    }
    finally
    {
      db.close();
    }
  }

  public Account getAccountFromAgent(long agentId)
  {
    Account result = null;
    SQLiteDatabase db = OpenRead();
    try
    {
      Cursor cursor = db.rawQuery(ACCOUNT_FROM_AGENT_QUERY,new String[] {Long.toString(agentId)});
      if(cursor!=null)
        try
        {
          if(cursor.moveToFirst())
          {
            result = new Account(cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
          }
        }
        finally
        {
          cursor.close();
        }
      return result;
    }
    finally
    {
      db.close();
    }
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
      values.put(Terminals.COLUMN_LPD,0);
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
      
      db.replace(Terminals.TABLE_NAME, null, values);
    }
///////
    db.close();
  }
  
  public boolean hasAccounts()
  {
    boolean result = false;
    SQLiteDatabase db = OpenRead();
    try
    {
      Cursor cursor = db.rawQuery("select count(*) from "+Accounts.TABLE_NAME, null);
      if(cursor!=null)
        try
        {
          if(cursor.moveToFirst())
          {
            if(cursor.getInt(0)>0)
              result = true;
          }
        }
        finally
        {
          cursor.close();
        }
    }
    finally
    {
      db.close();
    }
    return(result);
  }
  
  public void getTerminals(final long agentId, final List<Terminal> terminals)
  {
    String clause = null;
    String[] clauseArgs = null;

    if(agentId!=0)
    {
      clause = Terminals.AGENT_ID_CLAUSE;
      clauseArgs = new String[] {Long.toString(agentId)};
    }

    SQLiteDatabase db = OpenRead();
    try
    {
      Cursor cursor = db.query(Terminals.TABLE_NAME, new String[] {Terminals.COLUMN_ID,
                                                                   Terminals.COLUMN_ADDRESS,
                                                                   Terminals.COLUMN_STATE,
                                                                   Terminals.COLUMN_PRINTERSTATE,
                                                                   Terminals.COLUMN_CASHBINSTATE,
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
                                                                   Terminals.COLUMN_AGENTNAME,
                                                                   Terminals.COLUMN_MS},
                               clause,clauseArgs,null,null,null,null);
      if(cursor!=null)
      {
        try
        {
          if(cursor.moveToFirst())
            do
            {
              Terminal terminal = new Terminal(cursor.getLong(0), cursor.getString(1));
              terminal.State(cursor.getInt(2));
              terminal.printer_state = cursor.getString(3);
              terminal.cashbin_state = cursor.getString(4);
              terminal.cash = cursor.getInt(5);
              terminal.lastActivity = cursor.getLong(6);
              terminal.lastPayment = cursor.getLong(7);
              terminal.bondsCount = cursor.getInt(8);
              terminal.balance = cursor.getString(9);
              terminal.signalLevel = cursor.getInt(10);
              terminal.softVersion = cursor.getString(11);
              terminal.printerModel = cursor.getString(12);
              terminal.cashbinModel = cursor.getString(13);
              terminal.bonds10count = cursor.getInt(14);
              terminal.bonds50count = cursor.getInt(15);
              terminal.bonds100count = cursor.getInt(16);
              terminal.bonds500count = cursor.getInt(17);
              terminal.bonds1000count = cursor.getInt(18);
              terminal.bonds5000count = cursor.getInt(19);
              terminal.bonds10000count = cursor.getInt(20);
              terminal.paysPerHour = cursor.getString(21);
              terminal.agentId = Long.parseLong(cursor.getString(22));
              terminal.agentName = cursor.getString(23);
              terminal.ms = cursor.getInt(24);
              terminals.add(terminal);
            }
            while(cursor.moveToNext());
        }
        finally
        {
          cursor.close();
        }
      }
    }
    finally
    {
      db.close();
    }
  }

  public boolean saveAgents(long account, List<Group> groups)
  {
    SQLiteDatabase db = OpenWrite();
    try
    {
      ContentValues values = new ContentValues();
      values.put(Agents.COLUMN_ACCOUNT, Long.toString(account));

      for(Group group : groups)
      {
        values.put(Agents.COLUMN_AGENT, group.id);
        values.put(Agents.COLUMN_AGENT_NAME, group.name);
        values.put(Agents.COLUMN_BALANCE, group.balance);
        values.put(Agents.COLUMN_OVERDRAFT, group.overdraft);
        values.put(Agents.COLUMN_LAST_UPDATE,System.currentTimeMillis());
        if(db.update(Agents.TABLE_NAME, values, Agents.COLUMN_AGENT+"=?",new String[] {Long.toString(group.id)})<1)
          db.insert(Agents.TABLE_NAME, null, values);
      }
    }
    finally
    {
      db.close();
    }
    return(true);
  }
  
  public void ClearAgents(long account)
  {
    SQLiteDatabase db = OpenWrite();
    try
    {
      db.delete(Agents.TABLE_NAME, Agents.ACCOUNT_CLAUSE,new String[] {Long.toString(account)});
    }
    finally
    {
      db.close();
    }
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
    try
    {
      Cursor cursor = db.query(Agents.TABLE_NAME,
                               new String[] {Agents.COLUMN_AGENT, Agents.COLUMN_AGENT_NAME, Agents.COLUMN_BALANCE, Agents.COLUMN_OVERDRAFT, Agents.COLUMN_LAST_UPDATE},
                               clause,
                               args,
                               null, null, null);
      if(cursor!=null)
        try
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
              group.lastUpdate = cursor.getLong(4);
              groups.add(group);
            }
            while(cursor.moveToNext());
          }
        }
        finally
        {
          cursor.close();
        }
    }
    finally
    {
      db.close();
    }
  }

  public void getAgentsActive(List<Group> groups)
  {
    SQLiteDatabase db = OpenRead();
    try
    {
      Cursor cursor = db.rawQuery(Agents.ACTIVE_AGENTS_QUERY,null);
      if(cursor!=null)
        try
        {
          if(cursor.moveToFirst())
          {
            do
            {
              Group group = new Group();
              group.id = cursor.getLong(0);
              group.name = cursor.getString(1);
              group.lastUpdate = cursor.getLong(2);
              group.balance = cursor.getDouble(3);
              group.overdraft = cursor.getDouble(4);
              groups.add(group);
            }
            while(cursor.moveToNext());
          }
        }
        finally
        {
          cursor.close();
        }
    }
    finally
    {
      db.close();
    }
  }

  public Terminal getTerminal(long id)
  {
    Terminal terminal = null;
    SQLiteDatabase db = OpenRead();
    try
    {
      Cursor cursor = db.query(Terminals.TABLE_NAME, new String[] {Terminals.COLUMN_ID,
                                                                   Terminals.COLUMN_ADDRESS,
                                                                   Terminals.COLUMN_STATE,
                                                                   Terminals.COLUMN_PRINTERSTATE,
                                                                   Terminals.COLUMN_CASHBINSTATE,
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
                                                                   Terminals.COLUMN_AGENTNAME,
                                                                   Terminals.COLUMN_MS},
                               Terminals.TERMINAL_ID_CLAUSE,new String[] {Long.toString(id)},null,null,null,null);
      if(cursor!=null)
        try
        {
          if(cursor.moveToFirst())
          {
            terminal = new Terminal(cursor.getLong(0), cursor.getString(1));
            terminal.State(cursor.getInt(2));
            terminal.printer_state = cursor.getString(3);
            terminal.cashbin_state = cursor.getString(4);
            terminal.cash = cursor.getInt(5);
            terminal.lastActivity = cursor.getLong(6);
            terminal.lastPayment = cursor.getLong(7);
            terminal.bondsCount = cursor.getInt(8);
            terminal.balance = cursor.getString(9);
            terminal.signalLevel = cursor.getInt(10);
            terminal.softVersion = cursor.getString(11);
            terminal.printerModel = cursor.getString(12);
            terminal.cashbinModel = cursor.getString(13);
            terminal.bonds10count = cursor.getInt(14);
            terminal.bonds50count = cursor.getInt(15);
            terminal.bonds100count = cursor.getInt(16);
            terminal.bonds500count = cursor.getInt(17);
            terminal.bonds1000count = cursor.getInt(18);
            terminal.bonds5000count = cursor.getInt(19);
            terminal.bonds10000count = cursor.getInt(20);
            terminal.paysPerHour = cursor.getString(21);
            terminal.agentId = Long.parseLong(cursor.getString(22));
            terminal.agentName = cursor.getString(23);
            terminal.ms = cursor.getInt(24);
          }
        }
        finally
        {
          cursor.close();
        }
    }
    finally
    {
      db.close();
    }
    return terminal;
  }
}
