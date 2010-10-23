package org.pvoid.apteryxaustralis.accounts;

import java.io.Serializable;

public abstract class Preserved implements Comparable<Preserved>, Serializable
{
  private static final long serialVersionUID = -2369111070886521999L;
  private long _Id;
  
  protected Preserved()
  {
  }
  
  public Preserved(long id)
  {
    _Id = id;
  }

  public long Id()
  {
    return(_Id);
  }
  
  @Override
  public int compareTo(Preserved another)
  {
    return((int)(another.Id() - _Id));
  }
  
  public abstract <T extends Preserved> void Copy(T another);
}
