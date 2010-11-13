package org.pvoid.apteryxaustralis.net;

public class RequestRunnable implements Runnable
{
  private Request _Request;
  private IResponseHandler _Handler;
  
  public RequestRunnable(Request request, IResponseHandler handler)
  {
    _Request = request;
    _Handler = handler;
  }
  
  @Override
  public void run()
  {
    Response response = _Request.getResponse();
    if(response!=null && _Handler!=null)
    {
      _Handler.onResponse(response);
    }
  }
}
