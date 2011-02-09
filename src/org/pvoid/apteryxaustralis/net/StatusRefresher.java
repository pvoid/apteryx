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
import org.pvoid.apteryxaustralis.accounts.*;
import org.pvoid.apteryxaustralis.storage.Storage;

import java.util.TreeMap;

public class StatusRefresher
{
  public static boolean RefreshStates(Context context, TreeMap<Long,TerminalListRecord> records)
  {
    Iterable<TerminalStatus> statusesIterable = Storage.getStatuses(context);
    TreeMap<Long,TerminalStatus> statuses = null;
    if(records==null)
    {
      statuses = new TreeMap<Long,TerminalStatus>();
      for(TerminalStatus status : statusesIterable)
        statuses.put(status.getId(),status);
    }

    Iterable<Account> accounts = Storage.getAccounts(context);
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
    return false;
  }
}
