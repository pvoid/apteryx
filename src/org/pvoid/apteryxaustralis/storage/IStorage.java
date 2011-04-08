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


import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.Agent;
import org.pvoid.apteryxaustralis.accounts.Terminal;

import java.util.List;

public interface IStorage
{
  static final int RES_OK = 0;
  static final int RES_OK_TERMINAL_ALARM = 1;

  static final int RES_ERR_INVALID_ACCOUNT = -1;
  static final int RES_ERR_NETWORK_ERROR = -2;


  void getAccounts(final List<Account> adapter);
  void deleteAccount(long id);
  int addAccount(Account account);
  Account getAccount(long id);
  int updateAccount(Account account);
  void getGroups(long accountId, List<Agent> agents);
  void getTerminals(long accountId, Agent group, List<Terminal> terminals);
  boolean isEmpty();
}
