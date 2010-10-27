package org.pvoid.apteryxaustralis.accounts;

import java.util.ArrayList;
import java.util.List;

import org.pvoid.apteryxaustralis.net.IResponseParser;
import org.xml.sax.Attributes;

public class ReportsSection implements IResponseParser
{
  private final int STATE_NONE = 0;
  private final int STATE_TERMINALS_STATUSES = 1;
  
  private int _CurrentState;
  private ArrayList<TerminalStatus> _Statuses = null;
  
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
      String value = attributes.getValue("trm_id");
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
      value = attributes.getValue("agt_id");
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

  public List<TerminalStatus> getTerminalsStatus()
  {
    return(_Statuses);
  }
}
