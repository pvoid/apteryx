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

package org.pvoid.apteryxaustralis.ui;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import org.pvoid.apteryxaustralis.types.Group;

import java.util.concurrent.locks.ReentrantLock;

public class TerminalsArrayAdapter implements ListAdapter
{
  ReentrantLock _mLock;
  private final LayoutInflater _mInflater;
  private Group _mGroup;
  private DataSetObserver _mObserver;
  private final Context _mContext;
  private long _mCash;

  public TerminalsArrayAdapter(Context context, Group group)
  {
    _mLock = new ReentrantLock();
    _mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    _mGroup = group;
    _mContext = context;
  }

  public Group getGroup()
  {
    return _mGroup;
  }

  @Override
  public synchronized void registerDataSetObserver(DataSetObserver dataSetObserver)
  {
    if(_mObserver==null)
      _mObserver = dataSetObserver;
  }

  @Override
  public void unregisterDataSetObserver(DataSetObserver dataSetObserver)
  {
    if(_mObserver == dataSetObserver)
      _mObserver = null;
  }

  @Override
  public int getCount()
  {
    return 0;
  }

  @Override
  public Object getItem(int index)
  {
    return null;
  }

  @Override
  public long getItemId(int i)
  {
    return 0;
  }

  @Override
  public boolean hasStableIds()
  {
    return false;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    View view = convertView;
    /*if(view==null)
    {
      view = _mInflater.inflate(R.layout.terminal, null);
    }

    ITerminal terminal = (ITerminal) getItem(position);
    if(terminal!=null)
    {
      TextView name = (TextView)view.findViewById(R.id.list_title);
      name.setText(terminal.getTitle());
      TextView status = (TextView) view.findViewById(R.id.status);
      status.setText(terminal.getStatus(_mContext));
      ImageView icon = (ImageView)view.findViewById(R.id.icon);
      switch(terminal.getState())
      {
        case ITerminal.STATE_OK:
          icon.setImageResource(R.drawable.ic_terminal_active);
          break;
        case ITerminal.STATE_WARNING:
          icon.setImageResource(R.drawable.ic_terminal_pending);
          break;
        case ITerminal.STATE_ERROR:
          icon.setImageResource(R.drawable.ic_terminal_printer_error);
          break;
        case ITerminal.STATE_ERROR_CRITICAL:
          icon.setImageResource(R.drawable.ic_terminal_inactive);
          break;
      }
    }*/
    return view;
  }

  @Override
  public int getItemViewType(int i)
  {
    return 0;
  }

  @Override
  public int getViewTypeCount()
  {
    return 1;
  }

  @Override
  public boolean isEmpty()
  {
    return false;
  }

  public void setGroup(Group group)
  {
    _mGroup = group;
  }

  public void setState(Integer state)
  {
    _mGroup.state = state;
  }

  public int getState()
  {
    return _mGroup.state;
  }

  @Override
  public boolean areAllItemsEnabled()
  {
    return true;
  }

  @Override
  public boolean isEnabled(int i)
  {
    return true;
  }

  public void notifyList()
  {
    _mObserver.onChanged();
  }

  public long getCash()
  {
    return _mCash;
  }
}
