package org.pvoid.apteryxaustralis.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.Agent;
import org.pvoid.apteryxaustralis.accounts.Terminal;
import org.pvoid.apteryxaustralis.accounts.TerminalStatus;

import java.util.Iterator;

public class Storage
{
  private static final String DB_NAME = "apx_storage";
  private static final int DB_VERSION = 1;
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
    static final String[] COLUMNS_AUTH = new String[] {LOGIN,PASSWORD,TERMINAL};

    static final int COLUMN_ID = 0;
    static final int COLUMN_TITLE = 1;
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
  private interface AgentsTable
  {
    static final String TABLE_NAME = "agents";

    static final String _ID = "id";
    static final String NAME = "name";
    static final String PHONE = "phone";
    static final String ACCOUNT = "account";
  }

  private static final String AGENT_TABLE = "CREATE TABLE "+AgentsTable.TABLE_NAME+" ("+
                                    AgentsTable._ID+" INTEGER PRIMARY KEY,"+
                                    AgentsTable.NAME+" TEXT NOT NULL,"+
                                    AgentsTable.PHONE+" TEXT NOT NULL,"+
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

    static final String _ID = "id";
    static final String ADDRESS = "address";
    static final String NAME = "name";
    static final String AGENT = "agent";
  }

  private static final String TERMINAL_TABLE = "CREATE TABLE "+TerminalsTable.TABLE_NAME+" ("+
                                    TerminalsTable._ID+" INTEGER PRIMARY KEY,"+
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
  }

  private static final String STATUS_TABLE = "CREATE TABLE "+StatusesTable.TABLE_NAME+" ("+
                                    StatusesTable.ID +" INTEGER PRIMARY KEY,"+
                                    StatusesTable.AGENT+" INTEGER NOT NULL,"+
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
      // nope
    }
  }

  private static class AccountsEnumarator implements Iterable<Account>
  {
    private final Cursor _mCursor;
    private final SQLiteDatabase _mDB;

    public AccountsEnumarator(Cursor cursor,SQLiteDatabase db)
    {
      _mCursor = cursor;
      _mDB = db;
    }

    @Override
    public Iterator<Account> iterator()
    {
      return new Iterator<Account>()
      {
        @Override
        public boolean hasNext()
        {
          if(_mCursor.isClosed())
            return false;

          if(_mCursor.isLast())
          {
            _mCursor.close();
            _mDB.close();
            return false;
          }
          return true;
        }

        @Override
        public Account next()
        {
          if(!_mCursor.moveToNext())
          {
            throw new RuntimeException("Don't call next after hasNext return false!");
          }

          return new Account(_mCursor.getLong(AccountTable.COLUMN_ID),_mCursor.getString(AccountTable.COLUMN_TITLE));
        }

        @Override
        public void remove()
        {
        }
      };
    }
  }

  private static SQLiteDatabase read(Context context)
  {
    final DataBase db = new DataBase(context);
    return db.getReadableDatabase();
  }

  private static SQLiteDatabase write(Context context)
  {
    final DataBase db = new DataBase(context);
    return db.getWritableDatabase();
  }

  public static Iterable<Account> getAccounts(Context context)
  {
    SQLiteDatabase db = read(context);
    return new AccountsEnumarator(db.query(AccountTable.TABLE_NAME,AccountTable.COLUMNS_SHORT,null,null,null,null,null),db);
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

  public static boolean addAgents(Context context, Iterable<Agent> agents)
  {
    SQLiteDatabase db = write(context);
    try
    {
      ContentValues values = new ContentValues();
      for(Agent agent : agents)
      {
        values.put(AgentsTable._ID,agent.getId());
        values.put(AgentsTable.NAME,agent.getName());
        String phone = agent.getPhone();
        values.put(AgentsTable.PHONE,phone==null?"":phone);
        values.put(AgentsTable.ACCOUNT,agent.getAccount());
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

  public static boolean addTerminals(Context context, Iterable<Terminal> terminals)
  {
    SQLiteDatabase db = write(context);
    try
    {
      ContentValues values = new ContentValues();
      for(Terminal terminal : terminals)
      {
        values.put(TerminalsTable._ID,terminal.getId());
        values.put(TerminalsTable.NAME,terminal.getDisplayName());
        values.put(TerminalsTable.ADDRESS,terminal.getAddress());
        values.put(TerminalsTable.AGENT,terminal.getAgentId());
        db.insert(TerminalsTable.TABLE_NAME,null,values);
      }
    }
    finally
    {
      if(db!=null)
        db.close();
    }
    return true;
  }

  public static boolean addStatuses(Context context, Iterable<TerminalStatus> statuses)
  {
    SQLiteDatabase db = write(context);
    try
    {
      ContentValues values = new ContentValues();
      for(TerminalStatus status : statuses)
      {
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
        db.insert(StatusesTable.TABLE_NAME,null,values);
      }
    }
    finally
    {
      if(db!=null)
        db.close();
    }
    return true;
  }
}