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

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request
{
  public static class Response
  {
    public final String data;
    public final int code;

    public Response(int code, String data)
    {
      this.data = data;
      this.code = code;
    }
  }

  static
  {
    SSLInitializer.Initialize();
    //--- ОСМП не может использовать соединение повторно
    System.setProperty("http.keepAlive","false");
  }

  public static Response Send(URL url, String data, String defaultEncoding)
  {
    try
    {
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//////// Отправим данные на сервер
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
      writer.write(data);
      writer.flush();
      writer.close();
//////// Вычитаем данные с сервера
      int httpCode = connection.getResponseCode();
      String encoding = connection.getContentEncoding();
      if(TextUtils.isEmpty(encoding))
      {
        encoding = defaultEncoding;
      }
      Log.v(Request.class.getSimpleName(),"Encoding: " + encoding);
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),encoding));
      String line;
      StringBuffer result = new StringBuffer();
      while((line = reader.readLine())!=null)
      {
        result.append(line);
      }
      reader.close();

      return new Response(httpCode,result.toString());
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    return null;
  }
}
