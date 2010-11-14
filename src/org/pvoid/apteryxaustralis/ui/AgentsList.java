package org.pvoid.apteryxaustralis.ui;

import org.pvoid.apteryxaustralis.Consts;
import org.pvoid.apteryxaustralis.accounts.Agent;
import org.pvoid.apteryxaustralis.accounts.AgentsStorage;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AgentsList extends ListActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
////////
		ArrayAdapter<Agent> agents = new ArrayAdapter<Agent>(this, android.R.layout.simple_list_item_1);
		for(Agent agent : AgentsStorage.Instance().getAgentsByName())
		{
			agents.add(agent);
		}
////////
		setListAdapter(agents);
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
