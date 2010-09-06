package org.pvoid.apteryx.ui;

import java.util.ArrayList;

import org.pvoid.apteryx.Consts;
import org.pvoid.apteryx.R;
import org.pvoid.apteryx.accounts.Agent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectMainAgent extends Activity
{
  private ArrayList<Agent> _Agents;
  private ListView _List;
  
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    Bundle extras = getIntent().getExtras();
    _Agents = extras.getParcelableArrayList(Consts.EXTRA_AGENTS);
    setContentView(R.layout.selectmainagent);
    _List = (ListView)findViewById(R.id.agents_list);
    _List.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    _List.setAdapter(new ArrayAdapter<Agent>(this, android.R.layout.simple_list_item_single_choice,_Agents));
  }
  
  public void Select(View view)
  {
    int position = _List.getCheckedItemPosition();
    if(position==ListView.INVALID_POSITION)
    {
      return;
    }
    Agent agent = _Agents.get(position);
    Intent result = new Intent();
    result.putExtra(Consts.EXTRA_SELECTED_AGENT, agent);
    setResult(RESULT_OK,result);
    finish();
  }
}
