package org.pvoid.apteryx.accounts;

import java.util.List;

import org.pvoid.apteryx.Consts;
import org.pvoid.apteryx.net.TerminalsProcessData;

import android.content.ContentValues;
import android.content.Context;
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
      "create table terminals_states (id text not null primary key asc, state integer)";
  
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
    for(String terminal_id : terminals)
    {
      values.put(Consts.COLUMN_ID, terminal_id);
      values.put(Consts.COLUMN_STATE, terminals.at(terminal_id).State());
      db.replace(Consts.TERMINALS_STATES_TABLE,null,values);
    }
    _database.close();
  }
  
  public boolean CheckStates(final TerminalsProcessData terminals,List<Terminal> states)
  {
    Boolean result = false;
    SQLiteDatabase db = OpenRead();
    Cursor cursor = db.query(Consts.TERMINALS_STATES_TABLE, new String[] {Consts.COLUMN_ID, Consts.COLUMN_STATE},
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
