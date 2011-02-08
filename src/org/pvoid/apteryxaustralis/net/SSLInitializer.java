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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLInitializer
{
  private static final TrustManager[] _TrustAllCerts = new TrustManager[]
  {
    new X509TrustManager()
    {
      public java.security.cert.X509Certificate[] getAcceptedIssuers()
      {
        return null;
      }
      public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) 
      {
      }
      public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
      {
      }
    }
  };
   
  private static final  HostnameVerifier _Verifier = new HostnameVerifier() 
  {
    public boolean verify(String string, SSLSession sSLSession)
    {
      return true;
    }
  };
  
  public SSLInitializer()
  {
    try
    {
      SSLContext sc = SSLContext.getInstance("TLS");
      sc.init(null, _TrustAllCerts, new java.security.SecureRandom());            
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      HttpsURLConnection.setDefaultHostnameVerifier(_Verifier);            
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
