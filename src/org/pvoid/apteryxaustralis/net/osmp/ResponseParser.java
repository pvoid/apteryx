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

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.TextFormat;
import org.pvoid.apteryxaustralis.storage.ICommandResult;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpStorage;
import org.pvoid.apteryxaustralis.types.InfoLine;
import org.pvoid.apteryxaustralis.types.StatusLine;
import org.pvoid.apteryxaustralis.types.TerminalAction;
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

  private StringBuilder _mText = new StringBuilder();

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
    TimeZone timezone = TimeZone.getTimeZone("Europe/Moscow");
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
      Log.v(ResponseParser.class.getSimpleName(),"ms value: " + attributes.getValue("ms") + " parsed "+terminal.ms);
//////// состояние принтера
      terminal.printer_state = getString(attributes, "rp", "none");
//////// состояние купироприемника
      terminal.cashbin_state = getString(attributes, "rc", "none");
//////// сумма
      terminal.cash = getInt(attributes, "cs");
//////// последняя активность
      try
      {
        terminal.lastActivity = _mDateFormat.parse(getString(attributes, "lat")).getTime();
      }
      catch(ParseException e)
      {
        e.printStackTrace();
        terminal.lastActivity = 0;
      }
//////// последний платеж
      try
      {
        terminal.lastPayment = _mDateFormat.parse(getString(attributes, "lpd")).getTime();
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
    protected final static int OSMP_STATE_OK = 0;
    protected final static int OSMP_STATE_WARRNING = 2;
    protected final static int OSMP_STATE_ERROR = 1;

    private final static int ACTION_REBOOT = 0;
    private final static int ACTION_POWER_OFF = 1;
    final static int STATE_PRINTER_STACKER_ERROR = 1;// Автомат остановлен из-за ошибок купюроприемника или принтера
    final static int STATE_INTERFACE_ERROR = 2; //Автомат остановлен из-за ошибки в конфигурации интерфейса.
    // Новый интерфейс загружается с сервера
    final static int STATE_UPLOADING_UPDATES = 4; // Автомат загружает с сервера обновление приложения
    final static int STATE_DEVICES_ABSENT = 8; // Автомат остановлен из-за того, что при старте не обнаружено
    // оборудование (купюроприемник или принтер)
    final static int STATE_WATCHDOG_TIMER = 0x10; // Работает сторожевой таймер
    final static int STATE_PAPER_COMING_TO_END = 0x20; // В принтере скоро закончится бумага
    final static int STATE_STACKER_REMOVED = 0x40; // C автомата был снят купюроприемник
    final static int STATE_ESSENTIAL_ELEMENTS_ERROR = 0x80; // Отсутствуют или неверно заполнены один или
    // несколько реквизитов для терминала
    final static int STATE_HARDDRIVE_PROBLEMS = 0x100; //256 Проблемы с жестким диском
    final static int STATE_STOPPED_DUE_BALANCE = 0x200; // Остановлен по сигналу сервера или из-за отсутствия денег на счету агента
    final static int STATE_HARDWARE_OR_SOFTWARE_PROBLEM  = 0x400; // Остановлен из-за проблем с железом или интерфейса
    final static int STATE_HAS_SECOND_MONITOR  = 0x800; // Автомат оснащен вторым монитором.
    final static int STATE_ALTERNATE_NETWORK_USED  = 0x1000; // Автомат использует альтернативную сеть
    final static int STATE_UNAUTHORIZED_SOFTWARE  = 0x2000; // Используется ПО, вызывающее сбои в работе автомата
    final static int STATE_PROXY_SERVER  = 0x4000; // Автомат работает через прокси
    final static int STATE_UPDATING_CONFIGURATION = 0x10000; // Терминал обновляет конфигурацию
    final static int STATE_UPDATING_NUMBERS = 0x20000; // Терминал обновляет номерные емкости.
    final static int STATE_UPDATING_PROVIDERS  = 0x40000; // Терминал обновляет список провайдеров.
    final static int STATE_UPDATING_ADVERT     = 0x80000; // Терминал проверяет и обновляет рекламный плэйлист.
    final static int STATE_UPDATING_FILES = 0x100000; // Терминал проверяет и обновляет файлы.
    final static int STATE_FAIR_FTP_IP = 0x200000; // Подменен IP-адрес FTP сервера
    final static int STATE_ASO_MODIFIED = 0x400000; // Модифицировано приложение АСО.
    final static int STATE_INTERFACE_MODIFIED = 0x800000; // Модифицирован интерфейс
    final static int STATE_ASO_ENABLED = 0x1000000; // Монитор АСО выключен.

    private int _mState;
    public String printer_state;
    public String cashbin_state;
    public int cash;
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

    protected String address;
    protected long tid;

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

    public void update(Terminal terminal)
    {
      _mState = terminal.State();
      printer_state = terminal.printer_state;
      cashbin_state = terminal.cashbin_state;
      cash = terminal.cash;
      lastActivity = terminal.lastActivity;
      lastPayment = terminal.lastPayment;
      bondsCount = terminal.bondsCount;
      balance = terminal.balance;
      signalLevel = terminal.signalLevel;
      softVersion = terminal.softVersion;
      printerModel = terminal.printerModel;
      cashbinModel = terminal.cashbinModel;
      bonds10count = terminal.bonds10count;
      bonds50count = terminal.bonds50count;
      bonds100count = terminal.bonds100count;
      bonds500count = terminal.bonds500count;
      bonds1000count = terminal.bonds1000count;
      bonds5000count = terminal.bonds5000count;
      bonds10000count = terminal.bonds10000count;
      paysPerHour = terminal.paysPerHour;
      agentId = terminal.agentId;
      agentName = terminal.agentName;
      address = terminal.address;
      ms = terminal.ms;
    }

    @Override
    public String toString()
    {
      return address;
    }

    public long getId()
    {
      return tid;
    }

    public int getState()
    {
      /*switch(_mState)
      {
        case OSMP_STATE_OK:
          return ITerminal.STATE_OK;
        case OSMP_STATE_WARRNING:
          return ITerminal.STATE_WARNING;
        default:
          if("OK".equals(printer_state) || System.currentTimeMillis() - lastActivity>60*60*1000)
            return ITerminal.STATE_ERROR_CRITICAL;
          return ITerminal.STATE_ERROR;
      }*/
      return 0;
    }


    public String getTitle()
    {
      return address;
    }

    public String getStatus(Context context)
    {
  ////////// Ошибки принтера или купюроприемника вперед
      if(!"OK".equals(cashbin_state))
        return context.getString(R.string.cachebin) + ": " + cashbin_state;

      if(!"OK".equals(printer_state))
        return context.getString(R.string.printer) + ": " + printer_state;
  ////////// Потом проверим флаги. Сначала ошибки железа
      if((ms & STATE_PRINTER_STACKER_ERROR) !=0)
        return context.getString(R.string.STATE_PRINTER_STACKER_ERROR);
      if((ms & STATE_STACKER_REMOVED) !=0)
        return context.getString(R.string.STATE_STACKER_REMOVED);
      if((ms & STATE_HARDDRIVE_PROBLEMS) !=0)
        return context.getString(R.string.STATE_HARDDRIVE_PROBLEMS);
      if((ms & STATE_DEVICES_ABSENT) !=0)
        return context.getString(R.string.STATE_DEVICES_ABSENT);
      if((ms & STATE_HARDWARE_OR_SOFTWARE_PROBLEM) !=0)
        return context.getString(R.string.STATE_HARDWARE_OR_SOFTWARE_PROBLEM);
  ////////// Потом вероятные угрозы
      if((ms & STATE_ASO_MODIFIED) !=0)
        return context.getString(R.string.STATE_ASO_MODIFIED);
      if((ms & STATE_INTERFACE_MODIFIED) !=0)
        return context.getString(R.string.STATE_INTERFACE_MODIFIED);
      /*if((ms & STATE_FAIR_FTP_IP) !=0)
        return context.getString(R.string.STATE_FAIR_FTP_IP);*/
      if((ms & STATE_UNAUTHORIZED_SOFTWARE) !=0)
        return context.getString(R.string.STATE_UNAUTHORIZED_SOFTWARE);
  ////////// Ошибки настройки
      if((ms & STATE_INTERFACE_ERROR) !=0)
        return context.getString(R.string.STATE_INTERFACE_ERROR);
      if((ms & STATE_STOPPED_DUE_BALANCE) !=0)
        return context.getString(R.string.STATE_STOPPED_DUE_BALANCE);
  ///////// Ну и прочее
      if((ms & STATE_PAPER_COMING_TO_END) !=0)
        return context.getString(R.string.STATE_PAPER_COMING_TO_END);

      StringBuilder status = new StringBuilder();
      switch(_mState)
      {
        case OSMP_STATE_OK:
          status.append(context.getString(R.string.fullinfo_cash)).append(' ').append(TextFormat.formatMoney(cash, true));
          break;
        case OSMP_STATE_WARRNING:
          status.append(context.getString(R.string.last_payment))
                .append(' ')
                .append(TextFormat.formatDateSmart(context, lastPayment));
          break;
        case OSMP_STATE_ERROR:
          status.append(context.getString(R.string.last_activity))
                .append(' ')
                .append(TextFormat.formatDateSmart(context, lastActivity));
          break;
      }
      return status.toString();
    }

    public void getStatuses(Context context, List<StatusLine> statuses)
    {
      if(!"OK".equals(printer_state))
        statuses.add(new StatusLine(printer_state,StatusLine.STATE_ERROR));

      if(!"OK".equals(cashbin_state))
        statuses.add(new StatusLine(cashbin_state,StatusLine.STATE_ERROR));

      /*if(ms & STATE_PRINTER_STACKER_ERROR != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_PRINTER_STACKER_ERROR)));*/

      if((ms & STATE_INTERFACE_ERROR) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_INTERFACE_ERROR),StatusLine.STATE_ERROR));

      if((ms & STATE_UPLOADING_UPDATES) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_UPLOADING_UPDATES),StatusLine.STATE_OK));

      if((ms & STATE_DEVICES_ABSENT) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_DEVICES_ABSENT),StatusLine.STATE_ERROR));

      if((ms & STATE_WATCHDOG_TIMER) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_WATCHDOG_TIMER),StatusLine.STATE_OK));

      if((ms & STATE_PAPER_COMING_TO_END) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_PAPER_COMING_TO_END),StatusLine.STATE_ERROR));

      if((ms & STATE_STACKER_REMOVED) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_STACKER_REMOVED),StatusLine.STATE_ERROR));

      if((ms & STATE_ESSENTIAL_ELEMENTS_ERROR) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_ESSENTIAL_ELEMENTS_ERROR),StatusLine.STATE_ERROR));

      if((ms & STATE_HARDDRIVE_PROBLEMS) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_HARDDRIVE_PROBLEMS),StatusLine.STATE_ERROR));

      if((ms & STATE_STOPPED_DUE_BALANCE) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_STOPPED_DUE_BALANCE),StatusLine.STATE_ERROR));

      if((ms & STATE_HARDWARE_OR_SOFTWARE_PROBLEM) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_HARDWARE_OR_SOFTWARE_PROBLEM),StatusLine.STATE_ERROR));

      if((ms & STATE_HAS_SECOND_MONITOR) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_HAS_SECOND_MONITOR),StatusLine.STATE_OK));

      if((ms & STATE_ALTERNATE_NETWORK_USED) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_ALTERNATE_NETWORK_USED),StatusLine.STATE_ERROR));

      if((ms & STATE_UNAUTHORIZED_SOFTWARE) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_UNAUTHORIZED_SOFTWARE),StatusLine.STATE_ERROR));

      if((ms & STATE_PROXY_SERVER) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_PROXY_SERVER),StatusLine.STATE_OK));

      if((ms & STATE_UPDATING_CONFIGURATION) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_UPDATING_CONFIGURATION),StatusLine.STATE_OK));

      if((ms & STATE_UPDATING_NUMBERS) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_UPDATING_NUMBERS),StatusLine.STATE_OK));

      if((ms & STATE_UPDATING_PROVIDERS) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_UPDATING_PROVIDERS),StatusLine.STATE_OK));

      if((ms & STATE_UPDATING_ADVERT) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_UPDATING_ADVERT),StatusLine.STATE_OK));

      if((ms & STATE_UPDATING_FILES) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_UPDATING_FILES),StatusLine.STATE_OK));

      if((ms & STATE_FAIR_FTP_IP) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_FAIR_FTP_IP),StatusLine.STATE_ERROR));

      if((ms & STATE_ASO_MODIFIED) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_ASO_MODIFIED),StatusLine.STATE_ERROR));

      if((ms & STATE_INTERFACE_MODIFIED) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_INTERFACE_MODIFIED),StatusLine.STATE_ERROR));

      if((ms & STATE_ASO_ENABLED) != 0)
        statuses.add(new StatusLine(context.getString(R.string.STATE_ASO_ENABLED),StatusLine.STATE_ERROR));
    }

    public void getInfo(Context context, List<InfoLine> statuses)
    {
      statuses.add(new InfoLine(context.getString(R.string.fullinfo_cash),TextFormat.formatMoney(cash,true)));
      statuses.add(new InfoLine(context.getString(R.string.fullinfo_last_payment), TextFormat.formatDateSmart(context, lastPayment)));
      statuses.add(new InfoLine(context.getString(R.string.fullinfo_last_activity), TextFormat.formatDateSmart(context, lastActivity)));
      statuses.add(new InfoLine(context.getString(R.string.fullinfo_pays_per_hour),paysPerHour));
      statuses.add(new InfoLine(context.getString(R.string.fullinfo_balance),balance));
      statuses.add(new InfoLine(context.getString(R.string.fullinfo_signal_level),Integer.toString(signalLevel)));

      statuses.add(new InfoLine(context.getString(R.string.fullinfo_soft_version),softVersion));

      statuses.add(new InfoLine(context.getString(R.string.fullinfo_bonds),Integer.toString(bondsCount)));
      statuses.add(new InfoLine(context.getString(R.string.fullinfo_bonds10),Integer.toString(bonds10count)));
      statuses.add(new InfoLine(context.getString(R.string.fullinfo_bonds50),Integer.toString(bonds50count)));
      statuses.add(new InfoLine(context.getString(R.string.fullinfo_bonds100),Integer.toString(bonds100count)));
      statuses.add(new InfoLine(context.getString(R.string.fullinfo_bonds500),Integer.toString(bonds500count)));
      statuses.add(new InfoLine(context.getString(R.string.fullinfo_bonds1000),Integer.toString(bonds1000count)));
      statuses.add(new InfoLine(context.getString(R.string.fullinfo_bonds5000),Integer.toString(bonds5000count)));

      statuses.add(new InfoLine(context.getString(R.string.fullinfo_printer),printerModel));
      statuses.add(new InfoLine(context.getString(R.string.fullinfo_cashbin),cashbinModel));
    }

    public void getActions(Context context, List<TerminalAction> actions)
    {
      actions.add(new TerminalAction(ACTION_REBOOT,context.getString(R.string.reboot)));
      //actions.add(new TerminalAction(ACTION_POWER_OFF,context.getString(R.string.switchoff)));
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

    public int getCash()
    {
      return cash;
    }

    private static class RebootTerminalTask extends AsyncTask<Long,Void,Integer>
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
          _mHandler.onCommandResult(false,R.string.network_error,_mName);*/
      }
    }
  }
}
