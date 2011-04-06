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

import java.util.ArrayList;
import java.util.HashMap;

import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.Agent;

import android.os.AsyncTask;

public class StatesRequestTask extends AsyncTask<Account, Integer, Boolean>
{
  private IStatesRespnseHandler _Handler;
  private StatesRequestWorker _Worker;
  
  public StatesRequestTask(IStatesRespnseHandler handler,HashMap<Long, ArrayList<Agent>> filter, TerminalsProcessData terminals)
  {
    _Handler = handler;
    _Worker = new StatesRequestWorker(terminals,filter);
  }
  
  @Override
  protected Boolean doInBackground(Account... accounts) 
  {
    return(_Worker.Work(accounts));
  }

  protected void onPostExecute(Boolean result)
  {
    if(_Handler==null)
      return;
////////
    if(result)
      _Handler.onSuccessRequest();
    else
      _Handler.onRequestError();
  }
}
