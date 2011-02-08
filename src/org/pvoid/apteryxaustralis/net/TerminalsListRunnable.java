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
