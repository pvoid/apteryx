package org.pvoid.apteryxaustralis.net;

import android.os.AsyncTask;

public class RequestTask extends AsyncTask<Request, Integer, Response>
{
  protected IResponseHandler _handler;
  
  public RequestTask(IResponseHandler handler)
  {
    _handler = handler; 
  }
  
  @Override
  protected Response doInBackground(Request... params)
  {
    if(params.length==0)
      return(null);
    
    return(params[0].getResponse());
  }
  
  @Override
  protected void onPostExecute(Response response)
  {
    if(_handler!=null)
      _handler.onResponse(response);
  }
}