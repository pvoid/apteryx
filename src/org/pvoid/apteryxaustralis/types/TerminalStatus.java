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

package org.pvoid.apteryxaustralis.types;

import android.content.Context;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.preference.Preferences;

import java.util.ArrayList;
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
  
  public final static int STATE_PRINTER_STACKER_ERROR = 0x01; // автомат остановлен из-за ошибок купюроприемника или принтера
  public final static int STATE_INTERFACE_ERROR = 0x02; // автомат остановлен из-за ошибки в конфигурации интерфейса. новый интерфейс загружается с сервера.
  public final static int STATE_UPLOADING_UPDATES = 0x04; // автомат загружает с сервера обновление приложения
  public final static int STATE_DEVICES_ABSENT = 0x08; // автомат остановлен из-за того, что при старте не обнаружено оборудование (купюроприемник или принтер)
  public final static int STATE_STORAGE_TIMER = 0x10; // работает сторожевой таймер
  public final static int STATE_PAPER_COMING_TO_END = 0x20; // в принтере скоро закончится бумага
  public final static int STATE_STACKER_REMOVED = 0x40; // C автомата был снят купюроприемник
  public final static int STATE_ESSENTIAL_ELEMENTS_ERROR = 0x80; // Отсутствуют или неверно заполнены один или несколько реквизитов для терминала
  public final static int STATE_HARDDRIVE_PROBLEMS = 0x100; // Проблемы с жестким диском!!!
  public final static int STATE_STOPPED_DUE_BALANCE = 0x200; // Остановлен по сигналу сервера или из-за отсутствия денег на счету агента!
  public final static int STATE_HARDWARE_OR_SOFTWARE_PROBLEM  = 0x400; // Остановлен из-за проблем с железом или интерфейса.
  public final static int STATE_HAS_SECOND_MONITOR  = 0x800; // Автомат оснащен вторым монитором.
  public final static int STATE_DOOR_IS_OPENED = 0x1000; // Открыта дверь терминала.
  public final static int STATE_UNAUTHORIZED_SOFTWARE  = 0x2000; // Обнаружено стороннее ПО, которое может вызывать сбой модемного соединения.
  public final static int STATE_PROXY_SERVER  = 0x4000; // Автомат соединен через прокси-сервер.
  public final static int STATE_EMPTY1 = 0x8000;
  public final static int STATE_UPDATING_CONFIGURATION = 0x10000; // Терминал обновляет конфигурацию
  public final static int STATE_UPDATING_NUMBERS = 0x20000; // Терминал обновляет номерные емкости.
  public final static int STATE_UPDATING_PROVIDERS  = 0x40000; // Терминал обновляет список провайдеров.
  public final static int STATE_UPDATING_ADVERT     = 0x80000; // Терминал проверяет и обновляет рекламный плэйлист.
  public final static int STATE_UPDATING_FILES = 0x100000; // Терминал проверяет и обновляет файлы.
  public final static int STATE_EMPTY2 = 0x200000;
  public final static int STATE_ASO_MODIFIED = 0x400000; // Модифицировано приложение АСО.
  public final static int STATE_EMPTY3 = 0x800000;
  public final static int STATE_ASO_ENABLED = 0x1000000; // Монитор АСО выключен.
  public final static int STATE_EMPTY4 = 0x2000000;
  public final static int STATE_EMPTY5 = 0x4000000;
  public final static int STATE_EMPTY6 = 0x8000000;
  public final static int STATE_INTERFACE_OVERLAPPED = 0x10000000; //Обнаружено перекрытие платежного интерфейса окном стороннего приложения.

  public final static int STATE_COMMON_NONE = 0;
  public final static int STATE_COMMON_OK = 1;
  public final static int STATE_COMMON_WARNING = 2;
  public final static int STATE_COMMON_ERROR = 3;
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
  private long _mInfoDate;
  private float _mCash;

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
    if(printerErrorId==null)
      _mPrinterErrorId = "";
    else
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
    machineStatus = machineStatus.trim();
  	for(int index = 0,length=machineStatus.length();index<length;++index)
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
    if(noteErrorId==null)
      _mNoteErrorId = "";
    else
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

  public void setRequestDate(long date)
  {
    _mInfoDate = date;
  }

  public long getRequestDate()
  {
    return _mInfoDate;
  }

  public void update(TerminalStatus status)
  {
    _mId = status.getId();
    _mAgentId = status.getAgentId();
    _mLastActivityDate = status.getLastActivityDate();
    _mPrinterErrorId = status.getPrinterErrorId();
    _mNoteErrorId = status.getNoteErrorId();
    _mSignalLevel = status.getSignalLevel();
    _mSimProviderBalance = status.getSimProviderBalance();
    _mMachineStatus = status.getMachineStatus();
    _mWdtDoorOpenCount = status.getWdtDoorOpenCount();
    _mWdtDoorAlarmCount = status.getWdtDoorAlarmCount();
    _mWdtEvent = status.getWdtEvent();
  }

  public int getCommonState(Context context)
  {
    if((_mMachineStatus&STATE_PRINTER_STACKER_ERROR)!=0 ||
       (_mMachineStatus&STATE_INTERFACE_ERROR)!=0 || (_mMachineStatus&STATE_DEVICES_ABSENT)!=0 ||
       (_mMachineStatus&STATE_STACKER_REMOVED)!=0 ||
       (_mMachineStatus&STATE_STOPPED_DUE_BALANCE)!=0 || (_mMachineStatus&STATE_HARDWARE_OR_SOFTWARE_PROBLEM)!=0 ||
       (_mMachineStatus& STATE_DOOR_IS_OPENED)!=0 || (_mMachineStatus&STATE_INTERFACE_OVERLAPPED)!=0)
    {
      return STATE_COMMON_ERROR;
    }
////////
    if((_mMachineStatus&STATE_PAPER_COMING_TO_END)!=0 || (_mMachineStatus&STATE_UNAUTHORIZED_SOFTWARE)!=0 ||
        (_mMachineStatus&STATE_HARDDRIVE_PROBLEMS)!=0)
    {
      return STATE_COMMON_WARNING;
    }
////////
    if((_mInfoDate - _mLastActivityDate)> Preferences.getActivityTimeout(context))
      return STATE_COMMON_ERROR;
////////
    return STATE_COMMON_OK;
  }

  public String getErrorText(Context context, boolean full)
  {
    StringBuffer buffer = new StringBuffer();

    if((_mMachineStatus&STATE_PRINTER_STACKER_ERROR)!=0)
    {
      if(!full)
        return context.getString(R.string.STATE_PRINTER_STACKER_ERROR);
      buffer.append(context.getString(R.string.STATE_PRINTER_STACKER_ERROR));
    }
////////
    if((_mMachineStatus&STATE_INTERFACE_ERROR)!=0)
    {
      if(!full)
        return context.getString(R.string.STATE_INTERFACE_ERROR);
      buffer.append(context.getString(R.string.STATE_INTERFACE_ERROR));
    }
////////
    if((_mMachineStatus&STATE_DEVICES_ABSENT)!=0)
    {
      if(!full)
        return context.getString(R.string.STATE_DEVICES_ABSENT);
      buffer.append(context.getString(R.string.STATE_DEVICES_ABSENT));
    }
////////
    if((_mMachineStatus&STATE_STACKER_REMOVED)!=0)
    {
      if(!full)
        return context.getString(R.string.STATE_STACKER_REMOVED);
      buffer.append(context.getString(R.string.STATE_STACKER_REMOVED));
    }
////////
    if((_mMachineStatus&STATE_STOPPED_DUE_BALANCE)!=0)
    {
      if(!full)
        return context.getString(R.string.STATE_STOPPED_DUE_BALANCE);
      buffer.append(context.getString(R.string.STATE_STOPPED_DUE_BALANCE));
    }
////////
    if((_mMachineStatus&STATE_HARDWARE_OR_SOFTWARE_PROBLEM)!=0)
    {
      if(!full)
        return context.getString(R.string.STATE_HARDWARE_OR_SOFTWARE_PROBLEM);
      buffer.append(context.getString(R.string.STATE_HARDWARE_OR_SOFTWARE_PROBLEM));
    }
////////
    if((_mMachineStatus& STATE_DOOR_IS_OPENED)!=0)
    {
      if(!full)
        return context.getString(R.string.STATE_DOOR_ID_OPENED);
      buffer.append(context.getString(R.string.STATE_DOOR_ID_OPENED));
    }
////////
    if((_mMachineStatus&STATE_INTERFACE_OVERLAPPED)!=0)
    {
      if(!full)
        return context.getString(R.string.STATE_INTERFACE_OVERLAPPED);
      buffer.append(context.getString(R.string.STATE_INTERFACE_OVERLAPPED));
    }
////////
    return buffer.toString();
  }

  public Iterable<String> getStates(Context context)
  {
    ArrayList<String> items = new ArrayList<String>();

    if((_mMachineStatus&STATE_PRINTER_STACKER_ERROR)!=0)
    {
      items.add(context.getString(R.string.STATE_PRINTER_STACKER_ERROR));
    }

    if((_mMachineStatus&STATE_INTERFACE_ERROR)!=0)
    {
      items.add(context.getString(R.string.STATE_INTERFACE_ERROR));
    }

    if((_mMachineStatus&STATE_UPLOADING_UPDATES)!=0)
    {
      items.add(context.getString(R.string.STATE_UPLOADING_UPDATES));
    }

    if((_mMachineStatus&STATE_DEVICES_ABSENT)!=0)
    {
      items.add(context.getString(R.string.STATE_DEVICES_ABSENT));
    }

    if((_mMachineStatus&STATE_STORAGE_TIMER)!=0)
    {
      items.add(context.getString(R.string.STATE_STORAGE_TIMER));
    }

    if((_mMachineStatus&STATE_PAPER_COMING_TO_END)!=0)
    {
      items.add(context.getString(R.string.STATE_PAPER_COMING_TO_END));
    }

    if((_mMachineStatus&STATE_STACKER_REMOVED)!=0)
    {
      items.add(context.getString(R.string.STATE_STACKER_REMOVED));
    }

    if((_mMachineStatus&STATE_ESSENTIAL_ELEMENTS_ERROR)!=0)
    {
      items.add(context.getString(R.string.STATE_ESSENTIAL_ELEMENTS_ERROR));
    }

    if((_mMachineStatus&STATE_HARDDRIVE_PROBLEMS)!=0)
    {
      items.add(context.getString(R.string.STATE_HARDDRIVE_PROBLEMS));
    }

    if((_mMachineStatus&STATE_HARDDRIVE_PROBLEMS)!=0)
    {
      items.add(context.getString(R.string.STATE_HARDDRIVE_PROBLEMS));
    }

    if((_mMachineStatus&STATE_STOPPED_DUE_BALANCE)!=0)
    {
      items.add(context.getString(R.string.STATE_STOPPED_DUE_BALANCE));
    }

    if((_mMachineStatus&STATE_HARDWARE_OR_SOFTWARE_PROBLEM)!=0)
    {
      items.add(context.getString(R.string.STATE_HARDWARE_OR_SOFTWARE_PROBLEM));
    }

    if((_mMachineStatus&STATE_HAS_SECOND_MONITOR)!=0)
    {
      items.add(context.getString(R.string.STATE_HAS_SECOND_MONITOR));
    }

    if((_mMachineStatus& STATE_DOOR_IS_OPENED)!=0)
    {
      items.add(context.getString(R.string.STATE_DOOR_ID_OPENED));
    }

    if((_mMachineStatus&STATE_UNAUTHORIZED_SOFTWARE)!=0)
    {
      items.add(context.getString(R.string.STATE_UNAUTHORIZED_SOFTWARE));
    }

    if((_mMachineStatus&STATE_PROXY_SERVER)!=0)
    {
      items.add(context.getString(R.string.STATE_PROXY_SERVER));
    }

    if((_mMachineStatus&STATE_UPDATING_CONFIGURATION)!=0)
    {
      items.add(context.getString(R.string.STATE_UPDATING_CONFIGURATION));
    }

    if((_mMachineStatus&STATE_UPDATING_NUMBERS)!=0)
    {
      items.add(context.getString(R.string.STATE_UPDATING_NUMBERS));
    }

    if((_mMachineStatus&STATE_UPDATING_PROVIDERS)!=0)
    {
      items.add(context.getString(R.string.STATE_UPDATING_PROVIDERS));
    }

    if((_mMachineStatus&STATE_UPDATING_ADVERT)!=0)
    {
      items.add(context.getString(R.string.STATE_UPDATING_ADVERT));
    }

    if((_mMachineStatus&STATE_UPDATING_FILES)!=0)
    {
      items.add(context.getString(R.string.STATE_UPDATING_FILES));
    }

    if((_mMachineStatus&STATE_ASO_MODIFIED)!=0)
    {
      items.add(context.getString(R.string.STATE_ASO_MODIFIED));
    }

    if((_mMachineStatus&STATE_ASO_ENABLED)!=0)
    {
      items.add(context.getString(R.string.STATE_ASO_ENABLED));
    }

    if((_mMachineStatus&STATE_INTERFACE_OVERLAPPED)!=0)
    {
      items.add(context.getString(R.string.STATE_INTERFACE_OVERLAPPED));
    }

    return items;
  }

  public float getCash()
  {
    return _mCash;
  }

  public void setCash(float cash)
  {
    _mCash = cash;
  }
}
