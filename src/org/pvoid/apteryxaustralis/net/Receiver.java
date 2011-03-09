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

package org.pvoid.apteryxaustralis.net;

import android.content.Context;
import android.util.Log;
import org.pvoid.apteryxaustralis.protocol.*;
import org.pvoid.apteryxaustralis.storage.IterableCursor;
import org.pvoid.apteryxaustralis.storage.Storage;
import org.pvoid.apteryxaustralis.types.*;

import java.util.List;
import java.util.TreeMap;

public class Receiver
{
  private static final int MAX_PAYMENTS_REQUESTS = 5;
  private static final int MAX_RETRIES = 5;

  public static boolean RefreshStates(Context context, TreeMap<Long,TerminalListRecord> records)
  {
    Log.d(Receiver.class.getSimpleName(),"RefreshStates started");
////////
    TreeMap<Long,TerminalStatus> statuses = null;
    if(records==null)
    {
      Iterable<TerminalStatus> statusesIterable = Storage.getStatuses(context);
      statuses = new TreeMap<Long,TerminalStatus>();
      if(statusesIterable!=null)
        for(TerminalStatus status : statusesIterable)
          statuses.put(status.getId(),status);
    }

    Iterable<Account> accounts = Storage.getAccounts(context);
    if(accounts!=null)
    {
      for(Account account : accounts)
      {
        Request request = new Request(account.getLogin(),account.getPassword(),Long.toString(account.getTerminalId()));
        Iterable<Agent> agents = Storage.getAgents(context, Storage.AgentsTable.ID);
        for(Agent agent : agents)
          request.getBalance(agent.getId());
        request.getTerminalsStatus();
        Response response = request.getResponse();
        if(response!=null)
        {
          ReportsSection section = response.Reports();
          if(section!=null)
          {
            Iterable<TerminalStatus> newStatuses = section.getTerminalsStatus();
            boolean loadTerminals = false;
            for(TerminalStatus status : newStatuses)
            {
              if(records!=null)
              {
                if(!records.containsKey(status.getId()))
                  loadTerminals = true;
                else
                  records.get(status.getId()).getStatus().update(status);
              }
              else if(!statuses.containsKey(status.getId()))
              {
                loadTerminals = true;
              }

              Storage.addStatus(context,status);
            }

            if(loadTerminals)
              RefreshTerminals(context,account);

            Storage.setAccountUpdateDate(context,account.getId(),System.currentTimeMillis());
          }

          AgentsSection agentsSection = response.Agents();
          if(agentsSection!=null)
          {
            List<AgentsSection.BalanceInfo> balances = agentsSection.getBalances();
            agents = Storage.getAgents(context, Storage.AgentsTable.ID);
            int index = 0;
            for(Agent agent : agents)
            {
              AgentsSection.BalanceInfo balanceInfo = balances.get(index);
              agent.setBalance(balanceInfo.getBalance());
              agent.setOverdraft(balanceInfo.getOverdraft());
              Storage.updateAgentBalance(context, agent);
              ++index;
            }
          }
        }
      }
      return true;
    }
    return false;
  }

  private static boolean receivePayments(Context context, Account account, Response response)
  {
    if(response==null)
      return false;

    ReportsSection reports = response.Reports();
    if(reports==null)
      return false;

    Request request = new Request(account.getLogin(),account.getPassword(),account.getTerminalId());
    for(ReportsSection.PaymentsRequest r : reports.getPaymentsRequests())
    {
      if(r.queId>0)
        request.getPaymentsFromQue(r.queId);
    }

    int retries = MAX_RETRIES;
    while(retries>0)
    {
      response = request.getResponse();
      request.clear();
      boolean retry = false;
      if(response!=null && (reports=response.Reports())!=null)
      {
        Storage.updatePayments(context, reports.getPayments());
        for(ReportsSection.PaymentsRequest r : reports.getPaymentsRequests())
        {
          switch(r.status)
          {
            case 1:
            case 2:
              request.getPaymentsFromQue(r.queId);
              retry = true;
              break;
          }
        }
      }

      if(retry)
      {
        --retries;
        try
        {
          Thread.sleep(500);
        }
        catch(InterruptedException e)
        {
          e.printStackTrace();
          return false;
        }
      }
      else
        return true;
    }
    return false;
  }

  public static boolean RefreshPayments(Context context)
  {
    Log.d(Receiver.class.getSimpleName(),"RefreshPayments started");
    IterableCursor<Account> accounts = (IterableCursor<Account>)Storage.getAccounts(context);
    if(accounts==null)
      return false;
////////
    for(Account account : accounts)
    {
      Iterable<Terminal> terminals = Storage.getTerminals(context,account);
      int requests = 0;
      Request request = new Request(account.getLogin(),account.getPassword(),account.getTerminalId());
      for(Terminal terminal : terminals)
      {
        if(requests==MAX_PAYMENTS_REQUESTS-1)
        {
          if(receivePayments(context,account,request.getResponse()))
            ;
          request.clear();
          requests = 0;
        }
        request.getPayments(terminal.getId());
        ++requests;
      }

      if(requests>0)
        receivePayments(context,account,request.getResponse());
    }

    return true;
  }

  public static boolean RefreshAgents(Context context)
  {
    boolean result = false;
    IterableCursor<Account> accounts = (IterableCursor<Account>)Storage.getAccounts(context);
    if(accounts==null)
      return result;
////////
    for(Account account : accounts)
    {
      Request request = new Request(account.getLogin(),account.getPassword(),account.getTerminalId());
      request.getAgentInfo();
      request.getAgents();
      Response response = request.getResponse();
      AgentsSection section;
      if(response!=null && (section=response.Agents())!=null)
      {
        Storage.addAgents(context,section.getAgents(),account.getId());
        result = true;
      }
    }
////////
    return result;
  }

  public static boolean RefreshTerminals(Context context, Account account)
  {
    boolean result = false;
    Request request = new Request(account.getLogin(),account.getPassword(),account.getTerminalId());
    request.getTerminals();
    Response response = request.getResponse();
    TerminalsSection terminalsSection;
    if(response!=null && (terminalsSection=response.Terminals())!=null)
    {
      Storage.addTerminals(context,terminalsSection.getTerminals());
    }
    return result;
  }
}
