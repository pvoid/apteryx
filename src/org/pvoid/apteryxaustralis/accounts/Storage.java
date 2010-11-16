package org.pvoid.apteryxaustralis.accounts;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

public abstract class Storage<T extends Preserved> implements Iterable<T>
{
  protected ArrayList<T> _Items = new ArrayList<T>();
  private boolean _Restored = false;
/**
 * Добавляет к хранилищу элементы. Записи не производит, добавление только в памяти
 * @param items Добавляемые элементы
 * @return  Вернет false в случае если какой либо из элементов невозможно добавить
 */
  public boolean Add(T... items)
  {
    return(Add(Arrays.asList(items)));
  }
/**
 * Добавляет к хранилищу элементы на основе итератора. Записи не производит, добавление только в памяти
 * @param items  итератор перебирающий добавляемые элементы
 * @return  Вернет false в случае если какой либо из элементов невозможно добавить
 */
  public boolean Add(Iterable<T> items)
  {
    boolean result = false;
    
    for(T item : items)
      if(!(result = _Items.add(item)))
        break;
///////
    if(result)
      Collections.sort(_Items,comparator());
///////
    return(result);
  }
/**
 * Добавляет к хранилищу элементы, не допуская их дублирования. Если элемент с таким ID уже существует,
 * произойдет его изменение. Записи не производит, добавление только в памяти
 * @param items Добавляемые элементы
 */
  public void AddUnique(T... items)
  {
    AddUnique(Arrays.asList(items));
  }
/**
 * Добавляет к хранилищу элементы на основе итератора, не допуская их дублирования. Если элемент с таким ID 
 * уже существует, произойдет его изменение. Записи не производит, добавление только в памяти
 * @param items итератор перебирающий добавляемые элементы
 */
  public void AddUnique(Iterable<T> items)
  {
    if(items==null)
      return;
/////////
    for(T item : items)
    {
      int index = Collections.binarySearch(_Items, item,comparator()); 
      if(index<0)
      {
        index = -index;
        if(index<_Items.size())
          _Items.add(index, item);
        else
          _Items.add(item);
      }
      else
      {
        _Items.get(index).<T>Copy(item);
      }
    }
/////////
    Collections.sort(_Items,comparator());
  }
/**
 * Удаляет элемент из хранилища
 * @param item  будет сравниватся с текущим элементом, при равенстве удален
 * @return
 */
  public boolean Delete(T item)
  {
    int index = Collections.binarySearch(_Items,item,comparator());
    if(index>=0)
    {
      _Items.remove(index);
      return(true);
    }
    return(false);
  }
/**
 * Удаляет элемент их хранилища
 * @param id идентификатор удаляемого элемента
 * @return
 */
  public boolean Delete(long id)
  {
    T item = EmptyItem(id);
    return(Delete(item));
  }
/**
 * Возвращает элемент из хранилища по его id
 * @param id
 * @return
 */
  public T Find(long id)
  {
    T needle = EmptyItem(id);
    int index = Collections.binarySearch(_Items, needle, comparator());
    if(index>-1)
      return(_Items.get(index));
    return(null);
  }
/**
 * Итератор для перебора записей в хранилище. Записи отсортированы по Id
 */
  @Override
  public Iterator<T> iterator()
  {
    return(_Items.iterator());
  }
/**
 * Проверяет не пусто ли хранилище.  
 * @return true если хранилище пусто
 */
  public boolean isEmpty()
  {
    return(_Items==null || _Items.isEmpty());
  }
  
  protected abstract T EmptyItem(long id);
/**
 * @return Имя файла куда должно записываться хранилище
 */
  protected abstract String FileName();
  
  protected abstract Comparator<T> comparator();
/**
 * Записывает хранилище в память телефона, в приватную зону данных
 * @param context
 * @return
 */
  public boolean Serialize(Context context)
  {
    try 
    {
      FileOutputStream stream = context.openFileOutput(FileName(), Context.MODE_PRIVATE);
      ObjectOutputStream objectStream = new ObjectOutputStream(stream);
      for(T item : _Items)
      {
        objectStream.writeObject(item);
      }
      objectStream.close();
    }
    catch (IOException e) 
    {
      e.printStackTrace();
    }
    return(true);
  }
/**
 * Восстанавливает хранилище из памяти телефона
 * @param context
 * @return
 */
  @SuppressWarnings("unchecked")
  public boolean Restore(Context context)
  {
  	if(_Restored)
  		return true;
////////
    boolean result = false;
    try 
    {
      FileInputStream stream = context.openFileInput(FileName());
      ObjectInputStream objectStream = new ObjectInputStream(stream);
      try
      {
        try
        {
          while(true)
          {
            _Items.add((T)objectStream.readObject());
          }
        }
        catch(EOFException eofe)
        {}
        result = true;
      }
      catch (ClassNotFoundException e)
      {
        e.printStackTrace();
      }
      objectStream.close();
    }
    catch (FileNotFoundException e)
    {
      // ну и ладно
      return(true);
    }
    catch (IOException e) 
    {
      e.printStackTrace();
    }
    _Restored = true;
    return(result);
  }
  
  public List<T> Fill(Comparable<T> comparator)
  {
    ArrayList<T> result = new ArrayList<T>();
///////////
    for(T item : _Items)
    {
      if(comparator.compareTo(item)==0)
        result.add(item);
    }
///////////
    return(result);
  }
  
  public int Size()
  {
    return(_Items.size());
  }
}
