package org.pvoid.apteryxaustralis.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class AgentListView extends ListView
{
  private long _AgentId;
  
  public AgentListView(Context context, long agentId)
  {
    super(context);
    _AgentId = agentId;
  }
  
  public AgentListView(Context context, long agentId, AttributeSet attrs)
  {
    super(context, attrs);
    _AgentId = agentId;
  }
  
  public AgentListView(Context context, long agentId, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    _AgentId = agentId;
  }

  public long getAgentId()
  {
    return(_AgentId);
  }
}
