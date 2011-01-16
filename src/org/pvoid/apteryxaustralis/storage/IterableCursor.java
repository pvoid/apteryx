package org.pvoid.apteryxaustralis.storage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Iterator;

public abstract class IterableCursor<T> implements Iterable<T>
{
  private final Cursor _mCursor;

  public IterableCursor(Cursor cursor)
  {
    _mCursor = cursor;
  }

  protected abstract T getItem(Cursor cursor);

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
