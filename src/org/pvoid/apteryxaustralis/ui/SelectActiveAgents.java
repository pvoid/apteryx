package org.pvoid.apteryxaustralis.ui;

import java.util.ArrayList;

import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.Consts;
import org.pvoid.apteryxaustralis.accounts.Agent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectActiveAgents extends Activity
{
  private ArrayList<Agent> _Agents;
  private ListView _List;
  
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    Bundle extras = getIntent().getExtras();
    _Agents = extras.getParcelableArrayList(Consts.EXTRA_AGENTS);
    setContentView(R.layout.selectagents);
    _List = (ListView)findViewById(R.id.agents_list);
    _List.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    _List.setAdapter(new ArrayAdapter<Agent>(this, android.R.layout.simple_list_item_multiple_choice,_Agents));
  }
  
  public void Select(View view)
  {
    SparseBooleanArray choise = _List.getCheckedItemPositions();
    ArrayList<Agent> result = new ArrayList<Agent>();
    for(int i=0;i<_Agents.size();i++)
    {
      if(choise.get(i))
      {
        result.add(_Agents.get(i));
      }
    }
    
    if(result.size()>0)
    {
      Intent intent = new Intent();
      intent.putExtra(Consts.EXTRA_SELECTED_AGENTS, result);
      setResult(RESULT_OK,intent);
      finish();
    }
  }
}
