package org.pvoid.apteryxaustralis.accounts;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class TerminalInfoOld implements Parcelable, Serializable
{
  private static final long serialVersionUID = -7462609920747072267L;
  
  public final static int STATE_OK = 0;
  public final static int STATE_WARRNING = 2;
  public final static int STATE_ERROR = 1;
  
  private long _Id;
  private long _AgentId;
  private String _Address;
  
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
  
  public TerminalInfoOld(long id, long agentId, String address)
  {
    _Id = id;
    _AgentId = agentId;
    _Address = address;
  }
  
  public String Address() 
  {
    return _Address;
  }
  
  public String id()
  {
    return(Long.toString(_Id));
  }
  
  public void State(int state)
  {
    this.state = state;
  }
  
  public int State()
  {
    return(state);
  }
  
  @Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    /*dest.writeString(tid);
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
    dest.writeString(agentName);*/
  }
  
  public static final Parcelable.Creator<TerminalInfoOld> CREATOR = new Parcelable.Creator<TerminalInfoOld>()
  {
    @Override
    public TerminalInfoOld createFromParcel(Parcel src)
    {
      TerminalInfoOld terminal = new TerminalInfoOld(src.readLong(),src.readLong(),src.readString());
      /*terminal.State(src.readInt());
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
      terminal.agentName = src.readString();*/
      return terminal;
    }

    @Override
    public TerminalInfoOld[] newArray(int size)
    {
      return new TerminalInfoOld[size];
    }
    
  };
}