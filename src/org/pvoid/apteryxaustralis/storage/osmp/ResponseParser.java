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

package org.pvoid.apteryxaustralis.storage.osmp;

import org.pvoid.apteryxaustralis.Utils;
import org.pvoid.apteryxaustralis.accounts.Account;
import org.pvoid.apteryxaustralis.accounts.Group;
import org.pvoid.apteryxaustralis.accounts.Terminal;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

public class ResponseParser extends DefaultHandler
{
  private static final byte STATE_NONE = 0;
  private static final byte STATE_AGENT_INFO = 1;
  private static final byte STATE_AGENTS = 2;

  private Account _mAccount;
  private List<Group> _mGroups;
  private List<Terminal> _mTerminals;
  private int _mState = STATE_NONE;

  private int _mAccountResult;

  public ResponseParser()
  {
  }

  public ResponseParser setAccount(Account account)
  {
    _mAccount = account;
    return this;
  }

  public ResponseParser setGroups(List<Group> groups)
  {
    _mGroups = groups;
    return this;
  }

  public ResponseParser setTerminals(List<Terminal> terminals)
  {
    _mTerminals = terminals;
    return this;
  }

  public int getAccountResult()
  {
    return _mAccountResult;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
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
      terminal.State(getInt(attributes, "rs", Terminal.STATE_ERROR));
      terminal.ms = getInt(attributes,"ms",0);
//////// состояние принтера
      terminal.printer_state = getString(attributes, "rp", "none");
//////// состояние купироприемника
      terminal.cashbin_state = getString(attributes, "rc", "none");
//////// сумма
      terminal.cash = getInt(attributes, "cs");
//////// последняя активность
      terminal.lastActivity = getString(attributes, "lat");
//////// последний платеж
      terminal.lastPayment = getString(attributes, "lpd");
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
    if(Utils.isEmptyString(value))
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
    if(Utils.isEmptyString(value))
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

  static private int getInt(Attributes attributes, String name)
  {
    return(getInt(attributes,name,0));
  }

  static private String getString(Attributes attributes, String name, String def)
  {
    String value = attributes.getValue(name);
    if(Utils.isEmptyString(value))
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
  }
}
