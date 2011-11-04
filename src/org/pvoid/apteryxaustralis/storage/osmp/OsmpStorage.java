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

import android.content.Context;
import org.pvoid.apteryxaustralis.net.Request;
import org.pvoid.apteryxaustralis.net.osmp.ResponseParser;
import org.pvoid.apteryxaustralis.types.Group;

import java.util.Comparator;
import java.util.List;

public class OsmpStorage
{
  private Storage _mStorage;

  private final Comparator<ResponseParser.Terminal> _mComparatorById = new Comparator<ResponseParser.Terminal>()
  {
    @Override
    public int compare(ResponseParser.Terminal o1, ResponseParser.Terminal o2)
    {
      return (int) (o1.id() - o2.id());
    }
  };

  public OsmpStorage(Context context)
  {
    _mStorage = new Storage(context.getApplicationContext());
  }

  public void deleteAccount(long id)
  {
    throw new RuntimeException("Not implemented!");
  }

  /*@Override
  public int addAccount(Account account)
  {
    ArrayList<Group> groups = new ArrayList<Group>();
    int result = OsmpRequest.checkAccount(account, groups);
    if(result==0)
    {
      _mStorage.addAccount(account.id,account.title,account.login,account.passwordHash,account.terminal);
/////// Вытащим балансы
      OsmpRequest.getBalances(account,groups);
      _mStorage.saveAgents(account.id, groups);
/////// Вытащим сразу терминалы
      ArrayList<Terminal> terminals = new ArrayList<Terminal>();
      if(OsmpRequest.getTerminals(account,terminals)==0)
        _mStorage.saveTerminals(account.id,terminals);
      return RES_OK;
    }
///////
    if(result>0)
      return RES_ERR_CUSTOM_FIRST - result;
///////
    return result;
  }*/

/*  @Override
  public int updateAccount(Account account, Hashtable<Long,Integer> states)
  {
    ArrayList<Terminal> terminals = new ArrayList<Terminal>();
    _mStorage.getTerminals(0,terminals);
    int result = refresh(account);
    if(result==IStorage.RES_OK)
    {
      Collections.sort(terminals,_mComparatorById);
      ArrayList<Terminal> updatedTerminals = new ArrayList<Terminal>();
      _mStorage.getTerminals(0,updatedTerminals);
      for(Terminal updatedTerminal : updatedTerminals)
      {
        int index = Collections.binarySearch(terminals,updatedTerminal,_mComparatorById);
        if(index>-1)
        {
          Terminal terminal = terminals.get(index);
          if(updatedTerminal.State()!=Terminal.OSMP_STATE_OK && terminal.State()==Terminal.OSMP_STATE_OK)
          {
            int state = updatedTerminal.getState();
            Integer groupState = states.get(terminal.agentId);
            if(groupState==null || groupState<state)
              states.put(terminal.agentId,state);

            if(result<state)
              result = state;
          }
        }
      }
    }
    return result;
  }*/

  public void getGroups(long accountId, List<Group> groups)
  {
    _mStorage.getAgents(accountId, groups);
  }

  public void getGroups(List<Group> groups)
  {
    _mStorage.getAgentsActive(groups);
  }

/*  public int refresh(Account account)
  {
    ArrayList<Group> groups = new ArrayList<Group>();
    int result = OsmpRequest.checkAccount(account, groups);
    if(result==0)
    {
/////// Вытащим балансы
      OsmpRequest.getBalances(account,groups);
      _mStorage.saveAgents(account.id, groups);
/////// Вытащим сразу терминалы
      ArrayList<Terminal> terminals = new ArrayList<Terminal>();
      if(OsmpRequest.getTerminals(account,terminals)==0)
      {
        _mStorage.saveTerminals(account.id,terminals);
      }
      return RES_OK;
    }
///////
    if(result>0)
      return RES_ERR_CUSTOM_FIRST - result;
///////
    return result;
  }*/

  public boolean isEmpty()
  {
    return !_mStorage.hasAccounts();
  }

  public int errorMessage(int errorCode)
  {
    return ErrorCodes.Message(errorCode);
  }

  public int rebootTerminal(long terminalId, long agentId)
  {
    /*Account account = _mStorage.getAccountFromAgent(agentId);
    return OsmpRequest.rebootTerminal(account,terminalId);*/
    return Request.RES_ERR_CUSTOM_FIRST;
  }

  public int switchOffTerminal(long terminalId, long agentId)
  {
    /*Account account = _mStorage.getAccountFromAgent(agentId);
    return OsmpRequest.switchOffTerminal(account, terminalId);*/
    return Request.RES_ERR_CUSTOM_FIRST;
  }
}
