package org.pvoid.apteryxaustralis.accounts;

import android.os.Parcel;

import java.util.Date;

public class TerminalStatus
{
  public final static int WDT_SENSORALARM = 0x01;
  public final static int WDT_DOORALARM = 0x02;
  public final static int WDT_DOOROPENED = 0x04;
  public final static int WDT_LOCKOPENED = 0x08;
  public final static int WDT_NO220POWER = 0x10;
  public final static int WDT_220POWERRESUMED = 0x20;
  public final static int WDT_SCHEDULEDPOWEROFF = 0x40;    
  public final static int WDT_SCHEDULEDPOWERON = 0x80;   
  public final static int WDT_UPSLOWBATTAREY = 0x100; 
  public final static int WDT_NO12VOLTSFROMCOMPUTER = 0x200;
  public final static int WDT_SECURITYALARMMODE = 0x400;
  
  public final static int STATE_PRINTER_ERROR = 0x01;
  public final static int STATE_UNKNOWN1 = 0x02;
  public final static int STATE_UPLOADING_UPDATES = 0x04;
  public final static int STATE_UNKNOWN2 = 0x08;
  public final static int STATE_STORAGE_TIMER = 0x10;
  public final static int STATE_UNKNOWN3 = 0x20;
  public final static int STATE_STACKER_REMOVED = 0x40;
  public final static int STATE_ESSENTIAL_ELEMENTS_ERROR = 0x80;
  public final static int STATE_UNKNOWN4 = 0x100;
  public final static int STATE_STOPED_DUE_BALANCE = 0x200;
  public final static int STATE_UNKNOWN5  = 0x400;
  public final static int STATE_UNKNOWN6  = 0x800;
  public final static int STATE_UNKNOWN7  = 0x1000;
  public final static int STATE_UNKNOWN8  = 0x2000;
  public final static int STATE_UNKNOWN9  = 0x4000;
  public final static int STATE_UNKNOWN10 = 0x8000;
  public final static int STATE_UNKNOWN11 = 0x10000;
  public final static int STATE_REFRESH_PROVIDERS = 0x20000;
  public final static int STATE_REFRESH_PLAYLIST  = 0x40000;
  public final static int STATE_REFRESH_FILES     = 0x80000;
  public final static int STATE_UNKNOWN12 = 0x100000;
  public final static int STATE_UNKNOWN13 = 0x200000;
  public final static int STATE_UNKNOWN14 = 0x400000;
//////
  private long _mId;
  private long _mAgentId;
  private long _mLastActivityDate;
  private String _mPrinterErrorId;
  private String _mNoteErrorId;
  private int _mSignalLevel;
  private float _mSimProviderBalance;
  private int _mMachineStatus;
  private short _mWdtDoorOpenCount;
  private short _mWdtDoorAlarmCount;
  private short _mWdtEvent;

  public TerminalStatus(long id)
  {
    _mId = id;
  }

  public long getId()
  {
    return _mId;
  }

  public void setAgentId(long agentId)
  {
    _mAgentId = agentId;
  }

  public long getAgentId()
  {
    return(_mAgentId);
  }

  public void setPrinterErrorId(String printerErrorId)
  {
    _mPrinterErrorId = printerErrorId;
  }

  public String getPrinterErrorId()
  {
    return(_mPrinterErrorId);
  }

  public void setSignalLevel(int SignalLevel)
  {
    _mSignalLevel = SignalLevel;
  }

  public int getSignalLevel()
  {
    return _mSignalLevel;
  }

  public void setSimProviderBalance(float simProviderBalance)
  {
    _mSimProviderBalance = simProviderBalance;
  }

  public float getSimProviderBalance()
  {
    return _mSimProviderBalance;
  }

  public void setMachineStatus(String machineStatus)
  {
  	int flag = 1;
  	int result = 0;
  	for(int index = machineStatus.length()-1;index>=0;--index)
  	{
  		if(machineStatus.charAt(index)=='1')
  			result|=flag;
  		flag*=2;
  	}
    _mMachineStatus = result;
  }

  public void setMachineStatus(int machineStatus)
  {
    _mMachineStatus = machineStatus;
  }

  public int getMachineStatus()
  {
    return _mMachineStatus;
  }

  public void setWdtDoorOpenCount(short wdtDoorOpenCount)
  {
    _mWdtDoorOpenCount = wdtDoorOpenCount;
  }

  public short getWdtDoorOpenCount()
  {
    return _mWdtDoorOpenCount;
  }

  public void setWdtDoorAlarmCount(short wdtDoorAlarmCount)
  {
    _mWdtDoorAlarmCount = wdtDoorAlarmCount;
  }

  public short getWdtDoorAlarmCount()
  {
    return _mWdtDoorAlarmCount;
  }

  public void setWdtEvent(short wdtEvent)
  {
    _mWdtEvent = wdtEvent;
  }

  public short getWdtEvent()
  {
    return _mWdtEvent;
  }

  public void setNoteErrorId(String noteErrorId)
  {
    _mNoteErrorId = noteErrorId;
  }

  public String getNoteErrorId()
  {
    return _mNoteErrorId;
  }

  public void setLastActivityDate(Date date)
  {
    _mLastActivityDate = date.getTime();
  }

  public void setLastActivityDate(long date)
  {
    _mLastActivityDate = date;
  }
  
  public long getLastActivityDate()
  {
  	return(_mLastActivityDate);
  }
}
