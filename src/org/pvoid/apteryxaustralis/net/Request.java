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

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import org.pvoid.apteryxaustralis.net.osmp.OsmpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request
{
  private static OsmpRequest _mOsmpRequest;

  public static final int STATE_OK = 0;
  public static final int STATE_WARNING = 1;
  public static final int STATE_ERROR = 2;
  public static final int STATE_ERROR_CRITICAL = 3;

  public static final int RES_ERR_NETWORK_ERROR = -1;
  public static final int RES_ERR_INCORRECT_RESPONSE = -2;
  public static final int RES_ERR_CUSTOM_FIRST = -20;

  static
  {
    SSLInitializer.Initialize();
    //--- В Android 2.2- есть ошибка с кэшированными запросами
    System.setProperty("http.keepAlive","false");
  }
  /**
   * Отправляет указанные данные методом POST на сервер по указанному URL
   * @param url             адрес куда отправляется запрос
   * @param data            отправляемые данные
   * @param defaultEncoding кодировка полученных данных
   * @return возвращает ответ сервера
   */
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
      StringBuilder result = new StringBuilder();
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
  /**
   * Добавляет новый аккаунт
   * @param context     контекст исполнения
   * @param accountData данные по аккаунту
   * @return код результата
   */
  public static int addAccount(Context context, Bundle accountData)
  {
    final OsmpRequest request = getOsmpRequest();
    ////////
    int result = request.checkAccount(context,accountData);
    if(result==0)
      result = request.getBalances(context,accountData);
    if(result==0)
      result = request.getTerminals(context,accountData);
    ////////
    return result;
  }

  public static int refresh(Context context, Bundle accountData)
  {
    final OsmpRequest request = getOsmpRequest();
    ////////
    int result = request.getBalances(context,accountData);
    if(result==0)
      result = request.getTerminals(context,accountData);
    return result;
  }

  private static OsmpRequest getOsmpRequest()
  {
    synchronized(OsmpRequest.class)
    {
      if(_mOsmpRequest==null)
        _mOsmpRequest = new OsmpRequest();
    }
    return _mOsmpRequest;
  }

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
}
