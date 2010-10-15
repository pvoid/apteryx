package org.pvoid.apteryxaustralis.accounts;

public class Preserved implements Comparable<Preserved>
{
  public final long Id;
  
  public Preserved(long id)
  {
    Id = id;
  }

  @Override
  public int compareTo(Preserved another)
  {
    return((int)(Id - another.Id));
  }
}
