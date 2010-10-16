package org.pvoid.apteryxaustralis.accounts;

public abstract class Preserved implements Comparable<Preserved>
{
  private long _Id;
  
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
    return((int)(_Id - another.Id()));
  }
  
  public abstract <T extends Preserved> void Copy(T another);
}
