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

import android.os.Parcel;
import android.os.Parcelable;

public class Terminal// implements Parcelable
{
  public final static int STATE_OK = 0;
  public final static int STATE_WARRNING = 2;
  public final static int STATE_ERROR = 1;
  
  private int state;
  public String printer_state;
  public String cashbin_state;
  public String lpd;
  public int cash;
  public String lastActivity;
  public String lastPayment;
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
    this.lpd = "";
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
    this.state = state;
  }
  
  public int State()
  {
    return(state);
  }
  
  /*@Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeLong(tid);
    dest.writeString(address);
    dest.writeInt(state);
    dest.writeString(printer_state);
    dest.writeString(cashbin_state);
    dest.writeString(lpd);
    dest.writeInt(cash);
    dest.writeString(lastActivity);
    dest.writeString(lastPayment);
    dest.writeInt(bondsCount);
    dest.writeString(balance);
    dest.writeInt(signalLevel);
    dest.writeString(softVersion);
    dest.writeString(printerModel);
    dest.writeString(cashbinModel);
    dest.writeInt(bonds10count);
    dest.writeInt(bonds50count);
    dest.writeInt(bonds100count);
    dest.writeInt(bonds500count);
    dest.writeInt(bonds1000count);
    dest.writeInt(bonds5000count);
    dest.writeInt(bonds10000count);
    dest.writeString(paysPerHour);
    dest.writeLong(agentId);
    dest.writeString(agentName);
  }
  
  public static final Parcelable.Creator<Terminal> CREATOR = new Parcelable.Creator<Terminal>()
  {
    @Override
    public Terminal createFromParcel(Parcel src)
    {
      Terminal terminal = new Terminal(src.readLong(), src.readString());
      terminal.State(src.readInt());
      terminal.printer_state = src.readString();
      terminal.cashbin_state = src.readString();
      terminal.lpd = src.readString();
      terminal.cash = src.readInt();
      terminal.lastActivity = src.readString();
      terminal.lastPayment = src.readString();
      terminal.bondsCount = src.readInt();
      terminal.balance = src.readString();
      terminal.signalLevel = src.readInt();
      terminal.softVersion = src.readString();
      terminal.printerModel = src.readString();
      terminal.cashbinModel = src.readString();
      terminal.bonds10count = src.readInt();
      terminal.bonds50count = src.readInt();
      terminal.bonds100count = src.readInt();
      terminal.bonds500count = src.readInt();
      terminal.bonds1000count = src.readInt();
      terminal.bonds5000count = src.readInt();
      terminal.bonds10000count = src.readInt();
      terminal.paysPerHour = src.readString();
      terminal.agentId = src.readLong();
      terminal.agentName = src.readString();
      return terminal;
    }

    @Override
    public Terminal[] newArray(int size)
    {
      return new Terminal[size];
    }
    
  };*/

  public void update(Terminal terminal)
  {
    state = terminal.State();
    printer_state = terminal.printer_state;
    cashbin_state = terminal.cashbin_state;
    lpd = terminal.lpd;
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
  }

  @Override
  public String toString()
  {
    return address;
  }
}