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

import org.pvoid.apteryxaustralis.Consts;

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