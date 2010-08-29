package org.pvoid.apteryx;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;

public class UpdateStatusService extends Service
{
  private static final int INTERVAL = 60000;
  
  private Runnable _Task = new Runnable()
  {
    @Override
    public void run()
    {
      /**
       * TODO: Работаем
       */
      PendingIntent intent = PendingIntent.getService(UpdateStatusService.this, 0, 
                                      new Intent(UpdateStatusService.this,UpdateStatusService.class), 0);
      
      AlarmManager amanager = (AlarmManager)getSystemService(ALARM_SERVICE);
      amanager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+UpdateStatusService.INTERVAL,intent);
      
      UpdateStatusService.this.stopSelf();
    }
  };
  
  @Override
  public void onCreate()
  {
    Thread t = new Thread(null,_Task,"TestServiceThread");
    t.start();
  }
  
  private final IBinder _Binder = new Binder() 
  {
    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply,int flags) 
              throws RemoteException 
    {
      return super.onTransact(code, data, reply, flags);
    }
  };
  
  @Override
  public IBinder onBind(Intent arg0)
  {
    return(_Binder);
  }

}
