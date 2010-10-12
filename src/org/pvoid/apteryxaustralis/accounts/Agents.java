package org.pvoid.apteryxaustralis.accounts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.pvoid.apteryxaustralis.net.IResponseParser;
import org.xml.sax.Attributes;

import android.util.Log;

public class Agents implements IResponseParser
{
  private ArrayList<Agent> _Agents = new ArrayList<Agent>();
  
  public static Agents getParser()
  {
    return(new Agents());
  }

  @Override
  public void ElementStart(String tagName, Attributes attributes)
  {
    if(tagName.equalsIgnoreCase("agent"))
    {
      String id = attributes.getValue("id");
      String name = attributes.getValue("name");
      String phone = attributes.getValue("phone");
      Agent agent = new Agent();
      try
      {
        agent.Id = Long.parseLong(id);
        agent.Name = name;
        agent.Phone = phone;
        _Agents.add(agent);
      }
      catch(NumberFormatException e)
      {
        Log.e("Apteryx", "Invalid agent id:"+id);
        e.printStackTrace();
      }
    }
  }

  @Override
  public void ElementEnd(String innerText)
  {
    // TODO Auto-generated method stub
  }

  @Override
  public void SectionStart()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void SectionEnd()
  {
    Arrays.sort(_Agents.toArray(), new Comparator<Object>()
      {
        @Override
        public int compare(Object object1, Object object2)
        {
          return((int)( ((Agent)object1).Id - ((Agent)object2).Id) );
        }
      });
  }
}
