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

package org.pvoid.apteryxaustralis.accounts;

import java.util.List;
import java.util.Set;

import org.pvoid.apteryxaustralis.Consts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Accounts
{
  private static final String CREATE_ACCOUNTS_TABLE = 
      "create table accounts (id text primary key,"
      + "title text not null,login text not null, password text not null,"
      +"terminal text not null);";
  
  private static final String CREATE_TERMINALS_TABLE = 
      "create table terminals (id integer not null primary key asc, address text not null, state integer," +
      "printer_state text not null, cashbin_state text not null," +
      "lpd text not null, cash integer not null," +
      "last_activity text not null, last_payment text not null, bonds_count integer not null," +
      "balance text not null, signal_level integer not null, soft_version text not null," +
      "printer_model text not null, cashbin_model text null," +
      "bonds_10 integer not null,bonds_50 integer not null,bonds_100 integer not null," +
      "bonds_500 integer not null,bonds_1000 integer not null,bonds_5000 integer not null," +
      "bonds_10000 integer not null,pays_per_hour text not null,agent_id text not null," +
      "agent_name text not null);";
  
  private static final String CREATE_BALANCE_TABLE = 
      "create table balances (agent_id text not null primary key, balance real not null, overdraft real not null);";
  
  private static final String CREATE_AGENTS_TABLE = 
      "create table agents (account text not null, agent_id text not null)";
  
  private DatabaseHelper _database;
  private Context _context;
  
  private static class DatabaseHelper extends SQLiteOpenHelper
  {
    public DatabaseHelper(Context context)
    {
      super(context,Consts.DB_NAME,null,Consts.DB_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db)
    {
      db.execSQL(CREATE_ACCOUNTS_TABLE);
      db.execSQL(CREATE_TERMINALS_TABLE);
      db.execSQL(CREATE_BALANCE_TABLE);
      db.execSQL(CREATE_AGENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
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
  
  public Accounts(Context context)
  {
    _context = context;
  }
  
  public boolean AddAccount(long id, String title, String login, String password, String terminal)
  {
    ContentValues values = new ContentValues();
    values.put(Consts.COLUMN_ID, Long.toString(id));
    values.put(Consts.COLUMN_TITLE, title);
    values.put(Consts.COLUMN_LOGIN, login);
    values.put(Consts.COLUMN_PASSWORD, password);
    values.put(Consts.COLUMN_TERMINAL, terminal);    
    SQLiteDatabase db = OpenWrite();
    boolean result = db.insert(Consts.ACCOUNTS_TABLE, null, values)!=-1;
    _database.close();
    return(result);
  }
  
  public void EditAccount(long id,String title, String login, String password, String terminal)
  {
    ContentValues values = new ContentValues();
    values.put(Consts.COLUMN_TITLE, title);
    values.put(Consts.COLUMN_LOGIN, login);
    values.put(Consts.COLUMN_PASSWORD, password);
    values.put(Consts.COLUMN_TERMINAL, terminal);
    SQLiteDatabase db = OpenWrite();
    db.update(Consts.ACCOUNTS_TABLE, values, Consts.COLUMN_ID+'='+id, null);
    _database.close();
  }
  
  public void DeleteAccount(Long id)
  {
    SQLiteDatabase db = OpenWrite();
    db.delete(Consts.ACCOUNTS_TABLE, Consts.COLUMN_ID + "=" + id,null);
    db.delete(Consts.BALANCES_TABLE, Consts.COLUMN_AGENTID + "=" + id, null);
    _database.close();
  }
/**
 * Возвращает все имеющиеся учетные записи
 * @param adapter Куда записать 
 */
  public void GetAccounts(final List<Account> adapter)
  {
    SQLiteDatabase db = OpenRead();
    Cursor cursor = db.query(true, Consts.ACCOUNTS_TABLE, new String[] {Consts.COLUMN_ID,Consts.COLUMN_TITLE,Consts.COLUMN_LOGIN,Consts.COLUMN_PASSWORD,Consts.COLUMN_TERMINAL},
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
                                  Long.parseLong(cursor.getString(4))));
        }
        while(cursor.moveToNext());
      }
      cursor.close();
    }
    _database.close();
  }

  public boolean HasAccounts()
  {
    boolean result = false;
    SQLiteDatabase db = OpenRead();
    Cursor cursor = db.rawQuery("select count(*) from "+Consts.ACCOUNTS_TABLE, null);
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

  public boolean SaveAgents(long account, List<Agent> agents)
  {
    SQLiteDatabase db = OpenWrite();
    ContentValues values = new ContentValues();
    values.put(Consts.COLUMN_ACCOUNT, Long.toString(account));
    
    for(Agent agent : agents)
    {
      values.put(Consts.COLUMN_AGENTID, agent.getId());
      db.insert(Consts.AGENTS_TABLE, null, values);
    }
    _database.close();
    return(true);
  }
  
  public void ClearAgents(long account)
  {
    SQLiteDatabase db = OpenWrite();
    db.delete(Consts.AGENTS_TABLE, Consts.COLUMN_ACCOUNT+"="+account,null);
    _database.close();
  }
  
  public void GetAgents(long account, List<Agent> agents)
  {
    /*
    SQLiteDatabase db = OpenRead();
    Cursor cursor = db.query(Consts.AGENTS_TABLE, new String[] {Consts.COLUMN_AGENTID}, null, null, Consts.COLUMN_ACCOUNT+"="+account, null, null);
    if(cursor!=null)
    {
      if(cursor.moveToFirst())
      {
        do
        {
          Agent agent = new Agent(cursor.getLong(0));
          agents.add(agent);
        }
        while(cursor.moveToNext());
      }
      cursor.close();
    }
    _database.close();
    */
  }
}
