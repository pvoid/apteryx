package org.pvoid.apteryx.net;

import org.pvoid.apteryx.Consts;
import android.os.AsyncTask;
import android.os.Bundle;

public class RequestTask extends AsyncTask<Bundle, Integer, String>
{
  public static final String DATA = "RequestTask.data";
  protected IResponseHandler _handler;
  
  public RequestTask(IResponseHandler handler)
  {
    _handler = handler; 
  }
  
  @Override
  protected String doInBackground(Bundle... params)
  {
    if(params.length==0)
      return(null);
    
    String data = params[0].getString(DATA);
    return(DataTransfer.Load(Consts.URL, data));
  }
  
  @Override
  protected void onPostExecute(String response)
  {
    if(_handler!=null)
      _handler.onResponse(response);
  }
}