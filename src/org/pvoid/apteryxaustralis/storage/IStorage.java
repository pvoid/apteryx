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


import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import org.pvoid.apteryxaustralis.types.Account;
import org.pvoid.apteryxaustralis.types.Group;
import org.pvoid.apteryxaustralis.types.ITerminal;
import org.pvoid.apteryxaustralis.ui.TerminalsArrayAdapter;

import java.util.Hashtable;
import java.util.List;

public interface IStorage
{
  static final int RES_OK = 0;
  static final int RES_OK_TERMINAL_WARNING = 1;
  static final int RES_OK_TERMINAL_ALARM = 2;
  static final int RES_OK_TERMINAL_ALARM_ALARM = 3;

  static final int RES_ERR_INVALID_ACCOUNT = -1;
  static final int RES_ERR_NETWORK_ERROR = -2;
  static final int RES_ERR_INCORRECT_RESPONSE = -3;

  static final int RES_ERR_CUSTOM_FIRST = -20;

  void      getAccounts(final List<Account> adapter);
  void      deleteAccount(long id);
  int       addAccount(Account account);
  Account   getAccount(long id);
  int       updateAccount(Account account, Hashtable<Long,Integer> states);
  void      getGroups(long accountId, List<Group> groups);
  void      getGroups(List<Group> groups);
  void      getTerminals(long accountId, Group group, TerminalsArrayAdapter terminals);
  ITerminal getTerminal(long id);
  int       refresh(Account account);
  boolean   isEmpty();
  int       errorMessage(int errorCode);
}
