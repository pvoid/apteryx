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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Iterator;

public abstract class IterableCursor<T> implements Iterable<T>
{
  protected final Cursor _mCursor;

  public IterableCursor(Cursor cursor)
  {
    _mCursor = cursor;
  }

  protected abstract T getItem(Cursor cursor);

  public int count()
  {
    return _mCursor.getCount();
  }

  @Override
  public Iterator<T> iterator()
  {
    return new Iterator<T>()
    {
      @Override
      public boolean hasNext()
      {
        if(_mCursor.isClosed())
          return false;

        if(_mCursor.isLast())
        {
          _mCursor.close();
          return false;
        }
        return true;
      }

      @Override
      public T next()
      {
        if(!_mCursor.moveToNext())
        {
          throw new RuntimeException("Don't call next after hasNext return false!");
        }

        return getItem(_mCursor);
      }

      @Override
      public void remove()
      {
      }
    };
  }
}
