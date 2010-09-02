package org.pvoid.apteryx.net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.pvoid.apteryx.Utils;
import org.pvoid.apteryx.accounts.Terminal;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class TerminalsProcessData extends DefaultHandler implements Iterable<String>
{
  private final int TAG_NONE   = 0;
  private final int TAG_EXTRA  = 1;
  private final int TAG_RESULT = 2;
  
  private final int EXTRA_UNKNOWN  = 0;
  private final int EXTRA_BALANCE  = 1;
  private final int EXTRA_OVERDRAFT  = 2;
  
  private HashMap<String,Terminal> _Terminals;
  private int _Status;
  private String _AgentId;
  private HashMap<String,Double> _Balances;
  private HashMap<String,Double> _Overdrafts;
  private int _Balance;
  private int _Overdraft;
  
  private int _TagState;
  private int _ExtraState;
  private StringBuilder _Text;
  private int _Count;
  
  public TerminalsProcessData()
  {
    _Terminals = new HashMap<String,Terminal>();
    _Balances = new HashMap<String, Double>();
    _Overdrafts = new HashMap<String, Double>();
    _Text = new StringBuilder();
    _Balance = 0;
    _Overdraft = 0;
    _Count=0;
  }
  
  public void SetAgent(String agentId)
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
  }
  
  static private int getInt(Attributes attributes, String name, int def)
  {
    String value = attributes.getValue(name);
    if(Utils.isEmptyString(value))
      return(def);
    return Integer.parseInt(value);
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
      
      Terminal terminal;
      if(_Terminals.containsKey(tid))
        terminal = _Terminals.get(tid);
      else
      {
        terminal = new Terminal(tid,address);
        _Terminals.put(tid, terminal);
        ++_Count;
      }
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
      terminal.bondsCount= getInt(attributes,"sl");
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
      terminal.agentId         = getString(attributes, "aid");
      terminal.agentName       = getString(attributes, "an");
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
  
  public Iterator<String> iterator()
  {
    return(_Terminals.keySet().iterator());
  }
  
  public Terminal at(String index)
  {
    return _Terminals.get(index);
  }

  public int Count()
  {
    return(_Count);
  }
  
  public double Balance()
  {
    return(_Balance);
  }
  
  public double Overdraft()
  {
    return(_Overdraft);
  }
  
  public double Balance(String agentId)
  {
    if(_Balances.containsKey(agentId))
      return(_Balances.get(agentId));
    return(0);
  }
  
  public void Balance(String agentId, double balance)
  {
    _Balances.put(agentId, new Double(balance));
    _Balance+=balance;
  }
  
  public double Overdraft(String agentId)
  {
    if(_Overdrafts.containsKey(agentId))
      return(_Overdrafts.get(agentId));
    return(0);
  }
  
  public void Overdraft(String agentId, double overdraft)
  {
    _Overdrafts.put(agentId, new Double(overdraft));
    _Overdraft+=overdraft;
  }
  
  public Set<String> Agents()
  {
    return(_Balances.keySet());
  }
  
  public boolean hasAgents()
  {
    return(_Balances.size()!=0);
  }
  
  public void add(Terminal terminal)
  {
    _Terminals.put(terminal.id(), terminal);
  }

  public void Clear()
  {
    _Terminals.clear();
    _Balances.clear();
    _Overdrafts.clear();
    _Balance = 0;
    _Overdraft = 0;
  }
  
  public boolean Success()
  {
    return(_Status==0);
  }
}
