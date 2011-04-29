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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Hashtable;

public class States
{
  private static final String DB_NAME = "apx_states";
  private static final int DB_VERSION = 1;

  private static interface GroupsStates
  {
    static final String TABLE_NAME = "groups_states";
    static final String COLUMN_GROUP = "group_id";
    static final String COLUMN_STATE = "state";

    static final String CREATE_SQL = "create table " + TABLE_NAME + " (" +
                                     COLUMN_GROUP + " integer not null primary key asc," +
                                     COLUMN_STATE + " integer not null);";
    static final String[] FULL_QUERY = new String[] {COLUMN_GROUP,COLUMN_STATE};
  }

  private static class DatabaseHelper extends SQLiteOpenHelper
  {
    public DatabaseHelper(Context context)
    {
      super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
      database.execSQL(GroupsStates.CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i1)
    {
      // nope
    }
  }

  private DatabaseHelper _mDatabase;
  private Context _mContext;

  public States(Context context)
  {
    _mContext = context.getApplicationContext();
  }

  private SQLiteDatabase OpenWrite()
  {
    _mDatabase = new DatabaseHelper(_mContext);
    return(_mDatabase.getWritableDatabase());
  }

  private SQLiteDatabase OpenRead()
  {
    _mDatabase = new DatabaseHelper(_mContext);
    return(_mDatabase.getReadableDatabase());
  }

  public void updateGroupsStates(Hashtable<Long,Integer> states)
  {
    SQLiteDatabase db = OpenWrite();
    try
    {
      ContentValues values = new ContentValues(2);
      for(Long groupId : states.keySet())
      {
        values.put(GroupsStates.COLUMN_GROUP, groupId);
        values.put(GroupsStates.COLUMN_STATE,states.get(groupId));
        db.replace(GroupsStates.TABLE_NAME,null,values);
      }
    }
    finally
    {
      _mDatabase.close();
    }
  }

  public boolean updateGroupState(long groupId, int state)
  {
    SQLiteDatabase db = OpenWrite();
    try
    {
      ContentValues values = new ContentValues(2);
      values.put(GroupsStates.COLUMN_GROUP, groupId);
      values.put(GroupsStates.COLUMN_STATE,state);
      db.replace(GroupsStates.TABLE_NAME,null,values);
      return true;
    }
    finally
    {
      _mDatabase.close();
    }
  }

  public boolean getGroupsStates(Hashtable<Long,Integer> states)
  {
    SQLiteDatabase db = OpenRead();
    try
    {
      Cursor cursor = db.query(GroupsStates.TABLE_NAME,GroupsStates.FULL_QUERY,null,null,null,null,null);
      if(cursor!=null)
        try
        {
          while(cursor.moveToNext())
          {
            states.put(cursor.getLong(0),cursor.getInt(1));
          }
          return true;
        }
        finally
        {
          cursor.close();
        }
    }
    finally
    {
      _mDatabase.close();
    }
    return false;
  }
}
