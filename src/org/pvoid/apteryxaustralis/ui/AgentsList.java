/*
 * Copyright (C) 2010-2011  Dmitry Petuhov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
