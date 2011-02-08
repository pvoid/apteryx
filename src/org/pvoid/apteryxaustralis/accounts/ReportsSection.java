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

package org.pvoid.apteryxaustralis.accounts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import org.pvoid.apteryxaustralis.net.IResponseParser;
import org.xml.sax.Attributes;

public class ReportsSection implements IResponseParser
{
  private final int STATE_NONE = 0;
  private final int STATE_TERMINALS_STATUSES = 1;
  
  private int _CurrentState;
  private SimpleDateFormat _DateFormat;  
  private ArrayList<TerminalStatus> _Statuses = null;
  
  public ReportsSection()
  {
    _DateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    TimeZone timezone = TimeZone.getTimeZone("Europe/Moscow");
    _DateFormat.setTimeZone(timezone);
  }
  
  @Override
  public void SectionStart()
  {
    _CurrentState = STATE_NONE;
  }

  @Override
  public void SectionEnd()
  {
    // TODO Auto-generated method stub

  }

  public static ReportsSection getParser()
  {
    return(new ReportsSection());
  }
  
  @Override
  public void ElementStart(String name, Attributes attributes)
  {
    if(name.equals("getTerminalsStatus"))
    {
      _CurrentState = STATE_TERMINALS_STATUSES;
      return;
    }
////////
    if(name.equals("row") && _CurrentState == STATE_TERMINALS_STATUSES)
    {
      String value = attributes.getValue("trmId");
      if(value==null || value.length()==0)
        return;
/////////////
      TerminalStatus status;
      try
      {
        status = new TerminalStatus(Long.parseLong(value));
      }
      catch(NumberFormatException e)
      {
        e.printStackTrace();
        return;
      }
/////////////
      value = attributes.getValue("agtId");
      if(value!=null && value.length()>0)
        try
        {
          status.setAgentId(Long.parseLong(value));
        }
        catch(NumberFormatException e)
        {
          e.printStackTrace();
        }
/////////////
      try
      {
        status.setLastActivityDate(_DateFormat.parse(attributes.getValue("lastActivityTime")));
      }
      catch (ParseException e)
      {
        e.printStackTrace();
      }
/////////////
      status.setPrinterErrorId(attributes.getValue("printerErrorId"));
/////////////
      status.setNoteErrorId(attributes.getValue("noteErrorId"));
/////////////
      value = attributes.getValue("signalLevel");
      if(value!=null && value.length()>0)
        try
        {
          status.setSignalLevel(Integer.parseInt(value));
        }
        catch(NumberFormatException e)
        {
          e.printStackTrace();
        }
/////////////
      value = attributes.getValue("simProviderBalance");
      if(value!=null && value.length()>0)
        try
        {
          status.setSimProviderBalance(Float.parseFloat(value));
        }
        catch(NumberFormatException e)
        {
          e.printStackTrace();
        }
/////////////
      status.setMachineStatus(attributes.getValue("machineStatus"));
/////////////
      value = attributes.getValue("wdtDoorOpenCount");
      if(value!=null && value.length()>0)
        try
        {
          status.setWdtDoorOpenCount(Short.parseShort(value));
        }
        catch(NumberFormatException e)
        {
          e.printStackTrace();
        }
/////////////
      value = attributes.getValue("wdtDoorAlarmCount");
      if(value!=null && value.length()>0)
        try
        {
          status.setWdtDoorAlarmCount(Short.parseShort(value));
        }
        catch(NumberFormatException e)
        {
          e.printStackTrace();
        }
/////////////
      value = attributes.getValue("wdtEvent");
      if(value!=null && value.length()>0)
        try
        {
          status.setWdtEvent(Short.parseShort(value));
        }
        catch(NumberFormatException e)
        {
          e.printStackTrace();
        }
      status.setRequestDate(System.currentTimeMillis());
/////////////
      if(_Statuses==null)
        _Statuses = new ArrayList<TerminalStatus>();
      _Statuses.add(status);
    }
  }

  @Override
  public void ElementEnd(String name, String innerText)
  {
    if(name.equals("getTerminalsStatus"))
    {
      _CurrentState = STATE_NONE; 
      return;
    }
  }

  public Iterable<TerminalStatus> getTerminalsStatus()
  {
    return(_Statuses);
  }
}
