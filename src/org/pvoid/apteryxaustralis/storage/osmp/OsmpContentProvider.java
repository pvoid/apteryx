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
import android.database.Cursor;
import android.net.Uri;

public class OsmpContentProvider extends ContentProvider
{
  public static final String AUTHORITY = "org.pvoid.apteryxaustralis.storage.osmp";
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
    return null;
  }

  @Override
  public String getType(Uri uri)
  {

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
}
