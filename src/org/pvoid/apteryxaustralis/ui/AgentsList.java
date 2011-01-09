package org.pvoid.apteryxaustralis.ui;

import org.pvoid.apteryxaustralis.Consts;
import org.pvoid.apteryxaustralis.accounts.Agent;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.pvoid.apteryxaustralis.storage.Storage;

public class AgentsList extends ListActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
////////
    Iterable<Agent> agents = Storage.getAgents(this,Storage.AgentsTable.NAME);
    if(agents!=null)
    {
      ArrayAdapter<Agent> agentsAdapter = new ArrayAdapter<Agent>(this, android.R.layout.simple_list_item_1);
      for(Agent agent :  agents)
      {
        agentsAdapter.add(agent);
      }
  		setListAdapter(agentsAdapter);
    }
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		Intent intent = new Intent();
		intent.putExtra(Consts.RESULT_AGENT_POSITION, position);
		setResult(Consts.ACTIVITY_SELECT_AGENT, intent);
		finish();
	}
}
