package org.pvoid.apteryxaustralis.net;

import org.pvoid.apteryxaustralis.accounts.Terminal;
import org.pvoid.apteryxaustralis.accounts.TerminalsSection;

public class TerminalsListRunnable implements Runnable
{
  public static Iterable<Terminal> getTerminals(String login, String password, String terminal)
  {
    Request request = new Request(login,password,terminal);
    request.getTerminals();
    Response response = request.getResponse();
    if(response==null)
      return null;
    TerminalsSection section = response.Terminals();
    if(section==null)
      return null;
    return section.getTerminals();
  }

  @Override
  public void run()
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
