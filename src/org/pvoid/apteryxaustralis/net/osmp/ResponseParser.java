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

package org.pvoid.apteryxaustralis.net.osmp;

import android.text.TextUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class ResponseParser extends DefaultHandler
{
  private static final byte STATE_NONE = 0;
  private static final byte STATE_AGENT_INFO = 1;
  private static final byte STATE_AGENTS = 2;
  private static final byte STATE_BALANCE = 3;
  private static final byte STATE_BALANCE_VALUE = 4;
  private static final byte STATE_BALANCE_OVERDRAFT = 5;

  private final Account _mAccount = new Account();
  private final List<Group> _mGroups = new ArrayList<Group>();
  private int _mGroupIndex;
  private final List<Terminal> _mTerminals = new ArrayList<Terminal>();
  private int _mState = STATE_NONE;
  SimpleDateFormat _mDateFormat;

  private final StringBuilder _mText = new StringBuilder();

  private int _mAccountResult;

  public ResponseParser()
  {
  }

  public int getAccountResult()
  {
    return _mAccountResult;
  }

  @Override
  public void startDocument() throws SAXException
  {
    _mGroupIndex = 0;
    _mDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    TimeZone timezone = TimeZone.getTimeZone("UTC");
    _mDateFormat.setTimeZone(timezone);
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
    _mText.delete(0,_mText.length());

    if("getAgentInfo".equals(localName))
    {
      _mState = STATE_AGENT_INFO;
      _mAccountResult = Integer.parseInt(attributes.getValue("result"));
      return;
    }
////////
    if("getAgents".equals(localName))
    {
      _mState = STATE_AGENTS;
      return;
    }
///////
    if("getBalance".equals(localName))
    {
      _mState = STATE_BALANCE;
      return;
    }
///////
    if("balance".equals(localName) && _mState==STATE_BALANCE)
    {
      _mState = STATE_BALANCE_VALUE;
      return;
    }
///////
    if("overdraft".equals(localName) && _mState==STATE_BALANCE)
    {
      _mState = STATE_BALANCE_OVERDRAFT;
      return;
    }
///////
    if("agent".equals(localName) && _mState==STATE_AGENT_INFO && _mAccount!= null)
    {
      _mAccount.id = Long.parseLong(attributes.getValue("id"));
      _mAccount.title = attributes.getValue("name");
      return;
    }
///////
    if("row".equals(localName) && _mState==STATE_AGENTS && _mGroups !=null)
    {
      Group group = new Group();
      group.id = Long.parseLong(attributes.getValue("agt_id"));
      group.name = attributes.getValue("agt_name");
      _mGroups.add(group);
    }
////// старый протокол. вытаскиваем терминалы
    if("term".equals(localName) && _mTerminals!=null)
    {
      long id = Long.parseLong(attributes.getValue("tid"));
      Terminal terminal = new Terminal(id,attributes.getValue("addr"));
//////// статус
      terminal.State(getInt(attributes, "rs", 0));
      terminal.ms = getInt(attributes,"ms",0);
//////// состояние принтера
      terminal.printer_state = getString(attributes, "rp", "none");
//////// состояние купироприемника
      terminal.cashbin_state = getString(attributes, "rc", "none");
//////// сумма
      terminal.cash = getFloat(attributes, "cs");
//////// последняя активность
      try
      {
        terminal.lastActivity = _mDateFormat.parse(getString(attributes, "lat")).getTime()-4*60*60*1000;
      }
      catch(ParseException e)
      {
        e.printStackTrace();
        terminal.lastActivity = 0;
      }
//////// последний платеж
      try
      {
        terminal.lastPayment = _mDateFormat.parse(getString(attributes, "lpd")).getTime()-4*60*60*1000;
      }
      catch(ParseException e)
      {
        e.printStackTrace();
        terminal.lastPayment = 0;
      }
//////// Число купюр
      terminal.bondsCount= getInt(attributes, "nc");
//////// Баланс сим карты
      terminal.balance= getString(attributes,"ss");
//////// Уровень сигнала сим карты
      terminal.signalLevel= getInt(attributes,"sl");
//////// Версия софта
      terminal.softVersion = getString(attributes, "csoft");
//////// Модель принтера и купюроприемника
      terminal.printerModel    = getString(attributes,"pm");
      terminal.cashbinModel    = getString(attributes,"dm");
//////// дынные по купюрам
      terminal.bonds10count    = getInt(attributes,"b_co_10");
      terminal.bonds50count    = getInt(attributes,"b_co_50");
      terminal.bonds100count   = getInt(attributes,"b_co_100");
      terminal.bonds500count   = getInt(attributes,"b_co_500");
      terminal.bonds1000count  = getInt(attributes,"b_co_1000");
      terminal.bonds5000count  = getInt(attributes,"b_co_5000");
      terminal.bonds10000count = getInt(attributes,"b_co_10000");
//////// чуть статистики
      terminal.paysPerHour     = getString(attributes,"pays_per_hour");
//////// агент
      terminal.agentId         = getLong(attributes, "aid",0);
      terminal.agentName       = getString(attributes, "an");
//////// и добавим
      _mTerminals.add(terminal);
    }
  }

  static private int getInt(Attributes attributes, String name, int def)
  {
    String value = attributes.getValue(name);
    if(TextUtils.isEmpty(value))
      return(def);
    try
    {
      return Integer.parseInt(value);
    }
    catch(NumberFormatException e)
    {
      return def;
    }
  }

  static private long getLong(Attributes attributes, String name, int def)
  {
    String value = attributes.getValue(name);
    if(TextUtils.isEmpty(value))
      return(def);
    try
    {
      return Long.parseLong(value);
    }
    catch(NumberFormatException e)
    {
      return def;
    }
  }

  static private float getFloat(Attributes attributes, String name, float def)
  {
    String value = attributes.getValue(name);
    if(TextUtils.isEmpty(value))
      return def;
    try
    {
      return Float.parseFloat(value);
    }
    catch(NumberFormatException e)
    {
      return def;
    }
  }

  static private float getFloat(Attributes attributes, String name)
  {
    return getFloat(attributes,name,0);
  }

  static private int getInt(Attributes attributes, String name)
  {
    return(getInt(attributes,name,0));
  }

  static private String getString(Attributes attributes, String name, String def)
  {
    String value = attributes.getValue(name);
    if(TextUtils.isEmpty(value))
      return(def);
    return value;
  }

  static private String getString(Attributes attributes, String name)
  {
    return(getString(attributes, name, "unknown"));
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException
  {
    if("getAgentInfo".equals(localName))
    {
      _mState = STATE_NONE;
      return;
    }
////////
    if("getAgents".equals(localName))
    {
      _mState = STATE_NONE;
      return;
    }
////////
    if("getBalance".equals(localName))
    {
      _mState = STATE_NONE;
      ++_mGroupIndex;
      return;
    }
///////
    if("balance".equals(localName))
    {
      _mState = STATE_BALANCE;
      if(_mGroups!=null && _mText.length()!=0 && _mGroupIndex <_mGroups.size())
        try
        {
          _mGroups.get(_mGroupIndex).balance = Double.parseDouble(_mText.toString());
        }
        catch(NumberFormatException e)
        {
          e.printStackTrace();
        }
      return;
    }
///////
    if("overdraft".equals(localName))
    {
      _mState = STATE_BALANCE;
      if(_mGroups!=null && _mText.length()!=0 && _mGroupIndex <_mGroups.size())
        try
        {
          _mGroups.get(_mGroupIndex).overdraft = Double.parseDouble(_mText.toString());
        }
        catch(NumberFormatException e)
        {
          e.printStackTrace();
        }
      //noinspection UnnecessaryReturnStatement
      return;
    }
  }

  @Override
  public void characters(char[] ch, int start, int length)
  {
    _mText.append(ch,start,length);
  }

  public Account getAccount()
  {
    return _mAccount;
  }

  public List<Group> getGroups()
  {
    return _mGroups;
  }

  public List<Terminal> getTerminals()
  {
    return _mTerminals;
  }

  public void addGroup(long id)
  {
    Group group = new Group();
    group.id = id;
    _mGroups.add(group);
  }

  public static class Account
  {
    public long id;
    public String title;
  }

  public static class Group
  {
    public long id;
    public String name;
    public double balance;
    public double overdraft;
  }

  public static class Terminal
  {

    private int _mState;
    public String printer_state;
    public String cashbin_state;
    public float cash;
    public long lastActivity;
    public long lastPayment;
    public int bondsCount;
    public String balance;
    public int signalLevel;
    public String softVersion;
    public String printerModel;
    public String cashbinModel;
    public int bonds10count;
    public int bonds50count;
    public int bonds100count;
    public int bonds500count;
    public int bonds1000count;
    public int bonds5000count;
    public int bonds10000count;
    public String paysPerHour;
    public long agentId;
    public String agentName;
    public int ms;

    protected final String address;
    protected final long tid;

    public Terminal(long id, String address)
    {
      this.tid = id;
      this.address = address;
    }

    public String Address()
    {
      return address;
    }

    public long id()
    {
      return(tid);
    }

    public void State(int state)
    {
      this._mState = state;
    }

    public int State()
    {
      return(_mState);
    }

    @Override
    public String toString()
    {
      return address;
    }

    /*public void runAction(IStorage storage, int action, ICommandResult resultHandler)
    {
      OsmpStorage strg;
  //////////
      try
      {
        strg = (OsmpStorage) storage;
      }
      catch(ClassCastException e)
      {
        e.printStackTrace();
        resultHandler.onCommandResult(false,R.string.cant_obtain_storage,address);
        return;
      }
  //////////
      switch(action)
      {
        case ACTION_REBOOT:
          (new RebootTerminalTask(address,strg,resultHandler)).execute(tid, agentId);
          break;
        case ACTION_POWER_OFF:
          //result = strg.switchOffTerminal(tid,agentId);
          break;
      }
    }*/

    /*private static class RebootTerminalTask extends AsyncTask<Long,Void,Integer>
    {
      private final String _mName;
      private final ICommandResult _mHandler;
      private final OsmpStorage _mStorage;

      public RebootTerminalTask(String name, OsmpStorage storage, ICommandResult handler)
      {
        _mName = name;
        _mHandler = handler;
        _mStorage = storage;
      }

      @Override
      protected Integer doInBackground(Long... longs)
      {
        return _mStorage.rebootTerminal(longs[0],longs[1]);
      }

      @Override
      protected void onPostExecute(Integer result)
      {
        /*if(result == IStorage.RES_OK)
          _mHandler.onCommandResult(true,R.string.reboot_command_sended,_mName);
        else
          _mHandler.onCommandResult(false,R.string.network_error,_mName);
      }
    }*/
  }
}
