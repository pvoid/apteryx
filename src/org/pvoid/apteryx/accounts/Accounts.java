package org.pvoid.apteryx.accounts;

import java.util.List;

import org.pvoid.apteryx.Consts;
import org.pvoid.apteryx.net.TerminalsProcessData;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Accounts
{
  private static final String CREATE_ACCOUNTS_TABLE = 
      "create table accounts (id integer primary key autoincrement, "
      + "title text not null,login text not null, password text not null,"
      +"terminal text not null);";
  
  private static final String CREATE_TERMINALS_TABLE = 
      "create table terminals (id text not null primary key asc, address text not null, state integer," +
      "printer_state text not null, cashbin_state text not null," +
      "lpd text not null, cash integer not null," +
      "last_activity text not null, last_payment text not null, bonds_count integer not null," +
      "balance text not null, signal_level integer not null, soft_version text not null," +
      "printer_model text not null, cashbin_model text null," +
      "bonds_10 integer not null,bonds_50 integer not null,bonds_100 integer not null," +
      "bonds_500 integer not null,bonds_1000 integer not null,bonds_5000 integer not null," +
      "bonds_10000 integer not null,pays_per_hour text not null,agent_id text not null," +
      "agent_name text not null);";
  
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
      // TODO Auto-generated method stub
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
  
  public long AddAccount(String title, String login, String password, String terminal)
  {
    ContentValues values = new ContentValues();
    values.put(Consts.COLUMN_TITLE, title);
    values.put(Consts.COLUMN_LOGIN, login);
    values.put(Consts.COLUMN_PASSWORD, password);
    values.put(Consts.COLUMN_TERMINAL, terminal);    
    SQLiteDatabase db = OpenWrite();
    long id = db.insert(Consts.ACCOUNTS_TABLE, null, values);
    _database.close();
    return(id);
  }
  
  public void EditAccount(long id,String title, String login, String password, String terminal)
  {
    ContentValues values = new ContentValues();
    values.put(Consts.COLUMN_TITLE, title);
    values.put(Consts.COLUMN_LOGIN, login);
    values.put(Consts.COLUMN_PASSWORD, password);
    values.put(Consts.COLUMN_TERMINAL, terminal);
    SQLiteDatabase db = OpenWrite();
    db.update(Consts.ACCOUNTS_TABLE, values, Consts.COLUMN_ID+'='+Long.toString(id), null);
    _database.close();
  }
  
  public void DeleteAccount(long id)
  {
    SQLiteDatabase db = OpenWrite();
    db.delete(Consts.ACCOUNTS_TABLE, Consts.COLUMN_ID + "=" + id,null);
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
          long id = cursor.getLong(0);
          adapter.add(new Account(id,cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4)));
        }
        while(cursor.moveToNext());
      }
      cursor.close();
    }
    _database.close();
  }
  
  public void SaveStates(final TerminalsProcessData terminals)
  {
    ContentValues values = new ContentValues();
    
    SQLiteDatabase db = OpenWrite();
    db.delete(Consts.TERMINALS_TABLE, null, null);    
    for(String terminal_id : terminals)
    {
      Terminal terminal = terminals.at(terminal_id);
      
      values.put(Consts.COLUMN_ID, terminal.id());
      values.put(Consts.COLUMN_ADDRESS, terminal.Address());
      values.put(Consts.COLUMN_STATE, terminal.State());
      values.put(Consts.COLUMN_PRINTERSTATE,terminal.printer_state);
      values.put(Consts.COLUMN_CASHBINSTATE,terminal.cashbin_state);
      values.put(Consts.COLUMN_LPD,terminal.lpd);
      values.put(Consts.COLUMN_CASH,terminal.cash);
      values.put(Consts.COLUMN_LASTACTIVITY,terminal.lastActivity);
      values.put(Consts.COLUMN_LASTPAYMENT,terminal.lastPayment);
      values.put(Consts.COLUMN_BONDS,terminal.bondsCount);
      values.put(Consts.COLUMN_BALANCE,terminal.balance);
      values.put(Consts.COLUMN_SIGNALLEVEL,terminal.signalLevel);
      values.put(Consts.COLUMN_SOFTVERSION,terminal.softVersion);
      values.put(Consts.COLUMN_PRINTERMODEL,terminal.printerModel);
      values.put(Consts.COLUMN_CASHBINMODEL,terminal.cashbinModel);
      values.put(Consts.COLUMN_BONDS10,terminal.bonds10count);
      values.put(Consts.COLUMN_BONDS50,terminal.bonds50count);
      values.put(Consts.COLUMN_BONDS100,terminal.bonds100count);
      values.put(Consts.COLUMN_BONDS500,terminal.bonds500count);
      values.put(Consts.COLUMN_BONDS1000,terminal.bonds1000count);
      values.put(Consts.COLUMN_BONDS5000,terminal.bonds5000count);
      values.put(Consts.COLUMN_BONDS10000,terminal.bonds10000count);
      values.put(Consts.COLUMN_PAYSPERHOUR,terminal.paysPerHour);
      values.put(Consts.COLUMN_AGENTID,terminal.agentId);
      values.put(Consts.COLUMN_AGENTNAME,terminal.agentName);
      
      db.insert(Consts.TERMINALS_TABLE, null, values);
    }
    _database.close();
///////
    SharedPreferences prefs = _context.getSharedPreferences(Consts.APTERYX_PREFS,Context.MODE_PRIVATE);
    SharedPreferences.Editor edit = prefs.edit();
    edit.putLong(Consts.PREF_LASTUPDATE, System.currentTimeMillis());
    edit.commit();
  }
  
  public void GetTerminals(final TerminalsProcessData terminals)
  {
    SQLiteDatabase db = OpenRead();
    Cursor cursor = db.query(Consts.TERMINALS_TABLE, new String[] {Consts.COLUMN_ID,
                                                                   Consts.COLUMN_ADDRESS,
                                                                   Consts.COLUMN_STATE,
                                                                   Consts.COLUMN_PRINTERSTATE,
                                                                   Consts.COLUMN_CASHBINSTATE,
                                                                   Consts.COLUMN_LPD,
                                                                   Consts.COLUMN_CASH,
                                                                   Consts.COLUMN_LASTACTIVITY,
                                                                   Consts.COLUMN_LASTPAYMENT,
                                                                   Consts.COLUMN_BONDS,
                                                                   Consts.COLUMN_BALANCE,
                                                                   Consts.COLUMN_SIGNALLEVEL,
                                                                   Consts.COLUMN_SOFTVERSION,
                                                                   Consts.COLUMN_PRINTERMODEL,
                                                                   Consts.COLUMN_CASHBINMODEL,
                                                                   Consts.COLUMN_BONDS10,
                                                                   Consts.COLUMN_BONDS50,
                                                                   Consts.COLUMN_BONDS100,
                                                                   Consts.COLUMN_BONDS500,
                                                                   Consts.COLUMN_BONDS1000,
                                                                   Consts.COLUMN_BONDS5000,
                                                                   Consts.COLUMN_BONDS10000,
                                                                   Consts.COLUMN_PAYSPERHOUR,
                                                                   Consts.COLUMN_AGENTID,
                                                                   Consts.COLUMN_AGENTNAME},
                             null,null,null,null,null,null);
    if(cursor!=null)
    {
      if(cursor.moveToFirst())
      {
        do
        {
          Terminal terminal = new Terminal(cursor.getString(0), cursor.getString(1));
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
          terminal.agentId = cursor.getString(23);
          terminal.agentName = cursor.getString(24);
          terminals.add(terminal);
        }
        while(cursor.moveToNext());
        cursor.close();
      }
    }
    _database.close();
  }
  
  public boolean CheckStates(final TerminalsProcessData terminals,List<Terminal> states)
  {
    Boolean result = false;
    SQLiteDatabase db = OpenRead();
    Cursor cursor = db.query(Consts.TERMINALS_TABLE, new String[] {Consts.COLUMN_ID, Consts.COLUMN_STATE},
                             null,null,null,null,null,null);
    if(cursor!=null)
    {
      if(cursor.moveToFirst())
      {
        do
        {
          String terminal_id = cursor.getString(0);
          int terminal_state = cursor.getInt(1);
          Terminal terminal = terminals.at(terminal_id);
          if(terminal!=null)
          {
            if(terminal.State() == Terminal.STATE_ERROR && terminal_state!=Terminal.STATE_ERROR)
            {
              result = true;
              states.add(terminal);
            }
          }
        }
        while(cursor.moveToNext());
        cursor.close();
      }
    }
    _database.close();
    return(result);
  }
}
