package org.pvoid.apteryx.net;

import org.pvoid.apteryx.accounts.Account;
import android.os.AsyncTask;

public class StatesRequestTask extends AsyncTask<Account, Integer, Boolean>
{
  private IStatesRespnseHandler _Handler;
  private StatesRequestWorker _Worker;
  
  public StatesRequestTask(IStatesRespnseHandler handler, TerminalsProcessData terminals)
  {
    _Handler = handler;
    _Worker = new StatesRequestWorker(terminals);
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
