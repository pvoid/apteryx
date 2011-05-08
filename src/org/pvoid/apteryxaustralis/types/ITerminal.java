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

package org.pvoid.apteryxaustralis.types;

import android.content.ContentValues;
import android.content.Context;
import org.pvoid.apteryxaustralis.storage.ICommandResult;
import org.pvoid.apteryxaustralis.storage.IStorage;

import java.util.List;

public interface ITerminal
{
  static final int STATE_OK = 0;
  static final int STATE_WARNING = 1;
  static final int STATE_ERROR = 2;
  static final int STATE_ERROR_CRITICAL = 3;

  long getId();
  int getState();
  String getTitle();
  String getStatus(Context context);
  void getStatuses(Context context, List<StatusLine> statuses);
  void getInfo(Context context, List<InfoLine> info);
  void getActions(Context context, List<TerminalAction> actions);
  void runAction(IStorage storage, int action, ICommandResult resultHandler);
}
