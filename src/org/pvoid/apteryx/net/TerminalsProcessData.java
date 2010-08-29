package org.pvoid.apteryx.net;

import java.util.HashMap;
import java.util.Iterator;
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
  
  protected HashMap<String,Terminal> terminals;
  protected int status;
  private double balance;
  private double overdraft;
  
  private int tagState;
  private int extraState;
  private StringBuilder text;
  private int count;
  
  public TerminalsProcessData()
  {
    terminals = new HashMap<String,Terminal>();
    text = new StringBuilder();
    count=0;
  }
  
  @Override
  public void startDocument()
  {
    tagState = TAG_NONE;
    text.delete(0, text.length());
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
      if(terminals.containsKey(tid))
        terminal = terminals.get(tid);
      else
      {
        terminal = new Terminal(tid,address);
        terminals.put(tid, terminal);
        ++count;
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
      tagState = TAG_RESULT;
    else if(localName.compareToIgnoreCase("extra")==0)
    {
      tagState = TAG_EXTRA;
      String extra_name = attributes.getValue("name");
      if(extra_name.compareToIgnoreCase("balance")==0)
        extraState = EXTRA_BALANCE;
      else if(extra_name.compareToIgnoreCase("overdraft")==0)
        extraState = EXTRA_OVERDRAFT;
      else
        extraState = EXTRA_UNKNOWN;
    }
    else
      tagState = TAG_NONE;
    // не допускаем вложенности
    text.delete(0, text.length());
  }
  
  @Override
  public void characters(char[] ch, int start, int length)
  {
    text.append(ch,start,length);
  }
  
  @Override
  public void endElement(String uri, String localName, String qName)
  {
    switch(tagState)
    {
      case TAG_RESULT:
        status = Integer.parseInt(text.toString());
        break;
      case TAG_EXTRA:
        if(extraState == EXTRA_BALANCE)
        {
          balance = Double.parseDouble(text.toString());
        }
        else if(extraState == EXTRA_OVERDRAFT)
          overdraft = Double.parseDouble(text.toString());
        break;
    }
    tagState = TAG_NONE;
  }
  
  public boolean isEmpty()
  {
    return(terminals.isEmpty());
  }
  
  public Iterator<String> iterator()
  {
    return(terminals.keySet().iterator());
  }
  
  public Terminal at(String index)
  {
    return terminals.get(index);
  }

  public int Count()
  {
    return(count);
  }
  
  public double Balance()
  {
    return(balance);
  }
  
  public double Overdraft()
  {
    return(overdraft);
  }
}
