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
