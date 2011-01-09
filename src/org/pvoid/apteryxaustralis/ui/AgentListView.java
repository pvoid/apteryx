package org.pvoid.apteryxaustralis.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.pvoid.apteryxaustralis.accounts.Agent;
import org.pvoid.apteryxaustralis.accounts.TerminalListRecord;

public class AgentListView extends ListView
{
  private Agent _mAgent;
  
  public AgentListView(Context context, Agent agent)
  {
    super(context);
    _mAgent = agent;
  }
  
  public AgentListView(Context context, Agent agent, AttributeSet attrs)
  {
    super(context, attrs);
    _mAgent = agent;
  }
  
  public AgentListView(Context context, Agent agent, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    _mAgent = agent;
  }

  public Agent getAgent()
  {
    return(_mAgent);
  }

  @Override
  public boolean performItemClick(View view, int position, long id)
  {
    boolean result = super.performItemClick(view, position, id);
    if(!result)
    {
      ListAdapter adapter = getAdapter();
      TerminalListRecord item = (TerminalListRecord) adapter.getItem(position);

      Toast.makeText(getContext(),item.toString(),Toast.LENGTH_LONG).show();
    }
    return true;
  }
}
