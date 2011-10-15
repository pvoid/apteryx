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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class OsmpContentProvider extends ContentProvider
{
  public static final String AUTHORITY = "org.pvoid.apteryxaustralis.storage.osmp";
  private static final int AGENTS_REQUEST = 1;
  private static final int TERMINALS_REQUEST = 2;

  public static interface Agents
  {
    static final String MIMETYPE    = "vnd.org.pvoid.osmp.agent";
    static final Uri    CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/agents");
    static final String TABLE_NAME = "agents";
    static final String COLUMN_ACCOUNT = "account";
    static final String COLUMN_AGENT = "_id";
    static final String COLUMN_AGENT_NAME = "agent_name";
    static final String COLUMN_BALANCE = "agent_balance";
    static final String COLUMN_OVERDRAFT = "agent_overdraft";
    static final String COLUMN_LAST_UPDATE = "last_update";
    static final String COLUMN_STATE = "state";
    static final String COLUMN_SEEN = "seen";
  }

  public static interface Terminals
  {
    static final String MIMETYPE    = "vnd.org.pvoid.osmp.terminal";
    static final Uri    CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/terminals");
    static final String TABLE_NAME = "terminals";
    static final String COLUMN_ID = "_id";
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
  }

  private static final UriMatcher _sUriMather;
  static
  {
    _sUriMather = new UriMatcher(UriMatcher.NO_MATCH);
    _sUriMather.addURI(AUTHORITY,"agents",AGENTS_REQUEST);
    _sUriMather.addURI(AUTHORITY,"terminals",TERMINALS_REQUEST);
  }
  private OsmpContentStorage _mStorage;

  @Override
  public boolean onCreate()
  {
    _mStorage = new OsmpContentStorage(getContext());
    return true;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
  {
    switch(_sUriMather.match(uri))
    {
      case AGENTS_REQUEST:
        final SQLiteDatabase db = _mStorage.getReadableDatabase();
        return db.query(Agents.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
    }
    return null;
  }

  @Override
  public String getType(Uri uri)
  {
    switch(_sUriMather.match(uri))
    {
      case AGENTS_REQUEST:
        return Agents.MIMETYPE;
      case TERMINALS_REQUEST:
        return Terminals.MIMETYPE;
    }
    return null;
  }

  @Override
  public Uri insert(Uri uri, ContentValues contentValues)
  {
    switch(_sUriMather.match(uri))
    {
      case AGENTS_REQUEST:
        final SQLiteDatabase db = _mStorage.getWritableDatabase();
        if(db.insert(Agents.TABLE_NAME,null,contentValues)!=-1)
          return uri;
    }
    return null;
  }

  @Override
  public int delete(Uri uri, String s, String[] strings)
  {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public int update(Uri uri, ContentValues contentValues, String whereClause, String[] selectionArgs)
  {
    switch(_sUriMather.match(uri))
    {
      case AGENTS_REQUEST:
        final SQLiteDatabase db = _mStorage.getWritableDatabase();
        return db.update(Agents.TABLE_NAME,contentValues,whereClause,selectionArgs);
    }
    return -1;
  }
}
