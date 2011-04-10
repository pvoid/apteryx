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

package org.pvoid.apteryxaustralis.net;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.pvoid.apteryxaustralis.Utils;
import org.pvoid.apteryxaustralis.accounts.Group;
import org.pvoid.apteryxaustralis.accounts.Terminal;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class TerminalsProcessData extends DefaultHandler implements Iterable<Long>
{
  private final int TAG_NONE   = 0;
  private final int TAG_EXTRA  = 1;
  private final int TAG_RESULT = 2;
  
  private final int EXTRA_UNKNOWN  = 0;
  private final int EXTRA_BALANCE  = 1;
  private final int EXTRA_OVERDRAFT  = 2;
  
  private HashMap<Long,Terminal> _Terminals;
  private int _Status;
  private long _AgentId;
  private HashMap<Long,Double> _Balances;
  private HashMap<Long,Double> _Overdrafts;
  private HashMap<Long,String> _Agents;
  private HashMap<Long,Integer> _Cash;
  private int _Balance;
  private int _Overdraft;
  private long[] _AgentsFilter;
  
  private int _TagState;
  private int _ExtraState;
  private StringBuilder _Text;
  private boolean _IsEmpty;
  
  public TerminalsProcessData()
  {
    _Terminals = new HashMap<Long,Terminal>();
    _Balances = new HashMap<Long, Double>();
    _Overdrafts = new HashMap<Long, Double>();
    _Cash = new HashMap<Long, Integer>();
    _Agents = new HashMap<Long, String>();
    _Text = new StringBuilder();
    _Balance = 0;
    _Overdraft = 0;
    _IsEmpty = true;
  }
  
  public void SetAgent(long agentId)
  {
    _AgentId = agentId;
    _Balance = 0;
    _Overdraft = 0;
  }
  
  @Override
  public void startDocument()
  {
    _TagState = TAG_NONE;
    _Text.delete(0, _Text.length());
    _IsEmpty = false;
  }
  
  static private int getInt(Attributes attributes, String name, int def)
  {
    String value = attributes.getValue(name);
    if(Utils.isEmptyString(value))
      return(def);
    return Integer.parseInt(value);
  }
  
  static private long getLong(Attributes attributes, String name, int def)
  {
    String value = attributes.getValue(name);
    if(Utils.isEmptyString(value))
      return(def);
    return Long.parseLong(value);
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
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
    if(localName.compareToIgnoreCase("term")==0)
    {
      String tid = attributes.getValue("tid");
      String address = attributes.getValue("addr");
      
      long agentId = getLong(attributes, "aid",0);
      
      if(_AgentsFilter==null || Arrays.binarySearch(_AgentsFilter, agentId)>=0)
      {
        Terminal terminal;
        terminal = new Terminal(Long.parseLong(tid),address);
        // статус
        terminal.State(getInt(attributes, "rs", Terminal.STATE_ERROR));
        // состояние принтера
        terminal.printer_state = getString(attributes, "rp", "none");
        // состояние купироприемника
        terminal.cashbin_state = getString(attributes, "rc", "none");
        // сумма
        terminal.cash = getInt(attributes, "cs");
        // последняя активность
        terminal.lastActivity = getString(attributes, "lat");
        // последний платеж
        terminal.lastPayment = getString(attributes, "lpd");
        // Число купюр
        terminal.bondsCount= getInt(attributes, "nc");
        // Баланс сим карты
        terminal.balance= getString(attributes,"ss");
        // Уровень сигнала сим карты
        terminal.signalLevel= getInt(attributes,"sl");
        // Версия софта
        terminal.softVersion = getString(attributes, "csoft");
        // Модель принтера
        terminal.printerModel    = getString(attributes,"pm");
        terminal.cashbinModel    = getString(attributes,"dm");
        terminal.bonds10count    = getInt(attributes,"b_co_10");
        terminal.bonds50count    = getInt(attributes,"b_co_50");
        terminal.bonds100count   = getInt(attributes,"b_co_100");
        terminal.bonds500count   = getInt(attributes,"b_co_500");
        terminal.bonds1000count  = getInt(attributes,"b_co_1000");
        terminal.bonds5000count  = getInt(attributes,"b_co_5000");
        terminal.bonds10000count = getInt(attributes,"b_co_10000");
        terminal.paysPerHour     = getString(attributes,"pays_per_hour");
        terminal.agentId         = agentId;
        terminal.agentName       = getString(attributes, "an");
        
        _Terminals.put(terminal.id(), terminal);
        _Agents.put(terminal.agentId, terminal.agentName);
        int cash = 0;
        if(_Cash.containsKey(agentId))
           cash = _Cash.get(agentId);
        cash+=terminal.cash;
        _Cash.put(agentId, cash);
      }
    }
    else if(localName.compareToIgnoreCase("result-code")==0)
      _TagState = TAG_RESULT;
    else if(localName.compareToIgnoreCase("extra")==0)
    {
      _TagState = TAG_EXTRA;
      String extra_name = attributes.getValue("name");
      if(extra_name.compareToIgnoreCase("balance")==0)
        _ExtraState = EXTRA_BALANCE;
      else if(extra_name.compareToIgnoreCase("overdraft")==0)
        _ExtraState = EXTRA_OVERDRAFT;
      else
        _ExtraState = EXTRA_UNKNOWN;
    }
    else
      _TagState = TAG_NONE;
    // не допускаем вложенности
    _Text.delete(0, _Text.length());
  }
 
  @Override
  public void characters(char[] ch, int start, int length)
  {
    _Text.append(ch,start,length);
  }
  
  @Override
  public void endElement(String uri, String localName, String qName)
  {
    switch(_TagState)
    {
      case TAG_RESULT:
        _Status = Integer.parseInt(_Text.toString());
        break;
      case TAG_EXTRA:
        if(_ExtraState == EXTRA_BALANCE)
        {
          Double balance = new Double(_Text.toString());
          _Balance+=balance;
          _Balances.put(_AgentId,balance);
        }
        else if(_ExtraState == EXTRA_OVERDRAFT)
        {
          Double overdraft = new Double(_Text.toString());
          _Overdraft+=overdraft;
          _Overdrafts.put(_AgentId,overdraft);
        }
        break;
    }
    _TagState = TAG_NONE;
  }
  
  public boolean isEmpty()
  {
    return(_Terminals.isEmpty());
  }
  
  public Iterator<Long> iterator()
  {
    return(_Terminals.keySet().iterator());
  }
  
  public Terminal at(Long index)
  {
    return _Terminals.get(index);
  }

  public double Balance()
  {
    return(_Balance);
  }
  
  public double Overdraft()
  {
    return(_Overdraft);
  }
  
  public double Balance(long agentId)
  {
    if(_Balances.containsKey(agentId))
      return(_Balances.get(agentId));
    return(0);
  }
  
  public void Balance(long agentId, double balance)
  {
    _Balances.put(agentId, new Double(balance));
    _Balance+=balance;
  }
  
  public double Overdraft(long agentId)
  {
    if(_Overdrafts.containsKey(agentId))
      return(_Overdrafts.get(agentId));
    return(0);
  }
  
  public void Overdraft(long agentId, double overdraft)
  {
    _Overdrafts.put(agentId, new Double(overdraft));
    _Overdraft+=overdraft;
  }
  
  public Set<Long> Accounts()
  {
    return(_Balances.keySet());
  }
  
  public HashMap<Long, String> Agents()
  {
    return(_Agents); 
  }
  
  public int AgentCash(Long agentId)
  {
    if(_Cash.containsKey(agentId))
      return(_Cash.get(agentId));
    return(0);
  }
  
  public boolean hasAccounts()
  {
    return(!_IsEmpty);
  }
  
  public void add(Terminal terminal)
  {
    _Terminals.put(terminal.id(), terminal);
    _Agents.put(terminal.agentId, terminal.agentName);
    int cash = 0;
    if(_Cash.containsKey(terminal.agentId))
      cash = _Cash.get(terminal.agentId);
    cash+=terminal.cash;
    _Cash.put(terminal.agentId, cash);
    _IsEmpty = false;
  }

  public void Clear()
  {
    _Terminals.clear();
    _Balances.clear();
    _Overdrafts.clear();
    _Cash.clear();
    _Balance = 0;
    _Overdraft = 0;
    _Agents.clear();
    _Status = 0;
    _IsEmpty = true;
  }

  public void SetAgentsFilter(List<Group> groups)
  {
    if(groups ==null)
    {
      _AgentsFilter = null;
      return;
    }
    
    _AgentsFilter = new long[groups.size()];
    int index = 0;
    for(Group group : groups)
    {
      _AgentsFilter[index] = group.id;
      index++;
    }
    Arrays.sort(_AgentsFilter);
  }
  
  public void SetNetworkError()
  {
    _Status = -1;
  }
  
  public boolean Success()
  {
    return(_Status==0);
  }
  
  public int Status()
  {
    return(_Status);
  }
}
