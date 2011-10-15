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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class AccountsProvider extends ContentProvider
{
  public static final String AUTHORITY = "org.pvoid.apteryxaustralis.storage.accounts";
    private static final int ACCOUNTS_REQUEST = 1;

  public static interface Accounts
  {
    static final String MIMETYPE    = "vnd.org.pvoid.account";
    static final Uri    CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/accounts");
    static final String TABLE_NAME = "accounts";
    static final String COLUMN_ID = "id";
    static final String COLUMN_TITLE = "title";
    static final String COLUMN_LOGIN = "login";
    static final String COLUMN_PASSWORD = "password";
    static final String COLUMN_CUSTOM1 = "custom_1";
    static final String COLUMN_CUSTOM2 = "custom_2";
    static final String COLUMN_CUSTOM3 = "custom_3";
    static final String COLUMN_CUSTOM4 = "custom_4";
  }

  private static final UriMatcher _sUriMather;
  static
  {
    _sUriMather = new UriMatcher(UriMatcher.NO_MATCH);
    _sUriMather.addURI(AUTHORITY,"accounts",ACCOUNTS_REQUEST);
  }
  private AccountStorage _mStorage;

  @Override
  public boolean onCreate()
  {
    _mStorage = new AccountStorage(getContext());
    return true;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
  {
    switch(_sUriMather.match(uri))
    {
      case ACCOUNTS_REQUEST:
        final SQLiteDatabase db = _mStorage.getReadableDatabase();
        if(db==null)
          return null;

        return db.query(Accounts.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
    }
    return null;
  }

  @Override
  public String getType(Uri uri)
  {
    switch(_sUriMather.match(uri))
    {
      case ACCOUNTS_REQUEST:
        return "vnd.android.cursor.item/" + Accounts.MIMETYPE;
    }
    return null;
  }

  @Override
  public Uri insert(Uri uri, ContentValues contentValues)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public int delete(Uri uri, String s, String[] strings)
  {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public int update(Uri uri, ContentValues contentValues, String s, String[] strings)
  {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  private static class AccountStorage extends SQLiteOpenHelper
  {
    private static final String DBNAME  = "accounts";
    private static final int    VERSION = 1;

    private static final String CREATE_ACCOUNTS_TABLE = "create table " + Accounts.TABLE_NAME + " (" +
                                                        Accounts.COLUMN_ID + " text primary key," +
                                                        Accounts.COLUMN_TITLE + " text not null," +
                                                        Accounts.COLUMN_LOGIN + " text not null," +
                                                        Accounts.COLUMN_PASSWORD + " text not null," +
                                                        Accounts.COLUMN_CUSTOM1 + " text not null,"+
                                                        Accounts.COLUMN_CUSTOM2 + " text not null,"+
                                                        Accounts.COLUMN_CUSTOM3 + " text not null,"+
                                                        Accounts.COLUMN_CUSTOM4 + " text not null"+
                                                        ");";

    public AccountStorage(Context context)
    {
      super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
      database.execSQL(CREATE_ACCOUNTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
      // nope
    }
  }
}
