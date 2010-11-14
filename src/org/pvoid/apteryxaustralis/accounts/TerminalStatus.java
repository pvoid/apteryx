package org.pvoid.apteryxaustralis.accounts;

import java.util.Date;

public class TerminalStatus extends Preserved
{
  private static final long serialVersionUID = 8845327592559909478L;
//////
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
//////
  private long _AgentId;
  private long _LastActivityDate;
  private String _PrinterErrorId;
  private String _NoteErrorId;
  private int _SignalLevel;
  private float _SimProviderBalance;
  private int _MachineStatus;
  private short _WdtDoorOpenCount;
  private short _WdtDoorAlarmCount;
  private short _WdtEvent;

  public TerminalStatus(long id)
  {
    super(id);
  }
  
  @Override
  public <T extends Preserved> void Copy(T another)
  {
    TerminalStatus status = (TerminalStatus)another;
    _AgentId = status.getAgentId();
    _PrinterErrorId = status.getPrinterErrorId();
    _NoteErrorId = status.getNoteErrorId();
    _SignalLevel = status.getSignalLevel();
    _SimProviderBalance = status.getSimProviderBalance();
    _MachineStatus = status.getMachineStatus();
    _WdtDoorOpenCount = status.getWdtDoorOpenCount();
    _WdtDoorAlarmCount = status.getWdtDoorAlarmCount();
    _WdtEvent = status.getWdtEvent();
  }

  public void setAgentId(long agentId)
  {
    _AgentId = agentId;
  }

  public long getAgentId()
  {
    return(_AgentId);
  }

  public void setPrinterErrorId(String printerErrorId)
  {
    _PrinterErrorId = printerErrorId;
  }

  public String getPrinterErrorId()
  {
    return(_PrinterErrorId);
  }

  public void setSignalLevel(int SignalLevel)
  {
    _SignalLevel = SignalLevel;
  }

  public int getSignalLevel()
  {
    return _SignalLevel;
  }

  public void setSimProviderBalance(float simProviderBalance)
  {
    _SimProviderBalance = simProviderBalance;
  }

  public float getSimProviderBalance()
  {
    return _SimProviderBalance;
  }

  public void setMachineStatus(String machineStatus)
  {
    // TODO: Реализовать как узнаешь тайну флагов
    //_MachineStatus = machineStatus;
  }

  public int getMachineStatus()
  {
    return _MachineStatus;
  }

  public void setWdtDoorOpenCount(short wdtDoorOpenCount)
  {
    _WdtDoorOpenCount = wdtDoorOpenCount;
  }

  public short getWdtDoorOpenCount()
  {
    return _WdtDoorOpenCount;
  }

  public void setWdtDoorAlarmCount(short wdtDoorAlarmCount)
  {
    _WdtDoorAlarmCount = wdtDoorAlarmCount;
  }

  public short getWdtDoorAlarmCount()
  {
    return _WdtDoorAlarmCount;
  }

  public void setWdtEvent(short wdtEvent)
  {
    _WdtEvent = wdtEvent;
  }

  public short getWdtEvent()
  {
    return _WdtEvent;
  }

  public void setNoteErrorId(String noteErrorId)
  {
    _NoteErrorId = noteErrorId;
  }

  public String getNoteErrorId()
  {
    return _NoteErrorId;
  }

  public void setLastActivityDate(Date date)
  {
    _LastActivityDate = date.getTime();
  }
  
  public long getLastActivityDate()
  {
  	return(_LastActivityDate);
  }
}
