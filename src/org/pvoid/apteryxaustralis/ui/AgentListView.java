package org.pvoid.apteryxaustralis.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.accounts.Agent;
import org.pvoid.apteryxaustralis.accounts.TerminalListRecord;

public class AgentListView extends ListView
{
  private Agent _mAgent;
  
  public AgentListView(Context context, Agent agent)
  {
    super(context);
    _mAgent = agent;
    setupUI(context);
  }
  
  public AgentListView(Context context, Agent agent, AttributeSet attrs)
  {
    super(context, attrs);
    _mAgent = agent;
    setupUI(context);
  }
  
  public AgentListView(Context context, Agent agent, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    _mAgent = agent;
    setupUI(context);
  }

  protected void setupUI(Context context)
  {
    setCacheColorHint(Color.argb(255,0xd8,0xd8,0xd8));
    setDivider(context.getResources().getDrawable(R.drawable.list_line));
    setDividerHeight(1);
  }

  public Agent getAgent()
  {
    return(_mAgent);
  }

}
