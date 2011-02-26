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
import org.pvoid.apteryxaustralis.types.Account;
import org.pvoid.apteryxaustralis.types.Terminal;
import org.pvoid.apteryxaustralis.types.TerminalListRecord;
import org.pvoid.apteryxaustralis.types.TerminalStatus;

import java.util.TreeMap;

public class Receiver
{
  private static final int MAX_PAYMENTS_REQUESTS = 5;
  private static final int MAX_RETRIES = 3;

  public static boolean RefreshStates(Context context, TreeMap<Long,TerminalListRecord> records)
  {
    Log.d(Receiver.class.getSimpleName(),"RefreshStates started");
////////
    Iterable<TerminalStatus> statusesIterable = Storage.getStatuses(context);
    TreeMap<Long,TerminalStatus> statuses = null;
    if(records==null)
    {
      statuses = new TreeMap<Long,TerminalStatus>();
      for(TerminalStatus status : statusesIterable)
        statuses.put(status.getId(),status);
    }

    Iterable<Account> accounts = Storage.getAccounts(context);
    if(accounts!=null)
    {
      for(Account account : accounts)
      {
        Request request = new Request(account.getLogin(),account.getPassword(),Long.toString(account.getTerminalId()));
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
            {
              request = new Request(account.getLogin(),account.getPassword(),Long.toString(account.getTerminalId()));
              response = request.getResponse();
              if(response!=null)
              {
                TerminalsSection terminalsSection = response.Terminals();
                if(terminalsSection!=null)
                {
                  Storage.addTerminals(context,terminalsSection.getTerminals());
                }
              }
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
        --retries;
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
}
