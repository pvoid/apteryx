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

package org.pvoid.apteryxaustralis.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import org.pvoid.apteryxaustralis.OnBootReceiver;
import org.pvoid.apteryxaustralis.types.*;

import java.io.File;
import java.util.Vector;

public class Storage
{
  private static final String DB_NAME = "apx_storage";
  private static final int DB_VERSION = 6;

  private static boolean _sImported = false;
  public static final int INVISIBLE_INTERVAL = 30*60*60*1000;
  //+--------------------------------------------------------------------+
//|                                                                    |
//| Аккаунты                                                           |
//|                                                                    |
//+--------------------------------------------------------------------+
  private interface AccountTable
  {
    static final String TABLE_NAME = "accounts";

    static final String ID = "id";
    static final String TITLE = "title";
    static final String LOGIN = "login";
    static final String PASSWORD = "password";
    static final String TERMINAL = "terminal";

    static final String[] COLUMNS_SHORT = new String[] {ID,TITLE};
    static final String[] COLUMNS_AUTH = new String[] {ID,LOGIN,PASSWORD,TERMINAL};

    static final int COLUMN_ID = 0;
    static final int COLUMN_TITLE = 1;

    static final int COLUMN_LOGIN = 1;
    static final int COLUMN_PASSWORD = 2;
    static final int COLUMN_TERMINAL = 3;
  }

  private static final String ACCOUNT_TABLE = "CREATE TABLE "+AccountTable.TABLE_NAME+" ("+
                                    AccountTable.ID +" INTEGER NOT NULL,"+
                                    AccountTable.TITLE+" TEXT NOT NULL,"+
                                    AccountTable.LOGIN+" TEXT NOT NULL,"+
                                    AccountTable.PASSWORD+" TEXT NOT NULL,"+
                                    AccountTable.TERMINAL+" INTEGER NOT NULL,"+
                                    "UNIQUE (" + AccountTable.ID + ") ON CONFLICT IGNORE);";
  private static final String ACCOUNT_INDEX = "CREATE INDEX idx_accounts_id ON "+
                                    AccountTable.TABLE_NAME+"("+AccountTable.ID +");";
  private static final String ACCOUNT_INDEX_TITLE = "CREATE INDEX idx_accounts_title ON "+
                                    AccountTable.TABLE_NAME+"("+AccountTable.TITLE+");";
//+--------------------------------------------------------------------+
//|                                                                    |
//| Агенты                                                             |
//|                                                                    |
//+--------------------------------------------------------------------+
  public interface AgentsTable
  {
    static final String TABLE_NAME = "agents";

    static final String ID = "id";
    static final String NAME = "name";
    static final String PHONE = "phone";
    static final String ACCOUNT = "account";
    static final String BALANCE = "balance";
    static final String OVERDRAFT = "overdraft";
    static final String UPDATE_DATE = "update_date";

    static final String[] COLUMNS_SHORT = new String[] {ID,NAME,UPDATE_DATE};
  }

  private static final String AGENT_TABLE = "CREATE TABLE "+AgentsTable.TABLE_NAME+" ("+
                                    AgentsTable.ID +" INTEGER PRIMARY KEY,"+
                                    AgentsTable.NAME+" TEXT NOT NULL,"+
                                    AgentsTable.PHONE+" TEXT NOT NULL,"+
                                    AgentsTable.UPDATE_DATE+" INTEGER NOT NULL,"+
                                    AgentsTable.BALANCE+" REAL NOT NULL,"+
                                    AgentsTable.OVERDRAFT+" REAL NOT NULL,"+
                                    AgentsTable.ACCOUNT+" INTEGER NOT NULL);";
  private static final String AGENT_NAME_INDEX = "CREATE INDEX idx_agents_name ON "+
                                    AgentsTable.TABLE_NAME+"("+AgentsTable.NAME+");";
  private static final String AGENT_ACCOUNT_INDEX = "CREATE INDEX idx_agents_account ON "+
                                    AgentsTable.TABLE_NAME+"("+AgentsTable.ACCOUNT+");";
//+--------------------------------------------------------------------+
//|                                                                    |
//| Терминалы                                                          |
//|                                                                    |
//+--------------------------------------------------------------------+
  private interface TerminalsTable
  {
    static final String TABLE_NAME = "terminals";

    static final String ID = "id";
    static final String ADDRESS = "address";
    static final String NAME = "name";
    static final String AGENT = "agent";

    static final String[] COLUMNS_FULL = new String[] {ID,NAME,ADDRESS,AGENT};

    static final int COLUMN_ID = 0;
    static final int COLUMN_NAME = 1;
    static final int COLUMN_ADDRESS = 2;
    static final int COLUMN_AGENT = 3;
  }

  private static final String TERMINAL_TABLE = "CREATE TABLE "+TerminalsTable.TABLE_NAME+" ("+
                                    TerminalsTable.ID +" INTEGER PRIMARY KEY,"+
                                    TerminalsTable.ADDRESS+" TEXT NOT NULL,"+
                                    TerminalsTable.NAME+" TEXT NOT NULL,"+
                                    TerminalsTable.AGENT+" INTEGER NOT NULL);";
  private static final String TERMINAL_NAME_INDEX = "CREATE INDEX idx_terminal_name ON "+
                                    TerminalsTable.TABLE_NAME+"("+TerminalsTable.NAME+");";
  private static final String TERMINAL_AGENT_INDEX = "CREATE INDEX idx_terminal_agent ON "+
                                    TerminalsTable.TABLE_NAME+"("+TerminalsTable.AGENT+");";
//+--------------------------------------------------------------------+
//|                                                                    |
//| Информация о трминалах                                             |
//|                                                                    |
//+--------------------------------------------------------------------+
  private interface StatusesTable
  {
    static final String TABLE_NAME = "statuses";

    static final String ID = "id";
    static final String DATE = "date";
    static final String AGENT = "agent";
    static final String LAST_ACTIVITY = "last_activity";
    static final String PRINTER_ERROR = "printer_error";
    static final String NOTE_ERROR = "note_error";
    static final String SIGNAL_LEVEL = "signal";
    static final String BALANCE = "balance";
    static final String STATUS = "status";
    static final String DOOR_OPEN = "door_open";
    static final String DOOR_ALARM = "door_alarm";
    static final String EVENT = "event";
    static final String CASH = "cash";

    static final String[] COLUMNS_FULL = new String[] {ID,AGENT,LAST_ACTIVITY,PRINTER_ERROR,
                                                       NOTE_ERROR,SIGNAL_LEVEL,BALANCE,STATUS,DOOR_OPEN,DOOR_ALARM,EVENT,DATE,CASH};
  }

  private static final String STATUS_TABLE = "CREATE TABLE "+StatusesTable.TABLE_NAME+" ("+
                                    StatusesTable.ID +" INTEGER PRIMARY KEY,"+
                                    StatusesTable.DATE+" INTEGER NOT NULL,"+
                                    StatusesTable.AGENT+" INTEGER NOT NULL,"+
                                    StatusesTable.CASH+" REAL NOT NULL,"+
                                    StatusesTable.LAST_ACTIVITY+" INTEGER NOT NULL,"+
                                    StatusesTable.PRINTER_ERROR+" TEXT NOT NULL,"+
                                    StatusesTable.NOTE_ERROR+" TEXT NOT NULL,"+
                                    StatusesTable.SIGNAL_LEVEL+" INTEGER NOT NULL,"+
                                    StatusesTable.BALANCE+" REAL NOT NULL,"+
                                    StatusesTable.STATUS+" INTEGER NOT NULL,"+
                                    StatusesTable.DOOR_OPEN+" INTEGER NOT NULL,"+
                                    StatusesTable.DOOR_ALARM+" INTEGER NOT NULL,"+
                                    StatusesTable.EVENT+" INTEGER NOT NULL);";

  private static final String STATUS_INDEX_AGENT = "CREATE INDEX idx_statuses_agent ON "+
                                    StatusesTable.TABLE_NAME+"("+StatusesTable.AGENT+");";
//+--------------------------------------------------------------------+
//|                                                                    |
//| Информация о платежах                                              |
//|                                                                    |
//+--------------------------------------------------------------------+
  private interface PaymentsTable
  {
    static final String TABLE_NAME = "payments";

    static final String ID = "id";
    static final String TERMINAL = "terminal_id";
    static final String STATUS = "status";
    static final String FROM_AMOUNT = "from_amont";
    static final String TO_AMOUNT = "to_amont";
    static final String PROVIDER_ID = "provider_id";
    static final String PROVIDER_NAME = "provider_name";
    static final String DATE_IN_TERMINAL = "terminal_date";
    static final String DATE_IN_PROCESSING = "processing_date";
    static final String UPDATE_DATE = "update_date";
    static final String[] COLUMNS_FULL = {ID,TERMINAL,STATUS,FROM_AMOUNT,TO_AMOUNT,
                                          PROVIDER_ID,PROVIDER_NAME,DATE_IN_TERMINAL,DATE_IN_PROCESSING,UPDATE_DATE};

    static final int COLUMN_ID = 0;
    static final int COLUMN_TERMINAL = 1;
    static final int COLUMN_STATUS = 2;
    static final int COLUMN_FROM_AMOUNT = 3;
    static final int COLUMN_TO_AMOUNT = 4;
    static final int COLUMN_PROVIDER_ID = 5;
    static final int COLUMN_PROVIDER_NAME = 6;
    static final int COLUMN_DATE_IN_TERMINAL = 7;
    static final int COLUMN_DATE_IN_PROCESSING = 8;
    static final int COLUMN_UPDATE_DATE = 9;
  }

  private static final String PAYMENTS_TABLE = "CREATE TABLE "+PaymentsTable.TABLE_NAME+" ("+
                                    PaymentsTable.ID +" INTEGER PRIMARY KEY,"+
                                    PaymentsTable.TERMINAL+" INTEGER NOT NULL,"+
                                    PaymentsTable.UPDATE_DATE+" INTEGER NOT NULL,"+
                                    PaymentsTable.STATUS+" INTEGER NOT NULL,"+
                                    PaymentsTable.FROM_AMOUNT+" TEXT NOT NULL,"+
                                    PaymentsTable.TO_AMOUNT+" TEXT NOT NULL,"+
                                    PaymentsTable.PROVIDER_ID+" INTEGER NOT NULL,"+
                                    PaymentsTable.PROVIDER_NAME+" TEXT NOT NULL,"+
                                    PaymentsTable.DATE_IN_TERMINAL+" INTEGER NOT NULL,"+
                                    PaymentsTable.DATE_IN_PROCESSING+" INTEGER NOT NULL);";

  private static final String PAYMENTS_INDEX_TERMINAL = "CREATE INDEX idx_payments_terminal ON "+
                                    PaymentsTable.TABLE_NAME+"("+PaymentsTable.TERMINAL+");";
//+--------------------------------------------------------------------+
//|                                                                    |
//| Информация о платежах                                              |
//|                                                                    |
//+--------------------------------------------------------------------+
  private interface BondsTable
  {
    static final String TABLE_NAME = "bonds";

    static final String TERMINAL = "terminal_id";
    static final String BOND = "bond";
    static final String COUNT = "count";
  }

  private static final String BONDS_TABLE = "CREATE TABLE "+BondsTable.TABLE_NAME + " ("+
                                            BondsTable.TERMINAL + " INTEGER NOT NULL"+
                                            BondsTable.BOND + "INTEGER NOT NULL";
//+--------------------------------------------------------------------+
//|                                                                    |
//| Полная информация по терминалу                                     |
//|                                                                    |
//+--------------------------------------------------------------------+
  private static interface TerminalInfoQuery
  {
    static final String
        QUERY = "SELECT t."+TerminalsTable.ADDRESS +
                                       ",t."+TerminalsTable.NAME +
                                       ",t."+TerminalsTable.AGENT+

                                       ",s."+StatusesTable.LAST_ACTIVITY+
                                       ",s."+StatusesTable.PRINTER_ERROR+
                                       ",s."+StatusesTable.NOTE_ERROR+
                                       ",s."+StatusesTable.SIGNAL_LEVEL+
                                       ",s."+StatusesTable.BALANCE+
                                       ",s."+StatusesTable.STATUS+
                                       ",s."+StatusesTable.DOOR_OPEN+
                                       ",s."+StatusesTable.DOOR_ALARM+
                                       ",s."+StatusesTable.EVENT+
                                       ",s."+StatusesTable.DATE+
                                       ",s."+StatusesTable.CASH+

                                       ",a."+AgentsTable.NAME+
                                       ",a."+AgentsTable.PHONE+
                                       ",a."+AgentsTable.ACCOUNT+

                                       ",p."+PaymentsTable.DATE_IN_TERMINAL+
                                       ",p."+PaymentsTable.DATE_IN_PROCESSING+

                                " FROM "+TerminalsTable.TABLE_NAME+" t INNER JOIN "+StatusesTable.TABLE_NAME+" s"+
                                    " ON t."+TerminalsTable.ID+"=s."+StatusesTable.ID+
                                " LEFT JOIN "+PaymentsTable.TABLE_NAME+" p "+
                                    "ON p."+PaymentsTable.TERMINAL+"=t."+TerminalsTable.ID+
                                " INNER JOIN "+AgentsTable.TABLE_NAME+
                                    " a ON t."+TerminalsTable.AGENT + "=a." + AgentsTable.ID +


                                " WHERE t."+TerminalsTable.ID+"=?";

    static final int COLUMN_ADDRESS = 0;
    static final int COLUMN_NAME = 1;
    static final int COLUMN_AGENT = 2;
    static final int COLUMN_LAST_ACTIVITY = 3;
    static final int COLUMN_PRINTER_ERROR = 4;
    static final int COLUMN_NOTE_ERROR = 5;
    static final int COLUMN_SIGNAL_LEVEL = 6;
    static final int COLUMN_BALANCE = 7;
    static final int COLUMN_STATUS = 8;
    static final int COLUMN_DOOR_OPEN = 9;
    static final int COLUMN_DOOR_ALARM = 10;
    static final int COLUMN_EVENT = 11;
    static final int COLUMN_DATE = 12;
    static final int COLUMN_CASH = 13;
    static final int COLUMN_AGENT_NAME = 14;
    static final int COLUMN_AGENT_PHONE=15;
    static final int COLUMN_ACCOUNT=16;
    static final int COLUMN_PAYMENT_DATE=17;
    static final int COLUMN_PROCESSING_DATE=18;
  }
//+--------------------------------------------------------------------+
//|                                                                    |
//| Полная информация по терминалу                                     |
//|                                                                    |
//+--------------------------------------------------------------------+
  private static interface TerminalsForAccountQuery
  {
    static final String
        QUERY = "SELECT t."+TerminalsTable.ID +
                      ",t."+TerminalsTable.ADDRESS +
                      ",t."+TerminalsTable.NAME +
                      ",t."+TerminalsTable.AGENT+
                " FROM "+TerminalsTable.TABLE_NAME+" t INNER JOIN "+AgentsTable.TABLE_NAME+
                                  " a ON t."+TerminalsTable.AGENT + "=a." + AgentsTable.ID + " WHERE a."
                                  +AgentsTable.ACCOUNT+"=?";
    static final int COLUMN_ID = 0;
    static final int COLUMN_ADDRESS = 1;
    static final int COLUMN_NAME = 2;
    static final int COLUMN_AGENT = 3;
  }
//+--------------------------------------------------------------------+
//|                                                                    |
//| Информация по активным агентам                                     |
//|                                                                    |
//+--------------------------------------------------------------------+
  private static interface ActiveAgentsQuery
  {
    static final String
        QUERY = "select a."+AgentsTable.ID+","+
                       "a."+AgentsTable.NAME+","+
                       "a."+AgentsTable.UPDATE_DATE+","+
                       "a."+AgentsTable.BALANCE+","+
                       "a."+AgentsTable.OVERDRAFT+
                " from "+AgentsTable.TABLE_NAME+" a inner join "+TerminalsTable.TABLE_NAME+" t"+
                    " on a."+AgentsTable.ID+"=t."+TerminalsTable.AGENT+
                " inner join "+StatusesTable.TABLE_NAME+" s on s."+StatusesTable.ID+"=t."+TerminalsTable.ID+
                " and (CAST(? as INTEGER)-s."+StatusesTable.LAST_ACTIVITY+"<CAST(? as INTEGER)) group by a."+AgentsTable.ID;

    static final String ORDERED_QUERY = QUERY+" order by a."+AgentsTable.NAME;


    static final int COLUMN_ID = 0;
    static final int COLUMN_NAME = 1;
    static final int COLUMN_UPDATE_DATE = 2;
    static final int COLUMN_BALANCE = 3;
    static final int COLUMN_OVERDRAFT = 4;
  }
//+--------------------------------------------------------------------+
//|                                                                    |
//| Сама база данных                                                   |
//|                                                                    |
//+--------------------------------------------------------------------+
  private static class DataBase extends SQLiteOpenHelper
  {
    public DataBase(Context context)
    {
      super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
      database.execSQL(ACCOUNT_TABLE);
      database.execSQL(ACCOUNT_INDEX);
      database.execSQL(ACCOUNT_INDEX_TITLE);
////////
      database.execSQL(AGENT_TABLE);
      database.execSQL(AGENT_NAME_INDEX);
      database.execSQL(AGENT_ACCOUNT_INDEX);
////////
      database.execSQL(TERMINAL_TABLE);
      database.execSQL(TERMINAL_NAME_INDEX);
      database.execSQL(TERMINAL_AGENT_INDEX);
////////
      database.execSQL(STATUS_TABLE);
      database.execSQL(STATUS_INDEX_AGENT);
////////
      database.execSQL(PAYMENTS_TABLE);
      database.execSQL(PAYMENTS_INDEX_TERMINAL);
////////
      importAccount(database);
    }

    private void importAccount(SQLiteDatabase database)
    {
      String path = Environment.getDataDirectory()+"/data/"+OnBootReceiver.class.getPackage().getName()+"/databases/apteryx";
      try
      {
        SQLiteDatabase old_db = SQLiteDatabase.openDatabase(path,
                                                            null,
                                                            SQLiteDatabase.OPEN_READONLY);

        Cursor cursor = old_db.rawQuery("select id,title,login,password,terminal from accounts",null);
        try
        {
          ContentValues values = new ContentValues();
          while(cursor.moveToNext())
          {
            values.put(AccountTable.ID,cursor.getLong(0));
            values.put(AccountTable.TITLE,cursor.getString(1));
            values.put(AccountTable.LOGIN,cursor.getString(2));
            values.put(AccountTable.PASSWORD,cursor.getString(3));
            values.put(AccountTable.TERMINAL,cursor.getLong(4));

            database.insert(AccountTable.TABLE_NAME,null,values);
            values.clear();
          }
        }
        finally
        {
          if(cursor!=null)
            cursor.close();
        }

        old_db.close();
      }
      catch(SQLException e)
      {
        e.printStackTrace();
      }

      File file = new File(path);
      if(file.exists())
        file.delete();

      _sImported = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
      switch(oldVersion)
      {
        case 1:
          db.execSQL("alter table "+AgentsTable.TABLE_NAME +" add column "+AgentsTable.UPDATE_DATE+" INTEGER NOT NULL DEFAULT 0");
        case 2:
          db.execSQL("alter table "+AgentsTable.TABLE_NAME +" add column "+AgentsTable.BALANCE+" REAL NOT NULL DEFAULT 0");
          db.execSQL("alter table "+AgentsTable.TABLE_NAME +" add column "+AgentsTable.OVERDRAFT+" REAL NOT NULL DEFAULT 0");
        case 3:
        case 4:
        case 5:
          db.execSQL("alter table "+StatusesTable.TABLE_NAME+" add column "+StatusesTable.CASH+" REAL NOT NULL DEFAULT 0");
      }
    }
  }
  private static final Object _mDataBaseLock = new Object();
  private static DataBase _mDatabase = null;

  private static SQLiteDatabase read(Context context)
  {
    synchronized(_mDataBaseLock)
    {
      if(_mDatabase==null)
        _mDatabase = new DataBase(context);
    }
    return _mDatabase.getReadableDatabase();
  }

  private static SQLiteDatabase write(Context context)
  {
    synchronized(_mDataBaseLock)
    {
      if(_mDatabase==null)
        _mDatabase = new DataBase(context);
    }
    return _mDatabase.getWritableDatabase();
  }

  public static boolean isImported()
  {
    return _sImported;
  }

  public static Iterable<Account> getAccountsInfo(Context context)
  {
    SQLiteDatabase db = read(context);
    final Cursor cursor = db.query(AccountTable.TABLE_NAME,AccountTable.COLUMNS_SHORT,null,null,null,null,null);
    Vector<Account> accounts = new Vector<Account>();
    try
    {
      if(cursor!=null)
        while(cursor.moveToNext())
          accounts.add(new Account(cursor.getLong(AccountTable.COLUMN_ID),cursor.getString(AccountTable.COLUMN_TITLE)));
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
    return accounts;
  }

  public static Iterable<Account> getAccounts(Context context)
  {
    SQLiteDatabase db = read(context);
    final Cursor cursor = db.query(AccountTable.TABLE_NAME,AccountTable.COLUMNS_AUTH,null,null,null,null,null);
    Vector<Account> accounts = new Vector<Account>();
    try
    {
      if(cursor!=null)
        while(cursor.moveToNext())
          accounts.add(new Account(cursor.getLong(AccountTable.COLUMN_ID),
                         cursor.getString(AccountTable.COLUMN_LOGIN),
                         cursor.getString(AccountTable.COLUMN_PASSWORD),
                         cursor.getLong(AccountTable.COLUMN_TERMINAL)));
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
    return accounts;
  }

  public static Account getAccount(Context context, long accountId)
  {
    SQLiteDatabase db = read(context);
    final Cursor cursor = db.query(AccountTable.TABLE_NAME,AccountTable.COLUMNS_AUTH,
                                   AccountTable.ID+"=?",new String[] {Long.toString(accountId)},null,null,null);
    if(cursor!=null)
    {
      if(cursor.moveToNext())
      {
        Account account = new Account(cursor.getString(AccountTable.COLUMN_LOGIN),
                                      cursor.getString(AccountTable.COLUMN_PASSWORD),
                                      cursor.getLong(AccountTable.COLUMN_TERMINAL));
        cursor.close();
        return account;
      }
      cursor.close();
    }
    return null;
  }

  public static Iterable<Agent> getAgents(Context context, String order)
  {
    SQLiteDatabase db = read(context);
    final Cursor cursor = db.query(AgentsTable.TABLE_NAME,AgentsTable.COLUMNS_SHORT,null,null,null,null,order);
    Vector<Agent> agents = new Vector<Agent>();
    try
    {
      if(cursor!=null)
        while(cursor.moveToNext())
          agents.add(new Agent(cursor.getLong(0),cursor.getString(1),cursor.getLong(2)));
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
    return agents;
  }

  public static Iterable<Agent> getAgents(Context context)
  {
    return getAgents(context,null);
  }

  public static Iterable<Agent> getActiveAgents(Context context)
  {
    SQLiteDatabase db = read(context);
    final Cursor cursor = db.rawQuery(ActiveAgentsQuery.ORDERED_QUERY,
                                      new String[]{Long.toString(System.currentTimeMillis()),
                                                                 Long.toString(INVISIBLE_INTERVAL)});
    Vector<Agent> agents = new Vector<Agent>();
    try
    {
      if(cursor!=null)
        while(cursor.moveToNext())
          agents.add(new Agent(cursor.getLong(0),cursor.getString(1),cursor.getLong(2),cursor.getFloat(3),cursor.getFloat(4)));
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
    return agents;
  }

  public static Agent getAgent(Context context,long agentId)
  {
    SQLiteDatabase db = read(context);
    Agent result = null;
    final Cursor cursor = db.query(AgentsTable.TABLE_NAME,
                                   AgentsTable.COLUMNS_SHORT,
                                   AgentsTable.ID+"=?",
                                   new String[] {Long.toString(agentId)},
                                   null,null,null);
    try
    {
      if(cursor.moveToNext())
        result = new Agent(cursor.getLong(0),cursor.getString(1),cursor.getLong(2));
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
    return result;
  }

  public static Iterable<Terminal> getTerminals(Context context, long agentId)
  {
    String selection = null;
    String[] clause = null;

    if(agentId>-1)
    {
      selection = TerminalsTable.AGENT+"=?";
      clause = new String[] { Long.toString(agentId) };
    }

    SQLiteDatabase db = read(context);
    final Cursor cursor = db.query(TerminalsTable.TABLE_NAME,TerminalsTable.COLUMNS_FULL,selection,clause,null,null,null);
    Vector<Terminal> terminals = new Vector<Terminal>();

    try
    {
      if(cursor!=null)
        while(cursor.moveToNext())
          terminals.add(new Terminal(cursor.getLong(0),cursor.getString(2),cursor.getString(1),cursor.getLong(3)));

    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
////////
    return terminals;
  }

  public static Iterable<Terminal> getTerminals(Context context)
  {
    return getTerminals(context,-1);
  }

  public static Iterable<Terminal> getTerminals(Context context, Account account)
  {
    SQLiteDatabase db = read(context);
    Cursor cursor = db.rawQuery(TerminalsForAccountQuery.QUERY,new String[] {Long.toString(account.getId())});
    Vector<Terminal> terminals = new Vector<Terminal>();
    try
    {
      if(cursor!=null)
        while(cursor.moveToNext())
          terminals.add(new Terminal(cursor.getLong(0),cursor.getString(2),cursor.getString(1),cursor.getLong(3)));
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
////////
    return terminals;
  }

  public static Iterable<TerminalStatus> getStatuses(Context context)
  {
    SQLiteDatabase db = read(context);
    Cursor cursor = db.query(StatusesTable.TABLE_NAME,StatusesTable.COLUMNS_FULL,null,null,null,null,null);
    Vector<TerminalStatus> statuses = new Vector<TerminalStatus>();
    try
    {
      if(cursor!=null)
        while(cursor.moveToNext())
        {
          final TerminalStatus status = new TerminalStatus(cursor.getLong(0));
          status.setAgentId(cursor.getLong(1));
          status.setLastActivityDate(cursor.getLong(2));
          status.setPrinterErrorId(cursor.getString(3));
          status.setNoteErrorId(cursor.getString(4));
          status.setSignalLevel(cursor.getInt(5));
          status.setSimProviderBalance(cursor.getFloat(6));
          status.setMachineStatus(cursor.getInt(7));
          status.setWdtDoorOpenCount(cursor.getShort(8));
          status.setWdtDoorAlarmCount(cursor.getShort(9));
          status.setWdtEvent(cursor.getShort(10));
          status.setRequestDate(cursor.getLong(11));
          status.setCash(cursor.getFloat(12));
          statuses.add(status);
        }
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
////////
    return statuses;
  }

  public static boolean addAccount(Context context, Account account)
  {
    SQLiteDatabase db = write(context);
    try
    {
      ContentValues values = new ContentValues();
      values.put(AccountTable.ID,account.getId());
      values.put(AccountTable.TITLE,account.getTitle());
      values.put(AccountTable.LOGIN,account.getLogin());
      values.put(AccountTable.PASSWORD,account.getPassword());
      values.put(AccountTable.TERMINAL,account.getTerminalId());
      return db.insert(AccountTable.TABLE_NAME,null,values)>0;
    }
    finally
    {
      if(db!=null)
        db.close();
    }
  }

  public static boolean addAgents(Context context, Iterable<Agent> agents, long accountId)
  {
    SQLiteDatabase db = write(context);
    try
    {
      ContentValues values = new ContentValues();
      for(Agent agent : agents)
      {
        values.put(AgentsTable.ID,agent.getId());
        values.put(AgentsTable.NAME,agent.getName());
        String phone = agent.getPhone();
        values.put(AgentsTable.PHONE,phone==null?"":phone);
        long account = agent.getAccount();
        if(account<=0)
          account = accountId;
        values.put(AgentsTable.ACCOUNT,account);
        db.insert(AgentsTable.TABLE_NAME,null,values);
      }
    }
    finally
    {
      if(db!=null)
        db.close();
    }
    return true;
  }

  public static boolean addAgent(Context context, Agent agent, long accountId)
  {
    SQLiteDatabase db = write(context);
    try
    {
      ContentValues values = new ContentValues();
      values.put(AgentsTable.ID,agent.getId());
      values.put(AgentsTable.NAME,agent.getName());
      String phone = agent.getPhone();
      values.put(AgentsTable.PHONE,phone==null?"":phone);
      long account = agent.getAccount();
        if(account<=0)
          account = accountId;
      values.put(AgentsTable.ACCOUNT,account);
      db.insert(AgentsTable.TABLE_NAME,null,values);
    }
    finally
    {
      if(db!=null)
        db.close();
    }
    return true;
  }


  public static boolean addTerminals(Context context, Iterable<Terminal> terminals)
  {
    SQLiteDatabase db = write(context);
    try
    {
      ContentValues values = new ContentValues();
      for(Terminal terminal : terminals)
      {
        values.put(TerminalsTable.ID,terminal.getId());
        values.put(TerminalsTable.NAME,terminal.getDisplayName());
        values.put(TerminalsTable.ADDRESS,terminal.getAddress());
        values.put(TerminalsTable.AGENT,terminal.getAgentId());
        db.replace(TerminalsTable.TABLE_NAME,null,values);
      }
    }
    finally
    {
      if(db!=null)
        db.close();
    }
    return true;
  }

  private static void addStatus(SQLiteDatabase db, TerminalStatus status)
  {
    ContentValues values = new ContentValues();
    values.put(StatusesTable.ID,status.getId());
    values.put(StatusesTable.AGENT,status.getAgentId());
    values.put(StatusesTable.LAST_ACTIVITY,status.getLastActivityDate());
    values.put(StatusesTable.PRINTER_ERROR,status.getPrinterErrorId());
    values.put(StatusesTable.NOTE_ERROR,status.getNoteErrorId());
    values.put(StatusesTable.SIGNAL_LEVEL,status.getSignalLevel());
    values.put(StatusesTable.BALANCE,status.getSimProviderBalance());
    values.put(StatusesTable.STATUS,status.getMachineStatus());
    values.put(StatusesTable.DOOR_OPEN,status.getWdtDoorOpenCount());
    values.put(StatusesTable.DOOR_ALARM,status.getWdtDoorAlarmCount());
    values.put(StatusesTable.EVENT,status.getWdtEvent());
    values.put(StatusesTable.DATE,status.getRequestDate());
    values.put(StatusesTable.CASH,status.getCash());
    db.replace(StatusesTable.TABLE_NAME, null, values);
  }

  public static boolean addStatuses(Context context, Iterable<TerminalStatus> statuses)
  {
    SQLiteDatabase db = write(context);
    try
    {
      for(TerminalStatus status : statuses)
      {
        addStatus(db,status); //TODO: Может запросы вместе объединить?
      }
    }
    finally
    {
      if(db!=null)
        db.close();
    }
    return true;
  }

  public static boolean addStatus(Context context, TerminalStatus status)
  {
    SQLiteDatabase db = write(context);
    try
    {
      addStatus(db,status);
    }
    finally
    {
      if(db!=null)
        db.close();
    }
    return true;
  }

  public static Terminal getTerminal(Context context, long id)
  {
    SQLiteDatabase db = read(context);
    if(db!=null)
    {
      final Cursor cursor = db.query(TerminalsTable.TABLE_NAME,
                                     TerminalsTable.COLUMNS_FULL,
                                     TerminalsTable.ID+"=?",
                                     new String[] {Long.toString(id)},
                                     null,null,null);
      if(cursor!=null)
        try
        {
          if(cursor.moveToNext())
          {
            return new Terminal(cursor.getLong(TerminalsTable.COLUMN_ID),
                                cursor.getString(TerminalsTable.COLUMN_ADDRESS),
                                cursor.getString(TerminalsTable.COLUMN_NAME),
                                cursor.getLong(TerminalsTable.COLUMN_AGENT));
          }
        }
        finally
        {
          cursor.close();
        }
    }
    return null;
  }

  public static boolean getTerminalInfo(Context context, long id, Terminal terminal, TerminalStatus status, Payment payment, Agent agent)
  {
    SQLiteDatabase db = read(context);
    final Cursor cursor = db.rawQuery(TerminalInfoQuery.QUERY,new String[] {Long.toString(id)});
    try
    {
      if(cursor.moveToNext())
      {
//////////// Заполняем терминал
        terminal.setAddress(cursor.getString(TerminalInfoQuery.COLUMN_ADDRESS));
        terminal.setDisplayName(cursor.getString(TerminalInfoQuery.COLUMN_NAME));
        terminal.setAgentId(cursor.getLong(TerminalInfoQuery.COLUMN_AGENT));
//////////// Заполняем статус
        status.setAgentId(cursor.getLong(TerminalInfoQuery.COLUMN_AGENT));
        status.setLastActivityDate(cursor.getLong(TerminalInfoQuery.COLUMN_LAST_ACTIVITY));
        status.setMachineStatus(cursor.getInt(TerminalInfoQuery.COLUMN_STATUS));
        status.setNoteErrorId(cursor.getString(TerminalInfoQuery.COLUMN_NOTE_ERROR));
        status.setPrinterErrorId(cursor.getString(TerminalInfoQuery.COLUMN_PRINTER_ERROR));
        status.setSimProviderBalance(cursor.getFloat(TerminalInfoQuery.COLUMN_BALANCE));
        status.setSignalLevel(cursor.getInt(TerminalInfoQuery.COLUMN_SIGNAL_LEVEL));
        status.setWdtDoorAlarmCount(cursor.getShort(TerminalInfoQuery.COLUMN_DOOR_ALARM));
        status.setWdtDoorOpenCount(cursor.getShort(TerminalInfoQuery.COLUMN_DOOR_OPEN));
        status.setWdtEvent(cursor.getShort(TerminalInfoQuery.COLUMN_EVENT));
        status.setRequestDate(cursor.getLong(TerminalInfoQuery.COLUMN_DATE));
        status.setCash(cursor.getFloat(TerminalInfoQuery.COLUMN_CASH));
//////////// Заполняем агента
        agent.setId(cursor.getLong(TerminalInfoQuery.COLUMN_AGENT));
        agent.setName(cursor.getString(TerminalInfoQuery.COLUMN_AGENT_NAME));
        agent.setPhone(cursor.getString(TerminalInfoQuery.COLUMN_AGENT_PHONE));
        agent.setAccount(cursor.getLong(TerminalInfoQuery.COLUMN_ACCOUNT));
//////////// Заполняем платеж
        if(!cursor.isNull(TerminalInfoQuery.COLUMN_PAYMENT_DATE))
          payment.setDateInTerminal(cursor.getLong(TerminalInfoQuery.COLUMN_PAYMENT_DATE));
        if(!cursor.isNull(TerminalInfoQuery.COLUMN_PROCESSING_DATE))
          payment.setDateInProcessing(cursor.getLong(TerminalInfoQuery.COLUMN_PROCESSING_DATE));
//////////// Все хорошо
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

  public static void deleteAccount(Context context, long accountId)
  {
    SQLiteDatabase db = read(context);
    String account = Long.toString(accountId);
    final Cursor cursor = db.query(AgentsTable.TABLE_NAME,
                                   new String[] {AgentsTable.ID},
                                   AgentsTable.ACCOUNT+"=?",
                                   new String[] {account},
                                   null,null,null);
    StringBuffer agents = new StringBuffer();
    try
    {
      boolean first = true;
      while(cursor.moveToNext())
      {
        if(first)
          first = false;
        else
          agents.append(',');
        agents.append(cursor.getString(0));
      }
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }

    if(agents.length()>0)
    {
      agents.insert(0," in (");
      agents.append(")");
      db = write(context);
      db.delete(TerminalsTable.TABLE_NAME,TerminalsTable.COLUMN_AGENT+agents.toString(),null);
      db.delete(StatusesTable.TABLE_NAME,StatusesTable.AGENT+agents.toString(),null);
      db.delete(AgentsTable.TABLE_NAME,AgentsTable.ID+agents.toString(),null);
      db.delete(AccountTable.TABLE_NAME,AccountTable.ID+"=?",new String[] {account});
      db.close();
    }
  }

  public static void updatePayments(Context context, Iterable<Payment> payments)
  {
    if(payments==null)
      return;

    ContentValues values = new ContentValues();
    SQLiteDatabase db = write(context);
    for(Payment payment : payments)
    {
      values.clear();
      values.put(PaymentsTable.ID,payment.getId());
      values.put(PaymentsTable.TERMINAL,payment.getTerminalId());
      values.put(PaymentsTable.FROM_AMOUNT,payment.getFromAmount());
      values.put(PaymentsTable.TO_AMOUNT,payment.getToAmount());
      values.put(PaymentsTable.PROVIDER_ID,payment.getProviderId());
      values.put(PaymentsTable.PROVIDER_NAME,payment.getProviderName());
      values.put(PaymentsTable.STATUS,payment.getStatus());
      values.put(PaymentsTable.DATE_IN_PROCESSING,payment.getDateInProcessing());
      values.put(PaymentsTable.DATE_IN_TERMINAL,payment.getDateInTerminal());
      values.put(PaymentsTable.UPDATE_DATE,System.currentTimeMillis());
      if(db.update(PaymentsTable.TABLE_NAME,values,PaymentsTable.TERMINAL+"=?",new String[] {Long.toString(payment.getTerminalId())})==0)
      {
        db.insert(PaymentsTable.TABLE_NAME,null,values);
      }
    }
    db.close();
  }

  public static Iterable<Payment> getPayments(Context context)
  {
    SQLiteDatabase db = read(context);
    Cursor cursor = db.query(PaymentsTable.TABLE_NAME,PaymentsTable.COLUMNS_FULL,null,null,null,null,null);
////////
    Vector<Payment> payments = new Vector<Payment>(cursor.getCount());
    try
    {
      if(cursor!=null)
        while(cursor.moveToNext())
        {
          final Payment payment = new Payment(cursor.getLong(PaymentsTable.COLUMN_ID),cursor.getLong(PaymentsTable.COLUMN_TERMINAL));
          payment.setDateInProcessing(cursor.getLong(PaymentsTable.COLUMN_DATE_IN_PROCESSING));
          payment.setDateInTerminal(cursor.getLong(PaymentsTable.COLUMN_DATE_IN_TERMINAL));
          payment.setFromAmount(cursor.getFloat(PaymentsTable.COLUMN_FROM_AMOUNT));
          payment.setToAmount(cursor.getFloat(PaymentsTable.COLUMN_TO_AMOUNT));
          payment.setProviderId(cursor.getLong(PaymentsTable.COLUMN_PROVIDER_ID));
          payment.setProviderName(cursor.getString(PaymentsTable.COLUMN_PROVIDER_NAME));
          payment.setStatus(cursor.getInt(PaymentsTable.COLUMN_STATUS));
          payment.setUpdateDate(cursor.getLong(PaymentsTable.COLUMN_UPDATE_DATE));
          payments.add(payment);
        }
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
    return payments;
  }

  public static void setAccountUpdateDate(Context context, long accountId, long time)
  {
    SQLiteDatabase db = write(context);
    db.execSQL("update "+AgentsTable.TABLE_NAME+" set "+AgentsTable.UPDATE_DATE+"=? where "+AgentsTable.ACCOUNT+"=?",
               new String[] {Long.toString(time),Long.toString(accountId)});
    db.close();
  }

  public static void updateAgentBalance(Context context, Agent agent)
  {

    SQLiteDatabase db = write(context);
    ContentValues values = new ContentValues();
    values.put(AgentsTable.BALANCE,agent.getBalance());
    values.put(AgentsTable.OVERDRAFT,agent.getOverdraft());
    try
    {
      db.update(AgentsTable.TABLE_NAME,values,AgentsTable.ID+"=?",new String[] {Long.toString(agent.getId())});
    }
    catch(SQLiteException e)
    {
      e.printStackTrace();
    }
    db.close();
  }
}