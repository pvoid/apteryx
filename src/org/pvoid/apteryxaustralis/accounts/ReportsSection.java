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

import android.text.TextUtils;
import org.pvoid.apteryxaustralis.net.IResponseParser;
import org.xml.sax.Attributes;

public class ReportsSection implements IResponseParser
{
  private static final int STATE_NONE = 0;
  private static final int STATE_TERMINALS_STATUSES = 1;
  private static final int STATE_PAYMENTS = 2;
  
  private int _mCurrentState;
  private SimpleDateFormat _mDateFormat;
  private ArrayList<TerminalStatus> _mStatuses = null;
  private ArrayList<Payment> _mPayments = null;
  private long _mQueId = -1;
  
  public ReportsSection()
  {
  }
  
  @Override
  public void SectionStart()
  {
    _mCurrentState = STATE_NONE;
  }

  @Override
  public void SectionEnd()
  {
  }

  public static ReportsSection getParser()
  {
    return(new ReportsSection());
  }

  private long getPaymentDate(String date)
  {
    int index = date.lastIndexOf('+');
    if(index<0)
      index = date.lastIndexOf('-');
    try
    {
      _mDateFormat.parse(date.replace('T', ' ').substring(0, index)).getTime();
    }
    catch(ParseException e)
    {
      e.printStackTrace();
    }
    return 0;
  }

  @Override
  public void ElementStart(String name, Attributes attributes)
  {
    if("getTerminalsStatus".equals(name))
    {
      _mCurrentState = STATE_TERMINALS_STATUSES;
      _mDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
      TimeZone timezone = TimeZone.getTimeZone("Europe/Moscow");
      _mDateFormat.setTimeZone(timezone);
      return;
    }
////////
    if("getPayments".equals(name))
    {
      _mCurrentState = STATE_PAYMENTS;
      _mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      TimeZone timezone = TimeZone.getTimeZone("Europe/Moscow");
      _mDateFormat.setTimeZone(timezone);

      String queId = attributes.getValue("quid");
      if(!TextUtils.isEmpty(queId))
        _mQueId = Long.parseLong(queId);

      return;
    }
////////
    if("row".equals(name))
      if(_mCurrentState == STATE_TERMINALS_STATUSES)
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
          status.setLastActivityDate(_mDateFormat.parse(attributes.getValue("lastActivityTime")));
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
        if(_mStatuses ==null)
          _mStatuses = new ArrayList<TerminalStatus>();
        _mStatuses.add(status);
      }
      else if(_mCurrentState ==STATE_PAYMENTS)
      {
        String id = attributes.getValue("uid");
        String terminal = attributes.getValue("trm_id");
        if(TextUtils.isEmpty(id) || TextUtils.isEmpty(terminal))
          return;
        Payment payment = new Payment(Long.parseLong(id),Long.parseLong(terminal));
        String text = attributes.getValue("from-amount");
        if(!TextUtils.isEmpty(text))
          payment.setFromAmount(Float.parseFloat(text));
///////////
        text = attributes.getValue("to-amount");
        if(!TextUtils.isEmpty(text))
          payment.setToAmount(Float.parseFloat(text));
///////////
        text = attributes.getValue("status");
        if(!TextUtils.isEmpty(text))
          payment.setStatus(Integer.parseInt(text));
///////////
        text = attributes.getValue("to-prv-id");
        if(!TextUtils.isEmpty(text))
          payment.setProviderId(Long.parseLong(text));
///////////
        text = attributes.getValue("to-prv-short-name");
        if(!TextUtils.isEmpty(text))
          payment.setProviderName(text);
///////////
        text = attributes.getValue("receipt_date");
        if(!TextUtils.isEmpty(text))
        {
          payment.setDateInTerminal(getPaymentDate(text));
        }
///////////
        text = attributes.getValue("payment_date");
        if(!TextUtils.isEmpty(text))
        {
          payment.setDateInProcessing(getPaymentDate(text));
        }
///////////
        if(_mPayments ==null)
          _mPayments = new ArrayList<Payment>();
        _mPayments.add(payment);
      }
  }

  @Override
  public void ElementEnd(String name, String innerText)
  {
    if("getTerminalsStatus".equals(name))
    {
      _mCurrentState = STATE_NONE;
      return;
    }
////////
    if("getPayments".equals(name))
    {
      _mCurrentState = STATE_NONE;
      return;
    }
  }

  public Iterable<TerminalStatus> getTerminalsStatus()
  {
    return _mStatuses;
  }

  public Iterable<Payment> getPayments()
  {
    return _mPayments;
  }

  public long getQueId()
  {
    return _mQueId;
  }
}
