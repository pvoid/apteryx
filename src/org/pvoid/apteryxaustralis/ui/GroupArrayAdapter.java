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
import android.widget.ArrayAdapter;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.accounts.Group;
import org.pvoid.apteryxaustralis.accounts.Terminal;

public class GroupArrayAdapter extends ArrayAdapter<Terminal>
{
  Group _mGroup;

  public GroupArrayAdapter(Context context, Group group)
  {
    super(context, R.layout.terminal,R.id.list_title);
    _mGroup = group;
  }

  public long getGroupId()
  {
    if(_mGroup==null)
      return 0;
////////
    return _mGroup.id;
  }
}
