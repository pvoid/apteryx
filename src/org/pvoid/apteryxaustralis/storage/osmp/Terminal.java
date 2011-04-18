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

import android.content.Context;
import org.pvoid.apteryxaustralis.DateFormat;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.storage.IStorage;
import org.pvoid.apteryxaustralis.types.ITerminal;
import org.pvoid.apteryxaustralis.types.InfoLine;
import org.pvoid.apteryxaustralis.types.StatusLine;
import org.pvoid.apteryxaustralis.types.TerminalAction;

import java.util.List;

public class Terminal implements ITerminal
{
  private final static int OSMP_STATE_OK = 0;
  private final static int OSMP_STATE_WARRNING = 2;
  private final static int OSMP_STATE_ERROR = 1;

  private final static int ACTION_REBOOT = 0;
  private final static int ACTION_POWER_OFF = 1;

  private final static int STATE_PRINTER_STACKER_ERROR = 1;// Автомат остановлен из-за ошибок купюроприемника или принтера
  private final static int STATE_INTERFACE_ERROR = 2; //Автомат остановлен из-за ошибки в конфигурации интерфейса.
                                                      // Новый интерфейс загружается с сервера
  private final static int STATE_UPLOADING_UPDATES = 4; // Автомат загружает с сервера обновление приложения
  private final static int STATE_DEVICES_ABSENT = 8; // Автомат остановлен из-за того, что при старте не обнаружено
                                                       // оборудование (купюроприемник или принтер)
  private final static int STATE_WATCHDOG_TIMER = 0x10; // Работает сторожевой таймер
  private final static int STATE_PAPER_COMING_TO_END = 0x20; // В принтере скоро закончится бумага
  private final static int STATE_STACKER_REMOVED = 0x40; // C автомата был снят купюроприемник
  private final static int STATE_ESSENTIAL_ELEMENTS_ERROR = 0x80; // Отсутствуют или неверно заполнены один или
                                                                // несколько реквизитов для терминала
  private final static int STATE_HARDDRIVE_PROBLEMS = 0x100; //256 Проблемы с жестким диском
  private final static int STATE_STOPPED_DUE_BALANCE = 0x200; // Остановлен по сигналу сервера или из-за отсутствия денег на счету агента
  private final static int STATE_HARDWARE_OR_SOFTWARE_PROBLEM  = 0x400; // Остановлен из-за проблем с железом или интерфейса
  private final static int STATE_HAS_SECOND_MONITOR  = 0x800; // Автомат оснащен вторым монитором.
  private final static int STATE_ALTERNATE_NETWORK_USED  = 0x1000; // Автомат использует альтернативную сеть
  private final static int STATE_UNAUTHORIZED_SOFTWARE  = 0x2000; // Используется ПО, вызывающее сбои в работе автомата
  private final static int STATE_PROXY_SERVER  = 0x4000; // Автомат работает через прокси

  private final static int STATE_UPDATING_CONFIGURATION = 0x10000; // Терминал обновляет конфигурацию
  private final static int STATE_UPDATING_NUMBERS = 0x20000; // Терминал обновляет номерные емкости.
  private final static int STATE_UPDATING_PROVIDERS  = 0x40000; // Терминал обновляет список провайдеров.
  private final static int STATE_UPDATING_ADVERT     = 0x80000; // Терминал проверяет и обновляет рекламный плэйлист.
  private final static int STATE_UPDATING_FILES = 0x100000; // Терминал проверяет и обновляет файлы.

  private final static int STATE_FAIR_FTP_IP = 0x200000; // Подменен IP-адрес FTP сервера
  private final static int STATE_ASO_MODIFIED = 0x400000; // Модифицировано приложение АСО.
  private final static int STATE_INTERFACE_MODIFIED = 0x800000; // Модифицирован интерфейс
  private final static int STATE_ASO_ENABLED = 0x1000000; // Монитор АСО выключен.

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

  @Override
  public long getId()
  {
    return tid;
  }

  @Override
  public int getState()
  {
    switch(_mState)
    {
      case OSMP_STATE_OK:
        return ITerminal.STATE_OK;
      case OSMP_STATE_WARRNING:
        return ITerminal.STATE_WARNING;
      default:
        if("OK".equals(printer_state))
          return ITerminal.STATE_ERROR_CRITICAL;
        return ITerminal.STATE_ERROR;
    }
  }


  @Override
  public String getTitle()
  {
    return address;
  }

  @Override
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
    if((ms & STATE_FAIR_FTP_IP) !=0)
      return context.getString(R.string.STATE_FAIR_FTP_IP);
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
        status.append(context.getString(R.string.fullinfo_cash)).append(' ').append(cash);
        break;
      case OSMP_STATE_WARRNING:
        status.append(context.getString(R.string.last_payment))
              .append(' ')
              .append(DateFormat.formatDateSmart(context, lastPayment));
        break;
      case OSMP_STATE_ERROR:
        status.append(context.getString(R.string.last_activity))
              .append(' ')
              .append(DateFormat.formatDateSmart(context,lastActivity));
        break;
    }
    return status.toString();
  }

  @Override
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

  @Override
  public void getInfo(Context context, List<InfoLine> statuses)
  {
    statuses.add(new InfoLine(context.getString(R.string.fullinfo_cash),Integer.toString(cash)));
    statuses.add(new InfoLine(context.getString(R.string.fullinfo_last_payment),DateFormat.formatDateSmart(context, lastPayment)));
    statuses.add(new InfoLine(context.getString(R.string.fullinfo_last_activity),DateFormat.formatDateSmart(context, lastActivity)));
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

  @Override
  public void getActions(Context context, List<TerminalAction> actions)
  {
    actions.add(new TerminalAction(ACTION_REBOOT,context.getString(R.string.reboot)));
    actions.add(new TerminalAction(ACTION_POWER_OFF,context.getString(R.string.switchoff)));
  }

  @Override
  public void runAction(IStorage storage, int action)
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
      return;
    }
//////////
    int result = IStorage.RES_OK;
    switch(action)
    {
      case ACTION_REBOOT:
        result = strg.rebootTerminal(tid,agentId);
        break;
      case ACTION_POWER_OFF:
        result = strg.switchOffTerminal(tid,agentId);
        break;
    }
//////////
    if(result!=IStorage.RES_OK)
    {

    }
  }
}