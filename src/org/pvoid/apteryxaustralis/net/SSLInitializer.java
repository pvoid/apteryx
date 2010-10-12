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
