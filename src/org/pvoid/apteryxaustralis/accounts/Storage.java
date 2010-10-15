package org.pvoid.apteryxaustralis.accounts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Storage<T extends Preserved> implements Iterable<T>
{
  private ArrayList<T> _Items = new ArrayList<T>();
  
  public boolean Add(T... items)
  {
    boolean result = false;
    for(int i=0;i<items.length;++i)
      if(!(result = _Items.add(items[i])))
        break;
///////
    if(result)
      Arrays.sort(_Items.toArray());
///////
    return(result);
  }
  
  public void AddUnique(T... items)
  {
    for(int i=0;i<items.length;++i)
    {
      int index = Arrays.binarySearch(_Items.toArray(), items[i]); 
      if(index<0)
      {
        _Items.add(-index, items[i]);
      }
      else
      {
        // TODO: Изменение записи
      }
    }
  }
  
  @Override
  public Iterator<T> iterator()
  {
    return(_Items.iterator());
  }
}
