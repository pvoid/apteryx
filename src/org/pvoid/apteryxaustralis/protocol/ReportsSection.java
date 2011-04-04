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

package org.pvoid.apteryxaustralis.protocol;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import android.text.TextUtils;
import android.util.Log;
import org.pvoid.apteryxaustralis.net.IResponseParser;
import org.pvoid.apteryxaustralis.types.Payment;
import org.pvoid.apteryxaustralis.types.TerminalStatus;
import org.xml.sax.Attributes;

public class ReportsSection implements IResponseParser
{
  private static final int STATE_NONE = 0;
  private static final int STATE_TERMINALS_STATUSES = 1;
  private static final int STATE_PAYMENTS = 2;
  private static final int STATE_CASH = 3;
  
  private int _mCurrentState;
  private SimpleDateFormat _mDateFormat;
  private Vector<TerminalStatus> _mStatuses = null;
  private Vector<Payment> _mPayments = null;
  private Vector<PaymentsRequest> _mRequests = null;
  private Vector<CashRecord> _mCash = null;

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
    if(_mCash==null || _mCash.size()<=0)
      return;
///////
    CashRecord[] cashes = new CashRecord[_mCash.size()];
    _mCash.toArray(cashes);
    Comparator<CashRecord> comparator = new Comparator<CashRecord>()
    {
      @Override
      public int compare(CashRecord a, CashRecord b)
      {
        return (int)(a.terminalId - b.terminalId);
      }
    };
///////
    Arrays.sort(cashes,comparator);
    CashRecord needle = new CashRecord();
    for(TerminalStatus status : _mStatuses)
    {
      needle.terminalId = status.getId();
      int pos = Arrays.binarySearch(cashes,needle,comparator);
      if(pos>=0)
      {
        status.setCash(cashes[pos].cash);
      }
    }
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
      return _mDateFormat.parse(date.replace('T', ' ').substring(0, index)).getTime();
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

      String text = attributes.getValue("quid");
      long queId = 0;
      if(!TextUtils.isEmpty(text))
        queId = Long.parseLong(text);

      text = attributes.getValue("status");
      int state = 0;
      if(!TextUtils.isEmpty(text) && TextUtils.isDigitsOnly(text))
        state = Integer.parseInt(text);

      text = attributes.getValue("result");
      int result = 0;
      if(!TextUtils.isEmpty(text))
        result = Integer.parseInt(text);

      if(_mRequests==null)
        _mRequests = new Vector<PaymentsRequest>();
      _mRequests.add(new PaymentsRequest(queId,state,result));

      return;
    }
////////
    if("getTerminalsCash".equals(name))
    {
      _mCurrentState = STATE_CASH;
      return;
    }
////////
    if("terminal".equals(name) && _mCurrentState==STATE_CASH)
    {
      try
      {
        CashRecord record = new CashRecord(Long.parseLong(attributes.getValue("id")));
        if(_mCash==null)
          _mCash = new Vector<CashRecord>();
        _mCash.add(record);
      }
      catch(NumberFormatException e)
      {
        e.printStackTrace();
      }
      return;
    }
////////
    if("notes".equals(name) && _mCash!=null && _mCurrentState==STATE_CASH)
    {
      String sum = attributes.getValue("sum");
      try
      {
        _mCash.lastElement().cash = Float.parseFloat(sum);
      }
      catch(NumberFormatException e)
      {
        e.printStackTrace();
        _mCash.lastElement().cash = 0;
      }
      return;
    }
////////
    if("nominal".equals(name) && _mCurrentState==STATE_CASH)
    {

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
          _mStatuses = new Vector<TerminalStatus>();
        _mStatuses.add(status);
      }
      else if(_mCurrentState == STATE_PAYMENTS)
      {
        String id = attributes.getValue("uid");
        String terminal = attributes.getValue("trm-id");
        if(TextUtils.isEmpty(id) || TextUtils.isEmpty(terminal))
          return;
        Payment payment = new Payment(Long.parseLong(id),Long.parseLong(terminal));
        String text = attributes.getValue("from-amount");
        if(!TextUtils.isEmpty(text))
          try
          {
            payment.setFromAmount(Float.parseFloat(text));
          }
          catch(NumberFormatException e)
          {
            e.printStackTrace();
          }
///////////
        text = attributes.getValue("to-amount");
        if(!TextUtils.isEmpty(text))
          try
          {
            payment.setToAmount(Float.parseFloat(text));
          }
          catch(NumberFormatException e)
          {
            e.printStackTrace();
          }
///////////
        text = attributes.getValue("status");
        if(!TextUtils.isEmpty(text))
          try
          {
            payment.setStatus(Integer.parseInt(text));
          }
          catch(NumberFormatException e)
          {
            e.printStackTrace();
          }
///////////
        text = attributes.getValue("to-prv-id");
        if(!TextUtils.isEmpty(text))
          try
          {
            payment.setProviderId(Long.parseLong(text));
          }
          catch(NumberFormatException e)
          {
            e.printStackTrace();
          }
///////////
        text = attributes.getValue("to-prv-short-name");
        if(!TextUtils.isEmpty(text))
          payment.setProviderName(text);
///////////
        text = attributes.getValue("receipt-date");
        if(!TextUtils.isEmpty(text))
        {
          payment.setDateInTerminal(getPaymentDate(text));
        }
///////////
        text = attributes.getValue("txn-date");
        if(!TextUtils.isEmpty(text))
        {
          payment.setDateInProcessing(getPaymentDate(text));
        }
///////////
        if(_mPayments == null)
          _mPayments = new Vector<Payment>();
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
////////
    if("getTerminalsCash".equals(name))
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

  public Iterable<PaymentsRequest> getPaymentsRequests()
  {
    return _mRequests;
  }

  public static class PaymentsRequest
  {
    public final long queId;
    public final int status;
    public final int result;

    private PaymentsRequest(long queId, int status, int result)
    {
      this.queId = queId;
      this.status = status;
      this.result = result;
    }
  }

  public static class CashRecord
  {
    public long terminalId;
    public float cash;

    public CashRecord(long terminalId)
    {
      this.terminalId = terminalId;
    }

    public CashRecord()
    {

    }
  }
}
